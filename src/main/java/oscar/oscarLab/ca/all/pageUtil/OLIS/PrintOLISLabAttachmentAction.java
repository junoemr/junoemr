/**
 * Copyright (c) 2008-2012 Indivica Inc.
 *
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */

/**
 * PrintLabsAction.java
 *
 * Created on November 27, 2007, 9:42 AM
 * Author: Adam Balanga
 */

package oscar.oscarLab.ca.all.pageUtil.OLIS;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.olis.OLISResultsAction;
import org.oscarehr.security.model.Permission;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.SpringUtils;
import oscar.oscarLab.ca.all.Hl7textResultsData;
import oscar.oscarLab.ca.all.pageUtil.PrintLabsAction;
import oscar.oscarLab.ca.all.parsers.Factory;
import oscar.oscarLab.ca.all.parsers.OLIS.OLISHL7Handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author wrighd
 */
public class PrintOLISLabAttachmentAction extends Action
{
	private static final Logger logger = Logger.getLogger(PrintLabsAction.class);
	private static final SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);

	/**
	 * Creates a new instance of PrintLabsAction
	 */
	public PrintOLISLabAttachmentAction()
	{
	}

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	{
		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
		securityInfoManager.requireAllPrivilege(loggedInInfo.getLoggedInProviderNo(), Permission.LAB_READ);

		try
		{
			String segmentID = request.getParameter("segmentID");
			String resultUuid = request.getParameter("uuid");


			int obr = Integer.parseInt(request.getParameter("obr"));
			int obx = Integer.parseInt(request.getParameter("obx"));

			if("true".equals(request.getParameter("showLatest")))
			{
				String multiLabId = Hl7textResultsData.getMatchingLabs(segmentID);
				segmentID = multiLabId.split(",")[multiLabId.split(",").length - 1];
			}

			OLISHL7Handler handler;
			if(StringUtils.isNotBlank(resultUuid))
			{
				handler = OLISResultsAction.searchResultsMap.get(resultUuid);
			}
			else
			{
				handler = (OLISHL7Handler) Factory.getHandler(segmentID);
			}

			if(handler != null)
			{
				handler.processEncapsulatedData(request, response, obr, obx);
			}
		}
		catch(Exception e)
		{
			logger.error("error", e);
		}
		return null;
	}
}
