package com.epeisong.net.ws.utils;

import com.epeisong.model.Wallet;

/**
 * @author Roy Lu
 * @since Dec 29, 2014
 * @function
 */
public class Resp {
    public static int SUCC = 1;

    int result = -1;

    String desc = "";

    Wallet wallet = null;

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

    @Override
    public String toString() {
        return "Resp [result=" + result + ", desc=" + desc + ", wallet=" + wallet + "]";
    }

}
