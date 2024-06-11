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
package top.sephy.infra.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.sephy.infra.mybatis.audit.annotaton.CreatedTime;
import top.sephy.infra.mybatis.audit.annotaton.CreatorId;
import top.sephy.infra.mybatis.audit.annotaton.ModifiedTime;
import top.sephy.infra.mybatis.audit.annotaton.ModifierId;

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
