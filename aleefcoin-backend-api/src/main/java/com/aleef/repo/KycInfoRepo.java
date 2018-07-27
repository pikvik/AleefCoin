package com.aleef.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.aleef.models.KycInfo;

public interface KycInfoRepo extends CrudRepository<KycInfo, Integer> {

	public KycInfo findKycInfoById(Integer id);

	public KycInfo findByEmailId(String mailId);
	
	public List<KycInfo> findKycInfoByEmailId(String mailId);

	public List<KycInfo> findAllByOrderByIdDesc();
	
	public List<KycInfo> findByKycStatus(int kycStatus);
	
	public List<KycInfo> findByFullName(String userName);
	
	@Query("select p from KycInfo p where upper(p.fullName) like concat('%', upper(?1), '%')")
	List<KycInfo> findByFullNameLike(@Param("fullName") String fullName);
	
}
