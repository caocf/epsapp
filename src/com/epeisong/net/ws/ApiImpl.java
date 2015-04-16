package com.epeisong.net.ws;

import android.text.TextUtils;

import com.epeisong.EpsNetConfig;
import com.epeisong.logistics.common.Properties;
import com.epeisong.net.ws.utils.BankCardResp;
import com.epeisong.net.ws.utils.BondWalletResp;
import com.epeisong.net.ws.utils.ComplainTaskResp;
import com.epeisong.net.ws.utils.Http;
import com.epeisong.net.ws.utils.Resp;
import com.epeisong.net.ws.utils.TaskResp;
import com.epeisong.net.ws.utils.WalletResp;
import com.epeisong.net.ws.utils.WithdrawTaskResp;
import com.epeisong.utils.LogUtils;
import com.epeisong.utils.java.JavaUtils;
import com.google.gson.Gson;

/**
 * WebService操作接口实现
 * 
 * @author poet
 * 
 */
public class ApiImpl implements Api {

	private <T> T getResult(String url, Class<T> clazz) throws Exception {
		LogUtils.d(this, url);
		String json = Http.request(url);
		Gson gson = new Gson();
		T t = gson.fromJson(json, clazz);
		return t;
	}

	@Override
	public Resp createAccountAndLogisticByPlatformManager(
			String mobileToCreate, int logisticType, String logisticTypeName,
			String name, int serveRegionCode, String serveRegionName,
			String mobileOfManager, String loginPwdOfManager,
			String payMentPwdOfManager, int operationType, int clientType,
			String passWord, String contactName, String contactTelephone,
			String contactMobile, String selfIntroduction, int regionCode,
			String regionName, String address, double currentLongitude,
			double currentLatitude, int routeCodeA, String routeNameA,
			int routeCodeB, String routeNameB, int goodsTypeCode,
			String goodsTypeName, int transportTypeCode,
			String transportTypeName) throws Exception {
		String url = EpsNetConfig.getLogisticsServeUrl()
				+ "UserAccountService/createAccountAndLogisticByPlatformManager/"
				+ JavaUtils.joinString("/", mobileToCreate, logisticType,
						logisticTypeName, name, serveRegionCode,
						serveRegionName, mobileOfManager, loginPwdOfManager,
						payMentPwdOfManager, operationType, clientType,
						passWord, contactName, contactTelephone, contactMobile,
						selfIntroduction, regionCode, regionName, address,
						currentLongitude, currentLatitude,

						routeCodeA, routeNameA, routeCodeB, routeNameB,
						goodsTypeCode, goodsTypeName, transportTypeCode,
						transportTypeName);
		Resp resp = getResult(url, Resp.class);
		return resp;
	}

	public Resp createAccount(String mobile, String password, String code)
			throws Exception {
		String url = EpsNetConfig.getLogisticsServeUrl()
				+ "UserAccountService/createAccount/" + mobile + "/" + password
				+ "/" + code;
		return getResult(url, Resp.class);
	}

	public Resp forgetPassword(String mobile, String verifyCode, String newPwd)
			throws Exception {
		String url = EpsNetConfig.getLogisticsServeUrl()
				+ "UserAccountService/forgetPassword/" + mobile + "/"
				+ verifyCode + "/" + newPwd;
		url += "/" + Properties.APP_CLIENT_BIG_TYPE_PHONE;
		return getResult(url, Resp.class);
	}

	public Resp updatePassword(String mobile, String oldPwd, String newPwd)
			throws Exception {
		String url = EpsNetConfig.getLogisticsServeUrl()
				+ "UserAccountService/updatePassword/" + mobile + "/" + oldPwd
				+ "/" + newPwd;
		url += "/" + Properties.APP_CLIENT_BIG_TYPE_PHONE;
		return getResult(url, Resp.class);
	}

	// Jack
	public WalletResp frozenWallet(String uname, String upwd, String realname,
			String identifyNo) throws Exception {
		String url = EpsNetConfig.getTransactionUrl()
				+ "WalletWS/frozenWallet/" + uname + "/" + upwd + "/"
				+ realname + "/" + identifyNo;
		return getResult(url, WalletResp.class);
	}

	// Jack
	public WalletResp thawWallet(String uname, String upwd, String realname,
			String identifyNo) throws Exception {
		String url = EpsNetConfig.getTransactionUrl() + "WalletWS/thawWallet/"
				+ uname + "/" + upwd + "/" + realname + "/" + identifyNo;
		return getResult(url, WalletResp.class);
	}

