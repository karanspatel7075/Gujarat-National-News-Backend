package com.gnn.newsnetwork.GnnNewsNetworkApplication.dto;

import com.gnn.newsnetwork.GnnNewsNetworkApplication.enums.TypeOfNews;
import lombok.Data;

@Data
public class NewsFilterRequestDto {
    private String state;
    private String city;
    private String category;
    private TypeOfNews typeOfNews;
    private String keyword;

}
