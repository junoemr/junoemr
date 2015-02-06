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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.apache.struts.util.MessageResources;
import org.oscarehr.common.dao.EFormDataDao;
import org.oscarehr.common.model.EFormData;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;

import oscar.eform.EFormUtil;
import oscar.util.DateUtils;
import oscar.util.OscarRoleObjectPrivilege;
import oscar.util.StringUtils;

import oscar.OscarProperties;

//import oscar.oscarSecurity.CookieSecurity;

public class EctDisplayEFormAction extends EctDisplayAction {
    private static Logger logger = MiscUtils.getLogger();
    //private final static String BGCOLOUR = "11CC00";
    private String cmd = "eforms";
    
  public boolean getInfo(EctSessionBean bean, HttpServletRequest request, NavBarDisplayDAO Dao, MessageResources messages) {                                                                                                  
	try
	{
		boolean a = true;
		Vector v = OscarRoleObjectPrivilege.getPrivilegeProp("_newCasemgmt.eForms");
		String roleName = (String)request.getSession().getAttribute("userrole") + "," + (String) request.getSession().getAttribute("user");
		a = OscarRoleObjectPrivilege.checkPrivilege(roleName, (Properties) v.get(0), (Vector) v.get(1));
		if(!a) {
			 return true; //eforms link won't show up on new CME screen.
		} else {
			
		 	String omitTypeStr = request.getParameter("omit");
		 	String[] omitTypes = new String[0];
		 	if(omitTypeStr!=null) {
		 		omitTypes = omitTypeStr.split(",");
		 	}
		      
	        //set lefthand module heading and link
	        String winName = "eForm" + bean.demographicNo;
	        String url = "popupPage(500,950,'" + winName + "', '" + request.getContextPath() + "/eform/efmpatientformlist.jsp?demographic_no="+bean.demographicNo+"&apptProvider="+bean.getCurProviderNo()+"&appointment="+bean.appointmentNo+"&parentAjaxId=" + cmd + "')";
	        Dao.setLeftHeading(messages.getMessage(request.getLocale(), "global.eForms"));
	        Dao.setLeftURL(url);
	        
	        //set the right hand heading link
	        winName = "AddeForm" + bean.demographicNo;
	        url = "popupPage(500,950,'"+winName+"','"+request.getContextPath()+"/eform/efmformslistadd.jsp?demographic_no="+bean.demographicNo+"&appointment="+bean.appointmentNo+"&parentAjaxId="+cmd+"'); return false;";
	        Dao.setRightURL(url);        
	        Dao.setRightHeadingID(cmd);  //no menu so set div id to unique id for this action      
	
	        StringBuilder javascript = new StringBuilder("<script type=\"text/javascript\">");        
	        String js = ""; 
	        ArrayList<HashMap<String, ? extends Object>> eForms = EFormUtil.listEForms(EFormUtil.DATE, EFormUtil.CURRENT, roleName);//EFormUtil.listEForms(EFormUtil.DATE, EFormUtil.NAME, EFormUtil.CURRENT, roleName);
	        String key;
	        int hash;
	        String BGCOLOUR = request.getParameter("hC");
	        for( int i = 0; i < eForms.size(); ++i ) {
	        	HashMap<String, ? extends Object> curform = eForms.get(i);
	            winName = (String)curform.get("formName") + bean.demographicNo;            
	            hash = Math.abs(winName.hashCode());
	            url = "popupPage(700,800,'"+hash+"','"+request.getContextPath()+"/eform/efmformadd_data.jsp?fid="+curform.get("fid")+"&demographic_no="+bean.demographicNo+"&appointment="+bean.appointmentNo+"&parentAjaxId="+cmd+"','FormA"+i+"');";
	            logger.debug("SETTING EFORM URL " + url);
	            key = StringUtils.maxLenString((String)curform.get("formName"), MAX_LEN_KEY, CROP_LEN_KEY, ELLIPSES) + " (new)";
	            key = StringEscapeUtils.escapeJavaScript(key);
	            js = "itemColours['" + key + "'] = '" + BGCOLOUR + "'; autoCompleted['" + key + "'] = \"" + url + "\"; autoCompList.push('" + key + "');";
	            javascript.append(js);
	        }
	        
	        eForms.clear();
	
			EFormDataDao eFormDataDao=(EFormDataDao)SpringUtils.getBean("EFormDataDao");
			
			List<EFormData> eFormDatas = new ArrayList<EFormData>();
			if(OscarProperties.getInstance().isGroupedEFormEnabled()){
				eFormDatas=eFormDataDao.findByDemographicIdCurrentPatientIndependentGroupByFormId(new Integer(bean.demographicNo), true, false);
			}else{
				eFormDatas=eFormDataDao.findByDemographicIdCurrentPatientIndependent(new Integer(bean.demographicNo), true, false);
			}
			
			filterRoles(eFormDatas, roleName);
			Collections.sort(eFormDatas, EFormData.FORM_DATE_COMPARATOR);
			Collections.reverse(eFormDatas);
	
			for (EFormData eFormData : eFormDatas)
			{
				boolean skip=false;
		        for(int x=0;x<omitTypes.length;x++) {
		        	if(omitTypes[x].equals(eFormData.getFormName())) {
		        		skip=true;
		        		break;
		        	}
		        }
		        if(skip)
		        	continue;
		        
	            NavBarDisplayDAO.Item item = NavBarDisplayDAO.Item();
	            winName = eFormData.getFormName() + bean.demographicNo;            
	            hash = Math.abs(winName.hashCode());            
	            url = "popupPage( 700, 800, '" + hash + "', '" + request.getContextPath() + "/eform/efmshowform_data.jsp?fdid="+eFormData.getId()+"&appointment="+bean.appointmentNo+"&parentAjaxId="+cmd+"');";
	            String formattedDate = DateUtils.formatDate(eFormData.getFormDate(),request.getLocale());
	            key = StringUtils.maxLenString(eFormData.getFormName(), MAX_LEN_KEY, CROP_LEN_KEY, ELLIPSES) + "(" + formattedDate + ")";
	            item.setLinkTitle(eFormData.getSubject());
	            key = StringEscapeUtils.escapeJavaScript(key);
	            js = "itemColours['" + key + "'] = '" + BGCOLOUR + "'; autoCompleted['" + key + "'] = \"" + url + "\"; autoCompList.push('" + key + "');";
	            javascript.append(js);                
	            url += "return false;";
	            item.setURL(url);
	            String strTitle = StringUtils.maxLenString(eFormData.getFormName(), MAX_LEN_TITLE, CROP_LEN_TITLE, ELLIPSES);
	            item.setTitle(strTitle);  
	            item.setDate(eFormData.getFormDate());
	            Dao.addItem(item);
	        }
	                        
	        javascript.append("</script>");
	        Dao.setJavaScript(javascript.toString());
	
	        return true;
		}
	}
	catch (Exception e)
	{
		logger.error("Unexpected error", e);
		throw(new RuntimeException(e));
	}
  }
  
  public String getCmd() {
        return cmd;
  }
  
	public static List<EFormData> filterRoles(List<EFormData> eFormDatas, String roleName) {
		ArrayList<EFormData> filteredResults = new ArrayList<EFormData>();

		for (EFormData eFormData : eFormDatas) {
			if (eFormData.getRoleType() != null && !eFormData.getRoleType().equals("")) {
				// ojectName: "_admin,_admin.eform"
				// roleName: "doctor,admin"
				String objectName = "_eform." + eFormData.getRoleType();
				Vector v = OscarRoleObjectPrivilege.getPrivilegeProp(objectName);
				if (!OscarRoleObjectPrivilege.checkPrivilege(roleName, (Properties) v.get(0), (Vector) v.get(1))) {
					continue;
				}
			}
			
			filteredResults.add(eFormData);
		}

		return (filteredResults);
	}
}
