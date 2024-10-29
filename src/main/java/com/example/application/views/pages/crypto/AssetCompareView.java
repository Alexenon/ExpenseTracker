package com.example.application.views.pages.crypto;

import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.views.components.native_components.Container;
import com.example.application.views.layouts.MainLayout;
import com.example.application.views.pages.crypto.comparator.AssetPriceAmountForm;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.TabSheetVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/*

DESIGNS:
    - https://dribbble.com/shots/14697418-Mortgage-calculator-ui-design
    - https://dribbble.com/shots/23957181-WalletHub-Dashboard-Buy-Rent-Calculator
    - https://dribbble.com/shots/12001805-Public-24-Deposit-calculator-in-online-banking

* */
@PermitAll
@PageTitle("Asset Details")
@Route(value = "details", layout = MainLayout.class)
public class AssetCompareView extends Main {

    private final InstrumentsFacadeService instrumentsFacadeService;

    @Autowired
    public AssetCompareView(InstrumentsFacadeService instrumentsFacadeService) {
        this.instrumentsFacadeService = instrumentsFacadeService;
        buildPage();
    }

    public static void main(String[] args) {
        LocalDate currentDate = LocalDate.now();
        LocalDate maxAllowedDate = currentDate.plus(1, ChronoUnit.MONTHS);
        System.out.println(maxAllowedDate);
    }

    private void buildPage() {
        getStyle().set("margin", "150px 50px");

        TabSheet tabSheet = new TabSheet();
        tabSheet.add("Profit Calculator", getProfitCalculatorTab());
        tabSheet.add("Compare Assets", getComparationTab());
        tabSheet.addThemeVariants(TabSheetVariant.LUMO_TABS_CENTERED);

        add(tabSheet);
    }

    private Tab getProfitCalculatorTab() {
        Tab tab = new Tab();
        tab.addClassName("compare-tab-content");

        H3 title = new H3("Profit Calculator");
        AssetPriceAmountForm assetForm = new AssetPriceAmountForm(instrumentsFacadeService);
        Button calculateBtn = new Button("Calculate");
        Div output = new Div();

        assetForm.addClassName("asset-compare-form");

        tab.add(title, assetForm, calculateBtn, output);
        return tab;
    }

    private Tab getComparationTab() {
        Tab tab = new Tab();
        tab.addClassName("compare-tab-content");

        H3 title = new H3("Assets Profit Comparator");
        AssetPriceAmountForm assetForm1 = new AssetPriceAmountForm(instrumentsFacadeService);
        AssetPriceAmountForm assetForm2 = new AssetPriceAmountForm(instrumentsFacadeService);
        Container comparationBody = new Container("comparation-body", assetForm1, assetForm2);
        Button compareBtn = new Button("Compare");
        Div output = new Div();

        assetForm1.addClassName("asset-compare-form");
        assetForm2.addClassName("asset-compare-form");

        tab.add(title, comparationBody, compareBtn, output);
        return tab;
    }

    private void test() {
        List<Integer> r = List.of(1, 2, 3).stream()
                .filter(o -> {
                    LocalDate expireDate = LocalDate.now();
                    LocalDate currentDate = LocalDate.now();
                    LocalDate maxAllowedDate = currentDate.plus(1, ChronoUnit.MONTHS);

                    return expireDate.isAfter(currentDate) && expireDate.isBefore(maxAllowedDate);
                })
                .toList();
    }

}
