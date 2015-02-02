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

public class MqttConnectConstants {

	// connect options parameters
	public static final String MQTT_SERVER_HOST_NAME = "hostName";
	public static final String MQTT_SERVER_PORT = "port";
	public static final String MQTT_USERNAME = "username";
	public static final String MQTT_PASSWORD = "password";
	public static final String MQTT_CLEAN_SESSION = "cleanSession";
	public static final String MQTT_CON_TIMEOUT = "connectionTimeout";
	public static final String MQTT_KEEPALIVE = "keepAliveInterval";
	public static final String MQTT_LW_MSG = "lwMessage";
	public static final String MQTT_LW_QOS = "lwQos";
	public static final String MQTT_LW_RETAINED = "lwRetained";
	public static final String MQTT_LW_TOPIC = "lwTopicName";
	public static final String MQTT_PERSISTANCE = "persistenceLocation";
	public static final String MQTT_NON_BLOCKING = "asyncClientEnable";
	public static final String MQTT_SSL_ENABLE = "sslEnable";

	// publish options
	public static final String MQTT_TOPIC_NAME = "topic";
	public static final String MQTT_QOS = "qos";
	public static final String MQTT_MSG = "msg";
	public static final String MQTT_DIS = "disconnectAfter";
	public static final String MQTT_RETAINED = "retained";

	public static final String INIT_MODE = "initMode";

}