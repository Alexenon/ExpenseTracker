package com.example.application.views.components.custom.dialogs.transactions.export;

import com.example.application.data.models.crypto.migration.TransactionModel;
import com.example.application.entities.common.TransactionType;
import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.Portfolio;
import com.example.application.entities.crypto.Transaction;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.utils.common.formatters.CommonFormatters;
import com.example.application.utils.common.parsers.CSVParser;
import com.example.application.views.components.core.Container;
import com.example.application.views.components.custom.icons.PictogramIcon;
import com.example.application.views.components.utils.HasNotifications;
import com.example.application.views.components.utils.common.GridUtils;
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

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/*
    TODO [LONG TERM]:
        [?] What if I add same transaction twice, it should at least warn user

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

	private final CSVParser csvParser = new CSVParser();
	private final FileBuffer buffer = new FileBuffer();
	private final Upload upload = new Upload(buffer);

	private final InstrumentsFacadeService instrumentsFacadeService;
	private final Grid<TransactionModel> grid = new Grid<>();

	private final Paragraph errorField = new Paragraph();
	private final Button saveButton = new Button("Save");
	private final Button cancelButton = new Button("Cancel", e -> this.close());
	private final Button closeBtn = new Button(LumoIcon.CROSS.create(), e -> this.close());

	private final Portfolio portfolio;
	private Map<TransactionModel, Transaction> mappedTransactions = new HashMap<>();

	public ImportTransactionsDialog(Portfolio portfolio, InstrumentsFacadeService instrumentsFacadeService) {
		this.portfolio = portfolio;
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
												   "File type: <b>CSV</b> (.csv)");
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
		upload.setAcceptedFileTypes(".csv");
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
			errorField.setVisible(false);
		});

		upload.addFailedListener(e -> {
			errorField.setVisible(true);
			errorField.setText("Unable to process file, cause: " + e.getReason().getLocalizedMessage());
		});

		upload.addFileRejectedListener(e -> errorField.setText("Too many files added, or the file size is bigger than 10MB"));

		errorField.setVisible(false);
		errorField.getElement().getStyle().set("color", "red");
	}

	private void initializeGrid() {
		grid.getElement().getStyle().set("min-width", "50vw");
		grid.setVisible(false);
		grid.setColumnReorderingAllowed(true);

		grid.addColumn(new LocalDateTimeRenderer<>(TransactionModel::getDateTime, CommonFormatters.DATE_FRIENDLY_FORMAT))
				.setHeader("Date & Time")
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
		saveButton.addClickShortcut(Key.ENTER);
		saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
		saveButton.addClickListener(e -> {
			List<Transaction> transactions = mappedTransactions.values()
					.stream()
					.toList();
			instrumentsFacadeService.saveTransactions(transactions);
		});
		getFooter().add(saveButton, cancelButton);
	}

	private void displayGrid() {
		try {
			mappedTransactions = getParsedTransactions()
					.stream()
					.collect(Collectors.toMap(Function.identity(), this::mapped));

			grid.setItems(mappedTransactions.keySet());
			grid.setVisible(true);
		} catch (Exception e) {
			errorField.setVisible(true);
			errorField.setText(e.getLocalizedMessage());
			throw new RuntimeException(e);
		}
	}

	private List<TransactionModel> getParsedTransactions() {
		return csvParser.parseImport(buffer.getInputStream());
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
		return new ComponentRenderer<>(Button::new, (editBtn, oldTransaction) -> {
			editBtn.setIcon(PictogramIcon.SQUARE_EDIT_OUTLINE.create("grid-action-btn"));
			editBtn.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY);
			editBtn.addClickListener(e -> {
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
		return new ComponentRenderer<>(Button::new, (deleteBtn, model) -> {
			deleteBtn.setIcon(PictogramIcon.DELETE_OUTLINE.create("grid-action-btn"));
			deleteBtn.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
			deleteBtn.addClickListener(e -> {
				mappedTransactions.remove(model);
				grid.getDataProvider().refreshAll();
			});
		});
	}

	private Transaction mapped(TransactionModel model) {
		Transaction transaction = new Transaction();
		Asset assetBySymbol = instrumentsFacadeService.getAssetBySymbol(model.getSymbol())
				.orElseThrow();
		transaction.setAsset(assetBySymbol);
		transaction.setPortfolio(portfolio);
		transaction.setOrderQuantity(model.getAmount());
		transaction.setMarketPrice(model.getPrice());
		transaction.setType(model.getType());
		transaction.setDateTime(model.getDateTime());
		transaction.setNote(model.getNote());
		return transaction;
	}

}
