package com.aleef.service.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.core.env.Environment;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aleef.dtos.TokenDTO;
import com.aleef.models.ConfigInfo;
import com.aleef.models.LevelsPercentage;
import com.aleef.models.PurchaseCoinInfo;
import com.aleef.models.PurchaseInfo;
import com.aleef.models.RegisterInfo;
import com.aleef.models.RequestTokensInfo;
import com.aleef.models.TokenInfo;
import com.aleef.repo.ConfigInfoRepository;
import com.aleef.repo.LevelPercentageRepository;
import com.aleef.repo.PurchaseCoinInfoRepository;
import com.aleef.repo.PurchaseInfoRepository;
import com.aleef.repo.RegisterInfoRepository;
import com.aleef.repo.RequestTokensInfoRepository;
import com.aleef.repo.TokenInfoRepository;
import com.aleef.services.TokenService;
import com.aleef.session.SessionCollector;
import com.aleef.solidityHandler.SolidityHandler;
import com.aleef.utils.CurrentValueUtils;
import com.aleef.utils.EncryptDecrypt;
import com.aleef.utils.FcmUtils;
import com.aleef.utils.UserUtils;

import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;

@Service
public class TokenServiceImplementations implements TokenService {

	static final Logger LOG = LoggerFactory.getLogger(TokenServiceImplementations.class);

	@Autowired
	private SolidityHandler solidityHandler;

	@Autowired
	private PurchaseCoinInfoRepository purchaseCoinInfoRepository;

	@Autowired
	private LevelPercentageRepository levelPercentageRepository;

	@Autowired
	private EmailNotificationServiceImpl emailNotificationServiceImpl;

	@Autowired
	private PurchaseInfoRepository purchaseInfoRepository;

	@Autowired
	private CurrentValueUtils currentValueUtils;

	@Autowired
	private TokenInfoRepository tokenInfoRepository;

	@Autowired
	private RegisterInfoRepository registerInfoRepository;

	@Autowired
	private ConfigInfoRepository configInfoRepository;

	@Autowired
	private UserUtils userUtils;

	@Autowired
	private RequestTokensInfoRepository requestTokensInfoRepository;

	@Autowired
	private Environment env;

	@Autowired
	private FcmUtils fcmUtils;

	@Override
	public boolean transferTokenAdmin(TokenDTO tokenDTO) throws Exception {
		boolean transfer = solidityHandler.transferTokenAdmin(tokenDTO);
		if (!transfer) {
			return true;
		}
		return false;
	}

	@Override
	public boolean transferTokenAdminApproval(TokenDTO tokenDTO) throws Exception {

		String transfer = solidityHandler.transferTokenAdminApproval(tokenDTO);
		if (transfer != null) {
			return true;
		}
		return false;
	}

	@Override
	public String transferToken(TokenDTO tokenDTO) throws Exception {
		String transfer = solidityHandler.transferToken(tokenDTO);
		if (transfer != null) {
			return transfer;
		}
		return null;
	}

	@Override
	public Double balanceTokens(TokenDTO tokenDTO) throws Exception {
		Double balance = solidityHandler.balanceTokens(tokenDTO);
		if (balance != null) {
			return balance;
		}
		return null;
	}

	@Override
	public Double balanceTokensForAdmin(TokenDTO tokenDTO) throws Exception {
		Double balance = solidityHandler.balanceTokensForAdmin(tokenDTO);
		if (balance != null) {
			return balance;
		}
		return null;
	}

	@Override
	public String burnTokens(TokenDTO tokenDTO) throws FileNotFoundException, IOException, ParseException, Exception {
		String burn = solidityHandler.burnTokens(tokenDTO);
		if (burn != null) {
			return burn;
		}
		return null;
	}

	@Override
	public List<TokenDTO> transactionHistory(TokenDTO tokenDTO, String etherWalletAddress) throws Exception {
		List<TokenDTO> transactionLists = solidityHandler.transactionHistory(tokenDTO, etherWalletAddress);

		return transactionLists;

	}
	
	
	 /**
     * 
     * Once aleef coins purchased by the user, equivalent ether will be withdrawn from corresponding users wallet
     * then that ether will be deposited into admin wallet 
     * 
     * Four referral persons will get the benefit as follows
     * 
     *  10% of user purchased coins for first level
     * 
     *  5% of user purchased coins for second level
     * 
     *  3% of user purchased coins for third level 
     *  
     *  2% of user purchased coins for fourth level referral persons 
     * 
     * Then it will trigger a acknowledge mail to the corresponding user
     * 
     */
	
	

