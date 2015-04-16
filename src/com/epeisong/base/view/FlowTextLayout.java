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
import android.widget.ImageView;
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
public class FlowTextLayout extends LinearLayout implements OnClickListener {

    private int mEditIconSize = DimensionUtls.getPixelFromDpInt(15);

    private boolean mUseAnimation = false;
    private int mLayoutWidth = EpsApplication.getScreenWidth();
    private int mTextMinWidth = DimensionUtls.getPixelFromDpInt(40);
    private int mTextHorizontalSpace = DimensionUtls.getPixelFromDpInt(10);
    private int mTextSizeInSp = 18;
    private int mTextColor = Color.WHITE;
    private int mTextColorSelected = Color.WHITE;
    private int mTextBgResId = R.drawable.shape_content_gray;
    private int mTextBgResIdSelected = R.drawable.shape_content_blue;
    private int mEditIconId;
    private List<Textable> mTextList = new ArrayList<Textable>();
    private List<Textable> mTextListSelected = new ArrayList<FlowTextLayout.Textable>();

    private View mEmptyView;
    private boolean mCanEdit;

    private LinearLayout.LayoutParams mLineParams;
    private LinearLayout.LayoutParams mTextParmas;
    private int mTextPaddingLR = DimensionUtls.getPixelFromDpInt(5);
    private int mTextPaddingTB = DimensionUtls.getPixelFromDpInt(2);

    private Paint mPaint;
    private Rect mRect;

    private OnFlowTextItemClickListener mOnFlowTextItemClickListener;
    private OnFlowTextItemClickEditaleListener mOnFlowTextItemClickEditaleListener;

    public FlowTextLayout(Context context) {
        this(context, null);
    }

