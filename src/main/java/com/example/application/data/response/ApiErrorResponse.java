package com.example.application.data.response;

import org.springframework.http.HttpStatus;

public class ApiErrorResponse extends ApiResponse<String> {

    private final String title;

    public ApiErrorResponse(String title) {
        super(null);
        this.title = title;
        statusCode = 400;
    }

    public ApiErrorResponse(String title, HttpStatus httpStatus) {
        super(null);
        this.title = title;
        this.statusCode = httpStatus.value();
    }

    public String getTitle() {
        return title;
    }

}