	@SuppressWarnings("unused")
	@Override
	public boolean purchaseTokens(TokenDTO tokenDTO) throws Exception {

		List<PurchaseCoinInfo> purchaseCoinInfos = (List<PurchaseCoinInfo>) purchaseCoinInfoRepository.findAll();
		PurchaseInfo purchaseInfo = new PurchaseInfo();

		BigDecimal aleefRateInUsd = null;
		BigDecimal balanceCoins = null;
		Integer freeCoinPercentage = 0;
		int id = 0;
		BigDecimal purchasedCoins;
		BigDecimal referralCoins;
		BigDecimal freeCoins;

		for (PurchaseCoinInfo purchaseCoinInfo : purchaseCoinInfos) {
			if (purchaseCoinInfo.getIcoStartDate().compareTo(new Date())
					* new Date().compareTo(purchaseCoinInfo.getIcoEndDate()) > 0) {
				LOG.info("purchaseCoinInfo.getIcoStartDate() " + purchaseCoinInfo.getIcoStartDate());
				LOG.info("purchaseCoinInfo.getIcoEndDate() " + purchaseCoinInfo.getIcoEndDate());

				aleefRateInUsd = purchaseCoinInfo.getAleefRateInUSD();
				balanceCoins = purchaseCoinInfo.getBalanceCoins();
				freeCoinPercentage = purchaseCoinInfo.getFreeCoinPercentage();
				id = purchaseCoinInfo.getId();
				purchasedCoins = purchaseCoinInfo.getPurchasedCoins();
				referralCoins = purchaseCoinInfo.getReferralCoins();

				freeCoins = purchaseCoinInfo.getFreeCoins();
			}
		}

		LOG.info("aleefRateInUsd " + aleefRateInUsd);

		LOG.info("id" + id);

		PurchaseCoinInfo purchaseCoinInfo = purchaseCoinInfoRepository.findById(id);

		LOG.info("freeCoinPercentage " + freeCoinPercentage);

		BigDecimal mulUserFreeCoins = (BigDecimal.valueOf(tokenDTO.getRequestTokens())
				.multiply(new BigDecimal(freeCoinPercentage)));
		LOG.info("Multiply values in bigdesimals" + mulUserFreeCoins);
		BigDecimal userFreeCoins = mulUserFreeCoins.divide(BigDecimal.valueOf(100));
		LOG.info("User Free Coins" + userFreeCoins);

		RegisterInfo registerInfo = registerInfoRepository.findRegisterInfoByEmailId(tokenDTO.getEmailId().trim());

		RegisterInfo findLevelOneSponcerInfo;
		RegisterInfo findLevelTwoSponcerInfo;
		RegisterInfo findLevelThreeSponcerInfo;
		RegisterInfo findLevelFourSponcerInfo;

		BigDecimal mullevelOneReferralCoins;
		BigDecimal mullevelTwoReferralCoins;
		BigDecimal mullevelThreeReferralCoins;
		BigDecimal mullevelFourReferralCoins;

		BigDecimal levelOneReferralCoins = BigDecimal.valueOf(0.00);
		BigDecimal levelTwoReferralCoins = BigDecimal.valueOf(0.00);
		BigDecimal levelThreeReferralCoins = BigDecimal.valueOf(0.00);
		BigDecimal levelFourReferralCoins = BigDecimal.valueOf(0.00);

		LevelsPercentage levelsPercentage = levelPercentageRepository.findOne(1);

		LOG.info("levelsPercentage.getLevelOnePercentage() " + levelsPercentage.getLevelOne());
		LOG.info("levelsPercentage.getLevelTwoPercentages() " + levelsPercentage.getLevelTwo());
		LOG.info("levelsPercentage.getLevelThreePercentage() " + levelsPercentage.getLevelThree());
		LOG.info("levelsPercentage.getLevelFourPercentages() " + levelsPercentage.getLevelFour());

		LOG.info("registerInfo.getSponser_id() " + registerInfo.getSponser_id());
		LOG.info("Admin " + env.getProperty("admin.id"));
		LOG.info("Status " + registerInfo.getSponser_id().toString().equals(env.getProperty("admin.id").toString()));

		if (registerInfo != null) {

			if (!registerInfo.getSponser_id().toString().equals(env.getProperty("admin.id").toString())) {
				findLevelOneSponcerInfo = registerInfoRepository.findById(registerInfo.getSponser_id());
				mullevelOneReferralCoins = new BigDecimal(tokenDTO.getRequestTokens())
						.multiply(new BigDecimal(levelsPercentage.getLevelOne()));
				levelOneReferralCoins = mullevelOneReferralCoins.divide(BigDecimal.valueOf(100));
				LOG.info("findLevelOneSponcerInfo.getLevelOneBonus(): " + findLevelOneSponcerInfo.getLevelOneBonus());
				LOG.info("findLevelOneSponcerInfo.getLevelOneBonus().add(levelOneReferralCoins)"
						+ findLevelOneSponcerInfo.getLevelOneBonus().add(levelOneReferralCoins));
				findLevelOneSponcerInfo
						.setLevelOneBonus(findLevelOneSponcerInfo.getLevelOneBonus().add(levelOneReferralCoins));
				registerInfoRepository.save(findLevelOneSponcerInfo);
				if (!findLevelOneSponcerInfo.getSponser_id().toString().equals(env.getProperty("admin.id").toString())
						&& findLevelOneSponcerInfo != null) {
					findLevelTwoSponcerInfo = registerInfoRepository.findById(findLevelOneSponcerInfo.getSponser_id());

					mullevelTwoReferralCoins = new BigDecimal(tokenDTO.getRequestTokens())
							.multiply(new BigDecimal(levelsPercentage.getLevelTwo()));
					levelTwoReferralCoins = mullevelTwoReferralCoins.divide(BigDecimal.valueOf(100));
					findLevelTwoSponcerInfo
							.setLevelTwoBonus(findLevelTwoSponcerInfo.getLevelTwoBonus().add(levelTwoReferralCoins));
					registerInfoRepository.save(findLevelTwoSponcerInfo);
					if (!findLevelTwoSponcerInfo.getSponser_id().toString()
							.equals(env.getProperty("admin.id").toString()) && findLevelTwoSponcerInfo != null) {
						findLevelThreeSponcerInfo = registerInfoRepository
								.findById(findLevelTwoSponcerInfo.getSponser_id());

						mullevelThreeReferralCoins = new BigDecimal(tokenDTO.getRequestTokens())
								.multiply(new BigDecimal(levelsPercentage.getLevelThree()));
						levelThreeReferralCoins = mullevelThreeReferralCoins.divide(BigDecimal.valueOf(100));
						findLevelThreeSponcerInfo.setLevelThreeBonus(
								findLevelThreeSponcerInfo.getLevelThreeBonus().add(levelThreeReferralCoins));
						registerInfoRepository.save(findLevelThreeSponcerInfo);
						if (!findLevelThreeSponcerInfo.getSponser_id().toString()
								.equals(env.getProperty("admin.id").toString()) && findLevelThreeSponcerInfo != null) {
							findLevelFourSponcerInfo = registerInfoRepository
									.findById(findLevelThreeSponcerInfo.getSponser_id());
							mullevelFourReferralCoins = new BigDecimal(tokenDTO.getRequestTokens())
									.multiply(new BigDecimal(levelsPercentage.getLevelFour()));
							levelFourReferralCoins = mullevelFourReferralCoins.divide(BigDecimal.valueOf(100));
							findLevelFourSponcerInfo.setLevelFourBonus(
									findLevelFourSponcerInfo.getLevelFourBonus().add(levelFourReferralCoins));
							registerInfoRepository.save(findLevelFourSponcerInfo);

						}

					}
				}
			}
		}

		BigDecimal distributionCoins = new BigDecimal(tokenDTO.getRequestTokens()).add(userFreeCoins)
				.add(levelOneReferralCoins).add(levelTwoReferralCoins).add(levelThreeReferralCoins)
				.add(levelFourReferralCoins);

		LOG.info("Total Distribution Coins " + distributionCoins);

		if (distributionCoins.toBigInteger().intValue() <= balanceCoins.intValue()) {
			LOG.info("Inside distributionCoins.toBigInteger().intValue() <= balanceCoins.intValue()");
			BigDecimal referralCoins1 = levelOneReferralCoins.add(levelTwoReferralCoins).add(levelThreeReferralCoins)
					.add(levelFourReferralCoins);
			LOG.info("referralCoins1 " + referralCoins1);
			BigDecimal remainingCoins = balanceCoins.subtract(distributionCoins);
			LOG.info("RemainingCoins " + remainingCoins);
			purchaseCoinInfo.setBalanceCoins(remainingCoins);
			purchaseCoinInfo.setFreeCoins(userFreeCoins.add(purchaseCoinInfo.getFreeCoins()));
			LOG.info("Before setPurchasedCoins ");
			purchaseCoinInfo.setPurchasedCoins(
					new BigDecimal(tokenDTO.getRequestTokens()).add(purchaseCoinInfo.getPurchasedCoins()));
			LOG.info("After setPurchasedCoins ");
			purchaseCoinInfo.setReferralCoins(referralCoins1.add(purchaseCoinInfo.getReferralCoins()));
			LOG.info("setReferralCoins  ");
			double amountOfEther = (tokenDTO.getRequestTokens().intValue()
					* currentValueUtils.getEtherValuerFromCurrentAleefCoinValue(aleefRateInUsd));
			LOG.info("amountOfEther  " + amountOfEther);
			LOG.info("AmountOfEther for (" + tokenDTO.getRequestTokens().intValue() + ") Aleef Coins " + amountOfEther);
			DecimalFormat df = new DecimalFormat("#.###############");
			BigDecimal amt = new BigDecimal(df.format(amountOfEther));
			LOG.info("EtherAmount In DecimalFormat " + amt);
			LOG.info("EtherAmount In DecimalFormat InDouble " + amt.doubleValue());
			RegisterInfo registerInfos = registerInfoRepository.findRegisterInfoByEmailId(tokenDTO.getEmailId());
			LOG.info("EncryptedEtherWalletAddress " + registerInfos.getEtherWalletAddress());

			String decryptedEtherWalletAddress = EncryptDecrypt.decrypt(registerInfos.getEtherWalletAddress());
			LOG.info("DecryptedEtherWalletAddress" + decryptedEtherWalletAddress);

			String decryptedEtherWalletPassword = EncryptDecrypt.decrypt(registerInfos.getEtherWalletPassword());
			LOG.info("decryptedEtherWalletPassword" + decryptedEtherWalletPassword);

			BigDecimal walletEtherBalance = solidityHandler.checkEtherBalance(decryptedEtherWalletAddress);
			LOG.info("Before WalletEtherAmount InDouble" + walletEtherBalance.doubleValue());

			purchaseInfo.setFreeTokens(userFreeCoins);
			purchaseInfo.setEmailId(tokenDTO.getEmailId());
			purchaseInfo.setEtherAmount(amt);
			purchaseInfo.setUserName(registerInfo.getUserName());

			String[] fetchAddress = decryptedEtherWalletAddress.split("--");
			String getAddress = fetchAddress[fetchAddress.length - 1].split("\\.")[0];
			LOG.info("actualEtherWalletAddress " + getAddress);
			purchaseInfo.setEtherWalletAddress("0x" + getAddress);
			purchaseInfo.setPurchasedDate(new Date());
			purchaseInfo.setRefferalTokens(levelOneReferralCoins.add(levelTwoReferralCoins).add(levelThreeReferralCoins)
					.add(levelFourReferralCoins));
			purchaseInfo.setRequestTokens(new BigDecimal(tokenDTO.getRequestTokens()));
			purchaseInfo.setTransferStatus(0);

			if (amt.doubleValue() <= walletEtherBalance.doubleValue()) {

				LOG.info("Inside amt.doubleValue() <= walletEtherBalance.doubleValue()");
				Boolean sendFund = solidityHandler.sendFundToAdmin(decryptedEtherWalletAddress,
						decryptedEtherWalletPassword, amt);
				if (sendFund) {
					BigDecimal walletEtherBalancea = solidityHandler.checkEtherBalance(decryptedEtherWalletAddress);
					LOG.info("After WalletEtherAmount InDouble" + walletEtherBalancea.doubleValue());
					purchaseInfoRepository.save(purchaseInfo);
					purchaseCoinInfoRepository.save(purchaseCoinInfo);
					String verificationLink = "Hi " + StringUtils.trim(registerInfo.getUserName()) + "," + "<br><br>"
							+ "Thanks for purchasing " + tokenDTO.getRequestTokens()
							+ " AleefCoins. These coins will be transferred to your wallet on coin distribution (i.e) after completion of ICO."
							+ "<br><br>" + "For more details, Please review our " + "<a href='"
							+ env.getProperty("aleefcoin.landing") + "'>" + "white paper" + "</a>" + "<br><br><br>"
							+ "With Regards,<br>" + "Support Team<br>" + "Aleef Coin<br>" + "email : info@aleefcoin.io";

					boolean isEmailSent = emailNotificationServiceImpl.sendEmail(registerInfo.getEmailId().trim(),
							"Reg: Purchase Of AleefCoin", verificationLink);

				}
			}

		}
		return true;
	}
	
	
	/**
	 * It will trigger the hurry up mail to every user, two days before ending of each slab
	 * 
	 * Once the current slab(ex: slab 1) ends, then the unsold tokens will be moved to the next slab
	 *this process will applicable to each slab until ICO ends 
	 */

