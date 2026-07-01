package com.example.application.entities.crypto;


import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "user_assets")
@EqualsAndHashCode(of = "id")
public class UserAsset {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne
	@JoinColumn(name = "asset_id", nullable = false)
	private Asset asset;

	@Nullable
	@Column(name = "comment")
	String comment;

	@Column(name = "marked_as_favorite", nullable = false)
	boolean markedAsFavorite = false;

	@Column(name = "last_time_updated", nullable = false)
	private LocalDateTime lastTimeUpdated = LocalDateTime.now();

}
