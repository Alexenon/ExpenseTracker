package com.example.application.views.components.custom.icons;

/*
 * https://pictogrammers.com/library/mdi/
 *
 * TO ADD
 *      Webfont: <span class="mdi mdi-tablet-cellphone"></span>  =====> TABLET_CELLPHONE
 * */
public enum PictogramIcon {
    LOGIN,
    LOGOUT,
    ACCOUNT,
    INFORMATION_OUTLINE,
    TRASH_CAN_OUTLINE,
    ARROW_RIGHT_THIN,
    TABLET_CELLPHONE,
    TRANSFER,
    ACCOUNT_CASH_OUTLINE,
    BITCOIN,
    CURRENCY_BTC,
    FINANCE,
    FILE_SIGN,
    INVOICE_CLOCK_OUTLINE,
    PIGGY_BANK_OUTLINE,
    CASH_LOCK,
    CASH_PLUS,
    SHIELD_LOCK,
    BRIEFCASE_EDIT,
    // Apps
    GITHUB,
    FACEBOOK,
    TWITTER;

    public MonoIcon create() {
        return new MonoIcon(this);
    }


}
