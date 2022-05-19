/**
 * Copyright (c) 2008-2012 Indivica Inc.
 *
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */

package org.oscarehr.hospitalReportManager;

import org.oscarehr.dataMigration.model.hrm.HrmObservation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HRMReport
{
	String getFileData();

	String getFileLocation();

	void setFileLocation(String fileLocation);

	String getLegalName();

	String getLegalLastName();

	String getLegalFirstName();

	List<String> getLegalOtherNames();

	Optional<LocalDate> getDateOfBirth();

	String getHCN();

	String getHCNVersion();

	Optional<LocalDate> getHCNExpiryDate();

	String getHCNProvinceCode();

	String getGender();

	String getUniqueVendorIdSequence();

	String getAddressLine1();

	String getAddressLine2();

	String getAddressCity();

	String getCountrySubDivisionCode();

	String getPostalCode();

	String getZipCode();

	String getPhoneNumber();

	String getEnrollmentStatus();

	String getPersonStatus();

	boolean isBinary();

	String getFileExtension();

	String getTextContent();

	Optional<String> getBinaryContentBase64();

	byte[] getBinaryContent();

	String getClassName();

	String getSubClassName();

	Optional<LocalDateTime> getEventTime();

	String getAuthorPhysician();

	String getSendingFacilityId();

	String getSendingFacilityReportNo();

	String getResultStatus();

	List<HrmObservation> getObservations();

	Optional<LocalDateTime> getFirstAccompanyingSubClassDateTime();

	String getMessageUniqueId();

	String getDeliverToUserId();

	String getDeliverToUserFirstName();

	String getDeliverToUserLastName();

	Integer getHrmDocumentId();

	void setHrmDocumentId(Integer hrmDocumentId);

	Integer getHrmParentDocumentId();

	void setHrmParentDocumentId(Integer hrmParentDocumentId);
}