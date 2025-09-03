package com.example.application.data.dtos.migration;

import com.example.application.entities.common.TransactionType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class TransactionModel {

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

    public boolean isValid() {
        return this.dateTime != null
               || (this.symbol != null && !this.symbol.isBlank())
               || (this.amount != null && !this.amount.isNaN() && !this.amount.isInfinite())
               || (this.price != null && !this.price.isNaN() && !this.price.isInfinite())
               || this.type != null;
    }

}