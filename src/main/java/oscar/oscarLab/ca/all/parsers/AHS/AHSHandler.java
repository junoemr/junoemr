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

package oscar.oscarLab.ca.all.parsers.AHS;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.oscarehr.provider.search.ProviderCriteriaSearch;
import oscar.oscarLab.ca.all.parsers.messageTypes.ORU_R01MessageHandler;
import oscar.util.ConversionUtils;

import java.util.Date;

public abstract class AHSHandler extends ORU_R01MessageHandler
{
	private static Logger logger = Logger.getLogger(AHSHandler.class);

	protected enum NameType {
		FIRST, MIDDLE, LAST
	}

	public AHSHandler() {}
	public AHSHandler(String hl7Body) throws HL7Exception
	{
		super(hl7Body);
	}
	public AHSHandler(Message msg) throws HL7Exception
	{
		super(msg);
	}
	@Override
	public void init(String hl7Body) {}

	@Override
	public String preUpload(String hl7Message) throws HL7Exception
	{
		return hl7Message;
	}
	@Override
	public boolean canUpload()
	{
		return false;
	}
	@Override
	public void postUpload() {}

	public ProviderCriteriaSearch getProviderMatchingCriteria(String routingId)
	{
		ProviderCriteriaSearch criteriaSearch = new ProviderCriteriaSearch();
		criteriaSearch.setAlbertaEDeliveryId(routingId);
		return criteriaSearch;
	}

	/* ===================================== MSH ====================================== */

	/**
	 * This is the OBR date. The MessageHandler architecture uses this to store in hl7TextInfo.obr_date
	 */
	@Override
	public String getMsgDate()
	{
		return formatDateTime(get("/.ORDER_OBSERVATION(0)/OBR-7"));
	}
	@Override
	public String getMsgType()
	{
		return "AHS";
	}

	@Override
	public String getUniqueIdentifier() {
		return get("/.OBR-20");
	}


	/* ===================================== PID ====================================== */

	@Override
	public String getPatientName()
	{
		String middleNameStr = StringUtils.isNotBlank(getMiddleName()) ? getMiddleName() + " " : "";
		return (getFirstName() + " " + middleNameStr + getLastName());
	}

	@Override
	public String getFirstName() {
		return getName(NameType.FIRST);
	}

	@Override
	public String getLastName() {
		return getName(NameType.LAST);
	}

	@Override
	public String getMiddleName() {
		return getName(NameType.MIDDLE);
	}


	@Override
	public String getHealthNum()
	{
		return getString(get("/.PID-2-1"));
	}

	/* ===================================== OBR ====================================== */

	@Override
	public String getUniqueVersionIdentifier() {
		// this is different from the filler order number in ORC
		return get("/.OBR-3-1");
	}

	@Override
	public String getObservationHeader(int i, int j) {
		return getOBRName(i);
	}

	@Override
	public String getServiceDate()
	{
		String serviceDate = getString(get("/.ORC-15"));
		if (serviceDate == null || serviceDate.isEmpty()) {
			serviceDate = get("/.OBR-14");
		}
		return (formatDateTime(serviceDate));
	}

	@Override
	public String getOrderStatus()
	{
		// of ORC is present - return it
		String orderStatus = getString(get("/.ORC-5"));
		if (orderStatus != null && !orderStatus.isEmpty())
		{
			return orderStatus;
		}
		// otherwise get first OBR status
		return getString(get("/.OBR-25-1"));
	}

	/**
	 * Gets the date and time the specimen was collected
	 *
	 * @param i
	 * 		Segment count
	 * @return
	 * 		Returns the date / time of the specimen collection or null if it's not available.
	 */
	public String getOBRDateTime(int i) {
		return get("/.OBR-7-1");
	}

	/**
	 * Gets the date and time the specimen was collected
	 *
	 * @param i
	 * 		Segment count
	 * @return
	 * 		Returns the date / time of the specimen collection or null if it's not available.
	 */
	public Date getOBRDateTimeAsDate(int i) {
		// 20101203122200
		String date = getOBRDateTime(i);
		if (date == null || date.equals("")) {
			return null;
		}
		return ConversionUtils.fromDateString(date, "yyyyMMddHHmmss");
	}

