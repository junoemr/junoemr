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

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v23.datatype.XCN;
import ca.uhn.hl7v2.model.v23.segment.MSH;
import ca.uhn.hl7v2.util.Terser;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import oscar.util.UtilDateUtilities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

/**
 *  When implementing this class a global variable 'msg' should be created as
 *  follows:
 *      ORU_R01 msg = null;
 *
 *  ORU_R01 is located at 'ca.uhn.hl7v2.model.vXX.message.ORU_R01' where 'vXX'
 *  is the version specified by the hl7 messages that you inted to parse, it is
 *  stored in the 12th field of the MSH segment.
 *
 *  'msg' should be initialized in the init(String hl7Body) method
 *
 *  The results for the majority of the methods should be retrieved from the
 *  'msg' object
 */
public abstract class MessageHandler {

	private static Logger logger = Logger.getLogger(MessageHandler.class);

	protected Terser terser;

	/**
	 * This method is run before labs are uploaded/saved to the database
	 * This is where duplicates can be checked, merged, or rejected based on each lab's specifications before the lab is routed
	 * @param hl7Message - the hl7 string for the message
	 * @return - the same hl7 message as the parameter with any required modifications
	 */
	public abstract String preUpload(String hl7Message) throws HL7Exception;
	/**
	 * This method should determine if the lab can be routed
	 * @return true if the lab can be routed, false otherwise
	 */
	public abstract boolean canUpload();

	/**
	 * This gets run after the lab is routed
	 */
	public abstract void postUpload();


    /* ===================================== Hl7 Parsing ====================================== */


	/**
	 * Each handler should implement this method.
	 * This method should return true if it should use this instance of MessageHandler to parse the lab
	 * @param messageHeaderSegment hl7 MSH header
	 * @return true if the header segment belongs to this lab parser. false otherwise
	 */
	@SuppressWarnings("unused")
	protected static boolean headerTypeMatch(MSH messageHeaderSegment)
	{
		return true;
	}

	/**
	 * default constructor. init must be called after instantiation.
	 */
	public MessageHandler()
	{
	}

	/**
	 * constructor which also calls init
	 * @param hl7Body - hl7 formatted string
	 * @throws HL7Exception if init fails
	 */
	public MessageHandler(String hl7Body) throws HL7Exception
	{
		init(hl7Body);
	}

    /**
     *  Initialize the 'msg' object and any other global variables that may be
     *  needed.
     *
     *  The 'msg' object should be initialized with the following code:
     *       Parser p = new PipeParser();
     *       p.setValidationContext(new NoValidation());
     *       msg = (ORU_R01) p.parse(hl7Body.replaceAll( "\n", "\r\n" ));

     *	- If you wish to validate the message please see the hapi documentation at
     *		'http://hl7api.sourceforge.net/'
     *  - The replaceAll statement is necessary to ensure that the parser
     *  correctly reads the end of each line.
     */
    public abstract void init(String hl7Body) throws HL7Exception;


    /* ===================================== MSH ====================================== */
    /**
     *  Return the message type
     *  - The message type returned should be the same as the prefix of your
     *  handlers name
     *      ie/ message type = XXXX
     *          handler name = XXXXHandler
     */
    public abstract String getMsgType();

    /**
     *  Return the date and time of the message, usually located in the 7th
     *  field of the MSH segment
     */
    public String getMsgDate()
    {
	    return formatDateTime(get("/.MSH-7"));
    }

    /**
     *  A String containing a single letter represinting the priority
     *		"C" - Critical 		"S" - Stat/Urgent
     *		"U" - Unclaimed 	"A" - ASAP
     *		"L" - Alert 		""  - Routine
     *  If there is no priority specified in the documentation for your message
     *  type then just return the empty string ""
     */
    public String getMsgPriority()
    {
    	return "";
    }

	/**
	 *  Return the patients location, usually the facility from which the
	 *  report has been sent ( the 4th field of the MSH segment )
	 */
	public String getPatientLocation()
	{
		return getString(get("/.MSH-4"));
	}

    /* ===================================== PID ====================================== */

	/**
	 *  Return the name of the patient. The format should be the first name
	 *  followed by the last name while being separated by a space.
	 *  String firstName = getFirstName();
	 *  String lastName = getLastName();
	 */
	public String getPatientName()
	{
		return(getFirstName()+" "+getMiddleName()+" "+getLastName());
	}

	/**
	 *  Return the patients health number
	 */
	public String getHealthNum()
	{
		return getString(get("/.PID-2"));
	}

