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


/*
 * 
 */
package oscar.appt;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.oscarehr.common.model.AppointmentStatus;
import org.oscarehr.managers.AppointmentManager;

import org.oscarehr.util.SpringUtils;
import oscar.OscarProperties;


/**
 * Class ApptStatusData : set appt status and get the icon name and link
 * 2003-01-11
 */
public final class ApptStatusData {

	private OscarProperties props = OscarProperties.getInstance();
	private String strEditable = props.getProperty("ENABLE_EDIT_APPT_STATUS");

	private HashMap<String, String> titleMap = new HashMap<String, String>();
	private AppointmentManager apptManager = SpringUtils.getBean(AppointmentManager.class);
	private String apptStatus;
	private AppointmentStatus statusData;
	private List<AppointmentStatus> allStatus;

	public ApptStatusData() {
		setTitleMap();
		setAllStatus();
	}
	public ApptStatusData(String status) {
		setTitleMap();
		setApptStatus(status);
		setAllStatus();
	}

	private void setTitleMap() {
		titleMap.put("t", "oscar.appt.ApptStatusData.msgTodo");
		titleMap.put("T", "oscar.appt.ApptStatusData.msgDaySheetPrinted");
		titleMap.put("H", "oscar.appt.ApptStatusData.msgHere");
		titleMap.put("P", "oscar.appt.ApptStatusData.msgPicked");
		titleMap.put("E", "oscar.appt.ApptStatusData.msgEmpty");
		titleMap.put("N", "oscar.appt.ApptStatusData.msgNoShow");
		titleMap.put("C", "oscar.appt.ApptStatusData.msgCanceled");
		titleMap.put("B", "oscar.appt.ApptStatusData.msgBilled");
	}

	private void setAllStatus() {
		allStatus = apptManager.getAppointmentStatuses();
	}

	private void setThisStatus() {
		// assume apptStatus not null
		// use only the first character in the status since second character denotes Signed/Verified etc.
		// and is not included in the appointment_status table
		String baseStatus = apptStatus.substring(0, 1);
		statusData = apptManager.findByStatus(baseStatus);
	}

	public void setApptStatus(String status) {
		apptStatus = status;
		setThisStatus();
	}


	public String getImageName() {
		String baseIcon;
		// icon images exists for all statuses including up to 11 custom statuses
		if(statusData != null)
		{
			baseIcon = statusData.getIcon();
		}
		else
		{
			return null;
		}
		String status = apptStatus;
		String returnIcon = baseIcon;

		if(status.length() >= 2 && !status.equalsIgnoreCase("null")) {
			String otherIcon = status.substring(1,2);
			returnIcon = otherIcon + baseIcon;
		}
		return returnIcon;
	}

	public String getNextStatus() {

		int currentStatusIndex;
		int nextStatusIndex;

		if(statusData != null)
		{
			currentStatusIndex = allStatus.indexOf(statusData);
			nextStatusIndex = getNextStatusIndex(currentStatusIndex);
		}
		else {
			nextStatusIndex = 0;
		}
		String currentStatus = apptStatus;
		String nextStatus = allStatus.get(nextStatusIndex).getStatus();

		if(currentStatus.charAt(0) == 'B') {
			return currentStatus;
		}

		if(nextStatus.charAt(0) == 'B') {
			nextStatusIndex = getNextStatusIndex(nextStatusIndex);
			nextStatus = allStatus.get(nextStatusIndex).getStatus();
		}

		// have to preserver billed/verified status
		if(currentStatus.length() >= 2 && !currentStatus.equalsIgnoreCase("null")) {
			nextStatus = nextStatus.charAt(0) + currentStatus.substring(1);
		}

		return nextStatus;
	}

	private int getNextStatusIndex(int currentStatus) {

		return (currentStatus + 1) % allStatus.size();
	}

	public String getTitle() {

		String title = statusData.getDescription();
		if(apptStatus.length() >= 2) {
			String other = apptStatus.substring(1,2);
			title += "/" + (other.equals("S")? "Signed":"Verified");
		}
		return title;
	}

	/**
	 * Converts the title which is the reference to the resource file to the actual value for this locale
	 *
	 * @return String
	 */
	public String getTitleString(Locale locale) {
		ResourceBundle bundle = ResourceBundle.getBundle("oscarResources", locale);
		String status;
		if(statusData != null)
		{
			status = statusData.getStatus();
		}
		else {
			return null;
		}
		String localeTitle = getTitle();

		if(bundle != null && !strEditable.equalsIgnoreCase("yes")) {
			if(titleMap.containsKey(status)) {
				localeTitle = bundle.getString(titleMap.get(status));
			}
		}

		return localeTitle;
	}

	public String getBgColor() {
		if (statusData == null)
		{
			return null;
		} else
		{
			return statusData.getColor();
		}
	}

	/**
	 *  Pulls in the short letters which represent the appointment status.
	 *
	 *	author Trimara Corp.
	 *	@return Short letters or null
	 *
	 **/
	public String getShortLetters(){
		return statusData.getShortLetters();
	}

	/**
	 * Pulls in the colour for the short letters of the appointment.
	 *
	 * author Trimara Corp.
	 * @return An integer representing the hex code for the colour. Null if there is no colour.
	 *
	 **/
	public String getShortLetterColour(){
		return statusData.getShortLetterColour();
	}

	public String signStatus() {
		return appendStatus(apptStatus, "S");
	}

	public String verifyStatus() {
		return appendStatus(apptStatus, "V");
	}

	public String billStatus(String fstatus) {
		return preStatus(fstatus, "B");
	}

	public String unbillStatus(String fstatus) {
		return preStatus(fstatus, "P");
	}

	public String[] getAllStatus() {
		String[] rStatus = new String[allStatus.size()];
		int idx = 0;
		for(AppointmentStatus status : allStatus) {
			rStatus[idx] = status.getStatus();
			idx++;
		}
		return rStatus;
	}

	public String[] getAllTitle() {
		String[] rStatus = new String[allStatus.size()];
		int idx = 0;
		for(AppointmentStatus status : allStatus) {
			rStatus[idx] = status.getDescription();
			idx++;
		}
		return rStatus;
	}

	private String appendStatus(String status, String s) {
		String temp = "";
		if (status != null) {
			if (status.length() == 1) {
				temp = status + s;
			} else {
				temp = status.substring(0, 1) + s;
			}
		}
		return temp;
	}

	private String preStatus(String status, String s) {
		String temp = "";
		if (status != null && !status.isEmpty()) {
			if (status.length() == 1) {
				temp = s;
			} else {
				temp = s + status.substring(1, 2);
			}
		}
		return temp;
	}

}
