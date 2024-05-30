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
