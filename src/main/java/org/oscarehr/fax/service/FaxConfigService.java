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
package org.oscarehr.fax.service;

import org.apache.log4j.Logger;
import org.oscarehr.fax.externalApi.srfax.SRFaxApiConnector;
import org.oscarehr.fax.externalApi.srfax.result.SRFaxResultWrapper_List;
import org.oscarehr.fax.externalApi.srfax.result.SRFaxResult_GetUsage;
import org.oscarehr.ws.rest.FaxConfigWebService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * This service should be responsible for handling all logic around fax setup and configuration
 */
@Service
@Transactional
public class FaxConfigService
{
	private static Logger logger = Logger.getLogger(FaxConfigService.class);

	/**
	 * Test the connection to the fax service based on the configuration settings
	 * @return true if the connection succeeded, false otherwise
	 */
	public boolean testConnectionStatus(String accountId, String password)
	{
		SRFaxApiConnector apiConnector = new SRFaxApiConnector(accountId, password);

		LocalDate localDate = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		String currentDateStr = localDate.format(formatter);


		Map<String, String> parameters = new HashMap<>();
		parameters.put("sPeriod", "RANGE");
		parameters.put("sStartDate", currentDateStr);
		parameters.put("sEndDate", currentDateStr);
		SRFaxResultWrapper_List<SRFaxResult_GetUsage> result = apiConnector.Get_Fax_Usage(parameters);

		logger.debug( result == null ? "null" : result.toString());

		return (result != null && result.isSuccess());
	}
}
