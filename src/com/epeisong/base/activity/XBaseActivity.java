package com.epeisong.base.activity;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.epeisong.EpsApplication;
import com.epeisong.R;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.base.adapter.impl.IconTextAdapter;
import com.epeisong.base.adapter.item.IconTextItem;
import com.epeisong.base.dialog.ListSimpleDialog;
import com.epeisong.base.dialog.MessageDialog;
import com.epeisong.base.dialog.PendingProgressDialog;
import com.epeisong.base.dialog.YesNoDialog;
import com.epeisong.base.dialog.YesNoDialog.OnYesNoDialogClickListener;
import com.epeisong.data.dao.ContactsDao;
import com.epeisong.model.Contacts;
import com.epeisong.model.Dictionary;
import com.epeisong.utils.BitmapUtils;
import com.epeisong.utils.ContactsUtils;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.FileUtils;
import com.epeisong.utils.ShapeUtils;
import com.epeisong.utils.ShapeUtils.ShapeParams;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;

/**
 * Activity基类：用于弹出对话框等
 * 
 * @author poet
 * 
 */
@SuppressWarnings("deprecation")
public class XBaseActivity extends FragmentActivity {

    private static final int REQUEST_CODE_CHOOSE_PICTURE = 1000;
    private static final int REQUEST_CODE_LAUNCH_CAMERA = 1001;

    private PopupWindow mContactsPopup;
    private IconTextAdapter mContactsPopupAdapter;
    private String mContactsId;

    private PopupWindow mPinyinPopup;
    private TextView mPinyinTv;
    private PendingProgressDialog mPendingProgressDialog;
    private MessageDialog mMessageDialog;

    private OnChoosePictureListener mOnChoosePictureListener;
    private boolean mIsChoosePictureCrop;

    protected void showContactsPopup(String id, View v) {
        mContactsId = id;
        if (mContactsPopup == null) {
            initContactsPopup();
        }
        Contacts c = ContactsDao.getInstance().queryById(mContactsId);
        List<IconTextItem> list = new ArrayList<IconTextItem>();
        if (c == null) {
            list.add(new IconTextItem(0, ContactsUtils.STR_ADD_CONTACTS, null));
            list.add(new IconTextItem(0, ContactsUtils.STR_ADD_BLACK, null));
        } else if (c.getStatus() == Contacts.STATUS_NORNAL) {
            list.add(new IconTextItem(0, ContactsUtils.STR_RM_CONTACTS, null));
            list.add(new IconTextItem(0, ContactsUtils.STR_ADD_BLACK, null));
        } else if (c.getStatus() == Contacts.STATUS_BLACKLIST) {
            list.add(new IconTextItem(0, ContactsUtils.STR_ADD_CONTACTS, null));
            list.add(new IconTextItem(0, ContactsUtils.STR_RM_BLACK, null));
        }
        list.add(new IconTextItem(0, ContactsUtils.STR_COMPLAIN, null));
        mContactsPopupAdapter.replaceAll(list);

        int statusBar = SystemUtils.getStatusBarHeight(this);
        int y = getResources().getDimensionPixelSize(R.dimen.custom_title_height) + statusBar + 1;
        mContactsPopup.showAtLocation(v, Gravity.TOP | Gravity.RIGHT, (int) DimensionUtls.getPixelFromDp(10), y);
    }

    protected void onContactsOption(String option) {
        ToastUtils.showToast("需要重写onContactsOption");
    }

