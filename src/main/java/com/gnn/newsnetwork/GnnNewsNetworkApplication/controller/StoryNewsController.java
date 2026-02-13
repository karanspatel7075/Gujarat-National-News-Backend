package com.gnn.newsnetwork.GnnNewsNetworkApplication.controller;

import com.gnn.newsnetwork.GnnNewsNetworkApplication.dto.ApiResponse;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.dto.StoryNewsRequestDto;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.entity.News;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.entity.Users;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.service.StoryNewsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@RequestMapping("/api/story-news")
@RequiredArgsConstructor
public class StoryNewsController {

    private final StoryNewsService storyNewsService;

//  Frontend must send multipart/form-data
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<News>> createStoryNews(@AuthenticationPrincipal Users editor, @ModelAttribute StoryNewsRequestDto dto) throws IOException {
        News news = storyNewsService.createStoryNews(dto, editor);
        return ResponseEntity.ok(
                ApiResponse.<News>builder()
                        .success(true)
                        .message("Story created successfully")
                        .data(news)
                        .build()
        );
    }
}
