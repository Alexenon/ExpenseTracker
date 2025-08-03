package com.example.application.components;

import com.example.application.services.crypto.InstrumentsService;
import com.example.application.views.components.PriceChangeHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class AssetUpdateScheduler {

    @Autowired
    private PriceChangeHandler priceChangeHandler;

    @Autowired
    private InstrumentsService instrumentsService;

    @Scheduled(fixedDelay = 5, timeUnit = TimeUnit.MINUTES)
    public void updateAssets() {
        log.info("------------------- [Task] Updating asset data -------------------");
        instrumentsService.updateAssetData();
        priceChangeHandler.updatePagePrices();
    }

}