package com.example.application.components;

import com.example.application.views.components.PriceChangeNotifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class AssetUpdateScheduler {

	@Autowired
	private InstrumentsService instrumentsService;

	@Autowired
	private PriceChangeNotifier priceChangeNotifier;

	@Scheduled(fixedDelay = 5, timeUnit = TimeUnit.MINUTES)
	public void updateAssets() {
		log.info("------------------- [Task] Starting updating asset data -------------------");
		instrumentsService.updateAssetData();
		priceChangeNotifier.updatePagePrices();
		log.info("------------------- [Task] Finished updating asset data -------------------");
	}

}