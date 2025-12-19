package com.example.application.views.pages.blockchain;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import java.util.LinkedList;
import java.util.List;

@PermitAll
@PageTitle("Blockchain")
@Route("blockchain")
public class BlockchainView extends HorizontalLayout {

	private final List<BlockComponent> blocks = new LinkedList<>();

	public BlockchainView() {
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

			component.applyColor();
			System.out.println(getAllBlocksAfter(component));
			getAllBlocksAfter(component).forEach(BlockComponent::applyColor);
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
