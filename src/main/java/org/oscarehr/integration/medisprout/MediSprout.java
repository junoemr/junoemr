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

package org.oscarehr.integration.medisprout;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.oscarehr.common.dao.MediSproutAppointmentDao;
import org.oscarehr.common.dao.ProviderMediSproutDao;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.common.model.MediSproutAppointment;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;

import oscar.OscarProperties;
import oscar.dao.AppointmentDao;
import oscar.dms.EDoc;
import oscar.dms.EDocUtil;



public class MediSprout {

	private MediSproutAppointmentDao mediSproutAppointmentDao;
	private ProviderMediSproutDao providerMediSproutDao; 
	private AppointmentDao appointmentDao;
	
	private static final Logger Logger = MiscUtils.getLogger();
	
	public MediSprout() {
		mediSproutAppointmentDao = (MediSproutAppointmentDao) SpringUtils.getBean("mediSproutAppointmentDao");
		providerMediSproutDao = (ProviderMediSproutDao) SpringUtils.getBean("providerMediSproutDao");
		appointmentDao = (AppointmentDao) SpringUtils.getBean("appointmentSuperDao");
	}
	
	public MediSproutAppointment getAppointment(String appointment_no) {
		return mediSproutAppointmentDao.getAppointment(Integer.parseInt(appointment_no));
	}
	
	public MediSproutAppointment createAppointment(String provider_no, int appointment_no, String apptDate, String apptStartTime, Demographic demographic) throws Exception {
		
		OscarProperties props = OscarProperties.getInstance();
		
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(props.getProperty("medisprouturl") + "/api/v2/visit/schedule");
		
		MediSproutAppointment appt = new MediSproutAppointment();
		try {
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
			Date appointmentDate = format.parse(apptDate + " " + apptStartTime);
			
			TimeZone tz = Calendar.getInstance().getTimeZone();
			
			Calendar now = Calendar.getInstance();
			
			String email = demographic.getEmail();
			if (email == null || email.length() == 0) {
				email = props.getProperty("medisproutdefaultemail");
			}
			
			String backLinkUrl = props.getProperty("medisproutbacklinkurl");
			String backLinkName = props.getProperty("medisproutbacklinkname");
			String apiKey = providerMediSproutDao.getProviderMediSproutApiKey(provider_no);
			
			if (apiKey == null || apiKey.isEmpty()) {
				throw new Exception("Missing MediSprout apiKey.  Check provider record.");
			}
		
			HashMap<String, Object> jsonMap = new HashMap<String, Object>();
			jsonMap.put( "apiKey", apiKey );  
			jsonMap.put( "visitOn", appointmentDate.getTime() );  
			jsonMap.put( "requestOn", now.getTime().getTime() );  
			jsonMap.put( "timezone", tz.getID() );
			jsonMap.put( "name", demographic.getFormattedName() );
			jsonMap.put( "email", email );
			jsonMap.put( "additionalInfo", "" );
			jsonMap.put( "phone", demographic.getPhone() );
			jsonMap.put( "backLinkUrl", backLinkUrl );
			jsonMap.put( "backLinkName", backLinkName );
			
			JSONObject json = (JSONObject) JSONSerializer.toJSON( jsonMap );
			
			Logger.debug(json.toString());
			
			StringEntity input;		
       
	      input = new StringEntity( json.toString() );
      
		  input.setContentType("application/json");

		  post.setEntity(input);
		  HttpResponse response = client.execute(post);
		  BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		  String output = "";
		  String line = "";
		  while ((line = rd.readLine()) != null) {
			  output += line + "\n";
		  }
		  
		  JSONObject jsonObject = JSONObject.fromObject(output);
		  @SuppressWarnings("unchecked")
          Map<String, Object> outputMap = jsonObject;
		  
		  Logger.debug(outputMap);
		  
		  if ((Boolean) outputMap.get("success")) {
			  appt.setAppointment_no(appointment_no);
			  appt.setProviderUrl((String) outputMap.get("providerUrl"));
			  appt.setAttendeesUrl((String) outputMap.get("attendeesUrl"));
			  if (outputMap.get("code") != null) {
				  appt.setCode(Integer.parseInt(((String) outputMap.get("code"))));
			  }
			  appt.setDowloadeddocs(0);
			  
			  mediSproutAppointmentDao.persist(appt);
		  }
		} catch (ParseException pe) {
			Logger.error("Json Parsing error", pe);
			throw new Exception("Json Parsing error.  See log files.");
        } catch (UnsupportedEncodingException uee) {
        	Logger.error("Json Encoding error.", uee);
	        throw new Exception("Json Encoding error.  See log files.");
        } catch (ClientProtocolException cpe) {
        	Logger.error("Protocol Error access MediSprout", cpe);
        	throw new Exception("Protocol Error accessing MediSprout.  See log files.");
        } catch (IOException ioe) {
        	Logger.error("IO Error accessing MediSprout", ioe);
        	throw new Exception("IO Error accessing MediSprout.  See log files.");
        }
        
        return appt;  
	}
	
