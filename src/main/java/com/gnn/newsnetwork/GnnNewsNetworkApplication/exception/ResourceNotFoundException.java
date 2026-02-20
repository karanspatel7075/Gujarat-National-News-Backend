package com.gnn.newsnetwork.GnnNewsNetworkApplication.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}