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
import ca.uhn.hl7v2.model.Group;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Type;
import ca.uhn.hl7v2.model.v251.datatype.CQ;
import ca.uhn.hl7v2.model.v251.datatype.CWE;
import ca.uhn.hl7v2.model.v251.datatype.ID;
import ca.uhn.hl7v2.model.v251.datatype.NM;
import ca.uhn.hl7v2.model.v251.datatype.RPT;
import ca.uhn.hl7v2.model.v251.datatype.SI;
import ca.uhn.hl7v2.model.v251.datatype.TM;
import ca.uhn.hl7v2.model.v251.datatype.TS;
import ca.uhn.hl7v2.model.v251.datatype.TX;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import org.oscarehr.util.MiscUtils;

/**
 * Custom timing quantity. Hl7 allows only one rep of (TQ.2). However CoPD allows 2.
 */
public class TQ1 extends ca.uhn.hl7v2.model.v251.segment.TQ1
{
	public TQ1(Group parent, ModelClassFactory factory) {
		super(parent, factory);
		Message message = this.getMessage();

		try {
			this.add(SI.class, false, 1, 4, new Object[]{message}, "Set ID - TQ1");
			this.add(CQ.class, false, 2, 20, new Object[]{message}, "Quantity");
			this.add(RPT.class, false, 0, 540, new Object[]{message}, "Repeat Pattern");
			this.add(TM.class, false, 0, 20, new Object[]{message}, "Explicit Time");
			this.add(CQ.class, false, 0, 20, new Object[]{message}, "Relative Time and Units");
			this.add(CQ.class, false, 1, 20, new Object[]{message}, "Service Duration");
			this.add(TS.class, false, 1, 26, new Object[]{message}, "Start date/time");
			this.add(TS.class, false, 1, 26, new Object[]{message}, "End date/time");
			this.add(CWE.class, false, 0, 250, new Object[]{message}, "Priority");
			this.add(TX.class, false, 1, 250, new Object[]{message}, "Condition text");
			this.add(TX.class, false, 1, 250, new Object[]{message}, "Text instruction");
			this.add(ID.class, false, 1, 10, new Object[]{message, new Integer(427)}, "Conjunction");
			this.add(CQ.class, false, 1, 20, new Object[]{message}, "Occurrence duration");
			this.add(NM.class, false, 1, 10, new Object[]{message}, "Total occurrence's");
		} catch (HL7Exception var5) {
			MiscUtils.getLogger().info("Cannot create CoPD Custom TQ1 segment");
		}
	}

	public CQ getQuantity(int rep) {
		CQ ret = null;

		try {
			Type t = this.getField(2, rep);
			ret = (CQ)t;
			return ret;
		} catch (ClassCastException var3) {
			MiscUtils.getLogger().error("Unexpected problem obtaining field value.  This is a bug.", var3);
			throw new RuntimeException(var3);
		} catch (HL7Exception var4) {
			MiscUtils.getLogger().error("Unexpected problem obtaining field value.  This is a bug.", var4);
			throw new RuntimeException(var4);
		}
	}

	public int getQuantityReps()
	{
		return this.getReps(2);
	}


}
