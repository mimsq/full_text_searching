package com.serching.fulltextsearching.dto;

import lombok.Data;

import java.util.List;

@Data
public class EsSearchResult {
    private List<String> ids;
    private long total;
}