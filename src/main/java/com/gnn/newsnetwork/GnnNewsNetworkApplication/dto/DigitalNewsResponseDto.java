package com.gnn.newsnetwork.GnnNewsNetworkApplication.dto;

import com.gnn.newsnetwork.GnnNewsNetworkApplication.enums.NewsStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class DigitalNewsResponseDto {
    private Long id;
    private String title;
    private String shortDescription;
    private String anchorName;
    private String category;
    private NewsStatus status;

    private List<String> mediaUrls;
    private String audioUrl;

    private LocalDateTime createdAt;
    private LocalDateTime publishedAt; // add this

    private String state;
    private String city;
}
