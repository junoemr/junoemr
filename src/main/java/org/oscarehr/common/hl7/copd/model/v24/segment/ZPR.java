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
import ca.uhn.hl7v2.model.v24.datatype.CE;
import ca.uhn.hl7v2.model.v24.datatype.SI;
import ca.uhn.hl7v2.model.v24.datatype.ST;
import ca.uhn.hl7v2.model.v24.datatype.TS;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtils;

/**
 * This segment contains a record of any prior medical or surgical procedures or diagnostic imaging undertaken to
 * diagnose a health problem, treat or correct a health problem, to support treatment or to assess the impact of
 * treatment.
 */
public class ZPR extends AbstractSegment
{
	private static final Logger logger = MiscUtils.getLogger();

	public ZPR(Group parent, ModelClassFactory factory)
	{
		super(parent, factory);
		Message message = this.getMessage();

		try
		{
			this.add(SI.class, true, 1, 4, new Object[]{message}, "Set ID - ZPR");
			this.add(ST.class, true, 1, 100, new Object[]{message}, "Procedure Name");
			this.add(TS.class, false, 1, 14, new Object[]{message}, "Procedure Date/Time");
			this.add(TS.class, false, 1, 8, new Object[]{message}, "Follow-up Date");
			this.add(CE.class, false, 1, 35, new Object[]{message}, "Service Code");
			this.add(ST.class, false, 1, 200, new Object[]{message}, "Results");
		}
		catch(HL7Exception e)
		{
			logger.error("Can't instantiate " + this.getClass().getName());
			throw new RuntimeException(e);
		}
	}

	public SI getZpr1_setId() throws HL7Exception
	{
		try
		{
			Type t = this.getField(1, 0);
			return (SI) t;
		}
		catch(ClassCastException var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public ST getZpr2_procedureName() throws HL7Exception
	{
		try
		{
			Type t = this.getField(2, 0);
			return (ST) t;
		}
		catch(ClassCastException var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public TS getZpr3_procedureDateTime() throws HL7Exception
	{
		try
		{
			Type t = this.getField(3, 0);
			return (TS) t;
		}
		catch(ClassCastException var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public TS getZpr4_followUpDate() throws HL7Exception
	{
		try
		{
			Type t = this.getField(4, 0);
			return (TS) t;
		}
		catch(ClassCastException var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public CE getZpr5_serviceCode() throws HL7Exception
	{
		try
		{
			Type t = this.getField(5, 0);
			return (CE) t;
		}
		catch(ClassCastException var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public ST getZpr6_results() throws HL7Exception
	{
		try
		{
			Type t = this.getField(6, 0);
			return (ST) t;
		}
		catch(ClassCastException var4)
		{
			throw new RuntimeException(var4);
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
