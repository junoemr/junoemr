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
package org.oscarehr.common.hl7.copd.model.v24.segment;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.AbstractSegment;
import ca.uhn.hl7v2.model.Group;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Type;
import ca.uhn.hl7v2.model.v24.datatype.DT;
import ca.uhn.hl7v2.model.v24.datatype.RP;
import ca.uhn.hl7v2.model.v24.datatype.SI;
import ca.uhn.hl7v2.model.v24.datatype.ST;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtils;

/**
 * This segment contains attachments information and reference pointers to the attachment files to be attached.
 * Includes Any additional documents attached to the MS, ie Discharge summaries, assessment scales, Psychology
 * reports, growth charts etc.
 */
public class ZAT extends AbstractSegment
{
	private static final Logger logger = MiscUtils.getLogger();

	public ZAT(Group parent, ModelClassFactory factory)
	{
		super(parent, factory);
		Message message = this.getMessage();

		try
		{
			this.add(SI.class, true, 1, 4, new Object[]{message}, "Set ID - ZAT");
			this.add(DT.class, false, 1, 8, new Object[]{message}, "Date");
			this.add(ST.class, true, 1, 255, new Object[]{message}, "Name");
			this.add(RP.class, true, 1, 237, new Object[]{message}, "Attachment");
		}
		catch(HL7Exception e)
		{
			logger.error("Can't instantiate " + this.getClass().getName());
			throw new RuntimeException(e);
		}
	}

	public SI getZat2_SetId()
	{
		try
		{
			Type t = this.getField(1, 0);
			return (SI) t;
		}
		catch(HL7Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public DT getZat2_Date()
	{
		try
		{
			Type t = this.getField(2, 0);
			return (DT) t;
		}
		catch(HL7Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public ST getZat3_Name()
	{
		try
		{
			Type t = this.getField(3, 0);
			return (ST) t;
		}
		catch(HL7Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public RP getZat4_Attachment()
	{
		try
		{
			Type t = this.getField(4, 0);
			return (RP) t;
		}
		catch(HL7Exception e)
		{
			throw new RuntimeException(e);
		}
	}


	/**
	 * This method must be overridden. The easiest way is just to return null.
	 */
	@Override
	protected Type createNewTypeWithoutReflection(int field)
	{
		return null;
	}
}
