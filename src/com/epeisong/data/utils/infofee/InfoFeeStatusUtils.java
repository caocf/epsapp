package com.epeisong.data.utils.infofee;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;

import com.epeisong.R;
import com.epeisong.base.view.statusview.StatusModel;
import com.epeisong.logistics.common.Properties;
import com.epeisong.model.InfoFee;
import com.epeisong.utils.LogUtils;

/**
 * 根据订单状态，获取本地自定义结果
 * @author poet
 *
 */
public class InfoFeeStatusUtils {

    public static final int OPERATION_CALL = -1;
    public static final int OPERATION_COMPLAIN_RESULT = -2;

    public static InfoFeeStatusResult getResult(InfoFeeStatusParams params) {
        InfoFeeStatusResult result = new InfoFeeStatusResult();
        if (params.needOperation) {
            result.operations = new ArrayList<Operation>();
        }
        if (params.needStatusModel) {
            result.statusModels = new ArrayList<StatusModel>();
        }
        if (params.curUserId.equals(String.valueOf(params.infoFee.getPayerId()))) {
            result.isPayer = true;
            if (Properties.INFO_FEE_TYPE_VEHICLE == params.infoFee.getType()) {
                result.source = "配货方";
                result.sourceName = params.infoFee.getPayeeName();
                setResultPayerPublisher(params, result);
            } else if (Properties.INFO_FEE_TYPE_GOODS == params.infoFee.getType()) {
                result.source = "货源方";
                result.sourceName = params.infoFee.getPayeeName();
                setResultPayerOrder(params, result);
            }
        } else if (params.curUserId.equals(String.valueOf(params.infoFee.getPayeeId()))) {
            result.isPayer = false;
            if (Properties.INFO_FEE_TYPE_VEHICLE == params.infoFee.getType()) {
                result.source = "车源方";
                result.sourceName = params.infoFee.getPayerName();
                setResultPayeeOrder(params, result);
            } else if (Properties.INFO_FEE_TYPE_GOODS == params.infoFee.getType()) {
                result.source = "订货方";
                result.sourceName = params.infoFee.getPayerName();
                setResultPayeePublisher(params, result);
            }
        }
        return result;
    }

