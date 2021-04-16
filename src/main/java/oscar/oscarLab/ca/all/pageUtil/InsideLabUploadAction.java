/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */


/*
 * InsideLabUploadAction.java
 *
 * Created on June 28, 2007, 1:11 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package oscar.oscarLab.ca.all.pageUtil;

import java.io.File;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.SpringUtils;

import oscar.oscarLab.ca.all.upload.handlers.LabHandlerService;
import oscar.oscarLab.ca.all.util.Utilities;

public class InsideLabUploadAction extends Action {
	private SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);
	private Logger logger = Logger.getLogger(InsideLabUploadAction.class);

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)  {
		logger.debug("Uploading lab file");
		LoggedInInfo loggedInInfo=LoggedInInfo.getLoggedInInfoFromSession(request);
		String providerNo = loggedInInfo.getLoggedInProviderNo();
		securityInfoManager.requireOnePrivilege(providerNo, securityInfoManager.CREATE, null, "_lab");

		LabUploadForm frm = (LabUploadForm) form;
		FormFile importFile = frm.getImportFile();
		String filename = importFile.getFileName();
		String providerNumber = (String) request.getSession().getAttribute("user");
		String outcome = "failure";

		InputStream formFileIs=null;
		try
		{
			formFileIs = importFile.getInputStream();


			String type = request.getParameter("type");
			if (type.equals("OTHER"))
			{
				type = request.getParameter("otherType");
			}

			// Check that we can use this filepath before attempting an import (throws NullPointerException)
			String filePath = Utilities.saveFile(formFileIs, filename);
			new File(filePath);

			logger.info("Lab Type: " + type);
			logger.info("Lab file path: " + filePath);
			String serviceName = getClass().getSimpleName();
			LabHandlerService labHandlerService = SpringUtils.getBean(LabHandlerService.class);

			labHandlerService.importLab(
					type,
					loggedInInfo,
					serviceName,
					filePath,
					providerNumber,
					request.getRemoteAddr()
			);

			// Set outcome to success in manual lab uploader as some handlers return an HL7 audit record
			// and the frontend JSP thinks any message other than "success" is an error
			outcome = "success";
		} catch(FileAlreadyExistsException e) {
			logger.error("Error: ",e);
			outcome = "uploaded previously";
		} catch(Exception e) {
			logger.error("Error: ",e);
			outcome = "exception";
		}
		finally {
			IOUtils.closeQuietly(formFileIs);
		}

		request.setAttribute("outcome", outcome);
		return mapping.findForward("success");
	}
}
