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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;
import org.eclipse.paho.client.mqttv3.MqttClient;

/**
 * Publish Mqtt messages to the broker url which is given at the init
 */
public class MqttPublish extends AbstractConnector {

	private boolean isAsync;

	private static Log log = LogFactory.getLog(MqttPublish.class);

	@Override
	public void connect(MessageContext messageContext) throws ConnectException {
		String topic = MqttUtils.lookupTemplateParamater(messageContext,
				"topic");
		String msg = MqttUtils.lookupTemplateParamater(messageContext, "msg");
		MqttClient Client = null;
		MqttAsyncClient asyncClient = null;
		MqttClientLoader clientLoader = new MqttClientLoader(messageContext);
		String ID = (String) messageContext.getProperty("ClientID");

		// setting QoS
		int qos = 1;
		if (MqttUtils.lookupTemplateParamater(messageContext,
				MqttConnectConstants.MQTT_QOS) != null
				&& !MqttUtils.lookupTemplateParamater(messageContext,
						MqttConnectConstants.MQTT_QOS).isEmpty()) {
			if (MqttUtils.lookupTemplateParamater(messageContext,
					MqttConnectConstants.MQTT_QOS) == "0")
				qos = 0;
			else if (MqttUtils.lookupTemplateParamater(messageContext,
					MqttConnectConstants.MQTT_QOS) == "2")
				qos = 2;
		}

		try {
			// if the clients have not yet being initialized
			if (messageContext
					.getProperty(MqttConnectConstants.MQTT_NON_BLOCKING) != null
					&& messageContext
							.getProperty(MqttConnectConstants.MQTT_NON_BLOCKING)
							.toString().equalsIgnoreCase("true")) {
				asyncClient = clientLoader.loadAsyncClient();
				isAsync = true;
				IMqttDeliveryToken pubToken = asyncClient.publish(topic,
						msg.getBytes(), qos, false);
				pubToken.waitForCompletion();
			} else {
				Client = clientLoader.loadClient();
				Client.publish(topic, new MqttMessage(msg.getBytes()));
				isAsync = false;
			}

			messageContext.setProperty(MqttConnectConstants.INIT_MODE, "false");

			// disconnect the client if specified in the input parameters
			if (MqttUtils.lookupTemplateParamater(messageContext,
					MqttConnectConstants.MQTT_DIS) != null
					&& MqttUtils.lookupTemplateParamater(messageContext,
							MqttConnectConstants.MQTT_DIS).equalsIgnoreCase(
							"true")) {
				if (isAsync) {
					asyncClient.disconnect();
					asyncClient.close();
				} else {
					Client.disconnect();
					Client.close();
				}
				clientLoader.destroyClient(ID, isAsync);
			}

		} catch (MqttPersistenceException e) {
			log.error("Coudn't perform the operation: " + e.getMessage(), e);
		} catch (MqttException e) {
			log.error("Coudn't perform the operation: " + e.getMessage(), e);
		} catch (NumberFormatException e) {
			log.error("Coudn't perform the operation: " + e.getMessage(), e);
		}

	}
}