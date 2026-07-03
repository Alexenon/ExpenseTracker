package com.example.application.data.models.blockchain;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class BlockChain {

	private final List<BlockNode> nodes = new LinkedList<>();

	public void addNode(BlockNode node) {
		if (lastNode().isPresent()) {
			BlockNode previous = lastNode().get();
			previous.setNextNode(node);
			node.setPreviousNode(previous);
		}
		node.setChain(this);
		nodes.add(node);
	}

	public void removeNode(BlockNode node) {
		Optional.ofNullable(node.getPreviousNode()).ifPresent(prev -> prev.setNextNode(node.getNextNode()));
		Optional.ofNullable(node.getNextNode()).ifPresent(next -> next.setPreviousNode(node.getPreviousNode()));
		node.setChain(null);
		nodes.remove(node);
	}

	public Optional<BlockNode> lastNode() {
		return nodes.isEmpty()
				? Optional.empty()
				: Optional.of(nodes.getLast());
	}

	public List<BlockNode> getNodes() {
		return nodes;
	}

	public List<BlockNode> getNodesAfter(BlockNode node) {
		return nodes.stream()
				.dropWhile(n -> n != node)
				.toList();
	}

	public boolean isValid() {
		return nodes.isEmpty() || isAllBlocksValid();
	}

	private boolean isAllBlocksValid() {
		return false;
	}

	void onBlockHashChanged(BlockNode node) {
		invalidateFollowingBlocks(node);
	}

	private void invalidateFollowingBlocks(BlockNode node) {
		System.out.println("Invalidation following blocks: ");
		BlockNode current = node.getNextNode();

		while (current != null) {
			System.out.println(" -> " + current);
			current.updateHashAndStatus();
			current = current.getNextNode();
		}
		System.out.println("----Finished invalidation----");
	}

}
