package com.example.application.data.requests.portfolio;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreatePortfolioRequest {

	private String portfolioName;
	private Long userId;

}
