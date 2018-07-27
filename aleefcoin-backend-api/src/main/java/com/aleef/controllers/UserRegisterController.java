package com.aleef.controllers;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.aleef.dtos.LoginDTO;
import com.aleef.dtos.RegisterDTO;
import com.aleef.dtos.StatusResponseDTO;
import com.aleef.models.ConfigInfo;
import com.aleef.models.PurchaseCoinInfo;
import com.aleef.repo.ConfigInfoRepository;
import com.aleef.repo.PurchaseCoinInfoRepository;
import com.aleef.service.impl.EmailNotificationServiceImpl;
import com.aleef.services.UserRegisterService;
import com.aleef.session.SessionCollector;
import com.aleef.utils.EncryptDecrypt;
import com.aleef.utils.UserUtils;
import com.google.gson.Gson;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.aleef.dtos.KycDTO;
import com.aleef.dtos.TokenDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping(value = "/aleef/api")
@Api(value = "UserRegisterController", description = "User Register Controller Api")
@CrossOrigin

public class UserRegisterController {

	static final Logger LOG = LoggerFactory.getLogger(UserRegisterController.class);

	@Value("${upload.file.directory}")
	private String uploadDirectory;

	@Autowired
	private Environment env;

	@Autowired
	private UserUtils userUtils;

	@Autowired
	private UserRegisterService userRegisterService;

	@Autowired
	private ConfigInfoRepository configInfoRepository;

	@Autowired
	private EmailNotificationServiceImpl emailNotificationServiceImpl;

	@Autowired
	private PurchaseCoinInfoRepository purchaseCoinInfoRepository;

	/* New user Registeration */
	
