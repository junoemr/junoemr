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
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<%-- This JSP is the multi-site admin site detail page --%>
<%@ include file="/taglibs.jsp" %>
<%@ taglib uri="/WEB-INF/security.tld" prefix="security" %>
<%@ taglib uri="/oscarPropertiestag" prefix="oscarProps" %>
<%
    String roleName$ = (String) session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
    boolean authed = true;
%>
<security:oscarSec roleName="<%=roleName$%>"
                   objectName="_admin,_admin.misc" rights="r" reverse="<%=true%>">
    <%authed = false; %>
    <%response.sendRedirect("../securityError.jsp?type=_admin&type=_admin.misc");%>
</security:oscarSec>
<%
    if (!authed)
    {
        return;
    }
%>

<%@page import="org.oscarehr.common.model.Site" %>
<html:html locale="true">
    <head>
            <%--<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>--%>
        <title>Add New Satellite Clinic</title>

            <%--

                    <script type="text/javascript" language="JavaScript"
                            src="../../share/javascript/prototype.js"></script>
                            --%>
        <script type="text/javascript" language="JavaScript"
                src="../../share/javascript/Oscar.js"></script>
        <script type="text/javascript" src="<%=request.getContextPath()%>/share/javascript/jquery/jquery-2.2.4.min.js"></script>
        <script type="text/javascript" src="./sites/sitesAdminDetail.js"></script>
        <script type="text/javascript" src="../share/javascript/picker.js"></script>

        <link rel="stylesheet" type="text/css" href="../../share/css/OscarStandardLayout.css">
        <link href="<html:rewrite page='/css/displaytag.css'/>" rel="stylesheet">

        <style>
            .button {
                border: 1px solid #666666;
            }

            .hidden {
                display: none;
            }

            .show {
                display: unset;
            }

        </style>

    </head>

    <body vlink="#0000FF" class="BodyStyle" <%--onload="$('colorField').style.backgroundColor=$('colorField').value;--%>">
    <nested:form action="/admin/ManageSites">
        <table class="MainTable">
            <tr class="MainTableTopRow">
                    <%--<td class="MainTableTopRowLeftColumn">admin</td>--%>
                <td class="MainTableTopRowRightColumn">
                    <table class="TopStatusBar" style="width: 100%;">
                        <tr>
                            <td>Add New Satellite Site</td>
                        </tr>
                    </table>
                </td>
            </tr>
            <tr>
                <td class="MainTableLeftColumn" valign="top" width="160px">
                </td>
                <td class="MainTableRightColumn" valign="top">
                    <html:messages id="errors" header="errors.header" footer="errors.footer">
                        <li><bean:write name="errors"/></li>
                    </html:messages>

                    <table>
                        <tr>
                            <td width="256px"></td>
                            <td></td>
                        </tr>
                        <tr>
                            <td>Site Name:<sup style="color:red">*</sup></td>
                            <td><nested:text property="site.name" maxlength="30"></nested:text></td>
                        </tr>
                        <tr>
                            <td>Short Name:<sup style="color:red">*</sup></td>
                            <td><nested:text property="site.shortName" maxlength="10"></nested:text></td>
                        </tr>
                        <tr>
                            <td>Theme Color:<sup style="color:red">*</sup></td>
                            <td>
                                <nested:text property="site.bgColor"></nested:text>
                                <a href="javascript:TCP.popup(document.forms['siteForm'].elements['site.bgColor']);">
                                    <img width="15" height="13" border="0" src="../images/sel.gif">
                                </a>
                                <bean:message key="schedule.scheduletemplatecodesetting.msgColorExample"/>
                            </td>
                        </tr>
                        <tr>
                            <td>Active:</td>
                            <td><nested:checkbox property="site.status" value="1"/></td>
                        </tr>
                        <tr>
                            <td>Telephone:</td>
                            <td><nested:text property="site.phone"></nested:text></td>
                        </tr>
                        <tr>
                            <td>Fax:</td>
                            <td><nested:text property="site.fax"></nested:text></td>
                        </tr>
                        <tr>
                            <td>Address:</td>
                            <td><nested:text property="site.address"></nested:text></td>
                        </tr>
                        <tr>
                            <td>City:</td>
                            <td><nested:text property="site.city"></nested:text></td>
                        </tr>
                        <tr>
                            <td>Province:</td>
                            <td>
                                <nested:select property="site.province" styleId="province-select">
                                    <html:option value="AB">Alberta</html:option>
                                    <html:option value="BC">British Columbia</html:option>
                                    <html:option value="MB">Manitoba</html:option>
                                    <html:option value="NB">New Brunswick</html:option>
                                    <html:option value="NL">Newfoundland & Labrador</html:option>
                                    <html:option value="NT">Northwest Territories</html:option>
                                    <html:option value="NS">Nova Scotia</html:option>
                                    <html:option value="NU">Nunavut</html:option>
                                    <html:option value="ON">Ontario</html:option>
                                    <html:option value="PE">Prince Edward Island</html:option>
                                    <html:option value="QC">Quebec</html:option>
                                    <html:option value="SK">Saskatchewan</html:option>
                                    <html:option value="YT">Yukon Territory</html:option>
                                </nested:select>
                            </td>
                        </tr>
                        <tr>
                            <td>Postal Code:</td>
                            <td><nested:text property="site.postal"></nested:text></td>
                        </tr>
                        <tr class="province-specific ab" style="display: none">
                            <td>Connect Care Lab Id:</td>
                            <td><nested:text property="site.albertaConnectCareLabId"></nested:text></td>
                        </tr>
                        <tr class="province-specific ab" style="display: none">
                            <td>Connect Care Department Id:</td>
                            <td><nested:text property="site.albertaConnectCareDepartmentId"></nested:text></td>
                        </tr>
                        <tr class="province-specific bc" style="display: none">
                            <td>Service Location Code:</td>
                            <td>
                                <nested:select property="site.bcServiceLocationCode">
                                    <html:option value="">None</html:option>
                                    <c:forEach items="${serviceLocationCodes}" var="code">
                                        <html:option value="${code.visitType}">(${code.visitType}) ${code.visitDescription}</html:option>
                                    </c:forEach>
                                </nested:select>
                            </td>
                        </tr>
                        <tr class="province-specific bc" style="display: none">
                            <td>BCP Facility Number:</td>
                            <td><nested:text property="site.bcFacilityNumber"></nested:text></td>
                        </tr>
                        <% if (org.oscarehr.common.IsPropertiesOn.isProviderFormalizeEnable())
                        { %>
                        <tr>
                            <td>ProviderID From:</td>
                            <td><nested:text property="site.providerIdFrom"></nested:text></td>
                        </tr>
                        <tr>
                            <td>ProviderID To:</td>
                            <td><nested:text property="site.providerIdTo"></nested:text></td>
                        </tr>
                        <% } %>
                    </table>

                    <nested:hidden property="site.siteId"/>
                    <input name="method" type="hidden" value="save"/>

                    <nested:submit styleClass="button" styleId="save-button">Save</nested:submit>
                    <nested:submit styleClass="button" onclick="this.form.method.value='view'">Cancel</nested:submit>
                </td>
            </tr>
            <tr>
                <td class="MainTableBottomRowLeftColumn">&nbsp;</td>

                <td class="MainTableBottomRowRightColumn">&nbsp;</td>
            </tr>
        </table>
    </nested:form>


</html:html>
