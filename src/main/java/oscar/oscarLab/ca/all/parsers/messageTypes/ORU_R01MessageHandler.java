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


package oscar.oscarLab.ca.all.parsers.messageTypes;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import org.oscarehr.common.hl7.v2.oscar_to_oscar.DataTypeUtils;
import oscar.oscarLab.ca.all.parsers.MessageHandler;

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
public abstract class ORU_R01MessageHandler extends MessageHandler
{
	public ORU_R01MessageHandler() {}

	public ORU_R01MessageHandler(String hl7Body) throws HL7Exception
	{
		super(hl7Body);
	}

	public ORU_R01MessageHandler(Message msg) throws HL7Exception
	{
		super(msg);
	}

    /* ===================================== MSH ====================================== */
    /**
     *  Return the message type
     *  - The message type returned should be the same as the prefix of your
     *  handlers name
     *      ie/ message type = XXXX
     *          handler name = XXXXHandler
     */
	@Override
    public abstract String getMsgType();

    /**
     *  Return the date and time of the message, usually located in the 7th
     *  field of the MSH segment
     */
	@Override
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
	@Override
    public String getMsgPriority()
    {
    	return "";
    }

	/**
	 *  Return the patients location, usually the facility from which the
	 *  report has been sent ( the 4th field of the MSH segment )
	 */
	@Override
	public String getPatientLocation()
	{
		return getString(get("/.MSH-4"));
	}

	/* ====================== MSH =============================== */

	public String getMsgSendingApplication()
	{
		return getString(get("/.MSH-3"));
	}


	/* ===================================== PID ====================================== */

	/**
	 *  Return the name of the patient. The format should be the first name
	 *  followed by the last name while being separated by a space.
	 *  String firstName = getFirstName();
	 *  String lastName = getLastName();
	 */
	@Override
	public String getPatientName()
	{
		return(getFirstName()+" "+getMiddleName()+" "+getLastName());
	}

	/**
	 *  Return the patients health number
	 */
	@Override
	public String getHealthNum()
	{
		return getString(get("/.PID-2"));
	}

	/**
	 * Return the family name of the patient
	 */
	@Override
	public String getLastName()
	{
		return getString(get("/.PID-5-1"));
	}

	/**
	 *  Return the given name of the patient
	 */
	@Override
	public String getFirstName()
	{
		return getString(get("/.PID-5-2"));
	}

	/**
	 * Return the middle name of the patient
	 */
	@Override
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
	@Override
	public String getSex()
	{
		return getString(get("/.PID-8"));
	}

	@Override
	protected String getBusinessPhone(int i) throws HL7Exception
	{
		return getString(get("/.PID-14-"+i));
	}

	@Override
	protected String getHomePhone(int i) throws HL7Exception
	{
		return getString(get("/.PID-13-"+i));
	}

	/* ===================================== OBR ====================================== */

    /**
     *  Return the number of OBR Segments in the message
     */
	@Override
	public int getOBRCount()
	{
		return getReps(getRootGroupName(), 0, "ORDER_OBSERVATION");
	}

	@Override
	public String getFillerOrderNumber()
	{
		return getString(get("/.OBR-3"));
	}

	/**
	 *  Return the name of the ith OBR Segment, usually stored in the
	 *  UniversalServiceIdentifier
	 */
	@Override
	public String getOBRName(int i)
	{
		return getString(get("/.ORDER_OBSERVATION("+i+")/OBR-4-2"));
	}

	/**
	 * get the OBR procedure code
	 * @param i - obr rep
	 * @return procedure code string
	 */
	public String getOBRProcedureCode(int i)
	{
		return getString(get("/.ORDER_OBSERVATION(" + i + ")/OBR-4-1"));
	}

	/**
	 *  Return the request date of the message
	 */
	@Override
	public String getRequestDate(int i)
	{
		return formatDateTime(get("/.ORDER_OBSERVATION("+i+")/OBR-6"));
	}

