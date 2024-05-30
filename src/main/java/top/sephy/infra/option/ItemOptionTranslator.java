package top.sephy.infra.option;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import top.sephy.infra.option.annotation.TranslateItemOption;
import lombok.Data;

public class ItemOptionTranslator {

    private ItemOptionProvider<Object, Object> optionProvider;

    private ConcurrentHashMap<Class<?>, List<TranslationMeta>> cache = new ConcurrentHashMap<>();

    public ItemOptionTranslator(ItemOptionProvider<Object, Object> optionProvider) {
        this.optionProvider = optionProvider;
    }

    public void translate(Object obj) {
        Class<?> klass = obj.getClass();
        List<TranslationMeta> metaList = cache.get(klass);
        if (metaList == null) {
            metaList = prepareMeta(klass);
        }
        if (!metaList.isEmpty()) {
            BeanWrapper beanWrapper = new BeanWrapperImpl(obj);
            for (TranslationMeta meta : metaList) {
                String keyFieldName = meta.getKeyFieldName();
                String valueFieldName = meta.getValueFieldName();
                String type = meta.getType();
                if (StringUtils.hasText(valueFieldName) && StringUtils.hasText(keyFieldName)) {
                    Object keyPropertyValue = beanWrapper.getPropertyValue(keyFieldName);
                    if (keyPropertyValue != null) {
                        Object value = optionProvider.getV(type, keyPropertyValue);
                        if (value != null) {
                            beanWrapper.setPropertyValue(valueFieldName, value);
                        } else {
                            beanWrapper.setPropertyValue(valueFieldName, meta.getDefaultValue());
                        }
                    }
                }
            }
        }
    }

    private synchronized List<TranslationMeta> prepareMeta(Class<?> klass) {
        List<TranslationMeta> list = new ArrayList<>();
        ReflectionUtils.doWithFields(klass, field -> {
            TranslateItemOption annotation = field.getAnnotation(TranslateItemOption.class);
            if (annotation != null) {
                TranslationMeta translationMeta = new TranslationMeta();
                translationMeta.setType(annotation.type());
                translationMeta.setKeyFieldName(annotation.keyFieldName());
                translationMeta.setDefaultValue(annotation.defaultValue());
                translationMeta.setValueFieldName(field.getName());
                list.add(translationMeta);
            }
            if (list.isEmpty()) {
                cache.put(klass, Collections.emptyList());
            } else {
                cache.put(klass, list);
            }
        });
        return list;
    }

    @Data
    private static class TranslationMeta {

        private String type;

        private String keyFieldName;

        private String valueFieldName;

        private String defaultValue;
    }
}
