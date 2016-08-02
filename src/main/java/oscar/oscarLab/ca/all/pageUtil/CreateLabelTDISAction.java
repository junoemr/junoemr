/**
 * Copyright (c) 2008-2012 Indivica Inc.
 *
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */

package oscar.oscarLab.ca.all.pageUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.oscarehr.common.dao.Hl7TextInfoDao;
import org.oscarehr.util.SpringUtils;

import oscar.log.LogAction;
import oscar.log.LogConst;


public class CreateLabelTDISAction extends Action {
	Logger logger = Logger.getLogger(CreateLabelTDISAction.class);
	
	public ActionForward execute (ActionMapping mapping,ActionForm form, HttpServletRequest request, HttpServletResponse response){
		
		CreateLabelTDISForm frm = (CreateLabelTDISForm) form;
		String label = frm.getLabel();//request.getParameter("label");
		logger.debug("Label before db insert = '"+label+"'");
		String lab_no = frm.getLab_no();//request.getParameter("lab_no");
		String accessionNum = frm.getAccessionNum();//request.getParameter("accessionNum");
		String ajaxcall=request.getParameter("ajaxcall");
		
		label = StringEscapeUtils.escapeJavaScript(label);
		
		if (label==null || label.trim().equals("")) {
			request.setAttribute("error", "Please enter a label");
		}
		response.setContentType("application/json");
		Hl7TextInfoDao hl7dao = (Hl7TextInfoDao) SpringUtils.getBean("hl7TextInfoDao");

		try {
			
			int labNum = Integer.parseInt(lab_no);
			hl7dao.createUpdateLabelByLabNumber(label, labNum);
			
			logger.info("Label '" + label + "' created/updated for lab_no "+lab_no+" successfully.");
			LogAction.addLog(request.getParameter("providerNo"), LogConst.EDIT, LogConst.CON_HL7_LAB, lab_no, request.getRemoteAddr());
			
		} catch (Exception e){
			logger.error("Error inserting label '" + label + "' into hl7TextInfo" + e);
			request.setAttribute("error", "There was an error creating a label.");
		}

		if( ajaxcall != null && !"null".equalsIgnoreCase(ajaxcall)) {
			return null;
		}
		return mapping.findForward("complete");
	}

}
