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

	public ApptStatusData() {}


	public ApptStatusData(String status) {
		setTitleMap();
		setApptStatus(status);
		setThisStatus();
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
		statusData = apptManager.findByStatus(apptStatus);
	}

	public void setApptStatus(String status) {
		apptStatus = status;
	}


	public String getImageName() {
		return statusData.getIcon();
	}

	public String getNextStatus() {
		int currentStatusIndex = allStatus.indexOf(statusData);
		String nextStatus;

		// Return first status if current status is the last status
		if(isLastStatus(currentStatusIndex) && statusData.getStatus().charAt(0) != 'B') {
			return allStatus.get(0).getStatus();
		}

		// If status is 'B', don't allow it to be changed from cycle
		if(statusData.getStatus().charAt(0) == 'B') {
			return statusData.getStatus();
		} else {
			nextStatus = allStatus.get(currentStatusIndex + 1).getStatus();

			// If next status is 'B' and the last status return the first status
			if(nextStatus.charAt(0) == 'B' && isLastStatus(currentStatusIndex + 1)) {
				return allStatus.get(0).getStatus();

			// If next status is 'B', and not the last status, skip it
			} else if (nextStatus.charAt(0) == 'B'){
				nextStatus = allStatus.get(currentStatusIndex + 2).getStatus();
			}
		}

		return nextStatus;
	}

	private boolean isLastStatus(int currentStatus) {
		int currentStatusPos = currentStatus + 1;
		if(currentStatusPos % allStatus.size() == 0) {
			return true;
		}
		return false;
	}

	public String getTitle() {
		return statusData.getDescription();
	}

	/**
	 * Converts the title which is the reference to the resource file to the actual value for this locale
	 *
	 * @return String
	 */
	public String getTitleString(Locale locale) {
		ResourceBundle bundle = ResourceBundle.getBundle("oscarResources", locale);
		String status = statusData.getStatus();
		String localeTitle = getTitle();

		if(bundle != null && !strEditable.equalsIgnoreCase("yes")) {
			if(titleMap.containsKey(status)) {
				localeTitle = bundle.getString(titleMap.get(status));
			}
		}

		return localeTitle;
	}

	public String getBgColor() {
		return statusData.getColor();
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
			rStatus[idx] = status.getDescription();
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
		if (status != null) {
			if (status.length() == 1) {
				temp = s;
			} else {
				temp = s + status.substring(1, 2);
			}
		}
		return temp;
	}

}
