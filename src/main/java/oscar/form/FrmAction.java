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


// form_class - a part of class name
// c_lastVisited, formId - if the form has multiple pages
package oscar.form;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.security.model.Permission;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import oscar.log.LogAction;
import oscar.log.LogConst;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.Properties;

public final class FrmAction extends Action {
    
    private Logger logger = MiscUtils.getLogger();
    private SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);
    
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws ServletException
    {
		String providerNo = LoggedInInfo.getLoggedInInfoFromSession(request).getLoggedInProviderNo();
		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
		int newID = 0;
		FrmRecord rec = null;
		String where = "";
		boolean save = false;
		int demographicNo;
		int formId = 0;

		try
		{
			demographicNo = Integer.parseInt(request.getParameter("demographic_no"));
		}
		catch (NumberFormatException e)
		{
			throw new IllegalStateException("Form entries require a valid demographicNo to save against.");
		}

		try
		{
			formId = Integer.parseInt(request.getParameter("formId"));
		}
		catch (NumberFormatException e)
		{
			logger.error("Ran into a problem treating formId as an integer: ", e);
		}

	    if (formId > 0)
	    {
		    securityInfoManager.requireAllPrivilege(providerNo, demographicNo, Permission.FORM_UPDATE);
	    }
	    else
	    {
		    securityInfoManager.requireAllPrivilege(providerNo, demographicNo, Permission.FORM_CREATE);
	    }

        try {
            FrmRecordFactory recorder = new FrmRecordFactory();
            rec = recorder.factory(request.getParameter("form_class"));
            Properties props = new Properties();
               
            logger.info("SUBMIT " + (request.getParameter("submit") == null));
            //if we are graphing, we need to grab info from db and add it to request object
            if( request.getParameter("submit").equals("graph") )
            {
            	//Rourke needs to know what type of graph is being plotted
            	String graphType = request.getParameter("__graphType");
            	if( graphType != null ) {
            		rec.setGraphType(graphType);
            	}
            	
               props = rec.getGraph(demographicNo, formId);
               
               for( Enumeration e = props.propertyNames(); e.hasMoreElements(); ) {
                   String name = (String)e.nextElement();                   
                   request.setAttribute(name,props.getProperty(name));                   
               }
            }
            //if we are printing all pages of form, grab info from db and merge with current page info
            else if( request.getParameter("submit").equals("printAll") ) {
                props = rec.getFormRecord(loggedInInfo, demographicNo, formId);
                
                String name;
                for( Enumeration e = props.propertyNames(); e.hasMoreElements();) {
                    name = (String)e.nextElement();
                    if( request.getParameter(name) == null )
                        request.setAttribute(name,props.getProperty(name));
                }

                if (("true").equals(request.getParameter("fax"))
                        && ("false").equals(request.getParameter("skipSave"))) {
                    save = true;
                }
            }
            else
            {
                save = true;
            }

            if (save)
            {
                boolean bMulPage = request.getParameter("c_lastVisited") != null ? true : false;
                String name;

                if (bMulPage) {
                    String curPageNum = request.getParameter("c_lastVisited");
                    String commonField = request.getParameter("commonField") != null ? request
                            .getParameter("commonField") : "&'";
                    curPageNum = curPageNum.length() > 3 ? ("" + curPageNum.charAt(0)) : curPageNum;

                    //copy an old record
                    props = rec.getFormRecord(loggedInInfo, demographicNo, formId);

                    //empty the current page
                    Properties currentParam = new Properties();
                    for (Enumeration varEnum = request.getParameterNames(); varEnum.hasMoreElements();) {
                        name = (String) varEnum.nextElement();
                        currentParam.setProperty(name, "");
                    }
                    for (Enumeration varEnum = props.propertyNames(); varEnum.hasMoreElements();) {
                        name = (String) varEnum.nextElement();
                        // kick off the current page elements, commonField on the current page
                        if (name.startsWith(curPageNum + "_")
                                || (name.startsWith(commonField) && currentParam.containsKey(name))) {
                            props.remove(name);
                        }
                    }
                }

                //update the current record
                for (Enumeration varEnum = request.getParameterNames(); varEnum.hasMoreElements();) {
                    name = (String) varEnum.nextElement();                    
                    props.setProperty(name, request.getParameter(name));                    
                }

                props.setProperty("provider_no", (String) request.getSession().getAttribute("user"));
                newID = rec.saveFormRecord(props);
                String ip = request.getRemoteAddr();
                if (formId > 0)
                {
                    LogAction.addLogEntry(providerNo,
                            demographicNo,
                            LogConst.ACTION_EDIT,
                            LogConst.CON_FORM,
                            LogConst.STATUS_SUCCESS,
                            "Form ID edited: " + formId + " | Form ID saved: " + newID,
                            ip,
                            request.getParameter("form_class"));

                }
                else
                {
                    LogAction.addLogEntry(providerNo,
                            demographicNo,
                            LogConst.ACTION_ADD,
                            LogConst.CON_FORM,
                            LogConst.STATUS_SUCCESS,
                            "New form saved with id: " + newID,
                            ip,
                            request.getParameter("form_class"));
                }
            }
            String strAction = rec.findActionValue(request.getParameter("submit"));            
            ActionForward af = mapping.findForward(strAction);
            where = af.getPath();
            where = rec.createActionURL(where, strAction, request.getParameter("demographic_no"), "" + newID);

        } catch (Exception ex) {
            throw new ServletException(ex);
        }

        return new ActionForward(where); 
    }

}
