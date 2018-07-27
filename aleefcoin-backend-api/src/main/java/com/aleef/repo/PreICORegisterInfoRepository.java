package com.aleef.repo;

import org.springframework.data.repository.CrudRepository;

import com.aleef.models.PreICORegisterInfo;

public interface PreICORegisterInfoRepository extends CrudRepository<PreICORegisterInfo, Integer> {
	
	public Integer countPreICORegisterInfoByEmailIdIgnoreCase(String emailId);
	
}
