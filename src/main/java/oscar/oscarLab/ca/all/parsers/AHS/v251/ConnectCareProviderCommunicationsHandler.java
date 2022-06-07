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
import ca.uhn.hl7v2.model.v251.message.MDM_T02;
import ca.uhn.hl7v2.model.v251.segment.MSH;
import oscar.oscarLab.ca.all.parsers.AHS.MDM_T08_T02ConnectCareHandler;

public class ConnectCareProviderCommunicationsHandler extends MDM_T08_T02ConnectCareHandler
{
	public static final String AHS_CCCOMM_LAB_TYPE = "CCCOMM";

	public ConnectCareProviderCommunicationsHandler(Message msg) throws HL7Exception
	{
		super(msg);
	}

	public static boolean handlerTypeMatch(Message message)
	{
		String version = message.getVersion();
		if(version.equals("2.5.1"))
		{
			MDM_T02 msh = (MDM_T02) message;
			MSH messageHeaderSegment = msh.getMSH();

			String sendingApplication = messageHeaderSegment.getMsh3_SendingApplication().getNamespaceID().getValue();
			String sendingFacility = messageHeaderSegment.getMsh4_SendingFacility().getNamespaceID().getValue();

			return AHS_CCCOMM_LAB_TYPE.equalsIgnoreCase(sendingApplication) &&
					"AHS".equalsIgnoreCase(sendingFacility);
		}
		return false;
	}

	@Override
	public String getMsgType()
	{
		return AHS_CCCOMM_LAB_TYPE;
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

	/* ================================= OBX ======================================= */

	@Override
	public boolean supportsEmbeddedDocuments()
	{
		return true;
	}

	/**
	 * get obx results. aka document id.
	 * @param i - ignored
	 * @param j - the obx segment
	 * @return - the document id string
	 */
	@Override
	public String getOBXResult(int i, int j)
	{
		return get("/.OBSERVATION(" + j + ")/OBX-5-5");
	}
}
