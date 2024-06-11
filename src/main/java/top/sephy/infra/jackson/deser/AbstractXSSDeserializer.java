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
package top.sephy.infra.jackson.deser;

import java.io.IOException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;

import top.sephy.infra.jackson.annotation.XSSIgnore;

public abstract class AbstractXSSDeserializer extends StdDeserializer<String> implements ContextualDeserializer {

    public AbstractXSSDeserializer() {
        super(StringDeserializer.instance);
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
        throws JsonMappingException {

        if (property != null) {
            XSSIgnore annotation = property.getAnnotation(XSSIgnore.class);
            if (annotation != null && annotation.ignore()) {
                return StringDeserializer.instance;
            }
        }

        return this;
    }

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        return doDeserialize(p, ctxt);
    }

    protected abstract String doDeserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException, JacksonException;
}
