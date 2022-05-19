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

package org.oscarehr.ws.external.rest.v1.conversion;

import org.oscarehr.demographic.entity.Demographic;
import org.oscarehr.ws.external.rest.v1.transfer.demographic.ApiDemographicListTransfer;
import org.oscarehr.ws.rest.to.model.DemographicSearchResult;
import oscar.util.ConversionUtils;

import java.util.ArrayList;
import java.util.List;

public class DemographicListConverter
{
	public static ApiDemographicListTransfer getAsTransferObject(DemographicSearchResult demographic)
	{
		ApiDemographicListTransfer transfer = new ApiDemographicListTransfer();

		// base info
		transfer.setDemographicNo(demographic.getDemographicNo());
		transfer.setFirstName(demographic.getFirstName());
		transfer.setLastName(demographic.getLastName());
		transfer.setDateOfBirth(ConversionUtils.toNullableLocalDate(demographic.getDob()));
		transfer.setSex(demographic.getSex());
		transfer.setHin(demographic.getHin());
		transfer.setPatientStatus(demographic.getPatientStatus());

		// physician info
		transfer.setProviderNo(demographic.getProviderNo());

		//other info
		transfer.setChartNo(demographic.getChartNo());

		return transfer;
	}

	public static ApiDemographicListTransfer getAsTransferObject(Demographic demographic)
	{
		ApiDemographicListTransfer transfer = new ApiDemographicListTransfer();

		// base info
		transfer.setDemographicNo(demographic.getDemographicId());
		transfer.setFirstName(demographic.getFirstName());
		transfer.setLastName(demographic.getLastName());
		transfer.setDateOfBirth(demographic.getDateOfBirth());
		transfer.setSex(demographic.getSex());
		transfer.setHin(demographic.getHin());
		transfer.setPatientStatus(demographic.getPatientStatus());

		// physician info
		transfer.setProviderNo(demographic.getProviderNo());

		//other info
		transfer.setChartNo(demographic.getChartNo());

		return transfer;
	}

	public static List<ApiDemographicListTransfer> getListAsTransferObjects(List<Demographic> demographics)
	{
		List<ApiDemographicListTransfer> response = new ArrayList<>(demographics.size());
		for(Demographic searchResult : demographics)
		{
			ApiDemographicListTransfer transfer = getAsTransferObject(searchResult);
			response.add(transfer);
		}
		return response;
	}
}
