package com.example.application.views.components.custom.dialogs.transactions.export;

import com.example.application.data.dtos.migration.TransactionModel;
import com.example.application.entities.common.TransactionType;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.utils.common.formatters.CommonFormatters;
import com.example.application.views.components.core.Container;
import com.example.application.views.components.custom.icons.PictogramIcon;
import com.example.application.views.components.utils.HasNotifications;
import com.example.application.views.components.utils.common.GridUtils;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.dom.DomEventListener;
import com.vaadin.flow.theme.lumo.LumoIcon;
import org.springframework.context.annotation.Lazy;

import java.io.IOException;
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

public class ImportTransactionsDialog extends Dialog implements HasNotifications {

    private static final int MAX_NUMBER_OF_FILES = 1;
    private static final int MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB

    // TODO:
    //  [?] Add option to replace existing transactions

    private final FileBuffer buffer = new FileBuffer();
    private final Upload upload = new Upload(buffer);

    private final InstrumentsFacadeService instrumentsFacadeService;
    private final Grid<TransactionModel> grid = new Grid<>();

    private final Paragraph errorField = new Paragraph();
    private final Button saveButton = new Button("Save");
    private final Button cancelButton = new Button("Cancel", e -> this.close());
    private final Button closeBtn = new Button(LumoIcon.CROSS.create(), e -> this.close());

    private List<TransactionModel> transactions = new ArrayList<>();

    public ImportTransactionsDialog(@Lazy InstrumentsFacadeService instrumentsFacadeService) {
        this.instrumentsFacadeService = instrumentsFacadeService;
        initialize();
    }

    private void initialize() {
        addClassName("import-dialog");
        initiliazeHeader();
        initializeUploader();
        initializeGrid();
        initializeFooter();

        add(
                createDialogHeader(),
                upload,
                errorField,
                grid
        );
    }

    private Div createDialogHeader() {
        Span hint = new Span();
        hint.getElement().setProperty("innerHTML", "Maximum <b>1</b> file allowed with max size: <b>10 MB</b>.<br>" +
                                                   "Accepted files: <b>CSV</b> (.csv), <b>Excel</b> (.xls, .xlsx)");
        return new Container("import-dialog-header", hint);
    }

    private void initiliazeHeader() {
        setHeaderTitle("Import transactions");
        closeBtn.addClickShortcut(Key.ESCAPE);
        closeBtn.addClassName("modal-close-btn");
        getHeader().add(closeBtn);
    }

    private void initializeUploader() {
        upload.addClassName("uploader");
        upload.setDropAllowed(true);
        upload.setMaxFileSize(MAX_FILE_SIZE);
        upload.setMaxFiles(MAX_NUMBER_OF_FILES);
        upload.setAcceptedFileTypes(".csv", ".xls", ".xlsx");
        upload.addSucceededListener(e -> {
            displayGrid();
            saveButton.setVisible(true);
        });

        Button uploadButton = new Button("Upload");
        uploadButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        upload.setUploadButton(uploadButton);

        upload.getElement().addEventListener("file-remove", (DomEventListener) arg -> {
            grid.setItems(new ArrayList<>());
            grid.setVisible(false);
        });

        upload.addFileRejectedListener(e -> errorField.setText("Too many files added, or the file size is bigger than 10MB"));
    }

