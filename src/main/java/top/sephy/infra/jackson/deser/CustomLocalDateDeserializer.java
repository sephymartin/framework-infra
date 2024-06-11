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
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.util.ClassUtil;

public class CustomLocalDateDeserializer extends StdDeserializer<LocalDate> implements ContextualDeserializer {

    public static final CustomLocalDateDeserializer INSTANCE =
        new CustomLocalDateDeserializer(null, JsonFormat.Shape.NUMBER_INT);

    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    @Serial
    private static final long serialVersionUID = -9131249230782845132L;

    private final DateTimeFormatter _formatter;

    /**
     * Setting that indicates the {@Link JsonFormat.Shape} specified for this deserializer as a {@link JsonFormat.Shape}
     * annotation on property or class, or due to per-type "config override", or from global settings: If Shape is
     * NUMBER_INT, the input value is considered to be epoch days. If not a NUMBER_INT, and the deserializer was not
     * specified with the leniency setting of true, then an exception will be thrown.
     *
     * @since 2.11
     */
    private final JsonFormat.Shape _shape;

    private CustomLocalDateDeserializer(DateTimeFormatter _formatter, JsonFormat.Shape _shape) {
        super(LocalDateTime.class);
        this._formatter = _formatter;
        this._shape = _shape;
    }

    // @Override
    // public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException,
    // JsonProcessingException {
    // return LocalDateTime.ofInstant(Instant.ofEpochMilli(p.getValueAsLong()), ZoneId.systemDefault());
    // }

    @Override
    public LocalDate deserialize(JsonParser parser, DeserializationContext context) throws IOException {

        if (parser.hasTokenId(JsonTokenId.ID_STRING)) {
            return _fromString(parser, context, parser.getText());
        }
        // 30-Sep-2020, tatu: New! "Scalar from Object" (mostly for XML)
        if (parser.isExpectedStartObjectToken()) {
            return _fromString(parser, context, context.extractScalarFromObject(parser, this, handledType()));
        }

        if (parser.hasToken(JsonToken.VALUE_EMBEDDED_OBJECT)) {
            return (LocalDate)parser.getEmbeddedObject();
        }

        if (parser.hasToken(JsonToken.VALUE_NUMBER_INT)) {
            return LocalDate.ofInstant(Instant.ofEpochMilli(parser.getValueAsLong()), ZoneId.systemDefault());
        }

        return _handleUnexpectedToken(context, parser, "Expected array or string.");
    }

    protected <R> R _handleUnexpectedToken(DeserializationContext context, JsonParser parser, String message,
        Object... args) throws JsonMappingException {
        try {
            return (R)context.handleUnexpectedToken(handledType(), parser.getCurrentToken(), parser, message, args);

        } catch (JsonMappingException e) {
            throw e;
        } catch (IOException e) {
            throw JsonMappingException.fromUnexpectedIOE(e);
        }
    }

    protected LocalDate _fromString(JsonParser p, DeserializationContext ctxt, String string0) throws IOException {
        String string = string0.trim();
        if (string.isEmpty()) {
            // 22-Oct-2020, tatu: not sure if we should pass original (to distinguish
            // b/w empty and blank); for now don't which will allow blanks to be
            // handled like "regular" empty (same as pre-2.12)
            return null;
        }
        try {
            // 21-Oct-2020, tatu: Changed as per [modules-base#94] for 2.12,
            // had bad timezone handle change from [modules-base#56]
            if (_formatter == DEFAULT_FORMATTER) {
                // ... only allow iff lenient mode enabled since
                // JavaScript by default includes time and zone in JSON serialized Dates (UTC/ISO instant format).
                // And if so, do NOT use zoned date parsing as that can easily produce
                // incorrect answer.
                if (string.length() > 10 && string.charAt(10) == 'T') {
                    if (string.endsWith("Z")) {
                        if (isLenient()) {
                            return LocalDate.parse(string.substring(0, string.length() - 1), _formatter);
                        }
                        return (LocalDate)ctxt.handleWeirdStringValue(LocalDateTime.class, string,
                            "Should not contain offset when 'strict' mode set for property or type (enable 'lenient' handling to allow)");
                    }
                }
            }

            return LocalDate.parse(string, _formatter);
        } catch (DateTimeException e) {
            return _handleDateTimeException(ctxt, e, string);
        }
    }

