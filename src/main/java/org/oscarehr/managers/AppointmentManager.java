/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */
package org.oscarehr.managers;

import org.apache.log4j.Logger;
import org.oscarehr.appointment.dao.AppointmentStatusDao;
import org.oscarehr.appointment.dto.AppointmentEditRecord;
import org.oscarehr.appointment.model.AppointmentStatusList;
import org.oscarehr.common.dao.AppointmentArchiveDao;
import org.oscarehr.common.dao.LookupListDao;
import org.oscarehr.common.dao.OscarAppointmentDao;
import org.oscarehr.common.model.Appointment;
import org.oscarehr.common.model.AppointmentArchive;
import org.oscarehr.common.model.AppointmentStatus;
import org.oscarehr.common.model.LookupList;
import org.oscarehr.common.model.LookupListItem;
import org.oscarehr.integration.model.Integration;
import org.oscarehr.integration.myhealthaccess.dto.ClinicUserLoginTokenTo1;
import org.oscarehr.integration.myhealthaccess.service.AppointmentService;
import org.oscarehr.integration.myhealthaccess.service.ClinicService;
import org.oscarehr.integration.myhealthaccess.service.PatientService;
import org.oscarehr.integration.service.IntegrationService;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.telehealth.service.MyHealthAccessService;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.annotation.Transactional;
import oscar.util.ConversionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/** use <org.oscarehr.appointment.service.Appointment> instead */
@Deprecated
@Service
@Transactional
public class AppointmentManager {

	protected Logger logger = MiscUtils.getLogger();

	@Autowired
	private OscarAppointmentDao appointmentDao;
	@Autowired
	private AppointmentStatusDao appointmentStatusDao;
	@Autowired
	private LookupListDao lookupListDao;
	@Autowired
	private SecurityInfoManager securityInfoManager;
	@Autowired
	private AppointmentArchiveDao appointmentArchiveDao;
	@Autowired
	private MyHealthAccessService myHealthAccessService;
	@Autowired
	private AppointmentService appointmentService;
	@Autowired
	private IntegrationService integrationService;
	@Autowired
	private PatientService patientService;
	@Autowired
	private ClinicService clinicService;

	public List<Appointment> getAppointmentHistoryWithoutDeleted(LoggedInInfo loggedInInfo, Integer demographicNo, Integer offset, Integer limit) {
		if (!securityInfoManager.hasPrivilege(loggedInInfo, "_appointment", "r", null)) {
			throw new RuntimeException("Access Denied");
		}
		
		List<Appointment> result = new ArrayList<Appointment>();
		StringBuilder ids = new StringBuilder();

		List<Appointment> nonDeleted = appointmentDao.getAppointmentHistory(demographicNo, offset, limit);
		for (Appointment tmp : nonDeleted) {
			ids.append(tmp.getId() + ",");
		}
		result.addAll(nonDeleted);
		return result;
	}

	public List<Object> getAppointmentHistoryWithDeleted(LoggedInInfo loggedInInfo, Integer demographicNo, Integer offset, Integer limit) {
		if (!securityInfoManager.hasPrivilege(loggedInInfo, "_appointment", "r", null)) {
			throw new RuntimeException("Access Denied");
		}
		
		List<Object> result = new ArrayList<Object>();
		StringBuilder ids = new StringBuilder();

		List<Appointment> nonDeleted = appointmentDao.getAppointmentHistory(demographicNo, offset, limit);
		for (Appointment tmp : nonDeleted) {
			ids.append(tmp.getId() + ",");
		}
		result.addAll(nonDeleted);

		List<AppointmentArchive> deleted = appointmentDao.getDeletedAppointmentHistory(demographicNo, offset, limit);

		for (AppointmentArchive aa : deleted) {
			if (!hasAppointmentNo(result, aa.getAppointmentNo()) && !aaIsAlreadyInList(result, aa)) {
				result.add(aa);
				ids.append(aa.getId() + ",");
			}
		}

		return result;
	}

