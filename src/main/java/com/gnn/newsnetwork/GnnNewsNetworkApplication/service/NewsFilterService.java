package com.gnn.newsnetwork.GnnNewsNetworkApplication.service;

import com.gnn.newsnetwork.GnnNewsNetworkApplication.common.NewsSpecification;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.dto.DigitalNewsResponseDto;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.dto.NewsFilterRequestDto;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.dto.PageResponse;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.dto.StoryNewsResponseDto;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.entity.Media;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.entity.News;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.enums.MediaType;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.enums.TypeOfNews;
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

    @Cacheable(value = "filteredNews", key = "T(java.util.Objects).toString(#dto.state,'') + '-' + " + "T(java.util.Objects).toString(#dto.city,'') + '-' + " + "T(java.util.Objects).toString(#dto.category,'') + '-' + " + "#dto.typeOfNews + '-' + #page + '-' + #size")
    public PageResponse<?> filterNews(NewsFilterRequestDto dto, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        // This is dynamic query building.
        Specification<News> spec = Specification
                .where(NewsSpecification.isApproved())
                .and(NewsSpecification.hasState(dto.getState()))
                .and(NewsSpecification.hasCity(dto.getCity()))
                .and(NewsSpecification.hasCategory(dto.getCategory()))
                .and(NewsSpecification.hasType(dto.getTypeOfNews()));

        Page<News> newsPage = newsRepository.findAll((root, query, cb) -> {
            root.fetch("mediaList", JoinType.LEFT);
            query.distinct(true);
            return spec.toPredicate(root, query, cb);}, pageable);

        if (dto.getTypeOfNews() == TypeOfNews.DIGITAL) {

            Page<DigitalNewsResponseDto> dtoPage =
                    newsPage.map(this::mapToDigitalDto);

            return buildPageResponse(
                    dtoPage,
                    dtoPage.isEmpty()
                            ? "No digital news found"
                            : "Digital news fetched successfully"
            );

        } else {

            Page<StoryNewsResponseDto> dtoPage =
                    newsPage.map(this::mapToStoryDto);

            return buildPageResponse(
                    dtoPage,
                    dtoPage.isEmpty()
                            ? "No story news found"
                            : "Story news fetched successfully"
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