	public WalletResp changeMobile(String loginName, String loginPwd,
			String code, int codeType, String payPwd, String oldMobile,
			String newMobile) throws Exception {
		if (TextUtils.isEmpty(payPwd)) {
			payPwd = "-1";
		}
		String url = EpsNetConfig.getTransactionUrl() + "WalletWS/changMobile/"
				+ loginName + "/" + loginPwd + "/" + code + "/" + codeType
				+ "/" + payPwd + "/" + oldMobile + "/" + newMobile;
		return getResult(url, WalletResp.class);
	}

	@Override
	public WalletResp openWallet(String loginName, String loginPwd,
			String mobile, String code, int codeType, String realName,
			int idType, String idNum, String payPwd) throws Exception {
		String url = EpsNetConfig.getTransactionUrl()
				+ "WalletWS/openWallet/"
				+ JavaUtils.joinString("/", loginName, loginPwd, mobile, code,
						codeType, realName, idType, idNum, payPwd);
		WalletResp resp = getResult(url, WalletResp.class);
		if (resp != null && resp.getWallet() != null) {
			if (resp.getWallet().getAmount() == null) {
				resp.getWallet().setAmount(0L);
			}
		}
		return resp;
	}

	@Override
	public WalletResp getWallet(String loginName, String loginPwd)
			throws Exception {
		String url = EpsNetConfig.getTransactionUrl() + "WalletWS/getWallet/"
				+ JavaUtils.joinString("/", loginName, loginPwd);
		return getResult(url, WalletResp.class);
	}

	@Override
	public WalletResp listDetail(String uname, String upwd, int detailId,
			int type, int count, int year, int month, int date)
			throws Exception {
		String url = EpsNetConfig.getTransactionUrl()
				+ "WalletWS/listDetail/"
				+ JavaUtils.joinString("/", uname, upwd, detailId, type, count,
						year, month, date);
		return getResult(url, WalletResp.class);
	}

	@Override
	public WalletResp withdraw(String loginName, String loginPwd, long amount,
			String paymentPwd, int payeeType, String openBankName,
			String bankCode, String bankName, int bankRegionCode,
			String bankRegionName, String payeeName, String payeeAccount)
			throws Exception {
		String url = EpsNetConfig.getTransactionUrl()
				+ "WalletWS/withdraw/"
				+ JavaUtils.joinString("/", loginName, loginPwd, amount,
						paymentPwd, payeeType, openBankName, bankCode,
						bankName, bankRegionCode, bankRegionName, payeeName,
						payeeAccount);
		return getResult(url, WalletResp.class);
	}

	@Override
	public WalletResp withdrawToBankCard(String loginName, String loginPwd,
			long amount, String paymentPwd, int payeeType, String payerName,
			int payerLogisticsType, int bankCardId) throws Exception {
		String url = EpsNetConfig.getTransactionUrl()
				+ "WalletWS/withdrawToBankCard/"
				+ JavaUtils.joinString("/", loginName, loginPwd, amount,
						paymentPwd, payeeType, payerName, payerLogisticsType,
						bankCardId);
		return getResult(url, WalletResp.class);
	}

	@Override
	public WalletResp changePaymentPwd(String loginName, String loginPwd,
			String mobile, String code, int codeType, String newPaymentPwd,
			String oldPaymentPwd) throws Exception {
		String url = EpsNetConfig.getTransactionUrl()
				+ "WalletWS/modfiyPaymentPwd/"
				+ JavaUtils.joinString("/", loginName, loginPwd, mobile, code,
						codeType, newPaymentPwd, oldPaymentPwd);
		return getResult(url, WalletResp.class);
	}

	@Override
	public WalletResp forgetPaymentPwd(String loginName, String loginPwd,
			String mobile, String code, int codeType, String idNum,
			String paymentPwd) throws Exception {
		String url = EpsNetConfig.getTransactionUrl()
				+ "WalletWS/forgetPaymentPwd/"
				+ JavaUtils.joinString("/", loginName, loginPwd, mobile, code,
						codeType, idNum, paymentPwd);
		return getResult(url, WalletResp.class);
	}

	@Override
	public WithdrawTaskResp execWithdrawTask(String uname, String upwd,
			int taskId, int status, int subStatus, String serialNumber,
			String note) throws Exception {
		String url = EpsNetConfig.getTransactionUrl()
				+ "WalletWS/execWithdrawTask/"
				+ JavaUtils.joinString("/", uname, upwd, taskId, status,
						subStatus, serialNumber, note);
		return getResult(url, WithdrawTaskResp.class);
	}

