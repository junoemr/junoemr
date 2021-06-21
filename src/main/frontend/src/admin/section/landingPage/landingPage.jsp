<%@ page import="oscar.OscarProperties" %><%--
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
--%>

<%-- Hack to get oscar context path. This is for legacy support only, and should not be duplicated --%>
<%
	String oscarContextPath = request.getContextPath();
%>
<script type="text/javascript">
	adminLandingPage = {};
	adminLandingPage.oscarContextPath = "<%=oscarContextPath%>"
</script>

<div id="admin-landing-page">
	<icon-card card-size="'card-md-wide'" text="'Unlock Account'" icon="'icon-user'" on-click="$ctrl.goToFrame('%2Fadmin%2FunLock.jsp', 'User Management')"></icon-card>
	<icon-card card-size="'card-md-wide'" text="'Manage Users'" icon="'icon-user'" on-click="$ctrl.goTo('admin/manageUsers', 'User Management')"></icon-card>
	<icon-card card-size="'card-md-wide'" text="'Manage eForms'" icon="'icon-file'" on-click="$ctrl.goToFrame('%2Feform%2Fefmformmanager.jsp&useCompat=true', 'Forms/eForms')"></icon-card>
	<icon-card card-size="'card-md-wide'" text="'Schedule Setting'" icon="'icon-calendar'" on-click="$ctrl.goToFrame('%2Fschedule%2Fscheduletemplatesetting.jsp', 'Schedule Management')"></icon-card>
	<icon-card card-size="'card-md-wide'" text="'Manage Groups'" icon="'icon-user-group'" on-click="$ctrl.goToFrame('%2Fadmin%2Fadmindisplaymygroup.jsp', 'Schedule Management')"></icon-card>
	<icon-card card-size="'card-md-wide'" text="'Insert a Template'" icon="'icon-briefcase'" on-click="$ctrl.goToFrame('%2Fadmin%2Fprovidertemplate.jsp', 'eChart')"></icon-card>
</div>
