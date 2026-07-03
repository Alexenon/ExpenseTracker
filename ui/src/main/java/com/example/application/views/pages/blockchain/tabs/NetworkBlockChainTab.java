package com.example.application.views.pages.blockchain.tabs;

import com.example.application.data.models.blockchain.BlockNode;
import com.example.application.views.components.core.Container;
import com.example.application.views.pages.blockchain.components.BlockComponent;
import com.example.application.views.pages.blockchain.components.ChainComponent;
import com.example.application.views.pages.blockchain.events.BlockNodeUpdatedEvent;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.tabs.Tab;

public class NetworkBlockChainTab extends Tab {

	private final ChainComponent chain1 = createChainComponent("Chain #1");
	private final ChainComponent chain2 = createChainComponent("Chain #2");
	private final ChainComponent chain3 = createChainComponent("Chain #3");

	public NetworkBlockChainTab() {
		addClassName("blockchain-network-tab");
		initializeFields();
		initializeBlockchainListener();
	}

	private void initializeFields() {
		Container content = Container.builder("content")
				.addComponent(chain1)
				.addComponent(chain2)
				.addComponent(chain3)
				.build();

		add(content);
	}

	private ChainComponent createChainComponent(String name) {
		ChainComponent chainComponent = new ChainComponent(name);
		chainComponent.addBlock(new BlockComponent("Block #1", new BlockNode("Block 1")));
		chainComponent.addBlock(new BlockComponent("Block #2", new BlockNode("Block 2")));
		chainComponent.addBlock(new BlockComponent("Block #3", new BlockNode("Block 3")));
		return chainComponent;
	}

	private void initializeBlockchainListener() {
		ComponentUtil.addListener(UI.getCurrent(), BlockNodeUpdatedEvent.class, event -> {
			BlockNode blockNode = event.getBlockNode();

			BlockComponent component = event.getSource();
			component.applyBadgeAndColor();
		});
	}

}
