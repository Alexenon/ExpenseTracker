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
    private double price;           // $3200
    private double quantity;        // 0.001 ETH
    private double amountUSD;       // $200
    private TransactionType type;
    private LocalDateTime dateTime;

    public enum TransactionType {
        BUY,
        SELL,
        TRANSFER,   // TODO: to implement
        STAKE       // TODO: to implement
    }
}



