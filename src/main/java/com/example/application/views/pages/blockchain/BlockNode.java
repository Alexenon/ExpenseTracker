package com.example.application.views.pages.blockchain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;

public class BlockNode {

	private final String id = UUID.randomUUID().toString();

	private Integer nonce;
	private String data;
	private String hashValue;
	private BlockNode previousNode;
	private BlockNode nextNode;

	public BlockNode(String data) {
		this(data, null);
	}

	public BlockNode(String data, BlockNode previousNode) {
		this(data, previousNode, null);
	}

	public BlockNode(String data, BlockNode previousNode, BlockNode nextNode) {
		this.data = data;
		this.previousNode = previousNode;
		this.nextNode = nextNode;
	}

	public void setNonce(Integer nonce) {
		this.nonce = nonce;
		this.hashValue = recalculateHash();
	}

	public void setData(String data) {
		this.data = data;
		this.hashValue = recalculateHash();
	}

	public void setPreviousNode(BlockNode previousNode) {
		this.previousNode = previousNode;
		this.hashValue = recalculateHash();
	}

	public void setNextNode(BlockNode nextNode) {
		this.nextNode = nextNode;
		this.hashValue = recalculateHash();
	}

	//<editor-fold desc="Getters">
	public Integer getNonce() {
		return nonce;
	}

	public String getData() {
		return data;
	}

	public String getHash() {
		return hashValue;
	}

	public BlockNode getPreviousNode() {
		return previousNode;
	}

	public BlockNode getNextNode() {
		return nextNode;
	}
	//</editor-fold>

	public void mineBlock() {
		nonce = 0;
		String hash = recalculateHash();

		while (!hash.startsWith("0000")) {
			nonce++;
			hash = recalculateHash();
		}

		this.hashValue = hash;
		System.out.println("Block mined! Nonce=" + nonce + " Hash=" + hash);
	}

	private String recalculateHash() {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			String input = (previousNode == null ? "" : previousNode.getHash()) + data + nonce;
			byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));

			StringBuilder sb = new StringBuilder();
			for (byte b : hashBytes) {
				sb.append(String.format("%02x", b));
			}
			return sb.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public boolean isMined() {
		return nonce != null && getHash() != null && getHash().startsWith("0000");
	}

	public boolean isFirstNode() {
		return previousNode == null;
	}

	/**
	 * @return whenever current node is pointed to previous node correctly, and this node is mined
	 * */
	public boolean isValid() {
		return (isFirstNode() || previousNode.isValid()) && isMined();
	}


	/*
		TODO:
			- Can be INVALID and MINED at same time ???
			- WHAT PENDING MEANS ?
	* */


}
