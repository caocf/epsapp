package com.epeisong.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.MotionEvent;

import com.epeisong.base.activity.TabPages2Activity;
import com.epeisong.base.view.CustomTitle.Action;
import com.epeisong.base.view.TitleParams;
import com.epeisong.model.User;
import com.epeisong.ui.fragment.NetInforFragment;

/**
 * 网点信息- 
 * @author Jack
 *
 */
public class SetGetGoodsActivity extends TabPages2Activity {

    public static final String EXTRA_GOODS_TYPE = "goods_getset";

    private List<Fragment> mFragments;
    
    private int mGoodstype;
    private int Logistic_type;
    private String mUserId;
    private User mUser;

    protected void onCreate(Bundle savedInstanceState) {
        mUser = (User) getIntent().getSerializableExtra(ContactsDetailActivity.EXTRA_USER);
//		if (mUser == null) {
//			ToastUtils.showToast("参数错误");
//			finish();
//			return;
//		}
        mUserId = getIntent().getStringExtra(ContactsDetailActivity.EXTRA_USER_ID);
        Logistic_type = getIntent().getIntExtra(ContactsDetailActivity.EXTRA_USER_TYPEID,Logistic_type);//mUser.getUser_type_code();
        mGoodstype = Integer.valueOf(getIntent().getStringExtra(EXTRA_GOODS_TYPE));
        //TabPages2Activity.TabStyle mTabStyle = getTabStyle();
        //mTabStyle.SetTabBg(R.drawable.net_fragment_tab);
        
        super.onCreate(savedInstanceState);   
        mFragments = new ArrayList<Fragment>();

        NetInforFragment setFragment = new NetInforFragment();
        Bundle args = new Bundle();
        args.putSerializable(ContactsDetailActivity.EXTRA_USER, mUser);
        args.putString(ContactsDetailActivity.EXTRA_USER_ID, mUserId);
        args.putInt(ContactsDetailActivity.EXTRA_USER_TYPEID, Logistic_type);
        args.putString(EXTRA_GOODS_TYPE, "0");
        ((Fragment) setFragment).setArguments(args);
        mFragments.add(0, setFragment);
        
        Bundle args2 = new Bundle();
        NetInforFragment getFragment = new NetInforFragment();
        args2.putSerializable(ContactsDetailActivity.EXTRA_USER, mUser);
        args2.putString(ContactsDetailActivity.EXTRA_USER_ID, mUserId);
        args2.putInt(ContactsDetailActivity.EXTRA_USER_TYPEID, Logistic_type);
        args2.putString(EXTRA_GOODS_TYPE, "1");
        getFragment.setArguments(args2);
        mFragments.add(1, getFragment);
              
        //mViewPager.setBackgroundResource(R.drawable.net_fragment_tab);

        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setCurrentItem(mGoodstype);

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

    @Override
    protected void onSetTabTitle(List<String> titles) {
        titles.add("出发地网点");
        titles.add("到达地网点");
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

	@Override
    protected TitleParams getTitleParams() {
        List<Action> actions = new ArrayList<Action>();
        //actions.add(createAction());
        return new TitleParams(getDefaultHomeAction(), "网点信息", actions).setShowLogo(false);
    }
}
