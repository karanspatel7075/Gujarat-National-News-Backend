package com.gnn.newsnetwork.GnnNewsNetworkApplication.controller;

import com.gnn.newsnetwork.GnnNewsNetworkApplication.dto.NewsFilterRequestDto;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.dto.PageResponse;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.enums.TypeOfNews;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.service.NewsFilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/searchEngine")
@RequiredArgsConstructor
public class NewsFilterController {

    private final NewsFilterService newsFilterService;

    @GetMapping("/filter")
    public PageResponse<?> filterNews(@RequestParam(required = false) String state, @RequestParam(required = false) String city, @RequestParam(required = false) String category, @RequestParam(required = false) String type, @RequestParam(required = false) String keyword, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        NewsFilterRequestDto dto = new NewsFilterRequestDto();
        dto.setState(state);
        dto.setCity(city);
        dto.setKeyword(keyword);
        dto.setCategory(category);

        if (type != null) {
            dto.setTypeOfNews(
                    TypeOfNews.valueOf(type.toUpperCase())
            );
        }

            return newsFilterService.filterNews(dto, page, size);
    }

        // If parameter not present → Spring sets it as null.
}
