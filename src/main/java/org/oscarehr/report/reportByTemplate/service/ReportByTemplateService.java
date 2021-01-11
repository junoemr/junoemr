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

package org.oscarehr.report.reportByTemplate.service;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.oscarehr.provider.dao.ProviderDataDao;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.report.reportByTemplate.dao.ReportTemplatesDao;
import org.oscarehr.report.reportByTemplate.exception.ReportByTemplateException;
import org.oscarehr.report.reportByTemplate.model.ReportTemplates;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import oscar.oscarReport.reportByTemplate.Choice;
import oscar.oscarReport.reportByTemplate.Parameter;
import oscar.oscarReport.reportByTemplate.ReportFactory;
import oscar.oscarReport.reportByTemplate.ReportObject;
import oscar.oscarReport.reportByTemplate.ReportObjectGeneric;
import oscar.util.UtilXML;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ReportByTemplateService
{
	private static final Logger logger = MiscUtils.getLogger();

	@Autowired
	private ReportTemplatesDao reportByTemplateDao;

	@Autowired
	private ProviderDataDao providerDataDao;

	public ReportTemplates addTemplate(String xmlTemplate, String providerNoStr, boolean setVerified) throws ReportByTemplateException
	{
		try
		{
			// determine if the provider can set the verification status
			ProviderData provider = providerDataDao.find(providerNoStr);
			boolean setTemplateVerified = provider.isSuperAdmin() && setVerified;

			// create the template entry
			ReportTemplates template = new ReportTemplates();
			template.setAdminVerified(setTemplateVerified);

			template = setTemplateQueryInfo(template, readXml(xmlTemplate));

			reportByTemplateDao.persist(template);
			return template;
		}
		catch(IOException | JDOMException e)
		{
			logger.debug("Template Report Error", e);
			throw new ReportByTemplateException(e.getMessage(), "XML Parse Error");
		}
	}

	public ReportTemplates updateTemplate(Integer templateId, String xmlTemplate, String providerNoStr, boolean setVerified) throws ReportByTemplateException
	{
		try
		{
			// load the existing template
			ReportTemplates template = reportByTemplateDao.find(templateId);

			// determine if the provider can set the verification status
			// if the provider is not a super user, retain the existing status.
			ProviderData provider = providerDataDao.find(providerNoStr);
			if(provider.isSuperAdmin())
			{
				template.setAdminVerified(setVerified);
			}
			//update the template entry
			template = setTemplateQueryInfo(template, readXml(xmlTemplate));
			reportByTemplateDao.merge(template);
			return template;
		}
		catch(IOException | JDOMException e)
		{
			logger.debug("Template Report Error", e);
			throw new ReportByTemplateException(e.getMessage(), "XML Parse Error");
		}
	}

	public void deleteTemplate(Integer templateId)
	{
		reportByTemplateDao.remove(templateId);
	}

	public List<ReportObject> getReportObjectList(boolean includeParams) throws ReportByTemplateException
	{
		//TODO-legacy implement paging?
		List<ReportTemplates> templateList = reportByTemplateDao.getTemplateList(null,null);
		List<ReportObject> reportList = new ArrayList<>();

		for(ReportTemplates template : templateList)
		{
			reportList.add(getAsTemplateReport(template, includeParams));
		}
		return reportList;
	}
	public List<ReportObject> getLegacyReportObjectList(boolean includeParams)
	{
		//TODO-legacy implement paging?
		List<ReportTemplates> templateList = reportByTemplateDao.getTemplateList(null,null);
		List<ReportObject> reportList = new ArrayList<>();

		for(ReportTemplates template : templateList)
		{
			try
			{
				reportList.add(getAsTemplateReport(template, includeParams));
			}
			catch(ReportByTemplateException e)
			{
				reportList.add(new ReportObjectGeneric(String.valueOf(template.getId()), e.getPublicMessage()));
			}
		}
		return reportList;
	}

	public ReportObject getAsTemplateReport(ReportTemplates template, boolean includeParams) throws ReportByTemplateException
	{
		ReportObjectGeneric report = new ReportObjectGeneric();
		report.setTemplateId(String.valueOf(template.getId()));
		report.setTitle(template.getTemplateTitle());
		report.setDescription(template.getTemplateDescription());
		report.setType(template.getType());
		report.setSuperAdminVerified(template.isAdminVerified());

		if(includeParams)
		{
			try
			{
				report.setParameters(getParameterList(template.getTemplateXml()));
			}
			catch(IOException | JDOMException e)
			{
				logger.debug("Template Report Error", e);
				throw new ReportByTemplateException(e.getMessage(), "XML Parse Error");
			}
		}
		return report;
	}

	public ReportObject getAsLegacyReport(Integer templateId, boolean includeParams)
	{
		ReportObject report;
		try
		{
			ReportTemplates template = reportByTemplateDao.find(templateId);
			report = getAsTemplateReport(template, includeParams);
		}
		catch(ReportByTemplateException e)
		{
			report = new ReportObjectGeneric(String.valueOf(templateId), e.getPublicMessage());
		}
		catch(Exception e)
		{
			logger.error("Unknown Template Error", e);
			report = new ReportObjectGeneric(String.valueOf(templateId), "Invalid template xml");
		}
		return report;
	}

	public String getTemplateSQL(Integer templateId, Map<String,String[]> parameters) throws ReportByTemplateException
	{
		ReportTemplates template = reportByTemplateDao.find(templateId);
		String templateSql = template.getTemplateSql();

		// match {parameter}
		Pattern pattern = Pattern.compile("\\{(.*?)\\}");
		Matcher matcher = pattern.matcher(templateSql);
		String nativeSql;

		StringBuffer sb = new StringBuffer();

		while(matcher.find())
		{
			String paramName = matcher.group(1);
			String paramValue;
			logger.info("Pattern Match: " + paramName);

			// get the value array. it can be in several forms
			if(parameters.containsKey(paramName))
			{
				paramValue = parameters.get(paramName)[0];
			}
			else if(parameters.containsKey(paramName+":list"))
			{
				String[] paramValues = parameters.get(paramName+":list");
				paramValue = String.join(",", paramValues);
			}
			else if(parameters.containsKey(paramName+":check"))
			{
				paramValue = "";
			}
			else if(parameters.containsKey(paramName+":limit"))
			{
				String limitParam = parameters.get(paramName+":limit")[0];
				if(!StringUtils.isNumeric(limitParam))
				{
					throw new ReportByTemplateException("Non numeric limit value: " + limitParam);
				}
				paramValue = limitParam;
			}
			else if(parameters.containsKey(paramName+":offset"))
			{
				String offsetParam = parameters.get(paramName+":offset")[0];
				if(!StringUtils.isNumeric(offsetParam))
				{
					throw new ReportByTemplateException("Non numeric offset value: " + offsetParam);
				}
				paramValue = offsetParam;
			}
			else
			{
				throw new ReportByTemplateException("Missing Parameter definition: " + paramName);
			}

			matcher.appendReplacement(sb, paramValue);
		}
		matcher.appendTail(sb);

		nativeSql = sb.toString();
		return nativeSql;
	}

	// ---- logic taken from old ReportManager
	private Document readXml(String xml) throws JDOMException, IOException
	{
		SAXBuilder parser = new SAXBuilder();
		xml = UtilXML.escapeXML(xml);  //escapes anomalies such as "date >= {mydate}" the '>' character
		//xml  UtilXML.escapeAllXML(xml, "<param-list>");  //escapes all markup in <report> tag, otherwise can't retrieve element.getText()
		Document doc = parser.build(new java.io.ByteArrayInputStream(xml.getBytes()));
		if(doc.getRootElement().getName().equals("report"))
		{
			Element newRoot = new Element("report-list");
			Element oldRoot = doc.detachRootElement();
			newRoot.setContent(oldRoot);
			doc.removeContent();
			doc.setRootElement(newRoot);
		}
		return doc;
	}

	private ReportTemplates setTemplateQueryInfo(ReportTemplates template, Document templateXML) throws ReportByTemplateException
	{
		Element rootElement = templateXML.getRootElement();
		List<Element> reports = rootElement.getChildren();
		for(int i = 0; i < reports.size(); i++)
		{
			// verify the xml.
			Element report = reports.get(i);
			String templateTitle = report.getAttributeValue("title");
			if(templateTitle == null)
			{
				throw new ReportByTemplateException("Error: Attribute 'title' missing in <report> tag");
			}
			String templateDescription = report.getAttributeValue("description");
			if(templateDescription == null)
			{
				throw new ReportByTemplateException("Error: Attribute 'description' missing in <report> tag");
			}
			String type = report.getChildTextTrim("type");
			if(type == null)
			{
				type = "";
			}
			String querySql = report.getChildText("query");
			if(type.equalsIgnoreCase(ReportFactory.SQL_TYPE) && (querySql == null || querySql.trim().isEmpty()))
			{
				throw new ReportByTemplateException("Error: The sql query is missing in <report> tag");
			}
			String active = report.getAttributeValue("active");
			int activeInt;
			try
			{
				activeInt = Integer.parseInt(active);
			}
			catch(NumberFormatException e)
			{
				activeInt = 1;
			}

			XMLOutputter templateout = new XMLOutputter();
			String templateXMLstr = templateout.outputString(report).trim();
			templateXMLstr = UtilXML.unescapeXML(templateXMLstr);

			// set the template values
			template.setTemplateTitle(templateTitle);
			template.setTemplateDescription(templateDescription);
			template.setTemplateSql(querySql);
			template.setTemplateXml(templateXMLstr);
			template.setActive(activeInt);
			template.setType(type);
		}
		return template;
	}

	/**
	 * parse the xml template for the parameters
	 * @return array list of parameters
	 * @throws JDOMException
	 * @throws IOException
	 * @throws ReportByTemplateException
	 */
	private ArrayList<Parameter> getParameterList(String paramXML) throws JDOMException, IOException, ReportByTemplateException
	{
		ArrayList<Parameter> paramList = new ArrayList<>();

//		String paramXML = getTemplateXml();

		paramXML = UtilXML.escapeXML(paramXML);  //escapes anomalies such as "date >= {mydate}" the '>' character
		SAXBuilder parser = new SAXBuilder();
		Document doc = parser.build(new java.io.ByteArrayInputStream(paramXML.getBytes()));
		Element root = doc.getRootElement();
		List<Element> paramsXml = root.getChildren("param");

		for(Element param : paramsXml)
		{
			String paramid = param.getAttributeValue("id");
			if (paramid == null)
			{
				throw new ReportByTemplateException("Error: Param id not found");
			}
			String paramtype = param.getAttributeValue("type");
			if (paramtype == null)
			{
				throw new ReportByTemplateException("Error: Param type not found on param '" + paramid + "'");
			}
			String paramdescription = param.getAttributeValue("description");
			if (paramdescription == null)
			{
				throw new ReportByTemplateException("Error: Param description not found on param '" + paramid + "'");
			}

			List<Element> choicesXml = param.getChildren("choice");
			ArrayList<Choice> choices = new ArrayList<>();
			String paramquery = param.getChildText("param-query"); //if retrieving choices from the DB
			if (paramquery != null)
			{
				List<Object[]> rsChoices = reportByTemplateDao.runNativeQuery(paramquery);
				for(Object[] choice : rsChoices)
				{
					String choiceid = String.valueOf(choice[0]);
					String choicetext = String.valueOf(choice[1]);
					if (choicetext.equalsIgnoreCase("null"))
					{
						choicetext = choiceid;
					}
					Choice curchoice = new Choice(choiceid, choicetext);
					choices.add(curchoice);
				}
			}
			for(Element choice: choicesXml)
			{
				String choiceid = choice.getAttributeValue("id");
				String choicetext = choice.getTextTrim();
				if(choiceid == null)
				{
					choiceid = choicetext;
				}
				Choice curchoice = new Choice(choiceid, choicetext);
				choices.add(curchoice);
			}
			Parameter curparam = new Parameter(paramid, paramtype, paramdescription, choices);

			// only calendar use for now
			// add the default parameter
			//TODO-legacy it would be nice to iterate all parameters
			String paramDefault = param.getAttributeValue("default");
			if(paramDefault != null) {
				// cheap and dirty way to always use the current date
				if(paramDefault.equalsIgnoreCase("CURDATE") || paramDefault.equalsIgnoreCase("TODAY")) {
					paramDefault = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
				}
				curparam.addAttribute("default", paramDefault);
			}
			paramList.add(curparam);
		}
		return paramList;
	}
}
