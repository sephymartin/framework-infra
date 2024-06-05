package top.sephy.infra.jackson.ser;

import java.io.IOException;
import java.util.Collection;

import org.springframework.core.convert.ConversionService;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import lombok.NonNull;
import top.sephy.infra.jackson.annotation.JsonDict;
import top.sephy.infra.jackson.annotation.JsonDictMeta;
import top.sephy.infra.option.DictEntry;
import top.sephy.infra.option.DictEntryProvider;

public class JsonDictSerializer extends StdSerializer<Object> {

    private static final long serialVersionUID = 2328857487201823680L;

    private final DictEntryProvider<Object, Object> dictEntryProvider;

    private final JsonDictMeta jsonDictMeta;

    private final ConversionService conversionService;

    public JsonDictSerializer(@NonNull DictEntryProvider<Object, Object> dictEntryProvider,
        @NonNull JsonDictMeta jsonDictMeta, @NonNull ConversionService conversionService) {
        super(Object.class);
        this.dictEntryProvider = dictEntryProvider;
        this.jsonDictMeta = jsonDictMeta;
        this.conversionService = conversionService;
    }

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        // 先输出原始 value 值
        gen.writeObject(value);

        // 输出要添加 label 值
        gen.writeFieldName(jsonDictMeta.getLabelFieldName());

        JsonDict annotation = jsonDictMeta.getAnnotation();
        Object defaultLabelVal = conversionService.convert(annotation.defaultLabelValue(), annotation.labelClass());

        // 数组或者集合类型, 需要逐个输出
        if (value instanceof Collection<?>) {
            Collection<?> collection = (Collection<?>)value;
            writeArray(gen, collection.toArray(), annotation, defaultLabelVal);
        } else if (value instanceof Object[]) {
            Object[] objects = (Object[])value;
            writeArray(gen, objects, annotation, defaultLabelVal);
        } else {
            DictEntry<Object, Object> option = dictEntryProvider.option(annotation.type(), value,
                annotation.compareWithString(), annotation.caseSensitive());
            Object val = option == null ? defaultLabelVal : option.getLabel();
            if (val == null) {
                val = defaultLabelVal;
            }
            if (jsonDictMeta.isWriteString()) {
                gen.writeString(String.valueOf(val));
            } else {
                gen.writeObject(val);
            }
        }
    }

    private void writeArray(JsonGenerator gen, Object[] array, JsonDict annotation, Object defaultLabelVal)
        throws IOException {
        gen.writeStartArray();
        for (Object orig : array) {
            DictEntry<Object, Object> option = dictEntryProvider.option(annotation.type(), orig,
                annotation.compareWithString(), annotation.caseSensitive());
            Object val = option == null ? defaultLabelVal : option.getLabel();
            if (val == null) {
                val = defaultLabelVal;
            }
            if (jsonDictMeta.isWriteString()) {
                gen.writeString(String.valueOf(val));
            } else {
                gen.writeObject(val);
            }
        }
        gen.writeEndArray();
    }
}
