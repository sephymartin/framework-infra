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
package top.sephy.infra.paging;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.sephy.infra.mybatis.query.IgnoreQuery;

@Data
public class PagingQuery {

    /**
     * 页码, 默认从1开始
     */
    @Schema(description = "页码, 默认从1开始")
    @IgnoreQuery
    private int pageNum = 1;

    /**
     * 分页大小
     */
    @Schema(description = "分页大小")
    @IgnoreQuery
    private int pageSize = 20;
}
