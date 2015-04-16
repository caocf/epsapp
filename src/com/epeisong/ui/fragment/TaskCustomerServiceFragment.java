package com.epeisong.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.epeisong.R;
import com.epeisong.ui.activity.ComplaintHandlingActivity;
import com.epeisong.ui.activity.CustomerProblemActivity;
import com.epeisong.utils.SystemUtils;
/**
 * 客户角色(任务页面)
 * @author gnn
 *
 */
public class TaskCustomerServiceFragment extends Fragment implements OnClickListener {
	
    public View root;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	root = SystemUtils.inflate(R.layout.activity_customer_service);
    	
    	root.findViewById(R.id.ll_complaints).setOnClickListener(this); //投诉处理
    	root.findViewById(R.id.ll_problem).setOnClickListener(this); //客户问题
    	root.findViewById(R.id.ll_add_user).setOnClickListener(this); // 添加用户
        
        return root;
    }

    @Override
    public void onClick(View v) {
    	Class<? extends Activity> clazz = null;
        switch (v.getId()) {
        case R.id.ll_complaints:
        	clazz = ComplaintHandlingActivity.class;
            break;
        case R.id.ll_problem:
        	clazz = CustomerProblemActivity.class;
            break;
        case R.id.ll_add_user:
        	break;
        default:
            break;
        }
        
        if( clazz != null ) {
        	Intent intent = new Intent(getActivity(), clazz);
        	startActivity(intent);
        }
    }

}
