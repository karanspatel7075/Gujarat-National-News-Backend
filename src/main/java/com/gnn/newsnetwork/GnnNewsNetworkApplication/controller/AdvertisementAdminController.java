package com.gnn.newsnetwork.GnnNewsNetworkApplication.controller;

import com.gnn.newsnetwork.GnnNewsNetworkApplication.dto.AdvertisementRequestDto;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.dto.AdvertisementResponseDto;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.entity.Advertisement;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.service.AdvertisementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/ads")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdvertisementAdminController {

    private final AdvertisementService advertisementService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AdvertisementResponseDto> createAd(@ModelAttribute AdvertisementRequestDto dto) {

        Advertisement ad = advertisementService.createAd(dto);

        AdvertisementResponseDto response = AdvertisementResponseDto.builder()
                .id(ad.getId())
                .title(ad.getTitle())
                .imageUrl(ad.getImageUrl())
                .redirectUrl(ad.getRedirectUrl())
                .position(ad.getPosition())
                .active(ad.isActive())
                .startDate(ad.getStartDate())
                .endDate(ad.getEndDate())
                .createdAt(ad.getCreatedAt())
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<String> deactivate(@PathVariable Long id) {
        advertisementService.deactivateAd(id);
        return ResponseEntity.ok("Ad deactivated");
    }
}