	/**
	 * Return the family name of the patient
	 */
	public String getLastName()
	{
		return getString(get("/.PID-5-1"));
	}
	/**
	 *  Return the given name of the patient
	 */
	public String getFirstName()
	{
		return getString(get("/.PID-5-2"));
	}

	/**
	 * Return the middle name of the patient
	 */
	public String getMiddleName()
	{
		return getString(get("/.PID-5-3"));
	}

	/**
	 *  Return the patients date of birth
	 */
	public String getDOB()
	{
		return formatDateTime(getString(get("/.PID-7"))).substring(0, 10);
	}
	/**
	 *  Return the gender of the patient: 'M' or 'F'
	 */
	public String getSex()
	{
		return getString(get("/.PID-8"));
	}



	protected String getBuisnessPhone(int i) throws HL7Exception
	{
		return getString(get("/.PID-13-"+i));
	}
	protected String getHomePhone(int i) throws HL7Exception
	{
		return getString(get("/.PID-14-"+i));
	}
	/**
	 *  Return the home phone number of the patient
	 */
	public String getHomePhone()
	{
		String phone = "";
		int i = 1;
		try
		{
			while (!getHomePhone(i).equals(""))
			{
				if (i == 1)
				{
					phone = getHomePhone(i);
				}
				else
				{
					phone = phone + ", " + getHomePhone(i);
				}
				i++;
			}
			return (phone);
		}
		catch (Exception e)
		{
			logger.error("Could not return phone number", e);

			return ("");
		}
	}
	/**
	 *  Return the work phone number of the patient
	 */
	public String getWorkPhone()
	{
		String phone = "";
		int i = 1;
		try
		{
			while (!getBuisnessPhone(i).equals(""))
			{
				if (i == 1)
				{
					phone = getBuisnessPhone(i);
				}
				else
				{
					phone = phone + ", " + getBuisnessPhone(i);
				}
				i++;
			}
			return (phone);
		}
		catch (Exception e)
		{
			logger.error("Could not return phone number", e);

			return ("");
		}
	}


	/* ===================================== OBR ====================================== */

    /**
     *  Return the number of OBR Segments in the message
     */
	public abstract int getOBRCount();

    /**
     *  Return the name of the ith OBR Segment, usually stored in the
     *  UniversalServiceIdentifier
     */
    public String getOBRName(int i)
    {
    	return getString(get("/.OBR("+i+")-4"));
    }

	/**
	 *  Return the number of comments (usually NTE segments) that follow ith
	 *  OBR segment, this should usually be either 0 or 1.
	 */
	public abstract int getOBRCommentCount(int i);

	/**
	 *  Return the jth comment of the ith OBR segment.
	 */
	public abstract String getOBRComment(int i, int j);

	/**
	 *  Return the observation header which represents the observation stored in
	 *  the jth OBX segment of the ith OBR group. May be stored in either the
	 *  OBR or OBX segment. It is used to separate the observations into groups.
	 *  ie/ 'CHEMISTRY' 'HEMATOLOGY' '
	 */
	public abstract String getObservationHeader( int i, int j);

	/**
	 *  Return a list of all possible headers retrieved from getObservationHeader
	 *  each header will only occur once in the list
	 */
	public ArrayList<String> getHeaders()
	{
		HashSet<String> headers = new HashSet<>();
		for (int i = 0; i < getOBRCount(); i++) {
			// duplicates are ignored in hash set add
			headers.add(getOBRName(i));
		}
		return new ArrayList<>(headers);
	}

    /* ===================================== OBX ====================================== */

	/**
	 *  Return the date and time of the observation referred to by the jth obx
	 *  segment of the ith obr group. If the date and time is not specified
	 *  within the obx segment it should be specified within the obr segment.
	 */
	public abstract String getTimeStamp(int i, int j);

	/**
	 *  Return the number of OBX Segments within the OBR group specified by i.
	 */
	public abstract int getOBXCount(int i);

    /**
     *  Return true if an abnormal flag other than 'N' is returned by
     *  getOBXAbnormalFlag( i, j ) for the OBX segment specified by j, in the
     *  ith OBR group. Return false otherwise.
     */
    public boolean isOBXAbnormal(int i, int j)
    {
    	String abnormalFlags = getOBXAbnormalFlag(i,j);
	    return !("N".equals(abnormalFlags));
    }

    /**
     *  Retrieve the abnormal flag if any from the OBX segment specified by j in
     *  the ith OBR group.
     */
    public String getOBXAbnormalFlag( int i, int j)
    {
	    return getString(get("/.ORDER_OBSERVATION("+i+")/OBSERVATION("+j+")/OBX-8"));
    }

