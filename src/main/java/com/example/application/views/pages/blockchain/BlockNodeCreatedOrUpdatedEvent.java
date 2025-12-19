package com.example.application.views.pages.blockchain;

import com.vaadin.flow.component.ComponentEvent;

public class BlockNodeCreatedOrUpdatedEvent extends ComponentEvent<BlockComponent> {
    private final BlockNode blockNode;

    public BlockNodeCreatedOrUpdatedEvent(BlockComponent source, BlockNode blockNode) {
        super(source, false);
        this.blockNode = blockNode;
    }

    public BlockNode getBlockNode() {
        return blockNode;
    }
}