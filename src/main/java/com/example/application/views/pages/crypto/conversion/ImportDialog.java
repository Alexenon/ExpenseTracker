package com.example.application.views.pages.crypto.conversion;


import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.util.unit.DataSize;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class ImportDialog extends Dialog {

    private static final int MAX_BUFFER_SIZE = (int) DataSize.ofMegabytes(10).toBytes();
    private final MemoryBuffer buffer = new MemoryBuffer();

    public ImportDialog() {
        Upload upload = new Upload(buffer);
        upload.setDropAllowed(false);
        upload.setAcceptedFileTypes(".csv", ".xlsx", ".xlsm", ".xlsb");
        upload.setMaxFileSize(MAX_BUFFER_SIZE);
        upload.addSucceededListener(e -> getUI().ifPresent(ui -> ui.access(() -> {
            try {
                displayCsv(buffer.getInputStream());
                ui.push();
            } catch (IOException ex) {
                System.out.println(ex.getLocalizedMessage());
                throw new RuntimeException(ex);
            }
        })));

        add(upload);
    }

    private void displayCsv(InputStream resourceAsStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(resourceAsStream));
        CSVParser parser = CSVParser.builder()
                .setFormat(CSVFormat.EXCEL)
                .setReader(reader)
                .setBufferSize(MAX_BUFFER_SIZE)
                .get();
//
//        List<String[]> records = parser.getRecords().stream()
//                .map(record -> record.toList().toArray(new String[0]))
//                .toList();

        List<List<String>> records = parser.getRecords().stream()
                .map(CSVRecord::toList)
                .toList();

        for (List<String> row : records) {
            System.out.println(row);
        }

        Grid<List<String>> grid = new Grid<>();
        grid.setItems(records);
        add(grid);
    }

//    private void setGridItems(List<String[]> entries) {
//        String[] headers = entries.getFirst();
//        for (int i = 0; i < headers.length; i++) {
//            int colIndex = i;
//            String colText = SharedUtil.camelCaseToHumanFriendly(headers[colIndex]);
//            grid.addColumn(row -> row[colIndex]).setHeader(colText);
//        }
//        grid.setItems(entries.subList(1, entries.size()));
//    }


}
