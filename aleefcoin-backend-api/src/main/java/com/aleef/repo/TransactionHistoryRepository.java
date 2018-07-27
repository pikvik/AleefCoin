package com.aleef.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.aleef.models.TransactionHistory;

public interface TransactionHistoryRepository extends CrudRepository<TransactionHistory, Integer> {

	public List<TransactionHistory> findByFromAddressOrToAddressOrderByTransactionDateDesc(String etherWalletAddress1,
			String etherWalletAddress2);

	public List<TransactionHistory> findByTransactionMode(String transactionMode);

	public List<TransactionHistory> findTop3ByFromAddressOrToAddressOrderByTransactionDateDesc(
			String etherWalletAddress1, String etherWalletAddress2);

	public TransactionHistory findById(Integer id);
}