	@SuppressWarnings("unused")
	@Override
	public boolean UpdatePurchaseTable() throws java.text.ParseException {

		PurchaseCoinInfo slabOne = purchaseCoinInfoRepository.findPuchaseCoinInfoBySlabs("slab_one");
		PurchaseCoinInfo slabTwo = purchaseCoinInfoRepository.findPuchaseCoinInfoBySlabs("slab_two");
		PurchaseCoinInfo slabThree = purchaseCoinInfoRepository.findPuchaseCoinInfoBySlabs("slab_three");
		PurchaseCoinInfo slabFour = purchaseCoinInfoRepository.findPuchaseCoinInfoBySlabs("slab_four");
		PurchaseCoinInfo slabFive = purchaseCoinInfoRepository.findPuchaseCoinInfoBySlabs("slab_five");
		PurchaseCoinInfo slabSix = purchaseCoinInfoRepository.findPuchaseCoinInfoBySlabs("slab_six");
		PurchaseCoinInfo slabSeven = purchaseCoinInfoRepository.findPuchaseCoinInfoBySlabs("slab_seven");

		LOG.info("slabOne.getSlabs()" + slabOne.getSlabs());
		LOG.info("slabTwo.getSlabs()" + slabTwo.getSlabs());
		LOG.info("slabThree.getSlabs()" + slabThree.getSlabs());
		LOG.info("slabFour.getSlabs()" + slabFour.getSlabs());
		LOG.info("slabFive.getSlabs()" + slabFive.getSlabs());
		LOG.info("slabSix.getSlabs()" + slabSix.getSlabs());
		LOG.info("slabSeven.getSlabs()" + slabSeven.getSlabs());

		

		List<PurchaseCoinInfo> purchaseCoinInfos = (List<PurchaseCoinInfo>) purchaseCoinInfoRepository.findAll();

		for (PurchaseCoinInfo purchaseCoinInfo : purchaseCoinInfos) {
			if (purchaseCoinInfo.getIcoStartDate().compareTo(new Date())
					* new Date().compareTo(purchaseCoinInfo.getIcoEndDate()) > 0) {

				Date d = purchaseCoinInfo.getActualIcoEndDate();

				Date dateBefore = new Date(d.getTime() - 2 * 24 * 3600 * 1000l); 
				LOG.info("Before Date" + dateBefore);
				if (dateBefore.toString().trim().equals(purchaseCoinInfo.getStaticDateForMail().trim())) {

					List<RegisterInfo> registerInfos = (List<RegisterInfo>) registerInfoRepository.findAll();

					for (RegisterInfo registerInfo : registerInfos) {
						LOG.info("registerInfo.getEmailId() " + registerInfo.getEmailId());
						String verificationLink = "Hi " + StringUtils.trim(registerInfo.getUserName()) + ","
								+ "<br><br>"
								+ "Hurry Up! Aleef Coin is offering coins at very best price. Don't miss this opportunity."
								+ "<br><br>" + "Aleef Coin Price : USD $"
								+ StringUtils.trim(purchaseCoinInfo.getAleefRateInUSD().toString()) + "<br>"
								+ "Free % of coins : "
								+ StringUtils.trim(purchaseCoinInfo.getFreeCoinPercentage().toString()) + " %" + "<br>"
								+ "Offer valid untill : "
								+ StringUtils.trim(purchaseCoinInfo.getIcoEndDateForMail().toString()) + "<br><p>"
								+ "Click" + "<a href='" + env.getProperty("login.page") + "'>" + " here " + "</a>"
								+ "to sign-in" + "<p><br><br><br>" + "With Regards,<br><br>" + "Support Team<br>"
								+ "Aleef Coin<br>" + "email : info@aleefcoin.io";

						boolean isEmailSent = emailNotificationServiceImpl.sendEmail(registerInfo.getEmailId(),
								"Aleef Coin ICO calendar", verificationLink);

					}
				}
			}
		}

		BigDecimal slabOneBalanceTokens;

		if (new Date().after(slabOne.getIcoEndDate())) {
			LOG.info(" Inside Slab_One ");
			slabOneBalanceTokens = slabOne.getBalanceCoins();
			slabOne.setBalanceCoins(new BigDecimal(0));
			slabTwo.setBalanceCoins(slabTwo.getBalanceCoins().add(slabOneBalanceTokens));

			purchaseCoinInfoRepository.save(slabOne);
			purchaseCoinInfoRepository.save(slabTwo);

		}

		BigDecimal slabTwoBalanceTokens;

		if (new Date().after(slabTwo.getIcoEndDate())) {
			LOG.info(" Inside Slab_Two ");
			slabTwoBalanceTokens = slabTwo.getBalanceCoins();
			slabTwo.setBalanceCoins(new BigDecimal(0));
			slabThree.setBalanceCoins(slabThree.getBalanceCoins().add(slabTwoBalanceTokens));

			purchaseCoinInfoRepository.save(slabTwo);
			purchaseCoinInfoRepository.save(slabThree);
		}

		BigDecimal slabThreeBalanceTokens;

		if (new Date().after(slabThree.getIcoEndDate())) {
			LOG.info(" Inside Slab_Three ");
			slabThreeBalanceTokens = slabThree.getBalanceCoins();
			slabThree.setBalanceCoins(new BigDecimal(0));
			slabFour.setBalanceCoins(slabFour.getBalanceCoins().add(slabThreeBalanceTokens));

			purchaseCoinInfoRepository.save(slabThree);
			purchaseCoinInfoRepository.save(slabFour);

		}

		BigDecimal slabFourBalanceTokens;

		if (new Date().after(slabFour.getIcoEndDate())) {
			LOG.info(" Inside Slab_Four ");
			slabFourBalanceTokens = slabFour.getBalanceCoins();
			slabFour.setBalanceCoins(new BigDecimal(0));
			slabFive.setBalanceCoins(slabFive.getBalanceCoins().add(slabFourBalanceTokens));

			purchaseCoinInfoRepository.save(slabFour);
			purchaseCoinInfoRepository.save(slabFive);

		}

		BigDecimal slabFiveBalanceTokens;

		if (new Date().after(slabFive.getIcoEndDate())) {
			LOG.info(" Inside Slab_Five ");
			slabFiveBalanceTokens = slabFive.getBalanceCoins();
			slabFive.setBalanceCoins(new BigDecimal(0));
			slabSix.setBalanceCoins(slabSix.getBalanceCoins().add(slabFiveBalanceTokens));

			purchaseCoinInfoRepository.save(slabFive);
			purchaseCoinInfoRepository.save(slabSix);

		}

		BigDecimal slabSixBalanceTokens;

		if (new Date().after(slabSix.getIcoEndDate())) {
			LOG.info(" Inside Slab_Six ");
			slabSixBalanceTokens = slabSix.getBalanceCoins();
			slabSix.setBalanceCoins(new BigDecimal(0));
			slabSeven.setBalanceCoins(slabSeven.getBalanceCoins().add(slabSixBalanceTokens));

			purchaseCoinInfoRepository.save(slabSix);
			purchaseCoinInfoRepository.save(slabSeven);

		}

		BigDecimal slabSevenBalanceTokens;

		if (new Date().after(slabSeven.getIcoEndDate())) {
			LOG.info(" Inside Slab_Seven ");

			TokenInfo tokenInfo = tokenInfoRepository.findOne(1);

			slabSevenBalanceTokens = slabSeven.getBalanceCoins();
			slabSeven.setBalanceCoins(new BigDecimal(0));
			tokenInfo.setTotalInternalTokens(slabSevenBalanceTokens.add(tokenInfo.getTotalInternalTokens()));
			purchaseCoinInfoRepository.save(slabSeven);
			tokenInfoRepository.save(tokenInfo);

		}
		return true;
	}

