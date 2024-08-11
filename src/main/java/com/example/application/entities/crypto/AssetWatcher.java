package com.example.application.entities.crypto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity(name = "asset_watcher")
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AssetWatcher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @Column(nullable = false)
    private double target;

    @Column(nullable = false)
    private double targetAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TargetType targetType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActionType actionType;

    @Column(nullable = false)
    private boolean isCompleted;

    /**
     * @param target       represents the price target or percentage target of the asset price
     * @param targetAmount represents the amount to invest when the target is reached
     * @param targetType   represents the type of the target - price / percentage
     * //@param isCompleted  represents if the target was achieved
     */
    public AssetWatcher(Asset asset, double target, double targetAmount, TargetType targetType, ActionType actionType) {
        this.asset = asset;
        this.target = target;
        this.targetAmount = targetAmount;
        this.targetType = targetType;
        this.actionType = actionType;
    }

    public enum TargetType {
        PERCENTAGE,
        PRICE
    }

    public enum ActionType {
        BUY,
        SELL
    }

}


