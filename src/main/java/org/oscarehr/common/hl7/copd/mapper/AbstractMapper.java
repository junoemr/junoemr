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
package org.oscarehr.common.hl7.copd.mapper;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.oscarehr.common.hl7.copd.model.v24.group.ZPD_ZTR_PROVIDER;
import org.oscarehr.common.hl7.copd.model.v24.message.ZPD_ZTR;
import org.oscarehr.dataMigration.service.ImporterExporterFactory;
import org.oscarehr.provider.model.ProviderData;
import oscar.util.ConversionUtils;

import java.util.Date;

public class AbstractMapper
{
	protected final Logger logger = Logger.getLogger(this.getClass());

	protected final ZPD_ZTR message;
	protected final ZPD_ZTR_PROVIDER provider;
	protected final ImporterExporterFactory.IMPORT_SOURCE importSource;

	protected AbstractMapper()
	{
		this.message = null;
		this.provider = null;
		this.importSource = ImporterExporterFactory.IMPORT_SOURCE.UNKNOWN;
	}
	protected AbstractMapper(ZPD_ZTR message, int providerRep, ImporterExporterFactory.IMPORT_SOURCE importSource)
	{
		this.message = message;
		this.provider = message.getPATIENT().getPROVIDER(providerRep);
		this.importSource = importSource;
	}
	protected AbstractMapper(ZPD_ZTR message, int providerRep)
	{
		this(message, providerRep, ImporterExporterFactory.IMPORT_SOURCE.UNKNOWN);
	}
	protected AbstractMapper(ZPD_ZTR message, ImporterExporterFactory.IMPORT_SOURCE importSource)
	{
		this.message = message;
		this.provider = null;
		this.importSource = importSource;
	}
	protected AbstractMapper(ZPD_ZTR message)
	{
		this(message, ImporterExporterFactory.IMPORT_SOURCE.UNKNOWN);
	}

	protected static Date getNullableDate(String segmentValue)
	{
		if(segmentValue==null || segmentValue.trim().isEmpty() || segmentValue.equals("00000000"))
		{
			return null;
		}

		//strip trailing time information, as we are only interested in the date.
		//if this is not done SimpleDateFormater will produce incorrect date strings WITHOUT throwing exceptions
		if (segmentValue.length() > 8)
		{
			segmentValue = segmentValue.substring(0, 8);
		}

		return ConversionUtils.fromDateString(segmentValue, "yyyyMMdd");
	}

	protected static Date getNullableDateTime(String segmentValue)
	{
		if(segmentValue==null || segmentValue.trim().isEmpty() || segmentValue.equals("00000000"))
		{
			return null;
		}

		//strip trailing time information, as we are only interested in the date + time
		//if this is not done SimpleDateFormater will produce incorrect date strings WITHOUT throwing exceptions
		if (segmentValue.length() > 16)
		{
			segmentValue = segmentValue.substring(0, 16);
		}

		Date newDate =  ConversionUtils.fromDateString(segmentValue, "yyyyMMddHHmmss");
		if (newDate == null)
		{
			if (segmentValue.length() > 8)
			{
				segmentValue = segmentValue.substring(0, 8);
			}
			newDate =  ConversionUtils.fromDateString(segmentValue, "yyyyMMdd");
		}
		return newDate;
	}

	/** Wolf puts provider names for a note in the form of 'first|last' in the comment signature.
	 *  Here we attempt to parse the names out and put them in a provider record */
	protected ProviderData getWOLFParsedProviderInfo(String segmentToParse, String debugLocation)
	{
		ProviderData parsedProvider = null;
		if(segmentToParse != null && segmentToParse.contains("|"))
		{
			String[] providerNames = segmentToParse.split("\\|", -1); // -1 forces all trailing empties to be included
			if(providerNames.length > 2)
			{
				logger.error("["+debugLocation+"] Malformed provider name contains too many delimiters: '" + segmentToParse + "'");
			}
			parsedProvider = new ProviderData();
			parsedProvider.setFirstName(StringUtils.trimToNull(providerNames[0]));
			parsedProvider.setLastName(StringUtils.trimToNull(providerNames[1]));
		}
		// often wolf just puts a space between provider names. this can still be matched if there is exactly 1 space in the signature
		else if(segmentToParse != null && segmentToParse.contains(" "))
		{
			String[] providerNames = segmentToParse.trim().split("\\s+");
			if(providerNames.length == 2)
			{
				parsedProvider = new ProviderData();
				parsedProvider.setFirstName(StringUtils.trimToNull(providerNames[0]));
				parsedProvider.setLastName(StringUtils.trimToNull(providerNames[1]));
			}
			else
			{
				logger.warn("["+debugLocation+"] WOLF provider data cannot be parsed (invalid whitespace): '"+segmentToParse+"'");
			}
		}
		else if(segmentToParse != null)
		{
			logger.error("["+debugLocation+"] WOLF provider data is malformed: '"+segmentToParse+"'");
		}
		else
		{
			/* Wolf exports their internal communication notes sometimes as notes without associated provider information. */
			logger.debug("["+debugLocation+"] WOLF provider data is empty.");
		}
		return parsedProvider;
	}
}
