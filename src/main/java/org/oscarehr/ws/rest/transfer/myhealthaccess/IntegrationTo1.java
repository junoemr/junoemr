package org.oscarehr.ws.rest.transfer.myhealthaccess;

import org.oscarehr.integration.model.Integration;

public class IntegrationTo1
{
	private Integer id;
	private String remoteId;
	private String apiKey;
	private Integer siteId;
	private String siteName;
	private String integrationType;

	public IntegrationTo1(Integration integration)
	{
		this.id = integration.getId();
		this.remoteId = integration.getRemoteId();
		this.apiKey = integration.getApiKey();
		this.siteName = integration.getSite().getName();
		this.siteId = integration.getSite().getId();
		this.integrationType = integration.getIntegrationType();
	}

	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public String getRemoteId()
	{
		return remoteId;
	}

	public void setRemoteId(String remoteId)
	{
		this.remoteId = remoteId;
	}

	public String getApiKey()
	{
		return apiKey;
	}

	public void setApiKey(String apiKey)
	{
		this.apiKey = apiKey;
	}

	public Integer getSiteId()
	{
		return siteId;
	}

	public void setSiteId(Integer siteId)
	{
		this.siteId = siteId;
	}

	public String getSiteName()
	{
		return siteName;
	}

	public void setSiteName(String siteName)
	{
		this.siteName = siteName;
	}

	public String getIntegrationType()
	{
		return integrationType;
	}

	public void setIntegrationType(String integrationType)
	{
		this.integrationType = integrationType;
	}
}
