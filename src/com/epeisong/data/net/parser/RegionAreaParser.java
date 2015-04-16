package com.epeisong.data.net.parser;

import java.util.ArrayList;
import java.util.List;

import com.epeisong.logistics.proto.Base.ProtoRMarketRegionsContained;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.model.RegionArea;

public class RegionAreaParser {
	
	public static RegionArea parse(ProtoRMarketRegionsContained proto){
		RegionArea area = new RegionArea();
		area.setId(proto.getId());
		area.setRegionCode(proto.getRegionCode());
		area.setRegionName(proto.getRegionName());
		return area;
	}
	
	public static List<RegionArea> parseList(CommonLogisticsResp.Builder resp){
		List<RegionArea> result = new ArrayList<RegionArea>();
		List<ProtoRMarketRegionsContained> list = resp.getRMarketRegionsContainedList();
		for(ProtoRMarketRegionsContained item: list){
			result.add(parse(item));
		}
		return result;
	}

}
