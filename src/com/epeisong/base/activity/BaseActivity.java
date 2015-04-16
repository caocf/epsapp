package com.epeisong.base.activity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.view.CustomTitle;
import com.epeisong.base.view.CustomTitle.Action;
import com.epeisong.base.view.CustomTitle.OnTitleClickListener;
import com.epeisong.base.view.TitleParams;
import com.epeisong.service.notify.NotifyService;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.NotificationUtils;

/**
 * Activity基类：用于统一处理标题栏
 * 
 * @author poet
 * 
 */
public abstract class BaseActivity extends XBaseActivity implements OnTitleClickListener {

    private static boolean sIsTop;

    private static List<WeakReference<BaseActivity>> sActivities = new ArrayList<WeakReference<BaseActivity>>();

    public static void clearActivities() {
        Iterator<WeakReference<BaseActivity>> it = sActivities.iterator();
        while (it.hasNext()) {
            WeakReference<BaseActivity> next = it.next();
            if (next.get() != null) {
                next.get().finish();
            }
            it.remove();
        }
    }

    public void refresh(Object... param) { // 刷新当前页面；暂时不写成abstract，需要监听更新的界面均重写该方法

    }

    private ViewGroup mRoot;
    protected CustomTitle mCustomTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sActivities.add(new WeakReference<BaseActivity>(this));
        NotifyService.addActivity(this);
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.setContentView(R.layout.base_activity_layout);
        mRoot = (ViewGroup) findViewById(R.id.ll_root);

        initTitle();

    }

    @Override
    protected void onDestroy() {
        Iterator<WeakReference<BaseActivity>> it = sActivities.iterator();
        while (it.hasNext()) {
            WeakReference<BaseActivity> next = it.next();
            if (next.get() == null || next.get() == this) {
                it.remove();
            }
        }
        super.onDestroy();
        NotifyService.removeActivity(this);
       if(mCustomTitle != null) {
    	   mCustomTitle.setDestory();
    	    mCustomTitle.removeAllViews();
    	   mCustomTitle = null;
       }
       System.gc();
    }

    @Override
    public boolean onTitleClick(TextView titleTv) {
        return false;
    }

    @Override
    public void onTitleLeftClick(TextView titleLeftTv) {
    }

    /**
     * 获取标题栏初始化参数<br>
     * TitleParams为null，不显示title<br>
     * @return
     */
    protected abstract TitleParams getTitleParams();

    private void initTitle() {
        mCustomTitle = (CustomTitle) findViewById(R.id.custom_title);
        mCustomTitle.setOnTitleClickListener(this);
        TitleParams params = getTitleParams();
        if (params != null) {
            setHomeAction(params);
            String title = params.getTitle();
            if (!TextUtils.isEmpty(title)) {
                mCustomTitle.setTitle(title);
                if (params.getTitleBackgroudResource() > 0) {
                    mCustomTitle.setTitleBackgroudResource(params.getTitleBackgroudResource());
                }
            }
            addActions(params.getActions());
            if (params.isShowLeftTitle()) {
                mCustomTitle.setShowTitleLeft();
            }
        } else {
            mCustomTitle.setVisibility(View.GONE);
        }

        // 标题颜色
        mCustomTitle.setBackgroundResource(R.color.main_bg);
    }

    private void setHomeAction(TitleParams params) {
        if (params == null) {
            return;
        }
        Action action = params.getHomeAction();
        boolean enable = params.isHomeActionEnable();
        boolean showLogo = params.isShowLogo();
        mCustomTitle.setHomeAction(action, enable, showLogo);
    }

    protected Action getDefaultHomeAction() {
        return new Action() {
            @Override
            public int getDrawable() {
                return R.drawable.base_title_back;
            }

            @Override
            public View getView() {
                return null;
            }

            @Override
            public void doAction(View v) {
                // ToastUtils.showToast("home back");
                // 隐藏软键盘
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                finish();
            }
        };
    }

    public RelativeLayout getTitleContainer() {
        return mCustomTitle.getTitleContainer();
    }

    protected TextView getRightTextView(String txt, int bgResId) {
        TextView tv = new TextView(getApplicationContext());
        tv.setText(txt);
        if (bgResId != 0) {
            tv.setBackgroundResource(bgResId);
        } else {
            tv.setBackgroundResource(R.color.blue);
        }
        int padding = (int) DimensionUtls.getPixelFromDp(5);
        tv.setPadding(padding * 2, padding, padding * 2, padding);
        tv.setGravity(Gravity.CENTER);
        return tv;
    }

    protected ImageButton getImageButton(int resId) {
        ImageButton btn = new ImageButton(this);
        int height = getResources().getDimensionPixelSize(R.dimen.custom_title_item_height);
        int width = getResources().getDimensionPixelSize(R.dimen.custom_title_item_width);
        android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(height, width);
        params.gravity = Gravity.CENTER_VERTICAL;
        btn.setLayoutParams(params);
        int padding = (int) DimensionUtls.getPixelValue(TypedValue.COMPLEX_UNIT_DIP, 5);
        btn.setPadding(padding, padding, padding, padding);
        btn.setBackgroundResource(R.drawable.selector_custom_title_item);
        btn.setImageResource(resId);
        return btn;
    }

    // 添加Action
    private void addActions(List<Action> actions) {
        if (actions != null && !actions.isEmpty()) {
            mCustomTitle.addActions(actions);
        }
    }

    protected void setTitleText(String title) {
        mCustomTitle.setTitle(title);
    }

    @Override
    public void setContentView(int layoutResID) {
        View view = View.inflate(this, layoutResID, null);
        setContentView(view);
    }

    @Override
    public void setContentView(View view) {
        setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
        mRoot.addView(view, params);
    }

    public void addContentViewSuper(View view, LayoutParams params) {
        super.addContentView(view, params);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        sIsTop = true;
        NotificationUtils.cancel(-1);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sIsTop = false;
    }

    public static boolean isTop() {
        return sIsTop;
    }
    
   
}
