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
package org.oscarehr.demographicImport.copd.hl7.v24.group;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.AbstractGroup;
import ca.uhn.hl7v2.model.Group;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import org.apache.log4j.Logger;
import org.oscarehr.demographicImport.copd.hl7.v24.segment.MSH;
import org.oscarehr.demographicImport.copd.hl7.v24.segment.ZR1;
import org.oscarehr.demographicImport.copd.hl7.v24.segment.ZS1;
import org.oscarehr.util.MiscUtils;

public class ZPD_ZTR_MESSAGE extends AbstractGroup
{
	private static final Logger logger = MiscUtils.getLogger();

	public ZPD_ZTR_MESSAGE(Group parent, ModelClassFactory factory)
	{
		super(parent, factory);
		logger.info("Init ZPD_ZTR_MESSAGE");
		try
		{
			this.add(MSH.class, true, false);
			this.add(ZS1.class, true, false);
			this.add(ZR1.class, false, false);
			this.add(ZPD_ZTR_PATIENT.class, true, false);
		}
		catch(HL7Exception var3)
		{
			throw new RuntimeException(var3);
		}
	}

	public MSH getMSH()
	{
		MSH ret = null;

		try
		{
			ret = (MSH) this.get("MSH");
			return ret;
		}
		catch(HL7Exception var3)
		{
			throw new RuntimeException(var3);
		}
	}

	public ZS1 getZS1()
	{
		ZS1 ret = null;

		try
		{
			ret = (ZS1) this.get("ZS1");
			return ret;
		}
		catch(HL7Exception var3)
		{
			throw new RuntimeException(var3);
		}
	}
	public ZPD_ZTR_PATIENT getPATIENT()
	{
		ZPD_ZTR_PATIENT ret = null;

		try
		{
			ret = (ZPD_ZTR_PATIENT) this.get("PATIENT");
			return ret;
		}
		catch(HL7Exception var3)
		{
			throw new RuntimeException(var3);
		}
	}
}
