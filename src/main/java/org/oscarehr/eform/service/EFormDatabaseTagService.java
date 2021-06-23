/**
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
package org.oscarehr.eform.service;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.oscarehr.eform.EFormHtmlParser;
import org.oscarehr.prevention.dao.PreventionDao;
import org.oscarehr.prevention.model.Prevention;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import oscar.eform.EFormLoader;
import oscar.eform.EFormUtil;
import oscar.eform.data.DatabaseAP;
import oscar.oscarEncounter.oscarMeasurements.bean.EctMeasurementsDataBeanHandler;
import oscar.util.ConversionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is responsible for all services that deal with loading/manipulating eform database tags
 */
@Service
@Transactional
public class EFormDatabaseTagService
{
	private static final Logger logger = MiscUtils.getLogger();

	@Autowired
	PreventionDao preventionDao;

	public Map<String,String> getDatabaseTagNameValueMap(String htmlString, Integer demographicNo, Integer providerNo)
	{
		EFormHtmlParser htmlParser = new EFormHtmlParser(htmlString);

		// for new eforms, both regular dbTags and updateDbTags work the same way, so load both and combine them
		// map of name:tag_name
		Map<String, String> nameMap = htmlParser.getElementNamesWithDatabaseTag();
		nameMap.putAll(htmlParser.getElementNamesWithUpdateDatabaseTag());

		// map of tag_name:database_value
		Map<String, String> tagMap = loadDatabaseTagValues(new ArrayList<>(nameMap.values()), demographicNo, providerNo);

		Map<String, String> aPValueMap = new HashMap<>(nameMap.size());
		for(Map.Entry<String, String> entry : nameMap.entrySet())
		{
			String databaseValue = tagMap.get(entry.getValue());
			aPValueMap.put(entry.getKey(), (databaseValue == null ? "" : databaseValue));
		}
		return  aPValueMap;
	}

	/**
	 * Load the values for each tag name and return them as a map
	 * @return - key value map of with oscar database tags being the keys, and their current database values
	 */
	public Map<String, String> loadDatabaseTagValues(List<String> tagList, Integer demographicId, Integer providerId)
	{
		Map<String, String> tagMap = new HashMap<>();
		EFormLoader.getInstance();

		for(String tagName : tagList)
		{
			// no need to load the same tag multiple times
			if(tagMap.containsKey(tagName))
			{
				continue;
			}

			DatabaseAP databaseAP = EFormLoader.getAP(tagName);
			// if the normal AP doesn't have it, check the extras
			if(databaseAP == null)
			{
				databaseAP = getAPExtra(tagName, demographicId);
			}
			// if it's still null, skip the rest
			// this will happen for invalid tags, but also when a measurement is not present for the demographic, etc.
			if(databaseAP == null)
			{
				continue;
			}

			// retrieve the value from the database and add it to the map
			String value = getValueFromDatabase(databaseAP, demographicId, providerId);
			if(value != null)
			{
				logger.debug("value: " + value);
				tagMap.put(tagName, value);
			}
		}
		return tagMap;
	}

	// mostly taken from eForm.putValuesFromAP
	private String getValueFromDatabase(DatabaseAP databaseAP, Integer demographicId, Integer providerId)
	{
		// prepare all sql & output
		String sql = databaseAP.getApSQL();
		String output = databaseAP.getApOutput();

		if (StringUtils.isNotEmpty(sql))
		{
			sql = replaceAllFields(sql, String.valueOf(demographicId), String.valueOf(providerId));
			ArrayList<String> names = DatabaseAP.parserGetNames(output);
			sql = DatabaseAP.parserClean(sql); // replaces all other ${apName} expressions with 'apName'

			ArrayList<String> values = EFormUtil.getValues(names, sql);
			if(values.size() != names.size())
			{
				output = "";
				logger.warn("values size does not match names size: " + values.size() + "/" + names.size());
			}
			else
			{
				for(int i = 0; i < names.size(); i++)
				{
					output = DatabaseAP.parserReplace(names.get(i), values.get(i), output);
				}
			}
		}
		return output;
	}

	/**
	 * This replaces specific tags with actual values in the sql.
	 * This is not performed on the object itself since the objects returned are shared across multiple uses,
	 * so calling a method like DatabaseAP.setTag(A,B) would not work with current architecture.
	 */
	public String replaceAllFields(String sql, String demographicId, String providerId)
	{
		sql = DatabaseAP.parserReplace("demographic", demographicId, sql);
		sql = DatabaseAP.parserReplace("provider", providerId, sql);
		sql = DatabaseAP.parserReplace("loggedInProvider", providerId, sql);
		sql = DatabaseAP.parserReplace("appt_no", "0", sql);

		return sql;
	}

