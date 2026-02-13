package com.gnn.newsnetwork.GnnNewsNetworkApplication.service;

import com.gnn.newsnetwork.GnnNewsNetworkApplication.dto.DigitalNewsResponseDto;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.dto.EditorResponseDto;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.dto.PageResponse;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.dto.StoryNewsResponseDto;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.entity.Media;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.entity.News;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.entity.Users;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.enums.MediaType;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.enums.NewsStatus;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.enums.ROLE;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.enums.TypeOfNews;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.repository.NewsRepository;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final NewsRepository newsRepository;

    //  Approve Editor
    public void approveEditors(Long editorId) {
        Users editor = userRepository.findById(editorId).orElseThrow(() -> new RuntimeException("Editor not found"));

        if(editor.getRole() != ROLE.EDITOR) {
            throw new RuntimeException("User is not a editor");
        }

        editor.setActive(true);
        userRepository.save(editor);
    }

    // Reject Editor
    public void rejectEditor(Long editorId) {
        Users editor = userRepository.findById(editorId)
                .orElseThrow(() -> new RuntimeException("Editor not found"));

        if (editor.getRole() != ROLE.EDITOR) {
            throw new RuntimeException("User is not an editor");
        }

        editor.setActive(false);
        userRepository.save(editor);
    }

    public List<EditorResponseDto> getAllEditors() {
        return userRepository.findByRole(ROLE.EDITOR)
                .stream()
                .map(user -> EditorResponseDto.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .phone(user.getPhone())
                        .active(user.isActive())
                        .build()
                )
                .toList();
    }

    // Approved Editors
    public List<EditorResponseDto> getApprovedEditorsDto() {
        return userRepository.findByRoleAndActive(ROLE.EDITOR, true)
                .stream()
                .map(this::mapToEditorDto)
                .toList();
    }

    // Rejected / Pending Editors in DTO form
    public List<EditorResponseDto> getRejectedEditorsDto() {
        return userRepository.findByRoleAndActive(ROLE.EDITOR, false)
                .stream()
                .map(this::mapToEditorDto)
                .toList();
    }

    // Mapping method
    private EditorResponseDto mapToEditorDto(Users user) {
        return EditorResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .active(user.isActive())
                .build();
    }

    // Paginated Story News + Meaningful response for the frontend
    @Transactional(readOnly = true)
    public PageResponse<StoryNewsResponseDto> getPendingStoryNewsDto(Pageable pageable) {
        Page<StoryNewsResponseDto> page =
                newsRepository.findByTypeOfNewsAndStatusOrderByCreatedAtDesc(
                        TypeOfNews.STORY,
                        NewsStatus.DRAFT,
                        pageable
                ).map(this::mapToStoryDto);

        String message = page.isEmpty()
                ? "No pending story news available"
                : "Pending story news fetched successfully";

        return PageResponse.<StoryNewsResponseDto>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .message(message)
                .build();
    }

    // Paginated Digital News
    @Transactional(readOnly = true)
    public Page<DigitalNewsResponseDto> getPendingDigitalNewsDto(Pageable pageable) {
        return newsRepository.findByTypeOfNewsAndStatusOrderByCreatedAtDesc(TypeOfNews.DIGITAL, NewsStatus.DRAFT, pageable)
                .map(this::mapToDigitalDto);
    }

    // Map News -> DTO for admin
    private StoryNewsResponseDto mapToStoryDto(News news) {
        return StoryNewsResponseDto.builder()
                .id(news.getId())
                .title(news.getTitle())
                .shortDescription(news.getShortDescription())
                .fullContext(news.getFullContext())
                .category(news.getCategory())
                .status(news.getStatus())
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
                .publishedAt(news.getPublishedAt()) // add here
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
                .status(news.getStatus())
                .category(news.getCategory())
                .state(news.getState())
                .city(news.getCity())
                .mediaUrls(mediaUrls)
                .audioUrl(audioUrl)
                .createdAt(news.getCreatedAt())
                .publishedAt(news.getPublishedAt()) // add here
                .build();
    }

    // Approve News
    public void approveNews(Long newsId, Users admin) {
        News news = newsRepository.findById(newsId).orElseThrow(() -> new RuntimeException("News not found"));

        news.setStatus(NewsStatus.PUBLISHED);
        news.setApprovedBy(admin);
        news.setPublishedAt(LocalDateTime.now());

        newsRepository.save(news);
    }

    public Page<StoryNewsResponseDto> getApprovedStoryNewsDto(Pageable pageable) {
        return newsRepository
                .findByTypeOfNewsAndStatusOrderByCreatedAtDesc(
                        TypeOfNews.STORY,
                        NewsStatus.PUBLISHED,
                        pageable
                )
                .map(this::mapToStoryDto);
    }

    public void rejectNews(Long newsId, Users admin) {
        News news = newsRepository.findById(newsId).orElseThrow(() -> new RuntimeException("News not found"));

        news.setStatus(NewsStatus.REJECTED);
        news.setApprovedBy(admin);
        news.setPublishedAt(LocalDateTime.now());
        newsRepository.delete(news);
    }
}
