package com.epeisong.ui.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import com.epeisong.utils.android.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.view.FlowTextLayout;
import com.epeisong.base.view.FlowTextLayout.Attr;
import com.epeisong.base.view.FlowTextLayout.OnFlowTextItemClickEditaleListener;
import com.epeisong.base.view.FlowTextLayout.Textable;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.ContactsTagDao;
import com.epeisong.data.dao.ContactsTagDao.ContactsTagObserver;
import com.epeisong.data.dao.EpsTagDao;
import com.epeisong.data.dao.EpsTagDao.EpsTagObserver;
import com.epeisong.data.dao.UserDao;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.net.NetTag;
import com.epeisong.data.net.parser.EpsTagParser;
import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.proto.Base.ProtoETag;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.ContactReq.Builder;
import com.epeisong.model.Contacts;
import com.epeisong.model.ContactsTag;
import com.epeisong.model.EpsTag;
import com.epeisong.utils.ToastUtils;

/**
 * 编辑标签
 * @author poet
 *
 */
public class EditTagActivity extends BaseActivity implements OnClickListener, EpsTagObserver, ContactsTagObserver {

    public static final String EXTRA_CONTACTS = "contacts";

    private FlowTextLayout mFlowTextLayoutCur;
    private FlowTextLayout mFlowTextLayoutAll;
    private EditText mEditText;

    private Contacts mContacts;
    private String mContactsId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContacts = (Contacts) getIntent().getSerializableExtra(EXTRA_CONTACTS);
        super.onCreate(savedInstanceState);
        if (mContacts == null) {
            ToastUtils.showToast("contacts is empty");
            return;
        }
        mContactsId = mContacts.getId();
        setContentView(R.layout.activity_edit_tag);
        TextView tv_name = (TextView) findViewById(R.id.tv_contacts_name);
        tv_name.setText(mContacts.getShow_name());
        mEditText = (EditText) findViewById(R.id.et_tag);
        findViewById(R.id.btn_add).setOnClickListener(this);
        mFlowTextLayoutCur = (FlowTextLayout) findViewById(R.id.flow_cur);
        mFlowTextLayoutAll = (FlowTextLayout) findViewById(R.id.flow_all);
        mFlowTextLayoutCur.setAttr(new Attr().setTextBgResId(R.drawable.shape_content_white_frame_0d9cff)
                .setTextColor(Color.argb(0xff, 0x0d, 0x9c, 0xff)).setEditIconId(R.drawable.icon_tag_delete)
                .setItemPadding(5, 4));
        mFlowTextLayoutAll.setAttr(new Attr().setTextColor(Color.BLACK).setTextBgResId(R.drawable.shape_content_white)
                .setEditIconId(R.drawable.icon_tag_add).setItemPadding(5, 4));

        mFlowTextLayoutCur.setOnFlowTextItemClickEditaleListener(new OnFlowTextItemClickEditaleListener() {
            @Override
            public void onFlowTextItemClickEditable(Textable textable) {
                addOrRemoveTag((EpsTag) textable, false);
            }
        });
        mFlowTextLayoutAll.setOnFlowTextItemClickEditaleListener(new OnFlowTextItemClickEditaleListener() {
            @Override
            public void onFlowTextItemClickEditable(Textable textable) {
                addOrRemoveTag((EpsTag) textable, true);
            }
        });
        TextView emptyCur = new TextView(this);
        emptyCur.setText("暂无标签");
        emptyCur.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        emptyCur.setTextColor(Color.GRAY);
        mFlowTextLayoutCur.setEmptyView(emptyCur);
        TextView emptyAll = new TextView(this);
        emptyAll.setText("暂无标签");
        emptyAll.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        emptyAll.setTextColor(Color.GRAY);
        mFlowTextLayoutAll.setEmptyView(emptyAll);
        requestTags();