	@Override
	public List<TokenDTO> PurchaseTokensList(TokenDTO tokenDTO, String etherWalletAddress) {

		List<TokenDTO> list = solidityHandler.listPurchaseTokens(tokenDTO, etherWalletAddress);

		return list;
	}

	@Override
	public List<TokenDTO> filterModes(TokenDTO tokenDTO) throws Exception {

		List<TokenDTO> filter = solidityHandler.filterMode(tokenDTO);

		return filter;
	}

	@Override
	public List<TokenDTO> filterAddress(String etherWalletAddress) {

		List<TokenDTO> filter = solidityHandler.filterAddress(etherWalletAddress);

		return filter;
	}

	@Override
	public boolean validAmount(TokenDTO tokenDTO) {

		List<PurchaseCoinInfo> purchaseCoinInfos = (List<PurchaseCoinInfo>) purchaseCoinInfoRepository.findAll();
		

		for (PurchaseCoinInfo purchaseCoinInfo : purchaseCoinInfos) {
			LOG.info("Purchase Start Date : " + purchaseCoinInfo.getIcoStartDate());
			LOG.info("Purchase End Date : " + purchaseCoinInfo.getIcoEndDate());
			if (purchaseCoinInfo.getIcoStartDate().compareTo(new Date())
					* new Date().compareTo(purchaseCoinInfo.getIcoEndDate()) > 0) {
				BigDecimal balancedTokens = purchaseCoinInfo.getBalanceCoins();
				if (tokenDTO.getRequestTokens() <= balancedTokens.doubleValue()) {
					return true;
				}
			}
		}

		return false;
	}
	
