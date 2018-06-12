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
package org.oscarehr.common.hl7.copd.mapper;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v24.segment.PRD;
import org.apache.commons.lang.StringUtils;
import org.oscarehr.common.hl7.copd.model.v24.message.ZPD_ZTR;
import org.oscarehr.common.model.ProviderData;

public class ProviderMapper
{
	private final ZPD_ZTR message;
	private final PRD messagePRD;

	public ProviderMapper()
	{
		message = null;
		messagePRD = null;
	}
	public ProviderMapper(ZPD_ZTR message)
	{
		this.message = message;
		this.messagePRD = message.getPATIENT().getPROVIDER().getPRD();
	}

	/* Methods for converting to oscar model */

	public ProviderData getProvider() throws HL7Exception
	{
		ProviderData provider = new ProviderData();
		provider.setFirstName(getFirstName(0));
		provider.setLastName(getLastName(0));
		return provider;
	}

	public boolean hasProviderInfo()
	{
		return (messagePRD != null);
	}

	/* Methods for accessing various values in the import message */

	public String getFirstName(int rep) throws HL7Exception
	{
		return StringUtils.trimToNull(messagePRD.getProviderName(rep).getGivenName().getValue());
	}
	public String getLastName(int rep) throws HL7Exception
	{
		return StringUtils.trimToNull(messagePRD.getProviderName(rep).getFamilyName().getSurname().getValue());
	}
}
