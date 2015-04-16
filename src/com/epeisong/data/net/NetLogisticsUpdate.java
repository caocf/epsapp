package com.epeisong.data.net;

import android.text.TextUtils;

import com.bdmap.epsloc.EpsLocation;
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
 * @author poet
 *
 */
public abstract class NetLogisticsUpdate extends NetRequestor<LogisticsReq.Builder, CommonLogisticsResp.Builder> {

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
        boolean setReq = onSetRequest(req);
        boolean setLogi = false;
        ProtoEBizLogistics.Builder logi = ProtoEBizLogistics.newBuilder();
        if (onSetRequestParams(logi)) {
            req.setBizLogistics(logi);
            setLogi = true;
        }
        if (setReq || setLogi) {
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

    public static CommonLogisticsResp.Builder updateLocation(final EpsLocation loc, final int dataType) {
        RegionResult regionResult = null;
        if (!TextUtils.isEmpty(loc.getCityName())) {
            Region city = RegionDao.getInstance().queryByCityName(loc.getCityName());
            if (city != null) {
                Region district = RegionDao.getInstance().queryByDistrictNameAndCityCode(loc.getDistrictName(),
                        city.getCode());
                if (district != null) {
                    regionResult = RegionDao.convertToResult(district);
                } else {
                    regionResult = RegionDao.convertToResult(city);
                }
            }
        }
        final RegionResult result = regionResult;

        NetLogisticsUpdate net = new NetLogisticsUpdate() {
            @Override
            protected boolean onSetRequestParams(ProtoEBizLogistics.Builder logi) {
                logi.setCurrentLongitude(loc.getLongitude());
                logi.setCurrentLatitude(loc.getLatitude());
                if (result != null) {
                    logi.setRegionCode(result.getFullCode());
                    logi.setRegionName(result.getGeneralName());
                }
                logi.setAddress(loc.getAddressName());
                return true;
            }

            @Override
            protected boolean onSetRequest(LogisticsReq.Builder req) {
                req.setIsRealTime(dataType);
                return true;
            }
        };
        try {
            CommonLogisticsResp.Builder resp = net.request();
            if (net.isSuccess(resp)) {
                User user = UserDao.getInstance().getUser();
                user.getUserRole().setCurrent_longitude(loc.getLongitude());
                user.getUserRole().setCurrent_latitude(loc.getLatitude());
                if (result != null) {
                    user.setRegion(result.getGeneralName());
                    user.setRegion_code(result.getFullCode());
                }
                user.setAddress(loc.getAddressName());
                UserDao.getInstance().replace(user);
            }
            if (resp != null && resp.getResult() != Constants.SUCC) {
                LogUtils.d("NetLogisticsUpdate.uploadLocation", resp.getDesc());
            }
            return resp;
        } catch (NetGetException e) {
            e.printStackTrace();
            LogUtils.e("NetLogisticsUpdate.uploadLocation", e);
        }
        return null;
    }
}
