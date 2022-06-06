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
import ca.uhn.hl7v2.model.v23.message.MDM_T02;
import ca.uhn.hl7v2.model.v23.message.MDM_T08;
import ca.uhn.hl7v2.model.v23.segment.PID;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import oscar.oscarLab.ca.all.parsers.messageTypes.MDM_T08_T02MessageHandler;

import java.util.ArrayList;
import java.util.List;

public abstract class MDM_T08_T02ConnectCareHandler extends MDM_T08_T02MessageHandler
{
	public MDM_T08_T02ConnectCareHandler(Message msg) throws HL7Exception
	{
		super(msg);
	}

	/* =================================== PID ============================ */
	/**
	 * Connect Care labs send more than just health card number, they can also send, EPI, ABH, NWT, BKR
	 * @param appendNamespace - if true append namespace to end of identifier
	 * @return - list of paris patient identification &lt;id type , id + assigning authority >
	 */
	public ArrayList<Pair<String, String>> getPatientIdentificationList(boolean appendNamespace)
	{
		if (message instanceof MDM_T08)
		{
			MDM_T08 msg = (MDM_T08) message;
			return getPatientIdentificationFromPID(msg.getPID(), appendNamespace);
		}
		else if (message instanceof MDM_T02)
		{
			MDM_T02 msg = (MDM_T02) message;
			return getPatientIdentificationFromPID(msg.getPID(), appendNamespace);
		}
		else
		{
			return new ArrayList<Pair<String, String>>();
		}
	}

	private ArrayList<Pair<String, String>> getPatientIdentificationFromPID(PID pid, boolean appendNamespace)
	{
		ArrayList<Pair<String, String>> identification = new ArrayList<>();
		for (int i =0; i < pid.getPatientIDInternalIDReps(); i ++)
		{
			CX id = pid.getPatientIDInternalID(i);
			if (id.getAssigningAuthority().getNamespaceID().getValue() != null && id.getID().getValue() != null &&
					id.getIdentifierTypeCode().getValue() != null)
			{
				String idString = id.getID().getValue();
				if (appendNamespace)
				{
					idString += " " + id.getAssigningAuthority().getNamespaceID().getValue();
				}
				identification.add(Pair.of(id.getIdentifierTypeCode().getValue(), idString));
			}
		}
		return identification;
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

		// add pv1 provider to cc docs
		int pd1_4Count = getFieldReps("/.PD1", 4);
		for(int k = 0; k < pd1_4Count; k++)
		{
			String docName = getFullDocName("/.PD1", 4, k);
			if(StringUtils.isNotBlank(docName))
			{
				docNames.add(docName);
			}
		}
		return docNames;
	}

}
