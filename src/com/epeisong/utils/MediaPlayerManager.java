package com.epeisong.utils;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.epeisong.EpsApplication;
import com.epeisong.utils.ShapeUtils.ShapeParams;

/**
 * 媒体播放管理者
 * @author poet
 *
 */
@SuppressWarnings("deprecation")
public class MediaPlayerManager implements OnCompletionListener {

    private static MediaPlayerManager instance;

    private MediaPlayer mMediaPlayer;
    private AudioManager mAudioManager;
    private int mAudioMode;
    private int mAudioModeCur;

    private WeakReference<OnMediaPlayingListener> mOnMediaPlayingListenerRefMain;
    private WeakReference<OnMediaPlayingListener> mOnMediaPlayingListenerRef;
    private String mPath;

    private Toast mToast;
    private TextView mTextView;

    private MediaPlayerManager() {
        int p = DimensionUtls.getPixelFromDpInt(10);
        mTextView = new TextView(EpsApplication.getInstance());
        mTextView.setPadding(p, p, p, p);
        mTextView.setTextColor(Color.WHITE);
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        mTextView.setBackgroundDrawable(ShapeUtils.getShape(new ShapeParams()
                .setCorner(DimensionUtls.getPixelFromDp(3)).setBgColor(Color.argb(0x88, 0x00, 0x00, 0x00))));
        mToast = new Toast(EpsApplication.getInstance());
        mToast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, (p + DimensionUtls.getPixelFromDpInt(45)));
        mToast.setView(mTextView);
        mToast.setDuration(Toast.LENGTH_SHORT);

        mAudioManager = (AudioManager) EpsApplication.getInstance().getSystemService(Context.AUDIO_SERVICE);
        mAudioModeCur = mAudioMode = mAudioManager.getMode();
    }

    public static MediaPlayerManager getInstance() {
        if (instance == null) {
            synchronized (MediaPlayerManager.class) {
                if (instance == null) {
                    instance = new MediaPlayerManager();
                }
            }
        }
        return instance;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        callbackListener(OnMediaPlayingListener.status_completed);
        setAudioMode(mAudioMode);
    }

    public void play(String path, OnMediaPlayingListener listener) {

        setAudioMode(mAudioModeCur);

        int mode = mAudioManager.getMode();
        if (mode == AudioManager.MODE_IN_CALL) {
            mTextView.setText("当前是听筒模式");
        } else if (mode == AudioManager.MODE_NORMAL) {
            mTextView.setText("当前是扬声器模式");
        }
        mToast.show();

        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setVolume(1f, 1f);
        }
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();

            callbackListener(OnMediaPlayingListener.status_completed);
        }
        if (mOnMediaPlayingListenerRef != null) {
            mOnMediaPlayingListenerRef.clear();
            mOnMediaPlayingListenerRef = null;
        }
        mOnMediaPlayingListenerRef = new WeakReference<MediaPlayerManager.OnMediaPlayingListener>(listener);
        mPath = path;
        try {
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            callbackListener(OnMediaPlayingListener.status_started);
        } catch (Exception e) {
            e.printStackTrace();
            callbackListener(OnMediaPlayingListener.status_completed);
            ToastUtils.showToast("播放错误");
        }
    }

    public void stop() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
                mMediaPlayer.reset();
                callbackListener(OnMediaPlayingListener.status_completed);
            }
            setAudioMode(mAudioMode);
        }
    }

    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if (mOnMediaPlayingListenerRefMain != null) {
            mOnMediaPlayingListenerRefMain.clear();
            mOnMediaPlayingListenerRefMain = null;
        }
        setAudioMode(mAudioMode);
        instance = null;
    }

    public boolean isPlaying() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    public boolean isPlaying(String path) {
        return mMediaPlayer != null && mMediaPlayer.isPlaying() && mPath.equals(path);
    }

    private void callbackListener(int status) {
        if (mOnMediaPlayingListenerRef != null && mOnMediaPlayingListenerRef.get() != null) {
            if (status == OnMediaPlayingListener.status_started) {
                mOnMediaPlayingListenerRef.get().onMediaPlayStarted(mPath);
            } else if (status == OnMediaPlayingListener.status_completed) {
                mOnMediaPlayingListenerRef.get().onMediaPlayCompleted(mPath);
                mOnMediaPlayingListenerRef.clear();
                mOnMediaPlayingListenerRef = null;
            }
        }
        if (mOnMediaPlayingListenerRefMain != null && mOnMediaPlayingListenerRefMain.get() != null) {
            if (status == OnMediaPlayingListener.status_started) {
                mOnMediaPlayingListenerRefMain.get().onMediaPlayStarted(mPath);
            } else if (status == OnMediaPlayingListener.status_completed) {
                mOnMediaPlayingListenerRefMain.get().onMediaPlayCompleted(mPath);
            }
        }
    }

    private void setAudioMode(int mode) {
        mAudioManager.setMode(mode);
    }

    public int getAudioMode() {
        return mAudioManager.getMode();
    }

    public int setAudioMode() {
        int mode;
        if (mAudioManager.getMode() == AudioManager.MODE_IN_CALL) {
            mode = AudioManager.MODE_NORMAL;
        } else {
            mode = AudioManager.MODE_IN_CALL;
        }
        mAudioManager.setMode(mode);
        mAudioModeCur = mode;
        if (mode == AudioManager.MODE_IN_CALL) {
            mTextView.setText("当前是听筒模式");
        } else if (mode == AudioManager.MODE_NORMAL) {
            mTextView.setText("当前是扬声器模式");
        }
        mToast.show();
        return mode;
    }

    public void setMainMediaPlayingListener(OnMediaPlayingListener l) {
        if (mOnMediaPlayingListenerRefMain != null) {
            mOnMediaPlayingListenerRefMain.clear();
            mOnMediaPlayingListenerRefMain = null;
        }
        mOnMediaPlayingListenerRefMain = new WeakReference<MediaPlayerManager.OnMediaPlayingListener>(l);
    }

    public interface OnMediaPlayingListener {
        public static int status_started = 1;
        public static int status_completed = 2;

        void onMediaPlayStarted(String path);

        void onMediaPlayCompleted(String path);
    }
}
