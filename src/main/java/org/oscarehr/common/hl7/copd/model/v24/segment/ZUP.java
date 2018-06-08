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
import ca.uhn.hl7v2.model.v24.datatype.CWE;
import ca.uhn.hl7v2.model.v24.datatype.TS;
import ca.uhn.hl7v2.model.v24.datatype.XCN;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtils;

/**
 * This will carry the date, identifcation, and assigning authority of the Health Service Provider who recorded the
 * prescription annotation.This will carry the date, identifcation, and assigning authority of the Health Service Provider
 * who recorded the prescription annotation. This segment contains information about an update that is not supported
 * by the segment that contains the data that was changed. This Segment is RE - Required Empty for the POSP CoPD
 * and does not need to be populated.
 */
public class ZUP extends AbstractSegment
{
	private static final Logger logger = MiscUtils.getLogger();

	public ZUP(Group parent, ModelClassFactory factory)
	{
		super(parent, factory);
		Message message = this.getMessage();

		try
		{
			this.add(TS.class, true, 1, 24, new Object[]{message}, "Last Update Date");
			this.add(XCN.class, true, 1, 4834, new Object[]{message}, "Last Update By");
			this.add(XCN.class, true, 1, 4789, new Object[]{message}, "Last Update Authorized By");
			this.add(CWE.class, false, 1, 5104, new Object[]{message}, "Reason");
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
