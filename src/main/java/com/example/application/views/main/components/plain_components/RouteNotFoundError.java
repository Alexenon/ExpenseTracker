package com.example.application.views.main.components.plain_components;

import com.example.application.views.main.components.ErrorContainer;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.NotFoundException;
import jakarta.servlet.http.HttpServletResponse;

@Tag(Tag.DIV)
public class RouteNotFoundError extends Div implements HasErrorParameter<NotFoundException> {

    private final ErrorContainer errorContainer;

    public RouteNotFoundError() {
        addClassNames("page-404");
        errorContainer = new ErrorContainer();

        add(errorContainer);
    }

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<NotFoundException> parameter) {
        String text = "Could not navigate to '" + event.getLocation().getPath() + "'";
        getElement().setText(text);
        add(errorContainer);

        return HttpServletResponse.SC_NOT_FOUND;
    }

}