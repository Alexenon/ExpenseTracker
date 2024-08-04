package com.example.application.services;

import com.example.application.data.enums.Symbols;
import com.example.application.entities.crypto.Asset;
import com.example.application.repositories.AssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InstrumentsService {

    @Autowired
    private AssetRepository assetRepository;

    public void updateDatabase() {
        Symbols.getAll().forEach(symbol -> {
            if (assetRepository.findBySymbol(symbol) == null) {
                assetRepository.save(new Asset(symbol));
            }
        });

        System.out.println("Filled database");
    }

}
