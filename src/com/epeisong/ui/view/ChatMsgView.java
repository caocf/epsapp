package com.epeisong.ui.view;

import java.io.File;
import java.io.IOException;

import lib.universal_image_loader.ImageLoaderUtils;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.data.dao.ContactsDao;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.logistics.proto.Eps.GetMediaBytesReq.Builder;
import com.epeisong.logistics.proto.Eps.GetMediaBytesResp;
import com.epeisong.model.ChatMsg;
import com.epeisong.model.Contacts;
import com.epeisong.model.User;
import com.epeisong.ui.fragment.ChatRoomFragment.Location;
import com.epeisong.ui.fragment.NetMediaGet;
import com.epeisong.utils.DateUtil;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.EncodeUtils;
import com.epeisong.utils.FileUtils;
import com.epeisong.utils.IOUtils;
import com.epeisong.utils.MediaPlayerManager;
import com.epeisong.utils.MediaPlayerManager.OnMediaPlayingListener;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 自定义View：聊天信息
 * 
 * @author poet
 * 
 */
public class ChatMsgView extends FrameLayout implements OnClickListener, OnLongClickListener, OnMediaPlayingListener {

    public static final int who_me = 1;
    public static final int who_other = 2;

    public static MediaPlayer sMediaPlayer;
    public static ChatMsg sPlayingChatMsg;
    public static boolean sPlaying;

    private OnChatMsgLongAndClickListener mOnChatMsgLongAndClickListener;
    private OnChatMsgReSendListener mOnChatMsgReSendListener;
    private OnUserLogoClickListener mOnUserLogoClickListener;
    private int mWho;
    private ViewHolder mHolder;
    private ChatMsg mChatMsg;

    private MediaPlayerManager mPlayerManager;

    private boolean mUseAnimOnPic = false;

    @Deprecated
    public ChatMsgView(Context context) {
        super(context);
    }

    public ChatMsgView(Context context, int who) {
        super(context);
        mWho = who;
        init();
    }

    private void init() {
        mHolder = new ViewHolder();
        View convertView = null;
        if (mWho == who_other) {
            convertView = SystemUtils.inflate(R.layout.chat_msg_view_left);
        } else {
            convertView = SystemUtils.inflate(R.layout.chat_msg_view_right);
        }
        mHolder.findView(convertView);
        this.addView(convertView);
    }

    @Override
    public void onMediaPlayCompleted(String path) {
        if (mChatMsg.getVoiceLocalPath().equals(path)) {
            mHolder.voiceIconNormal(mChatMsg.isSelf());
        }
    }

    @Override
    public void onMediaPlayStarted(String path) {
        if (mChatMsg.getVoiceLocalPath().equals(path)) {
            mHolder.voiceIconPlaying(mChatMsg.isSelf());
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rl_container) {
            if (mChatMsg.getLocal_status() != ChatMsg.local_status_normal) {
                return;
            }
            if (mChatMsg.getType() == ChatMsg.type_voice) {
                if (mPlayerManager == null) {
                    mPlayerManager = MediaPlayerManager.getInstance();
                }
                String path = mChatMsg.getVoiceLocalPath();
                if (path == null) {
                    downloadVoice(mChatMsg);
                } else {
                    if (mPlayerManager.isPlaying(mChatMsg.getVoiceLocalPath())) {
                        mPlayerManager.stop();
                    } else {
                        mPlayerManager.play(mChatMsg.getVoiceLocalPath(), this);
                        sPlayingChatMsg = mChatMsg;
                        mHolder.voiceIconPlaying(mChatMsg.isSelf());
                    }
                }
                // if (sPlaying) {
                // if (!mChatMsg.equals(sPlayingChatMsg)) {
                // // TODO
                // changePlay();
                // } else {
                // stopPlay();
                // }
                //
                // } else {
                // startPlay();
                // }
            } else {
                if (mOnChatMsgLongAndClickListener != null) {
                    mOnChatMsgLongAndClickListener.onChatMsgClick(mChatMsg, mHolder);
                }
            }
        } else if (id == R.id.iv_send_status) {
            Object tag = v.getTag();
            if (tag != null && tag instanceof ChatMsg) {
                if (mOnChatMsgReSendListener != null) {
                    mOnChatMsgReSendListener.onChatMsgReSend((ChatMsg) tag);
                }
            }
        } else if (id == R.id.iv_head) {
            if (mOnUserLogoClickListener != null) {
                mOnUserLogoClickListener.onUserLogoClick(mChatMsg.getSender_id());
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (mOnChatMsgLongAndClickListener != null) {
            mOnChatMsgLongAndClickListener.onChatMsgLongClick(mChatMsg);
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
                stopPlay();
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
            mHolder.voiceIconPlaying(mChatMsg.isSelf());
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
            mHolder.voiceIconNormal(mChatMsg.isSelf());
        }
    }

    private void downloadVoice(final ChatMsg msg) {
        mHolder.voiceDownloading();
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                NetMediaGet net = new NetMediaGet() {
                    @Override
                    protected boolean onSetRequest(Builder req) {
                        req.setVoiceUrl(msg.getType_data());
                        return true;
                    }
                };
                try {
                    GetMediaBytesResp.Builder resp = net.request();
                    if (net.isSuccess(resp)) {
                        byte[] data = resp.getMediaFile().toByteArray();
                        if (IOUtils.saveByteArrayToFile(data, FileUtils.getChatVoiceFileDir(),
                                EncodeUtils.md5base64(msg.getType_data()))) {
                            return FileUtils.getChatVoiceFileDir() + File.separator
                                    + EncodeUtils.md5base64(msg.getType_data());
                        }
                    }
                } catch (NetGetException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    if (!mPlayerManager.isPlaying()) {
                        mPlayerManager.play(result, ChatMsgView.this);
                        mHolder.voiceIconPlaying(msg.isSelf());
                    }
                } else {
                    mHolder.voiceIconNormal(msg.isSelf());
                }
            }
        };
        task.execute();
    }

