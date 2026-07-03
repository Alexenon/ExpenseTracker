package com.example.application.category;

import lombok.Data;

@Data
public class CategoryDTO {

	private final Long id;
	private final String name;
	private final String iconName;
	private final Long userId;

	public CategoryDTO(Category category) {
		this.id = category.getId();
		this.name = category.getName();
		this.iconName = category.getIconName();
		this.userId = category.getUser().getId();
	}

}