    /**
     * Return the obx value type
     * @param i
     * @param j
     * @return String the obx value
     */
	public String getOBXValueType(int i, int j)
	{
		return getString(get("/.ORDER_OBSERVATION("+i+")/OBSERVATION("+j+")/OBX-2"));
	}

	/**
	 *  Return the identifier from jth OBX segment of the ith OBR group. It is
	 *  usually stored in the first component of the third field of the OBX
	 *  segment.
	 */
	public String getOBXIdentifier(int i, int j)
	{
		return getString(get("/.ORDER_OBSERVATION("+i+")/OBSERVATION("+j+")/OBX-3-1"));
	}

    /**
     *  Return the name of the jth OBX segment of the ith OBR group. It is
     *  usually stored in the second component of the third field of the OBX
     *  segment.
     */
    public String getOBXName( int i, int j)
    {
	    return getString(get("/.ORDER_OBSERVATION("+i+")/OBSERVATION("+j+")/OBX-3-2"));
    }

    /**
     *  Return the result from the jth OBX segment of the ith OBR group
     */
    public String getOBXResult(int i, int j)
    {
	    return getString(get("/.ORDER_OBSERVATION("+i+")/OBSERVATION("+j+")/OBX-5"));
    }

	/**
	 *  Return the units from the jth OBX segment of the ith OBR group
	 */
	public String getOBXUnits( int i, int j)
	{
		return getString(get("/.ORDER_OBSERVATION("+i+")/OBSERVATION("+j+")/OBX-6"));
	}

    /**
     *  Return the reference range from the jth OBX segment of the ith OBR group
     */
    public String getOBXReferenceRange( int i, int j)
    {
	    return getString(get("/.ORDER_OBSERVATION("+i+")/OBSERVATION("+j+")/OBX-7"));
    }

    /**
     *  Return the result status from the jth OBX segment of the ith OBR group
     */
    public String getOBXResultStatus( int i, int j)
    {
	    return getString(get("/.ORDER_OBSERVATION("+i+")/OBSERVATION("+j+")/OBX-11"));
    }

    /**
     *  Return the number of comments (usually NTE segments) following the jth
     *  OBX segment of the ith OBR group.
     */
    public abstract int getOBXCommentCount( int i, int j);

    /**
     *  Return the kth comment of the jth OBX segment of the ith OBR group
     */
    public abstract String getOBXComment( int i, int j, int k);

	/**
	 *  Returns the number used to order labs with matching accession numbers.
	 *
	 *  - Multiple labs with the same accession number must display in a certain
	 *  order. They are ordered by their date but if two labs with the same
	 *  accession number have the same date they are ordered by the number
	 *  retrievied by this method
	 *
	 *  - The newest lab will have the greatest number returned from this method.
	 *
	 *  - If the hl7 messages do not contain a version number or other such
	 *  number, the total number of obx segments with final results should be
	 *  returned
	 */
	public abstract int getOBXFinalResultCount();


	/* ===================================== MISC ====================================== */


    /**
     *  Return the service date of the message
     */
    public abstract String getServiceDate();

    /**
     *  Return the request date of the message
     */
    public abstract String getRequestDate(int i);

    /**
     *  Return the status of the report, 'F' is returned for a final report,
     *  otherwise the report is partial
     */
    public abstract String getOrderStatus();

    /**
     *  Return the clients reference number, usually corresponds to the doctor
     *  who requested the report or the requesting facility.
     */
    public abstract String getClientRef();

    /**
     *  Return the accession number
     */
    public abstract String getAccessionNum();

    /**
     *  Return the name of the doctor who requested the report, the name should
     *  be formatted as follows:
     *      'PREFIX' 'GIVEN NAME' 'MIDDLE INITIALS' 'FAMILY NAME' 'SUFFIX' 'DEGREE'
     */
    public abstract String getDocName();

    /**
     *  Return the names of the doctors which the report should be copied to. The
     *  formatting of the names should be the same as in the method above. The
     *  names should be separated by a comma and a space.
     */
    public abstract String getCCDocs();

    /**
     *  Return an ArrayList of the requesting doctors billing number and the
     *  billing numbers of the cc'd docs
     */
    public abstract ArrayList getDocNums();

    /**
     * Returns a string audit of the messages.  If not required handler should just return an empty string;
     */
    public String audit()
    {
    	return "";
    }

    public abstract String getFillerOrderNumber();

    public abstract String getEncounterId();

    public abstract String getRadiologistInfo();

