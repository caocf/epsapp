package com.epeisong.base.view.statusview;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.epeisong.utils.DimensionUtls;

/**
 * 通用状态流程图
 * @author poet
 *
 */
public class StatusLayout extends LinearLayout {

    public StatusLayout(Context context) {
        super(context);
        init();
    }

    public StatusLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    void init() {
        setOrientation(LinearLayout.HORIZONTAL);
    }

    public void showStatus(List<StatusModel> list) {
        this.removeAllViews();
        if (list.size() > 0) {
            int w = DimensionUtls.getPixelFromDpInt(35);
            for (int i = 0; i < list.size(); i++) {
                if (i > 0) {
                    View line = new View(getContext());
                    LayoutParams params = new LayoutParams(DimensionUtls.getPixelFromDpInt(25), 3);
                    params.topMargin = w / 2;
                    params.leftMargin = (int) -DimensionUtls
                            .getPixelFromDp((list.get(i - 1).getText().length() * 14 - 30) / 2 - 10);
                    if (list.get(i).getText().length() > 3) {
                        params.rightMargin = (int) -DimensionUtls
                                .getPixelFromDp((list.get(i).getText().length() * 14 - 30) / 2 - 10);
                    }
                    line.setLayoutParams(params);
                    line.setBackgroundColor(Color.GRAY);
                    this.addView(line);
                }
                StatusModel item = list.get(i);
                LinearLayout ll = new LinearLayout(getContext());
                ll.setOrientation(LinearLayout.VERTICAL);
                ll.setGravity(Gravity.CENTER_HORIZONTAL);
                ImageView iv = new ImageView(getContext());
                iv.setLayoutParams(new LinearLayout.LayoutParams(w, w));
                iv.setImageResource(item.getResId());
                ll.addView(iv);
                TextView tv = new TextView(getContext());
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                tv.setTextColor(Color.BLACK);
                tv.setText(item.getText());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, -2);
                params.topMargin = DimensionUtls.getPixelFromDpInt(10);
                ll.addView(tv, params);
                this.addView(ll);
            }
        }
    }

}
