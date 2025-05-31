package com.example.application.views.components.core.buttons;

public class ToggleButton extends com.vaadin.componentfactory.ToggleButton {

    public ToggleButton() {
        this(null, false);
    }

    public ToggleButton(String labelText) {
        this(labelText, false);
    }

    public ToggleButton(String labelText, boolean initialValue) {
        super(labelText, initialValue);
        addAttachListener(e -> normalize());
    }

    public boolean isChecked() {
        return this.getValue();
    }

    private void normalize() {
        this.getElement().executeJs("""
                setTimeout(() => {
                    const toggleButton = document.querySelector('vcf-toggle-button');
                    const outerCheckbox = toggleButton?.shadowRoot?.querySelector('vaadin-checkbox');
                    const checkboxShadow = outerCheckbox?.shadowRoot;
                
                    const style = document.createElement('style');
                    style.textContent = `
                        [part="checkbox"]::after {
                            content: '';
                            display: block;
                            position: absolute !important;
                            width: 20px !important;
                            height: 20px !important;
                            top: 2px !important;
                            left: 2px !important;
                        }
                    `;
                    checkboxShadow.appendChild(style);
                }, 100);
                """);
    }


}
