package com.gnn.newsnetwork.GnnNewsNetworkApplication.exception;

//Bad request (validation / missing fields)
public class BadRequestException extends RuntimeException{
    public BadRequestException(String message) {
        super(message);
    }
}
