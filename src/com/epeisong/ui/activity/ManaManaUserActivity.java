package com.epeisong.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Intent;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.adapter.EndlessAdapter;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.base.adapter.EndlessAdapter.OnLoadMoreListener;
import com.epeisong.base.view.TitleParams;
import com.epeisong.model.MarketMember;
import com.epeisong.model.User;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;

/**
 * 管理用户(平台角色--任务管理)
 * 
 * @author Jack
 * 
 */
public class ManaManaUserActivity extends BaseActivity implements OnClickListener, OnItemClickListener, OnLoadMoreListener {
    private static final int SIZE_LOAD_MORE = 10;
    private static final int SIZE_LOAD_FIRST = 10;
    
    protected MyAdapter mAdapter;
    private TextView view_empty;
    private ListView lv;
    private EditText et_search;
    
    @Override
    protected TitleParams getTitleParams() {
        return new TitleParams(getDefaultHomeAction(), "管理用户", null).setShowLogo(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_manage_mana_user);

        findViewById(R.id.btn_search).setOnClickListener(this);
        
        et_search = (EditText) findViewById(R.id.et_search);
        lv = (ListView) findViewById(R.id.lv);
        lv.setAdapter(mAdapter = new MyAdapter());
        lv.setOnItemClickListener(this);

        if (mAdapter != null) {
//            mEndlessAdapter = new EndlessAdapter(getActivity(), mAdapter);
//            mEndlessAdapter.setIsAutoLoad(true);
//            mEndlessAdapter.setOnLoadMoreListener(this);
//            lv.setAdapter(mEndlessAdapter);
        }

        view_empty = (TextView) findViewById(R.id.view_empty);
        view_empty.setText(null);
        lv.setEmptyView(view_empty);

        loadData(SIZE_LOAD_FIRST, "0", 0, true);
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_search:
        	String searchString = et_search.getText().toString();
        	if(TextUtils.isEmpty(searchString))
        		ToastUtils.showToast("请输入搜索关键字");
        	else
        		ToastUtils.showToast(searchString);
        	break;
        }

    }
    
    private void loadData(final int size, final String edge_id, final double weight, final boolean bFirst) {

    	AsyncTask<Void, Void, List<MarketMember>> task = new AsyncTask<Void, Void, List<MarketMember>>() {

			@Override
			protected List<MarketMember> doInBackground(Void... params) {

				List<MarketMember> result= new ArrayList<MarketMember>();
				return result;
				//return null;
			}
			
			@Override
			protected void onPostExecute(List<MarketMember> result) {
				if (!result.isEmpty()) {

					mAdapter.replaceAll(result);
                
				}
				else {
					
	                MarketMember marketMember = new MarketMember();
	                User user = new User();
	                user.setAccount_name("15195874389");
	                user.setShow_name("小李物流");
	                user.setUser_type_name("配载信息部");
	                marketMember.setUser(user);
	                result.add(marketMember);
	                mAdapter.replaceAll(result);
	                
//                mAdapter.clear();
   //             view_empty.setText("没有数据");
                return;
            }
				//super.onPostExecute(result);
			}
			
    		
    	};
    	task.execute();
    	    }

    @Override
    public void onStartLoadMore(EndlessAdapter adapter) {
        String id = mAdapter.getItem(mAdapter.getCount() - 1).getId();
        loadData(SIZE_LOAD_MORE, id, 0, false);
        
    }
    
    void InputPayPassword(final int index, final String name) {
    	
		final AlertDialog dialog;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);   
		LayoutInflater factory = LayoutInflater.from(this);  
		View view = factory.inflate(R.layout.dialog_paypassword, null);  
		// builder.setIcon(R.drawable.icon);  
		//builder.setTitle("自定义输入框");  
		final EditText et_password = (EditText) view.findViewById(R.id.et_password);
		
		builder.setView(view);  
		((TextView) view.findViewById(R.id.tv_passwordhint)).setText("修改用户信息前，请输入支付密码");

		dialog = builder.show();
		dialog.setCanceledOnTouchOutside(true);
		view.findViewById(R.id.bt_cancel).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				//ToastUtils.showToast("cancel");
				dialog.dismiss();

			}
		});
		view.findViewById(R.id.bt_ok).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String passwords = et_password.getText().toString();
				if (passwords.length() >= 6) {
						dialog.dismiss();
					
				} else {
					ToastUtils.showToast("密码不正确！");
					return;
				}
			}
		});
		
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // Dint iit = lv.getHeaderViewsCount();

        MarketMember marketMember = mAdapter.getItem(arg2);// - 1);
        User user = marketMember.getUser();
        
    	Intent intent=new Intent();
        intent.setClass(this, ManaUserDetailActivity.class);
        //User user = UserDao.getInstance().getUser();
        intent.putExtra(ContactsDetailActivity.EXTRA_USER, user);
        startActivity(intent);
    }

    protected class MyAdapter extends HoldDataBaseAdapter<MarketMember> {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = SystemUtils.inflate(R.layout.item_manage_user);
                holder = new ViewHolder();
                holder.findView(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.fillData(getItem(position), position);
            return convertView;
        }
    }

    private class ViewHolder {
        public TextView tv_user_name;
        public TextView tv_user_account;
        public TextView tv_user_type;

        public void fillData(MarketMember marketMemberu, int pos) {
            // 参考 HomeFragment
        	User u=marketMemberu.getUser();
            if (u == null) {
                return;
            }

            tv_user_name.setText(u.getShow_name());
            tv_user_account.setText(u.getAccount_name());
            tv_user_type.setText(u.getUser_type_name());
        }

        public void findView(View v) {
        	tv_user_name = (TextView) SystemUtils.find(v, R.id.tv_user_name);
        	tv_user_account = (TextView) SystemUtils.find(v, R.id.tv_user_account);
        	tv_user_type = (TextView) SystemUtils.find(v, R.id.tv_user_type);
        }
    }
    
    
}
