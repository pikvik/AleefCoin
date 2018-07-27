package com.aleef.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.aleef.dtos.PurchaseCoinDTO;

import com.aleef.models.PurchaseCoinInfo;

import com.aleef.repo.PurchaseCoinInfoRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import com.aleef.dtos.LoginDTO;
import com.aleef.dtos.RegisterDTO;
import com.aleef.models.ConfigInfo;
import com.aleef.models.ExpirationDataInfo;
import com.aleef.models.QRcodeInfo;
import com.aleef.models.RegisterInfo;
import com.aleef.models.SecuredInfo;
import com.aleef.repo.ConfigInfoRepository;
import com.aleef.repo.ExpirationDataInfoRepository;
import com.aleef.repo.QRCodeInfoRepository;
import com.aleef.repo.RegisterInfoRepository;
import com.aleef.repo.SecuredInfoRepository;
import com.aleef.services.UserRegisterService;
import com.aleef.session.SessionCollector;
import com.aleef.utils.EncryptDecrypt;
import com.aleef.utils.QR_Code;
import com.aleef.utils.UserUtils;
import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.aleef.models.LevelsPercentage;
import com.aleef.models.PurchaseInfo;

import com.aleef.repo.LevelPercentageRepository;
import com.aleef.repo.PurchaseInfoRepository;
import com.aleef.solidityHandler.SolidityHandler;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

import com.aleef.dtos.KycDTO;
import com.aleef.dtos.TokenDTO;
import com.aleef.models.KycInfo;
import com.aleef.repo.KycInfoRepo;
import com.google.common.io.Files;

@Service
public class UserRegisterServiceImpl implements UserRegisterService {

	private final Web3j web3j = Web3j.build(new HttpService("https://mainnet.infura.io"));

	static final Logger LOG = LoggerFactory.getLogger(UserRegisterServiceImpl.class);

	@Autowired
	private Environment env;

	@Autowired
	private RegisterInfoRepository registerInfoRepository;

	@Autowired
	private ConfigInfoRepository configInfoRepository;

	@Autowired
	private PurchaseCoinInfoRepository purchaseCoinInfoRepository;

	@Autowired
	private KycInfoRepo kycInfoRepo;

	@Autowired
	private LevelPercentageRepository levelPercentageRepository;

	@Autowired
	private PurchaseInfoRepository purchaseInfoRepository;

	@Autowired
	private SolidityHandler solidityHandler;

	@Autowired
	private QRCodeInfoRepository qrCodeInfoRepository;

	@Autowired
	private UserUtils userUtils;

	@Autowired
	private SessionCollector sessionCollector;

	@Autowired
	private SecuredInfoRepository securedInfoRepository;

	@Autowired
	private EmailNotificationServiceImpl emailNotificationServiceImpl;

	@Autowired
	private ExpirationDataInfoRepository expirationDataInfoRepository;

