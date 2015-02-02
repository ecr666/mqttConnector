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
import org.eclipse.paho.client.mqttv3.*;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;

/**
 * Publish Mqtt messages to the broker url which is given at the init
 */
public class MqttPublish extends AbstractConnector {

	private static Log log = LogFactory.getLog(MqttPublish.class);

	private enum qosLevel {
		ZERO("0"), ONE("1"), TWO("2");
		String qos;

		qosLevel(String s) {
			qos = s;
		}
	}

	@Override
	public void connect(MessageContext messageContext) throws ConnectException {
		MqttClient Client = null;
		MqttAsyncClient asyncClient = null;
		MqttClientFactory clientFactory = new MqttClientFactory(messageContext);
		boolean isAsync;

		//input parameters
		String topic = MqttUtils.lookupTemplateParamater(messageContext, "topic");
		String msg = MqttUtils.lookupTemplateParamater(messageContext, "msg");
		String qosInput =
				(MqttUtils.lookupTemplateParamater(messageContext, MqttConnectConstants.MQTT_QOS) !=
				 null &&
				 !MqttUtils.lookupTemplateParamater(messageContext, MqttConnectConstants.MQTT_QOS)
				           .isEmpty()) ?
				MqttUtils.lookupTemplateParamater(messageContext, MqttConnectConstants.MQTT_QOS) :
				null;
		String async = (MqttUtils.lookupTemplateParamater(messageContext,
		                                                  MqttConnectConstants.MQTT_NON_BLOCKING) !=
		                null && !MqttUtils
				.lookupTemplateParamater(messageContext, MqttConnectConstants.MQTT_NON_BLOCKING)
				.isEmpty()) ? MqttUtils.lookupTemplateParamater(messageContext,
		                                                        MqttConnectConstants.MQTT_NON_BLOCKING) :
		               null;
		String disAfter =
				(MqttUtils.lookupTemplateParamater(messageContext, MqttConnectConstants.MQTT_DIS) !=
				 null &&
				 !MqttUtils.lookupTemplateParamater(messageContext, MqttConnectConstants.MQTT_DIS)
				           .isEmpty()) ?
				MqttUtils.lookupTemplateParamater(messageContext, MqttConnectConstants.MQTT_DIS) :
				null;
		String retained = (MqttUtils.lookupTemplateParamater(messageContext,
		                                                     MqttConnectConstants.MQTT_RETAINED) !=
		                   null && !MqttUtils
				.lookupTemplateParamater(messageContext, MqttConnectConstants.MQTT_RETAINED)
				.isEmpty()) ? MqttUtils.lookupTemplateParamater(messageContext,
		                                                        MqttConnectConstants.MQTT_RETAINED) :
		                  null;

		String ID = (String) messageContext.getProperty("ClientID");

		// setting QoS - default is 1
		int qos = 1;
		if (qosLevel.ZERO.qos.equals(qosInput))
			qos = 0;
		else if (qosLevel.TWO.qos.equals(qosInput))
			qos = 2;
		//setting retained settings
		boolean isRetained = false;
		if ("true".equalsIgnoreCase(retained))
			isRetained = true;
		//requesting for a client and publish the message
		try {
			if ("true".equalsIgnoreCase(async)) {
				asyncClient = clientFactory.loadAsyncClient();
				isAsync = true;
				IMqttDeliveryToken pubToken = asyncClient.publish(topic,
				                                                  msg.getBytes(), qos, isRetained);
				pubToken.waitForCompletion();
				log.info("Publish is completed to the topic: " + topic + " in QoS: " + qos +
				         " by Async MQTT Client: " + asyncClient.getClientId());
			} else {
				Client = clientFactory.loadClient();
				Client.publish(topic, msg.getBytes(), qos, isRetained);
				log.info("Publish is completed to the topic: " + topic + " in QoS: " + qos +
				         " by Blocking MQTT Client: " + Client.getClientId());
				isAsync = false;
			}

			messageContext.setProperty(MqttConnectConstants.INIT_MODE, "false");

			// disconnect the client if specified in the input parameters
			if ("true".equalsIgnoreCase(disAfter)) {
				if (isAsync) {
					asyncClient.disconnect();
					asyncClient.close();
				} else {
					Client.disconnect();
					Client.close();
				}
				clientFactory.destroyClient(ID, isAsync);
			}

		} catch (MqttPersistenceException e) {
			log.error("Couldn't perform the operation: " + e.getMessage(), e);
		} catch (MqttException e) {
			log.error("Couldn't perform the operation: " + e.getMessage(), e);
		} catch (NumberFormatException e) {
			log.error("Couldn't perform the operation: " + e.getMessage(), e);
		} catch (NullPointerException e) {
			log.error("Client is not initialized: " + e.getMessage(), e);
		}

	}
}