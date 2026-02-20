package com.gnn.newsnetwork.GnnNewsNetworkApplication.dto;

import com.gnn.newsnetwork.GnnNewsNetworkApplication.enums.NewsStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoryNewsResponseDto {

    private Long id;
    // Original Gujarati content
    private String title;
    private String shortDescription;
    private String fullContext;
    private String category;
    private String state;
    private String city;

    private NewsStatus status;
    private List<String> mediaUrls;

    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;
}
