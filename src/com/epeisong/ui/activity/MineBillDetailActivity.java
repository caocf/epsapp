package com.epeisong.ui.activity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.view.CustomTitle.Action;
import com.epeisong.base.view.CustomTitle.ActionImpl;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.UserDao;
import com.epeisong.logistics.common.Properties;
import com.epeisong.model.User;
import com.epeisong.model.UserRole;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;

/**
 * 详情
 * 
 * @author jack
 * 
 */
public class MineBillDetailActivity extends BaseActivity implements OnClickListener
{
	private int typestr;
    public static final String EXTRA_INFO_MBD = "minebilltype";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	typestr = Integer.valueOf(getIntent().getStringExtra(MineBillDetailActivity.EXTRA_INFO_MBD));
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minebill_detail);
        if(typestr==1)
        {
        	findViewById(R.id.line_top).setVisibility(View.GONE);
        	findViewById(R.id.tv_place_js).setVisibility(View.GONE);
        }
        else {
        	findViewById(R.id.tv_updatebill).setVisibility(View.GONE);
        	findViewById(R.id.btn_jiameng).setVisibility(View.GONE);
        	Button btnButton = (Button) findViewById(R.id.btn_shanchu);
        	btnButton.setText("删除");
		}
        findViewById(R.id.btn_shanchu).setOnClickListener((OnClickListener) this);
        findViewById(R.id.btn_jiameng).setOnClickListener((OnClickListener) this);
        findViewById(R.id.tv_updatebill).setOnClickListener((OnClickListener) this);
        //TextView ddTextView = (TextView) findViewById(R.id.tv_place_content);

        
        //addForSome(null);
    }

    void addForSome(User mUser)
    {
    	mUser = UserDao.getInstance().getUser();
    	Map<String, String> map = new LinkedHashMap<String, String>();
    	map.put("类别:", mUser.getUser_type_name());
    	UserRole userRole = mUser.getUserRole();

    	if (userRole != null) {

    		switch (mUser.getUser_type_code()) {
    		case Properties.LOGISTIC_TYPE_STOWAGE_INFORMATION_DEPARTMENT:
    		case Properties.LOGISTIC_TYPE_LESS_THAN_TRUCKLOAD_AND_LINE:
    		case Properties.LOGISTIC_TYPE_EQUIPMENT_LEASING:
    		case Properties.LOGISTIC_TYPE_STORAGE:
    		case Properties.LOGISTIC_TYPE_PACKAGING:
    			map.put("所在地:", userRole.getRegionName());
    			break;
    		}

    		switch (mUser.getUser_type_code()) {
    		case Properties.LOGISTIC_TYPE_ENTIRE_VEHICLE:
    			map.put("常驻地区:", userRole.getRegionName());
    			break;
    		}
    		switch (mUser.getUser_type_code()) {
    		//case Properties.LOGISTIC_TYPE_ENTIRE_VEHICLE:
    		case Properties.LOGISTIC_TYPE_INSURANCE:
    		case Properties.LOGISTIC_TYPE_EXPRESS:
    		case Properties.LOGISTIC_TYPE_MOVE_HOUSE:
    		case Properties.LOGISTIC_TYPE_COURIER:
    		case Properties.LOGISTIC_TYPE_CITY_DISTRIBUTION:
    			// case LogisticsProducts.PRODUCTS_DANGEROUS:
    			// case LogisticsProducts.PRODUCTS_FRESHPERISHABLE:
    			// case LogisticsProducts.PRODUCTS_LARGETRANSPORT:
    			// case LogisticsProducts.PRODUCTS_REFRIGERATED:
    			map.put("服务区域:", userRole.getRegionName());
    			break;
    		}
    		switch (mUser.getUser_type_code()) {
    		case Properties.LOGISTIC_TYPE_ENTIRE_VEHICLE:
    			map.put("车长:", userRole.getTruckLengthName());
    			break;
    		}
    		switch (mUser.getUser_type_code()) {
    		case Properties.LOGISTIC_TYPE_ENTIRE_VEHICLE:
    			map.put("车型:", userRole.getTruckTypeName());
    			break;
    		}
    		switch (mUser.getUser_type_code()) {
    		case Properties.LOGISTIC_TYPE_ENTIRE_VEHICLE:
    		case Properties.LOGISTIC_TYPE_LESS_THAN_TRUCKLOAD_AND_LINE:

    			//快递 快递员 收发网点 同城配送
    			//赞不加， 这个传过来当作服务区域，老的服务区域就不传过来了
    			//		case Properties.LOGISTIC_TYPE_COURIER:
    			//		case Properties.LOGISTIC_TYPE_EXPRESS:
    			//		case Properties.LOGISTIC_TYPE_CITY_DISTRIBUTION:
    			//		case Properties.LOGISTIC_TYPE_PICK_UP_POINT:
    			map.put("线路:", userRole.getline());
    			break;
    		}
    		switch (mUser.getUser_type_code()) {
    		case Properties.LOGISTIC_TYPE_LESS_THAN_TRUCKLOAD_AND_LINE:
    			map.put("时效:", userRole.getValidityName());
    			break;
    		}
    		switch (mUser.getUser_type_code()) {
    		case Properties.LOGISTIC_TYPE_STOWAGE_INFORMATION_DEPARTMENT:
    			map.put("配载线路:", userRole.getline());
    			break;
    		}

    		switch (mUser.getUser_type_code()) {
    		case Properties.LOGISTIC_TYPE_EXPRESS:
    			map.put("险种:", userRole.getInsuranceName());
    			break;
    		}
    		switch (mUser.getUser_type_code()) {
    		case Properties.LOGISTIC_TYPE_EQUIPMENT_LEASING:
    			map.put("设备类别:", userRole.getDeviceName());
    			break;
    		}
    		switch (mUser.getUser_type_code()) {
    		case Properties.LOGISTIC_TYPE_STORAGE:
    			map.put("仓库类别:", userRole.getDepotName());
    			break;
    		}
    		switch (mUser.getUser_type_code()) {
    		case Properties.LOGISTIC_TYPE_PACKAGING:
    			map.put("包装类别:", userRole.getPackName());
    			break;
    		}
    		switch (mUser.getUser_type_code()) {
    		case Properties.LOGISTIC_TYPE_MARKET:
    			//map.put("选择城市:", userRole.getPackName());
    			map.put("地区:", userRole.getPackName());
    			break;
    		}
    	}
    	Paint paint = new Paint();
    	float strwidth = paint.measureText("选择城市:");
    	LinearLayout ll_attr = (LinearLayout) findViewById(R.id.ll_attr);
    	for (Map.Entry<String, String> entry : map.entrySet()) {
    		View item = SystemUtils
    				.inflate(R.layout.fragment_contacts_info_attr_item);
    		ll_attr.addView(item);
    		TextView tv_key = (TextView) item.findViewById(R.id.tv_key);
    		tv_key.setText(entry.getKey());
    		tv_key.setWidth((int) strwidth);
    		TextView tv_value = (TextView) item.findViewById(R.id.tv_value);
    		tv_value.setText(entry.getValue());
    	}
    }
    
    
    protected TitleParams getTitleParams() {
        
        if(typestr==1)
        {
        	List<Action> actions = new ArrayList<Action>();
        	actions.add(new ActionImpl() {
            @Override
            public View getView() {
                return getRightTextView("自己可见", R.drawable.shape_frame_black_content_trans);
            }

            @Override
            public void doAction(View v) {
            	ToastUtils.showToast("自己可见");
            }
        });
        return new TitleParams(getDefaultHomeAction(), "详情", actions).setShowLogo(false);
        }
        else {
            return new TitleParams(getDefaultHomeAction(), "详情", null).setShowLogo(false);

		}
    }

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_shanchu:
			ToastUtils.showToast("删除报价");
			break;
		case R.id.btn_jiameng:
			ToastUtils.showToast("发给加盟商");
			break;
		case R.id.tv_updatebill:
			if(typestr==1)
				ToastUtils.showToast("删除");
			ToastUtils.showToast("修改报价");
			break;
		default:
			break;
		}
	}


}
