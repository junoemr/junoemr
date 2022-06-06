/**
 * Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
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
 * This software was written for
 * CloudPractice Inc.
 * Victoria, British Columbia
 * Canada
 */
package oscar.oscarLab.ca.all.parsers.messageTypes;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import oscar.oscarLab.ca.all.parsers.MessageHandler;

/**
 * base type for ORM^O01 messages (hl7 version 2.3)
 */
public abstract class ORM_O01MessageHandler extends MessageHandler
{
	public ORM_O01MessageHandler() {}

	public ORM_O01MessageHandler(String hl7Body) throws HL7Exception
	{
		super(hl7Body);
	}

	public ORM_O01MessageHandler(Message msg) throws HL7Exception
	{
		super(msg);
	}

	@Override
	public String getUniqueIdentifier()
	{
		return get("/.ORDER/ORC-3-1");
	}

	@Override
	public String getNteForPID() {
		return get("/.NTE-3");
	}

	/* ====================== PID =============================== */

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
		return getString(get("/.PATIENT/PID-3(1)-1"));
	}

	/**
	 * Return the family name of the patient
	 */
	@Override
	public String getLastName()
	{
		return getString(get("/.PATIENT/PID-5-1"));
	}

	/**
	 *  Return the given name of the patient
	 */
	@Override
	public String getFirstName()
	{
		return getString(get("/.PATIENT/PID-5-2"));
	}

	/**
	 * Return the middle name of the patient
	 */
	@Override
	public String getMiddleName()
	{
		return getString(get("/.PATIENT/PID-5-3"));
	}

	/**
	 *  Return the patients date of birth
	 */
	@Override
	public String getDOB()
	{
		return formatDate(get("/.PATIENT/PID-7"));
	}

	/**
	 *  Return the gender of the patient: 'M' or 'F'
	 */
	@Override
	public String getSex()
	{
		return getString(get("/.PATIENT/PID-8"));
	}

	@Override
	protected String getBusinessPhone(int i) throws HL7Exception
	{
		return getString(get("/.PATIENT/PID-14-"+i));
	}

	@Override
	protected String getHomePhone(int i) throws HL7Exception
	{
		return getString(get("/.PATIENT/PID-13-"+i));
	}

	/* ===================================== OBR ====================================== */

	/**
	 *  Return the number of OBR Segments in the message
	 */
	@Override
	public int getOBRCount()
	{
		int count =0;
		for (int i =0; i < getReps("ORDER"); i ++)
		{
			if (get("/.ORDER("+i+")/ORDER_DETAIL/OBR-1") != null)
			{
				count ++;
			}
		}
		return count;
	}

	@Override
	public String getUniqueVersionIdentifier()
	{
		return getString(get("/.ORDER/ORDER_DETAIL/OBR-3"));
	}

	/**
	 *  Return the name of the ith OBR Segment, usually stored in the
	 *  UniversalServiceIdentifier
	 *  NOTE. i ignored, only one OBR
	 */
	@Override
	public String getOBRName(int i)
	{
		return getString(get("/.ORDER("+i+")/ORDER_DETAIL/OBR-4-2"));
	}

	/**
	 *  Return the request date of the message
	 */
	@Override
	public String getRequestDate(int i)
	{
		return formatDateTime(get("/.ORDER("+i+")/ORDER_DETAIL/OBR-6"));
	}

	/**
	 *  Return the service date of the message
	 */
	@Override
	public String getServiceDate()
	{
		return formatDateTime(get("/.ORDER/ORDER_DETAIL/OBR-6"));
	}

	/**
	 *  Return the status of the report, 'F' is returned for a final report,
	 *  otherwise the report is partial
	 */
	@Override
	public String getOrderStatus()
	{
		return getString(get("/.ORDER(1)/ORDER_DETAIL/OBR-25"));
	}

	/**
	 *  Return the number of comments (usually NTE segments) that follow ith
	 *  OBR segment, this should usually be either 0 or 1.
	 */
	@Override
	public int getOBRCommentCount(int i)
	{
		return getReps("ORDER", i, "ORDER_DETAIL", 0, "NTE");
	}

	/**
	 *  Return the kth comment of the ith OBR segment.
	 */
	@Override
	public String getOBRComment(int i, int k)
	{
		return getString(get("/.ORDER("+i+")/ORDER_DETAIL/NTE("+k+")-3"));
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

	@Override
	protected String getClientRef(int i, int k) throws HL7Exception
	{
		return getString(get("/.ORDER("+i+")/ORDER_DETAIL/OBR-16("+k+")-1"));
	}

	@Override
	protected String getOrderingProvider(int i, int k) throws HL7Exception
	{
		return getFullDocName("/.ORDER("+i+")/ORDER_DETAIL/OBR", 16, k);
	}

	@Override
	protected String getResultCopiesTo(int i, int k) throws HL7Exception
	{
		return getFullDocName("/.ORDER("+i+")/ORDER_DETAIL/OBR", 28, k);
	}

	@Override
	protected String getOrderingProviderNo(int i, int k) throws HL7Exception
	{
		return getString(get("/.ORDER("+i+")/ORDER_DETAIL/OBR-16("+k+")-1"));
	}

	@Override
	protected String getResultCopiesToProviderNo(int i, int k) throws HL7Exception
	{
		return getString(get("/.ORDER("+i+")/ORDER_DETAIL/OBR-28("+k+")-1"));
	}


	/* ===================================== OBX ====================================== */

	/**
	 *  Return the number of OBX Segments within the OBR group specified by i.
	 */
	@Override
	public int getOBXCount(int i)
	{
		return getReps("ORDER", i, "ORDER_DETAIL", 0, "OBSERVATION");
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
		return 0;
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
		return getString(get("/.ORDER("+i+")/ORDER_DETAIL/OBSERVATION("+j+")/OBX-2"));
	}

	/**
	 *  Return the identifier from jth OBX segment of the ith OBR group. It is
	 *  usually stored in the first component of the third field of the OBX
	 *  segment.
	 */
	@Override
	public String getOBXIdentifier(int i, int j)
	{
		return getString(get("/.ORDER("+i+")/ORDER_DETAIL/OBSERVATION("+j+")/OBX-3-1"));
	}

	/**
	 *  Return the name of the jth OBX segment of the ith OBR group. It is
	 *  usually stored in the second component of the third field of the OBX
	 *  segment.
	 */
	@Override
	public String getOBXName( int i, int j)
	{
		return getString(get("/.ORDER("+i+")/ORDER_DETAIL/OBSERVATION("+j+")/OBX-3-2"));
	}

	/**
	 *  Return the result from the jth OBX segment at the kth component of the ith OBR group
	 */
	@Override
	public String getOBXResult(int i, int j, int k)
	{
		return getString(get("/.ORDER("+i+")/ORDER_DETAIL/OBSERVATION("+j+")/OBX-5-" + k));
	}


	/**
	 *  Return the units from the jth OBX segment of the ith OBR group
	 */
	@Override
	public String getOBXUnits( int i, int j)
	{
		return getString(get("/.ORDER("+i+")/ORDER_DETAIL/OBSERVATION("+j+")/OBX-6"));
	}

	/**
	 *  Return the reference range from the jth OBX segment of the ith OBR group
	 */
	@Override
	public String getOBXReferenceRange( int i, int j)
	{
		return getString(get("/.ORDER("+i+")/ORDER_DETAIL/OBSERVATION("+j+")/OBX-7"));
	}

	/**
	 *  Retrieve the abnormal flag if any from the OBX segment specified by j in
	 *  the ith OBR group.
	 */
	@Override
	public String getOBXAbnormalFlag( int i, int j)
	{
		return getString(get("/.ORDER("+i+")/ORDER_DETAIL/OBSERVATION("+j+")/OBX-8"));
	}

	/**
	 *  Return the result status from the jth OBX segment of the ith OBR group
	 */
	@Override
	public String getOBXResultStatus( int i, int j)
	{
		return getString(get("/.ORDER("+i+")/ORDER_DETAIL/OBSERVATION("+j+")/OBX-11"));
	}

	/**
	 *  Return the date and time of the observation referred to by the jth obx
	 *  segment of the ith obr group. If the date and time is not specified
	 *  within the obx segment it should be specified within the obr segment.
	 */
	@Override
	public String getTimeStamp(int i, int j)
	{
		return formatDateTime(get("/.ORDER("+i+")/ORDER_DETAIL/OBSERVATION("+j+")/OBX-14"));
	}

	/**
	 *  Return the number of comments (usually NTE segments) following the jth
	 *  OBX segment of the ith OBR group.
	 */
	@Override
	public int getOBXCommentCount( int i, int j)
	{
		String [] parentGroups = {"ORDER", "ORDER_DETAIL", "OBSERVATION"};
		int [] parentReps = {i, 1, j};
		return getReps(parentGroups, parentReps, "NTE");
	}

	/**
	 *  Return the kth comment of the jth OBX segment of the ith OBR group
	 */
	@Override
	public String getOBXComment( int i, int j, int k)
	{
		return getString(get("/.ORDER("+i+")/ORDER_DETAIL/OBSERVATION("+j+")/NTE("+k+")-3"));
	}

	@Override
	public String getNteForOBX(int i,int j)
	{
		return get("/.ORDER("+i+")/ORDER_DETAIL/OBSERVATION("+j+")/NTE-3");
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
		return get("/.ORDER("+ i +")/ORDER_DETAIL/OBSERVATION("+ k +")/OBX-23");
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
		if (get("/.ORDER("+ i +")/ORDER_DETAIL/OBSERVATION("+ k +")/OBX-24") != null &&
				get("/.ORDER("+ i +")/ORDER_DETAIL/OBSERVATION("+ k +")/OBX-24-3") != null &&
				get("/.ORDER("+ i +")/ORDER_DETAIL/OBSERVATION("+ k +")/OBX-24-4") != null &&
				get("/.ORDER("+ i +")/ORDER_DETAIL/OBSERVATION("+ k +")/OBX-24-5") != null)
		{
			return get("/.ORDER(" + i + ")/ORDER_DETAIL/OBSERVATION(" + k + ")/OBX-24") + ", " +
					get("/.ORDER(" + i + ")/ORDER_DETAIL/OBSERVATION(" + k + ")/OBX-24-3") + " " +
					get("/.ORDER(" + i + ")/ORDER_DETAIL/OBSERVATION(" + k + ")/OBX-24-4") + " " +
					get("/.ORDER(" + i + ")/ORDER_DETAIL/OBSERVATION(" + k + ")/OBX-24-5");
		}
		else
		{
			return null;
		}
	}

	/**
	 * get the name of the location the patient was assigned to
	 * @return name of the location
	 */
	public String getAssignedPatientLocation()
	{
		return get("/.PATIENT/PATIENT_VISIT/PV1-3-9");
	}
}
