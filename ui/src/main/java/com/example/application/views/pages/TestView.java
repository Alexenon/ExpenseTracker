package com.example.application.views.pages;

import com.example.application.utils.formatters.CommonFormatters;
import com.example.application.views.components.PriceChangeNotifier;
import com.example.application.views.components.PriceUpdatable;
import com.example.application.views.layouts.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Optional;

@AnonymousAllowed
@PageTitle("Test")
@Route(value = "test", layout = MainLayout.class)
public class TestView extends AbstractPage implements PriceUpdatable, BeforeEnterObserver, BeforeLeaveObserver {

    @Autowired
    private PriceChangeNotifier priceChangeNotifier;

    private UI ui;
    private final H3 title = new H3("Counter");
    private final NumberField counter = new NumberField();
    private final Paragraph text = new Paragraph("Last time updated:");
    private final Span lastTimeUpdated = new Span();

	private final Paragraph someText = new Paragraph("Some text");

    public TestView() {
        initializePage();
        buildPage();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        this.ui = UI.getCurrent();
        priceChangeNotifier.addObserver(ui, this);
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        priceChangeNotifier.removeObserver(ui);
    }

    public void initializePage() {
        getStyle().set("margin-top", "100px");
        counter.setValue(0.0);
        System.out.println("Page is initiliazed");
    }

    public void buildPage() {
//        Path pathToFile = Path.of("C:\\Users\\asus\\Desktop", "Orders.csv");
//        DownloadButton downloadButton = new DownloadButton(pathToFile);

        add(
                new HorizontalLayout(title, counter),
                new HorizontalLayout(text, lastTimeUpdated),
				new HorizontalLayout(someText)
//                ,new HorizontalLayout(downloadButton)
        );
    }

    @Override
    public void update() {
        String formatedDateTime = CommonFormatters.TIME.format(LocalDateTime.now());
        double value = Optional.ofNullable(counter.getValue()).orElse(Double.NaN);
        counter.setValue(value + 1);
        lastTimeUpdated.setText(formatedDateTime);
        System.out.println("Counter updated with " + counter.getValue());
    }


}
