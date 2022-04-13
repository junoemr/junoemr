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
package oscar.oscarLab.ca.all.parsers.other;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v24.message.ORU_R01;
import ca.uhn.hl7v2.model.v24.segment.MSH;
import org.oscarehr.common.hl7.copd.writer.JunoCoPDLabWriter;
import org.oscarehr.common.hl7.copd.writer.JunoGenericImportLabWriter;
import org.oscarehr.common.hl7.copd.writer.JunoLabCode;
import oscar.oscarLab.ca.all.parsers.messageTypes.ORU_R01MessageHandler;

public class JunoGenericLabHandler extends ORU_R01MessageHandler
{
	public static final String LAB_TYPE_VALUE = "JUNO-LAB";
	public static final String DEFAULT_OBX_NAME = "Unlabelled Test";

	public static boolean handlerTypeMatch(Message message)
	{
		String version = message.getVersion();
		if(version.equals("2.4"))
		{
			ORU_R01 msh = (ORU_R01) message;
			MSH messageHeaderSegment = msh.getMSH();

			String sendingApplication = messageHeaderSegment.getMsh3_SendingApplication().getHd1_NamespaceID().getValue();

			return
				sendingApplication.equals(JunoCoPDLabWriter.SENDING_APP) ||
				sendingApplication.equals(JunoGenericImportLabWriter.SENDING_APP);
		}
		return false;
	}

	public JunoGenericLabHandler()
	{
		super();
	}
	public JunoGenericLabHandler(Message msg) throws HL7Exception
	{
		super(msg);
	}

	@Override
	public String preUpload(String hl7Message) throws HL7Exception
	{
		return hl7Message;
	}

	@Override
	public boolean canUpload()
	{
		return true;
	}

	@Override
	public void postUpload()
	{

	}

	@Override
	public void init(String hl7Body) throws HL7Exception
	{
	}

	/* ===================================== MSH ====================================== */

	@Override
	public String getMsgType()
	{
		return LAB_TYPE_VALUE;
	}

	@Override
	public String getServiceDate()
	{
		return getMsgDate();
	}

	@Override
	public String getPatientLocation()
	{
		String location = getString(get("/.MSH-4")); // sending facility
		if(location.isEmpty())
		{
			location = "JUNOGenericLab"; // default if there is no data
		}
		return location;
	}

	@Override
	public String getUniqueIdentifier()
	{
		return get("/.ORC-3");
	}

	/* ===================================== OBR ====================================== */

	@Override
	public int getOBRCount()
	{
		return getReps("PATIENT_RESULT", 0, "ORDER_OBSERVATION");
	}



	@Override
	public boolean isUnstructured()
	{
		return false;
	}

	@Override
	public boolean isOBRBlocked(int obr)
	{
		try
		{
			ORU_R01 msg = (ORU_R01) message;
			int obr47Reps = msg.getPATIENT_RESULT().getORDER_OBSERVATION(obr).getOBR()
				.getObr47_FillerSupplementalServiceInformationReps();

			for (int k = 0; k < obr47Reps; k++)
			{
				String codingSystem = get("/.ORDER_OBSERVATION(" + obr + ")/OBR-47(" + k + ")-3");
				String indicator = get("/.ORDER_OBSERVATION(" + obr + ")/OBR-47(" + k + ")-1");
				if (JunoLabCode.CODING_SYSTEM.equals(codingSystem) && JunoLabCode.BLOCKED.name().equals(indicator))
				{
					return true;
				}
			}
		}
		catch (HL7Exception e)
		{
			logger.error("HL7 Parsing Error", e);
		}
		return false;
	}

	/* ===================================== OBX ====================================== */


	@Override
	public String getNteForPID()
	{
		return "";
	}

	@Override
	public String getNteForOBX(int i, int j)
	{
		return "";
	}

	@Override
	public String getOBXName(int i, int j)
	{
		String OBXName = super.getOBXName(i, j);
		if (!OBXName.isEmpty())
		{
			return OBXName;
		}

		String OBXIdentifier = super.getOBXIdentifier(i, j);
		if (!OBXIdentifier.isEmpty())
		{
			return OBXIdentifier;
		}
		else
		{
			return DEFAULT_OBX_NAME;
		}

	}
}
