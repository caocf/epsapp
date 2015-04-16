package com.epeisong.base.view;

import java.lang.ref.WeakReference;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.epeisong.EpsApplication;
import com.epeisong.R;
import com.epeisong.utils.DimensionUtls;
import com.test.log.LogcatService;

/**
 * 自定义标题栏
 * 
 * @author poet
 */
public class CustomTitle extends RelativeLayout implements OnClickListener {

    private Context mContext;
    private LayoutInflater mInflater;
    private RelativeLayout mTitleView;
    private RelativeLayout mHomeRl;
    private ImageView mHomeBtn;
    private ImageView mHomeLogo;
    private LinearLayout mActionsLl;
    private TextView mTitleTv;
    private TextView mTitleTv2;
    private RelativeLayout mTitleContainer;

    private OnTitleClickListener mOnTitleClickListener;

    public CustomTitle(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mTitleView = (RelativeLayout) mInflater.inflate(R.layout.custom_title, null);
        addView(mTitleView);

        mHomeRl = (RelativeLayout) mTitleView.findViewById(R.id.home_rl);
        mHomeBtn = (ImageView) mTitleView.findViewById(R.id.home_btn);
        mHomeLogo = (ImageView) mTitleView.findViewById(R.id.iv_logo);
        mHomeBtn.setVisibility(View.GONE);
        mActionsLl = (LinearLayout) mTitleView.findViewById(R.id.actions_ll);
        mTitleTv = (TextView) mTitleView.findViewById(R.id.title_tv);
        mTitleTv.setOnClickListener(this);
        mTitleContainer = (RelativeLayout) mTitleView.findViewById(R.id.rl_title_container);
        mTitleTv2 = (TextView) mTitleView.findViewById(R.id.title_tv_2);
        mTitleTv2.setVisibility(View.GONE);
        mTitleTv2.setOnClickListener(this);
    }

    public void setHomeAction(Action action, boolean enable, boolean showLogo) {
        mHomeRl.setVisibility(View.VISIBLE);
        mHomeRl.setClickable(enable);
        if (enable && action != null) {
            mHomeBtn.setVisibility(View.VISIBLE);
            mHomeBtn.setImageResource(action.getDrawable());
            mHomeRl.setOnClickListener(this);
            mHomeRl.setTag(action);
        }
        if (!showLogo) {
            mHomeLogo.setVisibility(View.GONE);
            // 不显示logo时，处理title居中。这里使用另外一个textview，此乃权宜之计
            mTitleTv.setVisibility(View.GONE);
            mTitleTv2.setVisibility(View.VISIBLE);
        }
    }

    public RelativeLayout getTitleContainer() {
        return mTitleContainer;
    }

    public void setTitle(String title) {
        mTitleTv.setText(title);
        mTitleTv2.setText(title);
    }

    public void setTitle(int resId) {
        mTitleTv.setText(resId);
        mTitleTv2.setText(resId);
    }

    public View getHomeActionContainer() {
        return mHomeRl;
    }

    public void setShowTitleLeft() {
        mTitleTv.setVisibility(View.VISIBLE);
        mTitleTv2.setVisibility(View.GONE);
    }

    public void setTitleBackgroudResource(int resId) {
        mTitleTv.setBackgroundResource(resId);
    }

    public void setOnTitleClickListener(OnTitleClickListener l) {
        mOnTitleClickListener = l;
    }

    public void addAction( Action  action) {
        View view = action .getView();
        if (view == null) {
            ImageButton btn = new ImageButton(mContext);
            int height = getResources().getDimensionPixelSize(R.dimen.custom_title_item_height);
            int width = getResources().getDimensionPixelSize(R.dimen.custom_title_item_width);
            android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(height,
                    width);
            params.gravity = Gravity.CENTER_VERTICAL;
            btn.setLayoutParams(params);
            int padding = (int) DimensionUtls.getPixelValue(TypedValue.COMPLEX_UNIT_DIP, 5);
            btn.setPadding(padding, padding, padding, padding);
            btn.setBackgroundResource(R.drawable.selector_custom_title_item);
            btn.setImageResource(action.getDrawable());
            view = btn;
        } else {
            if (view.getLayoutParams() == null) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, -1);
                params.gravity = Gravity.CENTER_VERTICAL;
                view.setLayoutParams(params);
                int p = DimensionUtls.getPixelFromDpInt(5);
                view.setPadding(p, 0, p, 0);
            }
            view.setBackgroundResource(R.drawable.selector_custom_title_item);
        }
        if (!(view instanceof AdapterView)) {
            view.setOnClickListener(this);
        }
        view.setTag(action);
        mActionsLl.addView(view);
    }

    public void addActions(List< Action > actions) {
        for ( Action  action : actions) {
            addAction(action);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.title_tv:
            if (mOnTitleClickListener != null) {
                mOnTitleClickListener.onTitleLeftClick(mTitleTv);
            }
            break;
        case R.id.title_tv_2:
            if (mOnTitleClickListener == null || !mOnTitleClickListener.onTitleClick(mTitleTv2)) {
                if (EpsApplication.DEBUGGING) {
                    Intent i = new Intent(mContext, LogcatService.class);
                    mContext.startService(i);
                    return;
                }
            }
            break;
        }
        Object tag = v.getTag();
        if (tag != null && tag instanceof Action) {
            Action action = (Action) tag;
            action.doAction(v);
        }
    }

    public interface OnTitleClickListener {
        void onTitleLeftClick(TextView titleLeftTv);

        boolean onTitleClick(TextView titleTv);
    }

    public interface  Action {
        int getDrawable();

        View getView();

        void doAction(View v);
    }

    public static abstract class ActionImpl implements Action {
        @Override
        public int getDrawable() {
            return 0;
        }

        @Override
        public View getView() {
            return null;
        }

        @Override
        public void doAction(View v) {
        }

    }

 
	public void setDestory( ) {
	    this.mTitleTv = null;
	    this.mTitleTv2 = null;
	    this.mTitleContainer.removeAllViews();
	    mTitleContainer = null;
		this.mActionsLl.removeAllViews();
		this.mTitleView.removeAllViews();
		this.mActionsLl = null;
		this.mTitleView = null;
	}
}
