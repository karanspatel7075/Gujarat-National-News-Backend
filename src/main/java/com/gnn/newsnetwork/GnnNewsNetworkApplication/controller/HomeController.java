package com.gnn.newsnetwork.GnnNewsNetworkApplication.controller;

import com.gnn.newsnetwork.GnnNewsNetworkApplication.dto.DigitalNewsResponseDto;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.dto.HomeNewsResponseDto;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.dto.StoryNewsResponseDto;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.entity.News;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.repository.NewsRepository;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.service.NewsFeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/homepage")
@RequiredArgsConstructor
public class HomeController {

    private final NewsFeedService newsFeedService;
    private final NewsRepository newsRepository;

//    GET /homepage           → mixed random news
//    GET /homepage/story     → only story
//    GET /homepage/digital   → only digital

    @GetMapping
    public ResponseEntity<List<HomeNewsResponseDto>> getHomeNews() {
        System.out.println("All news on Homepage successfully fetched !");
        return ResponseEntity.ok(newsFeedService.getHomePageNews());
    }

    @GetMapping("/news/{id}")
    public ResponseEntity<HomeNewsResponseDto> getSingleNews(@PathVariable Long id) {
        News news = newsRepository.findByIdWithMedia(id)
                .orElseThrow(() -> new RuntimeException("News not found with id: " + id));
         return ResponseEntity.ok(newsFeedService.mapToHomeDto(news));
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

    @GetMapping("/by-category")
    public ResponseEntity<List<HomeNewsResponseDto>> getNewsByCategory(
            @RequestParam String category) {

        return ResponseEntity.ok(
                newsFeedService.getNewsByCategory(category)
        );
    }

    @GetMapping("/videos")
    public ResponseEntity<List<DigitalNewsResponseDto>> getLatestVideos() { // Remove this part soon
        return ResponseEntity.ok(newsFeedService.getLatest10Videos());
    }

    @GetMapping("/videos/sliding")
    public ResponseEntity<List<DigitalNewsResponseDto>> getSlidingVideos() {
        return ResponseEntity.ok(newsFeedService.getSlidingVideos());
    }

    @GetMapping("/by-cities")
    public ResponseEntity<List<HomeNewsResponseDto>> getNewsByCities(@RequestParam List<String> cities)  {

        return ResponseEntity.ok(newsFeedService.getNewsByCities(cities));
    }

    // GET /homepage/videos
    // returns top 10 (latest)

    // GET /homepage/videos/sliding
    // returns next 10 (older ones)
}
