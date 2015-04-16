package com.epeisong.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.View;

import com.epeisong.R;
import com.epeisong.base.activity.TabPages2Activity;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.PointDao;
import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.common.Properties;
import com.epeisong.model.Point.PointCode;
import com.epeisong.service.notify.MenuBean;
import com.epeisong.service.notify.MenuEnum;
import com.epeisong.service.notify.NotifyService;
import com.epeisong.ui.fragment.InfoFeeListFragment;
import com.epeisong.ui.fragment.InfoFeeListFragment.changeUiByStatus;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.HandlerUtils;

/**
 * 信息费订单管理
 * @author poet
 *
 */
public class InfoFeeListActivity extends TabPages2Activity implements changeUiByStatus{

    public static boolean SHOWING = false;

    private List<Fragment> mFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SHOWING = true;
        mFragments = new ArrayList<Fragment>();
        InfoFeeListFragment exeFragment = new InfoFeeListFragment();
        Bundle args = new Bundle();
        args.putInt(InfoFeeListFragment.ARGS_ORDER_TYPE, Properties.INFO_FEE_STATUS_EXECUTE);
        exeFragment.setArguments(args);
        mFragments.add(exeFragment);

        InfoFeeListFragment doneFragment = new InfoFeeListFragment();
        Bundle args2 = new Bundle();
        args2.putInt(InfoFeeListFragment.ARGS_ORDER_TYPE, Properties.INFO_FEE_STATUS_COMPLETE);
        doneFragment.setArguments(args2);
        mFragments.add(doneFragment);

        InfoFeeListFragment canceledFragment = new InfoFeeListFragment();
        Bundle args3 = new Bundle();
        args3.putInt(InfoFeeListFragment.ARGS_ORDER_TYPE, Properties.INFO_FEE_STATUS_CANCEL);
        canceledFragment.setArguments(args3);
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
        
        
        initDbByUi() ;
   

//        Drawable redPoint = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(),
//                R.drawable.main_red_point_17));
//        redPoint.setBounds(0, 0, 17, 17);
//        getTabTextView(1).setCompoundDrawables(null, null, redPoint, null);
    }

    @Override
    protected TitleParams getTitleParams() {
        return new TitleParams(getDefaultHomeAction(), "订单列表", null).setShowLogo(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SHOWING = false;
    }

    @Override
    protected void onSetTabTitle(List<String> titles) {
        titles.add("执行中");
        titles.add("已完成");
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
    protected void onResume() {
       // PointDao.getInstance().hide(PointCode.Code_Task_InfoFee);
       
        super.onResume();
    }

	@Override
	public void refresh(Object... param) {
		int type = (Integer) param[0];
		switch(type) {
		case CommandConstants.INFO_FEE_SERVER_GET_INFO_FEE_PUSH_REQ: // 
			int show = (Integer)param[1];
			int status = (Integer)param[2];
			if(NotifyService.isShow == show) {
				setPointFresh(status);
			} 
			break;
		}
	}
	
	@Override
	protected void changeUiPoint() {
		getTabTextView(this.mCurPos ).setCompoundDrawables(null, null, null, null);
 
		if(this.mCurPos== 0) {
			NotifyService.updateTabByCode( MenuEnum.OrderListExe.getMenuCode(), NotifyService.noShow);
		 } else if(this.mCurPos== 1) {
			 NotifyService.updateTabByCode( MenuEnum.OrderListDone.getMenuCode(), NotifyService.noShow);
		 } else if(this.mCurPos== 2) {
			 NotifyService.updateTabByCode( MenuEnum.OrderListCancel.getMenuCode(), NotifyService.noShow);
		 }

	}
	
	
	private void setPointFresh(int status) {
		
		 if( this.mCurPos != status ) {
			   Drawable redPoint = new BitmapDrawable(getResources(), 
				    	BitmapFactory.decodeResource(getResources(), R.drawable.main_red_point_17));
				       redPoint.setBounds(0, 0, 24, 24);
			getTabTextView(status).setCompoundDrawables(null, null, redPoint, null);
		 }
	}
	
	
	//初始化时判断是否需要更新UI
	public void initDbByUi() {
		String filter  = "Where actName='" +MenuEnum.OrderList.getActName()+"'";
		List<MenuBean> listBean =  NotifyService.ListMenuBean(filter);
		   Drawable redPoint = new BitmapDrawable(getResources(), 
			    	BitmapFactory.decodeResource(getResources(), R.drawable.main_red_point_17));
			       redPoint.setBounds(0, 0, 24, 24);
		for(MenuBean bean : listBean) {
			if(bean.getIsShow() == NotifyService.isShow) {
				if(bean.getMenuCode().equals(MenuEnum.OrderListExe.getMenuCode())) {//执行中
					getTabTextView(0).setCompoundDrawables(null, null, redPoint, null); 
				} else if(bean.getMenuCode().equals(MenuEnum.OrderListDone.getMenuCode())) {//已完成
					getTabTextView(1).setCompoundDrawables(null, null, redPoint, null); 
				} else if(bean.getMenuCode().equals(MenuEnum.OrderListCancel.getMenuCode())) {//已取消
					getTabTextView(2).setCompoundDrawables(null, null, redPoint, null); 
				} 
			}			
		}
	}

	@Override
	public void onUiChange(int orderStatus) {
		   Drawable redPoint = new BitmapDrawable(getResources(), 
			    	BitmapFactory.decodeResource(getResources(), R.drawable.main_red_point_17));
			       redPoint.setBounds(0, 0, 24, 24);
	if(this.mCurPos== 0) {
		 if(orderStatus==Properties.INFO_FEE_STATUS_COMPLETE) {
	        	getTabTextView(1).setCompoundDrawables(null, null, redPoint, null); 
	        	NotifyService.updateTabByCode(MenuEnum.OrderListDone.getMenuCode(), 
	        			NotifyService.isShow );
	        } else if(orderStatus==Properties.INFO_FEE_STATUS_CANCEL) {
	        	getTabTextView(2).setCompoundDrawables(null, null, redPoint, null); 
	        	NotifyService.updateTabByCode(MenuEnum.OrderListCancel.getMenuCode(), 
	        			NotifyService.isShow );
	        }
	}	
		
	}
  
}
