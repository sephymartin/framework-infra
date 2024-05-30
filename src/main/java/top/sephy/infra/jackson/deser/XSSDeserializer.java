package top.sephy.infra.jackson.deser;

import java.io.IOException;
import java.io.Serial;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;

import top.sephy.infra.utils.XSSUtils;

public class XSSDeserializer extends AbstractXSSDeserializer {

    @Serial
    private static final long serialVersionUID = -3922759210715477667L;
    public static XSSDeserializer INSTANCE = new XSSDeserializer();

    public XSSDeserializer() {}

    @Override
    protected String doDeserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        String val = StringUtils.trim(p.getValueAsString());
        return XSSUtils.stripXSS(val);
    }
}
