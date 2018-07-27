package com.aleef.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.aleef.models.PurchaseInfo;

@Service
public interface PurchaseInfoRepository extends CrudRepository<PurchaseInfo, Integer> {

	public List<PurchaseInfo> findAll();

	public List<PurchaseInfo> findAllByOrderByIdDesc();

	public PurchaseInfo findById(Integer id);

	public List<PurchaseInfo> findByEtherWalletAddressOrderByPurchasedDateDesc(String etherWalletAddress);

	public List<PurchaseInfo> findByEtherWalletAddressOrderByPurchasedDateDesc(String etherWalletAddress,
			Date purchasedDate);

	public List<PurchaseInfo> findPurchaseInfoByPurchasedDate(Date purchasedDate);

	@Query("select p from PurchaseInfo p where upper(p.userName) like concat('%', upper(?1), '%')")
	List<PurchaseInfo> findByUserNameLike(@Param("userName") String userName);
}
