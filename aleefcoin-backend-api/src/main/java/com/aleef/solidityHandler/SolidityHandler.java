package com.aleef.solidityHandler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import com.aleef.dtos.TokenDTO;
import com.aleef.models.ConfigInfo;
import com.aleef.models.LevelsPercentage;
import com.aleef.models.PurchaseCoinInfo;
import com.aleef.models.PurchaseInfo;
import com.aleef.models.RegisterInfo;
import com.aleef.models.RequestTokensInfo;
import com.aleef.models.TokenInfo;
import com.aleef.models.TransactionHistory;
import com.aleef.repo.ConfigInfoRepository;
import com.aleef.repo.LevelPercentageRepository;
import com.aleef.repo.PurchaseCoinInfoRepository;
import com.aleef.repo.PurchaseInfoRepository;
import com.aleef.repo.RegisterInfoRepository;
import com.aleef.repo.RequestTokensInfoRepository;
import com.aleef.repo.TokenInfoRepository;
import com.aleef.repo.TransactionHistoryRepository;
import com.aleef.service.impl.EmailNotificationServiceImpl;
import com.aleef.session.SessionCollector;
import com.aleef.solidityToJava.AleefCoin;
import com.aleef.utils.EncryptDecrypt;
import com.aleef.utils.FcmUtils;
import com.aleef.utils.UserUtils;

@Service
public class SolidityHandler {

	public static AleefCoin Token;

	public static TransactionReceipt transactionReceipt;

	static final Logger LOG = LoggerFactory.getLogger(SolidityHandler.class);

	// Connecting with main Network
	private final Web3j web3j = Web3j.build(new HttpService("https://mainnet.infura.io"));
	
	// Setting Default gas price and gas limit
	private BigInteger gasPrice = Contract.GAS_PRICE;

	private BigInteger gasLimit = Contract.GAS_LIMIT;

	@Autowired
	private Environment env;

	@Autowired
	private ConfigInfoRepository configInfoRepository;

	@Autowired
	private RegisterInfoRepository registerInfoRepository;

	@Autowired
	private UserUtils userUtils;

	@Autowired
	private TransactionHistoryRepository transactionHistoryRepository;

	@Autowired
	private TokenInfoRepository tokenInfoRepository;

	@Autowired
	private PurchaseInfoRepository purchaseInfoRepository;

	@Autowired
	private PurchaseCoinInfoRepository purchaseCoinInfoRepository;

	@Autowired
	private LevelPercentageRepository levelPercentageRepository;

	@Autowired
	private EmailNotificationServiceImpl emailNotificationServiceImpl;

	@Autowired
	private RequestTokensInfoRepository requestTokensInfoRepository;

	@Autowired
	private FcmUtils fcmUtils;
	
	/**
	 * 
	 * While the process of tokens transfer to the users the approval mail will be send to the second admin for the approval
	 */

	@SuppressWarnings("unused")
	public boolean transferTokenAdmin(TokenDTO tokenDTO) throws Exception {

		TransactionHistory transactionHistory = new TransactionHistory();
		ConfigInfo configInfo = configInfoRepository.findConfigInfoByConfigKey("walletFile");

		HttpSession session = SessionCollector.find(tokenDTO.getSessionId());
		LOG.info("Session ID " + tokenDTO.getSessionId());

		String mail = (String) session.getAttribute("emailId");

		RegisterInfo registerInfo = registerInfoRepository.findRegisterInfoByEmailId(mail);

		String etherAddress = userUtils.getEtherWalletAddress(configInfo.getConfigValue(),
				EncryptDecrypt.decrypt(registerInfo.getEtherWalletAddress()));

		transactionHistory.setFromAddress(etherAddress);
		transactionHistory.setToAddress(tokenDTO.getToAddress());
		transactionHistory.setTransferAmount(tokenDTO.getAmount());
		transactionHistory.setTransactionDate(new Date());
		transactionHistory.setTransferStatus("Waiting For Approval");
		transactionHistory.setTransactionMode("MAC");
		transactionHistoryRepository.save(transactionHistory);

		String id = EncryptDecrypt.encrypt(transactionHistory.getId().toString());

		String verificationLink = "Hi," + "<br><br>" + env.getProperty("email.content.approval") + "<br><br>"
				+ "From Address : " + StringUtils.trim(etherAddress) + "<br>" + "To Address : "
				+ StringUtils.trim(tokenDTO.getToAddress()) + "<br>" + "Token Amount : " + tokenDTO.getAmount()
				+ "<br><br>" + "<a href='" + env.getProperty("approval.url") + id + "'>"
				+ env.getProperty("approval.url.content") + "</a>" + "<br><br>" + "" + "With Regards,<br><br>"
				+ "Support Team<br>" + "Aleef Coin<br>" + "email : info@aleefcoin.io";

		List<RegisterInfo> registerInfo1 = (List<RegisterInfo>) registerInfoRepository.findRegisterInfoByRoleId(1);

		for (RegisterInfo registerInfos : registerInfo1) {

			if (!registerInfos.getEmailId().equalsIgnoreCase(mail)) {
				LOG.info("registerInfos.getEmailId : " + registerInfos.getEmailId());
				tokenDTO.setEmailId(registerInfos.getEmailId());
				boolean isEmailSent = emailNotificationServiceImpl.sendEmail(StringUtils.trim(tokenDTO.getEmailId()),
						"Aleef Coin Team", verificationLink);
			}
		}
		return true;
	}
	
