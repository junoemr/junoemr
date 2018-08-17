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


package org.oscarehr.provider.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.oscarehr.common.dao.PropertyDao;
import org.oscarehr.common.model.Property;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.QueueCache;
import org.oscarehr.util.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import oscar.oscarPrevention.Prevention;
import oscar.oscarPrevention.PreventionDS;
import oscar.oscarPrevention.PreventionData;


/**
 *
 * @author rjonasz
 */
@Component
public class ProviderPreventionManager
{
	private static Logger logger = MiscUtils.getLogger();
	private static final QueueCache<String, String> dataCache=new QueueCache<String, String>(4, 500, DateUtils.MILLIS_PER_HOUR, null);

	@Autowired
	private PreventionDS pf = null;


	public String getWarnings(LoggedInInfo loggedInInfo, String demo)
	{
		String toReturn = dataCache.get(demo);

		if (toReturn == null)
		{
			try
			{
				Prevention prevention = PreventionData.getLocalandRemotePreventions(loggedInInfo, Integer.parseInt(demo));
				StringBuilder preventionWarnings = new StringBuilder();

				if (prevention != null)
				{
					pf.getMessages(prevention);

					@SuppressWarnings("unchecked")
					Map<String, Object> warningsMap = prevention.getWarningMsgs();
					for (Map.Entry<String, Object> entry : warningsMap.entrySet())
					{
						boolean isPreventionDisabled = ProviderPreventionManager.isPrevDisabled(entry.getKey());

						if (!isPreventionDisabled)
						{
							// The java compiler currently doesn't optimize string concatenation from inside->outside loops
							// so we will use StringBuilder to finish the concatenation.
							String warning = "[" + entry.getKey() + "=" + entry.getValue() + "]";
							preventionWarnings.append(warning);
						}
					}

					dataCache.put(demo, preventionWarnings.toString());
				}

				toReturn = preventionWarnings.toString();
			}
			catch (Exception e)
			{
				toReturn = "";
				logger.error("Error retrieving prevention warnings for demographic " + demo, e);
			}
		}

		return toReturn;
	}

	public void removePrevention(String demo) {
		dataCache.remove(demo);
	}


	public static String checkNames(String k){
		String rebuilt="";
		Pattern pattern = Pattern.compile("(\\[)(.*?)(\\])");
		Matcher matcher = pattern.matcher(k);

		while(matcher.find()){
			String[] key = matcher.group(2).split("=");
			boolean prevCheck = ProviderPreventionManager.isPrevDisabled(key[0]);

			if(prevCheck==false){
				rebuilt=rebuilt+"["+key[1]+"]";
			}
		}

		return rebuilt;
	}


	public static boolean isDisabled(){
		String getStatus="";
		PropertyDao propDao = (PropertyDao)SpringUtils.getBean("propertyDao");
		List<Property> pList = propDao.findByName("hide_prevention_stop_signs");

		Iterator<Property> i = pList.iterator();

		while (i.hasNext()) {
			Property item = i.next();
			getStatus  = item.getValue();

		}

		//disable all preventions warnings if result is master
		return getStatus.equals("master");
	}


	public static boolean isCreated(){
		String getStatus="";
		PropertyDao propDao = (PropertyDao)SpringUtils.getBean("propertyDao");
		List<Property> pList = propDao.findByName("hide_prevention_stop_signs");

		Iterator<Property> i = pList.iterator();

		while (i.hasNext()) {
			Property item = i.next();
			getStatus  = item.getName();

		}

		if(getStatus.equals("hide_prevention_stop_signs")){
			return true;
		}else{
			return false;
		}

	}

	public static boolean isPrevDisabled(String name){
		String getStatus="";
		PropertyDao propDao = (PropertyDao)SpringUtils.getBean("propertyDao");
		List<Property> pList = propDao.findByName("hide_prevention_stop_signs");

		Iterator<Property> i = pList.iterator();

		while (i.hasNext()) {
			Property item = i.next();
			getStatus  = item.getValue();

		}

		Pattern pattern = Pattern.compile("(\\[)(.*?)(\\])");
		Matcher matcher = pattern.matcher(getStatus);
		List<String> listMatches = new ArrayList<String>();

		while(matcher.find()){

			listMatches.add(matcher.group(2));

		}

		int x=0;
		for(String s : listMatches){

			if(name.equals(s)){
				x++;
			}

		}

		if(x>0){
			return true;
		}else{
			return false;
		}


	}



}
