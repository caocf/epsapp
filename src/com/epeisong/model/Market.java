package com.epeisong.model;

import java.io.Serializable;

/**
 * 配货市场
 * @author gnn
 *
 */
public class Market implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String marketName;
	private String marketTel;
	private String marketAddr;
	public int getId() {
		return id;
	}
	public void setId(int id) {
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
	

}