	private boolean hasAppointmentNo(List<Object> appts, Integer appointmentNo) {
		for (Object o : appts) {
			if (o instanceof Appointment) {
				Appointment appt = (Appointment) o;
				if (appt.getId().equals(appointmentNo)) return true;
			}
		}
		return false;
	}

	private boolean aaIsAlreadyInList(List<Object> appts, AppointmentArchive aa) {
		for (Object o : appts) {
			if (o instanceof AppointmentArchive) {
				AppointmentArchive appt = (AppointmentArchive) o;
				if (appt.getAppointmentNo().equals(aa.getAppointmentNo())) return true;
			}
		}
		return false;
	}

	/**
	 * Returns appointment for display.
	 *
	 * @param loggedInInfo
	 * @param appointment - appointment data
	 */
	public Appointment addAppointment(LoggedInInfo loggedInInfo, Appointment appointment, boolean sendNotification) {
		if (!securityInfoManager.hasPrivilege(loggedInInfo, "_appointment", "w", null)) {
			throw new RuntimeException("Access Denied");
		}

		// Set automatic information
		appointment.setCreator(loggedInInfo.getLoggedInProviderNo());
		appointment.setLastUpdateUser(loggedInInfo.getLoggedInProviderNo());
		appointment.setCreateDateTime(new Date());
		appointment.setUpdateDateTime(new Date());

		appointmentDao.persist(appointment);

		if (sendNotification)
		{// send booking notification through MHA
			Integration integration = integrationService.findMhaIntegration(appointment);
			if (integration != null)
			{
				ClinicUserLoginTokenTo1 loginTokenTo1 = clinicService.loginOrCreateClinicUser(integration,
						loggedInInfo.getLoggedInSecurity().getSecurityNo());
				appointmentService.sendGeneralAppointmentNotification(integration, loginTokenTo1.getToken(),
						appointment.getId());
			}
		}

		return appointment;
	}

	public List<Appointment> addRepeatingAppointment(LoggedInInfo loggedInInfo, Appointment appointment, List<Date> dateList)
	{
		if(!securityInfoManager.hasPrivilege(loggedInInfo, "_appointment", "w", null))
		{
			throw new RuntimeException("Access Denied");
		}
		ArrayList<Appointment> appointments = new ArrayList<>(dateList.size());

		appointment = addAppointment(loggedInInfo, appointment, false);
		appointments.add(appointment);

		for(Date date : dateList)
		{
			Appointment appointmentCopy = new Appointment(appointment);
			appointmentCopy.setAppointmentDate(date);
			appointmentCopy = addAppointment(loggedInInfo, appointmentCopy, false);
			appointments.add(appointmentCopy);
		}

		return appointments;
	}

	public Appointment updateAppointment(LoggedInInfo loggedInInfo, Appointment appointment) throws Throwable {
		
		if (!securityInfoManager.hasPrivilege(loggedInInfo, "_appointment", "w", null)) {
			throw new RuntimeException("Access Denied");
		}

		Appointment existing = appointmentDao.find(appointment.getId());
		if(existing == null) {
			throw new RuntimeException("Attempt to update an appointment that doesn't exist");
		}

		appointmentArchiveDao.archiveAppointment(existing);

		// Update automatic information
		appointment.setLastUpdateUser(loggedInInfo.getLoggedInProviderNo());
		appointment.setUpdateDateTime(new Date());

		// Copy automatic information
		appointment.setCreator(existing.getCreator());
		appointment.setCreateDateTime(existing.getCreateDateTime());

		try
		{
			logger.info(appointment.toString());
			appointmentDao.merge(appointment);

			if(appointment.getIsVirtual())
			{
				myHealthAccessService.queueAppointmentCacheUpdate(appointment);
			}
		}
		catch(TransactionSystemException exception)
		{
			// If a transaction exception is thrown, it might have been caused by a validation error,
			// but won't be caught by the exception mapper.
			// XXX: maybe just throw the root cause if it's a ConstraintViolationException?
			throw exception.getRootCause();
		}
		return appointment;
	}

