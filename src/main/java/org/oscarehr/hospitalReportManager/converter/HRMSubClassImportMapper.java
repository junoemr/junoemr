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

package org.oscarehr.hospitalReportManager.converter;

import org.oscarehr.dataMigration.model.hrm.HrmCategoryModel;
import org.oscarehr.dataMigration.model.hrm.HrmSubClassModel;
import org.oscarehr.hospitalReportManager.transfer.HRMSubClassTransferInbound;
import org.springframework.stereotype.Component;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class HRMSubClassImportMapper
{
	public List<HrmSubClassModel> convert(Collection<HRMSubClassTransferInbound> transfers, HrmCategoryModel parent)
	{
		return transfers.stream().map(transfer -> convert(transfer, parent)).collect(Collectors.toList());;
	}

	public HrmSubClassModel convert(HRMSubClassTransferInbound transfer, Integer parentId)
	{
		HrmSubClassModel model = new HrmSubClassModel();
		model.setHrmCategoryId(parentId);
		model.setFacilityNumber(transfer.getFacilityNumber());
		model.setClassName(transfer.getClassName());
		model.setSubClassName(transfer.getSubClassName());
		model.setAccompanyingSubClassName(transfer.getAccompanyingSubClassName());

		return model;
	}


	public HrmSubClassModel convert(HRMSubClassTransferInbound transfer, HrmCategoryModel parent)
	{
		return convert(transfer, parent.getId());
	}
}