package com.epeisong.net.ws.utils;

import java.util.List;

import com.epeisong.model.Wallet;

/**
 * @author Roy Lu
 * @since Dec 29, 2014
 * @function
 */
public class BondWalletResp {
	public static int SUCC = 1;
	public static int FAIL = -1;

	int result = -1;

	String desc = "";

	Wallet wallet = null;
	
	BondWallet bondWallet = null;
	
	List<BondWalletDetail> bondWalletDetailList = null;
	
	public String getDesc() {
		return desc;
	}

	public int getResult() {
		return result;
	}

	public Wallet getWallet() {
		return wallet;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public void setWallet(Wallet wallet) {
		this.wallet = wallet;
	}

    public BondWallet getBondWallet() {
        return bondWallet;
    }

    public void setBondWallet(BondWallet bondWallet) {
        this.bondWallet = bondWallet;
    }

    public List<BondWalletDetail> getBondWalletDetailList() {
        return bondWalletDetailList;
    }

    public void setBondWalletDetailList(List<BondWalletDetail> bondWalletDetailList) {
        this.bondWalletDetailList = bondWalletDetailList;
    }
	
}
