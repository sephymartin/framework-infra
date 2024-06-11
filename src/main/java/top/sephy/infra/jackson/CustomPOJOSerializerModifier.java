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
package top.sephy.infra.jackson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.core.convert.ConversionService;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;

import lombok.extern.slf4j.Slf4j;
import top.sephy.infra.jackson.annotation.JsonDict;
import top.sephy.infra.jackson.annotation.JsonDictMeta;
import top.sephy.infra.jackson.ser.JsonDictSerializer;
import top.sephy.infra.option.DictEntryProvider;

/**
 * 实现 {@link JsonDict} 注解, 在序列化时自动添加字典标签
 */
@Slf4j
public class CustomPOJOSerializerModifier extends BeanSerializerModifier {

    private final ConcurrentMap<Class<?>, Map<String, JsonDictMeta>> metaMap = new ConcurrentHashMap<>();

    private final DictEntryProvider<Object, Object> dictEntryProvider;

    private final ConversionService conversionService;

    public CustomPOJOSerializerModifier(DictEntryProvider<Object, Object> dictEntryProvider,
        ConversionService conversionService) {
        this.dictEntryProvider = dictEntryProvider;
        this.conversionService = conversionService;
    }

    public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc,
        List<BeanPropertyWriter> beanProperties) {

        Class<?> beanClass = beanDesc.getBeanClass();

        Map<String, JsonDictMeta> metaMap = this.metaMap.computeIfAbsent(beanClass, klass -> {

            Map<String, JsonDictMeta> map = new HashMap<>();
            for (BeanPropertyWriter beanProperty : beanProperties) {
                JsonDict annotation = beanProperty.getAnnotation(JsonDict.class);
                if (annotation != null) {
                    String labelFieldName = annotation.labelFieldName();
                    if (!StringUtils.hasText(labelFieldName)) {
                        labelFieldName = beanProperty.getName() + "Label";
                    }
                    JsonDictMeta meta = new JsonDictMeta(annotation, labelFieldName);
                    map.put(beanProperty.getName(), meta);
                }
            }
            return map;
        });

        if (!metaMap.isEmpty()) {
            for (BeanPropertyWriter beanProperty : beanProperties) {
                JsonDictMeta meta = metaMap.get(beanProperty.getName());
                if (meta != null) {
                    if (log.isDebugEnabled()) {
                        log.debug("对 {} 的 {} 属性添加字典标签", beanClass, beanProperty.getName());
                    }
                    beanProperty.assignSerializer(new JsonDictSerializer(dictEntryProvider, meta, conversionService));
                }
            }
        }

        return beanProperties;
    }
}
