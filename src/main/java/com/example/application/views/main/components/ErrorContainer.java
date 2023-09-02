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
@CssImport("./themes/light_theme/styles/component-styles/error-page.css")
public class ErrorContainer extends Div {

    private final H2 errorTitle;
    private final Paragraph errorDescription;
    private final Image errorImage;

    public ErrorContainer() {
        addClassName("error-container");

        errorTitle = new H2("H2");
        errorTitle.addClassName("error-title");
        errorTitle.setText("Ooops...");

        errorDescription = new Paragraph("p");
        errorDescription.addClassName("error-description");
        errorDescription.setText("Something went wrong");

        errorImage = new Image("./images/404.webp", "Error Image");
        errorImage.addClassName("error-image");

        Button goHomeBtn = new Button("Go Home");
        goHomeBtn.addClassName("return-btn");
        goHomeBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        goHomeBtn.addClickListener(event -> getUI().ifPresent(ui -> ui.navigate(HomeView.class)));

        Div detailsContainer = new Div();
        detailsContainer.add(errorTitle, errorDescription, goHomeBtn);

        add(errorImage, detailsContainer);
    }

    public void setErrorTitle(String title) {
        errorTitle.setText(title);
    }

    public void setErrorDescription(String message) {
        errorDescription.setText(message);
    }

    public void setImageSource(String src) {
        errorImage.setSrc(src);
    }

}
