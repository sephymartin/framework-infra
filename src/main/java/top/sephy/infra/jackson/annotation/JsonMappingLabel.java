package top.sephy.infra.jackson.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 配合 {@link JsonItemOptionObject} 使用，标记一个字段为字典字段, 在序列化时会自动添加一个字典标签
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface JsonMappingLabel {

    /**
     * 字典类型
     * 
     * @return
     */
    String type();

    /**
     * 字典标签名称
     * 
     * @return
     */
    String labelFieldName() default "";

    /**
     * 默认的值
     * 
     * @return
     */
    String defaultLabel() default "";

    /**
     * 将key转换为字符串再比较
     *
     * @return
     */
    boolean compareWithKeyString() default true;
}
