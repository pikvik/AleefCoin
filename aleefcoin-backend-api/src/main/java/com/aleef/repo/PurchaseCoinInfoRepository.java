package com.aleef.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.aleef.models.PurchaseCoinInfo;

public interface PurchaseCoinInfoRepository extends CrudRepository<PurchaseCoinInfo, Integer> {

	public PurchaseCoinInfo findById(Integer id);
	
	public PurchaseCoinInfo findPuchaseCoinInfoBySlabs(String slabs);
	
	public List<PurchaseCoinInfo> findAll();
	
}
