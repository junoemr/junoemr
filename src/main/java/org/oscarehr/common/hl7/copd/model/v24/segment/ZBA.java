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
import ca.uhn.hl7v2.model.v24.datatype.TX;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtils;

/**
 * Data associated with billing submitted to Alberta Health &amp; Wellness
 */
public class ZBA extends AbstractSegment
{
	private static final Logger logger = MiscUtils.getLogger();

	public ZBA(Group parent, ModelClassFactory factory)
	{
		super(parent, factory);
		Message message = this.getMessage();

		try
		{
			this.add(SI.class, true, 1, 4, new Object[]{message}, "Set ID - ZBA");
			this.add(NM.class, true, 1, 15, new Object[]{message}, "Provider Practitioner Number");
			this.add(ST.class, false, 1, 15, new Object[]{message}, "Claim Number");
			this.add(NM.class, false, 1, 6, new Object[]{message}, "Facility Number");
			this.add(ST.class, false, 1, 4, new Object[]{message}, "Functional Centre");
			this.add(TS.class, true, 1, 9, new Object[]{message}, "Service Date");
			this.add(ST.class, true, 1, 10, new Object[]{message}, "Fee Code");
			this.add(TS.class, false, 1, 8, new Object[]{message}, "Date of Injury");
			this.add(ST.class, false, 1, 200, new Object[]{message}, "Injury Area");
			this.add(NM.class, false, 1, 1, new Object[]{message}, "Encounter Number");
			this.add(NM.class, false, 1, 5, new Object[]{message}, "Calls");
			this.add(TS.class, false, 1, 8, new Object[]{message}, "Hospital Admit Date");
			this.add(TS.class, false, 1, 8, new Object[]{message}, "Hospital Discharge Date");
			this.add(ST.class, false, 10, 6, new Object[]{message}, "Modifier");
			this.add(ST.class, false, 3, 7, new Object[]{message}, "Diagnostic Code");
			this.add(ST.class, false, 1, 1, new Object[]{message}, "EMSAF Indicator");
			this.add(NM.class, false, 1, 9, new Object[]{message}, "Fee Amount");
			this.add(NM.class, true, 1, 9, new Object[]{message}, "Bill Amount");
			this.add(ST.class, false, 1, 1, new Object[]{message}, "Claim Amount Indicator");
			this.add(NM.class, true, 1, 9, new Object[]{message}, "Amount Paid");
			this.add(ST.class, false, 10, 4, new Object[]{message}, "Explanatory Code");
			this.add(NM.class, false, 1, 5, new Object[]{message}, "Statement Number");
			this.add(TS.class, true, 1, 8, new Object[]{message}, "Date Billed");
			this.add(TS.class, false, 1, 8, new Object[]{message}, "Date Bill Paid");
			this.add(ST.class, false, 1, 6, new Object[]{message}, "Bill Account");
			this.add(ST.class, false, 1, 60, new Object[]{message}, "Bill To");
			this.add(ST.class, true, 1, 8, new Object[]{message}, "Status of Bill");
			this.add(TX.class, false, 1, 250, new Object[]{message}, "Reconcile Comment");
			this.add(NM.class, false, 1, 5, new Object[]{message}, "Referral Practitioner Number");
			this.add(ST.class, false, 1, 4, new Object[]{message}, "Skill Code");
			this.add(NM.class, false, 1, 7, new Object[]{message}, "Business Arrangement");
			this.add(ST.class, true, 1, 4, new Object[]{message}, "Pay To");
			this.add(ST.class, false, 1, 9, new Object[]{message}, "Pay To ULI");
			this.add(ST.class, false, 1, 1, new Object[]{message}, "Paper Docs Indicator");
			this.add(ST.class, false, 1, 4, new Object[]{message}, "Intercept");
			this.add(ST.class, false, 1, 1, new Object[]{message}, "Good Faith Indicator");
			this.add(ST.class, false, 1, 4, new Object[]{message}, "New Born");
			this.add(NM.class, false, 1, 7, new Object[]{message}, "Locum");
			this.add(CE.class, false, 1, 28, new Object[]{message}, "Billing Action Code");
			this.add(ST.class, false, 1, 1, new Object[]{message}, "Billing Confidential Indicator");
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
