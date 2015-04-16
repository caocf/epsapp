package com.epeisong.data.net.parser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.epeisong.logistics.proto.CustomServiceTask.CustomServiceTaskResp;
import com.epeisong.logistics.proto.Transaction.ProtoCustomServiceTask;
import com.epeisong.net.ws.utils.CustomServiceTask;
import com.epeisong.utils.DateUtil;

public class CustomServiceParser {
	
	public static CustomServiceTask parse(ProtoCustomServiceTask proto){
		CustomServiceTask cp = new CustomServiceTask();
		cp.setId(proto.getId());
		cp.setLogisticsId(proto.getLogisticsId());
		cp.setDetail(proto.getDetail());
		cp.setType(proto.getType());
		cp.setUserAccount(proto.getUserAccount());
		cp.setUserName(proto.getUserName());
		cp.setStatus(proto.getStatus());
		cp.setContactTel(proto.getContactTel());
		cp.setSyncIndex(proto.getSyncIndex());
		cp.setResult(proto.getResult());
		cp.setCreateDate(new Date(proto.getCreateDate()));
		cp.setUpdateDate(new Date(proto.getUpdateDate()));
		return cp;
	}
	
	public static List<CustomServiceTask> parseList(CustomServiceTaskResp.Builder resp){
		List<CustomServiceTask> result = new ArrayList<CustomServiceTask>();
		List<ProtoCustomServiceTask> list = resp.getCustomServiceTasksList();
		for(ProtoCustomServiceTask item : list){
			result.add(parse(item));
		}
		return result;
	}

}
