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
package org.oscarehr.clinic.service;

import org.oscarehr.common.io.FileFactory;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.util.MiscUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ClinicImageService
{
	// image types. to add a new image add its type, and add an entry in the imageTypeMap.
	// Keeping in mind that these images are stored in the resource directory
	public enum IMAGE_TYPE {
		WATERMARK,
		CARD_LOGO,
		NAV_LOGO,
	}
	private static Map<IMAGE_TYPE, String> imageTypeMap = new HashMap<>();
	static
	{
		imageTypeMap.put(IMAGE_TYPE.WATERMARK, "watermark.png");
		imageTypeMap.put(IMAGE_TYPE.CARD_LOGO, "card_logo.png");
		imageTypeMap.put(IMAGE_TYPE.NAV_LOGO, "nav_logo.png");
	}

	/**
	 * convert string type name to enum image type
	 * @param typeString - string name of image type
	 * @return - the enum for that type
	 */
	public static IMAGE_TYPE stringToImageType(String typeString)
	{
		if (typeString == null)
		{
			return null;
		}

		try
		{
			return IMAGE_TYPE.valueOf(typeString);
		}
		catch (IllegalArgumentException e)
		{
			MiscUtils.getLogger().error("Invalid Clinic Image type: " + typeString);
			return null;
		}
	}

	/**
	 * get a image by type
	 * @param imageType - the type of image you wish to get
	 * @return - a generic file handle to the image.
	 * @throws IOException - if an error occurs fetching the file.
	 */
	public static GenericFile getImage(IMAGE_TYPE imageType) throws IOException
	{
		String resourceFile = imageTypeMap.get(imageType);

		if (FileFactory.isResourceFileExist(resourceFile))
		{
			return FileFactory.getResourceFile(resourceFile);
		}
		else
		{
			throw new FileNotFoundException(resourceFile);
		}
	}

	/**
	 * create / overwrite the image of the given type on disk.
	 * @param imageType - the type of image to set
	 * @param fileData - the new image data
	 * @throws IOException - if an error occurs writing file
	 * @throws InterruptedException - if an interrupt is triggered while writing file
	 */
	public static void setImage(IMAGE_TYPE imageType, InputStream fileData) throws IOException, InterruptedException
	{
		String resourceFile = imageTypeMap.get(imageType);

		if (!FileFactory.isResourceFileExist(resourceFile))
		{
			FileFactory.createResourceFile(fileData, resourceFile);
		}
		else
		{
			FileFactory.overwriteFileContents(FileFactory.getResourceFile(resourceFile), fileData);
		}
	}

	/**
	 * delete image of the given type
	 * @param imageType - the type of image to delete
	 * @throws IOException - if an error occurs while deleting the image
	 */
	public static void deleteImage(IMAGE_TYPE imageType) throws IOException
	{
		String resourceFile = imageTypeMap.get(imageType);

		if (FileFactory.isResourceFileExist(resourceFile))
		{
			try
			{
				FileFactory.getResourceFile(resourceFile).deleteFile();
			}
			catch (FileNotFoundException e)
			{
				// suppress
			}
		}
	}
}