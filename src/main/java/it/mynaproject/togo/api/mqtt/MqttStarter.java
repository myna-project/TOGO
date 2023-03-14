/*******************************************************************************
 * Copyright (c) Myna-Project SRL <info@myna-project.org>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 * - Myna-Project SRL <info@myna-project.org> - initial API and implementation
 ******************************************************************************/
package it.mynaproject.togo.api.mqtt;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.mynaproject.togo.api.domain.User;
import it.mynaproject.togo.api.model.ConfigMeasureJson;
import it.mynaproject.togo.api.model.CsvMeasuresJson;
import it.mynaproject.togo.api.service.ConfigMeasureService;
import it.mynaproject.togo.api.service.MeasureService;
import it.mynaproject.togo.api.service.UserService;
import it.mynaproject.togo.api.util.PropertiesFileReader;

@Component
public class MqttStarter implements InitializingBean, DisposableBean {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private MeasureService measureService;

	@Autowired
	private ConfigMeasureService configMeasureService;

	@Autowired
	private UserService userService;

	private Map<MqttClient, ClientInfo> clientMap = new HashMap<MqttClient, ClientInfo>();

	Properties prop = PropertiesFileReader.loadPropertiesFile();
	private String mqttServerUri = prop.getProperty("mqttServerURI");
	private List<String> mqttTopics = Arrays.asList(prop.getProperty("mqttTopics").split(","));

	private List<String> topics = new ArrayList<String>();

