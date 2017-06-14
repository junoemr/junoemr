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

package oscar.oscarWaitingList.bean;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.oscarehr.common.dao.WaitingListDao;
import org.oscarehr.common.dao.WaitingListNameDao;
import org.oscarehr.common.model.Appointment;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.common.model.WaitingList;
import org.oscarehr.common.model.WaitingListName;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;

import oscar.oscarWaitingList.util.WLWaitingListUtil;
import oscar.util.ConversionUtils;

public class WLWaitingListBeanHandler {
	
	private static WaitingListDao waitingListDao = SpringUtils.getBean(WaitingListDao.class);
	private static WaitingListNameDao waitingListNameDao = SpringUtils.getBean(WaitingListNameDao.class);
	private static Logger logger = MiscUtils.getLogger();

	List<WLPatientWaitingListBean> waitingListArrayList = new ArrayList<WLPatientWaitingListBean>();
	private String waitingListName = "";

	public WLWaitingListBeanHandler(String waitingListID) {
		init(waitingListID);
	}
	public WLWaitingListBeanHandler(Integer waitingListID) {
		init(waitingListID);
	}
	public boolean init(String waitingListID) {
		return init(ConversionUtils.fromIntString(waitingListID));
	}
	public boolean init(Integer waitingListID) {
		if(waitingListID == null || waitingListID <= 0) {
			logger.error("Invalid Wait List Id: " + waitingListID);
			return false;
		}
		List<WaitingList> waitingListEntries = waitingListDao.findByWaitingListId(waitingListID);

		String onListSinceDateOnly = "";
		for (WaitingList entry : waitingListEntries) {
			Demographic demographic = entry.getDemographic();

			onListSinceDateOnly = ConversionUtils.toDateString(entry.getOnListSince());
			
			WaitingListName waitingListName = entry.getWaitingListName();
			Integer waitingListNameId = (waitingListName == null) ? 0 : waitingListName.getId();

			WLPatientWaitingListBean wLBean = new WLPatientWaitingListBean(String.valueOf(demographic.getDemographicNo()),
	        String.valueOf(waitingListNameId),
	        String.valueOf(entry.getPosition()),
	        demographic.getFullName(),
	        demographic.getPhone(),
	        entry.getNote(),
	        onListSinceDateOnly);
			waitingListArrayList.add(wLBean);
		}

		WaitingListName name = waitingListNameDao.find(waitingListID);
		if (name != null) {
			waitingListName = name.getName();
		}
		return true;
	}

	static public void updateWaitingList(String waitingListID) {
		Integer waitingListId = ConversionUtils.fromIntString(waitingListID);
		List<WaitingList> waitingListEntries = waitingListDao.findByWaitingListId(waitingListId);

		boolean needUpdate = false;
		// go through all the patients on the list
		for (WaitingList entry : waitingListEntries) {
			int demographicNo = entry.getDemographicNo();

			// check if the patient has an appointment already
			List<Appointment> appointments = waitingListDao.findAppointmentFor(entry);
			if (!appointments.isEmpty()) {
				//delete patient from the waitingList
				WLWaitingListUtil.removeFromWaitingList(waitingListID, String.valueOf(demographicNo));
				needUpdate = true;
			}
		}
		if (needUpdate) {
			int i=1;
			for (WaitingList entry : waitingListEntries) {
				entry.setPosition(i);
				waitingListDao.saveEntity(entry);
				i++;
			}
		}
	}

	public List<WLPatientWaitingListBean> getWaitingList() {
		return waitingListArrayList;
	}

	public String getWaitingListName() {
		return waitingListName;
	}
}
