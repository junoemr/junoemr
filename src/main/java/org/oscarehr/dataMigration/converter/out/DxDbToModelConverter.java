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

import org.oscarehr.common.dao.Icd9Dao;
import org.oscarehr.common.model.Dxresearch;
import org.oscarehr.common.model.Icd9;
import org.oscarehr.dataMigration.model.dx.DxRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;

@Component
public class DxDbToModelConverter extends BaseDbToModelConverter<Dxresearch, DxRecord>
{
	@Autowired
	private Icd9Dao icd9Dao;

	@Override
	public DxRecord convert(Dxresearch input)
	{
		DxRecord dxRecord = new DxRecord();
		DxRecord.CodingSystem codingSystem = DxRecord.CodingSystem.fromValue(input.getCodingSystem());

		dxRecord.setId(input.getId());
		dxRecord.setCodingSystem(codingSystem);
		dxRecord.setDxCode(input.getDxresearchCode());
		dxRecord.setCodeDescription(getCodeDescription(codingSystem, input.getDxresearchCode()));
		dxRecord.setStatus(DxRecord.Status.fromValue(input.getStatus()));
		dxRecord.setStartDate(ConversionUtils.toNullableLocalDate(input.getStartDate()));
		dxRecord.setUpdateDate(ConversionUtils.toLocalDateTime(input.getUpdateDate()));
		dxRecord.setProvider(findProvider(input.getProviderNo()));

		return dxRecord;
	}

	private String getCodeDescription(DxRecord.CodingSystem codingSystem, String code)
	{
		String description = null;
		if(codingSystem != null && code != null)
		{
			switch(codingSystem)
			{
				case ICD9: {
					Icd9 icd9 = icd9Dao.findByCode(code);
					description = icd9.getDescription();
					break;
				}
			}
		}
		return description;
	}
}
