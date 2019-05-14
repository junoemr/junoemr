package org.oscarehr.ws.external.soap.v1.transfer.schedule.cancelrules;

import org.json.simple.JSONObject;
import org.oscarehr.common.model.Appointment;
import org.oscarehr.schedule.model.ScheduleSearchResult;

public abstract class CancelRule
{

    protected String jsonType;

    CancelRule(String jsonType)
    {
        this.jsonType = jsonType;
    }

    /**
     * Determines if the appointment violates this booking rule.
     * @param appointment The appointment to validate
     * @return true if rule is violated
     */
    public abstract Boolean isViolated(Appointment appointment);

    /**
     * Determines if the schedule slot would violate the booking rule if an appointment were to be booked into it.
     * @param result
     * @return true if rule is violated
     */
    public abstract Boolean isViolated(ScheduleSearchResult result);

    /**
     * Transform this rule into a JSON object for serialization which is compatible with MyHealthAccess schema
     * @return JSON representation
     */
    public abstract JSONObject toJSON();
}
