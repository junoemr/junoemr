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
import ca.uhn.hl7v2.model.v24.datatype.NM;
import ca.uhn.hl7v2.model.v24.datatype.SI;
import ca.uhn.hl7v2.model.v24.datatype.ST;
import ca.uhn.hl7v2.model.v24.datatype.TS;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtils;

/**
 * Invoice Summary - Third party billing
 */
public class ZIS extends AbstractSegment
{
	private static final Logger logger = MiscUtils.getLogger();

	public ZIS(Group parent, ModelClassFactory factory)
	{
		super(parent, factory);
		Message message = this.getMessage();

		try
		{
			this.add(SI.class, true, 1, 4, new Object[]{message}, "Set ID - ZIS");
			this.add(ST.class, true, 1, 15, new Object[]{message}, "Invoice Number");
			this.add(TS.class, true, 1, 8, new Object[]{message}, "Invoice Date");
			this.add(CE.class, false, 1, 89, new Object[]{message}, "Agency ID");
			this.add(ST.class, false, 1, 18, new Object[]{message}, "Agency File Number");
			this.add(NM.class, false, 1, 10, new Object[]{message}, "Insurance Group Number");
			this.add(ST.class, false, 1, 50, new Object[]{message}, "Payment Responsibility");
			this.add(TS.class, false, 1, 8, new Object[]{message}, "Bill Date");
			this.add(NM.class, false, 1, 10, new Object[]{message}, "GST Amount");
			this.add(NM.class, false, 1, 10, new Object[]{message}, "PST Amount");
			this.add(TS.class, true, 0, 8, new Object[]{message}, "Payment Date");
			this.add(NM.class, false, 1, 2, new Object[]{message}, "Accounting Month");
			this.add(NM.class, false, 1, 10, new Object[]{message}, "Amount Paid");
			this.add(ST.class, false, 1, 10, new Object[]{message}, "Payment Method");
			this.add(ST.class, false, 1, 11, new Object[]{message}, "Account Number");
			this.add(ST.class, false, 1, 35, new Object[]{message}, "Created By");
			this.add(TS.class, false, 0, 14, new Object[]{message}, "Creation Date/Time");
			this.add(ST.class, false, 1, 35, new Object[]{message}, "Modified By");
			this.add(TS.class, false, 0, 14, new Object[]{message}, "Modified Date/Time");
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
