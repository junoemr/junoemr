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
import ca.uhn.hl7v2.model.Structure;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.oscarehr.common.hl7.copd.model.v24.message.ZPD_ZTR;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.util.MiscUtils;

public class ProviderMapper
{
	private static final Logger logger = MiscUtils.getLogger();
	private final ZPD_ZTR message;

	public ProviderMapper()
	{
		message = null;
	}
	public ProviderMapper(ZPD_ZTR message)
	{
		this.message = message;

		try
		{
			Structure[] struct = message.getPATIENT().getPROVIDER(0).getAll("PRD");
			logger.info("found " + struct.length + " structures for PRD");

			Structure[] struct2 = message.getPATIENT().getPROVIDER(0).getAll("ZSH");
			logger.info("found " + struct2.length + " structures for ZSH");

			Structure[] struct3 = message.getPATIENT().getPROVIDER(0).getAll("ZPV");
			logger.info("found " + struct3.length + " structures for ZPV");

			Structure[] struct4 = message.getPATIENT().getPROVIDER(0).getAll("MEDS");
			logger.info("found " + struct4.length + " structures for MEDS");
		}
		catch(HL7Exception e)
		{
			logger.error("Error", e);
		}

		int medReps = message.getPATIENT().getPROVIDER(0).getMEDSReps();
		logger.info("Found MEDS Reps:" + medReps);

		try
		{
			Structure[] struct = message.getPATIENT().getPROVIDER(0).getAll("ZPR");
			logger.info("found " + struct.length + " structures for ZPR");

			Structure[] struct2 = message.getPATIENT().getPROVIDER(0).getAll("ZDV");
			logger.info("found " + struct2.length + " structures for ZDV");

			Structure[] struct3 = message.getPATIENT().getPROVIDER(0).getAll("ZHR");
			logger.info("found " + struct3.length + " structures for ZHR");

			Structure[] struct4 = message.getPATIENT().getPROVIDER(0).getAll("LAB");
			logger.info("found " + struct4.length + " structures for LAB");
		}
		catch(HL7Exception e)
		{
			logger.error("Error", e);
		}

		int labReps = message.getPATIENT().getPROVIDER(0).getLABReps();
		logger.info("Found LAB Reps:" + labReps);

		try
		{
			Structure[] struct = message.getPATIENT().getPROVIDER(0).getAll("ZHF");
			logger.info("found " + struct.length + " structures for ZHF");

			Structure[] struct2 = message.getPATIENT().getPROVIDER(0).getAll("ZCP");
			logger.info("found " + struct2.length + " structures for ZCP");

			Structure[] struct3 = message.getPATIENT().getPROVIDER(0).getAll("ZAT");
			logger.info("found " + struct3.length + " structures for ZAT");
		}
		catch(HL7Exception e)
		{
			logger.error("Error", e);
		}
	}

	/* Methods for converting to oscar model */

	public ProviderData getProvider(int recordRep) throws HL7Exception
	{
		ProviderData provider = new ProviderData();
		provider.setFirstName(getFirstName(recordRep));
		provider.setLastName(getLastName(recordRep));
		return provider;
	}

	public int getNumProviders()
	{
		return message.getPATIENT().getPROVIDERReps();
	}

	/* Methods for accessing various values in the import message */

	public String getFirstName(int recordRep) throws HL7Exception
	{
		return StringUtils.trimToNull(message.getPATIENT().getPROVIDER(recordRep).getPRD().getProviderName(0).getGivenName().getValue());
	}
	public String getLastName(int recordRep) throws HL7Exception
	{
		return StringUtils.trimToNull(message.getPATIENT().getPROVIDER(recordRep).getPRD().getProviderName(0).getFamilyName().getSurname().getValue());
	}
}