	@Override
	public void afterPropertiesSet() throws Exception {

		if ((mqttServerUri != null) && !mqttServerUri.equals("")) {
			log.info("== START MQTT SUBSCRIBER: {} ==", mqttServerUri);

			for (User u : userService.getUsers())
				if ((u.getRoles() != null) && u.getRoles().toString().contains("ROLE_USER"))
					for (String topic : mqttTopics)
						topics.add("wolf/" + u.getUsername() + "/" + topic);

			MqttClient client = new MqttClient(mqttServerUri, MqttClient.generateClientId(), new MemoryPersistence());
			client.setCallback(new MqttController());

			MqttConnectOptions options = new MqttConnectOptions();
			options.setUserName(prop.getProperty("mqttUser"));
			options.setPassword(prop.getProperty("mqttPassword").toCharArray());
			options.setConnectionTimeout(3600);
			options.setKeepAliveInterval(3600);
			options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);

			if (mqttServerUri.startsWith("ssl")) {
				String caFilePath = prop.getProperty("mqttCaCert");
				String clientCrtFilePath = prop.getProperty("mqttCert");
				String clientKeyFilePath = prop.getProperty("mqttKey");

				SSLSocketFactory socketFactory = getSocketFactory(caFilePath, clientCrtFilePath, clientKeyFilePath, "");
				options.setSocketFactory(socketFactory);
			}

			try {
				client.connect(options);
			} catch (Exception e) {
				log.error("Cannot connect to MQTT Broker: {}", e.getMessage());
			}

			if (client.isConnected()) {
				String mqttQos = prop.getProperty("mqttQos");
				if (mqttQos.equals(""))
					mqttQos = "2";
				for (String newTopic : topics.toArray(new String[0]))
					client.subscribe(newTopic, Integer.parseInt(mqttQos));

				clientMap.put(client, new ClientInfo(topics, options));
				log.info("== MQTT SUBSCRIBER STARTED ==");
			}
		}
	}

	public void destroy() throws Exception {

		if ((mqttServerUri != null) && !mqttServerUri.equals("")) {
			for (Map.Entry<MqttClient, ClientInfo> entry : clientMap.entrySet()) {
				MqttClient client = entry.getKey();
				if (client.isConnected()) {
					try {
						log.info("MQTT disconnection...");
						client.disconnectForcibly();
						client.close(true);
						log.info("MQTT disconnected forcibly");
					} catch (MqttException e1) {
						log.error("MQTT disconnected forcibly failed...");
					} finally {
						client = null;
					}
				}
			}
		}
	}

	public void unsubscribeUser(String username) throws MqttException {

		if ((mqttServerUri != null) && !mqttServerUri.equals("")) {
			for (Map.Entry<MqttClient, ClientInfo> entry : clientMap.entrySet()) {
				MqttClient client = entry.getKey();
				for (String topic : mqttTopics)
					client.unsubscribe("wolf/" + username + "/" + topic);
			}
		}
	}

	public void subscribeUser(User u) {

		if ((mqttServerUri != null) && !mqttServerUri.equals("")) {
			for (Map.Entry<MqttClient, ClientInfo> entry : clientMap.entrySet()) {
				MqttClient client = entry.getKey();

				String roles = u.getRoles().toString();
				if (roles.contains("ROLE_USER")) {
					String mqttQos = prop.getProperty("mqttQos");
					if (mqttQos.equals(""))
						mqttQos = "2";
					for (String topic : mqttTopics) {
						try {
							client.subscribe("wolf/" + u.getUsername() + "/" + topic, Integer.parseInt(mqttQos));
							client.subscribe("wolf/" + u.getUsername() + "/" + topic, Integer.parseInt(mqttQos));
						} catch (NumberFormatException e) {
							log.error("MqttException: {}", e.getMessage());
						} catch (MqttException e) {
							log.error("MqttException: {}", e.getMessage());
						}
					}
				}
			}
		}
	}

	public class MqttController implements MqttCallback {

		public void connectionLost(Throwable throwable) {
			log.error("Connection to MQTT broker {} lost!", mqttServerUri);

			for(Map.Entry<MqttClient, ClientInfo> entry : clientMap.entrySet()) {
				MqttClient client = entry.getKey();
				try {
					client.disconnect();
				} catch (MqttException e1) {
					log.error("MqttException: {}", e1.getMessage());
				}

				ClientInfo ci = entry.getValue();
				try {
					client.connect(ci.getOptions());
					client.subscribe(ci.getTopics().toArray(new String[0]));
				} catch (MqttException e) {
					log.error("MqttException: {}", e.getMessage());
				}
			}
		}

		public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {

			log.info("Mqtt message with id {} received on topic {}. Payload size: {} bytes", mqttMessage.getId(), topic, mqttMessage.getPayload().length);
			log.debug("Body of the message: {}", mqttMessage.toString());

			try {
				ObjectMapper mapper = new ObjectMapper();
				if (topic.endsWith("/measures")) {
					CsvMeasuresJson json = mapper.readValue(mqttMessage.getPayload(), CsvMeasuresJson.class);
					log.debug("Message {} is for device {}", mqttMessage.getId(), json.getDeviceId());

					User u = userService.getUserByUsername(topic.substring(5, topic.length() - 9));
					measureService.createMeasuresFromJson(json, false, u.getUsername());
				} else if (topic.endsWith("/config")) {
					User u = userService.getUserByUsername(topic.substring(5, topic.length()-7));
					configMeasureService.updateConfigFromInput((List<ConfigMeasureJson>) mapper.readValue(mqttMessage.getPayload(), new TypeReference<List<ConfigMeasureJson>>(){}), false, u.getUsername());
				}
			} catch (JsonGenerationException e) {
				log.error("JsonGenerationException: {}", e.getMessage());
			} catch (JsonMappingException e) {
				log.error("JsonMappingException: {}", e.getMessage());
			} catch (IOException e) {
				log.error("IOException: {}", e.getMessage());
			} catch (Exception e) {
				log.error("Exception: {}", e.getMessage());
			}

			log.info("Mqtt message with id {} on topic {} processed successfully", mqttMessage.getId(), topic);
		}

		@Override
		public void deliveryComplete(IMqttDeliveryToken token) {

		}
	}

	private static SSLSocketFactory getSocketFactory(final String caCrtFile, final String crtFile, final String keyFile, final String password) throws Exception {

		Security.addProvider(new BouncyCastleProvider());

		// load CA certificate
		X509Certificate caCert = null;

		FileInputStream fis = new FileInputStream(caCrtFile);
		BufferedInputStream bis = new BufferedInputStream(fis);
		CertificateFactory cf = CertificateFactory.getInstance("X.509");

		while (bis.available() > 0) {
			caCert = (X509Certificate) cf.generateCertificate(bis);
		}

		// CA certificate is used to authenticate server
		KeyStore caKs = KeyStore.getInstance(KeyStore.getDefaultType());
		caKs.load(null, null);
		caKs.setCertificateEntry("ca-certificate", caCert);
		TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
		tmf.init(caKs);

		if ((crtFile != null) && (keyFile != null)) {
			// load client certificate
			bis = new BufferedInputStream(new FileInputStream(crtFile));
			X509Certificate cert = null;
			while (bis.available() > 0) {
				cert = (X509Certificate) cf.generateCertificate(bis);
			}

			// load client private key
			PEMParser pemParser = new PEMParser(new FileReader(keyFile));
			Object object = pemParser.readObject();
			PEMDecryptorProvider decProv = new JcePEMDecryptorProviderBuilder().build(password.toCharArray());
			JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
			KeyPair key;
			if (object instanceof PEMEncryptedKeyPair) {
				key = converter.getKeyPair(((PEMEncryptedKeyPair) object).decryptKeyPair(decProv));
			} else {
				key = converter.getKeyPair((PEMKeyPair) object);
			}
			pemParser.close();

			KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
			ks.load(null, null);
			ks.setCertificateEntry("certificate", cert);
			ks.setKeyEntry("private-key", key.getPrivate(), password.toCharArray(), new java.security.cert.Certificate[] { cert });
			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmf.init(ks, password.toCharArray());

			// finally, create SSL socket factory
			SSLContext context = SSLContext.getInstance("TLSv1.2");
			context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

			return context.getSocketFactory();
		} else {
			SSLContext context = SSLContext.getInstance("TLSv1.2");
			context.init(null, tmf.getTrustManagers(), null);

			return context.getSocketFactory();
		}
	}

	public class ClientInfo {

		private List<String> topics;

		private MqttConnectOptions options;

		public ClientInfo(List<String> topics, MqttConnectOptions options) {
			super();
			this.topics = topics;
			this.options = options;
		}

		public List<String> getTopics() {
			return topics;
		}

		public void setTopics(List<String> topics) {
			this.topics = topics;
		}

		public MqttConnectOptions getOptions() {
			return options;
		}

		public void setOptions(MqttConnectOptions options) {
			this.options = options;
		}
	}
}
