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

package org.jbpm.integration.cmis.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.commons.io.IOUtils;
import org.jbpm.document.Document;
import org.jbpm.integration.cmis.Operation;
import org.jbpm.integration.cmis.UpdateMode;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

/**
 * WorkItemHandler implementation that provides integration with CMIS.
 * Allows to specify connection details (user, password, url, repository) either
 * at handler creation time or can be provided as part of work item parameters:
 * <ul>
 * 	<li>User</li>
 * 	<li>Password</li>
 * 	<li>Url</li>
 * 	<li>Repository</li>
 * </ul>
 * above are optional parameters assuming that these where given at handler creation time. 
 * A mandatory parameters are:
 * <ul>
 *  <li><code>Document</code> that must be of type <code>org.jbpm.integration.cmis.Document</code></li>
 *  <li><code>Operation</code> that must be a string representation of <code>Operation</code> enum values</li>
 * </ul>
 * in addition to that, update mode might be specified in case <code>Operation.DOC_UPDATE</code> operation is used.
 * UpdateMode allows to select on of following options on how to update document:
 * <ul>
 * 	<li>override (default) which always override the document content</li>
 * 	<li>major - which always creates new major version</li>
 * 	<li>minor - which always creates new minor version</li>
 * </ul>  
 */
public class OpenCMISWorkItemHandler extends OpenCMISSupport implements WorkItemHandler {
	
	private String user;
	private String password;
	private String url;
	private String repository;
	
	private UpdateMode mode = UpdateMode.OVERRIDE;
	
	private Operation operation = Operation.DOC_CREATE;
	
	public OpenCMISWorkItemHandler() {
		
	}
	
	public OpenCMISWorkItemHandler(String user, String password, String url, String repository) {
		this.user = user;
		this.password = password;
		this.url = url;
		this.repository = repository;
	}
	
	public OpenCMISWorkItemHandler(String user, String password, String url, String repository, UpdateMode mode) {
		this.user = user;
		this.password = password;
		this.url = url;
		this.repository = repository;
		this.mode = mode;
	}

	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		Document document = (Document) workItem.getParameter("Document");
		UpdateMode lMode = mode;
		if (workItem.getParameter("UpdateMode") != null) {
			lMode = UpdateMode.valueOf((String) workItem.getParameter("UpdateMode"));
		}
		
		Operation lOperation = operation;
		if (workItem.getParameter("Operation") != null) {
			lOperation = Operation.valueOf((String) workItem.getParameter("Operation"));
		}
		
		Session session = getRepositorySession(getUser(workItem), getPassword(workItem), getUrl(workItem), getRepository(workItem));
		try {
			String type = document.getAttribute("type");
			String location = document.getAttribute("location");
			switch (lOperation) {
			case DOC_CREATE:
				if (document.getIdentifier() == null) {
					Folder parent = findFolderForPath(session, location);
					if (parent == null) {
						parent = createFolder(session, null, location);
					}
					org.apache.chemistry.opencmis.client.api.Document doc = createDocument(session, parent, document.getName(), type, document.getContent());
					document.setIdentifier(doc.getId());
					document.addAttribute("updated", "false");
				}
				break;
			case DOC_UPDATE:
				if (document.getContent() != null) {
					org.apache.chemistry.opencmis.client.api.Document doc = updateDocument(session, document.getIdentifier(), type, document.getContent(), lMode);
					document.setIdentifier(doc.getId());
					document.addAttribute("updated", "false");
				}
				break;
			case DOC_DELETE:
				if (document.getIdentifier() != null) {
					deleteDocument(session, document.getIdentifier());
					document.setContent(null);
					document.addAttribute("updated", "false");
				}
				break;
			case DOC_FETCH:
				if (document.getIdentifier() != null) {
					org.apache.chemistry.opencmis.client.api.Document doc = 
							(org.apache.chemistry.opencmis.client.api.Document) findObjectForId(session, document.getIdentifier());
					document.setName(doc.getName());			
					document.addAttribute("location", getFolderName(doc.getParents()) + getPathAsString(doc.getPaths()));
					if (doc.getContentStream() != null) {
						ContentStream stream = doc.getContentStream();
						try {
							document.setContent(IOUtils.toByteArray(stream.getStream()));
						} catch (IOException e) {
							throw new RuntimeException("Cannot read document content", e);
						}
						document.addAttribute("updated", "false");
						document.addAttribute("type", stream.getMimeType());	
					}
				}
				break;
			default:
				break;
			}

			if (manager != null) {
				Map<String, Object> results = new HashMap<String, Object>();
				results.put("Result", document);
				manager.completeWorkItem(workItem.getId(), results);
			}
		} finally {
			session.clear();
		}

	}

	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		// do nothing
	}

	protected String getUser(WorkItem workItem) {
		String workItemValue = (String) workItem.getParameter("User");
		if (workItemValue == null && user == null) {
			throw new IllegalStateException("User for CMIS connection not provided");
		}
		
		if (workItemValue != null) {
			return workItemValue;
		}
		
		return user;
	}
	
	protected String getPassword(WorkItem workItem) {
		String workItemValue = (String) workItem.getParameter("Password");
		if (workItemValue == null && password == null) {
			throw new IllegalStateException("Password for CMIS connection not provided");
		}
		
		if (workItemValue != null) {
			return workItemValue;
		}
		
		return password;
	}
	
	protected String getUrl(WorkItem workItem) {
		String workItemValue = (String) workItem.getParameter("Url");
		if (workItemValue == null && url == null) {
			throw new IllegalStateException("Url for CMIS connection not provided");
		}
		
		if (workItemValue != null) {
			return workItemValue;
		}
		
		return url;
	}
	
	protected String getRepository(WorkItem workItem) {
		String workItemValue = (String) workItem.getParameter("Repository");
		if (workItemValue == null && repository == null) {
			throw new IllegalStateException("Repository for CMIS connection not provided");
		}
		
		if (workItemValue != null) {
			return workItemValue;
		}
		
		return repository;
	}
}
