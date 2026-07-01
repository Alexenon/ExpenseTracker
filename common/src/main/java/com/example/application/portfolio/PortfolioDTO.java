package com.example.application.portfolio;


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