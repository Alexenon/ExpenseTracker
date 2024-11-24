package com.example.application.views.components.complex_components.icons;

/*
 * https://pictogrammers.com/library/mdi/
 *
 * https://pictogrammers.com/docs/guides/iconify/
 *
 * */
public enum PictogramIcon {
    LOGIN,
    LOGOUT,
    ACCOUNT,
    INFORMATION_OUTLINE,
    TRASH_CAN_OUTLINE;


    public MonoIcon create() {
        return new MonoIcon(this);
    }


}
