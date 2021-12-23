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

package org.oscarehr.integration.clinicaid.service.v2;

import org.oscarehr.integration.clinicaid.dto.v2.MasterNumber;
import org.oscarehr.integration.clinicaid.dto.v2.base.ClinicAidResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

@Service
@Scope(value = WebApplicationContext.SCOPE_SESSION)
@ConditionalOnProperty(value="billing_type", havingValue="CLINICAID")
public class ClinicAidService
{
	@Autowired
	ClinicAidCommunicationService communicator;
	
	public MasterNumber getOntarioMasterNumber(String masterNumber)
	{
		ClinicAidResponse<MasterNumber> response = communicator.getOntarioMasterNumber(masterNumber);
		return response.getData();
	}
}
