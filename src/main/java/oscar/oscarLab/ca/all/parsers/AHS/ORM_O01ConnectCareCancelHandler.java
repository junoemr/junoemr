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
package oscar.oscarLab.ca.all.parsers.AHS;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v23.datatype.CX;
import ca.uhn.hl7v2.model.v23.message.ORM_O01;
import org.apache.commons.lang3.tuple.Pair;
import org.oscarehr.common.model.Hl7TextInfo;
import oscar.oscarLab.ca.all.parsers.messageTypes.ORM_O01MessageHandler;

import java.util.ArrayList;

public abstract class ORM_O01ConnectCareCancelHandler extends ORM_O01MessageHandler
{
	public ORM_O01ConnectCareCancelHandler() {}

	public ORM_O01ConnectCareCancelHandler(String hl7Body) throws HL7Exception
	{
		super(hl7Body);
	}

	public ORM_O01ConnectCareCancelHandler(Message msg) throws HL7Exception
	{
		super(msg);
	}

	@Override
	public String getOrderStatus()
	{
		return get("/.ORDER/ORC-1");
	}

	/**
	 * always cancel
	 * @return - only one possible status cancel
	 */
	@Override
	public Hl7TextInfo.REPORT_STATUS getJunoOrderStatus()
	{
		return Hl7TextInfo.REPORT_STATUS.X;
	}

	/**
	 * always unstructured doc
	 * @return true
	 */
	@Override
	public boolean isUnstructured()
	{
		return true;
	}

	/* ======================== PID =============================== */

	@Override
	public String getHealthNum()
	{
		ArrayList<Pair<String, String>> patientIds = getPatientIdentificationList(false);
		for (Pair<String, String> id : patientIds)
		{
			if (id.getLeft().equalsIgnoreCase("PHN") || id.getLeft().equalsIgnoreCase("ULI"))
			{
				return id.getRight();
			}
		}
		return "";
	}

	/**
	 * Connect Care labs send more than just health card number, they can also send, EPI, ABH, NWT, BKR
	 * @param appendNamespace - if true append namespace to end of identifier
	 * @return - list of paris patient identification &lt;id type , id + assigning authority >
	 */
	public ArrayList<Pair<String, String>> getPatientIdentificationList(boolean appendNamespace)
	{
		ArrayList<Pair<String, String>> ids = new ArrayList<Pair<String, String>>();
		if (message instanceof ORM_O01)
		{
			ORM_O01 msg = (ORM_O01) message;
			for (int i =0; i < msg.getPATIENT().getPID().getPatientIDInternalIDReps(); i ++)
			{
				CX id = msg.getPATIENT().getPID().getPatientIDInternalID(i);
				if (id.getAssigningAuthority().getNamespaceID().getValue() != null && id.getID().getValue() != null &&
						id.getIdentifierTypeCode().getValue() != null)
				{
					String idString = id.getID().getValue();
					if (appendNamespace)
					{
						idString += " " + id.getAssigningAuthority().getNamespaceID().getValue();
					}
					ids.add(Pair.of(id.getIdentifierTypeCode().getValue(), idString));
				}
			}
			return ids;
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
	 *  call getPatientIdentificationList default behavior of appending namespace
	 * @return - list of paris patient identification &lt;id type , id + assigning authority >
	 */
	public ArrayList<Pair<String, String>> getPatientIdentificationList()
	{
		return getPatientIdentificationList(true);
	}


	/* ======================== OBX =============================== */
	/**
	 * return 1 for obx count
	 * @return fixed count of 1
	 */
	@Override
	public int getOBXCount(int i) {return 1;}

	/**
	 * return fixed value type of ST
	 * @param i ignored
	 * @param j ignored
	 * @return fixed string of "ST".
	 */
	@Override
	public String getOBXValueType(int i, int j)
	{
		return "ST";
	}

	/**
	 * return fixed identifier "ADT"
	 * @param i ignored
	 * @param j ignored
	 * @return fixed string "ADT
	 */
	@Override
	public String getOBXIdentifier(int i, int j)
	{
		return "ADT";
	}

	/**
	 * return fixed name "Cancelled"
	 * @param i ignored
	 * @param j ignored
	 * @return fixed string "Cancelled"
	 */
	@Override
	public String getOBXName( int i, int j)
	{
		return "Cancelled";
	}

	/**
	 * return fixed string
	 * @param i ignored
	 * @param j ignored
	 * @return fixed string "Cancelled"
	 */
	@Override
	public String getOBXResult(int i, int j)
	{
		return "Cancelled";
	}

	/**
	 * return no units
	 * @param i ignored
	 * @param j ignored
	 * @return return ""
	 */
	@Override
	public String getOBXUnits( int i, int j)
	{
		return "";
	}

	/**
	 * return no reference range
	 * @param i ignored
	 * @param j ignored
	 * @return return ""
	 */
	@Override
	public String getOBXReferenceRange( int i, int j)
	{
		return "";
	}


	/**
	 * return fixed string "N"
	 * @param i ignored
	 * @param j ignored
	 * @return return "N"
	 */
	@Override
	public String getOBXAbnormalFlag( int i, int j)
	{
		return "N";
	}

	/**
	 * return 1 comment
	 * @param i ignored
	 * @param j ignored
	 * @return 1
	 */
	@Override
	public int getOBXCommentCount( int i, int j)
	{
		return 1;
	}

	/**
	 *  return help string for user
	 * @param i ignored
	 * @param j ignored
	 * @param k ingored
	 * @return help string
	 */
	@Override
	public String getOBXComment( int i, int j, int k)
	{
		return "You can use the version buttons to view the cancelled report.";
	}

	/* ========================= OBR ============================= */

	/**
	 * no comments should be displayed on cancel messages (as per guidance from connect care)
	 * @param i - obr rep
	 * @return - always zero
	 */
	@Override
	public int 	getOBRCommentCount(int i)
	{
		return 0;
	}
}
