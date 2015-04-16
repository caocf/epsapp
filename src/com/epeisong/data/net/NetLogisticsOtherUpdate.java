package com.epeisong.data.net;

import com.baidu.location.BDLocation;
import com.epeisong.data.dao.RegionDao;
import com.epeisong.data.dao.UserDao;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.common.Constants;
import com.epeisong.logistics.proto.Base.ProtoEBizLogistics;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.LogisticsReq;
import com.epeisong.model.Region;
import com.epeisong.model.RegionResult;
import com.epeisong.model.User;
import com.epeisong.utils.LogUtils;
import com.google.protobuf.GeneratedMessage.Builder;

/**
 * 修改大角色信息
 * @author Jack
 *
 */
public abstract class NetLogisticsOtherUpdate extends NetRequestor<LogisticsReq.Builder, CommonLogisticsResp.Builder> {

    @Override
    protected int getCommandCode() {
        return CommandConstants.UPDATE_LOGISTICS_INFO_REQ;
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
    protected Builder<LogisticsReq.Builder> getRequest() {
        LogisticsReq.Builder req = LogisticsReq.newBuilder();
        if (onSetRequest(req)) {
            return req;
        }
        ProtoEBizLogistics.Builder logi = ProtoEBizLogistics.newBuilder();
        if (onSetRequestParams(logi)) {
            req.setBizLogistics(logi);
            return req;
        }
        return null;
    }

    protected boolean onSetRequest(LogisticsReq.Builder req) {
        return false;
    }

    protected boolean onSetRequestParams(ProtoEBizLogistics.Builder logi) {
        return false;
    };

}
