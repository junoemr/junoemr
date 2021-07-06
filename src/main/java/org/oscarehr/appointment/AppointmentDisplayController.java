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
package org.oscarehr.appointment;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.oscarehr.appointment.model.AppointmentStatusList;
import org.oscarehr.common.dao.DemographicDao;
import org.oscarehr.common.dao.ProviderPreferenceDao;
import org.oscarehr.common.model.Appointment;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.common.model.LookupListItem;
import org.oscarehr.common.model.ProviderPreference;
import org.oscarehr.schedule.dto.AppointmentDetails;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.oscarehr.web.admin.ProviderPreferencesUIBean;
import oscar.OscarProperties;
import oscar.SxmlMisc;
import oscar.util.UtilMisc;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

public class AppointmentDisplayController
{
	private static final String BILLED_STATUS = "B";
	private static final String DEFAULT_SHORT_LETTER_COLOR = "#FFFFFF";
	private static final String DO_NOT_BOOK_LABEL = "DO_NOT_BOOK";
	private static final String TOGGLEABLE_CLASS = "toggleable";
	private static final String HIDE_REASON_CLASS = "hideReason";
	private static final String URGENCY_CRITICAL = "critical";
	private static final String ROSTER_STATUS_FS = "FS";
	private static final String ROSTER_STATUS_RO = "RO";
	private static final String ROSTER_STATUS_NR = "NR";
	private static final String ROSTER_STATUS_PL = "PL";

	private DateTimeFormatter timeFormatter;
	private DateTimeFormatter timeFormatterWithSeconds;
	private DateTimeFormatter dateFormatter;

	private AppointmentDetails appointment;
	private String sessionProviderNo;   // Logged in provider from the session
	private Integer scheduleProviderNo; // Provider for the schedule column
	private String parameterProviderNo; // Provider passed in via curProvider parameter
	private String parameterProviderName;
	private boolean multisitesEnabled;
	private Map<String, String> siteBgColour;
	private AppointmentStatusList appointmentStatusList;
	private Locale locale;
	private boolean isWeekView;
	private int view;
	private int numAvailProvider;
	private int nameLength;
	private int longLengthLimit;
	private String viewAll;
	private Map<Integer,LookupListItem> reasonCodesMap;
	private boolean showDocumentLink;
	private boolean showEncounterLink;
	private boolean showOldEchartLink;
	private boolean enablePreventionWarnings;
	private String preventionWarnings;
	private String record;
	private String module;
	private String userFirstName;
	private String userLastName;
	private String currentUserNo;
	private boolean showTicklers;
	private boolean showDoctorLink;
	private boolean showMasterLink;
	private boolean showBilling;
	private boolean showEChart;

	private DemographicDao demographicDao = (DemographicDao) SpringUtils.getBean("demographicDao");
	private ProviderPreferenceDao providerPreferenceDao = SpringUtils.getBean(ProviderPreferenceDao.class);

