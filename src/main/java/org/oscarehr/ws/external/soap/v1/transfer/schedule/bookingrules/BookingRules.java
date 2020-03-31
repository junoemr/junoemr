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
