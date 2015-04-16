package com.epeisong.base.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.epeisong.EpsApplication;
import com.epeisong.R;
import com.epeisong.utils.DimensionUtls;

/**
 * 智能的LinearLayout，横向显示TextView，自动换行 <br>
 * 使用：<br>
 * 1、setAttr(Attr attr) 可选<br>
 * 2、setTextList
 * 
 * @author poet
 * 
 */
public class FlowTextLayout_old extends LinearLayout implements OnClickListener {

    private boolean mUseAnimation = false;
    private int mLayoutWidth = EpsApplication.getScreenWidth();
    private int mTextMinWidth = DimensionUtls.getPixelFromDpInt(55);
    private int mTextHorizontalSpace = DimensionUtls.getPixelFromDpInt(10);
    private int mTextSizeInSp = 18;
    private int mTextColor = Color.WHITE;
    private int mTextBgResId = R.drawable.selector_shape_content_blue;
//    private int mTextBgResId = R.drawable.choose_contacts_bg;
    private List<String> mTextList = new ArrayList<String>();

    private LinearLayout.LayoutParams mLineParams;
    private LinearLayout.LayoutParams mTextParmas;
    private int mTextPaddingLR = DimensionUtls.getPixelFromDpInt(5);
    private int mTextPaddingTB = DimensionUtls.getPixelFromDpInt(2);

    private Paint mPaint;
    private Rect mRect;

    private OnFlowTextItemClickListener mOnFlowTextItemClickListener;

    public FlowTextLayout_old(Context context) {
        this(context, null);
    }

    public FlowTextLayout_old(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        mRect = new Rect();
    }

    @Override
    public void onClick(View v) {
        if (mOnFlowTextItemClickListener != null) {
            Object tag = v.getTag();
            if (tag != null && tag instanceof String) {
                mOnFlowTextItemClickListener.onFlowTextItemClick((String) tag);
            }
        }
    }

    private void addViews() {
        removeAllViews();
        if (mPaint == null) {
            mPaint = new Paint();
            mPaint.setTextSize(DimensionUtls.getPixelValue(TypedValue.COMPLEX_UNIT_SP, mTextSizeInSp));
        }
        if (mTextParmas == null) {
            mTextParmas = new LayoutParams(-2, -2);
            mTextParmas.leftMargin = mTextHorizontalSpace;
        }
        if (mLineParams == null) {
            mLineParams = new LayoutParams(-1, -2);
            mLineParams.topMargin = mTextHorizontalSpace;
        }
        List<Integer> wList = new ArrayList<Integer>();
        for (String text : mTextList) {
            mPaint.getTextBounds(text, 0, text.length(), mRect);
            int w = mRect.width();
            if (w < mTextMinWidth) {
                w = mTextMinWidth;
            }
            wList.add(w);
        }
        boolean bFirstLine = true;
        while (mTextList.size() > 0) {
            List<String> limit = new ArrayList<String>();
            int totalWidth = -mTextHorizontalSpace;
            int last = 0;
            do {
                last = wList.remove(0);
                totalWidth += last + mTextPaddingLR * 2 + mTextHorizontalSpace;
                limit.add(mTextList.remove(0));
            } while (mTextList.size() > 0 && totalWidth <= mLayoutWidth);
            if (totalWidth > mLayoutWidth + mTextHorizontalSpace && limit.size() > 1) {
                wList.add(0, last);
                mTextList.add(0, limit.remove(limit.size() - 1));
            }
            if (bFirstLine) {
                addLine(limit, true);
                bFirstLine = false;
            } else {
                addLine(limit, false);
            }
        }
    }

    private void addLine(List<String> textList, boolean bFirstLine) {
        LinearLayout ll = new LinearLayout(getContext());
        ll.setOrientation(HORIZONTAL);
        for (int i = 0; i < textList.size(); i++) {
            String text = textList.get(i);
            View v = createTextView(text);
            if (mUseAnimation) {
                v.setAnimation(AnimationUtils.makeInAnimation(getContext(), i < textList.size() / 2));
            }
            if (i == 0) {
                ll.addView(v);
            } else {
                ll.addView(v, mTextParmas);
            }
        }
        if (bFirstLine) {
            this.addView(ll);
        } else {
            this.addView(ll, mLineParams);
        }
    }

    private TextView createTextView(String text) {
        TextView tv = new TextView(getContext());
        tv.setText(text);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSizeInSp);
        tv.setTextColor(mTextColor);
        tv.setSingleLine();
        tv.setMinWidth(mTextMinWidth);
        tv.setGravity(Gravity.CENTER);
        tv.setBackgroundResource(mTextBgResId);
        tv.setPadding(mTextPaddingLR, mTextPaddingTB, mTextPaddingLR, mTextPaddingTB);
        tv.setTag(text);
        tv.setOnClickListener(this);
        return tv;
    }

    /**
     * 需要在setTextList(List<String> textList)之前调用
     * 
     * @param attr
     */
    public void setAttr(Attr attr) {
        if (attr != null) {
            if (attr.getUseAnimaton() != null) {
                mUseAnimation = attr.getUseAnimaton();
            }
            if (attr.getLayoutWidth() > 0) {
                mLayoutWidth = attr.getLayoutWidth();
            }
            if (attr.getTextHorizontalSpace() > 0) {
                mTextHorizontalSpace = attr.getTextHorizontalSpace();
            }
            if (attr.getTextSizeInSp() > 0) {
                mTextSizeInSp = attr.getTextSizeInSp();
            }
            if (attr.getTextColor() != null) {
                mTextColor = attr.getTextColor();
            }
            if (attr.getTextBgResId() > 0) {
                mTextBgResId = attr.getTextBgResId();
            }
        }
    }

    public void setTextList(List<String> textList) {
        mTextList.clear();
        mTextList.addAll(textList);
        addViews();
    }

    public void setOnFlowTextItemClickListener(OnFlowTextItemClickListener listener) {
        mOnFlowTextItemClickListener = listener;
    }

    public interface OnFlowTextItemClickListener {
        void onFlowTextItemClick(String text);
    }

    public static class Attr {
        private Boolean useAnimation;
        private int layoutWidth;
        private int textHorizontalSpace;
        private int textSizeInSp;
        private Integer textColor;
        private int textBgResId;

        public Boolean getUseAnimaton() {
            return useAnimation;
        }

        public Attr setUseAnimation(Boolean useAnimation) {
            this.useAnimation = useAnimation;
            return this;
        }

        public int getLayoutWidth() {
            return layoutWidth;
        }

        public Attr setLayoutWidth(int layoutWidth) {
            this.layoutWidth = layoutWidth;
            return this;
        }

        public int getTextHorizontalSpace() {
            return textHorizontalSpace;
        }

        public Attr setTextHorizontalSpace(int textHorizontalSpace) {
            this.textHorizontalSpace = textHorizontalSpace;
            return this;
        }

        public int getTextSizeInSp() {
            return textSizeInSp;
        }

        public Attr setTextSizeInSp(int textSizeInSp) {
            this.textSizeInSp = textSizeInSp;
            return this;
        }

        public Integer getTextColor() {
            return textColor;
        }

        public Attr setTextColor(int textColor) {
            this.textColor = textColor;
            return this;
        }

        public int getTextBgResId() {
            return textBgResId;
        }

        public Attr setTextBgResId(int textBgResId) {
            this.textBgResId = textBgResId;
            return this;
        }

    }
}
