package com.aleef.repo;

import org.springframework.data.repository.CrudRepository;

import com.aleef.models.ExpirationDataInfo;

public interface ExpirationDataInfoRepository extends CrudRepository<ExpirationDataInfo, Integer>{

	public ExpirationDataInfo findByToken(String string);
	
}
