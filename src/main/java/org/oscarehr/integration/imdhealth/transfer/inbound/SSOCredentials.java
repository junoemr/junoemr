package org.oscarehr.integration.imdhealth.transfer.inbound;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SSOCredentials implements Serializable
{
	@JsonProperty("access_token")
	private String accessToken;

	@JsonProperty("membershipId")
	private String membershipId;

	@JsonProperty("organizationId")
	private String organizationId;
}
