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

import org.hashids.Hashids;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.StdKeySerializers;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import lombok.NonNull;
import top.sephy.infra.jackson.annotation.JsonHashId;

public class HashIdSerializer extends StdSerializer<Long> implements ContextualSerializer {

    private Hashids hashids;

    public HashIdSerializer(@NonNull Hashids hashids) {
        super(Long.class);
        this.hashids = hashids;
    }

    @Override
    public void serialize(Long value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }
        gen.writeString(hashids.encode(value));
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property)
        throws JsonMappingException {
        JsonHashId jsonHashId = property.getAnnotation(JsonHashId.class);
        if (jsonHashId != null) {
            String salt = jsonHashId.salt();
            Hashids hashIds = new Hashids(salt);
            return new HashIdSerializer(hashIds);
        }
        return StdKeySerializers.getStdKeySerializer(prov.getConfig(), property.getClass(), true);
    }
}
