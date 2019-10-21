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

import org.drools.FactException;
import org.oscarehr.casemgmt.dto.EncounterNotes;
import org.oscarehr.casemgmt.dto.EncounterSection;
import org.oscarehr.casemgmt.dto.EncounterSectionMenuItem;
import org.oscarehr.casemgmt.dto.EncounterSectionNote;
import org.oscarehr.common.dao.EncounterFormDao;
import org.oscarehr.common.model.EncounterForm;
import org.oscarehr.managers.SecurityInfoManager;
import org.springframework.beans.factory.annotation.Autowired;
import oscar.oscarEncounter.data.EctFormData;
import oscar.oscarLab.LabRequestReportLink;
import oscar.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class EncounterFormService extends EncounterSectionService
{
	private static final String SECTION_ID = "forms";
	protected static final String SECTION_TITLE_KEY = "oscarEncounter.Index.msgForms";
	protected static final String SECTION_TITLE_COLOUR = "#917611";
	protected static final String SECTION_MENU_HEADER_KEY = "oscarEncounter.LeftNavBar.AddFrm";

	@Autowired
	protected SecurityInfoManager securityInfoManager;

	@Autowired
	private EncounterFormDao encounterFormDao;

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
		return "";
	}

	@Override
	protected String getOnClickTitle(SectionParameters sectionParams)
	{
		String url = sectionParams.getContextPath() + "/oscarEncounter/formlist.jsp" +
				"?demographic_no=" + encodeUrlParam(sectionParams.getDemographicNo());

		return "popupPage(600, 700, '" + getWinName(sectionParams) + "', '" + url + "');";
	}

	@Override
	protected String getMenuId()
	{
		return SECTION_ID;
	}

	@Override
	protected String getMenuHeaderKey()
	{
		return SECTION_MENU_HEADER_KEY;
	}

	public EncounterSection getSection(
			SectionParameters sectionParams, Integer limit,
			Integer offset
	) throws FactException
	{
		// XXX: Don't like that this is duplicated code, but don't have a great way to avoid this
		//      at the moment.
		EncounterSection section = new EncounterSection();

		section.setTitle(getSectionTitle());
		section.setTitleKey(getSectionTitleKey());
		section.setColour(getSectionTitleColour());
		section.setCppIssues("");
		section.setAddUrl("");
		section.setIdentUrl("");
		section.setOnClickTitle(getOnClickTitle(sectionParams));
		section.setOnClickPlus(getOnClickPlus(sectionParams));
		section.setMenuId(getMenuId());
		section.setMenuHeaderKey(getMenuHeaderKey());
		section.setMenuItems(getMenuItems(sectionParams));

		// Share data between menu items and notes
		List<EncounterForm> encounterForms = encounterFormDao.findAll();
		Collections.sort(encounterForms, EncounterForm.BC_FIRST_COMPARATOR);

		section.setMenuItems(getMenuItems(encounterForms, sectionParams));

		EncounterNotes notes = getNotes(
				encounterForms,
				sectionParams,
				limit,
				offset);

		section.setNotes(notes.getEncounterSectionNotes());

		section.setRemainingNotes(notes.getNoteCount() - notes.getEncounterSectionNotes().size());

		return section;
	}

	protected List<EncounterSectionMenuItem> getMenuItems(List<EncounterForm> encounterForms, SectionParameters sectionParams)
	{
		List<EncounterSectionMenuItem> menuItems = new ArrayList<>();

		String winName = "AllLabs" + sectionParams.getDemographicNo();

		for(EncounterForm encounterForm: encounterForms)
		{
			// Only add unhidden forms
			if (encounterForm.isHidden())
			{
				continue;
			}

			int hash = Math.abs(winName.hashCode());
			String url = encounterForm.getFormValue() + sectionParams.getDemographicNo() +
					"&formId=0" +
					"&provNo=" + encodeUrlParam(sectionParams.getProviderNo()) +
					"&parentAjaxId=" + SECTION_ID;
			if(sectionParams.getAppointmentNo() != null)
			{
				url += "&appointmentNo=" + encodeUrlParam(sectionParams.getAppointmentNo());
			}

			addMenuItem(
					menuItems,
					encounterForm.getFormName(),
					null,
					"popupPage(700,960, '" + hash + "new','" + url + "')"
			);
		}

		return menuItems;
	}

	public EncounterNotes getNotes(List<EncounterForm> encounterForms,
								   SectionParameters sectionParams, Integer limit,
			Integer offset
	)
	{
		List<EncounterSectionNote> out = new ArrayList<>();

		if (!securityInfoManager.hasPrivilege(sectionParams.getLoggedInInfo(), "_form", "r", null))
		{
			return EncounterNotes.noNotes();
		}

		//try
		//{

			//String winName = "Forms" + bean.demographicNo;
			//StringBuilder url = new StringBuilder("popupPage(600, 700, '" + winName + "', '" + request.getContextPath() + "/oscarEncounter/formlist.jsp?demographic_no=" + bean.demographicNo + "')");

			//// set text for lefthand module title
			//Dao.setLeftHeading(messages.getMessage(request.getLocale(), "oscarEncounter.Index.msgForms"));
			//// set link for lefthand module title
			//Dao.setLeftURL(url.toString());

			//// we're going to display a pop up menu of forms so we set the menu title and id num of menu
			//Dao.setRightHeadingID(menuId);
			//Dao.setMenuHeader(messages.getMessage("oscarEncounter.LeftNavBar.AddFrm"));
			//StringBuilder javascript = new StringBuilder("<script type=\"text/javascript\">");
			//String js = "";
			String dbFormat = "yy/MM/dd";
			//String dbFormat = "yyyy-MM-dd";
			//String serviceDateStr;
			//StringBuilder strTitle;
			//String fullTitle;
			//Date date;
			//String key;
			//int hash;

			// grab all of the forms
			//EncounterFormDao encounterFormDao=(EncounterFormDao) SpringUtils.getBean("encounterFormDao");

			//List<EncounterForm> encounterForms = encounterFormDao.findAll();
			//Collections.sort(encounterForms, EncounterForm.BC_FIRST_COMPARATOR);

			//String BGCOLOUR = request.getParameter("hC");

			for (EncounterForm encounterForm : encounterForms)
			{
				EncounterSectionNote sectionNote = new EncounterSectionNote();

				if (encounterForm.getFormName().equalsIgnoreCase("Discharge Summary"))
				{
					// This is a CAISI form, so ignore it
					continue;
				}

				//winName = encounterForm.getFormName() + bean.demographicNo;

				String table = encounterForm.getFormTable();
				if (!table.equalsIgnoreCase(""))
				{
					new EctFormData();
					EctFormData.PatientForm pfrm = EctFormData.getRecentPatientForm(sectionParams.getDemographicNo(), null, table);

					// if a form has been started for the patient, create a module item for it
					if (pfrm != null)
					{
						// Date
						String dateString = pfrm.getCreated();
						DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dbFormat);
						LocalDate date = LocalDate.parse(dateString, formatter);

						sectionNote.setUpdateDate(date.atStartOfDay());

						// Title
						String fullTitle = encounterForm.getFormName();
						StringBuilder strTitle = new StringBuilder(StringUtils.maxLenString(fullTitle, MAX_LEN_TITLE, CROP_LEN_TITLE, ELLIPSES));

						if(table.equals("formLabReq07")) {
							Long reportId = null;

							HashMap<String,Object> res = LabRequestReportLink.getLinkByRequestId("formLabReq07",Long.valueOf(pfrm.getFormId()));
							reportId = (Long)res.get("report_id");

							if(reportId == null) {
								strTitle.insert(0,"*");
								strTitle.append("*");
							}
						}

						sectionNote.setText(strTitle.toString());


						// Popup Menu
						//key = StringUtils.maxLenString(fullTitle, MAX_LEN_KEY, CROP_LEN_KEY, ELLIPSES) + "(" + serviceDateStr + ")";
						//key = StringEscapeUtils.escapeJavaScript(key);

						//// auto completion arrays and colour code are set
						//js = "itemColours['" + key + "'] = '" + BGCOLOUR + "'; autoCompList.push('" + key + "'); autoCompleted['" + key + "'] = \"" + url + "\";";
						//javascript.append(js);


						// OnClick link
						int hash = Math.abs(getWinName(sectionParams).hashCode());
						String url = sectionParams.getContextPath() + "/form/forwardshortcutname.jsp" +
								"?formname=" + encodeUrlParam(encounterForm.getFormName()) +
								"&demographic_no=" + encodeUrlParam(sectionParams.getDemographicNo());

						if(pfrm.getRemoteFacilityId()!=null)
						{
							url += "&remoteFacilityId=" + encodeUrlParam(pfrm.getRemoteFacilityId().toString());
						}

						if(sectionParams.getAppointmentNo() != null)
						{
							url += "&appointmentNo=" + encodeUrlParam(sectionParams.getAppointmentNo());
						}

						url += "&formId=" + encodeUrlParam(pfrm.getFormId());


						String onClickString = "popupPage(700,960,'" + hash + "started', '" + url + "');";

						sectionNote.setOnClick(onClickString);

						//sorry I have to do this, since the "hidden" field, doesn't mean hidden.
						//this is a fix so that when they've migrated to the enhanced form, the
						//regular one is hidden. It's still accessible from the list mode off
						//the tab header though, if they really need to get to it.
						boolean dontAdd=false;
						if(table.equals("formONAR"))
						{
							//check to see if we have an enhanced one
							EctFormData.PatientForm[] pf =
									EctFormData.getPatientFormsFromLocalAndRemote(
											sectionParams.getLoggedInInfo(),
											sectionParams.getDemographicNo(),
											"formONAREnhancedRecord");

							if(pf.length > 0)
							{
								dontAdd=true;
							}
						}

						if(!dontAdd)
						{
							//Dao.addItem(item);
							out.add(sectionNote);
						}
					}
				}

				// we add all unhidden forms to the pop up menu
				//if (!encounterForm.isHidden())
				//{
				//	hash = Math.abs(winName.hashCode());
				//	url = new StringBuilder("popupPage(700,960,'" + hash + "new', '" + encounterForm.getFormValue() + bean.demographicNo + "&formId=0&provNo=" + bean.providerNo + "&parentAjaxId=" + cmd + ((appointmentNo!=null)?"&appointmentNo="+ appointmentNo:"") +"')");
				//	Dao.addPopUpUrl(url.toString());
				//	key = StringUtils.maxLenString(encounterForm.getFormName(), MAX_LEN_KEY, CROP_LEN_KEY, ELLIPSES) + " (new)";
				//	Dao.addPopUpText(encounterForm.getFormName());
				//	key = StringEscapeUtils.escapeJavaScript(key);

				//	// auto completion arrays and colour code are set
				//	js = "itemColours['" + key + "'] = '" + BGCOLOUR + "'; autoCompList.push('" + key + "'); autoCompleted['" + key + "'] = \"" + url + ";\";";
				//	javascript.append(js);
				//}
			}
			//url = new StringBuilder("return !showMenu('" + menuId + "', event);");
			//Dao.setRightURL(url.toString());

			//javascript.append("</script>");
			//Dao.setJavaScript(javascript.toString());

			//// sort module items, i.e. forms, from most recently started to more distant
			//Dao.sortItems(NavBarDisplayDAO.DATESORT_ASC);
		//} catch (Exception e) {
		//	logger.error("EctDisplayFormAction SQL ERROR:", e);
		//	return false;
		//}

		return EncounterNotes.limitedEncounterNotes(out, offset, limit);
	}

	private String getWinName(SectionParameters sectionParams)
	{
		return "Forms" + sectionParams.getDemographicNo();
	}
}