    public abstract String getNteForOBX(int i,int j);
    
    public abstract String getNteForPID();

	public abstract boolean isUnstructured();

	/* ================================== Extra Methods and helpers ==================================== */

	/**
	 * use the terser to retrieve segment info, with exception handling
	 * @param path - terser formatted segment path
	 * @return String value of the segment, or null if parsing the path failed
	 */
	protected String get(String path)
	{
		try
		{
			return terser.get(path);
		}
		catch(HL7Exception e)
		{
			logger.warn("Unable to get field at " + path, e);
			return null;
		}
	}

	protected String formatDateTime(String plain)
	{
		if(plain == null || plain.trim().equals("")) return "";

		String dateFormat = "yyyyMMddHHmmss";
		dateFormat = dateFormat.substring(0, plain.length());
		String stringFormat = "yyyy-MM-dd HH:mm:ss";
		stringFormat = stringFormat.substring(0, stringFormat.lastIndexOf(dateFormat.charAt(dateFormat.length() - 1)) + 1);

		Date date = UtilDateUtilities.StringToDate(plain, dateFormat);
		return UtilDateUtilities.DateToString(date, stringFormat);
	}
	public String getAge()
	{
		String age = "N/A";
		String dob = getDOB();
		try
		{
			// Some examples
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date date = formatter.parse(dob);
			age = UtilDateUtilities.calcAge(date);
		}
		catch(ParseException e)
		{
			logger.error("Could not get age", e);
		}
		return age;
	}

	protected String getString(String retrieve)
	{
		return StringUtils.trimToEmpty(retrieve);
	}

	protected String getFullDocName(XCN docSeg) {
		String docName = "";

		if (docSeg.getPrefixEgDR().getValue() != null) docName = docSeg.getPrefixEgDR().getValue();

		if (docSeg.getGivenName().getValue() != null) {
			if (docName.equals("")) docName = docSeg.getGivenName().getValue();
			else docName = docName + " " + docSeg.getGivenName().getValue();
		}
		if (docSeg.getMiddleInitialOrName().getValue() != null) {
			if (docName.equals("")) docName = docSeg.getMiddleInitialOrName().getValue();
			else docName = docName + " " + docSeg.getMiddleInitialOrName().getValue();
		}
		if (docSeg.getFamilyName().getValue() != null) {
			if (docName.equals("")) docName = docSeg.getFamilyName().getValue();
			else docName = docName + " " + docSeg.getFamilyName().getValue();
		}
		if (docSeg.getSuffixEgJRorIII().getValue() != null) {
			if (docName.equals("")) docName = docSeg.getSuffixEgJRorIII().getValue();
			else docName = docName + " " + docSeg.getSuffixEgJRorIII().getValue();
		}
		if (docSeg.getDegreeEgMD().getValue() != null) {
			if (docName.equals("")) docName = docSeg.getDegreeEgMD().getValue();
			else docName = docName + " " + docSeg.getDegreeEgMD().getValue();
		}

		return (docName);
	}

	protected String getFullDocNameV25(ca.uhn.hl7v2.model.v25.datatype.XCN xcn) {
		String docName = "";

		if (xcn.getPrefixEgDR().getValue() != null) docName = xcn.getPrefixEgDR().getValue();

		if (xcn.getGivenName().getValue() != null) {
			if (docName.equals("")) docName = xcn.getGivenName().getValue();
			else docName = docName + " " + xcn.getGivenName().getValue();

		}
		if (xcn.getSecondAndFurtherGivenNamesOrInitialsThereof().getValue() != null) {
			if (docName.equals("")) docName = xcn.getSecondAndFurtherGivenNamesOrInitialsThereof().getValue();
			else docName = docName + " " + xcn.getSecondAndFurtherGivenNamesOrInitialsThereof().getValue();
		}
		if (xcn.getFamilyName().getSurname().getValue() != null) {
			if (docName.equals("")) docName = xcn.getFamilyName().getSurname().getValue();
			else docName = docName + " " + xcn.getFamilyName().getSurname().getValue();

		}
		if (xcn.getSuffixEgJRorIII().getValue() != null) {
			if (docName.equals("")) docName = xcn.getSuffixEgJRorIII().getValue();
			else docName = docName + " " + xcn.getSuffixEgJRorIII().getValue();
		}
		if (xcn.getDegreeEgMD().getValue() != null) {
			if (docName.equals("")) docName = xcn.getDegreeEgMD().getValue();
			else docName = docName + " " + xcn.getDegreeEgMD().getValue();
		}

		return docName;
	}
}
