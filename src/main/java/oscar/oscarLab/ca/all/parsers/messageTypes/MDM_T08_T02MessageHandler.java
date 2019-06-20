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
 * handler for both MDM T08 and MDM T02 Messages. This is because
 * they both share the same message format
 */
public abstract class MDM_T08_T02MessageHandler extends MessageHandler
{
	public MDM_T08_T02MessageHandler() {}

	public MDM_T08_T02MessageHandler(String hl7Body) throws HL7Exception
	{
		super(hl7Body);
	}

	public MDM_T08_T02MessageHandler(Message msg) throws HL7Exception
	{
		super(msg);
	}

	/* ========================= EVN ============================ */

	/**
	 * get event type code
	 * @return type code (specific to impl)
	 */
	public String getEventTypeCode()
	{
		return get("/.EVN-1");
	}

	/**
	 * get the recorded date of this event
	 * @return date
	 */
	public String getEventRecordedDate()
	{
		return formatDate(get("/.EVN-2"));
	}

	/**
	 * get the planned date of the event
	 * @return date
	 */
	public String getEventPlannedDate()
	{
		return formatDate(get("/.EVN-3"));
	}

	/**
	 * get the reason code for the event (specific to impl)
	 * @return reason code
	 */
	public String getEventReason()
	{
		return get("/.EVN-4");
	}

	/**
	 * get the person identifier of the person who triggered this event
	 * @return person identifier
	 */
	public String getEventOperatorPersonIdentifier()
	{
		return get("/.EVN-5-1");
	}

	/**
	 * get first name of event operator
	 * @return first name
	 */
	public String getEventOperatorFirstName()
	{
		return get("/.EVN-5-3");
	}

	/**
	 * get last name of event operator
	 * @return last name
	 */
	public String getEventOperatorLastName()
	{
		return get("/.EVN-5-2");
	}

	/* ============================ PID ================================= */


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
		return getString(get("/.PID-3(1)-1"));
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

	/**
	 * MDM_T08 / MDM_T02 does not support NTE after PID.
	 * @return blank string
	 */
	@Override
	public String getNteForPID()
	{
		return "";
	}


	/* =================================== TXA ========================== */

	/**
	 * get the accession number as the Document Unique id.
	 * @return the Document Unique id.
	 */
	@Override
	public String getAccessionNum()
	{
		return get("/.TXA-12");
	}

	/* ================================= OBX ============================== */

	/**
	 * MDM_T08 / MDM_T02 does not support NTE for OBX
	 * @param i ignored
	 * @param j ignored
	 * @return empty string "".
	 */
	@Override
	public String getNteForOBX(int i, int j)
	{
		return "";
	}


}