	/**
	 *  Return the service date of the message
	 */
	@Override
	public String getServiceDate()
	{
		return formatDateTime(get("/.OBR(0)-14"));
	}


	/**
	 * Get the report date (obr-22)
	 * @param i - obr rep
	 * @return the report date string
	 */
	@Override
	public String getReportDate(int i)
	{
		return formatDateTime(get("/.ORDER_OBSERVATION("+i+")/OBR-22"));
	}

	/**
	 *  Return the status of the report, 'F' is returned for a final report,
	 *  otherwise the report is partial
	 */
	@Override
	public String getOrderStatus()
	{
		return getString(get("/.OBR(0)-25"));
	}

	/**
	 *  Return the number of comments (usually NTE segments) that follow ith
	 *  OBR segment, this should usually be either 0 or 1.
	 */
	@Override
	public int getOBRCommentCount(int i)
	{
		return getReps("ORDER_OBSERVATION", i, "NTE");
	}

	/**
	 *  Return the kth comment of the ith OBR segment.
	 */
	@Override
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
	@Override
	public String getObservationHeader(int i, int j)
	{
		return getOBRName(i);
	}

	/**
	 *  Return a list of all possible headers retrieved from getObservationHeader
	 *  each header will only occur once in the list
	 */
	@Override
	public ArrayList<String> getHeaders()
	{
		HashSet<String> headers = new HashSet<>();
		for (int i = 0; i < getOBRCount(); i++) {
			// duplicates are ignored in hash set add
			headers.add(getOBRName(i));
		}
		return new ArrayList<>(headers);
	}

	@Override
	protected String getClientRef(int i, int k) throws HL7Exception
	{
		return getString(get("/.ORDER_OBSERVATION("+i+")/OBR-16("+k+")-1"));
	}

	@Override
	protected String getOrderingProvider(int i, int k)
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

	@Override
	protected String getResultCopiesTo(int i, int k)
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

	@Override
	protected String getOrderingProviderNo(int i, int k) throws HL7Exception
	{
		return getString(get("/.ORDER_OBSERVATION("+i+")/OBR-16("+k+")-1"));
	}

	@Override
	protected String getResultCopiesToProviderNo(int i, int k) throws HL7Exception
	{
		return getString(get("/.ORDER_OBSERVATION("+i+")/OBR-28("+k+")-1"));
	}

    /* ===================================== OBX ====================================== */

	/**
	 *  Return the number of OBX Segments within the OBR group specified by i.
	 */
	@Override
	public int getOBXCount(int i)
	{
		return getReps("ORDER_OBSERVATION", i, "OBSERVATION");
	}

	/**
	 * get the number of OBX reps for the specimen j in order observation i
	 * @param i order observation rep
	 * @param j specimen rep
	 * @return the number of OBX reps
	 */
	@Override
	public int getSpecimenOBXCount(int i, int j)
	{
		return getReps("ORDER_OBSERVATION", i, "SPECIMEN", j, "OBX");
	}

    /**
     * Return the obx value type
     * @param i
     * @param j
     * @return String the obx value
     */
	@Override
	public String getOBXValueType(int i, int j)
	{
		return getString(get("/.ORDER_OBSERVATION("+i+")/OBSERVATION("+j+")/OBX-2"));
	}

	/**
	 *  Return the identifier from jth OBX segment of the ith OBR group. It is
	 *  usually stored in the first component of the third field of the OBX
	 *  segment.
	 */
	@Override
	public String getOBXIdentifier(int i, int j)
	{
		return getString(get("/.ORDER_OBSERVATION("+i+")/OBSERVATION("+j+")/OBX-3-1"));
	}

    /**
     *  Return the name of the jth OBX segment of the ith OBR group. It is
     *  usually stored in the second component of the third field of the OBX
     *  segment.
     */
	@Override
    public String getOBXName( int i, int j)
    {
	    return getString(get("/.ORDER_OBSERVATION("+i+")/OBSERVATION("+j+")/OBX-3-2"));
    }

