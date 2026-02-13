package com.gnn.newsnetwork.GnnNewsNetworkApplication.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class StoryNewsRequestDto {

    private String title;
    private String shortDescription;
    private String fullContext;
    private String category;

    private String state;
    private String city;

    private MultipartFile[] mediaFiles; // images or videos
}