	/**
	 * 
	 * Requesting the Aleef coins from mobile wallet
	 */

	@SuppressWarnings("static-access")
	@Override
	public boolean requestTokens(TokenDTO tokenDTO)
			throws FileNotFoundException, IOException, ParseException, Exception {

		HttpSession session = SessionCollector.find(tokenDTO.getSessionId());
		LOG.info("Session ID " + tokenDTO.getSessionId());

		String mail = (String) session.getAttribute("emailId");
		RegisterInfo registerInfo = registerInfoRepository.findRegisterInfoByEmailId(mail);

		ConfigInfo configInfo = configInfoRepository.findConfigInfoByConfigKey("walletFile");

		String fromAddress = userUtils.getEtherWalletAddress(configInfo.getConfigValue(),
				EncryptDecrypt.decrypt(registerInfo.getEtherWalletAddress()));

		System.out
				.println("configInfo.getConfigValue() + EncryptDecrypt.decrypt(registerInfo.getEtherWalletAddress())");

		RequestTokensInfo requestTokensInfo = new RequestTokensInfo();

		requestTokensInfo.setFromAddress(tokenDTO.getToAddress());
		requestTokensInfo.setToAddress(fromAddress);
		requestTokensInfo.setEmailId(mail);
		requestTokensInfo.setUserId(registerInfo.getId());
		requestTokensInfo.setTokenAmount(tokenDTO.getAmount());
		requestTokensInfo.setStatus("pending");
		requestTokensInfoRepository.save(requestTokensInfo);

		RegisterInfo registerInfo1 = registerInfoRepository.findByWalletAddress(tokenDTO.getToAddress().trim());

		String appId = registerInfo1.getAppId();
		LOG.info(appId);
		if (appId.length() >= 1) {
			String server_key = env.getProperty("server_key").trim();

			String message = registerInfo.getUserName() + " Has Requested " + tokenDTO.getAmount() + " " + "From You";

			JSONObject infoJson = new JSONObject();

			infoJson.put("title", "Aleef Coin Request Notification");

			infoJson.put("body", message);

			infoJson.put("id", tokenDTO.getId());

			String deviceType = registerInfo1.getDeviceType();
			LOG.info(registerInfo1.getDeviceType());

			fcmUtils.send_FCM_Notification(appId, server_key, message, deviceType, infoJson);

		}

		return true;
	}
	
	
	/**
	 * 
	 * Listing the all purchase  request for mobile
	 */

