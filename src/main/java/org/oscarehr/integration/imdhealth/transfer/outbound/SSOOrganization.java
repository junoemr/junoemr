package org.oscarehr.integration.imdhealth.transfer.outbound;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.oscarehr.common.model.Clinic;
import org.oscarehr.common.model.Site;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.Serializable;

@Data
public class SSOOrganization implements Serializable
{

	@JsonProperty("country_code")
	private String countryCode = "CA";

	@JsonProperty("external_id")
	private String external_id;

	private String municipality;

	private String name;

	@JsonProperty("subdivision_code")
	private String subdivisionCode;

	/* OPTIONAL FIELDS (Implementation TBD)

	private String type;

    */

	public static SSOOrganization fromClinic(Clinic clinic, String instanceId)
	{
		if (StringUtils.isEmpty(instanceId))
		{
			throw new RuntimeException();
		}

		SSOOrganization org = new SSOOrganization();

		org.setExternal_id(instanceId);
		org.setMunicipality(clinic.getClinicCity());
		org.setName(clinic.getClinicName());
		org.setSubdivisionCode(clinic.getClinicProvince());

		return org;
	}

	public static SSOOrganization fromSite(Site site, String instanceId)
	{
		// For external_id want to concat instanceID + siteID,
		// just in case the credential is issued to Cloudpractice as a whole (to be shared by all our customers)
		// as opposed to a per-customer basis

		throw new NotImplementedException();
	}
}
