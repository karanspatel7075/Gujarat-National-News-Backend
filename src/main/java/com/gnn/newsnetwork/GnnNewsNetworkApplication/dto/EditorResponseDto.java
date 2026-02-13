package com.gnn.newsnetwork.GnnNewsNetworkApplication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditorResponseDto {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private boolean active;
}
