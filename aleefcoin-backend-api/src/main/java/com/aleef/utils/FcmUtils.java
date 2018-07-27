package com.aleef.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FcmUtils {
	
	static final Logger LOG = LoggerFactory.getLogger(FcmUtils.class);

	final static private String FCM_URL = "https://fcm.googleapis.com/fcm/send";

	/**
	 * 
	 * 
	 * 
	 * Method to send push notification to Android FireBased Cloud messaging
	 * Server.
	 * 
	 * 
	 * 
	 * @param tokenId
	 *            Generated and provided from Android Client Developer
	 * 
	 * @param server_key
	 *            Key which is Generated in FCM Server
	 * 
	 * @param message
	 *            which contains actual information.
	 * @param deviceType
	 * 
	 * 
	 * 
	 */

	public static void send_FCM_Notification(String tokenId, String server_key, String message, String deviceType,
			JSONObject infoJson) {

		try {

			LOG.info("FCM.send_FCM_Notification()... checking URL " + FCM_URL);
			// Create URL instance.

			URL url = new URL(FCM_URL);

			// create connection.

			HttpURLConnection conn;

			conn = (HttpURLConnection) url.openConnection();

			conn.setUseCaches(false);

			conn.setDoInput(true);

			conn.setDoOutput(true);

			// set method as POST or GET

			conn.setRequestMethod("POST");

			LOG.info("FCM.send_FCM_Notification()... checking RequestMethod " + conn.getRequestMethod());

			// pass FCM server key

			conn.setRequestProperty("Authorization", "key=" + server_key);

			// Specify Message Format

			conn.setRequestProperty("Content-Type", "application/json");

			// Create JSON Object & pass value

			JSONObject json = new JSONObject();

			if (deviceType.equalsIgnoreCase("android")) {

				json.put("to", tokenId.trim());

				json.put("notification", infoJson);
			} else {

				json.put("to", tokenId.trim());

				json.put("data", infoJson);
			}
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

			wr.write(json.toString());

			wr.flush();

			int status = 0;

			if (null != conn) {

				status = conn.getResponseCode();

			}

			if (status != 0) {

				LOG.info("FCM.send_FCM_Notification()...  checking status " + status);
				if (status == 200) {

					// SUCCESS message

					BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

					LOG.info("Android Notification Response : " + reader.readLine());

				} else if (status == 401) {

					// client side error

					LOG.info("Notification Response : TokenId : " + tokenId + " Error occurred :");

				} else if (status == 501) {

					// server side error

					LOG.info("Notification Response : [ errorCode=ServerError ] TokenId : " + tokenId);

				} else if (status == 503) {

					// server side error

					LOG.info("Notification Response : FCM Service is Unavailable  TokenId : " + tokenId);

				}

			}

		} catch (MalformedURLException mlfexception) {

			// Prototcal Error

			LOG.error("Error occurred while sending push Notification!.." + mlfexception.getMessage());

		} catch (IOException mlfexception) {

			// URL problem

			LOG.error(
					"Reading URL, Error occurred while sending push Notification!.." + mlfexception.getMessage());

		} catch (JSONException jsonexception) {

			// Message format error

			LOG.error(
					"Message Format, Error occurred while sending push Notification!.." + jsonexception.getMessage());

		} catch (Exception exception) {

			// General Error or exception.

			LOG.error("Error occurred while sending push Notification!.." + exception.getMessage());

		}

	}

}
