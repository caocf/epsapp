package com.epeisong.net.ws.utils;

import java.util.List;

/**
 * @author Roy Lu
 * @since Dec 29, 2014
 * @function
 */
public class BankCardResp {
	public static int SUCC = 1;
	public static int FAIL = -1;

	int result = -1;

	String desc = "";

	BankCard bankCard = null;
	
	List<BankCard> bankCardList=null;
	
	public String getDesc() {
		return desc;
	}

	public int getResult() {
		return result;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void setResult(int result) {
		this.result = result;
	}

    public BankCard getBankCard() {
        return bankCard;
    }

    public void setBankCard(BankCard bankCard) {
        this.bankCard = bankCard;
    }

    public List<BankCard> getBankCardList() {
        return bankCardList;
    }

    public void setBankCardList(List<BankCard> bankCardList) {
        this.bankCardList = bankCardList;
    }

	
}
