package com.example.application.views.pages;

import com.example.application.data.models.Asset;
import com.example.application.data.models.CurrencyProvider;
import com.example.application.services.AssetWatcherService;
import com.example.application.services.SecurityService;
import com.example.application.services.UserService;
import com.example.application.views.components.SearchFilterGrid;
import com.example.application.views.components.complex_components.dialogs.AddAssetDialog;
import com.example.application.views.layouts.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@AnonymousAllowed
@PageTitle("Assets Dashboard")
@Route(value = "assets-dashboard", layout = MainLayout.class)
public class AssetsDashboardView extends Main {

    private final AssetWatcherService assetWatcherService;
    private final SecurityService securityService;
    private final UserService userService;
    private final CurrencyProvider currencyProvider;

    private final List<Asset> assets = new ArrayList<>();
    private final Grid<Asset> grid = new Grid<>(Asset.class);

    @Autowired
    public AssetsDashboardView(AssetWatcherService assetWatcherService,
                               SecurityService securityService,
                               UserService userService) {
        this.assetWatcherService = assetWatcherService;
        this.securityService = securityService;
        this.userService = userService;
        currencyProvider = CurrencyProvider.getInstance();

        getStyle().set("margin-top", "100px");

        Button addBtn = new Button("Add");
        addBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addBtn.addClickListener(event -> {
            AddAssetDialog dialog = new AddAssetDialog(assetWatcherService, securityService, userService);
            dialog.open();
            dialog.addClickSaveBtnListener(grid -> {
                assets.add(dialog.getAsset());
                // Update dataGridView
            });
        });

        GridListDataView<Asset> dataView = grid.setItems(assets);

        TextField searchField = new TextField();
        searchField.setWidth("50%");
        searchField.setPlaceholder("Search");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> {
            searchField.setValue(e.getValue().toUpperCase()); // TODO: Make uppercase field more intuitive
            dataView.refreshAll();
        });

        /*
            // TODO: [Show / Hide] columns
            // TODO: Better display first columns
            _________________________________________________________________________________
            | Name | Price | 24h changes | Amount  | Avg buy | All-time low | All-time high |
            | BTC  | 64000 | 2%          | 0.00034 | 60000   | 10           | 73000         |
            _________________________________________________________________________________
        */

        grid.removeAllColumns();
        final Grid.Column<Asset> assetColumn = grid.addColumn(a -> a.getCurrency().getName());
        assetColumn.setHeader("Name").setSortable(true);
        grid.addColumn(a -> a.getCurrency().getCurrentPrice().toPlainString()).setHeader("Price").setSortable(true);
        grid.addColumn(Asset::getTotalAmount).setHeader("Amount").setSortable(true);
        grid.addColumn(Asset::getAveragePrice).setHeader("Avg Buy").setSortable(true);
        grid.addColumn(a -> a.getCurrency().getChangesLast24Hours().toPlainString()).setHeader("24h Changes").setSortable(true);
        grid.getColumns().forEach(c -> c.setAutoWidth(true));

        dataView.setFilter(a -> {
            String searchTerm = searchField.getValue().trim();

            if (searchTerm.isEmpty())
                return true;

            return a.getCurrency().getName().contains(searchTerm);
        });

        grid.addItemClickListener(System.out::println);

        initializeAssets();
        add(addBtn, searchField, grid);
    }

    private void initializeAssets() {
        assets.addAll(currencyProvider.getCurrencyList().stream().map(Asset::new).toList());
    }



}
