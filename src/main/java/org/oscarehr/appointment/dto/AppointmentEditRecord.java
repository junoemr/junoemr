package org.oscarehr.appointment.dto;

import java.time.LocalDateTime;

public class AppointmentEditRecord
{
	private Integer id;
	private Integer appointmentNo;
	private String providerNo;
	private LocalDateTime appointmentDate;
	private Integer demographicNo;
	private LocalDateTime updateDateTime;
	private LocalDateTime  createDateTime;
	private String lastUpdateUser;
	private String creator;

	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public Integer getAppointmentNo()
	{
		return appointmentNo;
	}

	public void setAppointmentNo(Integer appointmentNo)
	{
		this.appointmentNo = appointmentNo;
	}

	public String getProviderNo()
	{
		return providerNo;
	}

	public void setProviderNo(String providerNo)
	{
		this.providerNo = providerNo;
	}

	public LocalDateTime getAppointmentDate()
	{
		return appointmentDate;
	}

	public void setAppointmentDate(LocalDateTime appointmentDate)
	{
		this.appointmentDate = appointmentDate;
	}

	public Integer getDemographicNo()
	{
		return demographicNo;
	}

	public void setDemographicNo(Integer demographicNo)
	{
		this.demographicNo = demographicNo;
	}

	public LocalDateTime getUpdateDateTime()
	{
		return updateDateTime;
	}

	public void setUpdateDateTime(LocalDateTime updateDateTime)
	{
		this.updateDateTime = updateDateTime;
	}

	public LocalDateTime getCreateDateTime()
	{
		return createDateTime;
	}

	public void setCreateDateTime(LocalDateTime createDateTime)
	{
		this.createDateTime = createDateTime;
	}

	public String getLastUpdateUser()
	{
		return lastUpdateUser;
	}

	public void setLastUpdateUser(String lastUpdateUser)
	{
		this.lastUpdateUser = lastUpdateUser;
	}

	public String getCreator()
	{
		return creator;
	}

	public void setCreator(String creator)
	{
		this.creator = creator;
	}
}
