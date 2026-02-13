package com.gnn.newsnetwork.GnnNewsNetworkApplication.controller;

import com.gnn.newsnetwork.GnnNewsNetworkApplication.dto.DigitalNewsResponseDto;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.dto.HomeNewsResponseDto;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.dto.StoryNewsResponseDto;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.service.NewsFeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/homepage")
@RequiredArgsConstructor
public class HomeController {

    private final NewsFeedService newsFeedService;

//    GET /homepage           → mixed random news
//    GET /homepage/story     → only story
//    GET /homepage/digital   → only digital

    @GetMapping
    public ResponseEntity<List<HomeNewsResponseDto>> getHomeNews() {
        System.out.println("All news on Homepage successfully fetched !");
        return ResponseEntity.ok(newsFeedService.getHomePageNews());
    }

    @GetMapping("/story")
    public ResponseEntity<List<StoryNewsResponseDto>> getStoryNews() {
        return ResponseEntity.ok(newsFeedService.getStoryNews());
    }

    @GetMapping("/digital")
    public ResponseEntity<List<DigitalNewsResponseDto>> getDigitalNews() {
        return ResponseEntity.ok(newsFeedService.getDigitalNews());
    }

    @GetMapping("getCategories")
    public ResponseEntity<List<String>> getCategories() {
        return ResponseEntity.of(Optional.ofNullable(newsFeedService.getHomepageCategories()));
    }

    @GetMapping("/videos")
    public ResponseEntity<List<DigitalNewsResponseDto>> getLatestVideos() {
        return ResponseEntity.ok(newsFeedService.getLatest10Videos());
    }

    @GetMapping("/videos/sliding")
    public ResponseEntity<List<DigitalNewsResponseDto>> getSlidingVideos() {
        return ResponseEntity.ok(newsFeedService.getSlidingVideos());
    }

    // GET /homepage/videos
    // returns top 10 (latest)

    // GET /homepage/videos/sliding
    // returns next 10 (older ones)


}
