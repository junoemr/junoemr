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
package org.oscarehr.ws.rest.transfer.billing;
import java.util.ArrayList;
import java.util.List;

public class BCBillingVisitCodeTo1
{
	private String visitType;
	private String visitDescription;

	public static List<BCBillingVisitCodeTo1> fromList(List<Object[]> billingVisitList)
	{
		ArrayList<BCBillingVisitCodeTo1> bcBillingVisitCodeTo1s = new ArrayList<>();
		for (Object[] billingVisit : billingVisitList)
		{
			bcBillingVisitCodeTo1s.add(new BCBillingVisitCodeTo1(billingVisit));
		}
		return bcBillingVisitCodeTo1s;
	}

	public BCBillingVisitCodeTo1(Object[] billingVisit)
	{
		this.visitType = (String)billingVisit[0];
		this.visitDescription = (String)billingVisit[1];
	}

	public String getVisitType()
	{
		return visitType;
	}

	public void setVisitType(String visitType)
	{
		this.visitType = visitType;
	}

	public String getVisitDescription()
	{
		return visitDescription;
	}

	public void setVisitDescription(String visitDescription)
	{
		this.visitDescription = visitDescription;
	}
}
