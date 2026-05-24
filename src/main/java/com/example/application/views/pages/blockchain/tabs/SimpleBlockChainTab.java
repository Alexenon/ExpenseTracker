package com.example.application.views.pages.blockchain.tabs;

import com.example.application.data.models.blockchain.BlockNode;
import com.example.application.views.pages.blockchain.components.BlockComponent;
import com.example.application.views.pages.blockchain.components.ChainComponent;
import com.example.application.views.pages.blockchain.events.BlockNodeUpdatedEvent;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.tabs.Tab;

public class SimpleBlockChainTab extends Tab {

	private final ChainComponent chainComponent  = createChainComponent();

	public SimpleBlockChainTab() {
		add(chainComponent);
		initializeListener();
	}

	private ChainComponent createChainComponent() {
		ChainComponent chainComponent = new ChainComponent("Chain #1");
		chainComponent.addBlock(new BlockComponent("Block #1", new BlockNode("Block 1")));
		chainComponent.addBlock(new BlockComponent("Block #2", new BlockNode("Block 2")));
		chainComponent.addBlock(new BlockComponent("Block #3", new BlockNode("Block 3")));
		return chainComponent;
	}

	private void initializeListener() {
		ComponentUtil.addListener(UI.getCurrent(), BlockNodeUpdatedEvent.class, event -> {
			BlockComponent component = event.getSource();
			chainComponent.updateAllNodesAfter(component);
		});
	}


}
