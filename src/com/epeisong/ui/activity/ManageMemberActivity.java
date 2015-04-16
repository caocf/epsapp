package com.epeisong.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.net.parser.ContactsParser;
import com.epeisong.logistics.proto.Base.ProtoEMarketScreenBannedMember;
import com.epeisong.logistics.proto.Eps;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.model.Contacts;
import com.epeisong.net.request.NetDeleteBanned;
import com.epeisong.net.request.NetMarketScreenBannedList;
import com.epeisong.net.request.OnNetRequestListenerImpl;
import com.epeisong.utils.LogUtils;
import com.epeisong.utils.SystemUtils;

public class ManageMemberActivity extends BaseActivity implements OnItemClickListener, OnClickListener {

    private class MyAdapter extends HoldDataBaseAdapter<Contacts> {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null || !(convertView instanceof LinearLayout)) {
                convertView = SystemUtils.inflate(R.layout.activity_manage_member_item);
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
        TextView tv_shielding_user;
        Button btn_cancel_shielding;

        public void fillData(Contacts c) {
            tv_shielding_user.setText(c.getShow_name());
            btn_cancel_shielding.setTag(c);
        }

        public void findView(View v) {
            tv_shielding_user = (TextView) v.findViewById(R.id.tv_shielding_user);
            btn_cancel_shielding = (Button) v.findViewById(R.id.btn_cancel_shielding);
            btn_cancel_shielding.setOnClickListener(ManageMemberActivity.this);
        }
    }

    private ListView lv_shielding;

    private MyAdapter mAdapter;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_cancel_shielding:
            Object tag = v.getTag();
            if (tag != null && tag instanceof Contacts) {
                final Contacts c = (Contacts) tag;
                NetDeleteBanned net = new NetDeleteBanned(this, c.getMarket_banned_id());
                net.request(new OnNetRequestListenerImpl<Eps.CommonLogisticsResp.Builder>() {

                    @Override
                    public void onSuccess(com.epeisong.logistics.proto.Eps.CommonLogisticsResp.Builder response) {
                        // initView();
                        LogUtils.d("", "取消屏蔽成功");
                        mAdapter.removeItem(c);
                    }
                });
            }

            break;

        default:
            break;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
        // TODO Auto-generated method stub

    }

    private void initView() {
        NetMarketScreenBannedList net = new NetMarketScreenBannedList(this, 10, 0);
        net.request(new OnNetRequestListenerImpl<Eps.CommonLogisticsResp.Builder>() {

            @Override
            public void onSuccess(CommonLogisticsResp.Builder response) {
//                List<ProtoEMarketScreenBannedMember> ContactList = response.getBannedMemberList();
//                if (ContactList == null || ContactList.isEmpty()) {
//                    showMessageDialog(null, "没有屏蔽的联系人");
//                }
//                List<Contacts> result = new ArrayList<Contacts>();
//                for (ProtoEMarketScreenBannedMember item : ContactList) {
//                    Contacts c = ContactsParser.parse(item);
//                    if (c != null) {
//                        result.add(c);
//                    }
//                }
//                mAdapter.addAll(result);
            }
        });
    }

    @Override
    protected TitleParams getTitleParams() {
        // TODO Auto-generated method stub
        return new TitleParams(getDefaultHomeAction(), "管理会员信息", null).setShowLogo(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_manage_member);
        lv_shielding = (ListView) findViewById(R.id.lv_shielding);
        lv_shielding.setAdapter(mAdapter = new MyAdapter());
        lv_shielding.setOnItemClickListener(this);
        initView();
    }

}
