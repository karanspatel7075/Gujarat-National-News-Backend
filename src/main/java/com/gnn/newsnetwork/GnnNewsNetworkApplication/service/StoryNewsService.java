package com.gnn.newsnetwork.GnnNewsNetworkApplication.service;

import com.gnn.newsnetwork.GnnNewsNetworkApplication.dto.StoryNewsRequestDto;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.entity.Media;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.entity.News;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.entity.Users;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.enums.MediaType;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.enums.NewsStatus;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.enums.TypeOfNews;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.exception.BadRequestException;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.exception.FileUploadException;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.repository.MediaRepository;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.repository.MediaStorageService;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoryNewsService {

    private final NewsRepository newsRepository;
    private final MediaRepository mediaRepository;
    private final TranslationService translationService;
    private final VideoProcessingService videoProcessingService;

    private final TextToSpeechService2 textToSpeechService2;
    private final MediaStorageService mediaStorageService;

//    @CacheEvict(value = "filteredNews", allEntries = true)
    public News createStoryNews(StoryNewsRequestDto dto, Users editor) {

        log.info("====== STORY NEWS CREATION STARTED ======");
        log.info("Editor: {}", editor != null ? editor.getUsername() : "NULL");
        log.info("Title: {}", dto.getTitle());
        log.info("Category: {}", dto.getCategory());
        log.info("City: {}", dto.getCity());
        log.info("State: {}", dto.getState());

        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            log.error("Title validation failed");
            throw new BadRequestException("Title is required");
        }

        if (dto.getFullContext() == null || dto.getFullContext().isBlank()) {
            log.error("Full context validation failed");
            throw new BadRequestException("Full context is required");
        }

        log.info("Saving news in database...");

        News savedNews = newsRepository.save(
                News.builder()
                        .title(dto.getTitle())
                        .shortDescription(dto.getShortDescription())
                        .fullContext(dto.getFullContext())
                        .category(dto.getCategory())
                        .typeOfNews(TypeOfNews.STORY)
                        .status(NewsStatus.DRAFT)
                        .createdBy(editor)
                        .city(dto.getCity())
                        .state(dto.getState())
                        .publishedAt(LocalDateTime.now())
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        log.info("News saved successfully | NewsID={}", savedNews.getId());

        MultipartFile[] mediaFiles = dto.getMediaFiles();

        if (mediaFiles == null || mediaFiles.length == 0) {
            log.warn("No media files uploaded for NewsID={}", savedNews.getId());
            return savedNews;
        }

        log.info("Total files received: {}", mediaFiles.length);

        for (MultipartFile file : mediaFiles) {

            if (file == null || file.isEmpty()) {
                log.warn("Skipping empty file");
                continue;
            }

            try {

                String originalName = file.getOriginalFilename();
                String contentType = file.getContentType();
                long size = file.getSize();

                log.info("Processing file -> Name: {}, Type: {}, Size: {} bytes",
                        originalName, contentType, size);

                if (contentType == null) {
                    log.error("File content type is NULL for file {}", originalName);
                    throw new BadRequestException("File content type not detected");
                }

                String mediaUrl;

                // IMAGE UPLOAD
                if (contentType.startsWith("image/")) {

                    log.info("Uploading image to Cloudinary...");

                    mediaUrl = mediaStorageService.uploadImage(file);

                    log.info("Image uploaded successfully -> URL: {}", mediaUrl);

                    mediaRepository.save(
                            Media.builder()
                                    .news(savedNews)
                                    .mediaType(MediaType.IMAGE)
                                    .mediaUrl(mediaUrl)
                                    .build()
                    );

                    log.info("Image saved in database for NewsID={}", savedNews.getId());

                }

                // VIDEO UPLOAD
                else if ("video/mp4".equals(contentType)) {

                    log.info("Uploading video to Cloudinary...");

                    mediaUrl = mediaStorageService.uploadVideo(file);

                    log.info("Video uploaded successfully -> URL: {}", mediaUrl);

                    mediaRepository.save(
                            Media.builder()
                                    .news(savedNews)
                                    .mediaType(MediaType.VIDEO)
                                    .mediaUrl(mediaUrl)
                                    .build()
                    );

                    log.info("Video saved in database for NewsID={}", savedNews.getId());

                }

                else {

                    log.error("Unsupported file type detected: {}", contentType);
                    throw new BadRequestException("Only images or MP4 videos allowed");

                }

            } catch (Exception e) {

                log.error("File upload failed: {}", e.getMessage());
                log.error("Stack trace:", e);

                throw new RuntimeException("Media upload failed");

            }

        }

        log.info("====== STORY NEWS CREATION COMPLETED ======");
        return savedNews;
    }

//    @CacheEvict(value = "filteredNews", allEntries = true)
//    public News createStoryNews(StoryNewsRequestDto dto, Users editor) {
//
//        log.info("Creating story news | editorId={} | title={}", editor.getId(), dto.getTitle());
//
//
//        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
//            throw new BadRequestException("Title is required");
//        }
//
//        if (dto.getFullContext() == null || dto.getFullContext().isBlank()) {
//            throw new BadRequestException("Full context is required");
//        }
//
//        System.out.println(dto.getState() + " & " + dto.getState());
//
//
//        // 1️⃣ Save News
//        News savedNews = newsRepository.save(
//                News.builder()
//                        .title(dto.getTitle())
//                        .shortDescription(dto.getShortDescription())
//                        .fullContext(dto.getFullContext())
//                        .category(dto.getCategory())
//                        .typeOfNews(TypeOfNews.STORY)
//                        .status(NewsStatus.DRAFT)
//                        .createdBy(editor)
//                        .city(dto.getCity())
//                        .state(dto.getState())
//                        .publishedAt(LocalDateTime.now())
//                        .createdAt(LocalDateTime.now())
//                        .build()
//        );
//
//        newsRepository.save(savedNews);
//
//// Step 5: Save the translated news
//        newsRepository.save(savedNews);
//
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
//        System.out.println("Published time : " + savedNews.getPublishedAt().format(formatter));
//
//        System.out.println("Created by editor : " + savedNews.getCreatedBy().getUsername());
//        log.info("News saved | newsId={}", savedNews.getId());
//
//        MultipartFile[] mediaFiles = dto.getMediaFiles();
//
//        if (mediaFiles == null || mediaFiles.length == 0) {
//            log.info("No media uploaded for newsId={}", savedNews.getId());
//            return savedNews;
//        }
//
//        // Prepare Upload Directory
//        String uploadDir = System.getProperty("user.dir") + "/uploads/story-news";
//        File dir = new File(uploadDir);
//        if (!dir.exists()) dir.mkdirs();
//
//        for (MultipartFile file : mediaFiles) {
//            if (file == null || file.isEmpty()) continue;
//
//            String contentType = file.getContentType();
//
//            if (contentType == null ||
//                    !(contentType.startsWith("image/") || contentType.equals("video/mp4"))) {
//                throw new BadRequestException("Only images or MP4 videos are allowed");
//            }
//
////            Frontend → Backend → Save file in folder → Save file path in DB
//
//
//            try {
//                String originalName = file.getOriginalFilename();
//
//                // Determine extension from content type
//                String extension;
//
//                if (contentType.startsWith("image/")) {
//                    extension = "." + contentType.substring(contentType.lastIndexOf("/") + 1);
//                } else if (contentType.equals("video/mp4")) {
//                    extension = ".mp4";
//                } else {
//                    throw new BadRequestException("Only images or MP4 videos are allowed");
//                }
//
//// Generate unique filename
//                String fileName = UUID.randomUUID() + extension;
//
//                File destination = new File(dir, fileName);
//                file.transferTo(destination);
//
//                mediaRepository.save(
//                        Media.builder()
//                                .news(savedNews)
//                                .mediaType(detectMediaType(extension))
//                                .mediaUrl("/uploads/story-news/" + fileName)
//                                .build()
//                );
//
////                if (detectMediaType(extension) == MediaType.VIDEO) {
////                    savedNews.setVideoAbsolutePath(destination.getAbsolutePath());
////                    newsRepository.save(savedNews);
////
////                    videoProcessingService.processVideo(
////                            savedNews.getId(),
////                            savedNews.getFullContext(),
////                            "gu-IN"
////                    );
////                }
//
//
//                log.info("Media uploaded | newsId={} | file={}", savedNews.getId(), fileName);
//
//            } catch (IOException e) {
//                throw new FileUploadException("Failed to upload media file");
//            }
//
//        }
//
//        return savedNews;
//    }

    private MediaType detectMediaType(String filename) {
        return filename != null && filename.endsWith(".mp4")
                ? MediaType.VIDEO
                : MediaType.IMAGE;
    }
}

    /*
        "message": "Only images or MP4 videos are allowed",
        "status": 400,
        "timestamp": "2026-02-09T12:45:11"
    */
