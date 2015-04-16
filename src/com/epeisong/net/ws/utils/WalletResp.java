package com.epeisong.net.ws.utils;

import java.util.List;

import com.epeisong.model.Wallet;

/**
 * @author Roy Lu
 * @since Dec 29, 2014
 * @function
 */
public class WalletResp {
	public static int SUCC = 1;
	public static int FAIL = -1;

	int result = -1;

	String desc = "";

	Wallet wallet = null;

	List<Wallet> walletList = null;
	
	List<WalletDetail> walletDetailList = null;
	
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

    public List<Wallet> getWalletList() {
        return walletList;
    }

    public void setWalletList(List<Wallet> walletList) {
        this.walletList = walletList;
    }

    public List<WalletDetail> getWalletDetailList() {
        return walletDetailList;
    }

    public void setWalletDetailList(List<WalletDetail> walletDetailList) {
        this.walletDetailList = walletDetailList;
    }
	
	
}
