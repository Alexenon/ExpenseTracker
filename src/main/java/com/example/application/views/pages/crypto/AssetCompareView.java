package com.example.application.views.pages.crypto;

import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.views.layouts.MainLayout;
import com.example.application.views.pages.crypto.comparator.AssetPriceAmountForm;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

@PermitAll
@PageTitle("Asset Details")
@Route(value = "details", layout = MainLayout.class)
public class AssetCompareView extends Main {

    private final InstrumentsFacadeService instrumentsFacadeService;
    private final AssetPriceAmountForm assetForm1;
    private final AssetPriceAmountForm assetForm2;
    private final Button compareBtn = new Button("Compare");

    @Autowired
    public AssetCompareView(InstrumentsFacadeService instrumentsFacadeService) {
        this.instrumentsFacadeService = instrumentsFacadeService;
        this.assetForm1 = new AssetPriceAmountForm(instrumentsFacadeService);
        this.assetForm2 = new AssetPriceAmountForm(instrumentsFacadeService);
        buildPage();
    }

    private void buildPage() {
        //addClassName("page-content");
        getStyle().set("margin", "100 30");
        add(assetForm1, assetForm2, compareBtn);
    }


}
