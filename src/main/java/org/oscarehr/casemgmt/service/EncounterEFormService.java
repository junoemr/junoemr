/*
 * Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
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
 * This software was written for
 * CloudPractice Inc.
 * Victoria, British Columbia
 * Canada
 */

package org.oscarehr.casemgmt.service;

import org.oscarehr.casemgmt.dto.EncounterSectionNote;
import org.oscarehr.eform.dao.EFormDataDao;
import org.oscarehr.eform.model.EFormData;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.util.LoggedInInfo;
import org.springframework.beans.factory.annotation.Autowired;
import oscar.eform.EFormUtil;
import oscar.util.ConversionUtils;
import oscar.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EncounterEFormService extends EncounterSectionService
{
	@Autowired
	private SecurityInfoManager securityInfoManager;

	@Autowired
	private EFormDataDao eFormDataDao;

	public List<EncounterSectionNote> getNotes(LoggedInInfo loggedInInfo, String roleName, String providerNo, String demographicNo, String appointmentNo, String programId)
	{
		List<EncounterSectionNote> out = new ArrayList<>();


		//String roleName = (String)request.getSession().getAttribute("userrole") + "," + (String) request.getSession().getAttribute("user");

		if(!securityInfoManager.hasPrivilege(loggedInInfo, "_eform", "r", null)) {
			return out; //eforms link won't show up on new CME screen.
		}

		// XXX: appears to only be used in eyeform
		//String omitTypeStr = request.getParameter("omit");
		//String[] omitTypes = new String[0];
		//if(omitTypeStr!=null) {
		//	omitTypes = omitTypeStr.split(",");
		//}

		////set lefthand module heading and link
		//String winName = "eForm" + bean.demographicNo;
		//String url = "popupPage(500,950,'" + winName + "', '" + request.getContextPath() + "/eform/efmpatientformlist.jsp?demographic_no="+bean.demographicNo+"&apptProvider="+bean.getCurProviderNo()+"&appointment="+bean.appointmentNo+"&parentAjaxId=" + cmd + "')";
		//Dao.setLeftHeading(messages.getMessage(request.getLocale(), "global.eForms"));
		//Dao.setLeftURL(url);

		////set the right hand heading link
		//winName = "AddeForm" + bean.demographicNo;
		//url = "popupPage(500,950,'"+winName+"','"+request.getContextPath()+"/eform/efmformslistadd.jsp?demographic_no="+bean.demographicNo+"&appointment="+bean.appointmentNo+"&parentAjaxId="+cmd+"'); return false;";
		//Dao.setRightURL(url);
		//Dao.setRightHeadingID(cmd);  //no menu so set div id to unique id for this action

		//StringBuilder javascript = new StringBuilder("<script type=\"text/javascript\">");
		//String js = "";
		//ArrayList<HashMap<String, ? extends Object>> eForms = EFormUtil.listEForms(EFormUtil.DATE, EFormUtil.CURRENT, roleName);//EFormUtil.listEForms(EFormUtil.DATE, EFormUtil.NAME, EFormUtil.CURRENT, roleName);
		//String key;
		//int hash;
		//String BGCOLOUR = request.getParameter("hC");

		//for(int i = 0; i < eForms.size(); ++i)
		//{
		//	HashMap<String, ? extends Object> curform = eForms.get(i);
		//	winName = (String) curform.get("formName") + bean.demographicNo;
		//	hash = Math.abs(winName.hashCode());
		//	url = "popupPage(700,800,'" + hash + "','" + request.getContextPath() + "/eform/efmformadd_data.jsp?fid=" + curform.get("fid") + "&demographic_no=" + bean.demographicNo + "&appointment=" + bean.appointmentNo + "&parentAjaxId=" + cmd + "','" + curform.get("fid") + "_" + bean.demographicNo + "');";
		//	logger.debug("SETTING EFORM URL " + url);
		//	key = StringUtils.maxLenString((String) curform.get("formName"), MAX_LEN_KEY, CROP_LEN_KEY, ELLIPSES) + " (new)";
		//	key = StringEscapeUtils.escapeJavaScript(key);
		//	js = "itemColours['" + key + "'] = '" + BGCOLOUR + "'; autoCompleted['" + key + "'] = \"" + url + "\"; autoCompList.push('" + key + "');";
		//	javascript.append(js);
		//}

		//eForms.clear();

		//I've put in an arbitrary limit here of 100. Some people use a single eform/patient for
		//logging calls, etc. This makes this result set huge. People can click on the eform tab and view the full
		//history if they need to.
		List<EFormData> eFormDatas = eFormDataDao.findInstancedByDemographicId(Integer.parseInt(demographicNo), 0, 100, true);
		//EctDisplayEFormAction.filterRoles(eFormDatas, roleName);
		//Collections.sort(eFormDatas, EFormData.FORM_DATE_COMPARATOR);
		//Collections.reverse(eFormDatas);

		for (EFormData eFormData : eFormDatas)
		{
			if (eFormData.isShowLatestFormOnly() && !EFormUtil.isLatestShowLatestFormOnlyPatientForm(eFormData.getId()))
			{
				continue;
			}

			//boolean skip=false;
			//for(int x=0;x<omitTypes.length;x++)
			//{
			//	if(omitTypes[x].equals(eFormData.getFormName()))
			//	{
			//		skip=true;
			//		break;
			//	}
			//}
			//if(skip)
			//{
			//	continue;
			//}

			//NavBarDisplayDAO.Item item = NavBarDisplayDAO.Item();
			EncounterSectionNote sectionNote = new EncounterSectionNote();

			//winName = eFormData.getFormName() + bean.demographicNo;
			//hash = Math.abs(winName.hashCode());
			//url = "popupPage( 700, 800, '" + hash + "', '" + request.getContextPath() + "/eform/efmshowform_data.jsp?fdid="+eFormData.getId()+"&appointment="+bean.appointmentNo+"&parentAjaxId="+cmd+"');";
			//String formattedDate = DateUtils.formatDate(eFormData.getFormDate(),request.getLocale());
			//key = StringUtils.maxLenString(eFormData.getFormName(), MAX_LEN_KEY, CROP_LEN_KEY, ELLIPSES) + "(" + formattedDate + ")";
			//item.setLinkTitle(eFormData.getSubject());
			//key = StringEscapeUtils.escapeJavaScript(key);

			//js = "itemColours['" + key + "'] = '" + BGCOLOUR + "'; autoCompleted['" + key + "'] = \"" + url + "\"; autoCompList.push('" + key + "');";
			//javascript.append(js);
			//url += "return false;";
			//item.setURL(url);

			String strTitle = StringUtils.maxLenString(eFormData.getFormName() + ": " + eFormData.getSubject(), MAX_LEN_TITLE, CROP_LEN_TITLE, ELLIPSES);

			//item.setTitle(strTitle);
			sectionNote.setText(strTitle);

			//item.setDate(eFormData.getFormDate());
			LocalDate date = ConversionUtils.toNullableLocalDate(eFormData.getFormDate());
			//LocalDateTime date = eFormData.getFormDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
			sectionNote.setUpdateDate(date.atStartOfDay());

			//Dao.addItem(item);
			out.add(sectionNote);
		}

		//javascript.append("</script>");
		//Dao.setJavaScript(javascript.toString());


		return out;
	}
}
