package com.example.application.views.pages.crypto.conversion;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.shared.util.SharedUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class ImportDialog extends Dialog {

//    @Autowired
//    private InstrumentsFacadeService instrumentsFacadeService;

    private final Grid<String[]> grid = new Grid<>();

    public ImportDialog() {
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setDropAllowed(false);
        upload.setAcceptedFileTypes(".csv", ".xlsx", ".xlsm", ".xlsb");
        upload.addSucceededListener(e -> displayCsv(buffer.getInputStream()));
        add(upload, grid);
    }

    @SuppressWarnings("resource")
    private void displayCsv(InputStream resourceAsStream) {
        grid.removeAllColumns();
        CSVParser parser = new CSVParserBuilder().withSeparator(',').build();
        InputStreamReader streamReader = new InputStreamReader(resourceAsStream);
        CSVReader reader = new CSVReaderBuilder(streamReader).withCSVParser(parser).build();
        try {
            setGridItems(reader.readAll());
        } catch (IOException | CsvException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    private void setGridItems(List<String[]> entries) {
        String[] headers = entries.getFirst();
        for (int i = 0; i < headers.length; i++) {
            int colIndex = i;
            String colText = SharedUtil.camelCaseToHumanFriendly(headers[colIndex]);
            grid.addColumn(row -> row[colIndex]).setHeader(colText);
        }
        grid.setItems(entries.subList(1, entries.size()));
    }


}
