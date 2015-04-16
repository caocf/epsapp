package com.epeisong.base.view;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
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
import com.epeisong.base.view.indicator.CirclePageIndicator;
import com.epeisong.ui.view.RecordChatView.OnRecordListener;
import com.epeisong.ui.view.RecordPopupHelper;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.FileUtils;
import com.epeisong.utils.MediaRecorderManager;
import com.epeisong.utils.SystemUtils;

public class ChatInputView extends FrameLayout implements OnClickListener, TextWatcher, OnTouchListener,
        OnItemClickListener {

    private OnRecordListener mOnRecordListener;
    private OnChatInputListener mOnChatInputListener;
    private OnMoreActionListener mOnMoreActionListener;

    private ImageView mSwitchIv;
    private EditText mMsgEt;
    private TextView mRecordBtnTv;
    private View mSendBtn;
    private View mMoreView;
    private ViewPager mViewPager;

    private RecordPopupHelper mPopupHelper;

    private MediaRecorderManager mRecorderManager;

    public ChatInputView(Context context) {
        this(context, null);
    }

    public ChatInputView(Context context, AttributeSet attrs) {
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
        mRecorderManager = MediaRecorderManager.getInstance();
        // this.setOnRecordstateChangeListener(mPopupHelper);
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
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_switch) {
            if (mRecordBtnTv.getVisibility() == View.VISIBLE) {
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
        } else if (id == R.id.tv_send) {
            if (mOnChatInputListener != null) {
                String msg = mMsgEt.getText().toString();
                if (TextUtils.isEmpty(msg)) {
                    return;
                }
                mOnChatInputListener.onChatInputComplete(ChatInput.ChatText, msg);
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object tag = view.getTag(R.id.vp);
        if (tag != null && tag instanceof Item) {
            Item item = (Item) tag;
            if (mOnMoreActionListener != null) {
                mOnMoreActionListener.onMoreAction(item.action);
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            mRecorderManager.start(1000 * 60, mPopupHelper,
                    FileUtils.getRecordFileDir() + File.separator + System.currentTimeMillis());
            break;
        case MotionEvent.ACTION_MOVE:
            
            break;
        default:
            break;
        }
        return false;
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

    private List<GridView> createGridViews() {
        List<GridView> result = new ArrayList<GridView>();
        List<Item> items1 = new ArrayList<Item>();
        items1.add(new Item("图片", R.drawable.chatting_more_photo, MoreAction.PHOTO));
        items1.add(new Item("拍照", R.drawable.chatting_more_camera, MoreAction.CAMERA));
        items1.add(new Item("位置", R.drawable.chatting_more_location, MoreAction.LOCATION));
        result.add(createGridView(items1));
        return result;
    }

    private GridView createGridView(List<Item> items) {
        GridView gridView = new AdjustHeightGridView(getContext());
        HoldDataBaseAdapter<Item> adapter = new HoldDataBaseAdapter<Item>() {

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

    public void setOnMoreActionListener(OnMoreActionListener listener) {
        this.mOnMoreActionListener = listener;
    }

    public void setOnChatInputListener(OnChatInputListener listener) {
        mOnChatInputListener = listener;
    }

    public static interface OnMoreActionListener {
        void onMoreAction(MoreAction action);
    }

    public interface OnChatInputListener {
        void onChatInputComplete(ChatInput input, Object... extra);
    }

    public enum ChatInput {
        ChatText, ChatVoice
    }

    public static enum MoreAction {
        PHOTO, CAMERA, LOCATION
    }
}
