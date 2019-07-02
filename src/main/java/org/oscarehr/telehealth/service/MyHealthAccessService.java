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

package org.oscarehr.telehealth.service;

import org.oscarehr.common.dao.SecurityDao;
import org.oscarehr.common.model.Security;
import org.oscarehr.common.model.Site;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.integration.myhealthaccess.dto.ClinicUserAccessTokenTo1;
import org.oscarehr.integration.myhealthaccess.dto.ClinicUserTo1;
import org.oscarehr.integration.myhealthaccess.service.ClinicService;
import org.oscarehr.provider.dao.ProviderDataDao;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import oscar.OscarProperties;
import oscar.util.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

@Service("telehealth.service.MyHealthAccessService")
@Transactional
public class MyHealthAccessService
{
	@Autowired
	ProviderDataDao providerDataDao;

	@Autowired
	ClinicService clinicService;

	@Autowired
	SecurityDao securityDao;

	protected static OscarProperties oscarProps = OscarProperties.getInstance();
	protected final String MYHEALTHACCESS_PROTOCOL = oscarProps.getProperty("myhealthaccess_protocol");
	protected final String MYHEALTHACCESS_DOMAIN = oscarProps.getProperty("myhealthaccess_domain");
	protected final String CLINIC_ID = oscarProps.getProperty("myhealthaccess_clinic_id");

	public String buildTeleHealthRedirectURL(
			ClinicUserAccessTokenTo1 accessToken,
			ClinicUserTo1 linkedUser,
			Demographic patient,
			Site site) throws UnsupportedEncodingException
	{
		String redirectUrl;
		if (patient == null)
		{
			redirectUrl = "home";
		} else
		{
			redirectUrl = URLEncoder.encode("patient/remote_patient_id/" +
					patient.getDemographicId() + "?" +
					"&patient_first_name=" + StringUtils.noNull(patient.getFirstName()) +
					"&patient_last_name=" + StringUtils.noNull(patient.getLastName()), "UTF-8");
		}

		String endPointPath = "clinic_users/push_token?";
		String endPoint = ClinicService.concatEndpointStrings(MYHEALTHACCESS_DOMAIN, endPointPath);
		return clinicService.buildUrl(endPoint) +
				"clinic_id=" + getClinicID(site) +
				"&user_id=" + linkedUser.getMyhealthaccesID() +
				"&redirect_url=" + redirectUrl +
				"#token=" + accessToken.getToken();
	}

	public ClinicUserTo1 getLinkedUser(Security loggedInUser, Site site)
	{
		return clinicService.getLinkedUser(getClinicID(site), Integer.toString(loggedInUser.getId()));
	}

	public ClinicUserTo1 getUserByEmail(String email, Site site)
	{
		MiscUtils.getLogger().error("email1: " + email);
		return clinicService.getUserByEmail(getClinicID(site), email);
	}

	public ClinicUserTo1 createUser(
			Security loggedInUser, ProviderData loggedInProvider, String email, Site site)
	{
		MiscUtils.getLogger().error("email1: " + email);
		return clinicService.createUser(
				getClinicID(site),
				Integer.toString(loggedInUser.getId()),
				email,
				loggedInProvider.getFirstName(),
				loggedInProvider.getLastName()
		);
	}

	public ClinicUserAccessTokenTo1 getLoginToken(
			Security loggedInUser,
			Site site,
			String myHealthAccessUserID,
			String email,
			String password) throws NoSuchAlgorithmException, IOException, KeyManagementException
	{
		ClinicUserAccessTokenTo1 myHealthAccessAuthToken = clinicService.getLoginToken(
				getClinicID(site),
				myHealthAccessUserID,
				email,
				password,
				Integer.toString(loggedInUser.getId())
		);
		MiscUtils.getLogger().info("Saving Token: " + myHealthAccessAuthToken.getToken());
		Security securityRecord = securityDao.find(loggedInUser.getId());
		loggedInUser.setMyHealthAccessAuthToken(myHealthAccessAuthToken.getToken());
		securityRecord.setMyHealthAccessAuthToken(myHealthAccessAuthToken.getToken());
		securityDao.persist(securityRecord);
		MiscUtils.getLogger().info("SAVED!");
		return myHealthAccessAuthToken;
	}

	public String getClinicID(Site site)
	{
		String clinic_id;
		if (site == null)
		{
			clinic_id = CLINIC_ID;
		} else
		{
			// TODO GET BY SITE
			clinic_id = CLINIC_ID;
		}

		if (clinic_id.isEmpty())
		{
			throw new IllegalArgumentException("Missing required MyHealthAccess Clinic ID");
		}

		return clinic_id;
	}
}
