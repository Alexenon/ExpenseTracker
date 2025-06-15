//package com.example.application.views.components.core.buttons;
//
//import com.vaadin.flow.component.ClickEvent;
//import com.vaadin.flow.component.ComponentEventListener;
//import com.vaadin.flow.component.html.Div;
//import com.vaadin.flow.component.html.Paragraph;
//
//public class DualLabelToggleButton extends Div {
//
//    private String leftText;
//    private String rightText;
//    private final ToggleButton toggleButton;
//
//    public DualLabelToggleButton(String leftText, String rightText) {
//        this.leftText = leftText;
//        this.rightText = rightText;
//        this.toggleButton = new ToggleButton();
//        add(new Paragraph(leftText), toggleButton, new Paragraph(rightText));
//    }
//
//    public void addClickListen(ComponentEventListener<ClickEvent<com.vaadin.componentfactory.ToggleButton>> listener) {
//        toggleButton.addClickListener(listener);
//    }
//
//    public boolean isChecked() {
//        return toggleButton.isChecked();
//    }
//
//    //<editor-fold desc="Getters & Setters">
//    public void setLeftText(String leftText) {
//        this.leftText = leftText;
//    }
//
//    public void setRightText(String rightText) {
//        this.rightText = rightText;
//    }
//
//    public String getLeftText() {
//        return leftText;
//    }
//
//    public String getRightText() {
//        return rightText;
//    }
//    //</editor-fold>
//}
