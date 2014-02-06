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

/**
 * Document interface that represents process variable that shall be stored in Document/Content
 * management systems through CMIS.<br/>
 * It provides very simple capabilities that should cover basic use cases on documents:
 * <ul>
 * 	<li>fetch</li>
 * 	<li>create</li>
 * 	<li>update</li>
 * 	<li>delete</li>
 * </ul> 
 */
public interface Document {

	/**
	 * Returns unique identifier of the document in the CMIS system.
	 * Whenever operations are performed on the document this id might be changed
	 * e.g. when new version of the document is created by the update operation
	 * id of the latest version will be set.
	 * @return
	 */
	String getObjectId();
	
	/**
	 * Sets the unique id of the document.
	 * @param objectId
	 */
	void setObjectId(String objectId);
	
	/**
	 * Returns the folder name of the direct parent folder, in case of root folder it will be 
	 * special string {root}.
	 * @return
	 */
	String getFolderName();
	
	/**
	 * Sets folder name of the direct parent of the document.
	 * @param value
	 */
	void setFolderName(String value);
	
	/**
	 * Returns complete (from root) path to this document.
	 * @return
	 */
	String getFolderPath();
	
	/**
	 * Sets folder path to this document.
	 * @param value
	 */
	void setFolderPath(String value);
	
	/**
	 * Returns name of the document, usually file name
	 * @return
	 */
	String getDocumentName();
	
	/**
	 * Sets document file name.
	 * @param value
	 */
	void setDocumentName(String value);
	
	/**
	 * Returns document type - mime type of the document content
	 * @return
	 */
	String getDocumentType();
	
	/**
	 * Sets document type - mime type of the document content.
	 * @param value
	 */
	void setDocumentType(String value);
	
	/**
	 * Returns complete content of this document.
	 * @return
	 */
	byte[] getDocumentContent();
	
	/**
	 * Sets content of this document.
	 * @param value
	 */
	void setDocumentContent(byte[] value);
	
	/**
	 * Indicates if the document has been updated and shall be stored in CMIS.  
	 * @return
	 */
	boolean isUpdated();
	 
	/**
	 * Sets document as updated thus causing it to be stored in CMS. It is
	 * automatically set when document content is set but can be enforced manually by
	 * this flag.
	 * @param updated
	 */
	void setUpdated(boolean updated);
}
