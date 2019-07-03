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
import org.apache.commons.lang.StringUtils;
import oscar.oscarLab.ca.all.parsers.MessageHandler;

public abstract class MDM_T11MessageHandler extends MessageHandler
{
	public MDM_T11MessageHandler() {}

	public MDM_T11MessageHandler(String hl7Body) throws HL7Exception
	{
		super(hl7Body);
	}

	public MDM_T11MessageHandler(Message msg) throws HL7Exception
	{
		super(msg);
	}

	@Override
	public String getAccessionNum()
	{
		return get("/.TXA-12");
	}

	@Override
	public String getFillerOrderNumber()
	{
		//TODO better solution?
		return get("/.EVN-2");// event recorded date time.
	}

	/**
	 * empty string. no NTE in this type of message
	 * @return empty string
	 */
	@Override
	public String getNteForPID()
	{
		return "";
	}

	/**
	 * empty string. no OBX in this type of message
	 * @param i - ignored
	 * @param j - ignored
	 * @return empty string
	 */
	@Override
	public String getNteForOBX(int i, int j)
	{
		return "";
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
		return getFirstName() + " " + getLastName();
	}

	/**
	 *  Return the patients health number
	 */
	public String getHealthNum()
	{
		return getString(get("/.PID-3(1)-1"));
	}

	/**
	 * Return the family name of the patient
	 */
	public String getLastName()
	{
		return get("/.PID-5-1");
	}

	/**
	 *  Return the given name of the patient
	 */
	public String getFirstName()
	{
		return get("/.PID-5-2");
	}

	/**
	 * Return the middle name of the patient
	 */
	public String getMiddleName()
	{
		return StringUtils.trimToEmpty(get("/.PID-5-3"));
	}

	/**
	 *  Return the patients date of birth
	 */
	public String getDOB()
	{
		return get("/.PID-7");
	}

	/**
	 *  Return the gender of the patient: 'M' or 'F'
	 */
	public String getSex()
	{
		return get("/.PID-8");
	}


	protected String getBusinessPhone(int i) throws HL7Exception
	{
		return get("/.PID-14-1");
	}

	protected String getHomePhone(int i) throws HL7Exception
	{
		return get("/.PID-13-1");
	}
}
