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
import org.oscarehr.common.model.Clinic;
import org.oscarehr.common.model.Site;
import oscar.util.ConversionUtils;

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

	public static SSOOrganization fromClinic(Clinic clinic, String provCode)
	{
		SSOOrganization org = new SSOOrganization();

		org.setExternalId(clinic.getUuid());
		org.setMunicipality(clinic.getClinicCity());
		org.setName(clinic.getClinicName());
		org.setSubdivisionCode(provCode);
		return org;
	}

	public static SSOOrganization fromSite(Site site, String provCode)
	{
		SSOOrganization org = new SSOOrganization();

		org.setExternalId(site.getUuid());
		org.setMunicipality(site.getCity());
		org.setName(site.getName());
		org.setSubdivisionCode(provCode);
		return org;
	}


	/**
	 * Determine if a site can be mapped to an SSOOrganization which can be accepted by the iMDHealth API.
	 * To be valid a site must have a non-empty city and name.
	 *
	 * @param site site
	 * @return true if mappable
	 */
	public static boolean canMapSite(Site site)
	{
		// Province is also required but is determined using the instance type.
		// It must be a known known ISO-3166 code and Juno allows this to be a raw String input.

		return site != null &&
				ConversionUtils.hasContent(site.getCity()) &&
				ConversionUtils.hasContent(site.getName());
	}

	/**
	 * Determine if a clinic can be mapped to an SSOOrganization which can be accepted by the iMDHealth API.
	 * To be valid a clinic must have a non-empty city and name.
	 *
	 * @param clinic clinic
	 * @return true if mappable
	 */
	public static boolean canMapClinic(Clinic clinic)
	{
		// Province is also required but is determined using the instance type.
		// It must be a known known ISO-3166 code and Juno allows this to be a raw String input.

		return clinic != null &&
				ConversionUtils.hasContent(clinic.getClinicCity()) &&
				ConversionUtils.hasContent(clinic.getClinicName());
	}
}
