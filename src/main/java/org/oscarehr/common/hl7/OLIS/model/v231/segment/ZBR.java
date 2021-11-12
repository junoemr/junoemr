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
package org.oscarehr.common.hl7.OLIS.model.v231.segment;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.AbstractSegment;
import ca.uhn.hl7v2.model.Group;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v231.datatype.IS;
import ca.uhn.hl7v2.model.v231.datatype.ST;
import ca.uhn.hl7v2.model.v231.datatype.XAD;
import ca.uhn.hl7v2.model.v231.datatype.XON;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import org.oscarehr.util.MiscUtils;

/**
 * 10.2.5.8 ZBR – Observation Request Extension Segment
 */
public class ZBR extends AbstractSegment
{
	public ZBR(Group parent, ModelClassFactory factory)
	{
		super(parent, factory);
		Message message = this.getMessage();

		try
		{
			this.add(ST.class, false, 1,1, new Object[]{message}, "Test Request Blocking Indicator");
			this.add(XON.class, true, 1,523, new Object[]{message}, "Test Request Placer");
			this.add(XON.class, false, 1,523, new Object[]{message}, "Specimen Collector");
			this.add(XON.class, false, 1,523, new Object[]{message}, "Reporting Laboratory");
			this.add(XAD.class, false, 1,118, new Object[]{message}, "Reporting Laboratory Address");
			this.add(XON.class, false, 1,523, new Object[]{message}, "Performing Laboratory");
			this.add(XAD.class, false, 1,118, new Object[]{message}, "Performing Laboratory Address");
			this.add(XON.class, false, 1,523, new Object[]{message}, "Destination Laboratory");
			this.add(IS.class, false, 5,3, new Object[]{message}, "Reportable Test Indicator");
			this.add(IS.class, false, 5,5, new Object[]{message}, "Business Rule Intervention Code");
			this.add(ST.class, false, 1,15, new Object[]{message}, "Test Request Sort Key");
			this.add(ST.class, false, 1,1, new Object[]{message}, "Referred Test Indicator");
			this.add(ST.class, false, 1,1, new Object[]{message}, "Full Replace Amendment");
			this.add(ST.class, false, 1,1, new Object[]{message}, "Test Request Replace Amendment");
		}
		catch (HL7Exception var5)
		{
			MiscUtils.getLogger().error("Can't instantiate " + this.getClass().getName(), var5);
		}
	}

	public ST getZbr1_testRequestBlockingIndicator() throws HL7Exception
	{
		return (ST) this.getField(1, 0);
	}
	public XON getZbr2_testRequestPlacer() throws HL7Exception
	{
		return (XON) this.getField(2, 0);
	}
	public XON getZbr3_specimenCollector() throws HL7Exception
	{
		return (XON) this.getField(3, 0);
	}
	public XON getZbr4_reportingLaboratory() throws HL7Exception
	{
		return (XON) this.getField(4, 0);
	}
	public XAD getZbr5_reportingLaboratoryAddress() throws HL7Exception
	{
		return (XAD) this.getField(5, 0);
	}
	public XON getZbr6_performingLaboratory() throws HL7Exception
	{
		return (XON) this.getField(6, 0);
	}
	public XAD getZbr7_performingLaboratoryAddress() throws HL7Exception
	{
		return (XAD) this.getField(7, 0);
	}
	public XON getZbr8_destinationLaboratory() throws HL7Exception
	{
		return (XON) this.getField(8, 0);
	}
	public IS getZbr9_reportableTestIndicator() throws HL7Exception
	{
		return (IS) this.getField(9, 0);
	}
	public IS getZbr10_businessRuleInterventionCode() throws HL7Exception
	{
		return (IS) this.getField(10, 0);
	}
	public ST getZbr11_testRequestSortKey() throws HL7Exception
	{
		return (ST) this.getField(11, 0);
	}
	public ST getZbr12_referredTestIndicator() throws HL7Exception
	{
		return (ST) this.getField(12, 0);
	}
	public ST getZbr13_fullReplaceAmendment() throws HL7Exception
	{
		return (ST) this.getField(13, 0);
	}
	public ST getZbr14_testRequestReplaceAmendment() throws HL7Exception
	{
		return (ST) this.getField(14, 0);
	}

}
