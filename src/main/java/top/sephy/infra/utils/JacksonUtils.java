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

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.hashids.Hashids;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.ClassUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import lombok.extern.slf4j.Slf4j;
import top.sephy.infra.jackson.deser.CustomLocalDateDeserializer;
import top.sephy.infra.jackson.deser.CustomLocalDateTimeDeserializer;
import top.sephy.infra.jackson.deser.HashIdDeserializer;
import top.sephy.infra.jackson.ser.CustomBigDecimalSerializer;
import top.sephy.infra.jackson.ser.CustomLocalDateSerializer;
import top.sephy.infra.jackson.ser.CustomLocalDateTimeSerializer;
import top.sephy.infra.jackson.ser.HashIdSerializer;

@Slf4j
public abstract class JacksonUtils {

    private static ObjectMapper DEFAULT_OBJECT_MAPPER = newDefaultObjectMapper();

    private static ObjectMapper DEFAULT_OBJECT_MAPPER_INCLUDE_NULL;

    // private static boolean javaTimeModulePresent = false;

    private static boolean playwrightModulePresent = false;

    static {
        ClassLoader classLoader = JacksonUtils.class.getClassLoader();
        // javaTimeModulePresent =
        // ClassUtils.isPresent("com.fasterxml.jackson.datatype.jsr310.JavaTimeModule", classLoader);
        playwrightModulePresent = ClassUtils.isPresent("com.microsoft.playwright.Playwright", classLoader);
        DEFAULT_OBJECT_MAPPER_INCLUDE_NULL = newDefaultObjectMapper();
        DEFAULT_OBJECT_MAPPER_INCLUDE_NULL.setSerializationInclusion(JsonInclude.Include.ALWAYS);
    }

    private static TypeReference<HashMap<String, String>> STRING_MAP = new TypeReference<HashMap<String, String>>() {};

    public static ObjectMapper newDefaultObjectMapper() {

        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder()
            .serializerByType(LocalDateTime.class, CustomLocalDateTimeSerializer.INSTANCE)
            .deserializerByType(LocalDateTime.class, CustomLocalDateTimeDeserializer.INSTANCE)
            .serializerByType(BigDecimal.class, CustomBigDecimalSerializer.INSTANCE)
            .serializerByType(LocalDate.class, CustomLocalDateSerializer.INSTANCE)
            .deserializerByType(LocalDate.class, CustomLocalDateDeserializer.INSTANCE)
            .serializerByType(Long.class, new HashIdSerializer(new Hashids()))
            .deserializerByType(Long.class, new HashIdDeserializer(new Hashids()));
        if (playwrightModulePresent) {
            builder.modules(new PlaywrightModule());
        }

        ObjectMapper objectMapper = builder.build();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        disableFeatures(objectMapper);
        enableFeatures(objectMapper);
        return objectMapper;
    }

    private static void disableFeatures(ObjectMapper objectMapper) {
        objectMapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    private static void enableFeatures(ObjectMapper objectMapper) {}

    public static String toJson(Object object) {
        return toJson(DEFAULT_OBJECT_MAPPER, object);
    }

    public static String toJson(ObjectMapper objectMapper, Object object) {
        assertObjectMapper(objectMapper);
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new JsonException(e);
        }
    }

    public static <T> T jsonToObject(String json, Class<T> clazz) {
        return jsonToObject(DEFAULT_OBJECT_MAPPER, json, clazz);
    }

    public static <T> T jsonToObject(String json, TypeReference<T> typeReference) {
        return jsonToObject(DEFAULT_OBJECT_MAPPER, json, typeReference);
    }

