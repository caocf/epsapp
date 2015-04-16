package com.epeisong.net.request;

import com.epeisong.data.dao.UserDao;
import com.epeisong.data.net.NetRequestor;
import com.epeisong.logistics.proto.ComplainTask.ComplainTaskReq;
import com.epeisong.logistics.proto.ComplainTask.ComplainTaskResp;
import com.epeisong.utils.LogUtils;
import com.google.protobuf.GeneratedMessage.Builder;

/**
 * @author Jack
 *
 */
public abstract class NetComplainTask extends NetRequestor<ComplainTaskReq.Builder, ComplainTaskResp.Builder> {

    @Override
    protected abstract int getCommandCode();

    @Override
    protected String getResult(ComplainTaskResp.Builder resp) {
        return resp.getResult();
    }

    @Override
    protected String getDesc(ComplainTaskResp.Builder resp) {
        return resp.getDesc();
    }

    @Override
    protected Builder<ComplainTaskReq.Builder> getRequest() {
    	ComplainTaskReq.Builder req = ComplainTaskReq.newBuilder();
        setRequest(req);
        req.setLogisticsId(Integer.parseInt(UserDao.getInstance().getUser().getId()));
        return req;
    }

    protected abstract void setRequest(ComplainTaskReq.Builder req);
}
