package com.example.application.services;

import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.services.expenses.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DBOperationRunner implements CommandLineRunner {

	@Autowired
	private InstrumentsFacadeService instrumentsFacadeService;

	@Autowired
	private CategoryService categoryService;

	@Override
	public void run(String... args) throws Exception {
		updateDatabase();
	}

	private void updateDatabase() {
		categoryService.saveCategoriesInBatch();
	}

}