	/**
	 * 
	 * While second admin approval the transaction which is made by first admin, 
	 * the requested token will deposit to the users wallet
	 */

	public String transferTokenAdminApproval(TokenDTO tokenDTO) throws Exception {

		transactionReceipt = new TransactionReceipt();

		String id = EncryptDecrypt.decrypt(tokenDTO.getTransferId().replaceAll("\\s", "+"));
		int i = Integer.valueOf(id);
		TransactionHistory transactionHistory = transactionHistoryRepository.findById(i);

		LOG.info("From Address : " + transactionHistory.getFromAddress() + transactionHistory.getTransferAmount());

		CompletableFuture.supplyAsync(() -> {
			Credentials credentials;
			try {
				transactionHistory.setTransferStatus("Pending");
				transactionHistoryRepository.save(transactionHistory);

				if (transactionHistory != null) {

					credentials = WalletUtils.loadCredentials(env.getProperty("credentials.password"),
							env.getProperty("credentials.address"));

					Token = AleefCoin.load(this.env.getProperty("token.address"), web3j, credentials, gasPrice,
							gasLimit);

					if (Token != null) {
						LOG.info("Token Getting amount" + transactionHistory.getTransferAmount() * 10000);
						BigInteger amount = BigDecimal.valueOf(transactionHistory.getTransferAmount() * 10000)
								.toBigInteger();

						transactionReceipt = Token.transfer(transactionHistory.getToAddress().trim(), amount).send();

						LOG.info("transactionReceipt ::::::::::::::::::" + ":::::::"
								+ transactionReceipt.getTransactionHash() + transactionReceipt.getGasUsed()
								+ "::::::::::::::::" + transactionReceipt.getStatus() + "::::::::::::::::::"
								+ transactionReceipt.getCumulativeGasUsed());

					}
				}
			} catch (Exception e) {

				e.printStackTrace();
			}

			return "call blackchain";
		}).thenAccept(product -> {
			LOG.info("After calling the callback function");
			if (transactionReceipt.getStatus().equalsIgnoreCase("0x1")) {

				transactionHistory.setTransferStatus("Success");
				transactionHistoryRepository.save(transactionHistory);

				TokenInfo tokenInfo = tokenInfoRepository.findOne(1);
				BigDecimal tokens = tokenInfo.getTotalInternalTokens();

				BigDecimal amount = tokens.subtract(new BigDecimal(tokenDTO.getAmount()));
				tokenInfo.setTotalInternalTokens(amount);

				tokenInfoRepository.save(tokenInfo);

			} else {
				transactionHistory.setTransferStatus("Failed");
				transactionHistoryRepository.save(transactionHistory);
			}
		});
		return transactionReceipt.toString();
	}
	
	/**
	 * 
	 * Manual token transfer from admin to user for mobile app
	 * 
	 */
	