	@SuppressWarnings("unused")
	@CrossOrigin
	@RequestMapping(value = "/register", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Register", notes = "Need to Register")
	public synchronized ResponseEntity<String> register(
			@ApiParam(value = "Register Members", required = true) @RequestBody RegisterDTO registerDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {
			boolean isValid = userUtils.validateRegisteration(registerDTO);
			if (!isValid) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("register.valid"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean isUserName = userUtils.validateUserName(registerDTO);
			if (!isUserName) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("register.useName.valid"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean isValidEmail = userUtils.validateEmail(registerDTO);
			if (!isValidEmail) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("register.email.valid"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isValidPassword = userUtils.validatePassword(registerDTO);
			if (!isValidPassword) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("register.password.valid"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isValidConfirmPassword = userUtils.validateConfirmPassword(registerDTO);
			if (!isValidConfirmPassword) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("register.confirm.password.valid"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isValidEtherPassword = userUtils.validateEtherWalletPassword(registerDTO);
			if (!isValidEtherPassword) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("register.ether.password.valid"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isValidConfirmEtherPassword = userUtils.validateConfirmEtherWalletPassword(registerDTO);
			if (!isValidConfirmEtherPassword) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("register.confirm.ether.password.valid"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isEmailExist = userRegisterService
					.isAccountExistCheckByEmailId(registerDTO.getEmailId().toLowerCase());
			if (isEmailExist) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("emailId.exist"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isEtherWallet = userRegisterService.isEtherWalletCreated(registerDTO);
			if (!isEtherWallet) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("ether.wallet.creation.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			String encryptedPassword = EncryptDecrypt.encrypt(registerDTO.getPassword());
			String isRegister = userRegisterService.saveRegisterInfo(registerDTO, encryptedPassword);

			ConfigInfo configInfo = configInfoRepository.findConfigInfoByConfigKey("WalletFile");
			String decryptedEtherWalletAddress = EncryptDecrypt.decrypt(registerDTO.getEtherWalletAddress());
			LOG.info("decryptedEtherWalletAddress::::::::::::::::::" + decryptedEtherWalletAddress);
			String etherWalletAddress = userUtils.getEtherWalletAddress(configInfo.getConfigValue(),
					decryptedEtherWalletAddress);

			String decryptedEtherWalletPassword = EncryptDecrypt.decrypt(registerDTO.getEtherWalletPassword());
			String encryptEmailId = EncryptDecrypt.encrypt(registerDTO.getEmailId());

			LOG.info("Encrypted Email Id:::::::::::::::" + encryptEmailId);

			String verificationLink = "Hi," + "<br><br>" + env.getProperty("email.content") + "<br><br>" + "UserName = "
					+ StringUtils.trim(registerDTO.getUserName()) + "<br>" + "Mobile No. = "
					+ StringUtils.trim(registerDTO.getMobileNo()) + "<br>" + "Ether Wallet Address = "
					+ StringUtils.trim(etherWalletAddress) + "<br><br>"
					+ " Use this pin at the time of login, keep this pin confindential do not share with anyone. "
					+ "<br>" + "Security pin : " + registerDTO.getSecurityKey() + "<br><br>" + "<a href='"
					+ env.getProperty("activation.url") + encryptEmailId + "'>"
					+ env.getProperty("verification.user.portal.url") + "</a><br><br>" + "<br>" + ""
					+ "With Regards,<br>" + env.getProperty("aleef.team");

			List<PurchaseCoinInfo> purchaseCoinInfos = (List<PurchaseCoinInfo>) purchaseCoinInfoRepository.findAll();

			for (PurchaseCoinInfo purchaseCoinInfo : purchaseCoinInfos) {
				if (purchaseCoinInfo.getIcoStartDate().compareTo(new Date())
						* new Date().compareTo(purchaseCoinInfo.getIcoEndDate()) > 0) {

					String verificationLink1 = "Hi " + StringUtils.trim(registerDTO.getUserName()) + "," + "<br><br>"
							+ "Hurry Up! Aleef Coin is offering coins at very best price. Don't miss this opportunity."
							+ "<br><br>" + "Aleef Coin Price : USD $"
							+ StringUtils.trim(purchaseCoinInfo.getAleefRateInUSD().toString()) + "<br>"
							+ "Free % of coins : "
							+ StringUtils.trim(purchaseCoinInfo.getFreeCoinPercentage().toString()) + " %" + "<br>"
							+ "Offer valid untill : "
							+ StringUtils.trim(purchaseCoinInfo.getIcoEndDateForMail().toString()) + "<br><p>"
							+ "<p><br><br><br>" + "With Regards,<br><br>" + "Support Team<br>" + "Aleef Coin<br>" + "email : info@aleefcoin.io";

					boolean isEmailSent1 = emailNotificationServiceImpl.sendEmail(registerDTO.getEmailId(),
							"Aleef Coin ICO calendar", verificationLink1);

				}
			}

			boolean isEmailSent = emailNotificationServiceImpl.sendEmail(registerDTO.getEmailId(),
					"Aleef App Registration", verificationLink);
			if (!isEmailSent) {

				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("register.emailSendFailed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			if (isRegister == null) {
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("register.portal.success"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			} else {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("register.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
		} catch (Exception e) {

			e.printStackTrace();
			LOG.error("Problem in User Registration: ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}
	
	/*Email verification for newly registered users*/	
	

	@CrossOrigin
	@RequestMapping(value = "/emailVerification", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Email Verification", notes = "Need to Verify Email")
	public synchronized ResponseEntity<String> emailVerification(
			@ApiParam(value = "Email Verification for Members", required = true) @RequestBody RegisterDTO registerDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {

			boolean isValidInputParams = userUtils.validateverificationParams(registerDTO);
			if (!isValidInputParams) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("incorrect.details"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isStatusActive = userUtils.isStatusActive(registerDTO);

			if (isStatusActive) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("email.verification.exist"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean validateEmailLink = userUtils.validateEmailLink(registerDTO);

			if (!validateEmailLink) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("email.verification.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			} else {
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("email.verification.successfully"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);

			}

		} catch (Exception e) {

			e.printStackTrace();
			LOG.error("Problem in User Email Verifications: ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}
	
	/* Login for users as well as admin */

	@CrossOrigin
	@RequestMapping(value = "/login", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "login admin/user", notes = "Need to get user details and login admin/user")
	public synchronized ResponseEntity<String> loginUser(
			@ApiParam(value = "Login admin/user", required = true) @RequestBody RegisterDTO registerDTO,
			HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {
			boolean isValidLogin = userUtils.validateLoginParam(registerDTO);
			if (!isValidLogin) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("incorrect.details"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean validEmailId = userRegisterService.isAccountExistCheckByEmailId(registerDTO.getEmailId());
			if (!validEmailId) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("invalid.emailId"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean validPassword = userUtils.validatePasswordForLogin(registerDTO);
			if (!validPassword) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("invalid.password"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean check = userUtils.checkByRoleId(registerDTO);
			if (!check) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("invalid.roleId"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isValidate = userUtils.validateActivation(registerDTO);
			if (!isValidate) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("login.valid.activation"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			LoginDTO responseDTO = userRegisterService.isEmailAndPasswordExit(registerDTO, request);
			if (responseDTO.getStatus().equalsIgnoreCase("failed")) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("login.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);

			}

			statusResponseDTO.setStatus(env.getProperty("success"));
			statusResponseDTO.setMessage(env.getProperty("login.success"));
			statusResponseDTO.setLoginInfo(responseDTO);
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);

		} catch (Exception e) {

			e.printStackTrace();
			LOG.error("Problem in User Login: ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}
	
	/**
	 * Google authentication secure login for users
	 * 
	 */

	@CrossOrigin
	@RequestMapping(value = "/login/secure", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "login admin/user", notes = "Need to get admin/user details and login admin/user")
	public synchronized ResponseEntity<String> SecureLoginUser(
			@ApiParam(value = "Login admin/user", required = true) @RequestBody RegisterDTO registerDTO,
			HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {
			boolean isValid = userUtils.validateSecurityKey(registerDTO);
			if (!isValid) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("valid.key"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean validate2FA = userUtils.validate2FA(registerDTO);
			if (!validate2FA) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("invalid.2FA"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			LoginDTO responseDTO = userRegisterService.secureLogin(registerDTO, request);
			if (responseDTO.getStatus().equalsIgnoreCase("failed")) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("login.failed"));
				statusResponseDTO.setLoginInfo(responseDTO);
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);

			}

			statusResponseDTO.setStatus(env.getProperty("success"));
			statusResponseDTO.setMessage(env.getProperty("login.success"));
			statusResponseDTO.setLoginInfo(responseDTO);
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);

		} catch (Exception e) {

			e.printStackTrace();
			LOG.error("Problem in User Login Secure: ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}
	
	/**
	 *  Change password for admin as well as users
	 *
	 */

	@CrossOrigin
	@RequestMapping(value = "/reset/password", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Reset password for admin/user", notes = "Reset password for admin/user")
	public synchronized ResponseEntity<String> resetPassword(
			@ApiParam(value = "Reset password for admin/user", required = true) @RequestBody RegisterDTO registerDTO,
			HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {
			HttpSession sessions = SessionCollector.find(registerDTO.getSessionId());
			if (sessions == null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("session.expired"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean check = userUtils.checkByRoleIdForReset(registerDTO);
			if (!check) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("invalid.roleId"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isOldPassword = userRegisterService.isOldPassword(registerDTO);
			if (!isOldPassword) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("validate.old.password"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isValidPassword = userUtils.validateResetPassword(registerDTO);
			if (!isValidPassword) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("change.validate.password"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);

			}

			boolean validateOldAndNewPassword = userUtils.validateOldAndNewPassword(registerDTO);
			if (!validateOldAndNewPassword) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("change.validate.old.new.password"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);

			}

			String email = (String) sessions.getAttribute("emailId");
			registerDTO.setEmailId(email);
			boolean valid2FA = userUtils.validate2FA(registerDTO);
			if (!valid2FA) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("invalid.2FA"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);

			}

			boolean changePassword = userRegisterService.isChangePassword(registerDTO);
			if (!changePassword) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("change.password.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			} else {
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("change.password"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			}

		} catch (Exception e) {

			e.printStackTrace();
			LOG.error("Problem in User/Admin Reset Password: ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}
	
	/**
	 * Trigger a email to the users and admin regarding to forgot password 
	 *
	 * 
	 */

	@CrossOrigin
	@RequestMapping(value = "/forgot/password/emailVerification", method = RequestMethod.POST, produces = {
			"application/json" })
	@ApiOperation(value = "Forgot password for admin/user", notes = "Forgot password for admin/user")
	public synchronized ResponseEntity<String> forgotPassword(
			@ApiParam(value = "Forgot password for admin/user", required = true) @RequestBody RegisterDTO registerDTO,
			HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {

			boolean isValid = userUtils.isValidEmailForVerification(registerDTO);
			if (!isValid) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("invalid.emailId"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isValidate = userUtils.validateActivation(registerDTO);
			if (!isValidate) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("login.valid.activation"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean check = userUtils.checkByRoleId(registerDTO);
			if (!check) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("invalid.roleId"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isEmailSend = userRegisterService.isSendEmail(registerDTO);
			if (!isEmailSend) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("email.sending.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			statusResponseDTO.setStatus(env.getProperty("success"));
			statusResponseDTO.setMessage(env.getProperty("password.update"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);

		} catch (Exception e) {

			e.printStackTrace();
			LOG.error("Problem in User/Admin Forgot Password: ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}
	
	/**
	 * Trigger a mail with the verification link regarding to forgot password
	 * 
	 */

	@CrossOrigin
	@RequestMapping(value = "/forgot/password/emailLinkVerification", method = RequestMethod.POST, produces = {
			"application/json" })
	@ApiOperation(value = "Forgot password for admin/user", notes = "Forgot password for admin/user")
	public synchronized ResponseEntity<String> forgotPasswordLinkVerification(
			@ApiParam(value = "Forgot password for admin/user", required = true) @RequestBody RegisterDTO registerDTO,
			HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {
			boolean isResetPasswordLinkVerified = userUtils.isResetPasswordLinkVerified(registerDTO);
			if (!isResetPasswordLinkVerified) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("link.verified"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isTime = userUtils.validateTime(registerDTO);
			if (!isTime) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("time.validate.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			statusResponseDTO.setStatus(env.getProperty("success"));
			statusResponseDTO.setMessage(env.getProperty("password.reset.update"));
			statusResponseDTO.setLinkVerificationInfo(registerDTO);
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);

		} catch (Exception e) {

			e.printStackTrace();
			LOG.error("Problem in User/Admin Forgot Password Link Verification : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}
	
	 /**
	  * Change the existing password for users and admin
	  * 
	  */

	@CrossOrigin
	@RequestMapping(value = "/forgot/password/reset", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Forgot password Reset for admin/user", notes = "Forgot password Reset for admin/user")
	public synchronized ResponseEntity<String> forgotPasswordReset(
			@ApiParam(value = "Forgot password Reset for admin/user", required = true) @RequestBody RegisterDTO registerDTO,
			HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {

			boolean isValidPassword = userUtils.validateResetPassword(registerDTO);
			if (!isValidPassword) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("change.validate.password"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean validate2FA = userUtils.validate2FA(registerDTO);
			if (!validate2FA) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("invalid.2FA"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean changePassword = userUtils.isResetPassword(registerDTO);
			if (!changePassword) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("change.password.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			} else {
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("change.password"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			}

		} catch (Exception e) {

			e.printStackTrace();
			LOG.error("Problem in User/Admin Forgot Password Reset: ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}
	
	/**
	 * Social media login for already register users in the face book as well as G+
	 * 
	 */

	@CrossOrigin
	@RequestMapping(value = "/social/media/login", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Social Media Login for admin/user", notes = "Social Media Login for admin/user")
	public synchronized ResponseEntity<String> socialMediaLogin(
			@ApiParam(value = "Social Media Login for admin/user", required = true) @RequestBody RegisterDTO registerDTO,
			HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {
			LOG.info("EmailId :::::" + registerDTO.getEmailId());
			if (registerDTO.getEmailId() == null || registerDTO.getEmailId() == "") {
				LoginDTO loginDTO = new LoginDTO();
				loginDTO.setEmailPopUpStatus("true");
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("popup.open"));
				statusResponseDTO.setLoginInfo(loginDTO);
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			}

			boolean isValidEmail = userUtils.validateEmail(registerDTO);
			if (!isValidEmail) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("register.email.valid"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isEmailExist = userRegisterService
					.isAccountExistCheckByEmailId(registerDTO.getEmailId().toLowerCase());
			if (isEmailExist) {
				LoginDTO responseDTO = userRegisterService.isAccountExistCheckByEmailIdForSocialMediaLogin(registerDTO,
						request);
				if (responseDTO.getStatus().equalsIgnoreCase("failed")) {
					statusResponseDTO.setStatus(env.getProperty("failure"));
					statusResponseDTO.setMessage(env.getProperty("login.failed.byroleid"));
					return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
				}
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("login.success"));
				statusResponseDTO.setLoginInfo(responseDTO);
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			} else {
				if (registerDTO.getEtherWalletPassword() == null || registerDTO.getEtherWalletPassword() == ""
						|| registerDTO.getConfirmEtherWalletPassword() == null
						|| registerDTO.getConfirmEtherWalletPassword() == "") {
					LoginDTO loginDTO = new LoginDTO();
					LOG.info("Entering into pop up status");
					loginDTO.setPopUpStatus("true");
					statusResponseDTO.setStatus(env.getProperty("success"));
					statusResponseDTO.setMessage(env.getProperty("popup.open"));
					statusResponseDTO.setLoginInfo(loginDTO);
					return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
				}
			}

			boolean isValidEtherPassword = userUtils.validateEtherWalletPassword(registerDTO);
			if (!isValidEtherPassword) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("register.ether.password.valid"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isValidConfirmEtherPassword = userUtils.validateConfirmEtherWalletPassword(registerDTO);
			if (!isValidConfirmEtherPassword) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("register.confirm.ether.password.valid"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			LoginDTO responseDTO = userRegisterService.newSocialMediaRegisterAndLogin(registerDTO, request);
			if (responseDTO == null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("login.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			statusResponseDTO.setStatus(env.getProperty("success"));
			statusResponseDTO.setMessage(env.getProperty("login.success"));
			statusResponseDTO.setLoginInfo(responseDTO);
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in User/Admin Social media Login : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}
	
	/**
	 *  Check ether balance for users as well as admin
	 * 
	 */

	@CrossOrigin
	@RequestMapping(value = "/ether/balance", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Ether Balance for admin/user", notes = "Ether Balance for admin/user")
	public synchronized ResponseEntity<String> etherBalance(
			@ApiParam(value = "Ether Balance for admin/user", required = true) @RequestBody RegisterDTO registerDTO,
			HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {
			HttpSession sessions = SessionCollector.find(registerDTO.getSessionId());
			if (sessions == null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("session.expired"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			BigDecimal isValid = userRegisterService.etherBalance(registerDTO);
			if (isValid == null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("etherBalance.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			statusResponseDTO.setStatus(env.getProperty("success"));
			statusResponseDTO.setMessage(env.getProperty("etherBalance.success"));
			statusResponseDTO.setEtherBalanceInfo(registerDTO);
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in User/Admin Ether Balance : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}
	
	/*Users Registration for pre ico*/

	@CrossOrigin
	@RequestMapping(value = "/pre/register/ico", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Register", notes = "Need to Register")
	public synchronized ResponseEntity<String> preRegisterICO(
			@ApiParam(value = "Register Members", required = true) @RequestBody RegisterDTO registerDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {

			boolean isValid = userUtils.validatePreICORegisteration(registerDTO);
			if (!isValid) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("register.valid"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isUserName = userUtils.validateUserName(registerDTO);
			if (!isUserName) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("register.useName.valid"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isValidEmail = userUtils.validateEmail(registerDTO);
			if (!isValidEmail) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("register.email.valid"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isEmailExist = userUtils.isAccountExistCheckByEmailId(registerDTO.getEmailId().toLowerCase());
			if (isEmailExist) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("emailId.exist"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isSave = userUtils.savePreICORegisterUser(registerDTO);
			if (!isSave) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("register.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			statusResponseDTO.setStatus(env.getProperty("success"));
			statusResponseDTO.setMessage(env.getProperty("register.success"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in User/Admin Ether Balance : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}
	
	/* Users inquiry for policies aleef coin */

	@CrossOrigin
	@RequestMapping(value = "/contact/us", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Register", notes = "Need to Register")
	public synchronized ResponseEntity<String> contactUs(
			@ApiParam(value = "Register Members", required = true) @RequestBody RegisterDTO registerDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {

			boolean isValids = userUtils.validateContactUs(registerDTO);
			if (!isValids) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("register.valid"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isUserName = userUtils.validateUserName(registerDTO);
			if (!isUserName) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("register.useName.valid"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isValidEmail = userUtils.validateEmail(registerDTO);
			if (!isValidEmail) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("register.email.valid"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isValid = userUtils.contactUs(registerDTO);
			if (!isValid) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("contact.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			statusResponseDTO.setStatus(env.getProperty("success"));
			statusResponseDTO.setMessage(env.getProperty("contact.success"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in User/Admin Ether Balance : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}
	
	/*Listing the all users who are registered in aleef portal*/

	@CrossOrigin
	@RequestMapping(value = "/users/list", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Users List", notes = "Need to show users list")
	public synchronized ResponseEntity<String> usersList(
			@ApiParam(value = "Users List", required = true) @RequestBody RegisterDTO registerDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {

			HttpSession sessions = SessionCollector.find(registerDTO.getSessionId());
			if (sessions == null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("session.expired"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean validateAdmin = userUtils.validateAdminForUserList(registerDTO);
			if (!validateAdmin) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("validate.admin.user"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			List<RegisterDTO> isList = userRegisterService.listUsers();
			if (isList == null) {

				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("list.failed.user"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			statusResponseDTO.setStatus(env.getProperty("success"));
			statusResponseDTO.setMessage(env.getProperty("list.success.user"));
			statusResponseDTO.setListUsers(isList);
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in UserList : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}

	/**
	 * Aleef Portal log out for admin and users
	 * 
	 */
	
	@CrossOrigin
	@RequestMapping(value = "/logout", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "User Logout", notes = "Need to Logout")
	public synchronized ResponseEntity<String> logoutUser(
			@ApiParam(value = "User Logout", required = true) @RequestBody RegisterDTO registerDTO,
			HttpServletRequest request) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {

			HttpSession session = SessionCollector.find(registerDTO.getSessionId());
			if (session == null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("session.expired"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean islogout = userUtils.logoutParam(registerDTO);
			if (!islogout) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("logout.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			statusResponseDTO.setStatus(env.getProperty("success"));
			statusResponseDTO.setMessage(env.getProperty("logout"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in Logout  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}
	
	/*Get admin details to show over the admin dashboard*/

	@CrossOrigin
	@RequestMapping(value = "/admin/dashboard/details", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Admin DashBoard Details", notes = "Need to get Admin DashBoard Details")
	public synchronized ResponseEntity<String> adminDashboardDetails(
			@ApiParam(value = "Admin DashBoard Details", required = true) @RequestBody RegisterDTO registerDTO,
			HttpServletRequest request) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {

			HttpSession session = SessionCollector.find(registerDTO.getSessionId());
			if (session == null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("session.expired"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isValid = userUtils.adminDashboardDetails(registerDTO);
			if (!isValid) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("details.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			statusResponseDTO.setStatus(env.getProperty("success"));
			statusResponseDTO.setMessage(env.getProperty("details.success"));
			statusResponseDTO.setAdminDashboardDetails(registerDTO);
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in Logout  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}
	
	/* Upload users KYC details for approval*/

	@CrossOrigin
	@RequestMapping(value = "/uploadkyc", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "register account", notes = "Need to get user details")
	public synchronized ResponseEntity<String> userKycVerification(
			@ApiParam(value = "Required user details", required = true) @RequestParam(name = "userInfo", value = "userInfo", required = true) String kycDTOStr,
			@ApiParam(value = "Required file attachment", required = true) @RequestParam(name = "kycDoc1", value = "kycDoc1", required = false) MultipartFile kycDoc1,
			@ApiParam(value = "Required file attachment", required = true) @RequestParam(name = "kycDoc2", value = "kycDoc2", required = false) MultipartFile kycDoc2) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		LOG.info("######################  Inside /uploadkyc %%%%%%%%%%%%%%%%%%%%%%");
		try {

			ObjectMapper mapper = new ObjectMapper();
			KycDTO kycDTO = null;

			try {
				kycDTO = mapper.readValue(kycDTOStr, KycDTO.class);
			} catch (Exception e) {
				LOG.error("Catch block---->" + e.toString());
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("validate.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			HttpSession sessions = SessionCollector.find(kycDTO.getSessionId());

			LOG.info("Sesion value------>" + sessions);

			if (sessions == null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("session.expired"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isValidParams = userUtils.validateKYCParams(kycDTO, kycDoc1);
			{
				if (!isValidParams) {
					statusResponseDTO.setStatus(env.getProperty("failure"));
					statusResponseDTO.setMessage(env.getProperty("validate.failed"));
					return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
				}

				boolean isCompareEmailId = userUtils.compareEmailId(kycDTO);
				{
					if (!isCompareEmailId) {
						statusResponseDTO.setStatus(env.getProperty("failure"));
						statusResponseDTO.setMessage(env.getProperty("compare.email.failed"));
						return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO),
								HttpStatus.PARTIAL_CONTENT);
					}
				}

				boolean isVAlidFileExtn = userUtils.validateFileExtn(kycDoc1, kycDoc2);
				{
					if (!isVAlidFileExtn) {
						statusResponseDTO.setStatus(env.getProperty("failure"));
						statusResponseDTO.setMessage(env.getProperty("upload.req.extension"));
						return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO),
								HttpStatus.PARTIAL_CONTENT);
					}
				}

				LOG.info("Mail id before Upload ------>" + kycDTO.getEmailId());
				if (kycDoc1 != null && kycDoc2 != null) {
					LOG.info("Both available kycDoc1 != null && kycDoc2 != null ###############");
					String kycDoc1FilePath = userRegisterService.userDocumentUpload(kycDoc1, kycDTO.getEmailId(),
							"KYCDoc1");
					String kycDoc2FilePath = userRegisterService.userDocumentUpload(kycDoc2, kycDTO.getEmailId(),
							"KYCDoc2");

					LOG.info("panIdFilePath : " + kycDoc1FilePath);
					if (kycDoc1FilePath == null || kycDoc2FilePath == null) {
						statusResponseDTO.setStatus(env.getProperty("failure"));
						statusResponseDTO.setMessage("Please Upload the Required Document");

						return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO),
								HttpStatus.PARTIAL_CONTENT);
					}

					boolean isSaved = userRegisterService.saveKycInfo(kycDTO, kycDoc1FilePath, kycDoc2FilePath, kycDoc1,
							kycDoc2);
					if (!isSaved) {
						statusResponseDTO.setStatus(env.getProperty("failure"));
						statusResponseDTO.setMessage(env.getProperty("failure.save"));
						return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO),
								HttpStatus.PARTIAL_CONTENT);
					} else {
						statusResponseDTO.setStatus(env.getProperty("success"));
						statusResponseDTO.setMessage(env.getProperty("kyc.success"));
						statusResponseDTO.setKycInfo(kycDTO);
						return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
					}

				} else if (kycDoc1 != null) {
					LOG.info("One available kycDoc1 != null ###############");
					String kycDoc1FilePath = userRegisterService.userDocumentUpload(kycDoc1, kycDTO.getEmailId(),
							"KYCDoc1");

					LOG.info("panIdFilePath : " + kycDoc1FilePath);
					if (kycDoc1FilePath == null) {
						statusResponseDTO.setStatus(env.getProperty("failure"));
						statusResponseDTO.setMessage("Please Upload the Required Document");
						return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO),
								HttpStatus.PARTIAL_CONTENT);
					}
					String kycDoc2FilePath = "";
					boolean isSaved = userRegisterService.saveKycInfo(kycDTO, kycDoc1FilePath, kycDoc2FilePath, kycDoc1,
							kycDoc2);
					if (!isSaved) {
						statusResponseDTO.setStatus(env.getProperty("failure"));
						statusResponseDTO.setMessage(env.getProperty("failure.save"));
						return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO),
								HttpStatus.PARTIAL_CONTENT);
					} else {
						statusResponseDTO.setStatus(env.getProperty("success"));
						statusResponseDTO.setMessage(env.getProperty("kyc.success"));
						statusResponseDTO.setKycInfo(kycDTO);
						return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
					}
				} else {
					statusResponseDTO.setStatus(env.getProperty("failure"));
					statusResponseDTO.setMessage(env.getProperty("kyc.failure"));
					return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in uploadkyc  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
		}

	}
	
	/**
	 * Listing the all user kyc details
	 *
	 */

	@CrossOrigin
	@RequestMapping(value = "/list/kyc", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "List All KYC", notes = "Need to list out all the KYC uploaded")
	public synchronized ResponseEntity<String> getAllKYC(
			@ApiParam(value = "Required KYC details", required = true) @RequestBody TokenDTO tokenDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();

		try {

			HttpSession sessions = SessionCollector.find(tokenDTO.getSessionId());
			if (sessions == null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("session.expired"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			List<KycDTO> kycList = userRegisterService.kycList();
			if (kycList != null) {
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("kyc.list"));
				statusResponseDTO.setKycList(kycList);
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			} else {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("kyc.not.exist"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in KYC List : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
		}
	}
	
	/**
	 * Trigger a email to refer a new users with the referral link
	 * 
	 */

	@CrossOrigin
	@RequestMapping(value = "/referral", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Refer the person", notes = "Need to refer the person")
	public synchronized ResponseEntity<String> referPerson(
			@ApiParam(value = "Refer the person", required = true) @RequestBody RegisterDTO registerDTO,
			HttpServletRequest request) throws InterruptedException {

		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {
			HttpSession session = SessionCollector.find(registerDTO.getSessionId());
			if (session == null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("session.expired"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isValidEmail = userUtils.validateEmail(registerDTO);
			if (!isValidEmail) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("register.email.valid"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isEmailExist = userRegisterService
					.isAccountExistCheckByEmailId(registerDTO.getEmailId().toLowerCase());
			if (isEmailExist) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("emailId.exist.refer"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isValid = userRegisterService.referral(registerDTO);
			if (!isValid) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("refer.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			statusResponseDTO.setStatus(env.getProperty("success"));
			statusResponseDTO.setMessage(env.getProperty("refer.success"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in referral  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}
	
	/**
	 * Listing the  four levels referral users
	 *
	 */

	@CrossOrigin
	@RequestMapping(value = "/get/referrals", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Retrieve Refer", notes = "Need to retrieve Refer")
	public synchronized ResponseEntity<String> getReferrals(
			@ApiParam(value = "Retrieve Refer", required = true) @RequestBody RegisterDTO registerDTO,
			HttpServletRequest request) {
		LOG.info("Inside /get/referrals");
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {

			HttpSession sessions = SessionCollector.find(registerDTO.getSessionId());
			if (sessions == null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("session.expired"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			List<String>[] i = userRegisterService.getReferralDetails(registerDTO);

			if (i != null) {
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("get.referral.success"));
				statusResponseDTO.setAllLevelsRefNames(i);
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			} else {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("get.referral.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in retrieve referral details  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}

	}
	
	// Change security pin for all individual users

	@CrossOrigin
	@RequestMapping(value = "/reset/security/pin", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Forgot security Pin", notes = "Need to Reset Forgot security Pin")
	public synchronized ResponseEntity<String> forgotSecurityPin(
			@ApiParam(value = "Forgot security Pin", required = true) @RequestBody RegisterDTO registerDTO,
			HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {

			boolean isValid = userUtils.isValidEmailForVerification(registerDTO);
			if (!isValid) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("invalid.emailId"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean check = userUtils.checkByRoleId(registerDTO);
			if (!check) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("invalid.roleId"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean validPassword = userUtils.validatePasswordForLogin(registerDTO);
			if (!validPassword) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("invalid.password"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean validate = userUtils.validatePin(registerDTO);
			if (!validate) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("invalid.pin"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean checkPin = userUtils.validatePinConfirm(registerDTO);
			if (!checkPin) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("invalid.pin.check"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean reset = userUtils.resetSecurityPin(registerDTO);
			if (!reset) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("reset.pin.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			} else {
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("reset.pin.success"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in security pin change  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}

	}
	
	/**
	 *  Filter the existing users based on email id and user name
	 * 
	 */

	@CrossOrigin
	@RequestMapping(value = "/user/list/filter", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "List User Filter", notes = "Need to show List User Filter")
	public synchronized ResponseEntity<String> listUserFilter(
			@ApiParam(value = "List User Filter", required = true) @RequestBody RegisterDTO registerDTO,
			HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {

			HttpSession sessions = SessionCollector.find(registerDTO.getSessionId());
			if (sessions == null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("session.expired"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			List<RegisterDTO> filtered = userRegisterService.userListFilters(registerDTO);
			if (filtered == null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("list.filter.user.fail"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			} else {
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("list.filter.user.success"));
				statusResponseDTO.setListUsers(filtered);
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in retrieve referral details  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}

	}
	
	/**
	 *  Get four levels individual users, user names
	 * 
	 */

	@CrossOrigin
	@RequestMapping(value = "/refferal/points/list", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "List User refferal points list", notes = "Need to show List refferal User points list")
	public synchronized ResponseEntity<String> listReferralPointsList(
			@ApiParam(value = "List User refferal points list", required = true) @RequestBody RegisterDTO registerDTO,
			HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {

			HttpSession sessions = SessionCollector.find(registerDTO.getSessionId());
			if (sessions == null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("session.expired"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean list = userRegisterService.userReferralPointsList(registerDTO);
			if (!list) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("list.referral.user.fail"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			} else {
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("list.referral.user.success"));
				statusResponseDTO.setReferralTokens(registerDTO);
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in retrieve referral details for levels in user dashboards : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}
	
	/**
	 *  Showing the individual user kyc details
	 * 
	 */

	@CrossOrigin
	@RequestMapping(value = "/view/user/kyc", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "View User Kyc Details", notes = "Need to View User Kyc Details")
	public synchronized ResponseEntity<String> viewUserKYCDetails(
			@ApiParam(value = "View User Kyc Details", required = true) @RequestBody TokenDTO tokenDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		LOG.info("Inside /view/user/kyc");
		try {

			HttpSession sessions = SessionCollector.find(tokenDTO.getSessionId());
			if (sessions == null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("session.expired"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			KycDTO kycInfo = userRegisterService.getUserKycDetails(tokenDTO);
			if (kycInfo != null) {
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("kyc.user.success"));
				statusResponseDTO.setKycInfo(kycInfo);
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			} else {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("kyc.user.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in KYC update : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
		}

	}

}
