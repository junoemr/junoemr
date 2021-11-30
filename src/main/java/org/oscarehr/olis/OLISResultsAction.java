/**
 * Copyright (c) 2008-2012 Indivica Inc.
 *
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */
package org.oscarehr.olis;

import com.indivica.olis.Driver;
import com.indivica.olis.DriverResponse;
import org.apache.commons.io.FileUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.security.model.Permission;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import oscar.oscarLab.ca.all.parsers.Factory;
import oscar.oscarLab.ca.all.parsers.OLIS.OLISError;
import oscar.oscarLab.ca.all.parsers.OLIS.OLISHL7Handler;
import oscar.oscarLab.ca.all.util.Utilities;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class OLISResultsAction extends DispatchAction
{
	private static final SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);
	private static final Map<String, OLISHL7Handler> searchResultsMap = new HashMap<>();

	@Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	{
		String loggedInProviderNo = LoggedInInfo.getLoggedInInfoFromSession(request).getLoggedInProviderNo();
		securityInfoManager.requireAllPrivilege(loggedInProviderNo, Permission.LAB_READ);

		try
		{
			String olisResultString = (String) request.getAttribute("olisResponseContent");
			if(olisResultString == null)
			{
				olisResultString = oscar.Misc.getStr(request.getParameter("olisResponseContent"), "");
				request.setAttribute("olisResponseContent", olisResultString);

				String olisXmlResponse = oscar.Misc.getStr(request.getParameter("olisXmlResponse"), "");
				if(olisResultString.trim().equalsIgnoreCase(""))
				{
					if(!olisXmlResponse.trim().equalsIgnoreCase(""))
					{
						DriverResponse driverResponse = Driver.readResponseFromXML(loggedInProviderNo, olisXmlResponse);
						request.setAttribute("msgInXML", driverResponse.getUnsignedRequest());
						request.setAttribute("signedRequest", driverResponse.getSignedRequest());
						request.setAttribute("signedData", driverResponse.getSignedResponse());
						request.setAttribute("unsignedResponse", driverResponse.getUnsignedResponse());
						request.setAttribute("olisResponseContent", driverResponse.getHl7Response());
						request.setAttribute("errors", driverResponse.getErrors());
						request.setAttribute("searchException", driverResponse.getSearchException());
					}

					List<String> resultList = new LinkedList<>();
					request.setAttribute("resultList", resultList);
					return mapping.findForward("results");
				}
			}

			UUID uuid = UUID.randomUUID();

			File tempFile = new File(System.getProperty("java.io.tmpdir") + "/olis_" + uuid + ".response");
			FileUtils.writeStringToFile(tempFile, olisResultString);

			List<String> messages = Utilities.separateMessages(System.getProperty("java.io.tmpdir") + "/olis_" + uuid + ".response");
			List<String> resultList = new LinkedList<>();
			List<String> errors = new LinkedList<>();

			for(String message : messages)
			{
				String resultUuid = UUID.randomUUID().toString();

				tempFile = new File(System.getProperty("java.io.tmpdir") + "/olis_" + resultUuid + ".response");
				FileUtils.writeStringToFile(tempFile, message);

				// Parse the HL7 string...
				OLISHL7Handler handler = (OLISHL7Handler) Factory.getHandler(OLISHL7Handler.OLIS_MESSAGE_TYPE, message);

				// collect hl7 response errors
				errors.addAll(handler.getReportErrors().stream().map(OLISError::userFriendlyToString).collect(Collectors.toList()));
				if(handler.resultStatusNotFound())
				{
					errors.add("Request did not find and data");
					continue;
				}

				// skip responses with no test data
				if(handler.getOBRCount() == 0)
				{
					continue;
				}

				searchResultsMap.put(resultUuid, handler);
				resultList.add(resultUuid);
			}

			request.setAttribute("errors", errors);
			request.setAttribute("resultList", resultList);
		}
		catch(Exception e)
		{
			MiscUtils.getLogger().error("Can't pull out messages from OLIS response.", e);
		}
		return mapping.findForward("results");
	}

	public static OLISHL7Handler getHandlerByUUID(String uuid)
	{
		return searchResultsMap.get(uuid);
	}
}
