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

package org.oscarehr.rx.web;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.apache.struts.upload.FormFile;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.rx.service.RxWatermarkService;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class RxWatermarkAction  extends DispatchAction
{
	private SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);

	//returns the watermark image
	public ActionForward getWatermark(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		try
		{
			GenericFile outfile = RxWatermarkService.getWatermark();
			response.setContentType("image/png");

			ServletOutputStream output = response.getOutputStream();
			InputStream buffIn = null;
			try {
				buffIn = outfile.asFileInputStream();
				int data;
				while ((data = buffIn.read()) != -1) {
					output.write(data);
				}
			} finally {
				if (buffIn!=null) buffIn.close();
			}

			output.flush();
			output.close();

			return null;
		}
		catch (FileNotFoundException e)
		{
			MiscUtils.getLogger().error("could not find watermark file: " + e.getMessage());
			response.setStatus(500);
			return null;
		}
	}

	//update the watermark image
	public ActionForward setWatermark(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		LoggedInInfo loggedInInfo=LoggedInInfo.getLoggedInInfoFromSession(request);
		securityInfoManager.requireOnePrivilege(loggedInInfo.getLoggedInProviderNo(), SecurityInfoManager.WRITE, null,  "_admin");

		FormFile watermarkFile = (FormFile) form.getMultipartRequestHandler().getFileElements().get("watermarkFile");
		if (watermarkFile != null)
		{
			MiscUtils.getLogger().info("new watermark file: " + watermarkFile.getFileName());
			try
			{
				RxWatermarkService.setWatermark(watermarkFile.getInputStream());
			}
			catch (Exception e)
			{
				MiscUtils.getLogger().error("error while writing watermark file: " + e.getMessage());
			}
		}
		else
		{
			MiscUtils.getLogger().error("failed to retrieve watermark file");
		}

		response.setStatus(200);
		return null;
	}

	public ActionForward enableWatermark(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		LoggedInInfo loggedInInfo=LoggedInInfo.getLoggedInInfoFromSession(request);
		securityInfoManager.requireOnePrivilege(loggedInInfo.getLoggedInProviderNo(), SecurityInfoManager.WRITE, null,  "_admin");

		boolean enableWatermark = Boolean.parseBoolean(request.getParameter("enable"));
		RxWatermarkService.enableWatermark(enableWatermark);

		response.setStatus(200);
		return null;
	}
}
