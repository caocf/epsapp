package com.epeisong.data.net;

import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.proto.Wallet.WalletReq;
import com.epeisong.logistics.proto.Wallet.WalletResp;
import com.google.protobuf.GeneratedMessage.Builder;

public abstract class NetGetWallet extends NetRequestor<WalletReq.Builder, WalletResp.Builder> {

	@Override
	protected int getCommandCode() {
		return CommandConstants.GET_WALLET_REQ;
	}

	@Override
	protected Builder<WalletReq.Builder> getRequest() {
		WalletReq.Builder req = WalletReq.newBuilder();
        if (onSetRequest(req)) {
            return req;
        }
        return null;
	}

	@Override
	protected String getResult(WalletResp.Builder resp) {
		return resp.getResult();
	}

	@Override
	protected String getDesc(WalletResp.Builder resp) {
		return resp.getDesc();
	}
	
	protected abstract boolean onSetRequest(WalletReq.Builder req);

}
