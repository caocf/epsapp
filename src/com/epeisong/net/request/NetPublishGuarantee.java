package com.epeisong.net.request;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.common.Properties;
import com.epeisong.logistics.proto.Base.ProtoEGuaranteeProduct;
import com.epeisong.logistics.proto.Eps.GuaranteeReq;
import com.epeisong.logistics.proto.Eps.GuaranteeResp;
import com.epeisong.model.Guarantee;
import com.epeisong.ui.activity.ProductManaActivity;
import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage;

/**
 * 发布担保信息
 * 
 * @author Jack
 * 
 */
public class NetPublishGuarantee extends NetRequestorAsync<GuaranteeReq.Builder, GuaranteeResp.Builder> {

    private Guarantee mGuarantee;
    private Bitmap mbmp1, mbmp2;

    public NetPublishGuarantee(ProductManaActivity activity, Guarantee f, Bitmap bmp1, Bitmap bmp2) {
        super(activity);
        mGuarantee = f;
        mbmp1 = bmp1;
        mbmp2 = bmp2;
    }

    @Override
    protected int getCommandCode() {
        return CommandConstants.CREATE_GUARANTEE_PRODUCT_REQ;
    }

    @Override
    protected String getPendingMsg() {
        return "发布中...";
    }

    @Override
    protected GeneratedMessage.Builder<GuaranteeReq.Builder> getRequestBuilder() {
    	GuaranteeReq.Builder req = GuaranteeReq.newBuilder();
    	ProtoEGuaranteeProduct.Builder builder = ProtoEGuaranteeProduct.newBuilder();
    	builder.setGuaranteeId(Integer.valueOf(mGuarantee.getId()));
    	builder.setGuaranteeType(mGuarantee.getGuaType());
    	builder.setProductAmount(mGuarantee.getAccount());
    	builder.setProductName(mGuarantee.getName());
    	builder.setProductDesc(mGuarantee.getIntroduce());
    	builder.setGuaranteeName(mGuarantee.GetPublisher());
    	builder.setProductType(mGuarantee.getType());
    	builder.setStatus(Properties.GUARANTEE_PRODUCT_STATUS_NORMAL);
    	
    	
        ByteArrayOutputStream out;
        byte[] bytes;
        ByteString bs;
        out = new ByteArrayOutputStream();
        mbmp1.compress(CompressFormat.PNG, 100, out);
        bytes = out.toByteArray();
        bs = ByteString.copyFrom(bytes);
        req.setOwnerLogo(bs);
    	builder.setOwnerLogo(bs.toString());
    	req.setLogoFileType(".png");
    	
        out = new ByteArrayOutputStream();
        mbmp2.compress(CompressFormat.PNG, 100, out);
        bytes = out.toByteArray();
        bs = ByteString.copyFrom(bytes);
        req.setOtherLogo(bs);
    	builder.setOtherLogo(bs.toString());
    	
    	req.setGuaranteeProduct(builder);
    	req.setGuaranteeId(Integer.valueOf(mGuarantee.getId()));

    	return req;

    }

	@Override
	protected String getDesc(
			com.epeisong.logistics.proto.Eps.GuaranteeResp.Builder resp) {
		// TODO Auto-generated method stub
		return resp.getDesc();
	}

	@Override
	protected String getResult(
			com.epeisong.logistics.proto.Eps.GuaranteeResp.Builder resp) {
		// TODO Auto-generated method stub
		return resp.getResult();
	}


}
