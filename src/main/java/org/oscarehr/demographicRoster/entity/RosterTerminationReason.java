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
package org.oscarehr.demographicRoster.entity;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum RosterTerminationReason
{
	HEALTH_NUM("Health Number error", 12),
	MINISTRY_REPORTED_DECEASED("Patient identified as deceased on ministry database", 14),
	ASSIGNED_IN_ERROR("Patient added to roster in error", 24),
	REGISTERED_RED_CARD("Pre-member/ Assigned member ended; now enrolled or registered with red and white health card", 30),
	REGISTERED_PHOTO_CARD("Pre-member/ Assigned member ended; now enrolled or registered with photo health card", 32),
	CONFIDENTIAL("Termination reason cannot be released (due to patient confidentiality)", 33),
	TRANSFERRED("Patient transferred from roster per physician request", 35),
	RE_ENROLLED("Original enrolment ended; patient now re-enroled", 36),
	ENTERED_LONG_TERM_CARE("Original enrolment ended; patient now enrolled as Long Term Care", 37),
	LEFT_LONG_TERM_CARE("Long Term Care enrolment ended; patient has left Long Term Care", 38),
	ASSIGNMENT_ENDED("Assigned member status ended; roster transferred per physician request", 39),
	PHYSICIAN_REPORTED_DECEASED("Physician reported member as deceased", 40),
	NO_LONGER_MEETS_CRITERIA_REASSIGNED("Patient no longer meets selection criteria for your roster - assigned to another physician", 41),
	PHYSICIAN_ENDED_LONG_TERM_CARE("Physician ended enrolment; patient entered Long Term Care facility", 42),
	PHYSICIAN_ENDED_PATIENT_ENROLMENT("Physician ended patient enrolment", 44),
	NO_LONGER_MEETS_CRITERIA("Patient no longer meets selection criteria for your roster", 51),
	LEFT_GEOGRAPHIC_AREA("Physician ended enrolment; patient moved out of geographic area", 53),
	LEFT_PROVINCE("Physician ended enrolment; patient left province", 54),
	PATIENT_REQUESTED_END("Physician ended enrolment; per patient request", 56),
	PATIENT_TERMINATED_ENROLMENT("Enrolment terminated by patient", 57),
	OUT_OF_GEOGRAPHIC_AREA("Enrolment ended; patient out of geographic area", 59),
	NO_CURRENT_ELIGIBILITY("No current eligibility", 60),
	OUT_OF_GEOGRAPHIC_AREA_OVERRIDE_APPLIED("Patient out of geographic area; address over-ride applied", 61),
	OUT_OF_GEOGRAPHIC_AREA_OVERRIDE_REMOVED("Patient out of geographic area; address over-ride removed", 62),
	NO_ELIGIBILITY_73("No current eligibility", 73),
	NO_ELIGIBILITY_74("No current eligibility", 74),
	NO_CONSENT_FORM("Ministry has not received enrolment/ Consent form", 82),
	CONFIDENTIAL_84("Termination reason cannot be released (due to patient confidentiality)", 84),
	CONFIDENTIAL_90("Termination reason cannot be released (due to patient confidentiality)", 90),
	CONFIDENTIAL_91("Termination reason cannot be released (due to patient confidentiality)", 91);

	private static final Map<Integer, RosterTerminationReason> BY_CODE = new HashMap<>();

	public final String description;
	public final Integer terminationCode;

	static
	{
		for(RosterTerminationReason terminationReason : values())
		{
			BY_CODE.put(terminationReason.terminationCode, terminationReason);
		}
	}

	RosterTerminationReason(String description, Integer terminationCode)
	{
		this.description = description;
		this.terminationCode = terminationCode;
	}

	public String getDescription()
	{
		return description;
	}

	public Integer getTerminationCode()
	{
		return terminationCode;
	}

	public static RosterTerminationReason getByCode(Integer terminationCode)
	{
		return BY_CODE.get(terminationCode);
	}

	@Override
	@JsonValue
	public String toString()
	{
		return String.valueOf(getTerminationCode());
	}
}
