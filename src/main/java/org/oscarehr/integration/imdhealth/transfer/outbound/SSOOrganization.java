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

package org.oscarehr.integration.imdhealth.transfer.outbound;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.oscarehr.common.model.Clinic;
import org.oscarehr.common.model.Site;

import java.io.Serializable;

@Data
public class SSOOrganization implements Serializable
{

	@JsonProperty("country_code")
	private String countryCode = "CA";

	@JsonProperty("external_id")
	private String externalId;

	private String municipality;

	private String name;

	@JsonProperty("subdivision_code")
	private String subdivisionCode;

	/* OPTIONAL FIELDS (Implementation TBD)

	private String type;

    */

	public static SSOOrganization fromClinic(Clinic clinic, String practiceId, String provCode)
	{
		if (StringUtils.isBlank(practiceId))
		{
			throw new RuntimeException();
		}

		SSOOrganization org = new SSOOrganization();

		// Set the practice_id as the externalID, as it is unique across all live instances, and will still be compatible
		// if the iMDHealth credentials are issued to CloudPractice instead of to each individual clinic.
		// This also allows demo and live instances to connect to the same iMDHealth organization, provided
		// that the practice id is constant between the two.
		org.setExternalId("juno_"+ practiceId);
		org.setMunicipality(clinic.getClinicCity());
		org.setName(clinic.getClinicName());

		// Use the instance type as the province code, instead of the clinic's province, because the former is enumerated
		// to ISO-3166 and the latter is a raw string.
		org.setSubdivisionCode(provCode);
		return org;
	}

	public static SSOOrganization fromSite(Site site, String instanceId, String provCode)
	{
		if (StringUtils.isBlank(instanceId))
		{
			throw new RuntimeException();
		}
		SSOOrganization org = new SSOOrganization();
		// For external_id want to concat instanceID + siteID.  In case the credential is issued to CloudPractice
		// as a whole, then this combination will be unique across all live instances.  As above, this also allows demo
		// and live instances to share the same iMDHealth organization.
		org.setExternalId("juno_"+ instanceId + site.getId());
		org.setMunicipality(site.getCity());
		org.setName(site.getName());
		org.setSubdivisionCode(provCode);
		return org;
		//throw new RuntimeException("Not yet implemented");
	}
}
