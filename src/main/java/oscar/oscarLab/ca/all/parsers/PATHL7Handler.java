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
 * PATHL7Handler.java
 *
 * Created on June 4, 2007, 1:17 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package oscar.oscarLab.ca.all.parsers;


import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v23.message.ORU_R01;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.util.Terser;
import ca.uhn.hl7v2.validation.impl.NoValidation;
import org.apache.log4j.Logger;
import org.oscarehr.common.model.Hl7TextInfo;
import oscar.oscarLab.ca.all.model.EmbeddedDocument;
import oscar.oscarLab.ca.all.parsers.messageTypes.ORU_R01MessageHandler;
import oscar.util.UtilDateUtilities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 *
 * @author wrighd
 */
public class PATHL7Handler extends ORU_R01MessageHandler
{
	public static final String LIFELABS_MESSAGE_TYPE = "PATHL7";

    Logger logger = Logger.getLogger(PATHL7Handler.class);
    protected ORU_R01 msg;

	private static List<String> labDocuments = Arrays.asList(
			"BCCASMP",
			"BCCACSP",
			"BLOODBANKT",
			"CELLPATH",
			"CELLPATHR",
			"CYTO",
			"CYTOGEN",
			"DIAG IMAGE",
			"MICRO3T",
			"MICROGCMT",
			"MICROGRT",
			"MICROBCT",
			"NOTIF",
			"TRANSCRIP"
	);

	public static final String VIHARTF = "CELLPATHR";

    /**
     * Map Excelleris status codes to ones that we want to display to the user.
     * Applies only when uploading new labs.
     * This is a WIP and is based upon data previously seen in labs.
     * May be modified later if we get formal specifications.
     */
    private static final Map<String, Hl7TextInfo.REPORT_STATUS> orderStatusMap = new HashMap<String,  Hl7TextInfo.REPORT_STATUS>();
    static
    {
        orderStatusMap.put("P", Hl7TextInfo.REPORT_STATUS.E);
        orderStatusMap.put("F", Hl7TextInfo.REPORT_STATUS.F);
        orderStatusMap.put("C", Hl7TextInfo.REPORT_STATUS.C);
        orderStatusMap.put("X", Hl7TextInfo.REPORT_STATUS.X);
    }

    /** Creates a new instance of CMLHandler */
    public PATHL7Handler(){
    }

