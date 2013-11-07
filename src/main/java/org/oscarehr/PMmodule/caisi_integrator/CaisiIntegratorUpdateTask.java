/**
 *
 * Copyright (c) 2005-2012. Centre for Research on Inner City Health, St. Michael's Hospital, Toronto. All Rights Reserved.
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
 * Centre for Research on Inner City Health, St. Michael's Hospital,
 * Toronto, Ontario, Canada
 */

package org.oscarehr.PMmodule.caisi_integrator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.ws.WebServiceException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.oscarehr.PMmodule.dao.AdmissionDao;
import org.oscarehr.PMmodule.dao.ProgramDao;
import org.oscarehr.PMmodule.dao.ProviderDao;
import org.oscarehr.PMmodule.dao.SecUserRoleDao;
import org.oscarehr.PMmodule.model.Admission;
import org.oscarehr.PMmodule.model.Program;
import org.oscarehr.PMmodule.model.SecUserRole;
import org.oscarehr.caisi_integrator.ws.CachedAdmission;
import org.oscarehr.caisi_integrator.ws.CachedAppointment;
import org.oscarehr.caisi_integrator.ws.CachedBillingOnItem;
import org.oscarehr.caisi_integrator.ws.CachedDemographicAllergy;
import org.oscarehr.caisi_integrator.ws.CachedDemographicDocument;
import org.oscarehr.caisi_integrator.ws.CachedDemographicDrug;
import org.oscarehr.caisi_integrator.ws.CachedDemographicForm;
import org.oscarehr.caisi_integrator.ws.CachedDemographicIssue;
import org.oscarehr.caisi_integrator.ws.CachedDemographicLabResult;
import org.oscarehr.caisi_integrator.ws.CachedDemographicNote;
import org.oscarehr.caisi_integrator.ws.CachedDemographicNoteCompositePk;
import org.oscarehr.caisi_integrator.ws.CachedDemographicPrevention;
import org.oscarehr.caisi_integrator.ws.CachedDxresearch;
import org.oscarehr.caisi_integrator.ws.CachedEformData;
import org.oscarehr.caisi_integrator.ws.CachedEformValue;
import org.oscarehr.caisi_integrator.ws.CachedFacility;
import org.oscarehr.caisi_integrator.ws.CachedMeasurement;
import org.oscarehr.caisi_integrator.ws.CachedMeasurementExt;
import org.oscarehr.caisi_integrator.ws.CachedMeasurementMap;
import org.oscarehr.caisi_integrator.ws.CachedMeasurementType;
import org.oscarehr.caisi_integrator.ws.CachedProgram;
import org.oscarehr.caisi_integrator.ws.CachedProvider;
import org.oscarehr.caisi_integrator.ws.CodeType;
import org.oscarehr.caisi_integrator.ws.DemographicTransfer;
import org.oscarehr.caisi_integrator.ws.DemographicWs;
import org.oscarehr.caisi_integrator.ws.FacilityIdDemographicIssueCompositePk;
import org.oscarehr.caisi_integrator.ws.FacilityIdIntegerCompositePk;
import org.oscarehr.caisi_integrator.ws.FacilityIdLabResultCompositePk;
import org.oscarehr.caisi_integrator.ws.FacilityIdStringCompositePk;
import org.oscarehr.caisi_integrator.ws.FacilityWs;
import org.oscarehr.caisi_integrator.ws.Gender;
import org.oscarehr.caisi_integrator.ws.NoteIssue;
import org.oscarehr.caisi_integrator.ws.ProgramWs;
import org.oscarehr.caisi_integrator.ws.ProviderTransfer;
import org.oscarehr.caisi_integrator.ws.ProviderWs;
import org.oscarehr.caisi_integrator.ws.Role;
import org.oscarehr.caisi_integrator.ws.SetConsentTransfer;
import org.oscarehr.casemgmt.dao.CaseManagementIssueDAO;
import org.oscarehr.casemgmt.dao.CaseManagementNoteDAO;
import org.oscarehr.casemgmt.dao.ClientImageDAO;
import org.oscarehr.casemgmt.dao.IssueDAO;
import org.oscarehr.casemgmt.model.CaseManagementIssue;
import org.oscarehr.casemgmt.model.CaseManagementNote;
import org.oscarehr.casemgmt.model.ClientImage;
import org.oscarehr.casemgmt.model.Issue;
import org.oscarehr.common.dao.AllergyDao;
import org.oscarehr.common.dao.CaseManagementIssueNotesDao;
import org.oscarehr.common.dao.DemographicDao;
import org.oscarehr.common.dao.DemographicExtDao;
import org.oscarehr.common.dao.DrugDao;
import org.oscarehr.common.dao.DxresearchDAO;
import org.oscarehr.common.dao.EFormDataDao;
import org.oscarehr.common.dao.EFormValueDao;
import org.oscarehr.common.dao.FacilityDao;
import org.oscarehr.common.dao.GroupNoteDao;
import org.oscarehr.common.dao.IntegratorConsentDao;
import org.oscarehr.common.dao.IntegratorControlDao;
import org.oscarehr.common.dao.MeasurementDao;
import org.oscarehr.common.dao.MeasurementTypeDao;
import org.oscarehr.common.dao.OscarAppointmentDao;
import org.oscarehr.common.dao.PreventionDao;
import org.oscarehr.common.dao.PreventionExtDao;
import org.oscarehr.common.dao.UserPropertyDAO;
import org.oscarehr.common.model.Allergy;
import org.oscarehr.common.model.Appointment;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.common.model.DemographicExt;
import org.oscarehr.common.model.Drug;
import org.oscarehr.common.model.Dxresearch;
import org.oscarehr.common.model.EFormData;
import org.oscarehr.common.model.EFormValue;
import org.oscarehr.common.model.Facility;
import org.oscarehr.common.model.GroupNoteLink;
import org.oscarehr.common.model.IntegratorConsent;
import org.oscarehr.common.model.IntegratorConsent.ConsentStatus;
import org.oscarehr.common.model.Measurement;
import org.oscarehr.common.model.MeasurementType;
import org.oscarehr.common.model.Prevention;
import org.oscarehr.common.model.Provider;
import org.oscarehr.common.model.UserProperty;
import org.oscarehr.util.BenchmarkTimer;
import org.oscarehr.util.CxfClientUtils;
import org.oscarehr.util.DbConnectionFilter;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.ShutdownException;
import org.oscarehr.util.SpringUtils;
import org.oscarehr.util.XmlUtils;
import org.springframework.beans.BeanUtils;
import org.w3c.dom.Document;

import oscar.OscarProperties;
import oscar.dms.EDoc;
import oscar.dms.EDocUtil;
import oscar.form.FrmLabReq07Record;
import oscar.log.LogAction;
import oscar.oscarBilling.ca.on.dao.BillingOnItemDao;
import oscar.oscarBilling.ca.on.model.BillingOnCHeader1;
import oscar.oscarBilling.ca.on.model.BillingOnItem;
import oscar.oscarEncounter.oscarMeasurements.dao.MeasurementMapDao;
import oscar.oscarEncounter.oscarMeasurements.dao.MeasurementsExtDao;
import oscar.oscarEncounter.oscarMeasurements.model.Measurementmap;
import oscar.oscarEncounter.oscarMeasurements.model.MeasurementsExt;
import oscar.oscarLab.ca.all.web.LabDisplayHelper;
import oscar.oscarLab.ca.on.CommonLabResultData;
import oscar.oscarLab.ca.on.LabResultData;
import oscar.util.DateUtils;

public class CaisiIntegratorUpdateTask extends TimerTask {

	private static final Logger logger = MiscUtils.getLogger();

	private static final String INTEGRATOR_UPDATE_PERIOD_PROPERTIES_KEY = "INTEGRATOR_UPDATE_PERIOD";
	private static final String INTEGRATOR_THROTTLE_DELAY_PROPERTIES_KEY = "INTEGRATOR_THROTTLE_DELAY";
	private static final long INTEGRATOR_THROTTLE_DELAY = Long.parseLong((String) OscarProperties.getInstance().get(INTEGRATOR_THROTTLE_DELAY_PROPERTIES_KEY));

	private static Timer timer = new Timer("CaisiIntegratorUpdateTask Timer", true);

	private int numberOfTimesRun = 0;

