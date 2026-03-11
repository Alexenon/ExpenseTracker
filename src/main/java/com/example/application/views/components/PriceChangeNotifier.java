package com.example.application.views.components;

import com.vaadin.flow.component.UI;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Component design to notify UI pages whenever a price has been updated, so the UI can update the information as well.
 *
 * @see PriceUpdatable
 * @see com.example.application.components.AssetUpdateScheduler
 * */
@Slf4j
@Component
public class PriceChangeNotifier {

    private static final ConcurrentHashMap<UI, PriceUpdatable> pages = new ConcurrentHashMap<>();

    public void addObserver(@Nullable UI ui, @NotNull PriceUpdatable page) {
        Objects.requireNonNull(page, "price changeble price");
        if (ui == null || !ui.isAttached()) {
            log.debug("UI is null or detached, the UI is not added as observer");
            return;
        }

        ui.addDetachListener(e -> removeObserver(ui));
        pages.put(ui, page);
        log.debug("Added UI, size: {}", pages.size());
    }

    public void removeObserver(@NotNull UI ui) {
        pages.remove(ui);
        log.debug("Removed UI, size: {}", pages.size());
    }

    public void updatePagePrices() {
        log.debug("Starting updating {} UI's", pages.size());
        for (UI ui : pages.keySet()) {
            PriceUpdatable page = pages.get(ui);
            ui.access(page::update);
        }
        log.debug("Finished updating {} UI's", pages.size());
    }

}
