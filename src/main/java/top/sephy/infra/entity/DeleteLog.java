package top.sephy.infra.entity;

import java.io.Serial;
import java.util.Collection;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("delete_log")
public class DeleteLog extends AbstractBaseEntity<Long> {

    @Serial
    private static final long serialVersionUID = 6410332327917931515L;

    private String tableName;

    private Long dataId;

    private String deleteContent;

    @TableField(exist = false)
    private Collection<Long> deleteIds;
}
