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

import org.oscarehr.common.xml.cds.v5_0.model.DateTimeFullOrPartial;
import org.oscarehr.common.xml.cds.v5_0.model.ObjectFactory;
import org.oscarehr.common.xml.cds.v5_0.model.ResidualInformation;
import org.oscarehr.demographicImport.mapper.AbstractExportMapper;
import org.oscarehr.demographicImport.mapper.cds.CDSConstants;
import org.oscarehr.demographicImport.service.ExportPreferences;
import oscar.util.ConversionUtils;

import java.time.LocalDateTime;

public abstract class AbstractCDSExportMapper<I, E> extends AbstractExportMapper<I, E>
{
	protected final ObjectFactory objectFactory;

	public AbstractCDSExportMapper()
	{
		this(null);
	}
	public AbstractCDSExportMapper(ExportPreferences exportPreferences)
	{
		super(exportPreferences);
		this.objectFactory = new ObjectFactory();
	}


	public ObjectFactory getObjectFactory()
	{
		return this.objectFactory;
	}

	/* ==== common helper methods for cds ==== */

	protected ResidualInformation.DataElement createResidualInfoDataElement(CDSConstants.RESIDUAL_INFO_DATA_TYPE dataType, String name, String value)
	{
		ResidualInformation.DataElement dataElement = objectFactory.createResidualInformationDataElement();
		dataElement.setDataType(dataType.name());
		dataElement.setName(name);
		dataElement.setContent(value);

		return dataElement;
	}

	protected DateTimeFullOrPartial toFullDateTime(LocalDateTime localDateTime)
	{
		DateTimeFullOrPartial dateTimeFullOrPartial = objectFactory.createDateTimeFullOrPartial();
		dateTimeFullOrPartial.setFullDateTime(ConversionUtils.toNullableXmlGregorianCalendar(localDateTime));
		return dateTimeFullOrPartial;
	}
}
