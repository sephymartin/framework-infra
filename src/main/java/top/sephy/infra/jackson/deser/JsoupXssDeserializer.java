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