	public void init(
		AppointmentDetails appointment,
		String sessionProviderNo,
		Integer scheduleProviderNo,
		String parameterProviderNo,
		String parameterProviderName,
		boolean multisitesEnabled,
		Map<String, String> siteBgColour,
		AppointmentStatusList appointmentStatusList,
		Locale locale,
		boolean isWeekView,
		int view,
		int numAvailProvider,
		int nameLength,
		int longLengthLimit,
		String viewAll,
		Map<Integer,LookupListItem> reasonCodesMap,
		boolean showDocumentLink,
		boolean showEncounterLink,
		boolean showOldEchartLink,
		boolean enablePreventionWarnings,
		String preventionWarnings,
		String record,
		String module,
		String userFirstName,
		String userLastName,
		String currentUserNo,
		boolean showTicklers,
		boolean showDoctorLink,
		boolean showMasterLink,
		boolean showBilling,
		boolean showEChart
	)
	{
		this.timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
		this.timeFormatterWithSeconds = DateTimeFormatter.ofPattern("HH:mm:ss");
		this.dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

		this.appointment = appointment;
		this.sessionProviderNo = sessionProviderNo;
		this.scheduleProviderNo = scheduleProviderNo;
		this.parameterProviderNo = parameterProviderNo;
		this.parameterProviderName = parameterProviderName;
		this.multisitesEnabled = multisitesEnabled;
		this.siteBgColour = siteBgColour;
		this.appointmentStatusList = appointmentStatusList;
		this.locale = locale;
		this.isWeekView = isWeekView;
		this.view = view;
		this.numAvailProvider = numAvailProvider;
		this.nameLength = nameLength;
		this.longLengthLimit = longLengthLimit;
		this.viewAll = viewAll;
		this.reasonCodesMap = reasonCodesMap;
		this.showDocumentLink = showDocumentLink;
		this.showEncounterLink = showEncounterLink;
		this.showOldEchartLink = showOldEchartLink;
		this.enablePreventionWarnings = enablePreventionWarnings;
		this.preventionWarnings = preventionWarnings;
		this.record = record;
		this.module = module;
		this.userFirstName = userFirstName;
		this.userLastName = userLastName;
		this.currentUserNo = currentUserNo;
		this.showTicklers = showTicklers;
		this.showDoctorLink = showDoctorLink;
		this.showMasterLink = showMasterLink;
		this.showBilling = showBilling;
		this.showEChart = showEChart;
	}

	public boolean isSelfBooked()
	{
		return Appointment.BookingSource.MYOSCAR_SELF_BOOKING.toString().equals(
			appointment.getBookingSource());
	}

	public boolean isVirtual()
	{
		return appointment.isVirtual();
	}

	public boolean isConfirmed()
	{
		return appointment.isConfirmed();
	}

	public boolean isMultisitesEnabled()
	{
		return multisitesEnabled;
	}

	public String getSiteName()
	{
		return String.valueOf(appointment.getLocation()).trim();
	}

	public String getSiteColour()
	{
		return siteBgColour.get(getSiteName());
	}

	public boolean isWeekView()
	{
		return isWeekView;
	}

	public boolean isBilled()
	{
		if (appointment.getStatus() == null || !appointment.getStatus().contains(BILLED_STATUS))
		{
			return false;
		} else
		{
			return true;
		}
	}

	public boolean isShowDocumentLink()
	{
		return showDocumentLink;
	}

	public boolean isShowEncounterLink()
	{
		return (!isWeekView && showEncounterLink);
	}

	public boolean isShowOldEchartLink()
	{
		return showOldEchartLink;
	}

	public boolean isShowIntakeFormLink()
	{
		return OscarProperties.getInstance().isAppointmentIntakeFormEnabled();
	}

	public boolean isShowEyeformLink()
	{
		return (OscarProperties.getInstance().isNewEyeformEnabled() && !isWeekView);
	}

	public boolean isShowDollarSign()
	{
		String province = OscarProperties.getInstance().getBillingTypeUpperCase();

		return (
			"bc".equalsIgnoreCase(province) &&
			patientHasOutstandingPrivateBills(appointment.getDemographicNo().toString())
		);
	}

	private boolean patientHasOutstandingPrivateBills(String demographicNo)
	{
		oscar.oscarBilling.ca.bc.MSP.MSPReconcile msp = new oscar.oscarBilling.ca.bc.MSP.MSPReconcile();
		return msp.patientHasOutstandingPrivateBill(demographicNo);
	}

	public boolean isSinglePageChart()
	{
		return OscarProperties.getInstance().isSinglePageChartEnabled();
	}

	public String getAppointmentNo()
	{
		return appointment.getAppointmentNo().toString();
	}

	public String getAppointmentTitle()
	{
		return appointmentStatusList.getTitle(appointment.getStatus(), locale);
	}

	public boolean isShowShortLetters()
	{
		return OscarProperties.getInstance().isAppointmentShowShortLettersEnabled();
	}

	public String getShortLetters()
	{
		return StringEscapeUtils.escapeHtml(appointment.getShortLetters());
	}

