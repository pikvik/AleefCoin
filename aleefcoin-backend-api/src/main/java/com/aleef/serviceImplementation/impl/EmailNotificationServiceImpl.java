package com.aleef.service.impl;

import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationServiceImpl extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(EmailNotificationServiceImpl.class);

	@SuppressWarnings("unused")
	@Autowired
	private JavaMailSender javaMailService;

	@Value("${email.godaddy.host}")
	private String host;

	@Value("${smtpout.asia.secureserver.net}")
	private Integer port;

	@Value("${email.godaddy.username}")
	private String username;

	@Value("${email.godaddy.password}")
	private String password;

	@Value("${spring.mail.transport.protocol}")
	private String transportProtocol;

	@Value("${env}")
	private String env;

	@Value("${smtp.username}")
	private String SMTP_USERNAME;

	@Value("${smtp.password}")
	private String SMTP_PASSWORD;
	
	//Triggering mails to users
	public boolean sendEmail(String toEmailId, String subject, String content) {

		LOG.info("Before sending email  ENV : " + env);
		LOG.info("host  : " + host);
		LOG.info("port  : " + port);
		LOG.info("username  : " + username);
		LOG.info("transportProtocol  : " + transportProtocol);
		LOG.info("toEamilId  : " + toEmailId);
		LOG.info("subject  : " + subject);
		LOG.info("content  : " + content);

		// DEV Env
		if (env.equalsIgnoreCase("dev")) {
			return sendEmailDEV(toEmailId, subject, content);
		} else if (env.equalsIgnoreCase("prod")) {
			return sendEmailPROD(toEmailId, subject, content);
		} else {
			return sendEmailDEV(toEmailId, subject, content);
		}

	}

	@SuppressWarnings("unused")
	public boolean sendEmailForgot(String toEmailId, String subject, String content) {
		try {
			LOG.info("In  sendEmailDEV  START: " + env);

			SimpleMailMessage mail = new SimpleMailMessage();
			mail.setTo(toEmailId);
			mail.setFrom(username);
			mail.setSubject(subject);
			Properties properties = new Properties();
			properties.put("mail.smtp.host", host);
			properties.put("mail.smtp.port", port);
			properties.put("mail.smtp.auth", "true");
			properties.put("mail.smtp.starttls.enable", "true");
			// properties.put("mail.smtp.ssl.trust", host);

			Authenticator auth = new Authenticator() {
				public PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			};

			Session session = Session.getInstance(properties, auth);

			// creates a new e-mail message
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(username));
			InternetAddress[] toAddresses = { new InternetAddress(toEmailId) };
			msg.setRecipients(Message.RecipientType.TO, toAddresses);
			msg.setSubject(subject);
			msg.setSentDate(new Date());
			MimeMessageHelper helper = new MimeMessageHelper(msg, true);

			String message = "<b>Hi,</b><br><br>";
			message += "<b>Please find your login credentials </b><br>";
			message += "<font color=red> Your  password: " + content + "</font><br><br>";
			message += "<b>Thank you,</b><br>";
			message += "<b>Aleefteam.</b>";

			msg.setText(message);
			// set plain text message
			msg.setContent(message, "text/html");
			// sends the e-mail

			LOG.info("Attempting to send an email");

			Transport.send(msg);

			LOG.info("Email sent successfully !");
			LOG.info("In  sendEmailDEV  END: " + env);

			return true;
		} catch (MailException e) {
			LOG.error("Problem in sending mail sendEmailDEV  END: " + env);
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			LOG.error("Problem in sending mail sendEmailDEV  END: " + env);
			e.printStackTrace();
			return false;
		}
	}

	private boolean sendEmailDEV(String toEmailId, String subject, String content) {
		try {
			LOG.info("In  sendEmailDEV  START: " + env);

			SimpleMailMessage mail = new SimpleMailMessage();
			mail.setTo(toEmailId);
			mail.setFrom(username);
			mail.setSubject(subject);
			Properties properties = new Properties();
			properties.put("mail.smtp.host", host);
			properties.put("mail.smtp.port", port);
			properties.put("mail.smtp.auth", "true");
			properties.put("mail.smtp.starttls.enable", "true");
			properties.put("mail.smtp.ssl.trust", host);

			// creates a new session with an authenticator
			Authenticator auth = new Authenticator() {
				public PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			};

			Session session = Session.getInstance(properties, auth);

			// creates a new e-mail message
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(username));
			InternetAddress[] toAddresses = { new InternetAddress(toEmailId) };
			msg.setRecipients(Message.RecipientType.TO, toAddresses);
			msg.setSubject(subject);
			msg.setSentDate(new Date());
			MimeMessageHelper helper = new MimeMessageHelper(msg, true);
			helper.setText(content);
			// set plain text message
			msg.setContent(content, "text/html");
			// sends the e-mail
			LOG.info("Attempting to send an email");

			Transport.send(msg);

			LOG.info("Email sent successfully !");
			LOG.info("In  sendEmailDEV  END: " + env);
			return true;
		} catch (MailException e) {
			LOG.error("Problem in sending mail sendEmailDEV  END: " + env);
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			LOG.error("Problem in sending mail sendEmailDEV  END: " + env);
			e.printStackTrace();
			return false;
		}
	}

	private boolean sendEmailPROD(String toEamilId, String subject, String content) {
		Transport transport = null;
		try {
			LOG.info("In  sendEmail PROD  START: " + env);

			// Create a Properties object to contain connection configuration
			// information.
			Properties props = System.getProperties();
			props.put("mail.transport.protocol", transportProtocol);
			props.put("mail.smtp.port", port);
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.starttls.required", "true");

			// Create a Session object to represent a mail session with the
			// specified properties.
			Session session = Session.getDefaultInstance(props);
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(username));
			// Use to send an single 'TO' Recipient
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(toEamilId));
			msg.setSubject(subject);
			msg.setContent(content, "text/html");

			/*
			 * // Add multiple 'TO' Recipient in the Email msg =
			 * addMultipleMailId(msg, cc_SendAddress, Message.RecipientType.TO);
			 * 
			 * // Add multiple 'CC' Recipient in the Email msg =
			 * addMultipleMailId(msg, cc_SendAddress, Message.RecipientType.CC);
			 */

			// Create a transport
			transport = session.getTransport();
			LOG.info("Attempting to send an email through the Amazon SES SMTP interface...");
			transport.connect(host, username, password);
			transport.sendMessage(msg, msg.getAllRecipients());
			LOG.info("Email sent successfully !");
			transport.close();

			LOG.info("In  sendEmail PROD  END: " + env);
			return true;
		} catch (MailException e) {
			LOG.error("Problem in sending mail sendEmail PROD  END: " + env);
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			LOG.error("Problem in sending mail sendEmail PROD  END: " + env);
			e.printStackTrace();
			return false;
		}

	}

}
