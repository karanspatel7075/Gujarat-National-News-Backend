package com.gnn.newsnetwork.GnnNewsNetworkApplication.controller;

import com.gnn.newsnetwork.GnnNewsNetworkApplication.dto.DigitalNewsRequestDto;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.entity.News;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.entity.Users;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.service.DigitalNewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/digital-news")
@RequiredArgsConstructor
public class DigitalNewsController {

    private final DigitalNewsService digitalNewsService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<News> createDigitalNews(@ModelAttribute DigitalNewsRequestDto dto, @AuthenticationPrincipal Users editor) {
        return ResponseEntity.ok(digitalNewsService.createDigitalNews(dto, editor));
    }
}
