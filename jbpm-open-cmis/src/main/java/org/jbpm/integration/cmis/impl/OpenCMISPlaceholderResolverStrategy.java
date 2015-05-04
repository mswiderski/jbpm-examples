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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.commons.io.IOUtils;
import org.drools.core.common.DroolsObjectInputStream;
import org.jbpm.document.Document;
import org.jbpm.integration.cmis.UpdateMode;
import org.kie.api.marshalling.ObjectMarshallingStrategy;

/**
 * ObjectMarshallingStrategy implementation for storing and retrieving data from CMIS compatible systems.
 * It will act only on variables that are of type <code>Document</code>
 * It allows to specify connection details to the repository
 * <ul>
 * 	<li>user</li>
 * 	<li>password</li>
 * 	<li>url</li>
 * 	<li>repository</li>
 * </ul>
 * and uses ATOM binding to connect and interact with CMIS. On top of that update mode can also be selected - override,
 *  new major version, new minor version on update. By default override mode is used if none is specified. 
 * <br/>
 * Interaction with CMIS can be done with one of the following ways:
 * <ul>
 * 	<li>create new document based on variable - to do so Document variable should have null objectId and must have content</li>
 * 	<li>update document based on variable - to do so Document variable must have objectId set and must have content</li>
 * 	<li>read document from CMIS - to do so Document variable must have only objectId set and other fields left empty</li>
 * </ul>
 * Every time a document is updated it might get new objectId and thus it will be set upon such operations.
 *
 * @see Document
 */
public class OpenCMISPlaceholderResolverStrategy extends OpenCMISSupport implements ObjectMarshallingStrategy {

	private String user;
	private String password;
	private String url;
	private String repository;
	private String contentUrl;
	private UpdateMode mode = UpdateMode.OVERRIDE;
	
	public OpenCMISPlaceholderResolverStrategy(String user, String password, String url, String repository) {
		this.user = user;
		this.password = password;
		this.url = url;
		this.repository = repository;
	}
	
	public OpenCMISPlaceholderResolverStrategy(String user, String password, String url, String repository, UpdateMode mode) {
		this.user = user;
		this.password = password;
		this.url = url;
		this.repository = repository;
		this.mode = mode;
	}
	
	   public OpenCMISPlaceholderResolverStrategy(String user, String password, String url, String repository, String contentUrl) {
	        this.user = user;
	        this.password = password;
	        this.url = url;
	        this.repository = repository;
	        this.contentUrl = contentUrl;
	    }
	    
	    public OpenCMISPlaceholderResolverStrategy(String user, String password, String url, String repository, String contentUrl, UpdateMode mode) {
	        this.user = user;
	        this.password = password;
	        this.url = url;
	        this.repository = repository;
	        this.contentUrl = contentUrl;
	        this.mode = mode;
	    }

	public boolean accept(Object object) {
		if (object instanceof Document) {
			return true;
		}
		return false;
	}

	public byte[] marshal(Context context, ObjectOutputStream os, Object object) throws IOException {
		Document document = (Document) object;
		Session session = getRepositorySession(user, password, url, repository);
		try {
			if (document.getContent() != null) {
				String type = getType(document);
				if (document.getIdentifier() == null || document.getIdentifier().isEmpty()) {
					String location = getLocation(document);
					
					Folder parent = findFolderForPath(session, location);
					if (parent == null) {
						parent = createFolder(session, null, location);
					}
					org.apache.chemistry.opencmis.client.api.Document doc = createDocument(session, parent, document.getName(), type, document.getContent());
					document.setIdentifier(doc.getId());
					document.addAttribute("updated", "true");
				} else {
					if (document.getContent() != null && "true".equals(document.getAttribute("updated"))) {
						org.apache.chemistry.opencmis.client.api.Document doc = updateDocument(session, document.getIdentifier(), type, document.getContent(), mode);
						
						document.setIdentifier(doc.getId());
						document.addAttribute("updated", "false");
					}
				}
			}
			ByteArrayOutputStream buff = new ByteArrayOutputStream();
	        ObjectOutputStream oos = new ObjectOutputStream( buff );
	        oos.writeUTF(document.getIdentifier());
	        oos.writeUTF(object.getClass().getCanonicalName());
	        oos.close();
	        return buff.toByteArray();
		} finally {
			session.clear();
		}
	}

