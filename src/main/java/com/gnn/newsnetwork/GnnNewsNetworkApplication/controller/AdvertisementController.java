package com.gnn.newsnetwork.GnnNewsNetworkApplication.controller;

import com.gnn.newsnetwork.GnnNewsNetworkApplication.entity.Advertisement;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.enums.AdPosition;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.service.AdvertisementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ads")
@RequiredArgsConstructor
public class AdvertisementController {
    private final AdvertisementService advertisementService;

    @GetMapping("/{position}")
    public ResponseEntity<List<Advertisement>> getAds(
            @PathVariable AdPosition position) {

        return ResponseEntity.ok(
                advertisementService.getActiveAdsByPosition(position)
        );
    }
}
