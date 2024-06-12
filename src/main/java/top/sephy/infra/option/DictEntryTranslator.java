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
package top.sephy.infra.option;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import lombok.Data;
import top.sephy.infra.option.annotation.TranslateDict;

public class DictEntryTranslator {

    private DictEntryProvider<Object, Object> optionProvider;

    private ConcurrentHashMap<Class<?>, List<TranslationMeta>> cache = new ConcurrentHashMap<>();

    public DictEntryTranslator(DictEntryProvider<Object, Object> optionProvider) {
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
                        Object value = optionProvider.option(type, keyPropertyValue, meta.isCompareWithString(),
                            meta.isCaseSensitive());
                        if (value != null) {
                            beanWrapper.setPropertyValue(valueFieldName, value);
                        } else {
                            beanWrapper.setPropertyValue(valueFieldName, meta.getDefaultLabel());
                        }
                    }
                }
            }
        }
    }

    private synchronized List<TranslationMeta> prepareMeta(Class<?> klass) {
        List<TranslationMeta> metaList = cache.get(klass);
        if (metaList != null) {
            return metaList;
        }
        List<TranslationMeta> list = new ArrayList<>();
        ReflectionUtils.doWithFields(klass, field -> {
            TranslateDict annotation = field.getAnnotation(TranslateDict.class);
            if (annotation != null) {
                TranslationMeta translationMeta = new TranslationMeta();
                translationMeta.setType(annotation.type());
                translationMeta.setKeyFieldName(annotation.keyFieldName());
                translationMeta.setDefaultLabel(annotation.defaultValue());
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

        private String defaultLabel;

        private boolean compareWithString;

        private boolean caseSensitive;
    }
}
