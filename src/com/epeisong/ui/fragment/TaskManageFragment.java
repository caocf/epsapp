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
import com.epeisong.ui.activity.ManaAddUserActivity;
import com.epeisong.ui.activity.ManaManaUserActivity;
import com.epeisong.ui.activity.ManageAddUserActivity;
import com.epeisong.ui.activity.PublishBulletinActivity;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;

/**
 * 平台管理
 * 
 * @author Jack
 * 
 */

public class TaskManageFragment extends Fragment implements OnClickListener {
	
    public View root;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = SystemUtils.inflate(R.layout.activity_task_manage);
        
        root.findViewById(R.id.rl_adduser).setOnClickListener(this);//添加用户
        root.findViewById(R.id.rl_manauser).setOnClickListener(this);//管理用户
        root.findViewById(R.id.lv_problem).setOnClickListener(this);//常见问题管理
        root.findViewById(R.id.lv_publish).setOnClickListener(this);//发布公告
        
        return root;
    }

    @Override
    public void onClick(View v) {
    	Class<? extends Activity> clazz = null;
        switch (v.getId()) {
        case R.id.rl_adduser:
        	//clazz = ManaAddUserActivity.class;
        	clazz = ManageAddUserActivity.class;
            break;
        case R.id.rl_manauser:
        	//ToastUtils.showToast("管理用户");
        	clazz = ManaManaUserActivity.class;
            break;
        case R.id.lv_problem:
        	ToastUtils.showToast("常见问题管理");
        	//clazz = ProductManaActivity.class;
            break;    	
        case R.id.lv_publish:
        	//ToastUtils.showToast("发布公告");
        	clazz = PublishBulletinActivity.class;
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