	@SuppressWarnings("static-access")
	public String transferToken(TokenDTO tokenDTO) throws Exception {

		transactionReceipt = new TransactionReceipt();
		TransactionHistory transactionHistory = new TransactionHistory();

		ConfigInfo configInfo = configInfoRepository.findConfigInfoByConfigKey("walletFile");

		HttpSession session = SessionCollector.find(tokenDTO.getSessionId());

		String mail = (String) session.getAttribute("emailId");

		RegisterInfo registerInfo = registerInfoRepository.findRegisterInfoByEmailId(mail);

		String fromAddress = userUtils.getEtherWalletAddress(configInfo.getConfigValue(),
				EncryptDecrypt.decrypt(registerInfo.getEtherWalletAddress()));

		LOG.info("configInfo.getConfigValue() + EncryptDecrypt.decrypt(registerInfo.getEtherWalletAddress())");

		CompletableFuture.supplyAsync(() -> {
			Credentials credentials;
			try {
				transactionHistory.setFromAddress(fromAddress);
				transactionHistory.setToAddress(tokenDTO.getToAddress());
				transactionHistory.setTransferAmount(tokenDTO.getAmount());
				transactionHistory.setTransactionDate(new Date());
				transactionHistory.setTransferStatus("Pending");
				transactionHistory.setTransactionMode("MAC");
				transactionHistoryRepository.save(transactionHistory);

				if (transactionHistory != null) {

					credentials = WalletUtils.loadCredentials(tokenDTO.getEtherWalletPassword(),
							configInfo.getConfigValue() + EncryptDecrypt.decrypt(registerInfo.getEtherWalletAddress()));
					Token = AleefCoin.load(this.env.getProperty("token.address"), web3j, credentials, gasPrice,
							gasLimit);

					if (Token != null) {
						LOG.info("Token Getting Amount" + tokenDTO.getAmount() * 10000);
						BigInteger amount = BigDecimal.valueOf(tokenDTO.getAmount() * 10000).toBigInteger();

						transactionReceipt = Token.transfer(tokenDTO.getToAddress().trim(), amount).send();

						LOG.info("transactionReceipt ::::::::::::::::::" + ":::::::"
								+ transactionReceipt.getTransactionHash() + transactionReceipt.getGasUsed()
								+ "::::::::::::::::" + transactionReceipt.getStatus() + "::::::::::::::::::"
								+ transactionReceipt.getCumulativeGasUsed());

					}
				}
			} catch (Exception e) {

				e.printStackTrace();
			}

			return "call blackchain";
		}).thenAccept(product -> {
			LOG.info("After calling the callback function");
			if (transactionReceipt.getStatus().equalsIgnoreCase("0x1")) {

				transactionHistory.setTransferStatus("Success");
				transactionHistoryRepository.save(transactionHistory);

				if (registerInfo.getRoleId() == 1) {

					TokenInfo tokenInfo = tokenInfoRepository.findOne(1);
					BigDecimal tokens = tokenInfo.getTotalInternalTokens();

					BigDecimal amount = tokens.subtract(new BigDecimal(tokenDTO.getAmount()));
					tokenInfo.setTotalInternalTokens(amount);

					tokenInfoRepository.save(tokenInfo);

					RegisterInfo registerInfo1 = registerInfoRepository
							.findByWalletAddress(tokenDTO.getToAddress().trim());

					String appId = registerInfo1.getAppId();

					if (appId.length() >= 1) {
						String server_key = env.getProperty("server_key").trim();

						String message = registerInfo.getUserName() + " has transfered " + tokenDTO.getAmount() + " "
								+ "for you";

						JSONObject infoJson = new JSONObject();

						try {
							infoJson.put("title", "Aleef Send Coin Notification");

							infoJson.put("body", message);

							infoJson.put("id", tokenDTO.getId());

						} catch (JSONException e) {
							
							e.printStackTrace();
						}
						String deviceType = registerInfo1.getDeviceType();
						LOG.info("Device Type" + registerInfo1.getDeviceType());

						fcmUtils.send_FCM_Notification(appId, server_key, message, deviceType, infoJson);
					}
				}

			} else {
				transactionHistory.setTransferStatus("Failed");
				transactionHistoryRepository.save(transactionHistory);
			}
		});
		return transactionReceipt.toString();
	}

	// Check token balance of admin as well as users
	
	public Double balanceTokens(TokenDTO tokenDTO) throws Exception {

		ConfigInfo configInfo = configInfoRepository.findConfigInfoByConfigKey("walletFile");
		HttpSession session = SessionCollector.find(tokenDTO.getSessionId());

		String mail = (String) session.getAttribute("emailId");

		RegisterInfo registerInfo = registerInfoRepository.findRegisterInfoByEmailId(mail);

		String walletPassword = registerInfo.getEtherWalletPassword();

		String dwalletPassword = EncryptDecrypt.decrypt(walletPassword);

		String fromAddress = userUtils.getEtherWalletAddress(configInfo.getConfigValue(),
				EncryptDecrypt.decrypt(registerInfo.getEtherWalletAddress()));

		LOG.info("configInfo.getConfigValue() + EncryptDecrypt.decrypt(registerInfo.getEtherWalletAddress())");

		Credentials credentials = WalletUtils.loadCredentials(dwalletPassword,
				configInfo.getConfigValue() + EncryptDecrypt.decrypt(registerInfo.getEtherWalletAddress()));
		Token = AleefCoin.load(this.env.getProperty("token.address"), web3j, credentials, gasPrice, gasLimit);

		BigInteger amount = Token.balanceOf(fromAddress).send();

		double amt = amount.doubleValue() / 10000;

		tokenDTO.setTokenAmount(BigDecimal.valueOf(amt));

		String status = "pending";

		List<RequestTokensInfo> requestTokensInfo1 = (List<RequestTokensInfo>) requestTokensInfoRepository
				.findByFromAddressAndStatus(fromAddress, status);

		int count = requestTokensInfo1.size();
		LOG.info("count" + count);

		tokenDTO.setNotificationCount(count);

		Date date = new Date();

		List<PurchaseCoinInfo> purchaseCoinInfo = purchaseCoinInfoRepository.findAll();

		for (PurchaseCoinInfo purchase : purchaseCoinInfo) {
			if (date.after(purchase.getIcoStartDate()) && date.before(purchase.getIcoEndDate())) {

				double tokenAmount = purchase.getAleefRateInUSD().doubleValue();

				Double usdAmount = amt * tokenAmount;
				LOG.info("usdAmount" + usdAmount);
				tokenDTO.setTokenToUsd(usdAmount);
			}
		}
		return amount.doubleValue() / 10000;
	}
	
	// Check Balance tokens for admin

