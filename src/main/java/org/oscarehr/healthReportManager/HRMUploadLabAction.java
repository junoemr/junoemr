/**
 * Copyright (c) 2008-2012 Indivica Inc.
 *
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */
package org.oscarehr.healthReportManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.apache.struts.upload.FormFile;
import org.oscarehr.common.io.FileFactory;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.healthReportManager.service.HRMReportProcessor;
import org.oscarehr.util.MiscUtils;

import org.oscarehr.util.SpringUtils;
import oscar.oscarLab.ca.all.pageUtil.LabUploadForm;
import oscar.oscarLab.ca.all.util.Utilities;

public class HRMUploadLabAction extends DispatchAction {

	@Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)  {

		LabUploadForm frm = (LabUploadForm) form;
        FormFile importFile = frm.getImportFile();
        
        try {
	        String filePath = Utilities.saveFile(importFile.getInputStream(), importFile.getFileName());
	        
	        GenericFile hrmFile = FileFactory.getHrmFile(filePath);
	        
	        HRMReportProcessor processor = SpringUtils.getBean(HRMReportProcessor.class);
	        processor.processHRMFile_43(hrmFile);
	        
	        request.setAttribute("success", true);
	        
        } catch (Exception e) {
	        MiscUtils.getLogger().error("Couldn't handle uploaded HRM lab", e);
	        
	        request.setAttribute("success", false);
        } 
        
        
        return mapping.findForward("success");
	}
	
}