    public FlowTextLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        mRect = new Rect();
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag != null && tag instanceof Textable) {
            Textable textable = (Textable) tag;
            if (mCanEdit) {
                if (mOnFlowTextItemClickEditaleListener != null) {
                    mOnFlowTextItemClickEditaleListener.onFlowTextItemClickEditable(textable);
                }
            } else {
                if (mOnFlowTextItemClickListener != null) {
                    mOnFlowTextItemClickListener.onFlowTextItemClick(textable, mTextListSelected.contains(textable));
                }
            }
        }
    }

    private void addViews() {
        removeAllViews();
        if (mTextList == null || mTextList.isEmpty() && mEmptyView != null) {
            addView(mEmptyView);
            return;
        }
        List<Textable> textables = new ArrayList<FlowTextLayout.Textable>();
        textables.addAll(mTextList);

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
        for (Textable item : textables) {
            String text = getText(item);
            mPaint.getTextBounds(text, 0, text.length(), mRect);
            int w = mRect.width();
            if (w < mTextMinWidth) {
                w = mTextMinWidth;
            }
            if (mCanEdit && mEditIconId > 0) {
                w += mEditIconSize;
            }
            wList.add(w);
        }
        boolean bFirstLine = true;
        while (textables.size() > 0) {
            List<Textable> limit = new ArrayList<Textable>();
            int totalWidth = -mTextHorizontalSpace;
            int last = 0;
            do {
                last = wList.remove(0);
                totalWidth += last + mTextPaddingLR * 2 + mTextHorizontalSpace;
                limit.add(textables.remove(0));
            } while (textables.size() > 0 && totalWidth <= mLayoutWidth);
            if (totalWidth > mLayoutWidth + mTextHorizontalSpace && limit.size() > 1) {
                wList.add(0, last);
                textables.add(0, limit.remove(limit.size() - 1));
            }
            if (bFirstLine) {
                addLine(limit, true);
                bFirstLine = false;
            } else {
                addLine(limit, false);
            }
        }
    }

    private void addLine(List<Textable> textList, boolean bFirstLine) {
        LinearLayout ll = new LinearLayout(getContext());
        ll.setOrientation(HORIZONTAL);
        for (int i = 0; i < textList.size(); i++) {
            View v = createItem(textList.get(i));
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

    private View createItem(Textable textable) {
        TextView tv = new TextView(getContext());
        tv.setText(getText(textable));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSizeInSp);
        if (mTextListSelected.contains(textable)) {
            tv.setTextColor(mTextColorSelected);
        } else {
            tv.setTextColor(mTextColor);
        }
        tv.setSingleLine();
        tv.setMinWidth(mTextMinWidth);
        tv.setGravity(Gravity.CENTER);
        if (mCanEdit && mEditIconId > 0) {
            LinearLayout ll = new LinearLayout(getContext());
            ll.setOrientation(LinearLayout.HORIZONTAL);
            ll.setGravity(Gravity.CENTER_VERTICAL);
            ll.addView(tv);
            ImageView iv = new ImageView(getContext());
            iv.setImageResource(mEditIconId);
            ll.addView(iv, mEditIconSize, mEditIconSize);
            ll.setBackgroundResource(mTextBgResId);
            ll.setPadding(mTextPaddingLR, mTextPaddingTB, mTextPaddingLR, mTextPaddingTB);
            ll.setTag(textable);
            ll.setOnClickListener(this);
            return ll;
        } else {
            tv.setTag(textable);
            tv.setOnClickListener(this);
        }
        if (mTextListSelected.contains(textable)) {
            tv.setBackgroundResource(mTextBgResIdSelected);
        } else {
            tv.setBackgroundResource(mTextBgResId);
        }
        tv.setPadding(mTextPaddingLR, mTextPaddingTB, mTextPaddingLR, mTextPaddingTB);
        return tv;
    }

    private String getText(Textable textable) {
        if (textable == null || textable.getText() == null) {
            return "";
        }
        return textable.getText();
    }

    public void setEmptyView(View empty) {
        mEmptyView = empty;
    }

    public void setCanEdit(boolean canEdit) {
        mCanEdit = canEdit;
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
            if (attr.getTextColorSelected() != null) {
                mTextColorSelected = attr.getTextColorSelected();
            }
            if (attr.getTextBgResIdSelected() > 0) {
                mTextBgResIdSelected = attr.getTextBgResIdSelected();
            }
            if (attr.getEditIconId() > 0) {
                mEditIconId = attr.getEditIconId();
                mCanEdit = true;
            }
            if (attr.getItemPaddingLR() > 0) {
                mTextPaddingLR = attr.getItemPaddingLR();
            }
            if (attr.getItemPaddingTB() > 0) {
                mTextPaddingTB = attr.getItemPaddingTB();
            }
        }
    }

    public void setTextList(List<? extends Textable> textList) {
        mTextList.clear();
        if (textList != null && !textList.isEmpty()) {
            mTextList.addAll(textList);
        }
        addViews();
    }

    public void setTextListSelected(List<? extends Textable> textList) {
        mTextListSelected.clear();
        if (textList != null && !textList.isEmpty()) {
            mTextListSelected.addAll(textList);
        }
        addViews();
    }

    public List<Textable> getTextList() {
        return mTextList;
    }

    public void addTextable(Textable textable, boolean isSelected) {
        if (!mTextList.contains(textable)) {
            mTextList.add(textable);
            if (isSelected) {
                mTextListSelected.add(textable);
            }
            addViews();
        }
    }

    public void removeTextable(Textable textable) {
        mTextList.remove(textable);
        mTextListSelected.remove(textable);
        addViews();
    }

    public void setTextableSelected(Textable textable, boolean selected) {
        if (selected) {
            if (!mTextListSelected.contains(textable)) {
                mTextListSelected.add(textable);
            }
        } else {
            if (mTextListSelected.contains(textable)) {
                mTextListSelected.remove(textable);
            }
        }
        addViews();
    }

    public void setOnFlowTextItemClickListener(OnFlowTextItemClickListener listener) {
        mOnFlowTextItemClickListener = listener;
    }

    public void setOnFlowTextItemClickEditaleListener(OnFlowTextItemClickEditaleListener l) {
        mOnFlowTextItemClickEditaleListener = l;
    }

    public interface OnFlowTextItemClickListener {
        void onFlowTextItemClick(Textable textable, boolean isSelected);
    }

    public interface OnFlowTextItemClickEditaleListener {
        void onFlowTextItemClickEditable(Textable textable);
    }

    public interface Textable {
        String getText();
    }

    public static class Attr {
        private Boolean useAnimation;
        private int layoutWidth;
        private int textHorizontalSpace;
        private int textSizeInSp;
        private Integer textColor;
        private int textBgResId;
        private Integer textColorSelected;
        private int textBgResIdSelected;
        private int editIconId;
        private int itemPaddingLR, itemPaddingTB;

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

        public Integer getTextColorSelected() {
            return this.textColorSelected;
        }

        public Attr setTextColorSelected(int textColor) {
            this.textColorSelected = textColor;
            return this;
        }

        public int getTextBgResIdSelected() {
            return textBgResIdSelected;
        }

        public Attr setTextBgResIdSelected(int textBgResIdSelected) {
            this.textBgResIdSelected = textBgResIdSelected;
            return this;
        }

        public int getEditIconId() {
            return editIconId;
        }

        public Attr setEditIconId(int editIconId) {
            this.editIconId = editIconId;
            return this;
        }

        public int getItemPaddingLR() {
            return itemPaddingLR;
        }

        public int getItemPaddingTB() {
            return itemPaddingTB;
        }

        public Attr setItemPadding(int lrInDp, int tbInDp) {
            itemPaddingLR = DimensionUtls.getPixelFromDpInt(lrInDp);
            itemPaddingTB = DimensionUtls.getPixelFromDpInt(tbInDp);
            return this;
        }
    }
}
