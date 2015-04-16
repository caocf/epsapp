package com.epeisong.base.view;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.epeisong.R;
import com.epeisong.utils.DimensionUtls;

/**
 * 密码输入框
 * @author poet
 *
 */
public class PwdInputView extends LinearLayout implements OnTouchListener {

    private EditText mEditText;

    public PwdInputView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        this.setBackgroundResource(R.drawable.common_bg_rect_gray);
        this.setOrientation(HORIZONTAL);
        this.setGravity(Gravity.CENTER_VERTICAL);
        mEditText = new EditText(context);
        mEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        mEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        mEditText.setTextColor(Color.BLACK);
        mEditText.setBackgroundColor(Color.TRANSPARENT);
        this.addView(mEditText, new LayoutParams(0, -2, 1));
        int p10 = DimensionUtls.getPixelFromDpInt(10);
        ImageView iv = new ImageView(context);
        iv.setImageResource(R.drawable.selector_common_icon_eye);
        iv.setPadding(p10, p10, p10, p10);
        iv.setOnTouchListener(this);
        this.addView(iv, new LayoutParams(p10 * 4, p10 * 4));
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            mEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
            mEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            break;
        }
        mEditText.setSelection(mEditText.getText().length());
        return true;
    }

    public void setPwdHint(String hint) {
        mEditText.setHint(hint);
    }

    public void setPwdChangeListener(SimpleTextWatcher watcher) {
        mEditText.addTextChangedListener(watcher);
    }

    public String getPwd() {
        return mEditText.getText().toString();
    }

    public static class SimpleTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
        }

    }
}
