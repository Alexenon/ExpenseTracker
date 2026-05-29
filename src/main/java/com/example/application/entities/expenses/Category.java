package com.example.application.entities.expenses;

import jakarta.annotation.Nullable;
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
	@Size(min = 4, max = 20, message = "Category name should be between 4 and 20 characters")
	private String name;

	@Nullable
	@Column(name = "icon_name")
	@Size(min = 3, max = 20, message = "Icon name should be between 3 and 20 characters")
	private String iconName;

	public Category(String name) {
		this.name = name;
	}
}