package top.sephy.infra.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;

import top.sephy.infra.mybatis.audit.annotaton.CreatedTime;
import top.sephy.infra.mybatis.audit.annotaton.CreatorId;
import top.sephy.infra.mybatis.audit.annotaton.ModifiedTime;
import top.sephy.infra.mybatis.audit.annotaton.ModifierId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public abstract class AbstractBaseLogicDeleteVersionEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 6479935566765181999L;

    @Schema(description = "主键")
    @TableId(type = IdType.AUTO)
    protected Long id;
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

    @TableLogic(value = "0", delval = "UNIX_TIMESTAMP()")
    protected Integer deleted;

    @Version
    protected Integer version;
}
