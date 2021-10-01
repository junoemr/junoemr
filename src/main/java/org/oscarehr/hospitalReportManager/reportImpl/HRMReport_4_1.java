/**
 * Copyright (c) 2008-2012 Indivica Inc.
 *
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */

package org.oscarehr.hospitalReportManager.reportImpl;

import org.apache.commons.codec.binary.Base64;
import org.oscarehr.dataMigration.model.hrm.HrmObservation;
import org.oscarehr.hospitalReportManager.HRMReport;

import xml.hrm.v4_1.OmdCds;
import xml.hrm.v4_1.DateFullOrPartial;
import xml.hrm.v4_1.Demographics;
import xml.hrm.v4_1.PersonNameStandard;
import xml.hrm.v4_1.PersonNameStandard.LegalName.OtherName;
import xml.hrm.v4_1.ReportFormat;
import xml.hrm.v4_1.ReportsReceived;

import org.oscarehr.util.MiscUtils;
import oscar.util.ConversionUtils;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class HRMReport_4_1 implements HRMReport
{
	private final OmdCds hrmReport;
	private final Demographics demographics;
	private String fileLocation;
	private String fileData;

	private Integer hrmDocumentId;
	private Integer hrmParentDocumentId;

	public HRMReport_4_1(OmdCds hrmReport)
	{
		this.hrmReport = hrmReport;
		this.demographics = hrmReport.getPatientRecord().getDemographics();
	}

	public HRMReport_4_1(OmdCds root, String hrmReportFileLocation, String fileData)
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
		return name.getLegalName().getLastName().getPart() + ", " + name.getLegalName().getFirstName().getPart();
	}

	public String getLegalLastName()
	{
		PersonNameStandard name = demographics.getNames();
		return name.getLegalName().getLastName().getPart();
	}

	public String getLegalFirstName()
	{
		PersonNameStandard name = demographics.getNames();
		return name.getLegalName().getFirstName().getPart();
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

	public String getHCN()
	{
		return demographics.getHealthCard().getNumber();
	}

	public String getHCNVersion()
	{
		return demographics.getHealthCard().getVersion();
	}

	public Calendar getHCNExpiryDate()
	{
		return demographics.getHealthCard().getExpirydate().toGregorianCalendar();
	}

	public String getHCNProvinceCode()
	{
		return demographics.getHealthCard().getProvinceCode();
	}

	public String getGender()
	{
		return demographics.getGender().value();
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
				return new Base64().encodeToString(getBinaryContent());
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
		try
		{
			return hrmReport.getPatientRecord().getReportsReceived().get(0).getContent().getMedia();
		}
		catch(Exception e)
		{
			MiscUtils.getLogger().error("error", e);
		}
		return null;
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
			return dateFP(hrmReport.getPatientRecord().getReportsReceived().get(0).getEventDateTime()).toGregorianCalendar();
		return null;
	}

	public List<String> getFirstReportAuthorPhysician()
	{
		List<String> physicianName = new ArrayList<String>();
		String physicianHL7String = hrmReport.getPatientRecord().getReportsReceived().get(0).getAuthorPhysician().getLastName();
		String[] physicianNameArray = physicianHL7String.split("^");
		physicianName.add(physicianNameArray[0]);
		physicianName.add(physicianNameArray[1]);
		physicianName.add(physicianNameArray[2]);
		physicianName.add(physicianNameArray[3]);
		physicianName.add(physicianNameArray[6]);

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
				hrmReport.getPatientRecord().getReportsReceived().get(0).getOBRContent().get(0) != null &&
				hrmReport.getPatientRecord().getReportsReceived().get(0).getOBRContent().get(0).getObservationDateTime() != null)
		{
			return dateFP(hrmReport.getPatientRecord().getReportsReceived().get(0).getOBRContent().get(0).getObservationDateTime()).toGregorianCalendar();
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