	public void deleteAppointment(LoggedInInfo loggedInInfo, int apptNo) {
		if (!securityInfoManager.hasPrivilege(loggedInInfo, "_appointment", "d", null)) {
			throw new RuntimeException("Access Denied");
		}
		Appointment existing = appointmentDao.find(apptNo);
		if(existing != null) {
			appointmentArchiveDao.archiveAppointment(existing);

			if(existing.getIsVirtual())
			{
				myHealthAccessService.queueAppointmentCacheDelete(existing);
			}
		}
		
		appointmentDao.remove(apptNo);
	}

	public String rotateStatus(LoggedInInfo loggedInInfo, int apptNo)
	{
		if (!securityInfoManager.hasPrivilege(loggedInInfo, "_appointment", "w", null)) {
			throw new RuntimeException("Access Denied");
		}

		Appointment appointment = appointmentDao.find(apptNo);

		appointmentArchiveDao.archiveAppointment(appointment);

		AppointmentManager appointmentManager = SpringUtils.getBean(AppointmentManager.class);
		AppointmentStatusList appointmentStatusList =
				AppointmentStatusList.factory(appointmentManager);

		String nextStatus = appointmentStatusList.getStatusAfter(appointment.getStatus());

		appointment.setStatus(nextStatus);

		appointmentDao.merge(appointment);
		if(appointment.getIsVirtual())
		{
			myHealthAccessService.queueAppointmentCacheUpdate(appointment);
		}

		// return status without modifier
		return appointment.getAppointmentStatus();
	}

	public Appointment getAppointment(LoggedInInfo loggedInInfo, int apptNo) {
		if (!securityInfoManager.hasPrivilege(loggedInInfo, "_appointment", "r", null)) {
			throw new RuntimeException("Access Denied");
		}
		Appointment appt = appointmentDao.find(apptNo);
		return appt;
	}

	public Appointment updateAppointmentStatus(LoggedInInfo loggedInInfo, int apptNo, String status)
	{
		if(!securityInfoManager.hasPrivilege(loggedInInfo, "_appointment", "w", null))
		{
			throw new RuntimeException("Access Denied");
		}

		Appointment appt = appointmentDao.find(apptNo);
		if(appt != null)
		{
			appointmentArchiveDao.archiveAppointment(appt);

			String rawStatus = appt.getStatus();
			String statusModifier = "";
			if(rawStatus != null && rawStatus.length() > 1)
			{
				statusModifier = rawStatus.substring(1,2);
			}
			status = status + statusModifier;

			appt.setStatus(status);

			appointmentDao.merge(appt);
			if(appt.getIsVirtual())
			{
				myHealthAccessService.queueAppointmentCacheUpdate(appt);
			}
		}
		return appt;
	}

