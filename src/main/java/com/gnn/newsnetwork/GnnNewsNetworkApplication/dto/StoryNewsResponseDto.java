package com.gnn.newsnetwork.GnnNewsNetworkApplication.dto;

import com.gnn.newsnetwork.GnnNewsNetworkApplication.enums.NewsStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class StoryNewsResponseDto {
    private Long id;
    private String title;
    private String shortDescription;
    private String fullContext;
    private String category;
    private NewsStatus status;
    private String state;
    private String city;


    private List<String> mediaUrls;

    private LocalDateTime createdAt;
    private LocalDateTime publishedAt; // add this
}
