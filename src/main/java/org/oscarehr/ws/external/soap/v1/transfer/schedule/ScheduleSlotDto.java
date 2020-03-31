package org.oscarehr.ws.external.soap.v1.transfer.schedule;

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
}
