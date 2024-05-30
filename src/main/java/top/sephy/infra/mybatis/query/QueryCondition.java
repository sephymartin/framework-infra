package top.sephy.infra.mybatis.query;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QueryCondition {

    String tableAlias() default "";

    String name() default "";

    QueryOperator operator() default QueryOperator.EQ;

    /**
     *
     * @see ConverterStrategy
     * @return
     */
    ConverterStrategy converterStrategy() default ConverterStrategy.DEFAULT;

    /**
     * 值为null时是否忽略该条件
     * 
     * @return
     */
    boolean ignoreNull() default true;
}
