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
import org.oscarehr.common.dao.UserPropertyDAO;
import org.oscarehr.common.model.UserProperty;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import oscar.util.ConversionUtils;
import oscar.util.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EncounterConsultationService extends EncounterSectionService
{
	@Autowired
	private SecurityInfoManager securityInfoManager;

	@Autowired
	private UserPropertyDAO userPropertyDAO;

	public List<EncounterSectionNote> getNotes(LoggedInInfo loggedInInfo, String roleName, String providerNo, String demographicNo, String appointmentNo, String programId)
	{
		List<EncounterSectionNote> out = new ArrayList<>();

		if(!securityInfoManager.hasPrivilege(loggedInInfo, "_con", "r", null)) {
			return out; //Consultations link won't show up on new CME screen.
		}

		//set lefthand module heading and link
		//String winName = "Consultation" + bean.demographicNo;
		//String url = "popupPage(700,960,'" + winName + "','" + request.getContextPath() + "/oscarEncounter/oscarConsultationRequest/DisplayDemographicConsultationRequests.jsp?de=" + bean.demographicNo + "')";
		//Dao.setLeftHeading(messages.getMessage(request.getLocale(), "oscarEncounter.LeftNavBar.Consult"));
		//Dao.setLeftURL(url);

		////set the right hand heading link\
		//winName = "newConsult" + bean.demographicNo;
		//url = "popupPage(700,960,'" + winName + "','" + request.getContextPath() + "/oscarEncounter/oscarConsultationRequest/ConsultationFormRequest.jsp?de=" + bean.demographicNo + "&teamVar=&appNo="+appointmentNo+"'); return false;";
		//Dao.setRightURL(url);
		//Dao.setRightHeadingID(cmd);  //no menu so set div id to unique id for this action

		//grab all consultations for patient and add list item for each
		oscar.oscarEncounter.oscarConsultationRequest.pageUtil.EctViewConsultationRequestsUtil theRequests;
		theRequests = new  oscar.oscarEncounter.oscarConsultationRequest.pageUtil.EctViewConsultationRequestsUtil();
		theRequests.estConsultationVecByDemographic(loggedInInfo, demographicNo);

		//determine cut off period for highlighting
		//UserPropertyDAO userPropertyDAO = (UserPropertyDAO) WebApplicationContextUtils.getWebApplicationContext(request.getSession().getServletContext()).getBean("UserPropertyDAO");

		UserProperty up = userPropertyDAO.getProp(providerNo, UserProperty.CONSULTATION_TIME_PERIOD_WARNING);
		String timeperiod = null;

		if ( up != null && up.getValue() != null && !up.getValue().trim().equals(""))
		{
			timeperiod = up.getValue();
		}

		Calendar cal = Calendar.getInstance();
		int countback = -1;
		if (timeperiod != null)
		{
			countback = Integer.parseInt(timeperiod);
			countback = countback * -1;
		}
		cal.add(Calendar.MONTH, countback);
		Date cutoffDate = cal.getTime();

		String red = "red";
		String dbFormat = "yyyy-MM-dd";
		for (int idx = theRequests.ids.size() - 1; idx >= 0; --idx )
		{
			EncounterSectionNote sectionNote = new EncounterSectionNote();

			//NavBarDisplayDAO.Item item = NavBarDisplayDAO.Item();
			String service = theRequests.service.get(idx);
			String dateStr = theRequests.date.get(idx);
			String status = theRequests.status.get(idx);
			DateFormat formatter = new SimpleDateFormat(dbFormat);

			Date date;
			//String serviceDateStr;
			try
			{
				date = formatter.parse(dateStr);
				//serviceDateStr = DateUtils.formatDate(date, request.getLocale());
				//if we are after cut off date and not completed set to red
				if( date.before(cutoffDate) && !status.equals("4") )
				{
					//item.setColour(red);
					sectionNote.setColour(red);
				}
			}
			catch(ParseException ex ) {
				MiscUtils.getLogger().debug("EctDisplayConsultationAction: Error creating date " + ex.getMessage());
				//serviceDateStr = "Error";
				date = null;
			}
			//url = "popupPage(700,960,'" + winName + "','" + request.getContextPath() + "/oscarEncounter/ViewRequest.do?de=" + bean.demographicNo + "&requestId=" + theRequests.ids.get(idx) + "'); return false;";

			//item.setLinkTitle(service + " " + serviceDateStr);

			String title = StringUtils.maxLenString(service, MAX_LEN_TITLE, CROP_LEN_TITLE, ELLIPSES);
			//item.setTitle(title);

			sectionNote.setText(title);
			sectionNote.setUpdateDate(ConversionUtils.toNullableLocalDate(date).atStartOfDay());

			out.add(sectionNote);

			//item.setURL(url);
			//item.setDate(date);
			//Dao.addItem(item);
		}

		return out;
	}
}
