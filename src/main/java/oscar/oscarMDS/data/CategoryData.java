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


package oscar.oscarMDS.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.oscarehr.common.dao.MyGroupDao;
import org.oscarehr.util.DbConnectionFilter;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;

public class CategoryData {

	private int totalDocs = 0;
	private int totalLabs = 0;
	private int unmatchedLabs = 0;
	private int unmatchedDocs = 0;
    private int totalNumDocs = 0;
    private int abnormalCount = 0;
    private int normalCount = 0;
    private HashMap<Integer,PatientInfo> patients;

	public int getTotalDocs() {
		return totalDocs;
	}

	public int getTotalLabs() {
		return totalLabs;
	}

	public int getUnmatchedLabs() {
		return unmatchedLabs;
	}

	public int getUnmatchedDocs() {
		return unmatchedDocs;
	}

	public int getTotalNumDocs() {
		return totalNumDocs;
	}

	public int getAbnormalCount() {
		return abnormalCount;
	}

	public int getNormalCount() {
		return normalCount;
	}

	public HashMap<Integer, PatientInfo> getPatients() {
		return patients;
	}

	private String patientLastName;
	private String searchProviderNo;
	private String status;
	private String patientFirstName;
	private String patientHealthNumber;
	private boolean patientSearch;
	private boolean providerSearch;
	private boolean checkRequestingProvider;
	private boolean abnormalsOnly;
	private String endDateStr;
	private String searchGroupNo;
	private String providerNoList;
	private boolean neverAcknowledgedItems;

	public CategoryData(String patientLastName, String patientFirstName, String patientHealthNumber, boolean patientSearch,
					    boolean providerSearch, String searchProviderNo, String status, boolean checkRequestingProvider, boolean abnormalsOnly,
					    String endDateStr, String searchGroupNo, Boolean neverAcknowledgedItems)  {
		MiscUtils.getLogger().debug("constructor");

		this.patientLastName = patientLastName;
		this.searchProviderNo = searchProviderNo;
		this.status = status;
		this.patientFirstName = patientFirstName;
		this.patientHealthNumber = patientHealthNumber;
		this.patientSearch = patientSearch;
		this.providerSearch = providerSearch;
		this.checkRequestingProvider = checkRequestingProvider;
		this.abnormalsOnly = abnormalsOnly;
		this.endDateStr = endDateStr;
		this.searchGroupNo = searchGroupNo;
		this.neverAcknowledgedItems = neverAcknowledgedItems;
		
		MyGroupDao myGroupDao = (MyGroupDao)SpringUtils.getBean("myGroupDao");

		List<String> providerNoArr = myGroupDao.getGroupDoctors(this.searchGroupNo);
		
		providerNoList = "";
		
        if(providerNoArr != null && providerNoArr.size() > 0){
        	providerNoList = StringUtils.join(providerNoArr, ",");
        }

    	totalDocs = 0;
		totalLabs = 0;
		unmatchedLabs = 0;
	    unmatchedDocs = 0;
	    totalNumDocs = 0;
	    abnormalCount = 0;
	    normalCount = 0;

        patients = new HashMap<Integer,PatientInfo>();

	}
	public void populateCountsAndPatients() throws SQLException {

		// Retrieving documents and labs.
		totalDocs += getDocumentCountForPatientSearch();
        totalLabs += getLabCountForPatientSearch();       
        
		MiscUtils.getLogger().debug("totalDocs:"+totalDocs);
        MiscUtils.getLogger().debug("totalLabs:"+totalLabs);

        // If this is not a patient search, then we need to find the unmatched documents.
        if (!patientSearch) {
            unmatchedDocs += getDocumentCountForUnmatched();
            unmatchedLabs += getLabCountForUnmatched();
            totalDocs += unmatchedDocs;
            totalLabs += unmatchedLabs;
        }
        
        MiscUtils.getLogger().debug("unmatched docs:"+unmatchedDocs);
        MiscUtils.getLogger().debug("unmatched labs:"+unmatchedLabs);

        // The total overall items is the sum of docs and labs.
        totalNumDocs = totalDocs + totalLabs;

        // Retrieving abnormal labs.
        abnormalCount = getAbnormalCount(true);
        MiscUtils.getLogger().debug("abnormal count:"+abnormalCount);

        // Cheaper to subtract abnormal from total to find the number of normal docs.
        normalCount = totalNumDocs - abnormalCount;
	}

