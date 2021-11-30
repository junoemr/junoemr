/**
 * Copyright (c) 2008-2012 Indivica Inc.
 *
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */
package org.oscarehr.olis;

import com.indivica.olis.Driver;
import com.indivica.olis.DriverResponse;
import com.indivica.olis.parameters.OBR16;
import com.indivica.olis.parameters.OBR22;
import com.indivica.olis.parameters.OBR25;
import com.indivica.olis.parameters.OBR28;
import com.indivica.olis.parameters.OBR4;
import com.indivica.olis.parameters.OBR7;
import com.indivica.olis.parameters.OBX3;
import com.indivica.olis.parameters.ORC21;
import com.indivica.olis.parameters.ORC4;
import com.indivica.olis.parameters.PID3;
import com.indivica.olis.parameters.PID51;
import com.indivica.olis.parameters.PID52;
import com.indivica.olis.parameters.PID7;
import com.indivica.olis.parameters.PID8;
import com.indivica.olis.parameters.PV117;
import com.indivica.olis.parameters.PV17;
import com.indivica.olis.parameters.QRD7;
import com.indivica.olis.parameters.ZBE4;
import com.indivica.olis.parameters.ZBE6;
import com.indivica.olis.parameters.ZBR2;
import com.indivica.olis.parameters.ZBR3;
import com.indivica.olis.parameters.ZBR4;
import com.indivica.olis.parameters.ZBR6;
import com.indivica.olis.parameters.ZBR8;
import com.indivica.olis.parameters.ZBX1;
import com.indivica.olis.parameters.ZPD1;
import com.indivica.olis.parameters.ZPD3;
import com.indivica.olis.parameters.ZRP1;
import com.indivica.olis.parameters.ZSD;
import com.indivica.olis.queries.Query;
import com.indivica.olis.queries.Z01Query;
import com.indivica.olis.queries.Z02Query;
import com.indivica.olis.queries.Z04Query;
import com.indivica.olis.queries.Z05Query;
import com.indivica.olis.queries.Z06Query;
import com.indivica.olis.queries.Z07Query;
import com.indivica.olis.queries.Z08Query;
import com.indivica.olis.queries.Z50Query;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.oscarehr.common.dao.UserPropertyDAO;
import org.oscarehr.common.model.UserProperty;
import org.oscarehr.dataMigration.model.demographic.Demographic;
import org.oscarehr.demographic.service.DemographicService;
import org.oscarehr.provider.dao.ProviderDataDao;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import oscar.log.LogAction;
import oscar.log.LogConst;
import oscar.util.ConversionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.indivica.olis.parameters.ZPD1.CONSENT_MARKER_SUBSTITUTE;

public class OLISSearchAction extends DispatchAction
{
	private final DemographicService demographicService = (DemographicService) SpringUtils.getBean("demographic.service.DemographicService");
	private final ProviderDataDao providerDao = SpringUtils.getBean(ProviderDataDao.class);
	private final UserPropertyDAO userPropertyDAO = SpringUtils.getBean(UserPropertyDAO.class);

