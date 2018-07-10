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
import org.apache.log4j.Logger;
import org.oscarehr.common.hl7.copd.model.v24.group.ZPD_ZTR_LAB;
import org.oscarehr.common.hl7.copd.model.v24.group.ZPD_ZTR_PATIENT;
import org.oscarehr.common.hl7.copd.model.v24.group.ZPD_ZTR_PROVIDER;
import org.oscarehr.common.hl7.copd.model.v24.message.ZPD_ZTR;
import org.oscarehr.common.hl7.copd.writer.JunoCoPDLabWriter;
import org.oscarehr.util.MiscUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LabMapper
{
	private static final Logger logger = MiscUtils.getLogger();
	private final ZPD_ZTR message;
	private final ZPD_ZTR_PROVIDER provider;

	public LabMapper()
	{
		message = null;
		provider = null;
	}
	public LabMapper(ZPD_ZTR message, int providerRep)
	{
		this.message = message;
		this.provider = message.getPATIENT().getPROVIDER(providerRep);
	}

	public int getNumLabs()
	{
		return provider.getLABReps();
	}

	public List<String> getLabList() throws IOException, HL7Exception
	{
		int numLabs = getNumLabs();
		List<String> labMessages = new ArrayList<>(numLabs);

		for(int i=0; i<numLabs; i++)
		{
			labMessages.add(getLabMessage(i));
		}
		return labMessages;
	}

	public String getLabMessage(int rep) throws IOException, HL7Exception
	{
		ZPD_ZTR_PATIENT patient = message.getPATIENT();
		ZPD_ZTR_LAB lab = provider.getLAB(rep);

		JunoCoPDLabWriter labWriter = new JunoCoPDLabWriter(patient, lab);
		return labWriter.encode().replaceAll("~crlf~", "\n");
	}
}
