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

package org.oscarehr.prevention.service;

import org.oscarehr.common.dao.PropertyDao;
import org.oscarehr.common.model.Property;
import org.oscarehr.prevention.dao.PreventionDao;
import org.oscarehr.prevention.dao.PreventionExtDao;
import org.oscarehr.prevention.dto.PreventionTypeTransfer;
import org.oscarehr.prevention.model.Prevention;
import org.oscarehr.prevention.model.PreventionExt;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import oscar.oscarPrevention.PreventionDisplayConfig;
import oscar.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
@Transactional
public class PreventionManager
{
	@Autowired
	private PreventionDao preventionDao;
	@Autowired
	private PreventionExtDao preventionExtDao;
	@Autowired
	private PropertyDao propertyDao;

	private static final String HIDE_PREVENTION_ITEM = "hide_prevention_item";

	private ArrayList<String> preventionTypeList = new ArrayList<>();

	public List<Prevention> getUpdatedAfterDate(LoggedInInfo loggedInInfo, Date updatedAfterThisDateExclusive, int itemsToReturn)
	{
		List<Prevention> results = preventionDao.findByUpdateDate(updatedAfterThisDateExclusive, itemsToReturn);
		return (results);
	}

	public Prevention getPrevention(LoggedInInfo loggedInInfo, Integer id)
	{
		Prevention result = preventionDao.find(id);
		return (result);
	}

	public List<PreventionExt> getPreventionExtByPrevention(LoggedInInfo loggedInInfo, Integer preventionId)
	{
		List<PreventionExt> results = preventionExtDao.findByPreventionId(preventionId);
		return (results);
	}

	public ArrayList<String> getPreventionTypeList()
	{
		if(preventionTypeList.isEmpty())
		{
			PreventionDisplayConfig pdc = PreventionDisplayConfig.getInstance();
			for(HashMap<String, String> prevTypeHash : pdc.getPreventions())
			{
				if(prevTypeHash != null && StringUtils.filled(prevTypeHash.get("name")))
				{
					preventionTypeList.add(prevTypeHash.get("name").trim());
				}
			}
		}
		return preventionTypeList;
	}

	public HashMap<String, String> getPreventionByNameOrType(String nameOrType)
	{
		PreventionDisplayConfig pdc = PreventionDisplayConfig.getInstance();
		for(HashMap<String, String> prevTypeHash : pdc.getPreventions())
		{
			String hcType = prevTypeHash.get("healthCanadaType");
			String name = prevTypeHash.get("name");

			if(StringUtils.filled(name) && name.equals(nameOrType) || StringUtils.filled(hcType) && hcType.equals(nameOrType))
			{
				return prevTypeHash;
			}
		}
		return null;
	}

	@Deprecated
	public ArrayList<HashMap<String, String>> getPreventionTypeDescList()
	{
		PreventionDisplayConfig pdc = PreventionDisplayConfig.getInstance();
		ArrayList<HashMap<String, String>> preventionTypeDescList = pdc.getPreventions();

		return preventionTypeDescList;
	}

	public boolean isHidePrevItemExist()
	{
		List<Property> props = propertyDao.findByName(HIDE_PREVENTION_ITEM);
		if(props.size() > 0)
		{
			return true;
		}
		return false;
	}

	public boolean hideItem(String item)
	{
		List<String> itemsToHide = getItemsToHide();
		return hideItem(item, itemsToHide);
	}

	public List<String> getItemsToHide()
	{
		Property p = propertyDao.checkByName(HIDE_PREVENTION_ITEM);

		List<String> items = new ArrayList<>();
		if(p != null && p.getValue() != null)
		{
			String itemsToHideRawString = p.getValue();
			items = Arrays.asList(itemsToHideRawString.split("\\s*,\\s*"));
		}

		return items;
	}

	public boolean hideItem(String item, List<String> itemsToHide)
	{
		if(itemsToHide == null)
		{
			return false;
		}

		return itemsToHide.contains(item);
	}

