package com.example.application.views.components.complex_components.icons;

/*
 * https://pictogrammers.com/library/mdi/
 *
 * https://pictogrammers.com/docs/guides/iconify/
 *
 * */
public enum PictogramIcon {
    LOGIN,
    LOGOUT;

    public MonoIcon create() {
        return new MonoIcon(this);
    }


}
