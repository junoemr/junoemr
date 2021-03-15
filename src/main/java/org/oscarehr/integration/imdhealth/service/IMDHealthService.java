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

package org.oscarehr.integration.imdhealth.service;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;
import org.hibernate.annotations.Fetch;
import org.oscarehr.common.dao.ClinicDAO;
import org.oscarehr.common.dao.SiteDao;
import org.oscarehr.common.encryption.StringEncryptor;
import org.oscarehr.common.model.Clinic;
import org.oscarehr.common.model.Provider;
import org.oscarehr.common.model.Site;
import org.oscarehr.integration.dao.IntegrationDao;
import org.oscarehr.integration.exception.IntegrationException;
import org.oscarehr.integration.imdhealth.exception.IMDHealthException;
import org.oscarehr.integration.imdhealth.exception.SSOBearerException;
import org.oscarehr.integration.imdhealth.exception.SSOLoginException;
import org.oscarehr.integration.imdhealth.transfer.inbound.BearerToken;
import org.oscarehr.integration.imdhealth.transfer.inbound.SSOCredentials;
import org.oscarehr.integration.imdhealth.transfer.outbound.SSOOrganization;
import org.oscarehr.integration.imdhealth.transfer.outbound.SSORequest;
import org.oscarehr.integration.imdhealth.transfer.outbound.SSOUser;
import org.oscarehr.integration.model.Integration;
import org.oscarehr.clinic.service.ClinicService;
import org.oscarehr.integration.service.IntegrationService;
import org.oscarehr.provider.dao.ProviderDataDao;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.provider.service.ProviderService;
import org.oscarehr.site.service.SiteService;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import oscar.OscarProperties;

