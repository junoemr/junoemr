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

import org.apache.mina.util.Base64;
import org.oscarehr.dataMigration.model.hrm.HrmObservation;
import org.oscarehr.hospitalReportManager.HRMReport;
import oscar.util.ConversionUtils;
import xml.hrm.v4_3.DateFullOrPartial;
import xml.hrm.v4_3.Demographics;
import xml.hrm.v4_3.HealthCard;
import xml.hrm.v4_3.OmdCds;
import xml.hrm.v4_3.PatientRecord;
import xml.hrm.v4_3.PersonNameSimple;
import xml.hrm.v4_3.PersonNameStandard;
import xml.hrm.v4_3.PersonNameStandard.LegalName.OtherName;
import xml.hrm.v4_3.ReportFormat;
import xml.hrm.v4_3.ReportsReceived;
import xml.hrm.v4_3.ReportsReceived.OBRContent;
import xml.hrm.v4_3.TransactionInformation;

import javax.xml.datatype.XMLGregorianCalendar;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class HRMReport_4_3 implements HRMReport
{
	private final OmdCds hrmReport;
	private final Demographics demographics;
	private String fileLocation;
	private String fileData;

	private Integer hrmDocumentId;
	private Integer hrmParentDocumentId;

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
		LinkedList<String> otherNames = new LinkedList<>();
		PersonNameStandard name = demographics.getNames();
		for(OtherName otherName : name.getLegalName().getOtherName())
		{
			otherNames.add(otherName.getPart());
		}

		return otherNames;
	}

	public Optional<LocalDate> getDateOfBirth()
	{
		LocalDate dob = null;

		XMLGregorianCalendar fullDate = dateFP(demographics.getDateOfBirth());
		if (fullDate != null)
		{
			dob = LocalDate.of(fullDate.getYear(), fullDate.getMonth(), fullDate.getDay());
		}
		
		return Optional.ofNullable(dob);
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
		String className = "";
		if (getReport().isPresent())
		{
			className = getReport().get().getClazz().value();
		}

		return className;
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
		if (getReport().isPresent() && getReport().get().getEventDateTime() != null)
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
			PersonNameSimple physicianHL7 = getReport().get().getAuthorPhysician();
			if (physicianHL7 != null && physicianHL7.getLastName() != null)
			{
				String[] physicianNameArray = physicianHL7.getLastName().split("\\^");
				for (String namePart : physicianNameArray)
				{
					if (!namePart.isEmpty())
					{
						nameBuilder.append(namePart).append(" ");
					}
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
		if(hasReportContent())
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