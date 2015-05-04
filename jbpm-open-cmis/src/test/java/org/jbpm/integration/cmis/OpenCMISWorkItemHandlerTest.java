/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.integration.cmis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.HashMap;

import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.commons.io.IOUtils;
import org.drools.core.process.instance.impl.WorkItemImpl;
import org.jbpm.document.Document;
import org.jbpm.document.service.impl.DocumentImpl;
import org.jbpm.integration.cmis.impl.OpenCMISSupport;
import org.jbpm.integration.cmis.impl.OpenCMISWorkItemHandler;
import org.junit.Ignore;
import org.junit.Test;
//tests are ignored as they rely on external service and are here to illustrate the usage
@Ignore
public class OpenCMISWorkItemHandlerTest extends OpenCMISSupport {

	private String user = "admin";
	private String password = "admin";
	private String url = "http://cmis.alfresco.com/cmisatom";
	private String repository = "e993fdbb-f417-4c34-911a-21af532c04fc";
	
	@Test
	public void testCreateDocument() throws Exception {
		OpenCMISWorkItemHandler handler = new OpenCMISWorkItemHandler(user, password, url, repository);
		
		WorkItemImpl workItem = new WorkItemImpl();
		workItem.setId(1);
		
		String content = "very simple text content";
		
		Document doc = new DocumentImpl();
		doc.setAttributes(new HashMap<String, String>());
		doc.setName("simple" + System.currentTimeMillis() +".txt");
		doc.addAttribute("type", "text/plain");
		doc.addAttribute("location", "/jbpm-test");
		doc.setContent(content.getBytes());
		
		workItem.setParameter("Document", doc);
		handler.executeWorkItem(workItem, null);
		
		String storedValue = getDocumentContent(doc.getIdentifier());
		assertEquals(content, storedValue);
	}
	
	@Test
	public void testUpdateDocument() throws Exception {
		OpenCMISWorkItemHandler handler = new OpenCMISWorkItemHandler(user, password, url, repository);
		
		WorkItemImpl workItem = new WorkItemImpl();
		workItem.setId(1);
		
		String content = "very simple text content";
		
		Document doc = new DocumentImpl();
		doc.setAttributes(new HashMap<String, String>());
		doc.setName("simple" + System.currentTimeMillis() +".txt");
		doc.addAttribute("type", "text/plain");
		doc.addAttribute("location", "/jbpm-test");
		doc.setContent(content.getBytes());
		
		workItem.setParameter("Document", doc);
		handler.executeWorkItem(workItem, null);
		
		String storedValue = getDocumentContent(doc.getIdentifier());
		assertEquals(content, storedValue);
		
		doc.setContent("updated content".getBytes());
		
		workItem.setParameter("Operation", "DOC_UPDATE");
		handler.executeWorkItem(workItem, null);
		
		storedValue = getDocumentContent(doc.getIdentifier());
		assertEquals("updated content", storedValue);
	}
	
	@Test
	public void testDeleteDocument() throws Exception {
		OpenCMISWorkItemHandler handler = new OpenCMISWorkItemHandler(user, password, url, repository);
		
		WorkItemImpl workItem = new WorkItemImpl();
		workItem.setId(1);
		
		String content = "very simple text content";
		
		Document doc = new DocumentImpl();
		doc.setAttributes(new HashMap<String, String>());
		doc.setName("simple" + System.currentTimeMillis() +".txt");
		doc.addAttribute("type", "text/plain");
		doc.addAttribute("location", "/jbpm-test");
		doc.setContent(content.getBytes());
		
		workItem.setParameter("Document", doc);
		handler.executeWorkItem(workItem, null);
		
		String storedValue = getDocumentContent(doc.getIdentifier());
		assertEquals(content, storedValue);
				
		workItem.setParameter("Operation", "DOC_DELETE");
		handler.executeWorkItem(workItem, null);
		
		storedValue = getDocumentContent(doc.getIdentifier());
		assertNull(storedValue);
	}

	@Test
	public void testFetchDocument() throws Exception {
		OpenCMISWorkItemHandler handler = new OpenCMISWorkItemHandler(user, password, url, repository);
		
		WorkItemImpl workItem = new WorkItemImpl();
		workItem.setId(1);
		
		String content = "very simple text content";
		
		Document doc = new DocumentImpl();
		doc.setAttributes(new HashMap<String, String>());
		doc.setName("simple" + System.currentTimeMillis() +".txt");
		doc.addAttribute("type", "text/plain");
		doc.addAttribute("location", "/jbpm-test");
		doc.setContent(content.getBytes());
		
		workItem.setParameter("Document", doc);
		handler.executeWorkItem(workItem, null);
		
		String storedValue = getDocumentContent(doc.getIdentifier());
		assertEquals(content, storedValue);
				
		workItem.setParameter("Operation", "DOC_FETCH");
		handler.executeWorkItem(workItem, null);
		
		storedValue = getDocumentContent(doc.getIdentifier());
		assertEquals(content, storedValue);
	}
	
	private String getDocumentContent(String documentId) throws IOException {
		Session session = getRepositorySession(user, password, url, repository);
		org.apache.chemistry.opencmis.client.api.Document doc = null;
		try {
			doc = (org.apache.chemistry.opencmis.client.api.Document) findObjectForId(session, documentId);
		} catch (CmisObjectNotFoundException e) {
			return null;
		}
		return IOUtils.toString(doc.getContentStream().getStream());
	}
}
