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

import java.text.DateFormat;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Segment;
import ca.uhn.hl7v2.model.v23.datatype.XCN;
import ca.uhn.hl7v2.model.v23.message.ORU_R01;
import ca.uhn.hl7v2.model.v23.segment.OBX;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.util.Terser;
import ca.uhn.hl7v2.validation.impl.NoValidation;
import oscar.util.UtilDateUtilities;

public class GDMLQCHandler  implements MessageHandler {
	
	private static Logger logger = Logger.getLogger(GDMLQCHandler.class);

	private ORU_R01 msg;

	private Terser terser;

	@Override
	public void init(String hl7Body) throws HL7Exception {
		
		/* Normalize string (replace French unicode characters etc.) */
		String normalized = Normalizer.normalize(hl7Body, Normalizer.Form.NFD);
		String resultString = normalized.replaceAll("[^\\x00-\\x7F]", "");
		
		Parser p = new PipeParser();
		p.setValidationContext(new NoValidation());
		msg = (ORU_R01) p.parse(resultString);
		terser = new Terser(msg);		
	}

	@Override
	public String getMsgType() {
		return "GDMLQC";
	}

	/**
	 * This is the OBR date. The MessageHandler architecture uses this to store in hl7TextInfo.obr_date
	 */
	public String getMsgDate() {
		try {
			return (formatDateTime(msg.getRESPONSE().getORDER_OBSERVATION(0).getOBR().getObservationDateTime().getTimeOfAnEvent().getValue()));
		} catch (Exception e) {
			logger.error("Could not retrieve message date", e);
			return ("");
		}
	}

	public Date getMsgDateTime() {
		try {
			String dateFormat = "yyyyMMddHHmmss";
			Date dateTime = UtilDateUtilities.StringToDate(get("/.MSH-7"), dateFormat);
			return dateTime;
		} catch (Exception e) {
			logger.error("Could not retrieve message date", e);
			return null;
		}
	}

	@Override
	public String getMsgPriority() {
		return ("");
	}

	@Override
	public int getOBRCount() {
		return (msg.getRESPONSE().getORDER_OBSERVATIONReps());
	}

	@Override
	public int getOBXCount(int i) {
		try {
			return (msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATIONReps());
		} catch (Exception e) {
			return (0);
		}
	}

	@Override
	public String getOBRName(int i) {
		try {
			return (getString(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBR().getUniversalServiceIdentifier().getAlternateIdentifier().getValue()));
		} catch (Exception e) {
			return ("");
		}
	}

	@Override
	public String getTimeStamp(int i, int j) {
		try {
			return (formatDateTime(getString(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBR().getObservationDateTime().getTimeOfAnEvent().getValue())));
		} catch (Exception e) {
			return ("");
		}
	}

	@Override
	public boolean isOBXAbnormal(int i, int j) {
		String abnormalFlag = getOBXAbnormalFlag(i, j);
        if (abnormalFlag.equals("") || abnormalFlag.equals("N"))
            return(false);
        else
            return(true);
	}

	@Override
	public String getOBXAbnormalFlag(int i, int j) {
		try {
			return (getString(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getAbnormalFlags(0).getValue()));
		} catch (Exception e) {
			return ("");
		}
	}

	@Override
	public String getObservationHeader(int i, int j) {
		return getOBRName(i);
	}

	@Override
	public String getOBXIdentifier(int i, int j) {
        try {
            Segment obxSeg = msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX();
            String ident = getString(Terser.get(obxSeg, 3, 0, 1, 1 ));
            String subIdent = Terser.get(obxSeg, 3, 0, 1, 2);

            if (subIdent != null)
                ident = ident+"&"+subIdent;

            logger.info("returning obx identifier: "+ident);
            return(ident);
        }
        catch(Exception e){
            logger.error("error returning obx identifier", e);
            return("");
        }
	}