	public boolean deleteAppointment(int appointment_no, String provider_no) throws Exception {
		boolean result = false;
		
		MediSproutAppointment appt = mediSproutAppointmentDao.getAppointment(appointment_no);
		
		if (appt == null || appt.getCode() == null) {
			Logger.error("Unable to find appointment for: " + appointment_no);
			return result;
		}
		
		OscarProperties props = OscarProperties.getInstance();
		
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(props.getProperty("medisprouturl") + "/api/v2/visit/delete");
		
		try {
			Calendar now = Calendar.getInstance();
			
			String apiKey = providerMediSproutDao.getProviderMediSproutApiKey(provider_no);
			
			if (apiKey == null || apiKey.isEmpty()) {
				throw new Exception("Missing MediSprout apiKey.  Check provider record.");
			}
		
			HashMap<String, Object> jsonMap = new HashMap<String, Object>();
			jsonMap.put( "apiKey", apiKey );  
			jsonMap.put( "requestOn", now.getTime().getTime() );  
			jsonMap.put( "code", String.format("%09d", appt.getCode()) );
		
			JSONObject json = (JSONObject) JSONSerializer.toJSON( jsonMap );
			
			Logger.debug(json.toString());
			
			StringEntity input;		
       
	      input = new StringEntity( json.toString() );
      
		  input.setContentType("application/json");

		  post.setEntity(input);
		  HttpResponse response = client.execute(post);
		  BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		  String output = "";
		  String line = "";
		  while ((line = rd.readLine()) != null) {
			  output += line + "\n";
		  }
		  
		  JSONObject jsonObject = JSONObject.fromObject(output);
		  @SuppressWarnings("unchecked")
          Map<String, Object> outputMap = jsonObject;
		  
		  Logger.info(outputMap);
		  
//		  result = (Boolean) outputMap.get("success");
		  
		  if (outputMap.get("error") != null) {
			  Logger.error("Error from delete api call: " + outputMap.get("error"));
		  } else {
			  result = true;
		  }
		  
		  if (result) {
			  mediSproutAppointmentDao.merge(appt);
		  }
		  
		} catch (ParseException pe) {
			Logger.error("Json Parsing error", pe);
			throw new Exception("Json Parsing error.  See log files.");
        } catch (UnsupportedEncodingException uee) {
        	Logger.error("Json Encoding error.", uee);
	        throw new Exception("Json Encoding error.  See log files.");
        } catch (ClientProtocolException cpe) {
        	Logger.error("Protocol Error access MediSprout", cpe);
        	throw new Exception("Protocol Error accessing MediSprout.  See log files.");
        } catch (IOException ioe) {
        	Logger.error("IO Error accessing MediSprout", ioe);
        	throw new Exception("IO Error accessing MediSprout.  See log files.");
        }
        
        return result;  
	}
	
