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

    // Very common
    DELETE,
	IMPORT,
	EXPORT,
	SWAP_VERTICAL,
	SWAP_HORIZONTAL,
    DELETE_OUTLINE,
    SQUARE_EDIT_OUTLINE,
    INFORMATION_OUTLINE,
	ALERT_CIRCLE_OUTLINE,
	CHECK_CIRCLE_OUTLINE,

    // Third party applications,
    GITHUB,
    FACEBOOK,
    TWITTER,
    ;

    public MonoIcon create() {
        return new MonoIcon(this);
    }

    public MonoIcon create(String className) {
        return new MonoIcon(this, className);
    }

}