    private void initializeGrid() {
        grid.getElement().getStyle().set("min-width", "50vw");
        grid.setVisible(false);
        grid.setColumnReorderingAllowed(true);

        grid.addColumn(new LocalDateTimeRenderer<>(TransactionModel::getDateTime, CommonFormatters.DATE_FRIENDLY_FORMAT))
                .setHeader("Date Time")
                .setAutoWidth(true);

        grid.addColumn(TransactionModel::getSymbol)
                .setHeader("Symbol")
                .setAutoWidth(true);

        grid.addColumn(GridUtils.columnAmountRenderer(TransactionModel::getAmount))
                .setHeader("Amount")
                .setAutoWidth(true);

        grid.addColumn(GridUtils.columnPriceRenderer(TransactionModel::getPrice))
                .setHeader("Price")
                .setAutoWidth(true);

        grid.addColumn(typeComponentRenderer())
                .setHeader("Status")
                .setAutoWidth(true);

        grid.addColumn(columnEditRenderer())
                .setHeader("Edit")
                .setAutoWidth(true);

        grid.addColumn(columnDeleteRenderer())
                .setHeader("Delete")
                .setAutoWidth(true);

        grid.getDataCommunicator().setInMemorySorting(
                (a, b) -> Comparator.comparing(TransactionModel::getDateTime).reversed()
                        .thenComparing(TransactionModel::getSymbol)
                        .thenComparing(TransactionModel::getPrice, Comparator.reverseOrder())
                        .compare(a, b)
        );
    }

    private void initializeFooter() {
        saveButton.setVisible(false);
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        saveButton.addClickListener(e -> {
        });
        getFooter().add(saveButton, cancelButton);
    }

    private void displayGrid() {
        try {
            transactions = new ArrayList<>(parsedTransactions());
            grid.setItems(transactions);
            grid.setVisible(true);
        } catch (IOException e) {
            errorField.setText(e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
    }

    private List<TransactionModel> parsedTransactions() throws IOException {
        CsvMapper mapper = CsvMapper.builder()
                .addModule(new JavaTimeModule())
                .enable(CsvParser.Feature.SKIP_EMPTY_LINES)
                .enable(CsvParser.Feature.EMPTY_STRING_AS_NULL)
                .enable(CsvParser.Feature.FAIL_ON_MISSING_HEADER_COLUMNS)
                .defaultDateFormat(new SimpleDateFormat("M.d.yyyy HH:mm:ss"))
                .build();

        CsvSchema schema = CsvSchema.emptySchema().withHeader();

        return mapper.readerFor(TransactionModel.class)
                .with(schema)
                .<TransactionModel>readValues(buffer.getInputStream())
                .readAll()
                .stream()
                .filter(TransactionModel::isValid)
                .toList();
    }

    private static ComponentRenderer<Span, TransactionModel> typeComponentRenderer() {
        return new ComponentRenderer<>(Span::new, (span, model) -> {
            TransactionType type = Objects.requireNonNull(model.getType(), "transaction type");
            String theme = String.format("badge %s", type.isBuyTransaction() ? "success" : "error");
            span.getElement().setAttribute("theme", theme);
            span.setText(type.name());
        });
    }

    private ComponentRenderer<Button, TransactionModel> columnEditRenderer() {
        return new ComponentRenderer<>(Button::new, (button, oldTransaction) -> {
            button.setIcon(PictogramIcon.SQUARE_EDIT_OUTLINE.create("grid-action-btn"));
            button.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY);
            button.addClickListener(e -> {
                EditTransactionModelDialog editDialog = new EditTransactionModelDialog(oldTransaction, instrumentsFacadeService);
                editDialog.open();
                editDialog.addSaveListener(d -> {
                    TransactionModel updatedTransaction = editDialog.getTransaction();

                    oldTransaction.setDateTime(updatedTransaction.getDateTime());
                    oldTransaction.setSymbol(updatedTransaction.getSymbol());
                    oldTransaction.setAmount(updatedTransaction.getAmount());
                    oldTransaction.setPrice(updatedTransaction.getPrice());
                    oldTransaction.setType(updatedTransaction.getType());
                    oldTransaction.setNote(updatedTransaction.getNote());

                    grid.getDataProvider().refreshItem(oldTransaction);
                });
            });
        });
    }

    private ComponentRenderer<Button, TransactionModel> columnDeleteRenderer() {
        return new ComponentRenderer<>(Button::new, (button, model) -> {
            button.setIcon(PictogramIcon.DELETE_OUTLINE.create("grid-action-btn"));
            button.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
            button.addClickListener(e -> {
                transactions.remove(model);
                grid.getDataProvider().refreshAll();
            });
        });
    }

}
