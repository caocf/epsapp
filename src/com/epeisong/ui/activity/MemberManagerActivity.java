package com.epeisong.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.AdapterView.OnItemClickListener;

import com.epeisong.EpsApplication;
import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.adapter.impl.IconTextAdapter;
import com.epeisong.base.adapter.item.IconTextItem;
import com.epeisong.base.view.TitleParams;
import com.epeisong.base.view.CustomTitle.Action;
import com.epeisong.base.view.CustomTitle.ActionImpl;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.net.NetAddMembers;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.SearchCommonLogisticsReq.Builder;
import com.epeisong.model.MarketMember;
import com.epeisong.model.User;
import com.epeisong.ui.fragment.MembersManagerFragment;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.LogUtils;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;
import com.zxing.CaptureActivity;

public class MemberManagerActivity extends BaseActivity {

    private PopupWindow mPopupWindowMenu;
    private View mTitleRight;
    private String mResult;

    MembersManagerFragment membersManagerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_fragment);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame, membersManagerFragment = new MembersManagerFragment()).commit();
    }

    @Override
    protected TitleParams getTitleParams() {
        return new TitleParams(getDefaultHomeAction(), "会员管理").setAction(createAction());
    }

    private Action createAction() {
        return new ActionImpl() {

            @Override
            public void doAction(View v) {
                showMenuPopupWindow();
            }

            @Override
            public View getView() {
                mTitleRight = new ImageView(getApplicationContext());
                ((ImageView) mTitleRight).setImageResource(R.drawable.chatroom_contacts_option_not_contacts);
                return mTitleRight;
            }
        };
    }

    private void showMenuPopupWindow() {
        if (mPopupWindowMenu == null) {
            initPopupWindowMenu();
        }
        int statusBar = SystemUtils.getStatusBarHeight(this);
        int y = getResources().getDimensionPixelSize(R.dimen.custom_title_height) + statusBar + 1;
        mPopupWindowMenu.showAtLocation(mCustomTitle, Gravity.TOP | Gravity.RIGHT,
                (int) DimensionUtls.getPixelFromDp(10), y);
    }

    private void initPopupWindowMenu() {
        List<IconTextItem> items = new ArrayList<IconTextItem>();
        items.add(new IconTextItem(0, "手机号添加", null));
        items.add(new IconTextItem(0, "二维码添加", null));

        mPopupWindowMenu = new PopupWindow(getApplicationContext());
        IconTextAdapter adapter = new IconTextAdapter(getApplicationContext(), 40);
        adapter.replaceAll(items);
        ListView lv = new ListView(getApplicationContext());
        lv.setAdapter(adapter);
        lv.setBackgroundColor(Color.argb(0xFF, 0x2D, 0x30, 0x37));
        mPopupWindowMenu.setContentView(lv);
        mPopupWindowMenu.setWidth(EpsApplication.getScreenWidth() / 2);
        mPopupWindowMenu.setHeight(LayoutParams.WRAP_CONTENT);
        mPopupWindowMenu.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));
        mPopupWindowMenu.setFocusable(true);
        mPopupWindowMenu.setOutsideTouchable(true);
        mPopupWindowMenu.setAnimationStyle(R.style.popup_window_menu);
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                mPopupWindowMenu.dismiss();
                mPopupWindowMenu = null;
                if (position == 0) { // 手机号添加
                    Intent search = new Intent(getApplicationContext(), SearchContactsActivity.class);
                    search.putExtra("members", "members");
                    // startActivity(search);
                    startActivityForResult(search, 12);
                } else if (position == 1) { // 二维码添加
                    CaptureActivity.launchForResult(MemberManagerActivity.this, 22);
                }
            }

        });
        lv.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_MENU && event.getAction() == KeyEvent.ACTION_UP
                        && mPopupWindowMenu.isShowing()) {
                    mPopupWindowMenu.dismiss();
                    return true;
                }
                return false;
            }
        });
    }

    private void addMembers(final String qrUrl) {
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                NetAddMembers net = new NetAddMembers() {
                    @Override
                    protected boolean onSetRequest(Builder req) {
                        req.setQrCodeAddContactURL(mResult);
                        return true;
                    }
                };
                try {
                    CommonLogisticsResp.Builder resp = net.request();
                    if (net.isSuccess(resp)) {
                        return true;
                    } else {
                        LogUtils.e("", resp.getDesc());
                    }
                    ToastUtils.showToastInThread(resp.getDesc());
                } catch (NetGetException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtils.showToastInThread("解析失败");
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    ToastUtils.showToast("添加会员成功");
                    membersManagerFragment.refresh();
                }
            }
        };
        task.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 12 && resultCode == Activity.RESULT_OK) {
            User user = (User) data.getSerializableExtra(SearchUserDetailActivity.EXTRA_USER);
            data.putExtra("mUser", user);
            // mAdapter.addItem(user);
            // onPullUpToRefresh(mPullToRefreshListView);
            Intent intent = new Intent("com.epeisong.ui.activity.refreshMember");
            intent.putExtra("refreshMember", user);
            this.sendBroadcast(intent); // 发送广播
        }
        if (requestCode == 22 && resultCode == Activity.RESULT_OK) {
            mResult = data.getStringExtra(CaptureActivity.EXTRA_OUT_RESULT);
            if (TextUtils.isEmpty(mResult)) {
                ToastUtils.showToast("无结果");
            } else if (mResult.startsWith("http://www.epeisong.com/addcontact")) {
                addMembers(mResult);
            } else {
                ToastUtils.showToast("扫描失败，请重新扫描");
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
