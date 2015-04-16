package com.epeisong.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.epeisong.EpsApplication;
import com.epeisong.R;
import com.epeisong.base.activity.TabPages2Activity;
import com.epeisong.base.adapter.impl.IconTextAdapter;
import com.epeisong.base.adapter.item.IconTextItem;
import com.epeisong.base.view.CustomTitle.Action;
import com.epeisong.base.view.CustomTitle.ActionImpl;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.UserDao;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.net.NetAddMembers;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.SearchCommonLogisticsReq.Builder;
import com.epeisong.model.User;
import com.epeisong.ui.fragment.InfoAreaManageFragment;
import com.epeisong.ui.fragment.InfoScreenFragment;
import com.epeisong.ui.fragment.MembersManagerFragment;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.HandlerUtils;
import com.epeisong.utils.LogUtils;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;
import com.zxing.CaptureActivity;

/**
 * 市场管理页面
 * @author gnn
 *
 */
public class MarketManageActivity extends TabPages2Activity {

	private List<Fragment> mFragments;
	private PopupWindow mPopupWindowMenu;
	private View mTitleRight;
	private String mResult;
	public static final String EXTRA_MARKET = "market";
	private User mMarket;
	private InfoScreenFragment sFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMarket = UserDao.getInstance().getUser();
		mFragments = new ArrayList<Fragment>();
		InfoAreaManageFragment iFragment = new InfoAreaManageFragment();
		mFragments.add(iFragment);
//		MembersManagerFragment mFragment = new MembersManagerFragment(); // 会员管理页面
//		mFragments.add(mFragment);
		Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_MARKET, mMarket);
        bundle.putString("flag", "selfMarket");
		sFragment = new InfoScreenFragment();
		sFragment.setArguments(bundle);
//		MembersScreenFragment sFragment = new MembersScreenFragment();
		mFragments.add(sFragment);

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
//		titles.add("会员管理");
		titles.add("信息区域管理");
        titles.add("电子屏管理");
    }
	
	@Override
    public void onBackPressed() {
        if (sFragment.onBackPressed()) {
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
                return 2;
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
		return new TitleParams(getDefaultHomeAction(), "市场管理", null).setShowLogo(false);
	}
	
	private Action createAction() {
        return new ActionImpl() {

            @Override
            public void doAction(View v) {
                showMenuPopupWindow();
            }

            @Override
            public View getView() {
                mTitleRight = new ImageView(getApplicationContext());
                ((ImageView) mTitleRight).setImageResource(R.drawable.chatroom_contacts_option_not_contacts);
                return mTitleRight;
            }
        };
    }
	
	private void showMenuPopupWindow() {
        if (mPopupWindowMenu == null) {
            initPopupWindowMenu();
        }
        int statusBar = SystemUtils.getStatusBarHeight(this);
        int y = getResources().getDimensionPixelSize(R.dimen.custom_title_height) + statusBar + 1;
        mPopupWindowMenu.showAtLocation(mCustomTitle, Gravity.TOP | Gravity.RIGHT,
                (int) DimensionUtls.getPixelFromDp(10), y);
    }

    private void initPopupWindowMenu() {
        List<IconTextItem> items = new ArrayList<IconTextItem>();
        items.add(new IconTextItem(0, "手机号添加", null));
        items.add(new IconTextItem(0, "二维码添加", null));
        
        mPopupWindowMenu = new PopupWindow(getApplicationContext());
        IconTextAdapter adapter = new IconTextAdapter(getApplicationContext(), 40);
        adapter.replaceAll(items);
        ListView lv = new ListView(getApplicationContext());
        lv.setAdapter(adapter);
        lv.setBackgroundColor(Color.argb(0xFF, 0x2D, 0x30, 0x37));
        mPopupWindowMenu.setContentView(lv);
        mPopupWindowMenu.setWidth(EpsApplication.getScreenWidth() / 2);
        mPopupWindowMenu.setHeight(LayoutParams.WRAP_CONTENT);
        mPopupWindowMenu.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));
        mPopupWindowMenu.setFocusable(true);
        mPopupWindowMenu.setOutsideTouchable(true);
        mPopupWindowMenu.setAnimationStyle(R.style.popup_window_menu);
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                mPopupWindowMenu.dismiss();
                mPopupWindowMenu = null;
                if (position == 0) { //手机号添加
                	Intent search = new Intent(getApplicationContext(), SearchContactsActivity.class);
                	search.putExtra("members", "members");
//                    startActivity(search);
                    startActivityForResult(search, 12);
                } else if (position == 1){ //二维码添加
                	CaptureActivity.launchForResult(MarketManageActivity.this, 22);
                }
            }

        });
        lv.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_MENU && event.getAction() == KeyEvent.ACTION_UP
                        && mPopupWindowMenu.isShowing()) {
                    mPopupWindowMenu.dismiss();
                    return true;
                }
                return false;
            }
        });
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode == 12 && resultCode == Activity.RESULT_OK) {
			User user = (User) data.getSerializableExtra(SearchUserDetailActivity.EXTRA_USER);
        	data.putExtra("mUser", user);
//        	mAdapter.addItem(user);
//        	onPullUpToRefresh(mPullToRefreshListView);
        	Intent intent = new Intent("com.epeisong.ui.activity.refreshMember");
            intent.putExtra("refreshMember", user);
            this.sendBroadcast(intent); // 发送广播
		}
		if(requestCode == 22 && resultCode == Activity.RESULT_OK){
			mResult = data.getStringExtra(CaptureActivity.EXTRA_OUT_RESULT);
			if (TextUtils.isEmpty(mResult)) {
				ToastUtils.showToast("无结果");
	        } else if (mResult.startsWith("http://www.epeisong.com/addcontact")) {
				addMembers(mResult);
	        } else {
	        	ToastUtils.showToast("扫描失败，请重新扫描");
	        }

		}
    	super.onActivityResult(requestCode, resultCode, data);
    }
    
    private void addMembers(final String qrUrl) {
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
            	NetAddMembers net = new NetAddMembers() {
					@Override
					protected boolean onSetRequest(Builder req) {
						req.setQrCodeAddContactURL(mResult);
						return true;
					}
				};
                try {
                    CommonLogisticsResp.Builder resp = net.request();
                    if (net.isSuccess(resp)) {
//                    	onPullUpToRefresh(mPullToRefreshListView);
                    	Intent intent = new Intent("com.epeisong.ui.activity.refreshMember");
                        MarketManageActivity.this.sendBroadcast(intent); // 发送广播
                    	return true;
                    } else {
                        LogUtils.e("", resp.getDesc());
                    }
                    ToastUtils.showToastInThread(resp.getDesc());
                } catch (NetGetException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtils.showToastInThread("解析失败");
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    ToastUtils.showToast("添加会员成功");
                }
            }
        };
        task.execute();
    }

}
