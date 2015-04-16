package com.epeisong.data.net.parser;

import java.util.ArrayList;
import java.util.List;

import com.epeisong.logistics.proto.Base.ProtoEGuaranteeProduct;
import com.epeisong.logistics.proto.Base.ProtoEQuestion;
import com.epeisong.logistics.proto.Eps.GuaranteeResp;
import com.epeisong.logistics.proto.Eps.GuaranteeRespOrBuilder;
import com.epeisong.logistics.proto.Eps.QuestionResp;
import com.epeisong.model.Guarantee;
import com.epeisong.model.Question;

/**
 * 担保信息解析
 * 
 * @author poet
 * 
 */
public class GuaranteeParser {

	public static Guarantee parse(ProtoEGuaranteeProduct guaranteeproduct) {
		Guarantee g = new Guarantee();
		g.setId(String.valueOf(guaranteeproduct.getId()));
		g.setAccount(guaranteeproduct.getProductAmount());
		g.setGuaType(guaranteeproduct.getGuaranteeType());
		g.setIntroduce(guaranteeproduct.getProductDesc());
		g.setName(guaranteeproduct.getProductName());
		g.setPublisher(guaranteeproduct.getGuaranteeName());
		g.setType(guaranteeproduct.getProductType());
		g.setStatus(guaranteeproduct.getStatus());
		g.setCustomerStatus(guaranteeproduct.getStatus());
		
		g.setMark_url1(guaranteeproduct.getOwnerLogo());
		g.setMark_url2(guaranteeproduct.getOtherLogo());
		return g;

	}

	public static List<Guarantee> parse(GuaranteeResp.Builder resp) {
		List<ProtoEGuaranteeProduct> list = resp.getGuaranteeProductsList();
		List<Guarantee> result = new ArrayList<Guarantee>();
		for (ProtoEGuaranteeProduct item : list) {
			result.add(parse(item));
		}
		return result;
	}
}
