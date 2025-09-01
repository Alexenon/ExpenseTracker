package com.example.application.data.dtos.migration;

import com.example.application.entities.common.TransactionType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record TransactionModel(

        @NotNull
        @JsonFormat(pattern = "M.d.yyyy HH:mm:ss")
        @JsonProperty(value = "DateTime", required = true)
        LocalDateTime dateTime,

        @NotNull
        @JsonProperty(value = "Symbol", required = true)
        String symbol,

        @NotNull
        @Positive
        @JsonProperty(value = "Amount", required = true)
        Double amount,

        @NotNull
        @Positive
        @JsonProperty(value = "Price", required = true)
        Double price,

        @NotNull
        @JsonSetter(nulls = Nulls.SKIP)
        @JsonProperty(value = "Type", required = true)
        TransactionType type,

        @Nullable
        @JsonSetter(nulls = Nulls.SKIP)
        @JsonProperty(value = "Note")
        String note
) {}
