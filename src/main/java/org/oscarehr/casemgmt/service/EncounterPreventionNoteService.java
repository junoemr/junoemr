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

package org.oscarehr.casemgmt.service.impl;

import org.oscarehr.casemgmt.dto.EncounterSectionNote;
import org.oscarehr.util.LoggedInInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import oscar.oscarEncounter.pageUtil.NavBarDisplayDAO;
import oscar.oscarPrevention.Prevention;
import oscar.oscarPrevention.PreventionDS;
import oscar.oscarPrevention.PreventionData;
import oscar.oscarPrevention.PreventionDisplayConfig;
import oscar.util.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class EncounterPreventionNoteService
{
	protected static final String ELLIPSES = "...";
	protected static final int MAX_LEN_TITLE = 48;
	protected static final int CROP_LEN_TITLE = 45;
	protected static final int MAX_LEN_KEY = 12;
	protected static final int CROP_LEN_KEY = 9;

	@Autowired
	PreventionDS pf;

	public List<EncounterSectionNote> getPreventionNotes(LoggedInInfo loggedInInfo, String demographicNo)
			throws Exception
	{
		List<EncounterSectionNote> out = new ArrayList<>();

		//list warnings first as module items
		Prevention p = PreventionData.getPrevention(loggedInInfo, Integer.valueOf(demographicNo));
		//PreventionDS pf = SpringUtils.getBean(PreventionDS.class);//PreventionDS.getInstance();

		// Might throw an exception
		// XXX: make this exception better
		pf.getMessages(p);

		//now we list prevention modules as items
		PreventionDisplayConfig pdc = PreventionDisplayConfig.getInstance();
		ArrayList<HashMap<String,String>> prevList = pdc.getPreventions();
		Map warningTable = p.getWarningMsgs();



		String highliteColour = "#FF0000";
		String inelligibleColour = "#FF6600";
		String pendingColour = "#FF00FF";
		Date date = null;
		ArrayList<NavBarDisplayDAO.Item> warnings = new ArrayList<NavBarDisplayDAO.Item>();
		ArrayList<NavBarDisplayDAO.Item> items = new ArrayList<NavBarDisplayDAO.Item>();
		String result;
		for (int i = 0 ; i < prevList.size(); i++){

			NavBarDisplayDAO.Item item = NavBarDisplayDAO.Item();

			HashMap<String,String> h = prevList.get(i);
			String prevName = h.get("name");
			ArrayList<Map<String,Object>> alist = PreventionData.getPreventionData(loggedInInfo, prevName, Integer.valueOf(demographicNo));
			Date demographicDateOfBirth=PreventionData.getDemographicDateOfBirth(loggedInInfo, Integer.valueOf(demographicNo));
			PreventionData.addRemotePreventions(loggedInInfo, alist, Integer.valueOf(demographicNo),prevName,demographicDateOfBirth);
			boolean show = pdc.display(loggedInInfo, h, demographicNo,alist.size());
			if( show ) {
				if( alist.size() > 0 ) {
					Map<String,Object> hdata = alist.get(alist.size()-1);
					Map<String,String> hExt = PreventionData.getPreventionKeyValues((String)hdata.get("id"));
					result = hExt.get("result");

					Object dateObj = hdata.get("prevention_date_asDate");
					if(dateObj instanceof Date){
						date = (Date) dateObj;
					}else if(dateObj instanceof java.util.GregorianCalendar){
						Calendar cal = (Calendar) dateObj;
						date = cal.getTime();
					}

					item.setDate(date);

					if( hdata.get("refused") != null && hdata.get("refused").equals("2") ) {
						item.setColour(inelligibleColour);
					}
					else if( result != null && result.equalsIgnoreCase("pending") ) {
						item.setColour(pendingColour);
					}
				}
				else {
					item.setDate(null);
				}

				String title = StringUtils.maxLenString(h.get("name"),  MAX_LEN_TITLE, CROP_LEN_TITLE, ELLIPSES);
				item.setTitle(title);
				item.setLinkTitle(h.get("desc"));
				//item.setURL(url);

				//if there's a warning associated with this prevention set item apart
				if( warningTable.containsKey(prevName) ){
					item.setColour(highliteColour);
					warnings.add(item);
				}
				else {
					items.add(item);
				}
			}
		}

		/*
		//sort items without warnings chronologically
		Dao.sortItems(items, NavBarDisplayDAO.DATESORT_ASC);

		//add warnings to Dao array first so they will be at top of list
		for(int idx = 0; idx < warnings.size(); ++idx )
			Dao.addItem(warnings.get(idx));

		//now copy remaining sorted items
		for(int idx = 0; idx < items.size(); ++idx)
			Dao.addItem(items.get(idx));

		 */

		return out;
	}
}
