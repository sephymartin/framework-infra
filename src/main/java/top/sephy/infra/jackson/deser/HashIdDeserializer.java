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

import org.hashids.Hashids;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.NumberDeserializers;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import top.sephy.infra.jackson.annotation.JsonHashId;

public class HashIdDeserializer extends StdDeserializer<Long> implements ContextualDeserializer {

    private Hashids hashids;

    public HashIdDeserializer(Hashids hashids) {
        super(Long.class);
        this.hashids = hashids;
    }

    @Override
    public Long deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        if (p.hasTextCharacters()) {
            String text = p.getText();
            long[] decode = hashids.decode(text);
            if (decode.length > 0) {
                return decode[0];
            }
        }
        return null;
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
        throws JsonMappingException {
        JsonHashId jsonHashId = property.getAnnotation(JsonHashId.class);
        if (jsonHashId != null) {
            String salt = jsonHashId.salt();
            Hashids hashIds = new Hashids(salt);
            return new HashIdDeserializer(hashIds);
        }
        return NumberDeserializers.NumberDeserializer.instance;
    }
}
