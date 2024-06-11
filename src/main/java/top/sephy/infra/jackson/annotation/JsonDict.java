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
package top.sephy.infra.jackson.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 配合 {@link JsonDictObject} 使用，标记一个字段为字典字段, 在序列化时会自动添加一个字典标签
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface JsonDict {

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
    String defaultLabelValue() default "";

    /**
     * 将key转换为字符串再比较
     *
     * @return
     */
    boolean compareWithString() default true;

    boolean caseSensitive() default false;

    Class<?> labelClass() default String.class;
}
