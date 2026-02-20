package com.gnn.newsnetwork.GnnNewsNetworkApplication.service;

import com.gnn.newsnetwork.GnnNewsNetworkApplication.common.NewsSpecification;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.dto.*;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.entity.Media;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.entity.News;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.enums.MediaType;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.exception.SearchOperationException;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.repository.NewsRepository;
import jakarta.persistence.criteria.JoinType;
import org.springframework.cache.annotation.Cacheable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsFilterService {

    private final NewsRepository newsRepository;

    @Cacheable(
            value = "filteredNews",
            key = "T(java.util.Objects).toString(#dto.state,'') + '-' + " +
                    "T(java.util.Objects).toString(#dto.city,'') + '-' + " +
                    "T(java.util.Objects).toString(#dto.category,'') + '-' + " +
                    "T(java.util.Objects).toString(#dto.keyword,'') + '-' + " +
                    "#dto.typeOfNews + '-' + #page + '-' + #size"
    )
    public PageResponse<HomeNewsResponseDto> filterNews(NewsFilterRequestDto dto, int page, int size) {

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

            // This is dynamic query building.
            Specification<News> spec = Specification
                    .where(NewsSpecification.isApproved())
                    .and(NewsSpecification.hasState(dto.getState()))
                    .and(NewsSpecification.hasCity(dto.getCity()))
                    .and(NewsSpecification.hasCategory(dto.getCategory()))
                    .and(NewsSpecification.hasKeyword(dto.getKeyword()))
                    .and(NewsSpecification.hasType(dto.getTypeOfNews()));

            Page<News> newsPage = newsRepository.findAll((root, query, cb) -> {
                root.fetch("mediaList", JoinType.LEFT);
                query.distinct(true);
                return spec.toPredicate(root, query, cb);
            }, pageable);

            Page<HomeNewsResponseDto> dtoPage =
                    newsPage.map(this::mapToHomeDto);

            return buildPageResponse(
                    dtoPage,
                    dtoPage.isEmpty()
                            ? "No news found"
                            : "News fetched successfully"
            );
        } catch (Exception e) {
            throw new SearchOperationException(
                    "Error occurred while filtering news. Please try again."
            );
        }

    }

    // 🔥 Generic Page Builder
    private <T> PageResponse<T> buildPageResponse(Page<T> page, String message) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .message(message)
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
                .finalVideoUrl(news.getFinalVideoUrl())
                .city(news.getCity())
                .createdAt(news.getCreatedAt())
                .build();
    }

    // =========================
    // 🔹 STORY MAPPING
    // =========================
    private StoryNewsResponseDto mapToStoryDto(News news) {
        return StoryNewsResponseDto.builder()
                .id(news.getId())
                .title(news.getTitle())
                .shortDescription(news.getShortDescription())
                .fullContext(news.getFullContext())
                .category(news.getCategory())
                .city(news.getCity())
                .state(news.getState())
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

    // =========================
    // 🔹 DIGITAL MAPPING
    // =========================
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
                .city(news.getCity())
                .state(news.getState())
                .mediaUrls(mediaUrls)
                .audioUrl(audioUrl)
                .createdAt(news.getCreatedAt())
                .build();
    }
}
