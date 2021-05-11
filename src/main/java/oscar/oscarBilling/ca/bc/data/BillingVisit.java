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
