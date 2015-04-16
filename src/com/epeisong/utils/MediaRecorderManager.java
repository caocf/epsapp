package com.epeisong.utils;

import java.io.IOException;
import java.lang.ref.WeakReference;

import android.media.MediaRecorder;
import android.media.MediaRecorder.OnInfoListener;
import android.os.Looper;

/**
 * 媒体录音管理者
 * @author poet
 *
 */
public class MediaRecorderManager implements OnInfoListener {

    private static MediaRecorderManager instance;

    private int mRecordState;
    private MediaRecorder mRecorder;
    private long mStartTime;

    private WeakReference<OnRecordListener> mListenerRef;
    private int mMaxDuration;

    private MediaRecorderManager() {
    }

    public static MediaRecorderManager getInstance() {
        if (instance == null) {
            synchronized (MediaRecorderManager.class) {
                if (instance == null) {
                    instance = new MediaRecorderManager();
                }
            }
        }
        return instance;
    }

    Runnable recordRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                callListener(OnRecordListener.EV_PREPARING);
                mRecorder.prepare();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mRecorder.start();
            mStartTime = System.currentTimeMillis();
            callListener(OnRecordListener.EV_STARTING);
            HandlerUtils.post(ampRunnable);
        }
    };

    Runnable ampRunnable = new Runnable() {
        @Override
        public void run() {
            OnRecordListener l = null;
            if (mListenerRef != null) {
                l = mListenerRef.get();
            }
            if (l != null) {
                float result = mRecorder.getMaxAmplitude() / (float) 32768;
                l.onRecordEvent(OnRecordListener.EV_STARTING, result);
                HandlerUtils.postDelayed(this, 200);
            }
        }
    };

    @Override
    public void onInfo(MediaRecorder mr, int what, int extra) {
        if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
            callListener(OnRecordListener.EV_STOPED, mMaxDuration);
        }
    }

    public void start(int maxDuration, OnRecordListener listener, String path) {
        mMaxDuration = maxDuration;
        mListenerRef = new WeakReference<OnRecordListener>(listener);
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); // 指定音频来源：麦克风
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP); // 设置文件输出格式：3gp
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB); // 设置编码方式：amr窄带
        mRecorder.setOutputFile(path);
        mRecorder.setMaxDuration(mMaxDuration);
        mRecorder.setOnInfoListener(MediaRecorderManager.this);
        new Thread(recordRunnable).start();
    }

    public void stop() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
            if (mStartTime > 0) {
                int duration = (int) (System.currentTimeMillis() - mStartTime);
                callListener(OnRecordListener.EV_STOPED, duration);
                mStartTime = 0;
            }
        }
    }

    public int getState() {
        return mRecordState;
    }

    void callListener(final int event, final Object... extra) {
        mRecordState = event;
        if (mListenerRef != null && mListenerRef.get() != null) {
            if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
                mListenerRef.get().onRecordEvent(event);
            } else {
                HandlerUtils.post(new Runnable() {
                    @Override
                    public void run() {
                        mListenerRef.get().onRecordEvent(event, extra);
                    }
                });
            }
        }
    }

    public interface OnRecordListener {
        public static final int EV_PREPARING = 1;
        public static final int EV_STARTING = 2;
        public static final int EV_STOPED = 3;

        void onRecordEvent(int event, Object... extra);
    }
}
