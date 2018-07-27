package com.aleef.utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.aleef.dtos.RegisterDTO;
import com.aleef.dtos.TokenDTO;
import com.aleef.models.ExpirationDataInfo;
import com.aleef.models.PreICORegisterInfo;
import com.aleef.models.PurchaseCoinInfo;
import com.aleef.models.TokenInfo;
import com.aleef.models.RegisterInfo;
import com.aleef.models.SecuredInfo;
import com.aleef.models.TransactionHistory;
import com.aleef.repo.ExpirationDataInfoRepository;
import com.aleef.repo.PreICORegisterInfoRepository;
import com.aleef.repo.PurchaseCoinInfoRepository;
import com.aleef.repo.TokenInfoRepository;
import com.aleef.repo.TransactionHistoryRepository;
import com.aleef.repo.RegisterInfoRepository;
import com.aleef.repo.SecuredInfoRepository;
import com.aleef.service.impl.EmailNotificationServiceImpl;
import com.aleef.services.UserRegisterService;
import com.aleef.session.SessionCollector;
import com.aleef.solidityHandler.SolidityHandler;
import com.aleef.solidityToJava.AleefCoin;
import org.springframework.web.multipart.MultipartFile;
import com.aleef.dtos.KycDTO;
import com.aleef.models.KycInfo;
import com.google.common.io.Files;

import com.aleef.models.PurchaseInfo;

@Service
public class UserUtils {

	// Connecting with solidity to Java code
	public static AleefCoin Token;

	static final Logger LOG = LoggerFactory.getLogger(UserUtils.class);

	static final String regex = "[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

	@Autowired
	private Environment env;

	@Autowired
	private RegisterInfoRepository registerInfoRepository;

	@Autowired
	private ExpirationDataInfoRepository expirationDataInfoRepository;

	@Autowired
	private PreICORegisterInfoRepository preICORegisterInfoRepository;

	@Autowired
	private EmailNotificationServiceImpl emailNotificationServiceImpl;

	@Autowired
	private PurchaseCoinInfoRepository purchaseCoinInfoRepository;

	@Autowired
	private SolidityHandler solidityHandler;

	@Autowired
	private UserRegisterService userRegisterService;

	@Autowired
	private TransactionHistoryRepository transactionHistoryRepository;

	@Autowired
	private SessionCollector sessionCollector;

	@Autowired
	private TokenInfoRepository tokenInfoRepository;

	@Autowired
	private CurrentValueUtils currentValueUtils;

	@Autowired
	private SecuredInfoRepository securedInfoRepository;
	
	public boolean validateRegisteration(RegisterDTO registerDTO) {
		if (registerDTO.getUserName() != null && StringUtils.isNotBlank(registerDTO.getUserName())
				&& registerDTO.getEmailId() != null && StringUtils.isNotBlank(registerDTO.getEmailId())
				&& registerDTO.getPassword() != null && StringUtils.isNotBlank(registerDTO.getPassword())
				&& registerDTO.getMobileNo() != null && StringUtils.isNotBlank(registerDTO.getMobileNo())
				&& registerDTO.getConfirmPassword() != null && StringUtils.isNotBlank(registerDTO.getConfirmPassword())
				&& registerDTO.getEtherWalletPassword() != null
				&& StringUtils.isNotBlank(registerDTO.getEtherWalletPassword())
				&& registerDTO.getConfirmEtherWalletPassword() != null
				&& StringUtils.isNotBlank(registerDTO.getConfirmEtherWalletPassword())) {
			return true;
		}
		return false;
	}

	public boolean validateUserName(RegisterDTO registerDTO) {
		Pattern pattern = Pattern.compile("[a-zA-Z 0-9 @#$%&*~^!+-_~:;><?.-_ ]{5,20}$");
		Matcher matcher = pattern.matcher(registerDTO.getUserName());
		if (matcher.matches()) {
			LOG.info(registerDTO.getUserName());
			return true;
		}
		return false;
	}

	public String getWalletAddress(String walletAddress) {
		String[] fetchAddress = walletAddress.split("--");
		String getAddress = fetchAddress[fetchAddress.length - 1].split("\\.")[0];
		String fromWalletAddress = "0x" + getAddress;

		return fromWalletAddress;
	}

	public boolean validateEmail(RegisterDTO registerDTO) {

		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(registerDTO.getEmailId());
		LOG.info(registerDTO.getEmailId() + " : " + matcher.matches());
		if (matcher.matches()) {
			return true;
		} else {

			return false;
		}
	}

	public boolean validatePassword(RegisterDTO registerDTO) {

		Pattern pattern = Pattern
				.compile("^.*(?=.{7,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%&*~^!+-_~:;><?]).*$");
		Matcher matcher = pattern.matcher(registerDTO.getPassword());
		if (matcher.matches()) {
			return true;
		} else
			return false;
	}

	public boolean validateConfirmPassword(RegisterDTO registerDTO) {
		if (registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
			return true;
		}
		return false;
	}

	public boolean validateConfirmEtherWalletPassword(RegisterDTO registerDTO) {
		if (registerDTO.getEtherWalletPassword().equals(registerDTO.getConfirmEtherWalletPassword())) {
			return true;
		}
		return false;
	}

