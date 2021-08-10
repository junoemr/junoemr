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
package org.oscarehr.common.hl7.copd.mapper.medaccess;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.oscarehr.common.hl7.copd.mapper.DocumentMapper;
import org.oscarehr.common.hl7.copd.model.v24.message.ZPD_ZTR;
import org.oscarehr.dataMigration.service.ImporterExporterFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DocumentMapperMedaccess extends DocumentMapper
{
	public DocumentMapperMedaccess(ZPD_ZTR message, int providerRep, ImporterExporterFactory.IMPORT_SOURCE importSource)
	{
		super(message, providerRep, importSource);
	}

	/**
	 * get, Juno document type. things like, Consult, Lab, Procedure, ect.
	 * For Medaccess imports the document type is stored (some times) on the front of the description as, <doctype>;<description>
	 * @param rep - the document you wish to get the type for.
	 * @return - the document type if possible else returns the string "N/A"
	 */
	@Override
	public String getDocType(int rep)
	{
		Matcher match = Pattern.compile("([\\d\\w\\s]+);.*").matcher(StringUtils.trimToEmpty(provider.getZAT(rep).getZat3_Name().getValue()));
		if (match.matches())
		{
			return match.group(1);
		}
		return "N/A";
	}

	/**
	 * get the document description with leading type name removed, semicolons replaced with dashes.
	 * @param rep - the rep to get the description for
	 * @return - the document description
	 */
	@Override
	public String getDescription(int rep)
	{
		String description = provider.getZAT(rep).getZat3_Name().getValue();


		Matcher stripType = Pattern.compile("[^;]+;(.+)").matcher(description);
		if (stripType.matches())
		{
			description = stripType.group(1);
		}
		description = StringUtils.stripEnd(description, ";");
		description = description.replace(";", " - ");
		return StringEscapeUtils.unescapeXml(description);
	}
}