	@Override
	public String getOBXValueType(int i, int j) {
        String ret = "";
        try{
        	return (getString(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getValueType().getValue()));
        }catch(Exception e){
            logger.error("Error returning OBX name", e);
        }

        return ret;
	}

	@Override
	public String getOBXName(int i, int j) {
		String result = "";
		try {
            // leave the name blank if the value type is 'FT' this is because it
            // is a comment, if the name is blank the obx segment will not be displayed
            OBX obxSeg =  msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX();
            if (!obxSeg.getValueType().getValue().equals("FT"))
            	result = getString(obxSeg.getObservationIdentifier().getIdentifier().getValue());
		} catch (Exception e) {
			result = "";
		}
		return result;
	}

	@Override
	public String getOBXResult(int i, int j) {
		String result = "";
        try{

            result = getString(Terser.get((msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX()),5,0,1,1));

            // format the result
            if (result.endsWith("."))
                result = result.substring(0, result.length()-1);

        }
        catch(Exception e){
            logger.error("Exception returning result", e);
        }
        return result;
	}

	@Override
	public String getOBXReferenceRange(int i, int j) {
		String ret = "";
		try {
			OBX obxSeg = msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX();

			// If the units are not specified use the formatted reference range
			// which will usually contain the units as well

			if (getOBXUnits(i, j).equals("")) ret = getString(Terser.get(obxSeg, 7, 0, 2, 1));

			// may have to fall back to original reference range if the second
			// component is empty
			if (ret.equals("")) {
				ret = getString(obxSeg.getReferencesRange().getValue());
				if (!ret.equals("")) {
					// format the reference range if using the unformatted one
					String[] ranges = ret.split("-");
					for (int k = 0; k < ranges.length; k++) {
						if (ranges[k].endsWith(".")) ranges[k] = ranges[k].substring(0, ranges[k].length() - 1);
					}

					if (ranges.length > 1) {
						if (ranges[0].contains(">") || ranges[0].contains("<")) ret = ranges[0] + "= " + ranges[1];
						else ret = ranges[0] + " - " + ranges[1];
					}
					else if (ranges.length == 1) {
						ret = ranges[0] + " -";
					}
				}
			}
		}
		catch (Exception e) {
			logger.error("Exception retrieving reception range", e);
		}
		return getString(ret);
	}

	@Override
	public String getOBXUnits(int i, int j) {
		String ret = "";
		try {
			OBX obxSeg = msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX();
			ret = getString(obxSeg.getUnits().getIdentifier().getValue());

			// if there are no units specified check the formatted reference
			// range for the units
			if (ret.equals("")) {
				ret = getString(Terser.get(obxSeg, 7, 0, 2, 1));

				// only display units from the formatted reference range if they
				// have not already been displayed as the reference range
				if (ret.contains("-") || ret.contains("<") || ret.contains(">") || ret.contains("NEGATIVE")) ret = "";
			}
		}
		catch (Exception e) {
			logger.error("Exception retrieving units", e);
		}
        return getString(ret);
	}

	@Override
	public String getOBXResultStatus(int i, int j){
        try{

            // result status is stored in the wrong field.... i think
            return(getString(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getNatureOfAbnormalTest().getValue()));
        }catch(Exception e){
            logger.error("Exception retrieving results status", e);
            return("");
        }
    }

	@Override
    public int getOBXFinalResultCount(){
        // not applicable to gdml labs
        return 0;
    }

    /**
     *  Retrieve the possible segment headers from the OBX fields
     */
    public ArrayList<String> getHeaders(){
		ArrayList<String> headers = new ArrayList<String>();
		for (int i = 0; i < getOBRCount(); i++) {
			String obrName = getOBRName(i);
			if (!headers.contains(obrName)) {
				headers.add(obrName);
			}
		}
		return headers;
    }

    /**
     *  Methods to get information from observation notes
     */
    @Override
    public int getOBRCommentCount(int i){
        int count = 0;

        for (int j=0; j < getOBXCount(i); j++){
            if (getString(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getValueType().getValue()).equals("FT"))
                count++;
        }

        return count;

    }

