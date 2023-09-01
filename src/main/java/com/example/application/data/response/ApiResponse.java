package com.example.application.data.response;

import lombok.Data;

import java.time.LocalDateTime;

/*
* TODO: Implement ApiSuccessResponse and ApiErrorResponse
*  - Implement usage of this class
*  - Remove everywhere entity as an API request
*  - Add status code
* */

@Data
public class ApiResponse<T> {

    private final String message;
    private final T data;
    private final LocalDateTime dateTime;

    public ApiResponse(String message, T data) {
        this.message = message;
        this.data = data;
        this.dateTime = LocalDateTime.now();
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(message, data);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(null, data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(message, null);
    }

}