	public String getShortLetterColour()
	{
		String colour = appointment.getShortLetterColour();
		if (colour == null)
		{
			colour = DEFAULT_SHORT_LETTER_COLOR;
		}

		return colour;
	}

	public String getIconImage()
	{
		String iconImage = appointment.getIconImage();
		String status = appointment.getStatus();
		if (status == null)
		{
			return null;
		}
		else
		{
			if (status.length() >= 2)
			{
				iconImage = status.substring(1, 2) + iconImage;
			}

			return iconImage;
		}
	}

	public String getStatusTitle()
	{
		return appointment.getStatusTitle();
	}

	public String getRefreshURL()
	{
		String viewValue = "0";

		if (view != 0)
		{
			String curProviderName = "";
			try
			{
				curProviderName = URLEncoder.encode(parameterProviderName, "UTF-8");
			}
			catch(UnsupportedEncodingException e)
			{
				MiscUtils.getLogger().error("Refresh URL encoding error with string: " +
					parameterProviderName, e);
			}

			viewValue = "1&curProvider=" + parameterProviderNo + "&curProviderName=" + curProviderName;
		}

		String viewAllValue = "0";
		if (viewAll != null)
		{
			viewAllValue = viewAll;
		}

		String viewWeekValue = "";
		if (isWeekView)
		{
			viewWeekValue = "&viewWeek=1";
		}

		return "providercontrol.jsp" +
			"?appointment_no=" + appointment.getAppointmentNo() +
			"&provider_no=" + scheduleProviderNo.toString() +
			"&status=" +
			"&statusch=" + appointmentStatusList.getStatusAfter(appointment.getStatus()) +
			"&year=" + appointment.getDate().getYear() +
			"&month=" + appointment.getDate().getMonthValue() +
			"&day=" + appointment.getDate().getDayOfMonth() +
			"&view=" + viewValue +
			"&displaymode=addstatus" +
			"&dboperation=updateapptstatus" +
			"&viewall=" + viewAllValue +
			viewWeekValue;

	}

	public String getAppointmentURL()
	{
		return "../appointment/appointmentcontrol.jsp" +
			"?appointment_no=" + appointment.getAppointmentNo() +
			"&provider_no=" + scheduleProviderNo.toString() +
			"&year=" + appointment.getDate().getYear() +
			"&month=" + appointment.getDate().getMonthValue() +
			"&day=" + appointment.getDate().getDayOfMonth() +
			"&start_time=" + appointment.getStartTime().format(timeFormatter) +
			"&demographic_no=" + appointment.getDemographicNo().toString() +
			"&displaymode=edit" +
			"&dboperation=search";
	}

	public String getEformURL()
	{
		return "../eform/efmformslistadd.jsp" +
			"?parentAjaxId=eforms" +
			"&demographic_no=" + appointment.getDemographicNo().toString() +
			"&appointment=" + appointment.getAppointmentNo().toString();
	}

	public String getIncomingEncounterURL()
	{
		try
		{
			return "../oscarEncounter/IncomingEncounter.do" +
				"?providerNo=" + sessionProviderNo +
				"&appointmentNo=" + appointment.getAppointmentNo().toString() +
				"&demographicNo=" + appointment.getDemographicNo() +
				"&curProviderNo=" + parameterProviderNo +
				"&reason=" + URLEncoder.encode(StringUtils.trimToEmpty(appointment.getReason()), "UTF-8") +
				"&encType=" + URLEncoder.encode("face to face encounter with client","UTF-8") +
				"&userName=" + URLEncoder.encode( userFirstName + " " + userLastName, "UTF-8") +
				"&curDate=" + LocalDate.now().format(dateFormatter) +
				"&appointmentDate=" + appointment.getDate().format(dateFormatter) +
				"&startTime=" + appointment.getStartTime().format(timeFormatterWithSeconds) +
				"&status=" + appointment.getStatus() +
				"&apptProvider_no=" + scheduleProviderNo +
				"&providerview=" + scheduleProviderNo;

		}
		catch(UnsupportedEncodingException e)
		{
			MiscUtils.getLogger().error("Incoming URL encoding error with string: " +
				parameterProviderName, e);
		}

		return "";
	}

