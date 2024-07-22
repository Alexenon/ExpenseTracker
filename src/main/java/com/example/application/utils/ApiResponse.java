package com.example.application.utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponse {
    @JsonProperty("Response")
    private String response;

    @JsonProperty("Message")
    private String message;

    @JsonProperty("Data")
    private Map<String, Coin> data;

    // Getters and Setters
}