	@Override
	public WithdrawTaskResp listWithdrawTask(String loginName, String loginPwd,
			int taskId, int status, int type, int count) throws Exception {
		String url = EpsNetConfig.getTransactionUrl()
				+ "WalletWS/listWithdrawTask/"
				+ JavaUtils.joinString("/", loginName, loginPwd, taskId,
						status, type, count);
		return getResult(url, WithdrawTaskResp.class);
	}

	@Override
	public WithdrawTaskResp updateSerialNumber(String loginName,
			String loginPwd, int taskId, String serialNumber) throws Exception {
		String url = EpsNetConfig.getTransactionUrl()
				+ "WalletWS/updateSerialNumber/"
				+ JavaUtils.joinString("/", loginName, loginPwd, taskId,
						serialNumber);
		return getResult(url, WithdrawTaskResp.class);
	}

	@Override
	public WithdrawTaskResp cancelWithdrawTask(String uname, String upwd,
			String taskId, String note, String paymentPwd) throws Exception {
		String url = EpsNetConfig.getTransactionUrl()
				+ "WalletWS/cancelWithdrawTask/"
				+ JavaUtils.joinString("/", uname, upwd, taskId, note,
						paymentPwd);
		return getResult(url, WithdrawTaskResp.class);
	}

	@Override
	public WithdrawTaskResp getWithdrawTask(String uname, String upwd,
			String taskId) throws Exception {
		String url = EpsNetConfig.getTransactionUrl()
				+ "WalletWS/getWithdrawTask/"
				+ JavaUtils.joinString("/", uname, upwd, taskId);
		return getResult(url, WithdrawTaskResp.class);
	}

	@Override
	public BankCardResp createBankCard(String uname, String upwd,
			String paymentPwd, String bankCode, String bankName,
			String cardNumber, String realName, String idType,
			String identityNumber, String openBankName, String regionCode,
			String regionName) throws Exception {
		String url = EpsNetConfig.getTransactionUrl()
				+ "BankCardWS/createBankCard/"
				+ JavaUtils.joinString("/", uname, upwd, paymentPwd, bankCode,
						bankName, cardNumber, realName, idType, identityNumber,
						openBankName, regionCode, regionName);
		return getResult(url, BankCardResp.class);
	}

	@Override
	public BankCardResp deleteBankCard(String uname, String upwd,
			String paymentPwd, String bankCardId) throws Exception {
		String url = EpsNetConfig.getTransactionUrl()
				+ "BankCardWS/deleteBankCard/"
				+ JavaUtils
						.joinString("/", uname, upwd, paymentPwd, bankCardId);
		return getResult(url, BankCardResp.class);
	}

	@Override
	public BankCardResp listBankCard(String uname, String upwd)
			throws Exception {
		String url = EpsNetConfig.getTransactionUrl()
				+ "BankCardWS/listBankCard/"
				+ JavaUtils.joinString("/", uname, upwd);
		return getResult(url, BankCardResp.class);
	}

	@Override
	public BondWalletResp getBondWallet(String loginName, String loginPwd)
			throws Exception {
		String url = EpsNetConfig.getTransactionUrl()
				+ "BondWalletWS/getBondWallet/"
				+ JavaUtils.joinString("/", loginName, loginPwd);
		return getResult(url, BondWalletResp.class);
	}

	@Override
	public BondWalletResp deposit2BondWalletFromWallet(String loginName,
			String loginPwd, long amount, String paymentPwd) throws Exception {
		String url = EpsNetConfig.getTransactionUrl()
				+ "BondWalletWS/depositByWallet/"
				+ JavaUtils.joinString("/", loginName, loginPwd, amount,
						paymentPwd);
		return getResult(url, BondWalletResp.class);
	}

	@Override
	public BondWalletResp withdraw2WalletFromBondWallet(String loginName,
			String loginPwd, long amount, String paymentPwd) throws Exception {
		String url = EpsNetConfig.getTransactionUrl()
				+ "BondWalletWS/withdrawToWallet/"
				+ JavaUtils.joinString("/", loginName, loginPwd, amount,
						paymentPwd);
		return getResult(url, BondWalletResp.class);
	}

	@Override
	public BondWalletResp frozenBondWallet(String loginName, String loginPwd,
			long amount, String paymentPwd) throws Exception {
		String url = EpsNetConfig.getTransactionUrl()
				+ "BondWalletWS/frozen/"
				+ JavaUtils.joinString("/", loginName, loginPwd, amount,
						paymentPwd);
		return getResult(url, BondWalletResp.class);
	}

