package com.example.application.views.pages;

import com.example.application.data.models.Asset;
import com.example.application.services.AssetWatcherService;
import com.example.application.services.SecurityService;
import com.example.application.services.UserService;
import com.example.application.views.components.complex_components.dialogs.AddHoldingDialog;
import com.example.application.views.layouts.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;


@AnonymousAllowed
@PageTitle("Crypto Holdings")
@Route(value = "holdings", layout = MainLayout.class)
public class HoldingsView extends Main {

    private final AssetWatcherService assetWatcherService;
    private final SecurityService securityService;
    private final UserService userService;

    private final List<Asset> assets = new ArrayList<>();
    private final Grid<Asset> grid = new Grid<>(Asset.class);

    @Autowired
    public HoldingsView(AssetWatcherService assetWatcherService,
                        SecurityService securityService,
                        UserService userService) {
        this.assetWatcherService = assetWatcherService;
        this.securityService = securityService;
        this.userService = userService;

        getStyle().set("margin-top", "100px");

        Button addBtn = new Button("Add");
        addBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addBtn.addClickListener(event -> {
            AddHoldingDialog dialog = new AddHoldingDialog(assetWatcherService, securityService, userService);
            dialog.open();
            dialog.addClickSaveBtnListener(grid -> {
                assets.add(dialog.getAsset());
                updateGrid();
            });
        });

        /*
            _________________________________________________________________________________
            | Name | Price | 24h changes | Amount  | Avg buy | All-time low | All-time high |
            | BTC  | 64000 | 2%          | 0.00034 | 60000   | 10           | 73000         |
            _________________________________________________________________________________
        */

        grid.removeAllColumns();
        grid.addColumn(a -> a.getCurrency().getName()).setHeader("Name");
        grid.addColumn(a -> a.getCurrency().getCurrentPrice().toPlainString()).setHeader("Price");
        grid.addColumn(Asset::getTotalAmount).setHeader("Amount");
        grid.addColumn(Asset::getAveragePrice).setHeader("Avg Buy");
        grid.addColumn(a -> a.getCurrency().getChangesLast24Hours().toPlainString()).setHeader("24h Changes");
        grid.getColumns().forEach(c -> c.setAutoWidth(true));

        updateGrid();
        add(addBtn, grid);
    }

    private void updateGrid() {
        grid.setItems(assets);
    }

}
