package top.sephy.infra.event;

import java.io.Serial;
import java.io.Serializable;

public abstract class AbstractEvent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1453756550680920375L;

    protected long timestamp;

    public AbstractEvent() {
        this(System.currentTimeMillis());
    }

    public AbstractEvent(long timestamp) {
        this.timestamp = timestamp;
    }
}
