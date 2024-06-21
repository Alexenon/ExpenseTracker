package com.example.application.views.components;

import com.example.application.data.models.Asset;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.listbox.MultiSelectListBox;

public class SearchFilterGrid {

    private final Grid<Asset> grid = new Grid<>(Asset.class);
    private final MultiSelectListBox<String> listBox = new MultiSelectListBox<>(); //
    private final GridListDataView<Asset> dataView = grid.setItems();

    private Grid.Column<Asset> currencyNameCol;


}
