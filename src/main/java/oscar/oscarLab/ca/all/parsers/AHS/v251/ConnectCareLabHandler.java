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
import ca.uhn.hl7v2.model.v251.datatype.CX;
import ca.uhn.hl7v2.model.v251.message.ORU_R01;
import ca.uhn.hl7v2.model.v251.segment.MSH;
import org.apache.commons.lang3.tuple.Pair;
import org.oscarehr.common.hl7.AHS.model.v251.segment.ZBR;
import org.oscarehr.util.MiscUtils;
import oscar.oscarLab.ca.all.parsers.AHS.ConnectCareHandler;
import oscar.oscarLab.ca.all.parsers.AHS.ConnectCareLabType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConnectCareLabHandler extends ConnectCareHandler
{
	private final List<String> unstructuredLabTypes = Arrays.asList("IDENTITY TESTING", "SURGICAL PATHOLOGY", "BLOOD CULTURE, ROUTINE", "NON-GYNECOLOGICAL CYTOLOGY");

	public static boolean handlerTypeMatch(Message message)
	{
		String version = message.getVersion();
		if(version.equals("2.5.1"))
		{
			ORU_R01 msh = (ORU_R01) message;
			MSH messageHeaderSegment = msh.getMSH();

			String sendingApplication = messageHeaderSegment.getMsh3_SendingApplication().getNamespaceID().getValue();
			String sendingFacility = messageHeaderSegment.getMsh4_SendingFacility().getNamespaceID().getValue();

			return ConnectCareLabType.CCLAB.name().equalsIgnoreCase(sendingApplication) &&
					"AHS".equalsIgnoreCase(sendingFacility);
		}
		return false;
	}

	@Override
	public String getMsgType()
	{
		return ConnectCareLabType.CCLAB.name();
	}

	public ConnectCareLabHandler(Message msg) throws HL7Exception
	{
		super(msg);
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
	public boolean supportsEmbeddedDocuments()
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

	/**
	 * Connect Care labs send more than just health card number, they can also send, EPI, ABH, NWT, BKR
	 * An overload of the implementation is required because this handler uses ORU_R01 v 2.5.1 message
	 * @param appendNamespace - if true append namespace to end of identifier
	 * @return - list of paris patient identification &lt;id type , id + assigning authority >
	 */
	@Override
	public ArrayList<Pair<String, String>> getPatientIdentificationList(boolean appendNamespace)
	{
		ArrayList<Pair<String, String>> ids = new ArrayList<Pair<String, String>>();
		if (message instanceof org.oscarehr.common.hl7.AHS.model.v251.message.ORU_R01)
		{
			try
			{
				org.oscarehr.common.hl7.AHS.model.v251.message.ORU_R01 msg = (org.oscarehr.common.hl7.AHS.model.v251.message.ORU_R01) message;
				for (int i = 0; i < msg.getPID().getPatientIdentifierListReps(); i++)
				{
					CX id = msg.getPID().getPatientIdentifierList(i);
					if (id.getAssigningAuthority().getNamespaceID().getValue() != null && id.getIDNumber().getValue() != null &&
							id.getIdentifierTypeCode().getValue() != null)
					{
						String idString = id.getIDNumber().getValue();
						if (appendNamespace)
						{
							idString += " " + id.getAssigningAuthority().getNamespaceID().getValue();
						}
						ids.add(Pair.of(id.getIdentifierTypeCode().getValue(), idString));
					}
				}
				return ids;
			}
			catch (HL7Exception e)
			{
				MiscUtils.getLogger().error("Failed to get Patient Identifier list with error: " + e.getMessage(), e);
				return new ArrayList<Pair<String, String>>();
			}
		}
		else
		{
			return new ArrayList<Pair<String, String>>();
		}
	}

	/**
	 * return additional fields,
	 * - Patient ID \w assigning authority - all values
	 * @return list of pairs &lt;title, value>
	 */
	@Override
	public ArrayList<Pair<String, String>> getExtendedPatientDescriptionFields()
	{
		ArrayList<Pair<String, String>> extDesc = new ArrayList<Pair<String, String>>();

		// add additional patient ids
		for (Pair<String, String> patientId : getPatientIdentificationList())
		{
			if (!patientId.getLeft().equalsIgnoreCase("ULI") && !patientId.getLeft().equalsIgnoreCase("PHN"))
			{
				extDesc.add(Pair.of(patientId.getLeft() + "# ", patientId.getRight()));
			}
		}

		return extDesc;
	}

	/**
	 * return additional fields
	 * - epic order id (as required by docs)
	 * @return - list of fields
	 */
	public ArrayList<Pair<String, String>> getExtendedResultDescriptionFields()
	{
		ArrayList<Pair<String, String>> extDesc = new ArrayList<Pair<String, String>>();

		String epicId = getString(get("/.ORDER_OBSERVATION/ORC-2-1"));
		if (!epicId.isEmpty())
		{
			extDesc.add(Pair.of("EPIC Order ID:", epicId));
		}
		return extDesc;
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

	/**
	 * Connect Care requires that the procedure code be displayed along with the OBR name (Procedure code description).
	 * @param i - the OBR index
	 * @return - the obr name
	 */
	public String getOBRName(int i)
	{
		return super.getOBRName(i) + ", " + getOBRProcedureCode(i);
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
		if (getOBXContentType(i, j) == ObxContentType.PDF)
		{
			return getOBXResult(i, j, 5);
		}
		else if (getOBXContentType(i, j) == ObxContentType.SUSCEPTIBILITY)
		{
			return getString(super.getOBXName(i, j) + ": " + getOBXAbnormalFlag(i, j));
		}
		else if (getOBXContentType(i, j) == ObxContentType.STRUCTURED_NUMERIC)
		{
			return getOBXResult(i, j, 1) + getOBXResult(i, j, 2) + getOBXResult(i, j, 3) + getOBXResult(i, j, 4);
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
	public ObxContentType getOBXContentType(int i, int j)
	{
		if(getOBXValueType(i, j).equals("ED"))
		{
			if (getOBXResult(i, j, 2).equals("PDF"))
			{
				return ObxContentType.PDF;
			}
		}
		else if (getOBXValueType(i, j).equals("SN"))
		{
			return ObxContentType.STRUCTURED_NUMERIC;
		}
		else if (isOBRSusceptibility(i))
		{
			return ObxContentType.SUSCEPTIBILITY;
		}

		return ObxContentType.TEXT;
	}

	/**
	 * get OBX name + OBX component code if OBX is not Susceptibility, else
	 * get "Susceptibility:" + OBR 26-3
	 * @param i - OBR rep
	 * @param j - OBX rep
	 * @return - OBX display name
	 */
	@Override
	public String getOBXName(int i, int j)
	{
		if (isOBRSusceptibility(i))
		{
			return "Susceptibility, " + getString(get("/.ORDER_OBSERVATION(" + i + ")/OBR-26-3"));
		}

		return getString(get("/.ORDER_OBSERVATION("+i+")/OBSERVATION("+j+")/OBX-3-2")) + ", "
				+ getString(get("/.ORDER_OBSERVATION("+i+")/OBSERVATION("+j+")/OBX-3-1"));
	}

	/**
	 * if OBX is in susceptibility OBR just return fixed id string
	 * @param i - OBR rep
	 * @param j - OBX rep
	 * @return - obx identifier string
	 */
	@Override
	public String getOBXIdentifier(int i, int j)
	{
		if (isOBRSusceptibility(i))
		{
			return "Susceptibility, " + getString(get("/.ORDER_OBSERVATION(" + i + ")/OBR-26-3"));
		}

		return super.getOBXIdentifier(i, j);
	}

	/* ================== ZBR ========================= */

	/**
	 * check if this message contains a ZBR segment
	 * @return true if such a segment is present
	 */
	public boolean hasZBR()
	{
		return getReps("ZBR") != 0;
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
		if (message instanceof org.oscarehr.common.hl7.AHS.model.v251.message.ORU_R01)
		{
			org.oscarehr.common.hl7.AHS.model.v251.message.ORU_R01 oruMessage = (org.oscarehr.common.hl7.AHS.model.v251.message.ORU_R01) message;
			ZBR zbr = oruMessage.getZBR();

			ArrayList<Integer> ids = new ArrayList<>();
			for (int i = 0; i < zbr.getZBR3Reps(); i++)
			{
				try
				{
					ids.add(Integer.parseInt(get("/.ZBR-3(" + i + ")")) - 1);
				} catch (NumberFormatException nfe)
				{
					MiscUtils.getLogger().error("ZBR Sensitivity ID is not an integer! " + get("/.ZBR-3(" + i + ")"), nfe);
				}
			}

			return ids;
		}
		else
		{
			return null;
		}
	}
}
