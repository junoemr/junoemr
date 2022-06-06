package oscar.oscarLab.ca.all.parsers;
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
 * CDLHandler.java
 *
 * Created on June 4, 2007, 11:19 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */


import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Segment;
import ca.uhn.hl7v2.model.v23.message.ORU_R01;
import ca.uhn.hl7v2.model.v23.segment.OBR;
import ca.uhn.hl7v2.model.v23.segment.OBX;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.util.Terser;
import ca.uhn.hl7v2.validation.impl.NoValidation;
import org.apache.log4j.Logger;
import oscar.oscarLab.ca.all.parsers.messageTypes.ORU_R01MessageHandler;

import java.util.ArrayList;

public class CDLHandler extends ORU_R01MessageHandler
{

    Logger logger = Logger.getLogger(CDLHandler.class);

	private OBR obrseg = null;
	private OBX obxseg = null;
	private ca.uhn.hl7v2.model.v23.group.ORU_R01_PATIENT pat_23;
    protected ORU_R01 msg;

    /** Creates a new instance of CDLHandler */
    public CDLHandler(){
    }

    public void init(String hl7Body) throws HL7Exception {
        Parser p = new PipeParser();
        p.setValidationContext(new NoValidation());
        msg = (ORU_R01) p.parse(hl7Body.replaceAll( "\n", "\r\n" ));
		ca.uhn.hl7v2.model.v23.group.ORU_R01_RESPONSE pat_res = msg.getRESPONSE();
		ca.uhn.hl7v2.model.v23.group.ORU_R01_ORDER_OBSERVATION obsr =
			pat_res.getORDER_OBSERVATION();

		pat_23 = pat_res.getPATIENT();
		obrseg = obsr.getOBR();
		obxseg = obsr.getOBSERVATION().getOBX();

		this.message = msg;
		this.terser = new Terser(msg);
    }

    @Override
    public String preUpload(String hl7Message) throws HL7Exception
    {
        return hl7Message;
    }
    @Override
    public boolean canUpload()
    {
        return true;
    }
    @Override
    public void postUpload() {}

	@Override
    public String getMsgType() {
        return("CDL");
    }

    public String getMsgDate(){
        try {
            //return(formatDateTime(msg.getMSH().getDateTimeOfMessage().getTimeOfAnEvent().getValue()));
            return(formatDateTime(msg.getRESPONSE().getORDER_OBSERVATION(0).getOBR().getObservationDateTime().getTimeOfAnEvent().getValue()));
        } catch (Exception e) {
            logger.error("Could not retrieve message date", e);
            return("");
        }
    }

    public String getMsgPriority(){
        return("");
    }

    /**
     *  Methods to get information about the Observation Request
     */
    public int getOBRCount(){
        return(msg.getRESPONSE().getORDER_OBSERVATIONReps());
    }

    public int getOBXCount(int i){
        try{
            return(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATIONReps());
        }catch(Exception e){
            return(0);
        }
    }

    public String getOBRName(int i){
        try{
            return(getString(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBR().getUniversalServiceIdentifier().getText().getValue()));
        }catch(Exception e){
            return("");
        }
    }

    public String getTimeStamp(int i, int j){
        try{
            return(formatDateTime(getString(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBR().getObservationDateTime().getTimeOfAnEvent().getValue())));
        }catch(Exception e){
            return("");
        }
    }

    public boolean isOBXAbnormal(int i, int j){
        try{
            if(getOBXAbnormalFlag(i, j).equals("A")){
                return(true);
            }else{
                return(false);
            }

        }catch(Exception e){
            return(false);
        }
    }

    public String getOBXAbnormalFlag(int i, int j){
        try{
            return(getString(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getAbnormalFlags(0).getValue()));
        }catch(Exception e){
            return("");
        }
    }

    public String getObservationHeader(int i, int j){
        try{
            return (getString(Terser.get(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX(),4,0,1,1))+" "+
                    getString(Terser.get(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX(),4,0,2,1))+" "+
                    getString(Terser.get(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX(),4,0,3,1))).trim();
        }catch(Exception e){
            return("");
        }
    }

