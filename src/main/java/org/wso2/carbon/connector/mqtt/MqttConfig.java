/*
 *  Copyright (c) 2005-2013, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

  	public static final String MQTT_SERVER_HOST_NAME = "mqtt.server.host.name";
	public static final String MQTT_SERVER_PORT = "mqtt.server.port";	
	public static final String MQTT_USERNAME = "mqtt.connection.username";
	public static final String MQTT_PASSWORD = "mqtt.connection.password";

    @Override
    public void connect(MessageContext messageContext) throws ConnectException {
	try {
	    String hostName = MqttUtils.lookupTemplateParamater(messageContext, MQTT_SERVER_HOST_NAME);
	    String port = MqttUtils.lookupTemplateParamater(messageContext, MQTT_SERVER_PORT);
	    String userName = MqttUtils.lookupTemplateParamater(messageContext, MQTT_USERNAME);
	    String password = MqttUtils.lookupTemplateParamater(messageContext, MQTT_PASSWORD);

	    MqttUtils.storeLoginUser(messageContext, hostName, port, userName,
		    password);
	    if (log.isDebugEnabled()) {
		log.info("Connected to the Message Broker");
	    }
	} catch (Exception e) {
	    log.error("Failed to connect to the Message Broker: " + e.getMessage(), e);
	    MqttUtils.storeErrorResponseStatus(messageContext, e);
	}
    }
}