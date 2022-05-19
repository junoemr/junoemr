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
package org.oscarehr.demographic.transfer;

import lombok.Data;
import org.oscarehr.dataMigration.model.demographic.RosterData;
import org.oscarehr.dataMigration.model.provider.ProviderModel;
import org.oscarehr.demographic.entity.ElectronicMessagingConsentStatus;
import org.oscarehr.demographic.model.DemographicModel;
import org.oscarehr.waitList.transfer.DemographicWaitListUpdateInput;

import java.time.LocalDate;

@Data
public class DemographicUpdateInput extends DemographicCreateInput
{
	private Integer id;

	// base info
	private String sin;
	private String patientStatus;
	private LocalDate patientStatusDate;
	private LocalDate dateJoined;
	private LocalDate dateEnded;

	// contact info
	private String phoneComment;

	// physician info
	private String mrpProviderId;
	private String nurseProviderId;
	private String midwifeProviderId;
	private String residentProviderId;
	private ProviderModel referralDoctor;
	private ProviderModel familyDoctor;

	// roster info
	private RosterData currentRosterData;

	// other info

	private String alias;
	private String citizenship;
	private String spokenLanguage;
	private DemographicModel.OFFICIAL_LANGUAGE officialLanguage;
	private String countryOfOrigin;
	private String newsletter;
	private String nameOfMother;
	private String nameOfFather;
	private String veteranNumber;
	private String patientNote;
	private String patientAlert;

	private Boolean aboriginal;
	private String cytolNum;
	private Boolean paperChartArchived;
	private LocalDate paperChartArchivedDate;
	private String usSigned;
	private String privacyConsent;
	private String informedConsent;
	private String securityQuestion1;
	private String securityAnswer1;
	private String rxInteractionWarningLevel;

	private ElectronicMessagingConsentStatus electronicMessagingConsentStatus;
	private DemographicWaitListUpdateInput waitList;

	// legacy properties
	private String children;
	private String pcnIndicator;
	private String sourceOfIncome;
	private String previousAddress;
	private String anonymous;

	public DemographicUpdateInput()
	{
	}
}
