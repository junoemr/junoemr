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


package oscar.eform.actions;

import java.io.File;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DownloadAction;
import org.oscarehr.common.io.FileFactory;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.util.MiscUtils;

import oscar.OscarProperties;

/**
 * eform_image
 * @author jay
 *and Paul
 */
public class DisplayImageAction extends DownloadAction
{


	/** Creates a new instance of DisplayImageAction */
	public DisplayImageAction()
	{

	}

	protected StreamInfo getStreamInfo(ActionMapping mapping,
									   ActionForm form,
									   HttpServletRequest request,
									   HttpServletResponse response)
			throws Exception
	{


		String fileName = request.getParameter("imagefile");
		//if (fileName.indexOf('/') != -1) return null;  //prevents navigating away from the page.
		response.setHeader("Content-disposition", "inline; filename=" + fileName);

		GenericFile file = FileFactory.getEformImage(fileName);
		//String canonicalPath = file.getParentFile().getCanonicalPath(); //absolute path of the retrieved file

		String contentType;
		try
		{
			contentType = file.getContentType();
		}
		catch (Exception e)
		{
			MiscUtils.getLogger().error("Error", e);
			contentType = "application/octet-stream";
		}
		return new FileStreamInfo(contentType, file.getFileObject());
	}

	/**
	 *
	 * @param f String &lt;filename e.g example.jpeg>
	 * This method used to get file extension from a given filename
	 * @return String <file extension>
	 *
	 */
	public String extension(String f)
	{
		int dot = f.lastIndexOf(".");
		return f.substring(dot + 1);
	}

	public static File getImageFile(String imageFileName) throws Exception
	{
		String home_dir = OscarProperties.getInstance().getProperty("eform_image");

		File file = null;
		try
		{
			File directory = new File(home_dir);
			if (!directory.exists())
			{
				throw new Exception("Directory:  " + home_dir + " does not exist");
			}
			file = new File(directory, imageFileName);
			//String canonicalPath = file.getParentFile().getCanonicalPath(); //absolute path of the retrieved file

			if (!directory.equals(file.getParentFile()))
			{
				MiscUtils.getLogger().debug("SECURITY WARNING: Illegal file path detected, client attempted to navigate away from the file directory");
				throw new Exception("Could not open file " + imageFileName + ".  Check the file path");
			}
			return file;
		}
		catch (Exception e)
		{
			MiscUtils.getLogger().error("Error", e);
			throw new Exception("Could not open file " + home_dir + imageFileName + " does " + home_dir + " exist ?", e);
		}
	}

	/**
	 *
	 * Process only files under dir
	 * This method used to list images for eform generator
	 *
	 */
	public String[] visitAllFiles(File dir)
	{
		String[] children = null;
		if (dir.isDirectory())
		{
			children = dir.list();
			for (int i = 0; i < children.length; i++)
			{
				visitAllFiles(new File(dir, children[i]));
			}
		}
		return children;
	}

	public static String[] getRichTextLetterTemplates(File dir)
	{
		ArrayList<String> results = getFiles(dir, ".*(rtl)$", null);
		return results.toArray(new String[0]);
	}

	public static ArrayList<String> getFiles(File dir, String ext, ArrayList<String> files)
	{
		if (files == null)
		{
			files = new ArrayList<String>();
		}
		if (dir.isDirectory())
		{
			for (String fileName : dir.list())
			{
				if (fileName.toLowerCase().matches(ext))
				{
					files.add(fileName);
				}
			}
		}
		return files;
	}
}
