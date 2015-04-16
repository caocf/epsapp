package com.epeisong.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.epeisong.R;
import com.epeisong.base.activity.TabPages2Activity;
import com.epeisong.base.view.CustomTitle.Action;
import com.epeisong.base.view.CustomTitle.ActionImpl;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.ContactsDao;
import com.epeisong.data.net.NetComplain;
import com.epeisong.logistics.proto.Eps.LogisticsReq.Builder;
import com.epeisong.model.User;
import com.epeisong.ui.fragment.FreightStationListFragment;
import com.epeisong.ui.fragment.InfoScreenFragment;
import com.epeisong.utils.ContactsUtils;
import com.epeisong.utils.ContactsUtils.OnContactsUtilsListener;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.ToastUtils;

/**
 * 配货市场详情
 * @author poet
 *
 */
public class FreightMarketDetailActivity extends TabPages2Activity implements OnContactsUtilsListener {

    public static final String EXTRA_MARKET = "market";

    private ImageView mTitleRightIv;

    private User mMarket;

    private List<Fragment> mFragments;
    private InfoScreenFragment mInfoScreenFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mMarket = (User) getIntent().getSerializableExtra(EXTRA_MARKET);
        super.onCreate(savedInstanceState);
        if (mMarket == null) {
            ToastUtils.showToast("参数错误");
            finish();
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_MARKET, mMarket);
        mFragments = new ArrayList<Fragment>();

        mInfoScreenFragment = new InfoScreenFragment();
        mInfoScreenFragment.setArguments(bundle);
        mFragments.add(mInfoScreenFragment);

        FreightStationListFragment stationList = new FreightStationListFragment();
        stationList.setArguments(bundle);
        mFragments.add(stationList);

        mTabRoot.setBackgroundColor(Color.argb(0xff, 0x00, 0x9c, 0xff));
        int top = DimensionUtls.getPixelFromDpInt(10);
        int bottom = DimensionUtls.getPixelFromDpInt(15);
        mTabRoot.setPadding(bottom, top, bottom, bottom);
    }

    @Override
    protected TitleParams getTitleParams() {
        String title = "";
        if (mMarket != null) {
            title = mMarket.getShow_name();
        }
        Action action = new ActionImpl() {
            @Override
            public void doAction(View v) {
                showContactsPopup(mMarket.getId(), mCustomTitle);
            }

            @Override
            public View getView() {
                mTitleRightIv = new ImageView(FreightMarketDetailActivity.this);
                if (ContactsDao.getInstance().queryById(mMarket.getId()) == null) {
                    mTitleRightIv.setImageResource(R.drawable.chatroom_contacts_option_not_contacts);
                } else {
                    mTitleRightIv.setImageResource(R.drawable.chatroom_contacts_option_is_contacts);
                }
                return mTitleRightIv;
            }
        };
        return new TitleParams(getDefaultHomeAction(), title).setAction(action).setShowLogo(false);
    }

    @Override
    protected void onContactsOption(String option) {
//        showPendingDialog(null);
        if (ContactsUtils.STR_COMPLAIN.equals(option)) {
//        	ToastUtils.showToast(option);
        	if(mMarket != null){
        		Intent intent = new Intent(FreightMarketDetailActivity.this, CustomerComplaintActivity.class);
            	intent.putExtra("user", mMarket);
            	startActivity(intent);
			}else{
				 Toast.makeText(getApplicationContext(), "投诉", Toast.LENGTH_SHORT).show();// 投诉
			}
        	
        } else {
            ContactsUtils.onContactsOption(option, mMarket.getId(), this);
        }
    }
    
    @Override
    public void onContactsUtilsComplete(int option, boolean success) {
        dismissPendingDialog();
        if (success) {
            switch (option) {
            case OnContactsUtilsListener.option_add:
                mTitleRightIv.setImageResource(R.drawable.chatroom_contacts_option_is_contacts);
                ToastUtils.showToast("添加成功");
                break;
            case OnContactsUtilsListener.option_delete:
                mTitleRightIv.setImageResource(R.drawable.chatroom_contacts_option_not_contacts);
                ToastUtils.showToast("删除成功");
                break;
            case OnContactsUtilsListener.option_black:
                mTitleRightIv.setImageResource(R.drawable.chatroom_contacts_option_is_contacts);
                ToastUtils.showToast("已加入黑名单");
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mInfoScreenFragment.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected TabStyle getTabStyle() {
        return new TabStyle() {
            @Override
            public int getTabBg() {
                return R.drawable.shape_content_trans_frame_white;
            }

            public int[] getTabItemBgs() {
                return new int[] { R.drawable.shape_tab2_bg_white_left, R.drawable.shape_tab2_bg_white_right };
            }

            public int getTextColorSelectorId() {
                return R.color.selector_tab_text_3;
            }

            @Override
            public int getDividerColor() {
                return Color.TRANSPARENT;
            }
        };
    }

    @Override
    protected void onSetTabTitle(List<String> titles) {
        titles.add("信息电子屏");
        titles.add("信息摊位");
    }

    @Override
    protected PagerAdapter onGetAdapter() {
        return new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }
        };
    }

}
