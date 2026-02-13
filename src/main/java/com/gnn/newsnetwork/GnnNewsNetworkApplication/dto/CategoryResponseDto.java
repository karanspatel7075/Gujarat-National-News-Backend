package com.gnn.newsnetwork.GnnNewsNetworkApplication.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryResponseDto { // Integrate later after working normal
    private String category;
    private Long totalNews;
}
