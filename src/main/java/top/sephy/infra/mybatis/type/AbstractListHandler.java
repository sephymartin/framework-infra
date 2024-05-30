package top.sephy.infra.mybatis.type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractListHandler<E extends Serializable>
    extends AbstractComaStringCollectionHandler<E, List<E>> {

    @Override
    List<E> empty() {
        return Collections.emptyList();
    }

    @Override
    List<E> newCollection() {
        return new ArrayList<>();
    }
}
