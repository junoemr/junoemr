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

import org.oscarehr.clinic.service.ClinicImageService;
import org.oscarehr.common.dao.UserPropertyDAO;
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
	private static UserPropertyDAO userPropertyDao;
	@Autowired
	@Qualifier("UserPropertyDAO")
	private UserPropertyDAO _userPropertyDao;


	@PostConstruct
	public void PostConstructRxWatermarkService() {
		RxWatermarkService.userPropertyDao = this._userPropertyDao;
	}


	/**
	 * enabled this watermark
	 * @param enable - if true the configured watermark will display on Rx prescriptions
	 */
	public static void enableWatermark(Boolean enable)
	{
		userPropertyDao.saveProp(UserProperty.ENABLE_RX_WATERMARK, enable.toString());
	}

	/**
	 * set watermark display layer
	 * @param isForeground - if true watermark is forground else background
	 */
	public static void setWatermarkBackground(Boolean isForeground)
	{
		userPropertyDao.saveProp(UserProperty.IS_BACKGROUND_RX_WATERMARK, isForeground.toString());
	}

	/**
	 * check if the watermark is enabled
	 * @return - true if the watermark is enabled, else false
	 */
	public static boolean isWatermarkEnabled()
	{
		UserProperty watermarkEnabledProperty = userPropertyDao.getProp(UserProperty.ENABLE_RX_WATERMARK);

		if (watermarkEnabledProperty != null)
		{
			return Boolean.parseBoolean(watermarkEnabledProperty.getValue());
		}
		return false;
	}

	/**
	 * check if the watermark is configured in background mode
	 * @return - true if watermark is background
	 */
	public static boolean isWatermarkBackground()
	{
		UserProperty watermarkForegroundProperty = userPropertyDao.getProp(UserProperty.IS_BACKGROUND_RX_WATERMARK);

		if (watermarkForegroundProperty != null)
		{
			return Boolean.parseBoolean(watermarkForegroundProperty.getValue());
		}
		return false;
	}

	/**
	 * get the watermark image
	 * @return - the watermark image file
	 * @throws IOException - if an io error occurs retrieving the image file
	 */
	public static GenericFile getWatermark() throws IOException
	{
		return ClinicImageService.getImage(ClinicImageService.IMAGE_TYPE.WATERMARK);
	}

	/**
	 * set the watermark image
	 * @param fileData - an input stream containing the new watermark
	 * @throws IOException - if there is an error writing watermark file
	 * @throws InterruptedException - if the system triggers an interrupt while writing the watermark file
	 */
	public static void setWatermark(InputStream fileData) throws IOException, InterruptedException
	{
		ClinicImageService.setImage(ClinicImageService.IMAGE_TYPE.WATERMARK, fileData);
	}

	/**
	 * delete watermark image
	 * @throws IOException - if error deleting watermark
	 */
	public static void deleteWatermark() throws IOException
	{
		ClinicImageService.deleteImage(ClinicImageService.IMAGE_TYPE.WATERMARK);
	}

}
