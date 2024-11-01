package com.example.application.views.pages.crypto.comparator;

import com.example.application.data.models.NumberType;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.utils.common.MathUtils;
import com.example.application.utils.investment.EarnCalculator;
import com.example.application.views.components.native_components.Container;
import com.example.application.views.layouts.MainLayout;
import com.example.application.views.pages.crypto.comparator.form.ProfitAssetForm;
import com.example.application.views.pages.crypto.comparator.form.ProfitStakingForm;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Pre;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.TabSheetVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

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

    private void buildPage() {
        getStyle().set("margin", "100px 0 50px 0");

        TabSheet tabSheet = new TabSheet();
        tabSheet.add("Profit Calculator", getProfitCalculatorTab());
        tabSheet.add("Staking Calculator", getStakingCalculatorTab());
        tabSheet.add("Compare Assets", getComparationTab());
        tabSheet.addThemeVariants(TabSheetVariant.LUMO_TABS_CENTERED);

        add(tabSheet);
    }

    private Tab getProfitCalculatorTab() {
        Tab tab = new Tab();
        tab.addClassName("compare-tab-content");

        H3 title = new H3("Profit calculator");
        ProfitAssetForm assetForm = new ProfitAssetForm(instrumentsFacadeService);
        Button calculateBtn = new Button("Calculate");
        Pre output = new Pre();

        calculateBtn.addClickListener(e -> {
            double profit = MathUtils.profit(assetForm.getBuyPrice(), assetForm.getSellPrice(), assetForm.getTotalPrice());
            double profitPercentage = MathUtils.profitPercentage(assetForm.getBuyPrice(), assetForm.getSellPrice());

            String text = """
                    Invested in %s $%.0f
                    Buy Price: %s
                    Sell Price: %s
                    Profit: %s ~ %.1f%%
                    """.formatted(assetForm.getSelectedAssetSymbol(), assetForm.getTotalPrice(),
                    NumberType.PRICE.parse(assetForm.getBuyPrice()),
                    NumberType.PRICE.parse(assetForm.getSellPrice()),
                    NumberType.PRICE.parse(profit), profitPercentage
            );

            output.setText(text);
        });


        assetForm.addClassName("asset-compare-form");

        tab.add(title, assetForm, calculateBtn, output);
        return tab;
    }

    private Tab getComparationTab() {
        Tab tab = new Tab();
        tab.addClassName("compare-tab-content");

        H3 title = new H3("Compare assets");
        ProfitAssetForm assetForm1 = new ProfitAssetForm(instrumentsFacadeService);
        ProfitAssetForm assetForm2 = new ProfitAssetForm(instrumentsFacadeService);
        Container comparationBody = new Container("comparation-body", assetForm1, assetForm2);
        Button compareBtn = new Button("Compare");
        Div output = new Div();

        assetForm1.addClassName("asset-compare-form");
        assetForm2.addClassName("asset-compare-form");

        tab.add(title, comparationBody, compareBtn, output);
        return tab;
    }

    private Tab getStakingCalculatorTab() {
        Tab tab = new Tab();
        tab.addClassName("compare-tab-content");

        H3 title = new H3("Staking calculator");
        ProfitStakingForm profitStakingForm = new ProfitStakingForm(instrumentsFacadeService);
        Container comparationBody = new Container("comparation-body", profitStakingForm);
        Button calculateBtn = new Button("Calculate");
        Pre output = new Pre();

        calculateBtn.addClickListener(e -> {
            double apr = profitStakingForm.getApr();
            double stakingAmount = profitStakingForm.getStakingWorth();

            String text = """
                    | Stacking amount - $%.2f, APR - %.2f%%
                    |-------------------------------------------|
                    | Daily - $%.2f
                    | Weekly - $%.2f
                    | Monthly - $%.2f
                    | Yearly - $%.2f
                    |-------------------------------------------|
                    """.formatted(stakingAmount, apr,
                    EarnCalculator.earnDaily(stakingAmount, apr),
                    EarnCalculator.earnWeekly(stakingAmount, apr),
                    EarnCalculator.earnMonthly(stakingAmount, apr),
                    EarnCalculator.earnYearly(stakingAmount, apr));
            output.setText(text);
        });

        profitStakingForm.addClassName("asset-compare-form");

        tab.add(title, comparationBody, calculateBtn, output);
        return tab;
    }

}
