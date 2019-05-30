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

package oscar.telehealth.actions;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionRedirect;
import org.oscarehr.demographic.dao.DemographicDao;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.integration.myhealthaccess.dto.ClinicUserAccessTokenTo1;
import org.oscarehr.integration.myhealthaccess.dto.ClinicUserTo1;
import org.oscarehr.integration.myhealthaccess.exception.BaseException;
import org.oscarehr.integration.myhealthaccess.service.ClinicService;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import oscar.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;

public class MyHealthAccess extends Action
{
	public ActionForward execute(ActionMapping mapping,
								 ActionForm form,
								 HttpServletRequest request,
								 HttpServletResponse response)
	{
		String clinicID = "57100c58-9d0c-425f-8b8c-f55f6818a1c0";
		String remoteUserID = "999998";
		ClinicService clinicService = new ClinicService();
		ClinicUserTo1 linkedUser;
		try
		{
			linkedUser = clinicService.getLinkedUser(clinicID, remoteUserID);
			String email = request.getParameter("email");
			String password = request.getParameter("password");
			String demographicNo = request.getParameter("demographicNo");
			ClinicUserAccessTokenTo1 accessToken;
			try
			{
				if(email == null || email.isEmpty() || password == null || password.isEmpty())
				{
					ActionRedirect loginAction = new ActionRedirect(mapping.findForward("login"));
					loginAction.addParameter("demographicNo", demographicNo);
					return loginAction;
				}
				accessToken = clinicService.getLoginToken(
						clinicID, linkedUser.getMyhealthaccesID(), email, password);
			} catch (BaseException e)
			{
				MiscUtils.getLogger().error("*******************************");
				MiscUtils.getLogger().error("EXCEPTION: " + e);
				if(e.getErrorObject().isHasGenericErrors())
				{
					MiscUtils.getLogger().error("STATUS: " +
							e.getErrorObject().getGenericErrors().get(0).getCode());
					MiscUtils.getLogger().error("Message: " +
							e.getErrorObject().getGenericErrors().get(0).getMessage());
				}
				else if(e.getErrorObject().isHasAuthError())
				{


					MiscUtils.getLogger().error("STATUS: " +
							e.getErrorObject().getAuthError().getCode());
					MiscUtils.getLogger().error("Message: " +
							e.getErrorObject().getAuthError().getMessage());
				}
				ActionRedirect loginAction = new ActionRedirect(mapping.findForward("login"));
				loginAction.addParameter("demographicNo", demographicNo);
				return loginAction;
			}

			DemographicDao demographicDao =
					(DemographicDao) SpringUtils.getBean("demographic.dao.DemographicDao");
			Demographic patient = demographicDao.find(Integer.parseInt(demographicNo));

			String redirectUrl = URLEncoder.encode("patient/remote_patient_id/" +
					demographicNo + "?" +
					"&patient_first_name=" + StringUtils.noNull(patient.getFirstName()) +
					"&patient_last_name=" + StringUtils.noNull(patient.getLastName()));

			ActionRedirect myHealthAccessRedirectAction = new ActionRedirect();
			String myHealthAccessURL = "https://conan.mhadev.ca/clinic_users/push_token?" +
					"clinic_id=" + clinicID +
					"&user_id=" + linkedUser.getMyhealthaccesID() +
					"&redirect_url=" + redirectUrl +
					"#token=" +accessToken.getToken();

			myHealthAccessRedirectAction.setPath(myHealthAccessURL);
			myHealthAccessRedirectAction.setRedirect(true);
			return myHealthAccessRedirectAction;
		} catch (Exception e)
		{
			MiscUtils.getLogger().error("Error", e);
		}

		// TODO Add failure action
		return mapping.findForward("failure");
	}
}
