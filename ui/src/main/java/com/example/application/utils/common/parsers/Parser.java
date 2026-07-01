package com.example.application.utils.common.parsers;

import com.vaadin.flow.server.StreamResource;

import java.io.InputStream;
import java.util.List;

public interface Parser<T> {

    List<T> parseImport(InputStream stream);

    StreamResource parseExport(List<T> list);

}