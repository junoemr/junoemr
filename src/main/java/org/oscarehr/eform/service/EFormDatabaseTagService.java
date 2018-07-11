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
import org.oscarehr.util.MiscUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import oscar.eform.EFormLoader;
import oscar.eform.EFormUtil;
import oscar.eform.data.DatabaseAP;

import java.util.ArrayList;
import java.util.HashMap;
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

	/**
	 * Parse the html and find all of the oscarDB tags. load the values for each one and return them as a map
	 * @param html - the html to parse
	 * @return - key value map of with oscar database tags being the keys, and their current database values
	 */
	public Map<String, String> getAllDatabaseTagValues(String html, Integer demographicId, Integer providerId)
	{
		return getAllDatabaseTagValues(EFormLoader.getMarker(), html, demographicId, providerId);
	}

	/**
	 * Parse the html and find all of the oscarUpdateDB tags. load the values for each one and return them as a map
	 * @param html - the html to parse
	 * @return - key value map of with oscar database tags being the keys, and their current database values
	 */
	public Map<String, String> getAllDatabaseUpdateTagValues(String html, Integer demographicId, Integer providerId)
	{
		return getAllDatabaseTagValues(EFormLoader.getUpdateMarker(), html, demographicId, providerId);
	}

	private Map<String, String> getAllDatabaseTagValues(String marker, String html, Integer demographicId, Integer providerId)
	{
		Map<String, String> tagMap = new HashMap<>();
		EFormLoader.getInstance();

		Pattern tagPattern = Pattern.compile(marker + "=[\"'](.*?)[\"\']", Pattern.CASE_INSENSITIVE);
		Matcher tagPatternMatcher = tagPattern.matcher(html);
		while(tagPatternMatcher.find())
		{
			String tagName = tagPatternMatcher.group(1);
			logger.info("Load EForm AP tag data: " + tagName);

			if(tagMap.containsKey(tagName))
			{
				// skip duplicate keys
				logger.info("Skip duplicate key");
				continue;
			}

			DatabaseAP databaseAP = EFormLoader.getAP(tagName);
			// if the normal AP doesn't have it, check the extras
			if(databaseAP == null)
			{
				//TODO get ExtraAp???
				// this is things like m$measurement#stuff
			}
			// if it's still null, skip the rest
			if(databaseAP == null)
			{
				logger.info("null databaseAP");
				continue;
			}

			// retrieve the value from the database and add it to the map
			String value = getValueFromDatabase(databaseAP, demographicId, providerId);
			if(value != null)
			{
				logger.info("value: " + value);
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
			logger.info("sql: " + sql);
			sql = replaceAllFields(sql, String.valueOf(demographicId), String.valueOf(providerId));
			ArrayList<String> names = DatabaseAP.parserGetNames(output);
			sql = DatabaseAP.parserClean(sql); // replaces all other ${apName} expressions with 'apName'

			logger.info("sql final: " + sql);
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


	/* These are here for legacy purposes */
	private static final String EFORM_DEMOGRAPHIC = "eform_demographic";
	private static final String VAR_NAME = "var_name";
	private static final String VAR_VALUE = "var_value";
	private static final String REF_FID = "fid";
	private static final String REF_VAR_NAME = "ref_var_name";
	private static final String REF_VAR_VALUE = "ref_var_value";
	private static final String TABLE_NAME = "table_name";
	private static final String TABLE_ID = "table_id";
	private static final String OTHER_KEY = "other_key";
	private static final String OPENER_VALUE = "link$eform";
	private static final String PRECHECKED = "checked=\"checked\"";


	public String replaceAllFields(String sql, String demographicId, String providerId)
	{
		sql = DatabaseAP.parserReplace("demographic", demographicId, sql);
		sql = DatabaseAP.parserReplace("provider", providerId, sql);
		sql = DatabaseAP.parserReplace("loggedInProvider", providerId, sql);
		sql = DatabaseAP.parserReplace("appt_no", "0", sql);

		sql = DatabaseAP.parserReplace(EFORM_DEMOGRAPHIC, getSqlParams(EFORM_DEMOGRAPHIC), sql);
		sql = DatabaseAP.parserReplace(REF_FID, getSqlParams(REF_FID), sql);
		sql = DatabaseAP.parserReplace(VAR_NAME, getSqlParams(VAR_NAME), sql);
		sql = DatabaseAP.parserReplace(VAR_VALUE, getSqlParams(VAR_VALUE), sql);
		sql = DatabaseAP.parserReplace(REF_VAR_NAME, getSqlParams(REF_VAR_NAME), sql);
		sql = DatabaseAP.parserReplace(REF_VAR_VALUE, getSqlParams(REF_VAR_VALUE), sql);
		sql = DatabaseAP.parserReplace(TABLE_NAME, getSqlParams(TABLE_NAME), sql);
		sql = DatabaseAP.parserReplace(TABLE_ID, getSqlParams(TABLE_ID), sql);
		sql = DatabaseAP.parserReplace(OTHER_KEY, getSqlParams(OTHER_KEY), sql);
		return sql;
	}

	private String getSqlParams(String key) {
		//TODO
//		if (sql_params.containsKey(key)) {
//			String val =  sql_params.get(key);
//			return val==null ? "" : StringEscapeUtils.escapeSql(val);
//		}
		return "";
	}
}
