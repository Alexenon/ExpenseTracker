package com.example.application.utils.conversions;

import com.example.application.services.crypto.InstrumentsFacadeService;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/*
    TODO:
        - https://vaadin.com/blog/read-and-display-a-csv-file-in-java

* */

@Component
public class BinanceConvertor {

    @Autowired
    private InstrumentsFacadeService instrumentsFacadeService;

    @SuppressWarnings("resource")
    public void importFile(InputStream resourceAsStream) {
        CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
        InputStreamReader streamReader = new InputStreamReader(resourceAsStream);
        CSVReader reader = new CSVReaderBuilder(streamReader).withCSVParser(parser).build();

        try {
            List<String[]> entries = reader.readAll();
            String[] headers = entries.getFirst();

            for (int i = 0; i < headers.length; i++) {

            }

        } catch (IOException | CsvException e) {
            throw new RuntimeException(e);
        }
    }

    public void exportFile() {

    }


}
