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


package oscar.oscarLab.ca.on;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtils;

import oscar.oscarDB.DBHandler;
import oscar.oscarLab.ca.bc.PathNet.PathnetResultsData;
import oscar.oscarLab.ca.on.CML.CMLLabTest;
import oscar.oscarLab.ca.on.Spire.SpireLabTest;
import oscar.util.StringUtils;
import oscar.util.UtilDateUtilities;


/**
 *
 * @author Jay Gallagher
 */
public class LabResultData implements Comparable{

	Logger logger = MiscUtils.getLogger();

	public static String CML = "CML";
	public static String EPSILON = "Epsilon";
	public static String MDS = "MDS";
	public static String EXCELLERIS = "BCP"; //EXCELLERIS
	public static String DOCUMENT = "DOC"; //INTERNAL DOCUMENT
	public static String HRM = "HRM";
	public static String Spire = "Spire";

	//HL7TEXT handles all messages types recieved as a hl7 formatted string
	public static String HL7TEXT = "HL7";

	public String segmentID;
	public String labPatientId;
	public String acknowledgedStatus;

	public String accessionNumber;
	public String healthNumber;
	public String patientName;
	public String sex;
	public String resultStatus;
	public int finalResultsCount = 0;
	public String dateTime;
	private Date dateTimeObr;
	public String priority;
	public String requestingClient;
	public String requestingClientNo;
	public String discipline;
	public String reportStatus;
	public boolean abn = false;
	public String labType; // ie CML or MDS
	public boolean finalRes = true;
	public boolean isMatchedToPatient = true;
	public String multiLabId;
	public String label;
	public boolean cancelledReport = false;
	public String uploadedBy = null;

	private Integer ackCount = null;
	private Integer multiplAckCount = null;
	private Integer remoteFacilityId=null;

	private ArrayList<Integer> duplicateLabIds=new ArrayList<Integer>();

	public LabResultData() {
	}

	public LabResultData(String labT) {
		if (CML.equals(labT)){
			labType = CML;
		}else if (MDS.equals(labT)){
			labType = MDS;
		}else if (EXCELLERIS.equals(labT)){
			labType = EXCELLERIS;
		}else if (HL7TEXT.equals(labT)){
			labType = HL7TEXT;
		}else if(EPSILON.equals(labT)){
			labType = EPSILON;
		}else if (HRM.equals(labT)) {
			labType = HRM;
		}else if (Spire.equals(labT)) {
			labType = Spire;
		}

	}

	public String getLabPatientId(){
		return this.labPatientId;
	}
	public void setLabPatientId(String lpi){
		this.labPatientId=lpi;
	}
	public String getSegmentID(){
		return this.segmentID;
	}
	public void setSegmentID(String sid){
		this.segmentID=sid;
	}

	public boolean isAbnormal(){
		if (EXCELLERIS.equals(this.labType)){
			//logger.debug("excelleris is doc type");
			PathnetResultsData prd = new PathnetResultsData();
			if (prd.findPathnetAdnormalResults(this.segmentID) > 0){
				this.abn= true;
			}
		}else if(CML.equals(this.labType)||EPSILON.equals(this.labType)){

			//logger.debug("cml is doc type");
			CMLLabTest cml = new CMLLabTest();
			if (cml.findCMLAdnormalResults(this.segmentID) > 0){
				this.abn= true;
			}
		}

		return abn ;
	}


	public boolean isFinal(){ return finalRes ;}

	public boolean isReportCancelled(){ return cancelledReport ;}

	public boolean isMDS(){
		boolean ret = false;
		if (MDS.equals(labType)){ ret = true; }
		return ret;
	}

	public boolean isCML(){
		boolean ret = false;
		if (CML.equals(labType)){ ret = true; }
		return ret;
	}

	public boolean isHL7TEXT(){
		boolean ret = false;
		if (HL7TEXT.equals(labType)){ ret = true; }
		return ret;
	}

	public boolean isDocument(){
		boolean ret = false;
		if (DOCUMENT.equals(labType)){ ret = true; }
		return ret;
	}

	public boolean isHRM(){
		boolean ret = false;
		if(HRM.equals(labType)){ ret = true; }
		return ret;
	}
	////

