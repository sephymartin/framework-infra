package top.sephy.infra.beanmapper;

import java.util.Collection;
import java.util.List;

public interface BeanMapperOperations {

    void map(Object source, Object destination);

    <D> D map(Object source, Class<D> destinationType);

    void mapIgnoreNull(Object source, Object destination);

    <D> List<D> mapToList(Collection<?> sourceList, Class<D> destinationType);
}
