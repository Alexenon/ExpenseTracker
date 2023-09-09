package com.example.application.data.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public abstract class ApiResponse<T> {

    private final T data;
    private final LocalDateTime dateTime;
    protected int statusCode;

    public ApiResponse(T data) {
        this.data = data;
        this.dateTime = LocalDateTime.now();
    }

}


