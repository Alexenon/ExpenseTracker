package com.example.application.views.components.custom.dialogs.transactions.export;

import com.example.application.data.dtos.migration.TransactionModel;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.utils.common.parsers.CSVParser;
import com.example.application.views.components.core.buttons.DownloadButton;
import com.example.application.views.components.utils.HasNotifications;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.server.StreamResource;

import java.util.List;

public class ExportTransactionDialog extends Dialog implements HasNotifications {

    private final InstrumentsFacadeService instrumentsFacadeService;

    public ExportTransactionDialog(InstrumentsFacadeService instrumentsFacadeService) {
        this.instrumentsFacadeService = instrumentsFacadeService;
        initialize();
    }

    private void initialize() {
        this.setHeaderTitle("Export Transactions");
        this.getFooter().add(new Button("Close", e -> this.close()));

        List<TransactionModel> mappedTransactions = instrumentsFacadeService.getAllTransactions()
                .stream()
                .map(TransactionModel::from)
                .toList();

        add(
                downloadButton(mappedTransactions)
        );
    }

    // TODO: The file should be downloaded when user click downloadButton
    //  Display there the error, why the file wasn't able to be downloaded
    private DownloadButton downloadButton(List<TransactionModel> transactions) {
        CSVParser csvParser = new CSVParser();
        StreamResource resource = csvParser.parseExport(transactions);
        return new DownloadButton(resource);
    }


}
