package com.gnn.newsnetwork.GnnNewsNetworkApplication.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class DigitalNewsRequestDto {

    private String title;
    private String shortDescription;
    private String anchorName;
    private String category;
    private String state;
    private String city;


    private MultipartFile[] mediaFiles; // images or videos
    private MultipartFile audioFile;    // anchor voice / narration
}
