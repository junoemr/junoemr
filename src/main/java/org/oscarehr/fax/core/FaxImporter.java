/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */
package org.oscarehr.fax.core;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowagie.text.pdf.codec.Base64;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.oscarehr.common.dao.FaxConfigDao;
import org.oscarehr.common.dao.FaxJobDao;
import org.oscarehr.common.dao.QueueDocumentLinkDao;
import org.oscarehr.common.model.FaxConfig;
import org.oscarehr.common.model.FaxJob;
import org.oscarehr.document.model.Document;
import org.oscarehr.document.service.DocumentService;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import oscar.OscarProperties;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class FaxImporter {
	
	private static String PATH = "/fax";	
	private static String DOCUMENT_DIR = OscarProperties.getInstance().getProperty("DOCUMENT_DIR");
	
	private FaxConfigDao faxConfigDao = SpringUtils.getBean(FaxConfigDao.class);
	private FaxJobDao faxJobDao = SpringUtils.getBean(FaxJobDao.class);
	private QueueDocumentLinkDao queueDocumentLinkDao = SpringUtils.getBean(QueueDocumentLinkDao.class);
	private Logger logger = MiscUtils.getLogger();
	private DocumentService documentService = SpringUtils.getBean(DocumentService.class);


	public void poll() {
		
		List<FaxConfig> faxConfigList = faxConfigDao.findAll(null,null);
		DefaultHttpClient client = new DefaultHttpClient();
		
		for( FaxConfig faxConfig : faxConfigList ) {
			if( faxConfig.isActive() ) {
								
				client.getCredentialsProvider().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(faxConfig.getSiteUser(), faxConfig.getPasswd()));
	
				HttpGet mGet = new HttpGet(faxConfig.getUrl() + PATH + "/" + faxConfig.getFaxUser());
				mGet.setHeader("accept", "application/json");
				mGet.setHeader("user", faxConfig.getFaxUser());
				mGet.setHeader("passwd", faxConfig.getFaxPasswd());
				
				try {
	                HttpResponse response = client.execute(mGet);
	                logger.info("RESPONSE: " + response.getStatusLine().getStatusCode());
	                
	                if( response.getStatusLine().getStatusCode() == HttpStatus.SC_OK ) {
	                	
	                	HttpEntity httpEntity = response.getEntity();
	                	String content = EntityUtils.toString(httpEntity);
	                
	                	
	                	logger.info("CONTENT: " + content);
	                	ObjectMapper mapper = new ObjectMapper();
	                	
	                	List<FaxJob> faxList =  mapper.readValue(content, new TypeReference<List<FaxJob>>(){});
	                	
	                	FaxJob faxFile;
	                	for( FaxJob receivedFax : faxList ) {
	                		if( (faxFile = downloadFax( client, faxConfig, receivedFax )) != null ) {
	                			if( saveAndInsertIntoQueue( faxConfig, receivedFax, faxFile ) ) {
	                				deleteFax( client, faxConfig, receivedFax );
	                			}
	                		}
	                	}
	                	
	                }
	                
	                mGet.releaseConnection();
	            } catch (ClientProtocolException e) {
	            	logger.error("HTTP WS CLIENT ERROR", e);
	            
	            } catch (IOException e) {
	            	logger.error("IO ERROR", e);
	            	
	            } catch( Exception e ) {
	            	logger.error("UNKNOWN ERROR ",e);
	            }				
				finally {
					mGet.releaseConnection();
				}
				
			
			}
		}
		
	}
		
	private FaxJob downloadFax( DefaultHttpClient client, FaxConfig faxConfig, FaxJob fax ) {
		
		HttpGet mGet = new HttpGet(faxConfig.getUrl() + PATH + "/" + faxConfig.getFaxUser() + "/" + fax.getFile_name());
		mGet.setHeader("accept", "application/json");
		mGet.setHeader("user", faxConfig.getFaxUser());
		mGet.setHeader("passwd", faxConfig.getFaxPasswd());
		
		try {
		
			HttpResponse response = client.execute(mGet);
        
			if( response.getStatusLine().getStatusCode() == HttpStatus.SC_OK ) {
        	
				HttpEntity httpEntity = response.getEntity();
				String content = EntityUtils.toString(httpEntity);
				
				ObjectMapper mapper = new ObjectMapper();
				
				FaxJob downloadedFax = mapper.readValue(content, FaxJob.class); 
        		
				return downloadedFax;
			}
			
	      } catch (ClientProtocolException e) {
          	logger.error("HTTP WS CLIENT ERROR", e);
          
          } catch (IOException e) {
          	logger.error("IO ERROR", e);
          }
		  finally {
			  mGet.releaseConnection();
		  }
		

		  return null;
	}
	
	private void deleteFax( DefaultHttpClient client, FaxConfig faxConfig, FaxJob fax ) throws ClientProtocolException, IOException {
		HttpDelete mDelete = new HttpDelete(faxConfig.getUrl() + PATH + "/" + faxConfig.getFaxUser() + "/" + fax.getFile_name());
		mDelete.setHeader("accept", "application/json");
		mDelete.setHeader("user", faxConfig.getFaxUser());
		mDelete.setHeader("passwd", faxConfig.getFaxPasswd());
		
		HttpResponse response = client.execute(mDelete);
		mDelete.releaseConnection();
	       
		if( !(response.getStatusLine().getStatusCode() == HttpStatus.SC_NO_CONTENT) ) {
			throw new ClientProtocolException("CANNOT DELETE " + fax.getFile_name());
		}
		
		
	}

	private boolean saveAndInsertIntoQueue(FaxConfig faxConfig, FaxJob receivedFax, FaxJob faxFile)
	{
		boolean retval = false;

		String fileName = receivedFax.getFile_name().replace("tif", "pdf");
		String user = "-1";

		Document document = new Document();
		document.setPublic1(false);
		document.setResponsible(user);
		document.setDoccreator(user);
		document.setDocdesc("");
		document.setDoctype("");
		document.setDocfilename(fileName);
		document.setSource("");
		document.setObservationdate(receivedFax.getStamp());

		byte[] fileByteArray = Base64.decode(faxFile.getDocument());
		InputStream fileInputStream = new ByteArrayInputStream(fileByteArray);

		try
		{
			document = documentService.uploadNewDemographicDocument(document, fileInputStream);

			Integer queueId = faxConfig.getQueue();
			queueDocumentLinkDao.addActiveQueueDocumentLink(queueId, document.getDocumentNo());

			FaxJob saveFax = new FaxJob(receivedFax);
			saveFax.setFile_name(fileName);
			faxJobDao.persist(saveFax);
			retval = true;
		}
		catch(IOException e)
		{
			logger.error("IO Error", e);
		}
		return retval;
	}
}
