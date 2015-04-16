package com.epeisong.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

/**
 * 自定义的View，实现ListView A~Z快速索引效果
 * 
 * @author Folyd
 * 
 */
public class SlideBar extends View {
    private Paint paint = new Paint();
    private OnTouchLetterChangeListenner listenner;
    // 是否画出背景
    private boolean showBg = false;
    // 选中的项
    private int choose = -1;
    // 准备好的A~Z的字母数组
    public static String[] letters = { "定位", "常用", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
            "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };

    private int textSize;

    // 构造方法
    public SlideBar(Context context) {
        this(context, null);
    }

    public SlideBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        textSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, getResources().getDisplayMetrics());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 获取宽和高
        int width = getWidth();
        int height = getHeight();
        // 每个字母的高度
        int singleHeight = height / letters.length;
        if (showBg) {
            // 画出背景
            canvas.drawColor(Color.parseColor("#55000000"));
        }
        // 画字母
        for (int i = 0; i < letters.length; i++) {
            paint.setColor(Color.BLACK);
            // 设置字体格式
            // paint.setTypeface(Typeface.DEFAULT_BOLD);
            paint.setAntiAlias(true);
            paint.setTextSize(textSize);
            // 如果这一项被选中，则换一种颜色画
            if (i == choose) {
                paint.setColor(Color.parseColor("#F88701"));
                paint.setFakeBoldText(true);
            }
            // 要画的字母的x,y坐标
            float posX = width / 2 - paint.measureText(letters[i]) / 2;
            float posY = i * (singleHeight) + singleHeight;
            // 画出字母
            canvas.drawText(letters[i], posX, posY, paint);
            // 重新设置画笔
            paint.reset();
        }
    }

    /**
     * 处理SlideBar的状态
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final float y = event.getY();
        if (y < 0) {
            event.setAction(MotionEvent.ACTION_CANCEL);
        }
        // 算出点击的字母的索引
        final int index = (int) (y / getHeight() * letters.length);
        // 保存上次点击的字母的索引到oldChoose
        final int oldChoose = choose;
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            showBg = true;
            if (oldChoose != index && listenner != null && index >= 0 && index < letters.length) {
                choose = index;
                listenner.onTouchLetterChange(event, letters[index]);
                invalidate();
            }
            break;

        case MotionEvent.ACTION_MOVE:
            if (oldChoose != index && listenner != null && index >= 0 && index < letters.length) {
                choose = index;
                listenner.onTouchLetterChange(event, letters[index]);
                invalidate();
            }
            break;
        case MotionEvent.ACTION_UP:
        default:
            showBg = false;
            choose = -1;
            if (listenner != null && index > 0 && index < letters.length)
                listenner.onTouchLetterChange(event, letters[index]);
            invalidate();
            break;
        }
        return true;
    }

    /**
     * 回调方法，注册监听器
     * 
     * @param listenner
     */
    public void setOnTouchLetterChangeListenner(OnTouchLetterChangeListenner listenner) {
        this.listenner = listenner;
    }

    /**
     * SlideBar 的监听器接口
     * 
     * @author Folyd
     * 
     */
    public interface OnTouchLetterChangeListenner {

        void onTouchLetterChange(MotionEvent event, String s);
    }

}
