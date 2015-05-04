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
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.MimetypesFileTypeMap;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.DocumentType;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisBaseException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisContentAlreadyExistsException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.jbpm.integration.cmis.UpdateMode;

/**
 * Supporting class that provides helper method to interact with CMIS.
 *
 */
public abstract class OpenCMISSupport {

	/**
	 * Creates new session to CMIS based on given connection details using ATOM binding
	 * @param user
	 * @param password
	 * @param url
	 * @param repository
	 * @return
	 */
	protected Session getRepositorySession(String user, String password, String url, String repository) {
	
		SessionFactory factory = SessionFactoryImpl.newInstance();
		Map<String, String> parameter = new HashMap<String, String>();

		// user credentials
		parameter.put(SessionParameter.USER, user);
		parameter.put(SessionParameter.PASSWORD, password);

		// connection settings
		parameter.put(SessionParameter.ATOMPUB_URL, url);
		parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
		parameter.put(SessionParameter.REPOSITORY_ID, repository);

		// create session
		Session session = factory.createSession(parameter);
		
		return session;
	}
	
	/**
	 * Finds folder by path and if not found returns null.
	 * @param session
	 * @param path
	 * @return
	 */
	protected Folder findFolderForPath(Session session, String path) {
		try {
		return (Folder) session.getObjectByPath(path);
		} catch (CmisObjectNotFoundException e) {
			return null;
		}
	}

	/**
	 * Finds Object (document or folder) by unique id. If not could exceptions is thrown
	 * @param session
	 * @param id
	 * @return
	 * @throws CmisObjectNotFoundException
	 */
	protected CmisObject findObjectForId(Session session, String id) {
		return  session.getObject(id);
	}
	
	/**
	 * Produces String representation of path elements
	 * @param paths
	 * @return
	 */
	protected String getPathAsString(List<String> paths) {
		if (paths == null || paths.isEmpty()) {
			return "/";
		}
		StringBuffer buffer = new StringBuffer();
		for (String path : paths) {
			buffer.append("/" + path);
		}
		
		return buffer.toString();
	}
	
	/**
	 * Returns last folder name from the path, if no available {root}
	 * is returned.
	 * @param parents
	 * @return
	 */
	protected String getFolderName(List<Folder> parents) {
		if (parents == null || parents.isEmpty()) {
			return "{root}";
		}
		
		return parents.get(parents.size() - 1).getName();
	}

	/**
	 * Creates folder under given parent, if parent is null the root folder is used as parent.
	 * in case of nested folders entire path will be created.
	 * @param session
	 * @param parent
	 * @param foldername
	 * @return
	 */
	protected Folder createFolder(Session session, Folder parent, String foldername) {
		Folder root = parent;
		if (root == null) {
			root = session.getRootFolder();
		}
		Folder newFolder = null;
		if (foldername.startsWith("/")) {
			foldername = foldername.substring(1);
		}
		StringBuffer currentPath = new StringBuffer();
		String[] elems = foldername.split("/");
		for (String folder : elems) {
			// properties
			// (minimal set: name and object type id)
			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
			properties.put(PropertyIds.NAME, folder);
			currentPath.append("/" + folder);
			try {
				// create the folder
				newFolder = root.createFolder(properties);
				root = newFolder;
			} catch (CmisContentAlreadyExistsException e) {
				root = (Folder) session.getObjectByPath(currentPath.toString());
			}
		}
		return newFolder;
	}
	
	/**
	 * Creates document with given name, type and content inside the parent folder. If parent is null then
	 * root folder is used.
	 * @param session
	 * @param parent
	 * @param name
	 * @param type
	 * @param content
	 * @return
	 */
	protected Document createDocument(Session session, Folder parent, String name, String type, byte[] content) {
		Folder root = parent;
		if (root == null) {
			root = session.getRootFolder();
		}
		// properties 
		// (minimal set: name and object type id)
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
		properties.put(PropertyIds.NAME, name);
		// set default type if none is given
		if (type == null) {
			type = "text/plain";
		}
		// content	
		try {
			InputStream input = new ByteArrayInputStream(content);
			ContentStream contentStream = new ContentStreamImpl(name, null, type, input);
	
			// create a major version
			Document newDoc = root.createDocument(properties, contentStream, VersioningState.MAJOR);
			input.close();
			
			return newDoc;
		
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Updates document identified with objectId and type with given content accoring to the update mode.
	 * @param session
	 * @param objectId
	 * @param type
	 * @param content
	 * @param mode
	 * @return
	 */
	protected Document updateDocument(Session session, String objectId, String type, byte[] content, UpdateMode mode) {
		Document document = (Document) session.getObject(objectId);
		InputStream input = new ByteArrayInputStream(content);
		ContentStream contentStream = session.getObjectFactory().createContentStream(document.getName(), content.length, type, input);
		try {
			if (((DocumentType)(document.getType())).isVersionable()
					&& (mode.equals(UpdateMode.MAJOR) || mode.equals(UpdateMode.MINOR))) {
				Document pwc = (Document) session.getObject(document.checkOut());
				boolean major = mode.equals(UpdateMode.MAJOR);
				try {
			        ObjectId newObjectId = pwc.checkIn(major, null, contentStream, "Document update from process");
			        return (Document) session.getObject(newObjectId);
			    } catch (CmisBaseException e) {			        
			        pwc.cancelCheckOut();
			        throw new RuntimeException("Cannot store document in CMIS reposiory", e);
			    }
			} else if (mode.equals(UpdateMode.OVERRIDE)) {
			
			   document.setContentStream(contentStream, true);
				
			}
		} finally {
			try {		
				input.close();
			} catch (IOException e) {
				// log it only
			}
		}
		
		return document;
	}
	
	/**
	 * Deletes document identified by objectId.
	 * @param session
	 * @param objectId
	 */
	protected void deleteDocument(Session session, String objectId) {
		Document document = (Document) session.getObject(objectId);
		document.delete(false);
	}
	
	protected String getType(org.jbpm.document.Document document) {
	    String type = document.getAttribute("type");
	    if (type == null || type.isEmpty()) {
	        type = MimetypesFileTypeMap.getDefaultFileTypeMap().getContentType(document.getName());
	    }
	    
	    return type;
	}
	
	protected String getLocation(org.jbpm.document.Document document) {
	    String location = document.getAttribute("location");
        if (location == null || location.isEmpty()) {
            location = "/";
        }
        
        return location;
    }
	
}
