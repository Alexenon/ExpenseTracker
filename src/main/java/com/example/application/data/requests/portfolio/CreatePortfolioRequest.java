package com.example.application.data.requests.portfolio;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreatePortfolioRequest {

	@NotBlank
	@Size(min = 4, max = 20)
	private String portfolioName;

	@NotNull
	private Long userId;

	public CreatePortfolioRequest(String portfolioName, Long userId) {
		this.portfolioName = portfolioName;
		this.userId = userId;
	}

}
