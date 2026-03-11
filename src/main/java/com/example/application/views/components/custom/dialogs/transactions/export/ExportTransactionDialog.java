package com.example.application.views.components.custom.dialogs.transactions.export;

import com.example.application.data.dtos.migration.TransactionModel;
import com.example.application.entities.crypto.Portfolio;
import com.example.application.entities.crypto.Transaction;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.utils.common.parsers.CSVParser;
import com.example.application.views.components.core.Container;
import com.example.application.views.components.core.buttons.Download;
import com.example.application.views.components.utils.HasNotifications;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;

import java.time.LocalDate;
import java.util.List;

public class ExportTransactionDialog extends Dialog implements HasNotifications {

    private static final CSVParser CSV_PARSER = new CSVParser();

    private final InstrumentsFacadeService instrumentsFacadeService;
    private final Select<String> optionsSelector = new Select<>();
    private final DatePicker from = new DatePicker("From");
    private final DatePicker to = new DatePicker("To");
    private final Download download = new Download();

    private final Portfolio portfolio;
    private List<Transaction> transactions;

    public ExportTransactionDialog(Portfolio portfolio, InstrumentsFacadeService instrumentsFacadeService) {
        this.portfolio = portfolio;
        this.instrumentsFacadeService = instrumentsFacadeService;
        initialize();
    }

    private void initialize() {
        this.setHeaderTitle("Export Transactions");
        this.getFooter().add(new Button("Close", e -> this.close()));
        download.getButton().addClassName("add-entity-btn");

        optionsSelector.setLabel("Period");
        optionsSelector.setItems("Last month", "Last 3 months", "Last 6 months", "Last year", "All", "Custom");
        optionsSelector.setValue("Last month");

        LocalDate currentDate = LocalDate.now();
        updateDownloadedItems(currentDate.minusMonths(1));
        optionsSelector.addValueChangeListener(e -> {
            String value = e.getValue();
            from.setVisible(value.equals("Custom"));
            to.setVisible(value.equals("Custom"));

            switch (value) {
                case "All" -> updateDownloadedItems(instrumentsFacadeService.getTransactions(portfolio));
                case "Last month" -> updateDownloadedItems(currentDate.minusMonths(1));
                case "Last 3 months" -> updateDownloadedItems(currentDate.minusMonths(3));
                case "Last 6 months" -> updateDownloadedItems(currentDate.minusMonths(6));
                case "Last year" -> updateDownloadedItems(currentDate.minusYears(1));
                case "Custom" -> updateDownloadedItems(from.getValue(), to.getValue());
            }
        });

        from.setVisible(false);
        from.setValue(currentDate.minusMonths(3));
        from.addValueChangeListener(e -> updateDownloadedItems(from.getValue(), to.getValue()));
        to.setVisible(false);
        to.setValue(currentDate);
        to.addValueChangeListener(e -> updateDownloadedItems(from.getValue(), to.getValue()));

        Div contentBody = Container.builder("export-dialog-body")
                .addComponents(
                        new Paragraph("Transactions within the selected time range can be exported in CSV format"),
                        optionsSelector,
                        new HorizontalLayout(from, to),
                        download
                ).build();

        add(contentBody);
    }

    public void updateDownloadedItems(LocalDate from) {
        updateDownloadedItems(from, LocalDate.now());
    }

    public void updateDownloadedItems(LocalDate from, LocalDate to) {
        updateDownloadedItems(instrumentsFacadeService.getTransactions(portfolio, from, to));
    }

    public void updateDownloadedItems(List<Transaction> transactions) {
        List<TransactionModel> mappedTransactions = transactions.stream()
                .map(TransactionModel::from)
                .toList();

        download.setResource(() -> CSV_PARSER.parseExport(mappedTransactions));
    }

}
