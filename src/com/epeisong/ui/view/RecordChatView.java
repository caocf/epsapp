package com.epeisong.ui.view;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.base.view.AdjustHeightGridView;
import com.epeisong.base.view.indicator.CirclePageIndicator;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.FileUtils;
import com.epeisong.utils.HandlerUtils;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;

/**
 * 语音聊天的View
 * 
 * @author poet 存在bug: 快速按下时，MediaRecorder的stop和start处理不完善 快速抬起时，PopupWindow没有消失
 *         auido处理时，post(Runnable)泄漏 -- 处理：将MediaRecord抽取出来
 */
public class RecordChatView extends FrameLayout implements OnClickListener, TextWatcher, OnTouchListener,
        OnItemClickListener {

    private OnRecordChangeListener mOnRecordChangeListener;
    private OnRecordListener mOnRecordListener;
    private OnSendListener mOnSendListener;
    private OnMoreActionListener mOnMoreActionListener;

    private ImageView mSwitchIv;
    private EditText mMsgEt;
    private TextView mRecordBtnTv;
    private View mSendBtn;
    private View mMoreView;
    private ViewPager mViewPager;

    private RecordPopupHelper mPopupHelper;

    private int[] mRecordBtnLocation;
    private boolean mCanRecordable; // 是否是可以录音界面
    private boolean mIsTouching; // 是否touch到RecordBtnTv
    private boolean mIsPressed; // 是否已经按下

    private MediaRecorder mRecorder;
    private boolean mIsCancel; // 是否丢弃当前录音
    private AtomicBoolean mIsRecorderStarted = new AtomicBoolean(false);

    private String mRecordPath;
    private long mStartTime;

    private Runnable mPrepareRunnable = new Runnable() {
        @Override
        public void run() {
            mStartTime = 0;
            mRecordPath = FileUtils.getRecordFileDir() + File.separator + System.currentTimeMillis();
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); // 指定音频来源：麦克风
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP); // 设置文件输出格式：3gp
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB); // 设置编码方式：amr窄带
            mRecorder.setOutputFile(mRecordPath);
            try {
                mRecorder.prepare();
                // post(mStartRunnable);
                new Thread(mStartRunnable).start();
            } catch (IllegalStateException e) {
                e.printStackTrace();
                ToastUtils.showToast("录音初始化失败！");
            } catch (IOException e) {
                e.printStackTrace();
                ToastUtils.showToast("录音初始化失败！");
            }
        }
    };

    private Runnable mStartRunnable = new Runnable() {
        @Override
        public void run() {
            post(new Runnable() {
                @Override
                public void run() {
                    onRecordStateChange(OnRecordChangeListener.STATE_START);
                }
            });
            if (mRecorder != null && !mIsRecorderStarted.get()) {
                try {
                    mStartTime = System.currentTimeMillis();
                    mRecorder.start();
                    mIsRecorderStarted.compareAndSet(false, true);
                    new Thread(mAudioRunnable).start();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private Runnable mAudioRunnable = new Runnable() {
        @Override
        public void run() {
            if (mOnRecordChangeListener == null || !mIsRecorderStarted.get()) {
                return;
            }
            final float result = mRecorder.getMaxAmplitude() / (float) 32768;
            post(new Runnable() {
                @Override
                public void run() {
                    mOnRecordChangeListener.onAudioChange(result);
                    if (mIsRecorderStarted.get())
                        postDelayed(mAudioRunnable, 100);
                }
            });
        }
    };

    private Runnable mStopRunnable = new Runnable() {
        @Override
        public void run() {
            if (mRecorder != null && mIsRecorderStarted.get()) {
                try {
                    final int duration = (int) ((System.currentTimeMillis() - mStartTime) / 1000);
                    mRecorder.stop();
                    mRecorder.reset();
                    mRecorder.release();
                    mRecorder = null;
                    mIsRecorderStarted.compareAndSet(true, false);
                    if (!mIsCancel) {
                        if (mOnRecordChangeListener != null) {
                            if (mOnRecordListener != null) {
                                HandlerUtils.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mOnRecordListener.onRecordComplete(mRecordPath, duration);
                                    }
                                });
                            }
                        }
                    }
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public RecordChatView(Context context) {
        this(context, null);
    }

    public RecordChatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.one_chat_view, this);
        mSwitchIv = (ImageView) findViewById(R.id.iv_switch);
        mSwitchIv.setOnClickListener(this);
        mMsgEt = (EditText) findViewById(R.id.et_msg);
        mMsgEt.addTextChangedListener(this);
        mMsgEt.setOnClickListener(this);
        mRecordBtnTv = (TextView) findViewById(R.id.tv_record_btn);
        mRecordBtnTv.setOnTouchListener(this);
        mSendBtn = findViewById(R.id.tv_send);
        mSendBtn.setOnClickListener(this);
        findViewById(R.id.iv_more).setOnClickListener(this);
        mMoreView = findViewById(R.id.ll_more);
        mViewPager = (ViewPager) findViewById(R.id.vp);
        CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.indicator);
        final List<GridView> gridViews = createGridViews();
        mViewPager.setAdapter(new PagerAdapter() {
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View v = gridViews.get(position);
                container.addView(v);
                return v;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public int getCount() {
                return gridViews.size();
            }
        });
        indicator.setViewPager(mViewPager);

        mPopupHelper = new RecordPopupHelper(context, mRecordBtnTv);
        this.setOnRecordstateChangeListener(mPopupHelper);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        mSendBtn.setEnabled(mMsgEt.getText().toString().length() > 0);
        if (mMsgEt.getText().toString().length() > 0) {
            ((TextView) mSendBtn).setTextColor(getResources().getColor(R.color.white));
        } else {
            ((TextView) mSendBtn).setTextColor(getResources().getColor(R.color.darker_gray));
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_switch) {
            if (mCanRecordable) {
                mSwitchIv.setImageResource(R.drawable.chatting_setmode_voice_btn_normal);
                mMsgEt.setVisibility(View.VISIBLE);
                mRecordBtnTv.setVisibility(View.GONE);
                mSendBtn.setClickable(true);
            } else {
                mSwitchIv.setImageResource(R.drawable.chatting_setmode_keyboard_btn_normal);
                mRecordBtnTv.setVisibility(View.VISIBLE);
                mMsgEt.setVisibility(View.GONE);
                mSendBtn.setClickable(false);
                hideInputMethod();
            }
            mCanRecordable = !mCanRecordable;
        } else if (id == R.id.tv_send) {
            if (mOnSendListener != null) {
                String msg = mMsgEt.getText().toString();
                if (TextUtils.isEmpty(msg)) {
                    return;
                }
                mOnSendListener.onSend(msg);
                mMsgEt.setText("");
            }
        } else if (id == R.id.iv_more) {
            if (mMoreView.getVisibility() == View.GONE) {
                SystemUtils.hideInputMethod(mMsgEt);
                mMoreView.setVisibility(View.VISIBLE);
            } else {
                mMoreView.setVisibility(View.GONE);
            }
        } else if (id == R.id.et_msg) {
            if (mMoreView.getVisibility() == View.VISIBLE) {
                mMoreView.setVisibility(View.GONE);
                SystemUtils.showInputMethod(mMsgEt);
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
        case MotionEvent.ACTION_MOVE:
            mIsTouching = true;
            break;
        default:
            mIsTouching = false;
            break;
        }
        return true;
    }

    public boolean handleTouchEvent(MotionEvent ev) {
        if (!mIsTouching) {
            return false;
        }
        if (!mCanRecordable) {
            return false;
        }
        if (!mIsPressed) {
            if (mRecordBtnLocation == null) {
                mRecordBtnLocation = new int[2];
                mRecordBtnTv.getLocationInWindow(mRecordBtnLocation);
            }
            if (isPressedSpeak(ev)) {
                mIsPressed = true;
                mRecordBtnTv.setEnabled(false); // enable:已经按下
                mRecordBtnTv.setText("松开 结束");
                onRecordStateChange(OnRecordChangeListener.STATE_PREPARE);
                new Thread(mPrepareRunnable).start();
                return true;
            }
        } else {
            switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dy = ev.getY();
                int divide = mRecordBtnLocation[1] - mRecordBtnTv.getHeight() * 2;
                if (dy > divide) {
                    if (mIsCancel) {
                        mIsCancel = false;
                        onRecordStateChange(OnRecordChangeListener.STATE_NORMAL);
                        mRecordBtnTv.setText("松开 结束");
                    }
                } else {
                    if (!mIsCancel) {
                        mIsCancel = true;
                        onRecordStateChange(OnRecordChangeListener.STATE_CANCELABLE);
                        mRecordBtnTv.setText("松开手指，取消发送");
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                onRecordStateChange(OnRecordChangeListener.STATE_STOP);

                new Thread(mStopRunnable).start();

                mIsPressed = false;
                mRecordBtnTv.setEnabled(true);
                mRecordBtnTv.setText("按住 说话");
                break;
            }
            return true;
        }
        return false;
    }

    private boolean isPressedSpeak(MotionEvent ev) {
        float tx = ev.getX();
        float ty = ev.getY();
        if (tx < mRecordBtnLocation[0] || tx > mRecordBtnLocation[0] + mRecordBtnTv.getWidth()
                || ty < mRecordBtnLocation[1] || ty > mRecordBtnLocation[1] + mRecordBtnTv.getHeight()) {
            return false;
        }
        return true;
    }

    private void onRecordStateChange(int state) {
        if (mOnRecordChangeListener != null)
            mOnRecordChangeListener.onStateChange(state);
    }

    public void setOnRecordstateChangeListener(OnRecordChangeListener listener) {
        mOnRecordChangeListener = listener;
    }

    public void setOnRecordListener(OnRecordListener listener) {
        mOnRecordListener = listener;
    }

    public void setOnSendListener(OnSendListener listener) {
        mOnSendListener = listener;
    }

    public void setText(String text) {
        mMsgEt.setText(text);
    }

    public String getText() {
        return mMsgEt.getText().toString();
    }

    public interface OnRecordChangeListener {
        int STATE_PREPARE = 1;
        int STATE_START = 2;
        int STATE_NORMAL = 3;
        int STATE_CANCELABLE = 4;
        int STATE_STOP = 5;

        void onStateChange(int state);

        void onAudioChange(float audio);
    }

    public interface OnRecordListener {
        void onRecordComplete(String path, int duration);
    }

    public interface OnSendListener {
        void onSend(String msg);
    }

    public void hideInputMethod() {
        SystemUtils.hideInputMethod(mMsgEt);
        mMoreView.setVisibility(View.GONE);
    }

    public boolean hideMoreView() {
        if (mMoreView.getVisibility() == View.VISIBLE) {
            mMoreView.setVisibility(View.GONE);
            return true;
        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object tag = view.getTag(R.id.vp);
        if (tag != null && tag instanceof Item) {
            Item item = (Item) tag;
            if (mOnMoreActionListener != null) {
                mOnMoreActionListener.onMoreAction(item.action);
            }
        }
    }

    private List<GridView> createGridViews() {
        List<GridView> result = new ArrayList<GridView>();
        List<Item> items1 = new ArrayList<RecordChatView.Item>();
        items1.add(new Item("图片", R.drawable.chatting_more_photo, MoreAction.PHOTO));
        items1.add(new Item("拍照", R.drawable.chatting_more_camera, MoreAction.CAMERA));
        items1.add(new Item("位置", R.drawable.chatting_more_location, MoreAction.LOCATION));
        result.add(createGridView(items1));
        return result;
    }

    private GridView createGridView(List<Item> items) {
        GridView gridView = new AdjustHeightGridView(getContext());
        HoldDataBaseAdapter<Item> adapter = new HoldDataBaseAdapter<RecordChatView.Item>() {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder holder = null;
                if (convertView == null) {
                    holder = new ViewHolder();
                    convertView = holder.createView();
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                Item item = getItem(position);
                holder.fillData(item);
                convertView.setTag(R.id.vp, item);
                return convertView;
            }
        };
        adapter.replaceAll(items);
        gridView.setAdapter(adapter);
        gridView.setSelector(R.color.transparent);
        gridView.setNumColumns(3);
        gridView.setOnItemClickListener(this);
        int p = DimensionUtls.getPixelFromDpInt(10);
        gridView.setVerticalSpacing(p);
        return gridView;
    }

    private class ViewHolder {
        ImageView iv;
        TextView tv;

        public View createView() {
            LinearLayout ll = new LinearLayout(getContext());
            ll.setOrientation(LinearLayout.VERTICAL);
            ll.setGravity(Gravity.CENTER);
            iv = new ImageView(getContext());
            int w = DimensionUtls.getPixelFromDpInt(45);
            iv.setLayoutParams(new LayoutParams(w, w));
            ll.addView(iv);
            tv = new TextView(getContext());
            tv.setGravity(Gravity.CENTER);
            tv.setTextColor(Color.GRAY);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            ll.addView(tv);
            return ll;
        }

        public void fillData(Item item) {
            iv.setImageResource(item.icon);
            tv.setText(item.name);
        }
    }

    private class Item {
        String name;
        int icon;
        MoreAction action;

        public Item(String name, int icon, MoreAction action) {
            super();
            this.name = name;
            this.icon = icon;
            this.action = action;
        }
    }

    public static enum MoreAction {
        PHOTO, CAMERA, LOCATION
    }

    public void setOnMoreActionListener(OnMoreActionListener listener) {
        this.mOnMoreActionListener = listener;
    }

    public static interface OnMoreActionListener {
        void onMoreAction(MoreAction action);
    }
}
