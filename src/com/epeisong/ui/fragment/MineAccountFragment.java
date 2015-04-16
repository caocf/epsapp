package com.epeisong.ui.fragment;

import java.io.ByteArrayOutputStream;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import com.epeisong.utils.android.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.activity.XBaseActivity;
import com.epeisong.base.activity.XBaseActivity.OnChoosePictureListener;
import com.epeisong.data.dao.UserDao;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.net.NetLogisticsUpdate;
import com.epeisong.logistics.proto.Base.ProtoEBizLogistics.Builder;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.LogisticsReq;
import com.epeisong.model.User;
import com.epeisong.ui.activity.MineQrCodeActivity;
import com.epeisong.ui.view.SwitchButton;
import com.epeisong.ui.view.SwitchButton.OnSwitchListener;
import com.epeisong.utils.ToastUtils;
import com.google.protobuf.ByteString;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 我的账号信息
 * 
 * @author poet
 * 
 */
public class MineAccountFragment extends Fragment implements OnClickListener, OnSwitchListener, OnChoosePictureListener {

    private ImageView mLogoIv;
    private SwitchButton mSwitchButton;

    private View mHasQrCodeView;
    private TextView mAccountTv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_mine_account, null);
        mLogoIv = (ImageView) root.findViewById(R.id.iv_logo);
        mAccountTv = (TextView) root.findViewById(R.id.tv_account);
        mSwitchButton = (SwitchButton) root.findViewById(R.id.switchBtn);
        mSwitchButton.setOnSwitchListener(this);
        mHasQrCodeView = root.findViewById(R.id.rl_two_code_has);
        mHasQrCodeView.setOnClickListener(this);
        root.findViewById(R.id.rl_logo).setOnClickListener(this);
        root.findViewById(R.id.rl_account).setOnClickListener(this);
        root.findViewById(R.id.rl_status).setOnClickListener(this);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fillData(UserDao.getInstance().getUser());
    }

    private void fillData(final User user) {
        if (!TextUtils.isEmpty(user.getLogo_url())) {
            ImageLoader.getInstance().displayImage(user.getLogo_url(), mLogoIv);
        }
        mAccountTv.setText(user.getPhone());
        mSwitchButton.setSwitchText("公开", "不公开", true);
        mSwitchButton.setSwitch(user.getIs_hide() == User.CONFIG_NO_HIDE);
    }

    @Override
    public void onChoosePicture(String path) {
        final Bitmap bmp = BitmapFactory.decodeFile(path);
        ((XBaseActivity) getActivity()).showPendingDialog(null);
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                NetLogisticsUpdate net = new NetLogisticsUpdate() {
                    @Override
                    protected boolean isPrintReq() {
                        return false;
                    }

                    @Override
                    protected boolean onSetRequest(LogisticsReq.Builder req) {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        bmp.compress(CompressFormat.PNG, 100, out);
                        byte[] bytes = out.toByteArray();
                        ByteString bs = ByteString.copyFrom(bytes);
                        req.setLogo(bs);
                        req.setLogoFileType(".png");
                        return true;
                    }
                };
                try {
                    CommonLogisticsResp.Builder resp = net.request();
                    if (net.isSuccess(resp)) {
                        User user = UserDao.getInstance().getUser();
                        user.setLogo_url(resp.getLogoPicFilePath());
                        UserDao.getInstance().replace(user);
                        return true;
                    }
                } catch (NetGetException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                ((XBaseActivity) getActivity()).dismissPendingDialog();
                if (result != null) {
                    if (result) {
                        mLogoIv.setImageBitmap(bmp);
                        ToastUtils.showToast("头像上传成功");
                    } else {
                        ToastUtils.showToast("头像设置失败");
                    }
                }
            }
        };
        task.execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.rl_logo:
            String[] items = { "相册", "相机" };
            ((XBaseActivity) getActivity()).showListDialog(null, items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {
                        ((XBaseActivity) getActivity()).choosePicture(true, MineAccountFragment.this);
                    } else if (which == 1) {
                        ((XBaseActivity) getActivity()).launchCameraForPicture(true, MineAccountFragment.this);
                    }
                }
            });
            break;
        case R.id.rl_account:
            break;
        case R.id.rl_status:

            break;
        case R.id.rl_two_code_has:
        case R.id.rl_two_code_no:
            Intent qrcode = new Intent(getActivity(), MineQrCodeActivity.class);
            startActivity(qrcode);
            break;
        case R.id.rl_two_code_no2:
            break;
        }
    }

    @Override
    public synchronized void onSwitch(SwitchButton btn, final boolean on) {
        ((XBaseActivity) getActivity()).showPendingDialog(null);
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                NetLogisticsUpdate net = new NetLogisticsUpdate() {
                    @Override
                    protected boolean onSetRequestParams(Builder logi) {
                        logi.setIsHide(on ? User.CONFIG_NO_HIDE : User.CONFIG_HIDE);
                        return true;
                    }
                };
                try {
                    CommonLogisticsResp.Builder resp = net.request();
                    if (net.isSuccess(resp)) {
                        return true;
                    }
                } catch (NetGetException e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                ((XBaseActivity) getActivity()).dismissPendingDialog();
                if (result) {
                    User user = UserDao.getInstance().getUser();
                    user.setIs_hide(on ? User.CONFIG_NO_HIDE : User.CONFIG_HIDE);
                    UserDao.getInstance().replace(user);
                    mSwitchButton.setSwitch(on);
                }
            }
        };
        task.execute();
    }
}
