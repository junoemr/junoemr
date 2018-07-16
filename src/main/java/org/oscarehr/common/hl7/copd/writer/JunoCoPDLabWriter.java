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
package org.oscarehr.common.hl7.copd.writer;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v24.message.ORU_R01;
import ca.uhn.hl7v2.model.v24.segment.NTE;
import ca.uhn.hl7v2.model.v24.segment.OBR;
import ca.uhn.hl7v2.model.v24.segment.OBX;
import ca.uhn.hl7v2.model.v24.segment.PID;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.util.DeepCopy;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.oscarehr.common.hl7.copd.mapper.DemographicMapper;
import org.oscarehr.common.hl7.copd.model.v24.group.ZPD_ZTR_LAB;
import org.oscarehr.common.hl7.copd.model.v24.message.ZPD_ZTR;
import org.oscarehr.common.hl7.writer.HL7LabWriter;
import org.oscarehr.util.MiscUtils;
import oscar.util.ConversionUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.UUID;

public class JunoCoPDLabWriter extends HL7LabWriter
{
	public static final String SENDING_APP = "JUNO-COPD";
	private static final Logger logger = MiscUtils.getLogger();

	private ORU_R01 oru_r01;

	public JunoCoPDLabWriter(ZPD_ZTR message, ZPD_ZTR_LAB zpdZtrLab) throws IOException, HL7Exception
	{
		super(new ORU_R01(), new PipeParser());
		oru_r01 = (ORU_R01) this.message;

		// use the demographic mapper to pull some values into the new hl7, because the incoming data
		// doesn't always match the usual format
		DemographicMapper demographicMapper = new DemographicMapper(message);

		//initialize an MSH header segment with some custom CoPD values
		oru_r01.initQuickstart("ORU", "R01", "P");
		terser.set("/.MSH-3", SENDING_APP); // set sending application so lab can be identified
		terser.set("/.MSH-7", getLabDate(zpdZtrLab)); // set message date

		terser.set("/.ORC-3", generateAccessionNumber()); //set an accession number.


		//copy the CoPD incoming segment info to the newly created hl7 message segments
		PID pid = oru_r01.getPATIENT_RESULT().getPATIENT().getPID();
		DeepCopy.copy(message.getPATIENT().getPID(), pid);
		terser.set("/.PID-2", demographicMapper.getPHN());

		OBR obr = oru_r01.getPATIENT_RESULT().getORDER_OBSERVATION().getOBR();
		DeepCopy.copy(zpdZtrLab.getOBR(), obr);


		// copy observation info
		int obxReps = zpdZtrLab.getOBXReps();
		for(int i=0; i<obxReps; i++)
		{
			OBX obx = oru_r01.getPATIENT_RESULT().getORDER_OBSERVATION().getOBSERVATION(i).getOBX();
			DeepCopy.copy(zpdZtrLab.getOBX(i), obx);
		}

		// copy observation notes info
		int nteReps = zpdZtrLab.getNTEReps();
		for(int i=0; i<nteReps; i++)
		{
			NTE nte = oru_r01.getPATIENT_RESULT().getORDER_OBSERVATION().getOBSERVATION(i).getNTE();
			DeepCopy.copy(zpdZtrLab.getNTE(i), nte);
		}
	}

	private String getLabDate(ZPD_ZTR_LAB zpdZtrLab)
	{
		String dateString = StringUtils.trimToNull(zpdZtrLab.getOBR().getObr7_ObservationDateTime().getTs1_TimeOfAnEvent().getValue());
		if(dateString == null)
		{
			dateString = ConversionUtils.toDateString(new Date(), "yyyyMMdd");
		}
		return dateString;
	}

	private String generateAccessionNumber()
	{
		// generate a unique uuid to use as accession number of 36 characters
		UUID uuid = UUID.randomUUID();

		// base 64 encode the uuid to shorten the length to 22 characters (just happens to be the max length of ORC-3 in hl7 2.4)
		ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
		bb.putLong(uuid.getMostSignificantBits());
		bb.putLong(uuid.getLeastSignificantBits());
		String base64Key = Base64.encodeBase64URLSafeString(bb.array());

		if(base64Key.length() != 22)
		{
			logger.error("Invalid key length: " + base64Key.length() + "; key: " + base64Key);
		}
		return base64Key;
	}
}
