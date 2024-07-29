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
package top.sephy.infra.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public abstract class SpELUtils {

    /**
     * SpEL表达式解析工具
     */
    private static final ExpressionParser EXPRESSION_PARSER = new SpelExpressionParser();

    private static final ParserContext PARSER_CONTEXT = new TemplateParserContext();

    /**
     * 解析缓存
     */
    private static final Map<String, Expression> EXPRESSION_CACHE = new ConcurrentHashMap<>(64);

    public static String parse(String template, ProceedingJoinPoint joinPoint) {

        final Object target = joinPoint.getTarget();

        String className = target.getClass().getName();
        final Signature signature = joinPoint.getSignature();
        String[] paramNames = ((CodeSignature)signature).getParameterNames();
        String methodName = signature.getName();
        String contentKey = className + "#" + methodName;
        final Expression contentTemplate = EXPRESSION_CACHE.computeIfAbsent(contentKey,
            k -> EXPRESSION_PARSER.parseExpression(template, PARSER_CONTEXT));

        // 将方法参数添加到上下文中
        final Object[] paramValues = joinPoint.getArgs();

        StandardEvaluationContext ctx = new StandardEvaluationContext();
        for (int i = 0; i < paramNames.length; i++) {
            String paramName = paramNames[i];
            Object paramValue = paramValues[i];
            ctx.setVariable(paramName, paramValue);
        }
        return contentTemplate.getValue(ctx, String.class);
    }
}
