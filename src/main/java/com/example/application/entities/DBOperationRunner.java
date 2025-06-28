package com.example.application.entities;

import com.example.application.services.CategoryService;
import com.example.application.services.crypto.InstrumentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

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

}

