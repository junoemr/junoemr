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

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Group;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.util.SegmentFinder;
import ca.uhn.hl7v2.util.Terser;
import ca.uhn.hl7v2.validation.impl.NoValidation;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.oscarehr.common.dao.Hl7TextInfoDao;
import org.oscarehr.util.SpringUtils;
import oscar.util.UtilDateUtilities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * This is the main class for parsing hl7 text values.
 * This class is meant to provide default getters for all hl7 labs based on the hl7 spec
 * Individual labs will have unique usages of some hl7 fields, and lab handlers should extend this class.
 *
 * This class uses the terser to traverse hl7 messages without specific version requirements,
 * and it is recommended whenever possible to avoid versioned handlers unless necessary.
 */
public abstract class MessageHandler {

	private static Logger logger = Logger.getLogger(MessageHandler.class);

	protected Hl7TextInfoDao hl7TextInfoDao = SpringUtils.getBean(Hl7TextInfoDao.class);

	protected Terser terser;
	protected Message message;

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
		HapiContext context = new DefaultHapiContext();
		context.setValidationContext(new NoValidation());
		Parser p = context.getPipeParser();

		Message msg = p.parse(hl7Body);
		this.message = msg;
		this.terser = new Terser(msg);
	}
	public MessageHandler(Message msg) throws HL7Exception
	{
		this.message = msg;
		this.terser = new Terser(msg);
	}

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
	 * This method should return true if this instance of MessageHandler can parse the lab
	 * @param message hl7 Message used for determining lab parser type
	 * @return true if this parser matches the lab type. false otherwise
	 */
	@SuppressWarnings("unused")
	protected static boolean handlerTypeMatch(Message message)
	{
		return true;
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
     *
     *  @deprecated - initialization should be done in constructors.
     */
    @Deprecated
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

	/**
	 *  Return the accession number
	 */
	public abstract String getAccessionNum();

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
		return formatDate(getString(get("/.PID-7")));
	}
	/**
	 *  Return the gender of the patient: 'M' or 'F'
	 */
	public String getSex()
	{
		return getString(get("/.PID-8"));
	}

	protected String getBusinessPhone(int i) throws HL7Exception
	{
		return getString(get("/.PID-14-"+i));
	}
	protected String getHomePhone(int i) throws HL7Exception
	{
		return getString(get("/.PID-13-"+i));
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
			while (!getHomePhone(i).isEmpty())
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
			while (!getBusinessPhone(i).isEmpty())
			{
				if (i == 1)
				{
					phone = getBusinessPhone(i);
				}
				else
				{
					phone = phone + ", " + getBusinessPhone(i);
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

	public abstract String getNteForPID();


	/* ===================================== OBR ====================================== */

    /**
     *  Return the number of OBR Segments in the message
     */
	public int getOBRCount()
	{
		return getReps("RESPONSE", 0, "ORDER_OBSERVATION");
	}

	public String getFillerOrderNumber()
	{
		return getString(get("/.OBR-3"));
	}

	/**
	 *  Return the name of the ith OBR Segment, usually stored in the
	 *  UniversalServiceIdentifier
	 */
	public String getOBRName(int i)
	{
		return getString(get("/.ORDER_OBSERVATION("+i+")/OBR-4-2"));
	}

	/**
	 *  Return the request date of the message
	 */
	public String getRequestDate(int i)
	{
		return formatDateTime(get("/.ORDER_OBSERVATION("+i+")/OBR-6"));
	}

	/**
	 *  Return the service date of the message
	 */
	public String getServiceDate()
	{
		return formatDateTime(get("/.OBR(0)-14"));
	}

	/**
	 *  Return the status of the report, 'F' is returned for a final report,
	 *  otherwise the report is partial
	 */
	public String getOrderStatus()
	{
		return getString(get("/.OBR(0)-25"));
	}

	/**
	 *  Return the number of comments (usually NTE segments) that follow ith
	 *  OBR segment, this should usually be either 0 or 1.
	 */
	public int getOBRCommentCount(int i)
	{
		return getReps("ORDER_OBSERVATION", i, "NTE");
	}

	/**
	 *  Return the kth comment of the ith OBR segment.
	 */
	public String getOBRComment(int i, int k)
	{
		return getString(get("/.ORDER_OBSERVATION("+i+")/NTE("+k+")-3"));
	}

	/**
	 *  Return the observation header which represents the observation stored in
	 *  the jth OBX segment of the ith OBR group. May be stored in either the
	 *  OBR or OBX segment. It is used to separate the observations into groups.
	 *  ie/ 'CHEMISTRY' 'HEMATOLOGY' '
	 */
	public String getObservationHeader(int i, int j)
	{
		return getOBRName(i);
	}

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

	protected String getClientRef(int i, int k) throws HL7Exception
	{
		return getString(get("/.ORDER_OBSERVATION("+i+")/OBR-16("+k+")-1"));
	}
	/**
	 *  Return the clients reference number, usually corresponds to the doctor
	 *  who requested the report or the requesting facility.
	 */
	public String getClientRef() {
		String docNum = "";
		int i = 0;
		try {
			while (!getClientRef(0,i).isEmpty()) {
				if (i == 0) {
					docNum = getClientRef(0,i);
				} else {
					docNum = docNum + ", " + getClientRef(0,i);
				}
				i++;
			}
			return (docNum);
		} catch (Exception e) {
			logger.error("Could not return doctor id numbers", e);
			return ("");
		}
	}

	protected String getOrderingProvider(int i, int k) throws HL7Exception
	{
		String familyName = getString(get("/.ORDER_OBSERVATION("+i+")/OBR-16("+k+")-2"));
		String givenName = getString(get("/.ORDER_OBSERVATION("+i+")/OBR-16("+k+")-3"));
		String middleName = getString(get("/.ORDER_OBSERVATION("+i+")/OBR-16("+k+")-4"));
		String suffix = getString(get("/.ORDER_OBSERVATION("+i+")/OBR-16("+k+")-5"));
		String prefix = getString(get("/.ORDER_OBSERVATION("+i+")/OBR-16("+k+")-6"));
		String degree = getString(get("/.ORDER_OBSERVATION("+i+")/OBR-16("+k+")-7"));

		String fullName = prefix + " " + givenName + " " + middleName + " " + familyName + " " + suffix + " " + degree;
		return fullName.trim().replaceAll("\\s+", " ");
	}
	protected String getResultCopiesTo(int i, int k) throws HL7Exception
	{
		String familyName = getString(get("/.ORDER_OBSERVATION("+i+")/OBR-28("+k+")-2"));
		String givenName = getString(get("/.ORDER_OBSERVATION("+i+")/OBR-28("+k+")-3"));
		String middleName = getString(get("/.ORDER_OBSERVATION("+i+")/OBR-28("+k+")-4"));
		String suffix = getString(get("/.ORDER_OBSERVATION("+i+")/OBR-28("+k+")-5"));
		String prefix = getString(get("/.ORDER_OBSERVATION("+i+")/OBR-28("+k+")-6"));
		String degree = getString(get("/.ORDER_OBSERVATION("+i+")/OBR-28("+k+")-7"));

		String fullName = prefix + " " + givenName + " " + middleName + " " + familyName + " " + suffix + " " + degree;
		return fullName.trim().replaceAll("\\s+", " ");
	}
	protected String getOrderingProviderNo(int i, int k) throws HL7Exception
	{
		return getString(get("/.ORDER_OBSERVATION("+i+")/OBR-16("+k+")-1"));
	}
	protected String getResultCopiesToProviderNo(int i, int k) throws HL7Exception
	{
		return getString(get("/.ORDER_OBSERVATION("+i+")/OBR-28("+k+")-1"));
	}

	/**
	 *  Return the name of the doctor who requested the report, the name should
	 *  be formatted as follows:
	 *      'PREFIX' 'GIVEN NAME' 'MIDDLE INITIALS' 'FAMILY NAME' 'SUFFIX' 'DEGREE'
	 */
	public String getDocName()
	{
		String docName = "";
		int i = 0;
		try {
			while (!getOrderingProvider(0,i).isEmpty()) {
				if (i == 0) {
					docName = getOrderingProvider(0,i);
				} else {
					docName = docName + ", " + getOrderingProvider(0,i);
				}
				i++;
			}
			return (docName);
		} catch (Exception e) {
			logger.error("Could not return doctor names", e);
			return ("");
		}
	}

	/**
	 *  Return the names of the doctors which the report should be copied to. The
	 *  formatting of the names should be the same as in the method above. The
	 *  names should be separated by a comma and a space.
	 */
	public String getCCDocs() {
		String docName = "";
		int i = 0;
		try {
			while (!getResultCopiesTo(0,i).isEmpty()) {
				if (i == 0) {
					docName = getResultCopiesTo(0,i);
				} else {
					docName = docName + ", " + getResultCopiesTo(0,i);
				}
				i++;
			}
			return (docName);
		} catch (Exception e) {
			logger.error("Could not return cc'ed doctors", e);
			return ("");
		}
	}

	/**
	 *  Return an ArrayList of the requesting doctors billing number and the
	 *  billing numbers of the cc'd docs
	 */
	public ArrayList<String> getDocNums() {
		ArrayList<String> docNums = new ArrayList<>();
		String id;
		int i;

		try {
			String providerId = getOrderingProviderNo(0, 0);
			docNums.add(providerId);

			i = 0;
			id = getResultCopiesToProviderNo(0, i);
			while (id != null && !id.isEmpty()) {
				if (!id.equals(providerId))
					docNums.add(id);
				i++;
				id = getResultCopiesToProviderNo(0, i);
			}
		} catch (Exception e) {
			logger.error("Could not return doctor nums", e);

		}

		return (docNums);
	}

    /* ===================================== OBX ====================================== */

	/**
	 *  Return the number of OBX Segments within the OBR group specified by i.
	 */
	public int getOBXCount(int i)
	{
		return getReps("ORDER_OBSERVATION", i, "OBSERVATION");
	}

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

	public boolean isAbnormal()
	{
		for(int i = 0; i < getOBRCount(); i++)
		{
			for(int j = 0; j < getOBXCount(i); j++)
			{
				if(isOBXAbnormal(i, j))
				{
					return true;
				}
			}
		}
		return false;
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
	 *  Retrieve the abnormal flag if any from the OBX segment specified by j in
	 *  the ith OBR group.
	 */
	public String getOBXAbnormalFlag( int i, int j)
	{
		return getString(get("/.ORDER_OBSERVATION("+i+")/OBSERVATION("+j+")/OBX-8"));
	}

    /**
     *  Return the result status from the jth OBX segment of the ith OBR group
     */
    public String getOBXResultStatus( int i, int j)
    {
	    return getString(get("/.ORDER_OBSERVATION("+i+")/OBSERVATION("+j+")/OBX-11"));
    }

	/**
	 *  Return the date and time of the observation referred to by the jth obx
	 *  segment of the ith obr group. If the date and time is not specified
	 *  within the obx segment it should be specified within the obr segment.
	 */
	public String getTimeStamp(int i, int j)
	{
		return formatDateTime(get("/.ORDER_OBSERVATION("+i+")/OBSERVATION("+j+")/OBX-14"));
	}

    /**
     *  Return the number of comments (usually NTE segments) following the jth
     *  OBX segment of the ith OBR group.
     */
    public int getOBXCommentCount( int i, int j)
    {
    	return getReps("ORDER_OBSERVATION", i, "OBSERVATION", j, "NTE");
    }

    /**
     *  Return the kth comment of the jth OBX segment of the ith OBR group
     */
    public String getOBXComment( int i, int j, int k)
    {
	    return getString(get("/.ORDER_OBSERVATION("+i+")/OBSERVATION("+j+")/NTE("+k+")-3"));
    }

	/**
	 *  Returns the number used to order labs with matching accession numbers.
	 *
	 *  - Multiple labs with the same accession number must display in a certain
	 *  order. They are ordered by their date but if two labs with the same
	 *  accession number have the same date they are ordered by the number
	 *  retrieved by this method
	 *
	 *  - The newest lab will have the greatest number returned from this method.
	 *
	 *  - If the hl7 messages do not contain a version number or other such
	 *  number, the total number of obx segments with final results should be
	 *  returned
	 */
	public int getOBXFinalResultCount()
	{
		int obrCount = getOBRCount();
		int obxCount;
		int count = 0;
		for (int i = 0; i < obrCount; i++) {
			obxCount = getOBXCount(i);
			for (int j = 0; j < obxCount; j++) {
				if (getOBXResultStatus(i, j).equals("F"))
				{
					count++;
				}
			}
		}
		return count;
	}

	public abstract String getNteForOBX(int i,int j);


	/* ===================================== MISC ====================================== */

    /**
     * Returns a string audit of the messages.  If not required handler should just return an empty string;
     */
    public String audit()
    {
    	return "";
    }

    public String getEncounterId()
    {
	    return "";
    }

    public String getRadiologistInfo()
    {
    	return "";
    }

	public boolean isUnstructured()
	{
		return false;
	}

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

	/**
	 * finds a count of the segments under the group name. This uses terser groups
	 * The basic group structure of HL7 is similar to this:
	 * ORU_R01/(RESPONSE|PATIENT_RESULT)/ORDER_OBSERVATION/OBSERVATION
	 * for searching an OBR count, the parent group would be RESPONSE, and the rep 0 (only 1 response per message)
	 * but OBX would have parent ORDER_OBSERVATION, with n reps
	 * @param parentGroupName - name of the group to search within. this is paired with the repetition number.
	 * @param parentRep - the parent repetition to search for the segments in
	 * @param groupName - the group name type to count
	 * @return - the group count matching the group name
	 */
	protected int getReps(String parentGroupName, int parentRep, String groupName)
	{
		int count = 0;
		try
		{
			Group g = findGroupFromTop(parentGroupName, parentRep);
			count = g.getAll(groupName).length;
			logger.debug("get reps -> " + parentGroupName + "("+parentRep+")" + groupName + ": " + count);
		}
		catch(HL7Exception e) {
			logger.error("Terser Repetition Error", e);
		}
		return count;
	}

	/**
	 * finds a count of the segments under the group name. This uses terser groups
	 * This overloads the basic getReps method allowing a sub group with index.
	 * This is used for nested groups such as OBX NTE comments.
	 * Example: for searching an OBX NTE count, the parent group would be ORDER_OBSERVATION(i),the sub-parent OBSERVATION(j), groupName NTE
	 * @param parentGroupName - name of the group to search within. this is paired with the repetition number.
	 * @param parentRep - the parent repetition to search for the segments in
	 * @param subParentGroupName - name of the sup group to search within. this is paired with the sub repetition number.
	 * @param subParentRep - the sub parent repetition to search for the segments in
	 * @param groupName - the group name type to count
	 * @return - the group count matching the group name
	 */
	protected int getReps(String parentGroupName, int parentRep, String subParentGroupName, int subParentRep, String groupName)
	{
		int count = 0;
		try
		{
			Group g = findGroupFromTop(parentGroupName, parentRep);
			Group g2 = (Group) g.get(subParentGroupName, subParentRep);
			count = g2.getAll(groupName).length;
			logger.debug("get reps -> " + g.getName() + "("+parentRep+")/" + g2.getName() + "("+ subParentRep +")" + groupName + ": " + count);
		}
		catch(HL7Exception e) {
			logger.error("Terser Repetition Error", e);
		}
		return count;
	}

	/**
	 * convenient way to always search the entire hl7 message for the given group
	 */
	private Group findGroupFromTop(String groupName, int groupRep) throws HL7Exception
	{
		SegmentFinder finder = terser.getFinder();
		finder.reset(); // reset or it only searches from current parse position
		logger.debug("Find Group: " + groupName + "(" + groupRep + ")");
		return finder.findGroup(groupName, groupRep);
	}

	protected String formatDateTime(String plain)
	{
		return formatDateTime(plain, "yyyyMMddHHmmss", "yyyy-MM-dd HH:mm:ss");
	}
	protected String formatDate(String plain)
	{
		return formatDateTime(plain, "yyyyMMddHHmmss", "yyyy-MM-dd");
	}
	protected String formatTime(String plain)
	{
		return formatDateTime(plain, "yyyyMMddHHmmss", "HH:mm:ss");
	}

	protected String formatDateTime(String plain, String inFormat, String outFormat)
	{
		String formatted = "";

		if(plain == null || plain.trim().isEmpty())
			return formatted;

		if(inFormat.length() > plain.length())
			inFormat = inFormat.substring(0, plain.length());

		try
		{
			DateTimeFormatter inFormatter = DateTimeFormatter.ofPattern(inFormat);
			DateTimeFormatter outFormatter = DateTimeFormatter.ofPattern(outFormat);

			// use format builder to set default missing time values
			DateTimeFormatter customFormatter = new DateTimeFormatterBuilder().append(inFormatter)
					.parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
					.parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
					.parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
					.toFormatter();

			LocalDateTime parsedDate = LocalDateTime.parse(plain, customFormatter);

			formatted = parsedDate.format(outFormatter);
		}
		catch(DateTimeException e)
		{
			logger.error("Date parse Exception", e);
		}
		return formatted;
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

	// kept for older parsers
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

	// kept for older parsers
	protected String getFullDocName(ca.uhn.hl7v2.model.v23.datatype.XCN docSeg) {
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
}
