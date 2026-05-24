package com.example.application.views.pages.blockchain.components;

import com.example.application.data.models.blockchain.BlockChain;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;

import java.util.LinkedList;
import java.util.List;

public class ChainComponent extends Div {

	private final BlockChain chain = new BlockChain();
	private final List<BlockComponent> blocks = new LinkedList<>();

	public ChainComponent(String name) {
		addClassName("blocks-container");
		H3 blockHeader = new H3(name);
		add(blockHeader);
	}

	public void addBlock(BlockComponent component) {
		add(component);
		blocks.add(component);
		chain.addNode(component.getNode());
	}

	public void removeBlock(BlockComponent component) {
		remove(component);
		blocks.remove(component);
		chain.removeNode(component.getNode());
	}

	public void updateAllNodesAfter(BlockComponent component) {
		getBlockComponentsAfter(component)
				.forEach(BlockComponent::applyBadgeAndColor);
	}

	public List<BlockComponent> getBlockComponentsAfter(BlockComponent blockComponent) {
		return blocks.stream()
				.dropWhile(n -> n.getNode() != blockComponent.getNode())
				.toList();
	}

	public List<BlockComponent> getBlockAfter(int index) {
		return blocks.stream()
				.skip(index)
				.toList();
	}

	public int indexOf(BlockComponent blockComponent) {
		return blocks.indexOf(blockComponent);
	}

}
