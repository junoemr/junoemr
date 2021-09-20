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

package oscar.myhealthaccess;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionRedirect;
import org.apache.struts.actions.DispatchAction;
import org.oscarehr.common.dao.OscarAppointmentDao;
import org.oscarehr.common.model.Appointment;
import org.oscarehr.common.model.Security;
import org.oscarehr.integration.model.Integration;
import org.oscarehr.integration.model.IntegrationData;
import org.oscarehr.integration.model.IntegrationStatus;
import org.oscarehr.integration.model.UserIntegrationAccess;
import org.oscarehr.integration.myhealthaccess.dto.ClinicStatusResponseTo1;
import org.oscarehr.integration.myhealthaccess.dto.ClinicUserCreateTo1;
import org.oscarehr.integration.myhealthaccess.exception.InvalidIntegrationException;
import org.oscarehr.integration.myhealthaccess.exception.RecordNotFoundException;
import org.oscarehr.integration.service.IntegrationService;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.telehealth.service.MyHealthAccessService;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.springframework.transaction.annotation.Transactional;
import oscar.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Transactional
public class IntegrationAction extends DispatchAction
{
	private static MyHealthAccessService myHealthAccessService = SpringUtils.getBean(MyHealthAccessService.class);
	private static IntegrationService integrationService = SpringUtils.getBean(IntegrationService.class);
	private static final Logger logger = MiscUtils.getLogger();

	public ActionForward connectOrList(ActionMapping mapping, ActionForm form,
									   HttpServletRequest request, HttpServletResponse response)
	{
		List<Integration> integrations = integrationService.getMyHealthAccessIntegrations();

		if (integrations.size() == 1)
		{
			Integration integration = integrations.get(0);
			String siteName = null;

			if (integration.getSite() != null)
			{
				siteName = integration.getSite().getName();
			}

			return connectToClinic(request, mapping, siteName);
		}
		else
		{
			return listIntegrations(mapping, request, integrations);
		}
	}

	public ActionForward connect(ActionMapping mapping, ActionForm form,
							     HttpServletRequest request, HttpServletResponse response)
	{
		String siteName = request.getParameter(Param.SITE_NAME);

		if (StringUtils.empty(siteName))
		{
			siteName = null;
		}

		return connectToClinic(request, mapping, siteName);
	}

	private ActionForward listIntegrations(ActionMapping mapping, HttpServletRequest request)
	{
		List<Integration> integrations = integrationService.getMyHealthAccessIntegrations();
		return listIntegrations(mapping, request, integrations);
	}

	private ActionForward listIntegrations(ActionMapping mapping, HttpServletRequest request, List<Integration> integrations)
	{
		HttpSession session = request.getSession();
		List<IntegrationStatus> integrationStatuses = new ArrayList<>();

		for (Integration integration : integrations)
		{
			ClinicStatusResponseTo1 status = myHealthAccessService.testConnection(integration);
			integrationStatuses.add(new IntegrationStatus(integration, status));
		}

		session.setAttribute("integrations", integrationStatuses);
		return new ActionRedirect(mapping.findForward(Action.LIST_INTEGRATIONS));
	}

	private ActionForward connectToClinic(HttpServletRequest request, ActionMapping mapping, String siteName)
	{
		try
		{
			IntegrationData integrationData = getIntegrationData(request, siteName);

			if (!integrationData.userIntegrationExists())
			{
				integrationData = createClinicUser(request, integrationData);
			}
			else
			{
				try
				{
					integrationData = myHealthAccessService.clinicUserLogin(integrationData);
				}
				catch (RecordNotFoundException e)
				{
					integrationData = createClinicUser(request, integrationData);
				}
			}

			return getRemoteRedirect(integrationData, request);
		}
		catch (InvalidIntegrationException e)
		{
			return new ActionRedirect(mapping.findForward(Action.LIST_INTEGRATIONS));
		}
		catch (Exception e)
		{
			MiscUtils.getLogger().error(e.toString());
			return listIntegrations(mapping, request);
		}
	}

	protected ActionForward getRemoteRedirect(IntegrationData integrationData, HttpServletRequest request)
	{
		OscarAppointmentDao oscarAppointmentDao = SpringUtils.getBean(OscarAppointmentDao.class);
		String appointmentNo = request.getParameter(Param.APPOINTMENT);

		Appointment appointment = null;
		if(appointmentNo != null)
		{
			appointment = oscarAppointmentDao.find(Integer.parseInt(appointmentNo));
		}

		// TODO: Better implementation for dynamic redirect links
		String myHealthAccessURL = "";
		if (appointment != null && appointment.getQueuedAppointmentLink() != null)
		{
			// aqs telehealth
			myHealthAccessURL = myHealthAccessService.getSSORedirectUrl(integrationData,
			                                                           String.format(MyHealthAccessService.MHA_BASE_AQS_TELEHEALTH_URL,
			                                                                         appointment.getQueuedAppointmentLink().getQueueId(),
			                                                                         appointment.getQueuedAppointmentLink().getQueuedAppointmentId()));
		}
		else if (appointmentNo != null)
		{
			// regular telehealth
			myHealthAccessURL = myHealthAccessService.getTelehealthUrlForAppointment(integrationData, appointmentNo);
		}
		else
		{
			// clinic admin home page.
			myHealthAccessURL = myHealthAccessService.getSSORedirectUrl(integrationData, MyHealthAccessService.MHA_HOME_URL);
		}

		ActionRedirect myHealthAccessRedirectAction = new ActionRedirect();
		myHealthAccessRedirectAction.setPath(myHealthAccessURL);
		myHealthAccessRedirectAction.setRedirect(true);
		return myHealthAccessRedirectAction;
	}

	private IntegrationData createClinicUser(HttpServletRequest request, IntegrationData integrationData)
	{
		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
		ProviderData loggedInProvider = loggedInInfo.getLoggedInProvider().convertToProviderData();
		Security loggedInUser = loggedInInfo.getLoggedInSecurity();

		ClinicUserCreateTo1 clinicUser = new ClinicUserCreateTo1(
				Integer.toString(loggedInUser.getSecurityNo()),
				loggedInProvider.getFirstName(),
				loggedInProvider.getLastName()
		);

		return myHealthAccessService.createClinicUser(integrationData, loggedInUser, clinicUser);
	}

	private IntegrationData getIntegrationData(HttpServletRequest request, String siteName) throws InvalidIntegrationException
	{
		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
		Security security = loggedInInfo.getLoggedInSecurity();

		Integration integration = integrationService.findMhaIntegration(siteName);

		if (integration == null)
		{
			String noIntegrationError = InvalidIntegrationException.NO_INTEGRATION_MHA;

			if (!StringUtils.isNullOrEmpty(siteName))
			{
				noIntegrationError = String.format("%s for %s", noIntegrationError, siteName);
			}

			throw new InvalidIntegrationException(noIntegrationError);
		}

		IntegrationData integrationData = new IntegrationData(integration);

		UserIntegrationAccess userIntegrationAccess = integrationService.findMhaUserAccessBySecurityAndSiteName(security, siteName);
		integrationData.setUserIntegrationAccess(userIntegrationAccess);

		return integrationData;
	}

	private final static class Param
	{
		public static final String EMAIL = "email";
		public static final String PASSWORD = "password";
		public static final String SITE_NAME = "siteName";
		public static final String REMOTE_USER = "remoteUser";
		public static final String APPOINTMENT = "appt";
		public static final String ERROR = "errorMessage";
	}

	private final static class Action
	{
		public static final String LIST_INTEGRATIONS = "listIntegrations";
		public static final String ERROR = "error";
	}
}