	public static boolean isMDS(String type){
		boolean ret = false;
		if (MDS.equals(type)){ ret = true; }
		return ret;
	}

	public static boolean isCML(String type){
		boolean ret = false;
		if (CML.equals(type)){ ret = true; }
		return ret;
	}

	public static boolean isHL7TEXT(String type){
		boolean ret = false;
		if (HL7TEXT.equals(type)){ ret = true; }
		return ret;
	}

	public static boolean isDocument(String type){
		boolean ret = false;
		if (DOCUMENT.equals(type)){ ret = true; }
		return ret;
	}

	public static boolean isHRM(String type){
		boolean ret = false;
		if (HRM.equals(type)){ ret = true; }
		return ret;
	}


	public String getDiscipline(){
		if (CML.equals(this.labType)){
			CMLLabTest cml = new CMLLabTest();
			this.discipline = cml.getDiscipline(this.segmentID);
		}else if (EXCELLERIS.equals(this.labType)){
			PathnetResultsData prd = new PathnetResultsData();
			this.discipline = prd.findPathnetDisipline(this.segmentID);
		}else if (Spire.equals(this.labType)){
			SpireLabTest spire = new SpireLabTest();
			this.discipline = spire.getDiscipline(this.segmentID);
		}
		return this.discipline;
	}

	public String getPatientName(){
		return this.patientName;
	}

	public String getHealthNumber(){
		return this.healthNumber;
	}

	public String getSex(){
		return this.sex;
	}

	public boolean isMatchedToPatient(){
		//       if (EXCELLERIS.equals(this.labType)){
			//          PathnetResultsData prd = new PathnetResultsData();
			//          this.isMatchedToPatient = prd.isLabLinkedWithPatient(this.segmentID);
			//       }
		CommonLabResultData commonLabResultData = new CommonLabResultData();
		logger.debug("in ismatchedtopatient, "+this.segmentID+"--"+this.labType);
		if(this.labType.equals("DOC")){
			this.isMatchedToPatient=commonLabResultData.isDocLinkedWithPatient(this.segmentID,this.labType);
		}else if(this.labType.equals("HRM")){
			this.isMatchedToPatient = commonLabResultData.isHRMLinkedWithPatient(this.segmentID, this.labType);
		}else{
			this.isMatchedToPatient = commonLabResultData.isLabLinkedWithPatient(this.segmentID,this.labType);
		}
		return this.isMatchedToPatient;
	}


	public String getDateTime(){
		/* if (EXCELLERIS.equals(this.labType)){
            PathnetResultsData prd = new PathnetResultsData();
            this.dateTime = prd.findPathnetObservationDate(this.segmentID);
        }*/
        if ( this.dateTime == null ) {
            StackTraceElement stackTraceOne = new Throwable().getStackTrace()[1];
            String filename = stackTraceOne.getFileName();
            String lineNumber = new Integer(stackTraceOne.getLineNumber()).toString();
            logger.warn(filename + ":" + lineNumber + " this.dateTime is NULL. This is probably going to cause an exception somewhere in the code soon.");
        }
		return this.dateTime;
	}

	public String getAcknowledgedStatus(){
		return this.acknowledgedStatus;
	}

	public void setAcknowledgedStatus(String as){
		this.acknowledgedStatus=as;
	}
	public String getReportStatus(){
		/* if (EXCELLERIS.equals(this.labType)){
            PathnetResultsData prd = new PathnetResultsData();
            this.reportStatus = prd.findPathnetStatus(this.segmentID);
        }*/
		return this.reportStatus;
	}

	public String getPriority(){
		return this.priority;
	}



	public String getRequestingClient(){
		/*if (EXCELLERIS.equals(this.labType)){
            PathnetResultsData prd = new PathnetResultsData();
            this.requestingClient = prd.findPathnetOrderingProvider(this.segmentID);
        }*/
		return this.requestingClient;
	}

	public String getRequestingClientNo(){
		return this.requestingClientNo;
	}

