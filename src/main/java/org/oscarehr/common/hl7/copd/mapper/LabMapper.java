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
import org.apache.commons.lang.StringUtils;
import org.oscarehr.common.hl7.copd.model.v24.group.ZPD_ZTR_LAB;
import org.oscarehr.common.hl7.copd.model.v24.message.ZPD_ZTR;
import org.oscarehr.common.hl7.copd.writer.JunoCoPDLabWriter;
import org.oscarehr.dataMigration.service.CoPDImportService;
import oscar.util.ConversionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class LabMapper extends AbstractMapper
{
	public LabMapper(ZPD_ZTR message, int providerRep, CoPDImportService.IMPORT_SOURCE importSource)
	{
		super(message, providerRep, importSource);
	}

	public int getNumLabs()
	{
		return provider.getLABReps();
	}

	public List<String> getLabList() throws IOException, HL7Exception
	{
		int numLabs = getNumLabs();

		/* In CoPD, each OBR has it's own lab group, but we want to combine labs when the accession numbers match.
		 * Here, we group all the labs by accession number */
		Map<String, List<ZPD_ZTR_LAB>> labAccessionMap = new HashMap<>();
		for(int i=0; i<numLabs; i++)
		{
			String accessionNo = getAccessionNumber(i);
			if(accessionNo == null)
			{
				/* accession number is not actually required by CoPD If it is not present
				each lab will be imported with a fake accession number, and each one will get imported as a separate lab */
				accessionNo = JunoCoPDLabWriter.generateRandomAccessionNumber();
			}

			ZPD_ZTR_LAB lab = provider.getLAB(i);
			if(labAccessionMap.containsKey(accessionNo))
			{
				labAccessionMap.get(accessionNo).add(lab);
			}
			else
			{
				List<ZPD_ZTR_LAB> labList = new LinkedList<>();
				labList.add(lab);
				labAccessionMap.put(accessionNo, labList);
			}
		}

		/* Now that the labs are grouped on accession number, we create the hl7 messages, and return them in a list */
		List<String> labMessages = new ArrayList<>(labAccessionMap.size());
		for(Map.Entry<String, List<ZPD_ZTR_LAB>> entry : labAccessionMap.entrySet())
		{
			String accessionNo = entry.getKey();
			List<ZPD_ZTR_LAB> labObservationList = entry.getValue();
			String labDate = getLabDate(labObservationList.get(0)); // use the first OBR result date as the lab date

			JunoCoPDLabWriter labWriter = new JunoCoPDLabWriter(message, accessionNo, labDate, labObservationList, importSource);
			String labHl7 = labWriter.encode().replaceAll("\\\\R\\\\crlf\\\\R\\\\", "\n");

			labMessages.add(labHl7);
		}
		return labMessages;
	}

	private String getAccessionNumber(int rep)
	{
		return StringUtils.trimToNull(provider.getLAB(rep).getOBR().getObr20_FillerField1().getValue());
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
}
