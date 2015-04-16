/**
 * 
 * 
 */
package com.epeisong.service.thread;

import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

import android.media.AudioManager;
import android.media.SoundPool;

import com.epeisong.EpsApplication;
import com.epeisong.MainActivity;
import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.data.dao.BulletinDao;
import com.epeisong.data.dao.ChatMsgDao;
import com.epeisong.data.dao.ContactsDao;
import com.epeisong.data.dao.FansDao;
import com.epeisong.data.dao.FreightDao;
import com.epeisong.data.dao.PointDao;
import com.epeisong.data.dao.UserDao;
import com.epeisong.data.layer02.InfoFeeManager;
import com.epeisong.data.net.parser.BulletinParser;
import com.epeisong.data.net.parser.ChatMsgParser;
import com.epeisong.data.net.parser.FreightForwardParser;
import com.epeisong.data.net.parser.FreightParser;
import com.epeisong.data.net.parser.InfoFeeParser;
import com.epeisong.data.net.parser.SystemNoticeParser;
import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.common.Properties;
import com.epeisong.logistics.net.NetServiceFactory;
import com.epeisong.logistics.net.NetServiceUtils;
import com.epeisong.logistics.proto.Base.ProtoEChat;
import com.epeisong.logistics.proto.Eps.AddContactServerPushReq;
import com.epeisong.logistics.proto.Eps.BulletinServerPushReq;
import com.epeisong.logistics.proto.Eps.ChatSendMultiTypeServerPushReq;
import com.epeisong.logistics.proto.Eps.DeliverFreightServerPushReq;
import com.epeisong.logistics.proto.Eps.FreightServerPushReq;
import com.epeisong.logistics.proto.Eps.FreightServerPushReq.Builder;
import com.epeisong.logistics.proto.Eps.SystemNoticeReq;
import com.epeisong.logistics.proto.InfoFee.InfoFeeReq;
import com.epeisong.model.Bulletin;
import com.epeisong.model.ChatMsg;
import com.epeisong.model.Fans;
import com.epeisong.model.Freight;
import com.epeisong.model.FreightForward;
import com.epeisong.model.InfoFee;
import com.epeisong.model.Point.PointCode;
import com.epeisong.model.SystemNotice;
import com.epeisong.service.notify.NotifyService;
import com.epeisong.service.notify.Task;
import com.epeisong.service.receiver.FreightReceiver;
import com.epeisong.speech.tts.TTSServiceFactory;
import com.epeisong.ui.activity.temp.LoginActivity;
import com.epeisong.ui.fragment.InfoFeeFragment;
import com.epeisong.utils.LogUtils;
import com.epeisong.utils.NotificationUtils;
import com.epeisong.utils.SpUtilsCur;
import com.epeisong.utils.SpUtilsCur.SpListener;
import com.epeisong.utils.SysUtils;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.java.JavaUtils;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.TextFormat;

/**
 * @author cngaohk
 * @since Sep 25, 2014
 */
public class HandlerProcessThread extends Thread implements SpListener {
    private LinkedBlockingQueue<BuilderPackage> msgQueue;
    private boolean stop = false;
    private long lastVibrateTime;
    private long lastPlaySoundTime;

    private SoundPool mSoundPool;
    private int mSoundId;

    private boolean mIsNoDisturb;
    private long mStartTime, mEndTime;

    public HandlerProcessThread(LinkedBlockingQueue<BuilderPackage> msgQueue) {
        this.msgQueue = msgQueue;
        mSoundPool = new SoundPool(10, AudioManager.STREAM_ALARM, 5);
        mSoundId = mSoundPool.load(EpsApplication.getInstance(), R.raw.beep, 0);
        SpUtilsCur.registerListener(SpUtilsCur.KEYS_NOTIFY.BOOL_NO_DISTURB, this);
        SpUtilsCur.registerListener(SpUtilsCur.KEYS_NOTIFY.LONG_START_TIME, this);
        SpUtilsCur.registerListener(SpUtilsCur.KEYS_NOTIFY.LONG_END_TIME, this);

        onSpChange(SpUtilsCur.KEYS_NOTIFY.BOOL_NO_DISTURB);
        onSpChange(SpUtilsCur.KEYS_NOTIFY.LONG_START_TIME);
        onSpChange(SpUtilsCur.KEYS_NOTIFY.LONG_END_TIME);
    }

