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
@Entity(name = "portfolios")
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

	@Column(name = "last_time_updated", nullable = false)
	private LocalDateTime lastTimeUpdated = LocalDateTime.now();

	@Column(name = "time_created_at", nullable = false, updatable = false)
	private final LocalDateTime timeCreatedAt = LocalDateTime.now();

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
                .formatted(id, name, user.getUsername());
    }

}