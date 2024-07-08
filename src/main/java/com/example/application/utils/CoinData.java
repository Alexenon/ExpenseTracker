package com.example.application.utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigInteger;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CoinData {
    @JsonProperty("Id")
    private String id;

    @JsonProperty("Url")
    private String url;

    @JsonProperty("ImageUrl")
    private String imageUrl;

    @JsonProperty("ContentCreatedOn")
    private long contentCreatedOn;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Symbol")
    private String symbol;

    @JsonProperty("CoinName")
    private String coinName;

    @JsonProperty("FullName")
    private String fullName;

    @JsonProperty("Description")
    private String description;

    @JsonProperty("AssetTokenStatus")
    private String assetTokenStatus;

    @JsonProperty("Algorithm")
    private String algorithm;

    @JsonProperty("ProofType")
    private String proofType;

    @JsonProperty("SortOrder")
    private String sortOrder;

    @JsonProperty("Sponsored")
    private boolean sponsored;

    @JsonProperty("Taxonomy")
    private Taxonomy taxonomy;

    @JsonProperty("Rating")
    private Rating rating;

    @JsonProperty("IsTrading")
    private boolean isTrading;

    @JsonProperty("TotalCoinsMined")
    private BigInteger totalCoinsMined;

    @JsonProperty("CirculatingSupply")
    private long circulatingSupply;

    @JsonProperty("BlockNumber")
    private long blockNumber;

    @JsonProperty("NetHashesPerSecond")
    private double netHashesPerSecond;

    @JsonProperty("BlockReward")
    private double blockReward;

    @JsonProperty("BlockTime")
    private long blockTime;

    @JsonProperty("AssetLaunchDate")
    private String assetLaunchDate;

    @JsonProperty("AssetWhitepaperUrl")
    private String assetWhitepaperUrl;

    @JsonProperty("AssetWebsiteUrl")
    private String assetWebsiteUrl;

    @JsonProperty("MaxSupply")
    private double maxSupply;

    @JsonProperty("MktCapPenalty")
    private int mktCapPenalty;

    @JsonProperty("IsUsedInDefi")
    private int isUsedInDefi;

    @JsonProperty("IsUsedInNft")
    private int isUsedInNft;

    @JsonProperty("PlatformType")
    private String platformType;

    @JsonProperty("DecimalPoints")
    private int decimalPoints;

    @JsonProperty("AlgorithmType")
    private String algorithmType;

    @JsonProperty("Difficulty")
    private double difficulty;

    // Getters and Setters

    @Override
    public String toString() {
        return "CoinMetaData{" +
               "id='" + id + '\'' +
               ", url='" + url + '\'' +
               ", imageUrl='" + imageUrl + '\'' +
               ", contentCreatedOn=" + contentCreatedOn +
               ", name='" + name + '\'' +
               ", symbol='" + symbol + '\'' +
               ", coinName='" + coinName + '\'' +
               ", fullName='" + fullName + '\'' +
               ", description='" + description + '\'' +
               ", assetTokenStatus='" + assetTokenStatus + '\'' +
               ", algorithm='" + algorithm + '\'' +
               ", proofType='" + proofType + '\'' +
               ", sortOrder='" + sortOrder + '\'' +
               ", sponsored=" + sponsored +
               ", taxonomy=" + taxonomy +
               ", rating=" + rating +
               ", isTrading=" + isTrading +
               ", totalCoinsMined=" + totalCoinsMined +
               ", circulatingSupply=" + circulatingSupply +
               ", blockNumber=" + blockNumber +
               ", netHashesPerSecond=" + netHashesPerSecond +
               ", blockReward=" + blockReward +
               ", blockTime=" + blockTime +
               ", assetLaunchDate='" + assetLaunchDate + '\'' +
               ", assetWhitepaperUrl='" + assetWhitepaperUrl + '\'' +
               ", assetWebsiteUrl='" + assetWebsiteUrl + '\'' +
               ", maxSupply=" + maxSupply +
               ", mktCapPenalty=" + mktCapPenalty +
               ", isUsedInDefi=" + isUsedInDefi +
               ", isUsedInNft=" + isUsedInNft +
               ", platformType='" + platformType + '\'' +
               ", decimalPoints=" + decimalPoints +
               ", algorithmType='" + algorithmType + '\'' +
               ", difficulty=" + difficulty +
               '}';
    }

    // Inner classes for Taxonomy and Rating
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Taxonomy {
        @JsonProperty("Access")
        private String access;

        @JsonProperty("FCA")
        private String fca;

        @JsonProperty("FINMA")
        private String finma;

        @JsonProperty("Industry")
        private String industry;

        @JsonProperty("CollateralizedAsset")
        private String collateralizedAsset;

        @JsonProperty("CollateralizedAssetType")
        private String collateralizedAssetType;

        @JsonProperty("CollateralType")
        private String collateralType;

        @JsonProperty("CollateralInfo")
        private String collateralInfo;

        // Getters and Setters
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Rating {
        @JsonProperty("Weiss")
        private WeissRating weiss;

        // Getters and Setters

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class WeissRating {
            @JsonProperty("Rating")
            private String rating;

            @JsonProperty("TechnologyAdoptionRating")
            private String technologyAdoptionRating;

            @JsonProperty("MarketPerformanceRating")
            private String marketPerformanceRating;

            // Getters and Setters
        }
    }
}