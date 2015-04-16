package com.epeisong.ui.activity;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.net.NetGuarantee;
import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.common.Properties;
import com.epeisong.logistics.proto.Eps.GuaranteeReq;
import com.epeisong.logistics.proto.Eps.GuaranteeResp;
import com.epeisong.model.Guarantee;
import com.epeisong.utils.android.AsyncTask;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * 担保产品详情
 * @author Jack
 *
 */
public class ProductDetailActivity extends BaseActivity implements OnClickListener {
	public static final String EXTRA_GUARANTEE = "guarantee";
	public static final String EXTRA_GUARDIS = "disable";
	private Guarantee mGuarantee;
	
	private TextView tv_productmoney, tv_productinfor, tv_name, tv_type;
	private ImageView iv_img01, iv_img02;
    private DisplayImageOptions mDisplayImageOptions;
    private Boolean disaBoolean;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        Intent mIntent = getIntent();
        mGuarantee = (Guarantee) mIntent.getSerializableExtra(EXTRA_GUARANTEE);
        String guaString = mIntent.getStringExtra(EXTRA_GUARDIS);
        if( guaString!=null && mIntent.getStringExtra(EXTRA_GUARDIS).equals("disable"))
        	disaBoolean = true;
        else
        	disaBoolean = false;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_product_detail);
		
		TextView tvView = (TextView) findViewById(R.id.tv_publisher);
		tv_type = (TextView) findViewById(R.id.tv_type);
		tv_productmoney = (TextView) findViewById(R.id.tv_productmoney);
		tv_productinfor = (TextView) findViewById(R.id.tv_productinfor);
		tv_name = (TextView) findViewById(R.id.tv_name);
		iv_img01 = (ImageView) findViewById(R.id.iv_img01);
		iv_img02 = (ImageView) findViewById(R.id.iv_img02);
		
		tvView.setText(mGuarantee.GetPublisher());
		if(mGuarantee.getType()==0)
		{
			tv_type.setText("车源货源保证金");//String.valueOf(mGuarantee.getType()));
		
		}
		
		tv_productmoney.setText(String.valueOf(mGuarantee.getAccount()/100)+"元");
		tv_productinfor.setText(mGuarantee.getIntroduce());
		tv_name.setText(mGuarantee.getName());
		
        mDisplayImageOptions = new DisplayImageOptions.Builder().showImageOnFail(R.drawable.user_logo_default)
                .cacheOnDisk(true).cacheInMemory(true).bitmapConfig(Config.RGB_565).build();
        if (!TextUtils.isEmpty(mGuarantee.getMark_url1())) {
            ImageLoader.getInstance().displayImage(mGuarantee.getMark_url1(), iv_img01, mDisplayImageOptions);
        }
        if (!TextUtils.isEmpty(mGuarantee.getMark_url2())) {
            ImageLoader.getInstance().displayImage(mGuarantee.getMark_url2(), iv_img02, mDisplayImageOptions);
        }
        if(disaBoolean)
        	findViewById(R.id.bt_disable).setVisibility(View.GONE);
        else {
			
        if(mGuarantee.getStatus()!=Properties.GUARANTEE_PRODUCT_STATUS_NORMAL)
        	findViewById(R.id.bt_disable).setVisibility(View.GONE);
        else
        	findViewById(R.id.bt_disable).setOnClickListener(this);
        }
	}
	
	protected TitleParams getTitleParams() {
		return new TitleParams(getDefaultHomeAction(), "担保产品详情", null).setShowLogo(false);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.bt_disable:
	        //final int curStatus = mGuarantee.getStatus();

	        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
	        	@Override
	        	protected Boolean doInBackground(Void... params) {
	        		NetGuarantee net = new NetGuarantee() {
	        			@Override
	        			protected int getCommandCode() {
	        				return CommandConstants.UPDATE_GUARANTEE_PRODUCT_STATUS_REQ;
	        			}
	        			@Override
	        			protected boolean onSetRequest(GuaranteeReq.Builder req) {
	        				req.setProductId(Integer.parseInt(mGuarantee.getId()));
	        				return true;
	        			}
	        		};

	        		try {
	        			GuaranteeResp.Builder resp = net.request();
	        			if (resp == null) {
	        				return null;
	        			}
	        			return true;
	        		} catch (NetGetException e) {
	        			e.printStackTrace();
	        		}
	        		return null;                	

	        	}

	        	protected void onPostExecute(Boolean result) {
	        		if (result) {
		                Intent data = new Intent();
		                setResult(Activity.RESULT_OK, data);
		                finish();
	        		}
	        		else {
					}
	        	}
	        };
	        task.execute();
	        
	        
//	        NetGuaranteeUpdateStatus net = new NetGuaranteeUpdateStatus(this, mGuarantee) {
//	            @Override
//	            protected int getCommandCode() {
//	                return CommandConstants.UPDATE_GUARANTEE_PRODUCT_STATUS_REQ;
//	            }
//	            
//	            @Override
//	            protected boolean onSetRequest(GuaranteeReq.Builder req) {
//	                req.setProductId(Integer.parseInt(mGuarantee.getId()));
//	                return true;
//	            }
//	        };
//	        net.request(new OnNetRequestListenerImpl<Eps.GuaranteeResp.Builder>() {
//
//				@Override
//				public void onSuccess(Eps.GuaranteeResp.Builder response) {
//	                Intent data = new Intent();
//	                setResult(Activity.RESULT_OK, data);
//	                finish();
//				}
//			});
			break;
		default:
			break;
		}
	}
}
