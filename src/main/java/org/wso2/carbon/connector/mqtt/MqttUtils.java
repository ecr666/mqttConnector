/*
 *  Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseConstants;
import org.wso2.carbon.connector.core.util.ConnectorUtils;


public class MqttUtils {

    public static String lookupTemplateParamater(MessageContext ctxt, String paramName) {
	return (String) ConnectorUtils.lookupTemplateParamater(ctxt, paramName);

    }
/*
    public static void storeResponseStatus(MessageContext ctxt, Status status) {
	ctxt.setProperty(MqttConnectConstants.TWITTER_STATUS_USER_SCREEN_NAME, status.getUser()
		.getScreenName());
	ctxt.setProperty(MqttConnectConstants.TWITTER_STATUS_STATUS_TEXT, status.getText());
	ctxt.setProperty(MqttConnectConstants.TWITTER_API_RESPONSE, status);
    }
*/
    public static void storeLoginUser1(MessageContext ctxt, String hostName, String port, String username, String password) {
	ctxt.setProperty(MqttConnectConstants.MQTT_SERVER_HOST_NAME, hostName);
	ctxt.setProperty(MqttConnectConstants.MQTT_SERVER_PORT, port);
	ctxt.setProperty(MqttConnectConstants.MQTT_USERNAME, username);
	ctxt.setProperty(MqttConnectConstants.MQTT_PASSWORD, password);
    }
    
    public static void storeLoginUser(MessageContext ctxt, String hostName, String port, String username, String password, String ssl, String nonBlocking, String cleanSession, String connectionTimeout, String keepAliveInterval, String lwMessage, String lwQos, String lwRetained, String lwTopicName, String persistenceLocation ) {
    	ctxt.setProperty(MqttConnectConstants.MQTT_SERVER_HOST_NAME, hostName);
    	ctxt.setProperty(MqttConnectConstants.MQTT_SERVER_PORT, port);
    	ctxt.setProperty(MqttConnectConstants.MQTT_USERNAME, username);
    	ctxt.setProperty(MqttConnectConstants.MQTT_PASSWORD, password);
    	ctxt.setProperty(MqttConnectConstants.MQTT_SSL_ENABLE, ssl);
    	ctxt.setProperty(MqttConnectConstants.MQTT_NON_BLOCKING, nonBlocking);
    	ctxt.setProperty(MqttConnectConstants.MQTT_CLEAN_SESSION, cleanSession);
    	ctxt.setProperty(MqttConnectConstants.MQTT_CON_TIMEOUT, connectionTimeout);
    	ctxt.setProperty(MqttConnectConstants.MQTT_KEEPALIVE, keepAliveInterval);
    	ctxt.setProperty(MqttConnectConstants.MQTT_LW_MSG, lwMessage);
    	ctxt.setProperty(MqttConnectConstants.MQTT_LW_QOS, lwQos);
    	ctxt.setProperty(MqttConnectConstants.MQTT_LW_RETAINED, lwRetained);
    	ctxt.setProperty(MqttConnectConstants.MQTT_LW_TOPIC, lwTopicName);
    	ctxt.setProperty(MqttConnectConstants.MQTT_PERSISTANCE, persistenceLocation);
        }

    public static void storeErrorResponseStatus(MessageContext ctxt, Exception e) {
	ctxt.setProperty(SynapseConstants.ERROR_EXCEPTION, e);
	ctxt.setProperty(SynapseConstants.ERROR_MESSAGE, e.getMessage());
    }
}
