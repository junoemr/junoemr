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
<%
    CtlBillingServiceDao ctlBillingServiceDao = SpringUtils.getBean(CtlBillingServiceDao.class);
%>
<%

    int NUMBER_OF_GROUPS = 3;
    int GROUP_START_INDEX = 1;
    int MAX_CODES_PER_GROUP = 40;

    // The array is zero indexed.  Unfortunately the service group column of the data structure is 1-indexed.
    // There are three groups, in positions [null, Group1, Group2, Group3]
    String[] groupNames = new String[GROUP_START_INDEX + NUMBER_OF_GROUPS];
    String typeid = request.getParameter("typeid");
    String type = request.getParameter("type");

    for (CtlBillingService billingService : ctlBillingServiceDao.findByServiceType(typeid))
    {
        ctlBillingServiceDao.remove(billingService.getId());
    }

%>

<%
    for(int i = 1; i < GROUP_START_INDEX + NUMBER_OF_GROUPS; i++)
    {
        groupNames[i] = request.getParameter("group"+i);

        for (int j = 0; j < MAX_CODES_PER_GROUP; j++)
        {
            String entry = request.getParameter("group" + i + "_service" + j);

            if(entry != null && !entry.isEmpty())
            {
                CtlBillingService cbs = new CtlBillingService();
                cbs.setServiceTypeName(type);
                cbs.setServiceType(typeid);
                cbs.setServiceCode(request.getParameter("group" + i + "_service" + j));
                cbs.setServiceGroupName(groupNames[i]);
                cbs.setServiceGroup("Group" + i);
                cbs.setStatus("A");
                cbs.setServiceOrder(Integer.parseInt(request.getParameter("group" + i + "_service" + j + "_order")));
                ctlBillingServiceDao.persist(cbs);
            }
        }}
%>

<% response.sendRedirect("manageBillingform.jsp"); %>
