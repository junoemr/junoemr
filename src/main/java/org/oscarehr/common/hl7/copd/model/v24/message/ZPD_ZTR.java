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
package org.oscarehr.common.hl7.copd.model.v24.message;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.AbstractMessage;
import ca.uhn.hl7v2.model.v24.segment.MSH;
import ca.uhn.hl7v2.parser.DefaultModelClassFactory;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import org.apache.log4j.Logger;
import org.oscarehr.common.hl7.copd.model.v24.group.ZPD_ZTR_PATIENT;
import org.oscarehr.common.hl7.copd.model.v24.segment.ZR1;
import org.oscarehr.common.hl7.copd.model.v24.segment.ZS1;
import org.oscarehr.util.MiscUtils;

/**
 * Custom hl7 message class for COPD formatted messages
 */
public class ZPD_ZTR extends AbstractMessage
{
	private static final Logger logger = MiscUtils.getLogger();

	// the CustomModelClassFactory requires the root package for the message as a string. exclude the version and sub-folders
	public static final String ROOT_PACKAGE = "org.oscarehr.common.hl7.copd.model";

	public ZPD_ZTR()
	{
		this(new DefaultModelClassFactory());
	}
	public ZPD_ZTR(ModelClassFactory theFactory)
	{
		super(theFactory);
		try
		{
			this.add(MSH.class, true, false);
			this.add(ZS1.class, true, false);
			this.add(ZR1.class, false, false);
			this.add(ZPD_ZTR_PATIENT.class, true, false);
		}
		catch(HL7Exception var3)
		{
			logger.error("Can't instantiate " + this.getClass().getName());
			throw new RuntimeException(var3);
		}
	}

	public MSH getMSH()
	{
		try
		{
			return (MSH) this.get("MSH");
		}
		catch(HL7Exception var3)
		{
			logger.error("Unexpected error accessing data - this is probably a bug in the source code generator.", var3);
			throw new RuntimeException(var3);
		}
	}

	public ZS1 getZS1()
	{
		try
		{
			return (ZS1) this.get("ZS1");
		}
		catch(HL7Exception var3)
		{
			logger.error("Unexpected error accessing data - this is probably a bug in the source code generator.", var3);
			throw new RuntimeException(var3);
		}
	}

	public ZR1 getZR1()
	{
		try
		{
			return (ZR1) this.get("ZR1");
		}
		catch(HL7Exception var3)
		{
			logger.error("Unexpected error accessing data - this is probably a bug in the source code generator.", var3);
			throw new RuntimeException(var3);
		}
	}

	public ZPD_ZTR_PATIENT getPATIENT()
	{
		try
		{
			return (ZPD_ZTR_PATIENT) this.get("PATIENT");
		}
		catch(HL7Exception var3)
		{
			logger.error("Unexpected error accessing data - this is probably a bug in the source code generator.", var3);
			throw new RuntimeException(var3);
		}
	}

	@Override
	public String getVersion()
	{
		return "2.4";
	}
}
