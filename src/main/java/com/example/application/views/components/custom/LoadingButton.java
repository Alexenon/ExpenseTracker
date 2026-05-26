//package com.example.application.views.components.custom;
//
//import com.vaadin.flow.component.button.Button;
//import com.vaadin.flow.component.html.Span;
//import com.vaadin.flow.component.icon.Icon;
//import com.vaadin.flow.component.icon.VaadinIcon;
//import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
//
//public class LoadingButton extends Button {
//
//    private final Span textSpan = new Span();
//    private final Icon spinner = VaadinIcon.SPINNER.create();
//
//    private boolean loading = false;
//
//    public LoadingButton(String text) {
//        textSpan.setText(text);
//        spinner.getStyle().set("animation", "spin 1s linear infinite");
//        spinner.setVisible(false);
//
//        // Add text + spinner to button
//        add(textSpan, spinner);
//    }
//
//    /**
//     * Start loading: shows spinner and changes text to "Loading..."
//     */
//    public void startLoading() {
//        loading = true;
//        spinner.setVisible(true);
//        textSpan.setText("Loading...");
//        this.setEnabled(false);
//    }
//
//    /**
//     * Stop loading: hides spinner and restores original text
//     */
//    public void stopLoading() {
//        loading = false;
//        spinner.setVisible(false);
//        textSpan.setText("Done"); // or original text
//        this.setEnabled(true);
//    }
//
//    /**
//     * Check current loading state
//     */
//    public boolean isLoading() {
//        return loading;
//    }
//
//    /**
//     * Optionally, set text to show when loading stops
//     */
//    public void setDoneText(String text) {
//        if (!loading) {
//            textSpan.setText(text);
//        }
//    }
//
//    /**
//     * Optionally, set the loading text
//     */
//    public void setLoadingText(String text) {
//        if (loading) {
//            textSpan.setText(text);
//        }
//    }
//}
