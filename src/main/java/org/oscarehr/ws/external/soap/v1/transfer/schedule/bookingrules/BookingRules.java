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

package org.oscarehr.ws.external.soap.v1.transfer.schedule.bookingrules;

import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;

public class BookingRules {
	private List<MultipleBookingsRule> multipleBookingsRule = new ArrayList<>();
	private BlackoutRule blackoutRule = null;
	private CutoffRule cutoffRule = null;

	public BookingRules(String jsonRules) throws ParseException {
		this.multipleBookingsRule = BookingRuleFactory.buildMultipleBookingsRuleList(jsonRules);
		this.blackoutRule = BookingRuleFactory.buildBlackoutRule(jsonRules);
		this.cutoffRule = BookingRuleFactory.buildCutoffRule(jsonRules);
	}

	public List<MultipleBookingsRule> getMultipleBookingsRule() {
		return multipleBookingsRule;
	}

	public void setMultipleBookingsRule(List<MultipleBookingsRule> multipleBookingsRule) {
		this.multipleBookingsRule = multipleBookingsRule;
	}

	public BlackoutRule getBlackoutRule() {
		return blackoutRule;
	}

	public void setBlackoutRule(BlackoutRule blackoutRule) {
		this.blackoutRule = blackoutRule;
	}

	public CutoffRule getCutoffRule() {
		return cutoffRule;
	}

	public void setCutoffRule(CutoffRule cutoffRule) {
		this.cutoffRule = cutoffRule;
	}
}
