package com.example.application.data.models.blockchain;

import com.example.application.views.pages.blockchain.components.BlockStatus;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.UUID;

public class BlockNode {

	private final String id = UUID.randomUUID().toString();

	private Integer nonce;
	private String data;
	private String hashValue;
	private BlockNode previousNode;
	private BlockNode nextNode;
	private BlockStatus status = BlockStatus.WAITING;
	private BlockChain chain;

	public BlockNode(String data) {
		this.data = data;
	}

	public void mineBlock() {
		if (isMined())
			return;

		System.out.println("Started mining: " + this);
		nonce = 0;

		String previousHash = hashValue;
		String hash = recalculateHash();
		while (!isMinedCorrectly(hash)) {
			nonce++;
			hash = recalculateHash();
		}

		this.hashValue = hash;
		this.status = BlockStatus.MINED;
		notifyChainThatBlockChanged(previousHash, hash);
		System.out.println("Block mined! Nonce=" + nonce + " Hash=" + hash);
	}

	//<editor-fold desc="Setters">
	public void setNonce(Integer nonce) {
		this.nonce = nonce;
		updateHashAndStatus();
	}

	public void setData(String data) {
		this.data = data;
		updateHashAndStatus();
	}

	void setPreviousNode(BlockNode previousNode) {
		this.previousNode = previousNode;
		updateHashAndStatus();
	}

	void setNextNode(BlockNode nextNode) {
		this.nextNode = nextNode;
		updateHashAndStatus();
	}

	void setChain(BlockChain chain) {
		this.chain = chain;
	}

	void setStatus(BlockStatus status) {
		this.status = status;
	}
	//</editor-fold>

	//<editor-fold desc="Getters">
	public boolean isMined() {
		return status.equals(BlockStatus.MINED);
	}

	public boolean isInvalid() {
		return status.equals(BlockStatus.INVALID);
	}

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

	public BlockStatus getStatus() {
		return status;
	}
	//</editor-fold>

	void updateHashAndStatus() {
		String previousHash = hashValue;
		String newHash = recalculateHash();

		if (newHash.equals(previousHash))
			return;

		BlockStatus previousStatus = status;

		this.hashValue = newHash;
		this.status = recalculateStatus();
		notifyChainThatBlockChanged(previousHash, newHash);
		System.out.printf("Updated from [%s] to [%s], %s\n", previousStatus, status, this);
	}

	private String recalculateHash() {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			String input = id + (previousNode == null ? "" : previousNode.getHash()) + data + nonce;
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

	private BlockStatus recalculateStatus() {
		if (nonce == null || nonce == 0)
			return BlockStatus.WAITING;

		if (isMinedCorrectly(hashValue))
			return BlockStatus.MINED;

		return BlockStatus.INVALID;
	}

	private boolean isMinedCorrectly(String hash) {
		return nonce != null
			   && nonce > 0
			   && hash.startsWith("0000");
	}

	private void notifyChainThatBlockChanged(String oldHash, String newHash) {
		Optional.ofNullable(chain).ifPresent(c -> c.onBlockHashChanged(this));
		System.out.printf("""
				Blockchain detected block change:
					Old hash: %s
					New hash: %s
				%s
				%n""", oldHash, newHash, this);
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", BlockNode.class.getSimpleName() + "[", "]")
				.add("id='" + id + "'")
				.add("data='" + data + "'")
				.add("status=" + status)
				.add("nonce=" + nonce)
				.add("hashValue='" + hashValue + "'")
				.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		BlockNode blockNode = (BlockNode) o;
		return Objects.equals(id, blockNode.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(hashValue);
	}
}
