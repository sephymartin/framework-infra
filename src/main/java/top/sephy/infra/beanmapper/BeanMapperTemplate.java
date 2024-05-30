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