	@Override
	public List<TokenDTO> requestTokensList(TokenDTO tokenDTO)
			throws FileNotFoundException, IOException, ParseException, Exception {
		HttpSession session = SessionCollector.find(tokenDTO.getSessionId());
		LOG.info("Session ID " + tokenDTO.getSessionId());

		String mail = (String) session.getAttribute("emailId");
		RegisterInfo registerInfo = registerInfoRepository.findRegisterInfoByEmailId(mail);

		ConfigInfo configInfo = configInfoRepository.findConfigInfoByConfigKey("walletFile");

		String fromAddress = userUtils.getEtherWalletAddress(configInfo.getConfigValue(),
				EncryptDecrypt.decrypt(registerInfo.getEtherWalletAddress()));

		System.out
				.println("configInfo.getConfigValue() + EncryptDecrypt.decrypt(registerInfo.getEtherWalletAddress())");

		List<TokenDTO> requestList = new ArrayList<TokenDTO>();
		List<RequestTokensInfo> requestTokensInfo = requestTokensInfoRepository.findByFromAddress(fromAddress);

		if (requestTokensInfo != null) {

			for (RequestTokensInfo request : requestTokensInfo) {

				if (request.getStatus().equalsIgnoreCase("pending")) {

					LOG.info("request.getFromAddress(): " + request.getTokenAmount());
					LOG.info("request.getToAddress(): " + request.getFromAddress());
					LOG.info("request.getStatus().toString()::::::" + request.getStatus());
					TokenDTO tokenDTO2 = new TokenDTO();
					tokenDTO2.setFromAddress(request.getFromAddress());
					tokenDTO2.setToAddress(request.getToAddress());
					tokenDTO2.setAmount(request.getTokenAmount());
					tokenDTO2.setStatus(request.getStatus().toString());
					tokenDTO2.setId(request.getId());
					
					requestList.add(tokenDTO2);
				}
			}

		}
		return requestList;
	}
	
	
	/**
	 * 
	 * Transfer aleef tokens to the mobile wallet
	 */

