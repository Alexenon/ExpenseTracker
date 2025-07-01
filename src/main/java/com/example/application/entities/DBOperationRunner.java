package com.example.application.entities;

import com.example.application.services.CategoryService;
import com.example.application.services.crypto.InstrumentsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class DBOperationRunner implements CommandLineRunner {

    @Autowired
    private InstrumentsService instrumentsService;

    @Autowired
    private CategoryService categoryService;

    @Override
    public void run(String... args) throws Exception {
        updateDatabase();
    }

    private void updateDatabase() {
        instrumentsService.updateAssetData();
        categoryService.saveCategoriesInBatch();
    }

    /**
     * Every 5 minutes, updates database with prices
     * */
    private void updateSistematically() {
        Runnable r = () -> instrumentsService.updateAssetData();
        try (ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor()) {
            executor.scheduleWithFixedDelay(r, 5, 5, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("Error: {}", ExceptionUtils.getStackTrace(e));
        }
    }

}

