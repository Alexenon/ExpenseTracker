package com.example.application.data.response;

import org.springframework.http.HttpStatus;

public class ApiSuccessResponse<T> extends ApiResponse<T> {

    public ApiSuccessResponse(T data, HttpStatus httpStatus) {
        super(data);
        this.statusCode = httpStatus.value();
    }

    public ApiSuccessResponse(T data) {
        super(data);
        statusCode = 200;
    }

}
