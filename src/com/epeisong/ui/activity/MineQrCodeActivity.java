package com.epeisong.ui.activity;

import java.util.Hashtable;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.epeisong.EpsApplication;
import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.net.NetQRCode;
import com.epeisong.logistics.proto.Eps.QRCodeReq.Builder;
import com.epeisong.logistics.proto.Eps.QRCodeResp;
import com.epeisong.utils.BitmapUtils;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.android.AsyncTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.datamatrix.encoder.SymbolShapeHint;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.zxing.CaptureActivity;

public class MineQrCodeActivity extends BaseActivity implements OnClickListener {

    public static final int QRCODE_TYPE_USER_ID = 100;

    public static int QRCODE_WIDTH = (int) DimensionUtls.getPixelFromDp(200);
    public static final int QRCODE_MARGIN = 0;

    private boolean mUseLogo = false;

    private String mDesc = "\t通过扫一扫二维码，可以添加好友\n\t非公开用户的二维码有效期2分钟，扫过一次后无效，需要刷新方可使用。";

    private ImageView mQrCodeIv;

    private TextView mDescTv;
    private String mUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_qrcode);
        mQrCodeIv = (ImageView) findViewById(R.id.iv_qrcode);
        findViewById(R.id.btn_refresh).setOnClickListener(this);
        findViewById(R.id.btn_open_scan).setOnClickListener(this);
        mDescTv = (TextView) findViewById(R.id.tv_desc);

        mQrCodeIv.getLayoutParams().width = QRCODE_WIDTH;
        mQrCodeIv.getLayoutParams().height = QRCODE_WIDTH;

        refreshQrCode();
    }

    @Override
    protected TitleParams getTitleParams() {
        return new TitleParams(getDefaultHomeAction(), "二维码", null).setShowLogo(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_refresh:
            refreshQrCode();
            break;
        case R.id.btn_open_scan:
            CaptureActivity.launchDefaultResult(this);
            finish();
            break;
        }
    }

    private synchronized void refreshQrCode() {
        AsyncTask<Void, Void, Bitmap> task = new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {
                NetQRCode net = new NetQRCode() {
                    @Override
                    protected boolean onSetRequest(Builder req) {
                        req.setIsPublic(false);
                        return true;
                    }
                };
                String url = null;
                try {
                    QRCodeResp.Builder resp = net.request();
                    if (net.isSuccess(resp)) {
                        url = resp.getUrl();
                    }
                } catch (NetGetException e) {
                    e.printStackTrace();
                }
                if (TextUtils.isEmpty(url)) {
                    return null;
                }
                mUrl = url;
                Bitmap bmp = buildQrCode(url, QRCODE_WIDTH, QRCODE_WIDTH);
                if (bmp == null) {
                    return null;
                }
                if (!mUseLogo) {
                    return bmp;
                }
                Bitmap logo = BitmapUtils.scaleBitmapFromRes(getApplicationContext(), R.drawable.user_logo_default,
                        new Rect(0, 0, bmp.getWidth() / 5, bmp.getHeight() / 5));
                if (logo == null) {
                    return bmp;
                }
                Bitmap result = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());
                Canvas canvas = new Canvas(result);
                canvas.drawBitmap(bmp, 0, 0, null);
                canvas.drawBitmap(logo, bmp.getWidth() / 2 - logo.getWidth() / 2,
                        bmp.getHeight() / 2 - logo.getHeight() / 2, null);
                Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
                p.setStyle(Style.STROKE);
                p.setColor(Color.argb(0xFF, 0xF8, 0xF8, 0xFf));
                int rectWidth = (int) DimensionUtls.getPixelFromDp(2);
                p.setStrokeWidth(rectWidth);
                canvas.drawRoundRect(
                        new RectF(bmp.getWidth() / 2 - logo.getWidth() / 2 - 1, bmp.getHeight() / 2 - logo.getHeight()
                                / 2 - 1, bmp.getWidth() / 2 + logo.getWidth() / 2 + 1, bmp.getHeight() / 2
                                + logo.getHeight() / 2 + 1), 6, 6, p);
                return result;
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                dismissPendingDialog();
                if (result != null) {
                    mQrCodeIv.setImageBitmap(result);
                    mDescTv.setText(mDesc);
                    mDescTv.setTextColor(Color.argb(0xff, 0xaa, 0xaa, 0xaa));
                    if (EpsApplication.DEBUGGING) {
                        mDescTv.setText(mDesc + "\r\n\r\n" + mUrl);
                    }
                } else {
                    mQrCodeIv.setImageResource(R.drawable.mine_qrcode_fail);
                    mDescTv.setText("二维码生成失败，请点击\"刷新二维码\"");
                    mDescTv.setTextColor(Color.RED);
                }
            }
        };
        task.execute();
        showPendingDialog("正在生成二维码...");
    }

    private Bitmap buildQrCode(String msg, int w, int h) {
        if (TextUtils.isEmpty(msg)) {
            return null;
        }
        try {
            Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            hints.put(EncodeHintType.MARGIN, QRCODE_MARGIN);
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.DATA_MATRIX_SHAPE, SymbolShapeHint.FORCE_SQUARE);
            hints.put(EncodeHintType.MAX_SIZE, 350);
            hints.put(EncodeHintType.MIN_SIZE, 100);
            // BitMatrix bitMatri = new QRCodeWriter().encode(msg,
            // BarcodeFormat.QR_CODE, w, h, hints);
            BitMatrix bitMatrix = new MultiFormatWriter().encode(msg, BarcodeFormat.QR_CODE, w, h, hints);
            int[] pixels = new int[w * h];
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * w + x] = 0xff000000;
                    } else {
                        pixels[y * w + x] = 0xffffffff;
                    }

                }
            }
            Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

}
