package com.epeisong.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.activity.ShowImagesActivity;
import com.epeisong.data.dao.PointDao;
import com.epeisong.data.dao.PointDao.PointObserver;
import com.epeisong.data.dao.UserDao;
import com.epeisong.data.dao.UserDao.UserObserver;
import com.epeisong.model.Point;
import com.epeisong.model.Point.PointCode;
import com.epeisong.model.User;
import com.epeisong.ui.activity.CommonSetupActivity;
import com.epeisong.ui.activity.ConsultingToHelpActivity;
import com.epeisong.ui.activity.MineCEActivity;
import com.epeisong.ui.activity.MineDetailActivity;
import com.epeisong.ui.activity.MineQuoteActivity;
import com.epeisong.ui.activity.PublishBulletinActivity;
import com.epeisong.ui.activity.WalletActivity;
import com.epeisong.ui.activity.WalletGuaranteeActivity;
import com.epeisong.ui.adapter.SettingAdapter;
import com.epeisong.ui.adapter.SettingItem;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.SystemUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MineFragment extends Fragment implements OnItemClickListener, UserObserver, OnClickListener, PointObserver {

    private ListView mListView;
    private SettingAdapter mAdapter;

    private ImageView mLogoIv;
    private TextView mShowNameTv, mAccountTv, mTypeTv, mRegionTv;

    private DisplayImageOptions mDisplayImageOptions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mListView = new ListView(getActivity());
        mListView.setOnItemClickListener(this);
        mListView.setBackgroundColor(Color.argb(0xff, 0xf8, 0xf8, 0xf8));
        mListView.setDividerHeight(0);

        mDisplayImageOptions = new DisplayImageOptions.Builder().showImageOnFail(R.drawable.user_logo_default)
                .cacheOnDisk(true).cacheInMemory(true).bitmapConfig(Config.RGB_565).build();
        return mListView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 顶部间隙
        boolean useHead = false;
        if (useHead) {
            View headTop = new View(getActivity());
            headTop.setBackgroundColor(Color.argb(0xFF, 0xF4, 0xF4, 0xF4));
            int h = (int) DimensionUtls.getPixelFromDp(15);
            headTop.setLayoutParams(new AbsListView.LayoutParams(-1, h));
            FrameLayout fl = new FrameLayout(getActivity());
            fl.addView(headTop);
            fl.setBackgroundColor(Color.TRANSPARENT);
            mListView.addHeaderView(fl);
        }
        // 头View
        View head = SystemUtils.inflate(R.layout.fragment_mine_head);
        mAccountTv = (TextView) head.findViewById(R.id.tv_account);
        User user = UserDao.getInstance().getUser();
        if (user == null) {
            return;
        }
        mAccountTv.setText(user.getPhone());

        mLogoIv = (ImageView) head.findViewById(R.id.iv_logo);
        mLogoIv.setOnClickListener(this);
        mShowNameTv = (TextView) head.findViewById(R.id.tv_show_name);
        mTypeTv = (TextView) head.findViewById(R.id.tv_logistic_type);
        mRegionTv = (TextView) head.findViewById(R.id.tv_region);

        onUserChange(user);

        mListView.addHeaderView(head);

        List<SettingItem> data = new ArrayList<SettingItem>();
        data.add(new SettingItem());
        data.add(new SettingItem(R.drawable.mine_bulletin_normal, "我的公告", new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getActivity(), PublishBulletinActivity.class);
                startActivity(intent);
            }
        }));
        data.add(new SettingItem());
        data.add(new SettingItem(R.drawable.mine_wallet_normal, "我的钱包", new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getActivity(), WalletActivity.class);
                startActivity(intent);
            }
        }));
        data.add(new SettingItem(R.drawable.icon_mine_offer, "我的报价", new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getActivity(), MineQuoteActivity.class);
                startActivity(intent);
            }
        }));
        data.add(new SettingItem(R.drawable.icon_mine_ce, "我的保证金", new Runnable() {
            @Override
            public void run() {
//                Intent intent = new Intent(getActivity(), MineCEActivity.class);
//                startActivity(intent);
            	
    			Intent intent = new Intent(getActivity(), WalletGuaranteeActivity.class);
    			startActivity(intent);
            }
        }));

        data.add(new SettingItem());
        data.add(new SettingItem(R.drawable.mine_advisory_and_assist_normal, "咨询帮助", new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getActivity(), ConsultingToHelpActivity.class);
                startActivity(intent);
            }
        }));
        boolean hasNewmsg = PointDao.getInstance().query(PointCode.Code_Mine_Common_Setup).isShow();
        data.add(new SettingItem(R.drawable.icon_mine_setup, "通用设置", new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getActivity(), CommonSetupActivity.class);
                startActivity(intent);
            }
        }).setDesc("密码、隐私/消息提醒").setHasNewMsg(hasNewmsg));

        mAdapter = new SettingAdapter(getActivity());
        mListView.setAdapter(mAdapter);
        mAdapter.replaceAll(data);

        UserDao.getInstance().addObserver(this);

        PointDao.getInstance().addObserver(PointCode.Code_Mine_Common_Setup, this);
    }

    @Override
    public void onPointChange(Point p) {
        boolean show = p.isShow();
        PointCode pointCode = PointCode.convertFromValue(p.getCode());
        switch (pointCode) {
        case Code_Mine_Common_Setup:
            mAdapter.getItem(5).setHasNewMsg(show);
            mAdapter.notifyDataSetChanged();
            break;

        default:
            break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.iv_logo:
            String logo_url = UserDao.getInstance().getUser().getLogo_url();
            if (!TextUtils.isEmpty(logo_url)) {
                ArrayList<String> urls = new ArrayList<String>(1);
                urls.add(logo_url);
                ShowImagesActivity.launch(getActivity(), urls, 0);
            }
            break;

        default:
            break;
        }
    }

    @Override
    public void onDestroyView() {
        UserDao.getInstance().removeObserver(this);
        super.onDestroyView();
    }

    @Override
    public void onUserChange(User user) {
        if (!TextUtils.isEmpty(user.getLogo_url())) {
            ImageLoader.getInstance().displayImage(user.getLogo_url(), mLogoIv, mDisplayImageOptions);
        }
        mShowNameTv.setText(user.getShow_name());
        mAccountTv.setText(user.getPhone());
        mTypeTv.setText(user.getUser_type_name());
        mRegionTv.setText(user.getUserRole().getRegionName());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        position -= mListView.getHeaderViewsCount();
        if (position == -1) {
            Intent i = new Intent(getActivity(), MineDetailActivity.class);
            startActivity(i);
            return;
        } else if (position >= 0) {
            SettingItem item = mAdapter.getItem(position);
            if (item.getRunnable() != null) {
                view.post(item.getRunnable());
            }
        }
    }

}
