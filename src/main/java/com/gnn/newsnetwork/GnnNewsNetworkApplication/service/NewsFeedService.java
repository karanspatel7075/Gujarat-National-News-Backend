package com.gnn.newsnetwork.GnnNewsNetworkApplication.service;

import com.gnn.newsnetwork.GnnNewsNetworkApplication.dto.DigitalNewsResponseDto;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.dto.HomeNewsResponseDto;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.dto.StoryNewsResponseDto;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.entity.Media;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.entity.News;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.enums.MediaType;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.enums.NewsStatus;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.enums.TypeOfNews;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsFeedService {

    private final NewsRepository newsRepository;

    public List<StoryNewsResponseDto> getStoryNews() {
        return newsRepository.findByTypeOfNewsAndStatusOrderByCreatedAtDesc(TypeOfNews.STORY, NewsStatus.PUBLISHED)
                .stream()
                .map(this::mapToStoryDto)
                .toList();
    }

    public List<DigitalNewsResponseDto> getDigitalNews() {
        return newsRepository.findByTypeOfNewsAndStatusOrderByCreatedAtDesc(TypeOfNews.DIGITAL, NewsStatus.PUBLISHED)
                .stream()
                .map(this::mapToDigitalDto)
                .toList();
    }

    public List<HomeNewsResponseDto> getHomePageNews() {
//        List<News> newsList = newsRepository.findByStatusOrderByCreatedAtDesc(NewsStatus.PUBLISHED);

        List<News> newsList =
                newsRepository.findPublishedWithMedia(NewsStatus.PUBLISHED);
        // Shuffle to make random
        Collections.shuffle(newsList);

        return  newsList.stream()
                .limit(30)
                .map(this::mapToHomeDto)
                .toList();
    }

    // Learning : LazyInitializationException: could not initialize proxy - no Session
    // Hibernate said: “You are asking me to load mediaList, but the database connection is already closed.”

    // What Does JOIN FETCH Do?
    // It tells Hibernate: "While loading News, also load mediaList at the same time."

    public List<String> getHomepageCategories() {
        return newsRepository.findDistinctPublishedCategories();
    }

    public List<DigitalNewsResponseDto> getLatest10Videos() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

        Page<News> page = newsRepository.findPublishedVideoNews(NewsStatus.PUBLISHED, pageable);

        return page.getContent()
                .stream()
                .map(this::mapToVideoDto)
                .toList();
    }

    public List<DigitalNewsResponseDto> getSlidingVideos() {

        Pageable pageable = PageRequest.of(1,  // page 1 (skip first 10)
                10, // next 10
                Sort.by("createdAt").descending()
        );

        Page<News> page = newsRepository
                .findPublishedVideoNews(NewsStatus.PUBLISHED, pageable);

        return page.getContent()
                .stream()
                .map(this::mapToVideoDto)
                .toList();
    }


    private DigitalNewsResponseDto mapToVideoDto(News news) {
        List<String> videoUrls = news.getMediaList()
                .stream()
                .filter(m -> m.getMediaType() == MediaType.VIDEO)
                .map(Media::getMediaUrl)
                .toList();

        return DigitalNewsResponseDto.builder()
                .id(news.getId())
                .title(news.getTitle())
                .category(news.getCategory())
                .state(news.getState())
                .city(news.getCity())
                .mediaUrls(videoUrls)
                .createdAt(news.getCreatedAt())
                .build();
    }


    private HomeNewsResponseDto mapToHomeDto(News news) {
        String audioUrl = news.getMediaList()
                .stream()
                .filter(m -> m.getMediaType() == MediaType.AUDIO)
                .map(Media::getMediaUrl)
                .findFirst()
                .orElse(null);

        List<String> mediaUrls = news.getMediaList()
                .stream()
                .filter(m -> m.getMediaType() != MediaType.AUDIO)
                .map(Media::getMediaUrl)
                .toList();

        return HomeNewsResponseDto.builder()
                .id(news.getId())
                .title(news.getTitle())
                .shortDescription(news.getShortDescription())
                .fullContext(news.getFullContext())
                .category(news.getCategory())
                .type(news.getTypeOfNews().name())
                .anchorName(news.getAnchorName())
                .mediaUrls(mediaUrls)
                .audioUrl(audioUrl)
                .state(news.getState())
                .city(news.getCity())
                .createdAt(news.getCreatedAt())
                .build();
    }

    private StoryNewsResponseDto mapToStoryDto(News news) {
        return StoryNewsResponseDto.builder()
                .id(news.getId())
                .title(news.getTitle())
                .shortDescription(news.getShortDescription())
                .fullContext(news.getFullContext())
                .category(news.getCategory())
                .state(news.getState())
                .city(news.getCity())
                .mediaUrls(
                        news.getMediaList()
                                .stream()
                                .filter(m -> m.getMediaType() != MediaType.AUDIO)
                                .map(Media::getMediaUrl)
                                .toList()
                )
                .createdAt(news.getCreatedAt())
                .build();
    }

    private DigitalNewsResponseDto mapToDigitalDto(News news) {
        String audioUrl = news.getMediaList()
                .stream()
                .filter(m -> m.getMediaType() == MediaType.AUDIO)
                .map(Media::getMediaUrl)
                .findFirst()
                .orElse(null);

        List<String> mediaUrls = news.getMediaList()
                .stream()
                .filter(m -> m.getMediaType() != MediaType.AUDIO)
                .map(Media::getMediaUrl)
                .toList();

        return DigitalNewsResponseDto.builder()
                .id(news.getId())
                .title(news.getTitle())
                .shortDescription(news.getShortDescription())
                .anchorName(news.getAnchorName())
                .category(news.getCategory())
                .mediaUrls(mediaUrls)
                .state(news.getState())
                .city(news.getCity())
                .audioUrl(audioUrl)
                .createdAt(news.getCreatedAt())
                .build();
    }

//    public List<>
}
