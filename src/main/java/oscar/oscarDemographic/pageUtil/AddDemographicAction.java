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


package oscar.oscarDemographic.pageUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.oscarehr.PMmodule.service.AdmissionManager;
import org.oscarehr.PMmodule.service.ProgramManager;
import org.oscarehr.PMmodule.web.GenericIntakeEditAction;
import org.oscarehr.common.OtherIdManager;
import org.oscarehr.common.dao.DemographicDao;
import org.oscarehr.common.model.ConsentType;
import org.oscarehr.demographic.dao.DemographicExtArchiveDao;
import org.oscarehr.demographic.dao.DemographicExtDao;
import org.oscarehr.demographic.entity.Demographic;
import org.oscarehr.demographic.entity.DemographicCust;
import org.oscarehr.demographic.entity.DemographicExt;
import org.oscarehr.demographic.entity.DemographicExtArchive;
import org.oscarehr.demographic.service.DemographicService;
import org.oscarehr.document.model.Document;
import org.oscarehr.document.service.DocumentService;
import org.oscarehr.managers.PatientConsentManager;
import org.oscarehr.provider.service.RecentDemographicAccessService;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import oscar.MyDateFormat;
import oscar.OscarProperties;
import oscar.dms.data.AddEditDocumentForm;
import oscar.log.LogAction;
import oscar.log.LogConst;
import oscar.oscarWaitingList.util.WLWaitingListUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static oscar.util.StringUtils.filterControlCharacters;

public class AddDemographicAction extends Action
{
	private OscarProperties oscarVariables = oscar.OscarProperties.getInstance();

	private DemographicDao demographicDao = (DemographicDao)SpringUtils.getBean("demographicDao");
	DemographicExtArchiveDao demographicExtArchiveDao = SpringUtils.getBean(DemographicExtArchiveDao.class);
	DemographicExtDao demographicExtDao = SpringUtils.getBean(DemographicExtDao.class);

	private DemographicService demographicService = (DemographicService) SpringUtils.getBean("demographic.service.DemographicService");
	private ProgramManager pm = SpringUtils.getBean(ProgramManager.class);
	private AdmissionManager am = SpringUtils.getBean(AdmissionManager.class);

