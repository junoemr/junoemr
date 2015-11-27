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


package oscar.oscarEncounter.pageUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import org.oscarehr.util.MiscUtils;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsDateJsonBeanProcessor;
import oscar.util.UtilDateUtilities;
/**
 * Base action class for populating left navbar of encounter
 * @author rjonasz
 */
public class EctDisplayAction extends Action {
    private static Map<String, String> Actions = null;
    protected static final String ELLIPSES = "...";
    protected static final int MAX_LEN_TITLE = 48;
    protected static final int CROP_LEN_TITLE = 45;
    protected static final int MAX_LEN_KEY = 12;
    protected static final int CROP_LEN_KEY = 9;

    public EctDisplayAction() {
        super();
        if( Actions == null ) {
            Actions = new HashMap<String, String>();
            Actions.put("labs", "/oscarEncounter/displayLabs.do");
            Actions.put("forms", "/oscarEncounter/displayForms.do");
            Actions.put("msgs", "/oscarEncounter/displayMessages.do");
            Actions.put("eforms", "/oscarEncounter/displayEForms.do");
            Actions.put("docs", "/oscarEncounter/displayDocuments.do");
            Actions.put("measurements", "/oscarEncounter/displayMeasurements.do");
            Actions.put("tickler", "/oscarEncounter/displayTickler.do");
            Actions.put("Dx", "/oscarEncounter/displayDisease.do");
            Actions.put("preventions", "/oscarEncounter/displayPrevention.do");
            Actions.put("consultation", "/oscarEncounter/displayConsultation.do");
            Actions.put("allergies", "/oscarEncounter/displayAllergy.do");
            Actions.put("unresolvedIssues", "/oscarEncounter/displayIssues.do");
            Actions.put("resolvedIssues", "/oscarEncounter/displayIssues.do");
            Actions.put("Rx", "/oscarEncounter/displayRx.do");
            Actions.put("success", "/oscarEncounter/LeftNavBarDisplay.jsp");
            Actions.put("error", "/oscarEncounter/LeftNavBarError.jsp");
            Actions.put("HRM","/oscarEncounter/displayHRM.do");
            Actions.put("myoscar","/oscarEncounter/displayMyOscar.do");
        }
    }

    public ActionForward execute(ActionMapping mapping,
				 ActionForm form,
				 HttpServletRequest request,
				 HttpServletResponse response) throws IOException, ServletException {

        EctSessionBean bean = (EctSessionBean) request.getSession().getAttribute("EctSessionBean");
        String forward = "error";
        String cmd = getCmd();
        String navName;
        if( (navName = (String) request.getAttribute("navbarName")) != null )
            navName += "+" + cmd;
        else
            navName = cmd;

        request.setAttribute("navbarName", navName);

        boolean isJsonRequest = request.getParameter("json") != null && request.getParameter("json").equalsIgnoreCase("true");
        request.setAttribute("isJsonRequest", isJsonRequest);

        if( bean == null || request.getParameter("demographicNo") != null ) {
        	bean = new EctSessionBean();
        	bean.currentDate = UtilDateUtilities.StringToDate(request.getParameter("curDate"));

            if (bean.currentDate == null){
                bean.currentDate = UtilDateUtilities.Today();
            }
            bean.providerNo=request.getParameter("providerNo");
            if(bean.providerNo == null){
                bean.providerNo = (String) request.getSession().getAttribute("user");
            }
            bean.demographicNo=request.getParameter("demographicNo");
            bean.appointmentNo=request.getParameter("appointmentNo");
            bean.curProviderNo=request.getParameter("curProviderNo");
            bean.reason=request.getParameter("reason");
            bean.encType=request.getParameter("encType");
            bean.userName=request.getParameter("userName");
            if (bean.userName == null){
                 bean.userName =  ( (String) request.getSession().getAttribute("userfirstname") ) + " " + ( (String) request.getSession().getAttribute("userlastname") );
            }

            bean.appointmentDate=request.getParameter("appointmentDate");
            bean.startTime=request.getParameter("startTime");
            bean.status=request.getParameter("status");
            bean.date=request.getParameter("date");
            bean.check= "myCheck";
            bean.oscarMsgID = request.getParameter("msgId");
            bean.setUpEncounterPage();
            request.getSession().setAttribute("EctSessionBean",bean);
            request.getSession().setAttribute("eChartID", bean.eChartId);
            if(request.getParameter("source")!=null) {
            	bean.source = request.getParameter("source");
            }

            request.setAttribute("EctSessionBean", bean);
        }

        //Can we handle request?
        //Check attrib first so we know if we are in a chain call before a direct request
        String params = (String)request.getAttribute("cmd");
        if( params == null )
            params = request.getParameter("cmd");
        request.setAttribute("cmd", params);

        if( params != null ) {
            //Check to see if this call is for us
            if( params.indexOf(cmd) > -1 ) {

                MessageResources messages = getResources(request);

                NavBarDisplayDAO Dao = (NavBarDisplayDAO)request.getAttribute("DAO");
                if( Dao == null )
                    Dao = new NavBarDisplayDAO();

                String headingColour = request.getParameter("hC");
                if (headingColour != null){
                   Dao.setHeadingColour(headingColour);
                }

                Dao.setReloadUrl(request.getRequestURL().toString() + "?" + request.getQueryString());

                com.quatro.service.security.SecurityManager securityMgr = new com.quatro.service.security.SecurityManager();
            	if(securityMgr.hasReadAccess("_" + cmd.toLowerCase(), request.getSession().getAttribute("userrole") + "," + request.getSession().getAttribute("user"))) {


	                if( getInfo(bean,request, Dao,messages) ) {
	                    request.setAttribute("DAO",Dao);

	                    String regex = "\\b" + cmd + "\\b";
	                    String remainingCmds = params.replaceAll(regex,"").trim();

	                    //Are there more commmands to forward to or do we print what we have?
	                    if( remainingCmds.length() > 0 ) {
	                        request.setAttribute("cmd",remainingCmds);
	                        int pos = remainingCmds.indexOf(' ');
	                        if( pos > -1 )
	                            forward = remainingCmds.substring(0,pos);
	                        else
	                            forward = remainingCmds;

	                        if( Actions.get(forward) == null ){
	                        	MiscUtils.getLogger().error("forward not found, returning error");
	                            forward = "error";
	                        }
	                    } else if (isJsonRequest) {
	                        	JsonConfig config = new JsonConfig();
	                        	config.registerJsonBeanProcessor(java.sql.Date.class, new JsDateJsonBeanProcessor());
	                        	JSONObject json = JSONObject.fromObject(Dao.getMap(), config);
	                        	response.getOutputStream().write(json.toString().getBytes());
	                        	return null;
	                    } else {
	                        forward = "success";
	                    }
	                }
                } else {
                	return null;
                }
            }
        }
        if(forward != null && !forward.equals("success")) {
        	MiscUtils.getLogger().error("Forward:"+forward+" navName:"+navName+" cmd:"+cmd+" params:"+params);
        }
        return new ActionForward(Actions.get(forward));
    }

    //must be implemented by subclasses to populate dao object
    public boolean getInfo(EctSessionBean bean, HttpServletRequest request, NavBarDisplayDAO Dao, MessageResources messages) {
        return true;
    }

    //must be implemented by subclasses to retrieve module name
    public String getCmd() {
        return new String("");
    }

}
