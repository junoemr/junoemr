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
import ca.uhn.hl7v2.model.v24.datatype.XAD;
import ca.uhn.hl7v2.model.v24.datatype.XCN;
import ca.uhn.hl7v2.model.v24.datatype.XON;
import ca.uhn.hl7v2.model.v24.datatype.XTN;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtils;

/**
 * This Segment conveys the Sender (From) Document Identifying Information
 */
public class ZS1 extends AbstractSegment
{
	private static final Logger logger = MiscUtils.getLogger();

	public ZS1(Group parent, ModelClassFactory factory)
	{
		super(parent, factory);
		Message message = this.getMessage();

		try
		{
			this.add(SI.class, true, 1, 4, new Object[]{message}, "Set ID - ZS001");
			this.add(CE.class, true, 0, 23, new Object[]{message}, "Participant Role - Sender");
			this.add(XCN.class, true, 1, 264, new Object[]{message}, "Stakeholder");
			this.add(ST.class, true, 1, 30, new Object[]{message}, "Auth Phy Specialty");
			this.add(XON.class, true, 1, 243, new Object[]{message}, "Organization Identifier");
			this.add(XAD.class, true, 1, 157, new Object[]{message}, "Organization Address");
			this.add(XTN.class, false, 3, 432, new Object[]{message}, "Organization Telecom");
			this.add(ST.class, true, 1, 50, new Object[]{message}, "Form Name - Sender");
			this.add(ST.class, false, 1, 50, new Object[]{message}, "Export Info");
			this.add(TS.class, true, 1, 14, new Object[]{message}, "Date/Time of transmission");
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
