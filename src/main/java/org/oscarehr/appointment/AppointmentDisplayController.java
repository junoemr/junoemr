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

package org.oscarehr.appointment;

import org.apache.commons.lang.StringEscapeUtils;
import org.oscarehr.appointment.model.AppointmentStatusList;
import org.oscarehr.common.model.Appointment;
import org.oscarehr.common.model.LookupListItem;
import org.oscarehr.schedule.dto.AppointmentDetails;
import org.oscarehr.util.MiscUtils;
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
	public static final String BILLED_STATUS = "B";

	private DateTimeFormatter timeFormatter;
	private DateTimeFormatter dateFormatter;

	private AppointmentDetails appointment;
	private boolean multisitesEnabled;
	private Map<String, String> siteBgColour;
	private AppointmentStatusList appointmentStatusList;
	private Locale locale;
	private boolean isWeekView;
	private int view;
	private int numAvailProvider;
	private int nameLength;
	private int longLengthLimit;
	private String currentProvider;
	private String currentProviderName;
	private String viewAll;
	private Integer providerNo;
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

	public void init(
		AppointmentDetails appointment,
		boolean multisitesEnabled,
		Map<String, String> siteBgColour,
		AppointmentStatusList appointmentStatusList,
		Locale locale,
		boolean isWeekView,
		int view,
		int numAvailProvider,
		int nameLength,
		int longLengthLimit,
		String currentProvider,
		String currentProviderName,
		String viewAll,
		Integer providerNo,
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
		this.dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

		this.appointment = appointment;
		this.multisitesEnabled = multisitesEnabled;
		this.siteBgColour = siteBgColour;
		this.appointmentStatusList = appointmentStatusList;
		this.locale = locale;
		this.isWeekView = isWeekView;
		this.view = view;
		this.numAvailProvider = numAvailProvider;
		this.nameLength = nameLength;
		this.longLengthLimit = longLengthLimit;
		this.currentProvider = currentProvider;
		this.currentProviderName = currentProviderName;
		this.viewAll = viewAll;
		this.providerNo = providerNo;
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
			this.appointment.getBookingSource());
	}

	public boolean isMultisitesEnabled()
	{
		return this.multisitesEnabled;
	}

	public String getSiteName()
	{
		return String.valueOf(this.appointment.getLocation()).trim();
	}

	public String getSiteColour()
	{
		return this.siteBgColour.get(getSiteName());
	}

	public boolean isWeekView()
	{
		return isWeekView;
	}

	public boolean isBilled()
	{
		return (appointment.getStatus().equals(BILLED_STATUS));
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
		return OscarProperties.getInstance().isPropertyActive("appt_intake_form");
	}

	public boolean isShowEyeformLink()
	{
		return (OscarProperties.getInstance().isPropertyActive("new_eyeform_enabled") && !isWeekView);
	}

	public boolean isShowDollarSign()
	{
		String province = OscarProperties.getInstance().getBillingTypeUpperCase();

		return (
			"bc".equalsIgnoreCase(province) &&
			patientHasOutstandingPrivateBills(appointment.getDemographicNo().toString())
		);
	}

	public boolean patientHasOutstandingPrivateBills(String demographicNo)
	{
		oscar.oscarBilling.ca.bc.MSP.MSPReconcile msp = new oscar.oscarBilling.ca.bc.MSP.MSPReconcile();
		return msp.patientHasOutstandingPrivateBill(demographicNo);
	}

	public boolean isSinglePageChart()
	{
		return OscarProperties.getInstance().isPropertyActive("SINGLE_PAGE_CHART");
	}

	public String getAppointmentNo()
	{
		return appointment.getAppointmentNo().toString();
	}

	public String getAppointmentTitle()
	{
		return appointmentStatusList.getTitle(appointment.getStatus(), this.locale);
	}

	public boolean isShowShortLetters()
	{
		return (
			OscarProperties.getInstance().getProperty("APPT_SHOW_SHORT_LETTERS", "false") != null &&
				OscarProperties.getInstance().getProperty("APPT_SHOW_SHORT_LETTERS", "false")
					.equals("true")
		);
	}

	public String getShortLetters()
	{
		return UtilMisc.htmlEscape(this.appointment.getShortLetters());
	}

	public String getColour()
	{
		String colour = this.appointment.getShortLetterColour();
		if (colour == null)
		{
			colour = "#FFFFFF";
		}

		return colour;
	}

	public String getIconImage()
	{
		return appointment.getIconImage();
	}

	public String getStatusTitle()
	{
		return appointment.getStatusTitle();
	}

	public String getRefreshURL()
	{
		String viewValue = "0";

		if (this.view != 0)
		{
			String curProviderName = "";
			try
			{
				curProviderName = URLEncoder.encode(this.currentProviderName, "UTF-8");
			}
			catch(UnsupportedEncodingException e)
			{
				MiscUtils.getLogger().error("Refresh URL encoding error with string: " +
					this.currentProviderName, e);
			}

			viewValue = "1&curProvider=" + this.currentProvider +
				"&curProviderName=" + curProviderName;
		}

		String viewAllValue = "0";
		if (viewAll != null)
		{
			viewAllValue = viewAll;
		}

		String viewWeekValue = "";
		if (this.isWeekView)
		{
			viewWeekValue = "&viewWeek=1";
		}

		return "providercontrol.jsp" +
			"?appointment_no=" + this.appointment.getAppointmentNo() +
			"&provider_no=" + this.providerNo.toString() +
			"&status=" +
			"&statusch=" + this.appointmentStatusList.getStatusAfter(appointment.getStatus()) +
			"&year=" + this.appointment.getDate().getYear() +
			"&month=" + this.appointment.getDate().getMonthValue() +
			"&day=" + this.appointment.getDate().getDayOfMonth() +
			"&view=" + viewValue +
			"&displaymode=addstatus" +
			"&dboperation=updateapptstatus" +
			"&viewall=" + viewAllValue +
			viewWeekValue;

	}

	public String getAppointmentURL()
	{
		return "../appointment/appointmentcontrol.jsp" +
			"?appointment_no=" + this.appointment.getAppointmentNo() +
			"&provider_no=" + this.providerNo.toString() +
			"&year=" + this.appointment.getDate().getYear() +
			"&month=" + this.appointment.getDate().getMonthValue() +
			"&day=" + this.appointment.getDate().getDayOfMonth() +
			"&start_time=" + this.appointment.getStartTime().format(this.timeFormatter) +
			"&demographic_no=" + this.appointment.getDemographicNo().toString() +
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
				"?providerNo=" + providerNo +
				"&appointmentNo=" + appointment.getAppointmentNo().toString() +
				"&demographicNo=" + appointment.getDemographicNo() +
				"&curProviderNo=" + currentProvider +
				"&reason=" + getReason() +
				"&encType=" + URLEncoder.encode("face to face encounter with client","UTF-8") +
				"&userName=" + URLEncoder.encode( userFirstName + " " + userLastName, "UTF-8") +
				"&curDate=" + LocalDate.now().format(dateFormatter) +
				"&appointmentDate=" + appointment.getDate().format(dateFormatter) +
				"&start_time=" + appointment.getStartTime().format(timeFormatter) +
				"&status=" + appointment.getStatus() +
				"&apptProvider_no=" + currentProvider +
				"&providerview=" + currentProvider;

		}
		catch(UnsupportedEncodingException e)
		{
			MiscUtils.getLogger().error("Incoming URL encoding error with string: " +
				this.currentProviderName, e);
		}

		return "";
	}

	public String getBillLink()
	{
		String province = OscarProperties.getInstance().getBillingTypeUpperCase();
		String default_view = OscarProperties.getInstance().getProperty("default_view");

		return "../billing.do" +
			"?billRegion=" + URLEncoder.encode(province) +
			"&billForm=" + URLEncoder.encode(default_view) +
			"&hotclick=" +
			"&appointment_no=" + appointment.getAppointmentNo().toString() +
			"&demographic_name=" + getName() +
			"&status=" + appointment.getStatus() +
			"&demographic_no=" + appointment.getDemographicNo().toString() +
			"&providerview=" + currentProvider +
			"&user_no=" + currentUserNo +
			"&apptProvider_no=" + currentProvider +
			"&appointmentDate=" + appointment.getDate().format(dateFormatter) +
			"&start_time=" + appointment.getStartTime().format(timeFormatter) +
			"&bNewForm=1";

	}

	public String getUnbillURL()
	{
		String province = OscarProperties.getInstance().getBillingTypeUpperCase();

		return "../billing/CA/" + province + "/billingDeleteWithoutNo.jsp" +
			"?status=" + appointment.getStatus() +
			"&appointment_no=" + appointment.getAppointmentNo();
	}

	public String getReasonCodeName()
	{
		String reasonCodeName = "";
		if(this.appointment.getReasonCode() != null)    {
			LookupListItem lli  = this.reasonCodesMap.get(appointment.getReasonCode());
			if(lli != null) {
				reasonCodeName = lli.getLabel();
			}
		}
		return reasonCodeName;
	}

	public String getReason()
	{
		return UtilMisc.htmlEscape(this.appointment.getReason());
	}

	public boolean isReasonToggleable()
	{
		return !("DO_NOT_BOOK").equalsIgnoreCase(this.getName());
	}

	public String getReasonToggleableClass()
	{

		if(isReasonToggleable())
		{
			return "toggleable";
		}
		else
		{
			return "";
		}
	}

	public boolean isToggleReasonByProvider()
	{
		return OscarProperties.getInstance().isPropertyActive("TOGGLE_REASON_BY_PROVIDER", true);
	}

	public String getHideReasonClass()
	{
		if(isToggleReasonByProvider() && isReasonToggleable())
		{
			return "hideReason";
		}
		else
		{
			return "";
		}
	}

	public String getFormattedReason()
	{
		String out_string = "";

		String reasonCodeName = getReasonCodeName();
		if(!"".equals(reasonCodeName))
		{
			out_string = "&nbsp;" + reasonCodeName + " -";
		}

		if(this.appointment.getReason() != null)
		{
			out_string += "&nbsp;" + UtilMisc.htmlEscape(this.appointment.getReason());
		}

		return out_string;
	}

	public String getAppointmentLinkTitle()
	{
		String title = this.appointment.getStartTime().format(this.timeFormatter) + "-";
		title += this.appointment.getEndTime().format(timeFormatter) + "\n";
		title += this.getName() + "\n";

		if(this.appointment.getType() != null)
		{
			title += "type: " + this.appointment.getType() + "\n";
		}

		title += "reason: " + this.getReasonCodeName();

		if (this.appointment.getReason() != null && !this.appointment.getReason().isEmpty())
		{
			title += "- " + UtilMisc.htmlEscape(this.appointment.getReason() + "\n");
		}

		title += "notes: " + UtilMisc.htmlEscape(this.appointment.getNotes());

		return " title=\"" + title + "\"";
	}

	public boolean isCriticalUrgency()
	{
		return (this.appointment.getUrgency() != null && this.appointment.getUrgency().equals("critical"));
	}

	public boolean isEmptyDemographic()
	{
		return (appointment.getDemographicNo() == 0);
	}

	public boolean isShowTickler()
	{
		return this.showTicklers && this.appointment.hasTicklers();
	}

	public boolean isShowDoctorLink()
	{
		return this.showDoctorLink;
	}

	public boolean isShowMasterLink()
	{
		return this.showMasterLink;
	}

	public boolean isShowBilling()
	{
		return this.showBilling;
	}

	public boolean isShowEChart()
	{
		return this.showEChart;
	}

	public String getTicklerNote()
	{
		return UtilMisc.htmlEscape(this.appointment.getTicklerMessages());
	}

	public boolean isDisplayAlerts()
	{
		return (
			OscarProperties.getInstance().isPropertyActive("displayAlertsOnScheduleScreen") &&
				this.appointment.getCustAlert() != null &&
				!this.appointment.getCustAlert().isEmpty()
		);
	}

	public String getAlert()
	{
		return StringEscapeUtils.escapeHtml(this.appointment.getCustAlert());
	}

	public boolean isDisplayNotes()
	{
		return (
			OscarProperties.getInstance().isPropertyActive("displayNotesOnScheduleScreen") &&
				this.appointment.getCustNotes() != null &&
				!SxmlMisc.getXmlContent(this.appointment.getCustNotes(), "<unotes>", "</unotes>")
					.isEmpty()
		);
	}

	public String getNotes()
	{
		return StringEscapeUtils
			.escapeHtml(SxmlMisc.getXmlContent(this.appointment.getCustNotes(), "<unotes>", "</unotes>"));
	}

	public String getName()
	{
		StringBuilder nameSb = new StringBuilder();
		if(this.appointment.getDemographicNo() != 0)
		{
			nameSb.append(this.appointment.getLastName())
				.append(",")
				.append(this.appointment.getFirstName());
		}
		else
		{
			nameSb.append(String.valueOf(appointment.getName()));
		}

		return UtilMisc.toUpperLowerCase(nameSb.toString());
	}

	public String getTruncatedName()
	{
		String name = this.getName();

		if (this.view == 0 && this.numAvailProvider != 1 && name.length() > this.nameLength)
		{
			return name.substring(0, this.nameLength);
		}

		return name;
	}

	public String getTruncatedUpperName()
	{
		return this.getTruncatedName().toUpperCase();
	}

	public String getProviderNo()
	{
		return providerNo.toString();
	}

	public String getCurrentProviderNo()
	{
		return currentProvider;
	}

	public String getCurrentUserNo()
	{
		return currentUserNo;
	}

	public String getDemographicNo()
	{
		return this.appointment.getDemographicNo().toString();
	}

	public boolean isShowVerLink()
	{
		return "##".equals(appointment.getVer());
	}

	public String getVer()
	{
		return UtilMisc.htmlEscape(appointment.getVer());
	}

	public boolean isShowFSRosterLink()
	{
		return "FS".equalsIgnoreCase(appointment.getRosterStatus());
	}

	public boolean isShowNRorPLRosterLink()
	{
		return (
			"NR".equalsIgnoreCase(appointment.getRosterStatus()) ||
			"PL".equalsIgnoreCase(appointment.getRosterStatus())
		);
	}

	public String getRosterStatus()
	{
		return UtilMisc.htmlEscape(appointment.getRosterStatus());
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
			this.nameLength == this.longLengthLimit ||
			this.view != 0 ||
			this.numAvailProvider == 1 ||
			OscarProperties.getInstance().isPropertyActive("APPT_ALWAYS_SHOW_LINKS")
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
