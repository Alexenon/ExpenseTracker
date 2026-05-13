package com.example.application.views.pages.blockchain.tabs;

import com.example.application.data.models.blockchain.BlockNode;
import com.example.application.views.pages.blockchain.components.BlockComponent;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.tabs.Tab;

import java.util.LinkedList;
import java.util.List;

public class NetworkBlockChainTab extends Tab {

	public NetworkBlockChainTab() {
		addClassName("blockchain-network-tab");
		add(body());
	}

	private Div body() {
		Div div = new Div();
		div.addClassName("content");

		div.add(
				createChain(1),
				createChain(2),
				createChain(3)
		);

		return div;
	}

	private void initializeBlockchainListener() {
//		ComponentUtil.addListener(UI.getCurrent(), BlockNodeCreatedOrUpdatedEvent.class, event -> {
//			BlockComponent component = event.getSource();
//
//			component.applyColor();
//			System.out.println(getAllBlocksAfter(component));
//			getAllBlocksAfter(component).forEach(BlockComponent::applyColor);
//		});
	}

	public List<BlockComponent> getAllBlocksAfter(List<BlockComponent> blocks, BlockComponent blockComponent) {
		int index = blocks.indexOf(blockComponent);
		if (index == -1 || index == blocks.size() - 1) {
			return List.of();
		}
		return blocks.subList(index + 1, blocks.size());
	}

	public BlockComponent getPrevious(List<BlockComponent> blocks, BlockComponent blockComponent) {
		int index = blocks.indexOf(blockComponent);
		return (index > 0) ? blocks.get(index - 1) : null;
	}

	private Div createChain(int chainId) {
		Div div = new Div();
		div.addClassName("blocks-container");
		H3 blockHeader = new H3("Chain #" + chainId);

		List<BlockComponent> blocks = new LinkedList<>();
		BlockNode node1 = new BlockNode("Block 1", null);
		BlockNode node2 = new BlockNode("Block 2", node1);
		BlockNode node3 = new BlockNode("Block 3", node2);

		div.add(blockHeader);
		addBlocks(
				div,
				blocks,
				newBlockComponent("Block #1", node1),
				newBlockComponent("Block #2", node2),
				newBlockComponent("Block #3", node3)
		);

		return div;
	}

	private static BlockComponent newBlockComponent(String name, BlockNode node1) {
		BlockComponent blockComponent = new BlockComponent(name, node1);
		blockComponent.addClassName("block-card");
		return blockComponent;
	}

	private void addBlocks(Div div, List<BlockComponent> blocks, BlockComponent... blockComponents) {
		for (BlockComponent b : blockComponents) {
			blocks.add(b);
			div.add(b);
		}
	}

}