	public int getLabCountForUnmatched()
			throws SQLException {
		String sql;
		
		sql = " SELECT HIGH_PRIORITY COUNT(1) as count "
			+ " FROM (SELECT plr2.lab_no as plab_no2, plr.lab_no as plab_no, hl7.lab_no FROM patientLabRouting plr2, ";
		
		if(this.neverAcknowledgedItems && "N".equals(status)){
			sql = sql + "(" +
					    "    SELECT * FROM (" +
					    "        SELECT plr.*" +
					    "        FROM providerLabRouting plr" + 
					    "        GROUP BY lab_no, status" +
					    "    ) lab_status_grouped GROUP BY lab_no HAVING count(lab_no) = 1" +
					    ") plr, ";
		}else{
			sql = sql + "providerLabRouting plr, ";
		}
		
		sql = sql + " hl7TextInfo hl7 "
			+ " WHERE plr.lab_no = plr2.lab_no "
			+ " AND hl7.lab_no = plr2.lab_no "
			+ (providerSearch ? " AND plr.provider_no = ? " : "")
			+ (!providerNoList.equals("") ? " AND plr.provider_no IN ("+providerNoList+") " : "")
			+ "   AND plr.lab_type = 'HL7' "
			+ "   AND plr.status like ? "
			+ "   AND plr2.lab_type = 'HL7'"
			+ "   AND plr2.demographic_no = '0' "
			+ (abnormalsOnly ? " AND hl7.result_status = 'A' " : "")
			+ (endDateStr != null && !endDateStr.equals("") ? " AND DATE(hl7.obr_date) < ? " : "")
			+ "   GROUP BY COALESCE(hl7.accessionNum, hl7.lab_no)) counttable";
		    

		Connection c  = DbConnectionFilter.getThreadLocalDbConnection();
		PreparedStatement ps = c.prepareStatement(sql);
		
		int qp_number = 1;
		if(providerSearch){
			
			ps.setString(qp_number, searchProviderNo);
			qp_number++;
			ps.setString(qp_number, "%"+status+"%");
			qp_number++;
		}else{
			ps.setString(qp_number, "%"+status+"%");
			qp_number++;
		}
		
		if(endDateStr != null && !endDateStr.equals("")){
			ps.setString(qp_number, endDateStr);
			qp_number++;
		}
		ResultSet rs= ps.executeQuery();

		return (rs.next() ? rs.getInt("count") : 0);
	}

