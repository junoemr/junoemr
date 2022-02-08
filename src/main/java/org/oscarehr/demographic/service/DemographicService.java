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
package org.oscarehr.demographic.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.oscarehr.PMmodule.service.ProgramManager;
import org.oscarehr.common.dao.AdmissionDao;
import org.oscarehr.common.dao.DemographicArchiveDao;
import org.oscarehr.common.model.Admission;
import org.oscarehr.common.model.DemographicArchive;
import org.oscarehr.demographic.converter.DemographicCreateInputToModelConverter;
import org.oscarehr.demographic.converter.DemographicDbToModelConverter;
import org.oscarehr.demographic.converter.DemographicModelToDbConverter;
import org.oscarehr.demographic.converter.DemographicUpdateInputToEntityConverter;
import org.oscarehr.demographic.dao.DemographicCustDao;
import org.oscarehr.demographic.dao.DemographicDao;
import org.oscarehr.demographic.dao.DemographicIntegrationDao;
import org.oscarehr.demographic.entity.Demographic;
import org.oscarehr.demographic.entity.DemographicCust;
import org.oscarehr.demographic.entity.DemographicExt;
import org.oscarehr.demographic.entity.DemographicIntegration;
import org.oscarehr.demographic.model.DemographicModel;
import org.oscarehr.demographic.search.DemographicCriteriaSearch;
import org.oscarehr.demographic.transfer.DemographicCreateInput;
import org.oscarehr.demographic.transfer.DemographicUpdateInput;
import org.oscarehr.demographicRoster.dao.DemographicRosterDao;
import org.oscarehr.demographicRoster.model.DemographicRoster;
import org.oscarehr.integration.service.IntegrationPushUpdateService;
import org.oscarehr.managers.DemographicManager;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.ws.external.rest.v1.conversion.DemographicConverter;
import org.oscarehr.ws.external.rest.v1.transfer.demographic.DemographicTransferInbound;
import org.oscarehr.ws.external.rest.v1.transfer.demographic.DemographicTransferOutbound;
import org.oscarehr.ws.external.soap.v1.transfer.DemographicIntegrationTransfer;
import org.oscarehr.ws.rest.to.model.DemographicSearchResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import oscar.OscarProperties;
import oscar.util.ConversionUtils;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service("demographic.service.DemographicService")
@Transactional
public class DemographicService
{
	@Autowired
	private DemographicManager demographicManager;

	@Autowired
	private DemographicDao demographicDao;

	@Autowired
	private DemographicCustDao demographicCustDao;

	// ONLY FOR LEGACY SUPPORT. DO NOT USE
	@Autowired
	private org.oscarehr.common.dao.DemographicDao legacyDemographicDao;

	@Autowired
	private DemographicIntegrationDao demographicIntegrationDao;

	@Autowired
	private DemographicArchiveDao demographicArchiveDao;

	@Autowired
	private ProgramManager programManager;

	@Autowired
	private AdmissionDao admissionDao;

	@Autowired
	private DemographicRosterDao demographicRosterDao;

	@Autowired
	private IntegrationPushUpdateService integrationPushUpdateService;

	@Autowired
	private DemographicModelToDbConverter demographicModelToDBConverter;

	@Autowired
	private DemographicDbToModelConverter demographicDbToModelConverter;

	@Autowired
	private DemographicCreateInputToModelConverter demographicCreateInputToModelConverter;

	@Autowired
	private DemographicUpdateInputToEntityConverter demographicUpdateInputToEntityConverter;

	public enum SEARCH_MODE
	{
		demographicNo, name, phone, dob, address, hin, chart_no, email
	}

	public enum STATUS_MODE
	{
		all, active, inactive,
	}

	public DemographicTransferOutbound getDemographicTransferOutbound(Integer demographicNo)
	{
		Demographic demographic = demographicDao.find(demographicNo);
		Set<DemographicExt> demoExtras = demographic.getDemographicExtSet();
		List<DemographicExt> demographicExtList = new ArrayList<>(demoExtras);
		DemographicCust demoCustom = demographic.getDemographicCust();

		return DemographicConverter.getAsTransferObject(demographic, demographicExtList, demoCustom);
	}

