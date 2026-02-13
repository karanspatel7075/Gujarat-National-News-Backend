package com.gnn.newsnetwork.GnnNewsNetworkApplication.exception;

//File upload failure
public class FileUploadException extends RuntimeException{
    public FileUploadException(String message) {
        super(message);
    }
}
