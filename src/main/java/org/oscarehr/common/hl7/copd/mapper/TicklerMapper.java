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

import ca.uhn.hl7v2.HL7Exception;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.oscarehr.common.hl7.copd.model.v24.group.ZPD_ZTR_PROVIDER;
import org.oscarehr.common.hl7.copd.model.v24.message.ZPD_ZTR;
import org.oscarehr.common.model.Tickler;
import org.oscarehr.demographicImport.service.CoPDImportService;
import org.oscarehr.util.MiscUtils;
import oscar.util.ConversionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TicklerMapper
{

	private static final Logger logger = MiscUtils.getLogger();
	private final ZPD_ZTR message;
	private final ZPD_ZTR_PROVIDER provider;

	private final CoPDImportService.IMPORT_SOURCE importSource;

	public TicklerMapper()
	{
		message = null;
		provider = null;
		importSource = CoPDImportService.IMPORT_SOURCE.UNKNOWN;
	}
	public TicklerMapper(ZPD_ZTR message, int providerRep, CoPDImportService.IMPORT_SOURCE importSource)
	{
		this.message = message;
		this.provider = message.getPATIENT().getPROVIDER(providerRep);
		this.importSource = importSource;
	}

	public int getNumTicklers()
	{
		return provider.getZFUReps();
	}

	public List<Tickler> getTicklerList() throws HL7Exception
	{
		int numTicklers = getNumTicklers();
		List<Tickler> ticklerList = new ArrayList<>(numTicklers);
		for(int i = 0; i < numTicklers; i++)
		{
			Tickler tickler = getTickler(i);
			if(tickler != null)
			{
				ticklerList.add(tickler);
			}
		}
		return ticklerList;
	}

	public Tickler getTickler(int rep) throws HL7Exception
	{
		Tickler tickler = null;

		// so far only WOLF has added tickler info
		if(importSource.equals(CoPDImportService.IMPORT_SOURCE.WOLF))
		{
			String ticklerText = getTicklerText(rep);
			if(ticklerText != null)
			{
				tickler = new Tickler();

				tickler.setMessage(ticklerText);
				tickler.setServiceDate(getFollowupDate(rep));
				tickler.setUpdateDate(getDate(rep));
				tickler.setStatus(getStatus(rep));
				tickler.setPriority(getPriority(rep));
			}
		}
		return tickler;
	}

	public Date getDate(int rep) throws HL7Exception
	{
		String dateStr = provider.getZFU(rep).getZfu3_date().getTs1_TimeOfAnEvent().getValue();
		if(dateStr==null || dateStr.trim().isEmpty() || dateStr.equals("00000000"))
		{
			return null;
		}
		return ConversionUtils.fromDateString(dateStr, "yyyyMMdd");
	}

	public Date getFollowupDate(int rep) throws HL7Exception
	{
		String dateStr = provider.getZFU(rep).getZfu4_followupDate().getTs1_TimeOfAnEvent().getValue();
		if(dateStr==null || dateStr.trim().isEmpty() || dateStr.equals("00000000"))
		{
			return null;
		}
		return ConversionUtils.fromDateString(dateStr, "yyyyMMdd");
	}

	public String getTicklerText(int rep) throws HL7Exception
	{
		return StringUtils.trimToNull(provider.getZFU(rep).getZfu5_followupProblem().getValue());
	}

	public Tickler.STATUS getStatus(int rep) throws HL7Exception
	{
		String status = StringUtils.trimToEmpty(provider.getZFU(rep).getZfu6_done().getValue());
		switch(status)
		{
			case "Y": return Tickler.STATUS.C; // completed
			default:
			case "N": return Tickler.STATUS.A; // active
		}
	}

	public Tickler.PRIORITY getPriority(int rep) throws HL7Exception
	{
		String urgency = StringUtils.trimToEmpty(provider.getZFU(rep).getZfu9_urgency().getValue());
		switch(urgency)
		{
			case "1": return Tickler.PRIORITY.High;
			default:
			case "2": return Tickler.PRIORITY.Normal;
			case "3": return Tickler.PRIORITY.Low;
		}
	}
}
