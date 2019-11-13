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


package oscar.oscarLab.ca.all.parsers;

import java.util.ArrayList;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.oscarehr.common.hl7.v2.oscar_to_oscar.DynamicHapiLoaderUtils;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Segment;
import ca.uhn.hl7v2.model.Structure;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.util.Terser;
import ca.uhn.hl7v2.validation.impl.NoValidation;
import oscar.oscarLab.ca.all.parsers.messageTypes.ORU_R01MessageHandler;

public class IHAHandler extends ORU_R01MessageHandler
{
    
    Logger logger = Logger.getLogger(IHAHandler.class);
    protected Message msg = null;
    protected ArrayList<ArrayList<Segment>> obrGroups = null;
    
    public IHAHandler(){
    	//Creates a new instance of IHAHandler
    }
    
    @Override
    public void init(String hl7Body) throws HL7Exception {
        
        Parser p = new PipeParser();
        p.setValidationContext(new NoValidation());
        
        // force parsing as a generic message by changing the message structure
        hl7Body = hl7Body.replaceAll("R01", "O01");
        msg = p.parse(hl7Body.replaceAll( "\n", "\r\n"));
        message = msg;
        
        terser = new Terser(msg);
        int obrCount = getOBRCount();
        int obrNum;
        boolean obrFlag;
        String segmentName;
        String[] segments = terser.getFinder().getRoot().getNames();
        obrGroups = new ArrayList<ArrayList<Segment>>();
        
        /*
         *  Fill the OBX array list for use by future methods
         */
        for (int i=0; i < obrCount; i++){
            ArrayList<Segment> obxSegs = new ArrayList<Segment>();
            obrNum = i+1;
            obrFlag = false;
            
            for (int k=0; k < segments.length; k++){
                
                segmentName = segments[k].substring(0, 3);
                
                if (obrFlag && segmentName.equals("OBX")){
                    
                    // make sure to count all of the obx segments in the group
                    Structure[] segs = terser.getFinder().getRoot().getAll(segments[k]);
                    for (int l=0; l < segs.length; l++){
                        Segment obxSeg = (Segment) segs[l];
                        obxSegs.add(obxSeg);
                    }
                    
                }else if (obrFlag && segmentName.equals("OBR")){
                    break;
                }else if ( segments[k].equals("OBR"+obrNum) || ( obrNum==1 && segments[k].equals("OBR"))){
                    obrFlag = true;
                }
                
            }
            obrGroups.add(obxSegs);
        }
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
    public String getMsgType(){
        return("IHA");
    }
    
    @Override
    public String getMsgPriority(){
        return("");
    }
    /*
     *  MSH METHODS
     */
    
    @Override
    public String getMsgDate(){
        
        try{
            String dateString = formatDateTime(getString(terser.get("/.MSH-7-1")));
            return(dateString);
        }catch(Exception e){
            return("");
        }
    }

        /*
     *  PID METHODS
     */
    @Override
    public String getPatientName(){
        return(getFirstName()+" "+getLastName());
    }
    
/*    @Override
    public String getFirstName(){
        return(getString(msg.getRESPONSE().getPATIENT().getPID().getPatientName().getGivenName().getValue()));
    }
    
    @Override
    public String getLastName(){
        return(getString(msg.getRESPONSE().getPATIENT().getPID().getPatientName().getFamilyName().getValue()));
    }
    
    @Override
    public String getDOB(){
        try{
            return(formatDateTime(getString(msg.getRESPONSE().getPATIENT().getPID().getDateOfBirth().getTimeOfAnEvent().getValue())).substring(0, 10));
        }catch(Exception e){
            return("");
        }
    }*/

    public String getAdmittingProviderMnemonic(){
        try {
            return(getString(terser.get("/.ZDR-1-1")));
        }catch (HL7Exception ex) {
            logger.error("Error parsing HL7 file: " + ex);
            return("");
        }
    }

    public String getAttendingProviderMnemonic(){
        try {
            return(getString(terser.get("/.ZDR-2-1")));
        }catch (HL7Exception ex) {
            logger.error("Error parsing HL7 file: " + ex);
            return("");
        }
    }

    public String getOtherProviderMnemonic(){
        try {
            return(getString(terser.get("/.ZDR-3-1")));
        }catch (HL7Exception ex) {
            logger.error("Error parsing HL7 file: " + ex);
            return("");
        }
    }

    public String getFamilyProviderMnemonic(){
        try {
            return(getString(terser.get("/.ZDR-4-1")));
        }catch (HL7Exception ex) {
            logger.error("Error parsing HL7 file: " + ex);
            return("");
        }
    }

    public String getEmergencyProviderMnemonic(){
        try {
            return(getString(terser.get("/.ZDR-5-1")));
        }catch (HL7Exception ex) {
            logger.error("Error parsing HL7 file: " + ex);
            return("");
        }
    }

    public String getPrimaryCareProviderMnemonic(){
        try {
            return(getString(terser.get("/.ZDR-6-1")));
        }catch (HL7Exception ex) {
            logger.error("Error parsing HL7 file: " + ex);
            return("");
        }
    }
    
    @Override
    public String getFirstName(){
        try {
            return(getString(terser.get("/.PID-5-2")));
        } catch (HL7Exception ex) {
            return("");
        }
    }
    
    public String getLastName(){
        try {
            return(getString(terser.get("/.PID-5-1")));
        } catch (HL7Exception ex) {
            return("");
        }
    }
    
    public String getDOB(){
        try{
            return(formatDateTime(getString(terser.get("/.PID-7-1"))).substring(0, 10));
        }catch(Exception e){
            return("");
        }
    }

    
    @Override
    public String getSex(){
        try{
            return(getString(terser.get("/.PID-8-1")));
        }catch(Exception e){
            return("");
        }
    }
    
    @Override
    public String getHealthNum(){
        String healthNum;
        
        try{
            healthNum = getString(terser.get("/.PID-4-1"));
            if (healthNum.length() == 10)
                return(healthNum);
        }catch(Exception e){
            //ignore exceptions
        }
        
        return("");
    }
    
    @Override
    public String getHomePhone(){
        try{
            return(getString(terser.get("/.PID-13-1")));
        }catch(Exception e){
            return("");
        }
    }
    
    @Override
    public String getWorkPhone(){
        try{
            return(getString(terser.get("/.PID-14-1")));
        }catch(Exception e){
            return("");
        }
    }
    
    @Override
    public String getPatientLocation(){
        try{
            return(getString(terser.get("/.MSH-4-1")));
        }catch(Exception e){
            return("");
        }
    }

    /*@Override
    public String getPatientLocation(){
        return(getString(msg.getMSH().getSendingFacility().getNamespaceID().getValue()));
    }*/
    
    /*
     *  OBC METHODS
     */
    @Override
    public String getAccessionNum(){
        try{
            return (getString(terser.get("/.ORC-3-1")));
        }catch(Exception e){
            logger.error("Could not return accession number", e);
            return("");
        }
    }
    
    /*
     *  OBR METHODS
     */
    
    /*@Override
    public int getOBRCount(){
        return(msg.getRESPONSE().getORDER_OBSERVATIONReps());
    }*/
    
    /**
     *  Methods to get information about the Observation Request
     */
    @Override
    public int getOBRCount(){
        
        if (obrGroups != null){
            return(obrGroups.size());
        }else{
            int i = 1;
            //String test;
            Segment test;
            try{
                
                test = terser.getSegment("/.OBR");
                while(test != null){
                    i++;
                    test = (Segment) terser.getFinder().getRoot().get("OBR"+i);
                }
                
            }catch(Exception e){
                //ignore exceptions
            }
            
            return(i-1);
        }
    }
    
    private String getSendingApplication() {
    	try {
    	return(getString(terser.get("/.MSH-3-1")));
        }catch(Exception e){
            return("");
        }
    }
    
    private String getReceivingApplication() {
    	try {
    	return(getString(terser.get("/.MSH-5-1")));
        }catch(Exception e){
            return("");
        }
    }

    @Override
    public String getOBRName(int i){
        
        String obrName,sendApp;
        i++;
        try{
        	sendApp=getSendingApplication();
        	if(sendApp.startsWith("IHA")) sendApp = sendApp.substring(3);
            if (i == 1){
                obrName = getString(terser.get("/.OBR-4-2"));  //Universal Service ID
                if (obrName.equals(""))
                    obrName = getString(terser.get("/.OBR-4-1"));
            }else{
                Segment obrSeg = (Segment) terser.getFinder().getRoot().get("OBR"+i);
                obrName = getString(Terser.get(obrSeg,4,0,2,1));
                if (obrName.equals(""))
                    obrName = getString(Terser.get(obrSeg,4,0,1,1));
            }
            
            return(obrName);
            
        }catch(Exception e){
            return("");
        }
    }
    
    @Override
    public String getObservationHeader(int i, int j){
    	String obrHeader=null,sendApp=null;
    	i++;
        try{
        	sendApp=getSendingApplication();
        	if(sendApp.startsWith("IHA")) sendApp = sendApp.substring(3);
        	obrHeader = sendApp + "_" + getOBRName(i-1);
        	return obrHeader;
        }catch(Exception e){
            return("");
        }
    }
    
	@Override
    public int getOBRCommentCount(int i){
        try {
            if ( !getOBRComment(i, 0).equals("") ){
                return(1);
            }else{
                return(0);
            }
        } catch (Exception e) {
            return(0);
        }
    }
    
    /*()@Override
    public String getOBRComment(int i, int j){
        try {
            return(getString(msg.getRESPONSE().getORDER_OBSERVATION(i).getNTE(j).getComment(0).getValue()));
        } catch (Exception e) {
            return("");
        }
    }*/
    
    @Override
    public String getOBRComment(int i, int j){
        
        try{
            String[] segments = terser.getFinder().getRoot().getNames();
            int k = getNTELocation(i, -1);
            
            Structure[] nteSegs = terser.getFinder().getRoot().getAll(segments[k]);
            Segment nteSeg = (Segment) nteSegs[j];
            return(getString(Terser.get(nteSeg,3,0,1,1)));
            
        }catch(Exception e){
            logger.error("Could not retrieve OBX comments", e);
            
            return("");
        }
    }
    
    /*@Override
    public String getServiceDate(){
        try{
            return(formatDateTime(getString(msg.getRESPONSE().getORDER_OBSERVATION(0).getOBR().getObservationDateTime().getTimeOfAnEvent().getValue())));
            //return(formatDateTime(getString(msg.getRESPONSE().getORDER_OBSERVATION(0).getOBR().getObservationDateTime().getTimeOfAnEvent().getValue())));
        }catch(Exception e){
            return("");
        }
    }*/
    
    @Override
    public String getServiceDate(){
    	String sTime=null;
	    try{
	        sTime=getString(terser.get("/.OBR-7-1"));
	        //some PTH reports have no entry in 7-1;
	        if(sTime.equals("")) sTime = getString(terser.get("/.OBR-14-1"));
	        return sTime;
	    }catch(Exception e){
	        logger.error("Could not return service date", e);
	        return("");
	    }
    }

    public String getRequestDate(int i){
        String requestDate;
        i++;
        try{
            if (i == 1){
            	requestDate = formatDateTime(getString(DynamicHapiLoaderUtils.terserGet(terser, "/.OBR-14-1")));
            }else{
            	requestDate = formatDateTime(getString(DynamicHapiLoaderUtils.terserGet(terser, "/.OBR"+i+"-14-1")));
            }
            return(requestDate);
        }catch(Exception e){
            return getMsgDate();
        }
    }
    
    /*@Override
    public String getOrderStatus(){
        try{
	        return(getString(terser.get("/.OBR-7-1")));
        	String header = getObservationHeader(0,0);
        	if(header.equalsIgnoreCase("RAD"))
        		return "F";
            return(getString(msg.getRESPONSE().getORDER_OBSERVATION(0).getOBR().getResultStatus().getValue()));
        }catch(Exception e){
            return("");
        }
    }*/
    
    @Override
    public String getOrderStatus(){
        try{
	        return(getString(terser.get("/.OBR-25-1")));
        }catch(Exception e){
            return("");
        }
    }

    /*@Override
    public String getClientRef(){
        String docNum = null;
        int i=0;
        try{
            while(!getString(msg.getRESPONSE().getORDER_OBSERVATION(0).getOBR().getOrderingProvider(i).getIDNumber().getValue()).equals("")){
                if (i==0){
                    docNum = getString(msg.getRESPONSE().getORDER_OBSERVATION(0).getOBR().getOrderingProvider(i).getIDNumber().getValue());
                }else{
                    docNum = docNum + ", " + getString(msg.getRESPONSE().getORDER_OBSERVATION(0).getOBR().getOrderingProvider(i).getIDNumber().getValue());
                }
                i++;
            }
            return(docNum);
        }catch(Exception e){
            logger.error("Could not return doctor id numbers", e);
            
            return("");
        }
    }*/
    
    @Override
    public String getClientRef(){
        try{
            return(getString(terser.get("/.OBR-16-1")));
        }catch(Exception e){
            return("");
        }
    }

   /* @Override
    public String getDocName(){
        String temp=null,docName = null;
        int i=0;
        try{
            while((temp=getFullDocName(msg.getRESPONSE().getORDER_OBSERVATION(0).getOBR().getOrderingProvider(i)))!=null && !temp.equals("")){
                if (i==0){
                    docName = temp;
                }else{
                    docName = docName + ", " + temp;
                }
                i++;
            }
            return(docName);
        }catch(Exception e){
            logger.error("Could not return doctor names", e);
            
            return("");
        }
    }*/
    
    public String getDocName(){
        try{
            return(getFullDocName("/.OBR-16-"));
        }catch(Exception e){
            return("");
        }
    }

    /*@Override
    public String getCCDocs(){
        String temp=null,docName = null;
        int i=0;
        try{
            while((temp=getFullDocName(msg.getRESPONSE().getORDER_OBSERVATION(0).getOBR().getResultCopiesTo(i)))!=null && !temp.equals("")){
                if (i==0){
                    docName = temp;
                }else{
                    docName = docName + ", " + temp;
                }
                i++;
            }
            return(docName);
        }catch(Exception e){
            logger.error("Could not return cc'ed doctors", e);
            
            return("");
        }
    }*/
    
    @Override
    public String getCCDocs(){
        
        try {
            int i=0;
            String docs = getFullDocName("/.OBR-28("+i+")-");
            i++;
            String nextDoc = getFullDocName("/.OBR-28("+i+")-");
            
            while(!nextDoc.equals("")){
                docs = docs+", "+nextDoc;
                i++;
                nextDoc = getFullDocName("/.OBR-28("+i+")-");
            }
            
            return(docs);
        } catch (Exception e) {
            return("");
        }
    }

    /*@Override
    public ArrayList<String> getDocNums(){
        ArrayList<String> docNums = new ArrayList<String>();
        String id;
        int i;
        
        try{
            String providerId = msg.getRESPONSE().getORDER_OBSERVATION(0).getOBR().getOrderingProvider(0).getIDNumber().getValue();
            docNums.add(providerId);
            
            i=0;
            while((id = msg.getRESPONSE().getORDER_OBSERVATION(0).getOBR().getResultCopiesTo(i).getIDNumber().getValue()) != null){
                if (!id.equals(providerId))
                    docNums.add(id);
                i++;
            }
        }catch(Exception e){
            logger.error("Could not return doctor nums", e);
            
        }
        
        return(docNums);
    }*/
    
    @Override
    public ArrayList<String> getDocNums(){
        ArrayList<String> nums = new ArrayList<String>();
        String docNum;
        try{
            if ((docNum = terser.get("/.OBR-16-1")) != null)
                nums.add(docNum);
            
            int i=0;
            while((docNum = terser.get("/.OBR-28("+i+")-1")) != null){
                nums.add(docNum);
                i++;
            }
           nums.addAll(getProviderMnemonics());
        }catch(Exception e){
            logger.error("Error retrieving DocNums", e);
        }
        return(nums);
    }

    // Returns a list of all provider mnemonics associated with the lab
    private ArrayList<String> getProviderMnemonics()
    {
        ArrayList<String> mnemonicList = new ArrayList<>();
        String providerMnemonic;

        if ((providerMnemonic = StringUtils.trimToNull(getAdmittingProviderMnemonic())) != null)
        {
            mnemonicList.add(providerMnemonic);
        }
        if ((providerMnemonic = StringUtils.trimToNull(getAttendingProviderMnemonic())) != null)
        {
            mnemonicList.add(providerMnemonic);
        }
        if ((providerMnemonic = StringUtils.trimToNull(getFamilyProviderMnemonic())) != null)
        {
            mnemonicList.add(providerMnemonic);
        }
        if ((providerMnemonic = StringUtils.trimToNull(getEmergencyProviderMnemonic())) != null)
        {
            mnemonicList.add(providerMnemonic);
        }
        if ((providerMnemonic = StringUtils.trimToNull(getPrimaryCareProviderMnemonic())) != null)
        {
            mnemonicList.add(providerMnemonic);
        }
        if ((providerMnemonic = StringUtils.trimToNull(getOtherProviderMnemonic())) != null)
        {
            mnemonicList.add(providerMnemonic);
        }

        return mnemonicList;
    }
    
    /*
     *  OBX METHODS
     */
    /*@Override
    public int getOBXCount(int i){
        int count = 0;
        try{
            count = msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATIONReps();
            // if count is 1 there may only be an nte segment and no obx segments so check
            if (count == 1){
                String test = msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(0).getOBX().getObservationIdentifier().getText().getValue();
                logger.info("name: "+test);
                if (test == null)
                    count = 0;
            }
        }catch(Exception e){
            logger.error("Error retrieving obx count", e);
            count = 0;
        }
        return count;
    }*/
    
    @Override
    public int getOBXCount(int i){
        ArrayList<Segment> obxSegs = obrGroups.get(i);
        return(obxSegs.size());
    }
    
    /*@Override
    public String getOBXIdentifier(int i, int j){
        try{
        	String test = getString(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getObservationIdentifier().getIdentifier().getValue());
            return(getString(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getObservationIdentifier().getIdentifier().getValue()));
        }catch(Exception e){
            return("");
        }
    }*/
    
    @Override
    public String getOBXIdentifier(int i, int j){
        return(getOBXField(i, j, 3, 0, 1));
    }

    
    @Override
    public String getOBXValueType(int i, int j){
        try{
        	int index;
        	/*IHA says
        	This field contains the format of the observation.
        	ST - String data used to send short, and possibly encoded, text strings. Referred to as data
        	format or discrete results
        	TX - Free text data used to send large amounts of text in Health Record Messages. Referred to
        	as report format.
        	CE - CE is used exclusively for reporting culture organism results.
        	However, ST is used for everything that I think should be free text, so I had to use the
        	report type to differentiate, and send "NA" for "narrative" to indicate that text should stretch
        	across all columns
        	*/
        	String repType = getSendingApplication();
        	if((index=repType.indexOf("TX"))!=-1) return "NAR";
        	if((index=repType.indexOf("OE"))!=-1) return "NAR";
        	if((index=repType.indexOf("RAD"))!=-1) return "NAR";
        	if((index=repType.indexOf("LAB"))!=-1) {
        		String type = getReceivingApplication();
        		if(type.equals("BBK")) return "NAR";
        		//if(type.equals("MB")) return "NAR";
        		if(type.equals("PTH")) return "NAR";
        		//if(type.equals("UR")) return "NAR";
        		if(type.equals("MIC")) return "NAR";
        	}
        	return("");
        }catch(Exception e){
            return("");
        }
    }

    /*@Override
    public String getOBXName(int i, int j){
        try{
        	String test = getString(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getObservationIdentifier().getText().getValue());
            return(getString(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getObservationIdentifier().getText().getValue()));
        }catch(Exception e){
            return("");
        }
    }*/
    
    @Override
    public String getOBXName(int i, int j){
    	int index;
    	String repType = getSendingApplication();
    	if((index=repType.indexOf("OE"))!=-1) return "Narrative Report";
        return(getOBXField(i, j, 3, 0, 2));
    }
    
    /*@Override
    public String getOBXResult(int i, int j){
        try{
           String testText = (getString(Terser.get(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX(),5,0,1,1)));
           return (getString(Terser.get(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX(),5,0,1,1)));
           //*return(getString(Terser.get(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX(),5,0,1,1)));
        }catch(Exception e){
            return("");
        }
    }*/
    
    @Override
    public String getOBXResult(int i, int j){
        return(getOBXField(i, j, 5, 0, 1));
    }
    
    /*@Override
    public String getOBXReferenceRange(int i, int j){
        try{
            return(getString(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getReferencesRange().getValue()));
        }catch(Exception e){
            return("");
        }
    }*/
    
    @Override
    public String getOBXReferenceRange(int i, int j){
        return(getOBXField(i, j, 7, 0, 1));
    }
    
    /*@Override
    public String getOBXUnits(int i, int j){
        try{
            return(getString(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getUnits().getIdentifier().getValue()));
        }catch(Exception e){
            return("");
        }
    }*/
    
    @Override
    public String getOBXUnits(int i, int j){
        return(getOBXField(i, j, 6, 0, 1));
    }
    
    /*@Override
    public String getOBXResultStatus(int i, int j){
        try{
            return(getString(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getObservResultStatus().getValue()));
        }catch(Exception e){
            return("");
        }
    }*/
    
    @Override
    public String getOBXResultStatus(int i, int j){
        return(getOBXField(i, j, 11, 0, 1));
    }
    
    @Override
    public int getOBXFinalResultCount(){
        int obrCount = getOBRCount();
        int obxCount;
        int count = 0;
        for (int i=0; i < obrCount; i++){
            obxCount = getOBXCount(i);
            for (int j=0; j < obxCount; j++){
                String status = getOBXResultStatus(i, j);
                if (status.equalsIgnoreCase("F") || status.equalsIgnoreCase("C"))
                    count++;
            }
        }
        
        
        String orderStatus = getOrderStatus();
        // add extra so final reports are always the ordered as the latest except
        // if the report has been changed in which case that report should be the latest
        if (orderStatus.equalsIgnoreCase("F"))
            count = count + 100;
        else if (orderStatus.equalsIgnoreCase("C"))
            count = count + 150;
        
        return count;
    }
    
    /*@Override
    public String getTimeStamp(int i, int j){
        try{
            return(formatDateTime(getString(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getDateTimeOfTheObservation().getTimeOfAnEvent().getValue())));
        }catch(Exception e){
            return("");
        }
    }*/
    
    @Override
    public String getTimeStamp(int i, int j){
        String timeStamp;
        i++;
        try{
            if (i == 1){
                timeStamp = formatDateTime(getString(terser.get("/.OBR-7-1")));
            }else{
                Segment obrSeg = (Segment) terser.getFinder().getRoot().get("OBR"+i);
                timeStamp = formatDateTime(getString(Terser.get(obrSeg,7,0,1,1)));
            }
            return(timeStamp);
        }catch(Exception e){
            return("");
        }
    }
    
    @Override
    public boolean isOBXAbnormal(int i, int j){
        try{
            String abnormalFlag = getOBXAbnormalFlag(i, j);
            if(!abnormalFlag.equals("") && !abnormalFlag.equalsIgnoreCase("N")){
                return(true);
            }else{
                return(false);
            }
            
        }catch(Exception e){
            return(false);
        }
    }
    
    /*@Override
    public String getOBXAbnormalFlag(int i, int j){
        try{
            return(getString(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getAbnormalFlags(0).getValue()));
        }catch(Exception e){
            logger.error("Error retrieving obx abnormal flag", e);
            return("");
        }
    }*/
    
    public String getOBXAbnormalFlag(int i, int j){
        return(getOBXField(i, j, 8, 0, 1));
    }
    
    /*@Override
    public int getOBXCommentCount(int i, int j){
        try {
            if ( !getOBXComment(i, j, 0).equals("") ){
                return(1);
            }else{
                return(0);
            }
        } catch (Exception e) {
            return(0);
        }
    }*/
    
    @Override
    public int getOBXCommentCount(int i, int j){
        // jth obx of the ith obr
        
        try{
            
            String[] segments = terser.getFinder().getRoot().getNames();
            int k = getNTELocation(i, j);
            
            int count = 0;
            if (k < segments.length && segments[k].substring(0, 3).equals("NTE")){
                Structure[] nteSegs = terser.getFinder().getRoot().getAll(segments[k]);
                for (int l=0; l < nteSegs.length; l++){
                    count++;
                }
            }
            
            return(count);
        }catch(Exception e){
            logger.error("OBR Comment count error", e);
            
            return(0);
        }
        
    }
    
    /*@Override
    public String getOBXComment(int i, int j, int k){
        try {
            return(getString(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getNTE(k).getComment(0).getValue()));
        } catch (Exception e) {
            return("");
        }
    }*/
    
    @Override
    public String getOBXComment(int i, int j, int nteNum){
        
        
        try{
            
            String[] segments = terser.getFinder().getRoot().getNames();
            int k = getNTELocation(i, j);
            Structure[] nteSegs = terser.getFinder().getRoot().getAll(segments[k]);
            Segment nteSeg = (Segment) nteSegs[nteNum];
            return(getString(Terser.get(nteSeg,3,0,1,1)));
            
        }catch(Exception e){
            logger.error("Could not retrieve OBX comments", e);
            
            return("");
        }
    }
    
    
    /**
     *  Retrieve the possible segment headers from the OBX fields
     */
    @Override
    public ArrayList<String> getHeaders(){
        int i;
        int arraySize;
        
        ArrayList<String> headers = new ArrayList<String>();
        String currentHeader;

        try{
            for (i=0; i < getOBRCount(); i++){
                
                currentHeader = getObservationHeader(i, 0);
                arraySize = headers.size();
                if (arraySize == 0 || !currentHeader.equals(headers.get(arraySize-1))){
                    logger.info("Adding header: '"+currentHeader+"' to list");
                    headers.add(currentHeader);
                }
                
            }
            return(headers);
        }catch(Exception e){
            logger.error("Could not create header list", e);
            
            return(null);
        }
        
    }
    
    @Override
    public String audit(){
        return "";
    }
    
    /*
     *  END OF PUBLIC METHODS
     */
    
    protected String getOBXField(int i, int j, int field, int rep, int comp){
        ArrayList<Segment> obxSegs = obrGroups.get(i);
        
        try{
            Segment obxSeg = obxSegs.get(j);
            return (getString(Terser.get(obxSeg, field, rep, comp, 1 )));
        }catch(Exception e){
            return("");
        }
    }
    
    private int getNTELocation(int i, int j) throws HL7Exception{
        int k = 0;
        int obrCount = 0;
        int obxCount = 0;
        String[] segments = terser.getFinder().getRoot().getNames();
        
        while (k != segments.length && obrCount != i+1){
            if (segments[k].substring(0, 3).equals("OBR"))
                obrCount++;
            k++;
        }
        
        Structure[] obxSegs;
        while (k != segments.length && obxCount != j+1){
            
            
            if (segments[k].substring(0, 3).equals(("OBX"))){
                obxSegs = terser.getFinder().getRoot().getAll(segments[k]);
                obxCount = obxCount + obxSegs.length;
            }
            k++;
        }
        
        return(k);
    }
    
    /*private String getFullDocName(XCN docSeg){
        String docName = null;
        
        if(docSeg.getPrefixEgDR().getValue() != null)
            docName = docSeg.getPrefixEgDR().getValue();
        
        if(docSeg.getGivenName().getValue() != null){
            if (docName==null){
                docName = docSeg.getGivenName().getValue();
            }else{
                docName = docName +" "+ docSeg.getGivenName().getValue();
            }
        }
        if(docSeg.getMiddleInitialOrName().getValue() != null)
            docName = docName +" "+ docSeg.getMiddleInitialOrName().getValue();
        if(docSeg.getFamilyName().getValue() != null)
            docName = docName +" "+ docSeg.getFamilyName().getValue();
        if(docSeg.getSuffixEgJRorIII().getValue() != null)
            docName = docName +" "+ docSeg.getSuffixEgJRorIII().getValue();
        if(docSeg.getDegreeEgMD().getValue() != null)
            docName = docName +" "+ docSeg.getDegreeEgMD().getValue();
        
        return (docName);
    }*/
    
    private String getFullDocName(String docSeg) throws HL7Exception{
        String docName = "";
        String temp;
        
        // get name prefix ie/ DR.
        temp = terser.get(docSeg+"6");
        if(temp != null)
            docName = temp;
        
        // get the name
        temp = terser.get(docSeg+"3");
        if(temp != null){
            if (docName.equals("")){
                docName = temp;
            }else{
                docName = docName +" "+ temp;
            }
        }
        
        if(terser.get(docSeg+"4") != null)
            docName = docName +" "+ terser.get(docSeg+"4");
        if(terser.get(docSeg+"2") != null)
            docName = docName +" "+ terser.get(docSeg+"2");
        if(terser.get(docSeg+"5")!= null)
            docName = docName +" "+ terser.get(docSeg+"5");
        if(terser.get(docSeg+"7") != null)
            docName = docName +" "+ terser.get(docSeg+"7");
        
        return (docName);
    }

    @Override
    protected String getString(String retrieve) {
        return super.getString(retrieve).replaceAll("\\\\\\.br\\\\", "<br />");
    }
    
    public String getFillerOrderNumber(){
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


    @Override
    public boolean isUnstructured() {

        boolean result=true;
        for(int j = 0; j<this.getOBRCount();j++) {
            for(int k=0;k<this.getOBXCount(j);k++) {
                if(!"NAR".equals(getOBXValueType(j, k))) {
                    result=false;
                }
            }
        }
        return result;
    }
}