    private static void setResultPayerPublisher(InfoFeeStatusParams params, InfoFeeStatusResult result) {
        InfoFee infoFee = params.infoFee;
        String text1 = "对方提交订单";
        String text2 = "已接单";
        LogUtils.d("InfoFeeStatusUtils", "setResultPayerPublisher:" + infoFee.getPayerFlowStatus());
        switch (infoFee.getPayerFlowStatus()) {
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_REQUEST_ORDERS:
            // 对方请求接单：取消订单、同意
            result.payerStatus = "对方请求接单";
            if (params.needOperation) {
                result.operations.add(new Operation("取消订单",
                        Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_ORDERCANCELLATION_BEFOREORDERCONFIRMATION));
                result.operations.add(new Operation("同意",
                        Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_WAITING_FOR_PAYMENT));
            }
            setExeSmallStatus(params, result);
            if (params.needStatusModel) {
                setStatusModels(result.statusModels, text1, text2, true, false, false, false, false);
            }
            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_ORDERS_BE_CANCELLED:
            // 对方已取消订单
            result.payerStatus = "对方已取消订单";
            setCancelSmallStatus(params, result);
            setCancelWhenCommitStatusModel(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_ORDERCANCELLATION_BEFOREORDERCONFIRMATION:
            // 取消订单(等待车源方确认订单)(对方请求接单时)(自己待付款时)
            result.payerStatus = "您已取消订单";
            setCancelSmallStatus(params, result);
            setCancelWhenCommitStatusModel(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_WAITING_FOR_PAYMENT:
            // 待付款：取消、付款
            result.payerStatus = "待付款";
            result.payerStatusColor = Color.RED;
            if (params.needOperation) {
                result.operations
                        .add(new Operation(
                                "取消订单",
                                Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_ORDERCANCELLATION_BEFOREORDERCONFIRMATION_WAITING_FOR_PAYMENT));
                result.operations.add(new Operation("付款", Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_PAY_TO_PLATFORM));
            }
            setExeSmallStatus(params, result);
            setAcceptStatusModel(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_ORDERCANCELLATION_BEFOREORDERCONFIRMATION_WAITING_FOR_PAYMENT:
            // 取消订单（待付款）
            result.payerStatus = "您已取消订单";
            setCancelSmallStatus(params, result);
            setCancelWhenAcceptStatusModel(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_ORDERS_BE_CANCELLED_WAITING_FOR_PAYMENT:
            // 对方取消订单（待付款）
            result.payerStatus = "对方取消订单";
            setCancelSmallStatus(params, result);
            setCancelWhenAcceptStatusModel(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_UNNEET_PAYMENT:
            // 无需付款，等待拉货
            result.payerStatus = "等待拉货";
            if (params.needOperation) {
                result.operations.add(new Operation("没货，投诉",
                        Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_APPEALED_OF_NO_GOODS));
                result.operations.add(new Operation("申请取消",
                        Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_APPLY_ORDERCANCELLATION));
                result.operations.add(new Operation("确认拉货",
                        Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_CONFIRM_PULL_GOODS));
            }
            setExeSmallStatus(params, result);
            setExeStatusModel(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_PAY_TO_PLATFORM:
            // 已付款：申请取消、确认拉货、没拉到货，投诉
            result.payerStatus = "已付款，等待拉货";
            if (params.needOperation) {
                result.operations.add(new Operation("没货，投诉",
                        Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_APPEALED_OF_NO_GOODS));
                result.operations.add(new Operation("申请取消",
                        Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_APPLY_ORDERCANCELLATION));
                result.operations.add(new Operation("确认拉货",
                        Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_CONFIRM_PULL_GOODS));
            }
            setExeSmallStatus(params, result);
            setExeStatusModel(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_CONFIRM_PULL_GOODS:
            // 确认拉货：无担保，结束；有担保，需要对方确认
            if (infoFee.getPayerGuaranteeAmount() > 0) {
                result.payerStatus = "已拉货，等待对方确认";
                setExeSmallStatus(params, result);
                setExeStatusModel(params, result, text1, text2);
            } else {
                result.payerStatus = "已确认拉货";
                setCompleteSmallStatus(params, result);
                setCompleteStatusModels(params, result, text1, text2);
            }
            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_COMPLETE_FOR_CONFIRM_PULL_GOODS:
            // 订单完成（确认拉货）
            result.payerStatus = "订单完成";
            setCompleteSmallStatus(params, result);
            setCompleteStatusModels(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_APPEAL_OF_NOT_COME_PULL_GOODS_FOR_CONFIRM_PULL_GOODS:
            // 确认拉货时，对方投诉你没有来拉货
            if (infoFee.getStatus() == Properties.INFO_FEE_STATUS_COMPLETE) {
                result.payerStatus = "投诉已处理";
                result.hasComplainResult = true;
                setCompleteSmallStatus(params, result);
                setCompleteStatusModels(params, result, text1, text2);
            } else {
                result.payerStatus = "没去拉货，投诉处理中";
                setComplainSmallStatus(params, result);
                setExeStatusModel(params, result, text1, text2);
            }
            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_APPLY_ORDERCANCELLATION:
            // 申请取消中
            result.payerStatus = "申请取消中";
            setExeSmallStatus(params, result);
            setExeStatusModel(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_AGREE_ORDERCANCELLATION_OF_THE_PARTY:
            // 对方已同意取消订单:
            result.payerStatus = "对方已同意取消订单";
            setCancelSmallStatus(params, result);
            setCancelWhenExeStatusModels(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_APPEAL_BY_REFUSE_ORDERCANCELLATION_OF_THE_PARTY:
            // 对方拒绝取消订单，已投诉：拒绝赔付、同意赔付
            result.payerStatus = "对方拒绝取消订单，已投诉";
            if (params.needOperation && (infoFee.getPayerGuaranteeAmount() > 0 || infoFee.getInfoAmount() > 0)) {
                String no, yes;
                if (infoFee.getPayerGuaranteeAmount() > 0) {
                    no = "拒绝赔付";
                    yes = "同意赔付";
                } else {
                    no = "拒绝退款";
                    yes = "同意退款";
                }
                result.operations.add(new Operation(no,
                        Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_REFUSE_COMPENSATION_OF_APPLY_ORDERCANCELLATION));
                result.operations.add(new Operation(yes,
                        Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_AGREE_COMPENSATION_OF_APPLY_ORDERCANCELLATION));
            }
            setComplainSmallStatus(params, result);
            setExeStatusModel(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_AGREE_COMPENSATION_OF_APPLY_ORDERCANCELLATION:
            // 同意赔付
            result.payerStatus = "同意赔付";
            setCompleteSmallStatus(params, result);
            setCompleteStatusModels(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_REFUSE_COMPENSATION_OF_APPLY_ORDERCANCELLATION:
            // 拒绝赔付，等待平台处理
            if (infoFee.getStatus() == Properties.INFO_FEE_STATUS_COMPLETE) {
                result.payerStatus = "投诉已处理";
                result.hasComplainResult = true;
                setCompleteSmallStatus(params, result);
                setCompleteStatusModels(params, result, text1, text2);
            } else {
                result.payerStatus = "对方拒绝赔付，已投诉";
                setComplainSmallStatus(params, result);
                setExeStatusModel(params, result, text1, text2);
            }
            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_APPEALED_OF_NO_GOODS:
            // 没拉到货，投诉
            if (infoFee.getStatus() == Properties.INFO_FEE_STATUS_COMPLETE) {
                result.payerStatus = "投诉已处理";
                result.hasComplainResult = true;
                setCompleteSmallStatus(params, result);
                setCompleteStatusModels(params, result, text1, text2);
            } else {
                result.payerStatus = "没拉到货，已投诉";
                setComplainSmallStatus(params, result);
                setExeStatusModel(params, result, text1, text2);
            }
            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_AGREE_COMPENSATION_OF_NO_GOODS:
            // 对方已赔付和退款(没有拉到货，已投诉)
            result.payerStatus = "对方已赔付";
            setCompleteSmallStatus(params, result);
            setCompleteStatusModels(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_REFUSE_COMPENSATION_OF_NO_GOODS:
            // 对方拒绝赔付和退款(没有拉到货，已投诉)
            if (infoFee.getStatus() == Properties.INFO_FEE_STATUS_COMPLETE) {
                result.payerStatus = "投诉已处理";
                result.hasComplainResult = true;
                setCompleteSmallStatus(params, result);
                setCompleteStatusModels(params, result, text1, text2);
            } else {
                result.payerStatus = "等待平台处理";
                setComplainSmallStatus(params, result);
                setExeStatusModel(params, result, text1, text2);
            }
            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_DUNNING_OF_THE_PARTY:
            // 对方催促您确认：确认拉货、投诉
            result.payerStatus = "对方催促您确认拉货";
            if (params.needOperation) {
                result.operations.add(new Operation("投诉",
                        Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_APPEAL_DUNNING_OF_THE_PARTY));
                result.operations.add(new Operation("确认拉货",
                        Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_COMPLETE_FOR_DUNNING_THE_PARTY));
            }
            setExeSmallStatus(params, result);
            setExeStatusModel(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_APPLY_ORDERCANCELLATION_OF_THE_PARTY:
            // 对方申请取消：投诉、同意请求
            result.payerStatus = "对方申请取消";
            if (params.needOperation) {
                result.operations
                        .add(new Operation(
                                "投诉",
                                Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_APPEALING_BY_REFUSE_ORDERCANCELLATION_WITHY_THE_PARTY));
                result.operations.add(new Operation("同意请求",
                        Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_AGREE_ORDERCANCELLATION_WITHY_THE_PARTY));
            }
            setExeSmallStatus(params, result);
            setExeStatusModel(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_AGREE_ORDERCANCELLATION_WITHY_THE_PARTY:
            // 同意对方取消订单
            result.payerStatus = "同意对方取消订单";
            setCancelSmallStatus(params, result);
            setCancelWhenExeStatusModels(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_APPEALING_BY_REFUSE_ORDERCANCELLATION_WITHY_THE_PARTY:
            // 拒绝对方取消订单,投诉中
            if (infoFee.getStatus() == Properties.INFO_FEE_STATUS_COMPLETE) {
                result.payerStatus = "投诉已处理";
                result.hasComplainResult = true;
                setCompleteSmallStatus(params, result);
                setCompleteStatusModels(params, result, text1, text2);
            } else {
                result.payerStatus = "拒绝对方取消订单,已投诉";
                setComplainSmallStatus(params, result);
                setExeStatusModel(params, result, text1, text2);
            }
            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_AGREE_COMPENSATION_OF_APPLY_ORDERCANCELLATION_OF_THE_PARTY:
            // 对方已赔付(对方申请取消,拒绝后)
            result.payerStatus = "对方已赔付";
            setCompleteSmallStatus(params, result);
            setCompleteStatusModels(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_REFUSE_COMPENSATION_OF_APPLY_ORDERCANCELLATION_OF_THE_PARTY:
            // 对方拒绝赔付(对方申请取消,拒绝后)
            result.payerStatus = "对方拒绝赔付";
            setExeSmallStatus(params, result);
            setExeStatusModel(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_APPEAL_OF_NOT_COME_PULL_GOODS:
            // 没去拉货，对方投诉：拒绝赔付、同意赔付
            result.payerStatus = "没去拉货，对方投诉";
            if (params.needOperation && (infoFee.getPayerGuaranteeAmount() > 0 || infoFee.getInfoAmount() > 0)) {
                String no, yes;
                if (infoFee.getPayerGuaranteeAmount() > 0) {
                    no = "拒绝赔付";
                    yes = "同意赔付";
                } else {
                    no = "拒绝退款";
                    yes = "同意退款";
                }
                result.operations.add(new Operation(no,
                        Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_REFUSE_COMPENSATION_OF_NOT_COME_PULL_GOODS));
                result.operations.add(new Operation(yes,
                        Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_AGREE_COMPENSATION_OF_NOT_COME_PULL_GOODS));
            }
            setComplainSmallStatus(params, result);
            setExeStatusModel(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_REFUSE_COMPENSATION_OF_NOT_COME_PULL_GOODS:
            // 拒绝赔付(没有去拉货，已被投诉)
            if (infoFee.getStatus() == Properties.INFO_FEE_STATUS_COMPLETE) {
                result.payerStatus = "投诉已处理";
                result.hasComplainResult = true;
                setCompleteSmallStatus(params, result);
                setCompleteStatusModels(params, result, text1, text2);
            } else {
                result.payerStatus = "等待投诉处理结果";
                setComplainSmallStatus(params, result);
                setExeStatusModel(params, result, text1, text2);
            }
            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_AGREE_COMPENSATION_OF_NOT_COME_PULL_GOODS:
            // 同意赔付(没有去拉货，已被投诉)
            result.payerStatus = "已同意赔付";
            setCompleteSmallStatus(params, result);
            setCompleteStatusModels(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_APPEAL_COMPLETE:
            // 投诉处理完成
            result.payerStatus = "投诉处理完成";
            setCompleteSmallStatus(params, result);
            setCompleteStatusModels(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_COMPLETE_FOR_DUNNING_THE_PARTY:
            // 已经确认拉货(被对方催促)
            result.payerStatus = "已确认拉货";
            setCompleteSmallStatus(params, result);
            setCompleteStatusModels(params, result, text1, text2);
            break;
        default:
            setResultDefault(params, result, true);
            break;
        }
    }

    private static void setResultPayerOrder(InfoFeeStatusParams params, InfoFeeStatusResult result) {
        InfoFee infoFee = params.infoFee;
        String text1 = "订单已提交";
        String text2 = "对方已接单";
        LogUtils.d("InfoFeeStatusUtils", "setResultPayerOrder:" + infoFee.getPayerFlowStatus());
        switch (infoFee.getPayerFlowStatus()) {
        case Properties.INFO_FEE_PAYER_ORDER_STATUS_WAITING_FOR_CONFIRM:
            // 等待对方确认:取消订单
            result.payerStatus = "等待对方确认";
            if (params.needOperation) {
                result.operations.add(new Operation("电话催单", OPERATION_CALL));
                result.operations.add(new Operation("取消订单",
                        Properties.INFO_FEE_PAYER_ORDER_STATUS_ORDERCANCELLATION_BEFOREORDERCONFIRMATION));
            }
            setExeSmallStatus(params, result);
            setCommitStatusModel(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYER_ORDER_STATUS_ORDERCANCELLATION_BEFOREORDERCONFIRMATION:
            // 取消已订单
            result.payerStatus = "订单已取消";
            setCancelSmallStatus(params, result);
            setCancelWhenCommitStatusModel(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYER_ORDER_STATUS_ORDERS_BE_CANCELLED:
            // 对方取消订单:无
            result.payerStatus = "对方已取消订单";
            setCancelSmallStatus(params, result);
            setCancelWhenCommitStatusModel(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYER_ORDER_STATUS_WAITING_FOR_PAYMENT:
            // 待付款:付款、取消订单
            result.payerStatus = "待付款";
            result.payerStatusColor = Color.RED;
            if (params.needOperation) {
                result.operations
                        .add(new Operation(
                                "取消订单",
                                Properties.INFO_FEE_PAYER_ORDER_STATUS_ORDERCANCELLATION_BEFOREORDERCONFIRMATION_WAITING_FOR_PAYMENT));
                result.operations.add(new Operation("付款", Properties.INFO_FEE_PAYER_ORDER_STATUS_PAY_TO_PLATFORM));
            }
            setExeSmallStatus(params, result);
            setAcceptStatusModel(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYER_ORDER_STATUS_ORDERCANCELLATION_BEFOREORDERCONFIRMATION_WAITING_FOR_PAYMENT:
            // 取消订单(待付款)
            result.payerStatus = "您已取消订单";
            setCancelSmallStatus(params, result);
            setCancelWhenAcceptStatusModel(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYER_ORDER_STATUS_ORDERS_BE_CANCELLED_WAITING_FOR_PAYMENT:
            // 对方已取消订单（待付款）
            result.payerStatus = "对方已取消订单";
            setCancelSmallStatus(params, result);
            setCancelWhenAcceptStatusModel(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYER_ORDER_STATUS_UNNEET_PAYMENT:
            // 无需付款，等待拉货
            result.payerStatus = "等待拉货";
            if (params.needOperation) {
                result.operations.add(new Operation("没拉到货,投诉",
                        Properties.INFO_FEE_PAYER_ORDER_STATUS_APPEALED_OF_NO_GOODS));
                result.operations.add(new Operation("申请取消",
                        Properties.INFO_FEE_PAYER_ORDER_STATUS_APPLY_ORDERCANCELLATION));
                result.operations.add(new Operation("确认拉货", Properties.INFO_FEE_PAYER_ORDER_STATUS_CONFIRM_PULL_GOODS));
            }
            setExeSmallStatus(params, result);
            setExeStatusModel(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYER_ORDER_STATUS_PAY_TO_PLATFORM:
            // 付完款
            result.payerStatus = "已付款到平台，等待拉货";
            if (params.needOperation) {
                result.operations.add(new Operation("没拉到货,投诉",
                        Properties.INFO_FEE_PAYER_ORDER_STATUS_APPEALED_OF_NO_GOODS));
                result.operations.add(new Operation("申请取消",
                        Properties.INFO_FEE_PAYER_ORDER_STATUS_APPLY_ORDERCANCELLATION));
                result.operations.add(new Operation("确认拉货", Properties.INFO_FEE_PAYER_ORDER_STATUS_CONFIRM_PULL_GOODS));
            }
            setExeSmallStatus(params, result);
            setExeStatusModel(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYER_ORDER_STATUS_CONFIRM_PULL_GOODS:
            // 确认拉货：无担保，结束；有担保，需要对方确认
            if (infoFee.getPayerGuaranteeAmount() > 0) {
                result.payerStatus = "等待对方确认";
                setExeSmallStatus(params, result);
                setExeStatusModel(params, result, text1, text2);
            } else {
                result.payerStatus = "已确认拉货";
                setCompleteSmallStatus(params, result);
                setCompleteStatusModels(params, result, text1, text2);
            }
            break;
        case Properties.INFO_FEE_PAYER_ORDER_STATUS_COMPLETE_FOR_CONFIRM_PULL_GOODS:
            // 订单完成（确认拉货）
            result.payerStatus = "订单完成";
            setCompleteSmallStatus(params, result);
            setCompleteStatusModels(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYER_ORDER_STATUS_APPEAL_OF_NOT_COME_PULL_GOODS_FOR_CONFIRM_PULL_GOODS:
            // 确认拉货，对方投诉没去拉货
            if (infoFee.getStatus() == Properties.INFO_FEE_STATUS_COMPLETE) {
                result.payerStatus = "投诉已处理";
                result.hasComplainResult = true;
                setCompleteSmallStatus(params, result);
                setCompleteStatusModels(params, result, text1, text2);
            } else {
                result.payerStatus = "没去拉货，对方已投诉";
                setComplainSmallStatus(params, result);
                setExeStatusModel(params, result, text1, text2);
            }
            break;
        case Properties.INFO_FEE_PAYER_ORDER_STATUS_APPEALED_OF_NO_GOODS:
            // 申诉中:没拉到货,已投诉
            if (infoFee.getStatus() == Properties.INFO_FEE_STATUS_COMPLETE) {
                result.payerStatus = "投诉已处理";
                result.hasComplainResult = true;
                setCompleteSmallStatus(params, result);
                setCompleteStatusModels(params, result, text1, text2);
            } else {
                result.payerStatus = "没拉到货,已投诉";
                setComplainSmallStatus(params, result);
                setExeStatusModel(params, result, text1, text2);
            }
            break;
        case Properties.INFO_FEE_PAYER_ORDER_STATUS_AGREE_COMPENSATION_OF_NO_GOODS:
            // 没拉到货, 对方已赔付和退款
            if (infoFee.getPayeeGuaranteeAmount() > 0) {
                result.payerStatus = "对方已赔付";
            } else {
                result.payerStatus = "对方同意退款";
            }
            setCompleteSmallStatus(params, result);
            setCompleteStatusModels(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYER_ORDER_STATUS_REFUSE_COMPENSATION_OF_NO_GOODS:
            // 没拉到货,对方,拒绝赔付和退款
            if (infoFee.getStatus() == Properties.INFO_FEE_STATUS_COMPLETE) {
                result.payerStatus = "投诉已处理";
                result.hasComplainResult = true;
                setCompleteSmallStatus(params, result);
                setCompleteStatusModels(params, result, text1, text2);
            } else {
                result.payerStatus = "对方拒绝赔付，已投诉";
                setComplainSmallStatus(params, result);
                setExeStatusModel(params, result, text1, text2);
            }
            break;
        case Properties.INFO_FEE_PAYER_ORDER_STATUS_APPLY_ORDERCANCELLATION:
            // 申请取消中
            result.payerStatus = "申请取消中";
            setExeSmallStatus(params, result);
            setExeStatusModel(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYER_ORDER_STATUS_AGREE_ORDERCANCELLATION_OF_THE_PARTY:
            // 对方同意取消订单
            result.payerStatus = "对方同意取消订单";
            setCancelSmallStatus(params, result);
            setCancelWhenExeStatusModels(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYER_ORDER_STATUS_APPEAL_BY_REFUSE_ORDERCANCELLATION_OF_THE_PARTY:
            // 对方拒绝取消订单,已投诉
            result.payerStatus = "对方拒绝取消订单，已投诉";
            if (infoFee.getStatus() == Properties.INFO_FEE_STATUS_COMPLETE) {
                result.payerStatus = "投诉已处理";
                setCompleteSmallStatus(params, result);
                setCompleteStatusModels(params, result, text1, text2);
            } else {
                if (params.needOperation && (infoFee.getPayerGuaranteeAmount() > 0 || infoFee.getInfoAmount() > 0)) {
                    String no, yes;
                    if (infoFee.getPayerGuaranteeAmount() > 0) {
                        no = "拒绝赔付";
                        yes = "同意赔付";
                    } else {
                        no = "拒绝退款";
                        yes = "同意退款";
                    }
                    result.operations.add(new Operation(no,
                            Properties.INFO_FEE_PAYER_ORDER_STATUS_REFUSE_COMPENSATION_OF_APPLY_ORDERCANCELLATION));
                    result.operations.add(new Operation(yes,
                            Properties.INFO_FEE_PAYER_ORDER_STATUS_AGREE_COMPENSATION_OF_APPLY_ORDERCANCELLATION));
                }
                setComplainSmallStatus(params, result);
                setExeStatusModel(params, result, text1, text2);
            }
            break;
        case Properties.INFO_FEE_PAYER_ORDER_STATUS_AGREE_COMPENSATION_OF_APPLY_ORDERCANCELLATION:
            // 对方拒绝取消订单,已投诉 ,同意赔付
            result.payerStatus = "同意赔付";
            setCompleteSmallStatus(params, result);
            setCompleteStatusModels(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYER_ORDER_STATUS_REFUSE_COMPENSATION_OF_APPLY_ORDERCANCELLATION:
            // 对方拒绝取消订单,已投诉 ,拒绝赔付
            if (infoFee.getStatus() == Properties.INFO_FEE_STATUS_COMPLETE) {
                result.payerStatus = "投诉已处理";
                result.hasComplainResult = true;
                setCompleteSmallStatus(params, result);
                setCompleteStatusModels(params, result, text1, text2);
            } else {
                result.payerStatus = "对方拒绝赔付，已投诉";
                setComplainSmallStatus(params, result);
                setExeStatusModel(params, result, text1, text2);
            }
            break;
        case Properties.INFO_FEE_PAYER_ORDER_STATUS_APPLY_ORDERCANCELLATION_OF_THE_PARTY:
            // 对方请求取消订单
            result.payerStatus = "对方请求取消订单";
            if (params.needOperation) {
                result.operations.add(new Operation("投诉",
                        Properties.INFO_FEE_PAYER_ORDER_STATUS_APPEALING_BY_REFUSE_ORDERCANCELLATION_WITHY_THE_PARTY));
                result.operations.add(new Operation("同意请求",
                        Properties.INFO_FEE_PAYER_ORDER_STATUS_AGREE_ORDERCANCELLATION_WITHY_THE_PARTY));
            }
            setExeSmallStatus(params, result);
            setExeStatusModel(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYER_ORDER_STATUS_APPEALING_BY_REFUSE_ORDERCANCELLATION_WITHY_THE_PARTY:
            // 拒绝对方取消订单,投诉中
            if (infoFee.getStatus() == Properties.INFO_FEE_STATUS_COMPLETE) {
                result.payerStatus = "投诉已处理";
                result.hasComplainResult = true;
                setCompleteSmallStatus(params, result);
                setCompleteStatusModels(params, result, text1, text2);
            } else {
                result.payerStatus = "拒绝对方取消订单，已投诉";
                setComplainSmallStatus(params, result);
                setExeStatusModel(params, result, text1, text2);
            }
            break;
        case Properties.INFO_FEE_PAYER_ORDER_STATUS_AGREE_COMPENSATION_OF_APPLY_ORDERCANCELLATION_OF_THE_PARTY:
            // 对方已赔付(对方申请取消,拒绝后)
            result.payerStatus = "对方已赔付";
            setCompleteSmallStatus(params, result);
            setCompleteStatusModels(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYER_ORDER_STATUS_REFUSE_COMPENSATION_OF_APPLY_ORDERCANCELLATION_OF_THE_PARTY:
            // 对方拒绝赔付(对方申请取消,拒绝后)
            if (infoFee.getStatus() == Properties.INFO_FEE_STATUS_COMPLETE) {
                result.payerStatus = "投诉已处理";
                result.hasComplainResult = true;
                setCompleteSmallStatus(params, result);
                setCompleteStatusModels(params, result, text1, text2);
            } else {
                result.payerStatus = "对方拒绝赔付，已投诉";
                setComplainSmallStatus(params, result);
                setExeStatusModel(params, result, text1, text2);
            }
            break;
        case Properties.INFO_FEE_PAYER_ORDER_STATUS_AGREE_ORDERCANCELLATION_WITHY_THE_PARTY:
            // 同意对方取消订单
            result.payerStatus = "同意对方取消订单";
            setCancelSmallStatus(params, result);
            setCancelWhenExeStatusModels(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYER_ORDER_STATUS_APPEAL_OF_NOT_COME_PULL_GOODS:
            // 没有去拉货,已被投诉
            if (infoFee.getStatus() == Properties.INFO_FEE_STATUS_COMPLETE) {
                result.payerStatus = "投诉已处理";
                result.hasComplainResult = true;
                setCompleteSmallStatus(params, result);
                setCompleteStatusModels(params, result, text1, text2);
            } else {
                result.payerStatus = "没去拉货,对方已投诉";
                if (params.needOperation && (infoFee.getPayerGuaranteeAmount() > 0 || infoFee.getInfoAmount() > 0)) {
                    String no, yes;
                    if (infoFee.getPayerGuaranteeAmount() > 0) {
                        no = "拒绝赔付";
                        yes = "同意赔付";
                    } else {
                        no = "拒绝退款";
                        yes = "同意退款";
                    }
                    result.operations.add(new Operation(no,
                            Properties.INFO_FEE_PAYER_ORDER_STATUS_REFUSE_COMPENSATION_OF_NOT_COME_PULL_GOODS));
                    result.operations.add(new Operation(yes,
                            Properties.INFO_FEE_PAYER_ORDER_STATUS_AGREE_COMPENSATION_OF_NOT_COME_PULL_GOODS));
                }
                setComplainSmallStatus(params, result);
                setExeStatusModel(params, result, text1, text2);
            }
            break;
        case Properties.INFO_FEE_PAYER_ORDER_STATUS_AGREE_COMPENSATION_OF_NOT_COME_PULL_GOODS:
            // 没有去拉货,已被投诉,同意赔付
            result.payerStatus = "没去拉货，同意赔付";
            setCompleteSmallStatus(params, result);
            setCompleteStatusModels(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYER_ORDER_STATUS_REFUSE_COMPENSATION_OF_NOT_COME_PULL_GOODS:
            // 没有去拉货,已被投诉,拒绝赔付,等待投诉处理结果
            if (infoFee.getStatus() == Properties.INFO_FEE_STATUS_COMPLETE) {
                result.payerStatus = "投诉已处理";
                result.hasComplainResult = true;
                setCompleteSmallStatus(params, result);
                setCompleteStatusModels(params, result, text1, text2);
            } else {
                result.payerStatus = "拒绝赔付，对方已投诉";
                setComplainSmallStatus(params, result);
                setExeStatusModel(params, result, text1, text2);
            }
            break;
        case Properties.INFO_FEE_PAYER_ORDER_STATUS_DUNNING_OF_THE_PARTY:
            // 对方催促您:确认拉货、投诉
            result.payerStatus = "对方催促您确认拉货";
            if (params.needOperation) {
                result.operations.add(new Operation("投诉",
                        Properties.INFO_FEE_PAYER_ORDER_STATUS_APPEAL_DUNNING_OF_THE_PARTY));
                result.operations.add(new Operation("确认拉货",
                        Properties.INFO_FEE_PAYER_ORDER_STATUS_COMPLETE_FOR_DUNNING_THE_PARTY));
            }
            setExeSmallStatus(params, result);
            setExeStatusModel(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYER_ORDER_STATUS_APPEAL_DUNNING_OF_THE_PARTY:
            // 被对方催促后,投诉
            if (infoFee.getStatus() == Properties.INFO_FEE_STATUS_COMPLETE) {
                result.payerStatus = "投诉已处理";
                result.hasComplainResult = true;
                setCompleteSmallStatus(params, result);
                setCompleteStatusModels(params, result, text1, text2);
            } else {
                result.payerStatus = "等待投诉处理结果";
                setComplainSmallStatus(params, result);
                setExeStatusModel(params, result, text1, text2);
            }
            break;
        case Properties.INFO_FEE_PAYER_ORDER_STATUS_APPEAL_COMPLETE:
            // 投诉处理完成:无
            result.payerStatus = "投诉已处理";
            result.hasComplainResult = true;
            setCompleteSmallStatus(params, result);
            setCompleteStatusModels(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYER_ORDER_STATUS_COMPLETE_FOR_DUNNING_THE_PARTY:
            // 已经确认拉货(被对方催促)
            result.payerStatus = "已确认拉货";
            setCompleteSmallStatus(params, result);
            setCompleteStatusModels(params, result, text1, text2);
            break;
        default:
            setResultDefault(params, result, true);
            break;
        }
    }

    private static void setResultPayeePublisher(InfoFeeStatusParams params, InfoFeeStatusResult result) {
        InfoFee infoFee = params.infoFee;
        String text1 = "对方提交订单";
        String text2 = "已接单";
        LogUtils.d("InfoFeeStatusUtils", "setResultPayeePublisher:" + infoFee.getPayeeFlowStatus());
        switch (infoFee.getPayeeFlowStatus()) {
        case Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_REQUEST_ORDERS:
            // 对方请求接单：取消订单、同意
            result.payeeStatus = "对方请求接单";
            if (params.needOperation) {
                result.operations.add(new Operation("取消订单",
                        Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_ORDERCANCELLATION_INORDERCONFIRMATION));
                result.operations.add(new Operation("同意",
                        Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_WAITING_FOR_PAYMENT));
            }
            result.isCanChangeFee = true;
            setExeSmallStatus(params, result);
            setCommitStatusModel(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_ORDERS_BE_CANCELLED:
            // 对方已取消订单
            result.payeeStatus = "对方已取消订单";
            setCancelSmallStatus(params, result);
            setCancelWhenCommitStatusModel(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_ORDERCANCELLATION_INORDERCONFIRMATION:
            // 取消订单(货源方确认订单过程中)
            result.payeeStatus = "订单已取消";
            setCancelSmallStatus(params, result);
            setCancelWhenCommitStatusModel(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_WAITING_FOR_PAYMENT:
            // 等待对方付款(货源方确认订单后)：取消订单
            result.payeeStatus = "等待对方付款";
            if (params.needOperation) {
                result.operations
                        .add(new Operation(
                                "取消订单",
                                Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_ORDERCANCELLATION_INORDERCONFIRMATION_WAITING_FOR_PAYMENT));
            }
            setExeSmallStatus(params, result);
            setAcceptStatusModel(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_ORDERCANCELLATION_INORDERCONFIRMATION_WAITING_FOR_PAYMENT:
            // 取消订单(等待对方付款)
            result.payeeStatus = "您已取消订单";
            setCancelSmallStatus(params, result);
            setCancelWhenAcceptStatusModel(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_ORDERS_BE_CANCELLED_WAITING_FOR_PAYMENT:
            // 对方已取消订单(等待对方付款)
            result.payeeStatus = "对方取消订单";
            setCancelSmallStatus(params, result);
            setCancelWhenAcceptStatusModel(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_UNNEET_PAYMENT:
            // 无需付款，等待对方拉货
            result.payeeStatus = "等待对方拉货";
            if (params.needOperation) {
                result.operations.add(new Operation("没来拉货，投诉",
                        Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_APPEAL_OF_NOT_COME_PULL_GOODS_OF_THE_PARTY));
                result.operations.add(new Operation("申请取消",
                        Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_APPLY_ORDERCANCELLATION));
                result.operations.add(new Operation("催促结单",
                        Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_DUNNING_THE_PARTY));
            }
            setExeSmallStatus(params, result);
            setExeStatusModel(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_PLATFORM_RECEIVED_PAYMENT:
            // 对方已付款(平台已收到付款)：申请取消、催促对方确认、没来拉货，投诉
            result.payeeStatus = "对方已付款，等待对方拉货";
            if (params.needOperation) {
                result.operations.add(new Operation("没来拉货，投诉",
                        Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_APPEAL_OF_NOT_COME_PULL_GOODS_OF_THE_PARTY));
                result.operations.add(new Operation("申请取消",
                        Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_APPLY_ORDERCANCELLATION));
                result.operations.add(new Operation("催促结单",
                        Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_DUNNING_THE_PARTY));
            }
            setExeSmallStatus(params, result);
            setExeStatusModel(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_CONFIRM_PULL_GOODS:
            // 对方确认拉货
            result.payeeStatus = "对方确认拉货";
            if (infoFee.getPayerGuaranteeAmount() > 0) {
                if (params.needOperation) {
                    result.operations
                            .add(new Operation(
                                    "没来拉货，投诉",
                                    Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_APPEAL_OF_NOT_COME_PULL_GOODS_FOR_CONFIRM_PULL_GOODS));
                    result.operations.add(new Operation("确认",
                            Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_COMPLETE_FOR_CONFIRM_PULL_GOODS));
                }
                setExeSmallStatus(params, result);
                setExeStatusModel(params, result, text1, text2);
            } else {
                setCompleteSmallStatus(params, result);
                setCompleteStatusModels(params, result, text1, text2);
            }
            break;
        case Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_COMPLETE_FOR_CONFIRM_PULL_GOODS:
            // 同意对方确认拉货
            result.payeeStatus = "订单完成";
            setCompleteSmallStatus(params, result);
            setCompleteStatusModels(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_APPEAL_OF_NOT_COME_PULL_GOODS_FOR_CONFIRM_PULL_GOODS:
            // 对方确认拉货，没来拉货，已投诉
            if (infoFee.getStatus() == Properties.INFO_FEE_STATUS_COMPLETE) {
                result.payeeStatus = "投诉已处理";
                result.hasComplainResult = true;
                setCompleteSmallStatus(params, result);
                setCompleteStatusModels(params, result, text1, text2);
            } else {
                result.payeeStatus = "没来拉货，已投诉";
                setComplainSmallStatus(params, result);
                setExeStatusModel(params, result, text1, text2);
            }
            break;
        case Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_APPLY_ORDERCANCELLATION_OF_THE_PARTY:
            // 对方申请取消：投诉、同意请求
            result.payeeStatus = "对方申请取消";
            if (params.needOperation) {
                result.operations
                        .add(new Operation(
                                "投诉",
                                Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_APPEALING_BY_REFUSE_ORDERCANCELLATION_WITHY_THE_PARTY));
                result.operations.add(new Operation("同意请求",
                        Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_AGREE_ORDERCANCELLATION_WITHY_THE_PARTY));
            }
            setExeSmallStatus(params, result);
            setExeStatusModel(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_APPEALING_BY_REFUSE_ORDERCANCELLATION_WITHY_THE_PARTY:
            // 拒绝对方取消订单,投诉中
            if (infoFee.getStatus() == Properties.INFO_FEE_STATUS_COMPLETE) {
                result.payeeStatus = "投诉已处理";
                result.hasComplainResult = true;
                setCompleteSmallStatus(params, result);
                setCompleteStatusModels(params, result, text1, text2);
            } else {
                result.payeeStatus = "拒绝对方取消订单,已投诉";
                setComplainSmallStatus(params, result);
                setExeStatusModel(params, result, text1, text2);
            }
            break;
        case Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_AGREE_ORDERCANCELLATION_WITHY_THE_PARTY:
            // 同意对方取消订单
            result.payeeStatus = "同意对方取消订单";
            setCancelSmallStatus(params, result);
            setCancelWhenExeStatusModels(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_APPEAL_BY_NO_GOODS_OF_THE_PARTY:
            // 对方没有拉到货，已投诉：拒绝赔付、同意赔付
            if (infoFee.getStatus() == Properties.INFO_FEE_STATUS_COMPLETE) {
                result.payeeStatus = "投诉已处理";
                setCompleteSmallStatus(params, result);
                setCompleteStatusModels(params, result, text1, text2);
                result.hasComplainResult = true;
            } else {
                result.payeeStatus = "对方没有拉到货，已投诉";
                if (params.needOperation) {
                    if (infoFee.getPayeeGuaranteeAmount() > 0) {
                        result.operations.add(new Operation("拒绝赔付",
                                Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_REFUSE_COMPENSATION_OF_NO_GOODS));
                        result.operations.add(new Operation("同意赔付",
                                Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_AGREE_COMPENSATION_OF_NO_GOODS));
                    } else if (infoFee.getInfoAmount() > 0) {
                        result.operations.add(new Operation("拒绝退款",
                                Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_REFUSE_COMPENSATION_OF_NO_GOODS));
                        result.operations.add(new Operation("同意退款",
                                Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_AGREE_COMPENSATION_OF_NO_GOODS));
                    }
                }
                setComplainSmallStatus(params, result);
                setExeStatusModel(params, result, text1, text2);
            }
            break;
        case Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_REFUSE_COMPENSATION_OF_NO_GOODS:
            // 拒绝赔付和退款(对方没有拉到货，已投诉)
            if (infoFee.getStatus() == Properties.INFO_FEE_STATUS_COMPLETE) {
                result.payeeStatus = "投诉已处理";
                result.hasComplainResult = true;
                setCompleteSmallStatus(params, result);
                setCompleteStatusModels(params, result, text1, text2);
            } else {
                result.payeeStatus = "对方没拉到货，您拒绝赔付";
                setComplainSmallStatus(params, result);
                setExeStatusModel(params, result, text1, text2);
            }
            break;
        case Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_AGREE_COMPENSATION_OF_NO_GOODS:
            // 同意赔付和退款(对方没有拉到货，已投诉)
            result.payeeStatus = "对方没拉到货，您已同意赔付";
            setCompleteSmallStatus(params, result);
            setCompleteStatusModels(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_AGREE_COMPENSATION_OF_APPLY_ORDERCANCELLATION_OF_THE_PARTY:
            // 对方已赔付(对方申请取消,拒绝后)
            result.payeeStatus = "对方已赔付";
            setCompleteSmallStatus(params, result);
            setCompleteStatusModels(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_REFUSE_COMPENSATION_OF_APPLY_ORDERCANCELLATION_OF_THE_PARTY:
            // 对方拒绝赔付(对方申请取消,拒绝后)
            if (infoFee.getStatus() == Properties.INFO_FEE_STATUS_COMPLETE) {
                result.payeeStatus = "投诉已处理";
                result.hasComplainResult = true;
                setCompleteSmallStatus(params, result);
                setCompleteStatusModels(params, result, text1, text2);
            } else {
                result.payeeStatus = "对方拒绝赔付";
                setComplainSmallStatus(params, result);
                setExeStatusModel(params, result, text1, text2);
            }
            break;
        case Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_DUNNING_THE_PARTY:
            // 催促对方中
            result.payeeStatus = "催促对方中";
            setExeSmallStatus(params, result);
            setExeStatusModel(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_APPEAL_REFUSE_DUNNING_OF_THE_PARTY:
            // 对方拒绝催促后，投诉
            if (infoFee.getStatus() == Properties.INFO_FEE_STATUS_COMPLETE) {
                result.payeeStatus = "投诉已处理";
                result.hasComplainResult = true;
                setCompleteSmallStatus(params, result);
                setCompleteStatusModels(params, result, text1, text2);
            } else {
                result.payeeStatus = "催促拒绝，对方已投诉";
                setComplainSmallStatus(params, result);
                setExeStatusModel(params, result, text1, text2);
            }
            break;
        case Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_APPLY_ORDERCANCELLATION:
            // 申请取消中
            result.payeeStatus = "申请取消中";
            setExeSmallStatus(params, result);
            setExeStatusModel(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_AGREE_ORDERCANCELLATION_OF_THE_PARTY:
            // 对方同意取消订单
            result.payeeStatus = "对方已同意取消订单";
            setCancelSmallStatus(params, result);
            setCancelWhenExeStatusModels(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_APPEAL_BY_REFUSE_ORDERCANCELLATION_OF_THE_PARTY:
            // 对方拒绝取消订单,已投诉：拒绝赔付、同意赔付
            result.payeeStatus = "对方拒绝取消订单,已投诉";
            if (params.needOperation) {
                if (infoFee.getPayeeGuaranteeAmount() > 0) {
                    result.operations.add(new Operation("拒绝赔付",
                            Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_REFUSE_COMPENSATION_OF_APPLY_ORDERCANCELLATION));
                    result.operations.add(new Operation("同意赔付",
                            Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_AGREE_COMPENSATION_OF_APPLY_ORDERCANCELLATION));
                } else if (infoFee.getInfoAmount() > 0) {
                    result.operations.add(new Operation("拒绝退款",
                            Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_REFUSE_COMPENSATION_OF_APPLY_ORDERCANCELLATION));
                    result.operations.add(new Operation("同意退款",
                            Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_AGREE_COMPENSATION_OF_APPLY_ORDERCANCELLATION));
                }
            }
            setComplainSmallStatus(params, result);
            setExeStatusModel(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_REFUSE_COMPENSATION_OF_APPLY_ORDERCANCELLATION:
            // 拒绝赔付
            if (infoFee.getStatus() == Properties.INFO_FEE_STATUS_COMPLETE) {
                result.payeeStatus = "投诉已处理";
                result.hasComplainResult = true;
                setCompleteSmallStatus(params, result);
                setCompleteStatusModels(params, result, text1, text2);
            } else {
                result.payeeStatus = "拒绝赔付，等待平台处理";
                setComplainSmallStatus(params, result);
                setExeStatusModel(params, result, text1, text2);
            }
            break;
        case Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_AGREE_COMPENSATION_OF_APPLY_ORDERCANCELLATION:
            // 同意赔付
            result.payeeStatus = "已同意赔付";
            setCompleteSmallStatus(params, result);
            setCompleteStatusModels(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_APPEAL_OF_NOT_COME_PULL_GOODS_OF_THE_PARTY:
            // 对方没来拉货，投诉
            if (infoFee.getStatus() == Properties.INFO_FEE_STATUS_COMPLETE) {
                result.payeeStatus = "投诉已处理";
                result.hasComplainResult = true;
                setCompleteSmallStatus(params, result);
                setCompleteStatusModels(params, result, text1, text2);
            } else {
                result.payeeStatus = "对方没来拉货，已投诉";
                setComplainSmallStatus(params, result);
                setExeStatusModel(params, result, text1, text2);
            }
            break;
        case Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_AGREE_COMPENSATION_OF_NOT_COME_PULL_GOODS:
            // 对方已赔付(对方没来拉货，投诉)
            result.payeeStatus = "对方已赔付";
            setCompleteSmallStatus(params, result);
            setCompleteStatusModels(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_REFUSE_COMPENSATION_OF_NOT_COME_PULL_GOODS:
            // 对方拒绝赔(对方没来拉货，投诉)
            if (infoFee.getStatus() == Properties.INFO_FEE_STATUS_COMPLETE) {
                result.payeeStatus = "投诉已处理";
                result.hasComplainResult = true;
                setCompleteSmallStatus(params, result);
                setCompleteStatusModels(params, result, text1, text2);
            } else {
                result.payeeStatus = "对方拒绝赔付";
                setComplainSmallStatus(params, result);
                setExeStatusModel(params, result, text1, text2);
            }
            break;
        case Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_COMPLETE_FOR_DUNNING_THE_PARTY:
            // 已经确认拉货(对方催促)
            result.payeeStatus = "对方已确认拉货";
            setCompleteSmallStatus(params, result);
            setCompleteStatusModels(params, result, text1, text2);
            break;
        default:
            setResultDefault(params, result, false);
            break;
        }
    }

    private static void setResultPayeeOrder(InfoFeeStatusParams params, InfoFeeStatusResult result) {
        InfoFee infoFee = params.infoFee;
        String text1 = "订单已提交";
        String text2 = "对方已接单";
        LogUtils.d("InfoFeeStatusUtils", "setResultPayeeOrder:" + infoFee.getPayeeFlowStatus());
        switch (infoFee.getPayeeFlowStatus()) {
        case Properties.INFO_FEE_PAYEE_ORDER_STATUS_WAITING_FOR_CONFIRM:
            // 等待对方确认：取消
            result.payeeStatus = "等待对方确认";
            if (params.needOperation) {
                result.operations.add(new Operation("电话催单", OPERATION_CALL));
                result.operations.add(new Operation("取消订单",
                        Properties.INFO_FEE_PAYEE_ORDER_STATUS_ORDERCANCELLATION_INORDERCONFIRMATION));
            }
            setExeSmallStatus(params, result);
            setCommitStatusModel(params, result, text1, text2);
            result.isCanChangeFee = true;
            break;
        case Properties.INFO_FEE_PAYEE_ORDER_STATUS_ORDERCANCELLATION_INORDERCONFIRMATION:
            // 取消订单(车源方确认订单过程中)
            result.payeeStatus = "订单已取消";
            setCancelSmallStatus(params, result);
            setCancelWhenCommitStatusModel(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYEE_ORDER_STATUS_ORDERS_BE_CANCELLED:
            // 对方已取消订单
            result.payeeStatus = "对方已取消订单";
            setCancelSmallStatus(params, result);
            setCancelWhenCommitStatusModel(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYEE_ORDER_STATUS_WAITING_FOR_PAYMENT:
            // 等待对方付款(车源方确认订单后)
            result.payeeStatus = "等待对方付款";
            if (params.needOperation) {
                result.operations
                        .add(new Operation(
                                "取消订单",
                                Properties.INFO_FEE_PAYEE_ORDER_STATUS_ORDERCANCELLATION_INORDERCONFIRMATION_WAITING_FOR_PAYMENT));
            }
            setExeSmallStatus(params, result);
            setAcceptStatusModel(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYEE_ORDER_STATUS_ORDERCANCELLATION_INORDERCONFIRMATION_WAITING_FOR_PAYMENT:
            // 取消订单(等待对方付款)
            result.payeeStatus = "您已取消订单";
            setCancelSmallStatus(params, result);
            setCancelWhenAcceptStatusModel(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYEE_ORDER_STATUS_ORDERS_BE_CANCELLED_WAITING_FOR_PAYMENT:
            // 对方已取消订单(等待对方付款)
            result.payeeStatus = "对方已取消订单";
            setCancelSmallStatus(params, result);
            setCancelWhenAcceptStatusModel(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYEE_ORDER_STATUS_UNNEET_PAYMENT:
            // 无需付款，等待对方拉货
            result.payeeStatus = "等待对方拉货";
            if (params.needOperation) {
                result.operations.add(new Operation("没来拉货，投诉",
                        Properties.INFO_FEE_PAYEE_ORDER_STATUS_APPEAL_OF_NOT_COME_PULL_GOODS_OF_THE_PARTY));
                result.operations.add(new Operation("申请取消",
                        Properties.INFO_FEE_PAYEE_ORDER_STATUS_APPLY_ORDERCANCELLATION));
                result.operations.add(new Operation("催促结单", Properties.INFO_FEE_PAYEE_ORDER_STATUS_DUNNING_THE_PARTY));
            }
            setExeSmallStatus(params, result);
            setExeStatusModel(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYEE_ORDER_STATUS_PLATFORM_RECEIVED_PAYMENT:
            // 对方已付款(平台已收到付款)
            result.payeeStatus = "对方已付款，等待对方拉货";
            if (params.needOperation) {
                result.operations.add(new Operation("没来拉货，投诉",
                        Properties.INFO_FEE_PAYEE_ORDER_STATUS_APPEAL_OF_NOT_COME_PULL_GOODS_OF_THE_PARTY));
                result.operations.add(new Operation("申请取消",
                        Properties.INFO_FEE_PAYEE_ORDER_STATUS_APPLY_ORDERCANCELLATION));
                result.operations.add(new Operation("催促结单", Properties.INFO_FEE_PAYEE_ORDER_STATUS_DUNNING_THE_PARTY));
            }
            setExeSmallStatus(params, result);
            setExeStatusModel(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYEE_ORDER_STATUS_CONFIRM_PULL_GOODS:
            // 对方确认拉货
            if (infoFee.getPayerGuaranteeAmount() > 0) {
                result.payeeStatus = "若对方已拉货，请确认";
                if (params.needOperation) {
                    result.operations
                            .add(new Operation(
                                    "没来拉货，投诉",
                                    Properties.INFO_FEE_PAYEE_ORDER_STATUS_APPEAL_OF_NOT_COME_PULL_GOODS_FOR_CONFIRM_PULL_GOODS));
                    result.operations.add(new Operation("确认",
                            Properties.INFO_FEE_PAYEE_ORDER_STATUS_COMPLETE_FOR_CONFIRM_PULL_GOODS));
                }
                setExeSmallStatus(params, result);
                setExeStatusModel(params, result, text1, text2);
            } else {
                result.payeeStatus = "对方已确认拉货";
                setCompleteSmallStatus(params, result);
                setCompleteStatusModels(params, result, text1, text2);
            }
            break;
        case Properties.INFO_FEE_PAYEE_ORDER_STATUS_COMPLETE_FOR_CONFIRM_PULL_GOODS:
            // 订单完成（同意对方确认拉货）
            result.payeeStatus = "订单完成";
            setCompleteSmallStatus(params, result);
            setCompleteStatusModels(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYEE_ORDER_STATUS_APPEAL_OF_NOT_COME_PULL_GOODS_FOR_CONFIRM_PULL_GOODS:
            // 对方确认拉货，没来拉货，投诉
            if (infoFee.getStatus() == Properties.INFO_FEE_STATUS_COMPLETE) {
                result.payeeStatus = "投诉已处理";
                result.hasComplainResult = true;
                setCompleteSmallStatus(params, result);
                setCompleteStatusModels(params, result, text1, text2);
            } else {
                result.payeeStatus = "没来拉货，已投诉";
                setComplainSmallStatus(params, result);
                setExeStatusModel(params, result, text1, text2);
            }
            break;
        case Properties.INFO_FEE_PAYEE_ORDER_STATUS_APPLY_ORDERCANCELLATION_OF_THE_PARTY:
            // 对方请求取消订单：拒绝，投诉、同意
            result.payeeStatus = "对方请求取消订单";
            if (params.needOperation) {
                result.operations.add(new Operation("拒绝请求，投诉",
                        Properties.INFO_FEE_PAYEE_ORDER_STATUS_APPEALING_BY_REFUSE_ORDERCANCELLATION_WITHY_THE_PARTY));
                result.operations.add(new Operation("同意",
                        Properties.INFO_FEE_PAYEE_ORDER_STATUS_AGREE_ORDERCANCELLATION_WITHY_THE_PARTY));
            }
            setExeSmallStatus(params, result);
            setExeStatusModel(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYEE_ORDER_STATUS_APPEALING_BY_REFUSE_ORDERCANCELLATION_WITHY_THE_PARTY:
            // 拒绝对方取消订单,投诉中
            if (infoFee.getStatus() == Properties.INFO_FEE_STATUS_COMPLETE) {
                result.payeeStatus = "投诉已处理";
                result.hasComplainResult = true;
                setCompleteSmallStatus(params, result);
                setCompleteStatusModels(params, result, text1, text2);
            } else {
                result.payeeStatus = "拒绝对方取消订单,已投诉";
                setComplainSmallStatus(params, result);
                setExeStatusModel(params, result, text1, text2);
            }
            break;
        case Properties.INFO_FEE_PAYEE_ORDER_STATUS_AGREE_ORDERCANCELLATION_WITHY_THE_PARTY:
            // 同意对方取消订单
            result.payeeStatus = "同意对方取消订单";
            setCompleteSmallStatus(params, result);
            setCompleteStatusModels(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYEE_ORDER_STATUS_AGREE_COMPENSATION_OF_APPLY_ORDERCANCELLATION_OF_THE_PARTY:
            // 对方已赔付(对方申请取消,拒绝后)
            result.payeeStatus = "对方已赔付";
            setCompleteSmallStatus(params, result);
            setCompleteStatusModels(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYEE_ORDER_STATUS_REFUSE_COMPENSATION_OF_APPLY_ORDERCANCELLATION_OF_THE_PARTY:
            // 对方拒绝赔付(对方申请取消,拒绝后)
            if (infoFee.getStatus() == Properties.INFO_FEE_STATUS_COMPLETE) {
                result.payeeStatus = "投诉已处理";
                result.hasComplainResult = true;
                setCompleteSmallStatus(params, result);
                setCompleteStatusModels(params, result, text1, text2);
            } else {
                result.payeeStatus = "对方拒绝赔付";
                setComplainSmallStatus(params, result);
                setExeStatusModel(params, result, text1, text2);
            }
            break;
        case Properties.INFO_FEE_PAYEE_ORDER_STATUS_APPEAL_BY_NO_GOODS_OF_THE_PARTY:
            // 对方没有拉到货，已投诉：拒绝赔付、同意赔付
            result.payeeStatus = "对方没有拉到货，已投诉";
            if (params.needOperation) {
                if (infoFee.getPayeeGuaranteeAmount() > 0) {
                    result.operations.add(new Operation("拒绝赔付",
                            Properties.INFO_FEE_PAYEE_ORDER_STATUS_REFUSE_COMPENSATION_OF_NO_GOODS));
                    result.operations.add(new Operation("同意赔付",
                            Properties.INFO_FEE_PAYEE_ORDER_STATUS_AGREE_COMPENSATION_OF_NO_GOODS));
                } else if (infoFee.getInfoAmount() > 0) {
                    result.operations.add(new Operation("拒绝退款",
                            Properties.INFO_FEE_PAYEE_ORDER_STATUS_REFUSE_COMPENSATION_OF_NO_GOODS));
                    result.operations.add(new Operation("同意退款",
                            Properties.INFO_FEE_PAYEE_ORDER_STATUS_AGREE_COMPENSATION_OF_NO_GOODS));
                }
            }
            setComplainSmallStatus(params, result);
            setExeStatusModel(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYEE_ORDER_STATUS_REFUSE_COMPENSATION_OF_NO_GOODS:
            // 拒绝赔付和退款(对方没有拉到货，已投诉)
            if (infoFee.getStatus() == Properties.INFO_FEE_STATUS_COMPLETE) {
                result.payeeStatus = "投诉已处理";
                result.hasComplainResult = true;
                setCompleteSmallStatus(params, result);
                setCompleteStatusModels(params, result, text1, text2);
            } else {
                result.payeeStatus = "等待平台处理";
                setComplainSmallStatus(params, result);
                setExeStatusModel(params, result, text1, text2);
            }
            break;
        case Properties.INFO_FEE_PAYEE_ORDER_STATUS_AGREE_COMPENSATION_OF_NO_GOODS:
            // 同意赔付和退款(对方没有拉到货，已投诉)
            result.payeeStatus = "同意赔付";
            setCompleteSmallStatus(params, result);
            setCompleteStatusModels(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYEE_ORDER_STATUS_DUNNING_THE_PARTY:
            // 催促对方中
            result.payeeStatus = "催促对方中";
            setExeSmallStatus(params, result);
            setExeStatusModel(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYEE_ORDER_STATUS_APPEAL_REFUSE_DUNNING_OF_THE_PARTY:
            // 对方拒绝催促后，投诉
            if (infoFee.getStatus() == Properties.INFO_FEE_STATUS_COMPLETE) {
                result.payeeStatus = "投诉已处理";
                result.hasComplainResult = true;
                setCompleteSmallStatus(params, result);
                setCompleteStatusModels(params, result, text1, text2);
            } else {
                result.payeeStatus = "对方拒绝催促，已投诉";
                setComplainSmallStatus(params, result);
                setExeStatusModel(params, result, text1, text2);
            }
            break;
        case Properties.INFO_FEE_PAYEE_ORDER_STATUS_APPLY_ORDERCANCELLATION:
            // 申请取消中
            result.payeeStatus = "申请取消中";
            setExeSmallStatus(params, result);
            setExeStatusModel(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYEE_ORDER_STATUS_AGREE_ORDERCANCELLATION_OF_THE_PARTY:
            // 对方同意取消订单
            result.payeeStatus = "对方已同意取消订单";
            setCancelSmallStatus(params, result);
            setCancelWhenExeStatusModels(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYEE_ORDER_STATUS_APPEAL_BY_REFUSE_ORDERCANCELLATION_OF_THE_PARTY:
            // 对方拒绝取消订单,已投诉：拒绝赔付、同意赔付
            result.payeeStatus = "对方拒绝取消订单,已投诉";
            if (params.needOperation) {
                if (infoFee.getPayeeGuaranteeAmount() > 0) {
                    result.operations.add(new Operation("拒绝赔付",
                            Properties.INFO_FEE_PAYEE_ORDER_STATUS_REFUSE_COMPENSATION_OF_APPLY_ORDERCANCELLATION));
                    result.operations.add(new Operation("同意赔付",
                            Properties.INFO_FEE_PAYEE_ORDER_STATUS_AGREE_COMPENSATION_OF_APPLY_ORDERCANCELLATION));
                } else if (infoFee.getInfoAmount() > 0) {
                    result.operations.add(new Operation("拒绝退款",
                            Properties.INFO_FEE_PAYEE_ORDER_STATUS_REFUSE_COMPENSATION_OF_APPLY_ORDERCANCELLATION));
                    result.operations.add(new Operation("同意退款",
                            Properties.INFO_FEE_PAYEE_ORDER_STATUS_AGREE_COMPENSATION_OF_APPLY_ORDERCANCELLATION));
                }
            }
            setComplainSmallStatus(params, result);
            setExeStatusModel(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYEE_ORDER_STATUS_REFUSE_COMPENSATION_OF_APPLY_ORDERCANCELLATION:
            // 拒绝赔付(申请取消,对方拒绝后)
            if (infoFee.getStatus() == Properties.INFO_FEE_STATUS_COMPLETE) {
                result.payeeStatus = "投诉已处理";
                result.hasComplainResult = true;
                setCompleteSmallStatus(params, result);
                setCompleteStatusModels(params, result, text1, text2);
            } else {
                result.payeeStatus = "拒绝赔付，等待平台处理";
                setComplainSmallStatus(params, result);
                setExeStatusModel(params, result, text1, text2);
            }
            break;
        case Properties.INFO_FEE_PAYEE_ORDER_STATUS_AGREE_COMPENSATION_OF_APPLY_ORDERCANCELLATION:
            // 同意赔付(申请取消,对方拒绝后)
            result.payeeStatus = "已同意赔付";
            setCompleteSmallStatus(params, result);
            setCompleteStatusModels(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYEE_ORDER_STATUS_APPEAL_OF_NOT_COME_PULL_GOODS_OF_THE_PARTY:
            // 对方没来拉货，投诉
            if (infoFee.getStatus() == Properties.INFO_FEE_STATUS_COMPLETE) {
                result.payeeStatus = "投诉已处理";
                result.hasComplainResult = true;
                setCompleteSmallStatus(params, result);
                setCompleteStatusModels(params, result, text1, text2);
            } else {
                result.payeeStatus = "对方没来拉货，投诉中";
                setComplainSmallStatus(params, result);
                setExeStatusModel(params, result, text1, text2);
            }
            break;
        case Properties.INFO_FEE_PAYEE_ORDER_STATUS_AGREE_COMPENSATION_OF_NOT_COME_PULL_GOODS:
            // 对方已赔付(对方没来拉货，投诉)
            result.payeeStatus = "对方已赔付";
            setCompleteSmallStatus(params, result);
            setCompleteStatusModels(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYEE_ORDER_STATUS_REFUSE_COMPENSATION_OF_NOT_COME_PULL_GOODS:
            // 对方拒绝赔(对方没来拉货，投诉)
            if (infoFee.getStatus() == Properties.INFO_FEE_STATUS_COMPLETE) {
                result.payeeStatus = "投诉已处理";
                result.hasComplainResult = true;
                setCompleteSmallStatus(params, result);
                setCompleteStatusModels(params, result, text1, text2);
            } else {
                result.payeeStatus = "对方拒绝赔付，等待平台处理";
                setComplainSmallStatus(params, result);
                setExeStatusModel(params, result, text1, text2);
            }
            break;
        case Properties.INFO_FEE_PAYEE_ORDER_STATUS_APPEAL_COMPLETE:
            // 投诉处理完成
            result.payeeStatus = "投诉处理完成";
            setCompleteSmallStatus(params, result);
            setCompleteStatusModels(params, result, text1, text2);
            break;
        case Properties.INFO_FEE_PAYEE_ORDER_STATUS_COMPLETE_FOR_DUNNING_THE_PARTY:
            // 已经确认拉货(催促对方)
            result.payeeStatus = "对方已确认拉货";
            setCompleteSmallStatus(params, result);
            setCompleteStatusModels(params, result, text1, text2);
            break;
        default:
            setResultDefault(params, result, false);
            break;
        }
    }

    static void setResultDefault(InfoFeeStatusParams params, InfoFeeStatusResult result, boolean bPayer) {
        InfoFee infoFee = params.infoFee;
        String s = "";
        switch (infoFee.getStatus()) {
        case Properties.INFO_FEE_STATUS_EXECUTE:
            s = "执行中";
            break;
        case Properties.INFO_FEE_STATUS_CANCEL:
            s = "已取消";
            break;
        case Properties.INFO_FEE_STATUS_COMPLETE:
            s = "已完成";
            break;
        default:
            break;
        }
        if (bPayer) {
            result.payerStatus = s;
        } else {
            result.payeeStatus = s;
        }
    }

    // 小状态-执行中
    static void setExeSmallStatus(InfoFeeStatusParams params, InfoFeeStatusResult result) {
        if (params.needSmallStatus) {
            result.smallStatusId = R.drawable.icon_infofee_status_exeing;
        }
    }

    // 小状态-已投诉
    static void setComplainSmallStatus(InfoFeeStatusParams params, InfoFeeStatusResult result) {
        if (params.needSmallStatus) {
            result.smallStatusId = R.drawable.icon_infofee_status_complain;
        }
    }

    // 小状态-已取消
    static void setCancelSmallStatus(InfoFeeStatusParams params, InfoFeeStatusResult result) {
        if (params.needSmallStatus) {
            result.smallStatusId = R.drawable.icon_infofee_status_cancel;
        }
    }

    // 小状态-已完成
    static void setCompleteSmallStatus(InfoFeeStatusParams params, InfoFeeStatusResult result) {
        if (params.needSmallStatus) {
            result.smallStatusId = R.drawable.icon_infofee_status_complete;
        }
    }

    // 状态流程图-订单提交
    static void setCommitStatusModel(InfoFeeStatusParams params, InfoFeeStatusResult result, String text1, String text2) {
        if (params.needStatusModel)
            setStatusModels(result.statusModels, text1, text2, true, false, false, false, false);
    }

    // 状态流程图-已接单
    static void setAcceptStatusModel(InfoFeeStatusParams params, InfoFeeStatusResult result, String text1, String text2) {
        if (params.needStatusModel)
            setStatusModels(result.statusModels, text1, text2, true, true, false, false, false);
    }

    // 状态流程图-订单执行中
    static void setExeStatusModel(InfoFeeStatusParams params, InfoFeeStatusResult result, String text1, String text2) {
        if (params.needStatusModel)
            setStatusModels(result.statusModels, text1, text2, true, true, true, false, false);
    }

    // 状态流程图-订单提交后取消
    static void setCancelWhenCommitStatusModel(InfoFeeStatusParams params, InfoFeeStatusResult result, String text1,
            String text2) {
        if (params.needStatusModel) {
            setStatusModels(result.statusModels, text1, text2, true, false, false, true, true);
        }
    }

    // 状态流程图-接单后取消
    static void setCancelWhenAcceptStatusModel(InfoFeeStatusParams params, InfoFeeStatusResult result, String text1,
            String text2) {
        if (params.needStatusModel)
            setStatusModels(result.statusModels, text1, text2, true, true, false, true, true);
    }

    // 状态流程图-订单执行中取消
    static void setCancelWhenExeStatusModels(InfoFeeStatusParams params, InfoFeeStatusResult result, String text1,
            String text2) {
        if (params.needStatusModel)
            setStatusModels(result.statusModels, text1, text2, true, true, true, true, true);
    }

    // 状态流程图-订单完成
    static void setCompleteStatusModels(InfoFeeStatusParams params, InfoFeeStatusResult result, String text1,
            String text2) {
        if (params.needStatusModel)
            setStatusModels(result.statusModels, text1, text2, true, true, true, true, false);
    }

    private static void setStatusModels(List<StatusModel> list, String text1, String text2, final boolean b1,
            final boolean b2, final boolean b3, final boolean b4, boolean bCanceled) {
        list.add(new StatusModel(R.drawable.icon_infofee_commit_true, R.drawable.icon_infofee_commit_false, text1) {
            @Override
            protected boolean getIsOn() {
                return b1;
            }
        });
        list.add(new StatusModel(R.drawable.icon_infofee_accept_true, R.drawable.icon_infofee_accept_false, text2) {
            @Override
            protected boolean getIsOn() {
                return b2;
            }
        });
        list.add(new StatusModel(R.drawable.icon_infofee_exeing_true, R.drawable.icon_infofee_exeing_false, "执行中") {
            @Override
            protected boolean getIsOn() {
                return b3;
            }
        });
        int onId, offId;
        String text;
        if (bCanceled) {
            onId = R.drawable.icon_infofee_cancel_true;
            offId = R.drawable.icon_infofee_cancel_false;
            text = "已取消";
        } else {
            onId = R.drawable.icon_infofee_complete_true;
            offId = R.drawable.icon_infofee_complete_false;
            text = "已完成";
        }
        list.add(new StatusModel(onId, offId, text) {
            @Override
            protected boolean getIsOn() {
                return b4;
            }
        });
    }

    public static class InfoFeeStatusParams {
        String curUserId;
        InfoFee infoFee;
        boolean needSmallStatus;
        boolean needOperation;
        boolean needStatusModel;

        public InfoFeeStatusParams(String curUserId, InfoFee infoFee) {
            this.curUserId = curUserId;
            this.infoFee = infoFee;
        }

        public InfoFeeStatusParams needSmallStatus() {
            needSmallStatus = true;
            return this;
        }

        public InfoFeeStatusParams needOperation() {
            needOperation = true;
            return this;
        }

        public InfoFeeStatusParams needStatusModel() {
            needStatusModel = true;
            return this;
        }
    }

    public static class InfoFeeStatusResult {
        public String source;
        public String sourceName;
        public String payerStatus;
        public String payeeStatus;
        public boolean isPayer;
        public int payerStatusColor = Color.GRAY;
        public int payeeStatusColor = Color.GRAY;
        public int smallStatusId;
        public boolean isCanChangeFee;
        public List<Operation> operations;
        public List<StatusModel> statusModels;
        public boolean hasComplainResult;

        public String getStatus() {
            if (isPayer) {
                return payerStatus;
            }
            return payeeStatus;
        }
    }

    public static class Operation {
        String name;
        int action;

        public Operation(String name, int action) {
            super();
            this.name = name;
            this.action = action;
        }

        public String getName() {
            return name;
        }

        public int getAction() {
            return action;
        }

    }
}