	public Double balanceTokensForAdmin(TokenDTO tokenDTO) throws Exception {

		ConfigInfo configInfo = configInfoRepository.findConfigInfoByConfigKey("walletFile");
		HttpSession session = SessionCollector.find(tokenDTO.getSessionId());

		String mail = (String) session.getAttribute("emailId");

		RegisterInfo registerInfo = registerInfoRepository.findRegisterInfoByEmailId(mail);

		String walletPassword = registerInfo.getEtherWalletPassword();

		String dwalletPassword = EncryptDecrypt.decrypt(walletPassword);

		String fromAddress = userUtils.getEtherWalletAddress(configInfo.getConfigValue(),
				EncryptDecrypt.decrypt(registerInfo.getEtherWalletAddress()));

		LOG.info("configInfo.getConfigValue() + EncryptDecrypt.decrypt(registerInfo.getEtherWalletAddress())");

		Credentials credentials = WalletUtils.loadCredentials(dwalletPassword,
				configInfo.getConfigValue() + EncryptDecrypt.decrypt(registerInfo.getEtherWalletAddress()));
		Token = AleefCoin.load(this.env.getProperty("token.address"), web3j, credentials, gasPrice, gasLimit);

		BigInteger amount = Token.balanceOf(fromAddress).send();

		LOG.info("amount :::::::::::::::" + amount);

		Double amt = amount.doubleValue() / 10000;

		TokenInfo tokenInfo = tokenInfoRepository.findOne(1);

		BigDecimal tokens = new BigDecimal(amt).subtract(tokenInfo.getTotalIcoTokens());

		tokenDTO.setTokenAmount(tokens);

		return tokens.doubleValue();
	}
	
	// Burn Aleef tokens 

	public String burnTokens(TokenDTO tokenDTO) throws FileNotFoundException, IOException, ParseException, Exception {

		transactionReceipt = new TransactionReceipt();

		HttpSession session = SessionCollector.find(tokenDTO.getSessionId());
		String mail = (String) session.getAttribute("emailId");

		RegisterInfo registerInfo = registerInfoRepository.findRegisterInfoByEmailId(mail);

		ConfigInfo configInfo = configInfoRepository.findConfigInfoByConfigKey("walletFile");

		LOG.info("configInfo.getConfigValue() + EncryptDecrypt.decrypt(registerInfo.getEtherWalletAddress())");

		TokenInfo tokenInfo = tokenInfoRepository.findOne(1);

		CompletableFuture.supplyAsync(() -> {
			Credentials credentials;
			try {

				credentials = WalletUtils.loadCredentials(tokenDTO.getEtherWalletPassword(),

						configInfo.getConfigValue() + EncryptDecrypt.decrypt(registerInfo.getEtherWalletAddress()));

				Token = AleefCoin.load(this.env.getProperty("token.address"), web3j, credentials, gasPrice, gasLimit);

				if (Token != null) {
					LOG.info("Amount" + tokenDTO.getAmount() * 10000);

					BigInteger amt = new BigDecimal(tokenDTO.getAmount() * 10000).toBigInteger();

					transactionReceipt = Token.burn(amt).send();

					LOG.info("transactionReceipt ::::::::::::::::::" + ":::::::"
							+ transactionReceipt.getTransactionHash() + transactionReceipt.getGasUsed()
							+ "::::::::::::::::" + transactionReceipt.getStatus() + "::::::::::::::::::"
							+ transactionReceipt.getCumulativeGasUsed());
				}
			} catch (Exception e) {

				e.printStackTrace();
			}

			return "call blackchain";
		}).thenAccept(product -> {
			LOG.info("After calling the callback function");
			if (transactionReceipt.getStatus().equalsIgnoreCase("0x1")) {

				registerInfo.setBurnTokens(registerInfo.getBurnTokens() + tokenDTO.getAmount());
				registerInfoRepository.save(registerInfo);

				if (registerInfo.getRoleId() == 1) {

					BigDecimal tokens = tokenInfo.getTotalInternalTokens();
					BigDecimal amount = tokens.subtract(new BigDecimal(tokenDTO.getAmount()));
					tokenInfo.setTotalInternalTokens(amount);
					tokenInfoRepository.save(tokenInfo);
				}
			}

		});
		return transactionReceipt.toString();
	}
	
	/**
	 * 
	 * Transaction history should be updated for all transactions
	 */

