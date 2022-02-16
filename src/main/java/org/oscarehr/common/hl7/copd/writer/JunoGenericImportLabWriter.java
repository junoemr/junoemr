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
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.oscarehr.common.hl7.writer.HL7LabWriter;
import org.oscarehr.demographic.model.DemographicModel;
import org.oscarehr.dataMigration.model.common.PhoneNumberModel;
import org.oscarehr.dataMigration.model.lab.Lab;
import org.oscarehr.dataMigration.model.lab.LabObservation;
import org.oscarehr.dataMigration.model.lab.LabObservationResult;
import org.oscarehr.util.MiscUtils;
import oscar.util.ConversionUtils;

import java.io.IOException;

import static org.oscarehr.common.hl7.Hl7Const.ABNORMAL_FLAG_NO;
import static org.oscarehr.common.hl7.Hl7Const.ABNORMAL_FLAG_YES;
import static oscar.util.ConversionUtils.HL7_DATE_FORMAT;
import static oscar.util.ConversionUtils.HL7_DATE_TIME_DEFAULT_IN_PATTERN;

public class JunoGenericImportLabWriter extends HL7LabWriter
{
	public static final String SENDING_APP = "JUNO-LAB-IMPORT";
	private static final Logger logger = MiscUtils.getLogger();

	private ORU_R01 oru_r01;

	public JunoGenericImportLabWriter(DemographicModel demographic, Lab labModel) throws IOException, HL7Exception
	{
		super(new ORU_R01());
		oru_r01 = (ORU_R01) this.message;

		//initialize an MSH header segment with some custom values
		oru_r01.initQuickstart("ORU", "R01", "P");
		terser.set("/.MSH-3", SENDING_APP); // set sending application so lab can be identified
		terser.set("/.MSH-3-2", labModel.getSendingApplication()); // so it's not lost.
		terser.set("/.MSH-4", labModel.getSendingFacility());
		terser.set("/.MSH-7", ConversionUtils.toDateString(labModel.getMessageDateTime(), HL7_DATE_FORMAT)); // set message date

		// populate patient PID segment info from the demographic record
		buildPID(demographic);

		// populate lab info
		String accessionNumber = labModel.getAccessionNumber();
		if(accessionNumber == null)
		{
			accessionNumber = generateRandomAccessionNumber();
		}

		terser.set("/.ORC-1", "RE"); // Observations/Performed Service to follow
		terser.set("/.ORC-3", accessionNumber); //set an accession number.

		// add the list of OBR results to the lab
		int obrIndex = 0;
		for(LabObservation labObservation : labModel.getLabObservationList())
		{
			buildOBR(labObservation, obrIndex);
			obrIndex++;
		}
	}

	private void buildPID(DemographicModel demographic) throws HL7Exception
	{
		terser.set("/.PID-1", "1"); // force set the SetId

		// hin info
		terser.set("/.PID-2-1", demographic.getHealthNumber());
		terser.set("/.PID-2-2", demographic.getHealthNumberVersion());
		terser.set("/.PID-2-7", ConversionUtils.toDateString(demographic.getHealthNumberEffectiveDate(), HL7_DATE_FORMAT));
		terser.set("/.PID-2-8", ConversionUtils.toDateString(demographic.getHealthNumberRenewDate(), HL7_DATE_FORMAT));

		// names
		terser.set("/.PID-5-1", demographic.getLastName());
		terser.set("/.PID-5-2", demographic.getFirstName());
		terser.set("/.PID-5-5", demographic.getTitleString());

		// dob
		terser.set("/.PID-7", ConversionUtils.toDateString(demographic.getDateOfBirth(), HL7_DATE_FORMAT));
		terser.set("/.PID-8", demographic.getSexString());

		// phone numbers
		PhoneNumberModel homePhone = demographic.getHomePhone();
		PhoneNumberModel workPhone = demographic.getWorkPhone();
		if(homePhone == null)
		{
			homePhone = demographic.getCellPhone();
		}
		if(homePhone != null)
		{
			if(StringUtils.isNumeric(homePhone.getNumber()))
			{
				terser.set("/.PID-13-1", homePhone.getNumberFormattedHL7());
			}
			if(StringUtils.isNumeric(homePhone.getExtension()))
			{
				terser.set("/.PID-13-8", homePhone.getExtension());
			}
		}
		if(workPhone != null)
		{
			if(StringUtils.isNumeric(workPhone.getNumber()))
			{
				terser.set("/.PID-14-1", workPhone.getNumberFormattedHL7());
			}
			if(StringUtils.isNumeric(workPhone.getExtension()))
			{
				terser.set("/.PID-14-8", workPhone.getExtension());
			}
		}
	}

