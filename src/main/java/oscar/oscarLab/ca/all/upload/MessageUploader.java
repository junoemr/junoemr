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


/*
 * MessageUploader.java
 *
 * Created on June 18, 2007, 1:53 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package oscar.oscarLab.ca.all.upload;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.oscarehr.PMmodule.dao.ProviderDao;
import org.oscarehr.common.OtherIdManager;
import org.oscarehr.common.dao.DemographicDao;
import org.oscarehr.common.dao.Hl7TextInfoDao;
import org.oscarehr.common.dao.Hl7TextMessageDao;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.common.model.Hl7TextInfo;
import org.oscarehr.common.model.Hl7TextMessage;
import org.oscarehr.common.model.OtherId;
import org.oscarehr.common.model.Provider;
import org.oscarehr.olis.dao.OLISSystemPreferencesDao;
import org.oscarehr.olis.model.OLISSystemPreferences;
import org.oscarehr.util.DbConnectionFilter;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;

import oscar.OscarProperties;
import oscar.oscarLab.ca.all.Hl7textResultsData;
import oscar.oscarLab.ca.all.parsers.Factory;
import oscar.oscarLab.ca.all.parsers.HHSEmrDownloadHandler;
import oscar.oscarLab.ca.all.parsers.MessageHandler;
import oscar.oscarLab.ca.all.parsers.PATHL7Handler;
import oscar.oscarLab.ca.all.parsers.SpireHandler;
import oscar.util.UtilDateUtilities;

public final class MessageUploader {

	private static final Logger logger = MiscUtils.getLogger();

	private MessageUploader() {
		// there's no reason to instantiate a class with no fields.
	}

	public static String routeReport(String serviceName, String type, String hl7Body, int fileId) throws Exception {
		return routeReport(serviceName, type, hl7Body, fileId, null);
	}

	/**
	 * Insert the lab into the proper tables of the database
	 */
	public static String routeReport(String serviceName, String type, String hl7Body, int fileId, RouteReportResults results) throws Exception {

		Hl7TextInfoDao hl7TextInfoDao = (Hl7TextInfoDao) SpringUtils.getBean("hl7TextInfoDao");
		Hl7TextMessageDao hl7TextMessageDao = (Hl7TextMessageDao) SpringUtils.getBean("hl7TextMessageDao");

		String retVal = "";
		try {
			MessageHandler h = Factory.getHandler(type, hl7Body);
			Base64 base64 = new Base64(0);

			String firstName = h.getFirstName();
			String lastName = h.getLastName();
			String dob = h.getDOB();
			String sex = h.getSex();
			String hin = h.getHealthNum();
			String resultStatus = "";
			String priority = h.getMsgPriority();
			String requestingClient = h.getDocName();
			String requestingClientNo = h.getClientRef();
			String reportStatus = h.getOrderStatus();
			String accessionNum = h.getAccessionNum();
			String fillerOrderNum = h.getFillerOrderNumber();
			String sendingFacility = h.getPatientLocation();
			ArrayList docNums = h.getDocNums();
			int finalResultCount = h.getOBXFinalResultCount();
			String obrDate = h.getMsgDate();

			if(h instanceof HHSEmrDownloadHandler) {
            	String chartNo = ((HHSEmrDownloadHandler)h).getPatientIdByType("MR");
            	if(chartNo != null) {
            		//let's get the hin
            		DemographicDao demographicDao = (DemographicDao)SpringUtils.getBean("demographicDao");
            		List<Demographic> clients = demographicDao.getClientsByChartNo(chartNo);
            		if(clients!=null && clients.size()>0) {
            			hin = clients.get(0).getHin();
            		}
            	}
            }
            
            // get actual ohip numbers based on doctor first and last name for spire lab
            if(h instanceof SpireHandler) {
				List<String> docNames = ((SpireHandler)h).getDocNames();
				//logger.debug("docNames:");
	            for (int i=0; i < docNames.size(); i++) {
					logger.info(i + " " + docNames.get(i));
				}
            	if (docNames != null) {
					docNums = findProvidersForSpireLab(docNames);
				}
            }
            //logger.debug("docNums:");
            for (int i=0; i < docNums.size(); i++) {
				//logger.debug(i + " " + docNums.get(i));
			}

			try {
				// reformat date
				String format = "yyyy-MM-dd HH:mm:ss".substring(0, obrDate.length() - 1);
				obrDate = UtilDateUtilities.DateToString(UtilDateUtilities.StringToDate(obrDate, format), "yyyy-MM-dd HH:mm:ss");
			} catch (Exception e) {
				obrDate = "";
				logger.error("Error of parsing message date : ", e);
			}

			int i = 0;
			int j = 0;
			while (resultStatus.equals("") && i < h.getOBRCount()) {
				j = 0;
				while (resultStatus.equals("") && j < h.getOBXCount(i)) {
					if (h.isOBXAbnormal(i, j)) resultStatus = "A";
					j++;
				}
				i++;
			}

			ArrayList<String> disciplineArray = h.getHeaders();
			String next = "";
			if (disciplineArray != null && disciplineArray.size() > 0) next = disciplineArray.get(0);

			int sepMark;
			if ((sepMark = next.indexOf("<br />")) < 0) {
				if ((sepMark = next.indexOf(" ")) < 0) sepMark = next.length();
			}
			String discipline = next.substring(0, sepMark).trim();

			for (i = 1; i < disciplineArray.size(); i++) {

				next = disciplineArray.get(i);
				if ((sepMark = next.indexOf("<br />")) < 0) {
					if ((sepMark = next.indexOf(" ")) < 0) sepMark = next.length();
				}

				if (!next.trim().equals("")) discipline = discipline + "/" + next.substring(0, sepMark);
			}

			boolean isTDIS = type.equals("TDIS");
			boolean hasBeenUpdated = false;
			Hl7TextMessage hl7TextMessage = new Hl7TextMessage();
			Hl7TextInfo hl7TextInfo = new Hl7TextInfo();


			if (isTDIS) {
				List<Hl7TextInfo> matchingTdisLab =  hl7TextInfoDao.searchByFillerOrderNumber(fillerOrderNum, sendingFacility);
				if (matchingTdisLab.size()>0) {

					hl7TextMessageDao.updateIfFillerOrderNumberMatches(MiscUtils.encodeToBase64String(hl7Body),fileId,matchingTdisLab.get(0).getLabNumber());

					hl7TextInfoDao.updateReportStatusByLabId(reportStatus,matchingTdisLab.get(0).getLabNumber());
					hasBeenUpdated = true;
				}
			}
			int insertID = 0;
			if (!isTDIS || !hasBeenUpdated) {
				hl7TextMessage.setFileUploadCheckId(fileId);
				hl7TextMessage.setType(type);
				hl7TextMessage.setBase64EncodedeMessage(MiscUtils.encodeToBase64String(hl7Body));
				hl7TextMessage.setServiceName(serviceName);
				hl7TextMessageDao.persist(hl7TextMessage);

				insertID = hl7TextMessage.getId();
				hl7TextInfo.setLabNumber(insertID);
				hl7TextInfo.setLastName(lastName);
				hl7TextInfo.setFirstName(firstName);
				hl7TextInfo.setSex(sex);
				hl7TextInfo.setHealthNumber(hin);
				hl7TextInfo.setResultStatus(resultStatus);
				hl7TextInfo.setFinalResultCount(finalResultCount);
				hl7TextInfo.setObrDate(obrDate);
				hl7TextInfo.setPriority(priority);
				hl7TextInfo.setRequestingProvider(requestingClient);
				hl7TextInfo.setRequestingProviderNo(requestingClientNo);
				hl7TextInfo.setDiscipline(discipline);
				hl7TextInfo.setReportStatus(reportStatus);
				hl7TextInfo.setAccessionNumber(accessionNum);
				if("CLS".equals(type)) {
					hl7TextInfo.setFillerOrderNum(fillerOrderNum);
				}
				hl7TextInfoDao.persist(hl7TextInfo);
			}

			boolean custom_route_enabled=false;
			String demProviderNo = patientRouteReport(insertID, lastName, firstName, sex, dob, hin, DbConnectionFilter.getThreadLocalDbConnection());
			if(type.equals("OLIS_HL7") && demProviderNo.equals("0")) {
				OLISSystemPreferencesDao olisPrefDao = (OLISSystemPreferencesDao)SpringUtils.getBean("OLISSystemPreferencesDao");
			    OLISSystemPreferences olisPreferences =  olisPrefDao.getPreferences();
			    if(olisPreferences.isFilterPatients()) {
			    	//set as unclaimed
			    	providerRouteReport(String.valueOf(insertID), null, DbConnectionFilter.getThreadLocalDbConnection(), String.valueOf(0), type);
			    } else {
			    	providerRouteReport(String.valueOf(insertID), docNums, DbConnectionFilter.getThreadLocalDbConnection(), demProviderNo, type);
			    }
			} else {
				Integer limit = null;
				String custom_lab_route="";
				boolean orderByLength = false;
				String search = null;
				if (type.equals("Spire")) {
					limit = new Integer(1);
					orderByLength = true;
					search = "provider_no";
				} else  if (type.equals("CLS")) {
					search = "hso_no";
				}
				else if (type.equals("PATHL7")){
					//custom lab routing for excelleris labs
					//Parses custom_lab_routeX properties (starting at 1)
					//Property format: custom_lab_routeX=<excelleris_lab_account>,<provider_no>
					custom_lab_route= OscarProperties.getInstance().getProperty("custom_lab_route1");	
				
				
					String account;
					String lab_user;
					PreparedStatement pstmt;
					ResultSet rs;
					
					PATHL7Handler handler = new PATHL7Handler();
					handler.init(hl7Body);
					
					int k = 1;					
					while(custom_lab_route!=null&&!custom_lab_route.equals("")){
						custom_route_enabled=true;
						
						ArrayList<String> cust_route = new ArrayList<String>(Arrays.asList(custom_lab_route.split(",")));
						account = cust_route.get(0);
						ArrayList<String> to_provider = new ArrayList<String>(Arrays.asList(cust_route.get(1)));
						
						lab_user = handler.getLabUser();
						if(lab_user.equals(account)) {
							providerRouteReport(String.valueOf(insertID), to_provider, DbConnectionFilter.getThreadLocalDbConnection(), demProviderNo, type, "provider_no", limit, orderByLength);
						}
						
						k++;
						custom_lab_route= OscarProperties.getInstance().getProperty("custom_lab_route"+k);
					}
				}
				
				if(!custom_route_enabled) {
					String route_labs_to_provider = OscarProperties.getInstance().getProperty("route_labs_to_provider", "");
					
					if(route_labs_to_provider.equals("0")){  
						// Send to the unclaimed inbox
						providerRouteReport(String.valueOf(insertID), null, DbConnectionFilter.getThreadLocalDbConnection(), String.valueOf(0), type);
						
					} else if(!route_labs_to_provider.equals("")) {
						// Send to matching provider ohip_no
						ArrayList<String> providers = new ArrayList<String>(Arrays.asList(route_labs_to_provider.split(",")));
						providerRouteReport(String.valueOf(insertID), providers, DbConnectionFilter.getThreadLocalDbConnection(), demProviderNo, type, search, limit, orderByLength);
					} else {
						// Normal -- send to docs who requested for the labs OR to the family doctor
						providerRouteReport(String.valueOf(insertID), docNums, DbConnectionFilter.getThreadLocalDbConnection(), demProviderNo, type, search, limit, orderByLength);
					}
				}
				
			}
			retVal = h.audit();
			if(results != null) {
				results.segmentId = insertID;
			}
		} catch (Exception e) {
			logger.error("Error uploading lab to database");
			throw e;
		}

		return (retVal);

	}
	
	/**
	 * Method findProvidersForSpireLab
	 * Finds the providers that are associated with a spire lab.  (need to do this using doctor names, as
	 * spire labs don't have a valid ohip number associated with them).
	 */ 
	private static ArrayList<String> findProvidersForSpireLab(List<String> docNames) {
		List<String> docNums = new ArrayList<String>();
		ProviderDao providerDao = (ProviderDao)SpringUtils.getBean("providerDao");
		
		for (int i=0; i < docNames.size(); i++) {
			String[] firstLastName = docNames.get(i).split("\\s");
			if (firstLastName != null && firstLastName.length >= 2) {
				//logger.debug("Searching for provider with first and last name: " + firstLastName[0] + " " + firstLastName[firstLastName.length-1]);
				List<Provider> provList = providerDao.getProviderLikeFirstLastName("%"+firstLastName[0]+"%", firstLastName[firstLastName.length-1]);
				if (provList != null) {
					int provIndex = findProviderWithShortestFirstName(provList);
					if (provIndex != -1 && provList.size() >= 1 && !provList.get(provIndex).getProviderNo().equals("0")) {
						docNums.add( provList.get(provIndex).getProviderNo() );
						//logger.debug("ADDED1: " + provList.get(provIndex).getProviderNo());
					} else {
						// prepend 'dr ' to first name and try again
						provList = providerDao.getProviderLikeFirstLastName("dr " + firstLastName[0], firstLastName[1]);
						if (provList != null) {
							provIndex = findProviderWithShortestFirstName(provList);
							if (provIndex != -1 && provList.size() == 1 && !provList.get(provIndex).getProviderNo().equals("0")) {
								//logger.debug("ADDED2: " + provList.get(provIndex).getProviderNo());
								docNums.add( provList.get(provIndex).getProviderNo() );
							}
						}
					}
				}
			}
		}
		
		return (ArrayList<String>)docNums;
	}
	
	/**
	 * Method findProviderWithShortestFirstName
	 * Finds the provider with the shortest first name in a list of providers.
	 */ 
	private static int findProviderWithShortestFirstName(List<Provider> provList) {
		if (provList == null || provList.isEmpty())
			return -1;
			
		int index = 0;
		int shortestLength = provList.get(0).getFirstName().length();
		for (int i=1; i < provList.size(); i++) {
			int curLength = provList.get(i).getFirstName().length();
			if (curLength < shortestLength) {
				index = i;
				shortestLength = curLength;
			}
		}
		
		return index;
	}
	
	/**
	 * Attempt to match the doctors from the lab to a provider
	 */ 
	private static void providerRouteReport(String labId, ArrayList docNums, Connection conn, String altProviderNo, String labType, String search_on, Integer limit, boolean orderByLength) throws Exception {
		ArrayList<String> providerNums = new ArrayList<String>();
		PreparedStatement pstmt;
		String sql = "";
		String sqlLimit = "";
		String sqlOrderByLength = "";
		String sqlSearchOn = "ohip_no";
		
		if (search_on != null && search_on.length() > 0) {
			sqlSearchOn = search_on;
		}
		
		if (limit != null && limit.intValue() > 0) {
			sqlLimit = " limit " + limit.toString();
		}	
		
		if (orderByLength) {
			sqlOrderByLength = " order by length(first_name)";
		}
		MiscUtils.getLogger().debug("Sending to: " +docNums);
		MiscUtils.getLogger().debug("Demographic Provider: " + altProviderNo);
		
		if (docNums != null) {
			for (int i = 0; i < docNums.size(); i++) {

				if (docNums.get(i) != null && !((String) docNums.get(i)).trim().equals("")) {
					sql = "select provider_no from provider where "+ sqlSearchOn +" = '" + ((String) docNums.get(i)) + "'" + sqlOrderByLength + sqlLimit;
					pstmt = conn.prepareStatement(sql);
					ResultSet rs = pstmt.executeQuery();
					while (rs.next()) {
						providerNums.add(oscar.Misc.getString(rs, "provider_no"));
					}
					rs.close();
					pstmt.close();

					String otherIdMatchKey = OscarProperties.getInstance().getProperty("lab.other_id_matching", "");
					if(otherIdMatchKey.length()>0) {
						OtherId otherId = OtherIdManager.searchTable(OtherIdManager.PROVIDER, otherIdMatchKey, (String)docNums.get(i));
						if(otherId != null) {
							providerNums.add(otherId.getTableId());
						}
					}

				}
			}
		}
		
		//if (!labType.equals("Spire"))
		//	labType = "HL7";
		
		
		ProviderLabRouting routing = new ProviderLabRouting();
		if (providerNums.size() > 0) {
			for (int i = 0; i < providerNums.size(); i++) {
				String provider_no = providerNums.get(i);
				routing.route(labId, provider_no, conn, "HL7");
			}
		} else {
			routing.route(labId, "0", conn, "HL7");
			routing.route(labId, altProviderNo, conn, "HL7");
		}
	}

	/**
	 * Attempt to match the doctors from the lab to a provider
	 */
	private static void providerRouteReport(String labId, ArrayList docNums, Connection conn, String altProviderNo, String labType) throws Exception {
		providerRouteReport(labId, docNums, conn, altProviderNo, labType, null, null, false);
	}

	/**
	 * Attempt to match the patient from the lab to a demographic, return the patients provider which is to be used then no other provider can be found to match the patient to.
	 */
	private static String patientRouteReport(int labId, String lastName, String firstName, String sex, String dob, String hin, Connection conn) throws SQLException {

		String sql;
		String demo = "0";
		String provider_no = "0";
		// 19481015
		String dobYear = "%";
		String dobMonth = "%";
		String dobDay = "%";
		String hinMod = "%";

		int count = 0;
		try {

			Boolean truncated_hin = false;
			if ( hin != null && !hin.trim().equals("") ) {
				hinMod = new String(hin);
				if (hinMod.length() == 12) {
					truncated_hin = true;
					hinMod = hinMod.substring(0, 10);
				}
			}

			if (dob != null && !dob.equals("")) {
				String[] dobArray = dob.trim().split("-");
				dobYear = dobArray[0];
				dobMonth = dobArray[1];
				dobDay = dobArray[2];
			}

			if (!firstName.equals("")) firstName = firstName.substring(0, 1);
			if (!lastName.equals("")) lastName = lastName.substring(0, 1);

			ArrayList<String> params = new ArrayList<String>();
			params.add(dobYear);
			params.add(dobMonth);
			params.add(dobDay);
			params.add(sex + "%");
			if (hinMod.equals("%")) {

				sql = "SELECT ";
				sql += "  d.demographic_no, d.provider_no ";
				sql += "FROM demographic d ";
				sql += "LEFT JOIN demographic_merged dm ";
				sql += "  ON d.demographic_no = dm.demographic_no ";
				sql += "WHERE dm.id IS NULL ";
				sql += "AND d.year_of_birth like ? ";
				sql += "AND d.month_of_birth like ? ";
				sql += "AND d.date_of_birth like ? ";
				sql += "AND d.sex like ? ";
				sql += "AND d.last_name like ? ";
				sql += "AND d.first_name like ? ";

				params.add(lastName + "%");
				params.add(firstName + "%");

			} else if (OscarProperties.getInstance().getBooleanProperty("LAB_NOMATCH_NAMES", "yes")) {

				sql = "SELECT ";
				sql += "  d.demographic_no, d.provider_no ";
				sql += "FROM demographic d ";
				sql += "LEFT JOIN demographic_merged dm ";
				sql += "  ON d.demographic_no = dm.demographic_no ";
				sql += "WHERE dm.id IS NULL ";
				sql += "AND d.year_of_birth like ? ";
				sql += "AND d.month_of_birth like ? ";
				sql += "AND d.date_of_birth like ? ";
				sql += "AND d.sex like ? ";
				sql += "AND d.hin = ? ";

				params.add(hinMod);

			} else {

				sql = "SELECT ";
				sql += "  d.demographic_no, d.provider_no ";
				sql += "FROM demographic d ";
				sql += "LEFT JOIN demographic_merged dm ";
				sql += "  ON d.demographic_no = dm.demographic_no ";
				sql += "WHERE dm.id IS NULL ";
				sql += "AND d.year_of_birth like ? ";
				sql += "AND d.month_of_birth like ? ";
				sql += "AND d.date_of_birth like ? ";
				sql += "AND d.sex like ? ";
				sql += "AND d.hin = ? ";
				sql += "AND d.last_name like ? ";
				sql += "AND d.first_name like ? ";

				params.add(hinMod);
				params.add(lastName + "%");
				params.add(firstName + "%");
			}

			PreparedStatement pstmt = conn.prepareStatement(sql);

			Iterator<String> iter = params.iterator();
			int index = 1;
			while(iter.hasNext())
			{
				String paramValue = iter.next();
				pstmt.setString(index, paramValue);

				index++;
			}

			ResultSet rs = pstmt.executeQuery();

			// If the hin was truncated try to match the full hin. This is here as there is code to 
			// truncate hins that are 12 characters. Unfortunately Quebec hins are 12 characters...
			if (truncated_hin && rs.first() == false)
			{
				sql = "SELECT ";
				sql += "  d.demographic_no, d.provider_no ";
				sql += "FROM demographic d ";
				sql += "LEFT JOIN demographic_merged dm ";
				sql += "  ON d.demographic_no = dm.demographic_no ";
				sql += "WHERE dm.id IS NULL ";
				sql += "AND d.year_of_birth like ? ";
				sql += "AND d.month_of_birth like ? ";
				sql += "AND d.date_of_birth like ? ";
				sql += "AND d.sex like ? ";
				sql += "AND d.hin = ? ";
				sql += "AND d.last_name like ? ";
				sql += "AND d.first_name like ? ";

				ArrayList<String> params_trunc = new ArrayList<String>();
				params_trunc.add(dobYear);
				params_trunc.add(dobMonth);
				params_trunc.add(dobDay);
				params_trunc.add(sex + "%");
				params_trunc.add(hin);
				params_trunc.add(lastName + "%");
				params_trunc.add(firstName + "%");

				rs.close();
				pstmt.close();
				pstmt = conn.prepareStatement(sql);

				iter = params_trunc.iterator();
				index = 1;
				while(iter.hasNext())
				{
					String paramValue = iter.next();
					pstmt.setString(index, paramValue);

					index++;
				}

				rs = pstmt.executeQuery();
			}

			while (rs.next()) {
				count++;
				demo = oscar.Misc.getString(rs, "demographic_no");
				provider_no = oscar.Misc.getString(rs, "provider_no");
			}
			rs.close();
			pstmt.close();
		} catch (SQLException sqlE) {
			throw sqlE;
		}

		try {
			if (count != 1) {
				demo = "0";
				logger.info("Could not find patient for lab: " + labId + " # of possible matches :" + count);
			} else {
				Hl7textResultsData.populateMeasurementsTable("" + labId, demo);
			}

			sql = "insert into patientLabRouting (demographic_no, lab_no,lab_type) values ('" + demo + "', '" + labId + "','HL7')";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.executeUpdate();

			pstmt.close();
		} catch (SQLException sqlE) {
			logger.info("NO MATCHING PATIENT FOR LAB id =" + labId);
			throw sqlE;
		}

		return provider_no;
	}

	/**
	 * Used when errors occur to clean the database of labs that have not been inserted into all of the necessary tables
	 */
	public static void clean(int fileId) {

		try {

			Connection conn = DbConnectionFilter.getThreadLocalDbConnection();
			PreparedStatement pstmt;

			ResultSet rs;
			String sql;

			sql = "SELECT lab_id FROM hl7TextMessage WHERE fileUploadCheck_id='" + fileId + "'";
			pstmt = conn.prepareStatement(sql);
			ResultSet labId_rs = pstmt.executeQuery();

			while (labId_rs.next()) {
				int lab_id = labId_rs.getInt("lab_id");

				try {
					sql = "SELECT * FROM hl7TextInfo WHERE lab_no='" + lab_id + "'";
					pstmt = conn.prepareStatement(sql);
					rs = pstmt.executeQuery();
					if (rs.next()) {
						sql = "INSERT INTO recyclebin (provider_no, updatedatetime, table_name, keyword, table_content) " + "VALUES ('0', '" + UtilDateUtilities.getToday("yyyy-MM-dd HH:mm:ss") + "', 'hl7TextInfo', '" + lab_id + "', " + "'<id>" + oscar.Misc.getString(rs, "id") + "</id>" + "<lab_no>" + lab_id + "</lab_no>" + "<sex>" + oscar.Misc.getString(rs, "sex") + "</sex>" + "<health_no>" + oscar.Misc.getString(rs, "health_no") + "</health_no>" + "<result_status>"
						        + oscar.Misc.getString(rs, "result_status") + "</result_status>" + "<final_result_count>" + oscar.Misc.getString(rs, "final_result_count") + "</final_result_count>" + "<obr_date>" + oscar.Misc.getString(rs, "obr_date") + "</obr_date>" + "<priority>" + oscar.Misc.getString(rs, "priority") + "</priority>" + "<requesting_client>" + oscar.Misc.getString(rs, "requesting_client") + "</requesting_client>" + "<discipline>" + oscar.Misc.getString(rs, "discipline") + "</discipline>"
						        + "<last_name>" + oscar.Misc.getString(rs, "last_name") + "</last_name>" + "<first_name>" + oscar.Misc.getString(rs, "first_name") + "</first_name>" + "<report_status>" + oscar.Misc.getString(rs, "report_status") + "</report_status>" + "<accessionNum>" + oscar.Misc.getString(rs, "accessionNum") + "</accessionNum>')";

						pstmt = conn.prepareStatement(sql);
						pstmt.executeUpdate();

						sql = "DELETE FROM hl7TextInfo where lab_no='" + lab_id + "'";
						pstmt = conn.prepareStatement(sql);
						pstmt.executeUpdate();
					}
				} catch (SQLException e) {
					logger.error("Error cleaning hl7TextInfo table for lab_no '" + lab_id + "'", e);
				}

				try {
					sql = "SELECT * FROM hl7TextMessage WHERE lab_id='" + lab_id + "'";
					pstmt = conn.prepareStatement(sql);
					rs = pstmt.executeQuery();
					if (rs.next()) {
						sql = "INSERT INTO recyclebin (provider_no, updatedatetime, table_name, keyword, table_content) " + "VALUES ('0', '" + UtilDateUtilities.getToday("yyyy-MM-dd HH:mm:ss") + "', 'hl7TextMessage', '" + lab_id + "', " + "'<lab_id>" + oscar.Misc.getString(rs, "lab_id") + "</lab_id>" + "<message>" + oscar.Misc.getString(rs, "message") + "</message>" + "<type>" + oscar.Misc.getString(rs, "type") + "</type>" + "<fileUploadCheck_id>" + oscar.Misc.getString(rs, "fileUploadCheck_id")
						        + "</fileUploadCheck_id>')";

						pstmt = conn.prepareStatement(sql);
						pstmt.executeUpdate();

						sql = "DELETE FROM hl7TextMessage where lab_id='" + lab_id + "'";
						pstmt = conn.prepareStatement(sql);
						pstmt.executeUpdate();
					}
				} catch (SQLException e) {
					logger.error("Error cleaning hl7TextMessage table for lab_id '" + lab_id + "'", e);
				}

				try {
					sql = "SELECT * FROM providerLabRouting WHERE lab_no='" + lab_id + "'";
					pstmt = conn.prepareStatement(sql);
					rs = pstmt.executeQuery();
					if (rs.next()) {
						sql = "INSERT INTO recyclebin (provider_no, updatedatetime, table_name, keyword, table_content) " + "VALUES ('0', '" + UtilDateUtilities.getToday("yyyy-MM-dd HH:mm:ss") + "', 'providerLabRouting', '" + lab_id + "', " + "'<provider_no>" + oscar.Misc.getString(rs, "provider_no") + "</provider_no>" + "<lab_no>" + oscar.Misc.getString(rs, "lab_no") + "</lab_no>" + "<status>" + oscar.Misc.getString(rs, "status") + "</status>" + "<comment>" + oscar.Misc.getString(rs, "comment")
						        + "</comment>" + "<timestamp>" + oscar.Misc.getString(rs, "timestamp") + "</timestamp>" + "<lab_type>" + oscar.Misc.getString(rs, "lab_type") + "</lab_type>" + "<id>" + oscar.Misc.getString(rs, "id") + "</id>')";

						pstmt = conn.prepareStatement(sql);
						pstmt.executeUpdate();

						sql = "DELETE FROM providerLabRouting where lab_no='" + lab_id + "'";
						pstmt = conn.prepareStatement(sql);
						pstmt.executeUpdate();
					}
				} catch (SQLException e) {
					logger.error("Error cleaning providerLabRouting table for lab_no '" + lab_id + "'", e);
				}

				try {
					sql = "SELECT * FROM patientLabRouting WHERE lab_no='" + lab_id + "'";
					pstmt = conn.prepareStatement(sql);
					rs = pstmt.executeQuery();
					if (rs.next()) {
						sql = "INSERT INTO recyclebin (provider_no, updatedatetime, table_name, keyword, table_content) " + "VALUES ('0', '" + UtilDateUtilities.getToday("yyyy-MM-dd HH:mm:ss") + "', 'patientLabRouting', '" + lab_id + "', " + "'<demographic_no>" + oscar.Misc.getString(rs, "demographic_no") + "</demographic_no>" + "<lab_no>" + oscar.Misc.getString(rs, "lab_no") + "</lab_no>" + "<lab_type>" + oscar.Misc.getString(rs, "lab_type") + "</lab_type>" + "<id>" + oscar.Misc.getString(rs, "id")
						        + "</id>')";

						pstmt = conn.prepareStatement(sql);
						pstmt.executeUpdate();

						sql = "DELETE FROM patientLabRouting where lab_no='" + lab_id + "'";
						pstmt = conn.prepareStatement(sql);
						pstmt.executeUpdate();
					}
				} catch (SQLException e) {
					logger.error("Error cleaning patientLabRouting table for lab_no '" + lab_id + "'", e);
				}

				try {
					sql = "SELECT measurement_id FROM measurementsExt WHERE keyval='lab_no' and val='" + lab_id + "'";
					pstmt = conn.prepareStatement(sql);
					rs = pstmt.executeQuery();

					while (rs.next()) {

						int meas_id = rs.getInt("measurement_id");
						sql = "SELECT * FROM measurements WHERE id='" + meas_id + "'";
						pstmt = conn.prepareStatement(sql);
						ResultSet rs2 = pstmt.executeQuery();

						if (rs2.next()) {
							sql = "INSERT INTO recyclebin (provider_no, updatedatetime, table_name, keyword, table_content) " + "VALUES ('0', '" + UtilDateUtilities.getToday("yyyy-MM-dd HH:mm:ss") + "', 'measurements', '" + meas_id + "', " + "'<id>" + rs2.getString("id") + "</id>" + "<type>" + rs2.getString("type") + "</type>" + "<demographicNo>" + rs2.getString("demographicNo") + "</demographicNo>" + "<providerNo>" + rs2.getString("providerNo") + "</providerNo>" + "<dataField>"
							        + rs2.getString("dataField") + "</dataField>" + "<measuringInstruction>" + rs2.getString("measuringInstruction") + "</measuringInstruction>" + "<comments>" + rs2.getString("comments") + "</comments>" + "<dateObserved>" + rs2.getString("dateObserved") + "</dateObserved>" + "<dateEntered>" + rs2.getString("dateEntered") + "</dateEntered>')";

							pstmt = conn.prepareStatement(sql);
							pstmt.executeUpdate();

							sql = "DELETE FROM measurements WHERE id='" + meas_id + "'";
							pstmt = conn.prepareStatement(sql);
							pstmt.executeUpdate();
						}

						sql = "SELECT * FROM measurementsExt WHERE measurement_id='" + meas_id + "'";
						pstmt = conn.prepareStatement(sql);
						rs2 = pstmt.executeQuery();

						while (rs2.next()) {
							sql = "INSERT INTO recyclebin (provider_no, updatedatetime, table_name, keyword, table_content) " + "VALUES ('0', '" + UtilDateUtilities.getToday("yyyy-MM-dd HH:mm:ss") + "', 'measurementsExt', '" + meas_id + "', " + "'<id>" + rs2.getString("id") + "</id>" + "<measurement_id>" + rs2.getString("measurement_id") + "</measurement_id>" + "<keyval>" + rs2.getString("keyval") + "</keyval>" + "<val>" + rs2.getString("val") + "</val>')";

							pstmt = conn.prepareStatement(sql);
							pstmt.executeUpdate();

							sql = "DELETE FROM measurementsExt WHERE measurement_id='" + meas_id + "'";
							pstmt = conn.prepareStatement(sql);
							pstmt.executeUpdate();
						}
					}

				} catch (SQLException e) {
					logger.error("Error cleaning measuremnts or measurementsExt table for lab_no '" + lab_id + "'", e);
				}

			}

			try {
				sql = "SELECT * FROM fileUploadCheck WHERE id = '" + fileId + "'";
				pstmt = conn.prepareStatement(sql);
				rs = pstmt.executeQuery();
				if (rs.next()) {
					sql = "INSERT INTO recyclebin (provider_no, updatedatetime, table_name, keyword, table_content) " + "VALUES ('0', '" + UtilDateUtilities.getToday("yyyy-MM-dd HH:mm:ss") + "', 'fileUploadCheck', '" + fileId + "', " + "'<id>" + oscar.Misc.getString(rs, "id") + "</id>" + "<provider_no>" + oscar.Misc.getString(rs, "provider_no") + "</provider_no>" + "<filename>" + oscar.Misc.getString(rs, "filename") + "</filename>" + "<md5sum>" + oscar.Misc.getString(rs, "md5sum") + "</md5sum>"
					        + "<datetime>" + oscar.Misc.getString(rs, "date_time") + "</datetime>')";

					pstmt = conn.prepareStatement(sql);
					pstmt.executeUpdate();

					sql = "DELETE FROM fileUploadCheck where id = '" + fileId + "'";
					pstmt = conn.prepareStatement(sql);
					pstmt.executeUpdate();

				}
			} catch (SQLException e) {
				logger.error("Error cleaning fileUploadCheck table for id '" + fileId + "'", e);
			}

			pstmt.close();
			logger.info("Successfully cleaned the database");

		} catch (SQLException e) {
			logger.error("Could not clean database: ", e);
		}
	}
}
