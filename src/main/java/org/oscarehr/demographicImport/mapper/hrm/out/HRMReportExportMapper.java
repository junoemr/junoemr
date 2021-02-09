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
package org.oscarehr.demographicImport.mapper.hrm.out;

import org.apache.log4j.Logger;
import org.oscarehr.common.xml.hrm.v4_3.model.ReportClass;
import org.oscarehr.common.xml.hrm.v4_3.model.ReportsReceived;
import org.oscarehr.demographicImport.model.hrm.HrmDocument;
import org.springframework.stereotype.Component;

@Component
public class HRMReportExportMapper extends AbstractHRMExportMapper<ReportsReceived, HrmDocument>
{
	private static final Logger logger = Logger.getLogger(HRMReportExportMapper.class);

	public HRMReportExportMapper()
	{
		super();
	}

	@Override
	public ReportsReceived exportFromJuno(HrmDocument exportStructure)
	{
		ReportsReceived reportsReceived = objectFactory.createReportsReceived();
		//TODO

		reportsReceived.setClazz(toReportClass(exportStructure.getReportClass()));

		return reportsReceived;
	}

	protected ReportClass toReportClass(HrmDocument.REPORT_CLASS exportClass)
	{
		ReportClass reportClass = ReportClass.OTHER_LETTER;
		if(exportClass != null)
		{
			reportClass = ReportClass.fromValue(exportClass.getValue());
		}
		return reportClass;
	}
}