	public SEARCH_MODE searchModeStringToEnum(String searchMode)
	{
		switch(searchMode)
		{
			case "search_demographic_no":
				return SEARCH_MODE.demographicNo;
			case "search_name":
				return SEARCH_MODE.name;
			case "search_phone":
				return SEARCH_MODE.phone;
			case "search_dob":
				return SEARCH_MODE.dob;
			case "search_address":
				return SEARCH_MODE.address;
			case "search_hin":
				return SEARCH_MODE.hin;
			case "search_chart_no":
				return SEARCH_MODE.chart_no;
			case "search_email":
				return SEARCH_MODE.email;
		}
		return null;
	}

	/**
	 * builds a demographic criteria search
	 * @param keyword keyword to look for. has different meaning depending on search mode.
	 * @param searchMode the search mode to use @see SEARCH_MODE.
	 * @param status the type of status to search for one of (all, active, inactive)
	 * @param orderBy by which column to sort results
	 * @return a criteria search object configured to search with the above
	 */
	public DemographicCriteriaSearch buildDemographicSearch(String keyword, SEARCH_MODE searchMode, STATUS_MODE status, DemographicCriteriaSearch.SORT_MODE orderBy)
	{
		//build criteria search
		DemographicCriteriaSearch demoCS = new DemographicCriteriaSearch();
		if (keyword.contains("*"))
		{
			demoCS.setCustomWildcardsEnabled(true);
		}
		demoCS.setMatchModeAnywhere();
		demoCS.setSortMode(orderBy);

		if (searchMode == SEARCH_MODE.demographicNo)
		{
			try
			{
				Integer demoNo = Integer.parseInt(keyword);
				demoCS.setDemographicNo(demoNo);
			}
			catch (NumberFormatException nfe)
			{
				MiscUtils.getLogger().warn("could not convert demographic no to int with error: " + nfe.getMessage());
			}
		}
		else if(searchMode == SEARCH_MODE.name)
		{
			demoCS.setMatchModeStart();
			String [] names = keyword.split(",");
			if (names.length >= 2)
			{
				demoCS.setFirstName(names[1].trim());
				demoCS.setLastName(names[0].trim());
			}
			else
			{
				demoCS.setLastName(keyword.replace(",", "").trim());
			}
		}
		else if(searchMode == SEARCH_MODE.phone)
		{
			demoCS.setPhone(keyword.trim());
		}
		else if(searchMode == SEARCH_MODE.dob)
		{
			try
			{
				LocalDate dob = LocalDate.parse(keyword);
				demoCS.setDateOfBirth(dob);
			}
			catch (DateTimeParseException ex)
			{
				MiscUtils.getLogger().error(ex.getMessage());
			}
		}
		else if(searchMode == SEARCH_MODE.address)
		{
			demoCS.setAddress(keyword.trim());
		}
		else if(searchMode == SEARCH_MODE.hin)
		{
			demoCS.setMatchModeStart();
			demoCS.setHin(keyword.trim());
		}
		else if(searchMode == SEARCH_MODE.chart_no)
		{
			demoCS.setMatchModeStart();
			demoCS.setChartNo(keyword.trim());
		}
		else if(searchMode == SEARCH_MODE.email)
		{
			demoCS.setMatchModeStart();
			demoCS.setEmail(keyword.trim());
		}

		if (status != STATUS_MODE.all)
		{
			//set status mode
			List<DemographicCriteriaSearch.STATUS_MODE> demoStatuses = getInactiveStatusModeListFromOscarProps();
			demoCS.setStatusModeList(demoStatuses);

			if (status == STATUS_MODE.active)
			{
				demoCS.setNegateStatus(true);
			}
			else if (status == STATUS_MODE.inactive)
			{
				demoCS.setNegateStatus(false);
			}
		}

		return demoCS;
	}

