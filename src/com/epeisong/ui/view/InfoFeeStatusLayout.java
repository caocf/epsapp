package com.epeisong.ui.view;

import java.util.ArrayList;
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

import com.epeisong.R;
import com.epeisong.utils.DimensionUtls;

/**
 * 信息费订单状态流程
 * @author poet
 *
 */
@Deprecated
public class InfoFeeStatusLayout extends LinearLayout {

    public static final int status_commit = 1;
    public static final int status_accept = 2;
    public static final int status_exeing = 3;
    public static final int status_complete = 4;
    public static final int status_cancel_when_commit = -1;
    public static final int status_cancel_when_accept = -2;
    public static final int status_cancel_when_exeing = -3;

    public InfoFeeStatusLayout(Context context) {
        super(context);
        init();
    }

    public InfoFeeStatusLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    void init() {
        setOrientation(LinearLayout.HORIZONTAL);
    }

    public void refresh(StatusObject status) {
        this.removeAllViews();
        List<StatusItem> items = new ArrayList<StatusItem>();
        int commitId, acceptId, exeingId, completeId, cancelId;
        String commitText, acceptText, exeingText, completeText, cancelText;
        exeingText = "执行中";
        completeText = "已完成";
        cancelText = "已取消";
        if (Math.abs(status.status) >= status_commit) {
            commitId = R.drawable.icon_infofee_commit_true;
        } else {
            commitId = R.drawable.icon_infofee_commit_false;
        }
        if (status.isSelfOrder) {
            commitText = "订单已提交";
            acceptText = "对方已接单";
        } else {
            commitText = "对方提交订单";
            acceptText = "已接单";
        }
        if (Math.abs(status.status) >= status_accept) {
            acceptId = R.drawable.icon_infofee_accept_true;
        } else {
            acceptId = R.drawable.icon_infofee_accept_false;
        }
        if (Math.abs(status.status) >= status_exeing) {
            exeingId = R.drawable.icon_infofee_exeing_true;
        } else {
            exeingId = R.drawable.icon_infofee_exeing_false;
        }
        cancelId = R.drawable.icon_infofee_cancel_true;
        switch (status.status) {
        case status_commit:
        case status_accept:
        case status_exeing:
        case status_complete:
            if (status.status >= status_complete) {
                completeId = R.drawable.icon_infofee_complete_true;
            } else {
                completeId = R.drawable.icon_infofee_complete_false;
            }
            items.add(new StatusItem(commitId, commitText));
            items.add(new StatusItem(acceptId, acceptText));
            items.add(new StatusItem(exeingId, exeingText));
            items.add(new StatusItem(completeId, completeText));
            break;
        case status_cancel_when_commit:
        case status_cancel_when_accept:
        case status_cancel_when_exeing:
            items.add(new StatusItem(commitId, commitText));
            items.add(new StatusItem(acceptId, acceptText));
            items.add(new StatusItem(exeingId, exeingText));
            items.add(new StatusItem(cancelId, cancelText));
            break;
        }
        if (items.size() > 0) {
            int w = DimensionUtls.getPixelFromDpInt(35);
            for (int i = 0; i < items.size(); i++) {
                if (i > 0) {
                    View line = new View(getContext());
                    LayoutParams params = new LayoutParams(DimensionUtls.getPixelFromDpInt(25), 3);
                    params.topMargin = w / 2;
                    params.leftMargin = (int) -DimensionUtls
                            .getPixelFromDp((items.get(i - 1).text.length() * 14 - 30) / 2 - 10);
                    if (items.get(i).text.length() > 3) {
                        params.rightMargin = (int) -DimensionUtls
                                .getPixelFromDp((items.get(i).text.length() * 14 - 30) / 2 - 10);
                    }
                    line.setLayoutParams(params);
                    line.setBackgroundColor(Color.GRAY);
                    this.addView(line);
                }
                StatusItem item = items.get(i);
                LinearLayout ll = new LinearLayout(getContext());
                ll.setOrientation(LinearLayout.VERTICAL);
                ll.setGravity(Gravity.CENTER_HORIZONTAL);
                ImageView iv = new ImageView(getContext());
                iv.setLayoutParams(new LinearLayout.LayoutParams(w, w));
                iv.setImageResource(item.iconId);
                ll.addView(iv);
                TextView tv = new TextView(getContext());
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                tv.setTextColor(Color.BLACK);
                tv.setText(item.text);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, -2);
                params.topMargin = DimensionUtls.getPixelFromDpInt(10);
                ll.addView(tv, params);
                this.addView(ll);
            }
        }
    }

    class StatusItem {
        int iconId;
        String text;

        public StatusItem(int iconId, String text) {
            super();
            this.iconId = iconId;
            this.text = text;
        }
    }

    public static class StatusObject {
        int status;
        boolean isSelfOrder;

        public StatusObject() {
        }

        public StatusObject(int status, boolean isSelfOrder) {
            super();
            this.status = status;
            this.isSelfOrder = isSelfOrder;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public void setSelfOrder(boolean isSelfOrder) {
            this.isSelfOrder = isSelfOrder;
        }

    }
}
