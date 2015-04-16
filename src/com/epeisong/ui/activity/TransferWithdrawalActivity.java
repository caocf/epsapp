package com.epeisong.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.activity.TabPages2Activity;
import com.epeisong.base.view.CustomTitle.Action;
import com.epeisong.base.view.CustomTitle.ActionImpl;
import com.epeisong.base.view.TitleParams;
import com.epeisong.logistics.common.Properties;
import com.epeisong.ui.fragment.TransferWithdrawalCancelFragment;
import com.epeisong.ui.fragment.TransferWithdrawalListFragment;
import com.epeisong.ui.fragment.TransferWithdrawalProcessedListFragment;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.HandlerUtils;

/**
 * 转账提现页面
 * @author Administrator
 *
 */
public class TransferWithdrawalActivity extends TabPages2Activity {
	private List<Fragment> mFragments;
	private TextView mTitleRightTv;
//	private IconTextAdapter mIconTextAdapter;
//    private PopupWindow mPopupWindowMenu;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mFragments = new ArrayList<Fragment>();
		//未处理列表页面
		TransferWithdrawalListFragment exeFragment = new TransferWithdrawalListFragment();
		Bundle args = new Bundle();
        args.putInt(TransferWithdrawalListFragment.ARGS_TRANSFER_TYPE, Properties.WITHDRAW_TASK_STATUS_NOT_PROCESSED);
        exeFragment.setArguments(args);
        mFragments.add(exeFragment);

        //已处理列表页面
        TransferWithdrawalProcessedListFragment doneFragment = new TransferWithdrawalProcessedListFragment();
        Bundle args1 = new Bundle();
        args1.putInt(TransferWithdrawalListFragment.ARGS_TRANSFER_TYPE, Properties.WITHDRAW_TASK_STATUS_PROCESSED);
        doneFragment.setArguments(args1);
        mFragments.add(doneFragment);

        //警报列表页面
        TransferWithdrawalCancelFragment canceledFragment = new TransferWithdrawalCancelFragment();
        Bundle args2 = new Bundle();
        args2.putInt(TransferWithdrawalCancelFragment.ARGS_TRANSFER_TYPE, Properties.WITHDRAW_TASK_STATUS_CANCEL);
        canceledFragment.setArguments(args2);
        mFragments.add(canceledFragment);

        mTabRoot.setBackgroundColor(Color.argb(0xff, 0x00, 0x9c, 0xff));
        int top = DimensionUtls.getPixelFromDpInt(10);
        int bottom = DimensionUtls.getPixelFromDpInt(15);
        mTabRoot.setPadding(bottom, top, bottom, bottom);

        mViewPager.setOffscreenPageLimit(3);
        HandlerUtils.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mFragments.get(0).isAdded()) {
                    mFragments.get(0).setUserVisibleHint(true);
                } else {
                    HandlerUtils.postDelayed(this, 100);
                }
            }
        }, 200);
	}

	@Override
	protected void onSetTabTitle(List<String> titles) {
		titles.add("未处理");
        titles.add("已处理");
        titles.add("已取消");
	}
	
	@Override
    protected TabStyle getTabStyle() {
        return new TabStyle() {
            @Override
            public int getTabBg() {
                return R.drawable.shape_content_trans_frame_white;
            }

            public int[] getTabItemBgs() {
                return new int[] { R.drawable.shape_tab2_bg_white_left, R.drawable.shape_tab2_bg_white_middle,
                        R.drawable.shape_tab2_bg_white_right };
            }

            public int getTextColorSelectorId() {
                return R.color.selector_tab_text_3;
            }

            @Override
            public int getDividerColor() {
                return Color.WHITE;
            }
        };
    }

	@Override
	protected PagerAdapter onGetAdapter() {
		return new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }
        };
	}

	@Override
	protected TitleParams getTitleParams() {
		List<Action> actions = new ArrayList<Action>();
        actions.add(createAction());
		return new TitleParams(getDefaultHomeAction(), "转账提现", actions).setShowLogo(false);
	}
	
	private Action createAction() {
        return new ActionImpl() {

            @Override
            public void doAction(View v) {
//                showMenuPopupWindow();
            }

            @Override
            public View getView() {
                
                mTitleRightTv = new TextView(getApplicationContext());
                int padding = (int) DimensionUtls.getPixelFromDp(5);
                mTitleRightTv.setPadding(padding, padding, padding, padding);
                mTitleRightTv.setBackgroundResource(R.drawable.selector_common_bg_blue_red);
                mTitleRightTv.setTextColor(Color.WHITE);
                mTitleRightTv.setTextSize(17);
                mTitleRightTv.setGravity(Gravity.CENTER);
//                mTitleRightTv.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
                mTitleRightTv.setText("开启中");
//                boolean receiving = UserDao.getInstance().getUser().isReceive_contacts_freight() == User.CONFIG_RECEIVE_CONTACTS_FREIGHT_YES;
//                if (receiving) {
//                    mTitleRightTv.setText("开启中");
//                } else {
//                    mTitleRightTv.setText("断开中");
//                }
//                mTitleRightTv.setSelected(receiving);
                mTitleRightTv.setSelected(true);
                return mTitleRightTv;
            }
        };
    }
	
