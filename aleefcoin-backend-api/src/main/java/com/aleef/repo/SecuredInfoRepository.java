package com.aleef.repo;

import org.springframework.data.repository.CrudRepository;

import com.aleef.models.SecuredInfo;

public interface SecuredInfoRepository extends CrudRepository<SecuredInfo, Integer>{

	public SecuredInfo findSecuredInfoByEmailId(String emailId);
	
}
