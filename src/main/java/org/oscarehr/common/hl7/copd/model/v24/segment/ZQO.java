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
import ca.uhn.hl7v2.model.v24.datatype.NM;
import ca.uhn.hl7v2.model.v24.datatype.SI;
import ca.uhn.hl7v2.model.v24.datatype.ST;
import ca.uhn.hl7v2.model.v24.datatype.TS;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtils;

/**
 * This segment conveys quantitative observations of the patient using discreet data points.
 */
public class ZQO extends AbstractSegment
{
	private static final Logger logger = MiscUtils.getLogger();

	public ZQO(Group parent, ModelClassFactory factory)
	{
		super(parent, factory);
		Message message = this.getMessage();

		try
		{
			this.add(SI.class, true, 1, 4, new Object[]{message}, "Set ID - ZQO");
			this.add(TS.class, true, 1, 8, new Object[]{message}, "Observation Date");
			this.add(ST.class, false, 1, 5, new Object[]{message}, "Mini mental health status score");
			this.add(NM.class, false, 1, 3, new Object[]{message}, "Systolic blood pressure");
			this.add(NM.class, false, 1, 3, new Object[]{message}, "Diastolic blood pressure");
			this.add(NM.class, false, 1, 4, new Object[]{message}, "height");
			this.add(NM.class, false, 1, 3, new Object[]{message}, "weight");
			this.add(NM.class, false, 1, 4, new Object[]{message}, "waist circumference");
		}
		catch(HL7Exception e)
		{
			logger.error("Can't instantiate " + this.getClass().getName());
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

	public TS getZQO2_1_observationDate() throws HL7Exception
	{
		try
		{
			return (TS) getField(2, 0);
		}
		catch (ClassCastException e)
		{
			throw new RuntimeException(e);
		}
	}

	public ST getZQO3_miniHealth() throws HL7Exception
	{
		try
		{
			return (ST) getField(3, 0);
		}
		catch (ClassCastException e)
		{
			throw new RuntimeException(e);
		}
	}

	public NM getZQO4_systolicBP() throws HL7Exception
	{
		try
		{
			return (NM) getField(4, 0);
		}
		catch (ClassCastException e)
		{
			throw new RuntimeException(e);
		}
	}

	public NM getZQO5_diastolicBP() throws HL7Exception
	{
		try
		{
			return (NM) getField(5, 0);
		}
		catch (ClassCastException e)
		{
			throw new RuntimeException(e);
		}
	}

	public NM getZQO6_height() throws HL7Exception
	{
		try
		{
			return (NM) getField(6, 0);
		}
		catch (ClassCastException e)
		{
			throw new RuntimeException(e);
		}
	}

	public NM getZQO7_weight() throws HL7Exception
	{
		try
		{
			return (NM) getField(7, 0);
		}
		catch (ClassCastException e)
		{
			throw new RuntimeException(e);
		}
	}

	public NM getZQO8_waist() throws HL7Exception
	{
		try
		{
			return (NM) getField(8, 0);
		}
		catch (ClassCastException e)
		{
			throw new RuntimeException(e);
		}
	}
}
