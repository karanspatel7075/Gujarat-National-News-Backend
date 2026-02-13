package com.gnn.newsnetwork.GnnNewsNetworkApplication.service;

import org.springframework.stereotype.Service;

@Service
public class TranslationService {

    public String translateEnglishToGujarati(String englishText) {

        // TEMP (for now)
        // Later connect Google AI / Translation API
        return callGoogleTranslateApi(englishText);
    }

    private String callGoogleTranslateApi(String text) {
        // PSEUDO-CODE (API integration later)
        // return translatedGujaratiText;

        return text; // placeholder
    }
}
