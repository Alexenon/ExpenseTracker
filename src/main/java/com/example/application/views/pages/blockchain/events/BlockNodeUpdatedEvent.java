package com.example.application.views.pages.blockchain.events;

import com.example.application.data.models.blockchain.BlockNode;
import com.example.application.views.pages.blockchain.components.BlockComponent;
import com.vaadin.flow.component.ComponentEvent;

public class BlockNodeUpdatedEvent extends ComponentEvent<BlockComponent> {

	private final BlockNode blockNode;

	public BlockNodeUpdatedEvent(BlockComponent source, BlockNode blockNode) {
		super(source, false);
		this.blockNode = blockNode;
	}

	public BlockNode getBlockNode() {
		return blockNode;
	}

}