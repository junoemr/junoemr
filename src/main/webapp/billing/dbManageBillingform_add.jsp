<%--

    Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
    This software is published under the GPL GNU General Public License.
    This program is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public License
    as published by the Free Software Foundation; either version 2
    of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.

    This software was written for the
    Department of Family Medicine
    McMaster University
    Hamilton
    Ontario, Canada

--%>


<%@ page import="org.oscarehr.util.SpringUtils" %>
<%@ page import="org.oscarehr.common.model.CtlBillingService" %>
<%@ page import="org.oscarehr.common.dao.CtlBillingServiceDao" %>
<%@ page import="org.oscarehr.common.model.CtlDiagCode" %>
<%@ page import="org.oscarehr.common.dao.CtlDiagCodeDao" %>
<%@ page import="java.util.List" %>
<%
	CtlBillingServiceDao ctlBillingServiceDao = SpringUtils.getBean(CtlBillingServiceDao.class);
	CtlDiagCodeDao ctlDiagCodeDao = SpringUtils.getBean(CtlDiagCodeDao.class);
%>
<%

	String typeid = request.getParameter("typeid");
	String type = request.getParameter("type");
	String group1 = request.getParameter("group1");
	String group2 = request.getParameter("group2");
	String group3 = request.getParameter("group3");

	boolean noTypeId = false;
	boolean noType = false;
	boolean noGroup1 = false;
	boolean noGroup2 = false;
	boolean noGroup3 = false;

	boolean typeIdInUse = false;
	boolean typeInUse = false;

	List<CtlBillingService> existingIds = ctlBillingServiceDao.findByServiceTypeId(typeid);
	List<CtlBillingService> existingTypes = ctlBillingServiceDao.findByServiceTypeName(type);

	if (typeid.isEmpty())
	{
		noTypeId = true;
	} else if (!existingIds.isEmpty())
	{
		typeIdInUse = true;
	}

	if (type.isEmpty())
	{
		noType = true;
	} else if (!existingTypes.isEmpty())
	{
		typeInUse = true;
	}

	if (group1.isEmpty())
	{
		noGroup1 = true;
	}

	if (group2.isEmpty())
	{
		noGroup2 = true;
	}

	if (group3.isEmpty())
	{
		noGroup3 = true;
	}

	if (noTypeId || noType || noGroup1 || noGroup2 || noGroup3 || typeIdInUse || typeInUse)
	{
		response.sendRedirect("manageBillingform.jsp?billingform=000&noTypeId=" + noTypeId + "&noType=" + noType + "&noGroup1=" + noGroup1 + "&noGroup2=" + noGroup2
								+ "&noGroup3=" + noGroup3+ "&typeIdInUse=" + typeIdInUse + "&typeInUse=" + typeInUse);
	} else
	{
		CtlBillingService cbs = new CtlBillingService();
		cbs.setServiceTypeName(type);
		cbs.setServiceType(typeid);
		cbs.setServiceCode("A007A");
		cbs.setServiceGroupName(group1);
		cbs.setServiceGroup("Group1");
		cbs.setStatus("A");
		cbs.setServiceOrder(1);
		ctlBillingServiceDao.persist(cbs);


		cbs = new CtlBillingService();
		cbs.setServiceTypeName(type);
		cbs.setServiceType(typeid);
		cbs.setServiceCode("A007A");
		cbs.setServiceGroupName(group2);
		cbs.setServiceGroup("Group2");
		cbs.setStatus("A");
		cbs.setServiceOrder(1);
		ctlBillingServiceDao.persist(cbs);

		cbs = new CtlBillingService();
		cbs.setServiceTypeName(type);
		cbs.setServiceType(typeid);
		cbs.setServiceCode("A007A");
		cbs.setServiceGroupName(group3);
		cbs.setServiceGroup("Group3");
		cbs.setStatus("A");
		cbs.setServiceOrder(1);
		ctlBillingServiceDao.persist(cbs);

		String[] param3 = new String[3];


		CtlDiagCode cdc = new CtlDiagCode();
		cdc.setServiceType(typeid);
		cdc.setDiagnosticCode("000");
		cdc.setStatus("A");
		ctlDiagCodeDao.persist(cdc);


		response.sendRedirect("manageBillingform.jsp");

	}
%>
