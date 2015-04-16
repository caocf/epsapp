package com.epeisong.ui.view;

import java.io.IOException;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.model.ChatMsg;
import com.epeisong.utils.DateUtil;
import com.epeisong.utils.SystemUtils;

/**
 * 自定义View：聊天信息
 * 
 * @author poet
 * 
 */
public class ChatMsgView_old extends FrameLayout implements OnClickListener, OnLongClickListener {

    public static final int who_other = 0;
    public static final int who_me = 1;

    public static MediaPlayer sMediaPlayer;
    public static ChatMsg sPlayingChatMsg;
    public static boolean sPlaying;

    private OnChatMsgLongClickListener mOnChatMsgLongClickListener;
    private int mWho;
    private ViewHolder mHolder;
    private ChatMsg mChatMsg;

    public ChatMsgView_old(Context context, int who) {
        super(context);
        mWho = who;
        init();
    }

    private void init() {
        mHolder = new ViewHolder();
        View convertView = null;
        if (mWho == who_other) {
            convertView = SystemUtils.inflate(R.layout.chat_msg_view_left_old);
        } else {
            convertView = SystemUtils.inflate(R.layout.chat_msg_view_right_old);
        }
        mHolder.findView(convertView);
        this.addView(convertView);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ll_voice_container) {
            if (mChatMsg.getLocal_status() != ChatMsg.local_status_normal) {
                return;
            }
            if (sPlaying) {
                if (!mChatMsg.equals(sPlayingChatMsg)) {
                    // TODO
                    changePlay();
                } else {
                    stopPlay();
                }

            } else {
                startPlay();
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (mOnChatMsgLongClickListener != null) {
            mOnChatMsgLongClickListener.onChatMsgLongClick(mChatMsg);
            return true;
        }
        return false;
    }

    private void startPlay() {
        sPlayingChatMsg = mChatMsg;
        sMediaPlayer = new MediaPlayer();
        sMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mHolder.voiceIconNormal(sPlayingChatMsg.isSelf());
            }
        });
        try {
            String path = sPlayingChatMsg.getVoiceLocalPath();
            if (path == null) {

                return;
            }
            sMediaPlayer.setDataSource(path);
            sMediaPlayer.prepare();
            sMediaPlayer.start();
            sPlaying = true;
            mHolder.voiceIconPlaying();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void changePlay() {
        sMediaPlayer.stop();
        sMediaPlayer.reset();
        sPlayingChatMsg = mChatMsg;
        try {
            sMediaPlayer.setDataSource(sPlayingChatMsg.getType_data());
            sMediaPlayer.prepare();
            sMediaPlayer.start();
            sPlaying = true;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void stopPlay() {
        if (sMediaPlayer != null) {
            sMediaPlayer.stop();
            sMediaPlayer.reset();
            sMediaPlayer.release();
            sMediaPlayer.setOnCompletionListener(null);
            sMediaPlayer = null;
            sPlaying = false;
        }
    }

    public void fillData(ChatMsg msg, boolean showTime) {
        mHolder.fillData(msg, showTime);
    }

    public class ViewHolder {
        TextView tv_time;
        ImageView iv_head;
        View container_text;
        TextView tv_msg_text;
        View container_voice;
        ImageView iv_voice_icon;
        View v_voice_duration;
        TextView tv_voice_duration;

        ImageView iv_send_status;

        public void voiceIconPlaying() {
            iv_voice_icon.setImageResource(R.drawable.ic_launcher);
        }

        public void voiceIconNormal(boolean isSelf) {
            if (isSelf) {
                iv_voice_icon.setImageResource(R.drawable.ic_audio_right);
            } else {
                iv_voice_icon.setImageResource(R.drawable.ic_audio);
            }

        }

        public void findView(View v) {
            tv_time = (TextView) v.findViewById(R.id.tv_msg_time);
            iv_head = (ImageView) v.findViewById(R.id.iv_head);
            container_text = v.findViewById(R.id.ll_text_container);
            tv_msg_text = (TextView) v.findViewById(R.id.tv_msg);
            container_voice = v.findViewById(R.id.ll_voice_container);
            iv_voice_icon = (ImageView) v.findViewById(R.id.iv_voice);
            v_voice_duration = v.findViewById(R.id.v_voice_duration);
            tv_voice_duration = (TextView) v.findViewById(R.id.tv_voice_duration);
            iv_send_status = (ImageView) v.findViewById(R.id.iv_send_status);

            iv_head.setOnClickListener(ChatMsgView_old.this);
            container_text.setLongClickable(true);
            container_text.setOnLongClickListener(ChatMsgView_old.this);
            container_voice.setOnClickListener(ChatMsgView_old.this);
            container_voice.setLongClickable(true);
            container_voice.setOnLongClickListener(ChatMsgView_old.this);
        }

        public void fillData(ChatMsg msg, boolean showTime) {
            mChatMsg = msg;
            tv_time.setText(DateUtil.long2YMDHM(msg.getSend_time()));
            if (msg.getType() == ChatMsg.type_text) {
                container_text.setVisibility(View.VISIBLE);
                container_voice.setVisibility(View.GONE);
                tv_msg_text.setText(msg.getType_data() + "----" + msg.getSerial());
            } else if (msg.getType() == ChatMsg.type_voice) {
                container_text.setVisibility(View.GONE);
                container_voice.setVisibility(View.VISIBLE);
            } else {
                container_text.setVisibility(View.VISIBLE);
                tv_msg_text.setText("类型无效");
            }
            if (msg.isSelf() && iv_send_status != null) {
                int status = msg.getLocal_status();
                switch (status) {
                case ChatMsg.local_status_normal:
                    iv_send_status.setVisibility(View.GONE);
                    break;
                case ChatMsg.local_status_sending:
                    iv_send_status.setVisibility(View.VISIBLE);
                    iv_send_status.setImageResource(R.drawable.chat_sending);
                    break;
                case ChatMsg.local_status_fail:
                    iv_send_status.setVisibility(View.VISIBLE);
                    iv_send_status.setImageResource(R.drawable.chat_send_fail);
                    break;
                }
            }
        }
    }

    public void setOnChatMsgLongClickListener(OnChatMsgLongClickListener listener) {
        mOnChatMsgLongClickListener = listener;
    }

    public interface OnChatMsgLongClickListener {
        void onChatMsgLongClick(ChatMsg msg);
    }
}
