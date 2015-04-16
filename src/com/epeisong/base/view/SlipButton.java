package com.epeisong.base.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.epeisong.R;
import com.epeisong.utils.ToastUtils;

public class SlipButton extends View implements OnTouchListener, OnGestureListener {

    private boolean NowChoose = false;// 记录当前按钮是否打开,true为打开,flase为关闭
    private boolean OnSlip = false;// 记录用户是否在滑动的变量
    private float DownX, NowX;// 按下时的x,当前的x,
    private Rect Btn_On, Btn_Off;// 打开和关闭状态下,游标的Rect

    private boolean isChgLsnOn = false;
    private SlipButtonChangeListener ChgLsn;

    private Bitmap bg_on, bg_off, slip_btn;
    private int mOpenBg = R.drawable.slip_bg_on;
    private int mCloseBg = R.drawable.slip_bg_off;
    private int mClickBg = R.drawable.slip_btn;

    private GestureDetector mGestureDetector;

    public SlipButton(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        init();
    }

    public SlipButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        init();
    }

    private void init() {// 初始化
        // 载入图片资源
        bg_on = BitmapFactory.decodeResource(getResources(), mOpenBg);
        bg_off = BitmapFactory.decodeResource(getResources(), mCloseBg);
        slip_btn = BitmapFactory.decodeResource(getResources(), mClickBg);
        // 获得需要的Rect数据
        Btn_On = new Rect(0, 0, slip_btn.getWidth(), slip_btn.getHeight());
        Btn_Off = new Rect(bg_off.getWidth() - slip_btn.getWidth(), 0, bg_off.getWidth(), slip_btn.getHeight());
        setOnTouchListener(this);// 设置监听器,也可以直接复写OnTouchEvent

        if (mGestureDetector == null) {
            mGestureDetector = new GestureDetector(getContext(), this);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {// 绘图函数
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        Matrix matrix = new Matrix();
        Paint paint = new Paint();
        float x;

        {
            if (OnSlip)// 是否是在滑动状态,
            {
                if (NowX < (bg_on.getWidth() / 2))// 滑动到前半段与后半段的背景不同,在此做判断
                    canvas.drawBitmap(bg_off, matrix, paint);// 画出关闭时的背景
                else
                    canvas.drawBitmap(bg_on, matrix, paint);// 画出打开时的背景

                if (NowX >= bg_on.getWidth())// 是否划出指定范围,不能让游标跑到外头,必须做这个判断
                    x = bg_on.getWidth() - slip_btn.getWidth() / 2;// 减去游标1/2的长度...
                else
                    x = NowX - slip_btn.getWidth() / 2;
            } else {// 非滑动状态
                if (NowChoose)// 根据现在的开关状态设置画游标的位置
                {
                    x = Btn_Off.left;
                    canvas.drawBitmap(bg_on, matrix, paint);// 画出打开时的背景
                } else {
                    x = Btn_On.left;
                    canvas.drawBitmap(bg_off, matrix, paint);// 画出关闭时的背景
                }
            }

            if (x < 0)// 对游标位置进行异常判断...
            {
                // Log.e("游标x："+x);
                x = 0;
            } else if (x > bg_on.getWidth() - slip_btn.getWidth())
                x = bg_on.getWidth() - slip_btn.getWidth();
            canvas.drawBitmap(slip_btn, x, 0, paint);// 画出游标.
        }
    }

    public boolean onTouch(View v, MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        }
        // TODO Auto-generated method stub
        switch (event.getAction())// 根据动作来执行代码
        {
        case MotionEvent.ACTION_MOVE:// 滑动
            NowX = event.getX();
            break;
        case MotionEvent.ACTION_DOWN:// 按下
            if (event.getX() > bg_on.getWidth() || event.getY() > bg_on.getHeight())
                return false;
            OnSlip = true;
            DownX = event.getX();
            NowX = DownX;
            break;
        case MotionEvent.ACTION_UP:// 松开
            OnSlip = false;
            // Log.w("u Btn_Off.left:"+Btn_Off.left);
            // Log.w("u Btn_Off.right:"+Btn_Off.right);
            // Log.w("u bg_on.left:"+Btn_On.left);
            // Log.w("u bg_on.right:"+Btn_On.right);

            boolean LastChoose = NowChoose;
            SlipButton.this.measure(0, 0);
            int w = bg_on.getWidth();

            // int w =SlipButton.this.getMeasuredWidth();
            // Log.i("bg_on w：" + w);
            // Log.e("u event.getX():" + event.getX());
            if (event.getX() >= (w / 2))
                NowChoose = true;
            else
                NowChoose = false;
            if (isChgLsnOn && (LastChoose != NowChoose))// 如果设置了监听器,就调用其方法..
                ChgLsn.OnChanged(NowChoose, this);
            break;
        case MotionEvent.ACTION_CANCEL:
            // NowX =event.getX();
            // Log.w("c Btn_Off.left:"+Btn_Off.left);
            // Log.w("c Btn_Off.right:"+Btn_Off.right);
            // Log.w("c bg_on.left:"+Btn_On.left);
            // Log.w("c bg_on.right:"+Btn_On.right);
            int m = (Btn_Off.right + Btn_On.left) / 2;

            // Log.e("middle x:"+m);
            OnSlip = false;
            LastChoose = NowChoose;
            SlipButton.this.measure(0, 0);
            w = bg_on.getWidth();
            // int w =SlipButton.this.getMeasuredWidth();
            // Log.i("cancel w：" + w);
            // Log.e("c event.getX():" + event.getX());
            if (event.getX() >= (m))
                NowChoose = true;
            else
                NowChoose = false;
            if (isChgLsnOn && (LastChoose != NowChoose))// 如果设置了监听器,就调用其方法..
                ChgLsn.OnChanged(NowChoose, this);
            break;
        default:
        }
        invalidate();// 重画控件
        return true;
    }

    public void SetHistoryChosen(boolean HistoryChosen) {
        NowChoose = HistoryChosen;
        ChgLsn.OnChanged(NowChoose, this);
        invalidate();
    }

    public void setDefaultOpen(boolean open) {
        NowChoose = open;
        invalidate();
    }

    public void toggle() {
        NowChoose = !NowChoose;
        invalidate();
    }

    public void SetOnChangedListener(SlipButtonChangeListener l) {// 设置监听器,当状态修改的时候
        isChgLsnOn = true;
        ChgLsn = l;
    }

    public static class Attr {
        private int openBgResId;
        private int closeBgResId;
        private int clickBgResId;

        public int getOpenBgResId() {
            return openBgResId;
        }

        public Attr setOpenBgResId(int openBgResId) {
            this.openBgResId = openBgResId;
            return this;
        }

        public int getCloseBgResId() {
            return closeBgResId;
        }

        public Attr setCloseBgResId(int closeBgResId) {
            this.closeBgResId = closeBgResId;
            return this;
        }

        public int getClickBgResId() {
            return clickBgResId;
        }

        public Attr setClickBgResId(int clickBgResId) {
            this.clickBgResId = clickBgResId;
            return this;
        }

    }

    public void setAttr(Attr attr) {
        if (attr != null) {
            if (attr.getOpenBgResId() > 0) {
                mOpenBg = attr.getOpenBgResId();
            }
            if (attr.getCloseBgResId() > 0) {
                mCloseBg = attr.getCloseBgResId();
            }
            if (attr.getClickBgResId() > 0) {
                mClickBg = attr.getClickBgResId();
            }
            init();
            invalidate();
        }
    }

    public interface SlipButtonChangeListener {
        abstract void OnChanged(boolean CheckState, SlipButton btn);
    }

    // 手势识别方法
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        if (isChgLsnOn) {// 如果设置了监听器,就调用其方法..
            boolean newStatus = !NowChoose;
            setDefaultOpen(newStatus);
            ChgLsn.OnChanged(newStatus, this);
        }
        OnSlip = false;
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }
}