//	private void showMenuPopupWindow() {
//        if (mPopupWindowMenu == null) {
//            initPopupWindowMenu();
//        }
//        int statusBar = SystemUtils.getStatusBarHeight(this);
//        int y = getResources().getDimensionPixelSize(R.dimen.custom_title_height) + statusBar + 1;
//        mPopupWindowMenu.showAtLocation(mCustomTitle, Gravity.TOP | Gravity.RIGHT,
//                (int) DimensionUtls.getPixelFromDp(10), y);
//    }
	
//	private void initPopupWindowMenu() {
//        int received = UserDao.getInstance().getUser().isReceive_contacts_freight();
//        List<IconTextItem> items = new ArrayList<IconTextItem>();
//        items.add(new IconTextItem(R.drawable.selector_common_hook, "开始接收", null)
//                .setSelected(received == User.CONFIG_RECEIVE_CONTACTS_FREIGHT_YES));
//        items.add(new IconTextItem(R.drawable.selector_common_hook, "暂停接收", null)
//                .setSelected(received == User.CONFIG_RECEIVE_CONTACTS_FREIGHT_NO));
//        mIconTextAdapter = new IconTextAdapter(getApplicationContext(), 50) {
//            @Override
//            public View getView(int position, View convertView, ViewGroup parent) {
//
//                return super.getView(position, convertView, parent);
//            }
//        };
//        mIconTextAdapter.setIconRight();
//        mIconTextAdapter.replaceAll(items);
//        ListView lv = new ListView(getApplicationContext());
//        lv.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
//        lv.setAdapter(mIconTextAdapter);
//        lv.setBackgroundColor(Color.argb(0xFF, 0x2D, 0x30, 0x37));
//        mPopupWindowMenu = new PopupWindow(getApplicationContext());
//        mPopupWindowMenu.setContentView(lv);
//        mPopupWindowMenu.setWidth(EpsApplication.getScreenWidth() / 2);
//        mPopupWindowMenu.setHeight(LayoutParams.WRAP_CONTENT);
//        mPopupWindowMenu.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));
//        mPopupWindowMenu.setFocusable(true);
//        mPopupWindowMenu.setOutsideTouchable(true);
//        mPopupWindowMenu.setAnimationStyle(R.style.popup_window_menu);
//        lv.setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                mPopupWindowMenu.dismiss();
//
//                if (position == 0) {
////                    changeReceive(User.CONFIG_RECEIVE_CONTACTS_FREIGHT_YES);
//                } else if (position == 1) {
////                    changeReceive(User.CONFIG_RECEIVE_CONTACTS_FREIGHT_NO);
//                }
//            }
//        });
//        lv.setOnKeyListener(new OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (keyCode == KeyEvent.KEYCODE_MENU && event.getAction() == KeyEvent.ACTION_UP
//                        && mPopupWindowMenu.isShowing()) {
//                    mPopupWindowMenu.dismiss();
//                    return true;
//                }
//                return false;
//            }
//        });
//    }

}
