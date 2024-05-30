package top.sephy.infra.option.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Translate option annotation
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TranslateItemOption {

    /**
     * option type
     * 
     * @return
     */
    String type();

    /**
     * key field name
     * 
     * @return
     */
    String keyFieldName();

    /**
     * default value if not found
     * 
     * @return
     */
    String defaultValue() default "";
}
