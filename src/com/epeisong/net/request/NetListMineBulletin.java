package com.epeisong.net.request;

import com.epeisong.logistics.proto.Eps.BulletinReq;
import com.epeisong.logistics.proto.Eps.BulletinResp;
import com.epeisong.logistics.common.CommandConstants;
import com.google.protobuf.GeneratedMessage;

/**
 * 获取我发布的公告列表
 * 
 * @author poet
 * 
 */
public abstract class NetListMineBulletin extends NetRequestorAsync<BulletinReq.Builder, BulletinResp.Builder> {

    @Override
    protected int getCommandCode() {
        return CommandConstants.LIST_OWN_BULLETINS_REQ;
    }

    @Override
    protected String getPendingMsg() {
        return null;
    }

    @Override
    protected String getResult(BulletinResp.Builder resp) {
        return resp.getResult();
    }

    @Override
    protected String getDesc(BulletinResp.Builder resp) {
        return resp.getDesc();
    }

    @Override
    protected GeneratedMessage.Builder<BulletinReq.Builder> getRequestBuilder() {
        BulletinReq.Builder req = BulletinReq.newBuilder();
        if (onSetRequest(req)) {
            return req;
        }
        return null;
    }

    protected abstract boolean onSetRequest(BulletinReq.Builder req);
}
