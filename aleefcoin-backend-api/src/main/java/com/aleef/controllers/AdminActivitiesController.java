package com.aleef.controllers;

import javax.servlet.http.HttpSession;

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

import java.util.List;

import com.aleef.dtos.PurchaseCoinDTO;
import com.aleef.dtos.RegisterDTO;
import com.aleef.dtos.KycDTO;
import com.aleef.dtos.StatusResponseDTO;
import com.aleef.dtos.TokenDTO;
import com.aleef.services.UserRegisterService;
import com.aleef.session.SessionCollector;
import com.aleef.utils.UserUtils;
import com.google.gson.Gson;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping(value = "/aleef/api")
@Api(value = "AdminActivitiesController", description = "Admin Activities Controller Api")
@CrossOrigin

public class AdminActivitiesController {

	static final Logger LOG = LoggerFactory.getLogger(AdminActivitiesController.class);

	@Value("${upload.file.directory}")
	private String uploadDirectory;

	@Autowired
	private Environment env;

	@Autowired
	private UserUtils userUtils;

	@Autowired
	private UserRegisterService userRegisterService;
	
	/**
	 *  View KYC details of individual users 
	 *
	 */

	@CrossOrigin
	@RequestMapping(value = "/view/kyc", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Approve Or Reject KYC", notes = "Need to Approve Or Reject KYC")
	public synchronized ResponseEntity<String> viewUserKYC(
			@ApiParam(value = "Required KYC details", required = true) @RequestBody TokenDTO tokenDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		LOG.info("Inside /view/kyc");
		try {

			HttpSession sessions = SessionCollector.find(tokenDTO.getSessionId());

			LOG.info("Sesion value " + sessions);

			if (sessions == null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("session.expired"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			KycDTO kycInfo = userRegisterService.getKycDetails(tokenDTO);
			LOG.info("Kyc Value " + kycInfo);
			if (kycInfo != null) {
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("kyc.user.success"));
				statusResponseDTO.setKycUserInfo(kycInfo);
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
	
	/**
	 *  To update the KYC status-- Approve or Reject
	 * 
	 */

	@CrossOrigin
	@RequestMapping(value = "/update/kycstatus", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Approve Or Reject KYC", notes = "Need to Approve Or Reject KYC")
	public synchronized ResponseEntity<String> updateUserKYC(
			@ApiParam(value = "Required KYC details", required = true) @RequestBody KycDTO kycDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		LOG.info("Inside /update/kycstatus");

		try {
			HttpSession sessions = SessionCollector.find(kycDTO.getSessionId());

			LOG.info("Sesion value------>" + sessions);

			if (sessions == null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("session.expired"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean isUpdateKyc = userUtils.validateUpdateKYCParams(kycDTO);
			if (!isUpdateKyc) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("incorrectDetails"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isUpdated = userRegisterService.updateKYC(kycDTO);
			if (isUpdated) {
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(kycDTO.getMessage());
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			} else {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("kyc.update.failure"));
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
	
	/**
	 *  To view Token Balance in Admin Dashboard
	 * 
	 */

	@CrossOrigin
	@RequestMapping(value = "/get/icotoken/details", method = RequestMethod.GET, produces = { "application/json" })
	@ApiOperation(value = "Get ICO Token Details", notes = "Need Get ICO Token Details")
	public synchronized ResponseEntity<String> getIcoTokenDetails() {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		LOG.info("Inside /get/icotoken/details");

		try {
			List<PurchaseCoinDTO> getIcoTokenDetails = userRegisterService.getIcoTokenDetails();
			if (getIcoTokenDetails != null) {
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("get.icotoken.det.success"));
				statusResponseDTO.setIcoTokensDetList(getIcoTokenDetails);
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			} else {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("get.icotoken.det.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in getting ico token details : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
		}
	}
	
	/**
	 *  Filter user KYC details based on KYC status of individual users	 
	 */

	@CrossOrigin
	@RequestMapping(value = "/filter/kyc/status", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Get Filter Kyc Details Based On Status", notes = "Need Get Filter Kyc Details Based On Status")
	public synchronized ResponseEntity<String> filterKycStatus(
			@ApiParam(value = "Required KYC details", required = true) @RequestBody KycDTO kycDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		LOG.info("Inside /filter/kyc/status");

		try {

			HttpSession sessions = SessionCollector.find(kycDTO.getSessionId());
			if (sessions == null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("session.expired"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			List<KycDTO> kycList = userRegisterService.filterKyCStatus(kycDTO);

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
	 *  Filter user kyc details based on user name
	 *
	 */

	@CrossOrigin
	@RequestMapping(value = "/filter/kyc/username", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Get Filter Kyc Details Based On username", notes = "Need Get Filter Kyc Details Based On username")
	public synchronized ResponseEntity<String> filterKycUserName(
			@ApiParam(value = "Required KYC details", required = true) @RequestBody KycDTO kycDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		LOG.info("Inside /filter/kyc/username");

		try {

			HttpSession sessions = SessionCollector.find(kycDTO.getSessionId());
			if (sessions == null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("session.expired"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			List<KycDTO> kycList = userRegisterService.filterKycUserName(kycDTO);

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
	 * Get Token details of four level Referral
	 * 
	 */

	@CrossOrigin
	@RequestMapping(value = "/referral/admin", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Get Details Based On referral", notes = "Need Details Based On referral")
	public synchronized ResponseEntity<String> adminDashboardReferrals(
			@ApiParam(value = "Required referral details", required = true) @RequestBody RegisterDTO registerDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {

			HttpSession sessions = SessionCollector.find(registerDTO.getSessionId());
			if (sessions == null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("session.expired"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean list = userRegisterService.totalReferralForAdmin(registerDTO);

			if (list) {
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("list.referral.user.success"));
				statusResponseDTO.setReferralTokens(registerDTO);
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			} else {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("list.referral.user.fail"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in referral  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
		}

	}

}
