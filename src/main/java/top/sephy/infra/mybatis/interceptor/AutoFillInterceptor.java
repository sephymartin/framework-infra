/*
 * Copyright 2022-2024 sephy.top
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package top.sephy.infra.mybatis.interceptor;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import lombok.Data;
import lombok.NonNull;
import top.sephy.infra.mybatis.MyBatisConstants;
import top.sephy.infra.mybatis.audit.annotaton.CreatedTime;
import top.sephy.infra.mybatis.audit.annotaton.CreatorId;
import top.sephy.infra.mybatis.audit.annotaton.CreatorName;
import top.sephy.infra.mybatis.audit.annotaton.ModifiedTime;
import top.sephy.infra.mybatis.audit.annotaton.ModifierId;
import top.sephy.infra.mybatis.audit.annotaton.ModifierName;

@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
public class AutoFillInterceptor implements Interceptor {

    private ConcurrentHashMap<Class<?>, AuditingConfig> auditingConfigMap = new ConcurrentHashMap<>();

    private CurrentUserExtractor<Object> currentUserExtractor;

    private ConversionService conversionService;

    public AutoFillInterceptor(ConversionService conversionService, CurrentUserExtractor<Object> currentUserExtractor) {
        this.conversionService = conversionService;
        this.currentUserExtractor = currentUserExtractor;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();

        MappedStatement mappedStatement = (MappedStatement)args[0];

        if (args.length > 1) {
            Object arg = args[1];
            Class<?> clazz = arg.getClass();

            AuditingConfig config = null;

            // 如果是集合类型, 取第一个元素的类型
            // mybatis 针对多参数，或者单个参数是Collection和Array 参数包装成MapperMethod.ParamMap（HashMap）,否则参数类型直接返回所传当前参数，
            // 可看org.apache.ibatis.reflection.ParamNameResolver.wrapToMapIfCollection
            if (MapperMethod.ParamMap.class.isAssignableFrom(clazz)) {
                MapperMethod.ParamMap<Object> paramMap = (MapperMethod.ParamMap<Object>)arg;
                if (paramMap.containsKey("collection")) {
                    Collection entityList = (Collection)paramMap.get("collection");
                    if (!CollectionUtils.isEmpty(entityList)) {
                        for (Object elem : entityList) {
                            clazz = elem.getClass();
                            config = auditingConfigMap.computeIfAbsent(clazz, this::extractConfig);
                            fillFields(mappedStatement, config, elem);
                        }
                    }
                }
                Object currentUserId = currentUserExtractor.getCurrentUserId();
                if (paramMap.containsKey(MyBatisConstants.PARAM_CREATED_BY)) {
                    paramMap.putIfAbsent(MyBatisConstants.PARAM_CREATED_BY, currentUserId);
                }
                if (paramMap.containsKey(MyBatisConstants.PARAM_UPDATED_BY)) {
                    paramMap.putIfAbsent(MyBatisConstants.PARAM_UPDATED_BY, currentUserId);
                }
            } else {
                config = auditingConfigMap.computeIfAbsent(clazz, this::extractConfig);
                fillFields(mappedStatement, config, arg);
            }
        }

        return invocation.proceed();
    }

    void fillFields(@NonNull MappedStatement mappedStatement, @NonNull AuditingConfig config, @NonNull Object object) {
        Field createdTimeField = config.getCreatedTimeField();
        Field modifiedTimeField = config.getModifiedTimeField();

        BeanWrapper beanWrapper = null;
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        if (createdTimeField != null || modifiedTimeField != null) {
            beanWrapper = new BeanWrapperImpl(object);
            LocalDateTime now = LocalDateTime.now();
            if (sqlCommandType == SqlCommandType.INSERT) {
                if (createdTimeField != null) {
                    setValue(beanWrapper, createdTimeField, now);
                }
                if (modifiedTimeField != null) {
                    setValue(beanWrapper, modifiedTimeField, now);
                }
            } else if (sqlCommandType == SqlCommandType.UPDATE) {
                if (modifiedTimeField != null) {
                    setValue(beanWrapper, modifiedTimeField, now);
                }
            }
        }

        if (currentUserExtractor != null) {
            Field creatorIdField = config.getCreatorIdField();
            Field creatorNameField = config.getCreatorNameField();
            Field modifierIdField = config.getModifierIdField();
            Field modifierNameField = config.getModifierNameField();
            if (creatorIdField != null && creatorNameField != null || modifierIdField != null
                || modifierNameField != null) {

                Object currentUserId = currentUserExtractor.getCurrentUserId();
                Object currentUserName = currentUserExtractor.getCurrentUserName();
                if (beanWrapper == null) {
                    beanWrapper = new BeanWrapperImpl(object);
                }

                if (sqlCommandType == SqlCommandType.INSERT) {
                    if (creatorIdField != null) {
                        setValue(beanWrapper, creatorIdField, currentUserId);
                    }
                    if (creatorNameField != null) {
                        setValue(beanWrapper, creatorNameField, currentUserName);
                    }
                    if (modifierIdField != null) {
                        setValue(beanWrapper, modifierIdField, currentUserId);
                    }
                    if (modifierNameField != null) {
                        setValue(beanWrapper, modifierNameField, currentUserName);
                    }
                } else if (sqlCommandType == SqlCommandType.UPDATE) {
                    if (modifierIdField != null) {
                        setValue(beanWrapper, modifierIdField, currentUserId);
                    }
                    if (modifierNameField != null) {
                        setValue(beanWrapper, modifierNameField, currentUserName);
                    }
                }
            }
        }
    }

    private void setValue(BeanWrapper beanWrapper, Field field, Object value) {
        String name = field.getName();
        if (beanWrapper.getPropertyValue(name) == null) {
            if (field.getType() != value.getClass()
                && conversionService.canConvert(value.getClass(), field.getType())) {
                value = conversionService.convert(value, field.getType());
            }
            beanWrapper.setPropertyValue(name, value);
        }
    }

    private synchronized AuditingConfig extractConfig(Class<?> clazz) {
        AuditingConfig config = new AuditingConfig();
        ReflectionUtils.doWithFields(clazz, field -> {

            CreatedTime createdTime = field.getAnnotation(CreatedTime.class);
            if (createdTime != null) {
                config.setCreatedTimeField(field);
            }

            ModifiedTime modifiedTime = field.getAnnotation(ModifiedTime.class);
            if (modifiedTime != null) {
                config.setModifiedTimeField(field);
            }

            CreatorId creatorId = field.getAnnotation(CreatorId.class);
            if (creatorId != null) {
                config.setCreatorIdField(field);
            }

            CreatorName creatorName = field.getAnnotation(CreatorName.class);
            if (creatorName != null) {
                config.setCreatorNameField(field);
            }

            ModifierId modifierId = field.getAnnotation(ModifierId.class);
            if (modifierId != null) {
                config.setModifierIdField(field);
            }

            ModifierName modifierName = field.getAnnotation(ModifierName.class);
            if (modifierName != null) {
                config.setModifierNameField(field);
            }

            // Version version = field.getAnnotation(Version.class);
            // if (version != null) {
            // config.setVersionFiled(field);
            // }
        });
        return config;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }

    @Data
    private static class AuditingConfig {

        private Field createdTimeField;

        private Field modifiedTimeField;

        private Field creatorIdField;

        private Field creatorNameField;

        private Field modifierIdField;

        private Field modifierNameField;
    }
}
