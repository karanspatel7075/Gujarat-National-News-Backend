package com.gnn.newsnetwork.GnnNewsNetworkApplication.repository;

import org.springframework.web.multipart.MultipartFile;

public interface MediaStorageService {
    String uploadImage(MultipartFile file);

    String uploadVideo(MultipartFile file);

    String uploadAudio(MultipartFile file);
}
