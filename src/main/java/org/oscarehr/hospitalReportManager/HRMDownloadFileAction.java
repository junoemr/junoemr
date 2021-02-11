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
import org.oscarehr.common.model.SecObjectName;
import org.oscarehr.hospitalReportManager.dao.HRMDocumentDao;
import org.oscarehr.hospitalReportManager.model.HRMDocument;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.SpringUtils;
import oscar.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;

public class HRMDownloadFileAction extends DownloadAction
{
	private static final HRMDocumentDao hrmDocumentDao = (HRMDocumentDao) SpringUtils.getBean("HRMDocumentDao");
	private final SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);
	 
    /** Creates a new instance of DownloadFileAction */
    public HRMDownloadFileAction() {
    }
    
    protected StreamInfo getStreamInfo(ActionMapping mapping, 
                                       ActionForm form,
                                       HttpServletRequest request, 
                                       HttpServletResponse response)
            throws Exception {

	    securityInfoManager.requireOnePrivilege(LoggedInInfo.getLoggedInInfoFromSession(request).getLoggedInProviderNo(),
			    SecurityInfoManager.READ, null, SecObjectName._HRM);
    	
    	String hash = request.getParameter("hash");
    	if(StringUtils.isNullOrEmpty(hash)) {
    		throw new Exception("no hash parameter passed");
    	}
    	
    	List<Integer> ids  = hrmDocumentDao.findByHash(hash);
    	
    	if(ids == null || ids.size() == 0) {
    		throw new Exception("no documents found for hash - " + hash);
    	}
    	
    	if(ids.size() > 1) {
    		throw new Exception("too many documents found for hash - " + hash);
    	}
    	
    	HRMDocument hd = hrmDocumentDao.find(ids.get(0));
    	
    	if(hd == null) {
    		throw new Exception("HRMDocument not found - " + ids.get(0));
    	}
    	
    	HRMReport report = HRMReportParser.parseReport(hd.getReportFile(), hd.getReportFileSchemaVersion());
        
    	if(report == null) {
    		throw new Exception("Failed to parse HRMDocument with id " + hd.getId());
    	}
    	
    	if(!report.isBinary()) {
    		throw new Exception("no binary document found");
    	}
    	
    	byte[] data = report.getBinaryContent();

    	String fileExtension = report.getFileExtension().toLowerCase();
    	if(!fileExtension.startsWith("."))
	    {
		    fileExtension = "." + fileExtension;
	    }

	    String fileName = GenericFile.getSanitizedFileName(
			    report.getLegalLastName() + "-" +
					    report.getLegalFirstName() + "-" +
					    report.getFirstReportClass() + fileExtension
	    );

    	String contentType = "application/octet-stream";
    	
    	if(fileExtension.equals(".pdf")) {
    		contentType = "application/pdf";
    	}
    	if(fileExtension.equals(".tiff")) {
    		contentType = "image/tiff";
    	}
    	if(fileExtension.equals(".rtf")) {
    		contentType = "text/enriched";
    	}
    	if(fileExtension.equals(".jpg")) {
    		contentType = "image/jpeg";
    	}
    	if(fileExtension.equals(".gif")) {
    		contentType = "image/gif";
    	}
    	if(fileExtension.equals(".png")) {
    		contentType = "image/png";
    	}
    	if(fileExtension.equals(".html")) {
    		contentType = "text/html";
    	}
    	
         
        response.setHeader("Content-disposition", 
                           "attachment; filename=" + fileName);
        
        File temp = File.createTempFile("HRMDownloadFile", fileExtension);
        temp.deleteOnExit();
        
        FileUtils.writeByteArrayToFile(temp, data);
       
        return new FileStreamInfo(contentType, temp);   
    }   
}
