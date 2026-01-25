package com.example.application.data.requests.asset_watchers;

import com.example.application.entities.common.TransactionType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public final class CreateAssetWatcherRequest {

    private Long portfolioId;
    private String assetSymbol;
    private TransactionType transactionType;
    private double targetPrice;
    private double targetAmount;
    private boolean isCompleted;

}
