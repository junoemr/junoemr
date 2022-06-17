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
package oscar.oscarLab.ca.all.parsers.AHS.v23;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v23.datatype.CX;
import ca.uhn.hl7v2.model.v23.message.MDM_T11;
import ca.uhn.hl7v2.model.v23.segment.MSH;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.oscarehr.common.model.Hl7TextInfo;
import oscar.oscarLab.ca.all.parsers.AHS.ConnectCareLabType;
import oscar.oscarLab.ca.all.parsers.messageTypes.MDM_T11MessageHandler;

import java.util.ArrayList;
import java.util.List;

public class ConnectCareDocumentationCancelHandler extends MDM_T11MessageHandler
{
	public ConnectCareDocumentationCancelHandler(Message msg) throws HL7Exception
	{
		super(msg);
	}

	public static boolean handlerTypeMatch(Message message)
	{
		String version = message.getVersion();
		if(version.equals("2.3"))
		{
			MDM_T11 msh = (MDM_T11) message;
			MSH messageHeaderSegment = msh.getMSH();

			String sendingApplication = messageHeaderSegment.getMsh3_SendingApplication().getNamespaceID().getValue();
			String sendingFacility = messageHeaderSegment.getMsh4_SendingFacility().getNamespaceID().getValue();

			return ConnectCareLabType.CCDOC.name().equalsIgnoreCase(sendingApplication) &&
					"AHS".equalsIgnoreCase(sendingFacility);
		}
		return false;
	}

	@Override
	public String getMsgType()
	{
		return ConnectCareLabType.CCDOC.name();
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
	/* ================================= PID ============================== */

	/**
	 * Connect Care labs send more than just health card number, they can also send, EPI, ABH, NWT, BKR
	 * @param appendNamespace - if true append namespace to end of identifier
	 * @return - list of paris patient identification &lt;id type , id + assigning authority >
	 */
	public ArrayList<Pair<String, String>> getPatientIdentificationList(boolean appendNamespace)
	{
		ArrayList<Pair<String, String>> ids = new ArrayList<Pair<String, String>>();
		if (message instanceof MDM_T11)
		{
			MDM_T11 msg = (MDM_T11) message;
			for (int i =0; i < msg.getPID().getPatientIDInternalIDReps(); i ++)
			{
				CX id = msg.getPID().getPatientIDInternalID(i);
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
	 * pull patient health number from the patient identifier list
	 * @return - the patients health number
	 */
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
		return null;
	}

	/* ================================= OBR ============================== */

	/**
	 *  Even though message has no OBR, return 1 for display purposes
	 */
	@Override
	public int getOBRCount()
	{
		return 1;
	}


	/**
	 * always return cancelled as the status
	 * @return - always status X
	 */
	@Override
	public Hl7TextInfo.REPORT_STATUS getJunoOrderStatus()
	{
		return Hl7TextInfo.REPORT_STATUS.X;
	}

	@Override
	public String getCCDocs()
	{
		try
		{
			return String.join(", ", getCCDocNames());
		}
		catch(HL7Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	protected List<String> getCCDocNames() throws HL7Exception
	{
		List<String> docNames = new ArrayList<>();
		int txa_23Count = getFieldReps("/.TXA", 23);
		for(int k = 0; k < txa_23Count; k++)
		{
			String docName = getResultCopiesTo(0, k);
			if(StringUtils.isNotBlank(docName))
			{
				docNames.add(docName);
			}
		}

		// add pv1 provider to cc docs if not marked confidential
		if(!isReportBlocked())
		{
			int pd1_4Count = getFieldReps("/.PD1", 4);
			for(int k = 0; k < pd1_4Count; k++)
			{
				String docName = getFullDocName("/.PD1", 4, k);
				if(StringUtils.isNotBlank(docName))
				{
					docNames.add(docName);
				}
			}
		}
		return docNames;
	}

	@Override
	public List<String> getDocNums()
	{
		List<String> docIds = new ArrayList<>();
		try
		{
			String providerId = getOrderingProviderNo(0, 0);
			docIds.add(providerId);

			int txa_23Count = getFieldReps("/.TXA", 23);
			for(int k = 0; k < txa_23Count; k++)
			{
				String docId = getResultCopiesToProviderNo(0, k);
				if(StringUtils.isNotBlank(docId))
				{
					docIds.add(docId);
				}
			}

			// add pv1 provider to cc docs if not marked confidential
			if(!isReportBlocked())
			{
				int pd1_4Count = getFieldReps("/.PD1", 4);
				for(int k = 0; k < pd1_4Count; k++)
				{
					String docId = get("/.PD1-4(" + k + ")-1");
					if(StringUtils.isNotBlank(docId))
					{
						docIds.add(docId);
					}
				}
			}
		}
		catch(Exception e)
		{
			logger.error("Could not return doctor nums", e);
		}
		return docIds;
	}

	/**
	 * indicates blocked or sensitive data within the report
	 * @return true if report flagged as sensitive/confidential
	 */
	@Override
	public boolean isReportBlocked()
	{
		switch(getString(get("/.TXA-18")))
		{
			case "RE":              // Restricted
			case "VR": return true; // Very restricted
			case "UC":              // Usual control
			default: return false;
		}
	}

	/* ========================== OBX ========================== */

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

	/**
	 * always unstructured doc
	 * @return true
	 */
	@Override
	public boolean isUnstructured()
	{
		return true;
	}



}