	// build list of inactive statuses from the properties file.
	private List<DemographicCriteriaSearch.STATUS_MODE> getInactiveStatusModeListFromOscarProps()
	{
		List<DemographicCriteriaSearch.STATUS_MODE> outStatusList = new ArrayList<>();

		String inactiveStatuses= OscarProperties.getInstance().getProperty("inactive_statuses", "IN, DE, IC, ID, MO, FI");
		String[] statuses = inactiveStatuses.split(",");
		for (String status : statuses)
		{
			status = status.trim().substring(1, status.length() -1);
			switch (status)
			{
				case "IN":
					outStatusList.add(DemographicCriteriaSearch.STATUS_MODE.inactive);
					break;
				case "DE":
					outStatusList.add(DemographicCriteriaSearch.STATUS_MODE.deceased);
					break;
				case "IC":
					outStatusList.add(DemographicCriteriaSearch.STATUS_MODE.ic);
					break;
				case "ID":
					outStatusList.add(DemographicCriteriaSearch.STATUS_MODE.id);
					break;
				case "MO":
					outStatusList.add(DemographicCriteriaSearch.STATUS_MODE.moved);
					break;
				case "FI":
					outStatusList.add(DemographicCriteriaSearch.STATUS_MODE.fired);
					break;
				default:
					MiscUtils.getLogger().warn("Un-mappable status code [" + status +"] in property file!");
			}
		}

		return outStatusList;
	}

	// this needs to be wrapped in a transaction
	public List<DemographicSearchResult> toSearchResultTransferList(DemographicCriteriaSearch criteriaSearch)
	{
		List<Demographic> demographicList = demographicDao.criteriaSearch(criteriaSearch);
		List<DemographicSearchResult> transferList = new ArrayList<>(demographicList.size());
		for(Demographic demographic : demographicList)
		{
			transferList.add(toSearchResultTransfer(demographic));
		}
		return transferList;
	}

	private DemographicSearchResult toSearchResultTransfer(Demographic demographic)
	{
		DemographicSearchResult result = new DemographicSearchResult();

		result.setDemographicNo(demographic.getDemographicId());
		result.setDob(ConversionUtils.toLegacyDate(demographic.getDateOfBirth()));
		result.setEmail(demographic.getEmail());
		result.setChartNo(demographic.getChartNo());
		result.setFirstName(demographic.getFirstName());
		result.setLastName(demographic.getLastName());
		result.setHin(demographic.getHin());
		result.setPatientStatus(demographic.getPatientStatus());
		result.setSex(demographic.getSex());
		result.setPhone(demographic.getPhone());
		result.setProviderNo(demographic.getProviderNo());
		result.setRosterStatus(demographic.getRosterStatus());

		ProviderData provider = demographic.getProvider();
		if(provider != null)
		{
			result.setProviderName(demographic.getProvider().getDisplayName());
		}

		return result;
	}

	public DemographicModel getDemographic(Integer id)
	{
		return demographicDbToModelConverter.convert(demographicDao.find(id));
	}

	/**
	 * Create a modern demographic object from a legacy demographic object.
	 * @param legacyDemographic - legacy demographic object to convert
	 * @return - modern demographic object
	 */
	public Demographic demographicFromLegacyDemographic(org.oscarehr.common.model.Demographic legacyDemographic)
	{
		Demographic demographic = new Demographic();
		String[] ignoreList = {"demographicNo", "dateOfBirth"};

		BeanUtils.copyProperties(legacyDemographic, demographic, ignoreList);
		demographic.setDemographicId(legacyDemographic.getDemographicNo());
		demographic.setDayOfBirth(legacyDemographic.getDateOfBirth());

		return demographic;
	}

	public Demographic addNewDemographicRecord(String providerNoStr, DemographicTransferInbound demographicTransferInbound)
	{
		Demographic demographic = DemographicConverter.getAsDomainObject(demographicTransferInbound);
		DemographicCust demoCustom = DemographicConverter.getCustom(demographicTransferInbound);
		List<DemographicExt> demographicExtensions = DemographicConverter.getExtensionList(demographicTransferInbound);
		Set<DemographicExt> demographicExtSet = new HashSet<>(demographicExtensions);

		return addNewDemographicRecord(providerNoStr, demographic, demoCustom, demographicExtSet);
	}
	public DemographicModel addNewDemographicRecord(String providerNoStr, DemographicCreateInput demographicInput)
	{
		DemographicModel model = demographicCreateInputToModelConverter.convert(demographicInput);
		return demographicDbToModelConverter.convert(addNewDemographicRecord(providerNoStr, model));
	}
	public Demographic addNewDemographicRecord(String providerNoStr, DemographicModel demographicModel)
	{
		Demographic demographic = demographicModelToDBConverter.convert(demographicModel);
		Set<DemographicExt> demographicExtSet = demographic.getDemographicExtSet();
		DemographicCust demoCustom = demographic.getDemographicCust();

		return addNewDemographicRecord(providerNoStr, demographic, demoCustom, demographicExtSet);
	}

