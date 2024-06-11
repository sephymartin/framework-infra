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
import java.io.Serial;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;

public class JsoupXssDeserializer extends AbstractXSSDeserializer {

    @Serial
    private static final long serialVersionUID = 3444056323052247564L;

    public static JsoupXssDeserializer INSTANCE = new JsoupXssDeserializer();

    @Override
    protected String doDeserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        String val = StringUtils.trim(p.getValueAsString());
        return Jsoup.clean(val, Whitelist.none());
    }
}
