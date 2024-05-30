package top.sephy.infra.jackson.ser;

import java.io.IOException;
import java.io.Serial;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import top.sephy.infra.option.ItemOptionProvider;
import lombok.NonNull;

public class JsonMappingLabelSerializer extends StdSerializer<Object> {

    @Serial
    private static final long serialVersionUID = 2328857487201823680L;
    private final ItemOptionProvider<Object, Object> itemOptionProvider;

    private final String type;

    private final String labelName;

    public JsonMappingLabelSerializer(@NonNull ItemOptionProvider<Object, Object> itemOptionProvider,
        @NonNull String type, @NonNull String labelName) {
        super(Object.class);
        this.itemOptionProvider = itemOptionProvider;
        this.type = type;
        this.labelName = labelName;
    }

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        // 先输出原始 value 值
        gen.writeObject(value);

        // 输出要添加 label 值
        gen.writeFieldName(labelName);

        // 数组或者集合类型, 需要逐个输出
        if (value instanceof Collection<?> collection) {
            gen.writeStartArray();
            Set<Object> filterValues = new LinkedHashSet<>(collection);
            Collection<Object> labelValues = itemOptionProvider.getOptionsMap(type, filterValues).values();
            for (Object labelValue : labelValues) {
                gen.writeObject(labelValue);
            }
            gen.writeEndArray();
        } else if (value instanceof Object[] objects) {
            gen.writeStartArray();
            Set<Object> filterValues = new LinkedHashSet<>(List.of(objects));
            Collection<Object> labelValues = itemOptionProvider.getOptionsMap(type, filterValues).values();
            for (Object labelValue : labelValues) {
                gen.writeObject(labelValue);
            }
            gen.writeEndArray();
        } else {
            Object labelValue = itemOptionProvider.getV(type, value);
            gen.writeObject(labelValue);
        }
    }
}
