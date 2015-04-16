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
import com.epeisong.ui.activity.TransferWithdrawalActivity;
import com.epeisong.ui.activity.WithdrawalActivity;
import com.epeisong.utils.SystemUtils;
/**
 * 提现角色(任务页面)
 * @author gnn
 *
 */
public class TaskWalletCashFragment extends Fragment implements OnClickListener {
	
    public View root;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	root = SystemUtils.inflate(R.layout.activity_task_wallet_cash);
    	
    	root.findViewById(R.id.ll_tixian).setOnClickListener(this); //转账提现
    	root.findViewById(R.id.ll_tixian_manager).setOnClickListener(this); //提现管理
        
        return root;
    }

    @Override
    public void onClick(View v) {
    	Class<? extends Activity> clazz = null;
        switch (v.getId()) {
        case R.id.ll_tixian:
        	clazz = TransferWithdrawalActivity.class;
            break;
        case R.id.ll_tixian_manager:
        	clazz = WithdrawalActivity.class;
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
