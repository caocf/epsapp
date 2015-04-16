package com.epeisong.model;

import java.io.Serializable;

public class MarketMember implements Serializable {

	private static final long serialVersionUID = 1L;
	private User user;
	private String id;
	private String marketName;
	private String marketTel;
	private String marketAddr;
	private int isBanned;
	private int status;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMarketName() {
		return marketName;
	}

	public void setMarketName(String marketName) {
		this.marketName = marketName;
	}

	public String getMarketTel() {
		return marketTel;
	}

	public void setMarketTel(String marketTel) {
		this.marketTel = marketTel;
	}

	public String getMarketAddr() {
		return marketAddr;
	}

	public void setMarketAddr(String marketAddr) {
		this.marketAddr = marketAddr;
	}

	public int getIsBanned() {
		return isBanned;
	}

	public void setIsBanned(int isBanned) {
		this.isBanned = isBanned;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
