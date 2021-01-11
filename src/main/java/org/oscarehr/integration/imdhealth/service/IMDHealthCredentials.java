package org.oscarehr.integration.imdhealth.service;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.oscarehr.integration.imdhealth.transfer.inbound.BearerToken;
import org.oscarehr.integration.imdhealth.transfer.inbound.SSOCredentials;

import javax.servlet.http.HttpSession;
import java.io.Serializable;

@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.PACKAGE)
public class IMDHealthCredentials implements Serializable
{
	private static final String IMD_CREDENTIALS_KEY = "INTEGRATION.IMDHEALTH";

	private BearerToken bearerToken;

	private String accessToken;
	private String membershipId;
	private String organizationId;

	IMDHealthCredentials () {}

	static IMDHealthCredentials getFromSession(HttpSession session)
	{
		return (IMDHealthCredentials) session.getAttribute(IMD_CREDENTIALS_KEY);
	}

	void saveToSession(HttpSession session)
	{
		session.setAttribute(IMD_CREDENTIALS_KEY, this);
	}

	void loadSSOCredentials(SSOCredentials ssoCreds)
	{
		this.accessToken = ssoCreds.getAccessToken();
		this.membershipId = ssoCreds.getMembershipId();
		this.organizationId = ssoCreds.getOrganizationId();
	}
}
