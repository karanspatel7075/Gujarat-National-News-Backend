package com.gnn.newsnetwork.GnnNewsNetworkApplication.dto;

import com.gnn.newsnetwork.GnnNewsNetworkApplication.enums.AdPosition;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
public class AdvertisementRequestDto {
    private String title;
    private MultipartFile imageFile;  // Multipart file from frontend
    private String redirectUrl;
    private AdPosition position;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
