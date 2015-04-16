package com.epeisong.data.net.parser;

import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;

import com.epeisong.logistics.proto.Base.ProtoEBizLogistics;
import com.epeisong.logistics.proto.Base.ProtoEBizLogisticsSubscribe;
import com.epeisong.logistics.proto.Base.ProtoRMarketMember;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.LogisticAndRMarketMember;
import com.epeisong.logistics.proto.Eps.ShortLogistics;
import com.epeisong.logistics.proto.Eps.UserLoginResp;
import com.epeisong.model.MarketMember;
import com.epeisong.model.User;
import com.epeisong.model.UserRole;

/**
 * 用户大小角色表合并后的解析
 * @author poet
 *
 */
public class UserParser {

    public static UserRole parseUserRole(ProtoEBizLogistics logi) {
        UserRole role = new UserRole();
        role.setRegionCode(logi.getServeRegionCode());
        role.setRegionName(logi.getServeRegionName());
        role.setCurrentRegionCode(logi.getCurrentRegionCode());
        role.setCurrentRegionName(logi.getCurrentRegionName());
        role.setCurrent_longitude(logi.getCurrentLongitude());
        role.setCurrent_latitude(logi.getCurrentLatitude());
        role.setLineStartCode(logi.getRouteCodeA());
        role.setLineStartName(logi.getRouteNameA());
        role.setLineEndCode(logi.getRouteCodeB());
        role.setLineEndName(logi.getRouteNameB());
        role.setValidityCode(logi.getPeriodOfValidity());
        role.setValidityName(logi.getPeriodOfValidityDesc());
        role.setInsuranceCode(logi.getInsuranceType());
        role.setInsuranceName(logi.getInsuranceTypeName());
        role.setDeviceCode(logi.getEquipmentType());
        role.setDeviceName(logi.getEquipmentTypeName());
        role.setDepotCode(logi.getStorageType());
        role.setDepotName(logi.getStorageTypeName());
        role.setPackCode(logi.getPackageType());
        role.setPackName(logi.getPackageTypeName());
        role.setTruckLengthCode(logi.getVehicleLengthCode());
        role.setTruckLengthName(logi.getVehicleLengthName());
        role.setTruckTypeCode(logi.getVehicleType());
        role.setTruckTypeName(logi.getVehicleTypeName());
        role.setLoadTon(logi.getMaxKilogram() / 1000);
        role.setGoodsTypeCode(logi.getGoodsType());
        role.setGoodsTypeName(logi.getGoodsTypeName());
        role.setTransportTypeCode(logi.getTransportTypeCode());
        role.setTransportTypeName(logi.getTransportTypeName());
        role.setWeight(logi.getWeightScore());
        role.setIs_full_loaded(logi.getIsFullLoaded());

        role.setRecommendedCount(logi.getRecommendedCount());
        role.setNotRecommendedCount(logi.getNotRecommendedCount());

        role.setrangedeliver(logi.getRangeToDeliver());
        role.setRange_not_to_delivervarchar(logi.getRangeNotToDeliver());

        return role;
    }

    public static User parse(ProtoEBizLogistics logi) {
        User user = new User();
        user.setId(String.valueOf(logi.getId()));
        if (!TextUtils.isEmpty(logi.getAccountName())) {
            user.setPhone(logi.getAccountName());
            user.setAccount_name(logi.getAccountName());
        }
        user.setContacts_name(logi.getContact());
        user.setContacts_phone(logi.getMobile1());
        user.setContacts_telephone(logi.getTelephone1());
        user.setEmail(logi.getEmail());
        user.setLogo_url(logi.getLogo());
        user.setPinyin(logi.getPinyin());
        user.setQq(logi.getQq());
        user.setShow_name(logi.getName());
        user.setWechat(logi.getWeixin());
        user.setAddress(logi.getAddress());
        user.setRegion(logi.getRegionName());
        user.setRegion_code(logi.getRegionCode());
        user.setSelf_intro(logi.getSelfIntroduction());
        user.setIs_hide(logi.getIsHide());
        user.setStar_level(logi.getStarLevel());
        user.setUser_type_code(logi.getLogisticsType());
        user.setUser_type_name(logi.getLogisticsTypeName());
        user.setUserRole(parseUserRole(logi));
        return user;
    }