    @Override
    public void onSpChange(String key) {
        if (key.equals(SpUtilsCur.KEYS_NOTIFY.BOOL_NO_DISTURB)) {
            mIsNoDisturb = SpUtilsCur.getBoolean(key, false);
        } else if (key.equals(SpUtilsCur.KEYS_NOTIFY.LONG_START_TIME)) {
            mStartTime = SpUtilsCur.getLong(SpUtilsCur.KEYS_NOTIFY.LONG_START_TIME, 0);
        } else if (key.equals(SpUtilsCur.KEYS_NOTIFY.LONG_END_TIME)) {
            mEndTime = SpUtilsCur.getLong(SpUtilsCur.KEYS_NOTIFY.LONG_END_TIME, 0);
        }
    }

    private boolean canDisturb() {
        mIsNoDisturb = SpUtilsCur.getBoolean(SpUtilsCur.KEYS_NOTIFY.BOOL_NO_DISTURB, false);
        if (!mIsNoDisturb) {
            return true;
        }
        mStartTime = SpUtilsCur.getLong(SpUtilsCur.KEYS_NOTIFY.LONG_START_TIME, 0);
        mEndTime = SpUtilsCur.getLong(SpUtilsCur.KEYS_NOTIFY.LONG_END_TIME, 0);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        cal.clear();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.YEAR, 0);
        cal.set(Calendar.MONTH, 0);
        cal.set(Calendar.DAY_OF_YEAR, 0);
        long cur = cal.getTimeInMillis();
        LogUtils.e("canDisturb", cur + "-cur\n" + mStartTime + "-start\n" + mEndTime + "-end");
        if (mStartTime < mEndTime) {
            return cur < mStartTime || cur > mEndTime;
        }
        return cur < mStartTime && cur > mEndTime;
    }

    private void callNotifyForNews() {
        if (canDisturb()) {
            if (SpUtilsCur.getBoolean(SpUtilsCur.KEYS_NOTIFY.BOOL_NOTIFY_NEWS_SOUND, true)) {
                if (System.currentTimeMillis() - lastPlaySoundTime >= 2000) {
                    lastPlaySoundTime = System.currentTimeMillis();
                    mSoundPool.play(mSoundId, 1f, 1f, 0, 0, 1);
                }
            }
            if (SpUtilsCur.getBoolean(SpUtilsCur.KEYS_NOTIFY.BOOL_NOTIFY_NEWS_SHAKE, true)) {
                if (System.currentTimeMillis() - lastVibrateTime >= 2000) {
                    lastVibrateTime = System.currentTimeMillis();
                    SysUtils.vibrate(new long[] { 100, 190, 200, 200 });
                }
            }
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void handle(GeneratedMessage.Builder b, int command, int sequence) {
        NetServiceFactory.getInstance().generalResp(0x80000000 + command, sequence);
        LogUtils.et("\r\n--------------HandlerProcessThread start --------------!!");

        switch (command) {
        // 系统通知
        case CommandConstants.SYSTEM_NOTICE_SERVER_PUSH_REQ:
            if (b instanceof SystemNoticeReq.Builder) {
                SystemNoticeReq.Builder req = (SystemNoticeReq.Builder) b;
                SystemNotice notice = SystemNoticeParser.parseSingle(req);
                if (notice != null) {
                    LogUtils.d("handlerProgressThread", JavaUtils.getString(notice));
                    switch (notice.getType()) {
                    case Properties.SYSNTEM_NOTICE_TYPE_NORMAL:

                        break;
                    case Properties.SYSNTEM_NOTICE_TYPE_FORCE_UPDATE:

                        break;
                    }
                }
                HashMap params = new HashMap();
                params.put("bean", notice);
                Task ts = new Task(CommandConstants.SYSTEM_NOTICE_SERVER_PUSH_REQ, params);
                NotifyService.newTask(ts);
            }
            break;
        // 其他终端登录
        case CommandConstants.USER_LOGIN_REQ:
        case CommandConstants.KICK_REQ:
            // UserLoginReq:clientType:code,mobile1:desc
            EpsApplication.exit(MainActivity.sMainActivity, LoginActivity.class, "该账号已在其他终端登录");
            LogUtils.d(this, "handle:CommandConstants.USER_LOGIN_REQ");
            break;
        // 有人关注我
        case CommandConstants.ADD_CONTACT_SERVER_PUSH_REQ:
            if (b instanceof AddContactServerPushReq.Builder) {
                AddContactServerPushReq.Builder req = (AddContactServerPushReq.Builder) b;
                Fans fans = new Fans();
                fans.setId(String.valueOf(req.getLogisticsId()));
                fans.setName(req.getName());
                fans.setTime(System.currentTimeMillis());
                if (ContactsDao.getInstance().queryById(fans.getId()) != null) {
                    fans.setStatus(Fans.status_added);
                }
                FansDao.getInstance().replace(fans);
                // PointDao.getInstance().show(PointCode.Code_Contacts_Fans);

                HashMap params = new HashMap();
                params.put("bean", fans);
                Task ts = new Task(CommandConstants.ADD_CONTACT_SERVER_PUSH_REQ, params);
                NotifyService.newTask(ts);
            }
            break;
        // 收到聊天消息
        case CommandConstants.CHAT_SEND_MULTI_TYPE_SERVER_PUSH_REQ:
            if (b instanceof ChatSendMultiTypeServerPushReq.Builder) {
                ChatSendMultiTypeServerPushReq.Builder req = (ChatSendMultiTypeServerPushReq.Builder) b;
                ProtoEChat chat = req.getChat();
                if (chat != null) {
                    ChatMsg chatMsg = ChatMsgParser.parse(chat);
                    // ChatMsgDao.getInstance().insert(chatMsg, true);
                    LogUtils.et("receiver chat:" + TextFormat.printToString(chat));
                    if (ChatMsgDao.getInstance().replace(chatMsg)) {
                        if (!isNeedSpeech()) {
                            callNotifyForNews();
                        }

                    }
                    callBySpeech(CommandConstants.CHAT_SEND_MULTI_TYPE_SERVER_PUSH_REQ, 
                    		 chatMsg.getSender_name() +"发来新消息");

                }
            }
            break;
        // 收到联系人发的公告
        case CommandConstants.BULLETIN_SERVER_PUSH_REQ:
            if (b instanceof BulletinServerPushReq.Builder) {
                BulletinServerPushReq.Builder req = (BulletinServerPushReq.Builder) b;
                Bulletin bulletin = BulletinParser.parse(req);
                BulletinDao.getInstance().insert(bulletin);

                LogUtils.et("receiver bulletin");

                if (!isNeedSpeech()) {
                    callNotifyForNews();
                }

                if (MainActivity.sCurPagePos != MainActivity.MESSAGE_POS) {
                    PointDao.getInstance().show(PointCode.Code_Message);
                }
                callBySpeech(CommandConstants.BULLETIN_SERVER_PUSH_REQ, "您有新的公告");

            }

            break;
        // 收到通知、转发的车源（货源）
        case CommandConstants.DELIVER_FREIGHT_SERVER_PUSH_REQ:
            if (b instanceof DeliverFreightServerPushReq.Builder) {
                DeliverFreightServerPushReq.Builder req = (DeliverFreightServerPushReq.Builder) b;
                Freight freight = null;
                // 朋友的车源货源
                if (req.hasIsFromFriend() && req.getIsFromFriend()) {
                    freight = FreightParser.parse(req.getFreight());
                    LogUtils.et("receive freight from contacts");
                } else {
                    // 收到的通知或转发的车源货源
                    FreightForward ff = FreightForwardParser.parse(req);
                    freight = ff.getFreight();
                    LogUtils.et("receive freight for message");
                }
                if (freight != null) {
                    FreightDao.getInstance().replace(freight);
                    if (!isNeedSpeech()) {
                        callNotifyForNews();
                    }
                }
                if (freight != null) {
                	 String tipcity = freight.getStart_region() + "到" + freight.getEnd_region();
                    if (freight.getType() == Properties.FREIGHT_TYPE_GOODS) {
                        callBySpeech(CommandConstants.DELIVER_FREIGHT_SERVER_PUSH_REQ,
                        		"您有新的货源从"+tipcity);
                    } else if (freight.getType() == Properties.FREIGHT_TYPE_VEHICLE) {
                        callBySpeech(CommandConstants.DELIVER_FREIGHT_SERVER_PUSH_REQ, 
                        		"您有新的车源从"+tipcity);
                    }
                }

            }
            break;
        // 车源货源被订状态变化
        case CommandConstants.FREIGHT_SERVER_ORDER_STATUS_PUSH_REQ:
            if (b instanceof FreightServerPushReq.Builder) {
                FreightServerPushReq.Builder req = (Builder) b;
                int freightId = req.getFreightId();
                Freight f = new Freight();
                f.setId(String.valueOf(freightId));
                FreightReceiver.send(f);
            }
            break;
        // 信息费订单创建推送 、信息费订单状态变化
        case CommandConstants.INFO_FEE_SERVER_GET_INFO_FEE_PUSH_REQ:
            if (b instanceof InfoFeeReq.Builder) {
                InfoFeeReq.Builder req = (InfoFeeReq.Builder) b;
                InfoFee infoFee = InfoFeeParser.parser(req.getInfoFee());

                LogUtils.d(this, JavaUtils.getString(infoFee));

                if (!isNeedSpeech()) {
                    callNotifyForNews();
                }

                HashMap params = new HashMap();
                params.put("bean", infoFee);
                Task ts = new Task(CommandConstants.INFO_FEE_SERVER_GET_INFO_FEE_PUSH_REQ, params);
                NotifyService.newTask(ts);

                String mineId = UserDao.getInstance().getUser().getId();
                boolean speech = false;
                String spehUser  ="";
                if (mineId.equals(String.valueOf(infoFee.getPayerId()))) {
                    switch (infoFee.getPayerFlowStatus()) {
                    case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_REQUEST_ORDERS:
                        speech = true;
                        spehUser = infoFee.getPayeeName();
                        break;
                    }
                } else {
                    if (infoFee.getPayeeFlowStatus() == Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_REQUEST_ORDERS) {
                        speech = true;
                        spehUser = infoFee.getPayerName();
                    }
                }
                boolean isNewOrder = false;
                if (speech) {
                    isNewOrder = true;
                    callBySpeech(CommandConstants.INFO_FEE_SERVER_GET_INFO_FEE_PUSH_REQ,
                    		spehUser+"给您下单");
                }
                NotificationUtils.notify(infoFee,isNewOrder);

                if (InfoFeeFragment.instace == null) {
                    infoFee.setLocalStatus(InfoFee.UNREAD);
                } else {
                    InfoFeeFragment.instace.onInfoFeeChange(infoFee);
                }
                new InfoFeeManager().changeDbAndList(infoFee, null);
            }
            break;
        // 使用 CommandConstants.INFO_FEE_SERVER_GET_INFO_FEE_PUSH_REQ
        case CommandConstants.INFO_FEE_SERVER_FLOW_STATUS_PUSH_REQ:
            break;
        default:
            LogUtils.et("not case the command:" + NetServiceUtils.formatCommand(command));
            break;
        }
        LogUtils.et("--------------HandlerProcessThread end --------------!!\r\n");
    }

    @Override
    public void run() {
        stop = false;

        while (!stop) {
            try {
                Thread.sleep(100);

                BuilderPackage builderPackage = msgQueue.take();
                handle(builderPackage.getMsg(), builderPackage.getCommand(), builderPackage.getSequence());

            } catch (Exception e) {
                LogUtils.e("HandlerProcessThread", e.toString());
                LogUtils.et("HandlerProcessThread: exception:" + e);
            }
        }
    }

    public void shutdown() {
        LogUtils.saveLog("HandlerProcessThread.shutdown", "entry");

        if (!stop) {
            stop = true;
            interrupt();
            if (mSoundPool != null) {
                mSoundPool.release();
            }
        }

        LogUtils.saveLog("HandlerProcessThread.shutdown", "exit");
    }

    // 处理语音播报
    private void callBySpeech(int what, String msg) {
        if (isNeedSpeech()) {
            try {
                boolean isSpeech = SpUtilsCur.getBoolean(SpUtilsCur.KEYS_NOTIFY.BOOL_OPEN_TTS, true);
                switch (what) {
                case CommandConstants.CHAT_SEND_MULTI_TYPE_SERVER_PUSH_REQ:// 收到聊天消息
                    if (isSpeech && canDisturb())
                        TTSServiceFactory.getInstance().play(msg);
                    else if (!isSpeech)
                        callNotifyForNews();

                    break;
                case CommandConstants.BULLETIN_SERVER_PUSH_REQ:// 收到公告
                    if (isSpeech && canDisturb())
                        TTSServiceFactory.getInstance().play(msg);
                    else if (!isSpeech)
                        callNotifyForNews();
                    break;
                case CommandConstants.DELIVER_FREIGHT_SERVER_PUSH_REQ:// 收到通知、转发的车源（货源）
                    if (isSpeech && canDisturb())
                        TTSServiceFactory.getInstance().play(msg);
                    else if (!isSpeech)
                        callNotifyForNews();
                    break;
                case CommandConstants.INFO_FEE_SERVER_GET_INFO_FEE_PUSH_REQ:// 产生新的订单
                    if (isSpeech && canDisturb())
                        TTSServiceFactory.getInstance().play(msg);
                    else if (!isSpeech)
                        callNotifyForNews();
                    break;
                default:
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.e(null, e);
                ToastUtils.showToast("语音播放失败");
            }

        }

    }

    private boolean isNeedSpeech() {
        if (!BaseActivity.isTop()) {
            return true;
        } else
            return false;

    }
}
