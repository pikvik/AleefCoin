package com.aleef.repo;

import org.springframework.data.repository.CrudRepository;

import com.aleef.models.ConfigInfo;

public interface ConfigInfoRepository extends CrudRepository<ConfigInfo, Integer> {

	public ConfigInfo findConfigInfoByConfigKey(String string);
	
}
