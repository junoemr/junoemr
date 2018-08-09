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
 * Invoice Details - Third party billing
 */
public class ZID extends AbstractSegment
{
	private static final Logger logger = MiscUtils.getLogger();

	public ZID(Group parent, ModelClassFactory factory)
	{
		super(parent, factory);
		Message message = this.getMessage();

		try
		{
			this.add(SI.class, true, 1, 4, new Object[]{message}, "Set ID - ZID");
			this.add(ST.class, true, 1, 10, new Object[]{message}, "Fee Code");
			this.add(NM.class, true, 1, 3, new Object[]{message}, "Units");
			this.add(ST.class, true, 1, 1, new Object[]{message}, "GST Indicator");
			this.add(NM.class, true, 1, 10, new Object[]{message}, "Fee Amount");
			this.add(ST.class, false, 1, 10, new Object[]{message}, "Part ID");
			this.add(NM.class, true, 1, 10, new Object[]{message}, "Bill Amount");
			this.add(ST.class, true, 1, 1, new Object[]{message}, "PST Indicator");
			this.add(TS.class, false, 3, 8, new Object[]{message}, "Adjustment Date");
			this.add(NM.class, false, 3, 10, new Object[]{message}, "Adjustment Amount");
			this.add(ST.class, false, 3, 25, new Object[]{message}, "Adjustment Explanation");
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
}
