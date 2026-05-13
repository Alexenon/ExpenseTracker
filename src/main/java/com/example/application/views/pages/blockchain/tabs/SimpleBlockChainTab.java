package com.example.application.views.pages.blockchain.tabs;

import com.example.application.data.models.blockchain.BlockNode;
import com.example.application.views.pages.blockchain.BlockNodeCreatedOrUpdatedEvent;
import com.example.application.views.pages.blockchain.components.BlockComponent;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.tabs.Tab;

import java.util.LinkedList;
import java.util.List;

public class SimpleBlockChainTab extends Tab {

	private final List<BlockComponent> blocks = new LinkedList<>();

	public SimpleBlockChainTab() {
		BlockNode node1 = new BlockNode("Block 1", null);
		BlockNode node2 = new BlockNode("Block 2", node1);
		BlockNode node3 = new BlockNode("Block 3", node2);

		addBlocks(
				new BlockComponent("Block #1", node1),
				new BlockComponent("Block #2", node2),
				new BlockComponent("Block #3", node3)
		);

		ComponentUtil.addListener(UI.getCurrent(), BlockNodeCreatedOrUpdatedEvent.class, event -> {
			BlockComponent component = event.getSource();

			component.applyBadgeAndColor();
			getAllBlocksAfter(component).forEach(BlockComponent::applyBadgeAndColor);
		});
	}

	public void addBlocks(BlockComponent... blockComponents) {
		for (BlockComponent b : blockComponents) {
			blocks.add(b);
			add(b);
		}
	}

	public List<BlockComponent> getAllBlocksAfter(BlockComponent blockComponent) {
		int index = blocks.indexOf(blockComponent);
		if (index == -1 || index == blocks.size() - 1) {
			return List.of();
		}
		return blocks.subList(index + 1, blocks.size());
	}

	public BlockComponent getPrevious(BlockComponent blockComponent) {
		int index = blocks.indexOf(blockComponent);
		return (index > 0) ? blocks.get(index - 1) : null;
	}

}
