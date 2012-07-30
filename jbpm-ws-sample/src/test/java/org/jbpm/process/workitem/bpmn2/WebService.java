package org.jbpm.process.workitem.bpmn2;

import javax.xml.ws.Endpoint;

public class WebService {

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		SimpleService service = new SimpleService();
        Endpoint.publish("http://127.0.0.1:9876/HelloService/greeting", service);
        
        Thread.sleep(1000000000);
	}

}
