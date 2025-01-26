package com.example.application.entities;

import com.example.application.services.crypto.InstrumentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DBOperationRunner implements CommandLineRunner {

    @Autowired
    private InstrumentsService instrumentsService;

    @Override
    public void run(String... args) throws Exception {
        instrumentsService.updateDatabase();
    }

}

