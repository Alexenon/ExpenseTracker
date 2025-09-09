package com.example.application.utils.common.parsers;

import com.example.application.data.dtos.migration.TransactionModel;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vaadin.flow.server.StreamResource;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.List;

public class CSVParser implements Parser<TransactionModel> {

    private static final CsvMapper MAPPER = CsvMapper.builder()
            .addModule(new JavaTimeModule())
            .enable(CsvParser.Feature.SKIP_EMPTY_LINES)
            .enable(CsvParser.Feature.EMPTY_STRING_AS_NULL)
            .enable(CsvParser.Feature.FAIL_ON_MISSING_HEADER_COLUMNS)
            .defaultDateFormat(new SimpleDateFormat("M.d.yyyy HH:mm:ss"))
            .build();

    private static final CsvSchema SCHEMA = MAPPER.schemaFor(TransactionModel.class)
            .withHeader();

    /**
     * @throws RuntimeException when an issue encountered
     */
    @Override
    public List<TransactionModel> parseImport(InputStream stream) {
        try {
            return MAPPER.readerFor(TransactionModel.class)
                    .with(SCHEMA)
                    .<TransactionModel>readValues(stream)
                    .readAll()
                    .stream()
                    .filter(TransactionModel::isValid)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Cannot parse the provided file, cause: %s".formatted(e.getMessage()), e);
        }
    }

    /**
     * @throws RuntimeException when an issue encountered
     */
    @Override
    public StreamResource parseExport(List<TransactionModel> transactions) {
        return new StreamResource("transactions.csv", () -> {
            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                Writer writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
                MAPPER.writer(SCHEMA).writeValue(writer, transactions);
                return new ByteArrayInputStream(out.toByteArray());
            } catch (IOException e) {
                throw new UncheckedIOException("Failed to export CSV", e);
            }
        });
    }

    private Path createCSVFile(List<TransactionModel> list) {
        try {
            Path file = Files.createTempFile("transactions-", ".csv");
            try (Writer writer = Files.newBufferedWriter(file)) {
                MAPPER.writer(SCHEMA).writeValue(writer, list);
            }
            return file;
        } catch (IOException e) {
            throw new RuntimeException("Failed to create CSV file, cause: %s".formatted(e.getMessage()), e);
        }
    }
}
