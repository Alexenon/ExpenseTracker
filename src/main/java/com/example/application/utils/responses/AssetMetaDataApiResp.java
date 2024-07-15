package com.example.application.utils.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * API Response for coin metadata information from Crypto Compare platform <br>
 *
 * <a href="https://developers.cryptocompare.com/documentation/data-api/asset_v1_metadata">Documentation</a>
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AssetMetaDataApiResp {

    @JsonProperty("Data")
    private AssetData data;

    @JsonProperty("Err")
    private ErrorData error;

}