	/* ===================================== OBX ====================================== */


	@Override
	public boolean isOBXAbnormal(int i, int j) {
		try
		{
			return getOBXAbnormalFlag(i, j).equals("C")
					|| getOBXAbnormalFlag(i, j).equals("H")
					|| getOBXAbnormalFlag(i, j).equals("L")
					|| getOBXAbnormalFlag(i, j).equals("A");
		}
		catch (Exception e) {
			return false;
		}
	}

	@Override
	public String getOBXResultStatus(int i, int j) {
		String status = "";
		try {
			status = super.getOBXResultStatus(i,j);
			if (status.equalsIgnoreCase("C")) {
				status = "Corrected";
			} else if (status.equalsIgnoreCase("F")) {
				status = "Final";
			} else if (status.equalsIgnoreCase("P")) {
				status = "Preliminary";
			} else if (status.equalsIgnoreCase("X")) {
				status = "Cancelled";
			}
		} catch (Exception e) {
			logger.error("Error retrieving obx result status", e);
			return status;
		}
		return status;
	}

	@Override
	public int getOBXFinalResultCount() {
		int obrCount = getOBRCount();
		int obxCount;
		int count = 0;
		for (int i = 0; i < obrCount; i++) {
			obxCount = getOBXCount(i);
			for (int j = 0; j < obxCount; j++) {
				if (getOBXResultStatus(i, j).equals("Final") ||
						getOBXResultStatus(i, j).equals("Corrected"))
				{
					count++;
				}
			}
		}
		return count;
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
		return "";
	}

	public String getAssigningAuthority() {
		return get("/.PID-2-4");
	}


	////this.isUnstructuredDoc = "TX".equals(handler.getOBXValueType(0,0));
	public boolean isUnstructured() {
		boolean result=true;
		for(int j = 0; j<this.getOBRCount();j++) {
			for(int k=0;k<this.getOBXCount(j);k++) {
				if(!"TX".equals(getOBXValueType(j, k))) {
					result=false;
				}
			}
		}
		return result;
	}

	@Override
	public String getNteForPID() {
		return "";
	}


	/* ================================== Extra Methods and helpers ==================================== */

	@Override
	protected String getString(String retrieve) {
		return super.getString(retrieve).replaceAll("\\\\\\.br\\\\", "<br />");
	}

	/**
	 * @param type first/middle/last
	 * @return the parsed name of the given type from the PID segment
	 */
	protected String getName(NameType type) {
		// format is last,first middle
		String content = get("/.PID-5-1");

		String firstName = super.getFirstName();
		String middleName = super.getMiddleName();

		if (content == null || content.trim().isEmpty() || content.trim().equals(",")) {
			return "";
		}
		return parseNames(type, content, firstName, middleName);
	}

	/**
	 * AHS labs like to send full names in a single segment (format: last,first middle)
	 * however sometimes they use the other segments just because.
	 * so the names need a customized parse method. This happens for patients and doctors
	 * @param type - first/middle/last
	 * @param lastSeg
	 * @param firstSeg
	 * @param middleSeg
	 * @return the parsed name of the given type
	 */
	protected String parseNames(NameType type, String lastSeg, String firstSeg, String middleSeg)
	{
		if (lastSeg == null || lastSeg.trim().isEmpty() || lastSeg.trim().equals(",")) {
			return "";
		}

		String[] allNames = lastSeg.trim().split(",");

		String lastName = allNames[0].trim();
		// First and middle names occur after the comma, if it exists
		if (allNames.length > 1) {
			String firstMiddle = allNames[1].trim();

			int firstMiddleDelimiterIndex = firstMiddle.lastIndexOf(' ');
			if (firstMiddleDelimiterIndex == -1) {
				firstSeg = firstMiddle;
			}
			else {
				firstSeg = firstMiddle.substring(0, firstMiddleDelimiterIndex).trim();
				middleSeg = firstMiddle.substring(firstMiddleDelimiterIndex).trim();
			}
		}

		switch (type) {
			case FIRST:
				return firstSeg;
			case MIDDLE:
				return middleSeg;
			case LAST:
				return lastName;
			default:
				throw new IllegalArgumentException("Invalid name type " + type);
		}
	}
}
