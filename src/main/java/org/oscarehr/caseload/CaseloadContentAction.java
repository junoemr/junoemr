/**
 * Copyright (c) 2008-2012 Indivica Inc.
 *
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */

package org.oscarehr.caseload;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.oscarehr.common.dao.CaseloadDao;
import org.oscarehr.common.dao.MeasurementDao;
import org.oscarehr.common.dao.MsgDemoMapDao;
import org.oscarehr.common.dao.OscarAppointmentDao;
import org.oscarehr.common.dao.TicklerDao;
import org.oscarehr.common.model.Appointment;
import org.oscarehr.common.model.Measurement;
import org.oscarehr.common.model.MessageList;
import org.oscarehr.demographic.dao.DemographicDao;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.security.model.Permission;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import oscar.OscarProperties;
import oscar.log.LogAction;
import oscar.log.LogConst;
import oscar.util.ConversionUtils;
import oscar.util.OscarRoleObjectPrivilege;
import oscar.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;

public class CaseloadContentAction extends DispatchAction {

	private SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);
	private CaseloadDao caseloadDao = SpringUtils.getBean(CaseloadDao.class);
	private DemographicDao demographicDao = (DemographicDao)SpringUtils.getBean("demographic.dao.DemographicDao");
	private TicklerDao ticklerDao = SpringUtils.getBean(TicklerDao.class);
	private OscarAppointmentDao oscarAppointmentDao = SpringUtils.getBean(OscarAppointmentDao.class);
	private MeasurementDao measurementDao = SpringUtils.getBean(MeasurementDao.class);
	private MsgDemoMapDao msgDemoMapDao = SpringUtils.getBean(MsgDemoMapDao.class);

	public static final String CASELOAD_MEASUREMENT_A1C = "A1C";
	public static final String CASELOAD_MEASUREMENT_ACR = "ACR";
	public static final String CASELOAD_MEASUREMENT_BMI = "BMI";
	public static final String CASELOAD_MEASUREMENT_BP = "BP";
	public static final String CASELOAD_MEASUREMENT_EGFR = "EGFR";
	public static final String CASELOAD_MEASUREMENT_EYEE = "EYEE";
	public static final String CASELOAD_MEASUREMENT_HDL = "HDL";
	public static final String CASELOAD_MEASUREMENT_LDL = "LDL";
	public static final String CASELOAD_MEASUREMENT_SCR = "SCR";
	public static final String CASELOAD_MEASUREMENT_SMK = "SMK";
	public static final String CASELOAD_MEASUREMENT_TCHD = "TCHD";
	public static final String CASELOAD_MEASUREMENT_WT = "WT";

	public ActionForward noteSearch(ActionMapping actionMapping,
			ActionForm actionForm,
			HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException
	{

		String providerNo = LoggedInInfo.getLoggedInInfoFromSession(request).getLoggedInProviderNo();
		securityInfoManager.requireAllPrivilege(providerNo, Permission.DEMOGRAPHIC_READ);

		String caseloadProv     = request.getParameter("clProv");
		String caseloadQuery    = request.getParameter("clQ");
		boolean sortAscending   = "true".equals(request.getParameter("clSortAsc"));

		Integer caseloadPage;
		Integer caseloadPageSize;

		HttpSession session = request.getSession();
		String curUser_no = (String) session.getAttribute("user");

		try {
			caseloadPage = Integer.parseInt(request.getParameter("clPage"));
		}
		catch (Exception e) { caseloadPage = 0; }
		try {
			caseloadPageSize = Integer.parseInt(request.getParameter("clPSize"));
		}
		catch (Exception e) { caseloadPageSize = 100; }

		CaseloadCategory caseloadCategory = CaseloadCategory.getCategory(request.getParameter("clCat"));
		if (caseloadCategory == null) { caseloadCategory = CaseloadCategory.Demographic; }

		String clSearchQuery = "search_notes";
		String[] clSearchParams = new String[] { caseloadProv, caseloadProv, caseloadProv, caseloadProv, "%"+caseloadQuery+"%" };
		String[] clSortParams = null;

		switch (caseloadCategory) {
			case Demographic:
			case Age:
			case Sex:
			case LastAppt:
			case NextAppt:
			case ApptsLYTD:
			case Tickler:
			case Msg:
				clSortParams = null;
				break;
			case Lab:
			case Doc:
				clSortParams = new String[] { curUser_no };
				break;
			case BMI:
			case BP:
			case WT:
			case SMK:
			case A1C:
			case ACR:
			case SCR:
			case LDL:
			case HDL:
			case TCHD:
			case EGFR:
			case EYEE:
				clSortParams = new String[] { caseloadCategory.getLabel() };
				break;
		}

		List<Integer> demoSearchResult = caseloadDao.getCaseloadDemographicSet(clSearchQuery, clSearchParams, clSortParams, caseloadCategory, sortAscending ? "ASC" : "DESC", caseloadPage, caseloadPageSize);
		JSONArray data = generateCaseloadDataForDemographics(request, response, caseloadProv, demoSearchResult);

		response.setContentType("text/x-json");
		JSONObject json = new JSONObject();
		json.put("data", data);

		if (caseloadPage == 0) {
			Integer size = caseloadDao.getCaseloadDemographicSearchSize(clSearchQuery, clSearchParams);
			json.put("size", size);
		}

		LogAction.addLogEntry(LoggedInInfo.getLoggedInInfoFromSession(request).getLoggedInProviderNo(),
				null,
				LogConst.CON_CASELOAD,
				LogConst.ACTION_ACCESS,
				LogConst.STATUS_SUCCESS);

		try {
			json.write(response.getWriter());
		} catch (IOException e) {
			MiscUtils.getLogger().error("Couldn't get data for caseload", e);
		}

		return null;
	}

	public ActionForward search(ActionMapping actionMapping,
			ActionForm actionForm,
			HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException
	{
		LoggedInInfo loggedInInfo=LoggedInInfo.getLoggedInInfoFromSession(request);

		String caseloadDx       = request.getParameter("clDx");
		String caseloadProv     = request.getParameter("clProv");
		String caseloadRoster   = request.getParameter("clRo");
		String caseloadProgram	= request.getParameter("clProg");
		boolean sortAscending   = "true".equals(request.getParameter("clSortAsc"));

		int caseloadPage;
		int caseloadPageSize;

		HttpSession session = request.getSession();
		String curUser_no = (String) session.getAttribute("user");

		try
		{
			caseloadPage = Integer.parseInt(request.getParameter("clPage"));
		}
		catch (NumberFormatException e)
		{
			caseloadPage = 0;
		}
		try
		{
			caseloadPageSize = Integer.parseInt(request.getParameter("clPSize"));
		}
		catch (NumberFormatException e)
		{
			caseloadPageSize = 100;
		}

		CaseloadCategory caseloadCategory = CaseloadCategory.getCategory(request.getParameter("clCat"));
		if (caseloadCategory == null)
		{
			caseloadCategory = CaseloadCategory.Demographic;
		}

		String clSearchQuery;
		String[] clSearchParams;
		String[] clSortParams = null;

		// all => constant?
		if ("all".equals(caseloadProgram) && "all".equals(caseloadProv)){ // program and provider are all
			
			Integer facilityId = loggedInInfo.getCurrentFacility().getId();
			
			if (!StringUtils.isNullOrEmpty(caseloadDx) && !StringUtils.isNullOrEmpty(caseloadRoster)) {
				// filter on dx and roster status
				clSearchQuery="search_allpg_alldemo_rodxfilter";
				clSearchParams = new String[3];
				clSearchParams[0] = caseloadDx;
				clSearchParams[1] = caseloadRoster;
				clSearchParams[2] = facilityId.toString();
			} else if (!StringUtils.isNullOrEmpty(caseloadRoster)) {
				// filter on dx and roster status
				clSearchQuery="search_allpg_alldemo_rofilter";
				clSearchParams = new String[2];
				clSearchParams[0] = caseloadRoster;
				clSearchParams[1] = facilityId.toString();
			}
			else if (!StringUtils.isNullOrEmpty(caseloadDx)) {
				// filter on dx
				clSearchQuery="search_allpg_alldemo_dxfilter";
				clSearchParams = new String[2];
				clSearchParams[0] = caseloadDx;
				clSearchParams[1] = facilityId.toString();
			} else {
				// no dx filter
				clSearchQuery="search_allpg_alldemo_nofilter";
				clSearchParams = new String[1];
				clSearchParams[0] = facilityId.toString();
			}
		} else if ("all".equals(caseloadProgram)) { // program is all
			// demographics from a specific provider
			
			Integer facilityId = loggedInInfo.getCurrentFacility().getId();
			
			if (!StringUtils.isNullOrEmpty(caseloadDx) && !StringUtils.isNullOrEmpty(caseloadRoster)) {
				// filter on dx
				clSearchQuery="search_allpg_provdemo_rodxfilter";
				clSearchParams = new String[7];
				clSearchParams[0] = caseloadProv;
				clSearchParams[1] = caseloadProv;
				clSearchParams[2] = caseloadProv;
				clSearchParams[3] = caseloadProv;
				clSearchParams[4] = caseloadRoster;
				clSearchParams[5] = caseloadDx;
				clSearchParams[6] = facilityId.toString();
			} else if (!StringUtils.isNullOrEmpty(caseloadRoster)) {
				// no dx filter
				clSearchQuery="search_allpg_provdemo_rofilter";
				clSearchParams = new String[6];
				clSearchParams[0] = caseloadProv;
				clSearchParams[1] = caseloadProv;
				clSearchParams[2] = caseloadProv;
				clSearchParams[3] = caseloadProv;
				clSearchParams[4] = caseloadRoster;
				clSearchParams[5] = facilityId.toString();
			} else if (!StringUtils.isNullOrEmpty(caseloadDx)) {
				// filter on dx
				clSearchQuery="search_allpg_provdemo_dxfilter";
				clSearchParams = new String[6];
				clSearchParams[0] = caseloadProv;
				clSearchParams[1] = caseloadProv;
				clSearchParams[2] = caseloadProv;
				clSearchParams[3] = caseloadProv;
				clSearchParams[4] = caseloadDx;
				clSearchParams[5] = facilityId.toString();
			} else {
				// no dx filter
				clSearchQuery="search_allpg_provdemo_nofilter";
				clSearchParams = new String[5];
				clSearchParams[0] = caseloadProv;
				clSearchParams[1] = caseloadProv;
				clSearchParams[2] = caseloadProv;
				clSearchParams[3] = caseloadProv;
				clSearchParams[4] = facilityId.toString();
		    }
		} else if ("all".equals(caseloadProv)) { // provider is all
			// all demographics
			if (!StringUtils.isNullOrEmpty(caseloadDx) && !StringUtils.isNullOrEmpty(caseloadRoster)) {
				// filter on dx and roster status
				clSearchQuery="search_alldemo_rodxfilter";
				clSearchParams = new String[3];
				clSearchParams[0] = caseloadDx;
				clSearchParams[1] = caseloadRoster;
				clSearchParams[2] = caseloadProgram;
			} else if (!StringUtils.isNullOrEmpty(caseloadRoster)) {
				// filter on dx and roster status
				clSearchQuery="search_alldemo_rofilter";
				clSearchParams = new String[2];
				clSearchParams[0] = caseloadRoster;
				clSearchParams[1] = caseloadProgram;
			}
			else if (!StringUtils.isNullOrEmpty(caseloadDx)) {
				// filter on dx
				clSearchQuery="search_alldemo_dxfilter";
				clSearchParams = new String[2];
				clSearchParams[0] = caseloadDx;
				clSearchParams[1] = caseloadProgram;
			} else {
				// no dx filter
				clSearchQuery="search_alldemo_nofilter";
				clSearchParams = new String[1];
				clSearchParams[0] = caseloadProgram;
			}
		} else { // program and provider aren't all
			// demographics from a specific provider
			if (!StringUtils.isNullOrEmpty(caseloadDx) && !StringUtils.isNullOrEmpty(caseloadRoster)) {
				// filter on dx
				clSearchQuery="search_provdemo_rodxfilter";
				clSearchParams = new String[7];
				clSearchParams[0] = caseloadProv;
				clSearchParams[1] = caseloadProv;
				clSearchParams[2] = caseloadProv;
				clSearchParams[3] = caseloadProv;
				clSearchParams[4] = caseloadRoster;
				clSearchParams[5] = caseloadDx;
				clSearchParams[6] = caseloadProgram;
			} else if (!StringUtils.isNullOrEmpty(caseloadRoster)) {
				// no dx filter
				clSearchQuery="search_provdemo_rofilter";
				clSearchParams = new String[6];
				clSearchParams[0] = caseloadProv;
				clSearchParams[1] = caseloadProv;
				clSearchParams[2] = caseloadProv;
				clSearchParams[3] = caseloadProv;
				clSearchParams[4] = caseloadRoster;
				clSearchParams[5] = caseloadProgram;
			} else if (!StringUtils.isNullOrEmpty(caseloadDx)) {
				// filter on dx
				clSearchQuery="search_provdemo_dxfilter";
				clSearchParams = new String[6];
				clSearchParams[0] = caseloadProv;
				clSearchParams[1] = caseloadProv;
				clSearchParams[2] = caseloadProv;
				clSearchParams[3] = caseloadProv;
				clSearchParams[4] = caseloadDx;
				clSearchParams[5] = caseloadProgram;
			} else {
				// no dx filter
				clSearchQuery="search_provdemo_nofilter";
				clSearchParams = new String[5];
				clSearchParams[0] = caseloadProv;
				clSearchParams[1] = caseloadProv;
				clSearchParams[2] = caseloadProv;
				clSearchParams[3] = caseloadProv;
				clSearchParams[4] = caseloadProgram;
		    }
		}

		switch (caseloadCategory) {
			case Demographic:
			case Age:
			case Sex:
			case LastAppt:
			case NextAppt:
			case ApptsLYTD:
			case Tickler:
			case Msg:
				clSortParams = null;
				break;
			case Lab:
			case Doc:
				clSortParams = new String[1];
				clSortParams[0] = curUser_no;
				break;
			case BMI:
			case BP:
			case WT:
			case SMK:
			case A1C:
			case ACR:
			case SCR:
			case LDL:
			case HDL:
			case TCHD:
			case EGFR:
			case EYEE:
				clSortParams = new String[2];
				clSortParams[0] = caseloadCategory.getLabel();
				clSortParams[1] = caseloadCategory.getLabel();
				break;
			case LastEncounterDate:
			case LastEncounterType:
				clSortParams = new String[2];
				clSortParams[0] = caseloadCategory.getLabel();
				clSortParams[1] = caseloadCategory.getQuery();
 				break;
		}

		List<Integer> demoSearchResult = caseloadDao.getCaseloadDemographicSet(clSearchQuery, clSearchParams, clSortParams, caseloadCategory, sortAscending ? "ASC" : "DESC", caseloadPage, caseloadPageSize);
		JSONArray data = generateCaseloadDataForDemographics(request, response, caseloadProv, demoSearchResult);

		response.setContentType("text/x-json");
		JSONObject json = new JSONObject();
		json.put("data", data);

		if (caseloadPage == 0)
		{
			Integer size = caseloadDao.getCaseloadDemographicSearchSize(clSearchQuery, clSearchParams);
			json.put("size", size);
		}

		try
		{
			json.write(response.getWriter());
		} catch (IOException e) {
			MiscUtils.getLogger().error("Couldn't get data for caseload", e);
		}

		return null;
	}

	private JSONArray generateCaseloadDataForDemographics(HttpServletRequest request, HttpServletResponse response, String caseloadProv, List<Integer> demoSearchResult) throws UnsupportedEncodingException
	{
		JSONArray entry;
		String buttons;
		JSONArray data = new JSONArray();

		HttpSession session = request.getSession();

		String roleName$ = (String)session.getAttribute("userrole") + "," + (String) session.getAttribute("user");

		String curUser_no = (String) session.getAttribute("user");
		String userfirstname = (String) session.getAttribute("userfirstname");
	    String userlastname = (String) session.getAttribute("userlastname");

	    GregorianCalendar cal = new GregorianCalendar();
	    int curYear = cal.get(Calendar.YEAR);
	    int curMonth = (cal.get(Calendar.MONTH)+1);
	    int curDay = cal.get(Calendar.DAY_OF_MONTH);

	    int year = Integer.parseInt(request.getParameter("year"));
	    int month = Integer.parseInt(request.getParameter("month"));
	    int day = Integer.parseInt(request.getParameter("day"));

	    java.util.Date apptime=new java.util.Date();

	    OscarProperties oscarProperties = OscarProperties.getInstance();
	    boolean bShortcutForm = oscarProperties.getProperty("appt_formview", "").equalsIgnoreCase("on") ? true : false;
	    String formName = bShortcutForm ? oscarProperties.getProperty("appt_formview_name") : "";
		String formNameShort = formName.length() > 3 ? (formName.substring(0,2)+".") : formName;
	    String formName2 = bShortcutForm ? oscarProperties.getProperty("appt_formview_name2", "") : "";
		String formName2Short = formName2.length() > 3 ? (formName2.substring(0,2)+".") : formName2;
	    boolean bShortcutForm2 = bShortcutForm && !formName2.equals("");
	    boolean bShortcutIntakeForm = oscarProperties.getProperty("appt_intake_form", "").equalsIgnoreCase("on") ? true : false;

	    String monthDay = String.format("%02d", month) + "-" + String.format("%02d", day);

	    String prov = oscarProperties.getBillingTypeUpperCase();

		for (Integer result : demoSearchResult) {

			String demographic_no = result.toString();
			entry = new JSONArray();
			// name
			Demographic demographic = demographicDao.find(result);
			String clLastName = demographic.getLastName();
			String clFirstName = demographic.getFirstName();
			String clFullName = StringEscapeUtils.escapeJavaScript(clLastName + ", " + clFirstName).toUpperCase();
			entry.add(clFullName);

			// add E button to string
			buttons = "";
			if (hasPrivilege("_caseload.DisplayMode", roleName$)){
				if (hasPrivilege("_eChart", roleName$)) {
					String encType = "";
					try {
						encType = URLEncoder.encode("face to face encounter with client","UTF-8");
					} catch (UnsupportedEncodingException e) {
						MiscUtils.getLogger().error("Couldn't encode string", e);
					}
					String eURL = "../oscarEncounter/IncomingEncounter.do?providerNo="+curUser_no+"&appointmentNo=0&demographicNo="+demographic_no+"&curProviderNo="+caseloadProv+"&reason=&encType="+encType+"&userName="+URLEncoder.encode( userfirstname+" "+userlastname)+"&curDate="+curYear+"-"+curMonth+"-"+curDay+"&appointmentDate="+year+"-"+month+"-"+day+"&startTime="+apptime.getHours()+":"+apptime.getMinutes()+"&status=T"+"&apptProvider_no="+caseloadProv+"&providerview="+caseloadProv;
					buttons += "<a href='#' onClick=\"popupWithApptNo(710, 1024,'../oscarSurveillance/CheckSurveillance.do?demographicNo="+demographic_no+"&proceed="+URLEncoder.encode(eURL)+"', 'encounter');return false;\" title='Encounter'>E</a> ";
				}

				// add form links to string
				if (hasPrivilege("_billing", roleName$)) {
					buttons += bShortcutForm?"| <a href=# onClick='popupPage2( \"../form/forwardshortcutname.jsp?formname="+formName+"&demographic_no="+demographic_no+"\")' title='form'>"+formNameShort+"</a> " : "";
					buttons += bShortcutForm2?"| <a href=# onClick='popupPage2( \"../form/forwardshortcutname.jsp?formname="+formName2+"&demographic_no="+demographic_no+"\")' title='form'>"+formName2Short+"</a> " : "";
					buttons += (bShortcutIntakeForm) ? "| <a href='#' onClick='popupPage(700, 1024, \"formIntake.jsp?demographic_no="+demographic_no+"\")'>In</a> " : "";
				}

				// add B button to string
				if (hasPrivilege("_billing", roleName$)) {
					buttons += "| <a href='#' onClick=\"popupPage(700,1000,'../billing.do?skipReload=true&billRegion="+URLEncoder.encode(prov)+"&billForm="+URLEncoder.encode(oscarProperties.getProperty("default_view"))+"&hotclick=&appointment_no=0&demographic_name="+URLEncoder.encode(clLastName)+"%2C"+URLEncoder.encode(clFirstName)+"&demographic_no="+demographic_no+"&providerview=1&user_no="+curUser_no+"&apptProvider_no=none&appointment_date="+year+"-"+month+"-"+day+"&start_time=0:00&bNewForm=1&status=t');return false;\" title='Billing'>B</a> ";
					buttons += "| <a href='#' onClick=\"popupPage(700,1000,'../billing/CA/ON/billinghistory.jsp?demographic_no="+demographic_no+"&last_name="+URLEncoder.encode(clLastName)+"&first_name="+URLEncoder.encode(clFirstName)+"&orderby=appointment_date&displaymode=appt_history&dboperation=appt_history&limit1=0&limit2=10');return false;\" title='Billing'>BHx</a> ";
				}

				// add M button to string
				if (hasPrivilege("_masterLink", roleName$)) {
					buttons += "| <a href='#' onClick=\"popupPage(700,1000,'../demographic/demographiccontrol.jsp?demographic_no="+demographic_no+"&displaymode=edit&dboperation=search_detail');return false;\" title='Master File'>M</a> ";
				}

				// add Rx button to string
				if (isModuleLoaded(request, "TORONTO_RFQ", true) && hasPrivilege("_appointment.doctorLink", roleName$)) {
					buttons += "| <a href='#' onClick=\"popupOscarRx(700,1027,'../oscarRx/choosePatient.do?providerNo="+curUser_no+"&demographicNo="+demographic_no+"');return false;\">Rx</a> ";
				}

				// add Tickler button to string
				buttons += "| <a href='#' onclick=\"popupPage('700', '1000', '../tickler/ticklerAdd.jsp?name="+URLEncoder.encode(clLastName)+"%2C"+URLEncoder.encode(clFirstName)+"&chart_no=&bFirstDisp=false&demographic_no="+demographic_no+"&messageID=null&doctor_no="+curUser_no+"'); return false;\">T</a> ";

				// add Msg button to string
				buttons += "| <a href='#' onclick=\"popupPage('700', '1000', '../oscarMessenger/SendDemoMessage.do?demographic_no="+demographic_no+"'); return false;\">Msg</a> ";
				
				entry.add(buttons);
			}
			
			// age
			if (hasPrivilege("_caseload.Age", roleName$)){
				String clAge = String.valueOf(Period.between(demographic.getDateOfBirth(), LocalDate.now()).getYears());
				String clBDay = demographic.getMonthOfBirth() + "-" + demographic.getDayOfBirth();
				if (isBirthday(monthDay,clBDay)) {
					clAge += " <img src='../images/cake.gif' height='20' />";
				}
				entry.add(clAge);
			}

			// sex
			if (hasPrivilege("_caseload.Sex", roleName$)){
				String clSex = demographic.getSex();
				entry.add(clSex);
			}

			// last appt
			if (hasPrivilege("_caseload.LastAppt", roleName$)){
				Appointment lastAppointment = oscarAppointmentDao.findLastAppointment(demographic.getDemographicId());
				if (lastAppointment != null)
				{
					String clLappt = lastAppointment.getAppointmentDate().toString();
					entry.add("<a href='#' onclick=\"popupPage('700', '1000', '../demographic/demographiccontrol.jsp?demographic_no="+demographic_no+"&last_name="+URLEncoder.encode(clLastName)+"&first_name="+URLEncoder.encode(clFirstName)+"&orderby=appttime&displaymode=appt_history&dboperation=appt_history&limit1=0&limit2=25'); return false;\">"+ clLappt + "</a>");
				}
				else
				{
					entry.add("&nbsp;");
				}
			}

			// next appt
			if (hasPrivilege("_caseload.NextAppt", roleName$)){
				Appointment nextAppointment = oscarAppointmentDao.findNextAppointment(demographic.getDemographicId());
				if (nextAppointment != null)
				{
					String clNappt = nextAppointment.getAppointmentDate().toString();
					entry.add("<a href='#' onclick=\"popupPage('700', '1000', '../demographic/demographiccontrol.jsp?demographic_no="+demographic_no+"&last_name="+URLEncoder.encode(clLastName)+"&first_name="+URLEncoder.encode(clFirstName)+"&orderby=appttime&displaymode=appt_history&dboperation=appt_history&limit1=0&limit2=25'); return false;\">" + clNappt + "</a>");
				}
				else
				{
					entry.add("&nbsp;");
				}
			}

			// num appts in last year
			if (hasPrivilege("_caseload.ApptsLYTD", roleName$)){
				int numAppointments = oscarAppointmentDao.findAppointmentsWithinLastYear(demographic.getDemographicId());
				if (numAppointments > 0)
				{
					String clNumAppts = Integer.toString(numAppointments);
					entry.add(clNumAppts);
				}
				else
				{
					entry.add("&nbsp;");
				}

			}

			if (hasPrivilege("_caseload.Lab", roleName$)){
				int numNewLabs = caseloadDao.getNumberNewLabs(curUser_no, demographic.getDemographicId());
				if (numNewLabs > 0)
				{
					String clNewLab = Integer.toString(numNewLabs);
					entry.add("<a href='#' onclick=\"popupPage('700', '1000', '../dms/inboxManage.do?method=prepareForIndexPage&providerNo="+curUser_no+"&selectedCategory=CATEGORY_PATIENT_SUB&selectedCategoryPatient="+demographic_no+"&selectedCategoryType=CATEGORY_TYPE_HL7'); return false;\">" + clNewLab + "</a>");
				}
				else
				{
					entry.add("&nbsp;");
				}
			}
			
			// new docs
			if (hasPrivilege("_caseload.Doc", roleName$)){
				int numNewDocs = caseloadDao.getNumNewDocs(curUser_no, demographic.getDemographicId());
				if (numNewDocs > 0)
				{
					String clNewDoc = Integer.toString(numNewDocs);
					entry.add("<a href='#' onclick=\"popupPage('700', '1000', '../dms/inboxManage.do?method=prepareForIndexPage&providerNo="+curUser_no+"&selectedCategory=CATEGORY_PATIENT_SUB&selectedCategoryPatient="+demographic_no+"&selectedCategoryType=CATEGORY_TYPE_DOC'); return false;\">" + clNewDoc + "</a>");
				}
				else
				{
					entry.add("&nbsp;");
				}
			}

			// new ticklers
			if (hasPrivilege("_caseload.Tickler", roleName$)){
				int numTicklers = ticklerDao.getActiveTicklerCountForDemographic(demographic.getDemographicId());
				if (numTicklers > 0)
				{
					String clNewTickler = Integer.toString(numTicklers);
					entry.add("<a href='#' onclick=\"popupPage('700', '1000', '../tickler/ticklerMain.jsp?demoview="+demographic_no+"'); return false;\">" + clNewTickler + "</a>");
				}
				else
				{
					entry.add("&nbsp;");
				}
			}

			// new messages
			if (hasPrivilege("_caseload.Msg", roleName$)){
				int numMessages = msgDemoMapDao.getNumMessagesWithStatus(demographic.getDemographicId(), MessageList.STATUS_NEW);
				if (numMessages > 0)
				{
					String clNewMsg = Integer.toString(numMessages);
					entry.add("<a href='#' onclick=\"popupPage('700', '1000', '../oscarMessenger/DisplayDemographicMessages.do?orderby=date&boxType=3&demographic_no="+demographic_no+"&providerNo="+curUser_no+"&userName="+URLEncoder.encode(userfirstname+" "+userlastname)+"'); return false;\">" + clNewMsg + "</a>");
				}
				else
				{
					entry.add("&nbsp;");
				}
			}

			// measurements, in order as they appear on caseload
			// temporary measure until we can rework this function or we find a way to grab all of these in one shot
			List<String> caseloadMeasurements = Arrays.asList(
					CASELOAD_MEASUREMENT_BMI,
					CASELOAD_MEASUREMENT_BP,
					CASELOAD_MEASUREMENT_WT,
					CASELOAD_MEASUREMENT_SMK,
					CASELOAD_MEASUREMENT_A1C,
					CASELOAD_MEASUREMENT_ACR,
					CASELOAD_MEASUREMENT_SCR,
					CASELOAD_MEASUREMENT_LDL,
					CASELOAD_MEASUREMENT_HDL,
					CASELOAD_MEASUREMENT_TCHD,
					CASELOAD_MEASUREMENT_EGFR,
					CASELOAD_MEASUREMENT_EYEE
			);

			for (String caseloadMeasurement : caseloadMeasurements)
			{
				if (hasPrivilege("_caseload." + caseloadMeasurement, roleName$))
				{
					Measurement measurement = measurementDao.findLatestByDemographicNoAndType(demographic.getDemographicId(), caseloadMeasurement);
					String newEntry = "&nbsp;";
					if (measurement != null)
					{
						newEntry = "<a href='#' onClick=\"popupPage('700', '1000', '../oscarEncounter/oscarMeasurements/SetupDisplayHistory.do?demographicNo="
								+ URLEncoder.encode(demographic_no, "UTF-8")
								+ "&type="
								+ URLEncoder.encode(caseloadMeasurement, "UTF-8")
								+ "'); return false;\">"
								+ measurement.getDataField()
								+ "</a>";
					}
					entry.add(newEntry);
				}
			}

			// LastEncounterDate
			if (hasPrivilege("_caseload.LastEncounterDate", roleName$))
			{
				Date lastEncounterDate = caseloadDao.getLastEncounterDate(demographic.getDemographicId());
				if (lastEncounterDate != null)
				{
					String lastEncDate = ConversionUtils.toTimestampString(lastEncounterDate);
					entry.add(lastEncDate);
				}
				else
				{
					entry.add("&nbsp;");
				}
			}

			// LastEncounterType
			if (hasPrivilege("_caseload.LastEncounterType", roleName$)){
				String lastEncType = caseloadDao.getLastEncounterType(demographic.getDemographicId());
				if (lastEncType != null)
				{
					entry.add(lastEncType);
				}
				else
				{
					entry.add("&nbsp;");
				}
			}

			data.add(entry);
		}
		return data;
	}

	public boolean hasPrivilege(String objectName, String roleName) {
		ArrayList<Object> v = OscarRoleObjectPrivilege.getPrivilegePropAsArrayList(objectName);
		return OscarRoleObjectPrivilege.checkPrivilege(roleName, (Properties) v.get(0), (ArrayList<String>) v.get(1));
	}

	public boolean isModuleLoaded(HttpServletRequest request, String moduleName, boolean reverse) {
        OscarProperties proper = OscarProperties.getInstance();
        boolean result = false;
        if (proper.getProperty(moduleName, "").equalsIgnoreCase("yes") || proper.getProperty(moduleName, "").equalsIgnoreCase("true") || proper.getProperty(moduleName, "").equalsIgnoreCase("on")) {
            result = true;
        }
        return reverse ? !result : result;
    }

	/**
	Checks if the schedule day is patients birthday
	**/
	public boolean isBirthday(String schedDate,String demBday){
		return schedDate.equals(demBday);
	}
	public boolean patientHasOutstandingPrivateBills(String demographicNo){
		oscar.oscarBilling.ca.bc.MSP.MSPReconcile msp = new oscar.oscarBilling.ca.bc.MSP.MSPReconcile();
		return msp.patientHasOutstandingPrivateBill(demographicNo);
	}
}