	public static Map<String, Query> searchQueryMap = new HashMap<>();

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	{

		LoggedInInfo loggedInInfo=LoggedInInfo.getLoggedInInfoFromSession(request);
		DriverResponse driverResponse = null;
		
		String queryType = request.getParameter("queryType");
		boolean redo = "true".equals(request.getParameter("redo"));
		if (redo)
		{
			String uuid = request.getParameter("uuid");
			request.setAttribute("searchUuid", uuid);
			boolean force = "true".equals(request.getParameter("force"));
			String continuationPointer = StringUtils.trimToNull(request.getParameter("continuationPointer"));

			Query query = (Query) searchQueryMap.get(uuid).clone();
			if(force)
			{
				fillConsentOverrideSegments(request, query);

				// Log the consent override
				logConsentGiven(loggedInInfo.getLoggedInProviderNo(), query.getDemographicNo(), request);
			}

			// ensure the demographic carries through to results when re-submitting queries
			String demographicNo = StringUtils.trimToNull(request.getParameter("demographic"));
			if(demographicNo != null)
			{
				Demographic demo = demographicService.getDemographicModel(Integer.parseInt(demographicNo));
				request.setAttribute("demographic", demo);
			}

			driverResponse = Driver.submitOLISQuery(loggedInInfo.getLoggedInProvider(), query, continuationPointer);
		}
		else if (queryType != null) {
			Query query = null;

			String[] dateFormat = new String[] {
					"yyyy-MM-dd"
			};

			if (queryType.equalsIgnoreCase("Z01")) {
				query = new Z01Query();
				String startTimePeriod = request.getParameter("startTimePeriod");
				String endTimePeriod = request.getParameter("endTimePeriod");



				try {
					if (startTimePeriod != null && startTimePeriod.trim().length() > 0) {
						Date startTime = DateUtils.parseDate(startTimePeriod, dateFormat);
						if (endTimePeriod != null && endTimePeriod.trim().length() > 0) {
							Date endTime = changeToEndOfDay(DateUtils.parseDate(endTimePeriod, dateFormat));

							List<Date> dateList = new LinkedList<Date>();
							dateList.add(startTime);
							dateList.add(endTime);

							OBR22 obr22 = new OBR22();
							obr22.setValue(dateList);

							((Z01Query) query).setStartEndTimestamp(obr22);
						} else {
							OBR22 obr22 = new OBR22();
							obr22.setValue(startTime);

							((Z01Query) query).setStartEndTimestamp(obr22);
						}
					}
				} catch (Exception e) {
					MiscUtils.getLogger().error("Can't parse date given for OLIS query", e);
				}


				String observationStartTimePeriod = request.getParameter("observationStartTimePeriod");
				String observationEndTimePeriod = request.getParameter("observationEndTimePeriod");

				try {
					if (observationStartTimePeriod != null && observationStartTimePeriod.trim().length() > 0) {
						Date observationStartTime = DateUtils.parseDate(observationStartTimePeriod, dateFormat);
						if (observationEndTimePeriod != null && observationEndTimePeriod.trim().length() > 0) {
							Date observationEndTime = changeToEndOfDay(DateUtils.parseDate(observationEndTimePeriod, dateFormat));

							List<Date> dateList = new LinkedList<Date>();
							dateList.add(observationStartTime);
							dateList.add(observationEndTime);

							OBR7 obr7 = new OBR7();
							obr7.setValue(dateList);

							((Z01Query) query).setEarliestLatestObservationDateTime(obr7);
						} else {
							OBR7 obr7 = new OBR7();
							obr7.setValue(observationStartTime);

							((Z01Query) query).setEarliestLatestObservationDateTime(obr7);
						}
					}
				} catch (Exception e) {
					MiscUtils.getLogger().error("Can't parse date given for OLIS query", e);
				}


				String quantityLimitedQuery = request.getParameter("quantityLimitedQuery");
				String quantityLimit = request.getParameter("quantityLimit");

				try {
					if (quantityLimitedQuery != null && quantityLimitedQuery.trim().length() > 0) {
						// Checked
						((Z01Query) query).setQuantityLimitedRequest(new QRD7(Integer.parseInt(quantityLimit)));
					}
				} catch (Exception e) {
					MiscUtils.getLogger().error("Can't parse the number given for quantity limit in OLIS query", e);
				}


				fillConsentOverrideSegments(request, query);

				String consentBlockAllIndicator = request.getParameter("consentBlockAllIndicator");
				if(StringUtils.isNotBlank(consentBlockAllIndicator))
				{
					((Z01Query) query).setPatientConsentBlockAllIndicator(new ZPD3("Y"));
				}


				String specimenCollector = request.getParameter("specimenCollector");
				if(StringUtils.isNotBlank(specimenCollector))
				{
					((Z01Query) query).setSpecimenCollector(new ZBR3(specimenCollector, "ISO"));
				}


				String performingLaboratory = request.getParameter("performingLaboratory");
				if(StringUtils.isNotBlank(performingLaboratory))
				{
					((Z01Query) query).setPerformingLaboratory(new ZBR6(performingLaboratory, "ISO"));
				}


				String excludePerformingLaboratory = request.getParameter("excludePerformingLaboratory");
				if(StringUtils.isNotBlank(excludePerformingLaboratory))
				{
					((Z01Query) query).setExcludePerformingLaboratory(new ZBE6(excludePerformingLaboratory, "ISO"));
				}


				String reportingLaboratory = request.getParameter("reportingLaboratory");
				if(StringUtils.isNotBlank(reportingLaboratory))
				{
					((Z01Query) query).setReportingLaboratory(new ZBR4(reportingLaboratory, "ISO"));
				}


				String excludeReportingLaboratory = request.getParameter("excludeReportingLaboratory");

				if(StringUtils.isNotBlank(excludeReportingLaboratory))
				{
					((Z01Query) query).setExcludeReportingLaboratory(new ZBE4(excludeReportingLaboratory, "ISO"));
				}

				String testRequestPlacer = request.getParameter("testRequestPlacer");
				if(StringUtils.isNotBlank(testRequestPlacer))
				{
					((Z01Query) query).setTestResultPlacer(new ZBR2(testRequestPlacer, "ISO"));
				}


				// Patient Identifier (PID.3 -- pull data from db and add to query)
				String demographicNo = request.getParameter("demographic");
				query.setDemographicNo(demographicNo);

				try {
					if (demographicNo != null && demographicNo.trim().length() > 0) {
						Demographic demo = demographicService.getDemographicModel(Integer.parseInt(demographicNo));
						request.setAttribute("demographic", demo);

						PID3 pid3 = new PID3(demo.getHealthNumber(), null, null, "JHN", demo.getHealthNumberProvinceCode(), "HL70347", demo.getSexString(), null);
						pid3.setValue(7, ConversionUtils.toLegacyDate(demo.getDateOfBirth()));

						((Z01Query) query).setPatientIdentifier(pid3);
					}
				} catch (Exception e) {
					MiscUtils.getLogger().error("Can't add requested patient data to OLIS query", e);
				}


				// Requesting HIC (ZRP.1 -- pull data from db and add to query)
				String requestingHicProviderNo = request.getParameter("requestingHic");

				try
				{
					if(StringUtils.isNotBlank(requestingHicProviderNo))
					{
						ProviderData provider = providerDao.find(requestingHicProviderNo);

						ZRP1 zrp1 = new ZRP1(provider.getOlisPractitionerNo(),
								userPropertyDAO.getStringValue(provider.getId(), UserProperty.OFFICIAL_OLIS_IDTYPE),
								"ON",
								"HL70347",
								userPropertyDAO.getStringValue(provider.getId(), UserProperty.OFFICIAL_LAST_NAME),
								userPropertyDAO.getStringValue(provider.getId(), UserProperty.OFFICIAL_FIRST_NAME),
								userPropertyDAO.getStringValue(provider.getId(), UserProperty.OFFICIAL_SECOND_NAME));

						((Z01Query) query).setRequestingHic(zrp1);
					}
				}
				catch(Exception e)
				{
					MiscUtils.getLogger().error("Can't add requested requesting HIC data to OLIS query", e);
				}


				// OBR.16
				String orderingPractitionerProviderNo = request.getParameter("orderingPractitioner");

				try
				{
					if(StringUtils.isNotBlank(orderingPractitionerProviderNo))
					{
						ProviderData orderingProvider = providerDao.find(orderingPractitionerProviderNo);
						OBR16 obr16 = new OBR16(
								orderingProvider.getOlisPractitionerNo(),
								userPropertyDAO.getStringValue(orderingProvider.getId(), UserProperty.OFFICIAL_OLIS_IDTYPE),
								"ON",
								"HL70347");

						((Z01Query) query).setOrderingPractitioner(obr16);
					}
				}
				catch(Exception e)
				{
					MiscUtils.getLogger().error("Can't add requested ordering practitioner data to OLIS query", e);
				}


				String copiedToPractitionerProviderNo = request.getParameter("copiedToPractitioner");

				try
				{
					if(StringUtils.isNotBlank(copiedToPractitionerProviderNo))
					{
						ProviderData ccProvider = providerDao.find(copiedToPractitionerProviderNo);
						OBR28 obr28 = new OBR28(
								ccProvider.getOlisPractitionerNo(),
								userPropertyDAO.getStringValue(ccProvider.getId(), UserProperty.OFFICIAL_OLIS_IDTYPE),
								"ON",
								"HL70347");

						((Z01Query) query).setCopiedToPractitioner(obr28);
					}
				}
				catch(Exception e)
				{
					MiscUtils.getLogger().error("Can't add requested copied to practitioner data to OLIS query", e);
				}


				String attendingPractitionerProviderNo = request.getParameter("attendingPractitioner");

				try
				{
					if(StringUtils.isNotBlank(attendingPractitionerProviderNo))
					{
						ProviderData attendingProvider = providerDao.find(attendingPractitionerProviderNo);
						PV17 pv17 = new PV17(
								attendingProvider.getOlisPractitionerNo(),
								userPropertyDAO.getStringValue(attendingProvider.getId(), UserProperty.OFFICIAL_OLIS_IDTYPE),
								"ON",
								"HL70347"
						);

						((Z01Query) query).setAttendingPractitioner(pv17);
					}
				}
				catch(Exception e)
				{
					MiscUtils.getLogger().error("Can't add requested attending practitioner data to OLIS query", e);
				}


				String admittingPractitionerProviderNo = request.getParameter("admittingPractitioner");

				try
				{
					if(StringUtils.isNotBlank(admittingPractitionerProviderNo))
					{
						ProviderData admittingProvider = providerDao.find(admittingPractitionerProviderNo);
						PV117 pv117 = new PV117(
								admittingProvider.getOlisPractitionerNo(),
								userPropertyDAO.getStringValue(admittingProvider.getId(), UserProperty.OFFICIAL_OLIS_IDTYPE),
								"ON",
								"HL70347");

						((Z01Query) query).setAdmittingPractitioner(pv117);
					}
				}
				catch(Exception e)
				{
					MiscUtils.getLogger().error("Can't add requested admitting practitioner data to OLIS query", e);
				}

				String orderingFacilityId = request.getParameter("orderingFacility");
				String placeGroupNumber = request.getParameter("placerGroupNumber");
				if(StringUtils.isNotBlank(orderingFacilityId) && StringUtils.isNotBlank(placeGroupNumber))
				{
					((Z01Query) query).setPlacerGroupNumber(new ORC4(placeGroupNumber, orderingFacilityId, "ISO"));
				}

				String[] testRequestStatusList = request.getParameterValues("testRequestStatus");
				if(testRequestStatusList != null)
				{
					for(String testRequestStatus : testRequestStatusList)
					{
						((Z01Query) query).addToTestRequestStatusList(new OBR25(testRequestStatus));
					}
				}

				String[] testResultCodeList = request.getParameterValues("testResultCode");
				if(testResultCodeList != null)
				{
					for(String testResultCode : testResultCodeList)
					{
						((Z01Query) query).addToTestResultCodeList(new OBX3(testResultCode, "HL79902"));
					}
				}

				String[] testRequestCodeList = request.getParameterValues("testRequestCode");
				if(testRequestCodeList != null)
				{
					for(String testRequestCode : testRequestCodeList)
					{
						((Z01Query) query).addToTestRequestCodeList(new OBR4(testRequestCode, "HL79901"));
					}
				}

				// Log the consent override
				logConsentGiven(loggedInInfo.getLoggedInProviderNo(), demographicNo, request);

			} else if (queryType.equalsIgnoreCase("Z02")) {
				query = new Z02Query();

				String retrieveAllResults = request.getParameter("retrieveAllResults");

				try {
					if (retrieveAllResults != null && retrieveAllResults.trim().length() > 0) {
						// Checked
						((Z02Query) query).setRetrieveAllTestResults(new ZBX1("*"));
					}
				} catch (Exception e) {
					MiscUtils.getLogger().error("Can't set retrieve all results option on OLIS query", e);
				}

				fillConsentOverrideSegments(request, query);

				String consentBlockAllIndicator = request.getParameter("consentBlockAllIndicator");

				if (consentBlockAllIndicator != null && consentBlockAllIndicator.trim().length() > 0) {
					((Z02Query) query).setPatientConsentBlockAllIndicator(new ZPD3("Y"));
				}


				// Requesting HIC (ZRP.1 -- pull data from db and add to query)
				String requestingHicProviderNo = request.getParameter("requestingHic");

				try {
					if (requestingHicProviderNo != null && requestingHicProviderNo.trim().length() > 0) {
						ProviderData provider = providerDao.find(requestingHicProviderNo);

						ZRP1 zrp1 = new ZRP1(provider.getOlisPractitionerNo(), "MDL", "ON", "HL70347",
								userPropertyDAO.getStringValue(provider.getId(),UserProperty.OFFICIAL_LAST_NAME),
								userPropertyDAO.getStringValue(provider.getId(),UserProperty.OFFICIAL_FIRST_NAME),
								userPropertyDAO.getStringValue(provider.getId(),UserProperty.OFFICIAL_SECOND_NAME));

						((Z02Query) query).setRequestingHic(zrp1);
					}
				} catch (Exception e) {
					MiscUtils.getLogger().error("Can't add requested requesting HIC data to OLIS query", e);
				}


				// Patient Identifier (PID.3 -- pull data from db and add to query)
				String demographicNo = request.getParameter("demographic");
				query.setDemographicNo(demographicNo);

				try {
					if (demographicNo != null && demographicNo.trim().length() > 0) {

						Demographic demo = demographicService.getDemographicModel(Integer.parseInt(demographicNo));
						request.setAttribute("demographic", demo);

						PID3 pid3 = new PID3(demo.getHealthNumber(), null, null, "JHN", demo.getHealthNumberProvinceCode(), "HL70347", demo.getSexString(), null);
						pid3.setValue(7, ConversionUtils.toLegacyDate(demo.getDateOfBirth()));

						((Z02Query) query).setPatientIdentifier(pid3);
					}
				} catch (Exception e) {
					MiscUtils.getLogger().error("Can't add requested patient data to OLIS query", e);
				}

				String orderingFacilityId = request.getParameter("orderingFacility");
				String placeGroupNumber = request.getParameter("placerGroupNumber");
				((Z02Query) query).setPlacerGroupNumber(new ORC4(placeGroupNumber, orderingFacilityId, "ISO"));

				// Log the consent override
				logConsentGiven(loggedInInfo.getLoggedInProviderNo(), demographicNo, request);


			} else if (queryType.equalsIgnoreCase("Z04")) {
				query = new Z04Query();

				String startTimePeriod = request.getParameter("startTimePeriod");
				String endTimePeriod = request.getParameter("endTimePeriod");

				try {
					if (startTimePeriod != null && startTimePeriod.trim().length() > 0) {
						Date startTime = DateUtils.parseDate(startTimePeriod, dateFormat);
						if (endTimePeriod != null && endTimePeriod.trim().length() > 0) {
							Date endTime = changeToEndOfDay(DateUtils.parseDate(endTimePeriod, dateFormat));

							List<Date> dateList = new LinkedList<Date>();
							dateList.add(startTime);
							dateList.add(endTime);

							OBR22 obr22 = new OBR22();
							obr22.setValue(dateList);

							((Z04Query) query).setStartEndTimestamp(obr22);
						} else {
							OBR22 obr22 = new OBR22();
							obr22.setValue(startTime);

							((Z04Query) query).setStartEndTimestamp(obr22);
						}
					}
				} catch (Exception e) {
					MiscUtils.getLogger().error("Can't parse date given for OLIS query", e);
				}


				String quantityLimitedQuery = request.getParameter("quantityLimitedQuery");
				String quantityLimit = request.getParameter("quantityLimit");

				try {
					if (quantityLimitedQuery != null && quantityLimitedQuery.trim().length() > 0) {
						// Checked
						((Z04Query) query).setQuantityLimitedRequest(new QRD7(Integer.parseInt(quantityLimit)));
					}
				} catch (Exception e) {
					MiscUtils.getLogger().error("Can't parse the number given for quantity limit in OLIS query", e);
				}


				// Requesting HIC (ZRP.1 -- pull data from db and add to query)
				String requestingHicProviderNo = request.getParameter("requestingHic");

				try {
					if (requestingHicProviderNo != null && requestingHicProviderNo.trim().length() > 0) {
						ProviderData provider = providerDao.find(requestingHicProviderNo);

						ZRP1 zrp1 = new ZRP1(provider.getOlisPractitionerNo(), "MDL", "ON", "HL70347",
								userPropertyDAO.getStringValue(provider.getId(),UserProperty.OFFICIAL_LAST_NAME),
								userPropertyDAO.getStringValue(provider.getId(),UserProperty.OFFICIAL_FIRST_NAME),
								userPropertyDAO.getStringValue(provider.getId(),UserProperty.OFFICIAL_SECOND_NAME));

						((Z04Query) query).setRequestingHic(zrp1);
					}
				} catch (Exception e) {
					MiscUtils.getLogger().error("Can't add requested requesting HIC data to OLIS query", e);
				}


				String[] testResultCodeList = request.getParameterValues("testResultCode");

				if (testResultCodeList != null) {
					for (String testResultCode : testResultCodeList) {
						((Z04Query) query).addToTestResultCodeList(new OBX3(testResultCode, "HL79902"));
					}
				}


				String[] testRequestCodeList = request.getParameterValues("testRequestCode");

				if (testRequestCodeList != null) {
					for (String testRequestCode : testRequestCodeList) {
						((Z04Query) query).addToTestRequestCodeList(new OBR4(testRequestCode, "HL79901"));
					}
				}


			} else if (queryType.equalsIgnoreCase("Z05")) {
				query = new Z05Query();


				String startTimePeriod = request.getParameter("startTimePeriod");
				String endTimePeriod = request.getParameter("endTimePeriod");

				try {
					if (startTimePeriod != null && startTimePeriod.trim().length() > 0) {
						Date startTime = DateUtils.parseDate(startTimePeriod, dateFormat);
						if (endTimePeriod != null && endTimePeriod.trim().length() > 0) {
							Date endTime = changeToEndOfDay(DateUtils.parseDate(endTimePeriod, dateFormat));

							List<Date> dateList = new LinkedList<Date>();
							dateList.add(startTime);
							dateList.add(endTime);

							OBR22 obr22 = new OBR22();
							obr22.setValue(dateList);

							((Z05Query) query).setStartEndTimestamp(obr22);
						} else {
							OBR22 obr22 = new OBR22();
							obr22.setValue(startTime);

							((Z05Query) query).setStartEndTimestamp(obr22);
						}
					}
				} catch (Exception e) {
					MiscUtils.getLogger().error("Can't parse date given for OLIS query", e);
				}


				String quantityLimitedQuery = request.getParameter("quantityLimitedQuery");
				String quantityLimit = request.getParameter("quantityLimit");

				try {
					if (quantityLimitedQuery != null && quantityLimitedQuery.trim().length() > 0) {
						// Checked
						((Z05Query) query).setQuantityLimitedRequest(new QRD7(Integer.parseInt(quantityLimit)));
					}
				} catch (Exception e) {
					MiscUtils.getLogger().error("Can't parse the number given for quantity limit in OLIS query", e);
				}


				String destinationLaboratory = request.getParameter("destinationLaboratory");

				if(StringUtils.isNotBlank(destinationLaboratory))
				{
					((Z05Query) query).setDestinationLaboratory(new ZBR8(destinationLaboratory, "ISO"));
				}

			} else if (queryType.equalsIgnoreCase("Z06")) {
				query = new Z06Query();


				String startTimePeriod = request.getParameter("startTimePeriod");
				String endTimePeriod = request.getParameter("endTimePeriod");

				try {
					if (startTimePeriod != null && startTimePeriod.trim().length() > 0) {
						Date startTime = DateUtils.parseDate(startTimePeriod, dateFormat);
						if (endTimePeriod != null && endTimePeriod.trim().length() > 0) {
							Date endTime = changeToEndOfDay(DateUtils.parseDate(endTimePeriod, dateFormat));

							List<Date> dateList = new LinkedList<Date>();
							dateList.add(startTime);
							dateList.add(endTime);

							OBR22 obr22 = new OBR22();
							obr22.setValue(dateList);

							((Z06Query) query).setStartEndTimestamp(obr22);
						} else {
							OBR22 obr22 = new OBR22();
							obr22.setValue(startTime);

							((Z06Query) query).setStartEndTimestamp(obr22);
						}
					}
				} catch (Exception e) {
					MiscUtils.getLogger().error("Can't parse date given for OLIS query", e);
				}


				String quantityLimitedQuery = request.getParameter("quantityLimitedQuery");
				String quantityLimit = request.getParameter("quantityLimit");

				try {
					if (quantityLimitedQuery != null && quantityLimitedQuery.trim().length() > 0) {
						// Checked
						((Z06Query) query).setQuantityLimitedRequest(new QRD7(Integer.parseInt(quantityLimit)));
					}
				} catch (Exception e) {
					MiscUtils.getLogger().error("Can't parse the number given for quantity limit in OLIS query", e);
				}


				String orderingFacility = request.getParameter("orderingFacility");

				if (orderingFacility != null && orderingFacility.trim().length() > 0) {
					((Z06Query) query).setOrderingFacilityId(new ORC21(orderingFacility, "^ISO"));
				}

			} else if (queryType.equalsIgnoreCase("Z07")) {
				query = new Z07Query();


				String startTimePeriod = request.getParameter("startTimePeriod");
				String endTimePeriod = request.getParameter("endTimePeriod");

				try {
					if (startTimePeriod != null && startTimePeriod.trim().length() > 0) {
						Date startTime = DateUtils.parseDate(startTimePeriod, dateFormat);
						if (endTimePeriod != null && endTimePeriod.trim().length() > 0) {
							Date endTime = changeToEndOfDay(DateUtils.parseDate(endTimePeriod, dateFormat));

							List<Date> dateList = new LinkedList<Date>();
							dateList.add(startTime);
							dateList.add(endTime);

							OBR22 obr22 = new OBR22();
							obr22.setValue(dateList);

							((Z07Query) query).setStartEndTimestamp(obr22);
						} else {
							OBR22 obr22 = new OBR22();
							obr22.setValue(startTime);

							((Z07Query) query).setStartEndTimestamp(obr22);
						}
					}
				} catch (Exception e) {
					MiscUtils.getLogger().error("Can't parse date given for OLIS query", e);
				}


				String quantityLimitedQuery = request.getParameter("quantityLimitedQuery");
				String quantityLimit = request.getParameter("quantityLimit");

				try {
					if (quantityLimitedQuery != null && quantityLimitedQuery.trim().length() > 0) {
						// Checked
						((Z07Query) query).setQuantityLimitedRequest(new QRD7(Integer.parseInt(quantityLimit)));
					}
				} catch (Exception e) {
					MiscUtils.getLogger().error("Can't parse the number given for quantity limit in OLIS query", e);
				}

			} else if (queryType.equalsIgnoreCase("Z08")) {
				query = new Z08Query();

				String startTimePeriod = request.getParameter("startTimePeriod");
				String endTimePeriod = request.getParameter("endTimePeriod");

				try {
					if (startTimePeriod != null && startTimePeriod.trim().length() > 0) {
						Date startTime = DateUtils.parseDate(startTimePeriod, dateFormat);
						if (endTimePeriod != null && endTimePeriod.trim().length() > 0) {
							Date endTime = changeToEndOfDay(DateUtils.parseDate(endTimePeriod, dateFormat));

							List<Date> dateList = new LinkedList<Date>();
							dateList.add(startTime);
							dateList.add(endTime);

							OBR22 obr22 = new OBR22();
							obr22.setValue(dateList);

							((Z08Query) query).setStartEndTimestamp(obr22);
						} else {
							OBR22 obr22 = new OBR22();
							obr22.setValue(startTime);

							((Z08Query) query).setStartEndTimestamp(obr22);
						}
					}
				} catch (Exception e) {
					MiscUtils.getLogger().error("Can't parse date given for OLIS query", e);
				}


				String quantityLimitedQuery = request.getParameter("quantityLimitedQuery");
				String quantityLimit = request.getParameter("quantityLimit");

				try {
					if (quantityLimitedQuery != null && quantityLimitedQuery.trim().length() > 0) {
						// Checked
						((Z08Query) query).setQuantityLimitedRequest(new QRD7(Integer.parseInt(quantityLimit)));
					}
				} catch (Exception e) {
					MiscUtils.getLogger().error("Can't parse the number given for quantity limit in OLIS query", e);
				}


			} else if (queryType.equalsIgnoreCase("Z50")) {
				query = new Z50Query();


				String firstName = request.getParameter("z50firstName");

				if (firstName != null && firstName.trim().length() > 0) {
					((Z50Query) query).setFirstName(new PID52(firstName));
				}


				String lastName = request.getParameter("z50lastName");

				if (lastName != null && lastName.trim().length() > 0) {
					((Z50Query) query).setLastName(new PID51(lastName));
				}


				String sex = request.getParameter("z50sex");

				if (sex != null && sex.trim().length() > 0) {
					((Z50Query) query).setSex(new PID8(sex));
				}


				String dateOfBirth = request.getParameter("z50dateOfBirth");
				try {
					if (dateOfBirth != null && dateOfBirth.trim().length() > 0) {
						PID7 pid7 = new PID7();
						pid7.setValue(DateUtils.parseDate(dateOfBirth,dateFormat));
						((Z50Query) query).setDateOfBirth(pid7);
					}
				} catch (Exception e) {
					MiscUtils.getLogger().error("Couldn't parse date given for OLIS query", e);
				}
			}
			
			String searchUuid = UUID.randomUUID().toString();
			searchQueryMap.put(searchUuid, query);
			request.setAttribute("searchUuid", searchUuid);
			if(queryType.equals("Z04") && request.getParameterValues("requestingHic") != null && request.getParameterValues("requestingHic").length>1) {
				for(String providerNo:request.getParameterValues("requestingHic")) {
					ProviderData provider = providerDao.find(providerNo);
					ZRP1 zrp1 = new ZRP1(provider.getOlisPractitionerNo(), "MDL", "ON", "HL70347",
							userPropertyDAO.getStringValue(provider.getId(),UserProperty.OFFICIAL_LAST_NAME),
							userPropertyDAO.getStringValue(provider.getId(),UserProperty.OFFICIAL_FIRST_NAME),
							userPropertyDAO.getStringValue(provider.getId(),UserProperty.OFFICIAL_SECOND_NAME));
					((Z04Query) query).setRequestingHic(zrp1);
					driverResponse = Driver.submitOLISQuery(loggedInInfo.getLoggedInProvider(), query);
				}
			}
			else {
				driverResponse = Driver.submitOLISQuery(loggedInInfo.getLoggedInProvider(), query);
			}

		}

		if(driverResponse != null)
		{
			request.setAttribute("olisResponseContent", driverResponse.getHl7Response());
			request.setAttribute("errors", driverResponse.getErrors());
			request.setAttribute("searchException", driverResponse.getSearchException());

			driverResponse.getContinuationPointer().ifPresent(s -> request.setAttribute("continuationPointer", s));
		}
		
		return mapping.findForward("results");
	
	}

