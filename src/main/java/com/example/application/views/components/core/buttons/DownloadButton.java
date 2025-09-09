package com.example.application.views.components.core.buttons;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoIcon;

/**
 * Component that allows to download a certain file/stream on client side
 * */
public class DownloadButton extends Div {

    private final Anchor anchor = new Anchor();
    private final Button button = new Button("Download", LumoIcon.DOWNLOAD.create());

    public DownloadButton(StreamResource resource) {
        anchor.setHref(resource);
        anchor.getElement().setAttribute("download", true);
        anchor.add(button);

        add(anchor);
    }

    public Button getButton() {
        return button;
    }

    public Anchor getAnchor() {
        return anchor;
    }

}