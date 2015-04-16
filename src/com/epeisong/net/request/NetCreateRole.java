package com.epeisong.net.request;

import com.epeisong.EpsNetConfig;
import com.epeisong.base.activity.XBaseActivity;
import com.epeisong.logistics.net.NetServiceFactory;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.LogisticsReq;
import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.utils.LogUtils;
import com.google.protobuf.TextFormat;
import com.google.protobuf.GeneratedMessage.Builder;

/**
 * 创建角色
 * 
 * @author poet
 * 
 */
public abstract class NetCreateRole extends NetRequestorAsync<LogisticsReq.Builder, CommonLogisticsResp.Builder> {

    public NetCreateRole(XBaseActivity activity) {
        super(activity);
    }

    @Override
    protected int getCommandCode() {
        return CommandConstants.CREATE_LOGISTICS_REQ;
    }

    @Override
    protected String getPendingMsg() {
        return null;
    }

    @Override
    protected String getResult(CommonLogisticsResp.Builder resp) {
        return resp.getResult();
    }

    @Override
    protected String getDesc(CommonLogisticsResp.Builder resp) {
        return resp.getDesc();
    }

    @Override
    protected CommonLogisticsResp.Builder requestOneself() throws Exception {
        LogisticsReq.Builder req = LogisticsReq.newBuilder();
        if (onSetRequest(req)) {
            LogUtils.d("NetRequest", TextFormat.printToString(req));
            return NetServiceFactory.getInstance().createLogistics(EpsNetConfig.getHost(), EpsNetConfig.PORT, req,
                    9000);
        }
        return null;
    }

    @Override
    protected Builder<LogisticsReq.Builder> getRequestBuilder() {
        LogisticsReq.Builder req = LogisticsReq.newBuilder();
        if (onSetRequest(req)) {
            return req;
        }
        return null;
    }

    protected abstract boolean onSetRequest(LogisticsReq.Builder req);
}
