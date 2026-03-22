package com.example.application.entities;

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

	public Category() {
	}

	public Category(String name) {
		this.name = name;
	}
}