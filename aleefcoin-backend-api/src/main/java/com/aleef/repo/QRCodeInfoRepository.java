package com.aleef.repo;

import org.springframework.data.repository.CrudRepository;

import com.aleef.models.QRcodeInfo;

public interface QRCodeInfoRepository extends CrudRepository<QRcodeInfo, Integer> {

	public QRcodeInfo findQRcodeByQrKey(String string);
	
}