        EpsTagDao.getInstance().addObserver(this);
        ContactsTagDao.getInstance().addObserver(this);
    }

    @Override
    public void onEpsTagChange() {
        requestTags();
    }

    @Override
    public void onContactsTagChange(String contacts_id) {
        if (mContactsId.equals(contacts_id)) {
            requestTags();
        }
    }

    private void requestTags() {
        List<EpsTag> allTags = EpsTagDao.getInstance().queryAll();
        List<Integer> curIds = ContactsTagDao.getInstance().queryTagIds(mContactsId);
        List<EpsTag> curTags = new ArrayList<EpsTag>();
        Iterator<EpsTag> iterator = allTags.iterator();
        while (iterator.hasNext()) {
            EpsTag tag = iterator.next();
            if (curIds.contains(tag.getId())) {
                curTags.add(tag);
                iterator.remove();
            }
        }
        mFlowTextLayoutCur.setTextList(curTags);
        mFlowTextLayoutAll.setTextList(allTags);
    }

    @Override
    protected TitleParams getTitleParams() {
        return new TitleParams(getDefaultHomeAction(), "添加标签").setShowLogo(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_add:
            final String tagName = mEditText.getText().toString();
            if (TextUtils.isEmpty(tagName)) {
                ToastUtils.showToast("请输入标签");
                return;
            }
            createTag(tagName);
            break;
        }
    }

    private void createTag(final String tagName) {
        showPendingDialog(null);
        AsyncTask<Void, Void, EpsTag> task = new AsyncTask<Void, Void, EpsTag>() {
            @Override
            protected EpsTag doInBackground(Void... params) {
                NetTag net = new NetTag() {
                    @Override
                    protected int getCommandCode() {
                        return CommandConstants.CREATE_ETAG_REQ;
                    }

                    @Override
                    protected boolean onSetRequest(Builder req) {
                        ProtoETag.Builder pTag = ProtoETag.newBuilder();
                        pTag.setName(tagName);
                        pTag.setOwnerId(Integer.parseInt(UserDao.getInstance().getUser().getId()));
                        req.setETag(pTag);
                        req.setContactId(Integer.parseInt(mContactsId));
                        return true;
                    }
                };
                try {
                    CommonLogisticsResp.Builder resp = net.request();
                    if (net.isSuccess(resp)) {
                        List<EpsTag> tags = EpsTagParser.parse(resp);
                        if (!tags.isEmpty()) {
                            return tags.get(0);
                        }
                    }
                } catch (NetGetException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(EpsTag result) {
                dismissPendingDialog();
                if (result != null) {
                    mEditText.setText("");
                    EpsTagDao.getInstance().insert(result);
                    addOrRemoveTag(result, true);
                }
            }
        };
        task.execute();
    }

    private void addOrRemoveTag(final EpsTag tag, final boolean isAdd) {
        showPendingDialog(null);
        AsyncTask<Void, Void, ContactsTag> task = new AsyncTask<Void, Void, ContactsTag>() {
            @Override
            protected ContactsTag doInBackground(Void... params) {
                NetTag net = new NetTag() {

                    @Override
                    protected int getCommandCode() {
                        if (isAdd) {
                            return CommandConstants.TAG_SOME_ONE_REQ;
                        }
                        return CommandConstants.DO_NOT_TAG_SOME_ONE_REQ;
                    }

                    @Override
                    protected boolean onSetRequest(Builder req) {
                        req.setTaggedLogisticId(Integer.parseInt(mContactsId));
                        req.setTagId(tag.getId());
                        req.setTagName(tag.getName());
                        req.setLogisticId(Integer.parseInt(UserDao.getInstance().getUser().getId()));
                        return true;
                    }
                };
                try {
                    CommonLogisticsResp.Builder resp = net.request();
                    if (net.isSuccess(resp)) {
                        ContactsTag ct = new ContactsTag();
                        ct.setContacts_id(mContactsId);
                        ct.setTag_id(tag.getId());
                        return ct;
                    }
                } catch (NetGetException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(ContactsTag result) {
                dismissPendingDialog();
                if (result != null) {
                    if (isAdd) {
                        ContactsTagDao.getInstance().insert(result);
                    } else {
                        ContactsTagDao.getInstance().delete(mContactsId, result.getTag_id());
                    }
                }
            }
        };
        task.execute();
    }

    public static void launch(Context context, Contacts contacts) {
        Intent intent = new Intent(context, EditTagActivity.class);
        intent.putExtra(EXTRA_CONTACTS, contacts);
        context.startActivity(intent);
    }
}
