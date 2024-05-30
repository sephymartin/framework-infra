package top.sephy.infra.jackson.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import top.sephy.infra.jackson.ser.JsonDesensitizeSerializer;
import top.sephy.infra.security.DesensitizationStrategy;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = JsonDesensitizeSerializer.class)
@Documented
public @interface JsonDesensitize {

    DesensitizationStrategy value();
}