    @Override
    public String getOBRComment(int i, int j){
        String comment = "";

        // update j to the number of the comment not the index of a comment array
        j++;
        try {
            int obxCount = getOBXCount(i);
            int count = 0;
            int l = 0;
            OBX obxSeg = null;

            while ( l < obxCount && count < j){

                obxSeg = msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(l).getOBX();
                if (getString(obxSeg.getValueType().getValue()).equals("FT")){
                    count++;
                }
                l++;

            }
            l--;

            int k = 0;
            String nextComment = Terser.get(obxSeg,5,k,1,1);
            while(nextComment != null){
                comment = comment + getString(nextComment);
                k++;
                nextComment = Terser.get(obxSeg,5,k,1,1);
            }

        } catch (Exception e) {
            logger.error("getOBRComment error", e);
            comment = "";
        }
        return comment;
    }

	/**
	 * Methods to get information from observation notes
	 */
	@Override
	public int getOBXCommentCount(int i, int j) {
		int count = 0;
		try {
			count = msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getNTEReps();

			// a bug in getNTEReps() causes it to return 1 instead of 0 so we check to make
			// sure there actually is a comment there
			if (count == 1) {
				String comment = msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getNTE().getComment(0).getValue();
				if (comment == null) count = 0;
			}

		} catch (Exception e) {
			logger.error("Error retrieving obx comment count", e);
		}
		return count;
	}

	@Override
	public String getOBXComment(int i, int j, int k) {
		try {
			return (getString(
					msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getNTE(k).getComment(0).getValue()));
		}
		catch (Exception e) {
			return ("");
		}
	}


    /**
     *  Methods to get information about the patient
     */
    @Override
    public String getPatientName(){
        return(getFirstName()+" "+getLastName());
    }