    public void init(String hl7Body) throws HL7Exception {
        Parser parser = new PipeParser();
        parser.setValidationContext(new NoValidation());
        msg = (ORU_R01) parser.parse(hl7Body.replaceAll( "\n", "\r\n" ).replace("\\.Zt\\", "\t"));
        this.message = msg;
        this.terser = new Terser(msg);

        // Legacy Excelleris labs with embedded PDFs don't have proper identifier segment set-up
        // if this is the case then we need to modify the message
        if (getOBXValueType(0, 0).equals("ED") && getOBXCount(0) == 0)
        {
            addOBXIdentifierText("PDF");
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

	public String getMsgType()
	{
		return (LIFELABS_MESSAGE_TYPE);
	}

    @Override
    public boolean supportsEmbeddedDocuments()
    {
        return true;
    }

    public String getMsgPriority(){
        return("");
    }
    /*
     *  MSH METHODS
     */

    public String getMsgDate(){
        return(formatDateTime(getString(msg.getMSH().getDateTimeOfMessage().getTimeOfAnEvent().getValue())));
    }

    public String getLabUser()
    {
        return (msg.getMSH().getReceivingFacility().getNamespaceID().toString());
    }

    /*
     *  PID METHODS
     */
    public String getPatientName(){
        return(getFirstName()+" "+getMiddleName()+" "+getLastName());
    }

    public String getFirstName(){
        return(getString(msg.getRESPONSE().getPATIENT().getPID().getPatientName(0).getGivenName().getValue()));
    }

    public String getMiddleName(){
        return(getString(msg.getRESPONSE().getPATIENT().getPID().getPatientName(0).getXpn3_MiddleInitialOrName().getValue()));
    }
    public String getLastName(){
        return(getString(msg.getRESPONSE().getPATIENT().getPID().getPatientName(0).getFamilyName().getValue()));
    }

    public String getDOB(){
        try
        {
            return formatDateTime(getString(msg.getRESPONSE().getPATIENT().getPID().getDateOfBirth().getTimeOfAnEvent().getValue())).substring(0, 10);
        }
        catch (Exception e)
        {
            return "";
        }
    }

    @Override
	public String getAge(){
		String age = "N/A";
		String dob = getDOB();
		String service = getServiceDate();
		try
		{
			// Some examples
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date birthDate = formatter.parse(dob);
			java.util.Date serviceDate = formatter.parse(service);
			age = UtilDateUtilities.calcAgeAtDate(birthDate, serviceDate);
		}
		catch (ParseException e)
		{
			logger.error("Could not get age", e);
		}
		return age;
	}

    public String getSex(){
        return(getString(msg.getRESPONSE().getPATIENT().getPID().getSex().getValue()));
    }

    public String getHealthNum(){
        return(getString(msg.getRESPONSE().getPATIENT().getPID().getPatientIDExternalID().getID().getValue()));
    }

    public String getPatientLocation(){
        return(getString(msg.getMSH().getSendingFacility().getNamespaceID().getValue()));
    }

    /*
     *  OBC METHODS
     */
    public String getUniqueIdentifier(){
        try
        {
            String str = msg.getRESPONSE().getORDER_OBSERVATION(0).getORC().getFillerOrderNumber().getEntityIdentifier().getValue();
            String accessionNum = getString(str);
            String[] nums = accessionNum.split("-");

            if (nums.length == 3)
            {
                return nums[0];
            }
            else if (nums.length == 5)
            {
                return nums[0]+"-"+nums[1]+"-"+nums[2];
            }
            else
            {
                if(nums.length>1)
                {
                    return nums[0]+"-"+nums[1];
                }
                else
                {
                    return "";
                }
            }
        }
        catch(Exception e)
        {
            logger.error("Could not return accession number", e);
            return("");
        }
    }

    /*
     *  OBR METHODS
     */

    public int getOBRCount(){
        return(msg.getRESPONSE().getORDER_OBSERVATIONReps());
    }

    public String getOBRName(int i){
        try
        {
            return getString(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBR().getUniversalServiceIdentifier().getText().getValue());
        }
        catch (Exception e)
        {
            return "";
        }
    }

	public String getObservationHeader(int i, int j)
	{
		try
		{
			return getString(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBR().getDiagnosticServiceSectionID().getValue());
		}
		catch(Exception e)
		{
			return "";
		}
	}

    public int getOBRCommentCount(int i){
        try
        {
            if (!getOBRComment(i, 0).isEmpty())
            {
                return(1);
            }
            else
            {
                return(0);
            }
        }
        catch (Exception e)
        {
            return(0);
        }
    }

    public String getOBRComment(int i, int j){
        try
        {
            return(getString(msg.getRESPONSE().getORDER_OBSERVATION(i).getNTE(j).getComment(0).getValue()));
        }
        catch (Exception e)
        {
            return("");
        }
    }

    public String getServiceDate(){
        try
        {
            return(formatDateTime(getString(msg.getRESPONSE().getORDER_OBSERVATION(0).getOBR().getObservationDateTime().getTimeOfAnEvent().getValue())));
        }
        catch(Exception e)
        {
            return("");
        }
    }

    public String getRequestDate(int i){
        try
        {
            return(formatDateTime(getString(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBR().getRequestedDateTime().getTimeOfAnEvent().getValue())));
        }
        catch (Exception e)
        {
            return("");
        }
    }

    public String getOrderStatus(){
        try
        {
            String orderStatus = getString(msg.getRESPONSE().getORDER_OBSERVATION(0).getOBR().getResultStatus().getValue());
            int obrCount = getOBRCount();
            int obxCount;
            int count = 0;
            for (int i = 0; i < obrCount; i++)
            {
                obxCount = getOBXCount(i);
                for (int j = 0; j < obxCount; j++)
                {
                    String obxStatus = getOBXResultStatus(i, j);
                    if (obxStatus.equalsIgnoreCase("C"))
                    {
                        count++;
                    }
                }
            }

            // If any of the OBX's have been corrected, mark the entire report as corrected
            if (count >= 1)
            {
            	orderStatus = "C";
            }
            return orderStatus;
        }
        catch(Exception e)
        {
            return("");
        }
    }

	/**
	 *  Return the status of the report in a human-readable format
	 */
	@Override
	public String getOrderStatusDisplayValue()
	{
		String orderStatusCode = getString(getOrderStatus());
		switch (orderStatusCode)
		{
			case "F": return "Final";
			case "C": return "Corrected";
			case "P": return "Preliminary";
			case "X": return "DELETED";
			default: return orderStatusCode;
		}
	}

    public String getClientRef(){
        String docNum = "";
        int i = 0;
        try
        {
            while (!getString(msg.getRESPONSE().getORDER_OBSERVATION(0).getOBR().getOrderingProvider(i).getIDNumber().getValue()).equals(""))
            {
                if (i == 0)
                {
                    docNum = getString(msg.getRESPONSE().getORDER_OBSERVATION(0).getOBR().getOrderingProvider(i).getIDNumber().getValue());
                }
                else
                {
                    docNum = docNum + ", " + getString(msg.getRESPONSE().getORDER_OBSERVATION(0).getOBR().getOrderingProvider(i).getIDNumber().getValue());
                }
                i++;
            }
            return(docNum);
        }
        catch(Exception e)
        {
            logger.error("Could not return doctor id numbers", e);
            return("");
        }
    }

    public String getDocName(){
        String docName = "";
        int i=0;
        try
        {
            while(!getFullDocName(msg.getRESPONSE().getORDER_OBSERVATION(0).getOBR().getOrderingProvider(i)).equals("")){
                if (i == 0)
                {
                    docName = getFullDocName(msg.getRESPONSE().getORDER_OBSERVATION(0).getOBR().getOrderingProvider(i));
                }
                else
                {
                    docName = docName + ", " + getFullDocName(msg.getRESPONSE().getORDER_OBSERVATION(0).getOBR().getOrderingProvider(i));
                }
                i++;
            }
            return(docName);
        }
        catch (Exception e)
        {
            logger.error("Could not return doctor names", e);
            return("");
        }
    }

    public String getCCDocs(){
        String docName = "";
        int i=0;
        try
        {
            while(!getFullDocName(msg.getRESPONSE().getORDER_OBSERVATION(0).getOBR().getResultCopiesTo(i)).equals("")){
                if (i == 0)
                {
                    docName = getFullDocName(msg.getRESPONSE().getORDER_OBSERVATION(0).getOBR().getResultCopiesTo(i));
                }
                else
                {
                    docName = docName + ", " + getFullDocName(msg.getRESPONSE().getORDER_OBSERVATION(0).getOBR().getResultCopiesTo(i));
                }
                i++;
            }
            return(docName);
        }
        catch (Exception e)
        {
            logger.error("Could not return cc'ed doctors", e);
            return("");
        }
    }

    public ArrayList<String> getDocNums(){
        ArrayList<String> docNums = new ArrayList<String>();
        String id;
        int i;

        try
        {
            String providerId = msg.getRESPONSE().getORDER_OBSERVATION(0).getOBR().getOrderingProvider(0).getIDNumber().getValue();
            docNums.add(providerId);
            i=0;
            while ((id = msg.getRESPONSE().getORDER_OBSERVATION(0).getOBR().getResultCopiesTo(i).getIDNumber().getValue()) != null)
            {
                if (!id.equals(providerId))
                {
                    docNums.add(id);
                }
                i++;
            }
        }
        catch(Exception e)
        {
            logger.error("Could not return doctor nums", e);
        }

        return(docNums);
    }


    /*
     *  OBX METHODS
     */
    public int getOBXCount(int i){
        int count;
        try
        {
            count = msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATIONReps();
            // if count is 1 there may only be an nte segment and no obx segments so check
            // We can identify the difference by using the fact that NTE segments have only the identifier
            // whereas an OBX segment will have identifier^text
            if (count == 1)
            {
                String test = msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(0).getOBX().getObservationIdentifier().getText().getValue();
                if (test == null)
                {
                    count = 0;
                }
            }
        }
        catch(Exception e)
        {
            logger.error("Error retrieving obx count", e);
            count = 0;
        }
        return count;
    }

	@Override
	public String getOBXIdentifier(int i, int j)
	{
		try
		{
			return (getString(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getObservationIdentifier().getIdentifier().getValue()));
		}
		catch(Exception e)
		{
			return ("");
		}
	}

	@Override
	public String getOBXValueType(int i, int j)
	{
		try
		{
			return (getString(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getValueType().getValue()));
		}
		catch(Exception e)
		{
			return ("");
		}
	}

	@Override
	public String getOBXName(int i, int j)
	{
		try
		{
			return (getString(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getObservationIdentifier().getText().getValue()));
		}
		catch(Exception e)
		{
			return ("");
		}
	}

	@Override
	public String getOBXResult(int i, int j)
	{
		try
		{
			return (getString(Terser.get(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX(), 5, 0, 1, 1)));
		}
		catch(Exception e)
		{
			return ("");
		}
	}

    /**
     * Very similar to the getOBXResult procedure above, except that some results are
     * not immediately available at the first component.
     * @param i OBR record
     * @param j OBX record
     * @param k component in OBX field
     * @return jth OBX result at kth component from ith OBR record if available, empty string otherwise
     */
    @Override
    public String getOBXResult(int i, int j, int k)
    {
        try
        {
            return (getString(Terser.get(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX(), 5, 0, k, 1)));
        }
        catch (Exception e)
        {
            return "";
        }
    }

    public String getOBXReferenceRange(int i, int j){
        try
        {
            return(getString(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getReferencesRange().getValue()));
        }
        catch (Exception e)
        {
            return("");
        }
    }

    public String getOBXUnits(int i, int j){
        try
        {
            return(getString(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getUnits().getIdentifier().getValue()));
        }
        catch(Exception e)
        {
            return("");
        }
    }

    public String getOBXResultStatus(int i, int j){
        try
        {
            return(getString(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getObservResultStatus().getValue()));
        }
        catch(Exception e)
        {
            return("");
        }
    }

    public int getOBXFinalResultCount(){
        int obrCount = getOBRCount();
        int obxCount;
        int count = 0;
        for (int i = 0; i < obrCount; i++)
        {
            obxCount = getOBXCount(i);
            for (int j = 0; j < obxCount; j++)
            {
                String status = getOBXResultStatus(i, j);
                if (status.equalsIgnoreCase("F") || status.equalsIgnoreCase("C"))
                {
                    count++;
                }
            }
        }


        String orderStatus = getOrderStatus();
        // add extra so final reports are always the ordered as the latest except
        // if the report has been changed in which case that report should be the latest
        if (orderStatus.equalsIgnoreCase("F"))
        {
            count = count + 100;
        }
        else if (orderStatus.equalsIgnoreCase("C"))
        {
            count = count + 150;
        }

        return count;
    }

    public String getTimeStamp(int i, int j){
        try
        {
            return(formatDateTime(getString(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getDateTimeOfTheObservation().getTimeOfAnEvent().getValue())));
        }
        catch(Exception e)
        {
            return("");
        }
    }

    public boolean isOBXAbnormal(int i, int j){
        try
        {
            String abnormalFlag = getOBXAbnormalFlag(i, j);
            if (!abnormalFlag.equals("") && !abnormalFlag.equalsIgnoreCase("N"))
            {
                return(true);
            }
            else
            {
                return(false);
            }
        }
        catch(Exception e)
        {
            return(false);
        }
    }

    public String getOBXAbnormalFlag(int i, int j){
        try
        {
            return(getString(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getAbnormalFlags(0).getValue()));
        }
        catch(Exception e)
        {
            logger.error("Error retrieving obx abnormal flag", e);
            return("");
        }
    }

    public int getOBXCommentCount(int i, int j){
        try
        {
            if (!getOBXComment(i, j, 0).equals("") )
            {
                return(1);
            }
            else
            {
                return(0);
            }
        }
        catch (Exception e)
        {
            return(0);
        }
    }

    public String getOBXComment(int i, int j, int k){
        try
        {
            return(getString(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getNTE(k).getComment(0).getValue()));
        }
        catch (Exception e)
        {
            return("");
        }
    }

	/**
	 *  Retrieve the possible segment headers from the OBR fields
	 */
	public ArrayList<String> getHeaders()
	{
		ArrayList<String> headers = new ArrayList<String>();
		String currentHeader;

		try
		{
			for (int i = 0; i < msg.getRESPONSE().getORDER_OBSERVATIONReps(); i++)
			{
				currentHeader = getObservationHeader(i, 0);
				int arraySize = headers.size();

				if (arraySize == 0 || !currentHeader.equals(headers.get(arraySize - 1)))
				{
					logger.info("Adding header: '" + currentHeader + "' to list");
					headers.add(currentHeader);
				}
			}
			return(headers);
		}
		catch(Exception e)
		{
			logger.error("Could not create header list", e);
			return null;
		}
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

    /*
     * Checks to see if the PATHL7 lab is an unstructured document or a VIHA RTF pathology report
     * labs that fall into any of these categories have certain requirements per Excelleris
     */
    public boolean unstructuredDocCheck(String header){
        return (labDocuments.contains(header));
    }
    public boolean vihaRtfCheck(String header){
        return (header.equals(VIHARTF));
    }

    /**
     * Only commonalities between embedded PDF uploads:
     * - the identifier is "ED", a.k.a. Electronic Document
     * - the PDF message itself is contained in an OBX and takes the form of ^Text^PDF^Base64^...
     * @return true if any OBX in this message contains an embedded PDF
     */
    public boolean hasEmbeddedPDF()
    {
        // First, check legacy case where the OBX is just OBX|1|ED|PDF|...
        if (getOBXValueType(0, 0).equals("ED") && getOBXIdentifier(0, 0).equals("PDF"))
        {
            return true;
        }

        // Check every OBX segment of every OBR segment for
        // OBX|...|ED|...|...|...^TEXT^PDF^Base64^...
        // If match is found, lab contains an embedded PDF
        for (int i = 0; i < getOBRCount(); i++)
        {
            for (int j = 0; j < getOBXCount(i); j++)
            {
                if (getOBXValueType(i, j).equals("ED")
                        && getOBXResult(i, j, 2).equals("TEXT")
                        && getOBXResult(i, j, 3).equals("PDF")
                        && getOBXResult(i, j, 4).equals("Base64"))
                {
                    return true;
                }
            }
        }
        return false;
    }

	@Override
	public List<EmbeddedDocument> getEmbeddedDocuments()
	{
		List<EmbeddedDocument> embeddedDocuments = new LinkedList<>();
		String[] referenceStrings = "^TEXT^PDF^Base64^MSG".split("\\^");
		// Every PDF should be prefixed with this due to b64 encoding of PDF header

		int count = 0;
		for(int i = 0; i < getOBRCount(); i++)
		{
			for(int j = 0; j < getOBXCount(i); j++)
			{
				if(getOBXValueType(i, j).equals("ED"))
				{
					// Some embedded PDFs simply have the lab as-is, some have it split up like above
					for(int k = 1; k <= referenceStrings.length; k++)
					{
						String embeddedPdf = getOBXResult(i, j, k);
						if(embeddedPdf.startsWith(MessageHandler.embeddedPdfPrefix))
						{
							embeddedDocuments.add(toEmbeddedPdf(embeddedPdf, count));
							count++;
						}
					}
				}
			}
		}

		return embeddedDocuments;
	}

    public String getNteForPID(){

        return "";
    }

    /*
     *  END OF PUBLIC METHODS
     */

    /**
     * This should only be used for situations where we have a single OBX message that has an
     * identifier without any associated text. Without associated text the parser returns an OBX count of 0.
     *
     * Example situation: OBX|1|ED|PDF|...
     * If this is the only message in the lab, this would return a count of 0 when we want a count of 1.
     *
     * NOTE: this only modifies the in-memory version. The persisted lab text contains the original text.
     *
     * @param text the text to append to identifier so that it takes the form of "identifier^text"
     */
    private void addOBXIdentifierText(String text) throws HL7Exception
    {
        terser.set("/.ORDER_OBSERVATION(" + 0 + ")/OBSERVATION(" + 0 + ")/OBX-3-2", text);
    }

    @Override
    protected String getString(String retrieve) {
        if (retrieve != null)
        {
            return (retrieve.trim().replaceAll("\\\\\\.br\\\\", "<br />"));
        }
        else
        {
            return ("");
        }
    }

    /**
     * Map OBR order status to Juno internal order status
     * @return - juno internal report status
     */
    @Override
    public Hl7TextInfo.REPORT_STATUS getJunoOrderStatus()
    {
        return orderStatusMap.get(getOrderStatus());
    }

}
