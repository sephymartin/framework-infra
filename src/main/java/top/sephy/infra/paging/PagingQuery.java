package top.sephy.infra.paging;

import top.sephy.infra.mybatis.query.IgnoreQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

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
