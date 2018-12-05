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
import ca.uhn.hl7v2.model.v23.segment.MSH;
import org.oscarehr.common.hl7.AHS.model.v23.message.ORM_002;

public class CLSDIORMHandler extends CLSDIHandler
{
	public static boolean handlerTypeMatch(Message message)
	{
		String version = message.getVersion();
		if(version.equals("2.3"))
		{
			ORM_002 msh = (ORM_002) message;
			MSH messageHeaderSegment = msh.getMSH();

			String sendingApplication = messageHeaderSegment.getSendingApplication().getNamespaceID().getValue();
			String sendingFacility = messageHeaderSegment.getSendingFacility().getNamespaceID().getValue();

			return CLSDI_SENDING_APPLICATION.equalsIgnoreCase(sendingApplication) &&
					CLSDI_SENDING_FACILITY.equalsIgnoreCase(sendingFacility);
		}
		return false;
	}

	public CLSDIORMHandler(Message msg) throws HL7Exception
	{
		super(msg);
	}

	/* ===================================== OBR ====================================== */

	@Override
	public String getOrderStatus()
	{
		return "F";
	}

	@Override
	public String getServiceDate()
	{
		String serviceDate = getString(get("/.ORC-15"));
		if (serviceDate == null || serviceDate.isEmpty())
		{
			serviceDate = get("/.OBR-14");
		}
		/* for whatever reason, the ORM segments don't have a service date in the usual AHS location
		 * In that case, use the observation date I guess. */
		if (serviceDate == null || serviceDate.isEmpty())
		{
			serviceDate = get("/.OBR-7");
		}
		return (formatDateTime(serviceDate));
	}

	/* ===================================== OBX ====================================== */
	/**
	 *  Return the number of OBX Segments within the OBR group specified by i.
	 */
	@Override
	public int getOBXCount(int i)
	{
		return 1;
	}

	/**
	 *  Return true if an abnormal flag other than 'N' is returned by
	 *  getOBXAbnormalFlag( i, j ) for the OBX segment specified by j, in the
	 *  ith OBR group. Return false otherwise.
	 */
	@Override
	public boolean isOBXAbnormal(int i, int j)
	{
		return false;
	}

	/**
	 * Return the obx value type
	 * @param i
	 * @param j
	 * @return String the obx value
	 */
	@Override
	public String getOBXValueType(int i, int j)
	{
		return "";
	}

	/**
	 *  Return the identifier from jth OBX segment of the ith OBR group. It is
	 *  usually stored in the first component of the third field of the OBX
	 *  segment.
	 */
	@Override
	public String getOBXIdentifier(int i, int j)
	{
		return "";
	}

	/**
	 *  Return the name of the jth OBX segment of the ith OBR group. It is
	 *  usually stored in the second component of the third field of the OBX
	 *  segment.
	 */
	@Override
	public String getOBXName( int i, int j)
	{
		return getString(get("/.ORC-16-1"));
	}

	/**
	 *  Return the result from the jth OBX segment of the ith OBR group
	 */
	@Override
	public String getOBXResult(int i, int j)
	{
		return getString(get("/.ORC-16-2"));
	}

	/**
	 *  Return the units from the jth OBX segment of the ith OBR group
	 */
	@Override
	public String getOBXUnits( int i, int j)
	{
		return "";
	}

	/**
	 *  Return the reference range from the jth OBX segment of the ith OBR group
	 */
	@Override
	public String getOBXReferenceRange( int i, int j)
	{
		return "";
	}

	/**
	 *  Retrieve the abnormal flag if any from the OBX segment specified by j in
	 *  the ith OBR group.
	 */
	@Override
	public String getOBXAbnormalFlag( int i, int j)
	{
		return "";
	}

	/**
	 *  Return the result status from the jth OBX segment of the ith OBR group
	 */
	@Override
	public String getOBXResultStatus( int i, int j)
	{
		return "";
	}

	/**
	 *  Return the date and time of the observation referred to by the jth obx
	 *  segment of the ith obrO group. If the date and time is not specified
	 *  within the obx segment it should be specified within the obr segment.
	 */
	@Override
	public String getTimeStamp(int i, int j)
	{
		return formatDateTime(getOBRDateTime(i));
	}

	/**
	 *  Return the number of comments (usually NTE segments) following the jth
	 *  OBX segment of the ith OBR group.
	 */
	@Override
	public int getOBXCommentCount( int i, int j)
	{
		return 0;
	}

	/**
	 *  Return the kth comment of the jth OBX segment of the ith OBR group
	 */
	@Override
	public String getOBXComment( int i, int j, int k)
	{
		return "";
	}

	/**
	 *  Returns the number used to order labs with matching accession numbers.
	 *
	 *  - Multiple labs with the same accession number must display in a certain
	 *  order. They are ordered by their date but if two labs with the same
	 *  accession number have the same date they are ordered by the number
	 *  retrieved by this method
	 *
	 *  - The newest lab will have the greatest number returned from this method.
	 *
	 *  - If the hl7 messages do not contain a version number or other such
	 *  number, the total number of obx segments with final results should be
	 *  returned
	 */
	@Override
	public int getOBXFinalResultCount()
	{
		return 1;
	}

	@Override
	public boolean isUnstructured()
	{
		return true;
	}
}