	@Override
	public boolean isAccountExistCheckByEmailId(String emailId) {

		Integer isEmailExit = registerInfoRepository.countRegisterInfoByEmailIdIgnoreCase(emailId);
		if (isEmailExit > 0) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isAccountExistCheckByMobileNo(String mobileNo) {

		Integer isMobileExit = registerInfoRepository.countRegisterInfoByMobileNo(mobileNo);
		if (isMobileExit > 0) {
			return true;
		}
		return false;
	}

	// Creation of ether wallet address for users and admin

	@Override
	public boolean isEtherWalletCreated(RegisterDTO registerDTO) {

		ConfigInfo configInfo = configInfoRepository.findConfigInfoByConfigKey("WalletFile");
		LOG.info("Config Info Values : " + configInfo.getConfigValue());

		try {
			String fileName = WalletUtils.generateNewWalletFile(registerDTO.getEtherWalletPassword(),
					new File(configInfo.getConfigValue()), false);

			LOG.info("fileName : " + fileName);

			if (fileName != null) {
				String encryptedEtherWalletAddress = EncryptDecrypt.encrypt(fileName);
				String encryptedEtherWalletPassword = EncryptDecrypt.encrypt(registerDTO.getEtherWalletPassword());
				registerDTO.setEtherWalletAddress(encryptedEtherWalletAddress);
				registerDTO.setEtherWalletPassword(encryptedEtherWalletPassword);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

	// Saving the users registration info's and updating the existing referrals
	

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public String saveRegisterInfo(RegisterDTO registerDTO, String encryptedPassword) throws Exception {
		RegisterInfo registerInfo = new RegisterInfo();
		ConfigInfo configInfo = configInfoRepository.findConfigInfoByConfigKey("WalletFile");

		if (registerDTO.getSponser_id() == null) {

			registerInfo.setSponser_id(1);
			registerInfo.setUserName(registerDTO.getUserName());
			registerInfo.setEmailId(registerDTO.getEmailId());
			registerInfo.setPassword(encryptedPassword);
			registerInfo.setMobileNo(registerDTO.getMobileNo());
			registerInfo.setRoleId(2);
			registerInfo.setKycStatus(0);
			registerInfo.setActivation(false);
			registerInfo.setMediaId("null");
			registerInfo.setBurnTokens(0.0);
			registerInfo.setEtherWalletAddress(registerDTO.getEtherWalletAddress());

			String walletAddress = EncryptDecrypt.decrypt(registerDTO.getEtherWalletAddress());
			String address = userUtils.getEtherWalletAddress(configInfo.getConfigValue(), walletAddress);

			registerInfo.setWalletAddress(address);
			registerInfo.setEtherWalletPassword(registerDTO.getEtherWalletPassword());
			registerInfo.setCreatedDate(new Date());
			registerInfo.setLevelOne(0);
			registerInfo.setLevelTwo(0);
			registerInfo.setLevelThree(0);
			registerInfo.setLevelFour(0);
			registerInfo.setKycStatus(0);
			registerInfo.setLevelOneBonus(new BigDecimal(0.00));
			registerInfo.setLevelTwoBonus(new BigDecimal(0.00));
			registerInfo.setLevelThreeBonus(new BigDecimal(0.00));
			registerInfo.setLevelFourBonus(new BigDecimal(0.00));
			registerInfo.setAppId("");
			registerInfo.setDeviceType("");

			GoogleAuthenticator gAuth = new GoogleAuthenticator();
			final GoogleAuthenticatorKey key = gAuth.createCredentials();
			String secretKey = key.getKey();

			String code = Integer.toString(gAuth.getTotpPassword(secretKey));
			registerDTO.setSecurityKey(code);
			SecuredInfo securedInfo = securedInfoRepository.findSecuredInfoByEmailId(registerDTO.getEmailId().trim());
			if (securedInfo != null) {
				securedInfo.setEmailId(registerDTO.getEmailId().trim());
				securedInfo.setDate(new Date());
				securedInfo.setSecuredKey(code);
				securedInfoRepository.save(securedInfo);
			} else {
				SecuredInfo securedInfos = new SecuredInfo();
				securedInfos.setEmailId(registerDTO.getEmailId().trim());
				securedInfos.setDate(new Date());
				securedInfos.setSecuredKey(code);
				securedInfoRepository.save(securedInfos);
			}

			registerInfoRepository.save(registerInfo);
		} else {

			String sponser = EncryptDecrypt.decrypt(registerDTO.getSponser_id().toString().replaceAll("\\s", "+"));
			LOG.info(sponser + " : ID");
			Integer i = Integer.parseInt(sponser);

			registerInfo.setSponser_id(i);
			registerInfo.setUserName(registerDTO.getUserName());
			registerInfo.setEmailId(registerDTO.getEmailId());
			registerInfo.setPassword(encryptedPassword);
			registerInfo.setMobileNo(registerDTO.getMobileNo());
			registerInfo.setRoleId(2);
			registerInfo.setActivation(false);
			registerInfo.setMediaId("null");
			registerInfo.setBurnTokens(0.0);
			registerInfo.setEtherWalletAddress(registerDTO.getEtherWalletAddress());

			String walletAddress = EncryptDecrypt.decrypt(registerDTO.getEtherWalletAddress());
			String address = userUtils.getEtherWalletAddress(configInfo.getConfigValue(), walletAddress);

			registerInfo.setWalletAddress(address);

			registerInfo.setEtherWalletPassword(registerDTO.getEtherWalletPassword());
			registerInfo.setCreatedDate(new Date());
			registerInfo.setLevelOne(0);
			registerInfo.setLevelTwo(0);
			registerInfo.setLevelThree(0);
			registerInfo.setLevelFour(0);
			registerInfo.setKycStatus(0);
			registerInfo.setLevelOneBonus(new BigDecimal(0.00));
			registerInfo.setLevelTwoBonus(new BigDecimal(0.00));
			registerInfo.setLevelThreeBonus(new BigDecimal(0.00));
			registerInfo.setLevelFourBonus(new BigDecimal(0.00));
			registerInfo.setAppId("");
			registerInfo.setDeviceType("");

			GoogleAuthenticator gAuth = new GoogleAuthenticator();
			final GoogleAuthenticatorKey key = gAuth.createCredentials();
			String secretKey = key.getKey();

			String code = Integer.toString(gAuth.getTotpPassword(secretKey));

			SecuredInfo securedInfo = securedInfoRepository.findSecuredInfoByEmailId(registerDTO.getEmailId().trim());
			if (securedInfo != null) {
				securedInfo.setEmailId(registerDTO.getEmailId().trim());
				securedInfo.setDate(new Date());
				securedInfo.setSecuredKey(code);
				securedInfoRepository.save(securedInfo);
			} else {
				SecuredInfo securedInfos = new SecuredInfo();
				securedInfos.setEmailId(registerDTO.getEmailId().trim());
				securedInfos.setDate(new Date());
				securedInfos.setSecuredKey(code);
				securedInfoRepository.save(securedInfos);
			}
			registerDTO.setSecurityKey(code);

			if (registerDTO.getSponser_id().equals(env.getProperty("admin.id"))) {
				LOG.info("registerDTO.getSponser_id()" + registerDTO.getSponser_id());
				RegisterInfo registerInfoOne = registerInfoRepository.findById(i);
				registerInfoOne.setLevelOne(1);
				registerInfoRepository.save(registerInfoOne);

				if (registerInfoOne.getSponser_id().equals(env.getProperty("admin.id"))) {
					LOG.info("registerInfoOne.getSponser_id() " + registerInfoOne.getSponser_id());
					RegisterInfo registerInfoTwo = registerInfoRepository.findById(registerInfoOne.getSponser_id());
					registerInfoTwo.setLevelTwo(1);
					registerInfoRepository.save(registerInfoTwo);

					if (registerInfoTwo.getSponser_id().equals(env.getProperty("admin.id"))) {
						LOG.info("registerInfoTwo.getSponser_id() " + registerInfoTwo.getSponser_id());
						RegisterInfo registerInfoThree = registerInfoRepository
								.findById(registerInfoTwo.getSponser_id());
						registerInfoThree.setLevelThree(1);
						registerInfoRepository.save(registerInfoThree);

						if (registerInfoThree.getSponser_id().equals(env.getProperty("admin.id"))) {
							LOG.info("registerInfoThree.getSponser_id() " + registerInfoThree.getSponser_id());
							RegisterInfo registerInfoFour = registerInfoRepository
									.findById(registerInfoThree.getSponser_id());
							registerInfoFour.setLevelFour(1);
							registerInfoRepository.save(registerInfoFour);
						}
					}
				}
			}

			registerInfoRepository.save(registerInfo);
		}

		int id = registerInfo.getId();
		String dynamicQRFolder = Integer.toString(id);
		QRcodeInfo qrcode = qrCodeInfoRepository.findQRcodeByQrKey("QRKey");
		String qrFileLocation = null;
		if (qrcode != null) {
			qrFileLocation = qrcode.getQrvalue();
			File createfolder = new File(qrFileLocation.concat(dynamicQRFolder));
			if (!createfolder.exists()) {
				createfolder.mkdir();
				qrFileLocation = createfolder.getPath().replace("//", "/");
				qrFileLocation = qrFileLocation.concat("/");
			}
		}

		String DecryptedWalletAddress = EncryptDecrypt.decrypt(registerDTO.getEtherWalletAddress());
		LOG.info("Wallet Address :: Encryption Details :: " + registerDTO.getEtherWalletAddress());

		String WalletAddress = userUtils.getEtherWalletAddress(configInfo.getConfigValue(), DecryptedWalletAddress);
		LOG.info("Wallet Address :: Encryption Details:: " + WalletAddress);
		String qrCodeData = WalletAddress;
		String filePath = qrFileLocation + id + ".png";
		String charset = "UTF-8"; // or "ISO-8859-1"
		Map hintMap = new HashMap();
		hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
		QR_Code.createQRCode(qrCodeData, filePath, charset, hintMap, 200, 200);
		LOG.info("QR Code image created successfully!");
		return null;
	}
	
	//Check the given mail id and password exist or not in aleef portal

	@Override
	public LoginDTO isEmailAndPasswordExit(RegisterDTO registerDTO, HttpServletRequest request) throws Exception {

		LoginDTO responseDTO = new LoginDTO();
		ConfigInfo configInfo = configInfoRepository.findConfigInfoByConfigKey("WalletFile");
		RegisterInfo registerInfo = registerInfoRepository.findRegisterInfoByEmailId(registerDTO.getEmailId().trim());
		LOG.info("Email Id : " + registerInfo);
		String password = registerInfo.getPassword();
		LOG.info("Password : " + password);

		String decryptPassword = EncryptDecrypt.decrypt(password);
		LOG.info("decryptPassword : " + decryptPassword);
		if (registerDTO.getPassword().equals(decryptPassword)) {
			if (registerInfo.getRoleId() == 2 && registerDTO.getAppId() == null) {
				LOG.info("appId : null");
				if (registerInfo != null) {
					responseDTO.setEmailId(registerInfo.getEmailId());
					responseDTO.setRoleId(registerInfo.getRoleId());
					responseDTO.setStatus("success");
					return responseDTO;
				}
			} else if (registerInfo.getRoleId() == 2) {
				if (registerInfo != null) {
					responseDTO.setEmailId(registerInfo.getEmailId());
					responseDTO.setRoleId(registerInfo.getRoleId());
					responseDTO.setStatus("success");

					registerInfo.setAppId(registerDTO.getAppId());
					registerInfo.setDeviceType(registerDTO.getDeviceType());
					registerInfoRepository.save(registerInfo);
					return responseDTO;
				}
			} else {

				if (registerInfo.getRoleId() == 1) {

					QRcodeInfo qrCode = qrCodeInfoRepository.findQRcodeByQrKey("QRKey");
					String qrCodePath = qrCode.getQrvalue();
					qrCodePath = env.getProperty("qrcode.url") + "//" + registerInfo.getId() + "//"
							+ registerInfo.getId() + ".png";

					responseDTO.setUserName(registerInfo.getUserName().trim());
					responseDTO.setEmailId(registerInfo.getEmailId().trim());

					String decryptEtherWalletAddress;
					try {
						decryptEtherWalletAddress = EncryptDecrypt.decrypt(registerInfo.getEtherWalletAddress());
						LOG.info("decryptWalletAddress : " + decryptEtherWalletAddress);
						String etherWalletAddress = userUtils.getEtherWalletAddress(configInfo.getConfigValue(),
								decryptEtherWalletAddress);
						if (etherWalletAddress != null) {
							responseDTO.setEtherWalletAddress(etherWalletAddress);
						}
						responseDTO.setRoleId(registerInfo.getRoleId());
						responseDTO.setMobileNo(registerInfo.getMobileNo());
						responseDTO.setStatus("Success");
						responseDTO.setQrCode(qrCodePath);
						responseDTO.setKycStatus(registerInfo.getKycStatus());

						HttpSession session = request.getSession(true);
						LOG.info("Getting Session Id : " + session.getId());
						session.setAttribute("id", registerInfo.getId());
						session.setAttribute("emailId", registerInfo.getEmailId());
						HttpSessionEvent event = new HttpSessionEvent(session);
						sessionCollector.sessionCreated(event);
						responseDTO.setSessionId(session.getId());

						Integer countUsers2 = registerInfoRepository.countRegisterInfoByRoleId(2);
						LOG.info("Count Users : " + countUsers2);

						Integer countUsers3 = registerInfoRepository.countRegisterInfoByRoleId(3);
						LOG.info("Count Users : " + countUsers3);

						Integer totalusers = countUsers2 + countUsers3;
						responseDTO.setUsersCount(totalusers);

					} catch (Exception e) {

						e.printStackTrace();
					}
					return responseDTO;
				}
			}
		}
		responseDTO.setStatus("failed");
		return responseDTO;
	}

	// Login securely by using secret pin(2FA login)

	@Override
	public LoginDTO secureLogin(RegisterDTO registerDTO, HttpServletRequest request) throws Exception {
		LoginDTO responseDTO = new LoginDTO();
		SecuredInfo securedInfo = securedInfoRepository.findSecuredInfoByEmailId(registerDTO.getEmailId());
		LOG.info("Secure Login " + registerDTO.getEmailId());
		LOG.info("securedInfo EmailId : " + securedInfo);
		if (securedInfo != null) {

			if (registerDTO.getSecurityKey().equals(securedInfo.getSecuredKey())) {

				RegisterInfo registerInfo = registerInfoRepository
						.findRegisterInfoByEmailId(registerDTO.getEmailId().trim());
				ConfigInfo configInfo = configInfoRepository.findConfigInfoByConfigKey("WalletFile");
				if (registerInfo.getRoleId() == 2) {

					QRcodeInfo qrCode = qrCodeInfoRepository.findQRcodeByQrKey("QRKey");
					String qrCodePath = qrCode.getQrvalue();
					qrCodePath = env.getProperty("qrcode.url") + "//" + registerInfo.getId() + "//"
							+ registerInfo.getId() + ".png";

					responseDTO.setUserName(registerInfo.getUserName().trim());
					responseDTO.setEmailId(registerInfo.getEmailId().trim());

					String decryptEtherWalletAddress = EncryptDecrypt.decrypt(registerInfo.getEtherWalletAddress());
					LOG.info("decryptWalletAddress : " + decryptEtherWalletAddress);
					String etherWalletAddress = userUtils.getEtherWalletAddress(configInfo.getConfigValue(),
							decryptEtherWalletAddress);
					if (etherWalletAddress != null) {
						responseDTO.setEtherWalletAddress(etherWalletAddress);
					}
					responseDTO.setRoleId(registerInfo.getRoleId());
					responseDTO.setMobileNo(registerInfo.getMobileNo());
					responseDTO.setStatus("Success");
					responseDTO.setQrCode(qrCodePath);
					responseDTO.setKycStatus(registerInfo.getKycStatus());
					responseDTO.setReferralLink(
							env.getProperty("referral.url") + EncryptDecrypt.encrypt(registerInfo.getId().toString()));

					HttpSession session = request.getSession(true);
					LOG.info("Getting Session Id : " + session.getId());
					session.setAttribute("id", registerInfo.getId());
					session.setAttribute("emailId", registerInfo.getEmailId());
					HttpSessionEvent event = new HttpSessionEvent(session);
					sessionCollector.sessionCreated(event);
					responseDTO.setSessionId(session.getId());
					return responseDTO;
				}
			}
		}
		responseDTO.setStatus("failed");
		return responseDTO;
	}

	@Override
	public boolean isOldPassword(RegisterDTO registerDTO) throws Exception {

		HttpSession session = SessionCollector.find(registerDTO.getSessionId());

		String email = (String) session.getAttribute("emailId");
		LOG.info("Email : " + email);
		RegisterInfo registerInfo = registerInfoRepository.findRegisterInfoByEmailId(email);

		if (registerInfo != null) {

			String oldpassword = registerInfo.getPassword();
			String decryptPassword = EncryptDecrypt.decrypt(oldpassword);
			LOG.info("decryptPassword :: decryptPassword :: " + decryptPassword);
			if (decryptPassword.equals(registerDTO.getOldPassword())) {
				return true;
			}
		}
		return false;
	}

	// Change the password

	@Override
	public boolean isChangePassword(RegisterDTO registerDTO) throws Exception {
		HttpSession session = SessionCollector.find(registerDTO.getSessionId());
		String email = (String) session.getAttribute("emailId");
		LOG.info("Email : " + email);
		RegisterInfo UserModelInfo = registerInfoRepository.findRegisterInfoByEmailId(email);

		String changePassword = registerDTO.getPassword();
		LOG.info("pserd : " + changePassword);
		String confirmChangePassword = registerDTO.getConfirmPassword();
		LOG.info("pserd : " + confirmChangePassword);

		if (changePassword.equals(confirmChangePassword)) {
			try {
				String encryptPassword = EncryptDecrypt.encrypt(changePassword);
				LOG.info("Changable Password : " + encryptPassword);
				if (encryptPassword != null) {
					UserModelInfo.setPassword(encryptPassword);
					registerInfoRepository.save(UserModelInfo);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}

	// Triggering a recovery mail for the forgot password

	@SuppressWarnings("deprecation")
	@Override
	public boolean isSendEmail(RegisterDTO registerDTO) {

		RegisterInfo registerInfo = registerInfoRepository.findRegisterInfoByEmailId(registerDTO.getEmailId());
		if (registerInfo != null) {
			try {

				String encryptedEmail = EncryptDecrypt.encrypt(registerDTO.getEmailId());

				ExpirationDataInfo expirationDataInfo = new ExpirationDataInfo();

				String token = UUID.randomUUID().toString();
				LOG.info("token : " + token);
				expirationDataInfo.setToken(token);
				Calendar cal = Calendar.getInstance();
				DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
				cal.add(Calendar.HOUR_OF_DAY, 1);
				Date expiredDate = new Date(dateFormat.format(cal.getTime()));
				LOG.info("expiredDate : " + expiredDate);
				expirationDataInfo.setExpiredDate(expiredDate);
				expirationDataInfo.setTokenStatus(false);
				expirationDataInfo.setEmailId(registerDTO.getEmailId());
				expirationDataInfoRepository.save(expirationDataInfo);

				String encryptedToken = EncryptDecrypt.encrypt(token);

				String verificationLink = "Hi," + "<br><br>" + env.getProperty("email.forgot.password") + "<br><br>"
						+ "<a href='" + env.getProperty("reset.url") + encryptedEmail + env.getProperty("reset.url2")
						+ encryptedToken + "'>" + env.getProperty("reset.user.portal.url") + "</a>" + "<br><br>" + ""
						+ "With Regards,<br><br>" + "Support Team<br>" + "Aleef Coin<br>" + "email : info@aleefcoin.io";

				boolean isEmailSent = emailNotificationServiceImpl.sendEmail(registerDTO.getEmailId(),
						"Aleef Registration Team", verificationLink);

				if (isEmailSent) {
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return false;
	}

	// While social media login, it will check the current account is existed or
	// not

	@Override
	public LoginDTO isAccountExistCheckByEmailIdForSocialMediaLogin(RegisterDTO registerDTO, HttpServletRequest request)
			throws Exception {

		LoginDTO responseDTO = new LoginDTO();
		RegisterInfo registerInfo = registerInfoRepository.findRegisterInfoByEmailId(registerDTO.getEmailId().trim());

		if (registerInfo.getRoleId() == 3) {
			ConfigInfo configInfo = configInfoRepository.findConfigInfoByConfigKey("WalletFile");

			QRcodeInfo qrCode = qrCodeInfoRepository.findQRcodeByQrKey("QRKey");
			String qrCodePath = qrCode.getQrvalue();
			qrCodePath = env.getProperty("qrcode.url") + "//" + registerInfo.getId() + "//" + registerInfo.getId()
					+ ".png";

			responseDTO.setUserName(registerInfo.getUserName().trim());
			responseDTO.setEmailId(registerInfo.getEmailId().trim());

			String decryptEtherWalletAddress = EncryptDecrypt.decrypt(registerInfo.getEtherWalletAddress());
			LOG.info("decryptWalletAddress : " + decryptEtherWalletAddress);
			String etherWalletAddress = userUtils.getEtherWalletAddress(configInfo.getConfigValue(),
					decryptEtherWalletAddress);
			if (etherWalletAddress != null) {
				responseDTO.setEtherWalletAddress(etherWalletAddress);
			}
			responseDTO.setRoleId(registerInfo.getRoleId());
			responseDTO.setStatus("Success");
			registerDTO.setPopUpStatus("false");
			responseDTO.setPopUpStatus("false");
			responseDTO.setQrCode(qrCodePath);
			responseDTO.setMediaId(registerInfo.getMediaId());
			responseDTO.setKycStatus(registerInfo.getKycStatus());
			responseDTO.setReferralLink(
					env.getProperty("referral.url") + EncryptDecrypt.encrypt(registerInfo.getId().toString()));

			HttpSession session = request.getSession(true);
			LOG.info("Getting Session Id : " + session.getId());
			session.setAttribute("id", registerInfo.getId());
			session.setAttribute("emailId", registerInfo.getEmailId());
			HttpSessionEvent event = new HttpSessionEvent(session);
			sessionCollector.sessionCreated(event);
			responseDTO.setSessionId(session.getId());
			return responseDTO;
		}
		responseDTO.setStatus("failed");
		return responseDTO;
	}

	// Social media registration and login

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public LoginDTO newSocialMediaRegisterAndLogin(RegisterDTO registerDTO, HttpServletRequest request)
			throws Exception {

		if (registerDTO.getEtherWalletPassword() != null) {
			boolean isValid = isEtherWalletCreated(registerDTO);
			try {
				if (isValid) {
					RegisterInfo registerInfo = new RegisterInfo();
					registerInfo.setUserName(registerDTO.getUserName());
					registerInfo.setEmailId(registerDTO.getEmailId());
					registerInfo.setRoleId(3);
					registerInfo.setActivation(true);
					registerInfo.setPassword("null");
					registerInfo.setBurnTokens(0.0);
					registerInfo.setMediaId(registerDTO.getMediaId());
					registerInfo.setEtherWalletAddress(registerDTO.getEtherWalletAddress());
					registerInfo.setEtherWalletPassword(registerDTO.getEtherWalletPassword());
					registerInfo.setCreatedDate(new Date());
					registerInfo.setMobileNo("null");
					registerInfo.setLevelOne(0);
					registerInfo.setLevelTwo(0);
					registerInfo.setLevelThree(0);
					registerInfo.setLevelFour(0);
					registerInfo.setKycStatus(0);
					registerInfo.setSponser_id(1);
					registerInfo.setLevelOneBonus(new BigDecimal(0.00));
					registerInfo.setLevelTwoBonus(new BigDecimal(0.00));
					registerInfo.setLevelThreeBonus(new BigDecimal(0.00));
					registerInfo.setLevelFourBonus(new BigDecimal(0.00));
					registerInfo.setAppId("");
					registerInfo.setDeviceType("");
					registerInfo.setWalletAddress("");

					registerInfoRepository.save(registerInfo);

					int id = registerInfo.getId();
					String dynamicQRFolder = Integer.toString(id);
					QRcodeInfo qrcode = qrCodeInfoRepository.findQRcodeByQrKey("QRKey");
					String qrFileLocation = null;
					if (qrcode != null) {
						qrFileLocation = qrcode.getQrvalue();
						File createfolder = new File(qrFileLocation.concat(dynamicQRFolder));
						if (!createfolder.exists()) {
							createfolder.mkdir();
							qrFileLocation = createfolder.getPath().replace("//", "/");
							qrFileLocation = qrFileLocation.concat("/");
						}
					}

					String DecryptedWalletAddress = EncryptDecrypt.decrypt(registerDTO.getEtherWalletAddress());
					LOG.info("Wallet Address : Encryption Details : " + registerDTO.getEtherWalletAddress());
					ConfigInfo configInfo = configInfoRepository.findConfigInfoByConfigKey("WalletFile");
					String WalletAddress = userUtils.getEtherWalletAddress(configInfo.getConfigValue(),
							DecryptedWalletAddress);
					LOG.info("Wallet Address :: Encryption Details : " + WalletAddress);
					String qrCodeData = WalletAddress;
					String filePath = qrFileLocation + id + ".png";
					String charset = "UTF-8";
					Map hintMap = new HashMap();
					hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
					QR_Code.createQRCode(qrCodeData, filePath, charset, hintMap, 200, 200);
					LOG.info("QR Code image created successfully!");

					if (registerInfo.getEmailId() != null) {
						LoginDTO responseDTO = new LoginDTO();
						if (registerInfo != null) {

							QRcodeInfo qrCode = qrCodeInfoRepository.findQRcodeByQrKey("QRKey");
							String qrCodePath = qrCode.getQrvalue();
							qrCodePath = env.getProperty("qrcode.url") + "//" + registerInfo.getId() + "//"
									+ registerInfo.getId() + ".png";

							responseDTO.setUserName(registerInfo.getUserName().trim());
							responseDTO.setEmailId(registerInfo.getEmailId().trim());

							String decryptEtherWalletAddress = EncryptDecrypt
									.decrypt(registerInfo.getEtherWalletAddress());
							LOG.info("decryptWalletAddress : " + decryptEtherWalletAddress);
							String etherWalletAddress = userUtils.getEtherWalletAddress(configInfo.getConfigValue(),
									decryptEtherWalletAddress);
							if (etherWalletAddress != null) {
								responseDTO.setEtherWalletAddress(etherWalletAddress);
							}
							responseDTO.setRoleId(registerInfo.getRoleId());
							responseDTO.setStatus("Success");
							registerDTO.setPopUpStatus("false");
							responseDTO.setQrCode(qrCodePath);
							responseDTO.setMediaId(registerInfo.getMediaId());
							responseDTO.setKycStatus(registerInfo.getKycStatus());
							responseDTO.setReferralLink(env.getProperty("referral.url")
									+ EncryptDecrypt.encrypt(registerInfo.getId().toString()));

							HttpSession session = request.getSession(true);
							LOG.info("Getting Session Id : " + session.getId());
							session.setAttribute("id", registerInfo.getId());
							session.setAttribute("emailId", registerInfo.getEmailId());
							HttpSessionEvent event = new HttpSessionEvent(session);
							sessionCollector.sessionCreated(event);
							responseDTO.setSessionId(session.getId());
							return responseDTO;
						}
					}
				}
			} catch (Exception e) {

				e.printStackTrace();
			}
		}
		return null;
	}

	// Check ether balance

	@Override
	public BigDecimal etherBalance(RegisterDTO registerDTO) throws Exception {
		HttpSession session = SessionCollector.find(registerDTO.getSessionId());

		ConfigInfo configInfo = configInfoRepository.findConfigInfoByConfigKey("walletfile");

		String mail = (String) session.getAttribute("emailId");
		LOG.info("Email : " + mail);

		RegisterInfo register = registerInfoRepository.findRegisterInfoByEmailId(mail);

		String walletAddress;
		if (register != null) {
			String decryptWalletAddress = EncryptDecrypt.decrypt(register.getEtherWalletAddress());
			walletAddress = userUtils.getEtherWalletAddress(configInfo.getConfigValue(), decryptWalletAddress);

			if (walletAddress == null) {
				return null;
			}
			LOG.info("Ether Address : " + walletAddress);
			EthGetBalance ethGetBalance;
			ethGetBalance = web3j.ethGetBalance(walletAddress, DefaultBlockParameterName.LATEST).sendAsync().get();
			BigInteger wei = ethGetBalance.getBalance();
			LOG.info("ether bal:::::::::::" + wei);
			BigDecimal amountCheck = Convert.fromWei(wei.toString(), Convert.Unit.ETHER);
			LOG.info("ether bal:::::::::::" + amountCheck);
			registerDTO.setEtherBalance(amountCheck);

			return amountCheck;
		} else
			return null;
	}

	// Listing the all existing users in aleef portal

	@Override
	public List<RegisterDTO> listUsers() {

		List<RegisterDTO> usersList = new ArrayList<RegisterDTO>();

		List<RegisterInfo> registerInfos = (List<RegisterInfo>) registerInfoRepository.findAllByOrderByIdDesc();

		for (RegisterInfo registerInfo : registerInfos) {
			RegisterDTO registerDTO = new RegisterDTO();

			if (registerInfo.getRoleId() != 1) {
				registerDTO.setId(registerInfo.getId());
				registerDTO.setUserName(registerInfo.getUserName());
				registerDTO.setEmailId(registerInfo.getEmailId());
				registerDTO.setCreatedDate(registerInfo.getCreatedDate());
				registerDTO.setActivation(registerInfo.getActivation());

				Double level1 = registerInfo.getLevelOneBonus().doubleValue();
				Double level2 = registerInfo.getLevelTwoBonus().doubleValue();
				Double level3 = registerInfo.getLevelThreeBonus().doubleValue();
				Double level4 = registerInfo.getLevelFourBonus().doubleValue();

				Double referralTokens = level1 + level2 + level3 + level4;

				registerDTO.setReferralLevel1Tokens(level1);
				registerDTO.setReferralLevel2Tokens(level2);
				registerDTO.setReferralLevel3Tokens(level3);
				registerDTO.setReferralLevel4Tokens(level4);
				registerDTO.setReferralTokens(referralTokens);

				usersList.add(registerDTO);
			}
		}
		return usersList;
	}

	// Filter users based on the users email id's

	@Override
	public List<RegisterDTO> userListFilters(RegisterDTO registerDTO) {

		List<RegisterDTO> usersList = new ArrayList<RegisterDTO>();

		if (registerDTO.getEmailId() != null) {

			List<RegisterInfo> registerInfo = registerInfoRepository.findByEmailId(registerDTO.getEmailId().trim());

			for (RegisterInfo registerInfos : registerInfo) {
				RegisterDTO dtos = new RegisterDTO();
				dtos.setId(registerInfos.getId());
				dtos.setUserName(registerInfos.getUserName());
				dtos.setEmailId(registerInfos.getEmailId());
				dtos.setCreatedDate(registerInfos.getCreatedDate());
				dtos.setActivation(registerInfos.getActivation());

				Double level1 = registerInfos.getLevelOneBonus().doubleValue();
				Double level2 = registerInfos.getLevelTwoBonus().doubleValue();
				Double level3 = registerInfos.getLevelThreeBonus().doubleValue();
				Double level4 = registerInfos.getLevelFourBonus().doubleValue();

				Double referralTokens = level1 + level2 + level3 + level4;

				dtos.setReferralLevel1Tokens(level1);
				dtos.setReferralLevel2Tokens(level2);
				dtos.setReferralLevel3Tokens(level3);
				dtos.setReferralLevel4Tokens(level4);
				dtos.setReferralTokens(referralTokens);

				usersList.add(dtos);
			}
		} else if (registerDTO.getUserName() != null) {

			List<RegisterInfo> registerInfo = registerInfoRepository
					.findByUserNameLike(registerDTO.getUserName().trim());

			for (RegisterInfo registerInfos : registerInfo) {
				RegisterDTO dtos = new RegisterDTO();
				dtos.setId(registerInfos.getId());
				dtos.setUserName(registerInfos.getUserName());
				dtos.setEmailId(registerInfos.getEmailId());
				dtos.setCreatedDate(registerInfos.getCreatedDate());
				dtos.setActivation(registerInfos.getActivation());

				Double level1 = registerInfos.getLevelOneBonus().doubleValue();
				Double level2 = registerInfos.getLevelTwoBonus().doubleValue();
				Double level3 = registerInfos.getLevelThreeBonus().doubleValue();
				Double level4 = registerInfos.getLevelFourBonus().doubleValue();

				Double referralTokens = level1 + level2 + level3 + level4;

				dtos.setReferralLevel1Tokens(level1);
				dtos.setReferralLevel2Tokens(level2);
				dtos.setReferralLevel3Tokens(level3);
				dtos.setReferralLevel4Tokens(level4);
				dtos.setReferralTokens(referralTokens);

				usersList.add(dtos);
			}

		}
		return usersList;
	}

	// Listing the referral token details

	@Override
	public boolean userReferralPointsList(RegisterDTO registerDTO) {

		HttpSession sessions = SessionCollector.find(registerDTO.getSessionId());

		String email = (String) sessions.getAttribute("emailId");

		RegisterInfo registerInfo = registerInfoRepository.findRegisterInfoByEmailId(email.trim());

		if (registerInfo != null) {
			Double level1 = registerInfo.getLevelOneBonus().doubleValue();
			Double level2 = registerInfo.getLevelTwoBonus().doubleValue();
			Double level3 = registerInfo.getLevelThreeBonus().doubleValue();
			Double level4 = registerInfo.getLevelFourBonus().doubleValue();

			Double referralTokens = level1 + level2 + level3 + level4;

			registerDTO.setUserName(registerInfo.getUserName());
			registerDTO.setReferralLevel1Tokens(level1);
			registerDTO.setReferralLevel2Tokens(level2);
			registerDTO.setReferralLevel3Tokens(level3);
			registerDTO.setReferralLevel4Tokens(level4);
			registerDTO.setReferralTokens(referralTokens);
			return true;
		}
		return false;
	}

	@Override
	public boolean postIcoTokenTransfer() throws Exception {
		LOG.info("Inside postIcoTokenTransfer() Method");
		String str = icoTokensTransfer();
		LOG.info("icoTokensTransfer() " + str);
		return true;
	}

	// ICO token transfer for both users as well as referrals

	@SuppressWarnings("unused")
	@Override
	public String icoTokensTransfer() throws Exception {
		LOG.info("Inside icoTokensTransfer()");
		List<PurchaseInfo> purchaseInfo = (List<PurchaseInfo>) purchaseInfoRepository.findAll();
		LOG.info("purchaseInfo.size() " + purchaseInfo.size());
		LevelsPercentage levelsPercentage = levelPercentageRepository.findOne(1);
		LOG.info("Level one" + levelsPercentage.getLevelOne());
		LOG.info("Level two" + levelsPercentage.getLevelTwo());
		LOG.info("Level three" + levelsPercentage.getLevelThree());
		LOG.info("Level four" + levelsPercentage.getLevelFour());
		int count = 0;

		for (PurchaseInfo purchaseCoinInfo : purchaseInfo) {
			LOG.info("Purchase coin details");

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

					LOG.info("registerInfo.getSponser_id() " + registerInfo.getSponser_id());
					LOG.info("env.getProperty(admin.id) " + env.getProperty("admin.id"));
					LOG.info(".equals"
							+ registerInfo.getSponser_id().toString().equals(env.getProperty("admin.id").toString()));

					if (!registerInfo.getSponser_id().toString().equals(env.getProperty("admin.id").toString())) {
						findLevelOneSponcerInfo = registerInfoRepository.findById(registerInfo.getSponser_id());
						decryptedEtherWalletAddress = EncryptDecrypt
								.decrypt(findLevelOneSponcerInfo.getEtherWalletAddress());
						walletAddressForLevel1 = userUtils.getWalletAddress(decryptedEtherWalletAddress);

						if (!findLevelOneSponcerInfo.getSponser_id().toString()
								.equals(env.getProperty("admin.id").toString()) && findLevelOneSponcerInfo != null) {
							findLevelTwoSponcerInfo = registerInfoRepository
									.findById(findLevelOneSponcerInfo.getSponser_id());
							decryptedEtherWalletAddress = EncryptDecrypt
									.decrypt(findLevelTwoSponcerInfo.getEtherWalletAddress());
							walletAddressForLevel2 = userUtils.getWalletAddress(decryptedEtherWalletAddress);

							if (!findLevelTwoSponcerInfo.getSponser_id().toString()
									.equals(env.getProperty("admin.id").toString())
									&& findLevelTwoSponcerInfo != null) {
								findLevelThreeSponcerInfo = registerInfoRepository
										.findById(findLevelTwoSponcerInfo.getSponser_id());
								decryptedEtherWalletAddress = EncryptDecrypt
										.decrypt(findLevelThreeSponcerInfo.getEtherWalletAddress());
								walletAddressForLevel3 = userUtils.getWalletAddress(decryptedEtherWalletAddress);
								if (!findLevelThreeSponcerInfo.getSponser_id().toString()
										.equals(env.getProperty("admin.id").toString())
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
					LOG.info(" -------Wallet Addresses------- ");
					LOG.info(" userWalletAddress " + userWalletAddress);
					LOG.info(" walletAddressForLevel1 " + walletAddressForLevel1);
					LOG.info(" walletAddressForLevel2 " + walletAddressForLevel2);
					LOG.info(" walletAddressForLevel3 " + walletAddressForLevel3);
					LOG.info(" walletAddressForLevel4 " + walletAddressForLevel4);

					BigDecimal i = purchaseCoinInfo.getRequestTokens().add(purchaseCoinInfo.getFreeTokens());

					LOG.info("i.toBigInteger()" + i.toBigInteger());
					LOG.info("i.doubleValue()" + i.doubleValue());
					String ac = "AC";
					String rac = "RAC";

					if (userWalletAddress != null) {

						String isUserTokenTransfer = solidityHandler.TransferCoinfromAdminWalletToReferalSponserWallet(
								userWalletAddress, i.doubleValue(), ac);

						if (isUserTokenTransfer == null) {
							return "cannot able to send token to the actual user";
						}
						if (isUserTokenTransfer != null) {
							boolean savePurchaseTokens = purchaseHistory(purchaseCoinInfo.getId());
							if (!savePurchaseTokens) {
								return "cannot able to insert data in purchase coin table";
							}
						}
					}

					if (walletAddressForLevel1 != null) {
						Integer levelOneOffer = (purchaseCoinInfo.getRequestTokens().intValue()
								* levelsPercentage.getLevelOne().intValue()) / 100;

						String isLevelOneTokenTransfer = solidityHandler
								.TransferCoinfromAdminWalletToReferalSponserWallet(walletAddressForLevel1,
										levelOneOffer.doubleValue(), rac);

						if (isLevelOneTokenTransfer == null) {
							return "cannot able to send token for that level one user";
						}
					}

					if (walletAddressForLevel2 != null) {
						Integer levelTwoOffer = (purchaseCoinInfo.getRequestTokens().intValue()
								* levelsPercentage.getLevelTwo().intValue()) / 100;

						String isLevelTwoTokenTransfer = solidityHandler
								.TransferCoinfromAdminWalletToReferalSponserWallet(walletAddressForLevel2,
										levelTwoOffer.doubleValue(), rac);

						if (isLevelTwoTokenTransfer == null) {
							return "cannot able to send token for that level two user";
						}

					}

					if (walletAddressForLevel3 != null) {
						Integer levelThreeOffer = (purchaseCoinInfo.getRequestTokens().intValue()
								* levelsPercentage.getLevelThree().intValue()) / 100;

						String isLevelThreeTokenTransfer = solidityHandler
								.TransferCoinfromAdminWalletToReferalSponserWallet(walletAddressForLevel3,
										levelThreeOffer.doubleValue(), rac);

						if (isLevelThreeTokenTransfer == null) {
							return "cannot able to send token for that level three user";
						}

					}
					if (walletAddressForLevel4 != null) {
						Integer levelFourOffer = (purchaseCoinInfo.getRequestTokens().intValue()
								* levelsPercentage.getLevelFour().intValue()) / 100;

						String isLevelFourTokenTransfer = solidityHandler
								.TransferCoinfromAdminWalletToReferalSponserWallet(walletAddressForLevel4,
										levelFourOffer.doubleValue(), rac);
						if (isLevelFourTokenTransfer == null) {
							return "cannot able to send token for that level four user";
						}

					}

				} catch (Exception e) {

					LOG.error("Exception e" + e);
				}
			}
		}
		return "Success";
	}

	@SuppressWarnings("unused")
	private boolean purchaseHistory(Integer id) {
		PurchaseInfo purchaseCoinInfos = (PurchaseInfo) purchaseInfoRepository.findById(id);

		LOG.info("Purchase Coin Info");

		purchaseCoinInfos.setTransferStatus(1);
		purchaseInfoRepository.save(purchaseCoinInfos);
		if (purchaseCoinInfos != null) {
			return true;
		}
		return false;
	}

	// Users upload KYC documents

	@Override
	public String userDocumentUpload(MultipartFile uploadedFileRef, String emailId, String docType) throws Exception {
		FileInputStream reader = null;
		FileOutputStream writer = null;
		String path = null;
		String dbPath = null;
		Integer userRegId = 0;

		LOG.info("Mail Id--------->" + emailId);
		LOG.info("In  userDocumentUpload : start");
		try {
			ConfigInfo configInfo = configInfoRepository.findConfigInfoByConfigKey("location");
			String basePath = configInfo.getConfigValue();

			RegisterInfo userModelInfo = registerInfoRepository.findRegisterInfoByEmailId(emailId);

			if (userModelInfo != null) {
				userRegId = userModelInfo.getId();
				LOG.info("userRegId : " + userRegId);
				LOG.info("USerRegisterId " + userModelInfo.getId());
			}
			LOG.info(" Base path from config table : " + basePath);
			String uploadingdir = basePath + File.separator + env.getProperty("user.document.location") + File.separator
					+ userRegId + File.separator + docType;
			LOG.info(" uploadingdir : " + uploadingdir);
			File file = new File(uploadingdir);

			if (file.exists()) {
				file.delete();
			}
			if (!file.exists()) {
				LOG.info(" In mkdir : " + uploadingdir);
				file.mkdirs();
			}
			LOG.info(" uploadingdir : " + uploadingdir);

			String fileType = Files.getFileExtension(uploadedFileRef.getOriginalFilename());
			String fileName = Files.getNameWithoutExtension(uploadedFileRef.getOriginalFilename());

			double fileSize = uploadedFileRef.getSize() / 1024;

			LOG.info("File size in MB " + fileSize);
			LOG.info("File Type " + fileType);

			if (fileType.equalsIgnoreCase("jpeg") || fileType.equalsIgnoreCase("png")
					|| fileType.equalsIgnoreCase("jpg") || fileType.equalsIgnoreCase("pdf")) {

				LOG.info("Uploaded File Name " + fileName);
				path = uploadingdir + File.separator + fileName + "-" + userRegId + "." + fileType;
				dbPath = File.separator + env.getProperty("user.document.location") + File.separator + userRegId
						+ File.separator + docType + File.separator + fileName + "-" + userRegId + "." + fileType;
				LOG.info(" file path : " + path);
				LOG.info("dbPath : " + dbPath);
				byte[] buffer = new byte[1000];
				File outputFile = new File(path);

				int totalBytes = 0;
				outputFile.createNewFile();
				reader = (FileInputStream) uploadedFileRef.getInputStream();
				writer = new FileOutputStream(outputFile);

				int bytesRead = 0;
				while ((bytesRead = reader.read(buffer)) != -1) {
					writer.write(buffer);
					totalBytes += bytesRead;
				}
				dbPath = dbPath.replace(File.separator, "/");
				LOG.info("totalBytes:::" + totalBytes);
				reader.close();
				writer.close();
			} else {
				path = null;
				dbPath = null;
			}

		} catch (IOException e) {
			path = null;
			dbPath = null;
			reader.close();
			writer.close();
			LOG.error("Problem in userDocumentUpload file path : " + path);
			e.printStackTrace();
		} finally {

		}
		LOG.info("In  userDocumentUpload : end : dbPath : " + dbPath);
		return dbPath;
	}

	// Saving users KYC info and documents

	@SuppressWarnings("unused")
	@Override
	public boolean saveKycInfo(KycDTO kycDTO, String kycDoc1FilePath, String kycDoc2FilePath, MultipartFile kycDoc1,
			MultipartFile kycDoc2) {

		RegisterInfo userModelInfo = registerInfoRepository.findRegisterInfoByEmailId(kycDTO.getEmailId());
		LOG.info("userModelInfo.getKycStatus() ::" + userModelInfo.getKycStatus());
		userModelInfo.setKycStatus(1);
		if (userModelInfo != null) {
			KycInfo kycInfo = kycInfoRepo.findByEmailId(kycDTO.getEmailId());
			if (kycInfo == null) {
				KycInfo kycInfo2 = new KycInfo();
				kycInfo2.setCountry(kycDTO.getCountry());
				kycInfo2.setCity(kycDTO.getCity());
				kycInfo2.setCreationTime(new Date());
				kycInfo2.setDob(kycDTO.getDob());
				kycInfo2.setEmailId(kycDTO.getEmailId());
				kycInfo2.setFullName(kycDTO.getFullName());
				kycInfo2.setHomeAddress(kycDTO.getAddress());
				kycInfo2.setPhoneNo(kycDTO.getMobileNo());
				kycInfo2.setGender(kycDTO.getGender());
				LOG.info("kycDoc1FilePath  " + kycDoc1FilePath);
				kycInfo2.setKycDoc1Path(kycDoc1FilePath);
				kycInfo2.setKycStatus(2);
				kycInfo2.setKycUploadStatus(1);
				LOG.info("Original file ++++++++++++++++++++++" + kycDoc1.getOriginalFilename());
				kycInfo2.setKycDoc1Name(Files.getNameWithoutExtension(kycDoc1.getOriginalFilename()));

				if (kycDoc2FilePath != null && !kycDoc2FilePath.isEmpty()) {
					LOG.info("kycDoc2FilePath  " + kycDoc2FilePath);
					kycInfo2.setKycDoc2Path(kycDoc2FilePath);
					kycInfo2.setKycDoc2Name(Files.getNameWithoutExtension(kycDoc2.getOriginalFilename()));
				} else {
					kycInfo2.setKycDoc2Path("");
					kycInfo2.setKycDoc2Name("");
				}
				kycInfoRepo.save(kycInfo2);
				registerInfoRepository.save(userModelInfo);
				kycDTO.setKycStatus(userModelInfo.getKycStatus());
				kycDTO.setKycDoc1Name(kycInfo2.getKycDoc1Name());
				kycDTO.setKycDoc2Name(kycInfo2.getKycDoc2Name());
				return true;
			} else {

				kycInfo.setCity(kycDTO.getCity());
				kycInfo.setCountry(kycDTO.getCountry());
				kycInfo.setCreationTime(new Date());
				kycInfo.setDob(kycDTO.getDob());
				kycInfo.setEmailId(kycDTO.getEmailId());
				kycInfo.setGender(kycDTO.getGender());
				kycInfo.setFullName(kycDTO.getFullName());
				kycInfo.setHomeAddress(kycDTO.getAddress());
				kycInfo.setPhoneNo(kycDTO.getMobileNo());
				LOG.info("kycDoc1FilePath  " + kycDoc1FilePath);
				LOG.info("kycDoc2FilePath  " + kycDoc2FilePath);
				kycInfo.setKycDoc1Path(kycDoc1FilePath);
				kycInfo.setKycStatus(2);
				kycInfo.setKycUploadStatus(1);
				LOG.info("Original file ++++++++++++++++++++++" + kycDoc1.getOriginalFilename());
				kycInfo.setKycDoc1Name(Files.getNameWithoutExtension(kycDoc1.getOriginalFilename()));

				if (kycDoc2FilePath != null && !kycDoc2FilePath.isEmpty()) {
					LOG.info("kycDoc2FilePath" + kycDoc2FilePath);
					kycInfo.setKycDoc2Path(kycDoc2FilePath);
					kycInfo.setKycDoc2Name(Files.getNameWithoutExtension(kycDoc2.getOriginalFilename()));
				} else {
					kycInfo.setKycDoc2Path("");
					kycInfo.setKycDoc2Name("");
				}

				kycInfoRepo.save(kycInfo);
				registerInfoRepository.save(userModelInfo);
				kycDTO.setKycStatus(userModelInfo.getKycStatus());
				kycDTO.setKycDoc1Name(kycInfo.getKycDoc1Name());
				kycDTO.setKycDoc2Name(kycInfo.getKycDoc2Name());
				return true;
			}
		}
		return false;
	}

	@Override
	public List<KycDTO> kycList() {
		List<KycDTO> kycList = new ArrayList<KycDTO>();
		List<KycInfo> kycInfo = (List<KycInfo>) kycInfoRepo.findAllByOrderByIdDesc();

		for (KycInfo info : kycInfo) {
			KycDTO list = userUtils.listKYC(info);
			kycList.add(list);
		}
		return kycList;
	}

	// Get individual users KYC details

	@Override
	public KycDTO getKycDetails(TokenDTO tokenDTO) {
		KycInfo kycInfo = kycInfoRepo.findKycInfoById(tokenDTO.getKycId());

		LOG.info("USer KYC ID" + tokenDTO.getKycId());
		KycDTO kycDTO = new KycDTO();
		if (kycInfo != null) {
			kycDTO.setId(kycInfo.getId());
			kycDTO.setFullName(kycInfo.getFullName());
			kycDTO.setDob(kycInfo.getDob());
			kycDTO.setCity(kycInfo.getCity());
			kycDTO.setCountry(kycInfo.getCountry());
			kycDTO.setAddress(kycInfo.getHomeAddress());
			kycDTO.setMobileNo(kycInfo.getPhoneNo());
			kycDTO.setGender(kycInfo.getGender());
			kycDTO.setEmailId(kycInfo.getEmailId());
			kycDTO.setKycDoc1Path(env.getProperty("apache.server") + kycInfo.getKycDoc1Path());

			if (!kycInfo.getKycDoc2Path().trim().equals("")) {
				kycDTO.setKycDoc2Path(env.getProperty("apache.server") + kycInfo.getKycDoc2Path());
			}
			kycDTO.setKycStatus(kycInfo.getKycStatus());

			return kycDTO;
		}
		return null;
	}

	// Users need to update the KYC documents again while admin rejects the KYC
	// approval and trigger a mail to users for inform the status of KYC info's

	@SuppressWarnings("unused")
	@Override
	public boolean updateKYC(KycDTO kycDTO) {
		LOG.info("KYC update function");

		KycInfo kycInfo = kycInfoRepo.findKycInfoById(kycDTO.getId());
		String mailId = kycInfo.getEmailId();

		LOG.info("KYC Mail :" + mailId);

		LOG.info("mailId ::" + mailId);
		RegisterInfo userModelInfo = registerInfoRepository.findRegisterInfoByEmailId(mailId);

		LOG.info("Register ::" + userModelInfo.getEmailId());

		if (kycInfo != null) {
			LOG.info("KYC info is not null");
			LOG.info("Kyc Status inside updateKYC:" + kycDTO.getKycStatus());

			if (kycDTO.getKycStatus() == 1) {
				LOG.info("KYC approved");
				userModelInfo.setKycStatus(1);
				kycInfo.setKycStatus(kycDTO.getKycStatus());
				kycDTO.setMessage("User KYC Details has been Approved Successfully!");
				String verificationLink = "Hi " + userModelInfo.getUserName() + ",<br><br>"
						+ "Your KYC details approved successfully." + "<br><br>" + "" + "With Regards,<br>"
						+ "Support Team<br>" + "Aleef Coin<br>" + "email : info@aleefcoin.io";
				boolean isEmailSent = emailNotificationServiceImpl.sendEmail(userModelInfo.getEmailId(),
						"AleefCoin Team", verificationLink);
			} else if (kycDTO.getKycStatus() == 0) {
				LOG.info("KYC rejected");
				userModelInfo.setKycStatus(0);
				kycInfo.setKycStatus(kycDTO.getKycStatus());

				kycDTO.setMessage("User KYC Details has been Rejected!");
				String verificationLink = "Hi " + userModelInfo.getUserName() + ",<br><br>"
						+ "Your KYC details rejected due to improper (or) mismatch information that you have provided. Kindly reapply with proper information"
						+ "<br><br>" + "" + "With Regards,<br><br>" + "Support Team<br>" + "Aleef Coin<br>"
						+ "email : info@aleefcoin.io";
				boolean isEmailSent = emailNotificationServiceImpl.sendEmail(userModelInfo.getEmailId(),
						"AleefCoin Team", verificationLink);
			}
			kycInfoRepo.save(kycInfo);
			registerInfoRepository.save(userModelInfo);
			return true;
		}
		return false;
	}

	// Triggering a mail to referrals for registration over the aleef portal

	@Override
	public boolean referral(RegisterDTO registerDTO) throws Exception {
		HttpSession session = SessionCollector.find(registerDTO.getSessionId());
		String mail = (String) session.getAttribute("emailId");

		RegisterInfo registerInfo = registerInfoRepository.findRegisterInfoByEmailId(mail);
		String id = EncryptDecrypt.encrypt(registerInfo.getId().toString());

		String verificationLink = "Hi there,<br><br>" + registerInfo.getUserName() + "(Ref. Id : "
				+ registerInfo.getId() + ") " + "has invited you to visit  " + env.getProperty("website")
				+ " . Please click the link below and register your self for an aleefcoin wallet" + "<br><br>"
				+ "<a href='" + env.getProperty("referral.url") + id + "'>" + env.getProperty("email.content.referral")
				+ "</a><br><br>" + "withRegards,<br><br>" + "Support Team<br>" + "Aleef Coin<br>"
				+ "email : info@aleefcoin.io";
		boolean isEmailSent = emailNotificationServiceImpl.sendEmail(registerDTO.getEmailId(),
				"Aleef Registration Team", verificationLink);
		if (isEmailSent) {
			return true;
		}
		return false;
	}

	// Get all slabs ICO token details

	@Override
	public List<PurchaseCoinDTO> getIcoTokenDetails() {

		List<PurchaseCoinDTO> purchaseCoinList = new ArrayList<PurchaseCoinDTO>();

		List<PurchaseCoinInfo> purchaseCoinInfos = (List<PurchaseCoinInfo>) purchaseCoinInfoRepository.findAll();

		for (PurchaseCoinInfo purchaseCoinInfo : purchaseCoinInfos) {

			PurchaseCoinDTO purchaseCoinDTO = new PurchaseCoinDTO();

			purchaseCoinDTO.setBalanceCoins(purchaseCoinInfo.getBalanceCoins());
			purchaseCoinDTO.setSoldTokens(purchaseCoinInfo.getFreeCoins()
					.add(purchaseCoinInfo.getPurchasedCoins().add(purchaseCoinInfo.getReferralCoins())));
			purchaseCoinDTO.setSlabs(purchaseCoinInfo.getIcoLevelsSlabs());
			purchaseCoinDTO.setTotalDistributionTokens(purchaseCoinInfo.getTotalDistributionTokens());
			purchaseCoinList.add(purchaseCoinDTO);
		}
		return purchaseCoinList;
	}

	// Get referral details for individual users

	@SuppressWarnings("unchecked")
	@Override
	public List<String>[] getReferralDetails(RegisterDTO registerDTO) {

		HttpSession session = SessionCollector.find(registerDTO.getSessionId());

		List<String> levelOneRefNames = new ArrayList<String>();
		List<String> levelTwoRefNames = new ArrayList<String>();
		List<String> levelThreeRefNames = new ArrayList<String>();
		List<String> levelFourRefNames = new ArrayList<String>();

		List<String>[] allLevelRefNames = new List[4];

		String email = (String) session.getAttribute("emailId");

		LOG.info("User Email Id " + email);

		RegisterInfo actualRegisterInfo = registerInfoRepository.findRegisterInfoByEmailId(email);

		actualRegisterInfo.getId();

		List<RegisterInfo> allRegisterInfo = (List<RegisterInfo>) registerInfoRepository.findAll();

		for (RegisterInfo RegisterInfoLevel11 : allRegisterInfo) {

			if (actualRegisterInfo.getId() == RegisterInfoLevel11.getSponser_id()) {

				levelOneRefNames.add(RegisterInfoLevel11.getUserName());

				LOG.info("RegisterInfoLevel11.getId() " + RegisterInfoLevel11.getId());

				for (RegisterInfo RegisterInfoLevel22 : allRegisterInfo) {

					if (RegisterInfoLevel11.getId() == RegisterInfoLevel22.getSponser_id()) {

						levelTwoRefNames.add(RegisterInfoLevel22.getUserName());

						LOG.info("RegisterInfoLevel22.getId() " + RegisterInfoLevel22.getId());

						for (RegisterInfo RegisterInfoLevel33 : allRegisterInfo) {

							if (RegisterInfoLevel22.getId() == RegisterInfoLevel33.getSponser_id()) {

								levelThreeRefNames.add(RegisterInfoLevel33.getUserName());

								LOG.info("RegisterInfoLevel33.getId() " + RegisterInfoLevel33.getId());

								for (RegisterInfo RegisterInfoLevel44 : allRegisterInfo) {

									if (RegisterInfoLevel33.getId() == RegisterInfoLevel44.getSponser_id()) {

										levelFourRefNames.add(RegisterInfoLevel44.getUserName());

									}

								}

							}

						}

					}

				}

			}

		}

		allLevelRefNames[0] = levelOneRefNames;
		allLevelRefNames[1] = levelTwoRefNames;
		allLevelRefNames[2] = levelThreeRefNames;
		allLevelRefNames[3] = levelFourRefNames;

		return allLevelRefNames;

	}

	// Filter kyc details based on kyc status

	@Override
	public List<KycDTO> filterKyCStatus(KycDTO kycDTO) {
		List<KycDTO> kycList = new ArrayList<KycDTO>();

		List<KycInfo> kycInfos = (List<KycInfo>) kycInfoRepo.findByKycStatus(kycDTO.getKycStatus());

		for (KycInfo kycInfo : kycInfos) {

			KycDTO list = userUtils.listKYC(kycInfo);

			kycList.add(list);
		}
		return kycList;
	}

	// Filter KYC details based on users user names

	@Override
	public List<KycDTO> filterKycUserName(KycDTO kycDTO) {
		List<KycDTO> kycList = new ArrayList<KycDTO>();

		if (kycDTO.getFullName() != null) {
			List<KycInfo> kycInfos = (List<KycInfo>) kycInfoRepo.findByFullNameLike(kycDTO.getFullName().trim());

			for (KycInfo kycInfo : kycInfos) {

				KycDTO list = userUtils.listKYC(kycInfo);

				kycList.add(list);
			}
		} else if (kycDTO.getEmailId() != null) {
			List<KycInfo> kycInfo = kycInfoRepo.findKycInfoByEmailId(kycDTO.getEmailId().trim());

			for (KycInfo kycInfos : kycInfo) {

				KycDTO kycLists = new KycDTO();
				kycLists.setId(kycInfos.getId());
				kycLists.setFullName(kycInfos.getFullName());
				kycLists.setDob(kycInfos.getDob());
				kycLists.setCity(kycInfos.getCity());
				kycLists.setCountry(kycInfos.getCountry());
				kycLists.setAddress(kycInfos.getHomeAddress());
				kycLists.setGender(kycInfos.getGender());
				kycLists.setEmailId(kycInfos.getEmailId());
				kycLists.setMobileNo(kycInfos.getPhoneNo());
				kycLists.setKycDoc1Path(env.getProperty("apache.server") + kycInfos.getKycDoc1Path());

				if (!kycInfos.getKycDoc2Path().trim().equals("")) {
					kycLists.setKycDoc2Path(env.getProperty("apache.server") + kycInfos.getKycDoc2Path());
				}
				kycLists.setKycStatus(kycInfos.getKycStatus());

				kycList.add(kycLists);
			}
		}
		return kycList;
	}

	// Get all referral tokens for admin dash board

	@Override
	public boolean totalReferralForAdmin(RegisterDTO registerDTO) {

		List<RegisterInfo> registerInfo = (List<RegisterInfo>) registerInfoRepository.findAll();
		if (registerInfo != null) {
			Double one = 0.0;
			Double two = 0.0;
			Double three = 0.0;
			Double four = 0.0;
			for (RegisterInfo register : registerInfo) {

				one = one + register.getLevelOneBonus().doubleValue();
				two = two + register.getLevelTwoBonus().doubleValue();
				three = three + register.getLevelThreeBonus().doubleValue();
				four = four + register.getLevelFourBonus().doubleValue();

			}

			LOG.info(one + ":: " + two + ":: " + three + ":: " + four);

			Double total = one + two + three + four;
			LOG.info(one + ":: " + two + ":: " + three + ":: " + four + total);

			registerDTO.setReferralLevel1Tokens(one);
			registerDTO.setReferralLevel2Tokens(two);
			registerDTO.setReferralLevel3Tokens(three);
			registerDTO.setReferralLevel4Tokens(four);
			registerDTO.setReferralTokens(total);
			return true;
		}
		return false;
	}

	// Get User KYC details

	@Override
	public KycDTO getUserKycDetails(TokenDTO tokenDTO) {
		HttpSession session = SessionCollector.find(tokenDTO.getSessionId());

		String email = (String) session.getAttribute("emailId");
		LOG.info("Email : " + email);

		KycInfo kycInfo = kycInfoRepo.findByEmailId(email);

		KycDTO kycDTO = new KycDTO();
		if (kycInfo != null) {
			kycDTO.setId(kycInfo.getId());
			kycDTO.setFullName(kycInfo.getFullName());
			kycDTO.setDob(kycInfo.getDob());
			kycDTO.setCity(kycInfo.getCity());
			kycDTO.setCountry(kycInfo.getCountry());
			kycDTO.setAddress(kycInfo.getHomeAddress());
			kycDTO.setMobileNo(kycInfo.getPhoneNo());
			kycDTO.setGender(kycInfo.getGender());
			kycDTO.setEmailId(kycInfo.getEmailId());
			kycDTO.setKycDoc1Path(env.getProperty("apache.server") + kycInfo.getKycDoc1Path());
			if (!kycInfo.getKycDoc2Path().trim().equals("")) {
				kycDTO.setKycDoc2Path(env.getProperty("apache.server") + kycInfo.getKycDoc2Path());
			}
			kycDTO.setKycDoc1Name(kycInfo.getKycDoc1Name());
			kycDTO.setKycDoc2Name(kycInfo.getKycDoc2Name());
			kycDTO.setKycStatus(kycInfo.getKycStatus());

			return kycDTO;
		}
		return null;
	}

}