	/**
	 *  Return the result from the jth OBX segment at the kth component of the ith OBR group
	 */
	@Override
	public String getOBXResult(int i, int j, int k)
	{
		return getString(get("/.ORDER_OBSERVATION("+i+")/OBSERVATION("+j+")/OBX-5-"+k));
	}

	/**
	 *  Return the units from the jth OBX segment of the ith OBR group
	 */
	@Override
	public String getOBXUnits( int i, int j)
	{
		return getString(get("/.ORDER_OBSERVATION("+i+")/OBSERVATION("+j+")/OBX-6"));
	}

    /**
     *  Return the reference range from the jth OBX segment of the ith OBR group
     */
	@Override
    public String getOBXReferenceRange( int i, int j)
    {
	    return getString(get("/.ORDER_OBSERVATION("+i+")/OBSERVATION("+j+")/OBX-7"));
    }

	/**
	 *  Retrieve the abnormal flag if any from the OBX segment specified by j in
	 *  the ith OBR group.
	 */
	@Override
	public String getOBXAbnormalFlag( int i, int j)
	{
		return getString(get("/.ORDER_OBSERVATION("+i+")/OBSERVATION("+j+")/OBX-8"));
	}

    /**
     *  Return the result status from the jth OBX segment of the ith OBR group
     */
	@Override
    public String getOBXResultStatus( int i, int j)
    {
	    return getString(get("/.ORDER_OBSERVATION("+i+")/OBSERVATION("+j+")/OBX-11"));
    }

	/**
	 *  Return the date and time of the observation referred to by the jth obx
	 *  segment of the ith obr group. If the date and time is not specified
	 *  within the obx segment it should be specified within the obr segment.
	 */
	@Override
	public String getTimeStamp(int i, int j)
	{
		if (i < 0 || j < 0)
		{// some fun peaces of code like to ask for negative values
			return null;
		}
		return formatDateTime(get("/.ORDER_OBSERVATION("+i+")/OBSERVATION("+j+")/OBX-14"));
	}

    /**
     *  Return the number of comments (usually NTE segments) following the jth
     *  OBX segment of the ith OBR group.
     */
	@Override
    public int getOBXCommentCount( int i, int j)
    {
    	return getReps("ORDER_OBSERVATION", i, "OBSERVATION", j, "NTE");
    }

    /**
     *  Return the kth comment of the jth OBX segment of the ith OBR group
     */
	@Override
    public String getOBXComment( int i, int j, int k)
    {
	    return getString(get("/.ORDER_OBSERVATION("+i+")/OBSERVATION("+j+")/NTE("+k+")-3"));
    }

	public abstract String getNteForOBX(int i,int j);

	/**
	 * true if performing organization information is available
	 * @param i - the order observation group to check
	 * @param k - the obx rep to check
	 * @return - true if the performing organization is available
	 */
	@Override
	public boolean hasPerformingOrganization(int i, int k)
	{
		return !legacyHandler && isComponentPresent("/.ORDER_OBSERVATION("+ i +")/OBSERVATION("+ k +")/OBX-23");
	}


	/**
	 * get the long name of the lab responsible for the given OBX segment
	 * @param i OBR rep
	 * @param k OBX rep
	 * @return lab name
	 */
	@Override
	public String getPerformingOrganizationName(int i, int k)
	{
		return get("/.ORDER_OBSERVATION("+ i +")/OBSERVATION("+ k +")/OBX-23");
	}

