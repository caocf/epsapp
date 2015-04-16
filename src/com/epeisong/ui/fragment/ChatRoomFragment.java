package com.epeisong.ui.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lib.pulltorefresh.PullToRefreshBase;
import lib.pulltorefresh.PullToRefreshBase.Mode;
import lib.pulltorefresh.PullToRefreshBase.State;
import lib.pulltorefresh.PullToRefreshListView;
import lib.universal_image_loader.ImageLoaderUtils;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.bdmap.epsloc.EpsLocation;
import com.bdmap.impl.FixedLocActivity;
import com.bdmap.impl.MyLocActivity;
import com.epeisong.EpsApplication;
import com.epeisong.R;
import com.epeisong.base.activity.ShowImagesActivity;
import com.epeisong.base.activity.XBaseActivity;
import com.epeisong.base.activity.XBaseActivity.OnChoosePictureListener;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.base.dialog.ListSimpleDialog;
import com.epeisong.data.dao.ChatMsgDao;
import com.epeisong.data.dao.ChatMsgDao.ChatMsgObserver;
import com.epeisong.data.dao.ChatRoomDao;
import com.epeisong.data.dao.FreightDao;
import com.epeisong.data.dao.PointDao;
import com.epeisong.data.dao.UserDao;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.layer02.UserProvider;
import com.epeisong.data.layer02.abs.ChatMsgProvider;
import com.epeisong.data.layer02.impl.ChatMsgProviderImplNew;
import com.epeisong.data.layer03.ChatMsgNetProvider;
import com.epeisong.data.net.NetChat;
import com.epeisong.data.net.NetFriendsGet;
import com.epeisong.data.utils.ChatUtils;
import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.common.Properties;
import com.epeisong.logistics.proto.Base.ProtoEChat;
import com.epeisong.logistics.proto.Eps.ChatReq;
import com.epeisong.logistics.proto.Eps.ChatResp;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.LogisticsReq.Builder;
import com.epeisong.model.BusinessChatModel;
import com.epeisong.model.ChatMsg;
import com.epeisong.model.ChatRoom;
import com.epeisong.model.Freight;
import com.epeisong.model.Point.PointCode;
import com.epeisong.model.Privacy;
import com.epeisong.model.User;
import com.epeisong.net.request.NetChatSendMsg;
import com.epeisong.net.request.OnNetRequestListenerImpl;
import com.epeisong.speech.tts.TTSServiceFactory;
import com.epeisong.ui.activity.ContactsDetailActivity;
import com.epeisong.ui.view.ChatMsgView;
import com.epeisong.ui.view.ChatMsgView.OnChatMsgLongAndClickListener;
import com.epeisong.ui.view.ChatMsgView.OnChatMsgReSendListener;
import com.epeisong.ui.view.ChatMsgView.OnUserLogoClickListener;
import com.epeisong.ui.view.ChatMsgView.ViewHolder;
import com.epeisong.ui.view.RecordChatView;
import com.epeisong.ui.view.RecordChatView.MoreAction;
import com.epeisong.ui.view.RecordChatView.OnMoreActionListener;
import com.epeisong.ui.view.RecordChatView.OnRecordListener;
import com.epeisong.ui.view.RecordChatView.OnSendListener;
import com.epeisong.utils.EncodeUtils;
import com.epeisong.utils.FileUtils;
import com.epeisong.utils.HandlerUtils;
import com.epeisong.utils.IOUtils;
import com.epeisong.utils.LogUtils;
import com.epeisong.utils.MediaPlayerManager;
import com.epeisong.utils.MediaPlayerManager.OnMediaPlayingListener;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;
import com.google.gson.Gson;
import com.google.protobuf.ByteString;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class ChatRoomFragment extends Fragment implements OnChatMsgLongAndClickListener, OnScrollListener,
        OnRecordListener, OnSendListener, ChatMsgObserver, OnChatMsgReSendListener, OnUserLogoClickListener,
        OnMoreActionListener, OnChoosePictureListener, OnMediaPlayingListener {

    public static final String ARGS_REMOTE_ID = "remote_id";
    public static final String ARGS_BUSINESS_CHAT_MODEL = "business_chat_model";

    public static final String ARGS_LOGO_SHOW_PAGE_COUNT_ME = "logo_show_page_count_me";
    public static final String ARGS_LOGO_SHOW_PAGE_COUNT_OTHER = "logo_show_page_count_other";

    private static final int BUFFER_SIZE = 100;
    private static final int SIZE_LOAD_FIRST = 10;
    private static final int SIZE_LOAD_OLDER = 10;
    private static final int SIZE_LOAD_NEWER = 10;

    private static final int REQUEST_CODE_GET_LOC = 110;

    private boolean mIsNewest = true;
    private boolean mIsLoadingNewest;

    private PullToRefreshListView mPullToRefreshListView;
    private ListView mListView;
    private MyAdapter mAdapter;
    private RecordChatView mChatView;
    private TextView mAudioModeTv;

    private String mChatRoomId;
    private ChatRoom mChatRoom;
    private String mRemoteId;
    private User mRemote;

    private Privacy mRemotePrivacy;
    private boolean mMeIsContacts;

    private BusinessChatModel mBusinessChatModel;
    private int mBusinessType;
    private String mBusinessId;
    private String mBusinessOwnerId;
    private String mBusinessDesc;
    private String mBusinessExtra;

    private int mLogoShowPageCountMe;
    private int mLogoShowPageCountOther;

    private List<String> mListSimpleDialogData;

    private XBaseActivity mActivity;

    private User mUserSelf;

    private Bitmap mSelfBmp, mRemoteBmp;

    private OnChatRoomInfoListener mOnChatRoomInfoListener;

    private boolean mUseAnimClickPic = false;

    private boolean mDoDeleteFreight; // 对于收到的转发通知的车源货源，咨询后，删除该记录

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            mLogoShowPageCountMe = args.getInt(ARGS_LOGO_SHOW_PAGE_COUNT_ME, -1);
            mLogoShowPageCountOther = args.getInt(ARGS_LOGO_SHOW_PAGE_COUNT_OTHER, -1);
            mBusinessChatModel = (BusinessChatModel) args.getSerializable(ARGS_BUSINESS_CHAT_MODEL);
            mRemoteId = args.getString(ARGS_REMOTE_ID);
            if (mBusinessChatModel != null) {
                mBusinessType = mBusinessChatModel.getBusiness_type();
                mBusinessId = mBusinessChatModel.getBusiness_id();
                mBusinessOwnerId = mBusinessChatModel.getBusiness_owner_id();
                mBusinessDesc = mBusinessChatModel.getBusiness_desc();
                mBusinessExtra = mBusinessChatModel.getBusiness_extra();
            } else {
                mBusinessType = ChatMsg.business_type_normal;
            }
        }
        if (mBusinessType == ChatMsg.business_type_freight) {
            mDoDeleteFreight = true;
        }
        mChatRoomId = ChatUtils.getChatMsgTableName(mRemoteId, mBusinessType, mBusinessId);
        if (mChatRoomId == null) {
            ToastUtils.showToast("business_type错误");
            TextView tv = new TextView(getActivity());
            tv.setText("参数错误");
            tv.setGravity(Gravity.CENTER);
            return tv;
        }
        LogUtils.e("ChatRoomFragment", "chatroomid:" + mChatRoomId);
        ChatMsgDao.getInstance().checkTable(mChatRoomId);
        mChatRoom = ChatRoomDao.getInstance().queryById(mChatRoomId);
        if (mChatRoom != null && mChatRoom.getNew_msg_count() > 0) {
            ChatRoomDao.getInstance().read(mChatRoom);
        }
        PointDao.getInstance().hide(PointCode.Code_Message);
        mUserSelf = UserDao.getInstance().getUser();
        View root = inflater.inflate(R.layout.fragment_chat_room, null);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mChatRoomId == null) {
            return;
        }
        mAudioModeTv = (TextView) view.findViewById(R.id.tv_audio_mode);
        mAudioModeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mode = MediaPlayerManager.getInstance().setAudioMode();
                if (mode == AudioManager.MODE_IN_CALL) {
                    mAudioModeTv.setText("听筒");
                } else if (mode == AudioManager.MODE_NORMAL) {
                    mAudioModeTv.setText("免提");
                }
            }
        });

        mPullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.lv_msgs);
        mListView = mPullToRefreshListView.getRefreshableView();
        // mListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        mAdapter = new MyAdapter();
        mAdapter.setMaxSize(BUFFER_SIZE);
        mPullToRefreshListView.setAdapter(mAdapter);
        mPullToRefreshListView.setMode(Mode.PULL_FROM_START);
        mPullToRefreshListView.setLoadingTextStart(State.PULL_TO_REFRESH, "下拉加载");
        mPullToRefreshListView.setLoadingTextStart(State.RELEASE_TO_REFRESH, "释放立即加载");
        mPullToRefreshListView.setLoadingTextStart(State.REFRESHING, "加载中");
        mPullToRefreshListView.setSelection(mAdapter.getCount() - 1);
        mPullToRefreshListView.getRefreshableView().setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mChatView.hideInputMethod();
                }
                return false;
            }
        });
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                List<ChatMsg> data = new ArrayList<ChatMsg>();
                for (ChatMsg msg : mAdapter.getAllItem()) {
                    if (msg.getSerial() > 0) {
                        data.add(msg);
                    }
                }
                if (data.size() == 0) {
                    AsyncTask<Void, Void, List<ChatMsg>> task = new AsyncTask<Void, Void, List<ChatMsg>>() {
                        @Override
                        protected List<ChatMsg> doInBackground(Void... params) {
                            ChatMsgNetProvider p = new ChatMsgNetProvider();
                            try {
                                return p.getNewest(mRemoteId, 0, SIZE_LOAD_FIRST, mBusinessType, mBusinessId);
                            } catch (NetGetException e) {
                                e.printStackTrace();
                                return null;
                            }
                        }

                        @Override
                        protected void onPostExecute(List<ChatMsg> result) {
                            mAdapter.addAll(result);
                            HandlerUtils.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mPullToRefreshListView.onRefreshComplete();
                                }
                            }, 100);
                        }
                    };
                    task.execute();
                } else {
                    final long last_time = data.get(0).getSend_time();
                    final long last_serial = data.get(0).getSerial();
                    AsyncTask<Void, Void, List<ChatMsg>> task = new AsyncTask<Void, Void, List<ChatMsg>>() {
                        @Override
                        protected List<ChatMsg> doInBackground(Void... params) {
                            ChatMsgProvider provider = new ChatMsgProviderImplNew();
                            return provider.provideOlder(mRemoteId, SIZE_LOAD_OLDER, mBusinessType, mBusinessId,
                                    last_time, last_serial);
                        }

                        @Override
                        protected void onPostExecute(final List<ChatMsg> result) {
                            if (result != null) {
                                if (!result.isEmpty()) {
                                    if (mAdapter.getCount() + result.size() > BUFFER_SIZE) {
                                        mIsNewest = false;
                                    }
                                    mAdapter.addAll(0, result);
                                    HandlerUtils.postDelayed(new Runnable() {

                                        @Override
                                        public void run() {
                                            mListView.setSelection(result.size());
                                        }
                                    }, 100);
                                } else {
                                    ToastUtils.showToast("没有更多消息");
                                }
                            }
                            mPullToRefreshListView.onRefreshComplete();
                        }
                    };
                    task.execute();
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

            }
        });
        mPullToRefreshListView.setOnScrollListener(this);

        mChatView = (RecordChatView) view.findViewById(R.id.rcv);
        mChatView.setOnRecordListener(this);
        mChatView.setOnSendListener(this);
        mChatView.setOnMoreActionListener(this);

        // 直接拉取数据
        if (true) {
            if (mActivity != null) {
                mActivity.showPendingDialog(null);
            }
            AsyncTask<Void, Void, List<ChatMsg>> task = new AsyncTask<Void, Void, List<ChatMsg>>() {
                @Override
                protected List<ChatMsg> doInBackground(Void... params) {
                    List<ChatMsg> firstList = new ChatMsgProviderImplNew().providerFirst(mRemoteId, SIZE_LOAD_FIRST,
                            mBusinessType, mBusinessId);
                    if (firstList != null && firstList.size() > 0) {
                        int count = 0;
                        for (ChatMsg msg : firstList) {
                            if (msg.getRemote_status() != Properties.CHAT_STATUS_DELETED) {
                                count++;
                            }
                        }
                        ChatMsg last = firstList.get(0);
                        if (count < SIZE_LOAD_FIRST) {
                            List<ChatMsg> againList = new ChatMsgProviderImplNew().provideOlder(mRemoteId,
                                    SIZE_LOAD_FIRST - count, mBusinessType, mBusinessId, last.getSend_time(),
                                    last.getSerial());
                            if (againList != null && againList.size() > 0) {
                                firstList.addAll(0, againList);
                            }
                        }
                    }
                    return firstList;
                }

                @Override
                protected void onPostExecute(List<ChatMsg> result) {
                    if (mActivity != null) {
                        mActivity.dismissPendingDialog();
                    }
                    mAdapter.addAll(result);
                }
            };
            task.execute();
        }

        AsyncTask<Void, Void, User> userTask = new AsyncTask<Void, Void, User>() {
            @Override
            protected User doInBackground(Void... params) {
                return UserProvider.provideById(mRemoteId);
            }

            @Override
            protected void onPostExecute(User user) {
                if (user != null) {
                    mRemote = user;
                    if (mOnChatRoomInfoListener != null) {
                        mOnChatRoomInfoListener.onChatRoomInfo(mRemote);
                    }
                    // mAdapter.notifyDataSetChanged();
                    if (!TextUtils.isEmpty(mRemote.getLogo_url())) {
                        ImageLoader.getInstance().loadImage((mRemote.getLogo_url()),
                                ImageLoaderUtils.getListOptionsForUserLogo(), new SimpleImageLoadingListener() {
                                    @Override
                                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                        mRemoteBmp = loadedImage;
                                        mAdapter.notifyDataSetChanged();
                                    }
                                });
                    }
                }
            }
        };
        userTask.execute();

        if (!TextUtils.isEmpty(mUserSelf.getLogo_url())) {
            ImageLoader.getInstance().loadImage((mUserSelf.getLogo_url()),
                    ImageLoaderUtils.getListOptionsForUserLogo(), new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            mSelfBmp = loadedImage;
                            mAdapter.notifyDataSetChanged();
                        }
                    });
        }

        String unSend = ChatMsgDao.getInstance().queryUnSend(mChatRoomId);
        if (!TextUtils.isEmpty(unSend)) {
            mChatView.setText(unSend);
        }

        ChatMsgDao.getInstance().addObserver(this, mChatRoomId);
    }

    private void getFriends(final Runnable runnable) {
        AsyncTask<Void, Void, Void> privacyTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                NetFriendsGet net = new NetFriendsGet() {
                    @Override
                    protected boolean onSetRequest(Builder req) {
                        req.setLogisticsId(Integer.parseInt(mRemoteId));
                        req.setPrivacyType(Properties.PRIVACY_TYPE_CHAT);
                        return true;
                    }
                };
                try {
                    CommonLogisticsResp.Builder resp = net.request();
                    if (net.isSuccess(resp)) {

                        mMeIsContacts = resp.getCanDo();// resp.getIsFriends();
                        if (runnable != null) {
                            HandlerUtils.post(runnable);
                        }
                    }
                } catch (NetGetException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                return null;
            }
        };
        privacyTask.execute();
    }

    @Override
    public void onMediaPlayStarted(String path) {
        mAudioModeTv.setVisibility(View.VISIBLE);
        int mode = MediaPlayerManager.getInstance().getAudioMode();
        if (mode == AudioManager.MODE_IN_CALL) {
            mAudioModeTv.setText("听筒");
        } else if (mode == AudioManager.MODE_NORMAL) {
            mAudioModeTv.setText("免提");
        }
    }

    @Override
    public void onMediaPlayCompleted(String path) {
        mAudioModeTv.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        MediaPlayerManager.getInstance().setMainMediaPlayingListener(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        MediaPlayerManager.getInstance().stop();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ChatMsgDao.getInstance().removeObserver(mChatRoomId);
        MediaPlayerManager.getInstance().release();
        String unSend = mChatView.getText();
        if (!TextUtils.isEmpty(unSend)) {
            ChatMsgDao.getInstance().insertUnSend(mChatRoomId, unSend);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (XBaseActivity) getActivity();
    }

    @Override
    public void onNewMsg(ChatMsg msg) {
        if (mIsNewest) {
            mAdapter.addItem(msg);
            mListView.setSelection(mAdapter.getCount() - 1);
        }
    }

    @Override
    public void onClear() {
        mAdapter.clear();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
            case REQUEST_CODE_GET_LOC:
                EpsLocation loc = (EpsLocation) data.getSerializableExtra(MyLocActivity.EXTRA_OUT_EPS_LOCATION);
                if (loc != null) {
                    Location l = new Location(loc.getLongitude(), loc.getLatitude(), loc.getAddressName(),
                            loc.getProvinceName() + loc.getCityName() + loc.getDistrictName());
                    Gson gson = new Gson();
                    String json = gson.toJson(l);

                    final ChatMsg chatMsg = new ChatMsg();
                    chatMsg.setType(ChatMsg.type_location);
                    chatMsg.setType_data(json);
                    sendMsg(chatMsg, null, 0);
                }
                break;
            }
        }
    }

    @Override
    public void onChoosePicture(String path) {
        ChatMsg msg = new ChatMsg();
        msg.setType(ChatMsg.type_pic);
        sendMsg(msg, path, 0);
    }

    @Override
    public void onMoreAction(MoreAction action) {
        switch (action) {
        case PHOTO:
            mActivity.choosePicture(false, this);
            break;
        case CAMERA:
            mActivity.launchCameraForPicture(false, this);
            break;
        case LOCATION:
            Intent intent = new Intent(mActivity, MyLocActivity.class);
            startActivityForResult(intent, REQUEST_CODE_GET_LOC);
            break;
        default:
            break;
        }
    }

    @Override
    public void onRecordComplete(String path, int duration) {
        final ChatMsg chatMsg = new ChatMsg();
        chatMsg.setType(ChatMsg.type_voice);
        sendMsg(chatMsg, path, duration);
    }

    @Override
    public void onSend(String msg) {
        if (TextUtils.isEmpty(mRemoteId)) {
            return;
        }
        final ChatMsg chatMsg = new ChatMsg();
        chatMsg.setType(ChatMsg.type_text);
        chatMsg.setType_data(msg);
        sendMsg(chatMsg, null, 0);
    }

    @Override
    public void onChatMsgReSend(ChatMsg msg) {
        if (msg.getType() == ChatMsg.type_pic || msg.getType() == ChatMsg.type_voice) {
            byte[] data = ChatMsgDao.getInstance().queryBytes(mChatRoomId, msg.getId());
            msg.setTemp_data(data);
        }
        mAdapter.removeItem(msg);
        ChatMsgDao.getInstance().delete(mChatRoomId, msg);
        msg.setLocal_status(ChatMsg.local_status_sending);
        sendMsg(msg, null, 0);
    }

    private void sendMsg(final ChatMsg chatMsg, final String path, final int duration) {
        if (mDoDeleteFreight) {
            Freight f = new Freight();
            f.setId(mBusinessId);
            FreightDao.getInstance().delete(f);
            mDoDeleteFreight = false;
        }

        chatMsg.setBusiness_type(mBusinessType);
        if (mBusinessType != ChatMsg.business_type_normal) {
            chatMsg.setBusiness_id(mBusinessId);
            chatMsg.setBusiness_desc(mBusinessDesc);
            chatMsg.setBusiness_extra(mBusinessExtra);
            chatMsg.setBusiness_owner_id(mBusinessOwnerId);
        }

        chatMsg.setSender_id(mUserSelf.getId());
        chatMsg.setSender_name(mUserSelf.getShow_name());
        chatMsg.setSender_logistic_type_code(mUserSelf.getUser_type_code());
        chatMsg.setSender_logistic_type_name(mUserSelf.getUser_type_name());
        chatMsg.setReceiver_id(mRemoteId);
        chatMsg.setSend_time(System.currentTimeMillis());
        chatMsg.setLocal_status(ChatMsg.local_status_sending);

        if (mRemote != null) {
            chatMsg.setRemote_logistic_type_code(mRemote.getUser_type_code());
            chatMsg.setRemote_logistic_type_name(mRemote.getUser_type_name());
            chatMsg.setRemote_name(mRemote.getShow_name());
        }

        if (!mIsNewest) {
            List<ChatMsg> list = ChatMsgDao.getInstance().queryFirst(mChatRoomId, BUFFER_SIZE - 1);
            mAdapter.replaceAll(list);
            mIsNewest = true;
        }
        mAdapter.addItem(chatMsg);
        mPullToRefreshListView.setSelection(mAdapter.getCount() - 1);

        final String uuid = UUID.randomUUID().toString();
        chatMsg.setId(uuid);
        if (mRemote != null) {
            chatMsg.setRemote_name(mRemote.getShow_name());
        } else {
            chatMsg.setRemote_name(mChatRoom.getRemote_name());
        }

        ChatMsgDao.getInstance().replace(chatMsg, false);

        byte[] bytes = null;
        if (chatMsg.getType() == ChatMsg.type_pic || chatMsg.getType() == ChatMsg.type_voice) {
            if (!TextUtils.isEmpty(path)) {
                bytes = IOUtils.getByteArrayFromFile(path);
            } else {
                bytes = chatMsg.getTemp_data();
            }
        }
        final byte[] data = bytes;

        NetChatSendMsg net = new NetChatSendMsg() {

            @Override
            protected boolean isPrintReq() {
                if (path != null) {
                    return false;
                }
                return super.isPrintReq();
            }

            @Override
            protected long getTimeout() {
                if (path != null) {
                    return 1000 * 30;
                }
                return super.getTimeout();
            }

            @Override
            protected boolean onSetRequest(ChatReq.Builder req) {
                ProtoEChat.Builder chat = ProtoEChat.newBuilder();
                chat.setBizTableId(chatMsg.getBusiness_type());
                if (chatMsg.getBusiness_type() != ChatMsg.business_type_normal) {
                    if (chatMsg.getBusiness_id() != null) {
//                        chat.setBizId(Integer.parseInt(chatMsg.getBusiness_id()));
                        chat.setBizIdStr(chatMsg.getBusiness_id());
                    }
                    if (chatMsg.getBusiness_desc() != null) {
                        chat.setBizDescription(chatMsg.getBusiness_desc());
                    }
                    if (chatMsg.getBusiness_extra() != null) {
                        chat.setBizDescriptionStandby(chatMsg.getBusiness_extra());
                    }
                    if (chatMsg.getBusiness_owner_id() != null) {
                        chat.setBizPublisherId(Integer.parseInt(chatMsg.getBusiness_owner_id()));
                    }
                }
                chat.setSenderId(Integer.parseInt(chatMsg.getSender_id()));
                chat.setSenderName(chatMsg.getSender_name());
                chat.setSenderLogisticTypeCode(chatMsg.getSender_logistic_type_code());
                chat.setSenderLogisticTypeName(chatMsg.getSender_logistic_type_name());
                chat.setReceiverId(Integer.parseInt(chatMsg.getReceiver_id()));
                chat.setChatType(chatMsg.getType());
                if (chat.getChatType() == ChatMsg.type_text || chat.getChatType() == ChatMsg.type_location) {
                    chat.setContent(chatMsg.getType_data());
                } else if (chat.getChatType() == ChatMsg.type_voice) {
                    req.setFileType(".3gp");
                    if (data == null || data.length == 0) {
                        ToastUtils.showToast("语音文件错误");
                        return false;
                    }
                    if (duration > 0) {
                        req.setMediaDuration(duration);
                    }
                    req.setMediaFile(ByteString.copyFrom(data));
                } else if (chat.getChatType() == ChatMsg.type_pic) {
                    if (path != null) {
                        int i = path.lastIndexOf(".");
                        if (i > 0) {
                            req.setFileType(path.substring(i));
                        }
                    } else {
                        req.setFileType(".jpg");
                    }
                    if (data == null || data.length <= 0) {
                        ToastUtils.showToast("图片解析失败");
                        return false;
                    }
                    req.setMediaFile(ByteString.copyFrom(data));
                } else {
                    return false;
                }
                req.setChat(chat);
                return true;
            }
        };
        net.request(new OnNetRequestListenerImpl<ChatResp.Builder>() {

            @Override
            public void onSuccess(ChatResp.Builder response) {
                ProtoEChat chat = response.getChat(0);
                if (chat == null) {
                    return;
                }
                String id = String.valueOf(chat.getId());
                long serial = chat.getSyncIndex();
                long create_time = chat.getCreateDate();
                int type = chat.getChatType();
                chatMsg.setId(id);
                chatMsg.setSerial(serial);
                chatMsg.setLocal_status(ChatMsg.local_status_normal);
                chatMsg.setRemote_status(Properties.CHAT_STATUS_NORMAL);
                chatMsg.setSend_time(create_time);
                if (type == ChatMsg.type_voice) {
                    chatMsg.setType_data(chat.getVoiceUrl());
                    IOUtils.saveByteArrayToFile(data, FileUtils.getChatVoiceFileDir(),
                            EncodeUtils.md5base64(chatMsg.getType_data()));
                } else if (type == ChatMsg.type_pic) {
                    chatMsg.setType_data(chat.getPictureUrl());
                }
                ChatMsgDao.getInstance().update(mChatRoomId, chatMsg, uuid);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFail(String msg) {
                super.onFail(msg);
                chatMsg.setLocal_status(ChatMsg.local_status_fail);
                deleteChatMsg(chatMsg);
                // ChatMsgDao.getInstance().update(mChatRoomId, chatMsg, uuid);
                // ChatMsgDao.getInstance().saveBytes(mChatRoomId, uuid, data);
                // mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError() {
                chatMsg.setLocal_status(ChatMsg.local_status_fail);
                ChatMsgDao.getInstance().update(mChatRoomId, chatMsg, uuid);
                ChatMsgDao.getInstance().saveBytes(mChatRoomId, uuid, data);
                mAdapter.notifyDataSetChanged();
                ToastUtils.showToast("网络异常，请稍候再试");
            }
        });
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && view.getLastVisiblePosition() == view.getCount() - 1) {
            if (!mIsNewest && !mIsLoadingNewest) {
                AsyncTask<Void, Void, List<ChatMsg>> task = new AsyncTask<Void, Void, List<ChatMsg>>() {
                    @Override
                    protected List<ChatMsg> doInBackground(Void... params) {
                        ChatMsg msg = mAdapter.getItem(mAdapter.getCount() - 1);
                        long last_time = msg.getSend_time();
                        long last_serial = msg.getSerial();
                        if (last_serial == 0) {
                            last_time += 1;
                        }
                        return new ChatMsgProviderImplNew().provideNewer(mRemoteId, SIZE_LOAD_NEWER, mBusinessType,
                                mBusinessId, last_time, last_serial);
                    }

                    @Override
                    protected void onPostExecute(List<ChatMsg> result) {
                        mIsLoadingNewest = false;
                        if (result != null) {
                            if (result.size() < SIZE_LOAD_NEWER) {
                                mIsNewest = true;
                            }
                            if (!result.isEmpty()) {
                                ChatMsg msg = mAdapter.getItem(mPullToRefreshListView.getFirstVisiblePosition());
                                mAdapter.addAll(result);
                                final int pos = mAdapter.indexOf(msg) + 1;
                                HandlerUtils.postDelayed(new Runnable() {
                                    public void run() {
                                        mPullToRefreshListView.setSelection(pos);
                                    };
                                }, 10);

                            }
                        }
                    }
                };
                task.execute();
            }
        }
    }

    @Override
    public void onUserLogoClick(String userId) {
        int showPageCount = 0;
        User user = null;
        if (UserDao.getInstance().getUser().getId().equals(userId)) {
            showPageCount = mLogoShowPageCountMe;
            user = UserDao.getInstance().getUser();
        } else {
            showPageCount = mLogoShowPageCountOther;
            user = mRemote;
        }
        if (showPageCount > 0) {
            Intent intent = new Intent(getActivity(), ContactsDetailActivity.class);
            intent.putExtra(ContactsDetailActivity.EXTRA_USER_ID, userId);
            intent.putExtra(ContactsDetailActivity.EXTRA_SHOW_PAGE_COUNT_OTHER, showPageCount);
            intent.putExtra(ContactsDetailActivity.EXTRA_USER, user);
            startActivity(intent);
        }
    }

    @Override
    public void onChatMsgClick(ChatMsg msg, ViewHolder holder) {
        if (msg.getType() == ChatMsg.type_location) {
            Gson gson = new Gson();
            Location l = gson.fromJson(msg.getType_data(), Location.class);
            if (l == null) {
                return;
            }
            Intent i = new Intent(mActivity, FixedLocActivity.class);
            i.putExtra(FixedLocActivity.EXTRA_DESC, l.address);
            i.putExtra(FixedLocActivity.EXTRA_LONGITUDE, l.longitude);
            i.putExtra(FixedLocActivity.EXTRA_LATITUDE, l.latitude);
            startActivity(i);
        } else if (msg.getType() == ChatMsg.type_pic) {
            final Intent i = new Intent(mActivity, ShowImagesActivity.class);
            ArrayList<String> list = new ArrayList<String>();
            List<ChatMsg> msgs = mAdapter.getAllItem();
            for (ChatMsg item : msgs) {
                if (item.getType() == ChatMsg.type_pic) {
                    list.add(item.getType_data());
                }
            }
            int curPos = list.indexOf(msg.getType_data());
            i.putExtra(ShowImagesActivity.EXTRA_URL_LIST, list);
            i.putExtra(ShowImagesActivity.EXTRA_CUR_POS, curPos);
            if (mUseAnimClickPic) {
                ScaleAnimation animation = new ScaleAnimation(1, 2, 1, 2, holder.iv_pic.getWidth() / 2f,
                        holder.iv_pic.getHeight() / 2f);
                animation.setDuration(700);
                holder.iv_pic.startAnimation(animation);
                HandlerUtils.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(i);
                    }
                }, 500);
            } else {
                startActivity(i);
            }
        }
    }

    @Override
    public void onChatMsgLongClick(final ChatMsg msg) {
        if (mListSimpleDialogData == null) {
            mListSimpleDialogData = new ArrayList<String>();
            mListSimpleDialogData.add("复制消息");
            mListSimpleDialogData.add("删除消息");
            if (EpsApplication.DEBUGGING) {
                mListSimpleDialogData.add("播报消息");
            }
        }
        showListSimpleDialog(mListSimpleDialogData, new OnItemClickListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    if (msg.getType() == ChatMsg.type_text) {
                        ClipboardManager clipboard = (ClipboardManager) mActivity
                                .getSystemService(Context.CLIPBOARD_SERVICE);
                        clipboard.setText(msg.getType_data());
                        ToastUtils.showToast("内容已复制粘贴板");
                    }
                } else if (position == 1) {
                    deleteChatMsg(msg);
                } else if (position == 2) {
                    if (msg.getType() == ChatMsg.type_text) {
                        try {
                            TTSServiceFactory.getInstance().play(msg.getType_data());
                        } catch (Exception e) {
                            ToastUtils.showToast("播报失败");
                            LogUtils.e("TTSService.play", e);
                        }
                    }
                }
            }
        }).showTitle(mRemote == null ? "提示" : mRemote.getShow_name());
    }

    private ListSimpleDialog showListSimpleDialog(List<String> data, OnItemClickListener listener) {
        ListSimpleDialog dialog = new ListSimpleDialog(getActivity());
        dialog.show();
        dialog.setData(data, listener);
        return dialog;
    }

    private void deleteChatMsg(final ChatMsg msg) {
        if (msg.getLocal_status() != ChatMsg.local_status_normal) {
            ChatMsgDao.getInstance().delete(mChatRoomId, msg);
            mAdapter.removeItem(msg);
        } else {
            mActivity.showPendingDialog(null);
            AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Void... params) {
                    NetChat net = new NetChat() {

                        @Override
                        protected int getCommandCode() {
                            return CommandConstants.CHAT_UPDATE_REQ;
                        }

                        @Override
                        protected boolean onSetRequest(ChatReq.Builder req) {
                            req.setStatus(Properties.CHAT_STATUS_DELETED);
                            req.setChatId(Integer.parseInt(msg.getId()));
                            req.setOppsiteId(Integer.parseInt(mRemoteId));
                            if (mBusinessId != null) {
                                req.setBizId(Integer.parseInt(mBusinessId));
                                req.setTableId(mBusinessType);
                            }
                            return true;
                        }
                    };
                    try {
                        ChatResp.Builder resp = net.request();
                        return net.isSuccess(resp);
                    } catch (NetGetException e) {
                        e.printStackTrace();
                    }
                    return false;
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    mActivity.dismissPendingDialog();
                    if (result) {
                        ChatMsgDao.getInstance().delete(mChatRoomId, msg);
                        mAdapter.removeItem(msg);
                    }
                }
            };
            task.execute();
        }
    }

    private class MyAdapter extends HoldDataBaseAdapter<ChatMsg> {

        @Override
        public int getViewTypeCount() {
            return 3;
        }

        @Override
        public int getItemViewType(int position) {
            if (getItem(position).getRemote_status() == Properties.CHAT_STATUS_DELETED) {
                return -1;
            }
            boolean b = getItem(position).isSelf();
            return b ? ChatMsgView.who_me : ChatMsgView.who_other;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ChatMsg chatMsg = getItem(position);
            if (chatMsg.getRemote_status() == Properties.CHAT_STATUS_DELETED) {
                return new View(getActivity());
            }
            boolean b = chatMsg.isSelf();
            int who = b ? ChatMsgView.who_me : ChatMsgView.who_other;
            ChatMsgView msgView = null;
            if (convertView == null || !(convertView instanceof ChatMsgView)) {
                msgView = new ChatMsgView(getActivity(), who);
                msgView.setOnChatMsgLongAndClickListener(ChatRoomFragment.this);
                msgView.setOnChatMsgReSendListener(ChatRoomFragment.this);
                msgView.setOnUserLogoClickListener(ChatRoomFragment.this);
                convertView = msgView;
            } else {
                msgView = (ChatMsgView) convertView;
            }
            boolean showTime = true;
            if (position > 0) {
                if (chatMsg.getSend_time() - getItem(position - 1).getSend_time() < 1000 * 60) {
                    showTime = false;
                }
            }
            msgView.fillData(b ? mUserSelf : mRemote, chatMsg, showTime, b ? mSelfBmp : mRemoteBmp);
            return convertView;
        }
    }

    public boolean hideMoreView() {
        if (mChatView == null) {
            return false;
        }
        return mChatView.hideMoreView();
    }

    public boolean handleTouchEvent(MotionEvent ev) {
        if (mChatView == null) {
            return false;
        }
        return mChatView.handleTouchEvent(ev);
    }

    public void setOnChatRoomInfoListener(OnChatRoomInfoListener listener) {
        mOnChatRoomInfoListener = listener;
    }

    public interface OnChatRoomInfoListener {
        void onChatRoomInfo(User remote);
    }

    public static class Location {
        double longitude, latitude;
        public String address, region;

        public Location(double longitude, double latitude, String address, String region) {
            super();
            this.longitude = longitude;
            this.latitude = latitude;
            this.address = address;
            this.region = region;
        }
    }
}