	public int getAbnormalCount(boolean isAbnormal) throws SQLException {
		ResultSet rs;
		String sql;
		
		Connection c  = DbConnectionFilter.getThreadLocalDbConnection();
		PreparedStatement ps;
		if (patientSearch) {
        	sql = " SELECT HIGH_PRIORITY COUNT(1) as count" 
        		+ " FROM (SELECT plr.lab_no as plrlab_no, info.lab_no, doc.document_no "
        		+ " 	FROM patientLabRouting cd, demographic d, ";

        	if(this.neverAcknowledgedItems && "N".equals(this.status)){
        		sql = sql +"(" +
					    "    SELECT * FROM (" +
					    "        SELECT plr.*" +
					    "        FROM providerLabRouting plr" + 
					    "        GROUP BY lab_no, status" +
					    "    ) lab_status_grouped GROUP BY lab_no HAVING count(lab_no) = 1" +
					    ") plr, ";
        		
        	}else{
        		sql = sql +"providerLabRouting plr, ";
        	}
        	
        	sql = sql + " hl7TextInfo info, " 
        		+ " document doc "
        		+ " WHERE d.last_name like ? "
        		+ " 	AND d.first_name like ? "
        		+ " 	AND d.hin like ? "
        		+ " 	AND plr.status like ? "
        		+ (providerSearch ? "AND plr.provider_no = ? " : "")
        		+ (!providerNoList.equals("") ? " AND plr.provider_no IN ("+providerNoList+") " : "")
        		+ " 	AND plr.lab_type = 'HL7' "
        		+ " 	AND cd.lab_type = 'HL7' "
        		+ " 	AND cd.lab_no = plr.lab_no "
        		+ " 	AND cd.demographic_no = d.demographic_no "
        		+ "     AND doc.document_no = plr.lab_no"
        		+ " 	AND info.lab_no = plr.lab_no "
        		+ " 	AND " 
        		+ "			(result_status "+(isAbnormal ? "" : "!")+"= 'A' "
        		+ " 	     OR doc.doc_result_status "+(isAbnormal ? "" : "!")+"= 'A' " 
        		+"           ) "
        		+ (endDateStr != null && !endDateStr.equals("")?"     AND DATE(info.obr_date) < ? " : "")
        		+ " GROUP BY COALESCE(info.accessionNum, info.lab_no)) counttable";
        	ps = c.prepareStatement(sql);
        	ps.setString(1, "%"+patientLastName+"%");
        	ps.setString(2, "%"+patientFirstName+"%");
        	ps.setString(3, "%"+patientHealthNumber+"%");
        	ps.setString(4, "%"+status+"%");
        	int qp_number = 5;
        	if(providerSearch){
        		ps.setString(qp_number, searchProviderNo);
        		qp_number++;
        	}
        	if(endDateStr != null && !endDateStr.equals("")){
        		ps.setString(qp_number, endDateStr);
        		qp_number++;
        	}
        }
        else if (providerSearch || !"".equals(status)){ // providerSearch
        	sql = "SELECT HIGH_PRIORITY COUNT(1) as count "
				+ " FROM (SELECT plr.lab_no as plrlab_no, info.lab_no, doc.document_no FROM ";
        	if(this.neverAcknowledgedItems && "N".equals(this.status)){
        		sql = sql + "(" +
					    "    SELECT * FROM (" +
					    "        SELECT plr.*" +
					    "        FROM providerLabRouting plr" + 
					    "        GROUP BY lab_no, status" +
					    "    ) lab_status_grouped GROUP BY lab_no HAVING count(lab_no) = 1" +
					    ")  plr, ";
        	}else{
        		sql = sql + "providerLabRouting plr, ";
        	}
        	
			sql = sql + "hl7TextInfo info, " 
				+ " document doc "
				+ " WHERE plr.status like ? "
				+ (providerSearch ? " AND plr.provider_no = ? " : " ")
				+ " AND plr.lab_type = 'HL7'  "
				+ " AND info.lab_no = plr.lab_no"
				+ " AND doc.document_no = plr.lab_no"
				+ " AND (info.result_status "+(isAbnormal ? "" : "!")+"= 'A' "
				+ " OR doc.doc_result_status "+(isAbnormal ? "" : "!")+"= 'A') "
				+ " GROUP BY COALESCE(info.accessionNum, info.lab_no)) counttable";
        	ps = c.prepareStatement(sql);
        	ps.setString(1, "%"+status+"%");
        	if(providerSearch){
        		ps.setString(2, searchProviderNo);
        	}
        }
        else {
        	sql = " SELECT HIGH_PRIORITY COUNT(1) as count "
            	+ " FROM (SELECT info.lab_no, doc.document_no FROM " 
				+ " hl7TextInfo info, " 
            	+ " document doc "
            	+ " WHERE (info.result_status "+(isAbnormal ? "" : "!")+"= 'A' "
            	+ " OR doc.doc_result_status "+(isAbnormal ? "" : "!")+"= 'A') "
        	    + " AND doc.document_no = info.lab_no"
        	    + " GROUP BY COALESCE(info.accessionNum, info.lab_no)) counttable";
        	ps = c.prepareStatement(sql);
        }
		
		rs= ps.executeQuery();
        return (rs.next() ? rs.getInt("count") : 0);
	}

