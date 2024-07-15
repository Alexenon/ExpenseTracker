package com.example.application.views.pages;

import com.example.application.data.models.Asset;
import com.example.application.data.models.InstrumentsProvider;
import com.example.application.utils.responses.AssetData;
import com.example.application.views.components.PriceMonitorContainer;
import com.example.application.views.layouts.MainLayout;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

// TODO: Think about Holdings + Transactions

@AnonymousAllowed
@PageTitle("Asset Details")
@Route(value = "details", layout = MainLayout.class)
public class AssetDetailsView extends Main implements HasUrlParameter<String> {

    private final TextArea commentTextArea = new TextArea();
    private final PriceMonitorContainer priceMonitorContainer = new PriceMonitorContainer();
    private final Checkbox markAsFavoriteCheckbox = new Checkbox();
    private Asset asset;
    private AssetData assetData;

//    public AssetDetailsView(Asset asset) {
//        this.asset = asset;
//        buildPage();
//    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, String assetName) {
        this.asset = InstrumentsProvider.getInstance().getAssets()
                .stream().filter(a -> a.getSymbol().equalsIgnoreCase(assetName))
                .findFirst().orElseThrow();
        this.assetData = asset.getAssetData();


        System.out.println(assetData);


        buildPage();
        getElement().executeJs("window.scrollTo(0,0)"); // Scroll to top of the page, on initialization
    }

    private void buildPage() {
        getStyle().set("margin-top", "100px");
        H2 h2 = new H2(asset.getAssetData().getName());
        Paragraph description = new Paragraph(assetData.getAssetDescription());
        Image image = new Image(assetData.getLogoUrl(), assetData.getName());
        add(image, h2, description, priceMonitorContainer);
    }

    private Div summaryInvestitionBlock() {
        Div div = new Div();
        Paragraph totalInvestedParagraph = new Paragraph();
        Paragraph dollarProfitParagraph = new Paragraph();
        Paragraph percentageProfitParagraph = new Paragraph();
        return div;
    }

    private Div holdingsBlock() {
        return new Div();
    }


}

