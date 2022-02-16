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
import ca.uhn.hl7v2.model.v23.message.MDM_T02;
import ca.uhn.hl7v2.model.v23.segment.MSH;
import oscar.oscarLab.ca.all.parsers.AHS.MDM_T08_T02ConnectCareHandler;

public class ConnectCareDocumentationAddHandler extends MDM_T08_T02ConnectCareHandler
{
	public ConnectCareDocumentationAddHandler(Message msg) throws HL7Exception
	{
		super(msg);
	}

	public static boolean handlerTypeMatch(Message message)
	{
		String version = message.getVersion();
		if(version.equals("2.3"))
		{
			MDM_T02 msh = (MDM_T02) message;
			MSH messageHeaderSegment = msh.getMSH();

			String sendingApplication = messageHeaderSegment.getMsh3_SendingApplication().getNamespaceID().getValue();
			String sendingFacility = messageHeaderSegment.getMsh4_SendingFacility().getNamespaceID().getValue();

			return "CCDOC".equalsIgnoreCase(sendingApplication) &&
					"AHS".equalsIgnoreCase(sendingFacility);
		}
		return false;
	}

	@Override
	public String getMsgType()
	{
		return "CCDOC";
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

	@Override
	public boolean supportsEmbeddedDocuments()
	{
		return true;
	}


	/* ================================= OBX ======================================= */

	/**
	 * get obx results. aka document id.
	 * @param i - ignored
	 * @param j - the obx segment
	 * @return - the document id string
	 */
	@Override
	public String getOBXResult(int i, int j)
	{
		return get("/.OBX(" + j + ")-5-5");
	}


	/**
	 * check for obx content type
	 * @param i - ignored
	 * @param j - obx rep
	 * @return PDF or UNKNOWN if type is not PDF
	 */
	@Override
	public OBX_CONTENT_TYPE getOBXContentType(int i, int j)
	{
		if (get("/.OBX(" + j + ")-5-2") != null && get("/.OBX(" + j + ")-5-2").equals("PDF"))
		{
			return OBX_CONTENT_TYPE.PDF;
		}
		else
		{
			return OBX_CONTENT_TYPE.UNKNOWN;
		}
	}
}
