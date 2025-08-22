package com.example.application.views.components;

import com.vaadin.flow.component.UI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class PriceChangeHandler {

    private static final ConcurrentHashMap<UI, PriceChangeblePage> pages = new ConcurrentHashMap<>();

    public void addObserver(UI ui, PriceChangeblePage page) {
        Objects.requireNonNull(page, "price changeble price");
        if (ui == null || !ui.isAttached()) {
            log.debug("UI is null or detached, the page is not added as observer");
            return;
        }

        pages.put(ui, page);
        ui.addDetachListener(e -> removeObserver(ui));
        log.debug("Added page, size: {}", pages.size());
    }

    public void removeObserver(UI ui) {
        pages.remove(ui);
        log.debug("Removed page, size: {}", pages.size());
    }

    public void updatePagePrices() {
        log.debug("Starting updating {} pages", pages.size());
        for (UI ui : pages.keySet()) {
            PriceChangeblePage page = pages.get(ui);
            ui.access(page::updatePage);
        }
        log.debug("Finished updating {} pages", pages.size());
    }

}