    private void initContactsPopup() {
        mContactsPopup = new PopupWindow(getApplicationContext());
        mContactsPopupAdapter = new IconTextAdapter(getApplicationContext(), 40);
        ListView lv = new ListView(getApplicationContext());
        lv.setAdapter(mContactsPopupAdapter);
        lv.setBackgroundColor(Color.argb(0xFF, 0x2D, 0x30, 0x37));
        mContactsPopup.setContentView(lv);
        mContactsPopup.setWidth(EpsApplication.getScreenWidth() / 2);
        mContactsPopup.setHeight(LayoutParams.WRAP_CONTENT);
        mContactsPopup.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));
        mContactsPopup.setFocusable(true);
        mContactsPopup.setOutsideTouchable(true);
        mContactsPopup.setAnimationStyle(R.style.popup_window_menu);
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mContactsPopup.dismiss();
                IconTextItem item = mContactsPopupAdapter.getItem(position);
                onContactsOption(item.getName());
            }
        });
    }

    /**
     * 显示拼音的PopupWindow，参数为null时，表示隐藏PopupWindow
     * @param pinyin
     */
    public void showPinyinPopup(String pinyin) {
        if (pinyin == null) {
            if (mPinyinPopup != null) {
                mPinyinPopup.dismiss();
                mPinyinPopup = null;
            }
        } else {
            if (mPinyinPopup == null) {
                View view = SystemUtils.inflate(R.layout.popup_window_pinyin);
                view.setBackgroundDrawable(ShapeUtils.getShape(new ShapeParams()
                        .setBgColor(Color.argb(0x88, 0x00, 0x00, 0x00)).setCorner(DimensionUtls.getPixelFromDp(5))
                        .setStrokeWidth(0)));
                mPinyinTv = (TextView) view.findViewById(R.id.tv_pinyin);
                int w = (int) DimensionUtls.getPixelFromDp(100);
                mPinyinPopup = new PopupWindow(view, w, w);
                mPinyinPopup.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
            }
            mPinyinTv.setText(pinyin);
        }
    }

    public void showPendingDialog(String msg) {
        showPendingDialog(msg, null);
    }

    public void showPendingDialog(String msg, OnCancelListener listener) {
        if (mPendingProgressDialog == null) {
            mPendingProgressDialog = new PendingProgressDialog(this);
            mPendingProgressDialog.setCanceledOnTouchOutside(false);
        }
        mPendingProgressDialog.setOnCancelListener(listener);
        mPendingProgressDialog.show();
        if (TextUtils.isEmpty(msg)) {
            mPendingProgressDialog.setPendingMsg("加载中...");
        } else {
            mPendingProgressDialog.setPendingMsg(msg);
        }
    }

    public void dismissPendingDialog() {
        if (mPendingProgressDialog != null && mPendingProgressDialog.isShowing()) {
            mPendingProgressDialog.dismiss();
        }
    }

    public void onPostData(Serializable seri) {

    }

    public void showMessageDialog(String title, String message) {
        if (mMessageDialog == null) {
            mMessageDialog = new MessageDialog(this);
        }
        if (mMessageDialog.isShowing()) {
            mMessageDialog.dismiss();
        }
        mMessageDialog.show();
        if (!TextUtils.isEmpty(title)) {
            mMessageDialog.setTitle(title);
        } else {
            mMessageDialog.setTitle("提示");
        }
        mMessageDialog.setMessage(message);
    }

    public void showListDialog(String title, String[] items, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        AlertDialog d = builder.setItems(items, listener).create();
        d.setCanceledOnTouchOutside(true);
        d.show();
    }

    public ListSimpleDialog showListSimpleDialog(List<String> data, OnItemClickListener listener) {
        ListSimpleDialog dialog = new ListSimpleDialog(this);
        dialog.show();
        dialog.setData(data, listener);
        return dialog;
    }

    public void showYesNoDialog(String title, String message, String noText, String yesText,
            OnYesNoDialogClickListener listener) {
        YesNoDialog dialog = new YesNoDialog(this);
        dialog.show();
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setButtonTxt(noText, yesText);
        dialog.setOnYesNoDialogClickListener(listener);
    }

    public AlertDialog showDialog(View v) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setView(v);
        AlertDialog d = b.create();
        d.show();
        return d;
    }

    public AlertDialog showYesNoDialog(String title, View view, OnClickListener listener) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        AlertDialog d = b.setTitle(title).setView(view).setPositiveButton("确定", listener)
                .setNegativeButton("取消", listener).create();
        d.show();
        return d;
    }

    public void showYesNoDialog(String title, String msg, String noTxt, String yesTxt,
            DialogInterface.OnClickListener listener) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        if (!TextUtils.isEmpty(title)) {
            b.setTitle(title);
        }
        b.setCancelable(false);
        b.setMessage(msg);
        if (noTxt != null) {
            b.setNegativeButton(noTxt, listener);
        }
        if (yesTxt != null) {
            b.setPositiveButton(yesTxt, listener);
        }
        b.create().show();
    }

    /**
     * 显示列表对话框，使用自定义对象封装数据
     * 
     * @param title
     * @param data
     * @param listener
     */
    public void showDictionaryListDialog(String title, List<Dictionary> data, final OnChooseDictionaryListener listener) {
        Builder builder = new Builder(this);
        final DictionaryAdapter adapter = new DictionaryAdapter();
        adapter.replaceAll(data);
        builder.setTitle(title);
        builder.setAdapter(adapter, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (listener != null) {
                    listener.onChoosedDictionary(adapter.getItem(which));
                }
            }
        });
        AlertDialog d = builder.create();
        d.setCanceledOnTouchOutside(true);
        d.show();
    }

    public void showDictionaryListDialogMulti(String title, List<Dictionary> data, int checkCode,
            OnChooseDictionaryListener listener) {
        List<Dictionary> checkedData = null;
        if (checkCode > 0) {
            checkedData = new ArrayList<Dictionary>();
            for (Dictionary d : data) {
                if ((checkCode & d.getId()) == d.getId()) {
                    checkedData.add(d);
                }
            }
        }
        showDictionaryListDialogMulti(title, data, checkedData, listener);
    }

    public void showDictionaryListDialogMulti(String title, final List<Dictionary> data, List<Dictionary> checkedData,
            final OnChooseDictionaryListener listener) {
        Builder builder = new Builder(this);
        builder.setTitle(title);
        CharSequence[] items = new CharSequence[data.size()];
        final boolean[] checkedItems = new boolean[data.size()];
        for (int i = 0; i < data.size(); i++) {
            items[i] = data.get(i).getName();
            if (checkedData != null && checkedData.contains(data.get(i))) {
                checkedItems[i] = true;
            } else {
                checkedItems[i] = false;
            }
        }
        builder.setMultiChoiceItems(items, checkedItems, new OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                checkedItems[which] = isChecked;
            }
        });
        builder.setPositiveButton("确定", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Dictionary result = new Dictionary();
                if (data.size() > 0) {
                    result.setType(data.get(0).getType());
                }
                for (int i = 0; i < data.size(); i++) {
                    if (checkedItems[i]) {
                        Dictionary d = data.get(i);
                        result.setId(result.getId() + d.getId());
                        if (TextUtils.isEmpty(result.getName())) {
                            result.setName(d.getName());
                        } else {
                            result.setName(result.getName() + "," + d.getName());
                        }
                    }
                }
                listener.onChoosedDictionary(result);
            }
        });
        AlertDialog d = builder.create();
        d.setCanceledOnTouchOutside(true);
        d.show();
    }

    private class DictionaryAdapter extends HoldDataBaseAdapter<Dictionary> {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv = new TextView(getApplicationContext());
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            tv.setTextColor(Color.BLACK);
            tv.setText(getItem(position).getName());
            int p = DimensionUtls.getPixelFromDpInt(10);
            tv.setPadding(p, p, p, p);
            return tv;
        }
    }

    public interface OnChooseDictionaryListener {
        void onChoosedDictionary(Dictionary dict);
    }

    public void choosePicture(boolean bCrop, OnChoosePictureListener listener) {
        mOnChoosePictureListener = listener;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        FileUtils.checkOrCreateDirectory(FileUtils.getChoosePictureFilePath());
        FileUtils.deleteFile(FileUtils.getChoosePictureFilePath());
        if (bCrop) {
            mIsChoosePictureCrop = bCrop;
            setCropIntent(intent);
        }
        startActivityForResult(intent, REQUEST_CODE_CHOOSE_PICTURE);
    }

    public void launchCameraForPicture(boolean bCrop, OnChoosePictureListener listener) {
        mIsChoosePictureCrop = bCrop;
        mOnChoosePictureListener = listener;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(FileUtils.getCameraSaveFilePath())));
        startActivityForResult(intent, REQUEST_CODE_LAUNCH_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
            case REQUEST_CODE_CHOOSE_PICTURE:
                if (mIsChoosePictureCrop) {
                    File file = new File(FileUtils.getChoosePictureFilePath());
                    if (file.exists()) {
                        callbackChoosePictureListener(FileUtils.getChoosePictureFilePath());
                    } else if (data != null) {
                        Object obj = data.getParcelableExtra("data");
                        if (obj != null && obj instanceof Bitmap) {
                            final Bitmap b = (Bitmap) obj;
                            FileUtils.saveBitmapToFile(FileUtils.getChoosePictureFilePath(), b);
                            callbackChoosePictureListener(FileUtils.getChoosePictureFilePath());
                        }
                    }
                } else {
                    String[] proj = { MediaStore.Images.Media.DATA };
                    Uri uri = data.getData();
                    Cursor cursor = managedQuery(uri, proj, null, null, null);
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    if (cursor.moveToFirst()) {
                        String srcPath = cursor.getString(column_index);
                        String path = FileUtils.getChoosePictureFilePath();
                        SystemUtils.compressBitmapFile(path, srcPath, EpsApplication.getScreenWidth(),
                                EpsApplication.getScreenHeight());
                        callbackChoosePictureListener(path);
                    }
                }
                break;
            case REQUEST_CODE_LAUNCH_CAMERA:
                String path = FileUtils.getCameraSaveFilePath();
                BitmapUtils.checkPicExifRotate(path);
                if (mIsChoosePictureCrop) {
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(Uri.fromFile(new File(path)), "image/*");
                    FileUtils.deleteFile(FileUtils.getChoosePictureFilePath());
                    setCropIntent(intent);
                    startActivityForResult(intent, REQUEST_CODE_CHOOSE_PICTURE);
                } else {
                    String dstPath = FileUtils.getExternalTempPath() + "/compressed.jpg";
                    SystemUtils.compressBitmapFile(dstPath, path, EpsApplication.getScreenWidth(),
                            EpsApplication.getScreenHeight());
                    callbackChoosePictureListener(dstPath);
                }
                break;
            }
        }
    }

    private void setCropIntent(Intent intent) {
        intent.putExtra("crop", "true");
        intent.putExtra("return-data", false);
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("noFaceDetection", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(FileUtils.getChoosePictureFilePath())));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        if ("mIsCropPhotoSquare" != null) { // 设置剪切宽高比
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
        }
    }

    private void callbackChoosePictureListener(String path) {
        if (mOnChoosePictureListener != null) {
            mOnChoosePictureListener.onChoosePicture(path);
            mOnChoosePictureListener = null;
        }
    }

    public interface OnChoosePictureListener {
        void onChoosePicture(String path);
    }

    @Override
    protected void onDestroy() {
        dismissPendingDialog();
        super.onDestroy();
    }
}
