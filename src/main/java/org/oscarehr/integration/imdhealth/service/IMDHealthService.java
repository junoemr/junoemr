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
import org.apache.log4j.Logger;
import org.oscarehr.clinic.service.ClinicService;
import org.oscarehr.common.dao.ClinicDAO;
import org.oscarehr.common.dao.SiteDao;
import org.oscarehr.common.encryption.StringEncryptor;
import org.oscarehr.common.model.Clinic;
import org.oscarehr.common.model.Provider;
import org.oscarehr.common.model.Site;
import org.oscarehr.demographic.dao.DemographicDao;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.integration.dao.IntegrationDao;
import org.oscarehr.integration.exception.IntegrationException;
import org.oscarehr.integration.imdhealth.exception.IMDHealthException;
import org.oscarehr.integration.imdhealth.exception.SSOBearerException;
import org.oscarehr.integration.imdhealth.exception.SSOLoginException;
import org.oscarehr.integration.imdhealth.transfer.inbound.BearerToken;
import org.oscarehr.integration.imdhealth.transfer.inbound.SSOCredentials;
import org.oscarehr.integration.imdhealth.transfer.outbound.SSOOrganization;
import org.oscarehr.integration.imdhealth.transfer.outbound.SSOPatient;
import org.oscarehr.integration.imdhealth.transfer.outbound.SSORequest;
import org.oscarehr.integration.imdhealth.transfer.outbound.SSOUser;
import org.oscarehr.integration.model.Integration;
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
import oscar.util.ConversionUtils;

