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

package org.oscarehr.ws.external.soap.v1.transfer.schedule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProviderScheduleTransfer
{
    private HashMap<String, List<DayTimeSlots>> providerScheduleResponse;

    public HashMap<String, List<DayTimeSlots>> getProviderScheduleResponse()
    {
        return providerScheduleResponse;
    }

    public void setProviderScheduleResponse(HashMap<String, List<DayTimeSlots>> providerScheduleResponse)
    {
        this.providerScheduleResponse = providerScheduleResponse;
    }

    public HashMap<String, DayTimeSlots[]> toTransfer()
    {
        HashMap<String, DayTimeSlots[]> transferResponse = new HashMap<>();

        for (Map.Entry<String, List<DayTimeSlots>> entry : this.providerScheduleResponse.entrySet())
        {
            String scheduleDay = entry.getKey();
            List<DayTimeSlots> timeSlotsList = entry.getValue();

            DayTimeSlots[] dayTimeSlotsTransfer = timeSlotsList.toArray(new DayTimeSlots[0]);

            transferResponse.put(scheduleDay, dayTimeSlotsTransfer);
        }

        return transferResponse;
    }
}