	private FacilityDao facilityDao = (FacilityDao) SpringUtils.getBean("facilityDao");
	private DemographicDao demographicDao = (DemographicDao) SpringUtils.getBean("demographicDao");
	private CaseManagementIssueDAO caseManagementIssueDAO = (CaseManagementIssueDAO) SpringUtils.getBean("caseManagementIssueDAO");
	private IssueDAO issueDao = (IssueDAO) SpringUtils.getBean("IssueDAO");
	private CaseManagementNoteDAO caseManagementNoteDAO = (CaseManagementNoteDAO) SpringUtils.getBean("CaseManagementNoteDAO");
	private CaseManagementIssueNotesDao caseManagementIssueNotesDao = (CaseManagementIssueNotesDao) SpringUtils.getBean("caseManagementIssueNotesDao");
	private ClientImageDAO clientImageDAO = (ClientImageDAO) SpringUtils.getBean("clientImageDAO");
	private IntegratorConsentDao integratorConsentDao = (IntegratorConsentDao) SpringUtils.getBean("integratorConsentDao");
	private ProgramDao programDao = (ProgramDao) SpringUtils.getBean("programDao");
	private ProviderDao providerDao = (ProviderDao) SpringUtils.getBean("providerDao");
	private PreventionDao preventionDao = (PreventionDao) SpringUtils.getBean("preventionDao");
	private PreventionExtDao preventionExtDao = (PreventionExtDao) SpringUtils.getBean("preventionExtDao");
	private DrugDao drugDao = (DrugDao) SpringUtils.getBean("drugDao");
	private SecUserRoleDao secUserRoleDao = (SecUserRoleDao) SpringUtils.getBean("secUserRoleDao");
	private AdmissionDao admissionDao = (AdmissionDao) SpringUtils.getBean("admissionDao");
	private OscarAppointmentDao appointmentDao = (OscarAppointmentDao) SpringUtils.getBean("oscarAppointmentDao");
	private IntegratorControlDao integratorControlDao = (IntegratorControlDao) SpringUtils.getBean("integratorControlDao");
	private MeasurementsExtDao measurementsExtDao = (MeasurementsExtDao) SpringUtils.getBean("measurementsExtDao");
	private MeasurementMapDao measurementMapDao = (MeasurementMapDao) SpringUtils.getBean("measurementMapDao");
	private DxresearchDAO dxresearchDao = (DxresearchDAO) SpringUtils.getBean("dxresearchDAO");
	private BillingOnItemDao billingOnItemDao = (BillingOnItemDao) SpringUtils.getBean("billingOnItemDao");
	private EFormValueDao eFormValueDao = (EFormValueDao) SpringUtils.getBean("EFormValueDao");
	private EFormDataDao eFormDataDao = (EFormDataDao) SpringUtils.getBean("EFormDataDao");
	private GroupNoteDao groupNoteDao = (GroupNoteDao) SpringUtils.getBean("groupNoteDao");
	private DemographicExtDao demographicExtDao = SpringUtils.getBean(DemographicExtDao.class);

	private UserPropertyDAO userPropertyDao = (UserPropertyDAO) SpringUtils.getBean("UserPropertyDAO");

	private static TimerTask timerTask = null;

	public static synchronized void startTask() {
		if (timerTask == null) {
			long period = 0;
			String periodStr = null;
			try {
				periodStr = (String) OscarProperties.getInstance().get(INTEGRATOR_UPDATE_PERIOD_PROPERTIES_KEY);
				period = Long.parseLong(periodStr);
			} catch (Exception e) {
				logger.error("CaisiIntegratorUpdateTask not scheduled, period is missing or invalid properties file : " + INTEGRATOR_UPDATE_PERIOD_PROPERTIES_KEY + '=' + periodStr, e);
				return;
			}

			logger.info("Scheduling CaisiIntegratorUpdateTask for period : " + period);
			timerTask = new CaisiIntegratorUpdateTask();
			timer.schedule(timerTask, 10000, period);
		} else {
			logger.error("Start was called twice on this timer task object.", new Exception());
		}
	}

	public static synchronized void stopTask() {
		if (timerTask != null) {
			timerTask.cancel();
			timerTask = null;

			logger.info("CaisiIntegratorUpdateTask has been unscheduled.");
		}
	}

	@Override
	public void run() {
		numberOfTimesRun++;
		

		LoggedInInfo.setLoggedInInfoToCurrentClassAndMethod();
		
		LoggedInInfo loggedInInfo=LoggedInInfo.loggedInInfo.get();
		logger.debug("CaisiIntegratorUpdateTask starting #" + numberOfTimesRun+"  running as "+loggedInInfo.loggedInProvider);

		try {
			pushAllFacilities();
		} catch (ShutdownException e) {
			logger.debug("CaisiIntegratorUpdateTask received shutdown notice.");
		} catch (Exception e) {
			logger.error("unexpected error occurred", e);
		} finally {
			LoggedInInfo.loggedInInfo.remove();
			DbConnectionFilter.releaseAllThreadDbResources();

			logger.debug("CaisiIntegratorUpdateTask finished #" + numberOfTimesRun);
		}
	}

	public void pushAllFacilities() throws ShutdownException {
		List<Facility> facilities = facilityDao.findAll(true);

		for (Facility facility : facilities) {
			try {
				if (facility.isIntegratorEnabled()) {
					pushAllDataForOneFacility(facility);
					findChangedRecordsFromIntegrator(facility);
				}
			} catch (WebServiceException e) {
				if (CxfClientUtils.isConnectionException(e)) {
					logger.warn("Error connecting to integrator. " + e.getMessage());
					logger.debug("Error connecting to integrator.", e);
				} else {
					logger.error("Unexpected error.", e);
				}
			} catch (ShutdownException e) {
				throw (e);
			} catch (Exception e) {
				logger.error("Unexpected error.", e);
			}
		}
	}

	private void pushAllDataForOneFacility(Facility facility) throws IOException, ShutdownException {
		logger.info("Start pushing data for facility : " + facility.getId() + " : " + facility.getName());

		// set working facility
		LoggedInInfo.loggedInInfo.get().currentFacility = facility;

		// check all parameters are present
		String integratorBaseUrl = facility.getIntegratorUrl();
		String user = facility.getIntegratorUser();
		String password = facility.getIntegratorPassword();

		if (integratorBaseUrl == null || user == null || password == null) {
			logger.warn("Integrator is enabled but information is incomplete. facilityId=" + facility.getId() + ", user=" + user + ", password=" + password + ", url=" + integratorBaseUrl);
			return;
		}

		FacilityWs service = CaisiIntegratorManager.getFacilityWs();
		CachedFacility cachedFacility = service.getMyFacility();

		// start at the beginning of time so by default everything is pushed
		Date lastDataUpdated = new Date(0);
		if (cachedFacility != null && cachedFacility.getLastDataUpdate() != null){
			lastDataUpdated = MiscUtils.toDate(cachedFacility.getLastDataUpdate());
		}else{
			userPropertyDao.saveProp(UserProperty.INTEGRATOR_FULL_PUSH+facility.getId(), "1");
		}

		// this needs to be set now, before we do any sends, this will cause anything updated after now to be resent twice but it's better than items being missed that were updated after this started.
		Date currentUpdateDate = new Date();

		// do all the sync work
		// in theory sync should only send changed data, but currently due to
		// the lack of proper data models, we don't have a reliable timestamp on when things change so we just push everything, highly inefficient but it works until we fix the
		// data model. The last update date is available though as per above...
		pushFacility(lastDataUpdated);
		pushProviders(lastDataUpdated, facility);
		pushPrograms(lastDataUpdated, facility);
		pushAllDemographics(lastDataUpdated);

		// all things updated successfully
		service.updateMyFacilityLastUpdateDate(MiscUtils.toCalendar(currentUpdateDate));

		logger.info("Finished pushing data for facility : " + facility.getId() + " : " + facility.getName());
	}

	private void pushFacility(Date lastDataUpdated) throws MalformedURLException {
		Facility facility = LoggedInInfo.loggedInInfo.get().currentFacility;

		if (facility.getLastUpdated().after(lastDataUpdated)) {
			logger.debug("pushing facility record");

			CachedFacility cachedFacility = new CachedFacility();
			BeanUtils.copyProperties(facility, cachedFacility);

			FacilityWs service = CaisiIntegratorManager.getFacilityWs();
			service.setMyFacility(cachedFacility);
		} else {
			logger.debug("skipping facility record, not updated since last push");
		}
	}

	private void pushPrograms(Date lastDataUpdated, Facility facility) throws MalformedURLException, ShutdownException {
		// all are always sent so program deletions have be proliferated.
		List<Program> programs = programDao.getProgramsByFacilityId(facility.getId());

		ArrayList<CachedProgram> cachedPrograms = new ArrayList<CachedProgram>();

		for (Program program : programs) {
			logger.debug("pushing program : " + program.getId() + ':' + program.getName());
			MiscUtils.checkShutdownSignaled();

			CachedProgram cachedProgram = new CachedProgram();

			BeanUtils.copyProperties(program, cachedProgram);

			FacilityIdIntegerCompositePk pk = new FacilityIdIntegerCompositePk();
			pk.setCaisiItemId(program.getId());
			cachedProgram.setFacilityIdIntegerCompositePk(pk);

			try {
				cachedProgram.setGender(Gender.valueOf(program.getManOrWoman().toUpperCase()));
			} catch (Exception e) {
				// do nothing, we can't assume anything is right or wrong with genders
				// until the whole mess is sorted out, for now it's a what you get is
				// what you get
			}

			if (program.isTransgender()) cachedProgram.setGender(Gender.T);

			cachedProgram.setMaxAge(program.getAgeMax());
			cachedProgram.setMinAge(program.getAgeMin());
			cachedProgram.setStatus(program.getProgramStatus());

			cachedPrograms.add(cachedProgram);
		}

		ProgramWs service = CaisiIntegratorManager.getProgramWs(facility);
		service.setCachedPrograms(cachedPrograms);
	}

	private void pushProviders(Date lastDataUpdated, Facility facility) throws MalformedURLException, ShutdownException {
		List<String> providerIds = ProviderDao.getProviderIds(facility.getId());
		ProviderWs service = CaisiIntegratorManager.getProviderWs(facility);

		for (String providerId : providerIds) {
			logger.debug("Adding provider " + providerId + " for " + facility.getName());

			// copy provider basic data over
			Provider provider = providerDao.getProvider(providerId);
			if (provider == null) continue;

			ProviderTransfer providerTransfer = new ProviderTransfer();
			CachedProvider cachedProvider = new CachedProvider();

			BeanUtils.copyProperties(provider, cachedProvider);

			FacilityIdStringCompositePk pk = new FacilityIdStringCompositePk();
			pk.setCaisiItemId(provider.getProviderNo());
			cachedProvider.setFacilityIdStringCompositePk(pk);

			providerTransfer.setCachedProvider(cachedProvider);

			// copy roles over
			List<SecUserRole> roles = secUserRoleDao.getUserRoles(providerId);
			for (SecUserRole role : roles) {
				Role integratorRole = IntegratorRoleUtils.getIntegratorRole(role.getRoleName());
				if (integratorRole != null) providerTransfer.getRoles().add(integratorRole);
			}

			ArrayList<ProviderTransfer> providerTransfers = new ArrayList<ProviderTransfer>();
			providerTransfers.add(providerTransfer);
			service.setCachedProviders(providerTransfers);
			throttleAndChecks();
		}
	}

