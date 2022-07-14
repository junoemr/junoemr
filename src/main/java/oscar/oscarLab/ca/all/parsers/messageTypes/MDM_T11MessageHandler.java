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
import org.oscarehr.common.model.Hl7TextInfo;
import oscar.oscarLab.ca.all.parsers.MessageHandler;

import java.util.HashMap;
import java.util.Map;

public abstract class MDM_T11MessageHandler extends MessageHandler
{
	/**
	 * order status interpretation map as per hl7 [0271] Document Completion Status
	 * hl7 [0271] -> juno internal
	 */
	private static final Map<String, Hl7TextInfo.REPORT_STATUS> orderStatusMap = new HashMap<>();
	static
	{
		orderStatusMap.put("AU", Hl7TextInfo.REPORT_STATUS.A);
		orderStatusMap.put("DI", Hl7TextInfo.REPORT_STATUS.D);
		orderStatusMap.put("DO", Hl7TextInfo.REPORT_STATUS.O);
		orderStatusMap.put("IN", Hl7TextInfo.REPORT_STATUS.I);
		orderStatusMap.put("IP", Hl7TextInfo.REPORT_STATUS.N);
		orderStatusMap.put("LA", Hl7TextInfo.REPORT_STATUS.L);
		orderStatusMap.put("PA", Hl7TextInfo.REPORT_STATUS.R);
		orderStatusMap.put("P", Hl7TextInfo.REPORT_STATUS.P);
	}

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
	public String getUniqueVersionIdentifier()
	{
		return getString(get("/.EVN-2"));
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

	/* ===================================== TXA ====================================== */

	@Override
	public String getUniqueIdentifier()
	{
		return get("/.TXA-12");
	}

	/**
	 * get the k'th ordering provider name
	 * @param i - ignored
	 * @param k - provider rep
	 * @return - ordering provider name (for display)
	 * @throws HL7Exception
	 */
	@Override
	protected String getOrderingProvider(int i, int k) throws HL7Exception
	{
		return getFullDocName("/.TXA", 5, k);
	}

	/**
	 * get the kth copy to provider name
	 * @param i - ignored
	 * @param k - provider rep
	 * @return - the provider name (for display)
	 * @throws HL7Exception
	 */
	@Override
	protected String getResultCopiesTo(int i, int k) throws HL7Exception
	{
		return getFullDocName("/.TXA", 23, k);
	}

	/**
	 *  Return the status of the report, 'F' is returned for a final report,
	 *  otherwise the report is partial
	 */
	@Override
	public String getOrderStatus()
	{
		return getString(get("/.TXA-17"));
	}

	/**
	 * map hl7 [0271] Document Completion Status codes to Juno internal codes
	 * @return - internal report status
	 */
	@Override
	public Hl7TextInfo.REPORT_STATUS getJunoOrderStatus()
	{
		return orderStatusMap.get(getOrderStatus());
	}

	/**
	 * get the kth ordering providers id code
	 * @param i - ignored
	 * @param k - the provider rep
	 * @return - the providers id code
	 * @throws HL7Exception
	 */
	@Override
	protected String getClientRef(int i, int k) throws HL7Exception
	{
		return getString(get("/.TXA-5(" + k + ")-1"));
	}

	/**
	 * get the provider no of the kth provider
	 * @param i - ignored
	 * @param k - the provider rep
	 * @return - the "provider number"
	 * @throws HL7Exception
	 */
	@Override
	protected String getOrderingProviderNo(int i, int k) throws HL7Exception
	{
		return getString(get("/.TXA-5(" + k + ")-1"));
	}

	/**
	 * get the provider no of the kth copying provider
	 * @param i - ignored
	 * @param k - the provider rep
	 * @return - the "provider number"
	 * @throws HL7Exception
	 */
	@Override
	protected String getResultCopiesToProviderNo(int i, int k) throws HL7Exception
	{
		return getString(get("/.TXA-23(" + k + ")-1"));
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
		return getString(getString(get("/.PID-3(1)-1")));
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
		return StringUtils.trimToEmpty(get("/.PID-5-3"));
	}

	/**
	 *  Return the patients date of birth
	 */
	public String getDOB()
	{
		return formatDate(get("/.PID-7"));
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
		return getString(get("/.PID-14-" + i));
	}

	protected String getHomePhone(int i) throws HL7Exception
	{
		return getString(get("/.PID-13-" + i));
	}

	/* ========================= EVN ======================= */

	/**
	 *  Return the service date of the message
	 */
	@Override
	public String getServiceDate()
	{
		return formatDateTime(get("/.EVN-2"));
	}
}