	public Demographic addNewDemographicRecord(String providerNoStr, Demographic demographic,
	                                    DemographicCust demoCustom, Set<DemographicExt> demographicExtensions)
	{
		// save the base demographic object
		addNewDemographicRecord(providerNoStr, demographic);
		Integer demographicNo = demographic.getDemographicId();

		if(demoCustom != null)
		{
			// save the custom fields
			demoCustom.setId(demographicNo);
			demoCustom.setDemographic(demographic);
			demographicCustDao.persist(demoCustom);
		}
		for(DemographicExt extension : demographicExtensions)
		{
			//save the extension fields
			extension.setDemographicNo(demographicNo);
			extension.setProviderNo(providerNoStr);
			demographicManager.createExtension(providerNoStr, extension);
		}
		
		List<DemographicRoster> rosterHistory = demographic.getRosterHistory();
		if (rosterHistory != null)
		{
			for (DemographicRoster rosterItem : rosterHistory)
			{
				rosterItem.setDemographicId(demographicNo);
				demographicRosterDao.persist(rosterItem);
			}
		}
		
		return demographic;
	}

	private void addNewDemographicRecord(String providerNoStr, Demographic demographic)
	{
		addNewDemographicRecord(providerNoStr, demographic, programManager.getDefaultProgramId());
	}
	private void addNewDemographicRecord(String providerNoStr, Demographic demographic, Integer programId)
	{
		/* set some default values */
		demographic.setLastUpdateDate(new Date());
		demographic.setLastUpdateUser(providerNoStr);
		if(demographic.getPatientStatus() == null)
		{
			demographic.setPatientStatus(org.oscarehr.common.model.Demographic.PatientStatus.AC.name());
		}
		if(demographic.getReferralDoctor() == null)
		{
			demographic.setReferralDoctor("<rdohip></rdohip><rd></rd>");
		}
		demographicDao.persist(demographic);

		Admission admission = new Admission();
		admission.setClientId(demographic.getDemographicId());
		admission.setProgramId(programId);
		admission.setProviderNo(providerNoStr);
		admission.setAdmissionDate(new Date());
		admission.setAdmissionStatus(Admission.STATUS_CURRENT);
		admission.setAdmissionNotes("");

		admissionDao.saveAdmission(admission);
	}

	public Long archiveDemographicRecord(Demographic demographic)
	{
		DemographicArchive da = new DemographicArchive(demographic);
		demographicArchiveDao.persist(da);
		return da.getId();
	}

	/**
	 * Apply a demographic update (save changes to demo + create a demographic archive record)
	 * @param demographic - the demographic to update.
	 * @param loggedInInfo - logged in info
	 * @return - the updated demographic (un changed object, save that it has been persisted to the database)
	 */
	public Demographic updateDemographicRecord(Demographic demographic, LoggedInInfo loggedInInfo)
	{
		Demographic oldDemographic = demographicDao.find(demographic.getId());
		archiveDemographicRecord(oldDemographic);

		queueMHAPatientUpdates(demographic, oldDemographic, loggedInInfo);

		demographicDao.merge(demographic);
		return demographic;
	}

	public DemographicModel updateDemographicRecord(DemographicUpdateInput updateInput, LoggedInInfo loggedInInfo)
	{
		Demographic oldDemographic = demographicDao.find(updateInput.getId());
		archiveDemographicRecord(oldDemographic);

		Demographic demographic = demographicUpdateInputToEntityConverter.convert(updateInput);
		demographic.setDemographicId(updateInput.getId());

		queueMHAPatientUpdates(demographic, oldDemographic, loggedInInfo);

		demographicDao.merge(demographic);
		return demographicDbToModelConverter.convert(demographic);
	}

