package it.mynaproject.togo.api.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Mail {

	final static private Logger log = LoggerFactory.getLogger(Mail.class);

	public static Boolean sendMail(String receiver, String subject, String content) {

		Properties prop = PropertiesFileReader.loadPropertiesFile();

		String host = prop.getProperty("smtpHost");
		final String user = prop.getProperty("smtpUser");

		Properties props = new Properties();
		props.put("mail.smtp.host", host);

		Session session;
		if (prop.getProperty("smtpPassword").equals("null")) {
			props.put("mail.smtp.auth", "false");
			session = Session.getInstance(props);
		} else {
			props.put("mail.smtp.auth", "true");
			final String password = prop.getProperty("smtpPassword");
			props.put("mail.smtp.port", prop.getProperty("smtpPort"));

			if (prop.getProperty("smtpPort").equals("465")) {
				log.info("SSL used");
				props.put("mail.smtp.ssl.protocols", "TLSv1.2");
				props.put("mail.smtp.socketFactory.port", "465"); // SSL Port
				props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); // SSL Factory Class
			}

			session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(user, password);
				}
			});
		}

		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(user));
			message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(receiver));
			message.setSubject(subject);
			message.setContent(content, "text/html; charset=utf-8");

			log.info("Ready to send e-mail with subject '{}' to {}", subject, receiver);

			try {
				Transport.send(message);
			} catch (MessagingException e) {
				log.info("E-mail not sent! {}", e.getMessage());
				return false;
			}
		} catch (Exception e) {
			log.info("E-mail not sent! {}", e.getMessage());
			return false;
		}
		log.info("E-mail sent successfully!");

		return true;
	}

	public static String getHostname() {

		String hostname = "Unknown";
		try {
			InetAddress addr;
			addr = InetAddress.getLocalHost();
			hostname = addr.getHostName();
		} catch (UnknownHostException ex) {
			log.error("Hostname can not be resolved");
		}

		return hostname;
	}
}