    public void fillData(User sender, ChatMsg msg, boolean showTime, Bitmap bmp) {
        mHolder.fillData(sender, msg, showTime, bmp);
    }

    public class ViewHolder {
        View container;
        TextView tv_time;
        ImageView iv_head;
        TextView tv_msg_text;

        View container_voice;
        View voice_len;
        ImageView iv_voice_icon;
        TextView tv_voice_duration;

        View container_loc;
        TextView tv_loc_top, tv_loc_bottom;

        View container_pic;
        public ImageView iv_pic;

        ImageView iv_send_status;

        public void voiceDownloading() {
            iv_voice_icon.setImageResource(R.drawable.arrow_down);
        }

        public void voiceIconPlaying(boolean isSelf) {
            iv_voice_icon.setImageResource(R.drawable.ic_launcher);
            if (isSelf) {
                iv_voice_icon.setImageResource(R.drawable.list_chat_msg_audio_right);
            } else {
                iv_voice_icon.setImageResource(R.drawable.list_chat_msg_audio_left);
            }
            Drawable d = iv_voice_icon.getDrawable();
            if (d instanceof AnimationDrawable) {
                ((AnimationDrawable) d).start();
            }
        }

        public void voiceIconNormal(boolean isSelf) {
            Drawable d = iv_voice_icon.getDrawable();
            if (d instanceof AnimationDrawable) {
                ((AnimationDrawable) d).stop();
            }
            if (isSelf) {
                iv_voice_icon.setImageResource(R.drawable.selector_chat_msg_audio_right);
            } else {
                iv_voice_icon.setImageResource(R.drawable.selector_chat_msg_audio_left);
            }

        }

        public void findView(View v) {
            container = v.findViewById(R.id.rl_container);
            tv_time = (TextView) v.findViewById(R.id.tv_msg_time);
            iv_head = (ImageView) v.findViewById(R.id.iv_head);
            tv_msg_text = (TextView) v.findViewById(R.id.tv_msg);
            container_voice = v.findViewById(R.id.voice_container);
            voice_len = v.findViewById(R.id.v_voice_len);
            iv_voice_icon = (ImageView) v.findViewById(R.id.iv_voice);
            tv_voice_duration = (TextView) v.findViewById(R.id.tv_voice_duration);
            iv_send_status = (ImageView) v.findViewById(R.id.iv_send_status);
            if (iv_send_status != null) {
                iv_send_status.setOnClickListener(ChatMsgView.this);
            }

            container_loc = v.findViewById(R.id.rl_loc_container);
            tv_loc_top = (TextView) v.findViewById(R.id.tv_loc_top);
            tv_loc_bottom = (TextView) v.findViewById(R.id.tv_loc_bottom);

            container_pic = v.findViewById(R.id.rl_pic_container);
            iv_pic = (ImageView) v.findViewById(R.id.iv_pic);
            if (mUseAnimOnPic) {
                iv_pic.setAnimation(AnimationUtils.makeInChildBottomAnimation(getContext()));
            }

            iv_head.setOnClickListener(ChatMsgView.this);
            container.setLongClickable(true);
            container.setOnLongClickListener(ChatMsgView.this);
            container.setOnClickListener(ChatMsgView.this);
        }

