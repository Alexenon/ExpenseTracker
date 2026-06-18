package com.example.application.entities.expenses;

import com.example.application.entities.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.StringJoiner;

@Data
@Entity(name = "categories")
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Column(nullable = false)
	@Size(min = 4, max = 20, message = "Name should be between 4 and 20 characters")
	private String name;

	@NotNull
	@Column(name = "icon_name", nullable = false)
	@Size(min = 3, max = 50, message = "Icon name should be between 3 and 20 characters")
	private String iconName;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Override
	public String toString() {
		return new StringJoiner(", ", Category.class.getSimpleName() + "[", "]")
				.add("id=" + id)
				.add("name='" + name + "'")
				.add("iconName='" + iconName + "'")
				.add("user=" + user.getId())
				.toString();
	}
}