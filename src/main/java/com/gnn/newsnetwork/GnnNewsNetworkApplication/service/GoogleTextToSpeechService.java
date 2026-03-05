package com.gnn.newsnetwork.GnnNewsNetworkApplication.service;

import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;

@Slf4j
@Service
public class GoogleTextToSpeechService {


//    public String generateAudio(Long newsId, String text, String languageCode) {
//
//        try (TextToSpeechClient ttsClient = TextToSpeechClient.create()) {
//
//            String projectDir = System.getProperty("user.dir");
//            String audioDir = projectDir + "/uploads/audio";
//            File dir = new File(audioDir);
//            if (!dir.exists()) dir.mkdirs();
//
//            String outputPath = audioDir + "/news_" + newsId + ".mp3";
//
//            // 1️⃣ Set text
//            SynthesisInput input = SynthesisInput.newBuilder()
//                    .setText(text)
//                    .build();
//
//            // 2️⃣ Set voice
//            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
//                    .setLanguageCode(languageCode) // e.g. "gu-IN", "en-US"
//                    .setSsmlGender(SsmlVoiceGender.NEUTRAL)
//                    .build();
//
//            // 3️⃣ Set audio config
//            AudioConfig audioConfig = AudioConfig.newBuilder()
//                    .setAudioEncoding(AudioEncoding.MP3)
//                    .build();
//
//            // 4️⃣ Perform TTS request
//            SynthesizeSpeechResponse response =
//                    ttsClient.synthesizeSpeech(input, voice, audioConfig);
//
//            ByteString audioContents = response.getAudioContent();
//
//            // 5️⃣ Save MP3 file
//            try (FileOutputStream out = new FileOutputStream(outputPath)) {
//                out.write(audioContents.toByteArray());
//            }
//
//            log.info("Google TTS audio generated for newsId={}", newsId);
//
//            return "/uploads/audio/news_" + newsId + ".mp3";
//
//        } catch (Exception e) {
//            log.error("Google TTS failed", e);
//            return null;
//        }
//    }
}