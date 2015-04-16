package com.epeisong.data.net;

import com.epeisong.logistics.proto.Wallet.WalletReq;
import com.epeisong.logistics.proto.Wallet.WalletResp;
import com.google.protobuf.GeneratedMessage.Builder;

/**
 * 钱包
 * @author poet
 *
 */
public abstract class NetWalletRequestor extends NetRequestor<WalletReq.Builder, WalletResp.Builder> {

    @Override
    protected String getResult(WalletResp.Builder resp) {
        // TODO Auto-generated method stub
        return resp.getResult();
    }

    @Override
    protected String getDesc(WalletResp.Builder resp) {
        // TODO Auto-generated method stub
        return resp.getDesc();
    }

    @Override
    protected Builder<WalletReq.Builder> getRequest() {
        WalletReq.Builder req = WalletReq.newBuilder();
        if (onSetRequest(req)) {
            return req;
        }
        return null;
    }

    protected abstract boolean onSetRequest(WalletReq.Builder req);
}
