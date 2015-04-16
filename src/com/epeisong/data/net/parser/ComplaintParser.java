package com.epeisong.data.net.parser;

import java.util.ArrayList;
import java.util.List;

import com.epeisong.logistics.proto.Base.ProtoEComplaint;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.model.Complaint;

public class ComplaintParser {
	
	public static Complaint parse(ProtoEComplaint proto){
		Complaint complaint = new Complaint();
		complaint.setId(proto.getId());
		complaint.setByName(proto.getRespondentName());
		complaint.setByNameId(String.valueOf(proto.getRespondentId()));
		complaint.setName(proto.getComplainantName());
		complaint.setNameId(String.valueOf(proto.getComplainantId()));
		complaint.setOwner_id(String.valueOf(proto.getCustomerServiceId()));
		complaint.setContent(proto.getContent());
		complaint.setStatus(proto.getStatus());
		complaint.setResult(proto.getResult());
		return complaint;
	}
	
	public static Complaint parseSingleComplaint(CommonLogisticsResp.Builder resp) {
        if (resp == null) {
            return null;
        }
        List<ProtoEComplaint> list = resp.getComplaintList();
        if (list == null || list.isEmpty()) {
            return null;
        }
        return parse(list.get(0));
    }
	
	public static List<Complaint> parseList(CommonLogisticsResp.Builder resp){
		List<Complaint> result = new ArrayList<Complaint>();
		List<ProtoEComplaint> list = resp.getComplaintList();
		for(ProtoEComplaint item : list){
			result.add(parse(item));
		}
		return result;
	}

}