	public boolean validateEtherWalletPassword(RegisterDTO registerDTO) {
		Pattern pattern = Pattern
				.compile("^.*(?=.{7,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%&*~^!+-_~:;><?]).*$");
		Matcher matcher = pattern.matcher(registerDTO.getEtherWalletPassword());
		if (matcher.matches()) {
			LOG.info(registerDTO.getEtherWalletPassword());
			return true;
		} else
			return false;
	}

	public String getEtherWalletAddress(String fileLocation, String fileName)
			throws FileNotFoundException, IOException, ParseException {

		fileLocation = fileLocation.replace("/", "\\");
		LOG.info("WalletCreated" + fileLocation);
		LOG.info("FileName:::" + fileName);

		JSONParser parser = new JSONParser();
		Object object;
		object = parser.parse(new FileReader(fileLocation + "//" + fileName));
		JSONObject jsonObject = (JSONObject) object;
		String address = "0x" + (String) jsonObject.get("address");
		LOG.info("FileName" + fileName);
		LOG.info("Wallet Address" + address);

		return address;
	}

	public boolean validateverificationParams(RegisterDTO registerDTO) {
		if (registerDTO.getEmailId() != null && StringUtils.isNotBlank(registerDTO.getEmailId())) {
			return true;
		}
		return false;
	}

	public boolean isStatusActive(RegisterDTO registerDTO) throws Exception {
		LOG.info("EmailId" + registerDTO.getEmailId());
		String decryptedEmail = EncryptDecrypt.decrypt(registerDTO.getEmailId().replaceAll("\\s", "+"));

		RegisterInfo registerInfoModel = registerInfoRepository.findRegisterInfoByEmailId(decryptedEmail);
		if (registerInfoModel != null) {
			LOG.info("Decrypted Email Id for Activation" + decryptedEmail);
			if (registerInfoModel.getActivation() == true) {
				return true;
			}
		}
		return false;
	}

	public boolean validateEmailLink(RegisterDTO registerDTO) throws Exception {

		String decryptedEmail = EncryptDecrypt.decrypt(registerDTO.getEmailId().replaceAll("\\s", "+"));

		LOG.info("EmailId" + decryptedEmail);

		RegisterInfo registerInfoModel = registerInfoRepository.findRegisterInfoByEmailId(decryptedEmail);

		if (registerInfoModel != null) {
			registerInfoModel.setActivation(true);
			registerInfoRepository.save(registerInfoModel);
			return true;
		}

		return false;
	}

	public boolean validateLoginParam(RegisterDTO registerDTO) {
		if (registerDTO.getEmailId() != null && StringUtils.isNotBlank(registerDTO.getEmailId())
				&& registerDTO.getPassword() != null && StringUtils.isNotBlank(registerDTO.getPassword())) {
			return true;
		}
		return false;
	}

	public boolean validatePasswordForLogin(RegisterDTO registerDTO) throws Exception {

		String email = registerDTO.getEmailId();

		RegisterInfo registerInfo = registerInfoRepository.findRegisterInfoByEmailId(email);

		String password = EncryptDecrypt.decrypt(registerInfo.getPassword());
		LOG.info(password);
		if (password.equals(registerDTO.getPassword())) {
			return true;
		}
		return false;
	}

	public boolean validate2FA(RegisterDTO registerDTO) {

		String email = registerDTO.getEmailId();

		RegisterInfo registerInfo = registerInfoRepository.findRegisterInfoByEmailId(email);

		if (registerInfo.getRoleId() != 1) {
			SecuredInfo securedInfo = securedInfoRepository.findSecuredInfoByEmailId(email.trim());

			if (securedInfo != null) {
				LOG.info(securedInfo.getEmailId());
				LOG.info("Current Date" + securedInfo.getDate());
				LOG.info(registerDTO.getSecurityKey());
				LOG.info(securedInfo.getSecuredKey());

				if (registerDTO.getSecurityKey() != null) {
					String a = registerDTO.getSecurityKey().toString();
					String b = securedInfo.getSecuredKey();

					if (a.equals(b)) {
						LOG.info(securedInfo.getSecuredKey());
						return true;
					}
				}
				return false;
			}
		}
		return true;
	}

	public boolean validateActivation(RegisterDTO registerDTO) {

		LOG.info("Inside Validate Activation");
		LOG.info("validate Activation Email" + registerDTO.getEmailId());

		RegisterInfo registerInfo = registerInfoRepository.findRegisterInfoByEmailId(registerDTO.getEmailId().trim());
		if (registerInfo != null) {
			LOG.info("Activation Status" + registerInfo.getActivation());
			if (registerInfo.getActivation() == true) {
				return true;
			}
		}
		return false;
	}

	public boolean validateSecurityKey(RegisterDTO registerDTO) {

		if (registerDTO.getSecurityKey() != null && StringUtils.isNotBlank(registerDTO.getSecurityKey().toString())) {
			return true;
		}
		return false;
	}

	public boolean validateResetPassword(RegisterDTO registerDTO) {

		Pattern pattern = Pattern.compile("[a-z A-Z 0-9 @#$%&*~^!+-_~:;><?].{7,24}");
		Matcher matcher = pattern.matcher(registerDTO.getPassword());
		LOG.info(registerDTO.getPassword() + " : " + matcher.matches());
		if (matcher.matches()) {

			return true;
		} else {
			return false;
		}
	}

