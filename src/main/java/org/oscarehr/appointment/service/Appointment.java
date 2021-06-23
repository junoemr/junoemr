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
package org.oscarehr.appointment.service;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang.time.DateUtils;
import org.oscarehr.common.dao.LookupListItemDao;
import org.oscarehr.common.dao.OscarAppointmentDao;
import org.oscarehr.common.model.LookupList;
import org.oscarehr.common.model.LookupListItem;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.dataMigration.converter.in.AppointmentModelToDbConverter;
import org.oscarehr.integration.model.Integration;
import org.oscarehr.integration.myhealthaccess.dto.ClinicUserLoginTokenTo1;
import org.oscarehr.integration.myhealthaccess.service.AppointmentService;
import org.oscarehr.integration.myhealthaccess.service.ClinicService;
import org.oscarehr.integration.myhealthaccess.service.CommunicationService;
import org.oscarehr.integration.myhealthaccess.service.PatientService;
import org.oscarehr.integration.service.IntegrationService;
import org.oscarehr.managers.LookupListManager;
import org.oscarehr.provider.dao.ProviderDataDao;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.schedule.dto.AppointmentDetails;
import org.oscarehr.schedule.dto.CalendarAppointment;
import org.oscarehr.schedule.dto.CalendarEvent;
import org.oscarehr.site.service.SiteService;
import org.oscarehr.telehealth.service.MyHealthAccessService;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.ws.rest.conversion.AppointmentConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import oscar.OscarProperties;
import oscar.SxmlMisc;
import oscar.log.LogAction;
import oscar.log.LogConst;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;

@Service
@Transactional
public class Appointment
{
	@Autowired
	OscarAppointmentDao oscarAppointmentDao;

	@Autowired
	ProviderDataDao providerDataDao;

	@Autowired
	AppointmentService appointmentService;

	@Autowired
	PatientService patientService;

	@Autowired
	ClinicService clinicService;

	@Autowired
	MyHealthAccessService myHealthAccessService;

	@Autowired
	CommunicationService communicationService;

	@Autowired
	IntegrationService integrationService;

	@Autowired
	AppointmentModelToDbConverter appointmentModelToDbConverter;

	@Autowired
	private LookupListManager lookupListManager;

	@Autowired
	private LookupListItemDao lookupListItemDao;

	@Autowired
	SiteService siteService;

	private String formatName(String upperFirstName, String upperLastName)
	{
		List<String> outputList = new ArrayList<>();

		if(upperLastName != null)
		{
			outputList.add(WordUtils.capitalize(upperLastName.toLowerCase()));
		}

		if(upperFirstName != null)
		{
			outputList.add(WordUtils.capitalize(upperFirstName.toLowerCase()));
		}

		if(outputList.size() == 0)
		{
			return null;
		}

		return String.join(", ", outputList);
	}

	private String getStatus(String statusString)
	{
		if(statusString != null && statusString.length() > 0)
		{
			return statusString.substring(0, 1);
		}

		return null;
	}

	private String getStatusModifier(String statusString)
	{
		if(statusString != null && statusString.length() > 1)
		{
			return statusString.substring(1,2);
		}

		return null;
	}

