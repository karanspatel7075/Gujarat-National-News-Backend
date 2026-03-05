package com.gnn.newsnetwork.GnnNewsNetworkApplication.service;

import com.gnn.newsnetwork.GnnNewsNetworkApplication.dto.DigitalNewsRequestDto;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.entity.Media;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.entity.News;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.entity.Users;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.enums.MediaType;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.enums.NewsStatus;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.enums.TypeOfNews;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.exception.BadRequestException;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.exception.FileUploadException;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.repository.MediaRepository;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class DigitalNewsService {

    private final NewsRepository newsRepository;
    private final MediaRepository mediaRepository;
    private final VideoProcessingService videoProcessingService;


    @CacheEvict(value = "filteredNews", allEntries = true)
    public News createDigitalNews(DigitalNewsRequestDto dto, Users editor) {

        log.info("Creating digital news | editorId={} | title={}", editor.getId(), dto.getTitle());

        if(dto.getTitle() == null || dto.getTitle().isBlank()) {
            throw new BadRequestException("Title is required");
        }

        System.out.println(dto.getState() + " & " + dto.getCity());


        // 1️⃣ Save News (NO fullContext for digital)
        News savedNews = newsRepository.save(
                News.builder()
                        .title(dto.getTitle())
                        .shortDescription(dto.getShortDescription())
                        .category(dto.getCategory())
                        .typeOfNews(TypeOfNews.DIGITAL)
                        .status(NewsStatus.DRAFT)
                        .city(dto.getCity())
                        .state(dto.getState())
                        .anchorName(dto.getAnchorName())
                        .publishedAt(LocalDateTime.now())
                        .createdBy(editor)
                        .build()
        );



        log.info("Digital news saved | newsId={}", savedNews.getId());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
        System.out.println("Published time : " + savedNews.getPublishedAt().format(formatter));

        System.out.println("Created by editor : " + savedNews.getCreatedBy().getUsername());

        // 2️⃣ Upload images / videos
        uploadMediaFiles(dto.getMediaFiles(), savedNews);

        // 3️⃣ Upload audio (optional)
        uploadAudio(dto.getAudioFile(), savedNews);

        return savedNews;
    }

    private void uploadMediaFiles(MultipartFile[] mediaFiles, News news) {
        if(mediaFiles == null || mediaFiles.length == 0) {
            return;
        }

        String uploadDir = System.getProperty("user.dir") + "/uploads/digital-news";

        File dir = new File(uploadDir); // Assigned the folder path
        if(!dir.exists()) { // If not exist create new one
            dir.mkdirs();
        }

        for (MultipartFile file : mediaFiles) {
            if (file == null || file.isEmpty()) continue;

            String contentType = file.getContentType();
            if (contentType == null || !(contentType.startsWith("image/") || contentType.equals("video/mp4"))) {
                throw new BadRequestException("Only images or MP4 videos are allowed");
            }

            try {
                String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename(); // Create new filename for image
                file.transferTo(new File(dir, filename));   // Store in local

                mediaRepository.save(
                        Media.builder()
                                .news(news)
                                .mediaType(detectMediaType(filename))
                                .mediaUrl("/uploads/digital-news/" + filename)
                                .build()
                ); // Save the path in DataBase

                if (detectMediaType(filename) == MediaType.VIDEO) {

                    news.setVideoAbsolutePath(new File(dir, filename).getAbsolutePath());
                    news.setProcessingStatus("PENDING");
                    newsRepository.save(news);

                    videoProcessingService.processVideo(
                            news.getId(),
                            news.getShortDescription(),   // or title
                            "gu-IN"
                    );
                }


            } catch (IOException e) {
                throw new FileUploadException("Failed to upload media file");
            }
        }
    }

    private void uploadAudio(MultipartFile audioFile, News news) {

        if (audioFile == null || audioFile.isEmpty()) return;

        String contentType = audioFile.getContentType();
        if (contentType == null || !contentType.startsWith("audio/")) {
            throw new BadRequestException("Only audio files are allowed");
        }

        try {
            String uploadDir = System.getProperty("user.dir") + "/uploads/digital-news/audio";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String filename = System.currentTimeMillis() + "_" + audioFile.getOriginalFilename();
            audioFile.transferTo(new File(dir, filename));

            mediaRepository.save(
                    Media.builder()
                            .news(news)
                            .mediaType(MediaType.AUDIO)
                            .mediaUrl("/uploads/digital-news/audio/" + filename)
                            .build()
            );

        } catch (IOException e) {
            throw new FileUploadException("Failed to upload audio file");
        }
    }

    private MediaType detectMediaType(String filename) {
        return filename.endsWith(".mp4") ? MediaType.VIDEO : MediaType.IMAGE;
    }
}
