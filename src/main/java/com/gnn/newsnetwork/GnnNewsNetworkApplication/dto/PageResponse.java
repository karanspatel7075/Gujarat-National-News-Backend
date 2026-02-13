package com.gnn.newsnetwork.GnnNewsNetworkApplication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageResponse<T> {

    private List<T> content; // Actual story news list
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;

    private String message; // 👈 informational message
}