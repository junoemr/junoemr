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


package oscar.dms.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.oscarehr.util.MiscUtils;

import oscar.OscarProperties;

/**
 * @author jay
 * revised by Robert 2016
 */
public class EmailPDFAction extends Action {
	
	OscarProperties props = OscarProperties.getInstance();
	Logger logger = MiscUtils.getLogger();
	
    /** Creates a new instance of CombinePDFAction */
    public EmailPDFAction() {
    }

    /** main execution method */
    public ActionForward execute(ActionMapping mapping, ActionForm form, 
		HttpServletRequest request, HttpServletResponse response) 
	{
    	String emailActionType = request.getParameter("emailActionType");
    	emailActionType = (emailActionType != null) ? emailActionType: "";
    	
    	request.setAttribute("emailActionType", emailActionType);
    	
    	if(emailActionType.equals("DOC")) {
    		return emailDoc(mapping, form, request, response);
    	}
    	else if (emailActionType.equals("RX")) {
    		return emailRx(mapping, form, request, response);
    	}
    	else if (emailActionType.equals("PREV")) {
    		return emailPrevention(mapping, form, request, response);
    	}
    	return mapping.findForward("failed");
    }
    /** set document email parameters and forward to correct page */
    @SuppressWarnings("unused")
    private ActionForward emailDoc(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) {
    	
    	if(props.isPropertyActive("document_email_enabled")) {
        	// Provide the docNo values to the view to pass on to the send page	
    		request.setAttribute("docNo", request.getParameterValues("docNo"));
			return mapping.findForward("success");
		}
		return mapping.findForward("failed");
    	
    }
    /** set rx email parameters and forward to correct page */
    @SuppressWarnings("unused")
	private ActionForward emailRx(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) {
    	
    	if(props.isPropertyActive("rx_email_enabled")) {
    		for(Object attr : request.getParameterMap().keySet()) {
    			request.setAttribute((String)attr, request.getParameterValues((String)attr));
    		}
			return mapping.findForward("success");
		}
    	return mapping.findForward("failed");
    }
    /** set prevention email parameters and forward to correct page */
    @SuppressWarnings("unused")
	private ActionForward emailPrevention(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) {
    	
    	if(props.isPropertyActive("prevention_email_enabled")) {
    		for(Object attr : request.getParameterMap().keySet()) {
    			request.setAttribute((String)attr, request.getParameterValues((String)attr));
    		}
			return mapping.findForward("success");
		}
    	return mapping.findForward("failed");
    }
}

