package com.example.application.views.pages;

import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.views.components.custom.dialogs.transactions.AddTransactionDialog;
import com.example.application.views.components.custom.fields.stats.dropdown.DropdownStats;
import com.example.application.views.components.utils.HasScroll;
import com.example.application.views.layouts.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoIcon;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@AnonymousAllowed
@PageTitle("Home")
@Route(value = "", layout = MainLayout.class)
public class HomeView extends Main implements HasScroll {

    @Autowired
    private InstrumentsFacadeService service;

    public HomeView() {
        addClassName("page-content");
        Paragraph text = new Paragraph("Welcome to Home page!");
        text.addClassNames(LumoUtility.FontSize.XLARGE);
        add(text);


        Div stats = new Div(new H3("Realized Profit"), new Paragraph("This is the realized profit: $234.39"));
        DropdownStats dropdownStats = new DropdownStats("Realized", stats);
        add(dropdownStats);


        add(getButtonGroup());

        // Items

        List<String> optionTitles = List.of("Option 1", "Option 2", "Option 3", "Option 4");
        MenuBar menuBar = new MenuBar();
        SubMenu subItems2 = menuBar.addItem(VaadinIcon.SLIDERS.create()).getSubMenu();
        optionTitles.forEach(t -> {
            MenuItem menuItem = subItems2.addItem(t);
            menuItem.setCheckable(true);
            menuItem.setChecked(false);
            menuItem.setKeepOpen(true);
        });
        add(menuBar);

        Button btn = new Button("Add Transaction");
        btn.addClickListener(e -> new AddTransactionDialog(service).open());
        add(btn);

        Button scrollDown = new Button("Scroll down", e -> scrollBy(this, 0, -100));
        Button scrollUp = new Button("Scroll up", e -> scrollBy(this, 0, 100));
        Button scrollHome = new Button("Scroll home", e -> scrollTo(0, 0));
        add(scrollDown, scrollUp, scrollHome);
    }

    private static RadioButtonGroup<String> getButtonGroup() {
        RadioButtonGroup<String> radioButtonGroup = new RadioButtonGroup<>();
        radioButtonGroup.addClassName("multi-button");
        radioButtonGroup.setItems("None", "Token", "USD", "Percentage");
        radioButtonGroup.setRenderer(new ComponentRenderer<>(item -> switch (item) {
            case "Token" -> new Button(item, LumoIcon.CHECKMARK.create());
            case "USD" -> new Button(item, LumoIcon.PLUS.create());
            case "Percentage" -> new Button(item, LumoIcon.MINUS.create());
            default -> new Button(item, LumoIcon.CROSS.create());
        }));
        radioButtonGroup.setValue("foo");
        return radioButtonGroup;
    }

}




