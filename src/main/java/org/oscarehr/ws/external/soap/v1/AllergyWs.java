/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */

package org.oscarehr.ws.external.soap.v1;

import org.apache.cxf.annotations.GZIP;
import org.oscarehr.allergy.model.Allergy;
import org.oscarehr.managers.AllergyManager;
import org.oscarehr.security.model.Permission;
import org.oscarehr.ws.external.soap.v1.transfer.AllergyTransfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jws.WebService;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@WebService
@Component
@GZIP(threshold = AbstractWs.GZIP_THRESHOLD)
public class AllergyWs extends AbstractWs
{
	@Autowired
	private AllergyManager allergyManager;

	public AllergyTransfer getAllergy(Integer allergyId)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.ALLERGY_READ);

		Allergy allergy = allergyManager.getAllergy(getLoggedInInfo(), allergyId);
		return (AllergyTransfer.toTransfer(allergy));
	}

	public AllergyTransfer[] getAllergiesUpdatedAfterDate(Date updatedAfterThisDateInclusive, int itemsToReturn)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.ALLERGY_READ);

		List<Allergy> allergies = allergyManager.getUpdatedAfterDate(getLoggedInInfo(), updatedAfterThisDateInclusive, itemsToReturn);
		return (AllergyTransfer.toTransfers(allergies));
	}

	public AllergyTransfer[] getAllergiesByProgramProviderDemographicDate(Integer programId, String providerNo, Integer demographicId, Calendar updatedAfterThisDateInclusive, int itemsToReturn)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(),Permission.ALLERGY_READ);

		List<Allergy> allergies = allergyManager.getAllergiesByProgramProviderDemographicDate(getLoggedInInfo(), programId, providerNo, demographicId, updatedAfterThisDateInclusive, itemsToReturn);
		return (AllergyTransfer.toTransfers(allergies));
	}
}
