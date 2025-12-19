package com.example.application.views.pages.blockchain;


import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

public class BlockChain {

	private BlockNode lastNodeAdded;
	private final List<BlockNode> nodes = new LinkedList<>();
	private final Queue<BlockNode> nodesToAdd = new LinkedBlockingDeque<>();

	public void add(BlockNode node) {
		nodesToAdd.add(node);
		manage();
	}

	private synchronized void manage() {
		BlockNode nodePolled = nodesToAdd.poll();
		nodePolled.mineBlock();
		nodes.getLast().setNextNode(nodePolled);

		Optional.ofNullable(nodes.getLast()).ifPresent(node -> node.setNextNode(nodePolled));
		nodePolled.setPreviousNode(nodes.getLast());

		nodes.add(nodePolled);
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


//  4, 5, 6, -1, 8, 8, -2, -3, -2


