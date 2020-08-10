<%--

    Copyright (c) 2006-. OSCARservice, OpenSoft System. All Rights Reserved.
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

--%>
<%@page import="java.util.List"%>
<%@page import="org.oscarehr.common.dao.Icd9Dao"%>
<%@page import="org.oscarehr.common.model.Icd9"%>
<%@page import="org.oscarehr.util.SpringUtils"%>
<%@ page import="org.json.simple.JSONArray" %>
<%@ page import="java.util.ArrayList" %>
<%

    Icd9Dao icd9dao = (Icd9Dao) SpringUtils.getBean("Icd9DAO");

    String searchParams = request.getParameter("term");

    List<Icd9> Icd9List = icd9dao.getIcd9(searchParams);

    List<String> responseList = new ArrayList<String>();

    if (Icd9List != null)
    {
    	for (Icd9 entry : Icd9List)
        {
        	responseList.add(entry.getCode() + " --> " + entry.getDescription());
        }
    }

    response.getWriter().write(JSONArray.toJSONString(responseList));
%>
