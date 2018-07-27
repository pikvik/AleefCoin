package com.aleef.services;

import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.aleef.dtos.LoginDTO;
import com.aleef.dtos.RegisterDTO;
import org.springframework.web.multipart.MultipartFile;
import com.aleef.dtos.KycDTO;
import com.aleef.dtos.TokenDTO;
import com.aleef.dtos.PurchaseCoinDTO;

@Service
public interface UserRegisterService {

	public boolean isAccountExistCheckByEmailId(String emailId);

	public boolean isEtherWalletCreated(RegisterDTO registerDTO);

	public String saveRegisterInfo(RegisterDTO registerDTO, String encryptedPassword) throws Exception;

	public LoginDTO isEmailAndPasswordExit(RegisterDTO registerDTO, HttpServletRequest request) throws Exception;

	public LoginDTO secureLogin(RegisterDTO registerDTO, HttpServletRequest request) throws Exception;

	public boolean isOldPassword(RegisterDTO registerDTO) throws Exception;

	public boolean isChangePassword(RegisterDTO registerDTO) throws Exception;

	boolean isSendEmail(RegisterDTO registerDTO);

	public LoginDTO isAccountExistCheckByEmailIdForSocialMediaLogin(RegisterDTO registerDTO, HttpServletRequest request)
			throws Exception;

	public LoginDTO newSocialMediaRegisterAndLogin(RegisterDTO registerDTO, HttpServletRequest request)
			throws Exception;

	public BigDecimal etherBalance(RegisterDTO registerDTO) throws Exception;

	public boolean isAccountExistCheckByMobileNo(String mobileNo);

	public List<RegisterDTO> listUsers();

	public List<RegisterDTO> userListFilters(RegisterDTO registerDTO);

	public String icoTokensTransfer() throws Exception;

	public String userDocumentUpload(MultipartFile uploadedFileRef, String emailId, String docType) throws Exception;

	public boolean saveKycInfo(KycDTO kycDTO, String kycDoc1FilePath, String kycDoc2FilePath, MultipartFile kycDoc1,
			MultipartFile kycDoc2);

	public List<KycDTO> kycList();

	public KycDTO getKycDetails(TokenDTO tokenDTO);

	public boolean updateKYC(KycDTO kycDTO);

	boolean postIcoTokenTransfer() throws Exception;

	public boolean referral(RegisterDTO registerDTO) throws Exception;

	public List<PurchaseCoinDTO> getIcoTokenDetails();

	List<String>[] getReferralDetails(RegisterDTO registerDTO);

	List<KycDTO> filterKyCStatus(KycDTO kycDTO);

	List<KycDTO> filterKycUserName(KycDTO kycDTO);

	public boolean userReferralPointsList(RegisterDTO registerDTO);

	public boolean totalReferralForAdmin(RegisterDTO registerDTO);

	public KycDTO getUserKycDetails(TokenDTO tokenDTO);

}
