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
package top.sephy.infra.mybatis.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import com.google.common.base.CaseFormat;

import lombok.Data;
import top.sephy.infra.utils.JacksonUtils;

public class DefaultQueryContextExtractor implements QueryContextExtractor {

    private boolean camelToUnderline = true;

    private Map<String, Converter> converterMap = new HashMap<>();
    private ConcurrentHashMap<Class<?>, QueryMetaInfo> cache = new ConcurrentHashMap<>();

    public DefaultQueryContextExtractor(boolean camelToUnderline) {
        this.camelToUnderline = camelToUnderline;
        initConverterMap();
    }

    void initConverterMap() {
        Arrays.stream(ConverterStrategy.values()).forEach(strategy -> {
            converterMap.put(strategy.name(), strategy);
        });
    }

    @Override
    public QueryContext extract(Object object) {
        List<QueryExpression<Object>> conditionList = new ArrayList<>();
        Map<String, QueryExpression<Object>> conditionMap = new HashMap<>();
        Class<?> clazz = object.getClass();
        Map<String, Object> params = JacksonUtils.convertToMap(object);

        QueryMetaInfo meta = cache.computeIfAbsent(clazz, this::extraCriteriaMeta);

        Map<String, $QueryCondition> criteriaMap = meta.getConditionMap();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            $QueryCondition condition = criteriaMap.get(key);
            if (condition != null) {
                QueryExpression<Object> queryExpression = new QueryExpression<>();
                queryExpression.setField(condition.name);
                queryExpression.setOp(condition.op());
                Converter<Object, Object> converter = condition.converter;
                Object val = converter.convert(entry.getValue());
                if (condition.ignoreNull && val == null) {
                    continue;
                }
                queryExpression.setVal(val);

                conditionList.add(queryExpression);
                conditionMap.put(key, queryExpression);
            }
        }
        QueryContext queryContext = new QueryContext();
        queryContext.setExpressions(conditionList);
        queryContext.setExpressionMap(conditionMap);
        return queryContext;
    }

    private synchronized QueryMetaInfo extraCriteriaMeta(Class<?> clazz) {

        Map<String, $QueryCondition> conditionMap = new HashMap<>();

        ReflectionUtils.doWithFields(clazz, field -> {

            IgnoreQuery ignoreQuery = field.getAnnotation(IgnoreQuery.class);
            if (ignoreQuery == null) {

                String name = field.getName();
                boolean nameSpecified = false;
                QueryCondition queryCondition = field.getAnnotation(QueryCondition.class);
                QueryOperator op = QueryOperator.EQ;
                Converter converter = ConverterStrategy.DEFAULT;
                boolean ignoreNull = true;

                if (queryCondition != null) {
                    if (StringUtils.hasText(queryCondition.name())) {
                        name = queryCondition.name();
                        nameSpecified = true;
                    }
                    op = queryCondition.operator();
                    ignoreNull = queryCondition.ignoreNull();
                    converter = queryCondition.converterStrategy();
                }

                if (!nameSpecified && camelToUnderline) {
                    name = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name);
                }

                if (StringUtils.hasText(queryCondition.tableAlias())) {
                    name = queryCondition.tableAlias() + "." + name;
                }

                conditionMap.put(field.getName(), new $QueryCondition(name, op, converter, ignoreNull));
            }

        });
        QueryMetaInfo meta = new QueryMetaInfo();
        meta.setConditionMap(conditionMap);
        return meta;
    }

    private record $QueryCondition(String name, QueryOperator op, Converter converter, boolean ignoreNull) {
    }

    @Data
    private static class QueryMetaInfo {
        private Map<String, $QueryCondition> conditionMap;
    }
}