	public int getDocumentCountForUnmatched() throws SQLException {
		int qp_number = 1;
		boolean qp_status = false;
		boolean qp_provider_no = false;
		boolean qp_end_date = false;

		String sql	= "SELECT count(*) AS count "
					+ "FROM document doc "
					+ "INNER JOIN ctl_document cdoc ON (cdoc.module = 'demographic' AND doc.document_no = cdoc.document_no) "
					+ "LEFT JOIN patientLabRouting patLR ON (patLR.lab_type = 'DOC' AND patLR.lab_no = doc.document_no) ";
		if(this.neverAcknowledgedItems && "N".equals(status)){
			sql = sql + "LEFT JOIN (" +
					    "    SELECT * FROM (" +
					    "        SELECT plr.*" +
					    "        FROM providerLabRouting plr" + 
					    "        GROUP BY lab_no, status" +
					    "    ) lab_status_grouped GROUP BY lab_no HAVING count(lab_no) = 1" +
					    ") proLR ON (proLR.lab_type = 'DOC' AND proLR.lab_no = doc.document_no) ";
		}else{
			sql = sql + "LEFT JOIN providerLabRouting proLR ON (proLR.lab_type = 'DOC' AND proLR.lab_no = doc.document_no) ";
		}
		sql = sql + "WHERE (cdoc.module_id = -1) ";

		if ("N".equals(status)) {
			sql = sql + " AND (proLR.status IN ('N', NULL)) ";
		} else if (!"".equals(status)) {
			sql = sql + " AND (proLR.status = ?) ";
			qp_status = true;
		}

		if (providerSearch)
		{
			if ("0".equals(searchProviderNo)) {
				sql = sql + "	AND (proLR.provider_no = '0') ";
			} else {
				sql = sql + " AND proLR.provider_no = ? ";
				qp_provider_no = true;
			}
		}
		
		if(!providerNoList.equals("")){
			sql = sql + " AND  proLR.provider_no IN ("+providerNoList+") ";
		}
		
		if (abnormalsOnly)
		{
			sql = sql + " AND doc.doc_result_status = 'A' ";
		}
		
		if( endDateStr != null && !endDateStr.equals("")){
			sql = sql + " AND doc.observationdate < ? ";
			qp_end_date = true;
		}
		
		sql = sql + " AND doc.status <> 'D' ";

		Connection c  = DbConnectionFilter.getThreadLocalDbConnection();
		PreparedStatement ps = c.prepareStatement(sql);
		qp_number = 1;
		if (qp_status) { ps.setString(qp_number++, status); }
		if (qp_provider_no) { ps.setString(qp_number++, searchProviderNo); }
		if (qp_end_date) { ps.setString(qp_number++, endDateStr); }

		ResultSet rs= ps.executeQuery();
		return (rs.next() ? rs.getInt("count") : 0);
	}

	public int getLabCountForPatientSearch() throws SQLException {
		PatientInfo info;
		;
		boolean qp_first_name = false;
		boolean qp_last_name = false;
		boolean qp_hin = false;
		/*
		String sql = " SELECT HIGH_PRIORITY d.demographic_no, d.last_name, d.first_name, COUNT(1) as count "
        	+ " FROM patientLabRouting cd,  demographic d, providerLabRouting plr, hl7TextInfo hl7, provider p "
        	+ " WHERE   d.last_name like ? "
        	+ " 	AND d.first_name like ? "
        	+ " 	AND d.hin like ? "
        	+ " 	AND cd.demographic_no = d.demographic_no "
        	+ "     AND hl7.lab_no = plr.lab_no "
        	+ " 	AND cd.lab_no = plr.lab_no "
        	+ " 	AND plr.lab_type = 'HL7' "
        	+ " 	AND cd.lab_type = 'HL7' "
        	+ " 	AND plr.status like ? "
        	+ (providerSearch ? "AND plr.provider_no = ? " : "")
        	+ (abnormalsOnly ? "AND hl7.result_status = 'A' " : "")
        	+ (checkRequestingProvider ? "AND hl7.requesting_client_no = p.ohip_no ":"")        	
        	+ " GROUP BY demographic_no ";
        	*/
		String sql = "SELECT * , COUNT(1) as count FROM ("
				+ " SELECT * FROM ("
				+ "   SELECT d.demographic_no, d.last_name, d.first_name, hl7.lab_no, hl7.accessionNum "
				+ "   FROM "
				+ "     patientLabRouting cd,";
		
		if(this.neverAcknowledgedItems && "N".equals(this.status)){
			sql = sql + "(" +
				    "    SELECT * FROM (" +
				    "        SELECT plr.*" +
				    "        FROM providerLabRouting plr" + 
				    "        GROUP BY lab_no, status" +
				    "    ) lab_status_grouped GROUP BY lab_no HAVING count(lab_no) = 1" +
				    ") plr, ";
		}else{
			sql = sql + "     providerLabRouting plr,";
		}
		
		
		sql = sql + " hl7TextInfo hl7, " 
				+ "     demographic d"
				+ (checkRequestingProvider ? ", provider p " : "")
				+ "   WHERE true";
		
		if (!"".equals(patientFirstName)) {
			sql = sql + "AND (d.first_name LIKE ?) ";
			qp_first_name = true;
		}
		
		if (!"".equals(patientLastName)) {
			sql = sql + "AND (d.last_name LIKE ?) ";
			qp_last_name = true;
		}
		
		if (!"".equals(patientHealthNumber)) {
			sql = sql + "AND (d.hin LIKE ?) ";
			qp_hin = true;
		}
		
		sql = sql + " 	AND cd.demographic_no = d.demographic_no "
	        	+ "     AND hl7.lab_no = plr.lab_no "
	        	+ " 	AND cd.lab_no = plr.lab_no "
	        	+ " 	AND plr.lab_type = 'HL7' "
	        	+ " 	AND cd.lab_type = 'HL7' "
	        	+ " 	AND plr.status like ? "
	        	+ (providerSearch ? "AND plr.provider_no = ? " : "")
	        	+ (!providerNoList.equals("") ? " AND  plr.provider_no IN ("+providerNoList+") " :"")	      
	        	+ (abnormalsOnly ? "AND hl7.result_status = 'A' " : "")
	        	+ (checkRequestingProvider ? "AND hl7.requesting_client_no = p.ohip_no ":"")
	        	+ ((endDateStr != null && !endDateStr.equals("")) ? "AND DATE(hl7.obr_date) < ? ": "")
	        	+ "   GROUP BY hl7.lab_no "
	        	+ " ) labs GROUP BY COALESCE(accessionNum, lab_no) ) labs2 GROUP BY demographic_no";

		Connection c  = DbConnectionFilter.getThreadLocalDbConnection();
		PreparedStatement ps = c.prepareStatement(sql);
		int q_number = 1;
		
		if (qp_first_name) { 
			ps.setString(q_number, "%" + patientFirstName + "%"); 
			q_number++;
		}
		
		if (qp_last_name) { 
			ps.setString(q_number++, "%" + patientLastName + "%");
			q_number++;
		}
		
		if (qp_hin) { 
			ps.setString(q_number++, "%" + patientHealthNumber + "%"); 
			q_number++;
		}
		
		ps.setString(q_number, "%"+status+"%");
		q_number++;
						
		if( providerSearch ){
			ps.setString(q_number, searchProviderNo);
			q_number++;
		}
		if(endDateStr != null && endDateStr != ""){
			ps.setString(q_number, endDateStr);
			q_number++;
		}
				
		ResultSet rs= ps.executeQuery();
        int count = 0;
        while(rs.next()){
        	int id = rs.getInt("demographic_no");
        	// Updating patient info if it already exists.
        	if (patients.containsKey(id)) {
        		info = patients.get(id);
        		info.setLabCount(rs.getInt("count"));
        	}
        	// Otherwise adding a new patient record.
        	else {
        		info = new PatientInfo(id, rs.getString("first_name"), rs.getString("last_name"));
        		info.setLabCount(rs.getInt("count"));
        		patients.put(info.id, info);
        	}
        	count += info.getLabCount();
        }
        return count;
	}

