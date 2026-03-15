package com.example.application.entities.crypto;

import com.example.application.entities.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "portfolios")
public class Portfolio {

	@Nullable
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Size(min = 4, max = 20, message = "Portfolio name should be between 4 and 20 characters")
	@Column(name = "name", nullable = false)
	private String name;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@NotNull
	@Column(name = "last_time_updated", nullable = false)
	private LocalDateTime lastTimeUpdated = LocalDateTime.now();

	@NotNull
	@Column(name = "time_created_at", nullable = false, updatable = false)
	private final LocalDateTime timeCreatedAt = LocalDateTime.now();

	public Portfolio(String name) {
		this.name = name;
	}

	//<editor-fold desc="UTILS">
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;

		if (!(o instanceof Portfolio other))
			return false;

		return id != null && id.equals(other.getId())
			   && this.name.equals(other.getName());
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}

	@Override
	public String toString() {
		return "Portfolio{id=%d, name='%s', user='%s'}"
				.formatted(id, name, user.getId());
	}
	//</editor-fold>

}