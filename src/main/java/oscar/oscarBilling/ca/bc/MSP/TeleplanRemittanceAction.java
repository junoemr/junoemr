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


package oscar.oscarBilling.ca.bc.MSP;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.oscarehr.billing.CA.BC.service.TeleplanRemittanceService;
import org.oscarehr.common.io.FileFactory;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TeleplanRemittanceAction extends Action
{
	private static final Logger logger = MiscUtils.getLogger();
	private static final TeleplanRemittanceService teleplanRemittanceService = SpringUtils.getBean(TeleplanRemittanceService.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException
	{
		String filename = (String) request.getAttribute("filename");
		try
		{
			return teleplanRemittanceService.execute(mapping, filename);
		}
		catch(Exception e)
		{
			logger.error("Remittance Processing Error", e);
			GenericFile file = FileFactory.getRemittanceFile(filename);
			file.moveToBillingRemittanceFailed();
		}
		request.setAttribute("error", "An error has occurred while processing the remittance file");
		return mapping.findForward("error");
	}
}