package com.aleef.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.aleef.models.RegisterInfo;

@Service
public interface RegisterInfoRepository extends CrudRepository<RegisterInfo, Integer> {

	public Integer countRegisterInfoByEmailIdIgnoreCase(String emailId);

	public RegisterInfo findRegisterInfoByEmailId(String emailId);

	public Integer countRegisterInfoByMobileNo(String mobileNo);

	public RegisterInfo findById(Integer id);

	public Integer countUserModelInfoByEmailIdIgnoreCase(String emailId);

	public Integer countRegisterInfoByRoleId(Integer roleId);

	public List<RegisterInfo> findRegisterInfoByRoleId(Integer roleId);

	public RegisterInfo findByWalletAddress(String walletAddress);

	public List<RegisterInfo> findRegisterInfoByUserName(String UserName);

	public List<RegisterInfo> findByEmailId(String emailId);

	public List<RegisterInfo> findAllByOrderByIdDesc();

	@Query("select p from RegisterInfo p where upper(p.userName) like concat('%', upper(?1), '%')")
	List<RegisterInfo> findByUserNameLike(@Param("userName") String userName);

}
