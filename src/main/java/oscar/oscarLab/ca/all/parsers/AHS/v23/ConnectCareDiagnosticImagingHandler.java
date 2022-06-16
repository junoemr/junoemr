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
import ca.uhn.hl7v2.model.v23.message.ORU_R01;
import ca.uhn.hl7v2.model.v23.segment.MSH;
import org.apache.commons.lang3.tuple.Pair;
import oscar.oscarLab.ca.all.parsers.AHS.ConnectCareHandler;
import oscar.oscarLab.ca.all.parsers.AHS.ConnectCareLabType;

import java.util.ArrayList;

public class ConnectCareDiagnosticImagingHandler extends ConnectCareHandler
{
	public static boolean handlerTypeMatch(Message message)
	{
		String version = message.getVersion();
		if(version.equals("2.3"))
		{
			ORU_R01 msh = (ORU_R01) message;
			MSH messageHeaderSegment = msh.getMSH();

			String sendingApplication = messageHeaderSegment.getMsh3_SendingApplication().getNamespaceID().getValue();
			String sendingFacility = messageHeaderSegment.getMsh4_SendingFacility().getNamespaceID().getValue();

			return ConnectCareLabType.CCIMAGING.name().equalsIgnoreCase(sendingApplication) &&
					"AHS".equalsIgnoreCase(sendingFacility);
		}
		return false;
	}

	public ConnectCareDiagnosticImagingHandler(Message msg) throws HL7Exception
	{
		super(msg);
	}

	@Override
	public String getMsgType()
	{
		return ConnectCareLabType.CCIMAGING.name();
	}

	/**
	 *  Return the name of the jth OBX segment of the ith OBR group.
	 * @param i the OBR group rep
	 * @param j the OBX rep
	 * @return the obx name
	 */
	@Override
	public String getOBXName( int i, int j)
	{ // NOTE, OBX name is in a sub component
		return getString(get("/.ORDER_OBSERVATION("+i+")/OBSERVATION("+j+")/OBX-3-1-2"));
	}

	/**
	 * As per guidance from Connect Care NTE should be ignored in diagnostic imaging
	 * @param i - ignored
	 * @param j - gnored
	 * @return always zero
	 */
	@Override
	public int getOBXCommentCount( int i, int j)
	{
		return 0;
	}

	/**
	 * As per guidance from Connect Care NTE should be ignored in diagnostic imaging
	 * @param i - ignored
	 * @return always zero
	 */
	@Override
	public int getOBRCommentCount( int i)
	{
		return 0;
	}


	@Override
	public boolean isUnstructured()
	{
		return true;
	}

	/**
	 * get service date
	 * @return the service date as a string
	 */
	@Override
	public String getServiceDate()
	{
		return formatDateTime(get("/.ORDER_OBSERVATION/OBR-6"));
	}

	/**
	 * As per Connect Care Conformance guidance abnormal flag is to be ignored in this type of lab.
	 * @return - always false
	 */
	@Override
	public boolean isAbnormal()
	{
		return false;
	}

	/**
	 * As per Connect Care Conformance guidance abnormal flag is to be ignored in this type of lab.
	 * @return - always N
	 */
	@Override
	public String getOBXAbnormalFlag( int i, int j)
	{
		return "N";
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
	 * - Sending Application, always "CCIMAGING"
	 * - Message Date and time (MSH-7)
	 * @return - list of fields
	 */
	public ArrayList<Pair<String, String>> getExtendedResultDescriptionFields()
	{
		ArrayList<Pair<String, String>> extDesc = new ArrayList<Pair<String, String>>();

		extDesc.add(Pair.of("Sending Application:", getMsgSendingApplication()));
		extDesc.add(Pair.of("Message Date:", getMsgDate()));
		return extDesc;
	}
}
