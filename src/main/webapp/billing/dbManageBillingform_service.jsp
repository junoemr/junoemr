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

<%@ page
        import="java.math.*, java.util.*, java.io.*, java.sql.*, oscar.*, java.net.*,oscar.MyDateFormat"%>


<%@ page import="org.oscarehr.util.SpringUtils" %>
<%@ page import="org.oscarehr.common.model.CtlBillingService" %>
<%@ page import="org.oscarehr.common.dao.CtlBillingServiceDao" %>
<%
    CtlBillingServiceDao ctlBillingServiceDao = SpringUtils.getBean(CtlBillingServiceDao.class);
%>
<%


    String[] group = new String[4];
    String typeid = "", type="";

    typeid = request.getParameter("typeid");
    type = request.getParameter("type");

    for(CtlBillingService b:ctlBillingServiceDao.findByServiceType(typeid)) {
        ctlBillingServiceDao.remove(b.getId());
    }

%>

<%
    for(int j = 1; j < 4; j++)
    {
        group[j] = request.getParameter("group"+j);

        for (int i = 0; i < 40; i++)
        {

            if(request.getParameter("group"+j+"_service"+i).length() !=0){

                CtlBillingService cbs = new CtlBillingService();
                cbs.setServiceTypeName(type);
                cbs.setServiceType(typeid);
                cbs.setServiceCode(request.getParameter("group"+j+"_service"+i));
                cbs.setServiceGroupName(group[j]);
                cbs.setServiceGroup("Group"+j);
                cbs.setStatus("A");
                cbs.setServiceOrder(Integer.parseInt(request.getParameter("group"+j+"_service"+i+"_order")));
                ctlBillingServiceDao.persist(cbs);


            }
        }}
%>

<% response.sendRedirect("manageBillingform.jsp"); %>