    public static MarketMember parse(LogisticAndRMarketMember logi) {
        ProtoEBizLogistics logistic = logi.getLogistic();
        ProtoRMarketMember logiMember = logi.getRMarketMember();
        User user = parse(logistic);
        MarketMember mm = new MarketMember();
        mm.setUser(user);
        mm.setStatus(logiMember.getMemberStatus());
        mm.setIsBanned(logiMember.getIsBanned());
        mm.setId(String.valueOf(logiMember.getId()));
        return mm;
    }

    // public static User parse(LogisticAndRMarketMember logi) {
    // User user = new User();
    // logi.getRMarketMember().getId();
    // user.setStatus(logi.getRMarketMember().getMemberStatus());
    // user.setAddress(logi.getLogistic().getAddress());
    // user.setId(String.valueOf(logi.getLogistic().getId()));
    // user.setContacts_name(logi.getLogistic().getContact());
    // user.setContacts_phone(logi.getLogistic().getMobile1());
    // user.setContacts_telephone(logi.getLogistic().getTelephone1());
    // user.setEmail(logi.getLogistic().getEmail());
    // user.setLogo_url(logi.getLogistic().getLogo());
    // user.setPinyin(logi.getLogistic().getPinyin());
    // user.setQq(logi.getLogistic().getQq());
    // user.setShow_name(logi.getLogistic().getName());
    // user.setWechat(logi.getLogistic().getWeixin());
    // user.setAddress(logi.getLogistic().getAddress());
    // user.setRegion(logi.getLogistic().getRegionName());
    // user.setRegion_code(logi.getLogistic().getRegionCode());
    // user.setSelf_intro(logi.getLogistic().getSelfIntroduction());
    // user.setIs_hide(logi.getLogistic().getIsHide());
    // user.setStar_level(logi.getLogistic().getStarLevel());
    // user.setUser_type_code(logi.getLogistic().getLogisticsType());
    // user.setUser_type_name(logi.getLogistic().getLogisticsTypeName());
    // user.setUserRole(parseUserRole(logi.getLogistic()));
    // return user;
    // }

    public static List<User> parse(CommonLogisticsResp.Builder resp) {
        List<ProtoEBizLogistics> logiList = resp.getBizLogisticsList();
        List<User> result = new ArrayList<User>();
        for (ProtoEBizLogistics logi : logiList) {
            result.add(parse(logi));
        }
        return result;
    }

    // public static List<User> parseMember(CommonLogisticsResp.Builder resp) {
    // List<LogisticAndRMarketMember> logiList =
    // resp.getLogisticAndRMarketMemberList();
    // List<User> result = new ArrayList<User>();
    // for (LogisticAndRMarketMember logi : logiList) {
    // result.add(parse(logi));
    // }
    // return result;
    // }

    public static List<MarketMember> parseMember(CommonLogisticsResp.Builder resp) {
        List<LogisticAndRMarketMember> logiList = resp.getLogisticAndRMarketMemberList();
        List<MarketMember> result = new ArrayList<MarketMember>();
        for (LogisticAndRMarketMember logi : logiList) {
            result.add(parse(logi));
        }
        return result;
    }

    public static User parseSingleUser(CommonLogisticsResp.Builder resp) {
        if (resp == null) {
            return null;
        }
        List<ProtoEBizLogistics> list = resp.getBizLogisticsList();
        if (list == null || list.isEmpty()) {
            return null;
        }
        return parse(list.get(0));
    }

    public static User parse(UserLoginResp.Builder resp) {
        String accountName = resp.getAccountName();
        User user = parse(resp.getBizLogistics());
        user.setAccount_name(accountName);
        ProtoEBizLogisticsSubscribe config = resp.getBizLogisticsSubscribe();
        if (config != null) {
            user.setReceive_contacts_freight(config.getWhetherReceiveFreightsOfFriends());
        }
        return user;
    }

    public static User parse(ShortLogistics logi) {
        User user = new User();
        user.setId(String.valueOf(logi.getLogisticsId()));
        user.setShow_name(logi.getName());
        user.setLogo_url(logi.getLogo());
        return user;
    }

}
