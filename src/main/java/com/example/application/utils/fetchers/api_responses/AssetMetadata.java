package com.example.application.utils.fetchers.api_responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AssetMetadata {
    @JsonProperty("ID")
    private int id;

    @JsonProperty("TYPE")
    private String type;

    @JsonProperty("ID_LEGACY")
    private int legacyId;

    @JsonProperty("ID_PARENT_ASSET")
    private Integer parentAssetId;

    @JsonProperty("ID_ASSET_ISSUER")
    private Integer assetIssuerId;

    @JsonProperty("SYMBOL")
    private String symbol;

    @JsonProperty("URI")
    private String uri;

    @JsonProperty("ASSET_TYPE")
    private String assetType;

    @JsonProperty("PARENT_ASSET_SYMBOL")
    private String parentAssetSymbol;

    @JsonProperty("ASSET_ISSUER_NAME")
    private String assetIssuerName;

    @JsonProperty("CREATED_ON")
    private int createdOn;

    @JsonProperty("UPDATED_ON")
    private int updatedOn;

    @JsonProperty("PUBLIC_NOTICE")
    private Object publicNotice;

    @JsonProperty("NAME")
    private String name;

    @JsonProperty("LOGO_URL")
    private String logoUrl;

    @JsonProperty("LAUNCH_DATE")
    private int launchDate;

    @JsonProperty("PREVIOUS_ASSET_SYMBOLS")
    private Object previousAssetSymbols;

    @JsonProperty("ASSET_ALTERNATIVE_IDS")
    private List<AssetAlternativeId> assetAlternativeIds;

    @JsonProperty("ASSET_DESCRIPTION_SNIPPET")
    private String assetDescriptionSnippet;

    @JsonProperty("ASSET_DECIMAL_POINTS")
    private int assetDecimalPoints;

    @JsonProperty("SUPPORTED_PLATFORMS")
    private List<Object> supportedPlatforms;

    @JsonProperty("ASSET_SECURITY_METRICS")
    private List<Object> assetSecurityMetrics;

    @JsonProperty("SUPPLY_MAX")
    private BigInteger supplyMax;

    @JsonProperty("SUPPLY_ISSUED")
    private BigInteger supplyIssued;

    @JsonProperty("SUPPLY_TOTAL")
    private BigInteger supplyTotal;

    @JsonProperty("SUPPLY_CIRCULATING")
    private BigInteger supplyCirculating;

    @JsonProperty("SUPPLY_FUTURE")
    private BigDecimal supplyFuture;

    @JsonProperty("SUPPLY_LOCKED")
    private BigInteger supplyLocked;

    @JsonProperty("SUPPLY_BURNT")
    private BigInteger supplyBurnt;

    @JsonProperty("SUPPLY_STAKED")
    private BigInteger supplyStaked;

    @JsonProperty("LAST_BLOCK_MINT")
    private double lastBlockMint;

    @JsonProperty("LAST_BLOCK_BURN")
    private Object lastBlockBurn;

    @JsonProperty("BURN_ADDRESSES")
    private Object burnAddresses;

    @JsonProperty("LOCKED_ADDRESSES")
    private Object lockedAddresses;

    @JsonProperty("HAS_SMART_CONTRACT_CAPABILITIES")
    private boolean hasSmartContractCapabilities;

    @JsonProperty("SMART_CONTRACT_SUPPORT_TYPE")
    private String smartContractSupportType;

    @JsonProperty("TARGET_BLOCK_MINT")
    private BigDecimal targetBlockMint;

    @JsonProperty("TARGET_BLOCK_TIME")
    private BigInteger targetBlockTime;

    @JsonProperty("LAST_BLOCK_NUMBER")
    private BigInteger lastBlockNumber;

    @JsonProperty("LAST_BLOCK_TIMESTAMP")
    private BigInteger lastBlockTimestamp;

    @JsonProperty("LAST_BLOCK_TIME")
    private int lastBlockTime;

    @JsonProperty("LAST_BLOCK_SIZE")
    private BigInteger lastBlockSize;

    @JsonProperty("LAST_BLOCK_ISSUER")
    private Object lastBlockIssuer;

    @JsonProperty("LAST_BLOCK_TRANSACTION_FEE_TOTAL")
    private Object lastBlockTransactionFeeTotal;

    @JsonProperty("LAST_BLOCK_TRANSACTION_COUNT")
    private int lastBlockTransactionCount;

    @JsonProperty("LAST_BLOCK_HASHES_PER_SECOND")
    private BigInteger lastBlockHashesPerSecond;

    @JsonProperty("LAST_BLOCK_DIFFICULTY")
    private long lastBlockDifficulty;

    @JsonProperty("SUPPORTED_STANDARDS")
    private List<Object> supportedStandards;

    @JsonProperty("LAYER_TWO_SOLUTIONS")
    private List<Object> layerTwoSolutions;

    @JsonProperty("PRIVACY_SOLUTIONS")
    private List<Object> privacySolutions;

    @JsonProperty("WEBSITE_URL")
    private String websiteUrl;

    @JsonProperty("BLOG_URL")
    private String blogUrl;

    @JsonProperty("WHITE_PAPER_URL")
    private String whitePaperUrl;

    @JsonProperty("OTHER_DOCUMENT_URLS")
    private Object otherDocumentUrls;

    @JsonProperty("EXPLORER_ADDRESSES")
    private List<Object> explorerAddresses;

    @JsonProperty("ASSET_INDUSTRIES")
    private List<Object> assetIndustries;

    @JsonProperty("CONSENSUS_MECHANISMS")
    private List<Object> consensusMechanisms;

    @JsonProperty("CONSENSUS_ALGORITHM_TYPES")
    private List<Object> consensusAlgorithmTypes;

    @JsonProperty("HASHING_ALGORITHM_TYPES")
    private List<Object> hashingAlgorithmTypes;

    @JsonProperty("PRICE_USD")
    private double priceUsd;

    @JsonProperty("PRICE_USD_SOURCE")
    private String priceUsdSource;

    @JsonProperty("PRICE_USD_LAST_UPDATE_TS")
    private long priceUsdLastUpdateTs;

    @JsonProperty("MKT_CAP_PENALTY")
    private int mktCapPenalty;

    @JsonProperty("CIRCULATING_MKT_CAP_USD")
    private double circulatingMktCapUsd;

    @JsonProperty("TOTAL_MKT_CAP_USD")
    private double totalMktCapUsd;

    @JsonProperty("SPOT_MOVING_24_HOUR_QUOTE_VOLUME_TOP_TIER_DIRECT_USD")
    private double spotMoving24HourQuoteVolumeTopTierDirectUsd;

    @JsonProperty("SPOT_MOVING_24_HOUR_QUOTE_VOLUME_DIRECT_USD")
    private double spotMoving24HourQuoteVolumeDirectUsd;

    @JsonProperty("SPOT_MOVING_24_HOUR_QUOTE_VOLUME_TOP_TIER_USD")
    private double spotMoving24HourQuoteVolumeTopTierUsd;

    @JsonProperty("SPOT_MOVING_24_HOUR_QUOTE_VOLUME_USD")
    private double spotMoving24HourQuoteVolumeUsd;

    @JsonProperty("SPOT_MOVING_24_HOUR_CHANGE_USD")
    private double spotMoving24HourChangeUsd;

    @JsonProperty("SPOT_MOVING_24_HOUR_CHANGE_PERCENTAGE_USD")
    private double spotMoving24HourChangePercentageUsd;

    @JsonProperty("TOPLIST_BASE_RANK")
    private Object toplistBaseRank;

    @JsonProperty("ASSET_DESCRIPTION")
    private String assetDescription;

    @JsonProperty("ASSET_DESCRIPTION_SUMMARY")
    private String assetDescriptionSummary;

    @JsonProperty("PROJECT_LEADERS")
    private List<ProjectLeader> projectLeaders;

    @JsonProperty("ASSOCIATED_CONTACT_DETAILS")
    private List<AssociatedContactDetail> associatedContactDetails;

    @JsonProperty("SEO_TITLE")
    private String seoTitle;

    @JsonProperty("SEO_DESCRIPTION")
    private String seoDescription;

    @Override
    public String toString() {
        return "AssetMetadata{id=%d, symbol='%s', name='%s', priceUsd=%s, uri='%s'}"
                .formatted(id, symbol, name, priceUsd, uri);
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class AssetAlternativeId {
        @JsonProperty("NAME")
        private String name;

        @JsonProperty("ID")
        private String id;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class ProjectLeader {
        @JsonProperty("LEADER_TYPE")
        private String leaderType;

        @JsonProperty("FULL_NAME")
        private String fullName;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class AssociatedContactDetail {
        @JsonProperty("CONTACT_MEDIUM")
        private String contactMedium;

        @JsonProperty("FULL_NAME")
        private String fullName;
    }

}


