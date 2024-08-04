package com.example.application.views.pages;

import com.example.application.data.models.InstrumentsProvider;
import com.example.application.services.AssetWatcherService;
import com.example.application.services.SecurityService;
import com.example.application.services.UserService;
import com.example.application.views.components.AssetsGrid;
import com.example.application.views.components.complex_components.dialogs.AddAssetDialog;
import com.example.application.views.layouts.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.beans.factory.annotation.Autowired;

@AnonymousAllowed
@PageTitle("Assets Dashboard")
@Route(value = "assets-dashboard", layout = MainLayout.class)
public class AssetsDashboardView extends Main {

    private final InstrumentsProvider instrumentsProvider;
    private final AssetWatcherService assetWatcherService;
    private final SecurityService securityService;
    private final UserService userService;

    private final AssetsGrid assetsGrid = new AssetsGrid();

    @Autowired
    public AssetsDashboardView(
            InstrumentsProvider instrumentsProvider,
            AssetWatcherService assetWatcherService,
            SecurityService securityService,
            UserService userService) {
        this.instrumentsProvider = instrumentsProvider;
        this.assetWatcherService = assetWatcherService;
        this.securityService = securityService;
        this.userService = userService;

        getStyle().set("margin-top", "100px");

        Button addBtn = new Button("Add");
        addBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addBtn.addClickListener(event -> {
            AddAssetDialog dialog = new AddAssetDialog(assetWatcherService, securityService, userService);
            dialog.open();
            dialog.addClickSaveBtnListener(grid -> {
//                assets.add(dialog.getAsset());
                // Update dataGridView
            });
        });

        updateAssets();
        assetsGrid.setItems(instrumentsProvider.getListOfAssetData());

        add(addBtn, assetsGrid);
    }

    public void updateAssets() {

    }

}
