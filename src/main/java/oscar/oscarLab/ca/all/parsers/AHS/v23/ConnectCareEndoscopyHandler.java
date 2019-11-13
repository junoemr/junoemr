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
import oscar.oscarLab.ca.all.parsers.AHS.ConnectCareHandler;
import oscar.oscarLab.ca.all.parsers.MessageHandler;

public class ConnectCareEndoscopyHandler extends ConnectCareHandler
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

			return "CCENDO".equalsIgnoreCase(sendingApplication) &&
					"AHS".equalsIgnoreCase(sendingFacility);
		}
		return false;
	}

	public ConnectCareEndoscopyHandler(Message msg) throws HL7Exception
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
		return formatDateTime(get("/.ORDER_OBSERVATION/OBR-6"));
	}

	@Override
	public String getMsgType()
	{
		return "CCENDO";
	}

	@Override
	public boolean isUnstructured()
	{
		return true;
	}

	/* ========================= OBX ========================= */

	/**
	 * check for obx content type
	 * @param i - obr rep
	 * @param j - obx rep
	 * @return TEXT OR PDF
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

		return OBX_CONTENT_TYPE.TEXT;
	}

	/**
	 * Observation identifier text is not provided for this type of report. just return "Embedded Document"
	 * @param i - ignored
	 * @param j - ignored
	 * @return - the string "Embedded Document"
	 */
	@Override
	public String getOBXName(int i, int j)
	{
		return "Embedded Document";
	}

	/**
	 * Observation identifier is not provided for this type of report. just return "Embedded Document"
	 * @param i - ignored
	 * @param j - ignored
	 * @return the string "Embedded Document"
	 */
	@Override
	public String getOBXIdentifier(int i, int j)
	{
		return "Embedded Document";
	}

	/**
	 * get the obx result. Includes alternate logic on PDF format OBX
	 * @param i - obr rep
	 * @param j - obx rep
	 * @return - the obx result
	 */
	@Override
	public String getOBXResult(int i, int j)
	{
		if (getOBXContentType(i, j) == MessageHandler.OBX_CONTENT_TYPE.PDF)
		{
			return getOBXResult(i, j, 5);
		}

		return super.getOBXResult(i, j);
	}

}