	/**
	 * save an appointment in to the database. updating the MHA integration if applicable.
	 * @param appointment - the appointment to save
	 * @param loggedInInfo - logged in info.
	 */
	public org.oscarehr.common.model.Appointment saveNewAppointment(org.oscarehr.common.model.Appointment appointment,
	                                                                LoggedInInfo loggedInInfo, HttpServletRequest request,
	                                                                boolean sendNotification)
	{
		appointment.setCreator(loggedInInfo.getLoggedInProviderNo());
		appointment.setLastUpdateUser(loggedInInfo.getLoggedInProviderNo());
		oscarAppointmentDao.persist(appointment);

		if (OscarProperties.getInstance().isMyHealthAccessEnabled())
		{
			Integration integration = integrationService.findMhaIntegration(appointment);

			// send MHA based appointment notification
			if (sendNotification)
			{
				if (integration != null)
				{
					ClinicUserLoginTokenTo1 loginTokenTo1 = clinicService.loginOrCreateClinicUser(integration,
					                                                                              loggedInInfo.getLoggedInSecurity().getSecurityNo());
					appointmentService.sendGeneralAppointmentNotification(integration, loginTokenTo1.getToken(),
					                                                      appointment.getId());
				}
			}

			// notify provider, booking notification numbers if appointment is virtual
			if (appointment.getIsVirtual() && appointment.hasDemographic() && DateUtils.isSameDay(appointment.getStartTimeAsFullDate(), new Date()))
			{
				sendAppointmentProviderNotificationSms(appointment, integration);
			}
		}

		LogAction.addLogEntry(loggedInInfo.getLoggedInProviderNo(),
						appointment.getDemographicNo(),
						LogConst.ACTION_ADD,
						LogConst.CON_APPT,
						LogConst.STATUS_SUCCESS,
						String.valueOf(appointment.getId()),
						request.getRemoteAddr());

		return appointment;
	}

	/**
	 * save a new telehealth appointment
	 * @param appointment - the appointment to save
	 * @param loggedInInfo - logged in info.
	 * @param sendNotification - Whether to send notification of appointment booking to user or not.
	 */
	public org.oscarehr.common.model.Appointment saveNewTelehealthAppointment(org.oscarehr.common.model.Appointment appointment,
	                                                                          LoggedInInfo loggedInInfo, HttpServletRequest request, boolean sendNotification)
	{
		if (!appointment.getIsVirtual())
		{
			throw new IllegalArgumentException("Could not save telehealth appointment. Appointment is not virtual");
		}

		appointment.setCreator(loggedInInfo.getLoggedInProviderNo());
		appointment.setLastUpdateUser(loggedInInfo.getLoggedInProviderNo());

		oscarAppointmentDao.persist(appointment);

		// book telehealth appointment in MHA
		String siteName = null;
		if (OscarProperties.getInstance().isMultisiteEnabled())
		{
			siteName = appointment.getLocation();
		}
		Integration integration = integrationService.findMhaIntegration(siteName);

		if (patientService.isPatientConfirmed(appointment.getDemographicNo(), integration))
		{
			appointmentService.bookTelehealthAppointment(loggedInInfo, appointment, sendNotification);
		}
		else
		{
			appointmentService.bookOneTimeTelehealthAppointment(loggedInInfo, appointment, sendNotification);
		}

		// send provider booking notification
		if (appointment.hasDemographic() && DateUtils.isSameDay(appointment.getStartTimeAsFullDate(), new Date()))
		{
			sendAppointmentProviderNotificationSms(appointment, integration);
		}

		LogAction.addLogEntry(loggedInInfo.getLoggedInProviderNo(),
				appointment.getDemographicNo(),
				LogConst.ACTION_ADD,
				LogConst.CON_APPT,
				LogConst.STATUS_SUCCESS,
				String.valueOf(appointment.getId()),
				request.getRemoteAddr());

		return appointment;
	}

	public org.oscarehr.common.model.Appointment saveNewAppointment(org.oscarehr.dataMigration.model.appointment.Appointment appointment, Demographic demographic)
	{
		org.oscarehr.common.model.Appointment dbAppointment = appointmentModelToDbConverter.convert(appointment);
		dbAppointment.setDemographicNo(demographic.getDemographicId());

		// for now, Juno does not handle null values in these fields well, so set them to empty string
		dbAppointment.setReason(StringUtils.trimToEmpty(dbAppointment.getReason()));
		dbAppointment.setType(StringUtils.trimToEmpty(dbAppointment.getType()));
		dbAppointment.setLocation(StringUtils.trimToEmpty(dbAppointment.getLocation()));
		dbAppointment.setResources(StringUtils.trimToEmpty(dbAppointment.getResources()));

		oscarAppointmentDao.persist(dbAppointment);
		return dbAppointment;
	}

	public void saveNewAppointments(List<org.oscarehr.dataMigration.model.appointment.Appointment> appointmentList, Demographic demographic)
	{
		for(org.oscarehr.dataMigration.model.appointment.Appointment appointment : appointmentList)
		{
			saveNewAppointment(appointment, demographic);
		}
	}