	/**
	 * A check to compare the time being requested from the integrator and that last time the integrator requested. If the date being requested is before the last date requested return true. If a last date hasn't been stored return true.
	 *
	 * @param lastDataUpdated time requested from the integrator
	 * @param lastPushUpdated prior time requested from the integrator
	 * @return
	 */
	private boolean isIntegratorRequestDateOlderThanLastKnownDate(Date lastDataUpdated, UserProperty lastPushUpdated) {
		if (lastPushUpdated == null) return (true);

		boolean ret = false;
		try {
			Date lastDateRequested = new Date();
			// If lastPushUpdated is null that means this is the first time this has run, return true
			lastDateRequested.setTime(Long.parseLong(lastPushUpdated.getValue()));
			if (lastDataUpdated.before(lastDateRequested)) {
				ret = true;
			}
			logger.debug("lastDataUpdated " + lastDataUpdated + " lastDateRequested " + lastDateRequested + " lastDataUpdated is before lastDateRequested: " + ret);
		} catch (Exception e) {
			logger.error("Unexpected error", e);
			return true;
		}
		return ret;
	}

	private List<Integer> getDemographicIdsToPush(Date lastDataUpdated) {
		Facility facility = LoggedInInfo.loggedInInfo.get().currentFacility;

		Properties p = OscarProperties.getInstance();
		boolean omdTestingOnly = Boolean.parseBoolean(p.getProperty("ENABLE_CONFORMANCE_ONLY_FEATURES"));
		logger.info("Integrator push demographics, omdTestingOnly="+omdTestingOnly);

		UserProperty fullPushProp = userPropertyDao.getProp(UserProperty.INTEGRATOR_FULL_PUSH+facility.getId());
		
		if (OscarProperties.getInstance().isPropertyActive("INTEGRATOR_FORCE_FULL")) {
			fullPushProp.setValue("1");
		}
	
		List<Integer> fullFacilitydemographicIds  = DemographicDao.getDemographicIdsAdmittedIntoFacility(facility.getId());
		
		if (!omdTestingOnly || (fullPushProp != null && fullPushProp.getValue().equals("1") ) ) {
			return fullFacilitydemographicIds;
		} else {
			
			List<Integer> demographicIds =  DemographicDao.getDemographicIdsAlteredSinceTime(lastDataUpdated);
			
			
			Iterator<Integer> demoIterator = demographicIds.iterator();
	        while(demoIterator.hasNext()){ //Verify that the demographic is in the Facility
	                Integer i = demoIterator.next();
	                if( !fullFacilitydemographicIds.contains(i)){
	                	demoIterator.remove();
	                }
	        }
			
			if(fullPushProp != null &&  fullPushProp.getValue().equals("1")){
			   userPropertyDao.saveProp(UserProperty.INTEGRATOR_FULL_PUSH+facility.getId(), "0");
			}
			
			return(demographicIds);
		}
	}

	private void pushAllDemographics(Date lastDataUpdated) throws MalformedURLException, ShutdownException {
		Facility facility = LoggedInInfo.loggedInInfo.get().currentFacility;

		List<Integer> demographicIds = getDemographicIdsToPush(lastDataUpdated);

		DemographicWs demographicService = CaisiIntegratorManager.getDemographicWs();
		List<Program> programsInFacility = programDao.getProgramsByFacilityId(facility.getId());
		List<String> providerIdsInFacility = ProviderDao.getProviderIds(facility.getId());

		long startTime = System.currentTimeMillis();
		int demographicPushCount = 0;
		for (Integer demographicId : demographicIds) {
			demographicPushCount++;
			logger.debug("pushing demographic facilityId:" + facility.getId() + ", demographicId:" + demographicId + "  " + demographicPushCount + " of " + demographicIds.size());
			BenchmarkTimer benchTimer = new BenchmarkTimer("pushing demo facilityId:" + facility.getId() + ", demographicId:" + demographicId + "  " + demographicPushCount + " of " + demographicIds.size());

			try {
				demographicService.setLastPushDate(demographicId);
				
				pushDemographic(facility, demographicService, demographicId, facility.getId());
				// it's safe to set the consent later so long as we default it to none when we send the original demographic data in the line above.
				benchTimer.tag("pushDemographic");
				pushDemographicConsent(facility, demographicService, demographicId);
				benchTimer.tag("pushDemographicConsent");
				pushDemographicIssues(lastDataUpdated, facility, programsInFacility, demographicService, demographicId);
				benchTimer.tag("pushDemographicIssues");
				pushDemographicPreventions(lastDataUpdated, facility, providerIdsInFacility, demographicService, demographicId);
				benchTimer.tag("pushDemographicPreventions");
				pushDemographicNotes(lastDataUpdated, facility, demographicService, demographicId);
				benchTimer.tag("pushDemographicNotes");
				pushDemographicDrugs(lastDataUpdated, facility, providerIdsInFacility, demographicService, demographicId);
				benchTimer.tag("pushDemographicDrugs");
				pushAdmissions(lastDataUpdated, facility, programsInFacility, demographicService, demographicId);
				benchTimer.tag("pushAdmissions");
				pushAppointments(lastDataUpdated, facility, demographicService, demographicId);
				benchTimer.tag("pushAppointments");
				pushMeasurements(lastDataUpdated, facility, demographicService, demographicId);
				benchTimer.tag("pushMeasurements");
				pushDxresearchs(lastDataUpdated, facility, demographicService, demographicId);
				benchTimer.tag("pushDxresearchs");
				pushBillingItems(lastDataUpdated, facility, demographicService, demographicId);
				benchTimer.tag("pushBillingItems");
				pushEforms(lastDataUpdated, facility, demographicService, demographicId);
				benchTimer.tag("pushEforms");
				pushAllergies(lastDataUpdated, facility, demographicService, demographicId);
				benchTimer.tag("pushAllergies");
				pushDocuments(lastDataUpdated, facility, demographicService, demographicId);
				benchTimer.tag("pushDocuments");
				pushForms(lastDataUpdated, facility, demographicService, demographicId);
				benchTimer.tag("pushForms");
				pushLabResults(lastDataUpdated, facility, demographicService, demographicId);
				benchTimer.tag("pushLabResults");

				logger.debug(benchTimer.report());

				DbConnectionFilter.releaseAllThreadDbResources();
			} catch (IllegalArgumentException iae) {
				// continue processing demographics if date values in current demographic are bad
				// all other errors thrown by the above methods should indicate a failure in the service
				// connection at large -- continuing to process not possible
				// need some way of notification here.
				logger.error("Error updating demographic, continuing with Demographic batch", iae);
			} catch (ShutdownException e) {
				throw (e);
			} catch (Exception e) {
				logger.error("Unexpected error.", e);
			}
		}
		logger.debug("Total pushAllDemographics :" + (System.currentTimeMillis() - startTime));
	}

	private void pushDemographic(Facility facility, DemographicWs service, Integer demographicId, Integer facilityId) throws ShutdownException {
		DemographicTransfer demographicTransfer = new DemographicTransfer();

		// set demographic info
		Demographic demographic = demographicDao.getDemographicById(demographicId);

		String ignoreProperties[] = { "lastUpdateDate" };
		BeanUtils.copyProperties(demographic, demographicTransfer, ignoreProperties);

		demographicTransfer.setCaisiDemographicId(demographic.getDemographicNo());
		demographicTransfer.setBirthDate(demographic.getBirthDay());

		demographicTransfer.setHinType(demographic.getHcType());
		demographicTransfer.setHinVersion(demographic.getVer());
		demographicTransfer.setHinValidEnd(DateUtils.toGregorianCalendar(demographic.getHcRenewDate()));
		demographicTransfer.setHinValidStart(DateUtils.toGregorianCalendar(demographic.getEffDate()));
		demographicTransfer.setCaisiProviderId(demographic.getProviderNo());

		demographicTransfer.setStreetAddress(demographic.getAddress());
		demographicTransfer.setPhone1(demographic.getPhone());
		demographicTransfer.setPhone2(demographic.getPhone2());

		demographicTransfer.setLastUpdateDate(DateUtils.toGregorianCalendar(demographic.getLastUpdateDate()));

		try {
			demographicTransfer.setGender(Gender.valueOf(demographic.getSex().toUpperCase()));
		} catch (Exception e) {
			// do nothing, for now gender is on a "good luck" what ever you
			// get is what you get basis until the whole gender mess is sorted.
		}

		// set image
		ClientImage clientImage = clientImageDAO.getClientImage(demographicId);
		if (clientImage != null) {
			demographicTransfer.setPhoto(clientImage.getImage_data());
			demographicTransfer.setPhotoUpdateDate(MiscUtils.toCalendar(clientImage.getUpdate_date()));
		}

		// set flag to remove demographic identity
		boolean rid = integratorControlDao.readRemoveDemographicIdentity(facilityId);
		demographicTransfer.setRemoveId(rid);

		// send the request
		service.setDemographic(demographicTransfer);
		throttleAndChecks();

		conformanceTestLog(facility, "Demographic", String.valueOf(demographicId));
	}

