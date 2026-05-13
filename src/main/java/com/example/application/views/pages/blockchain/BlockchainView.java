package com.example.application.views.pages.blockchain;

import com.example.application.views.pages.blockchain.tabs.NetworkBlockChainTab;
import com.example.application.views.pages.blockchain.tabs.SimpleBlockChainTab;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PermitAll
@PageTitle("Blockchain")
@Route("blockchain")
public class BlockchainView extends HorizontalLayout {

	public BlockchainView() {
		Tab simpleBlockChainTab = new SimpleBlockChainTab();
		Tab networkBlockChainTab = new NetworkBlockChainTab();

		TabSheet tabSheet = new TabSheet();
		tabSheet.setClassName("blockchain-tabs");

		tabSheet.add("Simple blockchain", simpleBlockChainTab);
		tabSheet.add("Network blockchain", networkBlockChainTab);

		add(tabSheet);
	}

}
