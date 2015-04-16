package com.epeisong.ui.activity;

import java.util.List;

import lib.universal_image_loader.ImageLoaderUtils;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.activity.SimpleListActivity;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.ContactsDao;
import com.epeisong.data.dao.ContactsDao.ContactsObserver;
import com.epeisong.model.Contacts;
import com.epeisong.utils.ContactsUtils;
import com.epeisong.utils.ContactsUtils.OnContactsUtilsListener;
import com.epeisong.utils.SystemUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 黑名单
 * 
 * @author poet
 * 
 */
public class BlacklistActivity extends SimpleListActivity<Contacts> implements ContactsObserver {

    private class ChildViewHolder {
        ImageView iv_image;
        TextView tv_user_name;

        public void fillData(Contacts item) {
            if (!TextUtils.isEmpty(item.getLogo_url())) {
                ImageLoader.getInstance().displayImage(item.getLogo_url(), iv_image,
                        ImageLoaderUtils.getListOptionsForUserLogo());
            }
            tv_user_name.setText(item.getShow_name());
        }

        public void findView(View v) {
            iv_image = (ImageView) v.findViewById(R.id.iv_image);
            tv_user_name = (TextView) v.findViewById(R.id.tv_user_name);
        }

    }

    @Override
    public void onContactsChange() {
        requestData();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

        final Contacts c = mAdapter.getItem(arg2);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(c.getShow_name());
        final String[] items = { "从黑名单移除", "投诉" };
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    addContactsList(c);
                } else if (which == 1) {
                    // deleteContacts(c);
                    // TODO 投诉
                }
            }

            private void addContactsList(final Contacts c) {
                showPendingDialog(null);
                ContactsUtils.add(c.getId(), new OnContactsUtilsListener() {
                    @Override
                    public void onContactsUtilsComplete(int option, boolean success) {
                        dismissPendingDialog();
                    }
                });
            }
        });
        builder.create().show();
        return super.onItemLongClick(arg0, arg1, arg2, arg3);
    }

    private void requestData() {

        List<Contacts> list = ContactsDao.getInstance().queryAll(Contacts.STATUS_BLACKLIST);

        mAdapter.replaceAll(list);
    }

    @Override
    protected View getItemView(int position, View convertView, ViewGroup parent) {
        /*
         * ImageView iv_image = new ImageView(getApplicationContext()); TextView
         * tv_user_name = new TextView(getApplicationContext());
         * tv_user_name.setText(mAdapter.getItem(position).getShow_name());
         * iv_image.setImageResource(position); return tv_user_name;
         */

        ChildViewHolder holder;
        if (convertView == null) {
            convertView = SystemUtils.inflate(R.layout.item_blacklist);
            holder = new ChildViewHolder();
            holder.findView(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ChildViewHolder) convertView.getTag();
        }
        holder.fillData(mAdapter.getItem(position));
        return convertView;
    }

    @Override
    protected TitleParams getTitleParams() {
        return new TitleParams(getDefaultHomeAction(), "黑名单", null).setShowLogo(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContactsDao.getInstance().addObserver(this);
        requestData();
    }

}