	@Override
	public BondWalletResp thawBondWallet(String loginName, String loginPwd,
			long amount, String paymentPwd) throws Exception {
		String url = EpsNetConfig.getTransactionUrl()
				+ "BondWalletWS/thaw/"
				+ JavaUtils.joinString("/", loginName, loginPwd, amount,
						paymentPwd);
		return getResult(url, BondWalletResp.class);
	}

	@Override
	public BondWalletResp compensateByGuarantee(String loginName,
			String loginPwd, int payerWalletId, int payeeWalletId, long amount,
			int dealBillType, int dealBillId, String paymentPwd)
			throws Exception {
		String url = EpsNetConfig.getTransactionUrl()
				+ "BondWalletWS/compensateByGuarantee/"
				+ JavaUtils.joinString("/", loginName, loginPwd, payerWalletId,
						payeeWalletId, amount, dealBillType, dealBillId,
						paymentPwd);
		return getResult(url, BondWalletResp.class);
	}

	@Override
	public BondWalletResp compensateByUser(String loginName, String loginPwd,
			int payeeWalletId, long amount, int dealBillType, int dealBillId,
			String paymentPwd) throws Exception {
		String url = EpsNetConfig.getTransactionUrl()
				+ "BondWalletWS/compensateByUser/"
				+ JavaUtils.joinString("/", loginName, loginPwd, payeeWalletId,
						amount, dealBillType, dealBillId, paymentPwd);
		return getResult(url, BondWalletResp.class);
	}

	@Override
	public BondWalletResp BondlistDetail(String uname, String upwd,
			int detailId, int type, int count, int year, int month, int date)
			throws Exception {
		String url = EpsNetConfig.getTransactionUrl()
				+ "BondWalletWS/listDetail/"
				+ JavaUtils.joinString("/", uname, upwd, detailId, type, count,
						year, month, date);
		return getResult(url, BondWalletResp.class);
	}

	@Override
	public TaskResp startGuaranteeTask(String loginName, String loginPwd,
			String paymentPwd, int walletType) throws Exception {
		String url = EpsNetConfig.getTransactionUrl()
				+ "TaskWS/start/"
				+ JavaUtils.joinString("/", loginName, loginPwd, paymentPwd,
						walletType);
		return getResult(url, TaskResp.class);
	}

	@Override
	public TaskResp stopGuaranteeTask(String loginName, String loginPwd,
			String paymentPwd, int walletType) throws Exception {
		String url = EpsNetConfig.getTransactionUrl()
				+ "TaskWS/stop/"
				+ JavaUtils.joinString("/", loginName, loginPwd, paymentPwd,
						walletType);
		return getResult(url, TaskResp.class);
	}

	@Override
	public TaskResp checkIsAuto(String loginName, String loginPwd,
			int walletType) throws Exception {
		String url = EpsNetConfig.getTransactionUrl() + "TaskWS/chkIsAuto/"
				+ JavaUtils.joinString("/", loginName, loginPwd, walletType);
		return getResult(url, TaskResp.class);
	}

	@Override
	public TaskResp listGuaranteeTask(String loginName, String loginPwd,
			int status, int taskId, int count, int type, int walletType)
			throws Exception {
		String url = EpsNetConfig.getTransactionUrl()
				+ "TaskWS/listTask/"
				+ JavaUtils.joinString("/", loginName, loginPwd, status,
						taskId, count, type, walletType);
		return getResult(url, TaskResp.class);
	}

	public ComplainTaskResp execComplainTask(String uname, String upwd,
			int taskId, int payerGuaranteeAmountResultType,
			int payerGuaranteeAmountResultId,
			int payeeGuaranteeAmountResultType,
			int payeeGuaranteeAmountResultId, int infoFeeResultType,
			int infoFeeResultId, int dealBillType, String dealBillId, String note,
			String paymentPwd) throws Exception {
		String url = EpsNetConfig.getTransactionUrl()
				+ "ComplainTaskWS/execComplainTask/"
				+ JavaUtils.joinString("/", uname, upwd, taskId,
						payerGuaranteeAmountResultType,
						payerGuaranteeAmountResultId,
						payeeGuaranteeAmountResultType,
						payeeGuaranteeAmountResultId, infoFeeResultType,
						infoFeeResultId, dealBillType, dealBillId, note,
						paymentPwd);
		return getResult(url, ComplainTaskResp.class);
	}

	// 其他问题
	@Override
	public Integer createTask(String userAccount, String userName,
			String contactTel, int type, String detail) throws Exception {
		String url = EpsNetConfig.getTransactionUrl()
				+ "CustomServiceTaskWS/createTask/"
				+ JavaUtils.joinString("/", userAccount, userName, contactTel,
						type, detail);
		String result = Http.request(url);
		try {
			return Integer.parseInt(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
