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
package oscar.oscarLab.ca.all.parsers.AHS.v251;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v251.datatype.CWE;
import ca.uhn.hl7v2.model.v251.message.ORU_R01;
import ca.uhn.hl7v2.model.v251.segment.MSH;
import org.oscarehr.util.MiscUtils;
import oscar.oscarLab.ca.all.parsers.AHS.ConnectCareHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConnectCareLabHandler extends ConnectCareHandler
{
	private final List<String> unstructuredLabTypes = Arrays.asList("IDENTITY TESTING", "SURGICAL PATHOLOGY", "BLOOD CULTURE, ROUTINE");

	public static boolean handlerTypeMatch(Message message)
	{
		String version = message.getVersion();
		if(version.equals("2.5.1"))
		{
			ORU_R01 msh = (ORU_R01) message;
			MSH messageHeaderSegment = msh.getMSH();

			String sendingApplication = messageHeaderSegment.getMsh3_SendingApplication().getNamespaceID().getValue();
			String sendingFacility = messageHeaderSegment.getMsh4_SendingFacility().getNamespaceID().getValue();

			return "CCLAB".equalsIgnoreCase(sendingApplication) &&
					"AHS".equalsIgnoreCase(sendingFacility);
		}
		return false;
	}

	@Override
	public String getMsgType()
	{
		return "AHS";
	}

	public ConnectCareLabHandler(Message msg) throws HL7Exception
	{
		super(msg);
	}

	@Override
	public String getHealthNum()
	{
		return get("/.PID-3(2)-1");
	}

	/**
	 * get service date
	 * @return the service date as a string
	 */
	@Override
	public String getServiceDate()
	{
		return formatDateTime(get("/.ORDER_OBSERVATION/OBR-7"));
	}

	@Override
	public boolean isSupportEmbeddedPdf()
	{
		return true;
	}

	@Override
	public boolean isUnstructured()
	{
		if (getOBRCount() > 0)
		{
			return unstructuredLabTypes.contains(get("/.ORDER_OBSERVATION(0)/OBR-4-9"));
		}
		return false;
	}

	/* ========================== OBR ========================== */

	/**
	 * check if the ith OBR segment is a susceptibility segment.
	 * @param i - the OBR index
	 * @return true if the OBR is a susceptibility segment
	 */
	public boolean isOBRSusceptibility(int i)
	{
		if (hasZBR())
		{
			return getZBRSensitivitySetIDs().contains(i);
		}
		return false;
	}


	/* ========================== OBX ========================== */

	/**
	 * get obx results. normal string result value or document id if OBX is a pdf.
	 * @param i - obx rep
	 * @param j - the obx segment
	 * @return - normal result string or pdf document id.
	 */
	@Override
	public String getOBXResult(int i, int j)
	{
		if (getOBXContentType(i, j) == OBX_CONTENT_TYPE.PDF)
		{
			return getOBXResult(i, j, 5);
		}
		else if (getOBXContentType(i, j) == OBX_CONTENT_TYPE.SUSCEPTIBILITY)
		{
			return getString(get("/.ORDER_OBSERVATION(" + i + ")/OBR-26-3")) + ": " + getOBXAbnormalFlag(i, j);
		}
		else if (hasZBR() && getOBXValueType(i, j).equals(CWE.class.getSimpleName()))
		{
			return getOBXResult(i, j, 2);
		}
		else
		{
			return super.getOBXResult(i, j);
		}
	}

	/**
	 * check for obx content type
	 * @param i - obr rep
	 * @param j - obx rep
	 * @return TEXT, SUSCEPTIBILITY OR PDF
	 */
	@Override
	public OBX_CONTENT_TYPE getOBXContentType(int i, int j)
	{
		if(getOBXValueType(i, j).equals("ED"))
		{
			if (getOBXResult(i, j, 2).equals("PDF"))
			{
				return OBX_CONTENT_TYPE.PDF;
			}
		}
		else if (isOBRSusceptibility(i))
		{
			return OBX_CONTENT_TYPE.SUSCEPTIBILITY;
		}

		return OBX_CONTENT_TYPE.TEXT;
	}

	/* ================== ZBR ========================= */

	/**
	 * check if this message contains a ZBR segment
	 * @return true if such a segment is present
	 */
	public boolean hasZBR()
	{
		return get("/.ZBR-2") != null;
	}

	/**
	 * get OBR that defines the culture set
	 * @return - the index of the OBR segment that defines the culture
	 */
	public String getZBRCulterSetID()
	{
		return getString(get("/.ZBR-2"));
	}

	/**
	 * get OBR segments that define the sensitivities
	 * @return - list of OBR segments that define sensitivity
	 */
	public List<Integer> getZBRSensitivitySetIDs()
	{
		ArrayList<Integer> ids = new ArrayList<>();

		String currId = get("/.ZBR-3(0)");
		int i = 1;
		while(currId != null)
		{
			try
			{
				ids.add(Integer.parseInt(currId) - 1);
			}
			catch (NumberFormatException nfe)
			{
				MiscUtils.getLogger().error("ZBR Sensitivity ID is not an integer! " + get("/.ZBR-3(" + Math.max(i - 1, 0) + ")"));
			}

			currId = get("/.ZBR-3(" + i + ")");
			i++;
		}

		return ids;
	}
}
