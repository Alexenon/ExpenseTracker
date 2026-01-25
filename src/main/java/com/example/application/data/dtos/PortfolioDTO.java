package com.example.application.data.dtos;

import com.example.application.entities.crypto.Portfolio;
import lombok.Data;

@Data
public class PortfolioDTO {

	private final Long id;
	private String name;
	private String ownerUsername;

	public PortfolioDTO() {
		this.id = null;
	}

	public PortfolioDTO(Portfolio portfolio) {
		this.id = portfolio.getId();
		this.name = portfolio.getName();
		this.ownerUsername = portfolio.getUser().getUsername();
	}

}