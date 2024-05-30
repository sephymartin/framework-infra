package top.sephy.infra.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import top.sephy.infra.mybatis.audit.annotaton.CreatedTime;
import top.sephy.infra.mybatis.audit.annotaton.CreatorId;
import top.sephy.infra.mybatis.audit.annotaton.ModifiedTime;
import top.sephy.infra.mybatis.audit.annotaton.ModifierId;
import lombok.Data;

/**
 * @author sephy
 * @date 2020-06-13 23:45
 */
@Data
public abstract class AbstractEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -6362917626913479219L;
    /**
     * 创建人
     */
    @CreatorId
    protected Long createdBy;

    /**
     * 创建时间
     */
    @CreatedTime
    protected LocalDateTime createdTime;

    // @Created
    // protected Date createdTime;

    /**
     * 创建日期
     */
    @ModifierId
    protected Long updatedBy;

    /**
     * 更新时间
     */
    @ModifiedTime
    protected LocalDateTime updatedTime;

    // @Modified
    // protected Date updatedTime;
}
