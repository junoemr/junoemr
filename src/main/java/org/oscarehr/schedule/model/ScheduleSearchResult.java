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

package org.oscarehr.schedule.model;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ScheduleSearchResult implements Comparable<ScheduleSearchResult>
{

    public LocalDateTime dateTime;
    public char templateCode;
    public Long length;
    public String providerNo;

    public ScheduleSearchResult(Date date, Time time, char templateCode, Long length, String providerNo)
    {
        this.templateCode = templateCode;
        this.length = length;
        this.providerNo = providerNo;

        LocalDate localDate = date.toLocalDate();
        LocalTime localTime = time.toLocalTime();

        this.dateTime = localDate.atTime(localTime);
    }

    @Override
    public int compareTo(ScheduleSearchResult o)
    {
        return dateTime.compareTo(o.dateTime);
    }
}
