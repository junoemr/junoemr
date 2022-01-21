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
package org.oscarehr.hospitalReportManager;

import org.apache.commons.io.FileUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DownloadAction;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.hospitalReportManager.exception.HrmDocumentException;
import org.oscarehr.hospitalReportManager.model.HRMDocument;
import org.oscarehr.hospitalReportManager.dao.HRMDocumentDao;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.security.model.Permission;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.SpringUtils;
import oscar.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

public class HRMDownloadFileAction extends DownloadAction
{
	private static final HRMDocumentDao hrmDocumentDao = (HRMDocumentDao) SpringUtils.getBean("HRMDocumentDao");
	private final SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);

	protected StreamInfo getStreamInfo(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
			throws Exception
	{
		securityInfoManager.requireAllPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request).getLoggedInProviderNo(), Permission.HRM_READ);

	    String id = request.getParameter("id");

	    if(StringUtils.isNullOrEmpty(id))
	    {
		    throw new HrmDocumentException("document id is null");
	    }

    	HRMDocument hd = hrmDocumentDao.find(Integer.parseInt(id));
		if(hd == null)
		{
			throw new HrmDocumentException("document not found - " + id);
		}

		HRMReport report = HRMReportParser.parseRelativeLocation(hd.getReportFile(), hd.getReportFileSchemaVersion());

		if(report == null)
		{
			throw new HrmDocumentException("failed to parse hrm document with id " + hd.getId());
		}

		if(!report.isBinary())
		{
			throw new HrmDocumentException("document is not binary");
		}
  
		// This is somehow already decoding the base64 content... don't ask me how.
    	byte[] binaryContent = report.getBase64BinaryContent();
		
    	String fileExtension = report.getFileExtension().toLowerCase();
	    fileExtension = fileExtension.replaceAll("\\.", "");

	    String fileName = GenericFile.getSanitizedFileName(
			    report.getLegalLastName() + "-" +
					    report.getLegalFirstName() + "-" +
					    report.getClassName() + "." + fileExtension
	    );

    	String contentType = "application/octet-stream";

	    if(fileExtension.equals("pdf"))
	    {
		    contentType = "application/pdf";
	    }
	    if(fileExtension.equals("tiff"))
	    {
		    contentType = "image/tiff";
	    }
	    if(fileExtension.equals("rtf"))
	    {
		    contentType = "text/enriched";
	    }
	    if(fileExtension.equals("jpg"))
	    {
		    contentType = "image/jpeg";
	    }
	    if(fileExtension.equals("gif"))
	    {
		    contentType = "image/gif";
	    }
	    if(fileExtension.equals("png"))
	    {
		    contentType = "image/png";
	    }
	    if(fileExtension.equals("html"))
	    {
		    contentType = "text/html";
	    }
         
        response.setHeader("Content-disposition", 
                           "attachment; filename=" + fileName);
        
        File temp = File.createTempFile("HRMDownloadFile", "." + fileExtension);
        temp.deleteOnExit();
        
        FileUtils.writeByteArrayToFile(temp, binaryContent);
       
        return new FileStreamInfo(contentType, temp);   
    }   
}