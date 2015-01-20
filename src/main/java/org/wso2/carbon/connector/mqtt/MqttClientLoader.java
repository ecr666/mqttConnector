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
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

import com.hazelcast.logging.Log4jFactory;

/**
 * @author eranda
 *
 */
public class MqttClientLoader {

	private MessageContext messageContext;
	private static final Log log = LogFactory.getLog(MqttClientLoader.class);
	private static MqttClient MQTTClient;
	private static MqttAsyncClient MQTTAsyncClient;

	/*
	 * Create the client and connect it to the hostName+port with given options
	 * Return the connected mqtt client
	 */
	public MqttClientLoader(MessageContext ctxt) {
		this.messageContext = ctxt;
	}

	/**
	 * 
	 * @return
	 * @throws MqttException
	 * @throws NumberFormatException
	 */
	public MqttClient loadClient() throws MqttException,
			NumberFormatException {
		if (messageContext
				.getProperty(MqttConnectConstants.MQTT_SERVER_HOST_NAME) != null
				&& messageContext
						.getProperty(MqttConnectConstants.MQTT_SERVER_PORT) != null) {
			
			//setting protocol and url
			String protocol = "tcp://";
			if(messageContext.getProperty(MqttConnectConstants.MQTT_SSL_ENABLE) != null)	if(messageContext.getProperty(MqttConnectConstants.MQTT_SSL_ENABLE).toString().equalsIgnoreCase("true"))	protocol = "ssl://";
			String hostName = messageContext.getProperty(
					MqttConnectConstants.MQTT_SERVER_HOST_NAME).toString();
			String port = messageContext.getProperty(
					MqttConnectConstants.MQTT_SERVER_PORT).toString();
			String broker = protocol+hostName + ":" + port;
			log.info("Setting client to the broker: " + broker);
			
			// setting persistance location
			String tmpDir = System.getProperty("java.io.tmpdir");
			if (messageContext.getProperty(MqttConnectConstants.MQTT_PERSISTANCE) != null) {
				tmpDir = messageContext.getProperty(MqttConnectConstants.MQTT_PERSISTANCE).toString();
			}
			MqttDefaultFilePersistence dataStore = new MqttDefaultFilePersistence(tmpDir);
			
			//if the Async Client is exists and connected disconnect it
			if(MQTTAsyncClient != null && MQTTAsyncClient.isConnected())	MQTTAsyncClient.disconnect();
			
			//if not the client is already created or if the broker url has been updated
			if ((MQTTClient == null) || (MQTTClient != null && !MQTTClient.getServerURI().equalsIgnoreCase(broker))){
				// creating the client
				MQTTClient = new MqttClient(broker, MqttClient.generateClientId());
			}
			if(!MQTTClient.isConnected())	MQTTClient.connect(getOptions());
			
		} else {
			MQTTClient = null;
		}
		return MQTTClient;
	}
	
	public MqttAsyncClient loadAsyncClient()throws MqttException,
	NumberFormatException {
		if (messageContext
				.getProperty(MqttConnectConstants.MQTT_SERVER_HOST_NAME) != null
				&& messageContext
						.getProperty(MqttConnectConstants.MQTT_SERVER_PORT) != null) {
			
			//setting protocol
			String protocol = "tcp://";
			if(messageContext.getProperty(MqttConnectConstants.MQTT_SSL_ENABLE) != null)	if(messageContext.getProperty(MqttConnectConstants.MQTT_SSL_ENABLE).toString().equalsIgnoreCase("true"))	protocol = "ssl://";
			String hostName = messageContext.getProperty(
					MqttConnectConstants.MQTT_SERVER_HOST_NAME).toString();
			String port = messageContext.getProperty(
					MqttConnectConstants.MQTT_SERVER_PORT).toString();
			String broker = protocol+hostName + ":" + port;
			log.info("Setting Async client to the broker: " + broker);
			
			// setting persistance location
			String tmpDir = System.getProperty("java.io.tmpdir");
			
			if (messageContext.getProperty(MqttConnectConstants.MQTT_PERSISTANCE) != null) {
				tmpDir = messageContext.getProperty(MqttConnectConstants.MQTT_PERSISTANCE).toString();
			}
			MqttDefaultFilePersistence dataStore = new MqttDefaultFilePersistence(tmpDir);
			
			//if the Blocking Client is exists and connected, disconnect it
			if(MQTTClient != null && MQTTClient.isConnected())	MQTTClient.disconnect();
			
			//if not the client is already created or broker url has been changed
			if ((MQTTAsyncClient == null) || (MQTTClient != null && !MQTTAsyncClient.getServerURI().equalsIgnoreCase(broker))){			
				if(MQTTAsyncClient != null)	MQTTAsyncClient.disconnect();
				// creating the async client
				MQTTAsyncClient = new MqttAsyncClient(broker, MqttClient.generateClientId());
			}
			if(!MQTTAsyncClient.isConnected()){
				IMqttToken conToken = MQTTAsyncClient.connect(getOptions());
				conToken.waitForCompletion();
			}
			
		} else {
			MQTTAsyncClient = null;
		}
		return MQTTAsyncClient;
	}
	
