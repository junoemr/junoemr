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
package org.oscarehr.dataMigration.converter.out;

import org.apache.commons.lang.NotImplementedException;
import org.oscarehr.common.dao.Icd9Dao;
import org.oscarehr.common.model.Dxresearch;
import org.oscarehr.common.model.Icd9;
import org.oscarehr.dataMigration.mapper.cds.CDSConstants;
import org.oscarehr.dataMigration.model.dx.DxRecord;
import org.oscarehr.dataMigration.service.context.PatientExportContext;
import org.oscarehr.dataMigration.service.context.PatientExportContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class DxDbToModelConverter extends BaseDbToModelConverter<Dxresearch, DxRecord>
{
	@Autowired
	private Icd9Dao icd9Dao;

	@Autowired
	protected PatientExportContextService patientExportContextService;

	@Override
	public DxRecord convert(Dxresearch input)
	{
		DxRecord dxRecord = new DxRecord();

		dxRecord.setId(input.getId());
		dxRecord.setStatus(DxRecord.Status.fromValue(input.getStatus()));
		dxRecord.setStartDate(ConversionUtils.toNullableLocalDate(input.getStartDate()));
		dxRecord.setUpdateDate(ConversionUtils.toLocalDateTime(input.getUpdateDate()));
		dxRecord.setProvider(findProvider(input.getProviderNo()));

		CDSConstants.CodingSystem codingSystem = CDSConstants.CodingSystem.fromValue(input.getCodingSystem());
		boolean valid = fillCodeInfo(dxRecord, codingSystem, input.getDxresearchCode());
		return (valid) ? dxRecord : null; // only return valid entries. null can be filtered out
	}

	// override to filter null values
	@Override
	public List<DxRecord> convert(Collection<Dxresearch> entities)
	{
		return entities.stream().map(this::convert).filter(Objects::nonNull).collect(Collectors.toList());
	}

	private boolean fillCodeInfo(DxRecord dxRecord, CDSConstants.CodingSystem codingSystem, String code)
	{
		if(codingSystem != null && code != null)
		{
			switch(codingSystem)
			{
				case ICD9: fillIcd9Info(dxRecord, code); break;
				default: throw new NotImplementedException(codingSystem + " dx conversion not implemented");
			}
		}
		return (dxRecord.getDxCode() != null);
	}

	private void fillIcd9Info(DxRecord dxRecord, String icd9Code)
	{
		Icd9 icd9 = icd9Dao.findByCode(icd9Code);
		if(icd9 != null)
		{
			dxRecord.setCodingSystem(CDSConstants.CodingSystem.ICD9);
			dxRecord.setDxCode(icd9.getCode());
			dxRecord.setCodeDescription(icd9.getDescription());
		}
		else
		{
			PatientExportContext context = patientExportContextService.getContext();
			String message = "Invalid icd9 code '" + icd9Code + "' could not be loaded";
			if(context != null)
			{
				context.getExportLogger().logEvent("[" + context.getCurrentProcessIdentifier() + "] " + message);
			}
		}
	}
}