	public String getBillLink()
	{
		String province = OscarProperties.getInstance().getBillingTypeUpperCase();
		String defaultView = OscarProperties.getInstance().getProperty("default_view");
		Demographic demographic = demographicDao.getDemographicById(appointment.getDemographicNo());
		String referralNoParameter = "";

		if(oscar.OscarProperties.getInstance().isPropertyActive("auto_populate_billingreferral_bc"))
		{
			String rdohip = SxmlMisc.getXmlContent(StringUtils.trimToEmpty(demographic.getFamilyDoctor()),"rdohip");
			rdohip = rdohip !=null ? rdohip : "" ;
			referralNoParameter = "&referral_no_1=" + rdohip;
		}

		String mrpPreferredView = null;

		if (demographic.getProviderNo() != null && !demographic.getProviderNo().isEmpty())
		{
			ProviderPreference mrpPreference = providerPreferenceDao.find(demographic.getProviderNo());
			if (mrpPreference != null)
			{
				mrpPreferredView = mrpPreference.getDefaultServiceType();
			}
		}

		if (mrpPreferredView != null && !mrpPreferredView.equals("no"))
		{
			defaultView = mrpPreferredView;
		} else
		{
			ProviderPreference currentUserPreference = providerPreferenceDao.find(sessionProviderNo);
			String currentUserPreferredView = null;

			if (currentUserPreference != null)
			{
				currentUserPreferredView = currentUserPreference.getDefaultServiceType();
			}

			if (currentUserPreferredView != null && !currentUserPreferredView.equals("no"))
			{
				defaultView = currentUserPreferredView;
			}
		}

		try
		{
			return "../billing.do" +
				"?billRegion=" + URLEncoder.encode(province, "UTF-8") +
				"&billForm=" + URLEncoder.encode(defaultView, "UTF-8") +
				"&hotclick=" +
				"&appointment_no=" + appointment.getAppointmentNo().toString() +
				"&demographic_name=" + URLEncoder.encode(getName(), "UTF-8") +
				"&status=" + appointment.getStatus() +
				"&demographic_no=" + appointment.getDemographicNo().toString() +
				"&providerview=" + scheduleProviderNo+
				"&user_no=" + currentUserNo +
				"&apptProvider_no=" + scheduleProviderNo +
				"&appointment_date=" + appointment.getDate().format(dateFormatter) +
				"&start_time=" + appointment.getStartTime().format(timeFormatterWithSeconds) +
				"&bNewForm=1" + referralNoParameter +
				"&appointment_site=" + appointment.getLocation();
		}
		catch(UnsupportedEncodingException e)
		{
			MiscUtils.getLogger().error("Billing link URL encoding error with string: " +
				parameterProviderName, e);
		}

		return "";
	}

	public String getUnbillURL()
	{
		String province = OscarProperties.getInstance().getBillingTypeUpperCase();

		return "../billing/CA/" + province + "/billingDeleteWithoutNo.jsp" +
			"?status=" + appointment.getStatus() +
			"&appointment_no=" + appointment.getAppointmentNo();
	}

	private String getReasonCodeName()
	{
		String reasonCodeName = "";
		if(appointment.getReasonCode() != null)    {
			LookupListItem lli  = reasonCodesMap.get(appointment.getReasonCode());
			if(lli != null) {
				reasonCodeName = lli.getLabel();
			}
		}
		return reasonCodeName;
	}

	public String getReason()
	{
		return StringEscapeUtils.escapeHtml(appointment.getReason());
	}

	private boolean isReasonToggleable()
	{
		return !DO_NOT_BOOK_LABEL.equalsIgnoreCase(getName());
	}

	public String getReasonToggleableClass()
	{

		if(isReasonToggleable())
		{
			return TOGGLEABLE_CLASS;
		}
		else
		{
			return "";
		}
	}

	private boolean isToggleReasonByProvider()
	{
		return OscarProperties.getInstance().isToggleReasonByProviderEnabled();
	}

