package top.sephy.infra.jackson;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import top.sephy.infra.jackson.annotation.JsonItemOptionObject;
import top.sephy.infra.jackson.annotation.JsonMappingLabel;
import top.sephy.infra.jackson.ser.JsonMappingLabelSerializer;
import top.sephy.infra.option.ItemOptionProvider;

/**
 * 实现 {@link JsonMappingLabel} 注解, 在序列化时自动添加字典标签
 */
@Slf4j
public class CustomPOJOSerializerModifier extends BeanSerializerModifier {

    private ConcurrentMap<Class<?>, Map<String, DictFiledMeta>> dictObjectClassCache = new ConcurrentHashMap<>();

    private ItemOptionProvider<Object, Object> itemOptionProvider;

    public CustomPOJOSerializerModifier(ItemOptionProvider<Object, Object> itemOptionProvider) {
        this.itemOptionProvider = itemOptionProvider;
    }

    public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc,
        List<BeanPropertyWriter> beanProperties) {

        Class<?> beanClass = beanDesc.getBeanClass();

        Map<String, DictFiledMeta> metaMap = dictObjectClassCache.computeIfAbsent(beanClass, klass -> {
            JsonItemOptionObject jsonItemOptionObject = beanDesc.getClassAnnotations().get(JsonItemOptionObject.class);
            if (jsonItemOptionObject == null) {
                return Collections.emptyMap();
            }

            Map<String, DictFiledMeta> map = new HashMap<>();
            for (BeanPropertyWriter beanProperty : beanProperties) {
                JsonMappingLabel annotation = beanProperty.getAnnotation(JsonMappingLabel.class);
                if (annotation != null) {
                    DictFiledMeta meta = new DictFiledMeta();
                    String labelName = annotation.labelFieldName();
                    if (!StringUtils.hasText(labelName)) {
                        labelName = beanProperty.getName() + "Label";
                    }
                    meta.setLabelName(labelName);
                    meta.setDictType(annotation.type());
                    meta.setDefaultLabel(annotation.defaultLabel());
                    meta.setCompareWithKeyString(annotation.compareWithKeyString());
                    map.put(beanProperty.getName(), meta);
                }
            }
            return map;
        });

        if (!metaMap.isEmpty()) {
            for (BeanPropertyWriter beanProperty : beanProperties) {
                DictFiledMeta meta = metaMap.get(beanProperty.getName());
                if (meta != null) {
                    if (log.isDebugEnabled()) {
                        log.debug("对 {} 的 {} 属性添加字典标签", beanClass, beanProperty.getName());
                    }
                    beanProperty.assignSerializer(new JsonMappingLabelSerializer(itemOptionProvider, meta.getDictType(),
                        meta.getLabelName(), meta.getDefaultLabel(), meta.isCompareWithKeyString()));
                    // JavaType type = beanProperty.getType();
                    // AnnotatedMember member = new VirtualAnnotatedMember(beanDesc.getClassInfo(),beanClass,
                    // labelName, type);
                    // ContextAttributes attributes = config.getAttributes();
                    // AttributePropertyWriter propertyWriter = AttributePropertyWriter.construct(dictType + "." +
                    // labelName, SimpleBeanPropertyDefinition.construct(config, member,
                    // PropertyName.construct(labelName)), type, );
                }
            }
        }

        return beanProperties;
    }

    @Data
    private static class DictFiledMeta {
        private String dictType;
        private String labelName;
        private boolean compareWithKeyString;
        private String defaultLabel;
    }
}