        public void fillData(User sender, ChatMsg msg, boolean showTime, Bitmap bmp) {
            mChatMsg = msg;
            if (sender == null || bmp == null) {
                if (sender == null) {
                    iv_head.setImageResource(R.drawable.user_logo_default);
                } else {
                    Contacts c = ContactsDao.getInstance().queryById(sender.getId());
                    iv_head.setImageResource(User.getDefaultIcon(sender.getUser_type_code(), c != null));
                }
            } else {
                iv_head.setImageBitmap(bmp);
            }
            if (showTime) {
                tv_time.setVisibility(View.VISIBLE);
                tv_time.setText(DateUtil.long2vague(msg.getSend_time(), true));
            } else {
                tv_time.setVisibility(View.GONE);
            }
            tv_msg_text.setVisibility(View.GONE);
            container_voice.setVisibility(View.GONE);
            tv_voice_duration.setVisibility(View.GONE);
            container_loc.setVisibility(View.GONE);
            container_pic.setVisibility(View.GONE);
            if (msg.getType() == ChatMsg.type_text) {
                tv_msg_text.setVisibility(View.VISIBLE);
                tv_msg_text.setText(msg.getType_data());
            } else if (msg.getType() == ChatMsg.type_voice) {
                container_voice.setVisibility(View.VISIBLE);
                tv_voice_duration.setVisibility(View.VISIBLE);
                int duration = 0;
                if (msg.getType_data() != null) {
                    int durationIndex = msg.getType_data().indexOf("duration");
                    int pointIndex = msg.getType_data().lastIndexOf(".");

                    if (durationIndex > 0 && pointIndex > 0 && pointIndex > durationIndex) {
                        String durationStr = msg.getType_data().substring(durationIndex + "duration".length(),
                                pointIndex);
                        try {
                            duration = Integer.parseInt(durationStr);
                        } catch (NumberFormatException e) {
                            ToastUtils.showToast(durationStr);
                        }
                    }
                }
                tv_voice_duration.setText(String.valueOf(duration));
                int w = 0;
                int wItem = DimensionUtls.getPixelFromDpInt(5);
                if (duration <= 20) {
                    w = wItem * duration;
                } else {
                    w = wItem * 20 + (duration - 20) * wItem / 5;
                }
                voice_len.getLayoutParams().width = w;
                if (mPlayerManager != null && mPlayerManager.isPlaying(msg.getType_data())) {
                    voiceIconPlaying(msg.isSelf());
                }
            } else if (msg.getType() == ChatMsg.type_location) {
                container_loc.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(msg.getType_data())) {
                    Gson gson = new Gson();
                    Location loc = gson.fromJson(msg.getType_data(), Location.class);
                    tv_loc_top.setText(loc.address);
                    tv_loc_bottom.setText(loc.region);
                }
            } else if (msg.getType() == ChatMsg.type_pic) {
                container_pic.setVisibility(View.VISIBLE);
                ImageLoader.getInstance().displayImage(msg.getType_data(), iv_pic, ImageLoaderUtils.getListOptions());
            } else {
                tv_msg_text.setVisibility(View.VISIBLE);
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
                    iv_send_status.setTag(msg);
                    break;
                }
            }
        }
    }

    public void setOnChatMsgLongAndClickListener(OnChatMsgLongAndClickListener listener) {
        mOnChatMsgLongAndClickListener = listener;
    }

    public void setOnChatMsgReSendListener(OnChatMsgReSendListener listener) {
        mOnChatMsgReSendListener = listener;
    }

    public void setOnUserLogoClickListener(OnUserLogoClickListener listener) {
        mOnUserLogoClickListener = listener;
    }

    public interface OnChatMsgLongAndClickListener {
        void onChatMsgLongClick(ChatMsg msg);

        void onChatMsgClick(ChatMsg msg, ViewHolder holder);
    }

    public interface OnChatMsgReSendListener {
        void onChatMsgReSend(ChatMsg msg);
    }

    public interface OnUserLogoClickListener {
        void onUserLogoClick(String userId);
    }
}
