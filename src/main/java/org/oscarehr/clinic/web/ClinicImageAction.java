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

package org.oscarehr.clinic.web;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.apache.struts.upload.FormFile;
import org.oscarehr.clinic.service.ClinicImageService;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;

// use to get and set clinic wide images
public class ClinicImageAction extends DispatchAction
{
	private SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);

	/**
	 * get a clinic image
	 * params
	 * 	image_type - the type of the clinic image to retrieve
	 */
	public ActionForward getImage(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	{
		ClinicImageService.IMAGE_TYPE imageType = ClinicImageService.stringToImageType(request.getParameter("image_type"));
		response.setContentType("image/png");

		if (imageType == null)
		{
			response.setStatus(400);
			return null;
		}

		try
		{
			GenericFile imageFile = ClinicImageService.getImage(imageType);
			ServletOutputStream output = response.getOutputStream();

			imageFile.writeToOutputStream(output);
			response.setStatus(200);
			return null;
		}
		catch (FileNotFoundException e)
		{
			MiscUtils.getLogger().warn("Missing requested Clinic image, " +  imageType.name());
			response.setStatus(404);
		}
		catch (IOException e)
		{
			MiscUtils.getLogger().error("Failed to get Card logo with error: " + e.toString(), e);
			response.setStatus(500);
		}

		return null;
	}

	/**
	 * upload and possibly overwrite a clinic image
	 * params
	 * 	image_type - the type of the image to upload
	 * 	image_file - the new image
	 */
	public ActionForward uploadImage(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	{
		LoggedInInfo loggedInInfo=LoggedInInfo.getLoggedInInfoFromSession(request);
		securityInfoManager.requireOnePrivilege(loggedInInfo.getLoggedInProviderNo(), SecurityInfoManager.CREATE, null,  "_admin");

		ClinicImageService.IMAGE_TYPE imageType = ClinicImageService.stringToImageType(request.getParameter("image_type"));
		if (imageType != null)
		{
			FormFile imageFile = (FormFile) form.getMultipartRequestHandler().getFileElements().get("image_file");
			if (imageFile != null && !imageFile.getFileName().isEmpty())
			{
				MiscUtils.getLogger().info("new image file: " + imageFile.getFileName());
				try
				{
					ClinicImageService.setImage(imageType, imageFile.getInputStream());
				} catch (Exception e)
				{
					MiscUtils.getLogger().error("error while writing image file: " + e.getMessage());
					response.setStatus(500);
				}
			} else
			{
				MiscUtils.getLogger().error("failed to retrieve image file");
				response.setStatus(500);
			}
		}
		else
		{
			response.setStatus(400);
		}

		return null;
	}

	/**
	 * delete a clinic image
	 * params
	 * 	image_type - the type of image to delete
	 */
	public ActionForward deleteImage(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	{
		LoggedInInfo loggedInInfo=LoggedInInfo.getLoggedInInfoFromSession(request);
		securityInfoManager.requireOnePrivilege(loggedInInfo.getLoggedInProviderNo(), SecurityInfoManager.CREATE, null,  "_admin");

		ClinicImageService.IMAGE_TYPE imageType = ClinicImageService.stringToImageType(request.getParameter("image_type"));
		if (imageType != null)
		{
			try
			{
				ClinicImageService.deleteImage(imageType);
				response.setStatus(200);
			}
			catch (IOException e)
			{
				MiscUtils.getLogger().error("Error deleting image, " + imageType.name() + " Error: " + e.toString(), e);
				response.setStatus(500);
			}
		}
		else
		{
			response.setStatus(400);
		}

		return null;
	}
}
