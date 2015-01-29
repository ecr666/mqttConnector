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

import java.util.Collection;
import java.util.Stack;

import junit.framework.TestCase;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.AxisFault;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseConstants;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.mediators.Value;
import org.apache.synapse.mediators.template.TemplateContext;
import org.wso2.carbon.connector.mqtt.MqttPublish;

public class MqttConnectorTest extends TestCase {

	private static final String TEST_TEMPLATE = "test123";

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Method to test single publish for one client and disconnect after that
	 * 
	 * @throws AxisFault
	 */
	public static void testPublishTest() throws AxisFault {
		org.apache.axis2.context.MessageContext axis2Ctx = new org.apache.axis2.context.MessageContext();
		SOAPFactory fac = OMAbstractFactory.getSOAP11Factory();
		org.apache.axiom.soap.SOAPEnvelope envelope = fac.getDefaultEnvelope();
		axis2Ctx.setEnvelope(envelope);
		MessageContext synCtx = new Axis2MessageContext(axis2Ctx, null, null);

		Collection<String> collection = new java.util.ArrayList<String>();
		collection.add("topic");
		collection.add("msg");
		collection.add("disconnectAfter");
		synCtx.setProperty(TEST_TEMPLATE + ":" + "topic", new Value(
				"ESBTesting"));
		synCtx.setProperty(TEST_TEMPLATE + ":" + "msg", new Value(
				"MQTT Connector Publish Testing"));
		synCtx.setProperty(TEST_TEMPLATE + ":" + "disconnectAfter", new Value(
				"true"));
		Stack<TemplateContext> stack = prepareMessageContext(synCtx, collection);

		synCtx.setProperty(SynapseConstants.SYNAPSE__FUNCTION__STACK, stack);

		MqttConfig init = new MqttConfig();
		init.mediate(synCtx);
		MqttPublish publisher = new MqttPublish();
		publisher.mediate(synCtx);
		assertTrue(((Axis2MessageContext) synCtx).getAxis2MessageContext()
				.getEnvelope().getFirstElement() != null);

	}

	private static Stack<TemplateContext> prepareMessageContext(
			MessageContext synCtx, Collection<String> collection) {

		synCtx.setProperty(TEST_TEMPLATE + ":" + "hostName", new Value(
				"localhost"));
		synCtx.setProperty(TEST_TEMPLATE + ":" + "port", new Value("1834"));
		collection.add("hostName");
		collection.add("port");
		TemplateContext context = new TemplateContext(TEST_TEMPLATE, collection);
		Stack<TemplateContext> stack = new Stack<TemplateContext>();
		stack.add(context);
		context.setupParams(synCtx);
		return stack;
	}

}
