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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import top.sephy.infra.mybatis.query.QueryContext;
import top.sephy.infra.mybatis.query.QueryContextExtractor;
import top.sephy.infra.mybatis.query.QueryExpression;
import top.sephy.infra.paging.QueryObject;
import top.sephy.infra.utils.JacksonUtils;

@Intercepts({
    @Signature(type = Executor.class, method = "query",
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class,
        ResultHandler.class, CacheKey.class, BoundSql.class}),})
public class QueryConditionExtractorInterceptor implements Interceptor {

    public static final String EXPRESSIONS = "_expressions";

    public static final String EXPRESSION_MAP = "_expressionMap";

    private QueryContextExtractor queryContextExtractor;

    public QueryConditionExtractorInterceptor(QueryContextExtractor queryContextExtractor) {
        this.queryContextExtractor = queryContextExtractor;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();

        if (args.length >= 1) {
            Object params = args[1];
            // 多个参数的情况
            if (params instanceof MapperMethod.ParamMap) {
                Map<String, Object> paramsMap = (Map<String, Object>)params;
                List<QueryContext> queryContextList = new ArrayList<>();
                for (Map.Entry<String, Object> entry : paramsMap.entrySet()) {
                    if (entry.getValue() instanceof QueryObject) {
                        queryContextList.add(queryContextExtractor.extract(entry.getValue()));
                    }
                }
                QueryContext mergedContext = queryContextList.isEmpty() ? null : queryContextList.get(0);
                if (queryContextList.size() > 1) {
                    for (int i = 1; i < queryContextList.size(); i++) {
                        mergedContext = mergedContext.merge(queryContextList.get(i));
                    }
                }
                if (mergedContext != null) {
                    paramsMap.put(EXPRESSIONS, mergedContext.getExpressions());
                    Map<String, QueryExpression<Object>> expressionMap = mergedContext.getExpressionMap();
                    paramsMap.put(EXPRESSION_MAP, expressionMap);
                    // 将 QueryExpression 中的值设置到参数中, 因为 Converter 策略会修改实际的值
                    for (Map.Entry<String, QueryExpression<Object>> entry : expressionMap.entrySet()) {
                        paramsMap.put(entry.getKey(), entry.getValue().getVal());
                    }
                }
            } else if (params instanceof QueryObject) {
                Map<String, Object> paramMap = new MapperMethod.ParamMap<>();
                QueryContext queryContext = queryContextExtractor.extract(params);
                // 当 value == null 时, 会被忽略, 所以这里需要将 null 值也加入到 map 中
                Map<String, Object> map = JacksonUtils.convertToMapIncludeNull(params);
                paramMap.putAll(map);
                paramMap.put(EXPRESSIONS, queryContext.getExpressions());
                Map<String, QueryExpression<Object>> expressionMap = queryContext.getExpressionMap();
                paramMap.put(EXPRESSION_MAP, expressionMap);
                // 将 QueryExpression 中的值设置到参数中, 因为 Converter 策略会修改实际的值
                for (Map.Entry<String, QueryExpression<Object>> entry : expressionMap.entrySet()) {
                    paramMap.put(entry.getKey(), entry.getValue().getVal());
                }
                args[1] = paramMap;
            }
        }
        return invocation.proceed();
    }
}