    public static <T> T jsonToObject(ObjectMapper objectMapper, String json, Class<T> clazz) {
        assertObjectMapper(objectMapper);
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new JsonException(e);
        }
    }

    public static <T> T jsonToObject(ObjectMapper objectMapper, String json, TypeReference<T> typeReference) {
        assertObjectMapper(objectMapper);
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (Exception e) {
            throw new JsonException(e);
        }
    }

    public static <T> T jsonToObject(InputStream inputStream, Class<T> clazz) {
        return jsonToObject(DEFAULT_OBJECT_MAPPER, inputStream, clazz);
    }

    public static <T> T jsonToObject(ObjectMapper objectMapper, InputStream inputStream, Class<T> clazz) {
        assertObjectMapper(objectMapper);
        try {
            return objectMapper.readValue(inputStream, clazz);
        } catch (Exception e) {
            throw new JsonException(e);
        }
    }

    public static <T> List<T> jsonToList(String json, Class<T> clazz) {
        return jsonToList(DEFAULT_OBJECT_MAPPER, json, clazz);
    }

    public static <T> List<T> jsonToList(ObjectMapper objectMapper, String json, Class<T> clazz) {
        assertObjectMapper(objectMapper);
        try {
            return objectMapper.readValue(json,
                objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
            // return objectMapper.readValue(json, new TypeReference<List<T>>() {
            // });
        } catch (Exception e) {
            throw new JsonException(e);
        }
    }

    public static JsonNode jsonToTree(String json) {
        return jsonToTree(DEFAULT_OBJECT_MAPPER, json);
    }

    public static JsonNode jsonToTree(ObjectMapper objectMapper, String json) {
        assertObjectMapper(objectMapper);
        try {
            return objectMapper.readTree(json);
        } catch (Exception e) {
            throw new JsonException(e);
        }
    }

    public static <T> T treeToValue(JsonNode jsonNode, Class<T> clazz) {
        return treeToValue(DEFAULT_OBJECT_MAPPER, jsonNode, clazz);
    }

    public static <T> T treeToValue(ObjectMapper objectMapper, JsonNode jsonNode, Class<T> clazz) {
        assertObjectMapper(objectMapper);
        try {
            return objectMapper.treeToValue(jsonNode, clazz);
        } catch (Exception e) {
            throw new JsonException(e);
        }
    }

    public static Map<String, Object> convertToMap(Object object) {
        return convertToMap(DEFAULT_OBJECT_MAPPER, object);
    }

    public static Map<String, Object> convertToMapIncludeNull(Object object) {
        return convertToMap(DEFAULT_OBJECT_MAPPER_INCLUDE_NULL, object);
    }

    public static Map<String, Object> convertToMap(ObjectMapper objectMapper, Object object) {
        assertObjectMapper(objectMapper);
        return objectMapper.convertValue(object, Map.class);
    }

    public static Map<String, String> convertToStringMap(Object object) {
        return convertToStringMap(DEFAULT_OBJECT_MAPPER, object);
    }

    public static Map<String, String> convertToStringMapIncludeNull(Object object) {
        return convertToStringMap(DEFAULT_OBJECT_MAPPER_INCLUDE_NULL, object);
    }

    public static Map<String, String> convertToStringMap(ObjectMapper objectMapper, Object object) {
        assertObjectMapper(objectMapper);
        return objectMapper.convertValue(object, STRING_MAP);
    }

    public static <E> E convert(Object from, Class<E> to) {
        return convert(DEFAULT_OBJECT_MAPPER, from, to);
    }

    public static <E> E convert(ObjectMapper objectMapper, Object from, Class<E> to) {
        assertObjectMapper(objectMapper);
        return objectMapper.convertValue(from, to);
    }

    public static <E> E stringMapToObject(Map<String, String> map, Class<E> clazz) {
        return stringMapToObject(DEFAULT_OBJECT_MAPPER, map, clazz);
    }

    public static <E> E stringMapToObject(ObjectMapper objectMapper, Map<String, String> map, Class<E> clazz) {
        return objectMapper.convertValue(map, clazz);
    }

    private static void assertObjectMapper(ObjectMapper objectMapper) {
        Objects.requireNonNull(objectMapper, "ObjectMapper must not be null.");
    }

    public static class JsonException extends RuntimeException {

        private static long serialVersionUID = -8318031819390714507L;

        public JsonException() {}

        public JsonException(String message) {
            super(message);
        }

        public JsonException(String message, Throwable cause) {
            super(message, cause);
        }

        public JsonException(Throwable cause) {
            super(cause);
        }

        public JsonException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }

    public static class PlaywrightModule extends SimpleModule {
        public PlaywrightModule() {
            super("PlaywrightModule", new Version(0, 0, 1, null));
        }

        @Override
        public void setupModule(SetupContext context) {
            context.setMixInAnnotations(com.microsoft.playwright.options.ViewportSize.class, ViewportSizeMixIn.class);
            context.setMixInAnnotations(com.microsoft.playwright.options.ScreenSize.class, ScreenSizeMixIn.class);
            context.setMixInAnnotations(com.microsoft.playwright.options.Cookie.class, CookieMixIn.class);
        }
    }

    @Slf4j
    public static class ViewportSizeMixIn {
        @JsonCreator
        public ViewportSizeMixIn(@JsonProperty("width") int width, @JsonProperty("height") int height) {
            log.info("ViewportSizeMixIn called!");
        }
    }

    @Slf4j
    public static class ScreenSizeMixIn {
        @JsonCreator
        public ScreenSizeMixIn(@JsonProperty("width") int width, @JsonProperty("height") int height) {
            log.info("ViewportSizeMixIn called!");
        }
    }

    @Slf4j
    public static class CookieMixIn {
        @JsonCreator
        public CookieMixIn(@JsonProperty("name") String name, @JsonProperty("value") String value) {
            log.info("CookieMixIn called!");
        }
    }
}
