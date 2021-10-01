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
import org.apache.commons.codec.binary.StringUtils;
import org.oscarehr.dataMigration.model.hrm.HrmObservation;
import org.oscarehr.hospitalReportManager.HRMReport;
import org.oscarehr.util.MiscUtils;
import oscar.util.ConversionUtils;
import xml.hrm.v4_3.DateFullOrPartial;
import xml.hrm.v4_3.Demographics;
import xml.hrm.v4_3.OmdCds;
import xml.hrm.v4_3.PersonNameSimple;
import xml.hrm.v4_3.PersonNameStandard;
import xml.hrm.v4_3.PersonNameStandard.LegalName.OtherName;
import xml.hrm.v4_3.ReportFormat;
import xml.hrm.v4_3.ReportsReceived;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class HRMReport_4_3 implements HRMReport
{
	private final OmdCds hrmReport;
	private final Demographics demographics;
	private String fileLocation;
	private String fileData;

	private Integer hrmDocumentId;
	private Integer hrmParentDocumentId;

	public HRMReport_4_3(OmdCds hrmReport)
	{
		this.hrmReport = hrmReport;
		this.demographics = hrmReport.getPatientRecord().getDemographics();
	}

	public HRMReport_4_3(OmdCds root, String hrmReportFileLocation, String fileData)
	{
		this.fileData = fileData;
		this.fileLocation = hrmReportFileLocation;
		this.hrmReport = root;
		this.demographics = hrmReport.getPatientRecord().getDemographics();
	}

	public OmdCds getDocumentRoot()
	{
		return hrmReport;
	}

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
		PersonNameStandard name = demographics.getNames();
		return getLegalLastName() + ", " + getLegalFirstName();
	}

	public String getLegalLastName()
	{
		PersonNameStandard.LegalName legalName = demographics.getNames().getLegalName();
		if (legalName != null && legalName.getLastName() != null)
		{
			return legalName.getLastName().getPart();
		}
		
		return "";
	}

	public String getLegalFirstName()
	{
		PersonNameStandard.LegalName legalName = demographics.getNames().getLegalName();
		if (legalName != null && legalName.getFirstName() != null)
		{
			return legalName.getFirstName().getPart();
		}
		
		return "";
	}

	public List<String> getLegalOtherNames()
	{
		LinkedList<String> otherNames = new LinkedList<String>();
		PersonNameStandard name = demographics.getNames();
		for(OtherName otherName : name.getLegalName().getOtherName())
		{
			otherNames.add(otherName.getPart());
		}

		return otherNames;
	}

	public List<Integer> getDateOfBirth()
	{
		List<Integer> dateOfBirthList = new ArrayList<Integer>();
		XMLGregorianCalendar fullDate = dateFP(demographics.getDateOfBirth());
		if (fullDate != null)
		{
			dateOfBirthList.add(fullDate.getYear());
			dateOfBirthList.add(fullDate.getMonth());
			dateOfBirthList.add(fullDate.getDay());
		}
		
		return dateOfBirthList;
	}
	
	public LocalDate getDateOfBirthAsLocalDate()
	{
		XMLGregorianCalendar fullDate = dateFP(demographics.getDateOfBirth());
		if (fullDate != null)
		{
			return LocalDate.of(fullDate.getYear(), fullDate.getMonth(), fullDate.getDay());
		}
		
		return null;
	}

	public String getDateOfBirthAsString()
	{
		List<Integer> dob = getDateOfBirth();
		if (dob != null && !dob.isEmpty())
		{
			return dob.get(0) + "-" + String.format("%02d", dob.get(1)) + "-" + String.format("%02d", dob.get(2));
		}
		
		return "";
	}

	public String getHCN()
	{
		if (demographics.getHealthCard() != null)
		{
			return demographics.getHealthCard().getNumber();
		}
		
		return "";
	}

	public String getHCNVersion()
	{
		if (demographics.getHealthCard() != null)
		{
			return demographics.getHealthCard().getVersion();
		}
		
		return "";
	}

	public Calendar getHCNExpiryDate()
	{
		if (demographics.getHealthCard() != null)
		{
			return demographics.getHealthCard().getExpirydate().toGregorianCalendar();
		}
		
		return null;
	}

	public String getHCNProvinceCode()
	{
		if (demographics.getHealthCard() != null)
		{
			return demographics.getHealthCard().getProvinceCode();
		}
		
		return "";
	}

	public String getGender()
	{
		if (demographics.getGender() != null)
		{
			return demographics.getGender().value();
		}
		
		return "";
	}

	public String getUniqueVendorIdSequence()
	{
		return demographics.getUniqueVendorIdSequence();
	}

	public String getAddressLine1()
	{
		if(demographics.getAddress() == null || demographics.getAddress().isEmpty())
		{
			return "";
		}
		return demographics.getAddress().get(0).getStructured().getLine1();
	}

	public String getAddressLine2()
	{
		if(demographics.getAddress() == null || demographics.getAddress().isEmpty())
		{
			return "";
		}
		return demographics.getAddress().get(0).getStructured().getLine2();
	}

	public String getAddressCity()
	{
		if(demographics.getAddress() == null || demographics.getAddress().isEmpty())
		{
			return "";
		}
		return demographics.getAddress().get(0).getStructured().getCity();
	}

	public String getCountrySubDivisionCode()
	{
		if(demographics.getAddress() == null || demographics.getAddress().isEmpty())
		{
			return "";
		}
		return demographics.getAddress().get(0).getStructured().getCountrySubdivisionCode();
	}

	public String getPostalCode()
	{
		if(demographics.getAddress() == null || demographics.getAddress().isEmpty())
		{
			return "";
		}
		return demographics.getAddress().get(0).getStructured().getPostalZipCode().getPostalCode();

	}

	public String getZipCode()
	{
		if(demographics.getAddress() == null || demographics.getAddress().isEmpty())
		{
			return "";
		}
		return demographics.getAddress().get(0).getStructured().getPostalZipCode().getZipCode();
	}

	public String getPhoneNumber()
	{
		if(demographics.getPhoneNumber() == null || demographics.getPhoneNumber().isEmpty())
		{
			return "";
		}
		return demographics.getPhoneNumber().get(0).getContent().get(0).getValue();
	}

	public String getEnrollmentStatus()
	{
		return demographics.getEnrollmentStatus();
	}

	public String getPersonStatus()
	{
		return demographics.getPersonStatusCode().value();
	}

	public boolean isBinary()
	{
		if(hrmReport.getPatientRecord().getReportsReceived() != null || hrmReport.getPatientRecord().getReportsReceived().isEmpty())
		{
			if(hrmReport.getPatientRecord().getReportsReceived().get(0).getFormat() == ReportFormat.BINARY)
			{
				return true;
			}
		}
		return false;
	}

	public String getFileExtension()
	{
		if(hrmReport.getPatientRecord().getReportsReceived() == null || hrmReport.getPatientRecord().getReportsReceived().isEmpty())
		{
			return "";
		}
		return hrmReport.getPatientRecord().getReportsReceived().get(0).getFileExtensionAndVersion();
	}

	public String getFirstReportTextContent()
	{
		String result = null;
		if(hrmReport.getPatientRecord().getReportsReceived() != null || hrmReport.getPatientRecord().getReportsReceived().isEmpty())
		{
			if(hrmReport.getPatientRecord().getReportsReceived().get(0).getFormat() == ReportFormat.BINARY)
			{
				return StringUtils.newStringUtf8(getBase64BinaryContent());
			}

			try
			{
				result = hrmReport.getPatientRecord().getReportsReceived().get(0).getContent().getTextContent();
			}
			catch(Exception e)
			{
				MiscUtils.getLogger().error("error", e);
			}
		}
		return result;
	}

	public byte[] getBase64BinaryContent()
	{
		return hrmReport.getPatientRecord().getReportsReceived().get(0).getContent().getMedia();
	}

	public byte[] getBinaryContent()
	{
		return Base64.decodeBase64(getBase64BinaryContent());
	}

	public String getFirstReportClass()
	{
		if(hrmReport.getPatientRecord().getReportsReceived() == null || hrmReport.getPatientRecord().getReportsReceived().isEmpty())
		{
			return "";
		}
		return hrmReport.getPatientRecord().getReportsReceived().get(0).getClazz().value();
	}

	public String getFirstReportSubClass()
	{
		if(hrmReport.getPatientRecord().getReportsReceived() == null || hrmReport.getPatientRecord().getReportsReceived().isEmpty())
		{
			return "";
		}
		return hrmReport.getPatientRecord().getReportsReceived().get(0).getSubClass();
	}

	public Calendar getFirstReportEventTime()
	{

		if(hrmReport.getPatientRecord().getReportsReceived() != null &&
				!hrmReport.getPatientRecord().getReportsReceived().isEmpty() &&
				hrmReport.getPatientRecord().getReportsReceived().get(0).getEventDateTime() != null)
		{
			XMLGregorianCalendar calendar = dateFP(hrmReport.getPatientRecord().getReportsReceived().get(0).getEventDateTime());
			
			if (calendar != null)
			{
				return calendar.toGregorianCalendar();
			}
		}
		return null;
	}

	public List<String> getFirstReportAuthorPhysician()
	{
		List<String> physicianName = new ArrayList<String>();
		PersonNameSimple physicianHL7 = hrmReport.getPatientRecord().getReportsReceived().get(0).getAuthorPhysician();
		
		if (physicianHL7 != null && physicianHL7.getLastName() != null)
		{
			String physicianHL7RawString = physicianHL7.getLastName();
			String[] physicianNameArray = physicianHL7RawString.split("\\^");
			
			if (physicianNameArray.length == 7)
			{
				physicianName.add(physicianNameArray[0]);
				physicianName.add(physicianNameArray[1]);
				physicianName.add(physicianNameArray[2]);
				physicianName.add(physicianNameArray[3]);
				physicianName.add(physicianNameArray[6]);
			}
		}
		
		return physicianName;
	}

	public String getSendingFacilityId()
	{
		if(hrmReport.getPatientRecord().getReportsReceived() == null || hrmReport.getPatientRecord().getReportsReceived().isEmpty())
		{
			return "";
		}
		return hrmReport.getPatientRecord().getReportsReceived().get(0).getSendingFacility();
	}

	public String getSendingFacilityReportNo()
	{
		if(hrmReport.getPatientRecord().getReportsReceived() == null || hrmReport.getPatientRecord().getReportsReceived().isEmpty())
		{
			return "";
		}
		return hrmReport.getPatientRecord().getReportsReceived().get(0).getSendingFacilityReportNumber();
	}

	public String getResultStatus()
	{
		if(hrmReport.getPatientRecord().getReportsReceived() == null || hrmReport.getPatientRecord().getReportsReceived().isEmpty())
		{
			return "";
		}
		return hrmReport.getPatientRecord().getReportsReceived().get(0).getResultStatus();
	}

	public List<HrmObservation> getObservations()
	{
		List<HrmObservation> observationList = new ArrayList<>();
		if(hrmReport.getPatientRecord().getReportsReceived() != null || !hrmReport.getPatientRecord().getReportsReceived().isEmpty())
		{
			List<ReportsReceived.OBRContent> obrContents = hrmReport.getPatientRecord().getReportsReceived().get(0).getOBRContent();

			for(ReportsReceived.OBRContent obrContent : obrContents)
			{
				HrmObservation observation = new HrmObservation();
				observation.setAccompanyingDescription(obrContent.getAccompanyingDescription());
				observation.setAccompanyingMnemonic(obrContent.getAccompanyingMnemonic());
				observation.setAccompanyingSubClass(obrContent.getAccompanyingSubClass());

				DateFullOrPartial obrDate = obrContent.getObservationDateTime();

				if(obrDate != null)
				{
					observation.setObservationDateTime(
							ConversionUtils.fillPartialCalendar(
									obrDate.getDateTime(),
									obrDate.getFullDate(),
									obrDate.getYearMonth(),
									obrDate.getYearOnly())
					);
				}
				observationList.add(observation);
			}
		}
		return observationList;
	}

	public Calendar getFirstAccompanyingSubClassDateTime()
	{
		if(hrmReport.getPatientRecord().getReportsReceived() != null &&
				!hrmReport.getPatientRecord().getReportsReceived().isEmpty() &&
				hrmReport.getPatientRecord().getReportsReceived().get(0).getOBRContent() != null &&
				!hrmReport.getPatientRecord().getReportsReceived().get(0).getOBRContent().isEmpty() &&
				hrmReport.getPatientRecord().getReportsReceived().get(0).getOBRContent().get(0) != null &&
				hrmReport.getPatientRecord().getReportsReceived().get(0).getOBRContent().get(0).getObservationDateTime() != null)
		{
			
			XMLGregorianCalendar calendar = dateFP(hrmReport.getPatientRecord().getReportsReceived().get(0).getOBRContent().get(0).getObservationDateTime());
			if (calendar != null)
			{
				return calendar.toGregorianCalendar();
			}
		}

		return null;
	}

	public String getMessageUniqueId()
	{
		if(hrmReport.getPatientRecord().getTransactionInformation() == null || hrmReport.getPatientRecord().getTransactionInformation().isEmpty())
		{
			return "";
		}
		return hrmReport.getPatientRecord().getTransactionInformation().get(0).getMessageUniqueID();
	}

	public String getDeliverToUserId()
	{
		if(hrmReport.getPatientRecord().getTransactionInformation() == null || hrmReport.getPatientRecord().getTransactionInformation().isEmpty())
		{
			return "";
		}
		return hrmReport.getPatientRecord().getTransactionInformation().get(0).getDeliverToUserID();
	}

	public String getDeliverToUserIdFirstName()
	{
		if(hrmReport.getPatientRecord().getTransactionInformation() == null || hrmReport.getPatientRecord().getTransactionInformation().isEmpty())
		{
			return "";
		}
		if(hrmReport.getPatientRecord().getTransactionInformation().get(0).getProvider() == null)
			return null;
		return hrmReport.getPatientRecord().getTransactionInformation().get(0).getProvider().getFirstName();
	}

	public String getDeliverToUserIdLastName()
	{
		if(hrmReport.getPatientRecord().getTransactionInformation() == null || hrmReport.getPatientRecord().getTransactionInformation().isEmpty())
		{
			return "";
		}
		if(hrmReport.getPatientRecord().getTransactionInformation().get(0).getProvider() == null)
			return null;
		return hrmReport.getPatientRecord().getTransactionInformation().get(0).getProvider().getLastName();
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


	private XMLGregorianCalendar dateFP(DateFullOrPartial dfp)
	{
		if(dfp == null) return null;

		if(dfp.getDateTime() != null) return dfp.getDateTime();
		else if(dfp.getFullDate() != null) return dfp.getFullDate();
		else if(dfp.getYearMonth() != null) return dfp.getYearMonth();
		else if(dfp.getYearOnly() != null) return dfp.getYearOnly();
		return null;
	}
}
