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

/*
 * AcknowledgementData.java
 *
 * Created on July 9, 2007, 11:49 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package oscar.oscarLab.ca.all;

import java.util.ArrayList;
import java.util.List;

import org.oscarehr.PMmodule.dao.ProviderDao;
import org.oscarehr.common.dao.ProviderLabRoutingDao;
import org.oscarehr.common.model.Provider;
import org.oscarehr.common.model.ProviderLabRoutingModel;
import org.oscarehr.util.SpringUtils;

import oscar.oscarMDS.data.ReportStatus;
import oscar.util.ConversionUtils;

public class AcknowledgementData {

	/** Creates a new instance of AcknowledgementData */
	private AcknowledgementData() {
		// don't instantiate
	}

	public static ArrayList<ReportStatus> getAcknowledgements(String segmentID) {
		return getAcknowledgements("HL7", segmentID);
	}

	public static ArrayList<ReportStatus> getAcknowledgements(String docType, String segmentID)
	{
		ProviderLabRoutingDao dao = (ProviderLabRoutingDao) SpringUtils.getBean(ProviderLabRoutingDao.class);
		ProviderDao providerDao = SpringUtils.getBean(ProviderDao.class);

		ArrayList<ReportStatus> acknowledgements = new ArrayList<ReportStatus>();

		List<ProviderLabRoutingModel> providerLabRoutings = dao.getProviderLabRoutings(ConversionUtils.fromIntString(segmentID), docType);

		for (ProviderLabRoutingModel routing : providerLabRoutings)
		{
			Provider provider = providerDao.getProvider(routing.getProviderNo());

			// Provider can be null. For example, the unclaimed inbox has a route for provider 0 which will never match to an actual provider record
			if (provider != null)
			{
				acknowledgements.add(new ReportStatus(provider.getFullName(), provider.getPractitionerNo(), provider.getProviderNo(), routing.getStatus(), routing.getComment(), ConversionUtils.toDateString(routing.getTimestamp(), ConversionUtils.DEFAULT_TS_PATTERN), segmentID));
			}
		}
		return acknowledgements;
	}
}
