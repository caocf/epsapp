package com.epeisong.data.net.parser;

import com.epeisong.logistics.proto.Wallet.ProtoWallet;
import com.epeisong.model.Wallet;

public class WalletParser {

    public static Wallet parser(ProtoWallet res) {
        Wallet dest = new Wallet();
        dest.setWalletName(res.getWalletName());
        dest.setAmount(res.getAmount());
        dest.setCreateDate(res.getCreateDate());
        dest.setIdentityNumber(res.getIdentityNumber());
        dest.setIdType(res.getIdType());
        dest.setIdStatus(res.getIdStatus());
        dest.setRealName(res.getRealName());
        dest.setId(res.getId());
        dest.setStatus(res.getStatus());
        dest.setUpdateDate(res.getUpdateDate());
        return dest;
    }

}