	public List<TokenDTO> transactionHistory(TokenDTO tokenDTO, String etherWalletAddress) throws Exception {

		List<TokenDTO> transactionList = new ArrayList<TokenDTO>();
		List<TransactionHistory> transactionHistory = (List<TransactionHistory>) transactionHistoryRepository
				.findByFromAddressOrToAddressOrderByTransactionDateDesc(etherWalletAddress, etherWalletAddress);
		LOG.info("Transaction History:::::::::::" + transactionHistory.toString());

		if (tokenDTO.getTransactionType() == 0) {

			for (TransactionHistory transactionInfo : transactionHistory) {

				TokenDTO transaction = userUtils.listTransactions(transactionInfo);
				transactionList.add(transaction);
			}

		} else if (tokenDTO.getTransactionType() == 1) {

			for (TransactionHistory transactionInfo : transactionHistory) {

				if (tokenDTO.getEtherWalletAddress().equalsIgnoreCase(transactionInfo.getFromAddress())) {

					TokenDTO transactions = new TokenDTO();
					transactions.setFromAddress(transactionInfo.getFromAddress());
					transactions.setToAddress(transactionInfo.getToAddress());
					transactions.setTransactionAmount(transactionInfo.getTransferAmount());
					transactions.setTransactionDate(transactionInfo.getTransactionDate());
					transactions.setTransactionMode(transactionInfo.getTransactionMode());
					transactions.setTransferStatus(transactionInfo.getTransferStatus());
					transactionList.add(transactions);
				}
			}

		} else if (tokenDTO.getTransactionType() == 2) {

			for (TransactionHistory transactionInfo : transactionHistory) {

				if (tokenDTO.getEtherWalletAddress().equalsIgnoreCase(transactionInfo.getToAddress())) {

					TokenDTO transactions = new TokenDTO();
					transactions.setFromAddress(transactionInfo.getFromAddress());
					transactions.setToAddress(transactionInfo.getToAddress());
					transactions.setTransactionAmount(transactionInfo.getTransferAmount());
					transactions.setTransactionDate(transactionInfo.getTransactionDate());
					transactions.setTransactionMode(transactionInfo.getTransactionMode());
					transactions.setTransferStatus(transactionInfo.getTransferStatus());
					transactionList.add(transactions);
				}
			}

		} else if (tokenDTO.getTransactionType() == 3) {

			List<TransactionHistory> transactionHistorys = (List<TransactionHistory>) transactionHistoryRepository
					.findTop3ByFromAddressOrToAddressOrderByTransactionDateDesc(etherWalletAddress, etherWalletAddress);

			for (TransactionHistory transactionInfo : transactionHistorys) {
				TokenDTO transactions = new TokenDTO();

				if (tokenDTO.getEtherWalletAddress().equalsIgnoreCase(transactionInfo.getFromAddress())) {

					transactions.setFromAddress(transactionInfo.getFromAddress());
					transactions.setToAddress(transactionInfo.getToAddress());
					transactions.setTransactionAmount(transactionInfo.getTransferAmount());
					transactions.setTransactionDate(transactionInfo.getTransactionDate());
					transactions.setTransactionMode(transactionInfo.getTransactionMode());
					transactions.setTransferStatus(transactionInfo.getTransferStatus());
					transactions.setTypeOfStatus("Sent");
					transactionList.add(transactions);
				} else {
					transactions.setFromAddress(transactionInfo.getFromAddress());
					transactions.setToAddress(transactionInfo.getToAddress());
					transactions.setTransactionAmount(transactionInfo.getTransferAmount());
					transactions.setTransactionDate(transactionInfo.getTransactionDate());
					transactions.setTransactionMode(transactionInfo.getTransactionMode());
					transactions.setTransferStatus(transactionInfo.getTransferStatus());
					transactions.setTypeOfStatus("Received");
					transactionList.add(transactions);
				}
			}
		}

		return transactionList;
	}
	
	// Listing the purchase tokens

	public List<TokenDTO> listPurchaseTokens(TokenDTO tokenDTO, String etherWalletAddress) {

		HttpSession sessions = SessionCollector.find(tokenDTO.getSessionId());

		String email = (String) sessions.getAttribute("emailId");

		RegisterInfo registerInfo = registerInfoRepository.findRegisterInfoByEmailId(email);

		if (registerInfo.getRoleId() == 1) {

			List<TokenDTO> listPurchaseTokens = new ArrayList<TokenDTO>();

			List<PurchaseInfo> purchaseInfo = purchaseInfoRepository.findAllByOrderByIdDesc();

			for (PurchaseInfo purchaseInfos : purchaseInfo) {

				TokenDTO tokenDTOs = new TokenDTO();

				tokenDTOs.setEtherWalletAddress(purchaseInfos.getEtherWalletAddress());
				tokenDTOs.setRequestTokens(purchaseInfos.getRequestTokens().doubleValue());
				tokenDTOs.setFreeTokens(purchaseInfos.getFreeTokens());
				tokenDTOs.setEtherAmount(purchaseInfos.getEtherAmount());
				tokenDTOs.setEmailId(purchaseInfos.getEmailId());
				tokenDTOs.setUserName(purchaseInfos.getUserName());
				tokenDTOs.setPurchasedDate(purchaseInfos.getPurchasedDate());
				tokenDTOs.setPurchaseStatus(purchaseInfos.getTransferStatus());

				listPurchaseTokens.add(tokenDTOs);
			}
			return listPurchaseTokens;
		} else {

			List<TokenDTO> listPurchaseTokens = new ArrayList<TokenDTO>();

			List<PurchaseInfo> purchaseInfo = purchaseInfoRepository
					.findByEtherWalletAddressOrderByPurchasedDateDesc(etherWalletAddress);

			LOG.info("Purchase Info Lists" + purchaseInfo.toString());
			for (PurchaseInfo purchaseTokens : purchaseInfo) {

				TokenDTO tokenDTOs = new TokenDTO();
				tokenDTOs.setEtherWalletAddress(purchaseTokens.getEtherWalletAddress());
				tokenDTOs.setRequestTokens(purchaseTokens.getRequestTokens().doubleValue());
				tokenDTOs.setFreeTokens(purchaseTokens.getFreeTokens());
				tokenDTOs.setEtherAmount(purchaseTokens.getEtherAmount());
				tokenDTOs.setEmailId(purchaseTokens.getEmailId());
				tokenDTOs.setUserName(purchaseTokens.getUserName());
				tokenDTOs.setPurchasedDate(purchaseTokens.getPurchasedDate());
				tokenDTOs.setPurchaseStatus(purchaseTokens.getTransferStatus());

				listPurchaseTokens.add(tokenDTOs);
			}
			return listPurchaseTokens;
		}
	}
	
