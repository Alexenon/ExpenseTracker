package com.example.application.tag;


import com.example.application.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.StringJoiner;

@Data
@Entity
@Table(name = "tags")
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class Tag {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Column(name = "name")
	@Size(min = 3, max = 20, message = "Tag name should be between 3 and 20 characters")
	private String name;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@NotNull
	@Column(name = "last_time_updated", nullable = false)
	private LocalDateTime lastTimeUpdated = LocalDateTime.now();

	@NotNull
	@Column(name = "time_created_at", nullable = false, updatable = false)
	private final LocalDateTime timeCreatedAt = LocalDateTime.now();

	public Tag(String name, User user) {
		this.name = name;
		this.user = user;
	}

	@Override
	public String toString() {
		return name;
	}

	public String toFullString() {
		return new StringJoiner(", ", Tag.class.getSimpleName() + "[", "]")
				.add("id=" + id)
				.add("name='" + name + "'")
				.add("user=" + user)
				.add("lastTimeUpdated=" + lastTimeUpdated)
				.add("timeCreatedAt=" + timeCreatedAt)
				.toString();
	}

}