	public static String getCustomPreventionItems()
	{
		String itemsToRemove = "";
		PropertyDao propertyDao = (PropertyDao) SpringUtils.getBean("propertyDao");
		Property p = propertyDao.checkByName(HIDE_PREVENTION_ITEM);
		if(p != null && p.getValue() != null)
		{
			itemsToRemove = p.getValue();
		}
		return itemsToRemove;
	}

	public void addCustomPreventionItems(String items)
	{
		boolean propertyExists = isHidePrevItemExist();
		if(propertyExists)
		{
			Property p = propertyDao.checkByName(HIDE_PREVENTION_ITEM);
			p.setValue(items);
			propertyDao.merge(p);
		}
		else
		{
			Property x = new Property();
			x.setName("hide_prevention_item");
			x.setValue(items);
			propertyDao.persist(x);
		}
	}

	/**
	 *
	 * @param prevention the prevention model to add
	 * @param exts the map of extension models
	 * @deprecated - add preventionExt to the prevention and call addPrevention instead.
	 */
	@Deprecated
	public void addPreventionWithExts(Prevention prevention, HashMap<String, String> exts)
	{
		if(prevention == null) return;

		if(exts != null)
		{
			for(String key : exts.keySet())
			{
				if(StringUtils.filled(key) && StringUtils.filled(exts.get(key)))
				{
					PreventionExt preventionExt = new PreventionExt();
					preventionExt.setPrevention(prevention);
					preventionExt.setKeyval(key);
					preventionExt.setVal(exts.get(key));

					prevention.addExtension(preventionExt);
				}
			}
		}
		preventionDao.persist(prevention);
	}

	/**
	 * programId is ignored for now as oscar doesn't support it yet.
	 */
	public List<Prevention> getPreventionsByProgramProviderDemographicDate(LoggedInInfo loggedInInfo, Integer programId, String providerNo, Integer demographicId, Calendar updatedAfterThisDateExclusive, int itemsToReturn)
	{
		List<Prevention> results = preventionDao.findByProviderDemographicLastUpdateDate(providerNo, demographicId, updatedAfterThisDateExclusive.getTime(), itemsToReturn);
		return (results);
	}

	public List<Prevention> getPreventionsByDemographicNo(LoggedInInfo loggedInInfo, Integer demographicNo)
	{
		List<Prevention> results = preventionDao.findUniqueByDemographicId(demographicNo);
		return (results);
	}

	/**
	 * kinda hackey keyword search of prevention types. checks name, code, and health Canada type
	 * @param filterTerm the term to filter on, set to null for all items
	 * @return the relevant preventions
	 */
	public List<PreventionTypeTransfer> searchPreventionTypes(String filterTerm, Comparator<PreventionTypeTransfer> comparator)
	{
		ArrayList<PreventionTypeTransfer> transfers = new ArrayList<>();
		PreventionDisplayConfig pdc = PreventionDisplayConfig.getInstance();
		for(HashMap<String, String> prevTypeHash : pdc.getPreventions())
		{
			PreventionTypeTransfer transfer = new PreventionTypeTransfer();
			transfer.setName(org.apache.commons.lang3.StringUtils.trimToNull(prevTypeHash.get("name")));
			transfer.setCode(org.apache.commons.lang3.StringUtils.trimToNull(prevTypeHash.get("name")));
			transfer.setHealthCanadaType(org.apache.commons.lang3.StringUtils.trimToNull(prevTypeHash.get("healthCanadaType")));
			transfer.setAtc(org.apache.commons.lang3.StringUtils.trimToNull(prevTypeHash.get("atc")));
			transfer.setDescription(org.apache.commons.lang3.StringUtils.trimToNull(prevTypeHash.get("description")));

			// no filter term
			if(org.apache.commons.lang3.StringUtils.trimToNull(filterTerm) == null)
			{
				transfers.add(transfer);
			}
			else if((transfer.getName() != null && transfer.getName().contains(filterTerm)) ||
					(transfer.getCode() != null && transfer.getCode().contains(filterTerm)) ||
					(transfer.getHealthCanadaType() != null && transfer.getHealthCanadaType().contains(filterTerm)))
			{
				transfers.add(transfer);
			}
		}

		if(comparator != null)
		{
			transfers.sort(comparator);
		}

		return transfers;
	}
}
