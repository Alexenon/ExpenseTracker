package com.example.application.entities.crypto;

import com.example.application.entities.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity(name = "crypto_watcher")
@NoArgsConstructor
@AllArgsConstructor
public class AssetWatcher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private long id;

    @Column(name = "currency_name", nullable = false)
    @Getter
    @Setter
    private String currencyName;

    @OneToMany
    @JoinColumn(name = "watch_price_id", nullable = false)
    @Getter
    @Setter
    private List<WatchPrice> watchPrices;

    @Getter
    @Setter
    private String comment;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @Getter
    @Setter
    private User user;

    public AssetWatcher(String currencyName, List<WatchPrice> watchPrices, String comment, User user) {
        this.currencyName = currencyName;
        this.watchPrices = watchPrices;
        this.comment = comment;
        this.user = user;
    }
}
