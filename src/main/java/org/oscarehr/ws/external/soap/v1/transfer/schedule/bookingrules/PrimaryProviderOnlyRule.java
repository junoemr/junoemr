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

import org.json.simple.JSONObject;
import org.oscarehr.common.model.Appointment;
import org.oscarehr.demographic.dao.DemographicDao;
import org.oscarehr.demographic.entity.Demographic;
import org.oscarehr.util.SpringUtils;

/**
 * Patient can only book with their MRP
 */
public class PrimaryProviderOnlyRule extends BookingRule
{
    private String demographicMRP;
    private static final DemographicDao demographicDao = SpringUtils.getBean(DemographicDao.class);

    PrimaryProviderOnlyRule(String jsonType, Integer demographicNo)
    {
        super(BookingRuleType.BOOKING_PRIMARY_PROVIDER_ONLY, jsonType);

        Demographic demographic = demographicDao.find(demographicNo);
        if (demographic != null)
        {
            this.demographicMRP = demographic.getProviderNo();
        }
    }

    @Override
    public Boolean isViolated(Appointment appointment)
    {
        return !appointment.getProviderNo().equals(demographicMRP);
    }

    @Override
    public JSONObject toJSON()
    {
        JSONObject json = new JSONObject();
        json.put("name", this.jsonType);
        return json;
    }
}
