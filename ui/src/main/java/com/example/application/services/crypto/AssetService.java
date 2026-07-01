package com.example.application.services.crypto;


import com.example.application.data.convertors.AssetConvertor;
import com.example.application.data.requests.asset.CreateAssetRequest;
import com.example.application.data.requests.asset.UpdateAssetRequest;
import com.example.application.entities.crypto.Asset;
import com.example.application.repositories.crypto.AssetRepository;
import com.example.application.utils.common.lang.StringUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssetService {

	private final AssetRepository assetRepository;
	private final EntityValidator validator;

	public Optional<Asset> findById(Long assetId) {
		Objects.requireNonNull(assetId, "assetId");
		return assetRepository.findById(assetId);
	}

	public Optional<Asset> findBySymbol(@Nullable String symbolName) {
		return Optional.ofNullable(symbolName)
				.map(String::trim)
				.filter(s -> !s.isEmpty())
				.map(String::toUpperCase)
				.flatMap(assetRepository::findBySymbol);
	}

	public List<Asset> findAll() {
		return assetRepository.findAll();
	}

	/**
	 * @throws InvalidDataException        when validating an invalid asset that comes from external resources
	 * @throws InternalUnexpectedException when there is any issue related to save the entity to the database
	 */
	@Transactional
	public Asset createNewAsset(CreateAssetRequest request) {
		log.info("Creating new asset: {}", request);
		validator.validate(request);
		Asset asset = AssetConvertor.mapToEntity(request);
		return save(asset);
	}

	@Transactional
	public Asset updateAsset(UpdateAssetRequest request) {
		log.info("Updating asset: {}", request);

		Asset asset = findById(request.getAssetId())
				.orElseThrow(() -> new EntityNotFoundException("Cannot update a non existent asset"));

		Map<String, String> validationErrors = validator.validateSafe(request);

		if (validationErrors.isEmpty()) {
			log.info("UpdateRequest has all fields valid, updating all fields");
			asset.setSummaryDescription(request.getSummaryDescription());
			asset.setMarketPrice(request.getMarketPrice());
			asset.setChangePercentage(request.getChangePercentage());
			asset.setTodayVolume(request.getTodayVolume());
			asset.setCirculationSupply(request.getCirculationSupply());
			asset.setTotalSupply(request.getTotalSupply());
			asset.setTotalMarketCap(request.getTotalMarketCap());
			asset.setImageUrl(request.getImageUrl());
			return save(asset);
		}

		return save(validatedAsset(request));
	}

	/**
	 * @throws InternalUnexpectedException when there is any issue related to save the entity to the database
	 */
	private Asset save(Asset asset) {
		try {
			log.info("Saving {}", asset.toFullString());
			Asset saved = assetRepository.save(asset);
			log.info("Saved successfully {}", saved);
			return saved;
		} catch (Exception e) {
			log.error("Failed to save {}", asset.toFullString(), e);
			throw new InternalUnexpectedException(e);
		}
	}

	@Transactional
	public void delete(Long assetId) {
		try {
			assetRepository.deleteById(assetId);
			log.info("Deleted successfully asset: #{}", assetId);
		} catch (Exception e) {
			log.error("Failed to delete asset: #{}", assetId, e);
			throw new InternalUnexpectedException(e);
		}
	}

	private Asset validatedAsset(UpdateAssetRequest updateAssetRequest) {
		Objects.requireNonNull(updateAssetRequest, "updateAssetRequest");

		Asset oldAsset = findById(updateAssetRequest.getAssetId())
				.orElseThrow(() -> new EntityNotFoundException("Cannot find asset: #" + updateAssetRequest.getAssetId()));

		oldAsset.setImageUrl(validatedField(updateAssetRequest, oldAsset, UpdateAssetRequest::getImageUrl, StringUtils::isBlank, "image url"));
		oldAsset.setSummaryDescription(validatedField(updateAssetRequest, oldAsset, UpdateAssetRequest::getSummaryDescription, StringUtils::isBlank, "description"));

		Function<UpdateAssetRequest, BigInteger> totalSupplyValue = UpdateAssetRequest::getCirculationSupply;
		BigInteger totalSupply = validatedField(updateAssetRequest, oldAsset, totalSupplyValue, supply -> supply.signum() < 0, "total supply");
		oldAsset.setTotalSupply(totalSupply);

		Function<UpdateAssetRequest, BigInteger> circulationSupplyValue = UpdateAssetRequest::getCirculationSupply;
		BigInteger circulationSupply = validatedField(updateAssetRequest, oldAsset, circulationSupplyValue, supply -> supply.signum() < 0, "circulation supply");
		oldAsset.setCirculationSupply(circulationSupply);

		Function<UpdateAssetRequest, BigInteger> marketCapValue = UpdateAssetRequest::getCirculationSupply;
		BigInteger totalMarketCap = validatedField(updateAssetRequest, oldAsset, marketCapValue, marketCap -> marketCap.signum() < 0, "total market cap");
		oldAsset.setTotalMarketCap(totalMarketCap);

		Function<UpdateAssetRequest, BigDecimal> changePercentage = UpdateAssetRequest::getChangePercentage;
		oldAsset.setChangePercentage(validatedField(updateAssetRequest, oldAsset, changePercentage, _ -> false, "change percentage"));

		Function<UpdateAssetRequest, BigInteger> todayVolume = UpdateAssetRequest::getTodayVolume;
		oldAsset.setTodayVolume(validatedField(updateAssetRequest, oldAsset, todayVolume, volume -> volume.signum() < 0, "today volume"));

		oldAsset.setMarketPrice(validatedField(updateAssetRequest, oldAsset, UpdateAssetRequest::getMarketPrice, price -> price == null || price.signum() < 0, "market price"));

		return oldAsset;
	}

	/**
	 * Checks the new value for the asset, if it's valid then it overrides the old value. If the new value is not valid,
	 * then it uses the previous value stored in the database.
	 *
	 * @throws InvalidDataException in case no previous value is stored in the database, and current value is invalid
	 */
	private <T> T validatedField(UpdateAssetRequest updateAssetRequest,
								 Asset oldAsset,
								 Function<UpdateAssetRequest, T> getter, Predicate<T> invalidCheck, String fieldName)
	{
		String symbol = oldAsset.getSymbol();
		T newValue = getter.apply(updateAssetRequest);
		T oldValue = getter.apply(updateAssetRequest);

		// If new value is valid, override the old value
		if (!invalidCheck.test(newValue)) {
			logUpdateInfo(fieldName, newValue, oldValue, symbol);
			return newValue;
		}

		log.debug("Warning: asset {} [{}] field uses old value: '{}' instead of '{}'", symbol, fieldName, oldValue, newValue);
		return oldValue;
	}

	private static <T> void logUpdateInfo(String fieldName, T newValue, T oldValue, String symbol) {
		if (!Objects.equals(oldValue, newValue))
			log.debug("Updated asset {} [{}] field: '{}' from '{}'", symbol, fieldName, newValue, oldValue);
	}

}