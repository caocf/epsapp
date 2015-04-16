package com.epeisong.ui.activity;

import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.activity.SimpleListActivity;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.ContactsDao;
import com.epeisong.data.dao.FansDao;
import com.epeisong.data.dao.FansDao.FansObserver;
import com.epeisong.data.dao.PointDao;
import com.epeisong.data.dao.util.CRUD;
import com.epeisong.data.net.parser.ContactsParser;
import com.epeisong.logistics.proto.Base.ProtoEBizLogistics;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.model.Contacts;
import com.epeisong.model.Fans;
import com.epeisong.model.Point.PointCode;
import com.epeisong.net.request.NetAddContacts;
import com.epeisong.net.request.OnNetRequestListenerImpl;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;

/**
 * 关注我的人
 * 
 * @author poet
 * 
 */
public class FansActivity extends SimpleListActivity<Fans> implements FansObserver, OnClickListener {

    private class ViewHolder {
        ImageView iv_head;
        TextView tv_name;
        TextView tv_action_or_status;

        public void fillData(Fans fans) {
            iv_head.setImageResource(R.drawable.user_logo_default);
            tv_name.setText(fans.getName());
            tv_action_or_status.setTag(null);
            tv_action_or_status.setOnClickListener(FansActivity.this);
            int status = fans.getStatus();
            if (status == Fans.status_added) {
                tv_action_or_status.setText("已添加");
                tv_action_or_status.setClickable(false);
            } else {
                tv_action_or_status.setClickable(true);
                tv_action_or_status.setText("添加");
                tv_action_or_status.setTag(fans);
            }
        }

        public void findView(View v) {
            iv_head = (ImageView) v.findViewById(R.id.iv_head);
            tv_name = (TextView) v.findViewById(R.id.tv_name);
            tv_action_or_status = (TextView) v.findViewById(R.id.tv_action_or_status);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_action_or_status) {
            Object tag = v.getTag();
            if (tag != null && tag instanceof Fans) {
                final Fans fans = (Fans) tag;
                NetAddContacts net = new NetAddContacts(this, fans.getId());
                net.request(new OnNetRequestListenerImpl<CommonLogisticsResp.Builder>() {
                    @Override
                    public void onSuccess(CommonLogisticsResp.Builder response) {

                        ProtoEBizLogistics logistics = response.getBizLogistics(0);
                        if (logistics != null) {
                            Contacts c = ContactsParser.parse(logistics);
                            c.setStatus(Contacts.STATUS_NORNAL);
                            boolean b = ContactsDao.getInstance().replace(c);
                            fans.setStatus(Fans.status_added);
                            FansDao.getInstance().update(fans);
                            ToastUtils.showToast("添加成功");
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onFansChange(Fans fans, CRUD crud) {
        refreshData();
    }

    private synchronized void refreshData() {
        AsyncTask<Void, Void, List<Fans>> task = new AsyncTask<Void, Void, List<Fans>>() {
            @Override
            protected List<Fans> doInBackground(Void... params) {
                return FansDao.getInstance().queryAll();
            }

            @Override
            protected void onPostExecute(List<Fans> result) {
                mAdapter.replaceAll(result);
            }
        };
        task.execute();
    }

    @Override
    protected View getItemView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        if (convertView == null) {
            convertView = SystemUtils.inflate(R.layout.item_my_fans);
            holder = new ViewHolder();
            holder.findView(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.fillData(mAdapter.getItem(position));
        return convertView;
    }

    @Override
    protected TitleParams getTitleParams() {
        return new TitleParams(getDefaultHomeAction(), "关注我的人", null).setShowLogo(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        refreshData();

        FansDao.getInstance().addObserver(this);

       // PointDao.getInstance().hide(PointCode.Code_Contacts_Fans);
    }

    @Override
    protected void onDestroy() {
        FansDao.getInstance().removeObserver(this);
        super.onDestroy();
    }
}