	private RecentDemographicAccessService recentDemographicAccessService = SpringUtils.getBean(RecentDemographicAccessService.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	{
		MiscUtils.getLogger().info("SAVING DEMOGRAPHIC");
		AddEditDocumentForm fm = (AddEditDocumentForm) form;

		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);

		String demoNo = null;
		String providerNo = loggedInInfo.getLoggedInProviderNo();

		String year, month, day;
		String curUser_no = loggedInInfo.getLoggedInProviderNo();

		LocalDate dateOfBirth;
		try
		{
			dateOfBirth = LocalDate.of(Integer.parseInt(request.getParameter("year_of_birth")), Integer.parseInt(request.getParameter("month_of_birth")), Integer.parseInt(request.getParameter("date_of_birth")));
		}
		catch (NumberFormatException | DateTimeException e)
		{
			// Return error message to user
			MiscUtils.getLogger().error("Date of Birth Format Exception: ", e);
			ActionForward invalidDOB = mapping.findForward("validationFail");
			invalidDOB = new ActionForward(invalidDOB.getPath() + "?invalidDOB=true", invalidDOB.getRedirect());
			return invalidDOB;
		}

		Demographic demographic = new Demographic();
		demographic.setLastName(StringUtils.trimToNull(request.getParameter("last_name")));
		demographic.setFirstName(StringUtils.trimToNull(request.getParameter("first_name")));
		demographic.setAddress(StringUtils.trimToNull(request.getParameter("address")));
		demographic.setCity(StringUtils.trimToNull(request.getParameter("city")));
		demographic.setProvince(StringUtils.trimToNull(request.getParameter("province")));
		demographic.setPostal(StringUtils.trimToNull(request.getParameter("postal")));
		demographic.setPhone(filterControlCharacters(StringUtils.trimToNull(request.getParameter("phone"))));
		demographic.setPhone2(filterControlCharacters(StringUtils.trimToNull(request.getParameter("phone2"))));
		demographic.setEmail(StringUtils.trimToNull(request.getParameter("email")));
		demographic.setDateOfBirth(dateOfBirth);

		String hin = request.getParameter("hin");
		if (hin != null)
		{
			hin = hin.replaceAll("[^0-9a-zA-Z]", "");
		}
		demographic.setHin(StringUtils.trimToNull(hin));

		demographic.setVer(StringUtils.trimToNull(request.getParameter("ver")));
		demographic.setRosterStatus(StringUtils.trimToNull(request.getParameter("roster_status")));
		demographic.setPatientStatus(StringUtils.trimToNull(request.getParameter("patient_status")));
		demographic.setDateJoined(MyDateFormat.getSysDate(request.getParameter("date_joined_year") + "-" + request.getParameter("date_joined_month") + "-" + request.getParameter("date_joined_date")));
		demographic.setChartNo(StringUtils.trimToNull(request.getParameter("chart_no")));
		demographic.setProviderNo(StringUtils.trimToNull(request.getParameter("staff")));
		demographic.setSex(StringUtils.trimToNull(request.getParameter("sex")));

		year = StringUtils.trimToNull(request.getParameter("end_date_year"));
		month = StringUtils.trimToNull(request.getParameter("end_date_month"));
		day = StringUtils.trimToNull(request.getParameter("end_date_date"));

		if (year != null && month != null && day != null)
		{
			demographic.setEndDate(MyDateFormat.getSysDate(year + "-" + month + "-" + day));
		} else
		{
			demographic.setEndDate(null);
		}

		year = StringUtils.trimToNull(request.getParameter("eff_date_year"));
		month = StringUtils.trimToNull(request.getParameter("eff_date_month"));
		day = StringUtils.trimToNull(request.getParameter("eff_date_date"));
		if (year!=null && month!=null && day!=null) {
			demographic.setHcEffectiveDate(MyDateFormat.getSysDate(year + "-" + month + "-" + day));
		} else {
			demographic.setHcEffectiveDate(null);
		}

		demographic.setPcnIndicator(StringUtils.trimToNull(request.getParameter("pcn_indicator")));
		demographic.setHcType(StringUtils.trimToNull(request.getParameter("hc_type")));

		year = StringUtils.trimToNull(request.getParameter("roster_date_year"));
		month = StringUtils.trimToNull(request.getParameter("roster_date_month"));
		day = StringUtils.trimToNull(request.getParameter("roster_date_date"));
		if (year != null && month != null && day != null)
		{
			demographic.setRosterDate(MyDateFormat.getSysDate(year + "-" + month + "-" + day));
		} else
		{
			demographic.setRosterDate(null);
		}

		year = StringUtils.trimToNull(request.getParameter("hc_renew_date_year"));
		month = StringUtils.trimToNull(request.getParameter("hc_renew_date_month"));
		day = StringUtils.trimToNull(request.getParameter("hc_renew_date_date"));
		if (year != null && month != null && day != null)
		{
			demographic.setHcRenewDate(MyDateFormat.getSysDate(year + "-" + month + "-" + day));
		} else
		{
			demographic.setHcRenewDate(null);
		}

		demographic.setReferralDoctor("<rdohip>" + StringUtils.trimToEmpty(request.getParameter("referral_doctor_no")) + "</rdohip>" + "<rd>" + StringUtils.trimToEmpty(request.getParameter("referral_doctor_name")) + "</rd>");
		demographic.setFamilyDoctor("<fd>" + StringUtils.trimToEmpty(request.getParameter("family_doctor_no")) + "</fd>" + "<fdname>" + StringUtils.trimToEmpty( request.getParameter("family_doctor_name"))+ "</fdname>");
		demographic.setCountryOfOrigin(StringUtils.trimToNull(request.getParameter("countryOfOrigin")));
		demographic.setNewsletter(StringUtils.trimToNull(request.getParameter("newsletter")));
		demographic.setSin(StringUtils.trimToNull(request.getParameter("sin")));
		demographic.setTitle(StringUtils.trimToNull(request.getParameter("title")));
		demographic.setOfficialLanguage(StringUtils.trimToNull(request.getParameter("official_lang")));
		demographic.setSpokenLanguage(StringUtils.trimToNull(request.getParameter("spoken_lang")));
		demographic.setLastUpdateUser(curUser_no);
		demographic.setLastUpdateDate(new java.util.Date());
		demographic.setPatientStatusDate(new java.util.Date());

		// add checking hin duplicated record, if there is a HIN number
		// added check to see if patient has a bc health card and has a version code of 66, in this case you are aloud to have dup hin
		boolean hinDupCheckException = false;
		String hcType = request.getParameter("hc_type");
		String ver = request.getParameter("ver");
		if (hcType != null && ver != null && hcType.equals("BC") && ver.equals("66"))
		{
			hinDupCheckException = true;
		}


		String paramNameHin = request.getParameter("hin");
		if(paramNameHin!=null && paramNameHin.length()>5 && !hinDupCheckException) {
			int demographics = demographicDao.searchByHealthCard(paramNameHin.trim()).size();
			if(demographics > 0){
				ActionForward dupHin = mapping.findForward("validationFail");
				dupHin = new ActionForward(dupHin.getPath() + "?dupHin=true", dupHin.getRedirect());
				return dupHin;
			}
		}



		if (oscarVariables.isPropertyActive("demographic_veteran_no"))
		{
			demographic.setVeteranNo(StringUtils.trimToNull(request.getParameter("veteranNo")));
		}


		DemographicCust demographicCust = new DemographicCust();
		demographicCust.setResident(StringUtils.trimToNull(request.getParameter("cust2")));
		demographicCust.setNurse(StringUtils.trimToNull(request.getParameter("cust1")));
		demographicCust.setAlert(StringUtils.trimToNull(request.getParameter("cust3")));
		demographicCust.setMidwife(StringUtils.trimToNull(request.getParameter("cust4")));
		demographicCust.setNotes("<unotes>" + StringUtils.trimToEmpty(request.getParameter("content")) + "</unotes>");


		// Save the patient consent values.
		if (OscarProperties.getInstance().getBooleanProperty("USE_NEW_PATIENT_CONSENT_MODULE", "true"))
		{

			PatientConsentManager patientConsentManager = SpringUtils.getBean(PatientConsentManager.class);
			List<ConsentType> consentTypes = patientConsentManager.getConsentTypes();
			String consentTypeId = null;

			for (ConsentType consentType : consentTypes)
			{
				consentTypeId = request.getParameter(consentType.getType());
				// checked box means add or edit consent.
				if (consentTypeId != null)
				{
					patientConsentManager.addConsent(loggedInInfo, demographic.getDemographicId(), Integer.parseInt(consentTypeId));
				}
			}
		}



		//Create demographic extensions
		Set<DemographicExt> extensions = new HashSet<>();

		extensions.add(new DemographicExt(providerNo, demographic.getDemographicId(), "hPhoneExt", filterControlCharacters(StringUtils.trimToEmpty(request.getParameter("hPhoneExt")))));
		extensions.add(new DemographicExt(providerNo, demographic.getDemographicId(), "wPhoneExt", filterControlCharacters(StringUtils.trimToEmpty(request.getParameter("wPhoneExt")))));
		extensions.add(new DemographicExt(providerNo, demographic.getDemographicId(), "demo_cell", filterControlCharacters(StringUtils.trimToEmpty(request.getParameter("demo_cell")))));
		extensions.add(new DemographicExt(providerNo, demographic.getDemographicId(), "aboriginal", StringUtils.trimToEmpty(request.getParameter("aboriginal"))));
		extensions.add(new DemographicExt(providerNo, demographic.getDemographicId(), "cytolNum", StringUtils.trimToEmpty(request.getParameter("cytolNum"))));
		extensions.add(new DemographicExt(providerNo, demographic.getDemographicId(), "ethnicity", StringUtils.trimToEmpty(request.getParameter("ethnicity"))));
		extensions.add(new DemographicExt(providerNo, demographic.getDemographicId(), "area", StringUtils.trimToEmpty(request.getParameter("area"))));
		extensions.add(new DemographicExt(providerNo, demographic.getDemographicId(), "statusNum", StringUtils.trimToEmpty(request.getParameter("statusNum"))));
		extensions.add(new DemographicExt(providerNo, demographic.getDemographicId(), "fNationCom", StringUtils.trimToEmpty(request.getParameter("fNationCom"))));
		extensions.add(new DemographicExt(providerNo, demographic.getDemographicId(), "given_consent", StringUtils.trimToEmpty(request.getParameter("given_consent"))));
		extensions.add(new DemographicExt(providerNo, demographic.getDemographicId(), "rxInteractionWarningLevel", StringUtils.trimToEmpty(request.getParameter("rxInteractionWarningLevel"))));
		extensions.add(new DemographicExt(providerNo, demographic.getDemographicId(), "primaryEMR", StringUtils.trimToEmpty(request.getParameter("primaryEMR"))));
		extensions.add(new DemographicExt(providerNo, demographic.getDemographicId(), "phoneComment", filterControlCharacters(StringUtils.trimToEmpty(request.getParameter("phoneComment")))));
		extensions.add(new DemographicExt(providerNo, demographic.getDemographicId(), "usSigned", StringUtils.trimToEmpty(request.getParameter("usSigned"))));
		extensions.add(new DemographicExt(providerNo, demographic.getDemographicId(), "privacyConsent", StringUtils.trimToEmpty(request.getParameter("privacyConsent"))));
		extensions.add(new DemographicExt(providerNo, demographic.getDemographicId(), "informedConsent", StringUtils.trimToEmpty(request.getParameter("informedConsent"))));


		// customized key
		if (oscarVariables.getProperty("demographicExt") != null)
		{
			String[] propDemoExt = oscarVariables.getProperty("demographicExt", "").split("\\|");
			for (int k = 0; k < propDemoExt.length; k++)
			{
				extensions.add(new DemographicExt(providerNo, demographic.getDemographicId(), propDemoExt[k], request.getParameter(propDemoExt[k].replace(' ', '_'))));
			}
		}

		demographic = demographicService.addNewDemographicRecord(curUser_no, demographic, demographicCust, extensions);

		demoNo = demographic.getDemographicId().toString();
		MiscUtils.getLogger().info("Added demographic with demographic number: " + demoNo);


		// save custom licensed producer if enabled
		if(oscarVariables.isPropertyActive("show_demographic_licensed_producers")) {
			try {
				int licensedProducerID = Integer.parseInt(request.getParameter("licensed_producer"));
				int licensedProducerID2 = Integer.parseInt(request.getParameter("licensed_producer2"));
				int licensedProducerAddressID = Integer.parseInt(request.getParameter("licensed_producer_address"));
				demographicDao.saveDemographicLicensedProducer(Integer.parseInt(demoNo), licensedProducerID, licensedProducerID2, licensedProducerAddressID);
			}
			catch(NumberFormatException e) {
				// unable to save licensed producer info
				MiscUtils.getLogger().warn(
						String.format("Failed to save licensed producer for demographic %d.", demographic.getDemographicId())
				);
			}
		}

		try
		{
			GenericIntakeEditAction gieat = new GenericIntakeEditAction();
			gieat.setAdmissionManager(am);
			gieat.setProgramManager(pm);
			String residentialStatus = request.getParameter("rps");
			Integer programId;
			if (residentialStatus == null || residentialStatus.trim().isEmpty())
			{
				programId = pm.getProgramIdByProgramName("OSCAR"); //Default to the oscar program
			} else
			{
				programId = Integer.parseInt(residentialStatus);
			}
			gieat.admitBedCommunityProgram(Integer.parseInt(demoNo),loggedInInfo.getLoggedInProviderNo(),programId,"","",null);

			String[] servP = request.getParameterValues("sp");
			if (servP != null && servP.length > 0)
			{
				Set<Integer> s = new HashSet<Integer>();
				for (String _s : servP) s.add(Integer.parseInt(_s));
				gieat.admitServicePrograms(demographic.getDemographicId(),loggedInInfo.getLoggedInProviderNo(),s,"",null);
			}
		} catch(Exception e)
		{
			MiscUtils.getLogger().error("Generic Intake Exception", e);
		}


		//for the IBD clinic
		OtherIdManager.saveIdDemographic(demoNo, "meditech_id", request.getParameter("meditech_id"));

		// add log
		String ip = request.getRemoteAddr();
		LogAction.addLogEntry(curUser_no, demographic.getDemographicId(), LogConst.ACTION_ADD, LogConst.CON_DEMOGRAPHIC, LogConst.STATUS_SUCCESS, demographic.getDemographicId().toString(), ip);
		recentDemographicAccessService.updateAccessRecord(Integer.parseInt(curUser_no), demographic.getDemographicId());

		//archive the original too
		Long archiveId = demographicService.archiveDemographicRecord(demographic);
		List<DemographicExt> demoExtensions = demographicExtDao.getDemographicExtByDemographicNo(Integer.parseInt(demoNo));
		for (DemographicExt extension : demoExtensions) {
			DemographicExtArchive archive = new DemographicExtArchive(extension);
			archive.setArchiveId(archiveId);
			archive.setValue(StringUtils.trimToEmpty(request.getParameter(archive.getKey())));
			demographicExtArchiveDao.saveEntity(archive);
		}

		// Assign the patient to a waitlist if necessary
		String waitListIdStr = request.getParameter("list_id");
		if (waitListIdStr != null)
		{
			int waitingListID = Integer.parseInt(waitListIdStr);
			WLWaitingListUtil.addToWaitingList(waitingListID, demographic.getDemographicId(), request.getParameter("waiting_list_referral_date"), request.getParameter("waiting_list_note"));
		}


		/*
			Optionally attach a document
		 */
		boolean docError = false;
		try
		{
			FormFile docFile = fm.getDocFile();

			if (docFile != null && docFile.getInputStream() != null && docFile.getInputStream().available() != 0)
			{
				Document document = new Document();
				document.setDoctype(request.getParameter("docType"));
				document.setDocdesc(request.getParameter("docDesc"));
				document.setDocfilename(docFile.getFileName());
				document.setDocCreator(loggedInInfo.getLoggedInProviderNo());
				document.setResponsible(loggedInInfo.getLoggedInProviderNo());
				DocumentService documentService = SpringUtils.getBean(org.oscarehr.document.service.DocumentService.class);
				try
				{
					documentService.uploadNewDemographicDocument(document, docFile.getInputStream(), Integer.parseInt(demoNo));
				} catch (Exception e)
				{
					docError = true;
					MiscUtils.getLogger().error("Document Upload Error: ", e);
				}
			}
		} catch (IOException e)
		{
			docError = true;
			MiscUtils.getLogger().error("Document Upload Error: ", e);
		}

		ActionForward success = mapping.findForward("successAdd");
		success = new ActionForward(success.getPath() + "?demoNo=" + demoNo + "&submitType=" + URLEncoder.encode(request.getParameter("submitType")) + "&providerNo=" + providerNo + "&docError=" + docError, success.getRedirect());

		return success;
	}
}