	/**
	 * just like updateAppointment but takes a transfer object instead.
	 * @param calendarAppointment - the transfer object that represents the appointment.
	 * @param loggedInInfo - logged in info
	 * @param request - http request
	 * @return updated appointment
	 */
	public org.oscarehr.common.model.Appointment updateAppointment(CalendarAppointment calendarAppointment,
			LoggedInInfo loggedInInfo, HttpServletRequest request)
	{
		AppointmentConverter converter = new AppointmentConverter();
		org.oscarehr.common.model.Appointment appointment = converter.getAsDomainObject(calendarAppointment);

		// if appointment is confirmed make sure not to overwrite confirmation values.
		if (calendarAppointment.getAppointmentNo() != null && calendarAppointment.isConfirmed())
		{
			org.oscarehr.common.model.Appointment existingAppointment = oscarAppointmentDao.find(calendarAppointment.getAppointmentNo());

			// only preserve the confirmation values if the start / end time of the appointment has not changed.
			if (existingAppointment.getStartTimeAsFullDate().equals(appointment.getStartTimeAsFullDate()) &&
					existingAppointment.getEndTimeAsFullDate().equals(appointment.getEndTimeAsFullDate()))
			{
				existingAppointment.getConfirmedAt().ifPresent(appointment::setConfirmedAt);
				existingAppointment.getConfirmedBy().ifPresent(appointment::setConfirmedBy);
				existingAppointment.getConfirmedByType().ifPresent(appointment::setConfirmedByType);
			}
		}

		return updateAppointment(appointment, loggedInInfo, request);
	}

	/**
	 * update appointment. notifying MHA of update if applicable.
	 * @param appointment - appointment to update
	 */
	public org.oscarehr.common.model.Appointment updateAppointment(org.oscarehr.common.model.Appointment appointment,
																LoggedInInfo loggedInInfo, HttpServletRequest request)
	{
		appointment.setLastUpdateUser(loggedInInfo.getLoggedInProviderNo());
		oscarAppointmentDao.merge(appointment);

		if (appointment.getIsVirtual())
		{
			myHealthAccessService.queueAppointmentCacheUpdate(appointment);
		}

		LogAction.addLogEntry(loggedInInfo.getLoggedInProviderNo(),
						appointment.getDemographicNo(),
						LogConst.ACTION_UPDATE,
						LogConst.CON_APPT,
						LogConst.STATUS_SUCCESS,
						String.valueOf(appointment.getId()),
						request.getRemoteAddr());

		return appointment;
	}

