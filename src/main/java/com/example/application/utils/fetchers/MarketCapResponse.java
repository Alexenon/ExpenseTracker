package com.example.application.utils.fetchers;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Data
@ToString
public class MarketCapResponse {
    private Map<String, CryptocurrencyData> data;
    private Status status;

    @Data
    static class CryptocurrencyData {
        private int id;
        private String name;
        private String symbol;
        private String slug;
        private String description;
        private String dateAdded;
        private String dateLaunched;
        private String logo;
        private Map<String, List<String>> urls;
        private List<String> tags;
        private String category;

        @JsonProperty("date_added")
        public String getDateAdded() {
            return dateAdded;
        }

        @JsonProperty("date_launched")
        public String getDateLaunched() {
            return dateLaunched;
        }
    }

    @Data
    static class Status {
        private String timestamp;
        private int errorCode;
        private String errorMessage;
        private int elapsed;
        private int creditCount;
    }
}