	private MqttConnectOptions getOptions(){
		//Setting Connection Options 
		MqttConnectOptions options = new  MqttConnectOptions();
		  
		  //set username and password
		  if(messageContext.getProperty(MqttConnectConstants.MQTT_USERNAME) != null &&
				  messageContext.getProperty(MqttConnectConstants.MQTT_PASSWORD) != null){
		  options.setUserName(messageContext.getProperty(MqttConnectConstants.MQTT_USERNAME).toString());
		  options.setUserName(messageContext.getProperty(MqttConnectConstants.MQTT_PASSWORD).toString()); }
		  
		 //set last will settings 
		  if(messageContext.getProperty(MqttConnectConstants.MQTT_LW_MSG)
		  != null && messageContext.getProperty(MqttConnectConstants.MQTT_LW_TOPIC) != null){ int
		  qos=1; boolean retained=false;
		  if(messageContext.getProperty(MqttConnectConstants.MQTT_LW_QOS) != null){
		  if(messageContext.getProperty(MqttConnectConstants.MQTT_LW_QOS).toString().equals("0") ||
				  messageContext.getProperty(MqttConnectConstants.MQTT_LW_QOS).toString().equals("2")){
		  qos=Integer.parseInt(messageContext.getProperty(MqttConnectConstants.MQTT_LW_QOS).toString()); } }
		  if
		  (messageContext.getProperty(MqttConnectConstants.MQTT_LW_RETAINED).toString().equalsIgnoreCase("true"
		  )){ retained=true; }
		  options.setWill(messageContext.getProperty(MqttConnectConstants.MQTT_LW_TOPIC).toString(),
				  messageContext.getProperty(MqttConnectConstants.MQTT_LW_MSG).toString().getBytes() , qos, retained);
		  }
		  /*
		   //set connection timeout
		  if(messageContext.getProperty(MqttConnectConstants.MQTT_CON_TIMEOUT) != null){ try{
		  options
		  .setConnectionTimeout(Integer.parseInt(messageContext.getProperty(MqttConnectConstants
		  .MQTT_CON_TIMEOUT).toString())); } catch(NumberFormatException e){ throw
		  new NumberFormatException(); } }
		  
		  //set keepalive interval
		  if(messageContext.getProperty(MqttConnectConstants.MQTT_KEEPALIVE) != null){ try{
		  options.
		  setConnectionTimeout(Integer.parseInt(messageContext.getProperty(MqttConnectConstants
		  .MQTT_KEEPALIVE).toString())); } catch(NumberFormatException e){ throw
		  new NumberFormatException(); } }
		  */
		  
		  //set clean session : true for default
		  if(messageContext.getProperty(MqttConnectConstants.MQTT_CLEAN_SESSION) != null)  if(messageContext.getProperty(MqttConnectConstants.MQTT_CLEAN_SESSION).toString().equalsIgnoreCase("false")){
		  options.setCleanSession(false); }
		  
		  return options;
	}

}