	private void fillConsentOverrideSegments(HttpServletRequest request, Query query)
	{
		String blockedInformationConsent = StringUtils.trimToNull(request.getParameter("blockedInformationConsent"));
		String blockedInfoIndividual = StringUtils.trimToNull(request.getParameter("blockedInformationIndividual"));

		if(StringUtils.isNotBlank(blockedInformationConsent))
		{
			query.setConsentToViewBlockedInformation(new ZPD1(blockedInfoIndividual));

			if(CONSENT_MARKER_SUBSTITUTE.equals(blockedInfoIndividual))
			{
				String substituteGivenName = StringUtils.trimToNull(request.getParameter("substituteGivenName"));
				String substituteLastName = StringUtils.trimToNull(request.getParameter("substituteLastName"));
				String substituteRelationship = StringUtils.trimToNull(request.getParameter("substituteRelationship"));

				query.setSubstituteDecisionMakerInfo(
						new ZSD(substituteGivenName, substituteLastName, ZSD.RelationshipToPatient.valueOf(substituteRelationship)));
			}
		}
	}

	private void logConsentGiven(String loggedInProviderId, String demographicIdStr, HttpServletRequest request)
	{
		String providerNo = StringUtils.isBlank(loggedInProviderId) ? ProviderData.SYSTEM_PROVIDER_NO : loggedInProviderId;
		Integer demographicId = StringUtils.isBlank(demographicIdStr) ? null : Integer.parseInt(demographicIdStr);

		String blockedInformationConsent = StringUtils.trimToNull(request.getParameter("blockedInformationConsent"));
		String blockedInfoIndividual = StringUtils.trimToNull(request.getParameter("blockedInformationIndividual"));
		if(StringUtils.isNotBlank(blockedInformationConsent))
		{
			String givenBy = "patient";
			if(CONSENT_MARKER_SUBSTITUTE.equals(blockedInfoIndividual))
			{
				String substituteGivenName = StringUtils.trimToNull(request.getParameter("substituteGivenName"));
				String substituteLastName = StringUtils.trimToNull(request.getParameter("substituteLastName"));
				givenBy = substituteLastName + ", " + substituteGivenName;
			}

			LogAction.addLogEntry(providerNo, demographicId, LogConst.ACTION_READ, LogConst.CON_OLIS_LAB, LogConst.STATUS_SUCCESS, null, request.getRemoteAddr(),
					"consent override given by: " + givenBy);
		}
	}
	
	private Date changeToEndOfDay(Date d) {
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND,59);
		return c.getTime();
	}
}