	public String getHideReasonClass()
	{
		if(!isWeekView && isToggleReasonByProvider() && isReasonToggleable())
		{
			return HIDE_REASON_CLASS;
		}
		else
		{
			return "";
		}
	}

	public String getFormattedReason()
	{
		String out_string = "";

		ProviderPreference prefs = ProviderPreferencesUIBean.getProviderPreference(this.sessionProviderNo);
		// Not all providers will have a ProviderPreference, or an AppointmentReasonDisplayLevel until they save one to their profile.
		// So we will consider nulls for each to be equivalent to the default value.
		
		if (prefs == null || prefs.getAppointmentReasonDisplayLevel() == null || prefs.getAppointmentReasonDisplayLevel() == ProviderPreference.AppointmentReasonDisplayLevel.DEFAULT_ALL)
		{
			String reasonCodeName = getReasonCodeName();
			if (reasonCodeName != null && !reasonCodeName.isEmpty())
			{
				out_string = "&nbsp;" + reasonCodeName + " -";
			}
		}

		if (appointment.getReason() != null)
		{
			out_string += "&nbsp;" + StringEscapeUtils.escapeHtml(appointment.getReason());
		}

		return out_string;
	}

	public String getAppointmentLinkTitle()
	{
		String title = appointment.getStartTime().format(timeFormatter) + "-";
		title += appointment.getEndTime().format(timeFormatter) + "\n";
		title += getName() + "\n";

		if(appointment.getType() != null)
		{
			title += "type: " + appointment.getType() + "\n";
		}

		title += "reason: " + getReasonCodeName();

		if (appointment.getReason() != null && !appointment.getReason().isEmpty())
		{
			title += " - " + StringEscapeUtils.escapeHtml(appointment.getReason());
		}

		title += "\nnotes: " + StringEscapeUtils.escapeHtml(appointment.getNotes());

		return " title=\"" + title + "\"";
	}

	public boolean isCriticalUrgency()
	{
		return (appointment.getUrgency() != null && appointment.getUrgency().equals(URGENCY_CRITICAL));
	}

	public boolean isEmptyDemographic()
	{
		return (appointment.getDemographicNo() == 0);
	}

	public boolean isShowTickler()
	{
		return showTicklers && appointment.hasTicklers();
	}

	public boolean isShowDoctorLink()
	{
		return showDoctorLink;
	}

	public boolean isShowMasterLink()
	{
		return showMasterLink;
	}

	public boolean isShowBilling()
	{
		return showBilling;
	}

	public boolean isShowEChart()
	{
		return showEChart;
	}

	public String getTicklerNote()
	{
		return StringEscapeUtils.escapeHtml(appointment.getTicklerMessages());
	}

	public boolean isDisplayAlerts()
	{
		return (
			OscarProperties.getInstance().isDisplayAlertsOnScheduleScreenEnabled() &&
			appointment.getCustAlert() != null &&
			!appointment.getCustAlert().isEmpty()
		);
	}

	public String getAlert()
	{
		return StringEscapeUtils.escapeHtml(appointment.getCustAlert());
	}

	public boolean isDisplayNotes()
	{
		return (
			OscarProperties.getInstance().isDisplayAlertsOnScheduleScreenEnabled() &&
			appointment.getCustNotes() != null &&
			!SxmlMisc.getXmlContent(appointment.getCustNotes(), "<unotes>", "</unotes>")
				.isEmpty()
		);
	}

	public String getNotes()
	{
		return StringEscapeUtils.escapeHtml(
			SxmlMisc.getXmlContent(appointment.getCustNotes(), "<unotes>", "</unotes>"));
	}

	public String getName()
	{
		StringBuilder nameSb = new StringBuilder();
		if(appointment.getDemographicNo() != 0)
		{
			nameSb.append(appointment.getLastName())
				.append(",")
				.append(appointment.getFirstName());
		}
		else
		{
			nameSb.append(String.valueOf(appointment.getName()));
		}

		return UtilMisc.toUpperLowerCase(nameSb.toString());
	}

