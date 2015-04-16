package com.epeisong.ui.activity.temp;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.base.view.CustomTitle.Action;
import com.epeisong.base.view.TitleParams;
import com.epeisong.logistics.common.Properties;
import com.epeisong.ui.activity.temp.ChooseRoleActivity.Role;
import com.epeisong.ui.activity.user.TempActivity;
import com.epeisong.utils.SystemUtils;

/**
 * 选择角色（新）
 * 
 * @author poet
 * 
 */
public class ChooseRoleActivityNew extends TempActivity implements OnItemClickListener {

    private ListView mListView;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListView = new ListView(this);
        setContentView(mListView);
        mListView.setCacheColorHint(Color.TRANSPARENT);
        mListView.setAdapter(mAdapter = new MyAdapter());
        List<Role> list = new ArrayList<Role>();
        list.add(new Role().setCode(Properties.LOGISTIC_TYPE_ENTIRE_VEHICLE).setName("整车运输"));
        list.add(new Role().setCode(Properties.LOGISTIC_TYPE_STOWAGE_INFORMATION_DEPARTMENT).setName("配载信息部"));
        list.add(new Role().setCode(Properties.LOGISTIC_TYPE_THIRD_PART_LOGISTICS).setName("第三方物流"));
        list.add(new Role().setCode(Properties.LOGISTIC_TYPE_LESS_THAN_TRUCKLOAD_AND_LINE).setName("零担专线"));
        list.add(new Role().setCode(Properties.LOGISTIC_TYPE_TRANSHIP_GOODS).setName("驳货"));
        list.add(new Role().setCode(Properties.LOGISTIC_TYPE_EQUIPMENT_LEASING).setName("设备租赁"));
        mListView.setOnItemClickListener(this);
        mAdapter.replaceAll(list);
    }

    @Override
    protected TitleParams getTitleParams() {
        Action actions = new Action() {

            @Override
            public View getView() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public int getDrawable() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public void doAction(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        };

        return new TitleParams(actions, "选择角色", null).setShowLogo(false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
        return false;
    }

    class MyAdapter extends HoldDataBaseAdapter<Role> {

        // @Override
        // public void onClick(View v) {
        // Object tag = v.getTag();
        // if (tag != null && tag instanceof Role) {
        // Role role = (Role) tag;
        // Intent intent = new Intent(ChooseRoleActivityNew.this,
        // SetupRoleInfoActivityNew.class);
        // intent.putExtra(SetupRoleInfoActivityNew.EXTRA_ROLE, role);
        // startActivity(intent);
        // }
        // }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = SystemUtils.inflate(R.layout.activity_choose_role_item_new);
            TextView tv = (TextView) v.findViewById(R.id.tv_role_name);
            View next = v.findViewById(R.id.btn_next);
            // next.setOnClickListener(this);
            Role role = getItem(position);
            if (role.getCode() == Properties.LOGISTIC_TYPE_ENTIRE_VEHICLE) {
                tv.setText(role.getName() + "(司机)");
            } else {
                tv.setText(role.getName());
            }
            next.setTag(role);
            return v;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Role role = mAdapter.getItem(position);
        // ToastUtils.showToast(role.getName());
        Intent intent = new Intent(ChooseRoleActivityNew.this, SetupRoleInfoActivityNew.class);
        intent.putExtra(SetupRoleInfoActivityNew.EXTRA_ROLE, role);
        startActivity(intent);
    }
}
