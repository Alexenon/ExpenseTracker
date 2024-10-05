package com.example.application.views.pages;

import com.example.application.views.layouts.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
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

import java.util.List;

@AnonymousAllowed
@PageTitle("Home")
@Route(value = "", layout = MainLayout.class)
public class HomeView extends Main {

    public HomeView() {
        addClassName("page-content");
        Paragraph text = new Paragraph("Welcome to Home page!");
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


        RadioButtonGroup<Button> buttonGroup = new RadioButtonGroup<>();
        Button option1 = new Button("None", LumoIcon.CROSS.create());
        Button option2 = new Button("Token", LumoIcon.CHECKMARK.create());
        Button option3 = new Button("USD", LumoIcon.MINUS.create());
        Button option4 = new Button("Percentage", LumoIcon.MINUS.create());
        buttonGroup.setItems(option1, option2, option3, option4);
        add(buttonGroup);


        List<String> optionTitles = List.of("Option 1", "Option 2", "Option 3", "Option 4");

        MenuBar menuBar = new MenuBar();
        SubMenu subItems = menuBar.addItem(VaadinIcon.SLIDERS.create()).getSubMenu();
        optionTitles.forEach(t -> {
            Checkbox checkbox = new Checkbox(t);
            MenuItem menuItem = subItems.addItem(checkbox);
            menuItem.addClickListener(e -> checkbox.setValue(!checkbox.getValue()));
            menuItem.setKeepOpen(true);
        });
        add(menuBar);

        // 2

        MenuBar menuBar2 = new MenuBar();
        SubMenu subItems2 = menuBar2.addItem(VaadinIcon.SLIDERS.create()).getSubMenu();
        optionTitles.forEach(t -> {
            MenuItem menuItem = subItems2.addItem(t);
            menuItem.setCheckable(true);
            menuItem.setChecked(false);
            menuItem.setKeepOpen(true);
        });
        add(menuBar2);

        // 3

        Paragraph paragraph = new Paragraph("Welcome to Home page!");
        add(paragraph);
    }
}




