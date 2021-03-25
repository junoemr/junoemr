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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.oscarehr.PMmodule.service.ProgramManager;
import org.oscarehr.common.Gender;
import org.oscarehr.common.dao.AdmissionDao;
import org.oscarehr.common.dao.DemographicArchiveDao;
import org.oscarehr.common.dao.DemographicContactDao;
import org.oscarehr.common.dao.DemographicDao;
import org.oscarehr.common.dao.PHRVerificationDao;
import org.oscarehr.common.exception.PatientDirectiveException;
import org.oscarehr.common.model.Admission;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.common.model.Demographic.PatientStatus;
import org.oscarehr.common.model.DemographicContact;
import org.oscarehr.common.model.PHRVerification;
import org.oscarehr.common.model.Provider;
import org.oscarehr.demographic.dao.DemographicCustArchiveDao;
import org.oscarehr.demographic.dao.DemographicCustDao;
import org.oscarehr.demographic.dao.DemographicExtArchiveDao;
import org.oscarehr.demographic.dao.DemographicExtDao;
import org.oscarehr.demographic.dao.DemographicMergedDao;
import org.oscarehr.demographic.model.DemographicCust;
import org.oscarehr.demographic.model.DemographicExt;
import org.oscarehr.demographic.model.DemographicExtArchive;
import org.oscarehr.demographic.model.DemographicMerged;
import org.oscarehr.demographic.service.DemographicService;
import org.oscarehr.demographic.service.HinValidationService;
import org.oscarehr.provider.dao.RecentDemographicAccessDao;
import org.oscarehr.provider.model.RecentDemographicAccess;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.ws.external.soap.v1.transfer.DemographicTransfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import oscar.log.LogAction;
import oscar.log.LogConst;
import oscar.util.ConversionUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Will provide access to demographic data, as well as closely related data such as 
 * extensions (DemographicExt), merge data, archive data, etc.
 * 
 * Future Use: Add privacy, security, and consent profiles
 * 
 *
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class DemographicManager {
	public static final String PHR_VERIFICATION_LEVEL_3 = "+3";
	public static final String PHR_VERIFICATION_LEVEL_2 = "+2";
	public static final String PHR_VERIFICATION_LEVEL_1 = "+1";

	//region ValidationErrorMessages
	public static final String FIRST_NAME_REQUIRED = "firstName is a required field.  ";
	public static final String LAST_NAME_REQUIRED = "lastName is a required field.  ";
	public static final String SEX_REQUIRED = "sex is a required field.  ";
	public static final String SEX_INVALID = "sex must be either \"M\" or \"F\".  ";
	public static final String YEAR_OF_BIRTH_REQUIRED = "yearOfBirth is a required field.  ";
	public static final String YEAR_OF_BIRTH_NUMERIC = "yearOfBirth should be a numeric value. ";
	public static final String YEAR_OF_BIRTH_4_DIGIT = "yearOfBirth is expected to be a 4-digit number.";
	public static final String MONTH_OF_BIRTH_REQUIRED = "monthOfBirth is a required field.  ";
	public static final String MONTH_OF_BIRTH_INVALID = "monthOfBirth should be a number between 1 and 12. ";
	public static final String DATE_OF_BIRTH_REQUIRED = "dateOfBirth is a required field.  ";
	public static final String DATE_OF_BIRTH_INVALID = "dateOfBirth should be a number between 1 and 31 (depending on month).";
	public static final String BIRTHDAY_INVALID = "Need a valid birth date.";
	public static final String FAMILY_DOCTOR_INVALID = "familyDoctor is formatted incorrectly.  It must " +
			"be a string like <rdohip>{referral doctor number}" +
			"</rdohip><rd>{last name},{first name}</rd>.  " +
			"Also no other tags and no quotes, line breaks " +
			"or semicolons are allowed.";
	public static final String FAMILY_DOCTOR_2_INVALID = "familyDoctor2 is formatted incorrectly.  It must " +
			"be a string like <fd>{family doctor number}" +
			"</fd><fdname>{last name},{first name}</fdname>.  " +
			"Also no other tags and no quotes, line breaks " +
			"or semicolons are allowed.";
	public static final String FIELD_UNSAFE = "No html tags and no quotes, line breaks " +
			"or semicolons are allowed.";
	//endregion

	private static Logger logger = MiscUtils.getLogger();

	@Autowired
	private DemographicDao demographicDao;
	@Autowired
	private DemographicExtDao demographicExtDao;
	@Autowired
	private DemographicCustDao demographicCustDao;
	@Autowired
	private DemographicContactDao demographicContactDao;

	@Autowired
	private DemographicArchiveDao demographicArchiveDao;
	@Autowired
	private DemographicExtArchiveDao demographicExtArchiveDao;
	@Autowired
	private DemographicCustArchiveDao demographicCustArchiveDao;

	@Autowired
	private DemographicMergedDao demographicMergedDao;

	@Autowired
	private DemographicService demographicService;

	@Autowired
	private PHRVerificationDao phrVerificationDao;

	@Autowired
	private AdmissionDao admissionDao;
	
	@Autowired
	private SecurityInfoManager securityInfoManager;

	@Autowired
	private RecentDemographicAccessDao recentDemographicAccessDao;

	@Autowired
	private ProgramManager programManager;

	@Autowired
	private HinValidationService hinValidationService;

	@Deprecated
	public Demographic getDemographic(LoggedInInfo loggedInInfo, Integer demographicId) throws PatientDirectiveException {
		return getDemographic(loggedInInfo.getLoggedInProviderNo() , demographicId);
	}
	public Demographic getDemographic(String providerNo, Integer demographicId) throws PatientDirectiveException {
		checkPrivilege(providerNo, SecurityInfoManager.READ, demographicId);

		return demographicDao.getDemographicById(demographicId);
	}
		
	public Demographic getDemographic(LoggedInInfo loggedInInfo, String demographicNo) {
		checkPrivilege(loggedInInfo, SecurityInfoManager.READ);
		Integer demographicId = null;
		try {
			demographicId = Integer.parseInt(demographicNo);
		} catch(NumberFormatException e) {
			return null;
		}
		return getDemographic(loggedInInfo,demographicId);
		
	}
	
	
	public Demographic getDemographicWithExt(LoggedInInfo loggedInInfo, Integer demographicId) {
		checkPrivilege(loggedInInfo, SecurityInfoManager.READ);
		Demographic result = getDemographic(loggedInInfo, demographicId);
		if (result!=null) {
			List<DemographicExt> demoExts = getDemographicExts(loggedInInfo,demographicId);
			if (demoExts!=null && !demoExts.isEmpty()) {
				DemographicExt[] demoExtArray = demoExts.toArray(new DemographicExt[demoExts.size()]);
				result.setExtras(demoExtArray);
			}
		}
		return result;
	}

	public String getDemographicFormattedName(LoggedInInfo loggedInInfo, Integer demographicId) {
		Demographic result = getDemographic(loggedInInfo, demographicId);
		String name = null;
		if (result != null) {
			name = result.getLastName() + ", " + result.getFirstName();
		}
		return (name);
	}

	public Demographic getDemographicByMyOscarUserName(LoggedInInfo loggedInInfo, String myOscarUserName) {
		checkPrivilege(loggedInInfo, SecurityInfoManager.READ);
		return demographicDao.getDemographicByMyOscarUserName(myOscarUserName);
	}

	public List getDemographicsByHealthNum(String hin)
	{
		return demographicDao.getDemographicsByHealthNum(hin);
	}

	public List<Demographic> searchDemographicByName(LoggedInInfo loggedInInfo, String searchString, int startIndex, int itemsToReturn) {
		checkPrivilege(loggedInInfo, SecurityInfoManager.READ);
		
		List<Demographic> results = demographicDao.searchDemographicByNameString(searchString, startIndex, itemsToReturn);

		if (logger.isDebugEnabled()) {
			logger.debug("searchDemographicByName, searchString=" + searchString + ", result.size=" + results.size());
		}

		return (results);
	}

	public List<DemographicExt> getDemographicExts(LoggedInInfo loggedInInfo, Integer id)
	{
		return getDemographicExts(loggedInInfo.getLoggedInProviderNo(), id);
	}
	public List<DemographicExt> getDemographicExts(String providerNo, Integer id)
	{
		checkPrivilege(providerNo, SecurityInfoManager.READ);
		return demographicExtDao.getDemographicExtByDemographicNo(id);
	}

	public DemographicExt getDemographicExt(LoggedInInfo loggedInInfo, Integer demographicNo, String key) {
		checkPrivilege(loggedInInfo, SecurityInfoManager.READ);
		return demographicExtDao.getDemographicExt(demographicNo, key);
	}

	public DemographicCust getDemographicCust(LoggedInInfo loggedInInfo, Integer id)
	{
		return getDemographicCust(loggedInInfo.getLoggedInProviderNo(), id);
	}
	public DemographicCust getDemographicCust(String providerNo, Integer id)
	{
		checkPrivilege(providerNo, SecurityInfoManager.READ);
		return demographicCustDao.find(id);
	}

	public void createUpdateDemographicCust(LoggedInInfo loggedInInfo, DemographicCust demoCust)
	{
		createUpdateDemographicCust(loggedInInfo.getLoggedInProviderNo(), demoCust);
	}

	public void createUpdateDemographicCust(String providerNo, DemographicCust demoCust)
	{
		checkPrivilege(providerNo, SecurityInfoManager.WRITE);
		if(demoCust != null)
		{
			//Archive previous demoCust
			DemographicCust prevCust = demographicCustDao.find(demoCust.getId());
			if(prevCust != null)
			{
				if(!(StringUtils.equals(prevCust.getAlert(), demoCust.getAlert()) &&
						StringUtils.equals(prevCust.getMidwife(), demoCust.getMidwife()) &&
						StringUtils.equals(prevCust.getNurse(), demoCust.getNurse()) &&
						StringUtils.equals(prevCust.getResident(), demoCust.getResident()) &&
						StringUtils.equals(prevCust.getNotes(), demoCust.getNotes())))
				{
					demographicCustArchiveDao.archiveDemographicCust(prevCust);
				}
			}

			demographicCustDao.merge(demoCust);
		}
	}

	public List<DemographicContact> getDemographicContacts(LoggedInInfo loggedInInfo, Integer id) {
		checkPrivilege(loggedInInfo, SecurityInfoManager.READ);
		return demographicContactDao.findActiveByDemographicNo(id);
	}

	/**
	 * Given a demographic and the type of contact, get all active demographic contact entries
	 * @param loggedInInfo user's logged in information so we can authenticate
	 * @param demographicNo demographic to get contacts for
	 * @param type the type of contact to pull
	 * @return list of matching DemographicContact entries
	 */
	public List<DemographicContact> getDemographicContactsByType(LoggedInInfo loggedInInfo, Integer demographicNo, Integer type)
	{
		checkPrivilege(loggedInInfo, SecurityInfoManager.READ);
		return demographicContactDao.findByDemographicNoAndType(demographicNo, type);
	}

	public List<Demographic> getDemographicsByProvider(LoggedInInfo loggedInInfo, Provider provider) {
		checkPrivilege(loggedInInfo, SecurityInfoManager.READ);
		List<Demographic> result = demographicDao.getDemographicByProvider(provider.getProviderNo(), true);

		return result;
	}

	public void createDemographic(LoggedInInfo loggedInInfo, Demographic demographic, Integer admissionProgramId)
	{
		createDemographic(loggedInInfo.getLoggedInProviderNo(), demographic, admissionProgramId);
	}
	public void createDemographic(String providerNo, Demographic demographic, Integer admissionProgramId) {
		checkPrivilege(providerNo, SecurityInfoManager.WRITE);
		try {
			demographic.getBirthDay();
		} catch (Exception e) {
			throw new IllegalArgumentException("Birth date was specified for " + demographic.getFullName() + ": " + demographic.getBirthDayAsString());
		}

		if(demographic.getPatientStatus() == null)
		{
			demographic.setPatientStatus(PatientStatus.AC.name());
		}
		if(demographic.getFamilyDoctor().isEmpty())
		{
			demographic.setFamilyDoctor("<rdohip></rdohip><rd></rd>");
		}
		if (demographic.getFamilyDoctor2() == null)
		{
			demographic.setFamilyDoctor2("<fd></fd><fdname></fdname>");
		}
		demographic.setPhone(oscar.util.StringUtils.filterControlCharacters(demographic.getPhone()));
		demographic.setPhone2(oscar.util.StringUtils.filterControlCharacters(demographic.getPhone2()));
		demographic.setLastUpdateUser(providerNo);
		demographicDao.save(demographic);

		Admission admission = new Admission();
		admission.setClientId(demographic.getDemographicNo());
		admission.setProgramId(admissionProgramId);
		admission.setProviderNo(providerNo);
		admission.setAdmissionDate(new Date());
		admission.setAdmissionStatus(Admission.STATUS_CURRENT);
		admission.setAdmissionNotes("");

		admissionDao.saveAdmission(admission);

		if (demographic.getExtras() != null) {
			for (DemographicExt ext : demographic.getExtras()) {
				createExtension(providerNo, ext);
			}
		}
	}

	public void updateDemographic(LoggedInInfo loggedInInfo, Demographic demographic) {
		checkPrivilege(loggedInInfo, SecurityInfoManager.UPDATE);
		try {
			demographic.getBirthDay();
		} catch (Exception e) {
			throw new IllegalArgumentException("Birth date was specified for " + demographic.getFullName() + ": " + demographic.getBirthDayAsString());
		}

		//Archive previous demo
		Demographic prevDemo = demographicDao.getDemographicById(demographic.getDemographicNo());
		demographicArchiveDao.archiveRecord(prevDemo);

		String previousStatus = prevDemo.getPatientStatus();
		Date previousStatusDate = prevDemo.getPatientStatusDate();
		String currentStatus = demographic.getPatientStatus();
		Date currentStatusDate = demographic.getPatientStatusDate();

		if (!(previousStatus.equals(currentStatus)))
		{
			demographic.setPatientStatusDate(new Date());
		}
		else if (previousStatusDate.compareTo(currentStatusDate) != 0)
		{
			demographic.setPatientStatusDate(currentStatusDate);
		}

		// update messaging consent timestamps
		updateElectronicMessagingConsentTimestamps(demographic, prevDemo, loggedInInfo);

		//retain merge info
		demographic.setSubRecord(prevDemo.getSubRecord());
		
		//save current demo
		demographic.setLastUpdateUser(loggedInInfo.getLoggedInProviderNo());
		//remove control characters for existing records.
		demographic.setPhone(oscar.util.StringUtils.filterControlCharacters(demographic.getPhone()));
		demographic.setPhone2(oscar.util.StringUtils.filterControlCharacters(demographic.getPhone2()));
		demographicDao.save(demographic);

		// update MyHealthAccess connection status.
		demographicService.queueMHAPatientUpdates(demographic, prevDemo, loggedInInfo);

		if (demographic.getExtras() != null) {
			for (DemographicExt ext : demographic.getExtras()) {

				DemographicExt existingExt = demographicExtDao.getLatestDemographicExt(demographic.getDemographicNo(), ext.getKey());
				if (existingExt != null)
				{
					ext.setId(existingExt.getId());
				}

				updateExtension(loggedInInfo, ext);
			}
		}
	}
	
	public void addDemographic(LoggedInInfo loggedInInfo, Demographic demographic) {
		checkPrivilege(loggedInInfo, SecurityInfoManager.WRITE);
		try {
			demographic.getBirthDay();
		} catch (Exception e) {
			throw new IllegalArgumentException("Birth date was specified for " + demographic.getFullName() + ": " + demographic.getBirthDayAsString());
		}

		//save current demo
		demographic.setLastUpdateUser(loggedInInfo.getLoggedInProviderNo());
		demographicDao.save(demographic);

		if (demographic.getExtras() != null) {
			for (DemographicExt ext : demographic.getExtras()) {
				updateExtension(loggedInInfo, ext);
			}
		}
	}
	

	public void createExtension(String providerNo, DemographicExt ext) {
		checkPrivilege(providerNo, SecurityInfoManager.WRITE);
		demographicExtDao.saveEntity(ext);
	}

	public void updateExtension(LoggedInInfo loggedInInfo, DemographicExt ext) {
		checkPrivilege(loggedInInfo, SecurityInfoManager.UPDATE);
		archiveExtension(ext);
		demographicExtDao.saveEntity(ext);
	}

	public void archiveExtension(DemographicExt ext) {
		//TODO: this needs a loggedInInfo
		if (ext != null && ext.getId() != null) {
			DemographicExt prevExt = demographicExtDao.find(ext.getId());
			if (!(ext.getKey().equals(prevExt.getKey()) && ext.getValue().equals(prevExt.getValue()))) {
				demographicExtArchiveDao.archiveDemographicExt(prevExt);
			}
		}
	}

	/**
	 * Saves the list of demographicExt objects to the ext database and ext archive
	 * @param demographicArchiveId - id of the archived demographic record
	 * @param extensions - list of objects to update/insert
	 */
	public void saveAndArchiveDemographicExt(Long demographicArchiveId, List<DemographicExt> extensions)
	{
		for(DemographicExt extension : extensions)
		{
			// update/insert extension entries
			demographicExtDao.saveEntity(extension);

			// save the demographic extension in the archive
			DemographicExtArchive archive = new DemographicExtArchive(extension);
			archive.setArchiveId(demographicArchiveId);
			demographicExtArchiveDao.persist(archive);
		}
	}

	public void createUpdateDemographicContact(LoggedInInfo loggedInInfo, DemographicContact demoContact) {
		checkPrivilege(loggedInInfo, SecurityInfoManager.WRITE);
		
		demographicContactDao.merge(demoContact);
	}

	public void deleteDemographic(LoggedInInfo loggedInInfo, Demographic demographic) {
		checkPrivilege(loggedInInfo, SecurityInfoManager.WRITE);
		
		demographicArchiveDao.archiveRecord(demographic);
		demographic.setPatientStatus(Demographic.PatientStatus.DE.name());
		demographic.setLastUpdateUser(loggedInInfo.getLoggedInProviderNo());
		demographicDao.save(demographic);

		for (DemographicExt ext : getDemographicExts(loggedInInfo, demographic.getDemographicNo())) {
			deleteExtension(loggedInInfo, ext);
		}
	}

	public void deleteExtension(LoggedInInfo loggedInInfo, DemographicExt ext) {
		checkPrivilege(loggedInInfo, SecurityInfoManager.WRITE);
		archiveExtension(ext);
		demographicExtDao.removeDemographicExt(ext.getId());
	}

	public void mergeDemographics(LoggedInInfo loggedInInfo, Integer parentId, List<Integer> children) {
		checkPrivilege(loggedInInfo, SecurityInfoManager.WRITE);
		for (Integer child : children) {
			DemographicMerged dm = new DemographicMerged();
			dm.setDemographicNo(child);
			dm.setMergedTo(parentId);
			demographicMergedDao.persist(dm);
		}

	}

	public void unmergeDemographics(LoggedInInfo loggedInInfo, Integer parentId, List<Integer> children)
	{
		checkPrivilege(loggedInInfo, SecurityInfoManager.WRITE);
		for (Integer childId : children)
		{
			List<DemographicMerged> demographicsMerged = demographicMergedDao.findByParentAndChildIds(parentId, childId);
			if (demographicsMerged.isEmpty())
			{
				throw new IllegalArgumentException("Unable to find merge record for parent " + parentId + " and child " + childId);
			}
			for (DemographicMerged dm : demographicsMerged)
			{
				// Update the demographicMerged entry to be deleted
				dm.delete();
				demographicMergedDao.merge(dm);
				// Add a log entry to indicate who did this
				LogAction.addLogEntry(loggedInInfo.getLoggedInProviderNo(),
						parentId,
						LogConst.ACTION_DELETE,
						LogConst.CON_DEMOGRAPHIC_MERGE,
						LogConst.STATUS_SUCCESS,
						"parentDemographic=" + parentId,
						loggedInInfo.getIp(),
						"mergedDemographic=" + childId);
			}
		}
	}

	public Long getActiveDemographicCount(LoggedInInfo loggedInInfo) {
		checkPrivilege(loggedInInfo, SecurityInfoManager.READ);
		return demographicDao.getActiveDemographicCount();
	}

	public List<Demographic> getActiveDemographics(LoggedInInfo loggedInInfo, int offset, int limit) {
		checkPrivilege(loggedInInfo, SecurityInfoManager.READ);
		return demographicDao.getActiveDemographics(offset, limit);
	}

	/**
	 * Gets all merged demographic for the specified parent record ID 
	 * 
	 * @param parentId
	 * 		ID of the parent demographic record 
	 * @return
	 * 		Returns all merged demographic records for the specified parent id.
	 */
	public List<DemographicMerged> getMergedDemographics(LoggedInInfo loggedInInfo, Integer parentId) {
		checkPrivilege(loggedInInfo, SecurityInfoManager.READ);
		return demographicMergedDao.findCurrentByMergedTo(parentId);
	}

	public PHRVerification getLatestPhrVerificationByDemographicId(LoggedInInfo loggedInInfo, Integer demographicId) {
		return phrVerificationDao.findLatestByDemographicId(demographicId);
	}

	public String getPhrVerificationLevelByDemographicId(LoggedInInfo loggedInInfo, Integer demographicId) {
		PHRVerification phrVerification = getLatestPhrVerificationByDemographicId(loggedInInfo, demographicId);

		if (phrVerification != null) {
			String authLevel = phrVerification.getVerificationLevel();
			if (PHRVerification.VERIFICATION_METHOD_FAX.equals(authLevel) || PHRVerification.VERIFICATION_METHOD_MAIL.equals(authLevel) || PHRVerification.VERIFICATION_METHOD_EMAIL.equals(authLevel)) {
				return PHR_VERIFICATION_LEVEL_1;
			} else if (PHRVerification.VERIFICATION_METHOD_TEL.equals(authLevel) || PHRVerification.VERIFICATION_METHOD_VIDEOPHONE.equals(authLevel)) {
				return PHR_VERIFICATION_LEVEL_2;
			} else if (PHRVerification.VERIFICATION_METHOD_INPERSON.equals(authLevel)) {
				return PHR_VERIFICATION_LEVEL_3;
			}
		}

		// blank string because preserving existing behaviour moved from PHRVerificationDao, I would have preferred returnning null on a new method...
		return ("");
	}

	/**
	 * This method should only return true if the demographic passed in is "phr verified" to a sufficient level to allow a provider to send this phr account messages.
	 */
	public boolean isPhrVerifiedToSendMessages(LoggedInInfo loggedInInfo, Integer demographicId) {
		String level = getPhrVerificationLevelByDemographicId(loggedInInfo, demographicId);
		// hard coded to 3 until some one tells me how to configure/check this
		if (PHR_VERIFICATION_LEVEL_3.equals(level))
		{
			return (true);
		}
		else
		{
			return (false);
		}
	}

	/**
	 * This method should only return true if the demographic passed in is "phr verified" to a sufficient level to allow a provider to send this phr account medicalData.
	 */
	public boolean isPhrVerifiedToSendMedicalData(LoggedInInfo loggedInInfo, Integer demographicId) {
		String level = getPhrVerificationLevelByDemographicId(loggedInInfo, demographicId);
		// hard coded to 3 until some one tells me how to configure/check this
		if (PHR_VERIFICATION_LEVEL_3.equals(level))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * @deprecated there should be a generic call for getDemographicExt(Integer demoId, String key) instead. Then the caller should assemble what it needs from the demographic and ext call itself.
	 */
	public String getDemographicWorkPhoneAndExtension(LoggedInInfo loggedInInfo, Integer demographicNo) {

		Demographic result = demographicDao.getDemographicById(demographicNo);
		String workPhone = result.getPhone2();
		if (workPhone != null && workPhone.length() > 0) {
			String value = demographicExtDao.getValueForDemoKey(demographicNo, "wPhoneExt");
			if (value != null && value.length() > 0) {
				workPhone += "x" + value;
			}
		}

		return (workPhone);
	}

	/**
	 * see DemographicDao.findByAttributes for parameter details
	 */
	public List<Demographic> searchDemographicsByAttributes(LoggedInInfo loggedInInfo, String hin, String firstName, String lastName, Gender gender, Calendar dateOfBirth, String city, String province, String phone, String email, String alias, int startIndex, int itemsToReturn) {
		checkPrivilege(loggedInInfo, SecurityInfoManager.READ);
		return demographicDao.findByAttributes(hin, firstName, lastName, gender, dateOfBirth, city, province, phone, email, alias, startIndex, itemsToReturn);
	}

	public List<String> getPatientStatusList() {
		return demographicDao.search_ptstatus();
	}

	public List<String> getRosterStatusList() {
		return demographicDao.getRosterStatuses();
	}

	/**
	 * programId can be null for all/any program
	 */
	public List<Integer> getAdmittedDemographicIdsByProgramAndProvider(LoggedInInfo loggedInInfo, Integer programId, String providerNo) {
		if (loggedInInfo == null)
		{
			throw (new SecurityException("user not logged in?"));
		}

		List<Integer> demographicIds = admissionDao.getAdmittedDemographicIdByProgramAndProvider(programId, providerNo);

		return (demographicIds);
	}
	
	public List<Integer> getDemographicIdsWithMyOscarAccounts(LoggedInInfo loggedInInfo, Integer startDemographicIdExclusive, int itemsToReturn) {
		if (loggedInInfo == null)
		{
			throw (new SecurityException("user not logged in?"));
		}

		List<Integer> demographicIds = demographicDao.getDemographicIdsWithMyOscarAccounts(startDemographicIdExclusive, itemsToReturn);

		return (demographicIds);
	}

	public List<Demographic> getDemographics(LoggedInInfo loggedInInfo, List<Integer> demographicIds) {
		checkPrivilege(loggedInInfo, SecurityInfoManager.READ);
		
		if (loggedInInfo == null)
		{
			throw (new SecurityException("user not logged in?"));
		}

		List<Demographic> demographics = demographicDao.getDemographics(demographicIds);

		return (demographics);
	}
	
	public List<Demographic> searchDemographic(LoggedInInfo loggedInInfo, String searchStr) {
		checkPrivilege(loggedInInfo, SecurityInfoManager.READ);
		if (loggedInInfo == null)
		{
			throw (new SecurityException("user not logged in?"));
		}

		List<Demographic> demographics = demographicDao.searchDemographic(searchStr);

		return (demographics);
	}
	
	public List<Demographic> getActiveDemosByHealthCardNo(LoggedInInfo loggedInInfo, String hcn, String hcnType) {
		checkPrivilege(loggedInInfo, SecurityInfoManager.READ);
		if (loggedInInfo == null)
		{
			throw (new SecurityException("user not logged in?"));
		}
		
		List<Demographic> demographics = demographicDao.getActiveDemosByHealthCardNo(hcn, hcnType);
		
		return (demographics);
	}

	public List<Integer> getMergedDemographicIds(LoggedInInfo loggedInInfo, Integer demographicNo) {
		if (loggedInInfo == null)
		{
			throw (new SecurityException("user not logged in?"));
		}

		List<Integer> ids = demographicDao.getMergedDemographics(demographicNo);

		return ids;
	}

	@Deprecated
	public List<Demographic> getDemosByChartNo(LoggedInInfo loggedInInfo, String chartNo) {
		return getDemosByChartNo(loggedInInfo.getLoggedInProviderNo(), chartNo);
	}
	public List<Demographic> getDemosByChartNo(String loggedInProviderNo, String chartNo) {
		checkPrivilege(loggedInProviderNo, SecurityInfoManager.READ);
		if (loggedInProviderNo == null)
		{
			throw (new SecurityException("user not logged in?"));
		}

		return demographicDao.getClientsByChartNo(chartNo);
	}

	public List<Demographic> searchByHealthCard(LoggedInInfo loggedInInfo, String hin) {
		if (loggedInInfo == null)
		{
			throw (new SecurityException("user not logged in?"));
		}
		checkPrivilege(loggedInInfo, SecurityInfoManager.READ);

		return demographicDao.searchByHealthCard(hin);
	}

	/**
	 * Given a HIN and a demographic to compare against, see if any other demographics are using the same HIN.
	 * @param loggedInInfo currently logged in user
	 * @param hin HIN we want to check potential duplication for
	 * @param ver optional version code to go with HIN
	 * @param demographicNo demographic to search for
	 * @return false if a demographic outside of our given one has this HIN set, true otherwise
	 */
	public boolean isUniqueHealthCard(LoggedInInfo loggedInInfo, String hin, String ver, String province, int demographicNo)
	{
		// whether it's unique or not, if we allow duplicates we don't care and this has to return true
		if (hinValidationService.isDuplicateAllowable(ver, province))
		{
			return true;
		}

		List<Demographic> potentialMatches = searchByHealthCard(loggedInInfo, hin);

		boolean isUnique = true;
		for (Demographic demographic : potentialMatches)
		{
			if (demographic.getDemographicNo() == demographicNo)
			{
				continue;
			}

			if (!hinValidationService.isDuplicateAllowable(demographic.getVer(), demographic.getHcType()))
			{
				isUnique = false;
			}
		}

		return isUnique;
	}

	public Demographic getDemographicByNamePhoneEmail(LoggedInInfo loggedInInfo, String firstName, String lastName,
			String hPhone, String wPhone, String email) {
		if (loggedInInfo == null)
		{
			throw (new SecurityException("user not logged in?"));
		}
		checkPrivilege(loggedInInfo, SecurityInfoManager.READ);

		return demographicDao.getDemographicByNamePhoneEmail(firstName, lastName, hPhone, wPhone, email);
	}

	public List<Demographic> getDemographicWithLastFirstDOB(LoggedInInfo loggedInInfo, String lastname,
			String firstname, String year_of_birth, String month_of_birth, String date_of_birth) {
		if (loggedInInfo == null)
		{
			throw (new SecurityException("user not logged in?"));
		}
		checkPrivilege(loggedInInfo, SecurityInfoManager.READ);

		return demographicDao.getDemographicWithLastFirstDOB(lastname, firstname, year_of_birth, month_of_birth, date_of_birth);
	}

	private void checkPrivilege(LoggedInInfo loggedInInfo, String privilege)
	{
		checkPrivilege(loggedInInfo.getLoggedInProviderNo(), privilege);
	}
	private void checkPrivilege(String providerNo, String privilege) {
		securityInfoManager.requireOnePrivilege(providerNo, privilege, null, "_demographic");
	}

	private void checkPrivilege(String providerNo, String privilege, int demographicNo) {
		securityInfoManager.requireOnePrivilege(providerNo, privilege, demographicNo, "_demographic");
	}

	public void addDemographicWithValidation(LoggedInInfo loggedInInfo, Demographic demographic) throws Exception
	{
		checkPrivilege(loggedInInfo, SecurityInfoManager.WRITE);

        if (demographic.getDateJoined() == null)
        {
            Date newDateJoined = new Date();
            demographic.setDateJoined(newDateJoined);
        }
		validateDemographic(demographic);
		filterDemographic(demographic);

		demographicDao.save(demographic);

		LogAction.addLogEntrySynchronous("DemographicManager.addDemographicWithValidation", "demographicId=" + demographic.getDemographicNo());
	}

	public void addDemographicExts(LoggedInInfo loggedInInfo, Demographic demographic,
									  DemographicTransfer demographicTransfer)
	{
		checkPrivilege(loggedInInfo, SecurityInfoManager.WRITE);

		if (demographicTransfer.getCellPhone() != null)
		{
			demographicExtDao.addKey(demographic.getProviderNo(),
					demographic.getDemographicNo(), "demo_cell",
					demographicTransfer.getCellPhone());
		}
	}

	public void updateDemographicExtras(LoggedInInfo loggedInInfo, Demographic demographic, DemographicTransfer demographicTransfer)
	{
		checkPrivilege(loggedInInfo, SecurityInfoManager.WRITE);

		DemographicCust demoCust = getDemographicCust(loggedInInfo, demographic.getDemographicNo());
		demoCust.setParsedNotes(demographicTransfer.getNotes());
		createUpdateDemographicCust(loggedInInfo, demoCust);
	}

	// When adding a demographic, entries are required in other tables.  This
	// method adds those entries.
	public void addDemographicExtras(LoggedInInfo loggedInInfo, Demographic demographic, DemographicTransfer demographicTransfer)
	{
		checkPrivilege(loggedInInfo, SecurityInfoManager.WRITE);

		// demographiccust
		DemographicCust demoCust = new DemographicCust();
		demoCust.setId(demographic.getDemographicNo());
		demoCust.setAlert("");
		demoCust.setResident("");
		demoCust.setMidwife("");
		demoCust.setNurse("");
		if (demographicTransfer.getNotes() != null)
		{
			demoCust.setParsedNotes(demographicTransfer.getNotes());
		}
		else
		{
			demoCust.setNotes("<unotes></unotes>");
		}

		demographicCustDao.persist(demoCust);

		Date date = new Date();

		// admission
		Admission admission = new Admission();

		admission.setProviderNo("");
		admission.setClientId(demographic.getDemographicNo());
		admission.setAdmissionDate(date);
		admission.setDischargeDate(date);
		admission.setProgramId(programManager.getDefaultProgramId());
		admission.setTemporaryAdmission(false);
		admission.setDischargeFromTransfer(false);
		admission.setAdmissionFromTransfer(false);
		admission.setAutomaticDischarge(false);
		admission.setAdmissionStatus(Admission.STATUS_CURRENT);

		admissionDao.saveAdmission(admission);

		// provider_recent_demographic_access
		RecentDemographicAccess recentDemographicAccess = new RecentDemographicAccess(
				-1, demographic.getDemographicNo()
		);
		recentDemographicAccess.setAccessDateTimeToNow();
		recentDemographicAccessDao.persist(recentDemographicAccess);
	}

	private void filterDemographic(Demographic demographic)
	{
		// Set some default values
		if (demographic.getPatientStatus() == null)
		{
			demographic.setPatientStatus("AC");

			if (demographic.getPatientStatusDate() == null)
			{
				Date date = new Date();
				demographic.setPatientStatusDate(date);
			}
		}

		if (StringUtils.isBlank(demographic.getFamilyDoctor()))
		{
			demographic.setFamilyDoctor("<rdohip></rdohip><rd></rd>");
		}

		if (StringUtils.isBlank(demographic.getFamilyDoctor2()))
		{
			demographic.setFamilyDoctor2("<fd></fd><fdname></fdname>");
		}

		if (demographic.getLastUpdateDate() == null)
		{
			demographic.setLastUpdateDate(new Date());
		}

		if (StringUtils.isBlank(demographic.getProviderNo()))
		{
			demographic.setProviderNo(null);
		}

		// Oscar expects date and month of birth to always be 2 character strings
		demographic.setDateOfBirth(StringUtils.leftPad(demographic.getDateOfBirth(), 2, "0"));
		demographic.setMonthOfBirth(StringUtils.leftPad(demographic.getMonthOfBirth(), 2, "0"));

	}

	private void validateDemographic(Demographic demographic)
			throws Exception
	{
		boolean has_error = false;
		String error_string = "";
		if (StringUtils.isEmpty(demographic.getFirstName()))
		{
			error_string += FIRST_NAME_REQUIRED;
			has_error = true;
		}

		if (StringUtils.isEmpty(demographic.getLastName()))
		{
			error_string += LAST_NAME_REQUIRED;
			has_error = true;
		}

		if (demographic.getSex() == null)
		{
			error_string += SEX_REQUIRED;
			has_error = true;
		}

		else if (!demographic.getSex().equals("M")
				&& !demographic.getSex().equals("F"))
		{
			error_string += SEX_INVALID;
			has_error = true;
		}

		if (demographic.getYearOfBirth() == null)
		{
			error_string += YEAR_OF_BIRTH_REQUIRED;
			has_error = true;
		}
		else if (!StringUtils.isNumeric(demographic.getYearOfBirth()))
		{
			error_string += YEAR_OF_BIRTH_NUMERIC;
			has_error = true;
		}
		else
		{
			// Convert the string value of the birth year to an int and ensure that it is 4 digits long.
			int yearOfBirth = ConversionUtils.fromIntString(demographic.getYearOfBirth());
			if (yearOfBirth < 1000 || yearOfBirth >= 10000)
			{
				error_string += YEAR_OF_BIRTH_4_DIGIT;
				has_error = true;
			}
		}

		if (demographic.getMonthOfBirth() == null)
		{
			error_string += MONTH_OF_BIRTH_REQUIRED;
			has_error = true;
		}
		else {
			int monthOfBirth = ConversionUtils.fromIntString(demographic.getMonthOfBirth());
			if (monthOfBirth < 1 || monthOfBirth > 12)
			{
				error_string += MONTH_OF_BIRTH_INVALID;
				has_error = true;
			}
		}

		if (demographic.getDateOfBirth() == null)
		{
			error_string += DATE_OF_BIRTH_REQUIRED;
			has_error = true;
		}
		else {
			int dateOfBirth = ConversionUtils.fromIntString(demographic.getDateOfBirth());
			if (dateOfBirth < 1 || dateOfBirth > 31) {
				error_string += DATE_OF_BIRTH_INVALID;
				has_error = true;
			}
		}

		// Ensure that the proposed date is actually a valid date
		String possibleBirthday = demographic.getBirthDayAsString();
		Date validDate = ConversionUtils.fromDateString(possibleBirthday);
		if (validDate == null)
		{
			error_string += BIRTHDAY_INVALID;
			has_error = true;
		}

		String familyDoctor = demographic.getFamilyDoctor();
		if (StringUtils.isNotBlank(familyDoctor) && !validatePattern(familyDoctor, "<rdohip>(.*)<\\/rdohip><rd>(.*)<\\/rd>"))
		{
			error_string += FAMILY_DOCTOR_INVALID;
			has_error = true;
		}

		if (!validatePattern(demographic.getFamilyDoctor2(), "<fd>(.*)<\\/fd><fdname>(.*)<\\/fdname>"))
		{
			error_string += FAMILY_DOCTOR_2_INVALID;
			has_error = true;
		}

		if (
				!oscar.util.StringUtils.isStringSafe(demographic.getPhone()) ||
						!oscar.util.StringUtils.isStringSafe(demographic.getPatientStatus()) ||
						!oscar.util.StringUtils.isStringSafe(demographic.getRosterStatus()) ||
						!oscar.util.StringUtils.isStringSafe(demographic.getProviderNo()) ||
						!oscar.util.StringUtils.isStringSafe(demographic.getMyOscarUserName()) ||
						!oscar.util.StringUtils.isStringSafe(demographic.getHin()) ||
						!oscar.util.StringUtils.isStringSafe(demographic.getAddress()) ||
						!oscar.util.StringUtils.isStringSafe(demographic.getProvince()) ||
						!oscar.util.StringUtils.isStringSafe(demographic.getMonthOfBirth()) ||
						!oscar.util.StringUtils.isStringSafe(demographic.getVer()) ||
						!oscar.util.StringUtils.isStringSafe(demographic.getDateOfBirth()) ||
						!oscar.util.StringUtils.isStringSafe(demographic.getSex()) ||
						!oscar.util.StringUtils.isStringSafe(demographic.getSexDesc()) ||
						!oscar.util.StringUtils.isStringSafe(demographic.getCity()) ||
						!oscar.util.StringUtils.isStringSafe(demographic.getFirstName()) ||
						!oscar.util.StringUtils.isStringSafe(demographic.getPostal()) ||
						!oscar.util.StringUtils.isStringSafe(demographic.getPhone2()) ||
						!oscar.util.StringUtils.isStringSafe(demographic.getPcnIndicator()) ||
						!oscar.util.StringUtils.isStringSafe(demographic.getLastName()) ||
						!oscar.util.StringUtils.isStringSafe(demographic.getHcType()) ||
						!oscar.util.StringUtils.isStringSafe(demographic.getChartNo()) ||
						!oscar.util.StringUtils.isStringSafe(demographic.getEmail()) ||
						!oscar.util.StringUtils.isStringSafe(demographic.getYearOfBirth()) ||
						!oscar.util.StringUtils.isStringSafe(demographic.getRosterTerminationReason()) ||
						!oscar.util.StringUtils.isStringSafe(demographic.getLinks()) ||
						!oscar.util.StringUtils.isStringSafe(demographic.getAlias()) ||
						!oscar.util.StringUtils.isStringSafe(demographic.getPreviousAddress()) ||
						!oscar.util.StringUtils.isStringSafe(demographic.getChildren()) ||
						!oscar.util.StringUtils.isStringSafe(demographic.getSourceOfIncome()) ||
						!oscar.util.StringUtils.isStringSafe(demographic.getCitizenship()) ||
						!oscar.util.StringUtils.isStringSafe(demographic.getSin()) ||
						!oscar.util.StringUtils.isStringSafe(demographic.getAnonymous()) ||
						!oscar.util.StringUtils.isStringSafe(demographic.getSpokenLanguage()) ||
						!oscar.util.StringUtils.isStringSafe(demographic.getDisplayName()) ||
						!oscar.util.StringUtils.isStringSafe(demographic.getLastUpdateUser()) ||
						!oscar.util.StringUtils.isStringSafe(demographic.getTitle()) ||
						!oscar.util.StringUtils.isStringSafe(demographic.getOfficialLanguage()) ||
						!oscar.util.StringUtils.isStringSafe(demographic.getCountryOfOrigin()) ||
						!oscar.util.StringUtils.isStringSafe(demographic.getNewsletter()) ||
						!oscar.util.StringUtils.isStringSafe(demographic.getVeteranNo()) ||
						!oscar.util.StringUtils.isStringSafe(demographic.getNameOfFather()) ||
						!oscar.util.StringUtils.isStringSafe(demographic.getNameOfMother())
				)
		{
			error_string += FIELD_UNSAFE;
			has_error = true;
		}

		if (has_error)
		{
			throw new Exception(error_string);
		}
	}

	private boolean validatePattern(String value, String pattern)
	{
		if (value == null)
		{
			return true;
		}

		// Make sure it is formatted correctly
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(value);

		if (!m.matches())
		{
			return false;
		}

		// Fail if there are invalid characters in the contents
		int numGroups = m.groupCount();
		for (int group = 1; group <= numGroups; group++)
		{
			if (!oscar.util.StringUtils.isStringSafe(m.group(group)))
			{
				return false;
			}
		}

		return true;
	}

	public Demographic getDemographicByHealthNumber(String healthNumber)
	{
		return this.demographicDao.getDemographicByHealthNumber(healthNumber);
	}


	/**
	 * Update the electronic messaging consent timestamps of the provided demographic
	 * if the messaging consent status has changed
	 * @param demographic - the demographic to update the timestamps on.
	 * @param existingDemographic - the previously saved demographic record (can be null)
	 * @param loggedInInfo - the user performing this action
	 */
	private void updateElectronicMessagingConsentTimestamps(Demographic demographic, Demographic existingDemographic, LoggedInInfo loggedInInfo)
	{
		if (existingDemographic == null || demographic.getElectronicMessagingConsentStatus() != existingDemographic.getElectronicMessagingConsentStatus())
		{
			// status has changed. update!
			switch(demographic.getElectronicMessagingConsentStatus())
			{
				case NONE:
					demographic.setElectronicMessagingConsentGivenAt(null);
					demographic.setElectronicMessagingConsentRejectedAt(null);
					break;
				case REVOKED:
					demographic.setElectronicMessagingConsentRejectedAt(new Date());
					break;
				case CONSENTED:
					demographic.setElectronicMessagingConsentGivenAt(new Date());
					demographic.setElectronicMessagingConsentRejectedAt(null);
					break;
			}

			// record the consent change.
			LogAction.addLogEntry(
					loggedInInfo.getLoggedInProviderNo(),
					demographic.getDemographicNo(),
					LogConst.ACTION_UPDATE,
					LogConst.CON_ELECTRONIC_MESSAGING_CONSENT_STATUS,
					LogConst.STATUS_SUCCESS,
					demographic.getElectronicMessagingConsentStatus().name());
		}
	}

}
