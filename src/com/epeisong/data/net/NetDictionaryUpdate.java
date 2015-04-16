package com.epeisong.data.net;

import com.epeisong.logistics.proto.Eps.SysDictionaryReq;
import com.epeisong.logistics.proto.Eps.SysDictionaryResp;
import com.epeisong.logistics.common.CommandConstants;
import com.google.protobuf.GeneratedMessage.Builder;

/**
 * 检查系统字典的更新
 * 
 * @author poet
 * 
 */
public abstract class NetDictionaryUpdate
        extends
        NetRequestor<SysDictionaryReq .Builder, SysDictionaryResp .Builder> {

    @Override
    protected int getCommandCode() {
        return CommandConstants.CHECK_SYS_DICTIONARY_NEW_VERSOIN_FOR_UPDATE_REQ;
    }

    @Override
    protected String getResult(com.epeisong.logistics.proto.Eps.SysDictionaryResp .Builder resp) {
        // TODO Auto-generated method stub
        return resp.getResult();
    }

    @Override
    protected String getDesc(com.epeisong.logistics.proto.Eps.SysDictionaryResp .Builder resp) {
        // TODO Auto-generated method stub
        return resp.getDesc();
    }

    @Override
    protected Builder<SysDictionaryReq .Builder> getRequest() {
        SysDictionaryReq .Builder req = SysDictionaryReq .newBuilder();
        if (onSetRequest(req)) {
            return req;
        }
        return null;
    }

    protected abstract boolean onSetRequest(SysDictionaryReq .Builder req);
}
