package com.gnn.newsnetwork.GnnNewsNetworkApplication.service;

import com.gnn.newsnetwork.GnnNewsNetworkApplication.entity.News;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoProcessingService {

    private final NewsRepository newsRepository;
    private final GoogleTextToSpeechService googleTextToSpeechService;
    private final TextToSpeechService2 textToSpeechService2;

    @Async
    public void processVideo(Long newsId, String text, String languageCode) {

        try {
            News news = newsRepository.findById(newsId)
                    .orElseThrow(() -> new RuntimeException("News not found"));

            newsRepository.save(news);

            // 1️⃣ Generate audio using Python gTTS
            String relativeAudioUrl = textToSpeechService2.generateAudio(newsId, text, languageCode);
//            String relativeAudioUrl = googleTextToSpeechService.generateAudio(newsId, text, languageCode);

            if (relativeAudioUrl == null) {
                throw new RuntimeException("Audio generation failed");
            }

            String projectDir = System.getProperty("user.dir");
            String audioAbsolutePath = projectDir + relativeAudioUrl;

            // 2️⃣ Prepare output folder
            String outputDir = projectDir + "/uploads/final-video";
            Files.createDirectories(Path.of(outputDir));

            String outputFileName = UUID.randomUUID() + ".mp4";
            String outputPath = outputDir + "/" + outputFileName;

            String ffmpegPath = "\"C:\\Users\\KARAN PATEL\\Desktop\\PavitraSoft\\ffmpeg-master-latest-win64-gpl-shared\\ffmpeg-master-latest-win64-gpl-shared\\bin\\ffmpeg.exe\"";

            ProcessBuilder builder = new ProcessBuilder(
                    ffmpegPath,
                    "-y",
                    "-i", news.getVideoAbsolutePath(),  // VIDEO FIRST
                    "-i", audioAbsolutePath,            // AUDIO SECOND
                    "-map", "0:v:0",
                    "-map", "1:a:0",
                    "-c:v", "copy",
                    "-c:a", "aac",
                    "-shortest",
                    outputPath
            );




            builder.redirectErrorStream(true);
            Process process = builder.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            String line;
            while ((line = reader.readLine()) != null) {
                log.info(line);
            }

            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new RuntimeException("FFmpeg failed with code: " + exitCode);
            }

            // 4️⃣ Save final video path
            news.setFinalVideoUrl("/uploads/final-video/" + outputFileName);
            news.setProcessingStatus("COMPLETED");
            newsRepository.save(news);

            log.info("Video merged successfully for newsId={}", newsId);

        } catch (Exception e) {

            log.error("Video processing failed", e);

            News news = newsRepository.findById(newsId).orElse(null);
            if (news != null) {
                news.setProcessingStatus("FAILED");
                newsRepository.save(news);
            }
        }
    }
}
