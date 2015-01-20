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


public class MqttPublish extends AbstractConnector {

    private static Log log = LogFactory.getLog(MqttPublish.class);

    public static final String MQTT_TOPIC_NAME = "topic";
    public static final String MQTT_MSG = "msg";

    @Override
    public void connect(MessageContext messageContext) throws ConnectException {
	String topic = MqttUtils.lookupTemplateParamater(messageContext, MQTT_TOPIC_NAME);
	String message = MqttUtils.lookupTemplateParamater(messageContext, MQTT_MSG);
	MqttMessage mqttMessage = new MqttMessage(message.getBytes());
	
	//setting QoS
	int qos=1;
	if(MqttUtils.lookupTemplateParamater(messageContext, MqttConnectConstants.MQTT_QOS) != null && !MqttUtils.lookupTemplateParamater(messageContext,MqttConnectConstants.MQTT_QOS).isEmpty()){
		if(MqttUtils.lookupTemplateParamater(messageContext, MqttConnectConstants.MQTT_QOS) == "0")	qos=0;
		else if(MqttUtils.lookupTemplateParamater(messageContext, MqttConnectConstants.MQTT_QOS) == "2") qos=2;
	}
	
	try {
		if(messageContext.getProperty(MqttConnectConstants.MQTT_NON_BLOCKING) != null && messageContext.getProperty(MqttConnectConstants.MQTT_NON_BLOCKING).toString().equalsIgnoreCase("true")){
			MqttAsyncClient mqttAsyncClient = new MqttClientLoader(messageContext).loadAsyncClient();
			IMqttDeliveryToken pubToken= mqttAsyncClient.publish(topic, message.getBytes(), qos, false);
			pubToken.waitForCompletion();
		}
		
		else{
			MqttClient mqttClient = new MqttClientLoader(messageContext).loadClient();
			mqttClient.publish( topic, message.getBytes(), qos, false );
		}
			
		if (log.isDebugEnabled()) {
			log.info("Published Message successfully");
		}
	} catch (MqttPersistenceException e) {
		log.error("Coudn't publish the message: " + e.getMessage(), e);
		e.printStackTrace();
	} catch (MqttException e) {
		log.error("Coudn't publish the message: " + e.getMessage(), e);
		e.printStackTrace();
	}
	catch (NumberFormatException e){
		log.error("Coudn't publish the message: " + e.getMessage(), e);
		e.printStackTrace();
	}
    }
}
