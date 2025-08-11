package com.serching.fulltextsearching.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult<T> {
    private List<T> records;    // 数据列表
    private long total;         // 总记录数
    private long size;          // 每页显示条数
    private long current;       // 当前页
    private long pages;         // 总页数
}