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
package top.sephy.infra.beanmapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.modelmapper.ModelMapper;

public class BeanMapperTemplate implements BeanMapperOperations {

    private ModelMapper modelMapper;

    private ModelMapper ignoreNullModelMapper;

    public BeanMapperTemplate(ModelMapper modelMapper, ModelMapper ignoreNullModelMapper) {
        this.modelMapper = modelMapper;
        this.ignoreNullModelMapper = ignoreNullModelMapper;
    }

    @Override
    public void map(Object source, Object destination) {
        if (source == null || destination == null) {
            return;
        }
        modelMapper.map(source, destination);
    }

    @Override
    public <D> D map(Object source, Class<D> destinationType) {
        if (source == null) {
            return null;
        }
        try {
            D destination = destinationType.newInstance();
            modelMapper.map(source, destination);
            return destination;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new BeanMapperException("bean map failed.", e);
        }
    }

    @Override
    public void mapIgnoreNull(Object source, Object destination) {
        if (source == null || destination == null) {
            return;
        }
        ignoreNullModelMapper.map(source, destination);
    }

    @Override
    public <D> List<D> mapToList(Collection<?> sourceList, Class<D> destinationType) {
        if (sourceList == null || sourceList.isEmpty()) {
            return Collections.emptyList();
        }
        List<D> arrayList = new ArrayList<>(sourceList.size());
        for (Object o : sourceList) {
            arrayList.add(map(o, destinationType));
        }
        return arrayList;
    }
}
