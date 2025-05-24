package com.example.application.views.components.custom.icons;

/*
 * https://pictogrammers.com/library/mdi/
 *
 * https://pictogrammers.com/docs/guides/iconify/
 *
 *
 * TO ADD
 *  Webfont name:
 *      <span class="mdi mdi-tablet-cellphone"></span>
 * Transforms into:
 *      TABLET_CELLPHONE
 *
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
    GITHUB,
    SHIELD_LOCK,
    BRIEFCASE_EDIT;

    public MonoIcon create() {
        return new MonoIcon(this);
    }


}