	public int getLabCountForDemographic(String demographicNo) throws SQLException {
		String sql = " SELECT HIGH_PRIORITY d.demographic_no, last_name, first_name, COUNT(1) as count "
        	+ " FROM patientLabRouting cd,  demographic d, providerLabRouting plr "
        	+ " WHERE   d.demographic_no = ?" 
        	+ " 	AND cd.demographic_no = d.demographic_no "
        	+ " 	AND cd.lab_no = plr.lab_no "
        	+ " 	AND plr.lab_type = 'HL7' "
        	+ " 	AND cd.lab_type = 'HL7' "
        	+ " 	AND plr.status like ? "
        	+ (providerSearch ? "AND plr.provider_no = ? " : "")
        	+ " GROUP BY demographic_no ";

		Connection c  = DbConnectionFilter.getThreadLocalDbConnection();
		PreparedStatement ps = c.prepareStatement(sql);
		ps.setString(1, demographicNo);
		ps.setString(2, "%"+status+"%");
		if(providerSearch){
			ps.setString(3, searchProviderNo);
		}
		ResultSet rs= ps.executeQuery();
        return (rs.next() ? rs.getInt("count") : 0);
	}

	public int getDocumentCountForPatientSearch() throws SQLException {
		PatientInfo info;
		int qp_number = 1;
		boolean qp_status = false;
		boolean qp_provider_no = false;
		boolean qp_first_name = false;
		boolean qp_last_name = false;
		boolean qp_hin = false;
		boolean qp_end_date = false;

		String sql 	= "SELECT d.demographic_no, d.last_name, d.first_name, COUNT(1) as count "
					+ "FROM ctl_document cd "
					+ "INNER JOIN demographic d ON d.demographic_no = cd.module_id ";
	
		if(this.neverAcknowledgedItems && "N".equals(status)){
			sql = sql + "INNER JOIN (" +
					    "    SELECT * FROM (" +
					    "        SELECT plr.*" +
					    "        FROM providerLabRouting plr" + 
					    "        GROUP BY lab_no, status" +
					    "    ) lab_status_grouped GROUP BY lab_no HAVING count(lab_no) = 1" +
					    ") proLR ON ( proLR.lab_type = 'DOC' AND proLR.lab_no = cd.document_no ) ";
		}else{
			sql = sql + "INNER JOIN providerLabRouting proLR ON ( proLR.lab_type = 'DOC' AND proLR.lab_no = cd.document_no ) ";
		}
		sql = sql + "LEFT JOIN document doc ON ( doc.document_no = cd.document_no ) "
					+ "WHERE cd.module='demographic' ";

		if ("N".equals(status)) {
			sql = sql + " AND (proLR.status IN ('N', NULL)) ";
		} else if (!"".equals(status)) {
			sql = sql + " AND (proLR.status = ?) ";
			qp_status = true;
		}

		if (providerSearch)
		{
			if ("0".equals(searchProviderNo)) {
				sql = sql + "	AND proLR.provider_no = '0' ";
			} else {
				sql = sql + " AND proLR.provider_no = ? ";
				qp_provider_no = true;
			}
		}
		
		if(!providerNoList.equals("")){
			sql = sql + " AND  proLR.provider_no IN ("+providerNoList+") ";
		}

		if (!"".equals(patientLastName)) {
			sql = sql + "  AND d.last_name like ? ";
			qp_last_name = true;
		}

		if (!"".equals(patientFirstName)) {
			sql = sql + "  AND d.first_name like ? ";
			qp_first_name = true;
		}

		if (!"".equals(patientHealthNumber)) {
			sql = sql + "  AND d.hin like ? ";
			qp_hin = true;
		}
		
		if(abnormalsOnly){
			sql = sql + "  AND doc.doc_result_status = 'A' ";
		}
		sql = sql + "AND doc.status <> 'D' ";
		
		
		if(endDateStr != null && !endDateStr.equals("")){
			sql = sql + "  AND doc.observationdate < ? ";
			qp_end_date = true;
		}
		

		sql = sql + "GROUP BY d.demographic_no ";
		

		Connection c  = DbConnectionFilter.getThreadLocalDbConnection();
		PreparedStatement ps = c.prepareStatement(sql);
		qp_number = 1;
		if (qp_status) { ps.setString(qp_number++, status); }
		if (qp_provider_no) { ps.setString(qp_number++, searchProviderNo); }
		if (qp_last_name) { ps.setString(qp_number++, "%" + patientLastName + "%"); }
		if (qp_first_name) { ps.setString(qp_number++, "%" + patientFirstName + "%"); }
		if (qp_hin) { ps.setString(qp_number++, "%" + patientHealthNumber + "%"); }
		if (qp_end_date) { ps.setString(qp_number++, endDateStr ); }

		ResultSet rs= ps.executeQuery();
		
        int count = 0;
        while(rs.next()){
        	info = new PatientInfo(rs.getInt("demographic_no"), rs.getString("first_name"), rs.getString("last_name"));
        	info.setDocCount(rs.getInt("count"));
        	patients.put(info.id, info);
        	count += info.getDocCount();
        }
        return count;
	}

