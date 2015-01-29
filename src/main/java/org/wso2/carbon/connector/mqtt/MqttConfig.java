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
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;

public class MqttConfig extends AbstractConnector {

	private static Log log = LogFactory.getLog(MqttConfig.class);

	@Override
	public void connect(MessageContext messageContext) throws ConnectException {
		try {
			String hostName = MqttUtils.lookupTemplateParamater(messageContext,
					MqttConnectConstants.MQTT_SERVER_HOST_NAME);
			String port = MqttUtils.lookupTemplateParamater(messageContext,
					MqttConnectConstants.MQTT_SERVER_PORT);

			// optional parameters
			String userName = (MqttUtils.lookupTemplateParamater(
					messageContext, MqttConnectConstants.MQTT_USERNAME) != null && !MqttUtils
					.lookupTemplateParamater(messageContext,
							MqttConnectConstants.MQTT_USERNAME).isEmpty()) ? MqttUtils
					.lookupTemplateParamater(messageContext,
							MqttConnectConstants.MQTT_USERNAME) : null;
			String password = (MqttUtils.lookupTemplateParamater(
					messageContext, MqttConnectConstants.MQTT_PASSWORD) != null && !MqttUtils
					.lookupTemplateParamater(messageContext,
							MqttConnectConstants.MQTT_PASSWORD).isEmpty()) ? MqttUtils
					.lookupTemplateParamater(messageContext,
							MqttConnectConstants.MQTT_PASSWORD) : null;
			String cleanSession = (MqttUtils.lookupTemplateParamater(
					messageContext, MqttConnectConstants.MQTT_CLEAN_SESSION) != null && !MqttUtils
					.lookupTemplateParamater(messageContext,
							MqttConnectConstants.MQTT_CLEAN_SESSION).isEmpty()) ? MqttUtils
					.lookupTemplateParamater(messageContext,
							MqttConnectConstants.MQTT_CLEAN_SESSION) : null;
			String connectionTimeout = (MqttUtils.lookupTemplateParamater(
					messageContext, MqttConnectConstants.MQTT_CON_TIMEOUT) != null && !MqttUtils
					.lookupTemplateParamater(messageContext,
							MqttConnectConstants.MQTT_CON_TIMEOUT).isEmpty()) ? MqttUtils
					.lookupTemplateParamater(messageContext,
							MqttConnectConstants.MQTT_CON_TIMEOUT) : null;
			String keepAliveInterval = (MqttUtils.lookupTemplateParamater(
					messageContext, MqttConnectConstants.MQTT_KEEPALIVE) != null && !MqttUtils
					.lookupTemplateParamater(messageContext,
							MqttConnectConstants.MQTT_KEEPALIVE).isEmpty()) ? MqttUtils
					.lookupTemplateParamater(messageContext,
							MqttConnectConstants.MQTT_KEEPALIVE) : null;
			String lwMessage = (MqttUtils.lookupTemplateParamater(
					messageContext, MqttConnectConstants.MQTT_LW_MSG) != null && !MqttUtils
					.lookupTemplateParamater(messageContext,
							MqttConnectConstants.MQTT_LW_MSG).isEmpty()) ? MqttUtils
					.lookupTemplateParamater(messageContext,
							MqttConnectConstants.MQTT_LW_MSG) : null;
			String lwQos = (MqttUtils.lookupTemplateParamater(messageContext,
					MqttConnectConstants.MQTT_LW_QOS) != null && !MqttUtils
					.lookupTemplateParamater(messageContext,
							MqttConnectConstants.MQTT_LW_QOS).isEmpty()) ? MqttUtils
					.lookupTemplateParamater(messageContext,
							MqttConnectConstants.MQTT_LW_QOS) : null;
			String lwRetained = (MqttUtils.lookupTemplateParamater(
					messageContext, MqttConnectConstants.MQTT_LW_RETAINED) != null && !MqttUtils
					.lookupTemplateParamater(messageContext,
							MqttConnectConstants.MQTT_LW_RETAINED).isEmpty()) ? MqttUtils
					.lookupTemplateParamater(messageContext,
							MqttConnectConstants.MQTT_LW_RETAINED) : null;
			String lwTopicName = (MqttUtils.lookupTemplateParamater(
					messageContext, MqttConnectConstants.MQTT_LW_TOPIC) != null && !MqttUtils
					.lookupTemplateParamater(messageContext,
							MqttConnectConstants.MQTT_LW_TOPIC).isEmpty()) ? MqttUtils
					.lookupTemplateParamater(messageContext,
							MqttConnectConstants.MQTT_LW_TOPIC) : null;
			String persistenceLocation = (MqttUtils.lookupTemplateParamater(
					messageContext, MqttConnectConstants.MQTT_PERSISTANCE) != null && !MqttUtils
					.lookupTemplateParamater(messageContext,
							MqttConnectConstants.MQTT_PERSISTANCE).isEmpty()) ? MqttUtils
					.lookupTemplateParamater(messageContext,
							MqttConnectConstants.MQTT_PERSISTANCE) : null;
			String ssl = (MqttUtils.lookupTemplateParamater(messageContext,
					MqttConnectConstants.MQTT_SSL_ENABLE) != null && !MqttUtils
					.lookupTemplateParamater(messageContext,
							MqttConnectConstants.MQTT_SSL_ENABLE).isEmpty()) ? MqttUtils
					.lookupTemplateParamater(messageContext,
							MqttConnectConstants.MQTT_SSL_ENABLE) : null;
			String nonBlocking = (MqttUtils.lookupTemplateParamater(
					messageContext, MqttConnectConstants.MQTT_NON_BLOCKING) != null && !MqttUtils
					.lookupTemplateParamater(messageContext,
							MqttConnectConstants.MQTT_NON_BLOCKING).isEmpty()) ? MqttUtils
					.lookupTemplateParamater(messageContext,
							MqttConnectConstants.MQTT_NON_BLOCKING) : null;

			MqttUtils.storeLoginUser(messageContext, hostName, port, userName,
					password, ssl, nonBlocking, cleanSession,
					connectionTimeout, keepAliveInterval, lwMessage, lwQos,
					lwRetained, lwTopicName, persistenceLocation);

		} catch (Exception e) {
			log.error(
					"Failed to connect to the Message Broker: "
							+ e.getMessage(), e);
		}
	}
}