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

package org.oscarehr.rx.service;

import org.oscarehr.common.dao.UserPropertyDAO;
import org.oscarehr.common.io.FileFactory;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.common.model.UserProperty;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;

import java.io.IOException;
import java.io.InputStream;


public class RxWatermarkService
{
	// document dir appended to this
	private static String WATERMARK_FILE = "watermark.png";


	public static void enableWatermark(Boolean enable)
	{
		UserPropertyDAO propertyDAO  = SpringUtils.getBean(UserPropertyDAO.class);
		propertyDAO.saveProp(UserProperty.ENABLE_RX_WATERMARK, enable.toString());
	}

	public static boolean isWatermarkEnabled()
	{
		UserPropertyDAO propertyDAO  = SpringUtils.getBean(UserPropertyDAO.class);
		UserProperty watermarkEnabledProperty = propertyDAO.getProp(UserProperty.ENABLE_RX_WATERMARK);

		if (watermarkEnabledProperty != null)
		{
			return Boolean.parseBoolean(watermarkEnabledProperty.getValue());
		}
		return false;
	}

	public static GenericFile getWatermark() throws IOException
	{
		return FileFactory.getResourceFile(WATERMARK_FILE);
	}

	public static void setWatermark(InputStream fileData)
	{
		try
		{
			GenericFile watermark = null;
			if (!FileFactory.isResourceFileExist(WATERMARK_FILE))
			{
				FileFactory.createResourceFile(fileData, WATERMARK_FILE);
			}
			else
			{
				FileFactory.overwriteFileContents(FileFactory.getResourceFile(WATERMARK_FILE), fileData);
			}

		}
		catch (Exception e)
		{
			MiscUtils.getLogger().error("error while writing watermark file: " + e.getMessage());
		}
	}

}
