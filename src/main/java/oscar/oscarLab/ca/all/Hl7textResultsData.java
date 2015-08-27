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


package oscar.oscarLab.ca.all;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.oscarehr.common.dao.MeasurementsDeletedDao;
import org.oscarehr.common.model.MeasurementsDeleted;
import org.oscarehr.util.DbConnectionFilter;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;

import oscar.oscarDB.DBHandler;
import oscar.oscarLab.ca.all.parsers.Factory;
import oscar.oscarLab.ca.all.parsers.MessageHandler;
import oscar.oscarLab.ca.on.LabResultData;
import oscar.util.UtilDateUtilities;

public class Hl7textResultsData {

	
    private static Logger logger = MiscUtils.getLogger();
    private static MeasurementsDeletedDao measurementsDeletedDao = (MeasurementsDeletedDao) SpringUtils.getBean("measurementsDeletedDao");

    private Hl7textResultsData() {
    	// no one should instantiate this
    }

    public static void populateMeasurementsTable(String lab_no, String demographic_no){
        MessageHandler h = Factory.getHandler(lab_no);

        java.util.Calendar calender = java.util.Calendar.getInstance();
        String day =  Integer.toString(calender.get(java.util.Calendar.DAY_OF_MONTH));
        String month =  Integer.toString(calender.get(java.util.Calendar.MONTH)+1);
        String year = Integer.toString(calender.get(java.util.Calendar.YEAR));
        String hour = Integer.toString(calender.get(java.util.Calendar.HOUR));
        String min = Integer.toString(calender.get(java.util.Calendar.MINUTE));
        String second = Integer.toString(calender.get(java.util.Calendar.SECOND));
        String dateEntered = year+"-"+month+"-"+day+" " + hour + ":" + min + ":" + second + ":";

        try{

            Connection conn = DbConnectionFilter.getThreadLocalDbConnection();

            //Check for other versions of this lab
            String[] matchingLabs = getMatchingLabs(lab_no).split(",");
            //if this lab is the latest version delete the measurements from the previous version and insert the new ones

            int k = 0;
            while ( k < matchingLabs.length && !matchingLabs[k].equals(lab_no)){
                k++;
            }

            if(k != 0){
            	MeasurementsDeleted measurementsDeleted;

                String sql = "SELECT m.* FROM measurements m LEFT JOIN measurementsExt e ON m.id = measurement_id AND e.keyval='lab_no' WHERE e.val='"+matchingLabs[k-1]+"'";
                ResultSet rs = DBHandler.GetSQL(sql);
                while(rs.next()){
                	measurementsDeleted  = new MeasurementsDeleted();
                	measurementsDeleted.setType(oscar.Misc.getString(rs, "type"));
                	measurementsDeleted.setDemographicNo(Integer.valueOf(oscar.Misc.getString(rs, "demographicNo")));
                	measurementsDeleted.setProviderNo(oscar.Misc.getString(rs, "providerNo"));
                	measurementsDeleted.setDataField(oscar.Misc.getString(rs, "dataField"));
                	measurementsDeleted.setMeasuringInstruction(oscar.Misc.getString(rs, "measuringInstruction"));
                	measurementsDeleted.setComments(oscar.Misc.getString(rs, "comments"));
                	measurementsDeleted.setDateObserved(UtilDateUtilities.StringToDate(oscar.Misc.getString(rs, "dateObserved"), "yyyy-MM-dd hh:mm:ss"));
                	measurementsDeleted.setDateEntered(UtilDateUtilities.StringToDate(oscar.Misc.getString(rs, "dateEntered"), "yyyy-MM-dd hh:mm:ss"));
                	measurementsDeleted.setOriginalId(Integer.valueOf(oscar.Misc.getString(rs, "id")));
                	measurementsDeletedDao.persist(measurementsDeleted);

                    sql = "DELETE FROM measurements WHERE id='"+oscar.Misc.getString(rs, "id")+"'";
                    DBHandler.RunSQL(sql);
                    //sql = "DELETE FROM measurementsExt WHERE measurement_id='"+oscar.Misc.getString(rs,"measurement_id")+"'";
                    //DBHandler.RunSQL(sql);

                }

            }
            // loop through the measurements for the lab and insert them

            for (int i=0; i < h.getOBRCount(); i++){
                for (int j=0; j < h.getOBXCount(i); j++){

                    String result = h.getOBXResult(i, j);

                    // only insert if there is a result and it is supposed to be viewed
                    if (result.equals("") || result.equals("DNR") || h.getOBXName(i, j).equals("") || h.getOBXResultStatus(i, j).equals("DNS"))
                        continue;
                    logger.debug("obx("+j+") should be inserted");
                    String identifier = h.getOBXIdentifier(i,j);
                    String name = h.getOBXName(i,j);
                    String unit = h.getOBXUnits(i,j);
                    String labname = h.getPatientLocation();
                    String accession = h.getAccessionNum();
                    String req_datetime = h.getRequestDate(i);
                    String datetime = h.getTimeStamp(i,j);
                    String olis_status = h.getOBXResultStatus(i, j);
                    String abnormal = h.getOBXAbnormalFlag(i,j);
                    if ( abnormal != null && ( abnormal.equals("A") || abnormal.startsWith("H")) ){
                        abnormal = "A";
                    }else if ( abnormal != null && abnormal.startsWith("L")){
                        abnormal = "L";
                    }else{
                        abnormal = "N";
                    }
                    String[] refRange = splitRefRange(h.getOBXReferenceRange(i,j));
                    String comments = "";
                    for (int l=0; l<h.getOBXCommentCount(i,j); l++) {
                    	comments += comments.length()>0 ? "\n"+h.getOBXComment(i,j,l) : h.getOBXComment(i,j,l);
                	}

                    String sql = "SELECT b.ident_code, type.measuringInstruction FROM measurementMap a, measurementMap b, measurementType type WHERE b.lab_type='FLOWSHEET' AND a.ident_code=? AND a.loinc_code = b.loinc_code and type.type = b.ident_code";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, identifier);
                    String measType="";
                    String measInst="";
                    ResultSet rs = pstmt.executeQuery();
                    if(rs.next()){
                        measType = oscar.Misc.getString(rs, "ident_code");
                        measInst = oscar.Misc.getString(rs, "measuringInstruction");
                    }else{
                       logger.debug("CODE:"+identifier+ " needs to be mapped");
                    }


                    sql = "INSERT INTO measurements (type, demographicNo, providerNo, dataField, measuringInstruction, dateObserved, dateEntered )VALUES (?, ?, '0', ?, ?, ?, ?)";
                    logger.debug(sql);
                    pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1,measType);
                    pstmt.setString(2,demographic_no);
                    pstmt.setString(3,result);
                    pstmt.setString(4,measInst);
                    pstmt.setString(5,h.getTimeStamp(i, j));
                    pstmt.setString(6,dateEntered);
                    pstmt.executeUpdate();
                    rs = pstmt.getGeneratedKeys();
                    String insertID = null;
                    if(rs.next())
                        insertID = oscar.Misc.getString(rs, 1);

                    String measurementExt = "INSERT INTO measurementsExt (measurement_id, keyval, val) VALUES (?,?,?)";

                    pstmt = conn.prepareStatement(measurementExt);

                    logger.debug("Inserting into measurementsExt id "+insertID+ " lab_no "+ lab_no);
                    pstmt.setString(1, insertID);
                    pstmt.setString(2, "lab_no");
                    pstmt.setString(3, lab_no);
                    pstmt.executeUpdate();
                    pstmt.clearParameters();

                    logger.debug("Inserting into measurementsExt id "+insertID+ " abnormal "+ abnormal);
                    pstmt.setString(1, insertID);
                    pstmt.setString(2, "abnormal");
                    pstmt.setString(3, abnormal);
                    pstmt.executeUpdate();
                    pstmt.clearParameters();

                    logger.debug("Inserting into measurementsExt id "+insertID+ " identifier "+ identifier);
                    pstmt.setString(1, insertID);
                    pstmt.setString(2, "identifier");
                    pstmt.setString(3, identifier);
                    pstmt.executeUpdate();
                    pstmt.clearParameters();

                    logger.debug("Inserting into measurementsExt id "+insertID+ " name "+ name);
                    pstmt.setString(1, insertID);
                    pstmt.setString(2, "name");
                    pstmt.setString(3, name);
                    pstmt.executeUpdate();
                    pstmt.clearParameters();

                    logger.debug("Inserting into measurementsExt id "+insertID+ " labname "+ labname);
                    pstmt.setString(1, insertID);
                    pstmt.setString(2, "labname");
                    pstmt.setString(3, labname);
                    pstmt.executeUpdate();
                    pstmt.clearParameters();

                    logger.debug("Inserting into measurementsExt id "+insertID+ " accession "+ accession);
                    pstmt.setString(1, insertID);
                    pstmt.setString(2, "accession");
                    pstmt.setString(3, accession);
                    pstmt.executeUpdate();
                    pstmt.clearParameters();

                    logger.debug("Inserting into measurementsExt id "+insertID+ " request_datetime "+ req_datetime);
                    pstmt.setString(1, insertID);
                    pstmt.setString(2, "request_datetime");
                    pstmt.setString(3, req_datetime);
                    pstmt.executeUpdate();
                    pstmt.clearParameters();

                    logger.debug("Inserting into measurementsExt id "+insertID+ " datetime "+ datetime);
                    pstmt.setString(1, insertID);
                    pstmt.setString(2, "datetime");
                    pstmt.setString(3, datetime);
                    pstmt.executeUpdate();
                    pstmt.clearParameters();

                    if (olis_status!=null && olis_status.length()>0) {
                        logger.debug("Inserting into measurementsExt id "+insertID+ " olis_status "+ olis_status);
                        pstmt.setString(1, insertID);
                        pstmt.setString(2, "olis_status");
                        pstmt.setString(3, olis_status);
                        pstmt.executeUpdate();
                        pstmt.clearParameters();
                    }

		    if (unit!=null && unit.length()>0) {
			logger.debug("Inserting into measurementsExt id "+insertID+ " unit "+ unit);
			pstmt.setString(1, insertID);
			pstmt.setString(2, "unit");
			pstmt.setString(3, unit);
			pstmt.executeUpdate();
			pstmt.clearParameters();
		    }

		    if (refRange[0].length()>0) {
			logger.debug("Inserting into measurementsExt id "+insertID+ " range "+ refRange[0]);
			pstmt.setString(1, insertID);
			pstmt.setString(2, "range");
			pstmt.setString(3, refRange[0]);
			pstmt.executeUpdate();
			pstmt.clearParameters();
		    } else {
			if (refRange[1].length()>0) {
			    logger.debug("Inserting into measurementsExt id "+insertID+ " minimum "+ refRange[1]);
			    pstmt.setString(1, insertID);
			    pstmt.setString(2, "minimum");
			    pstmt.setString(3, refRange[1]);
			    pstmt.executeUpdate();
			    pstmt.clearParameters();
			}

			
			

					// add other_id to measurementsExt so that annotation can be linked up through casemgmt_note_link
					logger.debug("Inserting into measurementsExt id "+insertID+ " other_id "+ i+"-"+j);
					pstmt.setString(1, insertID);
					pstmt.setString(2, "other_id");
					pstmt.setString(3, i+"-"+j);
					pstmt.executeUpdate();
					pstmt.clearParameters();

					pstmt.close();

				}
			}
            }

		}catch(Exception e){
			logger.error("Exception in HL7 populateMeasurementsTable", e);
		}

	}

	public static String getMatchingLabs(String lab_no){
		String sql = "SELECT a.lab_no, a.obr_date, b.obr_date as labDate FROM hl7TextInfo a, hl7TextInfo b " +
				"WHERE a.accessionNum !='' AND a.accessionNum=b.accessionNum AND b.lab_no='"+lab_no+"' " + 
				"ORDER BY a.obr_date, a.lab_no";
		String ret = "";
		int monthsBetween = 0;

		try{

			ResultSet rs = DBHandler.GetSQL(sql);


			while(rs.next()) {

				//Accession numbers may be recycled, accession
				//numbers for a lab should have lab dates within less than 4
				//months of eachother even this is a large timespan
				Date dateA = UtilDateUtilities.StringToDate(oscar.Misc.getString(rs, "obr_date"), "yyyy-MM-dd hh:mm:ss");
				Date dateB = UtilDateUtilities.StringToDate(oscar.Misc.getString(rs, "labDate"), "yyyy-MM-dd hh:mm:ss");
				if (dateA.before(dateB)){
					monthsBetween = UtilDateUtilities.getNumMonths(dateA, dateB);
				}else{
					monthsBetween = UtilDateUtilities.getNumMonths(dateB, dateA);
				}
				logger.debug("monthsBetween: "+monthsBetween);
				logger.debug("lab_no: "+oscar.Misc.getString(rs, "lab_no")+" lab: "+lab_no);
				if (monthsBetween < 4){
					if(ret.equals(""))
						ret = oscar.Misc.getString(rs, "lab_no");
					else
						ret = ret+","+oscar.Misc.getString(rs, "lab_no");
				}
			}
			rs.close();
		}catch(Exception e){
			logger.error("Exception in HL7 getMatchingLabs: ", e);
		}
		if (ret.equals(""))
			return(lab_no);
		else
			return(ret);
	}

	/**
	 *Populates ArrayList with labs attached to a consultation request
	 */
	public static ArrayList<LabResultData> populateHL7ResultsData(String demographicNo, String consultationId, boolean attached) {
		String sql = "SELECT hl7.label,hl7.lab_no, hl7.obr_date, hl7.discipline, hl7.accessionNum, hl7.final_result_count, patientLabRouting.id " +
		"FROM hl7TextInfo hl7, patientLabRouting " +
		"WHERE patientLabRouting.lab_no = hl7.lab_no "+
		"AND patientLabRouting.lab_type = 'HL7' AND patientLabRouting.demographic_no=" + demographicNo+" GROUP BY hl7.lab_no";

		String attachQuery = "SELECT consultdocs.document_no FROM consultdocs, patientLabRouting " +
		"WHERE patientLabRouting.id = consultdocs.document_no AND " +
		"consultdocs.requestId = " + consultationId + " AND consultdocs.doctype = 'L' AND consultdocs.deleted IS NULL ORDER BY consultdocs.document_no";

		ArrayList<LabResultData> labResults = new ArrayList<LabResultData>();
		ArrayList<LabResultData> attachedLabs = new ArrayList<LabResultData>();
		try {


			ResultSet rs = DBHandler.GetSQL(attachQuery);
			while(rs.next()) {
				LabResultData lbData = new LabResultData(LabResultData.HL7TEXT);
				lbData.labPatientId = oscar.Misc.getString(rs, "document_no");
				attachedLabs.add(lbData);
			}
			rs.close();

			LabResultData lbData = new LabResultData(LabResultData.HL7TEXT);
			LabResultData.CompareId c = lbData.getComparatorId();
			rs = DBHandler.GetSQL(sql);

			while(rs.next()){

				lbData.segmentID = oscar.Misc.getString(rs, "lab_no");
				lbData.labPatientId = oscar.Misc.getString(rs, "id");
				lbData.dateTime = oscar.Misc.getString(rs, "obr_date");
				lbData.discipline = oscar.Misc.getString(rs, "discipline");
				lbData.accessionNumber = oscar.Misc.getString(rs, "accessionNum");
				lbData.finalResultsCount = rs.getInt("final_result_count");
				lbData.label = oscar.Misc.getString(rs,"label");

				if( attached && Collections.binarySearch(attachedLabs, lbData, c) >= 0 )
					labResults.add(lbData);
				else if( !attached && Collections.binarySearch(attachedLabs, lbData, c) < 0 )
					labResults.add(lbData);

				lbData = new LabResultData(LabResultData.HL7TEXT);
			}
			rs.close();
		}catch(Exception e){
			logger.error("exception in HL7Populate",e);
		}
		return labResults;
	}

	public static ArrayList<LabResultData> getNotAckLabsFromLabNos(List<String> labNos){
		ArrayList<LabResultData> ret=new ArrayList<LabResultData>();
		LabResultData lrd=new LabResultData();
		for(String labNo:labNos){
			lrd=getNotAckLabResultDataFromLabNo(labNo);
			ret.add(lrd);
		}
		return ret;
	}

	public static LabResultData getNotAckLabResultDataFromLabNo(String labNo){
		LabResultData lbData = new LabResultData(LabResultData.HL7TEXT);
		String sql = "";
		try {


			// note to self: lab reports not found in the providerLabRouting table will not show up - need to ensure every lab is entered in providerLabRouting, with '0'
			// for the provider number if unable to find correct provider

			sql = "select info.lab_no, info.sex, info.health_no, info.result_status, info.obr_date, info.priority, info.requesting_client, info.discipline, info.last_name, "
				+ "info.first_name, info.report_status, info.accessionNum, info.final_result_count " +
				"from hl7TextInfo info " +
				" where info.lab_no = "+labNo +" ORDER BY info.obr_date DESC";

			logger.debug(sql);
			ResultSet rs = DBHandler.GetSQL(sql);
			if(rs.first()){

				if (logger.isDebugEnabled())
				{
					int columns=rs.getMetaData().getColumnCount();
					StringBuilder sb=new StringBuilder();
					for (int i=0; i<columns; i++)
					{
						sb.append(rs.getString(i+1));
						sb.append(", ");
					}
					logger.debug("Record found : "+sb.toString());
				}


				lbData.labType = LabResultData.HL7TEXT;
				lbData.segmentID = oscar.Misc.getString(rs, "lab_no");
				//check if any demographic is linked to this lab
				if(lbData.isMatchedToPatient()){
					//get matched demographic no
					String sql2="select * from patientLabRouting plr where plr.lab_no="+Integer.parseInt(lbData.segmentID)+" and plr.lab_type='"+lbData.labType+"'";
					logger.debug("sql2="+sql2);
					ResultSet rs2=DBHandler.GetSQL(sql2);
					if(rs2.next())
						lbData.setLabPatientId(oscar.Misc.getString(rs2, "demographic_no"));
					else
						lbData.setLabPatientId("-1");
				}else{
					lbData.setLabPatientId("-1");
				}
				lbData.acknowledgedStatus ="U";
				lbData.accessionNumber = oscar.Misc.getString(rs, "accessionNum");
				lbData.healthNumber = oscar.Misc.getString(rs, "health_no");
				lbData.patientName = oscar.Misc.getString(rs, "last_name")+", "+oscar.Misc.getString(rs, "first_name");
				lbData.sex = oscar.Misc.getString(rs, "sex");

				lbData.resultStatus = oscar.Misc.getString(rs, "result_status");
				if (lbData.resultStatus.equals("A"))
					lbData.abn = true;

				lbData.dateTime = oscar.Misc.getString(rs, "obr_date");

				//priority
				String priority = oscar.Misc.getString(rs, "priority");

				if(priority != null && !priority.equals("")){
					switch ( priority.charAt(0) ) {
					case 'C' : lbData.priority = "Critical"; break;
					case 'S' : lbData.priority = "Stat/Urgent"; break;
					case 'U' : lbData.priority = "Unclaimed"; break;
					case 'A' : lbData.priority = "ASAP"; break;
					case 'L' : lbData.priority = "Alert"; break;
					default: lbData.priority = "Routine"; break;
					}
				}else{
					lbData.priority = "----";
				}

				lbData.requestingClient = oscar.Misc.getString(rs, "requesting_client");
				lbData.reportStatus =  oscar.Misc.getString(rs, "report_status");

				// the "C" is for corrected excelleris labs
				if (lbData.reportStatus != null && (lbData.reportStatus.equals("F") || lbData.reportStatus.equals("C"))){
					lbData.finalRes = true;
				}else{
					lbData.finalRes = false;
				}

				lbData.discipline = oscar.Misc.getString(rs, "discipline");
				lbData.finalResultsCount = rs.getInt("final_result_count");

			}
			rs.close();
		}catch(Exception e){
			logger.error("exception in getNotAckLabResultDataFromLabNo:", e);
		}
		return lbData;
	}

	public static ArrayList<LabResultData> populateHl7ResultsData(String providerNo, String demographicNo, String patientFirstName, String patientLastName, String patientHealthNumber, String status, List<String> providerNoArr) {

		if ( providerNo == null) { providerNo = ""; }
		if ( patientFirstName == null) { patientFirstName = ""; }
		if ( patientLastName == null) { patientLastName = ""; }
		if ( status == null ) { status = ""; }

		String providerNoList = "";
		
        if(providerNoArr != null && providerNoArr.size() > 0){
        	providerNoList = StringUtils.join(providerNoArr, ",");
        }
		
		patientHealthNumber=StringUtils.trimToNull(patientHealthNumber);

		ArrayList<LabResultData> labResults =  new ArrayList<LabResultData>();
		String sql = "";
		try {

			if ( demographicNo == null) {
				// note to self: lab reports not found in the providerLabRouting table will not show up - need to ensure every lab is entered in providerLabRouting, with '0'
				// for the provider number if unable to find correct provider

				sql = "select info.label,info.lab_no, info.sex, info.health_no, info.result_status, info.obr_date, info.priority, info.requesting_client, info.requesting_client_no, info.discipline, info.last_name, info.first_name, info.report_status, info.accessionNum, info.final_result_count, providerLabRouting.status " +
				"from hl7TextInfo info, providerLabRouting " +
				" where info.lab_no = providerLabRouting.lab_no "+
				" AND providerLabRouting.status like '%"+status+"%' AND providerLabRouting.provider_no like '"+(providerNo.equals("")?"%":providerNo)+"'" +
				" AND providerLabRouting.lab_type = 'HL7' " +
				" AND info.first_name like '"+patientFirstName+"%' AND info.last_name like '"+patientLastName+"%'";

				if (patientHealthNumber!=null) sql=sql+" AND info.health_no like '%"+patientHealthNumber+"%'";

				if(providerNoList != ""){
                	sql = sql + "AND providerLabRouting.provider_no IN ("+providerNoList+")";
                }
				
				sql=sql+" ORDER BY info.lab_no DESC";

			} else {

				sql = "select info.label,info.lab_no, info.sex, info.health_no, info.result_status, info.obr_date, info.priority, info.requesting_client, info.discipline, info.last_name, info.first_name, info.report_status, info.accessionNum, info.final_result_count " +
				"from hl7TextInfo info, patientLabRouting " +
				" where info.lab_no = patientLabRouting.lab_no "+
				" AND patientLabRouting.lab_type = 'HL7' AND patientLabRouting.demographic_no='"+demographicNo+"' ORDER BY info.lab_no DESC";
			}

			logger.debug(sql);
			ResultSet rs = DBHandler.GetSQL(sql);
			while(rs.next()){

				if (logger.isDebugEnabled())
				{
					int columns=rs.getMetaData().getColumnCount();
					StringBuilder sb=new StringBuilder();
					for (int i=0; i<columns; i++)
					{
						sb.append(rs.getString(i+1));
						sb.append(", ");
					}
					logger.debug("Record found : "+sb.toString());
				}

				LabResultData lbData = new LabResultData(LabResultData.HL7TEXT);
				lbData.labType = LabResultData.HL7TEXT;
				lbData.segmentID = oscar.Misc.getString(rs, "lab_no");
				//check if any demographic is linked to this lab
				if(lbData.isMatchedToPatient()){
					//get matched demographic no
					String sql2="select * from patientLabRouting plr where plr.lab_no="+Integer.parseInt(lbData.segmentID)+" and plr.lab_type='"+lbData.labType+"'";
					logger.debug("sql2="+sql2);
					ResultSet rs2=DBHandler.GetSQL(sql2);
					if(rs2.next())
						lbData.setLabPatientId(oscar.Misc.getString(rs2, "demographic_no"));
					else
						lbData.setLabPatientId("-1");
				}else{
					lbData.setLabPatientId("-1");
				}

				if (demographicNo == null && !providerNo.equals("0")) {
					lbData.acknowledgedStatus = oscar.Misc.getString(rs, "status");
				} else {
					lbData.acknowledgedStatus ="U";
				}

				lbData.accessionNumber = oscar.Misc.getString(rs, "accessionNum");
				lbData.healthNumber = oscar.Misc.getString(rs, "health_no");
				lbData.patientName = oscar.Misc.getString(rs, "last_name")+", "+oscar.Misc.getString(rs, "first_name");
				lbData.sex = oscar.Misc.getString(rs, "sex");
				lbData.label = oscar.Misc.getString(rs, "label");

				lbData.resultStatus = oscar.Misc.getString(rs, "result_status");
				if (lbData.resultStatus.equals("A"))
					lbData.abn = true;

				lbData.dateTime = oscar.Misc.getString(rs, "obr_date");

				//priority
				String priority = oscar.Misc.getString(rs, "priority");

				if(priority != null && !priority.equals("")){
					switch ( priority.charAt(0) ) {
					case 'C' : lbData.priority = "Critical"; break;
					case 'S' : lbData.priority = "Stat/Urgent"; break;
					case 'U' : lbData.priority = "Unclaimed"; break;
					case 'A' : lbData.priority = "ASAP"; break;
					case 'L' : lbData.priority = "Alert"; break;
					default: lbData.priority = "Routine"; break;
					}
				}else{
					lbData.priority = "----";
				}

				lbData.requestingClient = oscar.Misc.getString(rs, "requesting_client");
				lbData.reportStatus =  oscar.Misc.getString(rs, "report_status");

				// the "C" is for corrected excelleris labs
				if (lbData.reportStatus != null && (lbData.reportStatus.equals("F") || lbData.reportStatus.equals("C"))){
					lbData.finalRes = true;
				}else{
					lbData.finalRes = false;
				}

				lbData.discipline = oscar.Misc.getString(rs, "discipline");
				lbData.finalResultsCount = rs.getInt("final_result_count");
				labResults.add(lbData);
			}
			rs.close();
		}catch(Exception e){
			logger.error("exception in Hl7Populate:", e);
		}
		return labResults;
	}

	public static ArrayList<LabResultData> populateHl7ResultsData(String providerNo, String demographicNo, String patientFirstName, String patientLastName, String patientHealthNumber,
			String status, boolean isPaged, Integer page, Integer pageSize, boolean mixLabsAndDocs, Boolean isAbnormal, List<String> providerNoArr, Boolean neverAcknowledgedItems) {

		boolean qp_provider_no = false;
		boolean qp_status = false;
		boolean qp_first_name = false;
		boolean qp_last_name = false;
		boolean qp_hin = false;
		boolean qp_demographic_no = false;
		boolean qp_page = false;
		int qp_index = 1;

		if ( providerNo == null) { providerNo = ""; }
		boolean searchProvider = !"-1".equals(providerNo) && !"".equals(providerNo);
		if ( patientFirstName == null) { patientFirstName = ""; }
		if ( patientLastName == null) { patientLastName = ""; }
		if ( patientHealthNumber == null) { patientHealthNumber = ""; }
		if ( status == null || "U".equals(status) ) { status = ""; }

		boolean patientSearch = !"".equals(patientFirstName)
		|| !"".equals(patientLastName)
		|| !"".equals(patientHealthNumber);

		ArrayList<LabResultData> labResults =  new ArrayList<LabResultData>();
		String providerNoList = "";
        if(providerNoArr != null && providerNoArr.size() > 0){
        	providerNoList = StringUtils.join(providerNoArr, ",");
        }
		String sql = "";
		try {
			// note to self: lab reports not found in the providerLabRouting table will not show up - need to ensure every lab is entered in providerLabRouting, with '0'
			// for the provider number if unable to find correct provider

			
			Connection c  = DbConnectionFilter.getThreadLocalDbConnection();
			PreparedStatement ps;

			sql = "SELECT * FROM (SELECT  hl7.label, "
				+ "		hl7.lab_no, "
				+ "		hl7.sex, "
				+ "		hl7.health_no, "
				+ "		hl7.result_status, "
				+ "		hl7.obr_date, "
				+ "		hl7.priority, "
				+ "		hl7.requesting_client, "
				+ "		hl7.requesting_client_no, "
				+ "		hl7.discipline, "
				+ "		hl7.last_name, "
				+ "		hl7.first_name, "
				+ "		hl7.report_status, "
				+ "		hl7.accessionNum, "
				+ "		hl7.final_result_count, "
				+ "		proLR.status "
				+ "FROM hl7TextInfo hl7 ";
			
			if(neverAcknowledgedItems && "N".equals(status)){
				sql = sql + "LEFT JOIN (" +
						    "    SELECT * FROM (" +
						    "        SELECT plr.*" +
						    "        FROM providerLabRouting plr" + 
						    "        GROUP BY lab_no, status" +
						    "    ) lab_status_grouped GROUP BY lab_no HAVING count(lab_no) = 1" +
						    ") proLR ON (hl7.lab_no = proLR.lab_no AND proLR.lab_type = 'HL7') ";
			}else{
				sql = sql + "LEFT JOIN providerLabRouting proLR ON (hl7.lab_no = proLR.lab_no AND proLR.lab_type = 'HL7') ";
			}
			
			sql = sql + "LEFT JOIN patientLabRouting patLR ON (hl7.lab_no = patLR.lab_no AND patLR.lab_type = 'HL7') "
				+ "LEFT JOIN demographic d ON (patLR.demographic_no = d.demographic_no ) "
				+ "WHERE true ";

			if ("-1".equals(providerNo) || "".equals(providerNo)) {
				// any provider
			} else if ("0".equals(providerNo)) {
				// unclaimed
				sql = sql + "AND (proLR.provider_no = '0') ";
			} else {
				sql = sql + "AND (proLR.provider_no = ?) ";
				qp_provider_no = true;
			}
			
			if(providerNoList != null && !providerNoList.equals("")){
				sql = sql + "AND (proLR.provider_no IN ("+providerNoList+") ) ";
			}

			if ("N".equals(status) || "A".equals(status) || "F".equals(status)) {
				sql = sql + "AND (proLR.status = ?) ";
				qp_status = true;
			}

			if (mixLabsAndDocs == false) {
				sql = sql + "AND (proLR.lab_type = 'HL7') ";
			}

			

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

			if ("0".equals(demographicNo)) {
				// There seems to be an inconsistency where a ctl_document.module_id of either 0 or -1
				// (when module='demographic') indicates that the document is unassigned hence the 
				// checking for either in the following condition
				sql = sql + "AND (d.demographic_no IN ('0', '-1') OR d.demographic_no IS NULL) ";
			} else if (demographicNo != null && !"".equals(demographicNo)) {
				sql = sql + "AND (d.demographic_no = ?) ";
				qp_demographic_no = true;
			}
			
			sql = sql + " ORDER BY hl7.obr_date desc, hl7.lab_no desc) ordered_labs ";
			sql = sql + " GROUP BY COALESCE( accessionNum, lab_no ) ";
			sql = sql + " ORDER BY obr_date desc, lab_no desc ";
			
			// Some labs sometimes become normal or abnormal. Let's just use the last
			// lab result in checking if it's normal or abnormal
			if (isAbnormal != null) {
				if (isAbnormal) {
					sql = sql + "HAVING (ordered_labs.result_status = 'A') ";
				} else {
					sql = sql + "HAVING (ordered_labs.result_status IS NULL OR ordered_labs.result_status != 'A') ";
				}
			}

			if (isPaged) {
				sql = sql + "LIMIT ?,?";
				qp_page = true;
			}

			ps = c.prepareStatement(sql);

			if (qp_provider_no) { ps.setString(qp_index++, providerNo); }
			if (qp_status) { ps.setString(qp_index++, status); }
			if (qp_first_name) { ps.setString(qp_index++, "%" + patientFirstName + "%"); }
			if (qp_last_name) { ps.setString(qp_index++, "%" + patientLastName + "%"); }
			if (qp_hin) { ps.setString(qp_index++, "%" + patientHealthNumber + "%"); }
			if (qp_demographic_no) { ps.setString(qp_index++, demographicNo); }
			if (qp_page) {
				ps.setInt(qp_index++, page * pageSize);
				ps.setInt(qp_index++, pageSize);
			}

			
			logger.info("provider list: "+providerNoList);

			logger.info(sql);
			
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()){

				LabResultData lbData = new LabResultData(LabResultData.HL7TEXT);
				lbData.labType = LabResultData.HL7TEXT;
				lbData.segmentID = rs.getString("lab_no");
				

				if (demographicNo == null && !providerNo.equals("0")) {
					lbData.acknowledgedStatus = rs.getString("status");
				} else {
					lbData.acknowledgedStatus ="U";
				}

				lbData.accessionNumber = rs.getString("accessionNum");
				lbData.healthNumber = rs.getString("health_no");
				lbData.patientName = rs.getString("last_name")+", "+rs.getString("first_name");
				lbData.sex = rs.getString("sex");
				lbData.label = rs.getString("label");

				lbData.resultStatus = rs.getString("result_status");
				if (lbData.resultStatus != null && lbData.resultStatus.equals("A"))
					lbData.abn = true;

				lbData.dateTime = rs.getString("obr_date");

				//priority
				String priority = rs.getString("priority");

				if(priority != null && !priority.equals("")){
					switch ( priority.charAt(0) ) {
					case 'C' : lbData.priority = "Critical"; break;
					case 'S' : lbData.priority = "Stat/Urgent"; break;
					case 'U' : lbData.priority = "Unclaimed"; break;
					case 'A' : lbData.priority = "ASAP"; break;
					case 'L' : lbData.priority = "Alert"; break;
					default: lbData.priority = "Routine"; break;
					}
				}else{
					lbData.priority = "----";
				}

				lbData.requestingClient = rs.getString("requesting_client");
				lbData.requestingClientNo = rs.getString("requesting_client_no");
				lbData.reportStatus =  rs.getString("report_status");

				// the "C" is for corrected excelleris labs
				if (lbData.reportStatus != null && (lbData.reportStatus.equals("F") || lbData.reportStatus.equals("C"))){
					lbData.finalRes = true;
				}else if (lbData.reportStatus != null && lbData.reportStatus.equals("X")){
					lbData.cancelledReport = true;
				}else{
					lbData.finalRes = false;
				}

				lbData.discipline = rs.getString("discipline");
				lbData.finalResultsCount = rs.getInt("final_result_count");
				labResults.add(lbData);
			}
			rs.close();
		}catch(Exception e){
			logger.error("exception in Hl7Populate:", e);
		}
		return labResults;
	}

	private static String[] splitRefRange(String refRangeTxt) {
		refRangeTxt = refRangeTxt.trim();
		String[] refRange = {"","",""};
		String numeric = "-. 0123456789";
		boolean textual = false;
		if (refRangeTxt==null || refRangeTxt.length()==0) return refRange;

		for (int i=0; i<refRangeTxt.length(); i++) {
			if (!numeric.contains(refRangeTxt.subSequence(i,i+1))) {
				if (i>0 || (refRangeTxt.charAt(i)!='>' && refRangeTxt.charAt(i)!='<')) {
					textual = true;
					break;
				}
			}
		}
		if (textual) {
			refRange[0] = refRangeTxt;
		} else {
			if (refRangeTxt.charAt(0)=='>') {
				refRange[1] = refRangeTxt.substring(1).trim();
			} else if (refRangeTxt.charAt(0)=='<') {
				refRange[2] = refRangeTxt.substring(1).trim();
			} else {
				String[] tmp = refRangeTxt.split("-");
				if (tmp.length==2) {
					refRange[1] = tmp[0].trim();
					refRange[2] = tmp[1].trim();
				} else {
					refRange[0] = refRangeTxt;
				}
			}
		}
		return refRange;
	}
}
