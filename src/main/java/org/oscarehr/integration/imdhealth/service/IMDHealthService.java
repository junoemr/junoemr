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
import org.oscarehr.common.model.Site;
import org.oscarehr.demographic.dao.DemographicDao;
import org.oscarehr.demographic.entity.Demographic;
import org.oscarehr.integration.dao.IntegrationDao;
import org.oscarehr.integration.exception.IntegrationException;
import org.oscarehr.integration.imdhealth.exception.IMDHealthException;
import org.oscarehr.integration.imdhealth.exception.SSOBearerException;
import org.oscarehr.integration.imdhealth.exception.SSOLoginException;
import org.oscarehr.integration.imdhealth.transfer.inbound.BearerToken;
import org.oscarehr.integration.imdhealth.transfer.inbound.SSOSessionCredentials;
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

import javax.annotation.Nullable;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

		List<String> clinicErrors = syncClinic(token);

		if (oscarProperties.isMultisiteEnabled())
		{
			List<String> siteErrors = syncSites(token);
			clinicErrors.addAll(siteErrors);
		}

		return clinicErrors;
	}

	public String getSSOLink(HttpSession session, @Nullable Integer demographicNo, @Nullable Integer siteId) throws IntegrationException
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

		SSOSessionCredentials ssoCredentials = createSSOSessionForLoggedInProvider(session, demographic, site);
		return ssoCredentials.getImdUrl();
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
	 * Register all sites, and their active providers with the iMDHealth app.
	 *
	 * @param token Valid bearer token
	 * @return List of errors either with the site or with active providers registered to that site
	 * @throws IMDHealthException on communication error
	 */
	private List<String> syncSites(BearerToken token) throws IMDHealthException
	{
		List<String> siteErrors = new ArrayList<>();
		List<Site> sites = siteDao.getAllActiveSites();

		for (Site activeSite: sites)
		{
			Set<ProviderData> siteProviders = activeSite.getProviders()
					.stream()
					.filter(provider -> provider.isActive())
					.map(provider -> provider.convertToProviderData())
					.collect(Collectors.toSet());

			if ((!SSOOrganization.canMapSite(activeSite)))
			{
				siteErrors.add(activeSite.getName() + ": is missing required information.");
			}
			else if (siteProviders.isEmpty())
			{
				siteErrors.add(activeSite.getName() + ": has no active providers.");
			}
			else
			{
				List<String> failedAtSite = loginProviderSite(token, activeSite, siteProviders);
				siteErrors.addAll(failedAtSite);
			}
		}

		return siteErrors;
	}

	/**
	 * Register all active providers on the instance with the base clinic on the iMDHealth app.
	 *
	 * @param token Valid bearer token
	 * @return List of errors, either associated with the clinic or active providers registered to that clinic
	 * @throws IMDHealthException
	 */
	private List<String> syncClinic(BearerToken token) throws IMDHealthException
	{
		List<String> errors = new ArrayList<>();
		Clinic clinic = clinicDao.getClinic();
		List<ProviderData> activeProviders = providerDataDao.findByActiveStatus(true);

		if (activeProviders.isEmpty() || !SSOOrganization.canMapClinic(clinic))
		{
			errors.add(clinic.getClinicName() + ": is missing required information or has no providers.");
		}
		else
		{
			errors.addAll(loginProviderClinic(token, activeProviders));
		}

		return errors;
	}
	
	/**
	 * Generate an SSO session for each of the given providers, using the clinic as the organization.  This generates
	 * a linkage between the organization (clinic) and user (provider) on the iMDHealth app.
	 *
	 * If no providers are provided to this method, and the clinic is not previously known to iMDHealth, then
	 * then the clinic will not register as iMDHealth requires at least one user to be associated with an organization.
	 *
	 * When a better implementation is found, this should be revisited.
	 *
	 * @param token Valid bearer token
	 * @param providers list of providers to register
	 * @return List of providers which could not be registered
	 *
	 * @throws IMDHealthException on communition error
	 */
	private List<String> loginProviderClinic(BearerToken token, List<ProviderData> providers) throws IMDHealthException
	{
		List<String> failedProviderClinicList = new ArrayList<>();
		
		for (ProviderData providerData : providers)
		{
			if (SSOUser.canConvertProvider(providerData))
			{
				if (!registerProviderAtClinic(token, providerData))
				{
					failedProviderClinicList.add(providerData.getDisplayName() + ": was unable to be registered with the clinic");
				}
			}
			else
			{
				failedProviderClinicList.add(providerData.getDisplayName() + ": is missing required information.");
			}
		}
		
		return failedProviderClinicList;
	}
	
	/**
	 * Generate an SSO session for each of the given providers, using the site as the organization.  This generates
	 * a linkage between the organization (site) and user (provider) on the iMDHealth app.
	 *
	 * If no providers are provided to this method, and the site is not previously known to iMDHealth, then
	 * then the site will not register as iMDHealth requires at least one user to be associated with an organization.
	 *
	 * When a better implementation is found, this should be revisited.
	 *
	 * @param token Valid bearer token
	 * @param site site to load
	 * @param providers providers associated with the site
	 * @return List of providers or sites which could not be loaded
	 *
	 * @throws IMDHealthException on communition error
	 */
	private List<String> loginProviderSite(BearerToken token, Site site, Set<ProviderData> providers) throws IMDHealthException
	{
		List<String> failedProviderSiteList = new ArrayList<>();
		
		for (ProviderData provider : providers)
		{
			if (SSOUser.canConvertProvider(provider))
			{
				boolean success = registerProviderAtSite(token, provider, site);
				
				if (!success)
				{
					failedProviderSiteList.add(provider.getDisplayName() + ": was unable to be registered with " + site.getName());
				}
			}
			// We already know if the provider can't be converted to an SSOUser when we previously tried to register them with the clinic
			// so ignore the error the second time around.
		}
		
		return failedProviderSiteList;
	}

	/**
	 * Get a set of IMD Health credentials to use when establishing an SSO session.
	 *
	 * Looks in the session first, and requests a new set if the credentials on the session are null or expired.
	 *
	 * @param session user session
	 * @return IMDHealth credentials if able, null otherwise
	 *
	 * @throws IMDHealthException on communication or database error
	 */
	private IMDHealthCredentials getCredentials(HttpSession session) throws IMDHealthException
	{
		IMDHealthCredentials credentials = IMDHealthCredentials.getFromSession(session);

		if (credentials == null || credentials.getBearerToken().isExpired())
		{
			Integration imdIntegration = integrationDao.findByIntegrationTypeAndSiteId(Integration.INTEGRATION_TYPE_IMD_HEALTH, null);

			if (imdIntegration != null)
			{
				credentials = regenerateCredentials(imdIntegration, session);
			}
		}

		return credentials;
	}

	/**
	 * Fetch a new bearer token from the iMD SSO API and store it on the session.
	 *
	 * @param imdHealthIntegration iMDHealth integration to use to login
	 * @param session User session
	 * @return iMDHealth credentials fetched.
	 * @throws SSOBearerException on communication error
	 */
	private IMDHealthCredentials regenerateCredentials(Integration imdHealthIntegration, HttpSession session) throws SSOBearerException
	{
		IMDHealthCredentials credentials = new IMDHealthCredentials();

		BearerToken token = getBearerToken(imdHealthIntegration);
		credentials.setBearerToken(token);

		credentials.saveToSession(session);
		return credentials;
	}

	/**
	 * Connect with the iMDHealth SSO API and retrieve a bearer token
	 * @param imdHealthIntegration iMDHealth integration to use
	 * @return the bearer token fetched
	 * @throws SSOBearerException on communication error
	 */
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
	 * Register the given provider with this instance's clinic to iMD Health
	 *
	 * @param token valid bearer token
	 * @param provider provider to post
	 * @return true if post returned a response
	 * @throws IMDHealthException on post error
	 */
	public boolean registerProviderAtClinic(BearerToken token, ProviderData provider) throws IMDHealthException
	{
		return createSSOSession(token, provider, null, null) != null;
	}

	/**
	 * Register the given provider with the given site to iMD Health
	 * @param provider provider to post
	 * @param site site to post
	 * @return true if post returned a response
	 * @throws IMDHealthException on post error
	 */
	public boolean registerProviderAtSite(BearerToken token, ProviderData provider, Site site) throws IMDHealthException
	{
		return createSSOSession(token, provider, null, site) != null;
	}

	/**
	 * Create an iMDHealth SSO session for the currently logged in provider, with optional patient and site contexts.
	 *
	 * @param session HttpSession
	 * @param demographic {optional} demographic to associate with the patient context of the session
	 * @param site {optional} site to associate with the organization context of the session.
	 *             If not provided will use the clinic
	 * @return credentials containing an SSO link to login to the iMDHealth webapp
	 * @throws IMDHealthException
	 */
	public SSOSessionCredentials createSSOSessionForLoggedInProvider(HttpSession session, @Nullable Demographic demographic, @Nullable Site site) throws IMDHealthException
	{
		IMDHealthCredentials token = getCredentials(session);

		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(session);
		ProviderData provider = loggedInInfo.getLoggedInProvider().convertToProviderData();

		return createSSOSession(token.getBearerToken(), provider, demographic, site);
	}

	/**
	 * Create an iMDHealth SSO session for the given provider, with optional patient and site contexts.
	 *
	 * @param token valid bearer token to establish the session
	 * @param provider provider to register as the session's user
	 * @param demographic {optional} demographic to associate with the patient context of the session
	 * @param site {optional} site to associate with the organization context of the session.
	 *             If not provided, will use the clinic
	 * @return credentials containing an SSO link to login to the iMDHealth webapp
	 * @throws SSOLoginException on communication or data failure
	 */
	private SSOSessionCredentials createSSOSession(BearerToken token, ProviderData provider, @Nullable Demographic demographic, @Nullable Site site) throws SSOLoginException
	{
		try
		{
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

	/**
	 * Return an SSOOrganization.  If a site is given, the SSOOrganization will be derived from its attributes,
	 * otherwise the SSOOrganization will be based on the clinic.  In either case the province code will be initialized
	 * using the instance type, as both clinic and site allow raw strings for province and we must adhere to ISO-3166
	 * conventions for this field.
	 *
	 * If either the site or the clinic does not have UUID associated with itself, one will be created and persisted.
	 * In contrast to the Providers iMDHealth UUID, the UUID associated with a site or a clinic can be considered to be
	 * globally unique in the Juno dataset.
	 *
	 * @param site {optional} site to create the SSOOrganization from, otherwise the clinic will be used.
	 * @return SSOOrganization
	 */
	private SSOOrganization createSSOOrganization(@Nullable Site site)
	{
		String provinceCode = OscarProperties.getInstance().getInstanceTypeUpperCase();

		if (site != null)
		{
			if (site.getUuid() == null)
			{
				siteService.createAndSaveSiteUuid(site);
			}

			return SSOOrganization.fromSite(site, provinceCode);
		}
		else
		{
			Clinic clinic = clinicDao.getClinic();
			if (clinic.getUuid() == null)
			{
				clinicService.createAndSaveClinicUuid(clinic);
			}

			return SSOOrganization.fromClinic(clinic, provinceCode);
		}

	}

	/**
	 * Create an SSO user given a provider.
	 *
	 * If the provider does not have an iMDHealth UUID associated with it,
	 * one will be created and persisted. The iMDHealth UUID is not a global UUID with respect to the Juno Dataset, as
	 * it is permissible for this UUID to exist on multiple instances if a provider works at two separate clinics.
	 *
	 * @param provider Provider to create SSOUSer from
	 * @return SSOUser
	 */
	private SSOUser createSSOUser(ProviderData provider)
	{
		if (provider.getImdHealthUuid() == null)
		{
			providerService.createAndSaveProviderImdHealthUuid(provider);
		}

		return SSOUser.fromProvider(provider);
	}

	/**
	 * Attempt to create an SSO patient object given a demographic.  Returns null if the demographic is null,
	 * or if the demographic does not have a valid email.
	 *
	 * @param demographic demographic object to create SSOPatient from
	 * @return SSOPatient if demographic is not null and has a valid email, otherwise null
	 * @throws SSOLoginException if demographic cannot be mapped due to invalid data
	 */
	private SSOPatient createSSOPatient(@Nullable Demographic demographic) throws SSOLoginException
	{
		SSOPatient patient = null;

		if (SSOPatient.canMapDemographic(demographic))
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
}
