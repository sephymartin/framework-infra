package top.sephy.infra.mybatis.type;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractSetHandler<E extends Serializable>
    extends AbstractComaStringCollectionHandler<E, Set<E>> {

    @Override
    Set<E> empty() {
        return Collections.emptySet();
    }

    @Override
    Set<E> newCollection() {
        return new HashSet<>();
    }
}