package com.epeisong.net.request;

import com.epeisong.base.activity.XBaseActivity;
import com.epeisong.data.dao.UserDao;
import com.epeisong.logistics.proto.Eps.FreightReq;
import com.epeisong.logistics.proto.Eps.FreightResp;
import com.epeisong.logistics.common.CommandConstants;
import com.google.protobuf.GeneratedMessage;

/**
 * 获取自己发布的车源货源
 * 
 * @author poet
 * 
 */
public class NetListMineFreight extends NetRequestorAsync<FreightReq.Builder, FreightResp.Builder> {

    private long createData;
    private int count;
    private int id;

    public NetListMineFreight(XBaseActivity activity, long createData, int count, int id) {
        super(activity);
        this.createData = createData;
        this.count = count;
        this.id = id;
    }

    @Override
    protected int getCommandCode() {
        return CommandConstants.LIST_FREIGHTS_ON_BLACK_BOARD_BY_CREATE_TIME_REQ;//LIST_OWN_FREIGHT_ON_BLACK_BOARD_BY_CREATE_TIME_REQ;
    }

    @Override
    protected String getPendingMsg() {
        return null;
    }

    @Override
    protected long getTimeout() {
        return 16000;
    }

    @Override
    protected String getResult(FreightResp.Builder resp) {
        return resp.getResult();
    }

    @Override
    protected String getDesc(FreightResp.Builder resp) {
        return resp.getDesc();
    }

    @Override
    protected GeneratedMessage.Builder<FreightReq.Builder> getRequestBuilder() {
        FreightReq.Builder req = FreightReq.newBuilder();
        req.setDate(createData);
        req.setLimitCount(count);

        req.setLogisticId(id);
        req.setLogisticName(UserDao.getInstance().getUser().getShow_name());
        return req;
    }
}
