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

import org.oscarehr.common.xml.cds.v5_0.model.ResidualInformation;
import org.oscarehr.demographicImport.mapper.cds.CDSConstants;
import org.oscarehr.demographicImport.model.common.PartialDate;
import org.oscarehr.demographicImport.service.ExportPreferences;

import java.math.BigInteger;

public abstract class AbstractCDSNoteExportMapper<I, E> extends AbstractCDSExportMapper<I, E>
{
	public AbstractCDSNoteExportMapper()
	{
		this(null);
	}
	public AbstractCDSNoteExportMapper(ExportPreferences exportPreferences)
	{
		super(exportPreferences);
	}

	protected BigInteger getAgeAtOnset(Long onsetAge)
	{
		if(onsetAge != null)
		{
			return BigInteger.valueOf(onsetAge);
		}
		return null;
	}

	protected void addNonNullDataElements(ResidualInformation residualInformation, CDSConstants.RESIDUAL_INFO_DATA_TYPE dataType, String name, String value)
	{
		if(value != null && !value.isEmpty())
		{
			residualInformation.getDataElement().add(createResidualInfoDataElement(dataType, name, value));
		}
	}
	protected void addNonNullDataElements(ResidualInformation residualInformation, String name, PartialDate partialDate)
	{
		if(partialDate != null)
		{
			CDSConstants.RESIDUAL_INFO_DATA_TYPE dataType;
			if(partialDate.isFullDate())
			{
				dataType = CDSConstants.RESIDUAL_INFO_DATA_TYPE.DATE;
			}
			else
			{
				dataType = CDSConstants.RESIDUAL_INFO_DATA_TYPE.DATE_PARTIAL;
			}
			addNonNullDataElements(residualInformation, dataType, name, partialDate.toISOString());
		}
	}
}
