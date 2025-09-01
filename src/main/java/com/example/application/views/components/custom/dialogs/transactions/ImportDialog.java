package com.example.application.views.components.custom.dialogs.transactions;

import com.example.application.data.dtos.migration.TransactionModel;
import com.example.application.entities.common.TransactionType;
import com.example.application.utils.common.formatters.CommonFormatters;
import com.example.application.views.components.utils.HasNotifications;
import com.example.application.views.components.utils.common.GridUtils;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.dom.DomEventListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;


/*
    Helper displays info while file is beeing added
        https://vaadin.com/directory/component/upload-helper-add-on

    Exports
        https://vaadin.com/directory/component/grid-exporter-add-on
        https://vaadin.com/directory/component/gridexporter-for-vaadin

    Others
        https://addons.dokku1.parttio.org/paginggrid
* */
public class ImportDialog extends Dialog implements HasNotifications {

    private static final int MAX_NUMBER_OF_FILES = 1;
    private static final int MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB

    // TODO:
    //  [?] Add option to replace existing transactions
    //  [!] Add a error message paragraph in the dialog itself, instead of quick notification

    private final FileBuffer buffer = new FileBuffer();
    private final Upload upload = new Upload(buffer);

    private final Grid<TransactionModel> grid = new Grid<>();
    private final Paragraph errorField = new Paragraph();
    private final Button saveButton = new Button("Save");
    private final Button cancelButton = new Button("Cancel");

    public ImportDialog() {
        initialize();
    }

    private void initialize() {
        initializeUploader();
        initializeGrid();
        initializeFooter();

        H4 title = new H4("Upload spreadsheet");
        Paragraph hint = new Paragraph("Maximum 1 file allowed with max size: 10 MB. Only Excel and CSV files are accepted.");

        add(
                title,
                hint,
                upload,
                errorField,
                grid
        );
    }

    private void initializeUploader() {
        upload.setDropAllowed(true);
        upload.setMaxFileSize(MAX_FILE_SIZE);
        upload.setMaxFiles(MAX_NUMBER_OF_FILES);
        upload.setAcceptedFileTypes(".csv", ".xls", ".xlsx");
        upload.addSucceededListener(e -> {
            displayGrid();
            saveButton.setVisible(true);
        });

        upload.getElement().addEventListener("file-remove", (DomEventListener) arg -> {
            grid.setVisible(false);
            grid.setItems(new ArrayList<>());
        });

        upload.addFileRejectedListener(e -> errorField.setText("Too many files added, or the file size is bigger than 10MB"));
    }

    private void initializeGrid() {
        grid.setVisible(false);
        grid.setColumnReorderingAllowed(true);

        grid.addColumn(new LocalDateTimeRenderer<>(TransactionModel::dateTime, CommonFormatters.DATE_FRIENDLY_FORMAT))
                .setHeader("Date Time")
                .setAutoWidth(true);

        grid.addColumn(TransactionModel::symbol)
                .setHeader("Symbol")
                .setAutoWidth(true);

        grid.addColumn(GridUtils.columnAmountRenderer(TransactionModel::price))
                .setHeader("Amount")
                .setAutoWidth(true);

        grid.addColumn(GridUtils.columnPriceRenderer(TransactionModel::price))
                .setHeader("Price")
                .setAutoWidth(true);

        grid.addColumn(typeComponentRenderer())
                .setHeader("Status")
                .setAutoWidth(true);
    }

    private void initializeFooter() {
        saveButton.setVisible(false);
        saveButton.addClickListener(e -> {
        });
        cancelButton.addClickListener(e -> this.close());
        getFooter().add(saveButton, cancelButton);
    }

    private void displayGrid() {
        grid.setVisible(true);
        try {
            grid.setItems(parseTransactions(buffer.getInputStream()));
        } catch (IOException e) {
            errorField.setText(e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
    }

    private static List<TransactionModel> parseTransactions(InputStream inputStream) throws IOException {
        CsvMapper mapper = CsvMapper.builder()
                .addModule(new JavaTimeModule())
                .enable(CsvParser.Feature.SKIP_EMPTY_LINES)
                .enable(CsvParser.Feature.EMPTY_STRING_AS_NULL)
                .defaultDateFormat(new SimpleDateFormat("M.d.yyyy HH:mm:ss"))
//                .withCoercionConfig(LogicalType.Enum, cfg -> cfg.setCoercion(CoercionInputShape.EmptyString, CoercionAction.AsNull))
                .build();

        CsvSchema schema = CsvSchema.emptySchema().withHeader();

        return mapper.readerFor(TransactionModel.class)
                .with(schema)
                .<TransactionModel>readValues(inputStream)
                .readAll()
                .stream()
                .filter(ImportDialog::isTransactionModelValid)
                .sorted(Comparator.comparing(TransactionModel::dateTime).reversed())
                .toList();
    }

    private static boolean isTransactionModelValid(TransactionModel model) {
        return model.dateTime() != null
               || (model.symbol() != null && !model.symbol().isBlank())
               || (model.amount() != null && !model.amount().isNaN() && !model.amount().isInfinite())
               || (model.price() != null && !model.price().isNaN() && !model.price().isInfinite())
               || model.type() != null
               || (model.note() != null && !model.note().isBlank());
    }

    private static ComponentRenderer<Span, TransactionModel> typeComponentRenderer() {
        return new ComponentRenderer<>(Span::new, (span, model) -> {
            TransactionType type = Objects.requireNonNull(model.type(), "transaction type");
            String theme = String.format("badge %s", type.isBuyTransaction() ? "success" : "error");
            span.getElement().setAttribute("theme", theme);
            span.setText(type.name());
        });
    }

    public static void main(String[] args) {
        File file = new File("C:\\Users\\asus\\Desktop\\Orders.csv");
        try (InputStream inputStream = new FileInputStream(file)) {
            List<TransactionModel> items = parseTransactions(inputStream);
            System.out.println(items);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
