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

package oscar.oscarLab.ca.all.parsers.v22;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v22.message.ORU_R01;
import ca.uhn.hl7v2.model.v22.segment.MSH;

import java.util.ArrayList;

public class AHSSpecimenGateHandler extends MessageHandler22
{

	protected enum NameType {
		FIRST, MIDDLE, LAST
	}

	public static boolean headerTypeMatch(MSH messageHeaderSegment)
	{
		String sendingApplication = messageHeaderSegment.getSendingApplication().getValue();
		return "SpecimenGate".equalsIgnoreCase(sendingApplication);
	}

	public AHSSpecimenGateHandler()
	{
		super();
	}
	public AHSSpecimenGateHandler(String hl7Body) throws HL7Exception
	{
		super(hl7Body);
	}
	public AHSSpecimenGateHandler(ORU_R01 msg) throws HL7Exception
	{
		super(msg);
	}

	@Override
	public String preUpload(String hl7Message) throws HL7Exception
	{
		return hl7Message;
	}
	@Override
	public boolean canUpload()
	{
		//get("/.PID-2")
		return true;
	}
	@Override
	public void postUpload() {}

    /* ===================================== MSH ====================================== */

	@Override
	public String getMsgType()
	{
		return "AHS";
	}
	@Override
	public String getAccessionNum()
	{
		return getString(get("/.MSH-10"));
	}

    /* ===================================== MSH ====================================== */

	@Override
	public String getPatientName() {
		return (getFirstName() + " " + getLastName());
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

	private String getName(NameType type) {
		// format is last,first middle
		String content = get("/.PID-5-1");

		String firstName = super.getFirstName();
		String middleName = super.getMiddleName();

		if (content == null || content.trim().isEmpty() || content.trim().equals(",")) {
			return "";
		}

		String[] allNames = content.trim().split(",");

		String lastName = allNames[0].trim();
		// First and middle names occur after the comma, if it exists
		if (allNames.length > 1) {
			String firstMiddle = allNames[1].trim();

			int firstMiddleDelimiterIndex = firstMiddle.lastIndexOf(' ');
			if (firstMiddleDelimiterIndex == -1) {
				firstName = firstMiddle;
			}
			else {
				firstName = firstMiddle.substring(0, firstMiddleDelimiterIndex).trim();
				middleName = firstMiddle.substring(firstMiddleDelimiterIndex).trim();
			}
		}

		switch (type) {
			case FIRST:
				return firstName;
			case MIDDLE:
				return middleName;
			case LAST:
				return lastName;
			default:
				throw new IllegalArgumentException("Invalid name type " + type);
		}
	}

    /* ===================================== OBR ====================================== */

	@Override
	public String getOrderStatus(){
		try{
			// of ORC is present - return it
			String orderStatus = getString(response.getORDER_OBSERVATION(0).getORC().getOrderStatus().getValue());
			if (orderStatus != null && !orderStatus.isEmpty()) {
				return orderStatus;
			}
			// otherwise get first OBR status
			return get("/.OBR-25-1");
		}catch(Exception e){
			return("");
		}
	}

	@Override
	public String getServiceDate()
	{
		String collectionDate = formatDate(getString(get("/.OBR-7")));
		String collectionTime = formatTime(getString(get("/.OBR-7")));

		// use collectionDate, birth-date, or all 1's for date
		if(collectionDate.isEmpty())
		{
			collectionDate = getDOB();
			if(collectionDate.isEmpty())
				collectionDate = "1111-11-11";
		}
		// use collectionTime or all 0's for time
		if(collectionTime.isEmpty())
			collectionTime = "00:00";

		return collectionDate + " " + collectionTime;
	}

    /* ===================================== OBX ====================================== */

	@Override
	public boolean isOBXAbnormal(int i, int j) {
		try {
			return getOBXAbnormalFlag(i, j).equals("C") || getOBXAbnormalFlag(i, j).equals("H")
					|| getOBXAbnormalFlag(i, j).equals("L") || getOBXAbnormalFlag(i, j).equals("A");
		} catch (Exception e) {
			return false;
		}
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

	/* ===================================== MISC ===================================== */

	@Override
	public boolean isUnstructured() {
		return true;
	}

	@Override
	public String getClientRef()
	{
		return ""; //not sent
	}


	@Override
	public String getDocName()
	{
		return ""; //not sent
	}

	@Override
	public String getCCDocs()
	{
		return "";
	}

	@Override
	public ArrayList getDocNums()
	{
		return null;
	}

	@Override
	public String getFillerOrderNumber()
	{
		return "";
	}

	@Override
	public String getEncounterId()
	{
		return "";
	}

	@Override
	public String getRadiologistInfo()
	{
		return "";
	}

	@Override
	public String getNteForOBX(int i, int j)
	{
		return "";
	}

	@Override
	public String getNteForPID() {
		return "";
	}
}