import javax.annotation.Nullable;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class IMDHealthService
{
	@Autowired
	IntegrationService integrationService;

	@Autowired
	IMDCommunicationService communicationService;

	@Autowired
	IntegrationDao integrationDao;

	@Autowired
	ClinicDAO clinicDao;
	@Autowired
	org.oscarehr.clinic.service.ClinicService clinicService;

	@Autowired
	SiteDao siteDao;
	@Autowired
	SiteService siteService;

	@Autowired
	ProviderDataDao providerDataDao;
	@Autowired
	ProviderService providerService;

	private static final String PROP_KEY_APP = "imdhealth_app_domain";
	private static final String PROP_KEY_SCHEME = "imdhealth_scheme";

	protected static final String HOST_URL = OscarProperties.getInstance().getProperty(PROP_KEY_APP);
	protected static final String DEFAULT_SCHEME= OscarProperties.getInstance().getProperty(PROP_KEY_SCHEME);


	private static final Logger logger = MiscUtils.getLogger();

	/**
	 * Create an iMD Health integration or update an existing iMD Health integration to the database.
	 *
	 * @param session user session
	 * @param clientId iMD Health client id
	 * @param clientSecret iMD Health client secret
	 * @param siteId Optional Juno site id, null for single site instances, or to share an integration across all sites.
	 *
	 * @return Updated Juno integration object
	 */
	public Integration updateSSOCredentials(HttpSession session, String clientId, String clientSecret, @Nullable Integer siteId)
	{
		Integration existingIntegration = integrationService.findIntegrationByTypeAndSite(Integration.INTEGRATION_TYPE_IMD_HEALTH, siteId);

		// We will need access to the plaintext value of the key to generate SSO login keys, so this needs to be reversible
		String encryptedSecret = StringEncryptor.encrypt(clientSecret);

		if (existingIntegration != null)
		{
			existingIntegration.setRemoteId(clientId);
			existingIntegration.setApiKey(encryptedSecret);
			integrationDao.merge(existingIntegration);
			IMDHealthCredentials.removeFromSession(session);

			return existingIntegration;
		}
		else
		{
			Integration imdIntegration = new Integration();
			imdIntegration.setIntegrationType(Integration.INTEGRATION_TYPE_IMD_HEALTH);
			imdIntegration.setRemoteId(clientId);
			imdIntegration.setApiKey(encryptedSecret);
			integrationDao.persist(imdIntegration);

			return imdIntegration;
		}
	}
	/**
	 * Initialize imD Health Organizations for each provider/site combination.
	 *
	 * @param integrationId integration id
	 *
	 * @return A list of failed initializations
	 */
	public List<String> initializeAllUsers(Integer integrationId) throws IntegrationException
	{
		Integration integration = integrationDao.find(integrationId);
		BearerToken token = getBearerToken(integration);
		OscarProperties oscarProperties = OscarProperties.getInstance();

		Clinic clinic = clinicDao.getClinic();
		List<ProviderData> providerDataList = providerDataDao.findByActiveStatus(true);
		List<Site> sites = siteDao.getAllActiveSites();

		List<String> failedToInitialize = new ArrayList<>();

		// Set clinic uuid
		if (clinic.getUuid() == null)
		{
			clinicService.createAndSaveClinicUuid(clinic);
		}

		getProviderClinicCredentials(token, providerDataList, clinic, failedToInitialize);
		for (Site site : sites)
		{
			// Set site uuid
			if (site.getUuid() == null)
			{
				siteService.createAndSaveSiteUuid(site);
			}

			Set<Provider> providers = site.getProviders();

			if (oscarProperties.isMultisiteEnabled())
			{
				getProviderSiteCredentials(token, providers, site, failedToInitialize);
			}
		}
		return failedToInitialize;
	}

	private void getProviderClinicCredentials(BearerToken token, List<ProviderData> providerDataList, Clinic clinic, List<String> failedToInitialize) throws SSOLoginException
	{
		for (ProviderData providerData : providerDataList)
		{
			// Set provider uuid
			if (providerData.getImdHealthUuid() == null)
			{
				providerService.createAndSaveProviderImdHealthUuid(providerData);
			}

			SSOCredentials ssoCredentials = getSSOCredentials(token, providerData, clinic);
			if (ssoCredentials == null)
			{
				failedToInitialize.add("ProviderNo " + providerData.getProviderNo() + ", Clinic " + clinic.getClinicName());
			}
		}
	}

	private void getProviderSiteCredentials(BearerToken token, Set<Provider> providers, Site site, List<String> failedToInitialize) throws SSOLoginException
	{
		for (Provider provider : providers)
		{
			if (provider.isActive())
			{
				SSOCredentials ssoCredentials = getSSOCredentials(token, provider, site);

				if (ssoCredentials == null)
				{
					failedToInitialize.add("ProviderNo " + provider.getProviderNo() + " ,SiteId " + site.getId());
				}
			}
		}
	}

	/**
	 * Generate the SSO link needed to connect to iMDHealth, logging in if necessary.
	 *
	 * A login will be performed if:
	 * 1)  The user is not logged in to iMDHealth
	 * 2)  The SSO credentials have expired
	 *
	 * @param session User session
	 * @param siteId {Optional} siteId to use to login.  If not needed, use null
	 *
	 * @return URL formatted link to iMDHealth with SSO login credentials applied
	 */
	public String getSSOLink(HttpSession session, @Nullable Integer siteId) throws IntegrationException
	{
		IMDHealthCredentials credentials = fetchCredentials(session, siteId);
		String returnString = "";

		if (credentials != null)
		{
			URIBuilder builder = new URIBuilder()
					.setScheme(DEFAULT_SCHEME)
					.setHost(HOST_URL)
					.setParameter("access_token", credentials.getAccessToken())
					.setParameter("membership_id", StringUtils.trimToEmpty(credentials.getMembershipId()))
					.setParameter("organization_id", StringUtils.trimToEmpty(credentials.getOrganizationId()));

			returnString = builder.toString();
		}

		return returnString;
	}

	/**
	 * Test an IMDIntegration for a site by attempting to use the credentials stored on the integration to
	 * retrieve a bearer token from the SSO api.  Any non-empty token is considered a positive response.
	 *
	 * @param integrationId integration to test
	 *
	 * @return true if a non-empty token is retrieved from the API, false in all other cases.
	 */
	public boolean testIntegration(Integer integrationId) throws IntegrationException
	{
		try
		{
			Integration imdIntegration = integrationDao.find(integrationId);

			if (!imdIntegration.getIntegrationType().equals(Integration.INTEGRATION_TYPE_IMD_HEALTH))
			{
				throw new IntegrationException("Invalid iMDHealth integration");
			}

			BearerToken token = getBearerToken(imdIntegration);
			return token != null && !StringUtils.isEmpty(token.getAccessToken());
		}
		catch (SSOBearerException ex)
		{
			return false;
		}
	}

	/**
	 * Remove an iMD Health integration from the database, returning it if found.  If not found, returns null.
	 * Also removes the associated data from the session.
	 *
	 * @param session user session
	 * @param integrationId id of the iMD Health integration to delete
	 * @return Integration removed if found, null otherwise
	 * @throws IntegrationException if the integration specified by integrationId is not an iMD Health integration.
	 */
	public Integration removeIntegration(HttpSession session, Integer integrationId) throws IntegrationException
	{
		Integration integration = integrationDao.find(integrationId);

		if (integration != null)
		{
			checkIntegrationIsIMDType(integration);
			integrationDao.remove(integrationId);
			IMDHealthCredentials.removeFromSession(session);
		}

		return integration;
	}


	/**
	 * Fetch IMD credentials, either from the session or the database, with priority given to the session.  If found in the database,
	 * they will be loaded onto the session for future use.
	 *
	 * @param session user session
	 * @param siteId {optional} if searching for credentials associated with a site, the id of that site.  If single-site or not
	 *               needed, leave null.
	 *
	 * @return IMDHealth credentials if found, null otherwise;
	 */
	private IMDHealthCredentials fetchCredentials(HttpSession session, @Nullable Integer siteId) throws IMDHealthException
	{
		IMDHealthCredentials credentials = IMDHealthCredentials.getFromSession(session);

		if (credentials == null || credentials.getBearerToken().isExpired())
		{
			Integration imdIntegration = integrationDao.findByIntegrationTypeAndSiteId(Integration.INTEGRATION_TYPE_IMD_HEALTH, siteId);

			if (imdIntegration != null)
			{
				credentials = login(imdIntegration, session, siteId);
			}
		}

		return credentials;
	}

	/**
	 * Log in via the iMDHealth SSO api and store the credentials on the session.
	 *
	 * The user is determined by the current logged in user.
	 *
	 * The organization is identified by first by the Juno context path (practice id), and then
	 * if needed, differentiated by siteId.  This makes the iMDCredentials compatible if regardless
	 * if issued organizationally (ie: to all of CloudPractice), or on a per-clinic basis.
	 *
	 * @param imdHealthIntegration iMDHealth integration to use to login
	 * @param session User session
	 * @param siteId {optional} Juno site Id
	 *
	 * @return iMDHealth SSO credentials
	 */
	private IMDHealthCredentials login(Integration imdHealthIntegration, HttpSession session, Integer siteId) throws IMDHealthException
	{
		IMDHealthCredentials credentials = new IMDHealthCredentials();

		BearerToken token = getBearerToken(imdHealthIntegration);
		credentials.setBearerToken(token);

		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(session);
		Provider provider = loggedInInfo.getLoggedInProvider();

		SSOCredentials ssoInfo = getSSOCredentials(token, provider, siteDao.getById(siteId));
		credentials.loadSSOCredentials(ssoInfo);

		credentials.saveToSession(session);
		return credentials;
	}

	private BearerToken getBearerToken(Integration imdHealthIntegration) throws SSOBearerException
	{
		try
		{
			String decryptedSecret = StringEncryptor.decrypt(imdHealthIntegration.getApiKey());
			String clientId = imdHealthIntegration.getRemoteId();
			return communicationService.getBearerToken(clientId, decryptedSecret);
		}
		catch (HttpClientErrorException | HttpServerErrorException ex)
		{
			logger.error("Error retrieving bearer token", ex);
			throw new SSOBearerException("IMDHealth Authentication error");
		}
	}

	// Clinic
	private SSOCredentials getSSOCredentials(BearerToken token,
	                                         ProviderData providerData,
	                                         Clinic clinic) throws SSOLoginException
	{
		try
		{
			SSOUser user = SSOUser.fromProvider(providerData);
			SSOOrganization organization;

			String provinceCode = OscarProperties.getInstance().getInstanceTypeUpperCase();
			organization = SSOOrganization.fromClinic(clinic, provinceCode);

			SSORequest ssoRequest = new SSORequest(user, organization);
			return communicationService.SSOLogin(token, ssoRequest);
		}
		catch (HttpClientErrorException | HttpServerErrorException ex)
		{
			logger.error("Error during SSO login", ex);
			throw new SSOLoginException("IMDHealth Login error");
		}
	}

	// Site
	private SSOCredentials getSSOCredentials(BearerToken token,
											 Provider provider,
											 Site site) throws SSOLoginException
	{
		try
		{
			SSOUser user = SSOUser.fromProvider(provider);
			SSOOrganization organization;

			String provinceCode = OscarProperties.getInstance().getInstanceTypeUpperCase();
			organization = SSOOrganization.fromSite(site, provinceCode);

			SSORequest ssoRequest = new SSORequest(user, organization);
			return communicationService.SSOLogin(token, ssoRequest);
		}
		catch (HttpClientErrorException | HttpServerErrorException ex)
		{
			logger.error("Error during SSO login", ex);
			throw new SSOLoginException("IMDHealth Login error");
		}
	}

	private static void checkIntegrationIsIMDType(Integration integration) throws IntegrationException
	{
		if (integration != null)
		{
			if (!integration.getIntegrationType().equals(Integration.INTEGRATION_TYPE_IMD_HEALTH))
			{
				throw new IntegrationException("Integration is not a valid iMDHealth integration");
			}
		}
	}
}
