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
import org.oscarehr.demographicImport.service.CoPDImportService;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.util.MiscUtils;
import oscar.util.ConversionUtils;

import java.util.Date;

public abstract class AbstractMapper
{
	private static final Logger logger = MiscUtils.getLogger();

	protected final ZPD_ZTR message;
	protected final ZPD_ZTR_PROVIDER provider;
	protected final CoPDImportService.IMPORT_SOURCE importSource;

	public AbstractMapper()
	{
		this.message = null;
		this.provider = null;
		this.importSource = CoPDImportService.IMPORT_SOURCE.UNKNOWN;
	}
	public AbstractMapper(ZPD_ZTR message, int providerRep, CoPDImportService.IMPORT_SOURCE importSource)
	{
		this.message = message;
		this.provider = message.getPATIENT().getPROVIDER(providerRep);
		this.importSource = importSource;
	}
	public AbstractMapper(ZPD_ZTR message, int providerRep)
	{
		this(message, providerRep, CoPDImportService.IMPORT_SOURCE.UNKNOWN);
	}
	public AbstractMapper(ZPD_ZTR message, CoPDImportService.IMPORT_SOURCE importSource)
	{
		this.message = message;
		this.provider = null;
		this.importSource = importSource;
	}
	public AbstractMapper(ZPD_ZTR message)
	{
		this(message, CoPDImportService.IMPORT_SOURCE.UNKNOWN);
	}

	protected static Date getNullableDate(String segmentValue)
	{
		if(segmentValue==null || segmentValue.trim().isEmpty() || segmentValue.equals("00000000"))
		{
			return null;
		}
		return ConversionUtils.fromDateString(segmentValue, "yyyyMMdd");
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
