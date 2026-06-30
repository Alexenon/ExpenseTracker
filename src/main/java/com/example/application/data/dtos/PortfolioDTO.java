package com.example.application.data.dtos;

import com.example.application.entities.crypto.Portfolio;
import lombok.Data;

@Data
public class PortfolioDTO {

	private final Long id;
	private final String name;
	private final Long userId;

	public PortfolioDTO(Portfolio portfolio) {
		this.id = portfolio.getId();
		this.name = portfolio.getName();
		this.userId = portfolio.getUser().getId();
	}

}