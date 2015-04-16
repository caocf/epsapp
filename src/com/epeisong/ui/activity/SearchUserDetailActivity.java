package com.epeisong.ui.activity;

import lib.universal_image_loader.ImageLoaderUtils;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.ContactsDao;
import com.epeisong.data.dao.PhoneContactsDao;
import com.epeisong.data.dao.UserDao;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.net.NetAddContacts;
import com.epeisong.data.net.NetAddMembers;
import com.epeisong.data.net.NetLogisticsInfo;
import com.epeisong.data.net.parser.ContactsParser;
import com.epeisong.data.net.parser.UserParser;
import com.epeisong.logistics.proto.Base.ProtoEBizLogistics;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.LogisticsReq;
import com.epeisong.logistics.proto.Eps.SearchCommonLogisticsReq.Builder;
import com.epeisong.model.Contacts;
import com.epeisong.model.User;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 搜索联系人后，显示对应用户的信息
 * 
 * @author poet
 * 
 */
public class SearchUserDetailActivity extends BaseActivity implements OnClickListener {

    public static final String EXTRA_USER = "user";
    public static final String EXTRA_USER_ID = "user_id";

    private ImageView mHeadIv;
    private TextView mNameTv;
    private Button mAddBtn;
    private String flag; // 判断是否是从配货市场页面过来的
    private User mUser;

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_add) {
            if (!TextUtils.isEmpty(flag)) {
            	NetAddMembers net = new NetAddMembers() {
					
					@Override
					protected boolean onSetRequest(Builder req) {
						req.setLogisticId(Integer.parseInt(mUser.getId()));
						return true;
					}
				};
				try {
					CommonLogisticsResp.Builder response = net.request();
					if (response != null && "SUCC".equals(response.getResult())) {
						ToastUtils.showToast("添加成功");
                        mAddBtn.setText("已添加到会员");
                        Intent in = new Intent();
                        Bundle bundle = new Bundle();
                        in.putExtras(bundle);
                        in.putExtra(SearchUserDetailActivity.EXTRA_USER, mUser);
                        setResult(10, in);
                        finish();
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
//                NetAddMembers net = new NetAddMembers(this, mUser.getId());
//                net.request(new OnNetRequestListenerImpl<CommonLogisticsResp.Builder>() {
//                    @Override
//                    public void onSuccess(CommonLogisticsResp.Builder response) {
//                        ToastUtils.showToast("添加成功");
//                        mAddBtn.setText("已添加到会员");
//                        Intent in = new Intent();
//                        Bundle bundle = new Bundle();
//                        in.putExtras(bundle);
//                        in.putExtra(SearchUserDetailActivity.EXTRA_USER, mUser);
//                        setResult(10, in);
//                        finish();
//                    }
//                });
            } else {
            	NetAddContacts net = new NetAddContacts() {
					
					@Override
					protected boolean onSetRequest(
							com.epeisong.logistics.proto.Eps.ContactReq.Builder req) {
						req.setContactId(Integer.parseInt(mUser.getId()));
						return true;
					}
				};
				try {
					CommonLogisticsResp.Builder resp = net.request();
					if (resp != null && "SUCC".equals(resp.getResult())) {
						ProtoEBizLogistics logistics = resp.getBizLogistics(0);
                        if (logistics != null) {
                            Contacts c = ContactsParser.parse(logistics);
                            c.setStatus(Contacts.STATUS_NORNAL);

                            ContactsDao.getInstance().insert(c);
                            PhoneContactsDao.getInstance().updateAdded(c.getPhone());
                            ToastUtils.showToast("添加成功");
                            mAddBtn.setText("已添加到通讯录");
                            mAddBtn.setEnabled(false);

                            Intent in = new Intent();
                            Bundle bundle = new Bundle();
                            in.putExtras(bundle);

                            setResult(SearchContactsActivity.CANCEL_OK, in);
                            finish();
					    }
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
//                NetAddContacts net = new NetAddContacts(this, mUser.getId());
//                net.request(new OnNetRequestListenerImpl<CommonLogisticsResp.Builder>() {
//                    @Override
//                    public void onSuccess(CommonLogisticsResp.Builder resp) {
//                        ProtoEBizLogistics logistics = resp.getBizLogistics(0);
//                        if (logistics != null) {
//                            Contacts c = ContactsParser.parse(logistics);
//                            c.setStatus(Contacts.STATUS_NORNAL);
//
//                            ContactsDao.getInstance().insert(c);
//                            PhoneContactsDao.getInstance().updateAdded(c.getPhone());
//                            ToastUtils.showToast("添加成功");
//                            mAddBtn.setText("已添加到通讯录");
//                            mAddBtn.setEnabled(false);
//
//                            Intent in = new Intent();
//                            Bundle bundle = new Bundle();
//                            in.putExtras(bundle);
//
//                            setResult(SearchContactsActivity.CANCEL_OK, in);
//                            finish();
//                        }
//                    }
//                });
            }
        }
    }

    private void fillData() {
        mNameTv.setText(mUser.getShow_name());
        if (TextUtils.isEmpty(mUser.getLogo_url())) {
            mHeadIv.setImageResource(User.getDefaultIcon(mUser.getUser_type_code(), true));
        } else {
            ImageLoader.getInstance().displayImage(mUser.getLogo_url(), mHeadIv,
                    ImageLoaderUtils.getListOptionsForUserLogo());
        }
        mAddBtn.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(flag)) {
            AsyncTask<Void, Void, User> task = new AsyncTask<Void, Void, User>() {
                @Override
                protected User doInBackground(Void... params) {
                    return UserDao.getInstance().queryById(mUser.getId());
                }

                @Override
                protected void onPostExecute(User result) {
                    if (result != null) {
                        mAddBtn.setText("已添加到会员");
                        mAddBtn.setEnabled(false);
                    }
                }
            };
            task.execute();
        } else {
            AsyncTask<Void, Void, Contacts> task = new AsyncTask<Void, Void, Contacts>() {
                @Override
                protected Contacts doInBackground(Void... params) {
                    return ContactsDao.getInstance().queryById(mUser.getId());
                }

                @Override
                protected void onPostExecute(Contacts result) {
                    if (result != null) {
                        mAddBtn.setText("已添加到通讯录");
                        mAddBtn.setEnabled(false);
                        PhoneContactsDao.getInstance().updateAdded(result.getPhone());
                    }
                }
            };
            task.execute();
        }
    }

    @Override
    protected TitleParams getTitleParams() {
        return new TitleParams(getDefaultHomeAction(), "详细资料", null).setShowLogo(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mUser = (User) getIntent().getSerializableExtra(EXTRA_USER);
        flag = getIntent().getStringExtra("members");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user_detial);
        mHeadIv = (ImageView) findViewById(R.id.iv_head);
        mNameTv = (TextView) findViewById(R.id.tv_name);
        mAddBtn = (Button) findViewById(R.id.btn_add);
        mAddBtn.setOnClickListener(this);

        if (!TextUtils.isEmpty(flag)) {
            mAddBtn.setText("添加到会员");
        }

        if (mUser != null) {
            fillData();
        } else {
            final String user_id = getIntent().getStringExtra(EXTRA_USER_ID);
            if (!TextUtils.isEmpty(user_id)) {
                AsyncTask<Void, Void, User> task = new AsyncTask<Void, Void, User>() {
                    @Override
                    protected User doInBackground(Void... params) {
                        NetLogisticsInfo net = new NetLogisticsInfo() {
                            @Override
                            protected boolean onSetRequest(LogisticsReq.Builder req) {
                                req.setLogisticsId(Integer.parseInt(user_id));
                                return true;
                            }
                        };
                        try {
                            CommonLogisticsResp.Builder resp = net.request();
                            return UserParser.parse(resp.getBizLogistics(0));
                        } catch (NetGetException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(User result) {
                        if (result != null) {
                            mUser = result;
                            fillData();
                        }
                    }
                };
                task.execute();
            }
        }
    }

}