	public List<AppointmentEditRecord> getAppointmentEdits(Integer appointmentNo)
	{
		List<AppointmentArchive> archiveList = appointmentArchiveDao.findByAppointmentId(appointmentNo, 100, 0);
		List<AppointmentEditRecord> editList = new ArrayList<>(archiveList.size() + 1);

		for(AppointmentArchive archive : archiveList)
		{
			AppointmentEditRecord editRecord = new AppointmentEditRecord();

			editRecord.setId(archive.getId());
			editRecord.setAppointmentNo(archive.getAppointmentNo());
			editRecord.setDemographicNo(archive.getDemographicNo());
			editRecord.setCreator(archive.getCreator());
			editRecord.setProviderNo(archive.getProviderNo());
			editRecord.setLastUpdateUser(archive.getLastUpdateUser());
			editRecord.setCreateDateTime(ConversionUtils.toNullableLocalDateTime(archive.getCreateDateTime()));
			editRecord.setUpdateDateTime(ConversionUtils.toNullableLocalDateTime(archive.getUpdateDateTime()));
			editRecord.setAppointmentDate(
					LocalDateTime.of(ConversionUtils.toLocalDateTime(archive.getAppointmentDate()).toLocalDate(),
							ConversionUtils.toLocalDateTime(archive.getStartTime()).toLocalTime())
			);

			ProviderData lastUpdateProvider = archive.getLastUpdateUserRecord();
			if (lastUpdateProvider != null)
			{
				editRecord.setUpdateUserDisplayName(lastUpdateProvider.getDisplayName());
			}
			else
			{
				editRecord.setUpdateUserDisplayName("Provider, Unknown");
			}

			editList.add(editRecord);
		}

		//include the appointment record as the latest revision
		Appointment currentRecord = appointmentDao.find(appointmentNo);
		AppointmentEditRecord editRecord = new AppointmentEditRecord();

		editRecord.setId(0); // no archive id //TODO-legacy ???
		editRecord.setAppointmentNo(currentRecord.getId());
		editRecord.setDemographicNo(currentRecord.getDemographicNo());
		editRecord.setCreator(currentRecord.getCreator());
		editRecord.setProviderNo(currentRecord.getProviderNo());
		editRecord.setLastUpdateUser(currentRecord.getLastUpdateUser());
		editRecord.setCreateDateTime(ConversionUtils.toNullableLocalDateTime(currentRecord.getCreateDateTime()));
		editRecord.setUpdateDateTime(ConversionUtils.toNullableLocalDateTime(currentRecord.getUpdateDateTime()));
		editRecord.setAppointmentDate(
				LocalDateTime.of(ConversionUtils.toLocalDateTime(currentRecord.getAppointmentDate()).toLocalDate(),
						ConversionUtils.toLocalDateTime(currentRecord.getStartTime()).toLocalTime())
		);

		ProviderData lastUpdateProvider = currentRecord.getLastUpdateUserRecord();
		if (lastUpdateProvider != null)
		{
			editRecord.setUpdateUserDisplayName(lastUpdateProvider.getDisplayName());
		}

		editList.add(editRecord);


		return editList;
	}

	
//	public Appointment updateAppointmentType(LoggedInInfo loggedInInfo, int apptNo, String type) {
//
//		Appointment appt = appointmentDao.find(apptNo);
//		if (appt != null) {
//			appointmentArchiveDao.archiveAppointment(appt);
//
//			appt.setType(type);
//		}
//		appointmentDao.merge(appt);
//		return appt;
//	}
//
//	public Appointment updateAppointmentUrgency(LoggedInInfo loggedInInfo, int apptNo, String urgency) {
//
//		Appointment appt = appointmentDao.find(apptNo);
//		if (appt != null) {
//			appointmentArchiveDao.archiveAppointment(appt);
//
//			appt.setUrgency(urgency);
//		}
//		appointmentDao.merge(appt);
//		return appt;
//	}

	
	
	public List<AppointmentStatus> getAppointmentStatuses() {

		List<AppointmentStatus> apptStatus = appointmentStatusDao.findAll();

		return apptStatus;
	}

	public AppointmentStatus findByStatus(String status) {
		return appointmentStatusDao.findByStatus(status);
	}

	public List<LookupListItem> getReasons() {

		List<LookupListItem> itemsList = new ArrayList<LookupListItem>();

		LookupList list = lookupListDao.findByName("reasonCode");
		if(list != null) {
			itemsList = list.getItems();
		}

		return itemsList;
	}

//	public List<Appointment> findMonthlyAppointments(LoggedInInfo loggedInInfo, String providerNo, int year, int month) {
//
//		Calendar cal = Calendar.getInstance();
//		cal.set(Calendar.YEAR, year);
//		cal.set(Calendar.MONTH, month);
//		cal.set(Calendar.DAY_OF_MONTH, 1);
//		cal.set(Calendar.HOUR_OF_DAY, 0);
//		cal.set(Calendar.MINUTE, 0);
//
//		Date startDate = cal.getTime();
//
//		cal.set(Calendar.DAY_OF_MONTH,cal.getActualMaximum(Calendar.DAY_OF_MONTH)+1);
//		cal.add(Calendar.MINUTE,-1);
//
//		Date endDate = cal.getTime();
//
//		logger.info("monthly - checking from " + startDate + " to " + endDate);
//
//		List<Appointment> results = appointmentDao.findByDateRangeAndProvider(startDate, endDate, providerNo);
//		return results;
//	}
	
}
