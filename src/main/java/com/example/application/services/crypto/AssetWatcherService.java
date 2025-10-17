package com.example.application.services.crypto;

import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.AssetWatcher;
import com.example.application.entities.crypto.Portfolio;
import com.example.application.repositories.crypto.AssetWatcherRepository;
import com.example.application.utils.exceptions.InternalUnexpectedException;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Objects;

/*
 * TODO: [LONG TERM] Add retrieve from holdings, on successful state
 *
 * TODO: [VERY URGENT] - DECIDE WHAT IF AN ASSET IS EMPTY IN THE FIND_ANY
 *    EITHER, ENSURE THAT NEVER PASS A NULL ASSET, OR ANY NULL ENITITY IN ANY OF THE SERVICES,
 *    OR IN CASE ITS NULL, RETURN AN EMPTY LIST
 *      PROS - {SILENT FAIL, NO EXCEPTION THROWN}
 *      CONS - {WAY HARDER TO SEE IF SOMETHING WENT WRONG}
 * */

@Service
public class AssetWatcherService {

    @Autowired
    private AssetWatcherRepository assetWatcherRepository;

    @NotNull
    @Transactional
    public AssetWatcher save(@NotNull AssetWatcher assetWatcher) {
        validate(assetWatcher);
        try {
            return assetWatcherRepository.save(assetWatcher);
        } catch (Exception e) {
            throw new InternalUnexpectedException(e);
        }
    }

    @Transactional
    public void delete(@NotNull AssetWatcher assetWatcher) {
        Objects.requireNonNull(assetWatcher, "assetWatcher");
        try {
            assetWatcherRepository.delete(assetWatcher);
        } catch (Exception e) {
            throw new InternalUnexpectedException(e);
        }
    }

    public List<AssetWatcher> findBy(@NotNull Portfolio portfolio) {
        Objects.requireNonNull(portfolio, "portfolio");
        return assetWatcherRepository.findByPortfolio(portfolio);
    }

    public List<AssetWatcher> findBy(@NotNull Asset asset) {
        Objects.requireNonNull(asset, "asset");
        return assetWatcherRepository.findByAsset(asset);
    }

    public List<AssetWatcher> findBy(@NotNull Portfolio portfolio, @NotNull Asset asset) {
        Objects.requireNonNull(portfolio, "portfolio");
        Objects.requireNonNull(asset, "asset");
        return assetWatcherRepository.findByPortfolioAndAsset(portfolio, asset);
    }

    public List<AssetWatcher> findBy(@NotNull Portfolio portfolio,
                                     @NotNull Asset asset,
                                     @NotNull AssetWatcher.ActionType actionType)
    {
        Objects.requireNonNull(portfolio, "portfolio");
        Objects.requireNonNull(asset, "asset");
        Objects.requireNonNull(actionType, "actionType");
        return assetWatcherRepository.findByPortfolioAndAssetAndActionType(portfolio, asset, actionType);
    }

    private void validate(AssetWatcher assetWatcher) {
        Objects.requireNonNull(assetWatcher, "assetWatcher");
        Assert.notNull(assetWatcher.getAsset(), "Asset is missing");
        Assert.notNull(assetWatcher.getPortfolio(), "Portfolio is missing");
        Assert.notNull(assetWatcher.getActionType(), "ActionType is missing");
        Assert.notNull(assetWatcher.getTargetType(), "TargetType is missing");
        Assert.isTrue(assetWatcher.getTargetAmount() >= 0, "amount cannot be negative");
    }

}
