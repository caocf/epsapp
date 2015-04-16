/**
 * 
 * 
 */
package com.epeisong.net.ws.utils;

public enum EnumWalletType {
    MAIN(1), TRANSACTION(2), BOND(3), WITHDRAW(4);

    private int value;

    private EnumWalletType(int value) {
        this.value = value;
    }

    /**
     * @return the value
     */
    public int getValue() {
        return value;
    }
}
