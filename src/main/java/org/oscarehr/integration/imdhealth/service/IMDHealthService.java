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
import org.oscarehr.common.dao.ClinicDAO;
import org.oscarehr.common.dao.SiteDao;
import org.oscarehr.common.encryption.StringEncryptor;
import org.oscarehr.common.model.Clinic;
import org.oscarehr.common.model.Site;
import org.oscarehr.integration.dao.IntegrationDao;
import org.oscarehr.integration.imdhealth.transfer.inbound.BearerToken;
import org.oscarehr.integration.imdhealth.transfer.inbound.SSOCredentials;
import org.oscarehr.integration.imdhealth.transfer.outbound.SSOOrganization;
import org.oscarehr.integration.imdhealth.transfer.outbound.SSORequest;
import org.oscarehr.integration.imdhealth.transfer.outbound.SSOUser;
import org.oscarehr.integration.model.Integration;
import org.oscarehr.integration.service.IntegrationService;
import org.oscarehr.util.LoggedInInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Service
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
	SiteDao siteDao;

	protected static final String HOST_URL = "v5.app.imdhealth.com";      // Production: https://app.imdhealth.com
	protected static final String DEFAULT_SCHEME= "https";

	/**
	 * Persist new IMDHealth SSO credentials
	 * @param clientId
	 * @param clientSecret
	 */
	public Integration updateSSOCredentials(String clientId, String clientSecret, @Nullable Integer siteId)
	{
		Integration existingIntegration = integrationService.findIntegrationByTypeAndSite(Integration.INTEGRATION_TYPE_IMD_HEALTH, siteId);

		// We will need access to the plaintext value of the key to generate SSO login keys, so this needs to be reversible
		String encryptedSecret = StringEncryptor.encrypt(clientSecret);

		if (existingIntegration != null)
		{
			existingIntegration.setRemoteId(clientId);
			existingIntegration.setApiKey(encryptedSecret);
			integrationDao.merge(existingIntegration);

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
	 * Generate the SSO link needed to connect to iMDHealth, logging in if necessary.
	 *
	 * A login will be performed if:
	 * 1)  The user is not logged in to iMDHealth
	 * 2)  The SSO credentials have expired
	 *
	 * @param request HTTPServletRequest
	 * @param siteId {Optional} siteId to use to login.  If not needed, use null
	 *
	 * @return URL formatted link to iMDHealth with SSO login credentials applied
	 */
	public String getSSOLink(HttpServletRequest request, @Nullable Integer siteId)
	{
		HttpSession session = request.getSession();
		IMDHealthCredentials credentials = IMDHealthCredentials.getFromSession(session);

		if (credentials == null) // TODO: check 24 hour time limit on token, check if any part of the credentials are null, empty, etc
		{
			Integration imdIntegration = integrationDao.findByIntegrationTypeAndSiteId(Integration.INTEGRATION_TYPE_IMD_HEALTH, siteId);
			credentials = login(imdIntegration, session, siteId);
		}

		URIBuilder builder = new URIBuilder()
				.setScheme(DEFAULT_SCHEME)
				.setHost(HOST_URL)
				.setParameter("access_token", credentials.getAccessToken())
				.setParameter("membership_id", StringUtils.trimToEmpty(credentials.getMembershipId()))
				.setParameter("organization_id", StringUtils.trimToEmpty(credentials.getOrganizationId()));

		return builder.toString();
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
	 *
	 * @return iMDHealth SSO credentials
	 */
	private IMDHealthCredentials login(Integration imdHealthIntegration, HttpSession session, Integer siteId)
	{
		IMDHealthCredentials credentials = new IMDHealthCredentials();

		BearerToken token = getBearerToken(imdHealthIntegration);
		credentials.setBearerToken(token);

		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(session);
		String junoPracticeId = session.getServletContext().getContextPath().replaceAll("^/", "");
		SSOCredentials ssoInfo = getSSOCredentials(token, loggedInInfo, junoPracticeId, siteId);
		credentials.loadSSOCredentials(ssoInfo);

		credentials.saveToSession(session);
		return credentials;
	}

	private BearerToken getBearerToken(Integration imdHealthIntegration)
	{
		String decryptedSecret = StringEncryptor.decrypt(imdHealthIntegration.getApiKey());
		String clientId = imdHealthIntegration.getRemoteId();

		return communicationService.getBearerToken(clientId, decryptedSecret);
	}

	private SSOCredentials getSSOCredentials(BearerToken token,
	                                         LoggedInInfo loggedInInfo,
	                                         String practiceId,
	                                         @Nullable Integer siteId)
	{
		SSOUser user = SSOUser.fromLoggedInInfo(loggedInInfo);

		SSOOrganization organization;
		if (siteId == null)
		{
			Clinic clinic = clinicDao.getClinic();
			organization = SSOOrganization.fromClinic(clinic, practiceId);
		}
		else
		{
			// TODO: Not yet implemented
			Site site = siteDao.find(siteId);
			organization = SSOOrganization.fromSite(site, practiceId);
		}

		SSORequest ssoRequest = new SSORequest(user, organization);

		return communicationService.SSOLogin(token, ssoRequest);
	}
}
