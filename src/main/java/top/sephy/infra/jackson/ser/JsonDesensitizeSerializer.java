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
package top.sephy.infra.jackson.ser;

import java.io.IOException;
import java.io.Serial;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import lombok.Setter;
import top.sephy.infra.jackson.annotation.JsonDesensitize;
import top.sephy.infra.security.DesensitizationStrategy;

@Setter
public class JsonDesensitizeSerializer extends StdSerializer<String> implements ContextualSerializer {

    @Serial
    private static final long serialVersionUID = -2517170909648829158L;
    private DesensitizationStrategy strategy;

    public JsonDesensitizeSerializer() {
        super(String.class);
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property)
        throws JsonMappingException {

        JsonSerializer<?> ser = prov.findValueSerializer(String.class, property);

        JsonDesensitize annotation = property.getAnnotation(JsonDesensitize.class);
        if (annotation != null && annotation.value() != null) {
            this.strategy = annotation.value();
            ser = this;
        }

        return ser;
    }

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (strategy != null) {
            gen.writeString(strategy.desensitize(value));
        } else {
            gen.writeString(value);
        }
    }
}
