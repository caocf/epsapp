package com.epeisong.data.utils.infofee;

import java.util.ArrayList;
import java.util.List;

import com.epeisong.R;
import com.epeisong.base.view.statusview.StatusModel;
import com.epeisong.data.dao.UserDao;
import com.epeisong.logistics.common.Properties;
import com.epeisong.model.InfoFee;

/**
 * 信息费订单工具类
 * @author poet
 *
 */
public class InfoFeeFlowStatusUtils {

    static final int who_payer_publisher = 1;
    static final int who_payer_order = 2;
    static final int who_payee_publisher = 3;
    static final int who_payee_order = 4;

    static final int status_commit = 1;
    static final int status_accept = 2;
    static final int status_exeing = 3;
    static final int status_complete = 4;
    static final int status_cancel = 5;

    public static List<StatusModel> getStatusModels(final InfoFee infoFee) {
        int temp = 0;
        // 我是付款方，我是车源
        String curUserId = UserDao.getInstance().getUser().getId();
        if (curUserId.equals(String.valueOf(infoFee.getPayerId()))) {
            // 车源
            if (Properties.INFO_FEE_TYPE_VEHICLE == infoFee.getType()) {
                temp = who_payer_publisher;
            }
            // 货源
            else if (Properties.INFO_FEE_TYPE_GOODS == infoFee.getType()) {
                temp = who_payer_order;
            }
        } // 我是收款方，我是货源
        else if (curUserId.equals(String.valueOf(infoFee.getPayeeId()))) {
            // 货源
            if (Properties.INFO_FEE_TYPE_GOODS == infoFee.getType()) {
                temp = who_payee_publisher;
            }
            // 我是接单方
            else if (Properties.INFO_FEE_TYPE_VEHICLE == infoFee.getType()) {
                temp = who_payee_order;
            }
        }
        if (temp == 0) {
            return null;
        }
        String commitText, acceptText;
        if (temp == who_payer_publisher || temp == who_payee_publisher) {
            commitText = "对方提交订单";
            acceptText = "已接单";
        } else {
            commitText = "订单已提交";
            acceptText = "对方已接单";
        }
        final int _temp = temp;
        List<StatusModel> list = new ArrayList<StatusModel>();
        list.add(new StatusModel(R.drawable.icon_infofee_commit_true, R.drawable.icon_infofee_commit_false, commitText) {
            @Override
            protected boolean getIsOn() {
                return InfoFeeFlowStatusUtils.getIsOn4(status_commit, infoFee, _temp);
            }
        });
        list.add(new StatusModel(R.drawable.icon_infofee_accept_true, R.drawable.icon_infofee_accept_false, acceptText) {
            @Override
            protected boolean getIsOn() {
                return InfoFeeFlowStatusUtils.getIsOn4(status_accept, infoFee, _temp);
            }
        });
        list.add(new StatusModel(R.drawable.icon_infofee_exeing_true, R.drawable.icon_infofee_exeing_false, "执行中") {
            @Override
            protected boolean getIsOn() {
                return InfoFeeFlowStatusUtils.getIsOn4(status_exeing, infoFee, _temp);
            }
        });
        StatusModel cancelModel = new StatusModel(R.drawable.icon_infofee_cancel_true,
                R.drawable.icon_infofee_cancel_false, "已取消") {
            @Override
            protected boolean getIsOn() {
                return getIsOn4(status_cancel, infoFee, _temp);
            }
        };
        if (!cancelModel.isOn()) {
            list.add(new StatusModel(R.drawable.icon_infofee_complete_true, R.drawable.icon_infofee_complete_false,
                    "已完成") {
                @Override
                protected boolean getIsOn() {
                    return getIsOn4(status_complete, infoFee, _temp);
                }
            });
        } else {
            list.add(cancelModel);
        }
        return list;
    }

    private static boolean getIsOn4(int status, InfoFee infoFee, int who) {
        switch (who) {
        case who_payer_publisher:

            break;
        case who_payer_order:
            break;
        case who_payee_publisher:

            break;
        case who_payee_order:

            break;
        }
        return false;
    }

