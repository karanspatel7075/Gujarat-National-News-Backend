package com.gnn.newsnetwork.GnnNewsNetworkApplication.service;


import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TranslationService {

    // How can I use Google Gemini to translate the language (17th Feb)
    // Never translate on every request.
    //→ Translate once.
    //→ 1. Store in DB.c
    //  2. Use async processing for heavy content.
    //  3. Cache translated content (Redis).
    //  4. Handle API failure fallback.

    private final RestTemplate restTemplate = new RestTemplate();
    private final String apiUrl = "http://localhost:8000/translate"; // FastAPI endpoint

    // Simple in-memory cache to avoid repeated translations
    private final Map<String, String> translationCache = new ConcurrentHashMap<>();

    /**
     * Translate text from sourceLang to targetLang using FastAPI
     */
    public String translateText(String text, String sourceLang, String targetLang) {
        if (text == null || text.isBlank()) return "";

        sourceLang = sourceLang.trim().toLowerCase();
        targetLang = targetLang.trim().toLowerCase();

        // Only allow Gujarati -> Hindi/English
        if (!sourceLang.equals("gu") || !(targetLang.equals("hi") || targetLang.equals("en"))) {
            throw new IllegalArgumentException("Only Gujarati to Hindi or English translation is supported");
        }

        String cacheKey = sourceLang + "->" + targetLang + ":" + text;
        if (translationCache.containsKey(cacheKey)) {
            return translationCache.get(cacheKey);
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> body = new HashMap<>();
            body.put("text", text);
            body.put("source_lang", sourceLang);
            body.put("target_lang", targetLang);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Object translatedText = response.getBody().get("translated_text");
                if (translatedText != null) {
                    String result = translatedText.toString();
                    translationCache.put(cacheKey, result);
                    return result;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return text; // fallback if translation fails
    }

    /**
     * Translate all relevant fields of a News object
     */
    public Map<String, Map<String, String>> translateNewsFields(Map<String, String> fields) {
        Map<String, Map<String, String>> translations = new HashMap<>();
        String sourceLang = "gu";

        // Hindi translations
        Map<String, String> hiMap = new HashMap<>();
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            hiMap.put(entry.getKey(), translateText(entry.getValue(), sourceLang, "hi"));
        }
        translations.put("hi", hiMap);

        // English translations
        Map<String, String> enMap = new HashMap<>();
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            enMap.put(entry.getKey(), translateText(entry.getValue(), sourceLang, "en"));
        }
        translations.put("en", enMap);

        return translations;
    }
}
