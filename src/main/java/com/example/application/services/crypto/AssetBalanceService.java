package com.example.application.services.crypto;

import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.AssetBalance;
import com.example.application.entities.crypto.CryptoTransaction;
import com.example.application.entities.crypto.Portfolio;
import com.example.application.repositories.crypto.AssetBalanceRepository;
import com.example.application.utils.common.lang.MathUtils;
import com.example.application.utils.common.lang.NumberUtils;
import com.example.application.utils.exceptions.InternalUnexpectedException;
import com.example.application.utils.exceptions.InvalidBalanceAmountException;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.example.application.utils.investment.ProfitCalculator.calculateNewAvgPrice;

@Service
public class AssetBalanceService {

    @Autowired
    private AssetBalanceRepository repository;

    // TODO: [URGENT] FIND A WAY TO EXTRACT THIS FROM DATABASE WITHOUT ANY EXCEPTIONS
    // TODO: DO WE NEED THIS READONLY ? MAKE SURE THAT EVERYTIME WE CALL IT ITS ALREADY SAFE
    public List<AssetBalance> findHoldingsByPortfolio(@NotNull Portfolio portfolio) {
        Objects.requireNonNull(portfolio, "portfolio");
//        return repository.findByPortfolioWithNonZeroAmount(portfolio.getId());
        return List.of();
    }

    public List<AssetBalance> findByPortfolio(@NotNull Portfolio portfolio) {
        return repository.findByPortfolio(Objects.requireNonNull(portfolio, "portfolio"));
    }

    public Optional<AssetBalance> findByPortfolioAndAsset(@NotNull Portfolio portfolio, @NotNull Asset asset) {
        Objects.requireNonNull(portfolio, "portfolio");
        Objects.requireNonNull(asset, "asset");
        return repository.findByPortfolioAndAsset(portfolio, asset);
    }

    @Transactional
    public AssetBalance createNew(@NotNull Portfolio portfolio, @NotNull Asset asset) {
        AssetBalance assetBalance = new AssetBalance();
        assetBalance.setPortfolio(Objects.requireNonNull(portfolio, "portfolio"));
        assetBalance.setAsset(Objects.requireNonNull(asset, "asset"));
        return save(assetBalance);
    }

    @Transactional
    public AssetBalance update(@NotNull AssetBalance assetBalance, @NotNull CryptoTransaction transaction) {
        validate(assetBalance);

        updateAvgBuySellPrice(assetBalance, transaction);
        assetBalance.setAmount(calculateAmountAfterSupply(assetBalance, transaction));
        assetBalance.setCost(calculateTotalCost(transaction, assetBalance));
        assetBalance.setLastTimeUpdated(LocalDateTime.now());

        return save(assetBalance);
    }

    @Transactional
    private AssetBalance save(@NotNull AssetBalance assetBalance) {
        try {
            return repository.save(assetBalance);
        } catch (Exception e) {
            throw new InternalUnexpectedException(e);
        }
    }

    private static double calculateTotalCost(CryptoTransaction transaction, AssetBalance assetBalance) {
        double newCost = MathUtils.withSign(transaction.getOrderTotalCost(), transaction.isBuyTransaction());
        return NumberUtils.checkDouble(assetBalance.getCost() + newCost);
    }

    private static double calculateAmountAfterSupply(AssetBalance assetBalance, CryptoTransaction transaction) {
        double transactionAmount = NumberUtils.checkDouble(transaction.getOrderQuantity());

        if (transactionAmount <= 0)
            throw new InvalidBalanceAmountException("Invalid transaction amount");

        double quantityToAdd = MathUtils.withSign(transactionAmount, transaction.isBuyTransaction());
        double tokensAmountAfterSupply = assetBalance.getAmount() + quantityToAdd;

        return NumberUtils.checkDouble(tokensAmountAfterSupply);
    }

    private static void updateAvgBuySellPrice(@NotNull AssetBalance assetBalance, @NotNull CryptoTransaction transaction) {
        double marketPrice = transaction.getMarketPrice();
        double previousAmount = assetBalance.getAmount();
        double orderQuantity = transaction.getOrderQuantity();

        if (transaction.isBuyTransaction()) {
            assetBalance.setAvgBuyPrice(
                    calculateNewAvgPrice(assetBalance.getAvgBuyPrice(), previousAmount, orderQuantity, marketPrice)
            );
        } else {
            assetBalance.setAvgSellPrice(
                    calculateNewAvgPrice(assetBalance.getAvgSellPrice(), previousAmount, orderQuantity, marketPrice)
            );
        }
    }

    private static void validate(AssetBalance assetBalance) {
        Objects.requireNonNull(assetBalance, "assetBalance");
        Objects.requireNonNull(assetBalance.getAsset(), "assetBalance asset");
        Objects.requireNonNull(assetBalance.getPortfolio(), "assetBalance portfolio");
        Objects.requireNonNull(assetBalance.getCreatedAt(), "assetBalance createdAt");
        Objects.requireNonNull(assetBalance.getLastTimeUpdated(), "assetBalance lastTimeUpdated");

        Assert.isTrue(assetBalance.getAmount() >= 0, "amount cannot be negative");
        Assert.isTrue(assetBalance.getHoldingDays() >= 0, "holdingDays cannot be negative");
        Assert.isTrue(assetBalance.getAvgBuyPrice() >= 0, "avgBuyPrice cannot be negative");
        Assert.isTrue(assetBalance.getAvgSellPrice() >= 0, "avgSellPrice cannot be negative");
    }

}
