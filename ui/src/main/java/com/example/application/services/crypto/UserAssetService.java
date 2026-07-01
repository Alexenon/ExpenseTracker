package com.example.application.services.crypto;


import com.example.application.entities.crypto.UserAsset;
import com.example.application.repositories.crypto.UserAssetRepository;
import com.example.application.utils.common.lang.StringUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * Service responsible for API on the {@link UserAsset} entity
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserAssetService {

	private final UserAssetRepository userAssetRepository;
	private final EntityValidator validator;

	public Optional<UserAsset> findById(Long userAssetId) {
		return userAssetRepository.findById(Objects.requireNonNull(userAssetId, "userAssetId"));
	}

	public Optional<UserAsset> findByUserAndAsset(@NotNull Long userId, @NotNull String assetSymbol) {
		Objects.requireNonNull(userId, "userId");
		Objects.requireNonNull(assetSymbol, "assetSymbol");
		return userAssetRepository.findByUserAndAsset(userId, assetSymbol);
	}

	@Transactional
	public void updateAssetComment(@NotNull Long userId, @NotNull String assetSymbol, @Nullable String comment) {
		UserAsset userAsset = findByUserAndAsset(userId, assetSymbol)
				.orElse(new UserAsset());

		userAsset.setComment(comment);
		update(userAsset);
	}

	@Transactional
	public void updateMarkAssetAsFavorite(@NotNull Long userId, @NotNull String assetSymbol, boolean markAsFavorite) {
		UserAsset userAsset = findByUserAndAsset(userId, assetSymbol)
				.orElse(new UserAsset());

		userAsset.setMarkedAsFavorite(markAsFavorite);
		update(userAsset);
	}

	@Nullable
	public String getAssetComment(Long userId, String assetSymbol) {
		return findByUserAndAsset(userId, assetSymbol)
				.map(UserAsset::getComment)
				.orElse(null);
	}

	public boolean isAssetMarkedAsFavorite(Long userId, String assetSymbol) {
		return findByUserAndAsset(userId, assetSymbol)
				.map(UserAsset::isMarkedAsFavorite)
				.orElse(false);
	}

	@Transactional
	private void update(@NotNull UserAsset userAsset) {
		validator.validate(userAsset);

		if (StringUtils.isBlank(userAsset.getComment()) && !userAsset.isMarkedAsFavorite()) {
			delete(userAsset.getId());
		} else {
			save(userAsset);
		}
	}

	@Transactional
	private UserAsset save(@NotNull UserAsset userAsset) {
		validator.validate(userAsset);
		try {
			userAsset.setLastTimeUpdated(LocalDateTime.now());
			UserAsset entity = userAssetRepository.save(userAsset);
			log.info("Saved successfully {}", entity);
			return entity;
		} catch (Exception e) {
			log.error("Failed to save {}", userAsset, e);
			throw new InternalUnexpectedException(e);
		}
	}

	@Transactional
	public void delete(@NotNull Long userAssetId) {
		UserAsset userAsset = findById(userAssetId)
				.orElseThrow(() -> new EntityNotFoundException("Cannot find user asset by id: #" + userAssetId));

		try {
			userAssetRepository.delete(userAsset);
			log.info("Deleted successfully {}", userAssetId);
		} catch (Exception e) {
			log.error("Failed to delete {}", userAsset, e);
			throw new InternalUnexpectedException(e);
		}
	}

}