    protected <R> R _handleDateTimeException(DeserializationContext context, DateTimeException e0, String value)
        throws JsonMappingException {
        try {
            return (R)context.handleWeirdStringValue(handledType(), value, "Failed to deserialize %s: (%s) %s",
                handledType().getName(), e0.getClass().getName(), e0.getMessage());

        } catch (JsonMappingException e) {
            e.initCause(e0);
            throw e;
        } catch (IOException e) {
            if (null == e.getCause()) {
                e.initCause(e0);
            }
            throw JsonMappingException.fromUnexpectedIOE(e);
        }
    }

    protected LocalDateTime _failForNotLenient(JsonParser p, DeserializationContext ctxt, JsonToken expToken)
        throws IOException {
        return (LocalDateTime)ctxt.handleUnexpectedToken(handledType(), expToken, p,
            "Cannot deserialize instance of %s out of %s token: not allowed because 'strict' mode set for property or type (enable 'lenient' handling to allow)",
            ClassUtil.nameOf(handledType()), p.currentToken());
    }

    @Override
    public CustomLocalDateDeserializer createContextual(DeserializationContext ctxt, BeanProperty property)
        throws JsonMappingException {
        JsonFormat.Value format = findFormatOverrides(ctxt, property, handledType());
        return (format == null) ? this : _withFormatOverrides(ctxt, property, format);
    }

    protected JsonFormat.Value findFormatOverrides(DeserializationContext ctxt, BeanProperty prop,
        Class<?> typeForDefaults) {
        if (prop != null) {
            return prop.findPropertyFormat(ctxt.getConfig(), typeForDefaults);
        }
        // even without property or AnnotationIntrospector, may have type-specific defaults
        return ctxt.getDefaultPropertyFormat(typeForDefaults);
    }

    protected CustomLocalDateDeserializer _withFormatOverrides(DeserializationContext ctxt, BeanProperty property,
        JsonFormat.Value formatOverrides) {
        CustomLocalDateDeserializer deser = this;

        // 17-Aug-2019, tatu: For 2.10 let's start considering leniency/strictness too
        if (formatOverrides.hasLenient()) {
            Boolean leniency = formatOverrides.getLenient();
            if (leniency != null) {
                deser = deser.withLeniency(leniency);
            }
        }
        if (formatOverrides.hasPattern()) {
            final String pattern = formatOverrides.getPattern();
            final Locale locale = formatOverrides.hasLocale() ? formatOverrides.getLocale() : ctxt.getLocale();
            DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
            if (acceptCaseInsensitiveValues(ctxt, formatOverrides)) {
                builder.parseCaseInsensitive();
            }
            builder.appendPattern(pattern);
            DateTimeFormatter df;
            if (locale == null) {
                df = builder.toFormatter();
            } else {
                df = builder.toFormatter(locale);
            }

            // [#148]: allow strict parsing
            if (!deser.isLenient()) {
                df = df.withResolverStyle(ResolverStyle.STRICT);
            }

            // [#69]: For instant serializers/deserializers we need to configure the formatter with
            // a time zone picked up from JsonFormat annotation, otherwise serialization might not work
            if (formatOverrides.hasTimeZone()) {
                df = df.withZone(formatOverrides.getTimeZone().toZoneId());
            }
            deser = deser.withDateFormat(df);
        }
        // [#58]: For LocalDate deserializers we need to configure the formatter with
        // a shape picked up from JsonFormat annotation, to decide if the value is EpochSeconds
        JsonFormat.Shape shape = formatOverrides.getShape();
        if (shape != null && shape != _shape) {
            deser = deser.withShape(shape);
        }
        // any use for TimeZone?

        return deser;
    }

    protected CustomLocalDateDeserializer withLeniency(Boolean leniency) {
        return new CustomLocalDateDeserializer(this._formatter, this._shape);
    }

    private boolean acceptCaseInsensitiveValues(DeserializationContext ctxt, JsonFormat.Value format) {
        Boolean enabled = format.getFeature(JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_VALUES);
        if (enabled == null) {
            enabled = ctxt.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_VALUES);
        }
        return enabled;
    }

    protected boolean isLenient() {
        return false;
    }

    protected CustomLocalDateDeserializer withDateFormat(DateTimeFormatter formatter) {
        return new CustomLocalDateDeserializer(formatter, null);
    }

    protected CustomLocalDateDeserializer withShape(JsonFormat.Shape shape) {
        return new CustomLocalDateDeserializer(null, shape);
    }
}