	public Date getDateObj(){
		if (EXCELLERIS.equals(this.labType)){
			this.dateTimeObr = UtilDateUtilities.getDateFromString(this.getDateTime(), "yyyy-MM-dd HH:mm:ss");
		}else if(HL7TEXT.equals(this.labType) || Spire.equals(this.labType)){
			this.dateTimeObr = UtilDateUtilities.getDateFromString(this.getDateTime(), "yyyy-MM-dd HH:mm:ss");
		}else if(CML.equals(this.labType)){
			String date="";
			String sql = "select collection_date from labReportInformation, labPatientPhysicianInfo where labPatientPhysicianInfo.id = '"+this.segmentID+"' and labReportInformation.id = labPatientPhysicianInfo.labReportInfo_id ";
			try{

				ResultSet rs = DBHandler.GetSQL(sql);
				if(rs.next()){
					date=oscar.Misc.getString(rs, "collection_date");
				}
				rs.close();
			}catch(Exception e){
				logger.error("Error in getDateObj (CML message)", e);
			}
			this.dateTimeObr = UtilDateUtilities.getDateFromString(date, "yyyy-MM-dd");
		}

		return this.dateTimeObr;
	}

	public void setDateObj(Date d){
		this.dateTimeObr = d;
	}

	public int compareTo(Object object) {
		LabResultData labData = (LabResultData) object;
		
		int ret = 0;
		if (this.getDateObj() != null) {
			
			Date labDataDate = labData.getDateObj();
			if (labDataDate!= null && this.dateTimeObr.after(labDataDate)){
				ret = -1;
			}else if(labDataDate!= null && this.dateTimeObr.before(labDataDate)){
				ret = 1;
			}else if(this.finalResultsCount > labData.finalResultsCount){
				ret = -1;
			}else if(this.finalResultsCount < labData.finalResultsCount){
				ret = 1;
			}
		}
		return ret;
	}

	public CompareId getComparatorId() {
		return new CompareId();
	}


	public class CompareId implements Comparator {

		public int compare( Object o1, Object o2 ) {
			LabResultData lab1 = (LabResultData)o1;
			LabResultData lab2 = (LabResultData)o2;

			int labPatientId1 = Integer.parseInt(lab1.labPatientId);
			int labPatientId2 = Integer.parseInt(lab2.labPatientId);

			if( labPatientId1 < labPatientId2 )
				return -1;
			else if( labPatientId1 > labPatientId2 )
				return 1;
			else
				return 0;
		}
	}

	public String getDisciplineDisplayString()
	{
		String temp;

		if ("REF_I12".equals(discipline)) temp="REFERRAL";
		else if (discipline!=null && discipline.startsWith("ORU_R01:")) return(discipline.substring("ORU_R01:".length()));
		else temp=discipline;

		return(StringUtils.maxLenString(temp, 13, 10, "..."));
	}


	public int getAckCount() {
		if (ackCount == null) {
			CommonLabResultData data = new CommonLabResultData();
			ackCount = data.getAckCount(this.segmentID, this.labType);
		}

		return (ackCount);
	}

	public void setAckCount(Integer ackCount) {
		this.ackCount = ackCount;
	}

	public int getMultipleAckCount() {
		if (multiplAckCount == null) {

			// String[] multiId = this.multiLabId.split(",");
			CommonLabResultData data = new CommonLabResultData();
			String[] multiId = data.getMatchingLabs(this.segmentID, this.labType).split(",");
			int count = 0;
			if (multiId.length == 1) {
				count = -1;
			} else {
				for (int i = 0; i < multiId.length; i++) {
					count = count + data.getAckCount(multiId[i], this.labType);
				}
			}
			multiplAckCount = count;
		}

		return(multiplAckCount);
	}

	public void setMultipleAckCount(Integer multipleAckCount)
	{
		this.multiplAckCount=multipleAckCount;
	}

	public Integer getRemoteFacilityId() {
		return (remoteFacilityId);
	}

	public void setRemoteFacilityId(Integer remoteFacilityId) {
		this.remoteFacilityId = remoteFacilityId;
	}

	public ArrayList<Integer> getDuplicateLabIds() {
		return (duplicateLabIds);
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getUploadedBy() {
		return uploadedBy;
	}

	public void setUploadedBy(String uploadedBy) {
		this.uploadedBy = uploadedBy;
	}
}
