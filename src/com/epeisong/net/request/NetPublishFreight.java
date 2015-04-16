package com.epeisong.net.request;

import com.epeisong.base.activity.XBaseActivity;
import com.epeisong.data.dao.UserDao;
import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.proto.Base.ProtoEFreight;
import com.epeisong.logistics.proto.Eps.FreightReq;
import com.epeisong.logistics.proto.Eps.FreightResp;
import com.epeisong.logistics.proto.Eps.FreightResp.Builder;
import com.epeisong.model.Freight;
import com.epeisong.model.User;
import com.epeisong.utils.LogUtils;
import com.google.protobuf.GeneratedMessage;

/**
 * 发布车源、货源
 * 
 * @author poet
 * 
 */
public class NetPublishFreight extends NetRequestorAsync<FreightReq.Builder, FreightResp.Builder> {

    private Freight mFreight;

    public NetPublishFreight(XBaseActivity activity, Freight f) {
        super(activity);
        mFreight = f;
    }

    @Override
    protected int getCommandCode() {
        return CommandConstants.CREATE_FREIGHT_REQ;
    }

    @Override
    protected String getPendingMsg() {
        return "发布中...";
    }

    @Override
    protected String getDesc(Builder resp) {
        // TODO Auto-generated method stub
        return resp.getDesc();
    }

    @Override
    protected String getResult(Builder resp) {
        // TODO Auto-generated method stub
        return resp.getResult();
    }

    @Override
    protected GeneratedMessage.Builder<FreightReq.Builder> getRequestBuilder() {
        FreightReq.Builder req = FreightReq.newBuilder();
        ProtoEFreight.Builder b = ProtoEFreight.newBuilder();
        b.setFreightType(mFreight.getType());
        b.setStartAddress(mFreight.getStart_region());
        b.setStartRegionCode(mFreight.getStart_region_code());
        b.setDestAddress(mFreight.getEnd_region());
        b.setDestRegionCode(mFreight.getEnd_region_code());
        // b.setTon((int) mFreight.getGoods_ton());
        b.setTon(mFreight.getGoods_ton());
        b.setSquare(mFreight.getGoods_square());
        if (mFreight.getTruck_length_code() > 0) {
            b.setVehicleLengthCode(mFreight.getTruck_length_code());
            b.setVehicleLengthName(mFreight.getTruck_length_name());
        }
        if (mFreight.getTruck_type_code() > 0) {
            b.setVehicleTypeCode(mFreight.getTruck_type_code());
            b.setVehicleTypeName(mFreight.getTruck_type_name());
        }
        if (mFreight.getType() == Freight.TYPE_TRUCK) {
            b.setRemainingSpace((int) mFreight.getTruck_spare_meter());
        } else {
            b.setGoodsTypeName(mFreight.getGoods_type_name());
            b.setGoodsType(mFreight.getGoods_type());
            b.setOverloadType(mFreight.getGoods_exceed());
        }
        b.setCanPostToScreen(mFreight.getDistribution_market());
        b.setAgencyFee(mFreight.getInfo_cost() * 100);
        LogUtils.t("NetPublishFreight.freight_type:" + b.getFreightType());
        req.setFreight(b);
        User u = UserDao.getInstance().getUser();
        req.setLogisticId(Integer.parseInt(u.getId()));
        req.setLogisticName(u.getShow_name());
        return req;
    }

}
