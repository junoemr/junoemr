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


package oscar.oscarEncounter.pageUtil;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.util.MessageResources;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.prevention.dao.PreventionDao;
import org.oscarehr.prevention.dto.PreventionListData;
import org.oscarehr.prevention.service.PreventionManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.SpringUtils;

import oscar.oscarDemographic.data.DemographicData;
import oscar.oscarPrevention.Prevention;
import oscar.oscarPrevention.PreventionDS;
import oscar.oscarPrevention.PreventionData;
import oscar.oscarPrevention.PreventionDisplayConfig;
import oscar.util.StringUtils;

/**
 *  Creates DAO for left navbar of encounter form
 *
 */
public class EctDisplayPreventionAction extends EctDisplayAction
{
	protected static final String COLOUR_HIGHLITE = "#FF0000";
	protected static final String COLOUR_INELLIGIBLE = "#FF6600";
	protected static final String COLOUR_PENDING = "#FF00FF";

	private static final String cmd = "preventions";

	public boolean getInfo(EctSessionBean bean, HttpServletRequest request, NavBarDisplayDAO Dao, MessageResources messages)
	{
		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);

		if (!securityInfoManager.hasPrivilege(loggedInInfo, "_prevention", "r", null))
		{
            return true; //Prevention link won't show up on new CME screen.
		}

		//set lefthand module heading and link
		String winName = "prevention" + bean.demographicNo;
		String url = "popupPage(700,960,'" + winName + "', '" + request.getContextPath() + "/oscarPrevention/index.jsp?demographic_no=" + bean.demographicNo + "')";
		Dao.setLeftHeading(messages.getMessage(request.getLocale(), "oscarEncounter.LeftNavBar.Prevent"));
		Dao.setLeftURL(url);

		//set righthand link to same as left so we have visual consistency with other modules
		url += ";return false;";
		Dao.setRightURL(url);
		Dao.setRightHeadingID(cmd);  //no menu so set div id to unique id for this action

		//list warnings first as module items
		Prevention p = PreventionData.getPrevention(loggedInInfo, Integer.valueOf(bean.demographicNo));
		PreventionDS pf = SpringUtils.getBean(PreventionDS.class);//PreventionDS.getInstance();
		PreventionDao preventionDao = SpringUtils.getBean(PreventionDao.class);//PreventionDS.getInstance();

		Map<String, PreventionListData>	preventionListDataMap = preventionDao.getPreventionListData(bean.demographicNo);

		try
		{
			pf.getMessages(p);
		}
		catch(Exception dsException)
		{
			return false;
		}

		//now we list prevention modules as items
		PreventionDisplayConfig pdc = PreventionDisplayConfig.getInstance();
		ArrayList<HashMap<String,String>> prevList = pdc.getPreventions();
		Map warningTable = p.getWarningMsgs();



		DemographicData dData = new DemographicData();
		Demographic demographic = dData.getDemographic(loggedInInfo, bean.demographicNo);

		PreventionManager preventionManager = SpringUtils.getBean(PreventionManager.class);
		List<String> itemsToHide = preventionManager.getItemsToHide();

		url += "; return false;";
        ArrayList<NavBarDisplayDAO.Item> warnings = new ArrayList<NavBarDisplayDAO.Item>();
        ArrayList<NavBarDisplayDAO.Item> items = new ArrayList<NavBarDisplayDAO.Item>();
        for (int i = 0 ; i < prevList.size(); i++)
		{
			HashMap<String, String> h = prevList.get(i);
			String prevName = h.get("name");

			PreventionListData preventionListData = preventionListDataMap.get(prevName);

			int preventionCount = 0;
			if (preventionListData != null)
			{
				preventionCount = preventionListData.getPreventionCount();
			}

			boolean show = pdc.display(h, demographic, preventionCount, itemsToHide);

			NavBarDisplayDAO.Item item = NavBarDisplayDAO.Item();
			if (show)
			{
				if (preventionCount > 0)
				{
					if (preventionListData.getPreventionDate() != null)
					{
						Date itemDate = Date.from(preventionListData.getPreventionDate().atZone(ZoneId.systemDefault()).toInstant());
						item.setDate(itemDate);
					}

					if (
							preventionListData.getRefused() != null &&
									preventionListData.getRefused().equals('2')
					)
					{
						item.setColour(COLOUR_INELLIGIBLE);
					} else if (
							preventionListData.getPreventionResult() != null &&
									preventionListData.getPreventionResult().equalsIgnoreCase(
											"pending")
					)
					{
						item.setColour(COLOUR_PENDING);
					}
				}

				String title = StringUtils.maxLenString(
						h.get("name"),
						MAX_LEN_TITLE,
						CROP_LEN_TITLE,
						ELLIPSES
				);

				item.setTitle(title);
				item.setURL(url);

				//if there's a warning associated with this prevention set item apart
				if (warningTable.containsKey(prevName))
				{
					item.setColour(COLOUR_HIGHLITE);
					warnings.add(item);
				}
				else
				{
					items.add(item);
				}
			}


			// TODO-legacy: add a flag to use the old code
			/*
			Date date = null;
			String result;

			NavBarDisplayDAO.Item item = NavBarDisplayDAO.Item();
            HashMap<String,String> h = prevList.get(i);
            String prevName = h.get("name");
            ArrayList<Map<String,Object>> alist = PreventionData.getPreventionData(loggedInInfo, prevName, Integer.valueOf(bean.demographicNo));
            Date demographicDateOfBirth=PreventionData.getDemographicDateOfBirth(loggedInInfo, Integer.valueOf(bean.demographicNo));
            PreventionData.addRemotePreventions(loggedInInfo, alist, Integer.valueOf(bean.demographicNo),prevName,demographicDateOfBirth);
            boolean show = pdc.display(loggedInInfo, h, bean.demographicNo,alist.size());
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
                        item.setColour(COLOUR_INELLIGIBLE);
                    }
                    else if( result != null && result.equalsIgnoreCase("pending") ) {
                        item.setColour(COLOUR_PENDING);
                    }
                }
                else {
                    item.setDate(null);
                }

                String title = StringUtils.maxLenString(h.get("name"),  MAX_LEN_TITLE, CROP_LEN_TITLE, ELLIPSES);
                item.setTitle(title);
                item.setLinkTitle(h.get("desc"));
                item.setURL(url);

                //if there's a warning associated with this prevention set item apart
                if( warningTable.containsKey(prevName) ){
                    item.setColour(COLOUR_HIGHLITE);
                    warnings.add(item);
                }
                else {
                    items.add(item);
                }
            }

			 */
		}

		//sort items without warnings chronologically
		Dao.sortItems(items, NavBarDisplayDAO.DATESORT_ASC);

		//add warnings to Dao array first so they will be at top of list
		for(int idx = 0; idx < warnings.size(); ++idx)
		{
			Dao.addItem(warnings.get(idx));
		}

		//now copy remaining sorted items
		for(int idx = 0; idx < items.size(); ++idx)
		{
			Dao.addItem(items.get(idx));
		}

		return true;
	}

    public String getCmd()
    {
        return cmd;
    }
}
