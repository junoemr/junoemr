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
package org.oscarehr.eform;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.oscarehr.util.MiscUtils;
import oscar.eform.EFormLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * This service should handle all eform html parsing interactions,
 * and is meant to eventually replace EFormUtils and the .data.EForm classes that do this.
 */
public class EFormHtmlParser
{
	private static Logger logger = MiscUtils.getLogger();

	private final Document soupDocument;

	public EFormHtmlParser()
	{
		this("");
	}
	public EFormHtmlParser(String htmlString)
	{
		soupDocument = Jsoup.parse(htmlString);
	}

	public Map<String, String> getElementNamesWithDatabaseTag()
	{
		return getElementNamesWithDatabaseTag(EFormLoader.getMarker());
	}
	public Map<String, String> getElementNamesWithUpdateDatabaseTag()
	{
		return getElementNamesWithDatabaseTag(EFormLoader.getUpdateMarker());
	}
	/**
	 * Retrieve a map of element names to database tag names.
	 */
	private Map<String, String> getElementNamesWithDatabaseTag(String marker)
	{
		Elements elements = soupDocument.select("["+ marker +"]");
		HashMap<String, String> nameTagMap = new HashMap<>(elements.size());

		for(Element element : elements)
		{
			String name = element.attr("name");
			String dbTagName = element.attr(marker);
			logger.debug("Element: " + element.id() + ", Name: " + name + ", dbTag: " + marker + ", dbTagValue: " + dbTagName);

			nameTagMap.put(name, dbTagName);
		}

		return nameTagMap;
	}
}
