package com.gnn.newsnetwork.GnnNewsNetworkApplication.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.repository.MediaStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService implements MediaStorageService {

    private final Cloudinary cloudinary;

    public String uploadFile(MultipartFile file) throws IOException {

        // 🔹 Add validation HERE
        if (file.getSize() > 50 * 1024 * 1024) {
            throw new RuntimeException("File too large");
        }

        Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "resource_type", "auto"
                )
        );

        return uploadResult.get("secure_url").toString();
    }

    @Override
    public String uploadImage(MultipartFile file) {
        return upload(file, "image");
    }

    @Override
    public String uploadVideo(MultipartFile file) {
        return upload(file, "video");
    }

    @Override
    public String uploadAudio(MultipartFile file) {
        return upload(file, "video"); // audio uses same resource type
    }

    private String upload(MultipartFile file, String type) {

        try {

            String folder;

            if (type.equals("image")) {
                folder = "gnn-news/images";
            } else if (type.equals("video")) {
                folder = "gnn-news/videos";
            } else {
                folder = "gnn-news/audio";
            }

            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "resource_type", "auto",
                            "folder", folder
                    )
            );

            return uploadResult.get("secure_url").toString();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Cloudinary upload failed: " + e.getMessage());
        }
    }
}
