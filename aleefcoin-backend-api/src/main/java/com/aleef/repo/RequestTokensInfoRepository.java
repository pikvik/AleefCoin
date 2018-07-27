package com.aleef.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import com.aleef.models.RequestTokensInfo;

@Service
public interface RequestTokensInfoRepository extends CrudRepository<RequestTokensInfo, Integer> {

	List<RequestTokensInfo> findByFromAddress(String fromAddress);

	List<RequestTokensInfo> findByFromAddressAndStatus(String fromAddress, String status);

	public RequestTokensInfo findById(Integer id);

}
