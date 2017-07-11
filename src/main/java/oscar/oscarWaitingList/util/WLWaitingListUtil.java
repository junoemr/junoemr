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

package oscar.oscarWaitingList.util;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.oscarehr.common.dao.DemographicDao;
import org.oscarehr.common.dao.WaitingListDao;
import org.oscarehr.common.dao.WaitingListNameDao;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.common.model.WaitingList;
import org.oscarehr.common.model.WaitingListName;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;

import oscar.util.ConversionUtils;

public class WLWaitingListUtil {
	
	private static WaitingListDao waitingListDao = SpringUtils.getBean(WaitingListDao.class);
	private static WaitingListNameDao waitingListNameDao = SpringUtils.getBean(WaitingListNameDao.class);
	private static DemographicDao demographicDao = SpringUtils.getBean(DemographicDao.class);
	
	private static Logger logger = MiscUtils.getLogger();
	
	// Modified this method in Feb 2007 to ensure that all records cannot be deleted except hidden.
	static public synchronized void removeFromWaitingList(String waitingListIDStr, String demographicNoStr) {
		logger.info("Removing patient # " + demographicNoStr + " from waitingList " + waitingListIDStr);
		
		Integer waitingListID = ConversionUtils.fromIntString(waitingListIDStr);
		Integer demographicNo = ConversionUtils.fromIntString(demographicNoStr);

		for (WaitingList wl : waitingListDao.findByWaitingListIdAndDemographicId(waitingListID, demographicNo)) {
			wl.setHistory(true);
			waitingListDao.merge(wl);
		}
		rePositionWaitingList(waitingListID);
	}

	/**
	 * This is now an alias for addToWaitingList but all inputs are Strings
	 */
	static public synchronized void add2WaitingList(String waitingListIDStr, String waitingListNote, String demographicNoStr, String onListSince) {
		
		boolean emptyIds = waitingListIDStr == null || waitingListIDStr.equalsIgnoreCase("0") && !demographicNoStr.equalsIgnoreCase("0");
		if (emptyIds) {
			logger.error("Waitlist Ids are not valid (listId=" + waitingListIDStr + ",demoNo=" + demographicNoStr + "). Failed to update patient waitlist");
			return;
		}
		Integer waitingListID = Integer.parseInt(waitingListIDStr);
		Integer demographicNo = Integer.parseInt(demographicNoStr);
		addToWaitingList(waitingListID, demographicNo, onListSince, waitingListNote);
	}
	
	/**
	 * Adds a new waitinlist entry for the given demographic 
	 * @param waitingListID - Id of waitListName to add to
	 * @param demographicNo - demographic id
	 * @param onListSince - String representing the date patient was added to the wait list
	 * @param waitingListNote - Note string
	 */
	static public synchronized WaitingList addToWaitingList(Integer waitingListID, Integer demographicNo, String onListSince, String waitingListNote) {
		logger.info("Adding patient #" + demographicNo + " to waitingList " + waitingListID);

		boolean emptyIds = waitingListID == null || waitingListID <= 0 || demographicNo <= 0;
		if (emptyIds) {
			logger.error("Waitlist Ids are not valid (listId=" + waitingListID + ",demoNo=" + demographicNo + "). Failed to add patient to waitlist");
			return null;
		}
		
		Date onListSinceDate = (onListSince == null || onListSince.isEmpty()) ? new Date() : ConversionUtils.fromDateString(onListSince);

		WaitingList waitListEntry  = new WaitingList();
		Demographic demographic = demographicDao.getDemographic(demographicNo.toString());
		WaitingListName waitingListName = waitingListNameDao.find(waitingListID);
		long position = waitingListDao.getMaxPosition(waitingListID) + 1;
		
		waitListEntry.setDemographic(demographic);
		waitListEntry.setHistory(false);
		waitListEntry.setWaitingListName(waitingListName);
		waitListEntry.setPosition(position);
		waitListEntry.setOnListSince(onListSinceDate);
		waitListEntry.setNote(waitingListNote);
		
		// save changes to database
		waitingListDao.persist(waitListEntry);
		rePositionWaitingList(waitingListID);
		return waitListEntry;
	}

	/**
	 * This method adds the Waiting List note to the same position in the waitingList table but do not delete previous ones - later on DisplayWaitingList.jsp will display only the most current Waiting List Note record.
	 */
	static public synchronized WaitingList updateWaitingListRecord(String waitingListIDStr, String waitingListNote, String demographicNoStr, String onListSince) {
		logger.info("Updating patient #" + demographicNoStr + " on waitingList " + waitingListIDStr);
		boolean emptyIds = waitingListIDStr == null || waitingListIDStr.equalsIgnoreCase("0") && !demographicNoStr.equalsIgnoreCase("0");
		if (emptyIds) {
			logger.error("Waitlist Ids are not valid (listId=" + waitingListIDStr + ",demoNo=" + demographicNoStr + "). Failed to update patient waitlist");
			return null;
		}
		
		Integer waitingListID = Integer.parseInt(waitingListIDStr);
		Integer demographicNo = Integer.parseInt(demographicNoStr);

		List<WaitingList> waitingLists = waitingListDao.findByWaitingListIdAndDemographicId(waitingListID, demographicNo);
		// set all previous records 'is_history' field to 'Y' --> to keep as record but never display
		for (WaitingList wl : waitingLists) {
			wl.setHistory(true);
			waitingListDao.merge(wl);
		}
		return addToWaitingList(waitingListID, demographicNo, onListSince, waitingListNote);
	}

	/**
	 * This method adds the Waiting List note to the same position in the waitingList table but do not delete previous ones - later on DisplayWaitingList.jsp will display only the most current Waiting List Note record.
	 */
	static public synchronized void updateWaitingList(String id, String waitingListIDStr, String waitingListNote, String demographicNoStr, String onListSince) {
		logger.info("Updating patient #" + demographicNoStr + " on waitingList " + waitingListIDStr);
		boolean idsSet = !waitingListIDStr.equalsIgnoreCase("0") && !demographicNoStr.equalsIgnoreCase("0");
		if (!idsSet) {
			MiscUtils.getLogger().debug("Ids are not set - exiting");
			return;
		}

		boolean wlIdsSet = (id != null && !id.equals(""));
		if (!wlIdsSet) {
			MiscUtils.getLogger().debug("Waiting list id is not set");
			return;
		}

		WaitingList waitingListEntry = waitingListDao.find(ConversionUtils.fromIntString(id));
		if (waitingListEntry == null) {
			MiscUtils.getLogger().debug("Unable to fetch waiting list with id " + id);
			return;
		}

		WaitingListName maxPositionList = waitingListNameDao.find(waitingListIDStr);
		
		waitingListEntry.setWaitingListName(maxPositionList);
		waitingListEntry.setNote(waitingListNote);
		waitingListEntry.setOnListSince(ConversionUtils.fromDateString(onListSince));

		waitingListDao.merge(waitingListEntry);
	}

	public static void rePositionWaitingList(Integer waitingListID) {
		int i = 1;
		for (WaitingList waitingList : waitingListDao.findByWaitingListId(waitingListID)) {
			waitingList.setPosition(i);
			waitingListDao.merge(waitingList);
			i++;
		}
	}
}
