package com.example.application.views.pages;

import com.example.application.data.models.Asset;
import com.example.application.views.layouts.MainLayout;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

// TODO: Think about Holdings + Transactions

@AnonymousAllowed
@PageTitle("Asset Details")
@Route(value = "details", layout = MainLayout.class)
public class AssetDetailsView extends Main {

    private final Asset asset;
    private final TextArea commentTextArea = new TextArea();
    private final Div wantedPrices = new Div();
    private final Checkbox markAsFavoriteCheckbox = new Checkbox();


    public AssetDetailsView(Asset asset) {
        this.asset = asset;
        buildPage();
    }

    private void buildPage() {

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
