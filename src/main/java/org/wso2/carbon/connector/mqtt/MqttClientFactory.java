/*
 *  Copyright (c) 2005-2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.connector.mqtt;

import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

/**
 * Create the client and connect it to the hostName+port with given options
 * Return the connected mqtt client blocking or non-blocking
 */
public class MqttClientFactory {
	private MessageContext messageContext;
	private static ConcurrentHashMap<String, MqttClient> Clients = new ConcurrentHashMap<String, MqttClient>();
	private static ConcurrentHashMap<String, MqttAsyncClient> AsyncClients = new ConcurrentHashMap<String, MqttAsyncClient>();
	private static final Log log = LogFactory.getLog(MqttClientFactory.class);

	public MqttClientFactory(MessageContext ctxt) {
		this.messageContext = ctxt;
	}

	/**
	 * @return blocking client
	 */
	public MqttClient loadClient() throws MqttException, NumberFormatException {
		MqttClient blockingClient = null;
		if (messageContext
				.getProperty(MqttConnectConstants.MQTT_SERVER_HOST_NAME) != null
				&& messageContext
						.getProperty(MqttConnectConstants.MQTT_SERVER_PORT) != null) {
			// setting protocol and url
			String protocol = "tcp://";
			if (messageContext
					.getProperty(MqttConnectConstants.MQTT_SSL_ENABLE) != null)
				if (messageContext
						.getProperty(MqttConnectConstants.MQTT_SSL_ENABLE)
						.toString().equalsIgnoreCase("true"))
					protocol = "ssl://";
			String hostName = messageContext.getProperty(
					MqttConnectConstants.MQTT_SERVER_HOST_NAME).toString();
			String port = messageContext.getProperty(
					MqttConnectConstants.MQTT_SERVER_PORT).toString();
			String broker = protocol + hostName + ":" + port;
			log.info("Setting client to the broker: " + broker);

			// setting persistence location
			String tmpDir = System.getProperty("java.io.tmpdir");
			if (messageContext
					.getProperty(MqttConnectConstants.MQTT_PERSISTANCE) != null) {
				tmpDir = messageContext.getProperty(
						MqttConnectConstants.MQTT_PERSISTANCE).toString();
			}
			MqttDefaultFilePersistence dataStore = new MqttDefaultFilePersistence(
					tmpDir);

			// getting the blocking client and connecting it to the MB
			String ID = (String) messageContext.getProperty("ClientID");
			if (messageContext.getProperty(MqttConnectConstants.INIT_MODE)
					.equals("true")) {
				blockingClient = new MqttClient(broker, ID);
				Clients.put(ID, blockingClient);
			} else {
				blockingClient = Clients.get(ID);
			}
			if (!blockingClient.isConnected()) {
				blockingClient.connect(getOptions());
			}
		} else {
			blockingClient = null;
		}
		return blockingClient;
	}

	/**
	 * @return non-blocking client
	 */
	public MqttAsyncClient loadAsyncClient() throws MqttException,
			NumberFormatException {
		MqttAsyncClient asyncClient = null;
		if (messageContext
				.getProperty(MqttConnectConstants.MQTT_SERVER_HOST_NAME) != null
				&& messageContext
						.getProperty(MqttConnectConstants.MQTT_SERVER_PORT) != null) {
			// setting protocol
			String protocol = "tcp://";
			if (messageContext
					.getProperty(MqttConnectConstants.MQTT_SSL_ENABLE) != null)
				if (messageContext
						.getProperty(MqttConnectConstants.MQTT_SSL_ENABLE)
						.toString().equalsIgnoreCase("true"))
					protocol = "ssl://";
			String hostName = messageContext.getProperty(
					MqttConnectConstants.MQTT_SERVER_HOST_NAME).toString();
			String port = messageContext.getProperty(
					MqttConnectConstants.MQTT_SERVER_PORT).toString();
			String broker = protocol + hostName + ":" + port;
			log.info("Setting Async client to the broker: " + broker);

			// setting persistence location
			String tmpDir = System.getProperty("java.io.tmpdir");

			if (messageContext
					.getProperty(MqttConnectConstants.MQTT_PERSISTANCE) != null) {
				tmpDir = messageContext.getProperty(
						MqttConnectConstants.MQTT_PERSISTANCE).toString();
			}
			MqttDefaultFilePersistence dataStore = new MqttDefaultFilePersistence(
					tmpDir);

			// creating the async client and connecting it
			String ID = (String) messageContext.getProperty("ClientID");
			if (messageContext.getProperty(MqttConnectConstants.INIT_MODE)
					.equals("true")) {
				asyncClient = new MqttAsyncClient(broker, ID);
				AsyncClients.put(ID, asyncClient);
			}
			if (!asyncClient.isConnected()) {
				IMqttToken conToken = asyncClient.connect(getOptions());
				conToken.waitForCompletion();
			}

		} else {
			asyncClient = null;
		}
		return asyncClient;
	}

	/**
	 * Remove the client from the HashMap when disconnects
	 * @param ClientID
	 * @param isAsync client
	 */
	public void destroyClient(String ID, boolean isAsync) {
		if (isAsync) {
			AsyncClients.remove(ID);
		} else {
			Clients.remove(ID);
		}
	}

	/**
	 * Setting options to the mqtt client
	 * @return options
	 */
	private MqttConnectOptions getOptions() {
		// Setting Connection Options
		MqttConnectOptions options = new MqttConnectOptions();

		// set username and password
		if (messageContext.getProperty(MqttConnectConstants.MQTT_USERNAME) != null
				&& messageContext
						.getProperty(MqttConnectConstants.MQTT_PASSWORD) != null) {
			options.setUserName(messageContext.getProperty(
					MqttConnectConstants.MQTT_USERNAME).toString());
			options.setUserName(messageContext.getProperty(
					MqttConnectConstants.MQTT_PASSWORD).toString());
		}

		// set last will settings
		if (messageContext.getProperty(MqttConnectConstants.MQTT_LW_MSG) != null
				&& messageContext
						.getProperty(MqttConnectConstants.MQTT_LW_TOPIC) != null) {
			int qos = 1;
			boolean retained = false;
			if (messageContext.getProperty(MqttConnectConstants.MQTT_LW_QOS) != null) {
				if (messageContext
						.getProperty(MqttConnectConstants.MQTT_LW_QOS)
						.toString().equals("0")
						|| messageContext
								.getProperty(MqttConnectConstants.MQTT_LW_QOS)
								.toString().equals("2")) {
					qos = Integer.parseInt(messageContext.getProperty(
							MqttConnectConstants.MQTT_LW_QOS).toString());
				}
			}
			if (messageContext
					.getProperty(MqttConnectConstants.MQTT_LW_RETAINED)
					.toString().equalsIgnoreCase("true")) {
				retained = true;
			}
			options.setWill(
					messageContext.getProperty(
							MqttConnectConstants.MQTT_LW_TOPIC).toString(),
					messageContext
							.getProperty(MqttConnectConstants.MQTT_LW_MSG)
							.toString().getBytes(), qos, retained);
		}

		// set clean session : true for default
		if (messageContext.getProperty(MqttConnectConstants.MQTT_CLEAN_SESSION) != null)
			if (messageContext
					.getProperty(MqttConnectConstants.MQTT_CLEAN_SESSION)
					.toString().equalsIgnoreCase("false")) {
				options.setCleanSession(false);
			}

		return options;
	}

}