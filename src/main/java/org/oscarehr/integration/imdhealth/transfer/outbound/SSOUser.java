package org.oscarehr.integration.imdhealth.transfer.outbound;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.oscarehr.util.LoggedInInfo;

import java.io.Serializable;

@Data
public class SSOUser implements Serializable
{
	private boolean kiosk = false;

	@JsonProperty("external_id")
	private String externalId;

	@JsonProperty("first_name")
	private String firstName;

	@JsonProperty("last_name")
	private String lastName;

	/* OPTIONAL FIELDS (Implementation TBD)

	private String gender;
	private String practitionerType;
	private String preferredLocale;
	private String prefix;

	*/

	public static SSOUser fromLoggedInInfo(LoggedInInfo loggedInInfo)
	{
		SSOUser user = new SSOUser();

		user.externalId = loggedInInfo.getLoggedInProviderNo();
		user.firstName = loggedInInfo.getLoggedInProvider().getFirstName();
		user.lastName = loggedInInfo.getLoggedInProvider().getLastName();

		return user;
	}
}