	private void pushDemographicConsent(Facility facility, DemographicWs demographicService, Integer demographicId) {

		// find the latest relvent consent that needs to be pushed.
		List<IntegratorConsent> tempConsents = integratorConsentDao.findByFacilityAndDemographic(facility.getId(), demographicId);

		for (IntegratorConsent tempConsent : tempConsents) {
			if (tempConsent.getClientConsentStatus() == ConsentStatus.GIVEN || tempConsent.getClientConsentStatus() == ConsentStatus.REVOKED) {
				SetConsentTransfer consentTransfer = CaisiIntegratorManager.makeSetConsentTransfer(tempConsent);
				demographicService.setCachedDemographicConsent(consentTransfer);
				logger.debug("pushDemographicConsent:" + tempConsent.getId() + "," + tempConsent.getFacilityId() + "," + tempConsent.getDemographicId());
				return;
			}
		}
	}

	private void pushDemographicIssues(Date lastDataUpdated, Facility facility, List<Program> programsInFacility, DemographicWs service, Integer demographicId) throws ShutdownException {
		logger.debug("pushing demographicIssues facilityId:" + facility.getId() + ", demographicId:" + demographicId);

		List<CaseManagementIssue> caseManagementIssues = caseManagementIssueDAO.getIssuesByDemographic(demographicId.toString());
		StringBuilder sentIds = new StringBuilder();
		if (caseManagementIssues.size() == 0) return;

		for (CaseManagementIssue caseManagementIssue : caseManagementIssues) {
			// don't send issue if it is not in our facility.
			logger.debug("Facility:" + facility.getName() + " - caseManagementIssue = " + caseManagementIssue.toString());
			if (caseManagementIssue.getProgram_id() == null || !isProgramIdInProgramList(programsInFacility, caseManagementIssue.getProgram_id())) continue;

			long issueId = caseManagementIssue.getIssue_id();
			Issue issue = issueDao.getIssue(issueId);
			CachedDemographicIssue cachedDemographicIssue = new CachedDemographicIssue();

			FacilityIdDemographicIssueCompositePk facilityDemographicIssuePrimaryKey = new FacilityIdDemographicIssueCompositePk();
			facilityDemographicIssuePrimaryKey.setCaisiDemographicId(Integer.parseInt(caseManagementIssue.getDemographic_no()));
			facilityDemographicIssuePrimaryKey.setCodeType(CodeType.ICD_10); // temporary hard code hack till we sort this out
			facilityDemographicIssuePrimaryKey.setIssueCode(issue.getCode());
			cachedDemographicIssue.setFacilityDemographicIssuePk(facilityDemographicIssuePrimaryKey);

			BeanUtils.copyProperties(caseManagementIssue, cachedDemographicIssue);
			cachedDemographicIssue.setIssueDescription(issue.getDescription());
			cachedDemographicIssue.setIssueRole(IntegratorRoleUtils.getIntegratorRole(issue.getRole()));

			ArrayList<CachedDemographicIssue> issues = new ArrayList<CachedDemographicIssue>();
			issues.add(cachedDemographicIssue);
			service.setCachedDemographicIssues(issues);
			throttleAndChecks();

			sentIds.append("," + caseManagementIssue.getId());
		}

		conformanceTestLog(facility, "CaseManagementIssue", sentIds.toString());
	}

	private void pushAdmissions(Date lastDataUpdated, Facility facility, List<Program> programsInFacility, DemographicWs demographicService, Integer demographicId) throws ShutdownException {
		logger.debug("pushing admissions facilityId:" + facility.getId() + ", demographicId:" + demographicId);

		List<Admission> admissions = admissionDao.getAdmissionsByFacility(demographicId, facility.getId());
		StringBuilder sentIds = new StringBuilder();
		if (admissions.size() == 0) return;

		for (Admission admission : admissions) {

			// don't send admission if it is not in our facility. yeah I know I'm double checking since it's selected
			// but the reality is I don't trust it and our facility segmentation is flakey at best so.. better to check again.
			logger.debug("Facility:" + facility.getName() + " - admissionId = " + admission.getId());
			if (!isProgramIdInProgramList(programsInFacility, admission.getProgramId())) continue;

			CachedAdmission cachedAdmission = new CachedAdmission();

			FacilityIdIntegerCompositePk facilityIdIntegerCompositePk = new FacilityIdIntegerCompositePk();
			facilityIdIntegerCompositePk.setCaisiItemId(admission.getId().intValue());
			cachedAdmission.setFacilityIdIntegerCompositePk(facilityIdIntegerCompositePk);

			cachedAdmission.setAdmissionDate(MiscUtils.toCalendar(admission.getAdmissionDate()));
			cachedAdmission.setAdmissionNotes(admission.getAdmissionNotes());
			cachedAdmission.setCaisiDemographicId(demographicId);
			cachedAdmission.setCaisiProgramId(admission.getProgramId());
			cachedAdmission.setDischargeDate(MiscUtils.toCalendar(admission.getDischargeDate()));
			cachedAdmission.setDischargeNotes(admission.getDischargeNotes());

			ArrayList<CachedAdmission> cachedAdmissions = new ArrayList<CachedAdmission>();
			cachedAdmissions.add(cachedAdmission);
			demographicService.setCachedAdmissions(cachedAdmissions);
			throttleAndChecks();

			sentIds.append("," + admission.getId());
		}

		conformanceTestLog(facility, "Admission", sentIds.toString());
	}

	private boolean isProgramIdInProgramList(List<Program> programList, int programId) {
		for (Program p : programList) {
			if (p.getId().intValue() == programId) return (true);
		}

		return (false);
	}

	private void pushDemographicPreventions(Date lastDataUpdated, Facility facility, List<String> providerIdsInFacility, DemographicWs service, Integer demographicId) throws ShutdownException, ParserConfigurationException, ClassCastException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		logger.debug("pushing demographicPreventions facilityId:" + facility.getId() + ", demographicId:" + demographicId);

		ArrayList<CachedDemographicPrevention> preventionsToSend = new ArrayList<CachedDemographicPrevention>();
		StringBuilder sentIds = new StringBuilder();

		// get all preventions
		// for each prevention, copy fields to an integrator prevention
		// need to copy ext info
		// add prevention to array list to send
		List<Prevention> localPreventions = preventionDao.findNotDeletedByDemographicId(demographicId);
		if (localPreventions.size() == 0) return;

		for (Prevention localPrevention : localPreventions) {

			if (!providerIdsInFacility.contains(localPrevention.getCreatorProviderNo())) continue;

			CachedDemographicPrevention cachedDemographicPrevention = new CachedDemographicPrevention();
			cachedDemographicPrevention.setCaisiDemographicId(demographicId);
			cachedDemographicPrevention.setCaisiProviderId(localPrevention.getProviderNo());

			{
				FacilityIdIntegerCompositePk pk = new FacilityIdIntegerCompositePk();
				pk.setCaisiItemId(localPrevention.getId());
				cachedDemographicPrevention.setFacilityPreventionPk(pk);
			}

			cachedDemographicPrevention.setNextDate(MiscUtils.toCalendar(localPrevention.getNextDate()));
			cachedDemographicPrevention.setPreventionDate(MiscUtils.toCalendar(localPrevention.getPreventionDate()));
			cachedDemographicPrevention.setPreventionType(localPrevention.getPreventionType());
			cachedDemographicPrevention.setRefused(localPrevention.isRefused());
			cachedDemographicPrevention.setNever(localPrevention.isNever());

			// add ext info
			// ext info should be added to the attributes field as xml data
			Document doc = XmlUtils.newDocument("PreventionExt");
			HashMap<String, String> exts = preventionExtDao.getPreventionExt(localPrevention.getId());
			for (Map.Entry<String, String> entry : exts.entrySet()) {
				XmlUtils.appendChildToRoot(doc, entry.getKey(), entry.getValue());
			}
			cachedDemographicPrevention.setAttributes(XmlUtils.toString(doc, false));

			preventionsToSend.add(cachedDemographicPrevention);

			sentIds.append("," + localPrevention.getId());
		}

		if (preventionsToSend.size() > 0) service.setCachedDemographicPreventions(preventionsToSend);

		throttleAndChecks();

