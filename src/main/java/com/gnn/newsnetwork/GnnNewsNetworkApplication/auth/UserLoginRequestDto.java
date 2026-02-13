package com.gnn.newsnetwork.GnnNewsNetworkApplication.auth;

import lombok.Data;

@Data
public class UserLoginRequestDto {
    private String username;
    private String password;
}
