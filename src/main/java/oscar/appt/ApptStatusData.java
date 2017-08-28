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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.oscarehr.common.model.AppointmentStatus;
import org.oscarehr.managers.AppointmentManager;

import org.oscarehr.util.SpringUtils;


/**
 * Class ApptStatusData : set appt status and get the icon name and link
 * 2003-01-11
 */
public final class ApptStatusData {

	private AppointmentManager apptManager = SpringUtils.getBean(AppointmentManager.class);
	private String apptStatus;
	private AppointmentStatus thisStatus;
	private List<AppointmentStatus> allStatus;

	public ApptStatusData() {}


	public ApptStatusData(String status) {
		setApptStatus(status);
		setThisStatus();
		setAllStatus();
	}

	public void setAllStatus() {
		allStatus = apptManager.getAppointmentStatuses();
	}

	public void setThisStatus() {
		thisStatus = apptManager.findByStatus(apptStatus);
	}

    public void setApptStatus(String status) {
        apptStatus = status;
    }


    public String getImageName() {
        return thisStatus.getIcon();
    }

    public String getNextStatus() {
    	int currentStatus = allStatus.indexOf(thisStatus);
    	String nextStatus;

    	if(thisStatus.getStatus().charAt(0) != 'B') {
    		if(allStatus.get(currentStatus + 1).getStatus().charAt(0) == 'B') {
    			nextStatus = allStatus.get(0).getStatus();
			} else {
				nextStatus = allStatus.get(currentStatus + 1).getStatus();
			}
		} else {
    		nextStatus = thisStatus.getStatus();
		}

		return nextStatus;
    }

    public String getTitle() {
        return thisStatus.getDescription();
    }

    /**
     * Converts the title which is the reference to the resource file to the actual value for this locale
     *
     * @return String
     */
    public String getTitleString(Locale locale) {
    	return thisStatus.getDescription();
    }

    public String getBgColor() {
        return thisStatus.getColor();
    }

	/**
	 *  Pulls in the short letters which represent the appointment status.
	 *
	 *	author Trimara Corp.
	 *	@return Short letters or null
	 *
	 **/
	public String getShortLetters(){
		return thisStatus.getShortLetters();
	}

	/**
	 * Pulls in the colour for the short letters of the appointment.
	 *
	 * author Trimara Corp.
	 * @return An integer representing the hex code for the colour. Null if there is no colour.
	 *
	 **/
	public String getShortLetterColour(){
		return thisStatus.getShortLetterColour();
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
		List<String> rStatuses = new ArrayList<String>();
		for(AppointmentStatus status : allStatus) {
			rStatuses.add(status.getStatus());
		}
		String[] rStatusArray = rStatuses.toArray(new String[0]);
		return rStatusArray;
    }

    public String[] getAllTitle() {
		List<String> rTitle = new ArrayList<String>();
		for(AppointmentStatus status : allStatus) {
			rTitle.add(status.getDescription());
		}
		String[] rStatusArray = rTitle.toArray(new String[0]);
		return rStatusArray;
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
