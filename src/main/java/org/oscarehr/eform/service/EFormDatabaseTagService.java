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
import org.oscarehr.common.dao.PreventionDao;
import org.oscarehr.common.model.Prevention;
import org.oscarehr.eform.EFormHtmlParser;
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
			if(databaseAP == null)
			{
				logger.warn("Invalid database tag (" + tagName + ") was skipped.");
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

	private String getSqlParams(String key)
	{
		//TODO
//		if (sql_params.containsKey(key)) {
//			String val =  sql_params.get(key);
//			return val==null ? "" : StringEscapeUtils.escapeSql(val);
//		}
		return "";
	}

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

		DatabaseAP databaseAP;
		switch(module)
		{
			case "m": databaseAP = getMeasurementsAP(type, field, demographicId); break;
			case "p": databaseAP = getPreventionsAP(type, field, demographicId); break;
			case "e": databaseAP = getEformValuesAP(type, field, demographicId); break;
			case "o": databaseAP = getOtherAP(type, field, demographicId); break;
			default: return null;
		}
		databaseAP.setApName(tagName);
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

		//TODO implement this?
//			String eform_name = EFormUtil.removeQuotes(EFormUtil.getAttribute("eform$name", fieldHeader));
//			String var_value = EFormUtil.removeQuotes(EFormUtil.getAttribute("var$value", fieldHeader));
//			String ref = EFormUtil.removeQuotes(EFormUtil.getAttribute("ref$", fieldHeader, true));
//
//			String eform_demographic = this.demographicNo;
//			if(this.patientIndependent) eform_demographic = "%";
//
//			String ref_name = null, ref_value = null, ref_fid = fid;
//			if(!StringUtils.isBlank(ref) && ref.contains("="))
//			{
//				ref_name = ref.substring(4, ref.indexOf("="));
//				ref_value = EFormUtil.removeQuotes(ref.substring(ref.indexOf("=") + 1));
//			}
//			else
//			{
//				ref_name = StringUtils.isBlank(ref) ? "" : ref.substring(4);
//			}
//			if(!StringUtils.isBlank(eform_name)) ref_fid = getRefFid(eform_name);
//			if((!StringUtils.isBlank(var_value) && var_value.trim().startsWith("{")) || (!StringUtils.isBlank(ref_value) && ref_value.trim().startsWith("{")))
//			{
//				if(setAP2nd)
//				{ // 2nd run, put value in required field
//					var_value = findValueInForm(var_value);
//					ref_value = findValueInForm(ref_value);
//					needValueInForm--;
//				}
//				else
//				{ // 1st run, note the need to reference other value in form
//					needValueInForm++;
//					return null;
//				}
//			}
//
//			if(type.equalsIgnoreCase("count") && var_value == null)
//			{
//				type = "countname";
//			}
//			else if((type.equalsIgnoreCase("first") || type.equalsIgnoreCase("last")) && field.equals("*"))
//			{
//				type += "_all_json";
//			}
//			if(!ref_name.equals(""))
//			{
//				type += "_ref";
//				if(ref_value == null) type += "name";
//			}
//
//			EFormLoader.getInstance();
//			curAP = EFormLoader.getAP("_eform_values_" + type);
//
//			if(curAP != null)
//			{
//				setSqlParams(EFORM_DEMOGRAPHIC, eform_demographic);
//				setSqlParams(VAR_NAME, field);
//				setSqlParams(REF_VAR_NAME, ref_name);
//				setSqlParams(VAR_VALUE, var_value);
//				setSqlParams(REF_VAR_VALUE, ref_value);
//				setSqlParams(REF_FID, ref_fid);
//			}
		return new DatabaseAP();
	}

	private DatabaseAP getOtherAP(String type, String field, Integer demographicId)
	{
		logger.debug("SWITCHING TO OTHER_ID");

		//TODO
//			String table_name = "", table_id = "";
//			EFormLoader.getInstance();
//			curAP = EFormLoader.getAP("_other_id");
//			if (type.equalsIgnoreCase("patient")) {
//				table_name = OtherIdManager.DEMOGRAPHIC.toString();
//				table_id = this.demographicNo;
//			} else if (type.equalsIgnoreCase("appointment")) {
//				table_name = OtherIdManager.APPOINTMENT.toString();
//				table_id = appointment_no;
//				if (StringUtils.isBlank(table_id)) table_id = "-1";
//			}
//			setSqlParams(OTHER_KEY, field);
//			setSqlParams(TABLE_NAME, table_name);
//			setSqlParams(TABLE_ID, table_id);
		return new DatabaseAP();
	}

	private String dateStringOrEmpty(Date date)
	{
		return ConversionUtils.toDateString(date, ConversionUtils.DEFAULT_DATE_PATTERN);
	}
}
