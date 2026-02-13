package com.gnn.newsnetwork.GnnNewsNetworkApplication.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class HomeNewsResponseDto {

    private Long id;
    private String title;
    private String shortDescription;
    private String fullContext;
    private String category;
    private String type; // STORY or DIGITAL

    private String anchorName; // only if digital

    private List<String> mediaUrls;
    private String audioUrl;

    private String state;
    private String city;

    private LocalDateTime createdAt;
}
