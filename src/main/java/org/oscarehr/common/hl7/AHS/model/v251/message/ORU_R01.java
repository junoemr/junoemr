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
package org.oscarehr.common.hl7.AHS.model.v251.message;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v251.segment.PID;
import ca.uhn.hl7v2.parser.DefaultModelClassFactory;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import org.oscarehr.common.hl7.AHS.model.v251.segment.ZBR;
import org.oscarehr.util.MiscUtils;

public class ORU_R01 extends ca.uhn.hl7v2.model.v251.message.ORU_R01
{
	// the CustomModelClassFactory requires the root package for the message as a string. exclude the version and sub-folders
	public static final String ROOT_PACKAGE = "org.oscarehr.common.hl7.AHS.model";

	public ORU_R01() throws HL7Exception
	{
		this(new DefaultModelClassFactory());
	}
	public ORU_R01(ModelClassFactory theFactory) throws HL7Exception
	{
		super(theFactory);

		// add custom connect care segment
		this.add(ZBR.class, false, false);
	}

	public ZBR getZBR()
	{
		try
		{
			return (ZBR)get("ZBR");
		}
		catch (HL7Exception e)
		{
			MiscUtils.getLogger().error("Failed to get ZBR segment with error: " + e.getMessage(), e);
		}
		return null;
	}

	/**
	 * get PID segment or null if none found
	 * @return PID segment
	 */
	public PID getPID()
	{
		try
		{
			return getPATIENT_RESULT().getPATIENT().getPID();
		}
		catch (NullPointerException e)
		{
			MiscUtils.getLogger().error("Could not get PID segment with error: " + e.getMessage(), e);
		}
		return null;
	}

}
