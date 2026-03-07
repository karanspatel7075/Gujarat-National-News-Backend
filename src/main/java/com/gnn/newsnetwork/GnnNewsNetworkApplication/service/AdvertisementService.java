package com.gnn.newsnetwork.GnnNewsNetworkApplication.service;

import com.gnn.newsnetwork.GnnNewsNetworkApplication.dto.AdvertisementRequestDto;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.entity.Advertisement;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.enums.AdPosition;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.repository.AdvertisementRepository;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.repository.MediaStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdvertisementService {

    private final AdvertisementRepository advertisementRepository;
    private final MediaStorageService mediaStorageService;

    public Advertisement createAd(AdvertisementRequestDto dto) {

        MultipartFile file = dto.getImageFile();

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Image file required");
        }

        String imageUrl = mediaStorageService.uploadImage(file);

        Advertisement ad = Advertisement.builder()
                .title(dto.getTitle())
                .imageUrl(imageUrl)
                .redirectUrl(dto.getRedirectUrl())
                .position(dto.getPosition())
                .active(true)
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .createdAt(LocalDateTime.now())
                .build();

        return advertisementRepository.save(ad);
    }

//    public Advertisement createAd(AdvertisementRequestDto dto) {
//
//        MultipartFile file = dto.getImageFile();
//        if (file == null || file.isEmpty()) {
//            throw new RuntimeException("Image file is required");
//        }
//
//        // Validate file type
//        String contentType = file.getContentType();
//        if (contentType == null || !contentType.startsWith("image/")) {
//            throw new RuntimeException("Only image files are allowed");
//        }
//
//        // Prepare upload directory
//        String uploadDir = System.getProperty("user.dir") + "/uploads/ads";
//        File dir = new File(uploadDir);
//        if (!dir.exists()) dir.mkdirs();
//
//        String extension = "." + contentType.substring(contentType.lastIndexOf("/") + 1);
//        String fileName = UUID.randomUUID() + extension;
//        File destination = new File(dir, fileName);
//
//        try {
//            file.transferTo(destination);
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to upload image");
//        }
//
//        Advertisement ad = Advertisement.builder()
//                .title(dto.getTitle())
//                .imageUrl("/uploads/ads/" + fileName)
//                .redirectUrl(dto.getRedirectUrl())
//                .position(dto.getPosition())
//                .active(true)
//                .startDate(dto.getStartDate())
//                .endDate(dto.getEndDate())
//                .createdAt(LocalDateTime.now())
//                .build();
//
//        return advertisementRepository.save(ad);
//    }

    public List<Advertisement> getActiveAdsByPosition(AdPosition position) {
        LocalDateTime now = LocalDateTime.now();

        return advertisementRepository
                .findByActiveTrueAndPositionAndStartDateBeforeAndEndDateAfter(
                        position,
                        now,
                        now
                );
    }

    public void deactivateAd(Long id) {
        Advertisement ad = advertisementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ad not found"));

        ad.setActive(false);
        advertisementRepository.save(ad);
    }
}
