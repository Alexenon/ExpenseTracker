package com.example.application.views.main.components;

import com.example.application.views.main.HomeView;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;

@Tag(Tag.DIV)
@CssImport("./error-page.css")
public class ErrorContainer extends Div {

    private final H2 errorTitle;
    private final Paragraph errorMessage;
    private Image image;

    public ErrorContainer() {
        setClassName("error-container");

        errorTitle = new H2("H2");
        errorTitle.setText("Ooops...");

        errorMessage = new Paragraph("p");
        errorMessage.setText("Something went wrong");
        // We can't seem to find the page you are looking for.

        Button goHomeBtn = new Button("Go Home");
        goHomeBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        goHomeBtn.addClickListener(event -> getUI().ifPresent(ui -> ui.navigate(HomeView.class)));

        add(errorTitle, errorMessage, goHomeBtn);
    }

    public void setErrorTitle(String title) {
        errorTitle.setText(title);
    }

    public void setErrorMessage(String message) {
        errorMessage.setText(message);
    }

    public void setImage(Image image) {
        this.image = image;
    }

}