    public String getOBXIdentifier(int i, int j){
        try{
    		Segment obxSeg = msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX();
    		String subIdent = Terser.get(obxSeg, 3, 0, 1, 2) ;
    		if(subIdent != null){ //HACK: for gdml labs generated with SubmitLabByFormAction
    			return getString(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getObservationIdentifier().getIdentifier().getValue())+"&"+subIdent;
    		}
            return(getString(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getObservationIdentifier().getIdentifier().getValue()));
        }catch(Exception e){
            return("");
        }
    }

    public String getOBXValueType(int i, int j){
        try{
            return(getString(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getValueType().getValue()));
        }catch(Exception e){
            return("");
        }
    }

    public String getOBXName(int i, int j){
        try{
            return(getString(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getObservationIdentifier().getText().getValue()));
        }catch(Exception e){
            return("");
        }
    }

    public String getOBXResult(int i, int j){
        try{
            return(getString(Terser.get(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX(),5,0,1,1)));
        }catch(Exception e){
            return("");
        }
    }

    public String getOBXReferenceRange(int i, int j){
        try{
            return(getString(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getReferencesRange().getValue()));
        }catch(Exception e){
            return("");
        }
    }

    public String getOBXUnits(int i, int j){
        try{
            return(getString(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getUnits().getIdentifier().getValue()));
        }catch(Exception e){
            return("");
        }
    }

    public String getOBXResultStatus(int i, int j){
        String status = "";
        try{
            status = getString(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getObservResultStatus().getValue());
            if (status.equalsIgnoreCase("I"))
                status = "Pending";
            else if (status.equalsIgnoreCase("F"))
                status = "Final";
        }catch(Exception e){
            logger.error("Error retrieving obx result status", e);
            return status;
        }
        return status;
    }

    public int getOBXFinalResultCount(){
        int obrCount = getOBRCount();
        int obxCount;
        int count = 0;
        for (int i=0; i < obrCount; i++){
            obxCount = getOBXCount(i);
            for (int j=0; j < obxCount; j++){
                if (getOBXResultStatus(i, j).equals("Final"))
                    count++;
            }
        }
        return count;
    }

    /**
     *  Retrieve the possible segment headers from the OBX fields
     */
    public ArrayList<String> getHeaders(){
        int i;
        int j;

        ArrayList<String> headers = new ArrayList<String>();
        String currentHeader;
        try{
            for (i=0; i < msg.getRESPONSE().getORDER_OBSERVATIONReps(); i++){

                for (j=0; j < msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATIONReps(); j++){
                    // only check the obx segment for a header if it is one that will be displayed
                    if (!getOBXName(i, j).equals("")){
                        currentHeader = getObservationHeader(i, j);

                        if (!headers.contains(currentHeader)){
                            logger.info("Adding header: '"+currentHeader+"' to list");
                            headers.add(currentHeader);
                        }
                    }

                }

            }
            return(headers);
        }catch(Exception e){
            logger.error("Could not create header list", e);

            return(null);
        }
    }

    /**
     *  Methods to get information from observation notes
     */
    public int getOBRCommentCount(int i){
        /*try {
            int lastOBX = msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATIONReps() - 1;
            return(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(lastOBX).getNTEReps());
        } catch (Exception e) {*/
        return(0);
        // }
    }

    public String getOBRComment(int i, int j){
       /* try {
            int lastOBX = msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATIONReps() - 1;
            return(getString(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(lastOBX).getNTE(j).getComment(0).getValue()));
        } catch (Exception e) {*/
        return("");
        //}
    }

    /**
     *  Methods to get information from observation notes
     */
    public int getOBXCommentCount(int i, int j){
        int count = 0;
        try {
            count = msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getNTEReps();

            // a bug in getNTEReps() causes it to return 1 instead of 0 so we check to make
            // sure there actually is a comment there
            if (count == 1){
                String comment = msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getNTE().getComment(0).getValue();
                if (comment == null)
                    count = 0;
            }

        } catch (Exception e) {
            logger.error("Error retrieving obx comment count", e);
        }
        return count;
    }

    public String getOBXComment(int i, int j, int k){
        try {
            //int lastOBX = msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATIONReps() - 1;
            return(getString(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getNTE(k).getComment(0).getValue()));
        } catch (Exception e) {
            return("");
        }
    }


