package com.example.application.data.models;

import com.example.application.entities.User;

import java.util.ArrayList;
import java.util.List;

public class Wallet {
    private final List<Holding> holdings;
    private final List<CryptoTransaction> transactions;
    private final User user;

    public Wallet() {
        this.holdings = new ArrayList<>();
        this.transactions = new ArrayList<>();
        this.user = null;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
