package com.example.application.views.pages.blockchain;

import java.util.Objects;
import java.util.UUID;

// https://github.com/anders94/blockchain-demo/tree/master/public
public class Block {

	private final String id = UUID.randomUUID().toString();
	private Integer nonce;
	private final String data;
	private final Block previousBlock;

	public Block(String data, Block previousBlock) {
		this(null, data, previousBlock);
	}

	public Block(Integer nonce, String data, Block previousBlock) {
		this.nonce = nonce;
		this.previousBlock = previousBlock;
		this.data = data;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;

		if (o == null || this.getClass() != o.getClass())
			return false;

		Block copyBlock = (Block) o;

		return Objects.equals(nonce, copyBlock.nonce)
			   && Objects.equals(previousBlock, copyBlock.previousBlock)
			   && Objects.equals(data, copyBlock.data);
	}

	@Override
	public int hashCode() {
		return Objects.hash(nonce, previousBlock, data);
	}

	public void setNonce(Integer nonce) {
		this.nonce = nonce;
	}

	public int getNonce() {
		return nonce;
	}

	public Block getPreviousBlock() {
		return previousBlock;
	}

	public String getData() {
		return data;
	}

	public String getId() {
		return id;
	}

}