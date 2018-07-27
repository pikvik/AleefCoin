package com.aleef.scheduler;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.aleef.models.PurchaseCoinInfo;
import com.aleef.repo.PurchaseCoinInfoRepository;
import com.aleef.services.TokenService;
import com.aleef.services.UserRegisterService;

@Service
public class PurchaseCoinScheduler {

	static final Logger LOG = LoggerFactory.getLogger(PurchaseCoinScheduler.class);

	@Autowired
	private TokenService tokenService;

	@Autowired
	private PurchaseCoinInfoRepository purchaseCoinInfoRepository;

	@Autowired
	private UserRegisterService userRegisterService;

	//Schdeuler to send Tokens to users after ICO sale
	@SuppressWarnings("unused")
	@Scheduled(cron = "0 0 0 * * ?")
	public boolean updatePurchaseCoinTable() {
		try {

			LOG.info("Schedular Started Every One 24 hours " + new Date());

			LOG.info("Schedular Started " + new Date());

			LOG.info("Inside PurchaseCoin : " + new Date());

			boolean updatePurchaseTable = tokenService.UpdatePurchaseTable();

			List<PurchaseCoinInfo> purchaseCoinInfos = (List<PurchaseCoinInfo>) purchaseCoinInfoRepository.findAll();

			LOG.info("purchaseCoinInfos.get(purchaseCoinInfos.size()-1).getStartTriggerPostIco() ========>>>"
					+ purchaseCoinInfos.get(purchaseCoinInfos.size() - 1).getStartTriggerPostIco());
			LOG.info("purchaseCoinInfos.get(purchaseCoinInfos.size()-1).getStopTriggerPostIco() ========>>>"
					+ purchaseCoinInfos.get(purchaseCoinInfos.size() - 1).getStopTriggerPostIco());

			if (purchaseCoinInfos.get(purchaseCoinInfos.size() - 1).getStartTriggerPostIco().compareTo(new Date())
					* new Date().compareTo(
							purchaseCoinInfos.get(purchaseCoinInfos.size() - 1).getStopTriggerPostIco()) > 0) {

				LOG.info("Inside userRegisterService.postIcoTokenTransfer()");

				boolean isTransfer = userRegisterService.postIcoTokenTransfer();

			}

			if (updatePurchaseTable) {
				LOG.info("Update Purchase Table " + new Date());
				return true;
			} else
				return false;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
}
