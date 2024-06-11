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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class PagingResult<T> implements Iterable<T> {

    // 页码
    private int pageNum;

    // 分页大小
    private int pageSize;

    // 总页数
    private int totalPages;

    // 总记录数
    private long total;

    // 分页内容
    private List<T> list;

    @JsonCreator
    public PagingResult(@JsonProperty("list") List<T> list, @JsonProperty("pageNum") int pageNum,
        @JsonProperty("pageSize") int pageSize, @JsonProperty("total") long total) {
        Assert.isTrue(pageNum > 0, "pageNum must be positive.");
        Assert.isTrue(pageSize > 0, "pageSize must be positive.");
        Assert.isTrue(total >= 0, "totalElements must net be negative.");
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.total = total;
        this.totalPages = (int)(total / pageSize + (total % pageSize == 0 ? 0 : 1));
        this.list = list == null ? Collections.EMPTY_LIST : list;
    }

    /**
     * 分页内容记录数
     * 
     * @return
     */
    public int getNumberOfElements() {
        return list.size();
    }

    /**
     * 是否有上一页
     * 
     * @return
     */
    public boolean hasPrevious() {
        return pageNum > 1;
    }

    /**
     * 是否有下一页
     * 
     * @return
     */
    public boolean hasNext() {
        return pageNum < totalPages;
    }

    /**
     * 是否是第一页
     * 
     * @return
     */
    public boolean isFirst() {
        return !hasPrevious();
    }

    /**
     * 是否是最后一页
     * 
     * @return
     */
    public boolean isLast() {
        return !hasNext();
    }

    /**
     * 是否有分页内容
     * 
     * @return
     */
    public boolean hasContent() {
        return !list.isEmpty();
    }

    @Override
    public @NonNull Iterator<T> iterator() {
        return list.iterator();
    }

    public static <T> PagingResult<T> fromPage(com.github.pagehelper.Page<T> page) {
        return new PagingResult<T>(page.getResult(), page.getPageNum(), page.getPageSize(), page.getTotal());
    }

    public static <T> PagingResult<T> fromPage(com.baomidou.mybatisplus.extension.plugins.pagination.Page<T> page) {
        return new PagingResult<T>(page.getRecords(), (int)page.getCurrent(), (int)page.getSize(), page.getTotal());
    }

    public static <T> PagingResult<T> empty(int pageNum, int pageSize) {
        return new PagingResult<T>(Collections.emptyList(), pageNum, pageSize, 0);
    }

    public static <T> PagingResult<T> empty(PagingQuery query) {
        return new PagingResult<T>(Collections.emptyList(), query.getPageNum(), query.getPageSize(), 0);
    }
}
