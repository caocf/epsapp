package com.epeisong.net.request;

import com.epeisong.base.activity.XBaseActivity;
import com.epeisong.data.dao.UserDao;
import com.epeisong.logistics.proto.Eps.FreightReq;
import com.epeisong.logistics.proto.Eps.FreightResp;
import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.model.User;
import com.google.protobuf.GeneratedMessage;

public abstract class NetFreightUpdateStatus extends NetRequestorAsync<FreightReq.Builder, FreightResp.Builder> {
    /*
     * private int freightId; private int newStatus;
     */

    public NetFreightUpdateStatus(XBaseActivity a) {
        super(a);
    }

    @Override
    protected int getCommandCode() {
        // TODO Auto-generated method stub
        return CommandConstants.UPDATE_FREIGHT_STATUS_REQ;
    }

    @Override
    protected String getPendingMsg() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected String getResult(FreightResp.Builder resp) {
        // TODO Auto-generated method stub
        return resp.getResult();
    }

    @Override
    protected String getDesc(FreightResp.Builder resp) {
        // TODO Auto-generated method stub
        return resp.getDesc();
    }

    @Override
    protected GeneratedMessage.Builder<FreightReq.Builder> getRequestBuilder() {
        FreightReq.Builder req = FreightReq.newBuilder();
        if (onSetRequest(req)) {
            User u = UserDao.getInstance().getUser();
            req.setLogisticId(Integer.parseInt(u.getId()));
            req.setLogisticName(u.getShow_name());
            return req;
        }
        return null;
    }

    protected abstract boolean onSetRequest(FreightReq.Builder req);
}
