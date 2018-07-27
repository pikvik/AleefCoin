package com.aleef.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.aleef.dtos.StatusResponseDTO;
import com.aleef.dtos.TokenDTO;
import com.aleef.services.TokenService;
import com.aleef.session.SessionCollector;
import com.aleef.utils.UserUtils;
import com.google.gson.Gson;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@RestController
@RequestMapping(value = "/aleef/api")
@Api(value = "tokenController", description = "Token Controller Api")
@CrossOrigin

public class TokenController {

	static final Logger LOG = LoggerFactory.getLogger(TokenController.class);

	@Autowired
	private Environment env;

	@Autowired
	private UserUtils userUtils;

	@Autowired
	private TokenService tokenService;

	/**
	 *  Transfer tokens from one wallet to another wallet
	 * 
	 */

	@CrossOrigin
	@RequestMapping(value = "/token/transfer", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Transfer Token", notes = "Need to Transfer Token")
	public synchronized ResponseEntity<String> tokenTransfer(
			@ApiParam(value = "Transfer Token", required = true) @RequestBody TokenDTO tokenDTO,
			HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {
			HttpSession sessions = SessionCollector.find(tokenDTO.getSessionId());
			if (sessions == null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("session.expired"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isValidateTokenName = userUtils.validateToAddress(tokenDTO);
			if (!isValidateTokenName) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("valid.tokenAddress"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isSameAddress = userUtils.isSameaddress(tokenDTO);
			if (isSameAddress) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("same.address"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isSamePassword = userUtils.isSamePassword(tokenDTO);
			if (!isSamePassword) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("same.password"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean etherValidation = userUtils.etherValidation(tokenDTO);
			if (!etherValidation) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("ether.balance"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean tokenAmountValidation = userUtils.tokenAmountValidation(tokenDTO);
			if (!tokenAmountValidation) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("token.amount.validation"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isValid = tokenService.transferTokenAdmin(tokenDTO);
			if (!isValid) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("token.transfer.failed.approval"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			statusResponseDTO.setStatus(env.getProperty("success"));
			statusResponseDTO.setMessage(env.getProperty("token.transfer.approval"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in Token Transfer : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}

	/**
	 *  After getting the approval from second admin , need to transfer tokens to
	 *   the appropriate user
	 */
	

	@CrossOrigin
	@RequestMapping(value = "/token/transfer/approval", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Transfer Token Approval", notes = "Need to Transfer Token Approval")
	public synchronized ResponseEntity<String> tokenTransferApproval(
			@ApiParam(value = "Transfer Token Approval", required = true) @RequestBody TokenDTO tokenDTO,
			HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {

			boolean isValids = userUtils.transferTokenAdminApprovalValidation(tokenDTO);
			if (!isValids) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("token.transfer.again.approval"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isValid = tokenService.transferTokenAdminApproval(tokenDTO);
			if (!isValid) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("token.transfer.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			statusResponseDTO.setStatus(env.getProperty("success"));
			statusResponseDTO.setMessage(env.getProperty("token.transfer"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in Token Transfer Approval : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}

	/**
	 *  User to user token transfer
	 *
	 */

	@CrossOrigin
	@RequestMapping(value = "/token/transfer/user", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Transfer Token to user", notes = "Need to Transfer Token to user")
	public synchronized ResponseEntity<String> tokenTransferToUser(
			@ApiParam(value = "Transfer Token to user", required = true) @RequestBody TokenDTO tokenDTO,
			HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {
			HttpSession sessions = SessionCollector.find(tokenDTO.getSessionId());
			if (sessions == null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("session.expired"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isValidateTokenName = userUtils.validateToAddress(tokenDTO);
			if (!isValidateTokenName) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("valid.tokenAddress"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isSameAddress = userUtils.isSameaddress(tokenDTO);
			if (isSameAddress) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("same.address"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isSamePassword = userUtils.isSamePassword(tokenDTO);
			if (!isSamePassword) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("same.password"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean etherValidation = userUtils.etherValidation(tokenDTO);
			if (!etherValidation) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("ether.balance"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean tokenAmountValidation = userUtils.tokenAmountValidationForUser(tokenDTO);
			if (!tokenAmountValidation) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("token.amount.validation"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			String isValid = tokenService.transferToken(tokenDTO);
			if (isValid == null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("token.transfer.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			statusResponseDTO.setStatus(env.getProperty("success"));
			statusResponseDTO.setMessage(env.getProperty("token.transfer"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in Token Transfer : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}

	/**
	 *  Check the token balance of user wallet address
	 * 
	 */

	@CrossOrigin
	@RequestMapping(value = "/token/balance", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Balance Token", notes = "Need to get Balance Token")
	public synchronized ResponseEntity<String> tokenBalance(
			@ApiParam(value = "Balance Token", required = true) @RequestBody TokenDTO tokenDTO,
			HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {
			HttpSession sessions = SessionCollector.find(tokenDTO.getSessionId());
			if (sessions == null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("session.expired"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			Double isValid = tokenService.balanceTokens(tokenDTO);
			if (isValid == null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("balance.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			statusResponseDTO.setStatus(env.getProperty("success"));
			statusResponseDTO.setMessage(env.getProperty("balance.success"));
			statusResponseDTO.setTokenBalance(tokenDTO);
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in Token Balance : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}

	// Check the token balance of Admin wallet

	@CrossOrigin
	@RequestMapping(value = "/token/balance/admin", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Balance Token for Admin", notes = "Need to get Balance Token for Admin")
	public synchronized ResponseEntity<String> tokenBalanceAdmin(
			@ApiParam(value = "Balance Token for Admin", required = true) @RequestBody TokenDTO tokenDTO,
			HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {
			HttpSession sessions = SessionCollector.find(tokenDTO.getSessionId());
			if (sessions == null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("session.expired"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			Double isValid = tokenService.balanceTokensForAdmin(tokenDTO);
			if (isValid == null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("balance.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			statusResponseDTO.setStatus(env.getProperty("success"));
			statusResponseDTO.setMessage(env.getProperty("balance.success"));
			statusResponseDTO.setTokenBalance(tokenDTO);
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in Token Balance for Admin : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}

	// Burn Tokens from Admin Wallet

	@CrossOrigin
	@RequestMapping(value = "/token/burn", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Burn Token", notes = "Need to Burn Token")
	public synchronized ResponseEntity<String> burnTokens(
			@ApiParam(value = "Burn Token", required = true) @RequestBody TokenDTO tokenDTO, HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {
			HttpSession sessions = SessionCollector.find(tokenDTO.getSessionId());
			if (sessions == null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("session.expired"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean validateAdmin = userUtils.validateAdmin(tokenDTO);
			if (!validateAdmin) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("validate.admin"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isSamePassword = userUtils.isSamePassword(tokenDTO);
			if (!isSamePassword) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("same.password"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean etherValidation = userUtils.etherValidation(tokenDTO);
			if (!etherValidation) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("ether.balance.burn"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isValid = userUtils.ValidateTokenBalanceForBurn(tokenDTO);
			if (!isValid) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("balance.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			String isValids = tokenService.burnTokens(tokenDTO);
			if (isValids == null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("burn.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			statusResponseDTO.setStatus(env.getProperty("success"));
			statusResponseDTO.setMessage(env.getProperty("burn.success"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in Burn Tokens : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}

	// Get all transaction histories of individual users

	@CrossOrigin
	@RequestMapping(value = "/token/transactionHistory", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Token Transaction History", notes = "Need to get Token Transaction History")
	public synchronized ResponseEntity<String> transactionHistory(
			@ApiParam(value = "Token Transaction History", required = true) @RequestBody TokenDTO tokenDTO,
			HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {
			HttpSession sessions = SessionCollector.find(tokenDTO.getSessionId());
			if (sessions == null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("session.expired"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			List<TokenDTO> transactionLists = tokenService.transactionHistory(tokenDTO,
					tokenDTO.getEtherWalletAddress());
			if (transactionLists == null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("history.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			statusResponseDTO.setStatus(env.getProperty("success"));
			statusResponseDTO.setMessage(env.getProperty("history.success"));
			statusResponseDTO.setTransactionHistoryInfo(transactionLists);
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in Tokens Transaction History : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}

	// Purchase Tokens

	@CrossOrigin
	@RequestMapping(value = "/token/purchase", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Token purchase", notes = "Need to purchase Tokens")
	public synchronized ResponseEntity<String> purchaseToken(
			@ApiParam(value = "Token purchase", required = true) @RequestBody TokenDTO tokenDTO,
			HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		LOG.info("Inside /tokens/purchase");

		try {
			boolean isSessionExpired = userUtils.isSessionExpired(tokenDTO);
			LOG.info("Get Email ID ++++++++++++++++>>>>>>>" + tokenDTO.getEmailId());
			if (!isSessionExpired) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("session.expired"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			LOG.info("tokenDTO.getRequestTokens() ::" + tokenDTO.getRequestTokens());
			LOG.info("tokenDTO.getEtherWalletPassword() ::" + tokenDTO.getEtherWalletPassword());
			boolean isValid = userUtils.validateTokenParam(tokenDTO);
			if (!isValid) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("validate.token.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isMinToken = tokenService.validMinToken(tokenDTO);
			if (!isMinToken) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("purchase.min.token"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isSamePassword = userUtils.isSamePassword(tokenDTO);
			if (!isSamePassword) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("same.password"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			LOG.info("Before isCrowdsaleDateStart ");
			boolean isValidStartDate = userUtils.isCrowdsaleDateStart(tokenDTO);
			LOG.info("After isCrowdsaleDateStart");
			if (isValidStartDate) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("crowdsale.start"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isValidEndDate = userUtils.isCrowdsaleDateEnd(tokenDTO);
			if (isValidEndDate) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("crowdsale.end"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isToken = tokenService.validAmount(tokenDTO);
			if (!isToken) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("balance.insufficient"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean etherValidation = userUtils.etherValidationForPurchase(tokenDTO);
			if (!etherValidation) {
				LOG.info("Inside !etherValidation");
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("ether.balance.purchase"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isContribute = tokenService.purchaseTokens(tokenDTO);
			if (!isContribute) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("contribute.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			statusResponseDTO.setStatus(env.getProperty("success"));
			statusResponseDTO.setMessage(env.getProperty("contribute.success"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);

		} catch (Exception e) {

			e.printStackTrace();
			LOG.error("Problem in Purchase Tokens : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}

	}

	// Listing the purchase tokens history for all individual users

	@CrossOrigin
	@RequestMapping(value = "/token/purchase/list", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Token purchase list", notes = "Need to show purchase Tokens list")
	public synchronized ResponseEntity<String> purchaseTokensList(
			@ApiParam(value = "Token purchase list", required = true) @RequestBody TokenDTO tokenDTO,
			HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {
			HttpSession sessions = SessionCollector.find(tokenDTO.getSessionId());
			if (sessions == null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("session.expired"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			List<TokenDTO> lists = tokenService.PurchaseTokensList(tokenDTO, tokenDTO.getEtherWalletAddress());
			if (lists == null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("list.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			statusResponseDTO.setStatus(env.getProperty("success"));
			statusResponseDTO.setMessage(env.getProperty("list.success"));
			statusResponseDTO.setPurchaseListInfo(lists);
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in List Purchase Tokens : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}

	// Filtering the token purchase list based on wallet address and user name for all users

	@CrossOrigin
	@RequestMapping(value = "/token/purchase/list/filter", method = RequestMethod.POST, produces = {
			"application/json" })
	@ApiOperation(value = "Need to purchase for tokens List filter", notes = "Need to purchase for tokens List filter")
	public synchronized ResponseEntity<String> listPurchaseFilter(
			@ApiParam(value = "Need to purchase for tokens List filter", required = true) @RequestBody TokenDTO tokenDTO,
			HttpServletRequest request) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {
			HttpSession session = SessionCollector.find(tokenDTO.getSessionId());
			if (session == null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("session.expired"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			List<TokenDTO> tokens = tokenService.purchaseListFilter(tokenDTO);
			if (tokens == null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("purchase.list.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			statusResponseDTO.setStatus(env.getProperty("success"));
			statusResponseDTO.setMessage(env.getProperty("purchase.list.success"));
			statusResponseDTO.setPurchaseListInfo(tokens);
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in Purchase Tokens List Filter : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}

	// Filtering the token transactions history based on transaction mode

	@CrossOrigin
	@RequestMapping(value = "/token/transaction/filter/mode", method = RequestMethod.POST, produces = {
			"application/json" })
	@ApiOperation(value = "Token filter transaction mode", notes = "Need to show Token filter transaction mode")
	public synchronized ResponseEntity<String> transactionFilterMode(
			@ApiParam(value = "Token filter transaction mode", required = true) @RequestBody TokenDTO tokenDTO,
			HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {
			HttpSession sessions = SessionCollector.find(tokenDTO.getSessionId());
			if (sessions == null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("session.expired"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			List<TokenDTO> lists = tokenService.filterModes(tokenDTO);
			if (lists == null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("filter.mode.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			statusResponseDTO.setStatus(env.getProperty("success"));
			statusResponseDTO.setMessage(env.getProperty("filter.mode.success"));
			statusResponseDTO.setPurchaseListInfo(lists);
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in Tokens transaction filter mode : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}

	// Filtering the token transactions history based on wallet address

	@CrossOrigin
	@RequestMapping(value = "/token/transaction/filter/address", method = RequestMethod.POST, produces = {
			"application/json" })
	@ApiOperation(value = "Token filter transaction address", notes = "Need to show Token filter transaction address")
	public synchronized ResponseEntity<String> transactionFilterAddress(
			@ApiParam(value = "Token filter transaction address", required = true) @RequestBody TokenDTO tokenDTO,
			HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {
			HttpSession sessions = SessionCollector.find(tokenDTO.getSessionId());
			if (sessions == null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("session.expired"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			List<TokenDTO> lists = tokenService.filterAddress(tokenDTO.getEtherWalletAddress());
			if (lists == null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("filter.address.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			statusResponseDTO.setStatus(env.getProperty("success"));
			statusResponseDTO.setMessage(env.getProperty("filter.address.success"));
			statusResponseDTO.setPurchaseListInfo(lists);
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in Tokens transaction filter address : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}

	// To send requested Aleef coins

	@CrossOrigin
	@RequestMapping(value = "/token/request", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Need to Request for tokens", notes = "Need to Request for tokens")
	public synchronized ResponseEntity<String> requestTokens(
			@ApiParam(value = "Need to Request for tokens", required = true) @RequestBody TokenDTO tokenDTO,
			HttpServletRequest request) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {
			HttpSession session = SessionCollector.find(tokenDTO.getSessionId());
			if (session == null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("session.expired"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isValidateTokenName = userUtils.validateToAddress(tokenDTO);
			if (!isValidateTokenName) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("valid.tokenAddress"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isSameAddress = userUtils.isSameaddress(tokenDTO);
			if (isSameAddress) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("same.address"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isValue = tokenService.requestTokens(tokenDTO);
			if (isValue == false) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("request.token.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			statusResponseDTO.setStatus(env.getProperty("success"));
			statusResponseDTO.setMessage(env.getProperty("request.token.success"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in Request Tokens : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}

	// Listing all token requests

	@CrossOrigin
	@RequestMapping(value = "/token/request/list", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Need to Request for tokens List", notes = "Need to Request for tokens List")
	public synchronized ResponseEntity<String> requestList(
			@ApiParam(value = "Need to Request for tokens List", required = true) @RequestBody TokenDTO tokenDTO,
			HttpServletRequest request) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {
			HttpSession session = SessionCollector.find(tokenDTO.getSessionId());
			if (session == null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("session.expired"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			List<TokenDTO> tokens = tokenService.requestTokensList(tokenDTO);
			if (tokens == null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("request.list.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			statusResponseDTO.setStatus(env.getProperty("success"));
			statusResponseDTO.setMessage(env.getProperty("request.list.success"));
			statusResponseDTO.setRequestTokensList(tokens);
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in Request Tokens List : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}
	
	//Request for tokens transfer

	@CrossOrigin
	@RequestMapping(value = "/request/token/transfer", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Need to Request for tokens transfer", notes = "Need to Request for tokens transfer")
	public synchronized ResponseEntity<String> requestTokensTransfer(
			@ApiParam(value = "Need to Request for tokens transfer", required = true) @RequestBody TokenDTO tokenDTO,
			HttpServletRequest request) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {
			HttpSession session = SessionCollector.find(tokenDTO.getSessionId());
			if (session == null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("session.expired"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isValidateTokenName = userUtils.validateToAddress(tokenDTO);
			if (!isValidateTokenName) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("valid.tokenAddress"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isSameAddress = userUtils.isSameaddress(tokenDTO);
			if (isSameAddress) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("same.address"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isSamePassword = userUtils.isSamePassword(tokenDTO);
			if (!isSamePassword) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("same.password"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean etherValidation = userUtils.etherValidation(tokenDTO);
			if (!etherValidation) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("ether.balance"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean tokenAmountValidation = userUtils.tokenAmountValidationForUser(tokenDTO);
			if (!tokenAmountValidation) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("token.amount.validation"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isValue = tokenService.requestTokensTransfer(tokenDTO);
			if (isValue == false) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("request.token.transfer.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			statusResponseDTO.setStatus(env.getProperty("success"));
			statusResponseDTO.setMessage(env.getProperty("request.token.transfer.success"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in Request Tokens Transfer : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}

}