    private static boolean getIsOn4PayerPublisher(int status, InfoFee infoFee) {
        switch (infoFee.getPayerFlowStatus()) {
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_REQUEST_ORDERS:
            // 对方请求接单：取消订单、同意
            if (status == status_commit) {
                return true;
            }
            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_ORDERS_BE_CANCELLED:
            // 对方已取消订单

            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_ORDERCANCELLATION_BEFOREORDERCONFIRMATION:
            // 取消订单(等待车源方确认订单)(对方请求接单时)(自己待付款时)
            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_WAITING_FOR_PAYMENT:
            // 待付款：取消、付款
            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_ORDERCANCELLATION_BEFOREORDERCONFIRMATION_WAITING_FOR_PAYMENT:
            // 取消订单（待付款）
            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_ORDERS_BE_CANCELLED_WAITING_FOR_PAYMENT:
            // 对方取消订单（待付款）
            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_UNNEET_PAYMENT:
            // 无需付款，等待拉货
            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_PAY_TO_PLATFORM:
            // 已付款：申请取消订单、确认拉货、没拉到货，投诉
            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_CONFIRM_PULL_GOODS:
            // 确认拉货：无担保，结束；有担保，需要对方确认
            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_COMPLETE_FOR_CONFIRM_PULL_GOODS:
            // 订单完成（确认拉货）
            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_APPEAL_OF_NOT_COME_PULL_GOODS_FOR_CONFIRM_PULL_GOODS:
            // 确认拉货时，对方投诉你没有来拉货
            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_APPLY_ORDERCANCELLATION:
            // 申请取消订单中
            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_AGREE_ORDERCANCELLATION_OF_THE_PARTY:
            // 对方已同意取消订单:
            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_APPEAL_BY_REFUSE_ORDERCANCELLATION_OF_THE_PARTY:
            // 对方拒绝取消订单，并且已投诉：拒绝赔付、同意赔付
            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_AGREE_COMPENSATION_OF_APPLY_ORDERCANCELLATION:
            // 同意赔付
            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_REFUSE_COMPENSATION_OF_APPLY_ORDERCANCELLATION:
            // 拒绝赔付，等待处理结果
            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_APPEALED_OF_NO_GOODS:
            // 没拉到货，投诉
            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_AGREE_COMPENSATION_OF_NO_GOODS:
            // 对方同意赔付和退款(没有拉到货，已投诉)
            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_REFUSE_COMPENSATION_OF_NO_GOODS:
            // 对方拒绝赔付和退款(没有拉到货，已投诉)
            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_DUNNING_OF_THE_PARTY:
            // 对方催促您确认：确认拉货、投诉
            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_APPLY_ORDERCANCELLATION_OF_THE_PARTY:
            // 对方申请取消订单：投诉、同意请求
            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_AGREE_ORDERCANCELLATION_WITHY_THE_PARTY:
            // 同意对方取消订单
            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_APPEALING_BY_REFUSE_ORDERCANCELLATION_WITHY_THE_PARTY:
            // 拒绝对方取消订单,投诉中
            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_AGREE_COMPENSATION_OF_APPLY_ORDERCANCELLATION_OF_THE_PARTY:
            // 对方同意赔付(对方申请取消订单,拒绝后)
            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_REFUSE_COMPENSATION_OF_APPLY_ORDERCANCELLATION_OF_THE_PARTY:
            // 对方拒绝赔付(对方申请取消订单,拒绝后)
            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_APPEAL_OF_NOT_COME_PULL_GOODS:
            // 没去拉货，对方投诉：拒绝赔付、同意赔付
            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_REFUSE_COMPENSATION_OF_NOT_COME_PULL_GOODS:
            // 拒绝赔付
            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_AGREE_COMPENSATION_OF_NOT_COME_PULL_GOODS:
            // 拒绝赔付
            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_APPEAL_COMPLETE:
            // 投诉处理完成
            break;
        }

        return false;
    }
}
