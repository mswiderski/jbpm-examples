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

import java.io.Serializable;

import org.jbpm.integration.cmis.Document;

/**
 * Base implementation of the <code>Document</code>. It's simple
 * data holder.
 *
 */
public class DocumentImpl implements Document, Serializable {

	private static final long serialVersionUID = -3613462788706984137L;
	
	private String objectId;
	private String folderName;
	private String folderPath;
	private String documentName;
	private String documentType;
	private byte[] documentContent;
	
	private boolean updated = false;
	
	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	public String getFolderName() {
		return folderName;
	}
	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}
	public String getFolderPath() {
		return folderPath;
	}
	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}
	public String getDocumentName() {
		return documentName;
	}
	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}
	public String getDocumentType() {
		return documentType;
	}
	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}
	public byte[] getDocumentContent() {
		return documentContent;
	}
	public void setDocumentContent(byte[] documentContent) {
		this.documentContent = documentContent;
		this.updated = true;
	}
	public boolean isUpdated() {
		return updated;
	}
	public void setUpdated(boolean updated) {
		this.updated = updated;
	}

}
