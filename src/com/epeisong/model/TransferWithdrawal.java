package com.epeisong.model;

import java.io.Serializable;
/**
 * 转账提现类
 * @author gnn
 *
 */
public class TransferWithdrawal implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String tName;
	private double tMoney;
	private Integer card;
	private Integer serialNumber; // 流水号
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String gettName() {
		return tName;
	}
	public void settName(String tName) {
		this.tName = tName;
	}
	public double gettMoney() {
		return tMoney;
	}
	public void settMoney(double tMoney) {
		this.tMoney = tMoney;
	}
	public Integer getCard() {
		return card;
	}
	public void setCard(Integer card) {
		this.card = card;
	}
	public Integer getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(Integer serialNumber) {
		this.serialNumber = serialNumber;
	}
	
	

}
