package com.example.application.data.requests.asset_watchers;

import com.example.application.entities.common.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public final class CreateAssetWatcherRequest {

    private Long portfolioId;
    private String assetSymbol;
    private TransactionType transactionType;
    private BigDecimal targetPrice;
    private BigDecimal targetAmount;
    private boolean isCompleted;

}
