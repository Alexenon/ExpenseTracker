package com.example.application.views.components;

import com.vaadin.flow.component.UI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/*
* TODO: [URGENT] Update name to "PriceChangeNotifier"
*  Add here a small javadoc why this component is required
* */
@Slf4j
@Component
public class PriceChangeHandler {

    private static final ConcurrentHashMap<UI, PriceChangeblePage> pages = new ConcurrentHashMap<>();

    public void addObserver(UI ui, PriceChangeblePage page) {
        Objects.requireNonNull(page, "price changeble price");
        if (ui == null || !ui.isAttached()) {
            log.debug("UI is null or detached, the UI is not added as observer");
            return;
        }

        ui.addDetachListener(e -> removeObserver(ui));
        pages.put(ui, page);
        log.debug("Added UI, size: {}", pages.size());
    }

    public void removeObserver(UI ui) {
        pages.remove(ui);
        log.debug("Removed UI, size: {}", pages.size());
    }

    public void updatePagePrices() {
        log.debug("Starting updating {} UI's", pages.size());
        for (UI ui : pages.keySet()) {
            PriceChangeblePage page = pages.get(ui);
            ui.access(page::updatePage);
        }
        log.debug("Finished updating {} UI's", pages.size());
    }

}
