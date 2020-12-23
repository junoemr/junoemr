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
package org.oscarehr.demographicImport.mapper.cds.out;

import org.apache.log4j.Logger;
import org.oscarehr.common.xml.cds.v5_0.model.ReportClass;
import org.oscarehr.common.xml.cds.v5_0.model.Reports;
import org.oscarehr.util.MiscUtils;
import org.springframework.stereotype.Component;

@Component
public abstract class AbstractCDSReportExportMapper<E> extends AbstractCDSExportMapper<Reports, E>
{
	public static final Logger logger = MiscUtils.getLogger();

	public AbstractCDSReportExportMapper()
	{
		super();
	}

	protected ReportClass toReportClass(String docClass)
	{
		ReportClass reportClass = ReportClass.OTHER_LETTER;
		try
		{
			if(docClass != null)
			{
				reportClass = ReportClass.fromValue(docClass);
			}
		}
		catch(IllegalArgumentException e)
		{
			logger.error("Invalid document class value: " + docClass);
		}
		return reportClass;
	}
}