	public boolean validateOldAndNewPassword(RegisterDTO registerDTO) {

		if (!registerDTO.getOldPassword().equals(registerDTO.getPassword())) {
			return true;
		}
		return false;
	}

	public boolean isValidEmailForVerification(RegisterDTO registerDTO) {
		if (registerDTO.getEmailId() != null && StringUtils.isNotBlank(registerDTO.getEmailId())) {
			RegisterInfo registerInfo = registerInfoRepository.findRegisterInfoByEmailId(registerDTO.getEmailId());
			if (registerInfo != null) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	public boolean checkByRoleId(RegisterDTO registerDTO) {

		if (registerDTO.getEmailId() != null && StringUtils.isNotBlank(registerDTO.getEmailId())) {
			RegisterInfo registerInfo = registerInfoRepository.findRegisterInfoByEmailId(registerDTO.getEmailId());
			if (registerInfo.getRoleId() != 3) {
				return true;
			} else
				return false;
		}
		return false;
	}

	public boolean checkByRoleIdForReset(RegisterDTO registerDTO) {

		HttpSession sessions = SessionCollector.find(registerDTO.getSessionId());

		String email = (String) sessions.getAttribute("emailId");

		RegisterInfo registerInfo = registerInfoRepository.findRegisterInfoByEmailId(email);

		if (registerInfo != null) {

			if (registerInfo.getRoleId() != 3) {
				return true;
			} else
				return false;
		}
		return false;
	}

	public boolean isResetPasswordLinkVerified(RegisterDTO registerDTO) throws Exception {
		String decryptedToken = EncryptDecrypt.decrypt(registerDTO.getToken().replaceAll("\\s", "+"));
		LOG.info("Decrypted Token" + decryptedToken);
		ExpirationDataInfo expirationDataInfo = expirationDataInfoRepository.findByToken(decryptedToken);
		if (!expirationDataInfo.isTokenStatus()) {
			return true;
		}
		return false;
	}

	public boolean validateTime(RegisterDTO registerDTO) throws Exception {

		LOG.info("Encrypted Email" + registerDTO.getEmailId().replaceAll("\\s", "+"));
		String decryptedEmail = EncryptDecrypt.decrypt(registerDTO.getEmailId().replaceAll("\\s", "+"));
		LOG.info("Decrypted Email" + decryptedEmail);
		LOG.info("Encrypted Token" + registerDTO.getToken().replaceAll("\\s", "+"));
		String decryptedToken = EncryptDecrypt.decrypt(registerDTO.getToken().replaceAll("\\s", "+"));
		LOG.info("Decrypted Token" + decryptedToken);

		ExpirationDataInfo expirationDataInfo = expirationDataInfoRepository.findByToken(decryptedToken);

		Date expiredDate = expirationDataInfo.getExpiredDate();
		LOG.info("expirationDataInfo.isTokenStatus()" + expirationDataInfo.isTokenStatus());
		Date date = new Date();

		if (expiredDate.before(date)) {
			return false;
		}
		registerDTO.setEmailId(expirationDataInfo.getEmailId());
		registerDTO.setToken(expirationDataInfo.getToken());
		return true;
	}

	public boolean isResetPassword(RegisterDTO registerDTO) {

		LOG.info("registerDTO.getToken()" + registerDTO.getToken().replaceAll("\\s", "+"));
		ExpirationDataInfo expirationDataInfo = expirationDataInfoRepository
				.findByToken(registerDTO.getToken().replaceAll("\\s", "+"));
		RegisterInfo registerInfo = registerInfoRepository
				.findRegisterInfoByEmailId(registerDTO.getEmailId().replaceAll("\\s", "+"));

		String changePassword = registerDTO.getPassword();
		String confirmChangePassword = registerDTO.getConfirmPassword();
		if (changePassword.equals(confirmChangePassword)) {
			try {
				String encryptPassword = EncryptDecrypt.encrypt(changePassword);

				if (encryptPassword != null) {

					registerInfo.setPassword(encryptPassword);
					registerInfoRepository.save(registerInfo);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			expirationDataInfo.setTokenStatus(true);
			expirationDataInfoRepository.save(expirationDataInfo);
			return true;
		}
		return false;
	}

	public boolean validateToAddress(TokenDTO tokenDTO) {
		Pattern pattern = Pattern.compile("^0x.{40}$");
		Matcher matcher = pattern.matcher(tokenDTO.getToAddress().trim());
		if (matcher.matches()) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isSameaddress(TokenDTO tokenDTO) {
		HttpSession session = SessionCollector.find(tokenDTO.getSessionId());

		String mail = (String) session.getAttribute("emailId");
		RegisterInfo registerInfo = registerInfoRepository.findRegisterInfoByEmailId(mail);

		try {
			String decryptedEtherWalletAddress = EncryptDecrypt.decrypt(registerInfo.getEtherWalletAddress());

			String[] fetchAddress = decryptedEtherWalletAddress.split("--");
			String getAddress = fetchAddress[fetchAddress.length - 1].split("\\.")[0];

			String etherWalletAddress = "0x" + getAddress;
			LOG.info("User Ether wallet address:" + tokenDTO.getToAddress());
			LOG.info("User Ether wallet address2:" + etherWalletAddress);

			if (etherWalletAddress.equalsIgnoreCase(tokenDTO.getToAddress())) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;

	}

	public boolean validateAdmin(TokenDTO tokenDTO) {
		HttpSession session = SessionCollector.find(tokenDTO.getSessionId());
		String mail = (String) session.getAttribute("emailId");
		RegisterInfo registerInfo = registerInfoRepository.findRegisterInfoByEmailId(mail);
		if (registerInfo.getRoleId() == 1) {
			return true;
		}
		return false;
	}

	public boolean validateAdminForUserList(RegisterDTO registerDTO) {
		HttpSession session = SessionCollector.find(registerDTO.getSessionId());
		String mail = (String) session.getAttribute("emailId");
		RegisterInfo registerInfo = registerInfoRepository.findRegisterInfoByEmailId(mail);
		if (registerInfo.getRoleId() == 1) {
			return true;
		}
		return false;
	}

	public boolean isSamePassword(TokenDTO tokenDTO) throws Exception {
		HttpSession session = SessionCollector.find(tokenDTO.getSessionId());
		String mail = (String) session.getAttribute("emailId");
		RegisterInfo registerInfo = registerInfoRepository.findRegisterInfoByEmailId(mail);
		LOG.info("EncryptDecrypt.decrypt(registerInfo.getEtherWalletPassword())"
				+ EncryptDecrypt.decrypt(registerInfo.getEtherWalletPassword()));
		LOG.info("tokenDTO.getEtherWalletPassword()" + EncryptDecrypt.decrypt(registerInfo.getEtherWalletPassword()));
		if (EncryptDecrypt.decrypt(registerInfo.getEtherWalletPassword()).equals(tokenDTO.getEtherWalletPassword())) {
			return true;
		} else {
			return false;
		}
	}

	public boolean etherValidation(TokenDTO tokenDTO) throws Exception {

		RegisterDTO registerDTO = new RegisterDTO();

		registerDTO.setSessionId(tokenDTO.getSessionId());
		BigDecimal balance = userRegisterService.etherBalance(registerDTO);
		LOG.info("balance :: Ether :: " + balance);

		if (balance.doubleValue() >= 0.01) {
			return true;
		} else {
			return false;
		}
	}

	public boolean tokenAmountValidation(TokenDTO tokenDTO) throws Exception {

		TokenInfo tokenInfo = tokenInfoRepository.findOne(1);

		BigDecimal balance = tokenInfo.getTotalInternalTokens();

		LOG.info("Amount Balance : " + balance);
		if (tokenDTO.getAmount() != null && tokenDTO.getAmount().toString().trim() != "") {

			double transferAmount = tokenDTO.getAmount().doubleValue();

			LOG.info("Amount to Transfer :" + transferAmount);

			if (transferAmount >= 0 && transferAmount <= balance.doubleValue()) {
				LOG.info("Inside Token Amount Validation");
				return true;
			}
			return false;
		}
		return false;
	}

	public boolean tokenAmountValidationForUser(TokenDTO tokenDTO) throws Exception {

		Double balance = solidityHandler.balanceTokens(tokenDTO);

		LOG.info("Amount Balance " + balance);
		if (tokenDTO.getAmount() != null && tokenDTO.getAmount().toString().trim() != "") {

			double transferAmount = tokenDTO.getAmount().doubleValue();

			LOG.info("Amount to Transfer :" + transferAmount);

			if (transferAmount > 0 && transferAmount <= balance) {
				LOG.info("Inside Token Amount Validation");
				return true;
			}
			return false;
		}
		return false;
	}

	public boolean ValidateTokenBalanceForBurn(TokenDTO tokenDTO) throws Exception {

		TokenInfo tokenInfo = tokenInfoRepository.findOne(1);

		BigDecimal balance = tokenInfo.getTotalInternalTokens();

		if (tokenDTO.getAmount() != null && tokenDTO.getAmount().toString().trim() != "") {

			double burningAmount = tokenDTO.getAmount().doubleValue();

			LOG.info("Amount to Transfer :" + burningAmount);
			if (burningAmount > 0 && burningAmount <= balance.doubleValue()) {
				LOG.info("Inside Token burn Validation");
				return true;
			}
			return false;
		}
		return false;
	}

	public TokenDTO listTransactions(TransactionHistory transaction) {
		LOG.info("transaction : " + transaction.getFromAddress());

		TokenDTO transactions = new TokenDTO();
		transactions.setFromAddress(transaction.getFromAddress());
		transactions.setToAddress(transaction.getToAddress());
		transactions.setTransactionAmount(transaction.getTransferAmount());
		transactions.setTransactionDate(transaction.getTransactionDate());
		transactions.setTransactionMode(transaction.getTransactionMode());
		transactions.setTransferStatus(transaction.getTransferStatus());
		return transactions;
	}

	public boolean validatePreICORegisteration(RegisterDTO registerDTO) {
		if (registerDTO.getUserName() != null && StringUtils.isNotBlank(registerDTO.getUserName())
				&& registerDTO.getEmailId() != null && StringUtils.isNotBlank(registerDTO.getEmailId())
				&& registerDTO.getMobileNo() != null && StringUtils.isNotBlank(registerDTO.getMobileNo())) {
			return true;
		}
		return false;
	}

	public boolean isAccountExistCheckByEmailId(String emailId) {
		Integer isEmailExit = preICORegisterInfoRepository.countPreICORegisterInfoByEmailIdIgnoreCase(emailId);
		if (isEmailExit > 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * Saving the PRE-ICO registration users and trigger email to the users as well as admin  
	 */

	public boolean savePreICORegisterUser(RegisterDTO registerDTO) {
		PreICORegisterInfo preICORegisterInfo = new PreICORegisterInfo();
		preICORegisterInfo.setUserName(registerDTO.getUserName());
		preICORegisterInfo.setEmailId(registerDTO.getEmailId());
		preICORegisterInfo.setCreatedDate(new Date());
		preICORegisterInfo.setMobileNo(registerDTO.getMobileNo());
		preICORegisterInfo.setRoleId(2);
		preICORegisterInfo.setActivation(true);
		preICORegisterInfoRepository.save(preICORegisterInfo);

		if (preICORegisterInfo.getId() != null) {

			String verificationLink = "Hi " + StringUtils.trim(registerDTO.getUserName()) + "," + "<br><br>"
					+ env.getProperty("email.content.user") + "<br><br>" + "UserName : "
					+ StringUtils.trim(registerDTO.getUserName()) + "<br>" + "Mobile No. : "
					+ StringUtils.trim(registerDTO.getMobileNo()) + "<br>" + "Email Id :"
					+ StringUtils.trim(registerDTO.getEmailId()) + "<br><br>" + "<br>" + "" + "With Regards,<br>"
					+ env.getProperty("aleef.team");

			boolean isEmailSent = emailNotificationServiceImpl.sendEmail(registerDTO.getEmailId(),
					env.getProperty("email.registration.reg"), verificationLink);

			if (isEmailSent) {
				String isEmail = "Hi," + "<br><br>" + env.getProperty("email.content.admin") + "<br><br>"
						+ "UserName : " + StringUtils.trim(registerDTO.getUserName()) + "<br>" + "Mobile No. : "
						+ StringUtils.trim(registerDTO.getMobileNo()) + "<br>" + "Email Id :"
						+ StringUtils.trim(registerDTO.getEmailId()) + "<br><br>" + "" + "With Regards," + "<br>"
						+ env.getProperty("aleef.team");

				emailNotificationServiceImpl.sendEmail("info@aleefcoin.io", env.getProperty("email.registration.reg"),
						isEmail);
			}
			return true;
		}

		return false;
	}

	public boolean validateContactUs(RegisterDTO registerDTO) {
		if (registerDTO.getUserName() != null && StringUtils.isNotBlank(registerDTO.getUserName())
				&& registerDTO.getEmailId() != null && StringUtils.isNotBlank(registerDTO.getEmailId())
				&& registerDTO.getMobileNo() != null && StringUtils.isNotBlank(registerDTO.getMobileNo())
				&& registerDTO.getSubject() != null && StringUtils.isNotBlank(registerDTO.getSubject())
				&& registerDTO.getDescription() != null && StringUtils.isNotBlank(registerDTO.getDescription())) {
			return true;
		}
		return false;
	}
	
	// Triggering email to the users as well as admin regarding users inquiry 

	public boolean contactUs(RegisterDTO registerDTO) {

		String isEmail = "Hi," + "<br><br>" + env.getProperty("email.content.contact.us") + "<br><br>" + "UserName : "
				+ StringUtils.trim(registerDTO.getUserName()) + "<br>" + "Mobile No. : "
				+ StringUtils.trim(registerDTO.getMobileNo()) + "<br>" + "Email Id : "
				+ StringUtils.trim(registerDTO.getEmailId()) + "<br>" + "Description :"
				+ StringUtils.trim(registerDTO.getDescription()) + "<br><br>" + "" + "With Regards," + "<br>"
				+ env.getProperty("aleef.team");

		boolean isEmailSent = emailNotificationServiceImpl.sendEmail("info@aleefcoin.io",
				"Reg :" + StringUtils.trim(registerDTO.getSubject()), isEmail);
		if (isEmailSent) {

			String email = "Hi " + StringUtils.trim(registerDTO.getUserName()) + "," + "<br><br>"
					+ env.getProperty("contact.user.content") + "<br><br>" + "UserName : "
					+ StringUtils.trim(registerDTO.getUserName()) + "<br>" + "Mobile No. : "
					+ StringUtils.trim(registerDTO.getMobileNo()) + "<br>" + "Email Id : "
					+ StringUtils.trim(registerDTO.getEmailId()) + "<br>" + "Description :"
					+ StringUtils.trim(registerDTO.getDescription()) + "<br><br>" + "" + "With Regards," + "<br>"
					+ env.getProperty("aleef.team");

			emailNotificationServiceImpl.sendEmail(registerDTO.getEmailId(), env.getProperty("email.contactus.reg"),
					email);

			return true;
		}
		return false;
	}

	public boolean validateTokenParam(TokenDTO tokenDTO) {
		if (tokenDTO.getRequestTokens() != null && tokenDTO.getRequestTokens().toString().trim() != ""
				&& tokenDTO.getRequestTokens() != 0 && tokenDTO.getEtherWalletPassword() != null
				&& StringUtils.isNotBlank(tokenDTO.getEtherWalletPassword())) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isCrowdsaleDateEnd(TokenDTO tokenDTO) {

		List<PurchaseCoinInfo> purchaseCoinInfos = (List<PurchaseCoinInfo>) purchaseCoinInfoRepository.findAll();

		if (new Date().after(purchaseCoinInfos.get(purchaseCoinInfos.size() - 1).getIcoEndDate())) {

			return true;
		}
		return false;
	}

	public boolean logoutParam(RegisterDTO registerDTO) {
		if (registerDTO.getSessionId() != null) {
			HttpSession session = SessionCollector.find(registerDTO.getSessionId());
			String email = (String) session.getAttribute("emailId");
			RegisterInfo registerInfo = registerInfoRepository.findRegisterInfoByEmailId(email);
			if (registerInfo != null) {
				HttpSessionEvent event = new HttpSessionEvent(session);
				sessionCollector.sessionDestroyed(event);
				session.invalidate();
				LOG.info("Invalidated");
				return true;
			}
			return false;
		}
		return false;
	}

	public boolean validateKYCParams(KycDTO kycDTO) {
		if (kycDTO.getDob() != null && StringUtils.isNotBlank(kycDTO.getDob()) && kycDTO.getAddress() != null
				&& StringUtils.isNotBlank(kycDTO.getAddress()) && kycDTO.getCity() != null
				&& StringUtils.isNotBlank(kycDTO.getCity()) && kycDTO.getEmailId() != null
				&& StringUtils.isNotBlank(kycDTO.getEmailId()) && kycDTO.getFullName() != null
				&& StringUtils.isNotBlank(kycDTO.getFullName()) && kycDTO.getGender() != null
				&& StringUtils.isNotBlank(kycDTO.getGender()) && kycDTO.getMobileNo() != null
				&& StringUtils.isNotBlank(kycDTO.getMobileNo())) {
			return true;
		}
		return false;
	}

	public boolean validateFileType(MultipartFile uploadedFileRef1, MultipartFile uploadedFileRef2) {
		String fileType1 = Files.getFileExtension(uploadedFileRef1.getOriginalFilename());
		String fileType2 = Files.getFileExtension(uploadedFileRef2.getOriginalFilename());
		if (fileType1.equalsIgnoreCase("jpeg") || fileType1.equalsIgnoreCase("png") || fileType1.equalsIgnoreCase("jpg")
				|| fileType1.equalsIgnoreCase("pdf") || fileType2.equalsIgnoreCase("jpeg")
				|| fileType2.equalsIgnoreCase("png") || fileType2.equalsIgnoreCase("jpg")
				|| fileType2.equalsIgnoreCase("pdf")) {
			return true;
		}
		return false;
	}

	public KycDTO listKYC(KycInfo kycInfo) {

		KycDTO kycList = new KycDTO();
		kycList.setId(kycInfo.getId());
		kycList.setFullName(kycInfo.getFullName());
		kycList.setDob(kycInfo.getDob());
		kycList.setCity(kycInfo.getCity());
		kycList.setCountry(kycInfo.getCountry());
		kycList.setAddress(kycInfo.getHomeAddress());
		kycList.setGender(kycInfo.getGender());
		kycList.setEmailId(kycInfo.getEmailId());
		kycList.setMobileNo(kycInfo.getPhoneNo());
		kycList.setKycDoc1Path(env.getProperty("apache.server") + kycInfo.getKycDoc1Path());

		if (!kycInfo.getKycDoc2Path().trim().equals("")) {
			kycList.setKycDoc2Path(env.getProperty("apache.server") + kycInfo.getKycDoc2Path());
		}
		kycList.setKycStatus(kycInfo.getKycStatus());

		return kycList;
	}

	public boolean validateUpdateKYCParams(KycDTO kycDTO) {
		if (kycDTO.getId() != null && StringUtils.isNotBlank(kycDTO.getId().toString()) && kycDTO.getKycStatus() != null
				&& StringUtils.isNotBlank(kycDTO.getKycStatus().toString())) {
			return true;
		}
		return false;
	}

	public boolean validateKYCParams(KycDTO kycDTO, MultipartFile kycDoc1) {
		LOG.info("Insede validateKYCParams");
		if (kycDTO.getDob() != null && StringUtils.isNotBlank(kycDTO.getDob()) && kycDTO.getAddress() != null
				&& StringUtils.isNotBlank(kycDTO.getAddress()) && kycDTO.getCity() != null
				&& StringUtils.isNotBlank(kycDTO.getCity()) && kycDTO.getCountry() != null
				&& StringUtils.isNotBlank(kycDTO.getCountry()) && kycDTO.getEmailId() != null
				&& StringUtils.isNotBlank(kycDTO.getEmailId()) && kycDTO.getFullName() != null
				&& StringUtils.isNotBlank(kycDTO.getFullName()) && kycDTO.getGender() != null
				&& StringUtils.isNotBlank(kycDTO.getGender()) && kycDTO.getMobileNo() != null
				&& StringUtils.isNotBlank(kycDTO.getMobileNo()) && kycDoc1 != null) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("unused")
	public boolean compareEmailId(KycDTO kycDTO) {
		HttpSession session = SessionCollector.find(kycDTO.getSessionId());
		String email = (String) session.getAttribute("emailId");

		RegisterInfo userModelInfo = registerInfoRepository.findRegisterInfoByEmailId(kycDTO.getEmailId());
		if (userModelInfo != null) {
			return true;
		}
		return false;
	}
	
	// Validating the KYC documents based on file extensions

	public boolean validateFileExtn(MultipartFile kycDoc1, MultipartFile kycDoc2) {

		String fileName1 = kycDoc1.getOriginalFilename();
		fileName1.substring(fileName1.lastIndexOf(".") + 1);

		if (kycDoc2 != null) {

			String fileName2 = kycDoc2.getOriginalFilename();
			fileName2.substring(fileName2.lastIndexOf(".") + 1);

			if (fileName1.substring(fileName1.lastIndexOf(".") + 1).equalsIgnoreCase("png")
					|| fileName1.substring(fileName1.lastIndexOf(".") + 1).equalsIgnoreCase("jpg")
					|| fileName1.substring(fileName1.lastIndexOf(".") + 1).equalsIgnoreCase("jpeg")
					|| fileName1.substring(fileName1.lastIndexOf(".") + 1).equalsIgnoreCase("pdf")) {
				if (fileName2.substring(fileName2.lastIndexOf(".") + 1).equalsIgnoreCase("png")
						|| fileName2.substring(fileName2.lastIndexOf(".") + 1).equalsIgnoreCase("jpg")
						|| fileName2.substring(fileName2.lastIndexOf(".") + 1).equalsIgnoreCase("jpeg")
						|| fileName2.substring(fileName2.lastIndexOf(".") + 1).equalsIgnoreCase("pdf")) {

					return true;
				}

			}

		} else {
			if (fileName1.substring(fileName1.lastIndexOf(".") + 1).equalsIgnoreCase("png")
					|| fileName1.substring(fileName1.lastIndexOf(".") + 1).equalsIgnoreCase("jpg")
					|| fileName1.substring(fileName1.lastIndexOf(".") + 1).equalsIgnoreCase("jpeg")
					|| fileName1.substring(fileName1.lastIndexOf(".") + 1).equalsIgnoreCase("pdf")) {
				return true;
			}

		}
		return false;
	}

	public boolean validateKYCDocParams(MultipartFile kycDoc1) {
		if (kycDoc1 != null) {
			return true;

		}
		return false;

	}

	public boolean isSessionExpired(TokenDTO tokenDTO) {
		try {
			HttpSession session = SessionCollector.find(tokenDTO.getSessionId());
			String email = (String) session.getAttribute("emailId");
			LOG.info("session emailId " + email);
			if (email != null) {
				tokenDTO.setEmailId(email);
				LOG.info("Session mail id " + tokenDTO.getEmailId());
				return true;
			}

		} catch (Exception e) {
			return false;
		}
		return false;
	}
	
	//Get admin dash board details

	public boolean adminDashboardDetails(RegisterDTO registerDTO) {

		HttpSession session = SessionCollector.find(registerDTO.getSessionId());

		String email = (String) session.getAttribute("emailId");

		RegisterInfo registerInfo = registerInfoRepository.findRegisterInfoByEmailId(email);

		registerInfo.getBurnTokens();

		registerDTO.setBurnTokens(new BigDecimal(registerInfo.getBurnTokens()));

		TokenInfo tokenInfo = tokenInfoRepository.findOne(1);
		registerDTO.setTotalTokens(tokenInfo.getTotalTokens());
		registerDTO.setIcoTokens(tokenInfo.getTotalIcoTokens());

		List<PurchaseCoinInfo> purchaseCoinInfos = (List<PurchaseCoinInfo>) purchaseCoinInfoRepository.findAll();
		List<RegisterDTO> listPurchaseTokens = new ArrayList<RegisterDTO>();
		Double a = 0.0;
		for (int i = purchaseCoinInfos.size(); i >= 1; i--) {

			PurchaseCoinInfo purchaseCoinInfo = purchaseCoinInfoRepository.findById(i);
			a = a + purchaseCoinInfo.getBalanceCoins().doubleValue();

			listPurchaseTokens.add(registerDTO);
		}
		LOG.info("Purchase Coins " + a);

		Double sold = tokenInfo.getTotalIcoTokens().doubleValue() - a;
		registerDTO.setSoldTokens(new BigDecimal(sold));

		return true;
	}

	public boolean isCrowdsaleDateStart(TokenDTO tokenDTO) {
		List<PurchaseCoinInfo> purchaseCoinInfos = (List<PurchaseCoinInfo>) purchaseCoinInfoRepository.findAll();

		LOG.info("purchaseCoinInfos.get(0).getIcoStartDate()) " + purchaseCoinInfos.get(0).getIcoStartDate());
		if (purchaseCoinInfos.get(0).getIcoStartDate().after(new Date())) {
			LOG.info(" Inside for loop");
			return true;
		}
		return false;
	}
	
	

	public boolean validateSlabs(TokenDTO tokenDTO) {
		List<PurchaseCoinInfo> purchaseCoinInfos = (List<PurchaseCoinInfo>) purchaseCoinInfoRepository.findAll();

		int id = 0;

		for (PurchaseCoinInfo purchaseCoinInfo : purchaseCoinInfos) {
			if (purchaseCoinInfo.getIcoStartDate().compareTo(new Date())
					* new Date().compareTo(purchaseCoinInfo.getIcoEndDate()) > 0) {
				LOG.info("purchaseCoinInfo.getIcoStartDate() " + purchaseCoinInfo.getIcoStartDate());
				LOG.info("purchaseCoinInfo.getIcoEndDate() " + purchaseCoinInfo.getIcoEndDate());

				id = purchaseCoinInfo.getId();

			}
		}

		PurchaseCoinInfo purchaseCoinInfo = purchaseCoinInfoRepository.findById(id);

		if (purchaseCoinInfo.getBalanceCoins().intValue() >= tokenDTO.getRequestTokens().intValue()) {

			return true;

		}
		return false;
	}

	public boolean transferTokenAdminApprovalValidation(TokenDTO tokenDTO) throws Exception {
		LOG.info(tokenDTO.getTransferId().replaceAll("\\s", "+"));
		String i = EncryptDecrypt.decrypt(tokenDTO.getTransferId().replaceAll("\\s", "+"));

		Integer id = Integer.valueOf(i);
		LOG.info("ID : " + id);
		TransactionHistory history = transactionHistoryRepository.findById(id);
		LOG.info("Status " + history.getTransferStatus());
		if (history.getTransferStatus().equalsIgnoreCase("Waiting For Approval")) {
			return true;
		}
		return false;
	}
	
	//Compare users KYC params

	public boolean compareKycParams(KycDTO kycDTO) {

		LOG.info("Inside compareKycParams");
		HttpSession session = SessionCollector.find(kycDTO.getSessionId());

		String email = (String) session.getAttribute("emailId");

		RegisterInfo registerInfo = registerInfoRepository.findRegisterInfoByEmailId(email);
		LOG.info("registerInfo.getEmailId()" + registerInfo.getEmailId());
		LOG.info("registerInfo.getMobileNo()" + registerInfo.getMobileNo());
		LOG.info(kycDTO.getEmailId());
		LOG.info(kycDTO.getMobileNo());

		if (registerInfo.getEmailId().equalsIgnoreCase(kycDTO.getEmailId())
				&& registerInfo.getMobileNo().equalsIgnoreCase(kycDTO.getMobileNo())) {
			return true;
		} else if (registerInfo.getEmailId().equalsIgnoreCase(kycDTO.getEmailId())) {
			return true;

		} else {
			return false;
		}
	}
	//Ether amount validation while purchasing as well as transferring aleef coin 

	@SuppressWarnings("unused")
	public boolean etherValidationForPurchase(TokenDTO tokenDTO) throws Exception {
		List<PurchaseCoinInfo> purchaseCoinInfos = (List<PurchaseCoinInfo>) purchaseCoinInfoRepository.findAll();
		PurchaseInfo purchaseInfo = new PurchaseInfo();

		BigDecimal aleefRateInUsd = null;

		RegisterDTO registerDTO = new RegisterDTO();

		registerDTO.setSessionId(tokenDTO.getSessionId());
		BigDecimal balance = userRegisterService.etherBalance(registerDTO);

		for (PurchaseCoinInfo purchaseCoinInfo : purchaseCoinInfos) {
			if (purchaseCoinInfo.getIcoStartDate().compareTo(new Date())
					* new Date().compareTo(purchaseCoinInfo.getIcoEndDate()) > 0) {

				aleefRateInUsd = purchaseCoinInfo.getAleefRateInUSD();

			}
		}
		LOG.info("Ether Balance " + balance);
		LOG.info("aleefRateInUsd " + aleefRateInUsd);

		double amountOfEther = (tokenDTO.getRequestTokens().intValue()
				* currentValueUtils.getEtherValuerFromCurrentAleefCoinValue(aleefRateInUsd));
		LOG.info("amountOfEther " + amountOfEther);
		if (balance.doubleValue() >= 0.01 + amountOfEther) {
			LOG.info("Amount Of Ether For Adding 0.01" + (0.01 + amountOfEther));
			return true;
		} else {
			return false;
		}

	}

	public boolean validatePin(RegisterDTO registerDTO) {

		if (StringUtils.isNotBlank(registerDTO.getSecurityKey())) {
			Pattern pattern = Pattern.compile("\\d{6}");
			Matcher matcher = pattern.matcher(registerDTO.getSecurityKey());
			if (matcher.matches()) {
				return true;
			} else
				return false;
		}
		return false;
	}

	public boolean validatePinConfirm(RegisterDTO registerDTO) {

		if (registerDTO.getSecurityKey().equals(registerDTO.getConfirmSecurityKey())) {
			return true;
		}
		return false;
	}

	public boolean resetSecurityPin(RegisterDTO registerDTO) {
		LOG.info("Inside resersecured email" + registerDTO.getEmailId());
		SecuredInfo securedInfo = securedInfoRepository.findSecuredInfoByEmailId(registerDTO.getEmailId().trim());

		if (securedInfo != null) {
			LOG.info("Inside resersecured email" + registerDTO.getSecurityKey());
			securedInfo.setSecuredKey(registerDTO.getSecurityKey());
			securedInfoRepository.save(securedInfo);
			return true;
		}
		return false;
	}

}