	/**
	 * DO NOT USE IN NEW CODE.
	 * Provides legacy saving support for old demographic objects
	 * @param demographic - legacy demographic to save
	 * @param extensions - extension entries to archive.
	 * @param loggedInInfo - logged in info
	 * @return - the save demographic
	 */
	public org.oscarehr.common.model.Demographic updateLegacyDemographicRecord(org.oscarehr.common.model.Demographic demographic,
																			   List<DemographicExt> extensions,
																			   LoggedInInfo loggedInInfo)
	{
		org.oscarehr.common.model.Demographic oldDemographic = legacyDemographicDao.getDemographicById(demographic.getDemographicNo());
		Long archiveId = demographicArchiveDao.archiveRecord(oldDemographic);
		legacyDemographicDao.save(demographic);
		demographicManager.saveAndArchiveDemographicExt(archiveId, extensions);

		queueMHAPatientUpdates(demographic, oldDemographic, loggedInInfo);

		return demographic;
	}

	public void addDemographicIntegrationRecord(Integer demographicNo, DemographicIntegrationTransfer transfer)
	{
		DemographicIntegration integrationRecord = new DemographicIntegration();
		integrationRecord.setDemographicNo(demographicNo);
		integrationRecord.setIntegrationType(transfer.getIntegrationType());
		integrationRecord.setCreatedBySource(transfer.getCreatedBySource());
		integrationRecord.setCreatedByRemoteId(transfer.getCreatedByRemoteId());
		integrationRecord.setRemoteId(transfer.getRemoteId());
		integrationRecord.setCreatedAt(new Date());
		integrationRecord.setUpdatedAt(new Date());

		demographicIntegrationDao.persist(integrationRecord);
	}

	/**
	 * compatibility wrapper for queueMHAPatientUpdates that converts legacy demographics.
	 * @see #queueMHAPatientUpdates
	 */
	public void queueMHAPatientUpdates(org.oscarehr.common.model.Demographic updatedDemographic,
									   org.oscarehr.common.model.Demographic oldDemographic,
									   LoggedInInfo loggedInInfo)
	{
		queueMHAPatientUpdates(demographicFromLegacyDemographic(updatedDemographic),
						demographicFromLegacyDemographic(oldDemographic), loggedInInfo);
	}

	/**
	 * queue any necessary MHA push updates based on the change of the demographic record.
	 * @param updatedDemographic - the updated demographic record
	 * @param oldDemographic - the old demographic record
	 * @param loggedInInfo - logged in info
	 */
	public void queueMHAPatientUpdates(Demographic updatedDemographic, Demographic oldDemographic, LoggedInInfo loggedInInfo)
	{
		queueMHAPatientUpdates(updatedDemographic, oldDemographic.getPatientStatus(), loggedInInfo);
	}

	public void queueMHAPatientUpdates(Demographic updatedDemographic, String previousPatientStatus, LoggedInInfo loggedInInfo)
	{
		if (!updatedDemographic.getPatientStatus().equals(previousPatientStatus))
		{// patient status change
			updateMHAPatientConnectionStatus(updatedDemographic.getId(), loggedInInfo, !updatedDemographic.isActive());
		}
	}

	/**
	 * Update MHA patient connection status with clinic.
	 * @param demographicId - the demographic to update
	 * @param loggedInInfo - logged in info
	 * @param rejected - whether or not to reject / un-reject the patient's connection
	 */
	public void updateMHAPatientConnectionStatus(Integer demographicId, LoggedInInfo loggedInInfo, Boolean rejected)
	{
		try
		{
			integrationPushUpdateService.queuePatientConnectionUpdate(loggedInInfo.getLoggedInSecurity().getSecurityNo(),
							demographicId, rejected);
		}
		catch (JsonProcessingException e)
		{
			MiscUtils.getLogger().error("Error queuing MHA patient connection update, " + e.getMessage(), e);
		}
	}
}
