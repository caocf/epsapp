package com.epeisong.data.net.parser;

import java.util.ArrayList;
import java.util.List;

import com.epeisong.logistics.proto.Base.ProtoEFreight;
import com.epeisong.logistics.proto.Base.ProtoEMarketScreen;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.FreightResp;
import com.epeisong.model.Freight;

/**
 * 车源货源解析
 * 
 * @author poet
 * 
 */
public class FreightParser {

    public static Freight parse(ProtoEFreight freight) {
        Freight f = new Freight();
        f.setId(String.valueOf(freight.getId()));
        f.setType(freight.getFreightType());
        f.setUser_id(String.valueOf(freight.getOwnerId()));
        f.setOwner_name(freight.getOwnerName());
        f.setCreate_time(freight.getCreateDate());
        f.setStart_region(freight.getStartAddress());
        f.setStart_region_code(freight.getStartRegionCode());
        f.setEnd_region(freight.getDestAddress());
        f.setEnd_region_code(freight.getDestRegionCode());
        f.setTruck_length_code(freight.getVehicleLengthCode());
        f.setTruck_length_name(freight.getVehicleLengthName());
        f.setTruck_type_name(freight.getVehicleTypeName());
        f.setTruck_type_code(freight.getVehicleTypeCode());
        f.setGoods_ton(freight.getTon());
        f.setGoods_square(freight.getSquare());
        if (f.getType() == Freight.TYPE_TRUCK) {
            f.setTruck_spare_meter(freight.getRemainingSpace());
        } else {
            f.setGoods_exceed(freight.getOverloadType());
            f.setGoods_type_name(freight.getGoodsTypeName());
            f.setGoods_type(freight.getGoodsType());
        }
        f.setInfo_cost(freight.getAgencyFee() / 100);
        f.setFreight_cost(freight.getFreightFee() / 100);
        f.setStatus(freight.getStatus());
        f.setDistribution_market(freight.getCanPostToScreen());
        f.setPushTime(freight.getCreateDate());
        return f;
    }

    public static Freight parse(ProtoEMarketScreen freight) {
        Freight f = new Freight();
        f.setId(String.valueOf(freight.getFreightId()));
        f.setType(freight.getFreightType());
        f.setUser_id(String.valueOf(freight.getFreightOwnerId()));
        f.setOwner_name(freight.getFreightOwnerName());
        f.setCreate_time(freight.getCreateDate());
        f.setStart_region(freight.getStartAddress());
        f.setStart_region_code(freight.getStartRegionCode());
        f.setEnd_region(freight.getDestAddress());
        f.setEnd_region_code(freight.getDestRegionCode());
        f.setTruck_length_code(freight.getVehicleLengthCode());
        f.setTruck_length_name(freight.getVehicleLengthName());
        f.setTruck_type_name(freight.getVehicleTypeName());
        f.setTruck_type_code(freight.getVehicleTypeCode());
        f.setGoods_ton(freight.getTon());
        f.setGoods_square(freight.getSquare());
        f.setMarket_screen_freight_id(freight.getId());

        if (f.getType() == Freight.TYPE_TRUCK) {
            f.setTruck_spare_meter(freight.getRemainingSpace());
        } else {
            f.setGoods_exceed(freight.getOverloadType());
            f.setGoods_type_name(freight.getGoodsTypeName());
            f.setGoods_type(freight.getGoodsType());
        }
        f.setStatus(freight.getStatus());
        f.setDistribution_market(freight.getCanPostToScreen());
        return f;
    }

    public static List<Freight> parse(FreightResp.Builder resp) {
        List<ProtoEFreight> list = resp.getFreightList();
        List<Freight> result = new ArrayList<Freight>();
        for (ProtoEFreight item : list) {
            result.add(parse(item));
        }
        return result;
    }

    public static Freight parseSingle(FreightResp.Builder resp) {
        if (resp == null) {
            return null;
        }
        List<Freight> list = parse(resp);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public static List<Freight> parse(CommonLogisticsResp.Builder resp) {
        List<Freight> result = new ArrayList<Freight>();
        List<ProtoEMarketScreen> list = resp.getMarketScreenList();
        for (ProtoEMarketScreen item : list) {
            result.add(parse(item));
        }
        return result;
    }
}