	public String getTruncatedName()
	{
		String name = getName();

		if (view == 0 && name.length() > nameLength)
		{
			return name.substring(0, nameLength);
		}

		return name;
	}

	public String getTruncatedUpperName()
	{
		String name = getName();

		if (view == 0 && numAvailProvider != 1 && name.length() > nameLength)
		{
			return name.substring(0, nameLength).toUpperCase();
		}

		return getTruncatedName().toUpperCase();
	}

	public String getSessionProviderNo()
	{
		return sessionProviderNo;
	}

	public String getScheduleProviderNo()
	{
		return scheduleProviderNo.toString();
	}

	public String getCurrentProviderNo()
	{
		return parameterProviderNo;
	}

	public String getDemographicNo()
	{
		return appointment.getDemographicNo().toString();
	}

	public String getDemographicHin() 
	{ 
		return appointment.getHin(); 
	}

	public String getDemographicChartNo() 
	{ 
		return appointment.getChart_no(); 
	}

	public boolean isShowVerLink()
	{
		return "##".equals(appointment.getVer());
	}

	public String getVer()
	{
		return StringEscapeUtils.escapeHtml(appointment.getVer());
	}

	public boolean isShowFSRosterLink()
	{
		return ROSTER_STATUS_FS.equalsIgnoreCase(appointment.getRosterStatus());
	}

	public boolean isShowRORosterLink()
	{
		return ROSTER_STATUS_RO.equalsIgnoreCase(appointment.getRosterStatus());
	}

	public boolean isShowNRorPLRosterLink()
	{
		return (
			ROSTER_STATUS_NR.equalsIgnoreCase(appointment.getRosterStatus()) ||
			ROSTER_STATUS_PL.equalsIgnoreCase(appointment.getRosterStatus())
		);
	}

	public String getRosterStatus()
	{
		return StringEscapeUtils.escapeHtml(appointment.getRosterStatus());
	}

	public boolean isShowPreventionWarnings()
	{
		return enablePreventionWarnings;
	}

	public String getPreventionWarnings()
	{
		return preventionWarnings;
	}

	public boolean isActiveMedicalCoverage()
	{
		return (
			appointment.getHcRenewDate() != null &&
			appointment.getHcRenewDate().getYear() == LocalDate.now().getYear() &&
			appointment.getHcRenewDate().getMonthValue() == LocalDate.now().getMonthValue()
		);
	}

	public boolean isShowAppointmentLinks()
	{
		return(
			nameLength == longLengthLimit ||
			view != 0 ||
			numAvailProvider == 1 ||
			OscarProperties.getInstance().isAppoinmtnetAlwaysShowLinksEnabled()
		);
	}

	public String getSinglePageChartURL()
	{
		String newUxUrl = "../web/#/record/" + appointment.getDemographicNo() + "/";

		if(appointment.getDemographicNo().toString().equals(record) && !module.equals("summary"))
		{
			return newUxUrl + module;
		}
		else
		{
			return newUxUrl + "summary?appointmentNo=" + appointment.getAppointmentNo() +
				"&encType=face%20to%20face%20encounter%20with%20client";
		}
	}

	public String getSinglePageChartStyle()
	{
		if(appointment.getDemographicNo().toString().equals(record) && !module.equals("summary"))
		{
			return "style='color: blue;'";
		}

		return "";
	}

	public boolean isHasProviderColor()
	{
		return "".equals(getProviderColor());
	}

	public String getProviderColor()
	{
		if(view == 1)
		{
			if (appointment.getColorProperty() != null)
			{
				return appointment.getColorProperty();
			}
		}

		return null;
	}

	public boolean isBirthday()
	{
		LocalDate appointmentDate = appointment.getDate();
		LocalDate birthday = appointment.getBirthday();

		if(appointmentDate == null || birthday == null)
		{
			return false;
		}

		return (
			appointmentDate.getMonthValue() == birthday.getMonthValue() &&
			appointmentDate.getDayOfMonth() == birthday.getDayOfMonth()
		);
	}
}
