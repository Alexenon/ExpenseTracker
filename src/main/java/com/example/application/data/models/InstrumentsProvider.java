package com.example.application.data.models;

import com.example.application.data.models.crypto.AssetData;
import com.example.application.data.models.crypto.Currency;
import com.example.application.entities.crypto.Asset;
import com.example.application.repositories.AssetRepository;
import com.example.application.utils.fetchers.BinanceFetcher;
import com.example.application.utils.fetchers.CryptoCompareFetcher;
import com.example.application.utils.fetchers.api_responses.AssetInfo;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class InstrumentsProvider {

    private final List<Asset> assets;

    @Getter
    private List<Currency> currencyList;

    @Getter
    private List<AssetData> listOfAssetData;

    @Autowired
    private InstrumentsProvider(AssetRepository assetRepository) {
        this.assets = assetRepository.findAll();
        this.listOfAssetData = initAssetsData();
    }

    public AssetInfo getAssetInfo(String name) {
        return CryptoCompareFetcher.getCoinMetaData(name).getData();
    }

    public List<AssetData> initAssetsData() {
        return assets.stream()
                .map(asset -> new AssetData(asset, getAssetInfo(asset.getSymbol())))
                .toList();
    }

    public List<AssetInfo> getAssetsInfo() {
        return assets.stream()
                .map(Asset::getSymbol)
                .map(this::getAssetInfo)
                .toList();
    }

    public List<Currency> updateCurrencyList() {
        List<Currency> currencyList = BinanceFetcher.getCryptoPrices("USDT")
                .stream()
                .map(cryptoCurrency -> {
                    String name = cryptoCurrency.getSymbol();
                    BigDecimal price = BigDecimal.valueOf(cryptoCurrency.getPrice());
                    return new Currency(name, price, BigDecimal.ZERO);
                })
                .toList();

        this.currencyList = currencyList;
        return currencyList;
    }

    public Currency getCurrencyByName(String name) {
        return currencyList.stream()
                .filter(c -> c.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

}
