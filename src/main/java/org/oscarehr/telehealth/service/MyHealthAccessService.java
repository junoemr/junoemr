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

import org.oscarehr.common.model.Security;
import org.oscarehr.common.model.Site;
import org.oscarehr.integration.myhealthaccess.dto.ClinicUserAccessTokenTo1;
import org.oscarehr.integration.myhealthaccess.dto.ClinicUserCreateTo1;
import org.oscarehr.integration.myhealthaccess.dto.ClinicUserTo1;
import org.oscarehr.integration.myhealthaccess.model.MHAUserToken;
import org.oscarehr.integration.myhealthaccess.service.ClinicService;
import org.oscarehr.provider.dao.ProviderDataDao;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import oscar.OscarProperties;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


@Service("telehealth.service.MyHealthAccessService")
@Transactional
public class MyHealthAccessService
{
	@Autowired
	ProviderDataDao providerDataDao;

	@Autowired
	ClinicService clinicService;

	protected static OscarProperties oscarProps = OscarProperties.getInstance();
	protected static final String MYHEALTHACCESS_PROTOCOL = oscarProps.getProperty("myhealthaccess_protocol");
	protected static final String MYHEALTHACCESS_DOMAIN = oscarProps.getProperty("myhealthaccess_domain");
	protected static final String CLINIC_ID = oscarProps.getProperty("myhealthaccess_clinic_id");

	public String buildTeleHealthRedirectURL(String remotedId, Site site, String appointmentNo)
	{
		try
		{
			String redirectUrl;

			if (appointmentNo == null)
			{
				redirectUrl = "home";
			}
			else
			{
				final String baseTelehealthURL = "telehealth/clinic/%s/appointment/%s/provider";
				redirectUrl = URLEncoder.encode(String.format(baseTelehealthURL, getClinicID(site), appointmentNo), "UTF-8");
			}

			String endPointPath = "clinic_users/push_token?";
			String endPoint = ClinicService.concatEndpointStrings(MYHEALTHACCESS_DOMAIN, endPointPath);

			return clinicService.buildUrl(endPoint) +
					"clinic_id=" + getClinicID(site) +
					"&user_id=" + remotedId +
					"&redirect_url=" + redirectUrl;

		}
		catch (UnsupportedEncodingException e)
		{
			MiscUtils.getLogger().error("Error encoding MyHealthAccess redirect URL " + e.getMessage());
			return "home";
		}
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

	public ClinicUserCreateTo1 createUser(Security loggedInUser, ProviderData loggedInProvider,
	                                String email, Site site)
	{
		ClinicUserCreateTo1 newUser = clinicService.createUser(
				getClinicID(site),
				Integer.toString(loggedInUser.getId()),
				email,
				loggedInProvider.getFirstName(),
				loggedInProvider.getLastName()
		);

		return newUser;
	}

	public MHAUserToken getShortToken(Site site, String remoteUserID, Security loggedInUser)
	{
		ClinicUserAccessTokenTo1 longToken = loggedInUser.getMyHealthAccessLongToken();
		ClinicUserAccessTokenTo1 shortToken = clinicService.getLoginToken(getClinicID(site), remoteUserID, longToken);

		return MHAUserToken.decodeToken(shortToken);
	}

	public MHAUserToken getLongToken(Site site, String remoteUserID,
	                                             Security loggedInUser, String email, String password)
	{
		ClinicUserAccessTokenTo1 longToken = clinicService.getAuthToken(
				getClinicID(site),
				remoteUserID,
				Integer.toString(loggedInUser.getId()),
				email,
				password);

		return MHAUserToken.decodeToken(longToken);
	}

	public MHAUserToken renewLongToken(Site site, String remoteUserID, Security loggedInUser)
	{
		ClinicUserAccessTokenTo1 longToken = loggedInUser.getMyHealthAccessLongToken();
		ClinicUserAccessTokenTo1 renewedToken = clinicService.renewAuthToken(getClinicID(site), remoteUserID, longToken);

		return MHAUserToken.decodeToken(renewedToken);
	}

	public static String getClinicID(Site site)
	{
		String clinic_id;
		if (site == null)
		{
			clinic_id = CLINIC_ID;
		}
		else
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
