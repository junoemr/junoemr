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

package oscar.oscarBilling.ca.bc.data;

import java.io.Serializable;

public class BillingVisit implements Serializable {
	String billingvisit = "";
	String description = "";
	String displayName = "";

	public BillingVisit(Object[] o) {
		this(String.valueOf(o[0]), String.valueOf(o[1]));
	}

	public BillingVisit(String billingvisit, String description) {
		this.billingvisit = billingvisit;
		this.description = description;

	}

	public String getVisitType() {
		return billingvisit;
	}

	public String getDescription() {
		return description;
	}

	public String getDisplayName() {
		return billingvisit + "|" + description;
	}

}