    /**
     *  Methods to get information about the patient
     */
    public String getPatientName(){
        return(getFirstName()+" "+getLastName());
    }

    public String getFirstName(){
        return(getString(msg.getRESPONSE().getPATIENT().getPID().getPatientName(0).getGivenName().getValue()));
    }

    public String getLastName(){
        return(getString(msg.getRESPONSE().getPATIENT().getPID().getPatientName(0).getFamilyName().getValue()));
    }

    public String getDOB(){
        try{
            return(formatDateTime(getString(msg.getRESPONSE().getPATIENT().getPID().getDateOfBirth().getTimeOfAnEvent().getValue())));
        }catch(Exception e){
            return("");
        }
    }

    public String getSex(){
        return(getString(msg.getRESPONSE().getPATIENT().getPID().getSex().getValue()));
    }

    public String getHealthNum(){
        return(getString(msg.getRESPONSE().getPATIENT().getPID().getSSNNumberPatient().getValue()));
    }

    public String getPatientLocation(){
        return(getString(msg.getMSH().getSendingApplication().getNamespaceID().getValue()));
    }

    public String getServiceDate(){
        try{
			String serviceDate =
				getString(msg.getRESPONSE().getPATIENT().getVISIT().getPV1().getAdmitDateTime().getTimeOfAnEvent().getValue());

            return(serviceDate);
        }catch(Exception e){
            return("");
        }
    }

    public String getRequestDate(int i){
        try{
            return(formatDateTime(getString(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBR().getRequestedDateTime().getTimeOfAnEvent().getValue())));
        }catch(Exception e){
            return("");
        }
    }

    public String getOrderStatus(){
		String status = "";
		try {
			status = obxseg.getObservResultStatus().toString();
		} catch (Exception e){
			logger.error("Error getting OBX-11 observation status"+ e);
		}
		return status;
    }

    public String getClientRef(){
        try{
			String providerId =
				getString(msg.getRESPONSE().getPATIENT().getVISIT().getPV1().getAttendingDoctor(0).getIDNumber().getValue());
            return(providerId);
        }catch(Exception e){
            logger.error("Could not return doctor id numbers", e);
            return("");
        }
    }

    public String getUniqueIdentifier(){
        String accessionNum = "";
        try{
			accessionNum =
				getString(msg.getRESPONSE().getPATIENT().getVISIT().getPV1().getVisitNumber().getID().getValue());
            return(accessionNum);
        }catch(Exception e){
            logger.error("Could not return accession number", e);
            return("");
        }
    }

    public String getDocName(){
        try{
			String docLastName =
				getString(msg.getRESPONSE().getPATIENT().getVISIT().getPV1().getAttendingDoctor(0).getFamilyName().getValue());
			String docFirstName =
				getString(msg.getRESPONSE().getPATIENT().getVISIT().getPV1().getAttendingDoctor(0).getGivenName().getValue());
            return(docLastName + "," + docFirstName);
        }catch(Exception e){
            logger.error("Could not return doctor names", e);
            return("");
        }
    }

    public String getCCDocs(){
        try{
			String docLastName =
				getString(msg.getRESPONSE().getPATIENT().getVISIT().getPV1().getConsultingDoctor(0).getFamilyName().getValue());
			String docFirstName =
				getString(msg.getRESPONSE().getPATIENT().getVISIT().getPV1().getConsultingDoctor(0).getGivenName().getValue());
            return(docLastName + "," + docFirstName);
        }catch(Exception e){
            logger.error("Could not return CC'd doctor names", e);
            return("");
        }
    }

    public ArrayList<String> getDocNums(){
        ArrayList<String> docNums = new ArrayList<String>();

        try{
			String providerId =
				getString(msg.getRESPONSE().getPATIENT().getVISIT().getPV1().getAttendingDoctor(0).getIDNumber().getValue());
            docNums.add(providerId);

        }catch(Exception e){
            logger.error("Could not return doctor nums", e);

        }

        return(docNums);
    }

    public String audit(){
        return "";
    }

    public String getUniqueVersionIdentifier(){
		return "";
	}

    public String getEncounterId(){
    	return "";
    }

    public String getRadiologistInfo(){
		return "";
	}

    public String getNteForOBX(int i, int j){
    	return "";
    }

    public String getNteForPID() {
	    return "";
    }
}