import javax.annotation.Nullable;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashSet;
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
	ClinicService clinicService;
	@Autowired
	SiteDao siteDao;
	@Autowired
	SiteService siteService;
	@Autowired
	ProviderDataDao providerDataDao;
	@Autowired
	ProviderService providerService;
	@Autowired
	DemographicDao demographicDao;

	private static final String PROP_KEY_APP = "imdhealth_app_domain";
	private static final String PROP_KEY_SCHEME = "imdhealth_scheme";
	private static final Logger logger = MiscUtils.getLogger();

	protected static final String HOST_URL = OscarProperties.getInstance().getProperty(PROP_KEY_APP);
	protected static final String DEFAULT_SCHEME= OscarProperties.getInstance().getProperty(PROP_KEY_SCHEME);

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

		if (clinic.getUuid() == null)
		{
			clinicService.createAndSaveClinicUuid(clinic);
		}

		failedToInitialize.addAll(loginProviderClinic(token, providerDataList, clinic));

		for (Site site : sites)
		{
			if (site.getUuid() == null)
			{
				siteService.createAndSaveSiteUuid(site);
			}

			if (oscarProperties.isMultisiteEnabled())
			{
				failedToInitialize.addAll(loginProviderSite(token, site));
			}
		}

		return failedToInitialize;
	}

	private List<String> loginProviderClinic(BearerToken token, List<ProviderData> providerDataList, Clinic clinic) throws IMDHealthException
	{
		List<String> failedProviderClinicList = new ArrayList<>();

		if (isValidClinic(clinic))
		{
			for (ProviderData providerData : providerDataList)
			{
				if (providerData.getImdHealthUuid() == null)
				{
					providerService.createAndSaveProviderImdHealthUuid(providerData);
				}

				boolean success = postProviderAtClinic(token, providerData);

				if (!success)
				{
					failedProviderClinicList.add("Failed to get sso-credentials for ProviderNo: " + providerData.getProviderNo() + ", Clinic: " + clinic.getClinicName());
				}
			}
		}
		else
		{
			failedProviderClinicList.add("Clinic is not valid: " + clinic.getClinicName());
		}

		return failedProviderClinicList;
	}

	private List<String> loginProviderSite(BearerToken token, Site site) throws IMDHealthException
	{
		List<String> failedProviderSiteList = new ArrayList<>();

		if (isValidSite(site))
		{
			for (Provider provider : site.getProviders())
			{
				if (provider.isActive())
				{
					ProviderData providerData = provider.convertToProviderData();
					boolean success = postProviderAtSite(token, providerData, site);

					if (!success)
					{
						failedProviderSiteList.add("Failed to get sso-credentials for ProviderNo: " + provider.getProviderNo() + ", SiteId: " + site.getId());
					}
				}
			}
		}
		else
		{
			failedProviderSiteList.add("Site is not valid: " + site.getName());
		}

		return failedProviderSiteList;
	}

	public String getSSOLink(HttpSession session, @Nullable Integer demographicNo, @Nullable Integer siteId) throws IntegrationException
	{
		IMDHealthCredentials bearerToken = fetchCredentials(session, null);

		String ssoLink = "";

		if (bearerToken != null)
		{
			Site site = null;
			if (siteId != null)
			{
				site = siteDao.getById(siteId);
			}

			Demographic demographic = null;
			if (demographicNo != null)
			{
				demographic = demographicDao.find(demographicNo);
			}

			SSOCredentials ssoCredentials = getSSOCredentialsForPatientSession(session, demographic, site);
			ssoLink = ssoCredentials.getImdUrl();
		}

		return ssoLink;
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
				// TODO refactor, it should only get the bearer token now.  All of the other stuff can't be cached anymore
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
	 * TODO rewrite java doc
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

	/**
	 * Upload information that the given provider exists at the juno clinic to IMD Health
	 * @param token valid bearer token
	 * @param provider provider to post
	 * @return true if post was successful
	 * @throws IMDHealthException on post error
	 */
	private boolean postProviderAtClinic(BearerToken token, ProviderData provider) throws IMDHealthException
	{
		return getSSOCredentials(token, provider, null, null) != null;
	}


	/**
	 * Post that the specified provider exists at the specified site to IMDHealth
	 * @param token Valid bearer token
	 * @param provider provider to post
	 * @param site site to post
	 * @return true if post was successful
	 * @throws IMDHealthException on post error
	 */
	private boolean postProviderAtSite(BearerToken token, ProviderData provider, Site site) throws IMDHealthException
	{
		return getSSOCredentials(token, provider, null, site) != null;
	}

	/**
	 * Generate the SSO credentials needed for a patient associated session
	 * @param session
	 * @param demographic
	 * @param site
	 * @return
	 * @throws IMDHealthException
	 */
	private SSOCredentials getSSOCredentialsForPatientSession(HttpSession session, @Nullable Demographic demographic, @Nullable Site site) throws IMDHealthException
	{
		IMDHealthCredentials token = fetchCredentials(session, null);

		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(session);
		ProviderData provider = loggedInInfo.getLoggedInProvider().convertToProviderData();

		return getSSOCredentials(token.getBearerToken(), provider, demographic, site);
	}

	/**
	 * Given a bearer token, establish an SSO session on the IMDHealth app and return it's information
	 * // TODO Java doc
	 * @param token
	 * @param provider
	 * @param demographic
	 * @param site
	 * @return
	 * @throws SSOLoginException
	 */
	private SSOCredentials getSSOCredentials(BearerToken token, ProviderData provider, @Nullable Demographic demographic, @Nullable Site site) throws SSOLoginException
	{
		try
		{
			// TODO TEST REMOVE ME
			demographic = demographicDao.find(8);


			SSOUser user = createSSOUser(provider);
			SSOOrganization organization = createSSOOrganization(site);
			SSOPatient patient = createSSOPatient(demographic);

			// Stub value.  Implementation TBD.
			Set<String> issues = new HashSet<>();

			SSORequest ssoRequest = new SSORequest(user, organization, patient, issues);

			return communicationService.SSOLogin(token, ssoRequest);
		}
		catch (HttpClientErrorException | HttpServerErrorException | IMDHealthException ex)
		{
			logger.error("Error during SSO login", ex);
			throw new SSOLoginException("IMDHealth Login error");
		}
	}

	private SSOUser createSSOUser(ProviderData provider)
	{
		// TODO preload the UUID here
		return SSOUser.fromProvider(provider);
	}

	/**
	 * Return an SSOOrganization.  If a site is given, the SSOOrganization will be derived from its attributes,
	 * otherwise the SSOOrganization will be based on the clinic.  In either case the province code will be initialized
	 * using the instance type, as both clinic and site allow raw strings for province and we must adhere to ISO-3166
	 * conventions for this field.
	 *
	 * @param site {optional} site to create the SSOOrganization from, otherwise the clinic will be used.
	 * @return
	 */
	private SSOOrganization createSSOOrganization(@Nullable Site site)
	{
		// TODO:  preload the UUID here
		String provinceCode = OscarProperties.getInstance().getInstanceTypeUpperCase();

		if (site != null)
		{
			return SSOOrganization.fromSite(site, provinceCode);
		}
		else
		{
			Clinic clinic = clinicDao.getClinic();
			return SSOOrganization.fromClinic(clinic, provinceCode);
		}

	}

	/**
	 * Attempt to create an SSO patient object given a demographic.  Returns null if the demographic is null,
	 * or if the demographic does not have a vaild email.
	 *
	 * @param demographic demographic object to create SSOPatient from
	 * @return SSOPatient if demographic is not null and has a valid email, otherwise null
	 * @throws SSOLoginException if demographic cannot be mapped due to invalid data
	 */
	private SSOPatient createSSOPatient(@Nullable Demographic demographic) throws SSOLoginException
	{
		SSOPatient patient = null;

		if (SSOPatient.canConvert(demographic))
		{
			patient = SSOPatient.fromDemographic(demographic);
		}

		return patient;
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

	// TODO Move out into SSOOrganization?
	private boolean isValidSite(Site site)
	{
		return ConversionUtils.hasContent(site.getCity()) &&
				ConversionUtils.hasContent(site.getName()) &&
				ConversionUtils.hasContent(site.getProvince());
	}

	// TODO Move out into SSOOrganization?
	private boolean isValidClinic(Clinic clinic)
	{
		return ConversionUtils.hasContent(clinic.getClinicCity()) &&
				ConversionUtils.hasContent(clinic.getClinicName()) &&
				ConversionUtils.hasContent(clinic.getClinicProvince());
	}
}
