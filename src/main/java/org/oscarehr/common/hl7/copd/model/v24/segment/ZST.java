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
import ca.uhn.hl7v2.model.v24.datatype.CQ;
import ca.uhn.hl7v2.model.v24.datatype.CWE;
import ca.uhn.hl7v2.model.v24.datatype.NM;
import ca.uhn.hl7v2.model.v24.datatype.SI;
import ca.uhn.hl7v2.model.v24.datatype.TS;
import ca.uhn.hl7v2.model.v24.datatype.XCN;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtils;

/**
 * This Segment is RE - Required Empty for the POSP CoPD and does not need to be populated. This segment
 * contains information about a prescription status change that is not handled by the ORC segment.Each occurrence of
 * the ZST segment represents a different status change over the life of the prescription. The status changes will be
 * listed in reverse-chronological order (most recent is first occurrence). When submitting a new status change, there
 * should only be one occurrence. It will contain the information pertaining to the new status. Some of this information
 * in this segment may be a copy of the information in the ORC segment, to allow for backward compatibility.
 */
public class ZST extends AbstractSegment
{
	private static final Logger logger = MiscUtils.getLogger();

	public ZST(Group parent, ModelClassFactory factory)
	{
		super(parent, factory);
		Message message = this.getMessage();

		try
		{
			this.add(SI.class, true, 1, 4, new Object[]{message}, "Set ID - ZST");
			this.add(CE.class, true, 1, 4104, new Object[]{message}, "Status");
			//spec missing segment 3
			this.add(TS.class, true, 1, 24, new Object[]{message}, "Effective Date");
			this.add(XCN.class, true, 1, 4789, new Object[]{message}, "Changed By");
			this.add(CWE.class, true, 1, 6104, new Object[]{message}, "Reason");
			//spec missing segment 7
			//spec missing segment 8
			//spec missing segment 9
			this.add(TS.class, false, 1, 24, new Object[]{message}, "Expected End Date");
			this.add(TS.class, false, 1, 24, new Object[]{message}, "Actual End Date");
			//spec missing segment 12
			this.add(NM.class, false, 1, 4, new Object[]{message}, "Fills Remaining");
			this.add(CQ.class, false, 1, 4135, new Object[]{message}, "Supply Duration");
			this.add(TS.class, true, 1, 24, new Object[]{message}, "Event Time");
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
