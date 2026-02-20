package com.gnn.newsnetwork.GnnNewsNetworkApplication.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;

@Slf4j
@Service
public class TextToSpeechService2 {

    public String generateAudio(Long newsId, String text, String languageCode) {

        try {
            String projectDir = System.getProperty("user.dir");

            // Create audio folder
            String audioDir = projectDir + "/uploads/audio";
            File dir = new File(audioDir);
            if (!dir.exists()) dir.mkdirs();

            // Create temp text file
            String textFilePath = projectDir + "/temp_" + newsId + ".txt";
            File textFile = new File(textFilePath);
            java.nio.file.Files.writeString(textFile.toPath(), text);

            String outputPath = audioDir + "/news_" + newsId + ".mp3";
//            String scriptPath = projectDir + "/tts.py";

            ClassPathResource resource = new ClassPathResource("tts.py");
            File tempScript = File.createTempFile("tts_", ".py");

// Copy resource content to temp file
            try (java.io.InputStream in = resource.getInputStream()) {
                java.nio.file.Files.copy(in, tempScript.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }

            String scriptPath = tempScript.getAbsolutePath();
            log.info("TTS script path: {}", scriptPath);


            ProcessBuilder pb = new ProcessBuilder(
                    "C:\\Users\\KARAN PATEL\\AppData\\Local\\Python\\pythoncore-3.14-64\\python.exe",
                    scriptPath,
                    textFilePath,
                    languageCode,
                    outputPath
            );

            pb.redirectErrorStream(true);

            Process process = pb.start();

            // 🔥 Read python logs
            java.io.BufferedReader reader =
                    new java.io.BufferedReader(
                            new java.io.InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                log.info("PYTHON: {}", line);
            }

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                log.info("Audio generated successfully for newsId={}", newsId);
                return "/uploads/audio/news_" + newsId + ".mp3";
            } else {
                log.error("Python exited with code {}", exitCode);
                return null;
            }

        } catch (Exception e) {
            log.error("TTS failed for newsId={}", newsId, e);
            return null;
        }
    }
}