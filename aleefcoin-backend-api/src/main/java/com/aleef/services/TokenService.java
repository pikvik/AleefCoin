package com.aleef.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import com.aleef.dtos.TokenDTO;

@Service
public interface TokenService {

	public boolean transferTokenAdmin(TokenDTO tokenDTO) throws Exception;

	public boolean transferTokenAdminApproval(TokenDTO tokenDTO) throws Exception;

	public String transferToken(TokenDTO tokenDTO) throws Exception;

	public Double balanceTokens(TokenDTO tokenDTO) throws Exception;

	public Double balanceTokensForAdmin(TokenDTO tokenDTO) throws Exception;

	public String burnTokens(TokenDTO tokenDTO) throws FileNotFoundException, IOException, ParseException, Exception;

	public List<TokenDTO> transactionHistory(TokenDTO tokenDTO, String etherWalletAddress) throws Exception;

	public boolean purchaseTokens(TokenDTO tokenDTO) throws Exception;

	public List<TokenDTO> PurchaseTokensList(TokenDTO tokenDTO, String etherWalletAddress);

	public List<TokenDTO> filterModes(TokenDTO tokenDTO) throws Exception;

	public List<TokenDTO> filterAddress(String etherWalletAddress);

	public boolean validAmount(TokenDTO tokenDTO);

	public boolean requestTokens(TokenDTO tokenDTO)
			throws FileNotFoundException, IOException, ParseException, Exception;

	public List<TokenDTO> requestTokensList(TokenDTO tokenDTO)
			throws FileNotFoundException, IOException, ParseException, Exception;

	public boolean requestTokensTransfer(TokenDTO tokenDTO)
			throws FileNotFoundException, IOException, ParseException, Exception;

	public boolean UpdatePurchaseTable() throws java.text.ParseException;

	public boolean validMinToken(TokenDTO tokenDTO);

	public List<TokenDTO> purchaseListFilter(TokenDTO tokenDTO) throws Exception;
}
