package com.example.application.views.components.custom.icons;

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
    TRASH_CAN_OUTLINE,
    ARROW_RIGHT_THIN;

    public MonoIcon create() {
        return new MonoIcon(this);
    }


}
