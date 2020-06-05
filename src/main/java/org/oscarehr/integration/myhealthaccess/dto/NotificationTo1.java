package org.oscarehr.integration.myhealthaccess.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.oscarehr.common.model.Appointment;
import org.oscarehr.demographic.dao.DemographicDao;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.provider.dao.ProviderDataDao;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.util.SpringUtils;
import oscar.util.ConversionUtils;
import oscar.util.Jackson.ZonedDateTimeStringDeserializer;
import oscar.util.Jackson.ZonedDateTimeStringSerializer;

import java.time.ZonedDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationTo1
{
	// Notification fields
	@JsonProperty("send_to_email")
	private String sendToEmail;

	// Demographic fields
	@JsonProperty("patient_first_name")
	private String demographicFirstName;
	@JsonProperty("patient_last_name")
	private String demographicLastName;
	@JsonProperty("patient_full_name")
	private String demographicFullName;

	// Provider fields
	@JsonProperty("provider_first_name")
	private String providerFirstName;
	@JsonProperty("provider_last_name")
	private String providerLastName;
	@JsonProperty("provider_full_name")
	private String providerFullName;

	// Appointment Fields
	@JsonProperty("appointment_date")
	@JsonSerialize(using = ZonedDateTimeStringSerializer.class)
	@JsonDeserialize(using = ZonedDateTimeStringDeserializer.class)
	private ZonedDateTime appointmentDate;

	public NotificationTo1(Appointment appointment)
	{
		DemographicDao demographicDao 	= (DemographicDao) SpringUtils.getBean("demographic.dao.DemographicDao");
		ProviderDataDao providerDataDao = SpringUtils.getBean(ProviderDataDao.class);

		ProviderData providerData 	= providerDataDao.find(appointment.getProviderNo());
		Demographic demographic 	= demographicDao.find(appointment.getDemographicNo());

		this.sendToEmail = demographic.getEmail();

		this.demographicFirstName = demographic.getFirstName();
		this.demographicLastName = demographic.getLastName();
		this.demographicFullName = demographic.getDisplayName();

		this.providerFirstName = providerData.getFirstName();
		this.providerLastName = providerData.getLastName();
		this.providerFullName = providerData.getDisplayName();

		this.appointmentDate = ConversionUtils.toZonedDateTime(appointment.getStartTimeAsFullDate());
	}

	public String getSendToEmail()
	{
		return sendToEmail;
	}

	public void setSendToEmail(String sendToEmail)
	{
		this.sendToEmail = sendToEmail;
	}

	public String getDemographicFirstName()
	{
		return demographicFirstName;
	}

	public void setDemographicFirstName(String demographicFirstName)
	{
		this.demographicFirstName = demographicFirstName;
	}

	public String getDemographicLastName()
	{
		return demographicLastName;
	}

	public void setDemographicLastName(String demographicLastName)
	{
		this.demographicLastName = demographicLastName;
	}

	public String getDemographicFullName()
	{
		return demographicFullName;
	}

	public void setDemographicFullName(String demographicFullName)
	{
		this.demographicFullName = demographicFullName;
	}

	public String getProviderFirstName()
	{
		return providerFirstName;
	}

	public void setProviderFirstName(String providerFirstName)
	{
		this.providerFirstName = providerFirstName;
	}

	public String getProviderLastName()
	{
		return providerLastName;
	}

	public void setProviderLastName(String providerLastName)
	{
		this.providerLastName = providerLastName;
	}

	public String getProviderFullName()
	{
		return providerFullName;
	}

	public void setProviderFullName(String providerFullName)
	{
		this.providerFullName = providerFullName;
	}

	public ZonedDateTime getAppointmentDate()
	{
		return appointmentDate;
	}

	public void setAppointmentDate(ZonedDateTime appointmentDate)
	{
		this.appointmentDate = appointmentDate;
	}
}
