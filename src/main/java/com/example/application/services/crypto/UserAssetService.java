package com.example.application.services.crypto;

import com.example.application.entities.User;
import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.UserAsset;
import com.example.application.repositories.crypto.UserAssetRepository;
import com.example.application.utils.common.lang.StringUtils;
import com.example.application.utils.exceptions.InternalUnexpectedException;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class UserAssetService {

    private final UserAssetRepository userAssetRepository;

    public UserAssetService(UserAssetRepository userAssetRepository) {
        this.userAssetRepository = userAssetRepository;
    }

    public void updateAssetComment(@NotNull User user, @NotNull Asset asset, @Nullable String comment) {
        UserAsset userAsset = findByUserAndAsset(user, asset)
                .orElse(new UserAsset());

        userAsset.setComment(comment);
        update(userAsset);
    }

    public void updateMarkAssetAsFavorite(@NotNull User user, @NotNull Asset asset, boolean markAsFavorite) {
        UserAsset userAsset = findByUserAndAsset(user, asset)
                .orElse(new UserAsset());

        userAsset.setMarkedAsFavorite(markAsFavorite);
        update(userAsset);
    }

    public String getAssetComment(User user, Asset asset) {
        return findByUserAndAsset(user, asset)
                .map(UserAsset::getComment)
                .orElse(null);
    }

    public boolean isAssetMarkedAsFavorite(User user, Asset asset) {
        return findByUserAndAsset(user, asset)
                .map(UserAsset::isMarkedAsFavorite)
                .orElse(false);
    }

    public Optional<UserAsset> findByUserAndAsset(@NotNull User user, @NotNull Asset asset) {
        Objects.requireNonNull(user, "user");
        Objects.requireNonNull(asset, "asset");
        return userAssetRepository.findByUserAndAsset(user, asset);
    }

    public void update(@NotNull UserAsset userAsset) {
        validate(userAsset);

        if (StringUtils.isBlank(userAsset.getComment()) && !userAsset.isMarkedAsFavorite()) {
            delete(userAsset);
        } else {
            save(userAsset);
        }
    }

    @Transactional
    public void delete(@NotNull UserAsset userAsset) {
        Objects.requireNonNull(userAsset, "userAsset");
        try {
            userAssetRepository.delete(userAsset);
            log.info("Deleted successfully {}", userAsset);
        } catch (Exception e) {
            log.error("Failed to delete {}, cause: {}", userAsset, e.getMessage());
            throw new InternalUnexpectedException(e);
        }
    }

    @Transactional
    private UserAsset save(@NotNull UserAsset userAsset) {
        validate(userAsset);

        try {
            userAsset.setLastTimeUpdated(LocalDateTime.now());
            UserAsset entity = userAssetRepository.save(userAsset);
            log.info("Saved successfully {}", entity);
            return entity;
        } catch (Exception e) {
            log.error("Failed to save {}, cause: {}", userAsset, e.getMessage());
            ExceptionUtils.printRootCauseStackTrace(e);
            throw new InternalUnexpectedException(e);
        }
    }

    public void validate(UserAsset userAsset) {
        Objects.requireNonNull(userAsset, "user-asset");
        Assert.isTrue(userAsset.getUser() != null, "user is missing");
        Assert.isTrue(userAsset.getAsset() != null, "asset is missing");
        Assert.isTrue(userAsset.getLastTimeUpdated() != null, "lastTimeUpdated is missing");
    }

}
