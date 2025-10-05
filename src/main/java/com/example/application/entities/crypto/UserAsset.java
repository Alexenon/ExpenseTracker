package com.example.application.entities.crypto;

import com.example.application.entities.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "user_assets")
public class UserAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @Column(name = "comment")
    String comment;

    @Column(name = "marked_as_favorite")
    boolean markedAsFavorite = false;

    @Column(name = "last_time_updated", nullable = false)
    private LocalDateTime lastTimeUpdated = LocalDateTime.now();

}
