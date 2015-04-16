package com.epeisong.data.net;

import com.epeisong.logistics.proto.Eps.BulletinReq;
import com.epeisong.logistics.proto.Eps.BulletinResp;
import com.epeisong.logistics.common.CommandConstants;
import com.google.protobuf.GeneratedMessage.Builder;

/**
 * 查看别人的公告列表
 * 
 * @author poet
 * 
 */
public abstract class NetBulletinList extends NetRequestor<BulletinReq.Builder, BulletinResp.Builder> {

    @Override
    protected int getCommandCode() {
        return CommandConstants.LIST_BULLETINS_REQ;
    }

    @Override
    protected String getResult(com.epeisong.logistics.proto.Eps.BulletinResp.Builder resp) {
        return resp.getResult();
    }

    @Override
    protected String getDesc(com.epeisong.logistics.proto.Eps.BulletinResp.Builder resp) {
        return resp.getDesc();
    }

    @Override
    protected Builder<BulletinReq.Builder> getRequest() {
        BulletinReq.Builder req = BulletinReq.newBuilder();
        if (onSetRequest(req)) {
            return req;
        }
        return null;
    }

    protected abstract boolean onSetRequest(BulletinReq.Builder req);

}
