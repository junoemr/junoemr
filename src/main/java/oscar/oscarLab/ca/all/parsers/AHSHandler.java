/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */

package oscar.oscarLab.ca.all.parsers;

import ca.uhn.hl7v2.HL7Exception;

import java.util.ArrayList;

public class AHSHandler extends MessageHandler
{
	@Override
	public void init(String hl7Body) throws HL7Exception
	{

	}

	@Override
	public String getMsgType()
	{
		return null;
	}

	@Override
	public String getMsgDate()
	{
		return null;
	}

	@Override
	public String getMsgPriority()
	{
		return null;
	}

	@Override
	public int getOBRCount()
	{
		return 0;
	}

	@Override
	public int getOBXCount(int i)
	{
		return 0;
	}

	@Override
	public String getOBRName(int i)
	{
		return null;
	}

	@Override
	public String getTimeStamp(int i, int j)
	{
		return null;
	}

	@Override
	public boolean isOBXAbnormal(int i, int j)
	{
		return false;
	}

	@Override
	public String getOBXAbnormalFlag(int i, int j)
	{
		return null;
	}

	@Override
	public String getObservationHeader(int i, int j)
	{
		return null;
	}

	@Override
	public String getOBXIdentifier(int i, int j)
	{
		return null;
	}

	@Override
	public String getOBXValueType(int i, int j)
	{
		return null;
	}

	@Override
	public String getOBXName(int i, int j)
	{
		return null;
	}

	@Override
	public String getOBXResult(int i, int j)
	{
		return null;
	}

	@Override
	public String getOBXReferenceRange(int i, int j)
	{
		return null;
	}

	@Override
	public String getOBXUnits(int i, int j)
	{
		return null;
	}

	@Override
	public String getOBXResultStatus(int i, int j)
	{
		return null;
	}

	@Override
	public ArrayList<String> getHeaders()
	{
		return null;
	}

	@Override
	public int getOBRCommentCount(int i)
	{
		return 0;
	}

	@Override
	public String getOBRComment(int i, int j)
	{
		return null;
	}

	@Override
	public int getOBXCommentCount(int i, int j)
	{
		return 0;
	}

	@Override
	public String getOBXComment(int i, int j, int k)
	{
		return null;
	}

	@Override
	public String getPatientName()
	{
		return null;
	}

	@Override
	public String getFirstName()
	{
		return null;
	}

	@Override
	public String getLastName()
	{
		return null;
	}

	@Override
	public String getDOB()
	{
		return null;
	}

	@Override
	public String getAge()
	{
		return null;
	}

	@Override
	public String getSex()
	{
		return null;
	}

	@Override
	public String getHealthNum()
	{
		return null;
	}

	@Override
	public String getHomePhone()
	{
		return null;
	}

	@Override
	public String getWorkPhone()
	{
		return null;
	}

	@Override
	public String getPatientLocation()
	{
		return null;
	}

	@Override
	public String getServiceDate()
	{
		return null;
	}

	@Override
	public String getRequestDate(int i)
	{
		return null;
	}

	@Override
	public String getOrderStatus()
	{
		return null;
	}

	@Override
	public int getOBXFinalResultCount()
	{
		return 0;
	}

	@Override
	public String getClientRef()
	{
		return null;
	}

	@Override
	public String getAccessionNum()
	{
		return null;
	}

	@Override
	public String getDocName()
	{
		return null;
	}

	@Override
	public String getCCDocs()
	{
		return null;
	}

	@Override
	public ArrayList getDocNums()
	{
		return null;
	}

	@Override
	public String audit()
	{
		return null;
	}

	@Override
	public String getFillerOrderNumber()
	{
		return null;
	}

	@Override
	public String getEncounterId()
	{
		return null;
	}

	@Override
	public String getRadiologistInfo()
	{
		return null;
	}

	@Override
	public String getNteForOBX(int i, int j)
	{
		return null;
	}

	@Override
	public String getNteForPID()
	{
		return null;
	}
}
