package com.example.application.utils.common.parsers;

import com.example.application.data.dtos.migration.TransactionModel;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vaadin.flow.server.StreamResource;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
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
     * */
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
     * */
    @Override
    @SuppressWarnings("resource")
    public StreamResource parseExport(List<TransactionModel> list) {
        Path filePath = createCSVFile(list);
        return new StreamResource(filePath.getFileName().toString(), () -> {
            try {
                InputStream input = Files.newInputStream(filePath);

                return new InputStream() {
                    @Override
                    public int read() throws IOException {
                        return input.read();
                    }

                    @Override
                    public void close() throws IOException {
                        try {
                            input.close();
                        } finally {
                            Files.deleteIfExists(filePath);
                        }
                    }
                };
            } catch (IOException e) {
                throw new RuntimeException("Unable to process temporary file for download", e);
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
