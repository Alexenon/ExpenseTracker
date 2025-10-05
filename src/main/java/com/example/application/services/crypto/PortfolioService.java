package com.example.application.services.crypto;

import com.example.application.entities.User;
import com.example.application.entities.crypto.AssetBalance;
import com.example.application.entities.crypto.Portfolio;
import com.example.application.repositories.crypto.AssetRepository;
import com.example.application.repositories.crypto.PortfolioRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class PortfolioService {

    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private PortfolioRepository portfolioRepository;
    @Autowired
    private AssetBalanceService assetBalanceService;

    /**
     * Creates and attach a new portfolio to the provided user.
     * */
    @NotNull
    @Transactional
    public Portfolio createPortfolio(@NotNull User user) {
        Objects.requireNonNull(user, "user");

        Portfolio portfolio = new Portfolio();
        portfolio.setUser(user);
        Portfolio savedPortfolio = portfolioRepository.save(portfolio);

        // TODO: [URGENT] !!!
        //  Instead of creating bunch of columns in the database tables with zero values,
        //  add only real saved transactions...
        //
        //  TODO: WRAP WITH TRY CATCH

        // Creating new Portfolio Balance for each asset with value 0.0
        assetRepository.findAll().forEach(asset -> {
            AssetBalance assetBalance = new AssetBalance();
            assetBalance.setPortfolio(savedPortfolio);
            assetBalance.setAsset(asset);
            assetBalanceService.save(assetBalance);
        });

        return savedPortfolio;
    }

    @NotNull
    public Portfolio getPortfolioByUser(@NotNull User user) {
        Objects.requireNonNull(user, "user");
        return portfolioRepository.findByUser(user).get(0); // TODO: [URGENT] !!! HERE
    }

}