		conformanceTestLog(facility, "Admission", sentIds.toString());
	}

	private void pushDocuments(Date lastDataUpdated, Facility facility, DemographicWs demographicWs, Integer demographicId) throws ShutdownException {
		logger.debug("pushing demographicDocuments facilityId:" + facility.getId() + ", demographicId:" + demographicId);

		StringBuilder sentIds = new StringBuilder();

		logger.debug("module=demographic, moduleid=" + demographicId.toString() + ", view=all, EDocUtil.PRIVATE=" + EDocUtil.PRIVATE + ", sort=" + EDocUtil.SORT_OBSERVATIONDATE + ", viewstatus=active");
		List<EDoc> privateDocs = EDocUtil.listDocs("demographic", demographicId.toString(), "all", EDocUtil.PRIVATE, EDocUtil.SORT_OBSERVATIONDATE, "active");
		for (EDoc eDoc : privateDocs) {
			sendSingleDocument(lastDataUpdated, demographicWs, eDoc, demographicId);
			throttleAndChecks();
			sentIds.append("," + eDoc.getDocId());
		}

		conformanceTestLog(facility, "EDoc", sentIds.toString());
	}

	private void sendSingleDocument(Date lastDataUpdated, DemographicWs demographicWs, EDoc eDoc, Integer demographicId) {
		// no change since last sync
		if (eDoc.getDateTimeStampAsDate() != null && eDoc.getDateTimeStampAsDate().before(lastDataUpdated)) return;

		// send this document
		CachedDemographicDocument cachedDemographicDocument = new CachedDemographicDocument();
		FacilityIdIntegerCompositePk facilityIdIntegerCompositePk = new FacilityIdIntegerCompositePk();
		facilityIdIntegerCompositePk.setCaisiItemId(Integer.parseInt(eDoc.getDocId()));
		cachedDemographicDocument.setFacilityIntegerPk(facilityIdIntegerCompositePk);

		cachedDemographicDocument.setAppointmentNo(eDoc.getAppointmentNo());
		cachedDemographicDocument.setCaisiDemographicId(demographicId);
		cachedDemographicDocument.setContentType(eDoc.getContentType());
		cachedDemographicDocument.setDocCreator(eDoc.getCreatorId());
		cachedDemographicDocument.setDocFilename(eDoc.getFileName());
		cachedDemographicDocument.setDocType(eDoc.getType());
		cachedDemographicDocument.setDocXml(eDoc.getHtml());
		cachedDemographicDocument.setNumberOfPages(eDoc.getNumberOfPages());
		cachedDemographicDocument.setObservationDate(DateUtils.toGregorianCalendarDate(eDoc.getObservationDate()));
		cachedDemographicDocument.setProgramId(eDoc.getProgramId());
		cachedDemographicDocument.setPublic1(Integer.parseInt(eDoc.getDocPublic()));
		cachedDemographicDocument.setResponsible(eDoc.getResponsibleId());
		cachedDemographicDocument.setReviewDateTime(DateUtils.toGregorianCalendar(eDoc.getReviewDateTimeDate()));
		cachedDemographicDocument.setReviewer(eDoc.getReviewerId());
		cachedDemographicDocument.setSource(eDoc.getSource());
		cachedDemographicDocument.setStatus("" + eDoc.getStatus());
		cachedDemographicDocument.setUpdateDateTime(DateUtils.toGregorianCalendar(eDoc.getDateTimeStampAsDate()));
		cachedDemographicDocument.setDescription(eDoc.getDescription());

		byte[] contents = EDocUtil.getFile(OscarProperties.getInstance().getProperty("DOCUMENT_DIR") + '/' + eDoc.getFileName());

		demographicWs.addCachedDemographicDocumentAndContents(cachedDemographicDocument, contents);
	}

	private void pushLabResults(Date lastDataUpdated, Facility facility, DemographicWs demographicWs, Integer demographicId) throws ShutdownException, ParserConfigurationException, UnsupportedEncodingException, ClassCastException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		logger.debug("pushing pushLabResults facilityId:" + facility.getId() + ", demographicId:" + demographicId);

		CommonLabResultData comLab = new CommonLabResultData();
		ArrayList<LabResultData> labs = comLab.populateLabResultsData("", demographicId.toString(), "", "", "", "U", null);
		StringBuilder sentIds = new StringBuilder();
		if (labs.size() == 0) return;

		for (LabResultData lab : labs) {
			CachedDemographicLabResult cachedDemographicLabResult = makeCachedDemographicLabResult(demographicId, lab);
			demographicWs.addCachedDemographicLabResult(cachedDemographicLabResult);

			throttleAndChecks();
			sentIds.append("," + lab.getLabPatientId() + ":" + lab.labType + ":" + lab.segmentID);
		}

		conformanceTestLog(facility, "LabResultData", sentIds.toString());
	}

	private CachedDemographicLabResult makeCachedDemographicLabResult(Integer demographicId, LabResultData lab) throws ParserConfigurationException, UnsupportedEncodingException, ClassCastException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		CachedDemographicLabResult cachedDemographicLabResult = new CachedDemographicLabResult();

		FacilityIdLabResultCompositePk pk = new FacilityIdLabResultCompositePk();
		// our attempt at making a fake pk....
		String key = LabDisplayHelper.makeLabKey(demographicId, lab.getSegmentID(), lab.labType, lab.getDateTime());
		pk.setLabResultId(key);
		cachedDemographicLabResult.setFacilityIdLabResultCompositePk(pk);

		cachedDemographicLabResult.setCaisiDemographicId(demographicId);
		cachedDemographicLabResult.setType(lab.labType);

		Document doc = LabDisplayHelper.labToXml(demographicId, lab);

		String data = XmlUtils.toString(doc, false);
		cachedDemographicLabResult.setData(data);

		return (cachedDemographicLabResult);
	}

	private void pushForms(Date lastDataUpdated, Facility facility, DemographicWs demographicWs, Integer demographicId) throws ShutdownException, SQLException, IOException, ParseException {
		logger.debug("pushing demographic forms facilityId:" + facility.getId() + ", demographicId:" + demographicId);

		pushLabReq2007(lastDataUpdated, facility, demographicWs, demographicId);
	}

	private void pushLabReq2007(Date lastDataUpdated, Facility facility, DemographicWs demographicWs, Integer demographicId) throws SQLException, ShutdownException, IOException, ParseException {
		List<Properties> records = FrmLabReq07Record.getPrintRecords(demographicId);
		if (records.size() == 0) return;

		StringBuilder sentIds = new StringBuilder();

		for (Properties p : records) {
			logger.debug("pushing form labReq2007 : " + p.get("ID") + " : " + p.get("formEdited"));

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = sdf.parse(p.getProperty("formEdited"));

			// no change since last sync
			if (date != null && date.before(lastDataUpdated)) continue;

			CachedDemographicForm cachedDemographicForm = new CachedDemographicForm();
			FacilityIdIntegerCompositePk facilityIdIntegerCompositePk = new FacilityIdIntegerCompositePk();
			facilityIdIntegerCompositePk.setCaisiItemId(Integer.parseInt(p.getProperty("ID")));
			cachedDemographicForm.setFacilityIdIntegerCompositePk(facilityIdIntegerCompositePk);

			cachedDemographicForm.setCaisiDemographicId(demographicId);
			cachedDemographicForm.setCaisiProviderId(p.getProperty("provider_no"));
			cachedDemographicForm.setEditDate(DateUtils.toGregorianCalendar(date));
			cachedDemographicForm.setFormName("formLabReq07");

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			p.store(baos, null);
			cachedDemographicForm.setFormData(baos.toString());

			demographicWs.addCachedDemographicForm(cachedDemographicForm);

			throttleAndChecks();
			sentIds.append("," + p.getProperty("ID"));
		}

		conformanceTestLog(facility, "formLabReq07", sentIds.toString());
	}

	private void pushDemographicNotes(Date lastDataUpdated, Facility facility, DemographicWs service, Integer demographicId) throws ShutdownException {
		logger.debug("pushing demographicNotes facilityId:" + facility.getId() + ", demographicId:" + demographicId);

		List<Program> programs = programDao.getProgramsByFacilityId(facility.getId());
		HashSet<Integer> programIds = new HashSet<Integer>();
		for (Program program : programs)
			programIds.add(program.getId());

		List<CaseManagementNote> localNotes = caseManagementNoteDAO.getNotesByDemographic(demographicId.toString());

		String issueType = OscarProperties.getInstance().getProperty("COMMUNITY_ISSUE_CODETYPE");
		if (issueType != null) issueType = issueType.toUpperCase();

		StringBuilder sentIds = new StringBuilder();

		for (CaseManagementNote localNote : localNotes) {	
			try {
				// if it's locked or if it's not in this facility ignore it.
				if (localNote.isLocked() || !programIds.contains(Integer.parseInt(localNote.getProgram_no()))) continue;

				// note hasn't changed since last sync
				if (localNote.getUpdate_date() != null && localNote.getUpdate_date().before(lastDataUpdated)) continue;

				CachedDemographicNote noteToSend = makeRemoteNote(localNote, issueType);
				ArrayList<CachedDemographicNote> notesToSend = new ArrayList<CachedDemographicNote>();
				notesToSend.add(noteToSend);
				service.setCachedDemographicNotes(notesToSend);

				sentIds.append("," + localNote.getId());
			} catch (NumberFormatException e) {
				logger.error("Unexpected error. ProgramNo=" + localNote.getProgram_no(), e);
			}
		}

		conformanceTestLog(facility, "CaseManagementNote", sentIds.toString());
		sentIds = new StringBuilder();

		// add group notes as well.
		logger.info("checking for group notes for " + demographicId);
		List<GroupNoteLink> noteLinks = groupNoteDao.findLinksByDemographic(demographicId);
		logger.info("found " + noteLinks.size() + " group notes for " + demographicId);
		for (GroupNoteLink noteLink : noteLinks) {
			int orginalNoteId = noteLink.getNoteId();
			CaseManagementNote localNote = caseManagementNoteDAO.getNote(Long.valueOf(orginalNoteId));
			localNote.setDemographic_no(String.valueOf(demographicId));

			try {
				// if it's locked or if it's not in this facility ignore it.
				if (localNote.isLocked() || !programIds.contains(Integer.parseInt(localNote.getProgram_no()))) continue;

				CachedDemographicNote noteToSend = makeRemoteNote(localNote, issueType);
				ArrayList<CachedDemographicNote> notesToSend = new ArrayList<CachedDemographicNote>();
				notesToSend.add(noteToSend);
				service.setCachedDemographicNotes(notesToSend);
				logger.info("adding group note to send");

				sentIds.append("," + noteLink.getId());
			} catch (NumberFormatException e) {
				logger.error("Unexpected error. ProgramNo=" + localNote.getProgram_no(), e);
			}

		}

		conformanceTestLog(facility, "GroupNoteLink", sentIds.toString());

		throttleAndChecks();
	}

	private CachedDemographicNote makeRemoteNote(CaseManagementNote localNote, String issueType) {

		CachedDemographicNote note = new CachedDemographicNote();

		CachedDemographicNoteCompositePk pk = new CachedDemographicNoteCompositePk();
		pk.setUuid(localNote.getUuid() + ":" + localNote.getDemographic_no());
		note.setCachedDemographicNoteCompositePk(pk);

		note.setCaisiDemographicId(Integer.parseInt(localNote.getDemographic_no()));
		note.setCaisiProgramId(Integer.parseInt(localNote.getProgram_no()));
		note.setEncounterType(localNote.getEncounter_type());
		note.setNote(localNote.getNote());
		note.setObservationCaisiProviderId(localNote.getProviderNo());
		note.setObservationDate(MiscUtils.toCalendar(localNote.getObservation_date()));
		note.setRole(localNote.getRoleName());
		note.setSigningCaisiProviderId(localNote.getSigning_provider_no());
		note.setUpdateDate(MiscUtils.toCalendar(localNote.getUpdate_date()));

		List<NoteIssue> issues = note.getIssues();
		List<CaseManagementIssue> localIssues = caseManagementIssueNotesDao.getNoteIssues(localNote.getId().intValue());
		for (CaseManagementIssue caseManagementIssue : localIssues) {
			long issueId = caseManagementIssue.getIssue_id();
			Issue localIssue = issueDao.getIssue(issueId);

			NoteIssue noteIssue = new NoteIssue();
			if ("ICD10".equalsIgnoreCase(issueType)) noteIssue.setCodeType(CodeType.ICD_10); // temporary hard code hack till we sort this out
			noteIssue.setIssueCode(localIssue.getCode());
			issues.add(noteIssue);
		}

		return (note);
	}

	private void pushDemographicDrugs(Date lastDataUpdated, Facility facility, List<String> providerIdsInFacility, DemographicWs demographicService, Integer demographicId) throws ShutdownException {
		logger.debug("pushing demographicDrugss facilityId:" + facility.getId() + ", demographicId:" + demographicId);
		StringBuilder sentIds = new StringBuilder();

		List<Drug> drugs = drugDao.findByDemographicIdUpdatedAfterDate(demographicId, lastDataUpdated);
		if (drugs == null || drugs.size() == 0) return;

		if (drugs != null) {
			for (Drug drug : drugs) {
				if (!providerIdsInFacility.contains(drug.getProviderNo())) continue;

				CachedDemographicDrug cachedDemographicDrug = new CachedDemographicDrug();

				cachedDemographicDrug.setArchived(drug.isArchived());
				cachedDemographicDrug.setArchivedReason(drug.getArchivedReason());
				cachedDemographicDrug.setArchivedDate(MiscUtils.toCalendar(drug.getArchivedDate()));
				cachedDemographicDrug.setAtc(drug.getAtc());
				cachedDemographicDrug.setBrandName(drug.getBrandName());
				cachedDemographicDrug.setCaisiDemographicId(drug.getDemographicId());
				cachedDemographicDrug.setCaisiProviderId(drug.getProviderNo());
				cachedDemographicDrug.setCreateDate(MiscUtils.toCalendar(drug.getCreateDate()));
				cachedDemographicDrug.setCustomInstructions(drug.isCustomInstructions());
				cachedDemographicDrug.setCustomName(drug.getCustomName());
				cachedDemographicDrug.setDosage(drug.getDosage());
				cachedDemographicDrug.setDrugForm(drug.getDrugForm());
				cachedDemographicDrug.setDuration(drug.getDuration());
				cachedDemographicDrug.setDurUnit(drug.getDurUnit());
				cachedDemographicDrug.setEndDate(MiscUtils.toCalendar(drug.getEndDate()));
				FacilityIdIntegerCompositePk pk = new FacilityIdIntegerCompositePk();
				pk.setCaisiItemId(drug.getId());
				cachedDemographicDrug.setFacilityIdIntegerCompositePk(pk);
				cachedDemographicDrug.setFreqCode(drug.getFreqCode());
				cachedDemographicDrug.setGenericName(drug.getGenericName());
				cachedDemographicDrug.setLastRefillDate(MiscUtils.toCalendar(drug.getLastRefillDate()));
				cachedDemographicDrug.setLongTerm(drug.getLongTerm());
				cachedDemographicDrug.setMethod(drug.getMethod());
				cachedDemographicDrug.setNoSubs(drug.isNoSubs());
				cachedDemographicDrug.setPastMed(drug.getPastMed());
				cachedDemographicDrug.setPatientCompliance(drug.getPatientCompliance());
				cachedDemographicDrug.setPrn(drug.isPrn());
				cachedDemographicDrug.setQuantity(drug.getQuantity());
				cachedDemographicDrug.setRegionalIdentifier(drug.getRegionalIdentifier());
				cachedDemographicDrug.setRepeats(drug.getRepeat());
				cachedDemographicDrug.setRoute(drug.getRoute());
				cachedDemographicDrug.setRxDate(MiscUtils.toCalendar(drug.getRxDate()));
				if (drug.getScriptNo() != null) cachedDemographicDrug.setScriptNo(drug.getScriptNo());
				cachedDemographicDrug.setSpecial(drug.getSpecial());
				cachedDemographicDrug.setTakeMax(drug.getTakeMax());
				cachedDemographicDrug.setTakeMin(drug.getTakeMin());
				cachedDemographicDrug.setUnit(drug.getUnit());
				cachedDemographicDrug.setUnitName(drug.getUnitName());

				ArrayList<CachedDemographicDrug> drugsToSend = new ArrayList<CachedDemographicDrug>();
				drugsToSend.add(cachedDemographicDrug);
				demographicService.setCachedDemographicDrugs(drugsToSend);

				sentIds.append("," + drug.getId());
			}
		}

		// if (drugsToSend.size()>0) demographicService.setCachedDemographicDrugs(drugsToSend);

		throttleAndChecks();
		conformanceTestLog(facility, "Drug", sentIds.toString());
	}

	private void pushAllergies(Date lastDataUpdated, Facility facility, DemographicWs demographicService, Integer demographicId) throws ShutdownException {
		logger.debug("pushing demographicAllergies facilityId:" + facility.getId() + ", demographicId:" + demographicId);

		AllergyDao allergyDao = (AllergyDao) SpringUtils.getBean("allergyDao");
		List<Allergy> allergies = allergyDao.findByDemographicIdUpdatedAfterDate(demographicId, lastDataUpdated);
		if (allergies.size() == 0) return;

		StringBuilder sentIds = new StringBuilder();

		for (Allergy allergy : allergies) {
			CachedDemographicAllergy cachedAllergy = new CachedDemographicAllergy();

			FacilityIdIntegerCompositePk facilityIdIntegerCompositePk = new FacilityIdIntegerCompositePk();
			facilityIdIntegerCompositePk.setCaisiItemId(allergy.getAllergyId());
			cachedAllergy.setFacilityIdIntegerCompositePk(facilityIdIntegerCompositePk);
			
			if(allergy.getAgccs() != null){
			   cachedAllergy.setAgccs(allergy.getAgccs());
			}
			if(allergy.getAgcsp() != null){
				cachedAllergy.setAgcsp(allergy.getAgcsp());
			}
			cachedAllergy.setAgeOfOnset(allergy.getAgeOfOnset());
			cachedAllergy.setCaisiDemographicId(demographicId);
			cachedAllergy.setDescription(allergy.getDescription());
			cachedAllergy.setEntryDate(DateUtils.toGregorianCalendar(allergy.getEntryDate()));
			
			if(allergy.getHiclSeqno() != null){
			   cachedAllergy.setHiclSeqNo(allergy.getHiclSeqno());
			}
			if(allergy.getHicSeqno() !=null){
				cachedAllergy.setHicSeqNo(allergy.getHicSeqno());
			}
			
			cachedAllergy.setLifeStage(allergy.getLifeStage());
			cachedAllergy.setOnSetCode(allergy.getOnsetOfReaction());
			if (allergy.getDrugrefId() != null) cachedAllergy.setPickId(Integer.parseInt(allergy.getDrugrefId()));
			cachedAllergy.setReaction(allergy.getReaction());
			cachedAllergy.setRegionalIdentifier(allergy.getRegionalIdentifier());
			cachedAllergy.setSeverityCode(allergy.getSeverityOfReaction());
			if (allergy.getStartDate() != null) cachedAllergy.setStartDate(DateUtils.toGregorianCalendar(allergy.getStartDate()));
			cachedAllergy.setTypeCode(allergy.getTypeCode());

			ArrayList<CachedDemographicAllergy> cachedAllergies = new ArrayList<CachedDemographicAllergy>();
			cachedAllergies.add(cachedAllergy);
			demographicService.setCachedDemographicAllergies(cachedAllergies);
			throttleAndChecks();

			sentIds.append("," + allergy.getAllergyId());
		}

		conformanceTestLog(facility, "Allergy", sentIds.toString());
	}

	private void pushAppointments(Date lastDataUpdated, Facility facility, DemographicWs demographicService, Integer demographicId) throws ShutdownException {
		logger.debug("pushing appointments facilityId:" + facility.getId() + ", demographicId:" + demographicId);

		List<Appointment> appointments = appointmentDao.getAllByDemographicNo(demographicId);
		if (appointments.size() == 0) return;

		StringBuilder sentIds = new StringBuilder();

		for (Appointment appointment : appointments) {
			if (appointment.getUpdateDateTime() != null && lastDataUpdated.after(appointment.getUpdateDateTime())) continue;

			if (appointment.getUpdateDateTime() != null && appointment.getUpdateDateTime().before(lastDataUpdated)) continue;

			CachedAppointment cachedAppointment = new CachedAppointment();
			FacilityIdIntegerCompositePk facilityIdIntegerCompositePk = new FacilityIdIntegerCompositePk();
			facilityIdIntegerCompositePk.setCaisiItemId(appointment.getId());
			cachedAppointment.setFacilityIdIntegerCompositePk(facilityIdIntegerCompositePk);

			cachedAppointment.setAppointmentDate(MiscUtils.toCalendar(appointment.getAppointmentDate()));
			cachedAppointment.setCaisiDemographicId(demographicId);
			cachedAppointment.setCaisiProviderId(appointment.getProviderNo());
			cachedAppointment.setCreateDatetime(MiscUtils.toCalendar(appointment.getCreateDateTime()));
			cachedAppointment.setEndTime(MiscUtils.toCalendar(appointment.getEndTime()));
			cachedAppointment.setLocation(appointment.getLocation());
			cachedAppointment.setNotes(appointment.getNotes());
			cachedAppointment.setReason(appointment.getReason());
			cachedAppointment.setRemarks(appointment.getRemarks());
			cachedAppointment.setResources(appointment.getResources());
			cachedAppointment.setStartTime(MiscUtils.toCalendar(appointment.getStartTime()));
			cachedAppointment.setStatus(appointment.getStatus());
			cachedAppointment.setStyle(appointment.getStyle());
			cachedAppointment.setType(appointment.getType());
			cachedAppointment.setUpdateDatetime(MiscUtils.toCalendar(appointment.getUpdateDateTime()));

			ArrayList<CachedAppointment> cachedAppointments = new ArrayList<CachedAppointment>();
			cachedAppointments.add(cachedAppointment);
			demographicService.setCachedAppointments(cachedAppointments);

			sentIds.append("," + appointment.getId());
		}

		throttleAndChecks();
		conformanceTestLog(facility, "Appointment", sentIds.toString());
	}

	private void pushDxresearchs(Date lastDataUpdated, Facility facility, DemographicWs demographicService, Integer demographicId) throws ShutdownException {
		logger.debug("pushing dxresearchs facilityId:" + facility.getId() + ", demographicId:" + demographicId);

		List<Dxresearch> dxresearchs = dxresearchDao.getByDemographicNo(demographicId);
		if (dxresearchs.size() == 0) return;

		StringBuilder sentIds = new StringBuilder();

		for (Dxresearch dxresearch : dxresearchs) {
			if (dxresearch.getUpdateDate() != null && dxresearch.getUpdateDate().before(lastDataUpdated)) continue;

			CachedDxresearch cachedDxresearch = new CachedDxresearch();
			FacilityIdIntegerCompositePk facilityIdIntegerCompositePk = new FacilityIdIntegerCompositePk();
			facilityIdIntegerCompositePk.setCaisiItemId(dxresearch.getId().intValue());
			cachedDxresearch.setFacilityIdIntegerCompositePk(facilityIdIntegerCompositePk);

			cachedDxresearch.setCaisiDemographicId(demographicId);
			cachedDxresearch.setDxresearchCode(dxresearch.getDxresearchCode());
			cachedDxresearch.setCodingSystem(dxresearch.getCodingSystem());
			cachedDxresearch.setStartDate(MiscUtils.toCalendar(dxresearch.getStartDate()));
			cachedDxresearch.setUpdateDate(MiscUtils.toCalendar(dxresearch.getUpdateDate()));
			cachedDxresearch.setStatus(String.valueOf(dxresearch.getStatus()));

			ArrayList<CachedDxresearch> cachedDxresearchs = new ArrayList<CachedDxresearch>();
			cachedDxresearchs.add(cachedDxresearch);
			demographicService.setCachedDxresearch(cachedDxresearchs);

			sentIds.append("," + dxresearch.getId());
		}

		throttleAndChecks();
		conformanceTestLog(facility, "DxResearch", sentIds.toString());
	}

	private void pushBillingItems(Date lastDataUpdated, Facility facility, DemographicWs demographicService, Integer demographicId) throws ShutdownException {
		logger.debug("pushing billingitems facilityId:" + facility.getId() + ", demographicId:" + demographicId);

		List<BillingOnCHeader1> billingCh1s = billingOnItemDao.getCh1ByDemographicNo(demographicId);
		if (billingCh1s.size() == 0) return;

		for (BillingOnCHeader1 billingCh1 : billingCh1s) {
			List<BillingOnItem> billingItems = billingOnItemDao.getBillingItemByCh1Id(billingCh1.getId());
			for (BillingOnItem billingItem : billingItems) {
				MiscUtils.checkShutdownSignaled();

				CachedBillingOnItem cachedBillingOnItem = new CachedBillingOnItem();
				FacilityIdIntegerCompositePk facilityIdIntegerCompositePk = new FacilityIdIntegerCompositePk();
				facilityIdIntegerCompositePk.setCaisiItemId(billingItem.getId());
				cachedBillingOnItem.setFacilityIdIntegerCompositePk(facilityIdIntegerCompositePk);

				cachedBillingOnItem.setCaisiDemographicId(demographicId);
				cachedBillingOnItem.setCaisiProviderId(billingCh1.getProvider_no());
				cachedBillingOnItem.setApptProviderId(billingCh1.getApptProvider_no());
				cachedBillingOnItem.setAsstProviderId(billingCh1.getAsstProvider_no());
				cachedBillingOnItem.setAppointmentId(billingCh1.getAppointment_no());
				cachedBillingOnItem.setDx(billingItem.getDx());
				cachedBillingOnItem.setDx1(billingItem.getDx1());
				cachedBillingOnItem.setDx2(billingItem.getDx2());
				cachedBillingOnItem.setServiceCode(billingItem.getService_code());
				cachedBillingOnItem.setServiceDate(MiscUtils.toCalendar(billingItem.getService_date()));
				cachedBillingOnItem.setStatus(billingItem.getStatus());

				ArrayList<CachedBillingOnItem> cachedBillingOnItems = new ArrayList<CachedBillingOnItem>();
				cachedBillingOnItems.add(cachedBillingOnItem);
				demographicService.setCachedBillingOnItem(cachedBillingOnItems);
			}
		}

		throttleAndChecks();
	}

	private void pushEforms(Date lastDataUpdated, Facility facility, DemographicWs demographicService, Integer demographicId) throws ShutdownException {
		logger.debug("pushing eforms facilityId:" + facility.getId() + ", demographicId:" + demographicId);

		List<EFormData> eformDatas = eFormDataDao.findByDemographicIdSinceLastDate(demographicId,lastDataUpdated);
		if (eformDatas.size() == 0) return;

		StringBuilder sentIds = new StringBuilder();
		List<Integer> fdids = new ArrayList<Integer>();

		for (EFormData eformData : eformDatas) {

			CachedEformData cachedEformData = new CachedEformData();
			FacilityIdIntegerCompositePk facilityIdIntegerCompositePk = new FacilityIdIntegerCompositePk();
			facilityIdIntegerCompositePk.setCaisiItemId(eformData.getId());
			cachedEformData.setFacilityIdIntegerCompositePk(facilityIdIntegerCompositePk);

			cachedEformData.setCaisiDemographicId(demographicId);
			cachedEformData.setFormDate(MiscUtils.toCalendar(eformData.getFormDate()));
			cachedEformData.setFormTime(MiscUtils.toCalendar(eformData.getFormTime()));
			cachedEformData.setFormId(eformData.getFormId());
			cachedEformData.setFormName(eformData.getFormName());
			cachedEformData.setFormData(eformData.getFormData());
			cachedEformData.setSubject(eformData.getSubject());
			cachedEformData.setStatus(eformData.isCurrent());
			cachedEformData.setFormProvider(eformData.getProviderNo());

			ArrayList<CachedEformData> cachedEformDatas = new ArrayList<CachedEformData>();
			cachedEformDatas.add(cachedEformData);
			demographicService.setCachedEformData(cachedEformDatas);

			sentIds.append("," + eformData.getId());
			fdids.add(eformData.getId());
		}

		conformanceTestLog(facility, "EFormData", sentIds.toString());

		List<EFormValue> eFormValues = eFormValueDao.findByFormDataIdList(fdids);
		if (eFormValues.size() == 0) return;

		for (EFormValue eFormValue : eFormValues) {
			CachedEformValue cachedEformValue = new CachedEformValue();
			FacilityIdIntegerCompositePk facilityIdIntegerCompositePk = new FacilityIdIntegerCompositePk();
			facilityIdIntegerCompositePk.setCaisiItemId(eFormValue.getId());
			cachedEformValue.setFacilityIdIntegerCompositePk(facilityIdIntegerCompositePk);

			cachedEformValue.setCaisiDemographicId(demographicId);
			cachedEformValue.setFormId(eFormValue.getFormId());
			cachedEformValue.setFormDataId(eFormValue.getFormDataId());
			cachedEformValue.setVarName(eFormValue.getVarName());
			cachedEformValue.setVarValue(eFormValue.getVarValue());

			ArrayList<CachedEformValue> cachedEformValues = new ArrayList<CachedEformValue>();
			cachedEformValues.add(cachedEformValue);
			demographicService.setCachedEformValues(cachedEformValues);
		}

		throttleAndChecks();
	}

	private void pushMeasurements(Date lastDataUpdated, Facility facility, DemographicWs demographicService, Integer demographicId) throws ShutdownException {
		logger.debug("pushing measurements facilityId:" + facility.getId() + ", demographicId:" + demographicId);

		MeasurementDao measurementDao = (MeasurementDao) SpringUtils.getBean("measurementDao");

		List<Measurement> measurements = measurementDao.findByDemographicIdUpdatedAfterDate(demographicId, lastDataUpdated);
		if (measurements.size() == 0) return;

		StringBuilder sentIds = new StringBuilder();

		for (Measurement measurement : measurements) {
			CachedMeasurement cachedMeasurement = new CachedMeasurement();
			FacilityIdIntegerCompositePk facilityIdIntegerCompositePk = new FacilityIdIntegerCompositePk();
			facilityIdIntegerCompositePk.setCaisiItemId(measurement.getId());
			cachedMeasurement.setFacilityIdIntegerCompositePk(facilityIdIntegerCompositePk);

			cachedMeasurement.setCaisiDemographicId(demographicId);
			cachedMeasurement.setCaisiProviderId(measurement.getProviderNo());
			cachedMeasurement.setComments(measurement.getComments());
			cachedMeasurement.setDataField(measurement.getDataField());
			cachedMeasurement.setDateEntered(MiscUtils.toCalendar(measurement.getCreateDate()));
			cachedMeasurement.setDateObserved(MiscUtils.toCalendar(measurement.getDateObserved()));
			cachedMeasurement.setMeasuringInstruction(measurement.getMeasuringInstruction());
			cachedMeasurement.setType(measurement.getType());

			ArrayList<CachedMeasurement> cachedMeasurements = new ArrayList<CachedMeasurement>();
			cachedMeasurements.add(cachedMeasurement);
			demographicService.setCachedMeasurements(cachedMeasurements);

			sentIds.append("," + measurement.getId());

			List<MeasurementsExt> measurementExts = measurementsExtDao.getMeasurementsExtByMeasurementId(measurement.getId());
			for (MeasurementsExt measurementExt : measurementExts) {
				MiscUtils.checkShutdownSignaled();
				CachedMeasurementExt cachedMeasurementExt = new CachedMeasurementExt();
				FacilityIdIntegerCompositePk fidIntegerCompositePk = new FacilityIdIntegerCompositePk();
				fidIntegerCompositePk.setCaisiItemId(measurementExt.getId());
				cachedMeasurementExt.setFacilityIdIntegerCompositePk(fidIntegerCompositePk);

				cachedMeasurementExt.setMeasurementId(measurementExt.getMeasurementId());
				cachedMeasurementExt.setKeyval(measurementExt.getKeyVal());
				cachedMeasurementExt.setVal(measurementExt.getVal());

				ArrayList<CachedMeasurementExt> cachedMeasurementExts = new ArrayList<CachedMeasurementExt>();
				cachedMeasurementExts.add(cachedMeasurementExt);
				demographicService.setCachedMeasurementExts(cachedMeasurementExts);
			}

			MeasurementTypeDao measurementTypeDao = (MeasurementTypeDao) SpringUtils.getBean("measurementTypeDao");
			List<MeasurementType> measurementTypes = measurementTypeDao.findByType(measurement.getType());
			for (MeasurementType measurementType : measurementTypes) {
				MiscUtils.checkShutdownSignaled();

				CachedMeasurementType cachedMeasurementType = new CachedMeasurementType();
				FacilityIdIntegerCompositePk fidIntegerCompositePk = new FacilityIdIntegerCompositePk();
				fidIntegerCompositePk.setCaisiItemId(measurementType.getId());
				cachedMeasurementType.setFacilityIdIntegerCompositePk(fidIntegerCompositePk);

				cachedMeasurementType.setType(measurementType.getType());
				cachedMeasurementType.setTypeDescription(measurementType.getTypeDescription());
				cachedMeasurementType.setMeasuringInstruction(measurementType.getMeasuringInstruction());

				ArrayList<CachedMeasurementType> cachedMeasurementTypes = new ArrayList<CachedMeasurementType>();
				cachedMeasurementTypes.add(cachedMeasurementType);
				demographicService.setCachedMeasurementTypes(cachedMeasurementTypes);
			}

			List<Measurementmap> measurementMaps = measurementMapDao.getMapsByIdent(measurement.getType());
			for (Measurementmap measurementMap : measurementMaps) {

				CachedMeasurementMap cachedMeasurementMap = new CachedMeasurementMap();
				FacilityIdIntegerCompositePk fidIntegerCompositePk = new FacilityIdIntegerCompositePk();
				fidIntegerCompositePk.setCaisiItemId(measurementMap.getId());
				cachedMeasurementMap.setFacilityIdIntegerCompositePk(fidIntegerCompositePk);

				cachedMeasurementMap.setIdentCode(measurementMap.getIdentCode());
				cachedMeasurementMap.setLoincCode(measurementMap.getLoincCode());
				cachedMeasurementMap.setName(measurementMap.getName());
				cachedMeasurementMap.setLabType(measurementMap.getLabType());

				ArrayList<CachedMeasurementMap> cachedMeasurementMaps = new ArrayList<CachedMeasurementMap>();
				cachedMeasurementMaps.add(cachedMeasurementMap);
				demographicService.setCachedMeasurementMaps(cachedMeasurementMaps);
			}
		}

		throttleAndChecks();
		conformanceTestLog(facility, "Measurements", sentIds.toString());
	}
	
	
	/*
	1) demographicWs.getDemographicIdPushedAfterDateByRequestingFacility : 
		which gets the local demographicId's which have changed, it will traverse linked records so if a linked record changes, your local id is reported as changed.

	2) demographicWs.getDemographicsPushedAfterDate : 
		which is a raw listing of the direct records which have changed, i.e. (facilityId, oscarDemographicId).
	*/	
	private void findChangedRecordsFromIntegrator(Facility facility) throws MalformedURLException {//throws IOException, ShutdownException {
		logger.info("Start fetch data for facility : " + facility.getId() + " : " + facility.getName());
		boolean integratorLocalStore = OscarProperties.getInstance().getBooleanProperty("INTEGRATOR_LOCAL_STORE","yes");
		if(!integratorLocalStore){
			logger.info("local store not enabled");
			return;
		}
		DemographicWs demographicService = CaisiIntegratorManager.getDemographicWs();
		
		
		Calendar nextTime = Calendar.getInstance();
		
		Date lastPushDate = new Date(0);
		try{
			UserProperty lastPull = userPropertyDao.getProp(UserProperty.INTEGRATOR_LAST_PULL_PRIMARY_EMR+"+"+facility.getId());
			lastPushDate.setTime(Long.parseLong(lastPull.getValue()));
		}catch(Exception epull){
			MiscUtils.getLogger().error("lastPull Error:",epull);
			lastPushDate = new Date(0);
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(lastPushDate);
		
		List<Integer> demographicNos = demographicService.getDemographicIdPushedAfterDateByRequestingFacility(cal);
		
		if(demographicNos.isEmpty()){
			logger.debug("No demographics updated on the integrator");
		}else{
			logger.debug("demos changed "+demographicNos.size());
		}
		int demographicFetchCount = 0;
		for(Integer demographicNo:demographicNos){
			logger.debug("Demographic "+demographicNo+" updated on the integrator, primary emr ? ");
			DemographicExt demographicExt = demographicExtDao.getLatestDemographicExt(demographicNo, "primaryEMR");
			if (demographicExt != null && demographicExt.getValue().equals("1")){
				demographicFetchCount++;
				BenchmarkTimer benchTimer = new BenchmarkTimer("fetch and save for facilityId:" + facility.getId() + ", demographicId:" + demographicNo + "  " + demographicFetchCount + " of " + demographicNos.size());
				IntegratorFallBackManager.saveLinkNotes(demographicNo);
				benchTimer.tag("saveLinkedNotes");
				IntegratorFallBackManager.saveRemoteForms(demographicNo);
				benchTimer.tag("saveRemoteForms");
				
				
				
				IntegratorFallBackManager.saveDemographicIssues(demographicNo);
				benchTimer.tag("saveDemographicIssues");
				IntegratorFallBackManager.saveDemographicPreventions(demographicNo);
				benchTimer.tag("saveDemographicPreventions");
				IntegratorFallBackManager.saveDemographicDrugs(demographicNo);
				benchTimer.tag("saveDemographicDrugs");
				IntegratorFallBackManager.saveAdmissions(demographicNo);
				benchTimer.tag("saveAdmissions");
				IntegratorFallBackManager.saveAppointments(demographicNo);
				benchTimer.tag("saveAppointments");
				IntegratorFallBackManager.saveAllergies(demographicNo);
				benchTimer.tag("saveAllergies");
				IntegratorFallBackManager.saveDocuments(demographicNo);
				benchTimer.tag("saveDocuments");
				IntegratorFallBackManager.saveLabResults(demographicNo);
				benchTimer.tag("saveLabResults");
 
				//These don't exist
				//IntegratorFallBackManager.saveMeasurements(demographicNo); // Not being displayed yet
				//IntegratorFallBackManager.saveDxresearchs(demographicNo);  //Not being displayed yet
				//IntegratorFallBackManager.saveBillingItems(demographicNo);//Not being displayed yet
				//IntegratorFallBackManager.saveEforms(demographicNo);//Not being displayed yet

				logger.debug(benchTimer.report());
			}
			userPropertyDao.saveProp(UserProperty.INTEGRATOR_LAST_PULL_PRIMARY_EMR+"+"+facility.getId(), "" + nextTime.getTime().getTime());
		}
		logger.info("End fetch data for facility : " + facility.getId() + " : " + facility.getName());
	}


	

	/**
	 * This method should not be used except during conformance testing. It will log all sends to the integrator. This is superfluous because all data is sent, we already know it's "all sent" even with out the logs.
	 */
	private static void conformanceTestLog(Facility facility, String dataType, String ids) {
		if (ConformanceTestHelper.enableConformanceOnlyTestFeatures) {
			ids = StringUtils.trimToNull(ids);
			if (ids != null) LogAction.addLogSynchronous(null, "Integrator Send", dataType, ids, facility.getIntegratorUrl());
		}
	}

	private static void throttleAndChecks() throws ShutdownException {
		MiscUtils.checkShutdownSignaled();

		try {
			Thread.sleep(INTEGRATOR_THROTTLE_DELAY);
		} catch (InterruptedException e) {
			logger.error("Error", e);
		}
	}
}