    @Override
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
            logger.error("Exception retrieving DOB", e);
            return("");
        }
    }

    @Override
    public String getAge(){
        String age = "N/A";
        String dob = getDOB();
        try {
            // Some examples
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date dobDate = formatter.parse(dob);
            java.util.Date serviceDate = formatter.parse(getServiceDate());
            age = UtilDateUtilities.calcAgeAtDate(dobDate, serviceDate);
        } catch (ParseException e) {
            logger.error("Could not get age", e);

        }
        return age;
    }

    @Override
    public String getSex(){
        return(getString(msg.getRESPONSE().getPATIENT().getPID().getSex().getValue()));
    }

    @Override
    public String getHealthNum(){
        return(getString(msg.getRESPONSE().getPATIENT().getPID().getPatientIDExternalID().getID().getValue()));
    }

    @Override
    public String getHomePhone(){
        String phone = "";
        int i=0;
        try{
            while(!getString(msg.getRESPONSE().getPATIENT().getPID().getPhoneNumberHome(i).get9999999X99999CAnyText().getValue()).equals("")){
                if (i==0){
                    phone = getString(msg.getRESPONSE().getPATIENT().getPID().getPhoneNumberHome(i).get9999999X99999CAnyText().getValue());
                }else{
                    phone = phone + ", " + getString(msg.getRESPONSE().getPATIENT().getPID().getPhoneNumberHome(i).get9999999X99999CAnyText().getValue());
                }
                i++;
            }
            return(phone);
        }catch(Exception e){
            logger.error("Could not return phone number", e);

            return("");
        }
    }

    @Override
    public String getWorkPhone(){
        String phone = "";
        int i=0;
        try{
            while(!getString(msg.getRESPONSE().getPATIENT().getPID().getPhoneNumberBusiness(i).get9999999X99999CAnyText().getValue()).equals("")){
                if (i==0){
                    phone = getString(msg.getRESPONSE().getPATIENT().getPID().getPhoneNumberBusiness(i).get9999999X99999CAnyText().getValue());
                }else{
                    phone = phone + ", " + getString(msg.getRESPONSE().getPATIENT().getPID().getPhoneNumberBusiness(i).get9999999X99999CAnyText().getValue());
                }
                i++;
            }
            return(phone);
        }catch(Exception e){
            logger.error("Could not return phone number", e);

            return("");
        }
    }

    @Override
    public String getPatientLocation(){
        return(getString(msg.getMSH().getSendingFacility().getNamespaceID().getValue()));
    }

    @Override
    public String getServiceDate(){
        try{
            return(formatDateTime(getString(msg.getRESPONSE().getORDER_OBSERVATION(0).getOBR().getObservationDateTime().getTimeOfAnEvent().getValue())));
        }catch(Exception e){
            logger.error("Exception retrieving service date", e);
            return("");
        }
    }

    @Override
    public String getRequestDate(int i){
        try{
            String ret = msg.getRESPONSE().getORDER_OBSERVATION(i).getOBR().getRequestedDateTime().getTimeOfAnEvent().getValue();
            return(formatDateTime(getString(ret)));
        }catch(Exception e){
            logger.error("Exception retrieving request date", e);
            return("");
        }
    }

    @Override
	public String getOrderStatus() {
		// gdml won't send pending labs... 
		// they'll send only the final parts of the labs
		return ("F");
	}

    public String getClientRef(){
        try{
            return(getString(msg.getRESPONSE().getPATIENT().getPID().getPatientIDInternalID(0).getAssigningAuthority().getNamespaceID().getValue()));
        }catch(Exception e){
            logger.error("Could not return accession num: ", e);
            return("");
        }
    }

    @Override
    public String getAccessionNum(){
        try{
            return(getString(msg.getRESPONSE().getPATIENT().getPID().getPatientIDInternalID(0).getID().getValue()));
        }catch(Exception e){
            logger.error("Could not return accession num: ", e);
            return("");
        }
    }

    @Override
    public String getDocName(){
        String docName = "";
        int i=0;
        try{
            while(!getFullDocName(msg.getRESPONSE().getORDER_OBSERVATION(0).getOBR().getOrderingProvider(i)).equals("")){
                if (i==0){
                    docName = getFullDocName(msg.getRESPONSE().getORDER_OBSERVATION(0).getOBR().getOrderingProvider(i));
                }else{
                    docName = docName + ", " + getFullDocName(msg.getRESPONSE().getORDER_OBSERVATION(0).getOBR().getOrderingProvider(i));
                }
                i++;
            }
            return(docName);
        }catch(Exception e){
            logger.error("Could not return doctor names", e);

            return("");
        }
    }

    @Override
    public String getCCDocs(){

        String docNames = "";

        try {
            Terser terser = new Terser(msg);

            String givenName = terser.get("/.ZDR(0)-4-1");
            String middleName = terser.get("/.ZDR(0)-4-3");
            String familyName = terser.get("/.ZDR(0)-4-2");

            int i=1;
            while (givenName != null){

                if (i==1)
                    docNames = givenName;
                else
                    docNames = docNames+", "+givenName;

                if (middleName != null)
                    docNames = docNames+" "+middleName;
                if (familyName != null)
                    docNames = docNames+" "+familyName;

                givenName = terser.get("/.ZDR("+i+")-4-1");
                middleName = terser.get("/.ZDR("+i+")-4-3");
                familyName = terser.get("/.ZDR("+i+")-4-2");

                i++;
            }

            return(docNames);

        } catch (Exception e) {
            //ignore error... it will occur when the zdr segment is not present
            //logger.error("Could not retrieve cc'd docs", e);
            return("");
        }

    }

    @Override
    public ArrayList<String> getDocNums(){
        String docNum = "";
        ArrayList<String> nums = new ArrayList<String>();
        int i=0;
        try{

            //requesting client number
            docNum = msg.getRESPONSE().getORDER_OBSERVATION(0).getOBR().getOrderingProvider(i).getIDNumber().getValue();
            if (docNum != null){
               nums.add(docNum);
            }

            //cc'd docs numbers
            Terser terser = new Terser(msg);
            String num = terser.get("/.ZDR(0)-3-1");
            i=1;
            while (num != null){
                if (!num.equals(docNum))
                    nums.add(num);
                num = terser.get("/.ZDR("+i+")-3-1");
                i++;
            }

        }catch(Exception e){
            //ignore error... it will occur when the zdr segment is not present
            //logger.error("Could not return numbers", e);
        }

        return(nums);
    }

    @Override
    public String audit(){
        return "";
    }

    private String getFullDocName(XCN docSeg){
        String docName = "";

        if(docSeg.getPrefixEgDR().getValue() != null)
            docName = docSeg.getPrefixEgDR().getValue();

        if(docSeg.getGivenName().getValue() != null){
            if (docName.equals(""))
                docName = docSeg.getGivenName().getValue();
            else
                docName = docName +" "+ docSeg.getGivenName().getValue();

        }
        if(docSeg.getMiddleInitialOrName().getValue() != null){
            if (docName.equals(""))
                docName = docSeg.getMiddleInitialOrName().getValue();
            else
                docName = docName +" "+ docSeg.getMiddleInitialOrName().getValue();

        }
        if(docSeg.getFamilyName().getValue() != null){
            if (docName.equals(""))
                docName = docSeg.getFamilyName().getValue();
            else
                docName = docName +" "+ docSeg.getFamilyName().getValue();

        }
        if(docSeg.getSuffixEgJRorIII().getValue() != null){
            if (docName.equals(""))
                docName = docSeg.getSuffixEgJRorIII().getValue();
            else
                docName = docName +" "+ docSeg.getSuffixEgJRorIII().getValue();
        }
        if(docSeg.getDegreeEgMD().getValue() != null){
            if (docName.equals(""))
                docName = docSeg.getDegreeEgMD().getValue();
            else
                docName = docName +" "+ docSeg.getDegreeEgMD().getValue();
        }

		return (docName);
	}

	@Override
	public String getFillerOrderNumber() {

		return "";
	}

	@Override
	public String getEncounterId() {
		return "";
	}
	@Override
	public String getRadiologistInfo() {
		return "";
	}

	@Override
	public String getNteForOBX(int i, int j) {
		
		String nteSeg = msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getNTE().getComment(0).getValue();
		return nteSeg;
	}
	@Override
	public String getNteForPID() {
		try {
			String comments = new String();
			int CommentsCount = msg.getRESPONSE().getPATIENT().getNTEReps();
			for (int i = 0; i < CommentsCount; i++) {
				comments += (i + 1) + "-" + msg.getRESPONSE().getPATIENT().getNTE(i).getSourceOfComment().getValue()
						+ ". ";
			}
			return comments;
		}
		catch (Exception e) {
			logger.error("Could not load Nte segment of patient: ", e);
			return "";
		}
	}

	/* PRIVATE METHODS */

	private String get(String path) {
		try {
			return terser.get(path);
		}
		catch (HL7Exception e) {
			logger.warn("Unable to get field at " + path, e);
			return null;
		}
	}
	protected String formatDateTime(String plain) {
		if (plain == null || plain.trim().equals("")) return "";

		String dateFormat = "yyyyMMddHHmmss";
		dateFormat = dateFormat.substring(0, plain.length());
		String stringFormat = "yyyy-MM-dd HH:mm:ss";
		stringFormat = stringFormat.substring(0,
				stringFormat.lastIndexOf(dateFormat.charAt(dateFormat.length() - 1)) + 1);

		Date date = UtilDateUtilities.StringToDate(plain, dateFormat);
		return UtilDateUtilities.DateToString(date, stringFormat);
	}
	protected String getString(String retrieve) {
		if (retrieve != null) {
			retrieve = retrieve.replaceAll("^", " ");
			return (retrieve.trim().replaceAll("\\\\\\.br\\\\", "<br />"));
		}
		else {
			return ("");
		}
	}
}