	public Object unmarshal(Context context, ObjectInputStream ois, byte[] object, ClassLoader classloader) throws IOException, ClassNotFoundException {
		DroolsObjectInputStream is = new DroolsObjectInputStream( new ByteArrayInputStream( object ), classloader );
		String objectId = is.readUTF();
		String canonicalName = is.readUTF();
		Session session = getRepositorySession(user, password, url, repository);
		try {
			org.apache.chemistry.opencmis.client.api.Document doc = (org.apache.chemistry.opencmis.client.api.Document) findObjectForId(session, objectId);
			Document document = (Document) Class.forName(canonicalName).newInstance();
			document.setAttributes(new HashMap<String, String>());
			
			document.setIdentifier(objectId);
			document.setName(doc.getName());
			document.setLastModified(doc.getLastModificationDate().getTime());
			document.setSize(doc.getContentStreamLength());
			document.addAttribute("location", getFolderName(doc.getParents()) + getPathAsString(doc.getPaths()));
			if (doc.getContentStream() != null && contentUrl == null) {
				ContentStream stream = doc.getContentStream();
				document.setContent(IOUtils.toByteArray(stream.getStream()));				
				document.addAttribute("updated", "false");
				document.addAttribute("type", stream.getMimeType());				
			} else {
			    document.setLink(contentUrl + document.getIdentifier());
			}
			return document;
		} catch(Exception e) {
			throw new RuntimeException("Cannot read document from CMIS", e);
		} finally {
			is.close();
			session.clear();
		}
	}

	public Context createContext() {
		return null;
	}
	
	/*
	 * for backward compatibility with previous serialization mechanism - before protobuf was used
	 * (non-Javadoc)
	 * @see org.kie.api.marshalling.ObjectMarshallingStrategy#write(java.io.ObjectOutputStream, java.lang.Object)
	 * @see org.kie.api.marshalling.ObjectMarshallingStrategy#read(java.io.ObjectOutputStream)
	 */

	public void write(ObjectOutputStream os, Object object) throws IOException {
		Document document = (Document) object;
		Session session = getRepositorySession(user, password, url, repository);
		try {
			if (document.getContent() != null) {
				String type = document.getAttribute("type");
				if (document.getIdentifier() == null) {
					String location = document.getAttribute("location");
					
					Folder parent = findFolderForPath(session, location);
					if (parent == null) {
						parent = createFolder(session, null, location);
					}
					org.apache.chemistry.opencmis.client.api.Document doc = createDocument(session, parent, document.getName(), type, document.getContent());
					document.setIdentifier(doc.getId());
					document.addAttribute("updated", "false");
				} else {
					if (document.getContent() != null && "true".equals(document.getAttribute("updated"))) {
						org.apache.chemistry.opencmis.client.api.Document doc = updateDocument(session, document.getIdentifier(), type, document.getContent(), mode);
						
						document.setIdentifier(doc.getId());
						document.addAttribute("updated", "false");
					}
				}
			}
			ByteArrayOutputStream buff = new ByteArrayOutputStream();
	        ObjectOutputStream oos = new ObjectOutputStream( buff );
	        oos.writeUTF(document.getIdentifier());
	        oos.writeUTF(object.getClass().getCanonicalName());
	        oos.close();
		} finally {
			session.clear();
		}
	}

	public Object read(ObjectInputStream os) throws IOException, ClassNotFoundException {
		String objectId = os.readUTF();
		String canonicalName = os.readUTF();
		Session session = getRepositorySession(user, password, url, repository);
		try {
			org.apache.chemistry.opencmis.client.api.Document doc = (org.apache.chemistry.opencmis.client.api.Document) findObjectForId(session, objectId);
			Document document = (Document) Class.forName(canonicalName).newInstance();
			
			document.setIdentifier(objectId);
			document.setName(doc.getName());
			document.addAttribute("location", getFolderName(doc.getParents()) + getPathAsString(doc.getPaths()));
			if (doc.getContentStream() != null) {
				ContentStream stream = doc.getContentStream();
				document.setContent(IOUtils.toByteArray(stream.getStream()));
				document.addAttribute("updated", "false");
				document.addAttribute("type", stream.getMimeType());				
			}
			return document;
		} catch(Exception e) {
			throw new RuntimeException("Cannot read document from CMIS", e);
		} finally {			
			session.clear();
		}
	}

}
