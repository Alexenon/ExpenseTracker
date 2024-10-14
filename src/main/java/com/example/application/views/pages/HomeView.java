package com.example.application.views.pages;

import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.views.components.complex_components.dialogs.transactions.AddTransactionDialog;
import com.example.application.views.layouts.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
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
public class HomeView extends Main {

    @Autowired
    private InstrumentsFacadeService service;

    public HomeView() {
        addClassName("page-content");
        Paragraph text = new Paragraph("Welcome to Home page!");
        text.addClassNames(LumoUtility.FontSize.XLARGE);
        add(text);

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
        add(radioButtonGroup);

        // Items

        List<String> optionTitles = List.of("Option 1", "Option 2", "Option 3", "Option 4");
        MenuBar menuBar2 = new MenuBar();
        SubMenu subItems2 = menuBar2.addItem(VaadinIcon.SLIDERS.create()).getSubMenu();
        optionTitles.forEach(t -> {
            MenuItem menuItem = subItems2.addItem(t);
            menuItem.setCheckable(true);
            menuItem.setChecked(false);
            menuItem.setKeepOpen(true);
        });
        add(menuBar2);


        Button btn = new Button("Add Transaction");
        btn.addClickListener(e -> new AddTransactionDialog(service).open());
        add(btn);
    }
}