	//Filtering the purchase tokens history by using user name of users

	public List<TokenDTO> purchaseListFilter(TokenDTO tokenDTO) throws Exception {

		List<TokenDTO> filter = new ArrayList<TokenDTO>();

		if (tokenDTO.getEtherWalletAddress() != null) {

			List<PurchaseInfo> purchaseInfo = purchaseInfoRepository
					.findByEtherWalletAddressOrderByPurchasedDateDesc(tokenDTO.getEtherWalletAddress().trim());

			for (PurchaseInfo purchaseInfos : purchaseInfo) {

				TokenDTO tokenDTOs = new TokenDTO();
				tokenDTOs.setEtherWalletAddress(purchaseInfos.getEtherWalletAddress());
				tokenDTOs.setRequestTokens(purchaseInfos.getRequestTokens().doubleValue());
				tokenDTOs.setFreeTokens(purchaseInfos.getFreeTokens());
				tokenDTOs.setEtherAmount(purchaseInfos.getEtherAmount());
				tokenDTOs.setEmailId(purchaseInfos.getEmailId());
				tokenDTOs.setUserName(purchaseInfos.getUserName());
				tokenDTOs.setPurchasedDate(purchaseInfos.getPurchasedDate());
				tokenDTOs.setPurchaseStatus(purchaseInfos.getTransferStatus());
				filter.add(tokenDTOs);
			}
		} else if (tokenDTO.getUserName() != null) {
			LOG.info(tokenDTO.getUserName());
			List<PurchaseInfo> purchaseInfo = purchaseInfoRepository.findByUserNameLike(tokenDTO.getUserName().trim());
			for (PurchaseInfo purchaseInfos : purchaseInfo) {

				TokenDTO tokenDTOs = new TokenDTO();
				tokenDTOs.setEtherWalletAddress(purchaseInfos.getEtherWalletAddress());
				tokenDTOs.setRequestTokens(purchaseInfos.getRequestTokens().doubleValue());
				tokenDTOs.setFreeTokens(purchaseInfos.getFreeTokens());
				tokenDTOs.setEtherAmount(purchaseInfos.getEtherAmount());
				tokenDTOs.setEmailId(purchaseInfos.getEmailId());
				tokenDTOs.setUserName(purchaseInfos.getUserName());
				tokenDTOs.setPurchasedDate(purchaseInfos.getPurchasedDate());
				tokenDTOs.setPurchaseStatus(purchaseInfos.getTransferStatus());
				filter.add(tokenDTOs);
			}
		}
		return filter;

	}
	
	// Filtering the transaction history based on transaction mode

	public List<TokenDTO> filterMode(TokenDTO tokenDTO) throws Exception {

		HttpSession sessions = SessionCollector.find(tokenDTO.getSessionId());

		String email = (String) sessions.getAttribute("emailId");

		RegisterInfo registerInfo = registerInfoRepository.findRegisterInfoByEmailId(email.trim());

		ConfigInfo configInfo = configInfoRepository.findConfigInfoByConfigKey("walletFile");

		String address = userUtils.getEtherWalletAddress(configInfo.getConfigValue(),
				EncryptDecrypt.decrypt(registerInfo.getEtherWalletAddress()));

		List<TokenDTO> filter = new ArrayList<TokenDTO>();

		List<TransactionHistory> list = transactionHistoryRepository
				.findByTransactionMode(tokenDTO.getTransactionMode().trim());

		for (TransactionHistory listMode : list) {

			if (listMode.getFromAddress().equals(address) || listMode.getToAddress().equals(address)) {
				TokenDTO transactions = new TokenDTO();
				transactions.setFromAddress(listMode.getFromAddress());
				transactions.setToAddress(listMode.getToAddress());
				transactions.setTransactionAmount(listMode.getTransferAmount());
				transactions.setTransactionDate(listMode.getTransactionDate());
				transactions.setTransactionMode(listMode.getTransactionMode());
				transactions.setTransferStatus(listMode.getTransferStatus());

				filter.add(transactions);
			}
		}
		return filter;
	}
	
	//Filtering the transaction history based on ether wallet address

	public List<TokenDTO> filterAddress(String etherWalletAddress) {

		List<TokenDTO> filter = new ArrayList<TokenDTO>();

		List<TransactionHistory> list = transactionHistoryRepository
				.findByFromAddressOrToAddressOrderByTransactionDateDesc(etherWalletAddress.trim(),
						etherWalletAddress.trim());

		for (TransactionHistory listMode : list) {

			TokenDTO transactions = new TokenDTO();
			transactions.setFromAddress(listMode.getFromAddress());
			transactions.setToAddress(listMode.getToAddress());
			transactions.setTransactionAmount(listMode.getTransferAmount());
			transactions.setTransactionDate(listMode.getTransactionDate());
			transactions.setTransactionMode(listMode.getTransactionMode());
			transactions.setTransferStatus(listMode.getTransferStatus());

			filter.add(transactions);
		}

		return filter;
	}
	
