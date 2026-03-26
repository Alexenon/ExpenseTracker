package com.example.application.services.crypto;

import com.example.application.entities.crypto.Asset;
import com.example.application.repositories.crypto.AssetRepository;
import com.example.application.utils.common.lang.StringUtils;
import com.example.application.utils.exceptions.InternalUnexpectedException;
import com.example.application.utils.exceptions.InvalidDataException;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

@Slf4j
@Service
public class AssetService {

	@Autowired
	private AssetRepository assetRepository;

	public Optional<Asset> findById(Long assetId) {
		Objects.requireNonNull(assetId, "assetId");
		return assetRepository.findById(assetId);
	}

	public Optional<Asset> findBySymbol(@NotNull String symbolName) {
		return Optional.of(symbolName)
				.map(String::trim)
				.filter(s -> !s.isEmpty())
				.map(String::toUpperCase)
				.flatMap(assetRepository::findBySymbol);
	}

	public List<Asset> findAll() {
		return assetRepository.findAll();
	}

	public void delete(Asset asset) {
		try {
			assetRepository.delete(asset);
			log.info("Deleted successfully {}", asset);
		} catch (Exception e) {
			log.error("Failed to delete {}", asset, e);
			throw new InternalUnexpectedException(e);
		}
	}

	/**
	 * @throws InvalidDataException        when validating an invalid asset that comes from external resources
	 * @throws InternalUnexpectedException when there is any issue related to save the entity to the database
	 */
	@Nullable
	public Asset save(@NotNull Asset asset) {
		try {
			Asset saved = assetRepository.save(validatedAsset(asset));
			log.debug("Saved successfully {}", saved);
			return saved;
		} catch (InvalidDataException e) {
			log.error("Failed to save invalid {}", asset, e);
			return null;
		} catch (Exception e) {
			log.error("Failed to save {}", asset, e);
			throw new InternalUnexpectedException(e);
		}
	}

	private Asset validatedAsset(Asset asset) {
		Objects.requireNonNull(asset, "asset");
		Asset target = findBySymbol(asset.getSymbol())
				.orElse(new Asset());

		target.setSymbol(validatedField(asset, target, Asset::getSymbol, StringUtils::isBlank, "symbol"));
		target.setFullName(validatedField(asset, target, Asset::getFullName, StringUtils::isBlank, "full name"));
		target.setImageUrl(validatedField(asset, target, Asset::getImageUrl, StringUtils::isBlank, "image url"));
		target.setSummaryDescription(validatedField(asset, target, Asset::getSummaryDescription, StringUtils::isBlank, "description"));

		Function<Asset, BigInteger> totalSupplyValue = a -> Objects.requireNonNullElse(a.getCirculationSupply(), BigInteger.ZERO);
		BigInteger totalSupply = validatedField(asset, target, totalSupplyValue, supply -> supply.signum() < 0, "total supply");
		target.setTotalSupply(totalSupply);

		Function<Asset, BigInteger> circulationSupplyValue = a -> Objects.requireNonNullElse(a.getCirculationSupply(), BigInteger.ZERO);
		BigInteger circulationSupply = validatedField(asset, target, circulationSupplyValue, supply -> supply.signum() < 0, "circulation supply");
		target.setCirculationSupply(circulationSupply);

		Function<Asset, BigInteger> marketCapValue = a -> Objects.requireNonNullElse(a.getCirculationSupply(), BigInteger.ZERO);
		BigInteger totalMarketCap = validatedField(asset, target, marketCapValue, marketCap -> marketCap.signum() < 0, "total market cap");
		target.setTotalMarketCap(totalMarketCap);

		Function<Asset, BigDecimal> changePercentage = a -> Objects.requireNonNullElse(a.getChangePercentage(), BigDecimal.ZERO);
		target.setChangePercentage(validatedField(asset, target, changePercentage, _ -> false, "change percentage"));

		Function<Asset, BigInteger> todayVolume = a -> Objects.requireNonNullElse(a.getTodayVolume(), BigInteger.ZERO);
		target.setTodayVolume(validatedField(asset, target, todayVolume, volume -> volume.signum() < 0, "today volume"));

		target.setMarketPrice(validatedField(asset, target, Asset::getMarketPrice, price -> price == null || price.signum() < 0, "market price"));

		return target;
	}

	/**
	 * @return either the value for the field from new asset if it's valid, or the value from old asset if it's not valid
	 * */
	private <T> T validatedField(@NotNull Asset newAsset,
								 @Nullable Asset oldAsset,
								 Function<Asset, T> getter, Predicate<T> invalidCheck, String fieldName)
	{
		T newValue = getter.apply(newAsset);
		String symbol = newAsset.getSymbol();

		if (!invalidCheck.test(newValue)) {
			logUpdateInfo(oldAsset, getter, fieldName, newValue, symbol);
			return newValue;
		}

		if (oldAsset == null)
			throw new InvalidDataException("Invalid asset %s: %s".formatted(fieldName, newValue));

		T oldValue = getter.apply(oldAsset);
		log.debug("Warning: asset {} [{}] field uses old value: '{}' instead of '{}'", symbol, fieldName, oldValue, newValue);
		return oldValue;
	}

	private static <T> void logUpdateInfo(Asset oldAsset, Function<Asset, T> getter, String fieldName, T newValue, String symbol) {
		if (oldAsset != null) {
			T oldValue = getter.apply(oldAsset);
			if (!Objects.equals(oldValue, newValue))
				log.debug("Updated asset {} [{}] field: '{}' from '{}'", symbol, fieldName, newValue, oldValue);
		}
	}

}