package org.oscarehr.ws.external.soap.v1.transfer.schedule;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class ScheduleSlotDto
{
	String date;
	String startTime;
	int duration;

	public ScheduleSlotDto(String date, String startTime, int duration)
	{
		this.date = date;
		this.startTime = startTime;
		this.duration = duration;
	}

	public int getDuration()
	{
		return duration;
	}

	public void setDuration(int duration)
	{
		this.duration = duration;
	}

	public String getDate()
	{
		return date;
	}

	public void setDate(String date)
	{
		this.date = date;
	}

	public String getStartTime()
	{
		return startTime;
	}

	public void setStartTime(String startTime)
	{
		this.startTime = startTime;
	}

	public LocalDateTime getDateTime()
	{
		return LocalDateTime.parse(String.format("%sT%s", date, startTime));
	}

	public static List<ScheduleSlotDto> getSlotsInThreshold(List<ScheduleSlotDto> scheduleSlots, LocalDateTime dateTimePivot,
	                                                        long threshold, ChronoUnit granularity)
	{
		boolean slotIsAvailable = false;

		List<ScheduleSlotDto> slotsInThreshold = new ArrayList<>();

		for (ScheduleSlotDto slot : scheduleSlots)
		{
			LocalDateTime slotDateTime = slot.getDateTime();
			long nGranBetween = granularity.between(slotDateTime, dateTimePivot);

			if (nGranBetween < -threshold)
			{
				break;
			}
			else if (Math.abs(nGranBetween) <= threshold)
			{
				if (!slotIsAvailable)
				{
					slotIsAvailable = dateTimePivot.equals(slotDateTime);
				}

				slotsInThreshold.add(slot);
			}
		}

		if (!slotIsAvailable)
		{
			slotsInThreshold.clear();
		}

		return slotsInThreshold;
	}
}