	public List<CalendarEvent> getCalendarEvents(HttpSession session,
	                                             Integer providerId, LocalDate startDate, LocalDate endDate, String siteName)
	{
		return getCalendarEvents(session, providerId, startDate, endDate, siteName, new ArrayList<>(0));
	}
	public List<CalendarEvent> getCalendarEvents(HttpSession session,
		Integer providerId, LocalDate startDate, LocalDate endDate, String siteName, List<Integer> hiddenDays)
	{
		List<CalendarEvent> calendarEvents = new ArrayList<>();


		SortedMap<LocalTime, List<AppointmentDetails>> appointments =
			oscarAppointmentDao.findAppointmentDetailsByDateAndProvider(
				startDate, endDate, providerId, siteName);

		for(List<AppointmentDetails> dateList: appointments.values())
		{
			for(AppointmentDetails details: dateList)
			{
				LocalDateTime startDateTime =
						LocalDateTime.of(details.getDate(), details.getStartTime());

				int dayValue = startDateTime.getDayOfWeek().getValue() % 7;
				if (hiddenDays.contains(dayValue))
				{
					// don't include appointments on hidden days
					continue;
				}

				// Add an extra minute because oscar stores the endtime minus a minute
				// set the seconds to 0 to prevent overlap issues
				LocalDateTime endDateTime =
						LocalDateTime.of(details.getDate(), details.getEndTime()).plusMinutes(1).withSecond(0);


				String rawStatus = details.getStatus();

				String province = OscarProperties.getInstance().getBillingTypeUpperCase();
				String defaultView = OscarProperties.getInstance().getProperty("default_view");
				String userProviderNo = (String) session.getAttribute("user");
				String userFirstName = (String) session.getAttribute("userfirstname");
				String userLastName = (String) session.getAttribute("userlastname");

				String rdohip = null;
				if(oscar.OscarProperties.getInstance().isPropertyActive("auto_populate_billingreferral_bc"))
				{
					rdohip = SxmlMisc.getXmlContent(StringUtils.trimToEmpty(details.getFamilyDoctor()),"rdohip");
					rdohip = rdohip !=null ? rdohip : null;
				}
				boolean isSelfBooked = org.oscarehr.common.model.Appointment.BookingSource.MYOSCAR_SELF_BOOKING.name().equals(details.getBookingSource());

				CalendarAppointment appointment = new CalendarAppointment(
						details.getAppointmentNo(),
						province,
						defaultView,
						rdohip,
						userProviderNo,
						userFirstName,
						userLastName,
						details.getBirthday(),
						formatName(details.getFirstName(), details.getLastName()),
						null, // TODO get phone number
						details.getDemographicNo(),
						null, // TODO get patient's doctor
						startDateTime,
						endDateTime,
						getStatus(rawStatus),
						getStatusModifier(rawStatus),
						null,
						details.getReason(),
						details.getReasonCode(),
						details.getNotes(),
						null,
						details.getLocation(),
						details.getType(),
						details.getResources(),
						details.getUrgency(),
						details.getName().equals(org.oscarehr.common.model.Appointment.DONOTBOOK),
						isSelfBooked,
						false,
						details.isVirtual(),
						null,
						details.isConfirmed(),
						details.getCreatorSecurityId(),
						details.getBookingSource()
				);
				// for the case where appointments are saved with a name but no demographic
				if((appointment.getDemographicNo() == null || appointment.getDemographicNo() == 0) && details.getName() != null)
				{
					appointment.setAppointmentName(details.getName());
				}

				calendarEvents.add(new CalendarEvent(
					startDateTime,
					endDateTime,
					details.getJunoColor(),
					null,
					"text-dark",       // TODO-legacy remove?
					providerId, // TODO-legacy remove?
					null,
					null,
					appointment
				));
			}
		}

		return calendarEvents;
	}

	/**
	 * Appointment reason codes map to lookup list items, which usually have the correct IDs but may not
	 * be guaranteed to have the exact ID between instances.
	 * This is the "safe" way of getting the correct reasonCode id for the reason we're trying to set.
	 * @param reasonValue string corresponding to the reason code we're looking for
	 * @return id of the LookupListItem entry with the correct reasonCode and LookupList, -1 otherwise
	 */
	public Integer getIdForAppointmentReasonCode(String reasonValue)
	{
		// reasonCode is intended to be a foreign key on LookupListItem, which is referenced by LookupList
		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoAsCurrentClassAndMethod();
		LookupList reasonCodeList = lookupListManager.findLookupListByName(loggedInInfo, "reasonCode");
		List<LookupListItem> reasonCodes = lookupListItemDao.findAll(null, null);
		for (LookupListItem reasonCode : reasonCodes)
		{
			if (reasonCode.getLookupListId().equals(reasonCodeList.getId()) && reasonCode.getValue().equals(reasonValue))
			{
				return reasonCode.getId();
			}
		}
		return -1;
	}

	/**
	 * send appointment booking notification to providers notification numbers
	 * @param appointment - the appointment that the notification is about
	 * @param integration - integration used  to send the sms
	 */
	private void sendAppointmentProviderNotificationSms(org.oscarehr.common.model.Appointment appointment, Integration integration)
	{
		ProviderData provider = providerDataDao.find(appointment.getProviderNo());
		List<String> smsNumbers = provider.getBookingNotificationNumbersList();
		for (String phoneNumber: smsNumbers)
		{
			communicationService.sendSms(integration, phoneNumber, "New appointment: " + appointment.getName() + " booked for provider: " + provider.getDisplayName());
		}
	}

}
