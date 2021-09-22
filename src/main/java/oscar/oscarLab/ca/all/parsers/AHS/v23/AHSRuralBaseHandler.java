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
package oscar.oscarLab.ca.all.parsers.AHS.v23;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import oscar.oscarLab.ca.all.parsers.AHS.AHSHandler;

/**
 * Handler for:
 * AHS Rural Diagnostic Imaging
 *
 * @author Robert
 */
public abstract class AHSRuralBaseHandler extends AHSHandler
{
	public AHSRuralBaseHandler()
	{
		super();
	}

	public AHSRuralBaseHandler(String hl7Body) throws HL7Exception
	{
		super(hl7Body);
	}

	public AHSRuralBaseHandler(Message msg) throws HL7Exception
	{
		super(msg);
	}

	@Override
	public boolean canUpload()
	{
		return true;
	}

    /* ===================================== Hl7 Parsing ====================================== */

	/* ===================================== MSH ====================================== */

	/* ===================================== OBR ====================================== */

	@Override
	public String getAccessionNum()
	{
		// use the filler order number as the unique lab identifier apparently
		return get("/.OBR-3-1");
	}

	@Override
	public String getServiceDate()
	{
		return get("/.OBR-7-1");
	}

	@Override
	public String getOrderStatus()
	{
		return get("/.OBR-25-1");
	}

	@Override
	public String getOrderStatusDisplayValue()
	{
		if("X".equals(getOrderStatus()))
		{
			return "Cancelled";
		}
		return "Final";
	}

	/**
	 * Rural DI is always unstructured
	 */
	@Override
	public boolean isUnstructured()
	{
		return true;
	}

	/* ===================================== OBX ====================================== */

	@Override
	public String getTimeStamp(int i, int j)
	{
		if (i < 0 || j < 0)
		{
			return null;
		}
		return formatDateTime(get("/.ORDER_OBSERVATION("+i+")/OBR-8-1"));
	}


}
