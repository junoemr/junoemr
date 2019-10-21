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

import org.oscarehr.casemgmt.dto.EncounterNotes;
import org.oscarehr.casemgmt.dto.EncounterSectionNote;
import org.oscarehr.eform.dao.EFormDataDao;
import org.oscarehr.eform.model.EFormData;
import org.oscarehr.managers.SecurityInfoManager;
import org.springframework.beans.factory.annotation.Autowired;
import oscar.eform.EFormUtil;
import oscar.util.ConversionUtils;
import oscar.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EncounterEFormService extends EncounterSectionService
{
	public static final String SECTION_ID = "eforms";
	protected static final String SECTION_TITLE_KEY = "global.eForms";
	protected static final String SECTION_TITLE_COLOUR = "#008000";

	@Autowired
	private SecurityInfoManager securityInfoManager;

	@Autowired
	private EFormDataDao eFormDataDao;

	@Override
	public String getSectionId()
	{
		return SECTION_ID;
	}

	@Override
	protected String getSectionTitleKey()
	{
		return SECTION_TITLE_KEY;
	}

	@Override
	protected String getSectionTitleColour()
	{
		return SECTION_TITLE_COLOUR;
	}

	@Override
	protected String getOnClickPlus(SectionParameters sectionParams)
	{
		String winName = "AddeForm" + sectionParams.getDemographicNo();

		String url = sectionParams.getContextPath() + "/eform/efmformslistadd.jsp" +
				"?demographic_no=" + encodeUrlParam(sectionParams.getDemographicNo()) +
				"&appointment=" + encodeUrlParam(sectionParams.getAppointmentNo()) +
				"&parentAjaxId=" + SECTION_ID;

		return "popupPage(500,950,'" + winName + "','" + url +"');";
	}

	@Override
	protected String getOnClickTitle(SectionParameters sectionParams)
	{
		String winName = "eForm" + sectionParams.getDemographicNo();
		String url = sectionParams.getContextPath() + "/eform/efmpatientformlist.jsp" +
				"?demographic_no=" + encodeUrlParam(sectionParams.getDemographicNo()) +
				"&apptProvider=" + encodeUrlParam(sectionParams.getProviderNo()) +
				"&appointment=" + encodeUrlParam(sectionParams.getAppointmentNo()) +
				"&parentAjaxId=" + SECTION_ID;

		return "popupPage(500,950,'" + winName + "', '" + url + "')";
	}

	public EncounterNotes getNotes(
			SectionParameters sectionParams, Integer limit,
			Integer offset
	)
	{
		List<EncounterSectionNote> out = new ArrayList<>();

		if(!securityInfoManager.hasPrivilege(sectionParams.getLoggedInInfo(), "_eform", "r", null))
		{
			return EncounterNotes.noNotes();
		}

		// Popup menu
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
		List<EFormData> eFormDatas = eFormDataDao.findInstancedByDemographicId(
				Integer.parseInt(sectionParams.getDemographicNo()), 0, 100, true);

		for (EFormData eFormData : eFormDatas)
		{
			if (eFormData.isShowLatestFormOnly() && !EFormUtil.isLatestShowLatestFormOnlyPatientForm(eFormData.getId()))
			{
				continue;
			}

			EncounterSectionNote sectionNote = new EncounterSectionNote();

			String winName = eFormData.getFormName() + sectionParams.getDemographicNo();
			int hash = Math.abs(winName.hashCode());

			String url = sectionParams.getContextPath() + "/eform/efmshowform_data.jsp" +
					"?fdid=" + encodeUrlParam(eFormData.getId().toString()) +
					"&appointment=" + encodeUrlParam(sectionParams.getAppointmentNo()) +
					"&parentAjaxId=" + SECTION_ID;

			String onClickString = "popupPage( 700, 800, '" + hash + "', '" + url +"');";

			sectionNote.setOnClick(onClickString);

			//String formattedDate = DateUtils.formatDate(eFormData.getFormDate(),request.getLocale());
			//key = StringUtils.maxLenString(eFormData.getFormName(), MAX_LEN_KEY, CROP_LEN_KEY, ELLIPSES) + "(" + formattedDate + ")";
			//item.setLinkTitle(eFormData.getSubject());
			//key = StringEscapeUtils.escapeJavaScript(key);

			//js = "itemColours['" + key + "'] = '" + BGCOLOUR + "'; autoCompleted['" + key + "'] = \"" + url + "\"; autoCompList.push('" + key + "');";
			//javascript.append(js);

			String strTitle = StringUtils.maxLenString(eFormData.getFormName() + ": " + eFormData.getSubject(), MAX_LEN_TITLE, CROP_LEN_TITLE, ELLIPSES);

			//item.setTitle(strTitle);
			sectionNote.setText(strTitle);

			LocalDate date = ConversionUtils.toNullableLocalDate(eFormData.getFormDate());
			sectionNote.setUpdateDate(date.atStartOfDay());

			out.add(sectionNote);
		}

		//javascript.append("</script>");
		//Dao.setJavaScript(javascript.toString());


		return EncounterNotes.limitedEncounterNotes(out, offset, limit);
	}
}
