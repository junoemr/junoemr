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


package oscar.oauth;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.cloudresourcemanager.CloudResourceManager;
import com.google.api.services.cloudresourcemanager.model.Binding;
import com.google.api.services.cloudresourcemanager.model.GetIamPolicyRequest;
import com.google.api.services.cloudresourcemanager.model.Policy;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.oscarehr.PMmodule.dao.ProviderDao;
import org.oscarehr.security.dao.SecUserRoleDao;
import org.oscarehr.security.model.SecUserRole;
import org.oscarehr.common.dao.SecurityDao;
import org.oscarehr.common.model.Provider;
import org.oscarehr.common.model.Security;
import org.oscarehr.login.dto.LoginForwardURL;
import org.oscarehr.login.service.LoginService;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import oscar.OscarProperties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public final class OAuthLoginAction extends DispatchAction
{
	private static final Logger logger = MiscUtils.getLogger();
	protected static final OscarProperties props = oscar.OscarProperties.getInstance();

	private LoginService loginService = SpringUtils.getBean(LoginService.class);
	private ProviderDao providerDao = SpringUtils.getBean(ProviderDao.class);
	private SecurityDao securityDao = SpringUtils.getBean(SecurityDao.class);
	private SecUserRoleDao secUserRoleDao = SpringUtils.getBean(SecUserRoleDao.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	{
		// Check that Google OAuth has been set in the properties file
		String googleClientID = props.getGoogleClientID();
		if (googleClientID == null || googleClientID.isEmpty())
		{
			logger.info("Google OAuth is not enabled");
			return mapping.findForward("failure");
		}

		// Check that a token as been passed in
		HashMap<String, String[]> parameters = new HashMap(request.getParameterMap());
		String idTokenString = parameters.containsKey("idtoken") ? parameters.get("idtoken")[0] : "";
		if (idTokenString == "")
		{
			logger.info("ID Token is required");
			return mapping.findForward("failure");
		}

		try
		{
			GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
					.setAudience(Collections.singletonList(googleClientID))
					.build();

			GoogleIdToken idToken = verifier.verify(idTokenString);
			if (idToken != null)
			{
				Payload payload = idToken.getPayload();
				String email = payload.getEmail();
				logger.info("Successfully logged in user by Google OAuth: " + email);

				if (isProjectMember(email))
				{
					String userName = "oscar_host";
					Security security = securityDao.findByUserName(userName);
					Provider provider = providerDao.getProvider(security.getProviderNo());
					List<SecUserRole> roles = secUserRoleDao.getUserRoles(security.getProviderNo());
					String rolename = null;
					for (SecUserRole role : roles)
					{
						if (rolename == null)
						{
							rolename = role.getRoleName();
						}
						else
						{
							rolename += "," + role.getRoleName();
						}
					}

					LoginForwardURL loginForwardURL = loginService.loginSuccess(mapping,
							request,
							userName,
							security.getProviderNo(),
							(String) payload.get("given_name"),
							(String) payload.get("family_name"),
							provider.getProviderType(),
							rolename,
							payload.getExpirationTimeSeconds().toString(),
							"",
							"",
							"",
							false);
					return mapping.findForward(loginForwardURL.getUrl());
				}
				else
				{
					logger.warn("Google OAuth account not in Google project Juno");
					return mapping.findForward("failure");
				}
			}
			else
			{
				logger.warn("Google OAuth could not be verified");
				return mapping.findForward("failure");
			}
		}
		catch (IllegalArgumentException e)
		{
			logger.error("Error, invalid token", e);
			return mapping.findForward("failure");
		}
		catch (Exception e)
		{
			logger.error("Error", e);
			return mapping.findForward("failure");
		}
	}

	private Boolean isProjectMember(String email) throws IOException, GeneralSecurityException
	{
		GetIamPolicyRequest requestBody = new GetIamPolicyRequest();

		CloudResourceManager cloudResourceManagerService = createCloudResourceManagerService();
		CloudResourceManager.Projects.GetIamPolicy googleRequest =
				cloudResourceManagerService.projects().getIamPolicy(props.getGoogleResource(), requestBody);

		Policy googleResponse = googleRequest.execute();
		List<Binding> bindings = googleResponse.getBindings();

		Boolean isProjectMember = false;
		for (Binding binding : bindings)
		{
			isProjectMember = binding.getMembers().stream().anyMatch(member -> member.equals("user:" + email));
			if (isProjectMember)
			{
				return isProjectMember;
			}
		}
		return isProjectMember;
	}

	private static CloudResourceManager createCloudResourceManagerService()
			throws IOException, GeneralSecurityException
	{
		HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

		GoogleCredential credential = GoogleCredential.getApplicationDefault();
		if (credential.createScopedRequired())
		{
			credential = credential.createScoped(Arrays.asList("https://www.googleapis.com/auth/cloud-platform"));
		}

		return new CloudResourceManager.Builder(httpTransport, jsonFactory, credential)
				.setApplicationName("Google-Juno/0.1")
				.build();
	}
}