	/**
	 * get address string for the lab responsible for the given OBX segment
	 * @param i OBR rep
	 * @param k OBX rep
	 * @return lab address string
	 */
	@Override
	public String getPerformingOrganizationAddress(int i, int k)
	{
		if (get("/.ORDER_OBSERVATION("+ i +")/OBSERVATION("+ k +")/OBX-24") != null &&
				get("/.ORDER_OBSERVATION("+ i +")/OBSERVATION("+ k +")/OBX-24-3") != null &&
				get("/.ORDER_OBSERVATION("+ i +")/OBSERVATION("+ k +")/OBX-24-4") != null &&
				get("/.ORDER_OBSERVATION("+ i +")/OBSERVATION("+ k +")/OBX-24-5") != null)
		{
			return get("/.ORDER_OBSERVATION(" + i + ")/OBSERVATION(" + k + ")/OBX-24") + ", " +
					get("/.ORDER_OBSERVATION(" + i + ")/OBSERVATION(" + k + ")/OBX-24-3") + " " +
					get("/.ORDER_OBSERVATION(" + i + ")/OBSERVATION(" + k + ")/OBX-24-4") + " " +
					get("/.ORDER_OBSERVATION(" + i + ")/OBSERVATION(" + k + ")/OBX-24-5");
		}
		else
		{
			return null;
		}
	}

	/**
	 * get the short name of the lab responsible for the lab
	 * @return lab short name
	 */
	@Override
	public String getAssignedPatientLocation()
	{
		return get("/.PV1-3-9");
	}


	/* ============================== Specimen ============================= */
	/**
	 * true if this order observation has at least one specimen segment
	 * @param i the order observation group to check
	 * @return true / false indicating existence of specimen segment
	 */
	@Override
	public boolean hasSpecimenSegment(int i )
	{
		if (!legacyHandler && getMsgVersion() == DataTypeUtils.HL7_VERSION.VERSION_251)
		{
			return getSpecimenCount(i) != 0;
		}
		return false;
	}

	/**
	 * return the number of specimen segments in the given order observation group
	 * @param i the order observation group to check
	 * @return count of specimen segments
	 */
	@Override
	public int getSpecimenCount(int i)
	{
		return getReps("ORDER_OBSERVATION", i, "SPECIMEN");
	}

	/**
	 * get the type string of the specimen j in order observation i
	 * @param i the order observation in which to look
	 * @param j the specimen to check
	 * @return the specimen type string
	 */
	@Override
	public String getSpecimenType(int i, int j)
	{
		return get("/.ORDER_OBSERVATION(" + i + ")/SPECIMEN(" + j + ")/SPM-4-1");
	}

	/**
	 * get the specimen site for specimen j in order observation i
	 * @param i the order observation in which to look
	 * @param j the specimen to check
	 * @return the specimen site string
	 */
	@Override
	public String getSpecimenSite(int i, int j)
	{
		return get("/.ORDER_OBSERVATION(" + i + ")/SPECIMEN(" + j + ")/SPM-8-1");
	}

	/**
	 * get the specimen collection datetime
	 * @param i the order observation rep
	 * @param j the specimen rep
	 * @return the specimen collection date as a string
	 */
	@Override
	public String getSpecimenCollectionDateTime(int i, int j)
	{
		return formatDateTime(get("/.ORDER_OBSERVATION(" + i + ")/SPECIMEN(" + j + ")/SPM-17"));
	}

	/**
	 * get the specimen received datetime
	 * @param i the order observation rep
	 * @param j the specimen rep
	 * @return the specimen received date as a string
	 */
	@Override
	public String getSpecimenReceivedDateTime(int i, int j)
	{
		return formatDateTime(get("/.ORDER_OBSERVATION(" + i + ")/SPECIMEN(" + j + ")/SPM-18"));
	}

	/**
	 * check if the specimen, j has an extended description
	 * @param i the order observation rep
	 * @param j the specimen rep
	 * @return true if the specimen has an extended description
	 */
	@Override
	public boolean hasExtendedSpecimenDescription(int i, int j)
	{// disable for now, I don't trust the extended description
		return false;
	}

	/**
	 * get the OBX observation value for order observation i , specimen j , OBX k
	 * @param i order observation rep
	 * @param j specimen rep
	 * @param k OBX rep
	 * @return the OBX Observation value string
	 */
	@Override
	public String getSpecimenOBXObservationValue(int i, int j, int k)
	{
		return get("/.ORDER_OBSERVATION(" + i + ")/SPECIMEN(" + j + ")/OBX(" + k + ")-5");
	}
}