	@SuppressWarnings("static-access")
	@Override
	public boolean requestTokensTransfer(TokenDTO tokenDTO)
			throws FileNotFoundException, IOException, ParseException, Exception {

		HttpSession session = SessionCollector.find(tokenDTO.getSessionId());
		LOG.info("Session ID " + tokenDTO.getSessionId());

		String mail = (String) session.getAttribute("emailId");
		RegisterInfo registerInfo = registerInfoRepository.findRegisterInfoByEmailId(mail);

		ConfigInfo configInfo = configInfoRepository.findConfigInfoByConfigKey("walletFile");

		String fromAddress = userUtils.getEtherWalletAddress(configInfo.getConfigValue(),
				EncryptDecrypt.decrypt(registerInfo.getEtherWalletAddress()));

		LOG.info("configInfo.getConfigValue() + EncryptDecrypt.decrypt(registerInfo.getEtherWalletAddress()) :"
				+ fromAddress);
		LOG.info("tokenDTO.getStatus(): " + tokenDTO.getStatus());
		RequestTokensInfo requestTokensInfo = requestTokensInfoRepository.findById(tokenDTO.getId());
		if (tokenDTO.getStatus().equalsIgnoreCase("pending")) {
			LOG.info("tokenDTO.getStatus(): " + tokenDTO.getStatus() + ": " + requestTokensInfo.getStatus());
			String request = transferToken(tokenDTO);
			if (request != null) {

				requestTokensInfo.setStatus("success");
				requestTokensInfoRepository.save(requestTokensInfo);

				RegisterInfo registerInfo1 = registerInfoRepository.findByWalletAddress(tokenDTO.getToAddress().trim());

				String appId = registerInfo1.getAppId();
				LOG.info(appId);
				if (appId.length() >= 1) {
					String server_key = env.getProperty("server_key").trim();

					String message = registerInfo.getUserName() + " has transfered " + tokenDTO.getAmount() + " "
							+ "for you";

					JSONObject infoJson = new JSONObject();

					infoJson.put("title", "Aleef Send Coin Notification");

					infoJson.put("body", message);

					infoJson.put("id", tokenDTO.getId());

					String deviceType = registerInfo1.getDeviceType();
					LOG.info(registerInfo1.getDeviceType());

					fcmUtils.send_FCM_Notification(appId, server_key, message, deviceType, infoJson);
				}
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean validMinToken(TokenDTO tokenDTO) {
		if (tokenDTO.getRequestTokens() >= 1) {

			return true;
		}
		return false;
	}

	@Override
	public List<TokenDTO> purchaseListFilter(TokenDTO tokenDTO) throws Exception {

		List<TokenDTO> filter = solidityHandler.purchaseListFilter(tokenDTO);

		return filter;
	}

}
