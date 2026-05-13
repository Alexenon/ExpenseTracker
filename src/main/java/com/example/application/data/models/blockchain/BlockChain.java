package com.example.application.data.models.blockchain;


import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class BlockChain {

	private BlockNode lastNodeAdded;
	private final List<BlockNode> nodes = new LinkedList<>();

	public void add(BlockNode node) {
		node.mineBlock();
		nodes.getLast().setNextNode(node);

		Optional.ofNullable(nodes.getLast()).ifPresent(n -> n.setNextNode(node));
		node.setPreviousNode(nodes.getLast());

		nodes.add(node);
		System.out.println("Added new block");
	}

	public static void main(String[] args) {
		BlockChain chain = new BlockChain();

		BlockNode node1 = new BlockNode("Node 1", null);
		BlockNode node2 = new BlockNode("Node 2", node1);
		BlockNode node3 = new BlockNode("Node 3", node2);

		chain.add(node1);
		chain.add(node2);
		chain.add(node3);
	}

}
