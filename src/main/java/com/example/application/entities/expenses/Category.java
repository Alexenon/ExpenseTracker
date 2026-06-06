package com.example.application.entities.expenses;

import com.example.application.entities.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;

@Data
@Entity(name = "categories")
@ToString
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Column(unique = true, nullable = false)
	@Size(min = 4, max = 20, message = "Name should be between 4 and 20 characters")
	private String name;

	@NotNull
	@Column(name = "icon_name", unique = true, nullable = false)
	@Size(min = 3, max = 20, message = "Icon name should be between 3 and 20 characters")
	private String iconName;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

}