/**
 * Copyright (c) 2008-2012 Indivica Inc.
 *
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */

package org.oscarehr.hospitalReportManager.reportImpl;

import org.apache.mina.util.Base64;
import org.oscarehr.dataMigration.model.hrm.HrmObservation;
import org.oscarehr.hospitalReportManager.HRMReport;
import oscar.util.ConversionUtils;
import xml.hrm.v4_1.DateFullOrPartial;
import xml.hrm.v4_1.Demographics;
import xml.hrm.v4_1.HealthCard;
import xml.hrm.v4_1.OmdCds;
import xml.hrm.v4_1.PatientRecord;
import xml.hrm.v4_1.PersonNameStandard;
import xml.hrm.v4_1.PersonNameStandard.LegalName.OtherName;
import xml.hrm.v4_1.ReportFormat;
import xml.hrm.v4_1.ReportsReceived;
import xml.hrm.v4_1.ReportsReceived.OBRContent;
import xml.hrm.v4_1.TransactionInformation;

import javax.xml.datatype.XMLGregorianCalendar;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class HRMReport_4_1 implements HRMReport
{
	private final OmdCds hrmReport;
	private final Demographics demographics;
	private String fileLocation;
	private String fileData;

	private Integer hrmDocumentId;
	private Integer hrmParentDocumentId;

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

	public Optional<LocalDate> getDateOfBirth()
	{
		LocalDate dateOfBirth = null;

		XMLGregorianCalendar fullDate = dateFP(demographics.getDateOfBirth());
		if (fullDate != null)
		{
			dateOfBirth = ConversionUtils.toLocalDate(fullDate);
		}

		return Optional.ofNullable(dateOfBirth);
	}

	public String getHCN()
	{
		return demographics.getHealthCard().getNumber();
	}

	public String getHCNVersion()
	{
		return demographics.getHealthCard().getVersion();
	}

	public Optional<LocalDate> getHCNExpiryDate()
	{
		LocalDate expiryDate = null;

		HealthCard healthCard = demographics.getHealthCard();
		if (healthCard != null)
		{
			expiryDate = ConversionUtils.toLocalDate(healthCard.getExpirydate());
		}

		return Optional.ofNullable(expiryDate);
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
		if (getReport().isPresent())
		{
			return getReport().get().getFormat().equals(ReportFormat.BINARY);
		}

		return false;
	}

	public String getFileExtension()
	{
		return getReport()
			.map(ReportsReceived::getFileExtensionAndVersion)
			.orElse("");
	}

	public String getTextContent()
	{
		String content = "";
		if (getReport().isPresent() && !isBinary() && getReport().get().getContent() != null)
		{
			content = getReport().get().getContent().getTextContent();
		}

		return content;
	}

	public byte[] getBinaryContent()
	{
		byte[] media = null;
		if (getReport().isPresent() && getReport().get().getContent() != null)
		{
			media = getReport().get().getContent().getMedia();
		}

		return media;
	}

	public Optional<String> getBinaryContentBase64()
	{
		if(getBinaryContent() != null)
		{
			return Optional.of(new String(Base64.encodeBase64(getBinaryContent()), StandardCharsets.UTF_8));
		}
		return Optional.empty();
	}

	public String getClassName()
	{
		if (getReport().isPresent())
		{
			return getReport().get().getClazz().value();
		}

		return "";
	}

	public String getSubClassName()
	{
		return getReport()
			.map(ReportsReceived::getSubClass)
			.orElse("");
	}

	public Optional<LocalDateTime> getEventTime()
	{
		LocalDateTime reportTime = null;

		if (getReport().isPresent() & getReport().get().getEventDateTime() != null)
		{
			XMLGregorianCalendar calendar = dateFP(hrmReport.getPatientRecord().getReportsReceived().get(0).getEventDateTime());

			if (calendar != null)
			{
				reportTime = calendar.toGregorianCalendar().toZonedDateTime().toLocalDateTime();
			}
		}

		return Optional.ofNullable(reportTime);
	}

	public String getAuthorPhysician()
	{
		StringBuilder nameBuilder = new StringBuilder();

		if (getReport().isPresent())
		{
			String physicianHL7String = getReport().get().getAuthorPhysician().getLastName();
			String[] physicianNameComponents = physicianHL7String.split("^");

			for (int i = 0; i < physicianNameComponents.length; i++)
			{
				if (!physicianNameComponents[i].isEmpty())
				{
					nameBuilder.append(physicianNameComponents[i]).append(" ");
				}
			}
		}

		return nameBuilder.toString().trim();
	}
	
	public String getSendingFacilityId()
	{
		return getReport()
			.map(ReportsReceived::getSendingFacility)
			.orElse("");
	}

	public String getSendingFacilityReportNo()
	{
		return getReport()
			.map(ReportsReceived::getSendingFacilityReportNumber)
			.orElse("");
	}

	public String getResultStatus()
	{
		return getReport()
			.map(ReportsReceived::getResultStatus)
			.orElse("");
	}

	public List<HrmObservation> getObservations()
	{
		List<HrmObservation> observationList = new ArrayList<>();
		if(getReportObrContent().isPresent())
		{
			List<OBRContent> obrSegments = getReportObrContent().get();
			for(OBRContent obrContent : obrSegments)
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

	public Optional<LocalDateTime> getFirstAccompanyingSubClassDateTime()
	{
		LocalDateTime observationTime = null;
		if(getFirstObrContent().isPresent())
		{
			OBRContent firstObr = getFirstObrContent().get();
			XMLGregorianCalendar calendar = dateFP(firstObr.getObservationDateTime());
			if (calendar != null)
			{
				observationTime = calendar.toGregorianCalendar().toZonedDateTime().toLocalDateTime();
			}
		}

		return Optional.ofNullable(observationTime);
	}

	public String getMessageUniqueId()
	{
		return getTransactionInformation()
			.map(TransactionInformation::getMessageUniqueID)
			.orElse("");
	}

	public String getDeliverToUserId()
	{
		return getTransactionInformation()
			.map(TransactionInformation::getDeliverToUserID)
			.orElse("");
	}

	public String getDeliverToUserFirstName()
	{
		String firstName = "";

		if (getTransactionInformation().isPresent())
		{
			TransactionInformation txInfo = getTransactionInformation().get();
			if (txInfo.getProvider() != null)
			{
				firstName = txInfo.getProvider().getFirstName();
			}
		}

		return firstName;
	}

	public String getDeliverToUserLastName()
	{
		String lastName = "";

		if (getTransactionInformation().isPresent())
		{
			TransactionInformation txInfo = getTransactionInformation().get();
			if (txInfo.getProvider() != null)
			{
				lastName = txInfo.getProvider().getLastName();
			}
		}

		return lastName;
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

	private boolean hasReportContent()
	{
		return hrmReport.getPatientRecord().getReportsReceived() != null &&
			!hrmReport.getPatientRecord().getReportsReceived().isEmpty();
	}

	private Optional<ReportsReceived> getReport()
	{
		ReportsReceived reportContent = null;

		if (hasReportContent())
		{
			reportContent = hrmReport.getPatientRecord().getReportsReceived().get(0);
		}

		return Optional.ofNullable(reportContent);
	}

	private Optional<List<OBRContent>> getReportObrContent()
	{
		List<OBRContent> obrContent = null;
 		if (getReport().isPresent())
		{
			if (getReport().get().getOBRContent() != null)
			{
				obrContent = getReport().get().getOBRContent();
			}
		}

 		return Optional.ofNullable(obrContent);
	}

	private Optional<OBRContent> getFirstObrContent()
	{
		OBRContent firstObr = null;
		if (getReportObrContent().isPresent() && !getReportObrContent().get().isEmpty())
		{
			firstObr = getReportObrContent().get().get(0);
		}

		return Optional.ofNullable(firstObr);
	}

	private Optional<TransactionInformation> getTransactionInformation()
	{
		TransactionInformation txInfo = null;

		PatientRecord patientRecord = hrmReport.getPatientRecord();
		if (patientRecord.getTransactionInformation() != null && !patientRecord.getTransactionInformation().isEmpty())
		{
			txInfo = patientRecord.getTransactionInformation().get(0);
		}

		return Optional.ofNullable(txInfo);
	}
}
