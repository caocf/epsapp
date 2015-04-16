package com.epeisong.data.net.parser;

import java.util.ArrayList;
import java.util.List;

import com.epeisong.logistics.proto.ComplainTask.ComplainTaskResp;
import com.epeisong.logistics.proto.ComplainTask.ProtoCustComplainTask;
import com.epeisong.logistics.proto.Transaction.ProtoComplainTask;
import com.epeisong.logistics.proto.Transaction.ProtoInfoFee;
import com.epeisong.model.ComplainTask;
import com.epeisong.model.GuaComplainTask;
import com.epeisong.model.InfoFee;

/**
 * 担保投诉解析
 * @author Jack
 *
 */
public class ComplainTaskParser {

    public static ComplainTask parse(ProtoComplainTask pTask) {
        ComplainTask complainTask = new ComplainTask();// .getComplainTask();
        complainTask.setId(pTask.getId());
        complainTask.setDealBillType(pTask.getDealBillType());
        complainTask.setDealBillId(pTask.getDealBillId());
        complainTask.setInfoFeeResultId(pTask.getInfoFeeResultId());
        complainTask.setInfoFeeResultType(pTask.getInfoFeeResultType());
        complainTask.setPayeeGuaranteeAmountResultId(pTask.getPayeeGuaranteeAmountResultId());
        complainTask.setPayeeGuaranteeAmountResultType(pTask.getPayeeGuaranteeAmountResultType());
        complainTask.setPayerGuaranteeAmountResultId(pTask.getPayerGuaranteeAmountResultId());
        complainTask.setPayerGuaranteeAmountResultType(pTask.getPayerGuaranteeAmountResultType());
        complainTask.setNote(pTask.getNote());
        complainTask.setPayerGuaranteeId(pTask.getPayerGuaranteeId());
        complainTask.setPayerGuaranteeName(pTask.getPayerGuaranteeName());
        complainTask.setPayeeGuaranteeId(pTask.getPayeeGuaranteeId());
        complainTask.setPayeeGuaranteeName(pTask.getPayeeGuaranteeName());
        complainTask.setTradingPlatformId(pTask.getTradingPlatformId());
        complainTask.setTradingPlatformName(pTask.getTradingPlatformName());
        complainTask.setPayerId(pTask.getPayerId());
        complainTask.setPayerName(pTask.getPayerName());
        complainTask.setPayeeId(pTask.getPayeeId());
        complainTask.setPayeeName(pTask.getPayeeName());
        complainTask.setStatus(pTask.getStatus());
        complainTask.setCreateDate(pTask.getCreateDate());
        complainTask.setUpdateDate(pTask.getUpdateDate());
        complainTask.setSyncIndex(pTask.getSyncIndex());
        return complainTask;
    }

    public static GuaComplainTask parser(ProtoCustComplainTask item) {
        GuaComplainTask guaComplainTask = new GuaComplainTask();
        
        ProtoComplainTask eComplainTask = item.getComplainTask();
        guaComplainTask.setComplainTask(parse(eComplainTask));

        ProtoInfoFee eInfoFee = item.getInfoFee();
        InfoFee infoFee = InfoFeeParser.parser(eInfoFee);
        guaComplainTask.setInfoFee(infoFee);
        
        return guaComplainTask;
    }

    public static List<GuaComplainTask> parseList(ComplainTaskResp.Builder resp) {
        List<GuaComplainTask> result = new ArrayList<GuaComplainTask>();
        List<ProtoCustComplainTask> list = resp.getComplainTasksList();

        for (ProtoCustComplainTask item : list) {
            result.add(parser(item));
        }
        return result;
    }
}
