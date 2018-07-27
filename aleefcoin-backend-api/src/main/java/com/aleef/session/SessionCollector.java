package com.aleef.session;

import java.util.HashMap;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SessionCollector implements HttpSessionListener {

	private static final HashMap<String, HttpSession> sessions = new HashMap<String, HttpSession>();

	static final Logger LOG = LoggerFactory.getLogger(SessionCollector.class);
// To create session
	@Override
	public void sessionCreated(HttpSessionEvent event) {
		HttpSession session = event.getSession();
		sessions.put(session.getId(), session);
		LOG.info("Get SessionId" + session.getId());
	}
// To kill the session
	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		sessions.remove(event.getSession().getId());
	}

	public static HttpSession find(String sessionId) {
		return sessions.get(sessionId);
	}

}
