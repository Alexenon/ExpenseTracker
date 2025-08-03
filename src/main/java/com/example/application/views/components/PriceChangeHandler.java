package com.example.application.views.components;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.UIDetachedException;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PriceChangeHandler {

    private static final ConcurrentHashMap<UI, PriceChangeblePage> pages = new ConcurrentHashMap<>();

    public void addObserver(UI ui, PriceChangeblePage page) {
        System.out.println("Starting updating counter");
        if (ui == null || !ui.isAttached()) {
            System.out.println("UI is null or detached, the page is not added as observer");
            return;
        }

        Objects.requireNonNull(page, "price changeble price");
        pages.put(ui, page);
        System.out.println("Added page, size: " + pages.size());
    }

    public void removeObserver(UI ui) {
        pages.remove(ui);
        System.out.println("Removed page, size: " + pages.size());
    }

    public void updatePagePrices() {
        System.out.format("Starting updating %d pages%n", pages.size());
        for (UI ui : pages.keySet()) {
            PriceChangeblePage page = pages.get(ui);
            try {
                ui.accessSynchronously(page::updatePage);
            } catch (UIDetachedException e) {
                System.out.println("UI is detached, removing observer");
                pages.remove(ui); // Safe here since we're not in forEach
            }
        }
        System.out.println("Finished updating pages");
    }

}
