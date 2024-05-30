package top.sephy.infra.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableLogic;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractBaseLogicDeleteEntity<K extends Serializable> extends AbstractBaseEntity<K> {

    @TableLogic(value = "0", delval = "UNIX_TIMESTAMP()")
    protected Integer deleted;

}
