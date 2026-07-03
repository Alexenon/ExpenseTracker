package com.example.application.tag;

import lombok.Data;

@Data
public class TagDTO {

	private final Long id;
	private final String name;
	private final Long userId;

	public TagDTO(Tag tag) {
		this.id = tag.getId();
		this.name = tag.getName();
		this.userId = tag.getUser().getId();
	}
}
