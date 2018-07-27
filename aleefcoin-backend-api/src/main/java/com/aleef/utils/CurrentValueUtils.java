package com.aleef.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DecimalFormat;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.aleef.dtos.RegisterDTO;

@Service
public class CurrentValueUtils {

	static final Logger LOG = LoggerFactory.getLogger(CurrentValueUtils.class);

	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONObject json = new JSONObject(jsonText);
			return json;
		} finally {
			is.close();
		}
	}
	
	// Convert current live ether balance to equivalent of US dollars 

	public Double getEtherValueForDollar(RegisterDTO registerDTO) throws JSONException, IOException {
		JSONObject json = readJsonFromUrl("https://api.coinbase.com/v2/prices/ETH-USD/spot");
		JSONObject json2 = (JSONObject) json.get("data");
		LOG.info("JSON" + json.toString());
		LOG.info("JSON to get data" + json.get("data"));
		LOG.info("JSON to get amount from data" + json2.get("amount"));
		String amount = (String) json2.get("amount");
		BigDecimal Dollars = new BigDecimal(amount);
		LOG.info("Big Decimal: " + Dollars);
		LOG.info("Dollars: " + Dollars.doubleValue());
		LOG.info("One Dollar is equal to Btc: " + 1 / Dollars.doubleValue());

		Double b = (Dollars.doubleValue() * registerDTO.getEtherBalance().doubleValue());

		LOG.info("B value" + b);
		return b;
	}
	
	// Convert current aleef coin value to equivalent of ether balance

	public Double getEtherValuerFromCurrentAleefCoinValue(BigDecimal aleefCurrentRate)
			throws JSONException, IOException {
		JSONObject json = readJsonFromUrl("https://api.coinbase.com/v2/prices/ETH-USD/spot");
		JSONObject json2 = (JSONObject) json.get("data");
		LOG.info("JSON" + json.toString());
		LOG.info("JSON to get data" + json.get("data"));
		LOG.info("JSON to get amount from data" + json2.get("amount"));
		String amount = (String) json2.get("amount");
		BigDecimal Dollars = new BigDecimal(amount);
		LOG.info("Big Decimal: " + Dollars);
		LOG.info("Dollars: " + Dollars.doubleValue());
		LOG.info("One Dollar is equal to Btc: " + 1 / Dollars.doubleValue());
		Double a = aleefCurrentRate.doubleValue() / Dollars.doubleValue();
		DecimalFormat df = new DecimalFormat("#.###############");
		LOG.info("Final Output: " + df.format(a));
		double value = Double.parseDouble(df.format(a));
		LOG.info("Get Value" + value);
		return value;
	}

}
