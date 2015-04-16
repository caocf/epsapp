package com.epeisong.ui.activity;

import java.util.ArrayList;
import java.util.List;

import lib.universal_image_loader.ImageLoaderUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.net.parser.UserParser;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.ShortLogistics;
import com.epeisong.model.User;
import com.epeisong.net.request.NetSearchContacts;
import com.epeisong.net.request.OnNetRequestListenerImpl;
import com.epeisong.ui.fragment.MembersManagerFragment;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 搜索联系人
 * 
 * @author poet
 * 
 */
public class SearchContactsActivity extends BaseActivity implements OnClickListener, OnItemClickListener {

    private class MyAdapter extends HoldDataBaseAdapter<User> {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = SystemUtils.inflate(R.layout.item_search_contacts);
                holder = new ViewHolder();
                holder.findView(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.fillData(getItem(position));
            return convertView;
        }
    }

    private class ViewHolder {

        ImageView iv_logo;
        TextView tv_name;

        public void fillData(User u) {
            if (!TextUtils.isEmpty(u.getLogo_url())) {
                ImageLoader.getInstance().displayImage(u.getLogo_url(), iv_logo, ImageLoaderUtils.getListOptions());
            } else {
                int id = User.getDefaultIcon(u.getUser_type_code(), true);
                iv_logo.setImageResource(id);
            }
            tv_name.setText(u.getShow_name());
        }

        public void findView(View v) {
            iv_logo = (ImageView) v.findViewById(R.id.iv_logo);
            tv_name = (TextView) v.findViewById(R.id.tv_name);
        }
    }

    public static final int REQUEST_CODE = 315;
    public static final int CANCEL_OK = 508;
    private EditText mPhoneEt;
    private String flag; // 判断是否是从配货市场页面过来的

    private String mPhone;

    private MyAdapter mAdapter;

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_search) {
            String phone = mPhoneEt.getText().toString().replace(" ", "");
            if (TextUtils.isEmpty(phone) || phone.length() != 11) {
                ToastUtils.showToast("请输入正确的手机号码");
                return;
            }
            mPhone = phone;
            NetSearchContacts net = new NetSearchContacts(this) {
                @Override
                protected boolean onSetRequest(com.epeisong.logistics.proto.Eps.ContactReq.Builder req) {
                    req.setMobile(mPhone);
                    return true;
                }
            };
            net.request(new OnNetRequestListenerImpl<CommonLogisticsResp.Builder>() {
                @Override
                public void onSuccess(CommonLogisticsResp.Builder response) {
                    List<ShortLogistics> logisticsList = response.getShortLogisticsList();
                    if (logisticsList == null || logisticsList.isEmpty()) {
                        showMessageDialog(null, "用户不存在");
                        return;
                    }
                    List<User> users = new ArrayList<User>();
                    for (ShortLogistics logi : logisticsList) {
                        users.add(UserParser.parse(logi));
                    }
                    mAdapter.replaceAll(users);
                }
            });
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        User user = mAdapter.getItem(position);
        Intent intent = new Intent(getApplicationContext(), SearchUserDetailActivity.class);
        intent.putExtra(SearchUserDetailActivity.EXTRA_USER, user);
        // startActivity(intent);
        if(!TextUtils.isEmpty(flag)){
        	intent.putExtra("members", flag);
        }
        this.startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected TitleParams getTitleParams() {
    	if(!TextUtils.isEmpty(flag)){
    		return new TitleParams(getDefaultHomeAction(), "添加会员", null).setShowLogo(false);
        }else{
        	return new TitleParams(getDefaultHomeAction(), "添加联系人", null).setShowLogo(false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	flag = getIntent().getStringExtra("members");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_contacts);
        findViewById(R.id.btn_search).setOnClickListener(this);
        mPhoneEt = (EditText) findViewById(R.id.et_phone);

        ListView lv = (ListView) findViewById(R.id.lv);
        lv.setOnItemClickListener(this);
        mAdapter = new MyAdapter();
        lv.setAdapter(mAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (requestCode == REQUEST_CODE) {
            if (resultCode == CANCEL_OK){
                finish();
            }else if(resultCode == 10){
            	User user = (User) data.getSerializableExtra(SearchUserDetailActivity.EXTRA_USER);
            	data.putExtra("mUser", user);
            	setResult(Activity.RESULT_OK, data);
            	finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
