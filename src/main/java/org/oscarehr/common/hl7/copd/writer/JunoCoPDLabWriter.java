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
import ca.uhn.hl7v2.model.v24.datatype.CE;
import ca.uhn.hl7v2.model.v24.datatype.IS;
import ca.uhn.hl7v2.model.v24.message.ORU_R01;
import ca.uhn.hl7v2.model.v24.segment.NTE;
import ca.uhn.hl7v2.model.v24.segment.OBR;
import ca.uhn.hl7v2.model.v24.segment.OBX;
import ca.uhn.hl7v2.model.v24.segment.PID;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.util.DeepCopy;
import org.apache.log4j.Logger;
import org.oscarehr.common.hl7.copd.mapper.DemographicMapper;
import org.oscarehr.common.hl7.copd.mapper.MapperFactory;
import org.oscarehr.common.hl7.copd.model.v24.group.ZPD_ZTR_LAB;
import org.oscarehr.common.hl7.copd.model.v24.message.ZPD_ZTR;
import org.oscarehr.common.hl7.writer.HL7LabWriter;
import org.oscarehr.dataMigration.service.ImporterExporterFactory;
import org.oscarehr.util.MiscUtils;

import java.io.IOException;
import java.util.List;

public class JunoCoPDLabWriter extends HL7LabWriter
{
	public static final String SENDING_APP = "JUNO-COPD";
	private static final Logger logger = MiscUtils.getLogger();

	private ORU_R01 oru_r01;

	public JunoCoPDLabWriter(ZPD_ZTR message, String accessionNumber, String labDateString,
							 List<ZPD_ZTR_LAB> zpdZtrLabList, ImporterExporterFactory.IMPORT_SOURCE importSource) throws IOException, HL7Exception
	{
		super(new ORU_R01(), new PipeParser());
		oru_r01 = (ORU_R01) this.message;

		// use the demographic mapper to pull some values into the new hl7, because the incoming data
		// doesn't always match the usual format
		DemographicMapper demographicMapper = MapperFactory.newDemographicMapper(message, importSource);

		//initialize an MSH header segment with some custom CoPD values
		oru_r01.initQuickstart("ORU", "R01", "P");
		terser.set("/.MSH-3", SENDING_APP); // set sending application so lab can be identified
		terser.set("/.MSH-7", labDateString); // set message date

		//copy the CoPD incoming segment info to the newly created hl7 message segments
		PID pid = oru_r01.getPATIENT_RESULT().getPATIENT().getPID();
		DeepCopy.copy(message.getPATIENT().getPID(), pid);
		terser.set("/.PID-2", demographicMapper.getPHN());

		if(accessionNumber == null)
		{
			accessionNumber = generateRandomAccessionNumber();
		}
		terser.set("/.ORC-3", accessionNumber); //set an accession number.

		// add the list of OBR results to the lab
		int ObrIndex = 0;
		for(ZPD_ZTR_LAB zpdZtrLab : zpdZtrLabList)
		{
			OBR obr = oru_r01.getPATIENT_RESULT().getORDER_OBSERVATION(ObrIndex).getOBR();
			DeepCopy.copy(zpdZtrLab.getOBR(), obr);
			terser.set("/.ORDER_OBSERVATION("+ObrIndex+")/OBR-1", String.valueOf(ObrIndex+1)); // force set the SetId
			if (obr.getObr4_UniversalServiceIdentifier().getCe2_Text().getValue().isEmpty())
			{
				terser.set("/.ORDER_OBSERVATION("+ObrIndex+")/OBR-4-2", "Imported lab");
			}

			// copy observation info
			int obxReps = zpdZtrLab.getOBXReps();
			for(int i = 0; i < obxReps; i++)
			{
				OBX obx = oru_r01.getPATIENT_RESULT().getORDER_OBSERVATION(ObrIndex).getOBSERVATION(i).getOBX();
				DeepCopy.copy(zpdZtrLab.getOBX(i), obx);

				// if there is not lab status set to normal.
				IS abnFlags = obx.getAbnormalFlags();
				if ( abnFlags == null || abnFlags.isEmpty())
				{
					terser.set("/.ORDER_OBSERVATION(" + ObrIndex + ")/.OBSERVATION(" + i + ")/OBX-8", "N");
				}

				// if there is not identifier use name as id.
				CE identifier = obx.getObservationIdentifier();
				if (identifier.getCe1_Identifier().getValue() == null || identifier.getCe1_Identifier().getValue().isEmpty())
				{
					terser.set("/.ORDER_OBSERVATION(" + ObrIndex + ")/OBSERVATION(" + i + ")/OBX-3-1", obx.getObservationIdentifier().getCe2_Text().getValue());
				}
			}

			// copy observation notes info
			int nteReps = zpdZtrLab.getNTEReps();
			for(int i = 0; i < nteReps; i++)
			{
				NTE nte = oru_r01.getPATIENT_RESULT().getORDER_OBSERVATION(ObrIndex).getOBSERVATION(i).getNTE();
				DeepCopy.copy(zpdZtrLab.getNTE(i), nte);
			}
			ObrIndex++;
		}
	}
}
