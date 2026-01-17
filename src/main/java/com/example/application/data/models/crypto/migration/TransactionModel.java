package com.example.application.data.models.crypto.migration;

import com.example.application.entities.common.TransactionType;
import com.example.application.entities.crypto.Transaction;
import com.fasterxml.jackson.annotation.*;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@JsonPropertyOrder({ "dateTime", "symbol", "amount", "price", "type", "note" })
public class TransactionModel implements Serializable {

    @Serial
    private static final long serialVersionUID = 3794134806418825618L;

    @JsonIgnore
    private final String id = UUID.randomUUID().toString();

    @NotNull
    @JsonFormat(pattern = "M.d.yyyy HH:mm:ss")
    @JsonProperty(value = "DateTime", required = true)
    private LocalDateTime dateTime;

    @NotNull
    @JsonProperty(value = "Symbol", required = true)
    private String symbol;

    @NotNull
    @Positive
    @JsonProperty(value = "Amount", required = true)
    private Double amount;

    @NotNull
    @Positive
    @JsonProperty(value = "Price", required = true)
    private Double price;

    @NotNull
    @JsonProperty(value = "Type", required = true)
    private TransactionType type;

    @Nullable
    @JsonSetter(nulls = Nulls.SKIP)
    @JsonProperty(value = "Note")
    private String note;

    @JsonIgnore
    public boolean isValid() {
        return this.dateTime != null
               || (this.symbol != null && !this.symbol.isBlank())
               || (this.amount != null && !this.amount.isNaN() && !this.amount.isInfinite())
               || (this.price != null && !this.price.isNaN() && !this.price.isInfinite())
               || this.type != null;
    }

    public static TransactionModel from(Transaction transaction) {
        TransactionModel transactionModel = new TransactionModel();
        transactionModel.setSymbol(transaction.getAsset().getSymbol());
        transactionModel.setType(transaction.getType());
        transactionModel.setPrice(transaction.getMarketPrice());
        transactionModel.setAmount(transaction.getOrderQuantity());
        transactionModel.setDateTime(transaction.getDateTime());
        return transactionModel;
    }

}