	/**
	 * This handles database tags not defined in the standard apconfig file (measurements/preventions, etc.).
	 */
	private DatabaseAP getAPExtra(String tagName, Integer demographicId)
	{
		// --------------------------Process extra attributes for APs --------------------------------
		Pattern p = Pattern.compile("\\b[a-z]\\$[^\\$#]+#[^\n]+");
		Matcher m = p.matcher(tagName);
		if(!m.matches())
		{
			return null;
		}

		String module = tagName.substring(0, tagName.indexOf("$"));
		String type = tagName.substring(tagName.indexOf("$") + 1, tagName.indexOf("#"));
		String field = tagName.substring(tagName.indexOf("#") + 1, tagName.length());

		DatabaseAP databaseAP = null;
		switch(module)
		{
			case "m": databaseAP = getMeasurementsAP(type, field, demographicId); break;
			case "p": databaseAP = getPreventionsAP(type, field, demographicId); break;
			case "e": databaseAP = getEformValuesAP(type, field, demographicId); break;
			case "o": databaseAP = getOtherAP(type, field, demographicId); break;
			default: logger.warn("Invalid database tag (" + tagName + ") was skipped."); break;
		}
		if(databaseAP != null)
		{
			databaseAP.setApName(tagName);
		}
		return databaseAP;
	}

	private DatabaseAP getMeasurementsAP(String type, String field, Integer demographicId)
	{
		logger.debug("SWITCHING TO MEASUREMENTS");

		int maxResults = 1;
		int startAt = type.indexOf('@');
		if(startAt != -1)
		{
			String count = type.substring(startAt + 1);
			type = type.substring(0, startAt);
			maxResults = StringUtils.isNumeric(count) ? Integer.parseInt(count) : 1;
		}

		Hashtable<String, String> data = EctMeasurementsDataBeanHandler.getLast(String.valueOf(demographicId), type, maxResults);

		DatabaseAP databaseAP = null;
		if(!data.isEmpty())
		{
			databaseAP = new DatabaseAP();
			databaseAP.setApOutput(data.get(field));
		}
		return databaseAP;
	}

	private DatabaseAP getPreventionsAP(String type, String field, Integer demographicId)
	{
		logger.debug("SWITCHING TO PREVENTIONS");

		DatabaseAP databaseAP = null;
		// get the latest prevention
		Prevention prevention = preventionDao.findMostRecentByTypeAndDemoNo(type, demographicId);
		if(prevention != null)
		{
			databaseAP = new DatabaseAP();
			String value = "";

			switch(field.toUpperCase())
			{
				case "ID":              value = String.valueOf(prevention.getId()); break;
				case "DEMOGRAPHIC_NO":  value = String.valueOf(prevention.getDemographicId()); break;
				case "CREATION_DATE":   value = dateStringOrEmpty(prevention.getCreationDate()); break;
				case "CREATOR":         value = String.valueOf(prevention.getCreatorProviderNo()); break;
				case "PREVENTION_DATE": value = dateStringOrEmpty(prevention.getPreventionDate()); break;
				case "PROVIDER_NO":     value = String.valueOf(prevention.getProviderNo()); break;
				case "REFUSED":         value = String.valueOf(prevention.isRefused()); break;
				case "UPDATE_DATE":     value = dateStringOrEmpty(prevention.getLastUpdateDate()); break;
			}
			databaseAP.setApOutput(value);
		}
		return databaseAP;
	}

	private DatabaseAP getEformValuesAP(String type, String field, Integer demographicId)
	{
		logger.debug("SWITCHING TO EFORM_VALUES");

		/*
		 TODO-legacy It looks like oscar tries to allow eforms to pull information from other eforms using a tag in the form of e$_#_
		 but it also expects other markers on the element in this case, 'eform$name', 'var$value', and 'ref$'
		 This functionality should be ported here before this service replaces the current EForm parsing.

		 This is not yet implemented here, but exists in the old EForm code.
		 */
		return null;
	}

	private DatabaseAP getOtherAP(String type, String field, Integer demographicId)
	{
		logger.debug("SWITCHING TO OTHER_ID");

		/*
		TODO-legacy oscar attempts to allow a generic database tag in the form of o$_#_
		You can load values from arbitrary tables.
		This functionality should be ported here before this service replaces the current EForm parsing.

		This is not yet implemented here, but exists in the old EForm code.
		 */
		return null;
	}

	private String dateStringOrEmpty(Date date)
	{
		return ConversionUtils.toDateString(date, ConversionUtils.DEFAULT_DATE_PATTERN);
	}
}
