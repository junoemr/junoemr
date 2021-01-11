package org.oscarehr.integration.imdhealth.transfer.outbound;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class SSORequest implements Serializable
{
	@JsonProperty("user")
	private SSOUser userInfo;

	@JsonProperty("organization")
	private SSOOrganization organizationInfo;

	public SSORequest(SSOUser user, SSOOrganization organization)
	{
		this.userInfo = user;
		this.organizationInfo = organization;
	}
}
