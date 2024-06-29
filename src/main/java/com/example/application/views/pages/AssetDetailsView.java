package com.example.application.views.pages;

import com.example.application.data.models.Asset;
import com.example.application.data.models.CurrencyProvider;
import com.example.application.views.components.PriceMonitorContainer;
import com.example.application.views.layouts.MainLayout;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Paragraph;
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

//    public AssetDetailsView(Asset asset) {
//        this.asset = asset;
//        buildPage();
//    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, String assetName) {
        this.asset = CurrencyProvider.getInstance().getCurrencyList()
                .stream().map(Asset::new).toList()
                .stream().filter(a -> a.getCurrency().getName().equalsIgnoreCase(assetName))
                .findFirst().orElse(null);

        buildPage();
        getElement().executeJs("window.scrollTo(0,0)"); // Scroll to top of the page, on initialization
    }

    private void buildPage() {
        getStyle().set("margin-top", "100px");
        H2 h2 = new H2(asset.getCurrency().getName());
        add(h2, priceMonitorContainer);
    }

    private Div summaryBlock() {
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
