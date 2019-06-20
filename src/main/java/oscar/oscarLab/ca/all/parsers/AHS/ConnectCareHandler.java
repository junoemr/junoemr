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
import org.apache.commons.lang.StringUtils;
import oscar.oscarLab.ca.all.parsers.messageTypes.ORU_R01MessageHandler;

public abstract class ConnectCareHandler extends ORU_R01MessageHandler
{
	public ConnectCareHandler(Message msg) throws HL7Exception
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

	@Override
	public String getHealthNum()
	{
		return get("/.PID-3(1)-1");
	}

	@Override
	public String getMsgType()
	{
		return "AHS";
	}

	@Override
	public String getAccessionNum()
	{
		return get("/.ORDER_OBSERVATION/OBR-3-1");
	}

	@Override
	public String getFillerOrderNumber()
	{
		return get("/.ORDER_OBSERVATION/ORC-3-1");
	}

	@Override
	public String getNteForPID() {
		return get("/.NTE-3");
	}

	@Override
	public String getPatientLocation()
	{
		return getAssignedPatientLocation();
	}


	@Override
	public String getNteForOBX(int i, int j)
	{
		return get("/.ORDER_OBSERVATION("+ i +")/OBSERVATION("+ j +")/NTE-3");
	}

	/**
	 *  Retrieve the abnormal flag if any from the OBX segment specified by j in
	 *  the ith OBR group.
	 */
	@Override
	public String getOBXAbnormalFlag( int i, int j)
	{
		String ab = StringUtils.trimToNull(getString(get("/.ORDER_OBSERVATION("+i+")/OBSERVATION("+j+")/OBX-8")));
		if (ab == null)
		{ // no flag == normal result
			ab = "N";
		}
		return ab;
	}
}