	private void buildOBR(LabObservation observation, int obrIndex) throws HL7Exception
	{
		String obrPath = "/.ORDER_OBSERVATION("+obrIndex+")/";

		terser.set(obrPath + "OBR-1", String.valueOf(obrIndex+1)); // force set the SetId

		String universalServiceIdName = "Imported lab";
		if(observation.getName() != null)
		{
			universalServiceIdName = observation.getName();
		}
		terser.set(obrPath + "OBR-4-2", universalServiceIdName);
		terser.set(obrPath + "OBR-6-1", ConversionUtils.toDateTimeString(observation.getRequestDateTime(), HL7_DATE_TIME_DEFAULT_IN_PATTERN));
		terser.set(obrPath + "OBR-7-1", ConversionUtils.toDateTimeString(observation.getObservationDateTime(), HL7_DATE_TIME_DEFAULT_IN_PATTERN));
		terser.set(obrPath + "OBR-25-1", observation.getReportStatus().name());
		terser.set(obrPath + "OBR-44-1", observation.getProcedureCode());

		if(observation.isBlockedResult())
		{
			terser.set(obrPath + "OBR-47(0)-1", JunoLabCode.BLOCKED.name());
			terser.set(obrPath + "OBR-47(0)-2", JunoLabCode.BLOCKED.getDescription());
			terser.set(obrPath + "OBR-47(0)-3", JunoLabCode.CODING_SYSTEM);
		}


		// results info
		int obxIndex = 0;
		for(LabObservationResult labObservationResult : observation.getResults())
		{
			buildOBX(labObservationResult, obrIndex, obxIndex);
			obxIndex++;
		}

		// notes info
		int nteIndex = 0;
		for(String noteText : observation.getComments())
		{
			String ntePath = "/.ORDER_OBSERVATION(" + obrIndex + ")/.NTE(" + nteIndex + ")-";
			terser.set(ntePath + "1", String.valueOf(nteIndex + 1)); // force set the SetId
			terser.set(ntePath + "3", noteText);
			nteIndex++;
		}
	}

	private void buildOBX(LabObservationResult result, int obrIndex, int obxIndex) throws HL7Exception
	{
		String obxPath = "/.ORDER_OBSERVATION(" + obrIndex + ")/.OBSERVATION(" + obxIndex + ")/";

		terser.set(obxPath + "OBX-1", String.valueOf(obxIndex+1)); // force set the SetId
		terser.set(obxPath + "OBX-2", "ST"); // specify the data type of OBX-5 as string
		terser.set(obxPath + "OBX-3-1", result.getIdentifier());
		terser.set(obxPath + "OBX-3-2", result.getName());
		terser.set(obxPath + "OBX-5-1", result.getValue());
		terser.set(obxPath + "OBX-6-1", result.getUnits());
		terser.set(obxPath + "OBX-7-1", result.getRange());

		// if there is not lab status set to normal.
		Boolean isAbnormal = result.getAbnormal();
		String abnormalValue = (isAbnormal != null && isAbnormal) ? ABNORMAL_FLAG_YES : ABNORMAL_FLAG_NO;
		terser.set(obxPath + "OBX-8-1", abnormalValue);

		terser.set(obxPath + "OBX-11-1", result.getResultStatus());
		terser.set(obxPath + "OBX-14-1", ConversionUtils.toDateTimeString(result.getObservationDateTime(), HL7_DATE_TIME_DEFAULT_IN_PATTERN));


		// notes info
		int nteIndex = 0;
		for(String noteText : result.getComments())
		{
			String ntePath = obxPath + ".NTE(" + nteIndex + ")-";
			terser.set(ntePath + "1", String.valueOf(nteIndex + 1)); // force set the SetId
			terser.set(ntePath + "3", noteText);
			nteIndex++;
		}
	}
}
