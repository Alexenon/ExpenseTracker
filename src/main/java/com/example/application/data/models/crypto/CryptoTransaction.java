package com.example.application.data.models.crypto;

import com.example.application.entities.crypto.Asset;
import lombok.Data;

import java.time.LocalDateTime;

/* User can use
 *   - coin amount to sell/buy
 *   - money amount to sell/buy
 * */

@Data
public class CryptoTransaction {
    private Asset asset;
    private double amount;
    private TransactionType type;
    private LocalDateTime dateTime;

    public enum TransactionType {
        BUY,
        SELL,
        TRANSFER,   // TODO: to implement
        STAKE       // TODO: to implement
    }
}



