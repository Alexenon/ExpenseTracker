//package com.example.application.views.components.utils.validators;
//
//import com.example.application.entities.crypto.Asset;
//import com.example.application.services.crypto.InstrumentsFacadeService;
//import com.vaadin.flow.data.binder.ValidationResult;
//import com.vaadin.flow.data.binder.ValueContext;
//import com.vaadin.flow.data.validator.AbstractValidator;
//
//public class AssetBalanceValidator extends AbstractValidator<Asset> {
//
//    private final InstrumentsFacadeService instrumentsFacadeService;
//
//    public AssetBalanceValidator(InstrumentsFacadeService instrumentsFacadeService) {
//        super("");
//        this.instrumentsFacadeService = instrumentsFacadeService;
//    }
//
//    @Override
//    public ValidationResult apply(Asset value, ValueContext context) {
//        return toResult(value, true);
//    }
//
//    private boolean isBalanceGood(Asset asset) {
////        return instrumentsFacadeService.fillAssetBalance(a)
//    }
//
//}
