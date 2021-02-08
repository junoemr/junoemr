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


package org.oscarehr.util;

import java.io.IOException;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.oscarehr.common.model.UserProperty;
import org.oscarehr.integration.model.CmeJs;
import org.oscarehr.preferences.service.SystemPreferenceService;
import org.oscarehr.provider.web.CppPreferencesUIBean;

public class CustomInterfaceTag extends TagSupport {

	private Logger logger = MiscUtils.getLogger();
	private SystemPreferenceService systemPreferenceService = SpringUtils.getBean(SystemPreferenceService.class);

	private String name;
	private String section;

	@Override
	public int doStartTag() throws JspException {
		String customJs = getCustomJs();

		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);

		if (CmeJs.DEFAULT.label.equals(customJs) && getSection().equals("cme"))
		{
			//check preferences
			CppPreferencesUIBean bean = new CppPreferencesUIBean(loggedInInfo.getLoggedInProviderNo());
			bean.loadValues();
			if ("on".equals(bean.getEnable()))
			{
				writePreferenceUIBean(bean);
				return SKIP_BODY;
			}
		}

		if (StringUtils.isNotEmpty(customJs))
		{
			writeTagWithCustomJs(customJs);
		}
		return SKIP_BODY;
	}

	private String getCustomJs()
	{
		String customJs = CmeJs.DEFAULT.label;

		if (StringUtils.isNotEmpty(name))
		{
			customJs = name;
		}
		else
		{
			boolean hasOceanToolBar = systemPreferenceService.isPreferenceEnabled(UserProperty.OCEAN_TOOLBAR_ENABLED, false);
			if (hasOceanToolBar)
			{
				customJs = CmeJs.OCEAN_TOOLBAR.label;
			}
		}

		return customJs;
	}

	/**
	 * Writes the CppPreferencesUIBean to the page context.
	 * @param bean
	 */
	private void writePreferenceUIBean(CppPreferencesUIBean bean)
	{
		logger.info("Use preference based echart");
		try
		{
			JspWriter out = super.pageContext.getOut();
			out.println(this.getPreferenceBasedEChart(bean));
		}
		catch (IOException e)
		{
			logger.error("Error:", e);
		}
	}

	/**
	 * This writes a tag if the custom JavaScript value has been set.
	 * @param customJs
	 */
	private void writeTagWithCustomJs(String customJs)
	{
		JspWriter out = super.pageContext.getOut();
		String contextPath = this.pageContext.getServletContext().getContextPath();
		try
		{
			out.println("<script src=\"" + contextPath + "/js/custom/" + customJs + "/global.js\"></script>");
			if (StringUtils.isNotEmpty(getSection()))
			{
				int randomNo = new Random().nextInt();
				out.println("<script src=\"" + contextPath + "/js/custom/" + customJs + "/" + getSection() + ".js?no-cache=" + randomNo + "\"></script>");
			}
		}
		catch (IOException e)
		{
			logger.error("Error", e);
		}
	}

	/*
	@Override
	public int doEndTag() throws JspException {		        
		return EVAL_PAGE;	   
	}
	 */
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSection() {
		return section;
	}
	public void setSection(String section) {
		this.section = section;
	}
	
	
	public String getPreferenceBasedEChart(CppPreferencesUIBean bean) {		
		StringBuilder sb = new StringBuilder();
		sb.append("<script>");
		sb.append("jQuery(document).ready(function(){");
		sb.append("issueNoteUrls = {");
		boolean flag=false, row1=false, row2=false;
		if(!bean.getSocialHxPosition().equals("")) {
			sb.append("div"+bean.getSocialHxPosition()+":    ctx + \"/CaseManagementView.do?hc=996633&method=listNotes&providerNo=\" + providerNo + \"&demographicNo=\" + demographicNo + \"&issue_code=SocHistory&title=\" + socHistoryLabel + \"&cmd=div"+bean.getSocialHxPosition()+"\"");
			flag=true;
			if(bean.getSocialHxPosition().startsWith("R1")) {row1=true;}
			if(bean.getSocialHxPosition().startsWith("R2")) {row2=true;}
		}
		if(!bean.getMedicalHxPosition().equals("")) {
			if(flag) {sb.append(",");}
			sb.append("div"+bean.getMedicalHxPosition()+":    ctx + \"/CaseManagementView.do?hc=996633&method=listNotes&providerNo=\" + providerNo + \"&demographicNo=\" + demographicNo + \"&issue_code=MedHistory&title=\" + medHistoryLabel + \"&cmd=div"+bean.getMedicalHxPosition()+"\"");
			flag=true;
			if(bean.getMedicalHxPosition().startsWith("R1")) {row1=true;}
			if(bean.getMedicalHxPosition().startsWith("R2")) {row2=true;}
		}
		if(!bean.getOngoingConcernsPosition().equals("")) {
			if(flag) {sb.append(",");}
			sb.append("div"+bean.getOngoingConcernsPosition()+":    ctx + \"/CaseManagementView.do?hc=996633&method=listNotes&providerNo=\" + providerNo + \"&demographicNo=\" + demographicNo + \"&issue_code=Concerns&title=\" + onGoingLabel + \"&cmd=div"+bean.getOngoingConcernsPosition()+"\"");
			flag=true;
			if(bean.getOngoingConcernsPosition().startsWith("R1")) {row1=true;}
			if(bean.getOngoingConcernsPosition().startsWith("R2")) {row2=true;}
		}
		if(!bean.getRemindersPosition().equals("")) {
			if(flag) {sb.append(",");}
			sb.append("div"+bean.getRemindersPosition()+":    ctx + \"/CaseManagementView.do?hc=996633&method=listNotes&providerNo=\" + providerNo + \"&demographicNo=\" + demographicNo + \"&issue_code=Reminders&title=\" + remindersLabel + \"&cmd=div"+bean.getRemindersPosition()+"\"");
			flag=true;
			if(bean.getRemindersPosition().startsWith("R1")) {row1=true;}
			if(bean.getRemindersPosition().startsWith("R2")) {row2=true;}
		}
		sb.append("};");
		
		//can we delete a row?
		if(!row1) {	sb.append("removeCppRow(1);");}
		if(!row2) {	sb.append("removeCppRow(2);");}
		
		sb.append("init();");
		sb.append("});");
		sb.append("function notifyIssueUpdate() {}");
		sb.append("function notifyDivLoaded(divId) {}");
		sb.append("</script>");
		return sb.toString();
	}
}
