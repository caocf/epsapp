package com.zxing;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.view.TitleParams;
import com.epeisong.ui.activity.MineQrCodeActivity;
import com.epeisong.utils.ShapeUtils;
import com.epeisong.utils.ToastUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.zxing.camera.CameraManager;
import com.zxing.decoding.CaptureActivityHandler;
import com.zxing.decoding.InactivityTimer;
import com.zxing.decoding.RGBLuminanceSource;
import com.zxing.view.ViewfinderView;

public class CaptureActivity extends BaseActivity implements Callback {

    public static final String EXTRA_OUT_RESULT = "out_result";

    public static final String EXTRA_IN_IS_DEFAULT = "in_is_default";

    private static final int REQUEST_CODE_CHOOSE_PIC = 100;

    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;

    private boolean mIsDefaultHandle;

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        mIsDefaultHandle = getIntent().getBooleanExtra(EXTRA_IN_IS_DEFAULT, false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zxing);

        CameraManager.init(getApplication());
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);

        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);

        Button btn = (Button) findViewById(R.id.btn);
        btn.setBackgroundDrawable(ShapeUtils.getMainBtnBg(3));
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CaptureActivity.this, MineQrCodeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected TitleParams getTitleParams() {
        return new TitleParams(getDefaultHomeAction(), "扫一扫", null).setShowLogo(false);
    }

    // 处理扫描手机相册中的二维码
    private Bitmap scanBitmap;

    private static final int DECODE_IMAGE_SUCCESS = 22;
    private static final int DECODE_IMAGE_FAIL = 11;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case DECODE_IMAGE_FAIL:
                ToastUtils.showToast("Scan failed!");
                break;
            case DECODE_IMAGE_SUCCESS:
                String result = (String) msg.obj;
                scanFinish(result, scanBitmap);
                break;
            }
        };
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
            case REQUEST_CODE_CHOOSE_PIC:
                // 获取选中图片的路径
                String path = null;
                Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);
                if (cursor.moveToFirst()) {
                    path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                }
                cursor.close();

                // 开启扫描
                if (!TextUtils.isEmpty(path)) {
                    startupScan(path);
                } else {
                    ToastUtils.showToast("选择图片失败");
                }
                break;
            }
        }
    }

    private void startupScan(final String path) {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("正在扫描...");
        pd.setCancelable(false);
        pd.show();
        new Thread() {
            public void run() {
                Result result = decodeImage(path);
                Message msg = Message.obtain();
                if (result == null) {
                    msg.what = DECODE_IMAGE_FAIL;
                    pd.cancel();
                } else {
                    msg.what = DECODE_IMAGE_SUCCESS;
                    msg.obj = result.getText();
                }
                mHandler.sendMessage(msg);
            };
        }.start();
    }

    /**
     * 解码 二维码图片
     * 
     * @param path
     *            图片路径
     * @return
     */
    public Result decodeImage(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "UTF8"); // 设置二维码内容的编码

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 先获取原大小
        scanBitmap = BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false; // 获取新的大小
        int sampleSize = (int) (options.outHeight / (float) 200);
        if (sampleSize <= 0)
            sampleSize = 1;
        options.inSampleSize = sampleSize;
        scanBitmap = BitmapFactory.decodeFile(path, options);
        RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        try {
            return reader.decode(bitmap1, hints);
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = false;
        if (playBeep) {
            AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
            if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
                playBeep = false;
            }
            initBeepSound();
        }
        vibrate = true;

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        super.onDestroy();
    }

    /**
     * 处理扫描结果
     * 
     * @param result
     * @param barcode
     */
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        String resultString = result.getText();
        scanFinish(resultString, barcode);
    }

    private void scanFinish(String result, Bitmap barcode) {
        if (TextUtils.isEmpty(result)) {
            Toast.makeText(CaptureActivity.this, "Scan failed!", Toast.LENGTH_SHORT).show();
        } else {
            if (mIsDefaultHandle) {
                CaptureResultActivity.launch(this, result);
            } else {
                Intent data = new Intent();
                data.putExtra(EXTRA_OUT_RESULT, result);
                setResult(Activity.RESULT_OK, data);
            }
        }
        CaptureActivity.this.finish();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    public static void launchDefaultResult(Activity a) {
        Intent intent = new Intent(a, CaptureActivity.class);
        intent.putExtra(EXTRA_IN_IS_DEFAULT, true);
        a.startActivity(intent);
    }

    public static void launchDefaultResult(Fragment f) {
        if (f == null || f.getActivity() == null) {
            return;
        }
        Intent intent = new Intent(f.getActivity(), CaptureActivity.class);
        intent.putExtra(EXTRA_IN_IS_DEFAULT, true);
        f.startActivity(intent);
    }

    public static void launchForResult(Activity a, int requestCode) {
        Intent intent = new Intent(a, CaptureActivity.class);
        a.startActivityForResult(intent, requestCode);
    }

    public static void launchForResult(Fragment f, int requestCode) {
        if (f == null || f.getActivity() == null) {
            return;
        }
        Intent intent = new Intent(f.getActivity(), CaptureActivity.class);
        f.startActivityForResult(intent, requestCode);
    }
}