	public void downloadDocs() {
		List<MediSproutAppointment> appts = mediSproutAppointmentDao.getAppointmentsToDownloadDocs();
		
		for (MediSproutAppointment appt : appts) {
			try {
				 List<Map<String, Object>> apptList  = appointmentDao.executeSelectQuery("search", new Object[]{appt.getAppointment_no()});
				 
				 if (apptList.size() == 1) {
					 Map<String, Object> mAppt = apptList.get(0); 
					 String providerNo = (String)mAppt.get("provider_no");
					 String demographicNo = ((Integer)mAppt.get("demographic_no")).toString();
					 JSONObject jsonObj = getDocuments(providerNo, appt.getCode());
					 
					 if (jsonObj.getBoolean("success")) {
						 Logger.debug("endedOn: " + jsonObj.getLong("endedOn"));
					 }
					 
					 if (jsonObj.getBoolean("success") && jsonObj.getLong("endedOn") > 0) {
						 if (jsonObj.get("records") instanceof JSONArray) {
							 JSONArray records = jsonObj.getJSONArray("records");
							 
							 if (records != null) {
								for (int i=0; i < records.size(); i++) {
									JSONObject record = records.getJSONObject(i);
									Logger.debug(record);
									
									if (record.get("downloadUrl") != null && ! record.getString("downloadUrl").equals("null") && 
											record.get("name") != null && ! record.getString("name").equals("null")) {
										String filename = writeFile(record.getString("downloadUrl"), record.getString("name"));
										
										EDoc eDoc = new EDoc();
										eDoc.setFileName(filename);
										eDoc.setCreatorId(providerNo);
										eDoc.setResponsibleId(providerNo);
										eDoc.setDescription(filename);
										eDoc.setObservationDate(new Date(record.getLong("createdOn")));
										eDoc.setModule("demographic");
										eDoc.setModuleId(demographicNo);
										eDoc.setStatus('A');
										
										EDocUtil.addDocumentSQL(eDoc);
										
									}
								}
							 }
							appt.setDowloadeddocs(1);
						 }
						 
						 mediSproutAppointmentDao.merge(appt);	
					 } else {
						 Logger.warn("Unable to download documents for: " + appt.getAppointment_no());
					 }			  
				 } else {
					 Logger.warn("Unable to find appointment for: " + appt.getAppointment_no());
				 }
			} catch (Exception e) {
				Logger.warn("Unable to download for appt: " + appt.getAppointment_no(), e);
			}
		}
	}
	
	public JSONObject getDocuments(String provider_no, Integer code) throws Exception {
		OscarProperties props = OscarProperties.getInstance();
		
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(props.getProperty("medisprouturl") + "/api/v2/visit/record");
		
		JSONObject jsonObject = null;
		
		try {
			TimeZone tz = Calendar.getInstance().getTimeZone();
			
			Calendar now = Calendar.getInstance();
			
			String apiKey = providerMediSproutDao.getProviderMediSproutApiKey(provider_no);
			
			if (apiKey == null || apiKey.isEmpty()) {
				throw new Exception("Missing MediSprout apiKey.  Check provider record.");
			}
		
			HashMap<String, Object> jsonMap = new HashMap<String, Object>();
			jsonMap.put( "apiKey", apiKey );  
			jsonMap.put( "requestOn", now.getTime().getTime() );  
			jsonMap.put( "timezone", tz.getID() );
			jsonMap.put( "code", code.toString() );
	
			JSONObject json = (JSONObject) JSONSerializer.toJSON( jsonMap );
			
			Logger.debug(json.toString());
			
			StringEntity input;		
       
	      input = new StringEntity( json.toString() );
      
		  input.setContentType("application/json");

		  post.setEntity(input);
		  HttpResponse response = client.execute(post);
		  BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		  String output = "";
		  String line = "";
		  while ((line = rd.readLine()) != null) {
			  output += line + "\n";
		  }
		  
		  jsonObject = JSONObject.fromObject(output);
		 
		  Logger.debug(jsonObject);
		  
		 
		} catch (ParseException pe) {
			Logger.error("Json Parsing error", pe);
			throw new Exception("Json Parsing error.  See log files.");
        } catch (UnsupportedEncodingException uee) {
        	Logger.error("Json Encoding error.", uee);
	        throw new Exception("Json Encoding error.  See log files.");
        } catch (ClientProtocolException cpe) {
        	Logger.error("Protocol Error access MediSprout", cpe);
        	throw new Exception("Protocol Error accessing MediSprout.  See log files.");
        } catch (IOException ioe) {
        	Logger.error("IO Error accessing MediSprout", ioe);
        	throw new Exception("IO Error accessing MediSprout.  See log files.");
        }
        
        return jsonObject; 
	}
	
	private String writeFile(String url, String filename) {
		
		OscarProperties props = OscarProperties.getInstance();

		String file = null;
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(props.getProperty("medisprouturl") + url);
			HttpResponse response = client.execute(get);
            HttpEntity entity = response.getEntity();
            
            filename = "LC-" + filename;
            
			file = OscarProperties.getInstance().getProperty("DOCUMENT_DIR","./") + "/" + filename; 
	        
	        BufferedInputStream bis = new BufferedInputStream(entity.getContent());
	       
	        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(file)));
	        int inByte;
	        while((inByte = bis.read()) != -1) 
	        	bos.write(inByte);
	        bis.close();
	        bos.close();
	        
	        Logger.debug("Written file: " + file);
	        
	       
	    } catch (IOException e) {
	       Logger.error("Error saving file: ", e);
	    }
	    
	    return filename;
	}

}