	public int getDocumentCountForDemographic(String demographicNo) throws SQLException {
		String sql = " SELECT HIGH_PRIORITY demographic_no, last_name, first_name, COUNT(1) as count "
					+ " FROM ctl_document cd, demographic d, providerLabRouting plr "
					+ " WHERE   d.demographic_no = ?" 
					+ " 	AND cd.module_id = d.demographic_no "
					+ " 	AND cd.document_no = plr.lab_no "
					+ " 	AND plr.lab_type = 'DOC' "
					+ " 	AND plr.status like ? "
					+ (providerSearch ? "AND plr.provider_no = ? " : "")
					+ " GROUP BY demographic_no ";
		Connection c  = DbConnectionFilter.getThreadLocalDbConnection();
		PreparedStatement ps = c.prepareStatement(sql);
		ps.setString(1, demographicNo);
		ps.setString(2, "%"+status+"%");
		if(providerSearch){
			ps.setString(3, searchProviderNo);
		}
		ResultSet rs= ps.executeQuery();
        return (rs.next() ? rs.getInt("count") : 0);
	}

	public Long getCategoryHash() {
		return Long.valueOf("" + (int)'A' + totalNumDocs)
			 + Long.valueOf("" + (int)'D' + totalDocs)
			 + Long.valueOf("" + (int)'L' + totalLabs);
	}
}
