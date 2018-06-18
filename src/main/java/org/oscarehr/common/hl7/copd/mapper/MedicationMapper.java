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
import org.apache.log4j.Logger;
import org.oscarehr.common.hl7.copd.model.v24.group.ZPD_ZTR_PROVIDER;
import org.oscarehr.common.hl7.copd.model.v24.message.ZPD_ZTR;
import org.oscarehr.util.MiscUtils;

public class MedicationMapper
{
	private static final Logger logger = MiscUtils.getLogger();
	private final ZPD_ZTR message;
	private final ZPD_ZTR_PROVIDER provider;

	public MedicationMapper()
	{
		message = null;
		provider = null;
	}
	public MedicationMapper(ZPD_ZTR message, int providerRep)
	{
		this.message = message;
		this.provider = message.getPATIENT().getPROVIDER(providerRep);

		try
		{
			Structure[] struct = provider.getMEDS(0).getAll("ORC");
			logger.info("found " + struct.length + " structures for ORC");

			logger.info("ORC date/time: " + provider.getMEDS(0).getORC().getDateTimeOfTransaction().getTs1_TimeOfAnEvent().getValue());

			Structure[] struct2 = provider.getMEDS(0).getAll("RXO");
			logger.info("found " + struct2.length + " structures for RXO");

			Structure[] struct3 = provider.getMEDS(0).getAll("TIMING_QUANTITY");
			logger.info("found " + struct3.length + " structures for TIMING_QUANTITY");

			Structure[] struct4 = provider.getMEDS(0).getAll("NOTES");
			logger.info("found " + struct4.length + " structures for NOTES");

			Structure[] struct5 = provider.getMEDS(0).getAll("RXE");
			logger.info("found " + struct5.length + " structures for RXE");

			Structure[] struct6 = provider.getMEDS(0).getAll("RXR");
			logger.info("found " + struct6.length + " structures for RXR");

			Structure[] struct7 = provider.getMEDS(0).getAll("COMPONENT");
			logger.info("found " + struct7.length + " structures for COMPONENT");

			Structure[] struct8 = provider.getMEDS(0).getAll("OBSERVATION");
			logger.info("found " + struct8.length + " structures for OBSERVATION");

			Structure[] struct9 = provider.getMEDS(0).getAll("ZRX");
			logger.info("found " + struct9.length + " structures for ZRX");

			Structure[] struct10 = provider.getMEDS(0).getAll("ZST");
			logger.info("found " + struct10.length + " structures for ZST");
		}
		catch(HL7Exception e)
		{
			logger.error("Error", e);
		}
	}
}
