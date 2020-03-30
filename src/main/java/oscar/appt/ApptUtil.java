/**
 *
 * Copyright (c) 2005-2012. Centre for Research on Inner City Health, St. Michael's Hospital, Toronto. All Rights Reserved.
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
 * Centre for Research on Inner City Health, St. Michael's Hospital,
 * Toronto, Ontario, Canada
 */

package oscar.appt;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.oscarehr.common.model.Site;

/**
 * This class contains Appointment related presentation layer helper methods.
 * 
 * @author Eugene Petruhin
 * 
 */
public class ApptUtil {

	private static final String SESSION_APPT_BEAN = "apptBean";

	public static final String APPOINTMENT_OP_TYPE_ID = "APPOINTMENT_OP_TYPE";
	public enum APPOINTMENT_OP_TYPE {
		NONE,
		COPY,
		CUT
	}

	public static void copyAppointmentIntoSession(HttpServletRequest request, APPOINTMENT_OP_TYPE opType)
	{
		ApptData obj = new ApptData();
		obj.setAppointmentNo(request.getParameter("appointment_no"));
		obj.setAppointment_date(request.getParameter("appointment_date"));
		obj.setStart_time(request.getParameter("start_time"));
		obj.setEnd_time(request.getParameter("end_time"));
		obj.setName(request.getParameter("keyword"));
		obj.setDemographic_no(request.getParameter("demographic_no"));
		obj.setNotes(request.getParameter("notes"));
		obj.setReason(request.getParameter("reason"));
		obj.setLocation(request.getParameter("location"));
		obj.setResources(request.getParameter("resources"));
		obj.setType(request.getParameter("type"));
		obj.setStyle(request.getParameter("style"));
		obj.setBilling(request.getParameter("billing"));
		obj.setStatus(request.getParameter("status"));
		obj.setRemarks(request.getParameter("remarks"));
		obj.setDuration(request.getParameter("duration"));
		obj.setChart_no(request.getParameter("chart_no"));
		obj.setUrgency(request.getParameter("urgency"));
		obj.setVirtual(request.getParameter("isVirtual"));
		// set up session bean
		request.getSession().setAttribute(SESSION_APPT_BEAN, obj);
		request.getSession().setAttribute(APPOINTMENT_OP_TYPE_ID, opType);
	}

	public static ApptData getAppointmentFromSession(HttpServletRequest request) {
		return (ApptData) request.getSession().getAttribute(SESSION_APPT_BEAN);
	}

	/**
	 * pull the current operation type from the session
	 * @param request - session
	 * @return - current operation type
	 */
	public static APPOINTMENT_OP_TYPE getOperationTypeFromSession(HttpServletRequest request)
	{
		return (APPOINTMENT_OP_TYPE) request.getSession().getAttribute(APPOINTMENT_OP_TYPE_ID);
	}

	/**
	 * get APPOINTMENT_OP_TYPE from string. If the string does not match a known type then op type, NONE is returned.
	 * @param opType - the string to decode
	 * @return - the operation type
	 */
	public static APPOINTMENT_OP_TYPE stringToOperationType(String opType)
	{
		try
		{
			return APPOINTMENT_OP_TYPE.valueOf(opType);
		}
		catch(IllegalArgumentException e)
		{
			return APPOINTMENT_OP_TYPE.NONE;
		}
	}

	public static String getColorFromLocation(String site, String colo, String loca) {
		String ret = "white";
		String[] s = site.split("\\|");
		String[] c = colo.split("\\|");
		for (int i = 0; i < s.length; i++) {
			if (s[i].startsWith(loca)) {
				ret = c[i];
				break;
			}
		}
		return ret;
	}
	
	public static String getColorFromLocation(List<Site> sites, String siteName) {
		for (Site s:sites) {
			if (s.getName().equals(siteName))
				return s.getBgColor();
		}
		return "white";
	}
	
	public static String getShortNameFromLocation(List<Site> sites, String siteName) {
		for (Site s:sites) {
			if (s.getName().equals(siteName))
				return s.getShortName();
		}
		return "";
	}
	
	public static Site getSiteFromName(List<Site> sites, String siteName) {
		for (Site s:sites) {
			if (s.getName().equals(siteName))
				return s;
		}
		return null;
	}
}
