package com.example.application.services.crypto;

import com.example.application.entities.crypto.Asset;
import com.example.application.repositories.crypto.AssetRepository;
import com.example.application.utils.exceptions.InternalUnexpectedException;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class AssetService {

    @Autowired
    private AssetRepository assetRepository;

    public List<Asset> findAll() {
        return assetRepository.findAll();
    }

    public Optional<Asset> findBySymbol(String symbolName) {
        return Optional.ofNullable(symbolName)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toUpperCase)
                .flatMap(assetRepository::findBySymbol);
    }

    @NotNull
    @Transactional
    public Asset save(@NotNull Asset asset) {
        Objects.requireNonNull(asset, "asset");
        try {
            Asset saved = assetRepository.save(asset);
            log.info("Saved successfully {}", asset);
            return saved;
        } catch (Exception e) {
            log.error("Failed to save {}, cause: {}", asset, e.getMessage());
            ExceptionUtils.printRootCauseStackTrace(e);
            throw new InternalUnexpectedException(e);
        }
    }

    public void delete(Asset asset) {
        try {
            assetRepository.delete(asset);
            log.info("Deleted successfully {}", asset);
        } catch (Exception e) {
            log.error("Failed to delete {}, cause: {}", asset, e.getMessage());
            throw new InternalUnexpectedException(e);
        }
    }

}
