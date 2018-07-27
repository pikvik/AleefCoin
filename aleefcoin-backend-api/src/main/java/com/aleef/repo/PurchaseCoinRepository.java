package com.aleef.repo;

import org.springframework.data.repository.CrudRepository;

import com.aleef.models.PurchaseInfo;

public interface PurchaseCoinRepository extends CrudRepository<PurchaseInfo,Integer>{
	
	public PurchaseInfo findById(Integer id);

}
