package top.sephy.infra.jackson.annotation;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class JsonDictMeta {

    private JsonDict annotation;

    private String labelFieldName;

    private boolean writeString;

    public JsonDictMeta(@NonNull JsonDict annotation, String labelFieldName) {
        this.annotation = annotation;
        this.labelFieldName = labelFieldName;
        this.writeString = annotation.labelClass() == String.class;
    }

}
