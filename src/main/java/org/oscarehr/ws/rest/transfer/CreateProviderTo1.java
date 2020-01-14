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
package org.oscarehr.ws.rest.transfer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateProviderTo1 implements Serializable
{
	// user info
	private String firstName;
	private String lastName;
	private String type;
	private String speciality;
	private String team;
	private String sex;
	private LocalDate dateOfBirth;

	// login info
	private String email;
	private String userName;
	private String password;
	private String secondLevelPasscode;

	// Contact Information
	private String address;
	private String homePhone;
	private String workPhone;
	private String cellPhone;
	private String otherPhone;
	private String fax;
	private String contactEmail;
	private String pagerNumber;

	// access Roles
	private String[] userRoles;

	// site assignments
	private String[] siteAssignment;

	// BC billing
	private String bcBillingNo;
	private String bcRuralRetentionCode;
	private String bcServiceLocation;

	// ON billing
	private String onGroupNumber;
	private String onSpecialityCode;
	private String onVisitLocation;
	private String onServiceLocationIndicator;

	// AB billing
	private String abClinic;
	private String abSourceCode;
	private String abSkillCode;
	private String abLocationCode;
	private String abBANumber;
	private String abFacilityNumber;
	private String abFunctionalCenter;
	private String abRoleModifier;

	// SK billing
	private String skMode;
	private String skLocationCode;
	private String skSubmissionType;
	private String skCorporationIndicator;

	// Common Billing
	private String ohipNo;
	private String thirdPartyBillingNo;
	private String alternateBillingNo;

	//3rd Party Identifiers
	private String cpsid;
	private String ihaProviderMnemonic;
	private String connectCareId;
	private String takNumber;
	private String lifeLabsClientIds;
	private String eDeliveryIds;

}
