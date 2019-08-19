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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Service
public class RxWatermarkService
{
	// document dir appended to this
	private static String WATERMARK_FILE = "watermark.png";

	private static UserPropertyDAO userPropertyDao;
	@Autowired
	@Qualifier("UserPropertyDAO")
	private UserPropertyDAO _userPropertyDao;


	@PostConstruct
	public void PostConstructRxWatermarkService() {
		RxWatermarkService.userPropertyDao = this._userPropertyDao;
	}


	public static void enableWatermark(Boolean enable)
	{
		userPropertyDao.saveProp(UserProperty.ENABLE_RX_WATERMARK, enable.toString());
	}

	public static void setWatermarkBackground(Boolean isForeground)
	{
		userPropertyDao.saveProp(UserProperty.IS_BACKGROUND_RX_WATERMARK, isForeground.toString());
	}

	public static boolean isWatermarkEnabled()
	{
		UserProperty watermarkEnabledProperty = userPropertyDao.getProp(UserProperty.ENABLE_RX_WATERMARK);

		if (watermarkEnabledProperty != null)
		{
			return Boolean.parseBoolean(watermarkEnabledProperty.getValue());
		}
		return false;
	}

	public static boolean isWatermarkBackground()
	{
		UserProperty watermarkForegroundProperty = userPropertyDao.getProp(UserProperty.IS_BACKGROUND_RX_WATERMARK);

		if (watermarkForegroundProperty != null)
		{
			return Boolean.parseBoolean(watermarkForegroundProperty.getValue());
		}
		return false;
	}

	public static GenericFile getWatermark() throws IOException
	{
		return FileFactory.getResourceFile(WATERMARK_FILE);
	}

	public static void setWatermark(InputStream fileData) throws IOException, InterruptedException
	{
		if (!FileFactory.isResourceFileExist(WATERMARK_FILE))
		{
			FileFactory.createResourceFile(fileData, WATERMARK_FILE);
		}
		else
		{
			FileFactory.overwriteFileContents(FileFactory.getResourceFile(WATERMARK_FILE), fileData);
		}
	}

	public static void deleteWatermark() throws IOException
	{
		getWatermark().deleteFile();
	}

}