	// Transfer tokens from admin wallet to referrals wallet

	public String TransferCoinfromAdminWalletToReferalSponserWallet(String getAddress, Double Tokens, String acMode)
			throws Exception {

		transactionReceipt = new TransactionReceipt();
		TransactionHistory transactionHistory = new TransactionHistory();

		Credentials credentials;
		try {
			transactionHistory.setFromAddress(this.env.getProperty("main.wallet.address"));
			transactionHistory.setToAddress(getAddress);
			transactionHistory.setTransferAmount(Tokens.doubleValue());
			transactionHistory.setTransactionDate(new Date());
			transactionHistory.setTransferStatus("Pending");
			transactionHistory.setTransactionMode(acMode);
			transactionHistoryRepository.save(transactionHistory);

			if (transactionHistory != null) {
				credentials = WalletUtils.loadCredentials(this.env.getProperty("credentials.password"),
						this.env.getProperty("credentials.address"));
				Token = AleefCoin.load(this.env.getProperty("token.address"), web3j, credentials, gasPrice, gasLimit);

				if (Token != null) {
					LOG.info("Before Transfer Wallet Address" + getAddress);

					Double i = Tokens * 10000;
					BigInteger amount = BigDecimal.valueOf(i).toBigInteger();

					transactionReceipt = Token.transfer(getAddress.trim(), amount).send();
					LOG.info("After Transfer Wallet Address" + getAddress);
					LOG.info("Waller Addr ::" + getAddress + "  Hash ::::::" + transactionReceipt.getTransactionHash()
							+ "  GasUsed :::" + transactionReceipt.getGasUsed() + "  Status ::"
							+ transactionReceipt.getStatus() + "  CumulativeGasUsed::"
							+ transactionReceipt.getCumulativeGasUsed());
				}
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
		LOG.info("Inside callback function");
		if (transactionReceipt.getStatus() == null) {

			transactionHistory.setTransferStatus("Success");
			transactionHistoryRepository.save(transactionHistory);

		} else {
			transactionHistory.setTransferStatus("Failed");
			transactionHistoryRepository.save(transactionHistory);
		}
		return transactionReceipt.toString();
	}

	//Save referral tokens for all individual referrals in the purchase info table
	
	@SuppressWarnings("unused")
	public boolean refferal() {

		List<PurchaseInfo> listPurchaseInfo = purchaseInfoRepository.findAll();

		LOG.info("List Purchase Info" + listPurchaseInfo);

		LevelsPercentage levelsPercentage = levelPercentageRepository.findOne(1);
		int count = 0;
		for (PurchaseInfo purchaseCoinInfo : listPurchaseInfo) {

			Integer transactionId = purchaseCoinInfo.getId().intValue();

			if (purchaseCoinInfo.getId().toString() == null) {
				transactionId = 0;
			} else {
				transactionId = purchaseCoinInfo.getId().intValue();
			}

			if (purchaseCoinInfo.getFreeTokens() == null) {
				purchaseCoinInfo.setFreeTokens(new BigDecimal(0));
			}

			RegisterInfo findLevelOneSponcerInfo = null;
			RegisterInfo findLevelTwoSponcerInfo = null;
			RegisterInfo findLevelThreeSponcerInfo = null;
			RegisterInfo findLevelFourSponcerInfo = null;
			String userWalletAddress = null;
			String walletAddressForLevel1 = null;
			String walletAddressForLevel2 = null;
			String walletAddressForLevel3 = null;
			String walletAddressForLevel4 = null;

			String decryptedEtherWalletAddress;

			RegisterInfo registerInfo = registerInfoRepository
					.findRegisterInfoByEmailId(purchaseCoinInfo.getEmailId().trim());

			if (registerInfo != null) {
				try {

					decryptedEtherWalletAddress = EncryptDecrypt.decrypt(registerInfo.getEtherWalletAddress());
					userWalletAddress = userUtils.getWalletAddress(decryptedEtherWalletAddress);

					if (!registerInfo.getSponser_id().equals(env.getProperty("admin.id"))) {
						findLevelOneSponcerInfo = registerInfoRepository.findById(registerInfo.getSponser_id());
						decryptedEtherWalletAddress = EncryptDecrypt
								.decrypt(findLevelOneSponcerInfo.getEtherWalletAddress());
						walletAddressForLevel1 = userUtils.getWalletAddress(decryptedEtherWalletAddress);

						if (!findLevelOneSponcerInfo.getSponser_id().equals(env.getProperty("admin.id"))
								&& findLevelOneSponcerInfo != null) {
							findLevelTwoSponcerInfo = registerInfoRepository
									.findById(findLevelOneSponcerInfo.getSponser_id());
							decryptedEtherWalletAddress = EncryptDecrypt
									.decrypt(findLevelTwoSponcerInfo.getEtherWalletAddress());
							walletAddressForLevel2 = userUtils.getWalletAddress(decryptedEtherWalletAddress);

							if (!findLevelTwoSponcerInfo.getSponser_id().equals(env.getProperty("admin.id"))
									&& findLevelTwoSponcerInfo != null) {
								findLevelThreeSponcerInfo = registerInfoRepository
										.findById(findLevelTwoSponcerInfo.getSponser_id());
								decryptedEtherWalletAddress = EncryptDecrypt
										.decrypt(findLevelThreeSponcerInfo.getEtherWalletAddress());
								walletAddressForLevel3 = userUtils.getWalletAddress(decryptedEtherWalletAddress);
								if (!findLevelThreeSponcerInfo.getSponser_id().equals(env.getProperty("admin.id"))
										&& findLevelThreeSponcerInfo != null) {
									findLevelFourSponcerInfo = registerInfoRepository
											.findById(findLevelThreeSponcerInfo.getSponser_id());
									decryptedEtherWalletAddress = EncryptDecrypt
											.decrypt(findLevelFourSponcerInfo.getEtherWalletAddress());
									walletAddressForLevel4 = userUtils.getWalletAddress(decryptedEtherWalletAddress);
								}

							}
						}
					}

					PurchaseInfo purchaseInfo = new PurchaseInfo();
					if (walletAddressForLevel1 != null) {
						double levelOneOffer = (purchaseCoinInfo.getRequestTokens().intValue()
								* levelsPercentage.getLevelOne().intValue()) / 100;

						purchaseInfo.setRefferalTokens(new BigDecimal(levelOneOffer));
						purchaseInfoRepository.save(purchaseInfo);

					}
					if (walletAddressForLevel2 != null) {
						double levelTwoOffer = (purchaseCoinInfo.getRequestTokens().intValue()
								* levelsPercentage.getLevelTwo().intValue()) / 100;

						purchaseInfo.setRefferalTokens(new BigDecimal(levelTwoOffer));
						purchaseInfoRepository.save(purchaseInfo);
					}
					if (walletAddressForLevel3 != null) {
						double levelThreeOffer = (purchaseCoinInfo.getRequestTokens().intValue()
								* levelsPercentage.getLevelThree().intValue()) / 100;

						purchaseInfo.setRefferalTokens(new BigDecimal(levelThreeOffer));
						purchaseInfoRepository.save(purchaseInfo);
					}
					if (walletAddressForLevel4 != null) {
						double levelFourOffer = (purchaseCoinInfo.getRequestTokens().intValue()
								* levelsPercentage.getLevelFour().intValue()) / 100;

						purchaseInfo.setRefferalTokens(new BigDecimal(levelFourOffer));
						purchaseInfoRepository.save(purchaseInfo);
					}

				} catch (Exception e) {
					
					LOG.error("Exception e" + e);
				}
			}
		}
		return true;
	}
	
	//Check ether balance

	public BigDecimal checkEtherBalance(String decryptedEtherWalletAddress)
			throws InterruptedException, ExecutionException {

		String[] fetchAddress = decryptedEtherWalletAddress.split("--");
		String getAddress = fetchAddress[fetchAddress.length - 1].split("\\.")[0];
		LOG.info("Actual Ether Wallet Address" + getAddress);

		EthGetBalance etherBalance = web3j.ethGetBalance("0x" + getAddress, DefaultBlockParameterName.LATEST)
				.sendAsync().get();
		BigInteger wei = etherBalance.getBalance();
		String s = wei.toString();
		BigDecimal ether = Convert.fromWei(s, Convert.Unit.ETHER);
		LOG.info("Actual Ether Balance" + ether);

		return ether;
	}
	
	//Send ether fund to the admin wallet 

	@SuppressWarnings("unused")
	public boolean sendFundToAdmin(String decryptedEtherWalletAddress, String decryptedEtherWalletPassword,
			BigDecimal fund) throws InterruptedException, TransactionException, Exception {

		String[] fetchAddress = decryptedEtherWalletAddress.split("--");
		String getAddress = fetchAddress[fetchAddress.length - 1].split("\\.")[0];

		LOG.info("Actual Ether Wallet Address" + decryptedEtherWalletAddress);

		ConfigInfo configInfo = configInfoRepository.findConfigInfoByConfigKey("WalletFile");
		Credentials credentials = WalletUtils.loadCredentials(decryptedEtherWalletPassword,
				configInfo.getConfigValue() + decryptedEtherWalletAddress);

		TransactionReceipt transactionReceipt = Transfer
				.sendFunds(web3j, credentials, env.getProperty("main.wallet.address"), fund, Convert.Unit.ETHER).send();
		if (transactionReceipt != null) {
			LOG.info("transactionReceipt.getStatus()" + transactionReceipt.getStatus());
			LOG.info("transactionReceipt.getBlockHash()" + transactionReceipt.getBlockHash());
			LOG.info("transactionReceipt.getCumulativeGasUsedRaw()" + transactionReceipt.getCumulativeGasUsedRaw());
			LOG.info("transactionReceipt.hashCode()" + transactionReceipt.hashCode());

			return true;

		}
		return false;

	}

}
