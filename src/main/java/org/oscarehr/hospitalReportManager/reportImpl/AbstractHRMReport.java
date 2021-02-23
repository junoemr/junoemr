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

package org.oscarehr.hospitalReportManager.reportImpl;

import org.apache.commons.codec.binary.Base64;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractHRMReport
{
	protected String fileLocation;
	protected String fileData;

	protected Integer hrmDocumentId;
	protected Integer hrmParentDocumentId;

	public String getFileData()
	{
		return fileData;
	}

	public String getFileLocation()
	{
		return fileLocation;
	}

	public void setFileLocation(String fileLocation)
	{
		this.fileLocation = fileLocation;
	}

	public String getLegalName()
	{
		return getLegalLastName() + ", " + getLegalFirstName();
	}

	public abstract String getLegalLastName();

	public abstract String getLegalFirstName();

	public abstract List<String> getLegalOtherNames();

	public abstract XMLGregorianCalendar getXMLDateOfBirth();

	public List<Integer> getDateOfBirth()
	{
		List<Integer> dateOfBirthList = new ArrayList<>();
		XMLGregorianCalendar fullDate = getXMLDateOfBirth();
		dateOfBirthList.add(fullDate.getYear());
		dateOfBirthList.add(fullDate.getMonth());
		dateOfBirthList.add(fullDate.getDay());

		return dateOfBirthList;
	}

	public String getDateOfBirthAsString()
	{
		List<Integer> dob = getDateOfBirth();
		return dob.get(0) + "-" + dob.get(1) + "-" + dob.get(2);
	}

	public abstract byte[] getBase64BinaryContent();

	public byte[] getBinaryContent()
	{
		return Base64.decodeBase64(getBase64BinaryContent());
	}

	public abstract String getPhysicianHL7String();

	public List<String> getFirstReportAuthorPhysician()
	{
		List<String> physicianName = new ArrayList<>();
		String physicianHL7String = getPhysicianHL7String();
		String[] physicianNameArray = physicianHL7String.split("^");
		physicianName.add(physicianNameArray[0]);
		physicianName.add(physicianNameArray[1]);
		physicianName.add(physicianNameArray[2]);
		physicianName.add(physicianNameArray[3]);
		physicianName.add(physicianNameArray[6]);

		return physicianName;
	}


	public Integer getHrmDocumentId()
	{
		return hrmDocumentId;
	}

	public void setHrmDocumentId(Integer hrmDocumentId)
	{
		this.hrmDocumentId = hrmDocumentId;
	}

	public Integer getHrmParentDocumentId()
	{
		return hrmParentDocumentId;
	}

	public void setHrmParentDocumentId(Integer hrmParentDocumentId)
	{
		this.hrmParentDocumentId = hrmParentDocumentId;
	}
}
