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
import java.io.FileInputStream;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import oscar.OscarProperties;
import oscar.oscarLab.FileUploadCheck;
import oscar.oscarLab.ca.all.upload.HandlerClassFactory;
import oscar.oscarLab.ca.all.upload.handlers.MessageHandler;
import oscar.oscarLab.ca.all.util.Utilities;

public class ExcellerisImport extends Action 
{
    Logger logger = Logger.getLogger(ExcellerisImport.class);
    
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response)  
	{
        String proNo = (String) request.getSession().getAttribute("user");
        String importoutcome;
		String failed_string = "";
		String success_string = "";
		List<String> failed_files = new ArrayList<String>();
		List<String> success_files = new ArrayList<String>();
        int total_files = 0;

        try
		{
            
            OscarProperties props = OscarProperties.getInstance();
            //properties must exist
            String excelleris_labdir = props.getProperty("DOCUMENT_DIR") + "excelleris/";
            logger.info("Opening "+excelleris_labdir);
            
            File lab_dir = new File(excelleris_labdir);
			File[] file_list = lab_dir.listFiles();
            List<String> labdir_files = new ArrayList<String>();
			for( File incoming_file : file_list)
			{
				if(!incoming_file.isDirectory())
				{
					labdir_files.add(incoming_file.getName());
				}
			}

            total_files = labdir_files.size();

			String file_list_out = "";
			for(String files : labdir_files)
			{
				file_list_out += files + "<br>";
			}
			logger.info("Total files: " + Integer.toString(total_files));
			logger.info("Files: \n" + file_list_out);
            
            String type = "PATHL7";
                        
			for(String current_file : labdir_files)
			{
				try
				{
					logger.info("Found "+current_file+ " at " + 
							excelleris_labdir+current_file);
					InputStream is = new FileInputStream(excelleris_labdir +
							current_file);
					
					String filePath = Utilities.saveFile(is, current_file);
					is.close();
					File file = new File(filePath);
					
					is = new FileInputStream(filePath);
            		int checkFileUploadedSuccessfully = 
						FileUploadCheck.addFile(file.getName(),is,proNo);            
					is.close();
					
					if (checkFileUploadedSuccessfully != FileUploadCheck.UNSUCCESSFUL_SAVE)
					{
						logger.info("File uploaded");
						logger.info("filePath: " + filePath);
						logger.info("Type :" + type);
						MessageHandler msgHandler = HandlerClassFactory.getHandler(type);
						if(msgHandler != null)
						{
						   logger.info("MESSAGE HANDLER "+msgHandler.getClass().getName());
						}
						if((msgHandler.parse(getClass().getSimpleName(), 
							filePath,checkFileUploadedSuccessfully)) == "success")
						{
							success_files.add(current_file);

							//Move file to "done" folder
							File uploadedLab = new File(
									excelleris_labdir+current_file);
							File doneDir = new File(excelleris_labdir +
									"done/"+current_file);
							uploadedLab.renameTo(doneDir);
						}
						else
						{
							throw new RuntimeException("Failed to parse: " +
								current_file);
						}
					}
					else
					{
						failed_files.add(current_file);
						logger.error("Excelleris: File has already " + 
							"been imported. Note: If the file was " +
							"uploaded, but failed to parse, it will " +
							"show up as a duplicate. Duplicates are " + 
							"compared by md5 hash.");
					}
				}
				catch(Exception e)
				{
					failed_files.add(current_file);
					logger.error("Excelleris: An error occurred handling file: " +
						current_file + "\n" ,e);
					throw e;
				}
			}
        }
		catch(Exception e)
		{
			logger.error("Excelleris: Outer Exception", e);
        }

		// Merge array of failed and successful file names
		for(String failed_file : failed_files)
		{
			failed_string += failed_file + "<br>";
		}
		for(String success_file : success_files)
		{
			success_string += success_file + "<br>";
		}

		if(total_files == 0)
		{
			importoutcome = "No new labs to import";
		}
		else if(failed_files.size() == 0 && success_files.size() == total_files)
		{
			importoutcome = "Successfully import " + total_files + " lab files.";
			importoutcome+= "<b>The following files successfully imported: " +
				"<br>" + success_string;
		}
		else
		{
			importoutcome = "An exception occurred importing files from " +
				" excelleris. Please contact your administrator.<br>" +
				"The following files failed to import: <br>" +
				failed_string;
			if(!success_string.equals(""))
			{
				importoutcome+= "<b>The following files successfully imported: " +
					"<br>" + success_string;
			}
		}
       
        logger.info(importoutcome.replaceAll("<br>", "\n"));
        request.setAttribute("importoutcome", importoutcome);
        return mapping.findForward("success");
        
    }
    
}
