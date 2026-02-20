package com.gnn.newsnetwork.GnnNewsNetworkApplication.dto;

import com.gnn.newsnetwork.GnnNewsNetworkApplication.enums.AdPosition;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AdvertisementResponseDto {
    private Long id;
    private String title;
    private String imageUrl;
    private String redirectUrl;
    private AdPosition position;
    private boolean active;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createdAt;
}
