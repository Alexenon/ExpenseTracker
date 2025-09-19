package com.example.application.views.components.core.buttons;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoIcon;
import org.springframework.lang.Nullable;

import java.util.function.Supplier;

/*
    TODO URGENT:
        [!] Add working successfulListener & failedListener
        [!] Style the errorMessage padding
* */
public class Download extends Div {

    private final Anchor anchor = new Anchor();
    private final Button button = new Button("Download", LumoIcon.DOWNLOAD.create());
    private final Paragraph errorMessage = new Paragraph();

    @Nullable
    private Supplier<StreamResource> resourceSupplier;

    public Download() {
        this(null);
    }

    public Download(Supplier<StreamResource> resourceSupplier) {
        setResource(resourceSupplier);
        initialize();
    }

    private void initialize() {
        anchor.getElement().setAttribute("download", true);
        add(button, anchor);
        errorMessage.setVisible(false);
        errorMessage.getStyle().set("color", "red");

        button.addClickListener(e -> {
            errorMessage.setVisible(false);

            if (resourceSupplier == null)
                return;

            try {
                StreamResource resource = resourceSupplier.get();
                resource.setCacheTime(0);
                anchor.setHref(resource);
                anchor.getElement().callJsFunction("click");
            } catch (Exception ex) {
                errorMessage.setText("Error generating file: " + ex.getLocalizedMessage());
                add(errorMessage);
                errorMessage.setVisible(true);
            }
        });
    }

    public void setResource(Supplier<StreamResource> resourceSupplier) {
        this.resourceSupplier = resourceSupplier;
    }

    public Button getButton() {
        return button;
    }

    public Anchor getAnchor() {
        return anchor;
    }

}