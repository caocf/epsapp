package com.epeisong.ui.fragment;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.epeisong.R;
import com.epeisong.data.dao.PointDao.PointObserver;
import com.epeisong.logistics.common.Properties;
import com.epeisong.model.Point;
import com.epeisong.model.Point.PointCode;
import com.epeisong.net.ws.utils.EnumWalletType;
import com.epeisong.ui.activity.ComplaintActivity;
import com.epeisong.ui.activity.GuaranteeActivity;
import com.epeisong.ui.activity.PaymentActivity;
import com.epeisong.ui.activity.ProductManaActivity;
import com.epeisong.ui.activity.TradeManaActivity;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;

/**
 * 任务billfee
 * 
 * @author Jack
 * 
 */

public class TaskGuaranteeFragment extends Fragment implements OnClickListener,
        PointObserver  {

    public View root;
    private ImageView iv_redmanage;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = SystemUtils.inflate(R.layout.activity_tasknew2);
        
        root.findViewById(R.id.rl_complaint).setOnClickListener(this);//申诉处理
        //root.findViewById(R.id.rl_payment).setOnClickListener(this);//转账支付
        root.findViewById(R.id.lv_guapayment).setOnClickListener(this);//担保账户转账
        root.findViewById(R.id.lv_teepayment).setOnClickListener(this);//交易账户转账

        root.findViewById(R.id.lv_accountmana).setOnClickListener(this);//担保账户管理
        root.findViewById(R.id.lv_costmana).setOnClickListener(this);//交易费管理
        root.findViewById(R.id.lv_accountinfor).setOnClickListener(this);//担保账户日志
        root.findViewById(R.id.lv_costinfor).setOnClickListener(this);//交易费日志
        root.findViewById(R.id.rl_guarantee).setOnClickListener(this);//担保产品管理
        
        iv_redmanage = (ImageView) root.findViewById(R.id.iv_redmanage);
        //PointDao.getInstance().addObserver(PointCode.Code_Task_DealOrder, this);

        return root;
    }

    @Override
    public void onPointChange(Point p) {
        PointCode pointCode = PointCode.convertFromValue(p.getCode());
        switch (pointCode) {
//        case Code_Task_DealOrder:
//            iv_redmanage.setVisibility(p.isShow() ? View.VISIBLE : View.GONE);
//            break;
		default:
			break;
        }
    }

    @Override
    public void onClick(View v) {
    	Class<? extends Activity> clazz = null;
    	int puttypenum=-1;
        switch (v.getId()) {
        case R.id.rl_complaint:
        	//ToastUtils.showToast("申诉处理");
        	clazz = ComplaintActivity.class;
            break;
        case R.id.lv_guapayment:
        	//ToastUtils.showToast("担保账户转账");
        	clazz = PaymentActivity.class;
        	puttypenum = EnumWalletType.BOND.getValue();//PaymentActivity.PAYMENT_TYPE_GUARANTEE;
            break;
        case R.id.lv_teepayment:
        	//ToastUtils.showToast("交易账户转账");
        	clazz = PaymentActivity.class;
        	puttypenum = EnumWalletType.TRANSACTION.getValue();
            break;
        case R.id.lv_accountmana:
        	//ToastUtils.showToast("担保账户管理");
        	clazz = GuaranteeActivity.class;
            break;    	
        case R.id.lv_costmana:
        	//ToastUtils.showToast("交易费管理");
        	clazz = TradeManaActivity.class;
            break;    	
        case R.id.lv_accountinfor:
        	ToastUtils.showToast("担保账户日志");
        	//clazz = PublishBulletinActivity.class;
            break;    	
        case R.id.lv_costinfor:
        	ToastUtils.showToast("交易费日志");
        	//clazz = PublishBulletinActivity.class;
            break;
        case R.id.rl_guarantee:
        	//ToastUtils.showToast("担保产品管理");
        	clazz = ProductManaActivity.class;
            break;
        default:
            break;
        }
        
        if( clazz != null ) {
        	Intent intent = new Intent(getActivity(), clazz);
        	if(puttypenum!=-1)
        		intent.putExtra(PaymentActivity.PAYMENT_TYPE_STRING, puttypenum);
        	startActivity(intent);
        }
    }
}
