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
	private PHRVerificationDao phrVerificationDao;

	@Autowired
	private AdmissionDao admissionDao;
	
	@Autowired
	private SecurityInfoManager securityInfoManager;

	@Autowired
	private RecentDemographicAccessDao recentDemographicAccessDao;

	@Autowired
	private ProgramManager programManager;
	

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
		if(demographic.getFamilyDoctor() == null)
		{
			demographic.setFamilyDoctor("<rdohip></rdohip><rd></rd>");
		}
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

		//retain merge info
		demographic.setSubRecord(prevDemo.getSubRecord());
		
		//save current demo
		demographic.setLastUpdateUser(loggedInInfo.getLoggedInProviderNo());
		demographicDao.save(demographic);

		if (demographic.getExtras() != null) {
			for (DemographicExt ext : demographic.getExtras()) {
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

	}

	private void validateDemographic(Demographic demographic)
			throws Exception
	{
		boolean has_error = false;
		String error_string = "";
		if (demographic.getFirstName() == null)
		{
			error_string += "firstName is a required field.  ";
			has_error = true;
		}

		if (demographic.getLastName() == null)
		{
			error_string += "lastName is a required field.  ";
			has_error = true;
		}

		if (demographic.getSex() == null)
		{
			error_string += "sex is a required field.  ";
			has_error = true;
		}

		else if (!demographic.getSex().equals("M")
				&& !demographic.getSex().equals("F"))
		{
			error_string += "sex must be either \"M\" or \"F\" (received " +
					demographic.getSex() + ").  ";

			has_error = true;
		}

		if (demographic.getYearOfBirth() == null)
		{
			error_string += "yearOfBirth is a required field.  ";
			has_error = true;
		}

		if (demographic.getMonthOfBirth() == null)
		{
			error_string += "monthOfBirth is a required field.  ";
			has_error = true;
		}

		if (demographic.getDateOfBirth() == null)
		{
			error_string += "dateOfBirth is a required field.  ";
			has_error = true;
		}

		String familyDoctor = demographic.getFamilyDoctor();
		if (!StringUtils.isBlank(familyDoctor) && !validatePattern(familyDoctor, "<rdohip>(.*)<\\/rdohip><rd>(.*)<\\/rd>"))
		{
			error_string += "familyDoctor is formatted incorrectly.  It must ";
			error_string += "be a string like <rdohip>{referral doctor number}";
			error_string += "</rdohip><rd>{last name},{first name}</rd>.  ";
			error_string += "Also no other tags and no quotes, line breaks ";
			error_string += "or semicolons are allowed.";
			has_error = true;
		}

		if (!validatePattern(demographic.getFamilyDoctor2(), "<fd>(.*)<\\/fd><fdname>(.*)<\\/fdname>"))
		{
			error_string += "familyDoctor2 is formatted incorrectly.  It must ";
			error_string += "be a string like <fd>{family doctor number}";
			error_string += "</fd><fdname>{last name},{first name}</fdname>.  ";
			error_string += "Also no other tags and no quotes, line breaks ";
			error_string += "or semicolons are allowed.";
			has_error = true;
		}

		if (
				!validateString(demographic.getPhone()) ||
						!validateString(demographic.getPatientStatus()) ||
						!validateString(demographic.getRosterStatus()) ||
						!validateString(demographic.getProviderNo()) ||
						!validateString(demographic.getMyOscarUserName()) ||
						!validateString(demographic.getHin()) ||
						!validateString(demographic.getAddress()) ||
						!validateString(demographic.getProvince()) ||
						!validateString(demographic.getMonthOfBirth()) ||
						!validateString(demographic.getVer()) ||
						!validateString(demographic.getDateOfBirth()) ||
						!validateString(demographic.getSex()) ||
						!validateString(demographic.getSexDesc()) ||
						!validateString(demographic.getCity()) ||
						!validateString(demographic.getFirstName()) ||
						!validateString(demographic.getPostal()) ||
						!validateString(demographic.getPhone2()) ||
						!validateString(demographic.getPcnIndicator()) ||
						!validateString(demographic.getLastName()) ||
						!validateString(demographic.getHcType()) ||
						!validateString(demographic.getChartNo()) ||
						!validateString(demographic.getEmail()) ||
						!validateString(demographic.getYearOfBirth()) ||
						!validateString(demographic.getRosterTerminationReason()) ||
						!validateString(demographic.getLinks()) ||
						!validateString(demographic.getAlias()) ||
						!validateString(demographic.getPreviousAddress()) ||
						!validateString(demographic.getChildren()) ||
						!validateString(demographic.getSourceOfIncome()) ||
						!validateString(demographic.getCitizenship()) ||
						!validateString(demographic.getSin()) ||
						!validateString(demographic.getAnonymous()) ||
						!validateString(demographic.getSpokenLanguage()) ||
						!validateString(demographic.getDisplayName()) ||
						!validateString(demographic.getLastUpdateUser()) ||
						!validateString(demographic.getTitle()) ||
						!validateString(demographic.getOfficialLanguage()) ||
						!validateString(demographic.getCountryOfOrigin()) ||
						!validateString(demographic.getNewsletter()) ||
						!validateString(demographic.getVeteranNo()) ||
						!validateString(demographic.getNameOfFather()) ||
						!validateString(demographic.getNameOfMother())
				)
		{
			error_string += "No html tags and no quotes, line breaks ";
			error_string += "or semicolons are allowed.";
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
			if (!validateString(m.group(group)))
			{
				return false;
			}
		}

		return true;
	}

	private boolean validateString(String testValue)
	{
		if (testValue == null)
		{
			return true;
		}

		Pattern p = Pattern.compile(".*\\<.*?>.*");
		Matcher m = p.matcher(testValue);

		if (m.matches())
		{
			return false;
		}

		if (
				testValue.matches("(?s).*;.*") ||
						testValue.matches("(?s).*\".*") ||
						testValue.matches("(?s).*'.*") ||
						testValue.matches("(?s).*--.*") ||
						testValue.matches("(?s).*\\n.*") ||
						testValue.matches("(?s).*\\r.*") ||
						testValue.matches("(?s).*\\\\.*") ||
						testValue.matches("(?s).*\\x00.*") ||
						testValue.matches("(?s).*\\x1a.*")
				)
		{
			return false;
		}

		return true;
	}

	public Demographic getDemographicByHealthNumber(String healthNumber)
	{
		return this.demographicDao.getDemographicByHealthNumber(healthNumber);
	}

}
