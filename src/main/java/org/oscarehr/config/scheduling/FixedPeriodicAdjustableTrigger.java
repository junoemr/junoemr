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
package org.oscarehr.config.scheduling;

import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtils;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.Date;
import java.util.Optional;

/**
 * Custom trigger that allows a periodic scheduling task to run, but re-calculates the period upon completion of a scheduled task.
 * This allows the scheduler frequency of to be stored in an adjustable format.
 */
public class FixedPeriodicAdjustableTrigger implements Trigger
{
	private static final Logger logger = MiscUtils.getLogger();

	public interface AdjustablePeriod
	{
		TemporalAmount getPeriod();
	}

	private final AdjustablePeriod adjustablePeriod;
	private final TemporalAmount initialDelay;

	public FixedPeriodicAdjustableTrigger(AdjustablePeriod adjustablePeriod)
	{
		this(adjustablePeriod, Duration.ofMinutes(0));
	}

	public FixedPeriodicAdjustableTrigger(AdjustablePeriod adjustablePeriod, TemporalAmount initialDelay)
	{
		this.adjustablePeriod = adjustablePeriod;
		this.initialDelay = initialDelay;
	}

	@Override
	public Date nextExecutionTime(TriggerContext context)
	{
		Optional<Date> lastScheduledDateTime = Optional.ofNullable(context.lastScheduledExecutionTime());
		TemporalAmount period = this.adjustablePeriod.getPeriod();

		// continually increment the next scheduled time based on the period. This ensures that if the task runs beyond the previous period, the next execution is in the future.
		// this keeps the schedule on a fixed rate (no offset based on execution run time) without stacking tasks.
		Instant nextExecutionTime = lastScheduledDateTime.map(Date::toInstant).orElse(Instant.now().truncatedTo(ChronoUnit.MINUTES).plus(initialDelay));
		while(nextExecutionTime.isBefore(Instant.now()))
		{
			nextExecutionTime = nextExecutionTime.plus(period);
		}

		logger.info("nextExecutionTime -> " + Date.from(nextExecutionTime));
		return Date.from(nextExecutionTime);
	}
}
