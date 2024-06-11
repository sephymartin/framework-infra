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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class CustomBigDecimalSerializer extends StdSerializer<BigDecimal> implements ContextualSerializer {

    public static final CustomBigDecimalSerializer INSTANCE = new CustomBigDecimalSerializer(null);

    private static final int CURRENCY_SCALE = 2;
    @Serial
    private static final long serialVersionUID = 8317887631741166562L;

    protected DecimalFormat decimalFormat;

    private CustomBigDecimalSerializer(DecimalFormat decimalFormat) {
        super(BigDecimal.class);
        this.decimalFormat = decimalFormat;
    }

    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (Objects.isNull(value)) {
            gen.writeNull();
        } else {
            if (null != decimalFormat) {
                gen.writeNumber(decimalFormat.format(value));
            } else {
                int scale = value.scale();
                if (scale < CURRENCY_SCALE) {
                    // 小于 2 位小数输出 2 位小数
                    gen.writeString(value.setScale(CURRENCY_SCALE, RoundingMode.HALF_UP).toPlainString());
                } else {
                    gen.writeString(value.toPlainString());
                }
            }
        }
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property)
        throws JsonMappingException {
        JsonFormat.Value format = findFormatOverrides(prov, property, handledType());
        if (format == null) {
            return this;
        }

        if (format.hasPattern()) {
            DecimalFormat customFormat = new DecimalFormat(format.getPattern());
            customFormat.setRoundingMode(RoundingMode.HALF_UP);
            return new CustomBigDecimalSerializer(customFormat);
        }

        return this;
    }

    @Override
    protected JsonFormat.Value findFormatOverrides(SerializerProvider provider, BeanProperty prop,
        Class<?> typeForDefaults) {
        if (prop != null) {
            return prop.findPropertyFormat(provider.getConfig(), typeForDefaults);
        }
        return provider.getDefaultPropertyFormat(typeForDefaults);
    }
}
