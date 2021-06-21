<%--

    Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
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

    This software was written for
    CloudPractice Inc.
    Victoria, British Columbia
    Canada

--%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/WEB-INF/security.tld" prefix="security" %>
<%
	String roleName$ = (String) session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
	boolean authorized = true;
%>
<security:oscarSec roleName="<%=roleName$%>" objectName="_admin.fax" rights="r" reverse="<%=true%>">
	<%authorized = false; %>
	<%response.sendRedirect("../securityError.jsp?type=_admin");%>
</security:oscarSec>
<%
	if (!authorized) {
		return;
	}
%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html lang="en" ng-app="oscarProviderViewModule">
<head></head>
<body ng-controller="Admin.Section.Fax.FaxSendReceiveController as faxSendReceiveController">

<link href="<%=request.getContextPath() %>/library/bootstrap/3.0.0/css/bootstrap.css" rel="stylesheet">
<link rel="stylesheet" href="<%=request.getContextPath() %>/css/font-awesome.min.css">
<!-- Hide the main program nav as a fix for having angular in an iframe -->
<link rel="stylesheet" href="<%=request.getContextPath() %>/web/admin/section/know2act/Know2actHideNavBars.css">

<title><bean:message bundle="ui" key="admin.fax.sr.window-title"/></title>

<div class="fax-send_receive">
	<div class="flex-row search-filters">
		<label class="flex-row-label" for="input-fax-inbox-select-account">
			<bean:message bundle="ui" key="admin.fax.sr.choose-account"/>
		</label>
		<select class="flex-row-content form-control" id="input-fax-inbox-select-account"
		        ng-model="faxSendReceiveController.selectedFaxAccount"
		        ng-options="faxAccount.displayName for faxAccount in faxSendReceiveController.faxAccountList">
		</select>
	</div>
	<div class="tabs-heading">
		<ul class="nav nav-tabs">
			<li>
				<a data-toggle="tab" ng-click="faxSendReceiveController.changeTab(faxSendReceiveController.tabEnum.inbox);">
					<bean:message bundle="ui" key="admin.fax.sr.inbox"/></a>
			</li>
			<li class="active">
				<a data-toggle="tab" ng-click="faxSendReceiveController.changeTab(faxSendReceiveController.tabEnum.outbox);">
					<bean:message bundle="ui" key="admin.fax.sr.outbox"/>
				</a>
			</li>
		</ul>
	</div>
	<div class="tabs-body">
		<div id="fax_inbox" class="tab-pane"
		ng-show="faxSendReceiveController.activeTab == faxSendReceiveController.tabEnum.inbox">
			<div class="fax-inbox-header">
				<h1><bean:message bundle="ui" key="admin.fax.sr.inbox.header-title"/></h1>
			</div>
			<div class="fax-inbox-body">

				<div ng-show="faxSendReceiveController.selectedFaxAccount.enableInbound == true">
					<span><bean:message bundle="ui" key="admin.fax.sr.inbox.checkNewFaxesAt"/> {{faxSendReceiveController.nextPullTime}}</span>
					<br/>
					<span><bean:message bundle="ui" key="admin.fax.sr.inbox.unreadOnlyWarning"/></span>
				</div>
				<div ng-show="faxSendReceiveController.selectedFaxAccount.enableInbound == false">
					<span><bean:message bundle="ui" key="admin.fax.sr.inbox.disabledMessage"/></span>
				</div>

				<div class="flex-row search-frame">
					<div class="row search-filters">
						<div class="col-lg-3 col-xs-6">
							<label for="inbox-datepicker-startdate-id"><bean:message bundle="ui" key="admin.fax.sr.search.startDate"/></label>
							<juno-datepicker-popup id="inbox-datepicker-startdate-id"
							                       juno-model="faxSendReceiveController.inbox.startDate"
							                       show-icon="true"
							                       type="Input">
							</juno-datepicker-popup>
						</div>
						<div class="col-lg-3 col-xs-6">
							<label for="inbox-datepicker-enddate-id"><bean:message bundle="ui" key="admin.fax.sr.search.endDate"/></label>
							<juno-datepicker-popup id="inbox-datepicker-enddate-id"
							                       juno-model="faxSendReceiveController.inbox.endDate"
							                       show-icon="true"
							                       type="Input">
							</juno-datepicker-popup>
						</div>
					</div>
					<div class="row search-buttons">
						<div class="col-lg-3 col-xs-6">
							<button type="button" class="btn btn-primary"
							        ng-click="faxSendReceiveController.loadInboxItems();"><bean:message bundle="ui" key="global.search"/>
							</button>
						</div>
					</div>
				</div>

				<table ng-table="faxSendReceiveController.tableParamsInbox" show-filter="false" class="table table-striped table-bordered">
					<tbody>
					<tr ng-repeat="item in faxSendReceiveController.inboxItemList">

						<td data-title="'<bean:message bundle="ui" key="admin.fax.sr.inbox.tbl-hdr.systemDateReceived"/>'">
							{{item.systemDateReceived}}
						</td>
						<td data-title="'<bean:message bundle="ui" key="admin.fax.sr.inbox.tbl-hdr.sentFrom"/>'">
							{{item.sentFrom}}
						</td>
						<td>
							<button class="btn btn-primary btn-xs"
							ng-click="faxSendReceiveController.openDocument(item.documentId)">
								<bean:message bundle="ui" key="admin.fax.sr.inbox.tbl-btn.viewDocument"/>
							</button>
						</td>
					</tr>
					</tbody>
				</table>

			</div>
		</div>
		<div id="fax_outbox" class="tab-pane"
		     ng-show="faxSendReceiveController.activeTab == faxSendReceiveController.tabEnum.outbox">
			<div class="fax-outbox-header">
				<h1><bean:message bundle="ui" key="admin.fax.sr.outbox.header-title"/></h1>
			</div>
			<div class="fax-outbox-body">

				<div>
					<span><bean:message bundle="ui" key="admin.fax.sr.outbox.resendAtMessage"/> {{faxSendReceiveController.nextPushTime}}</span>
				</div>

				<div class="flex-row search-frame">
					<div class="row search-filters">
						<div class="col-lg-3 col-xs-6">
							<label for="outbox-datepicker-startdate-id"><bean:message bundle="ui" key="admin.fax.sr.search.startDate"/></label>
							<juno-datepicker-popup id="outbox-datepicker-startdate-id"
							                       juno-model="faxSendReceiveController.outbox.startDate"
							                       show-icon="true"
							                       type="Input">
							</juno-datepicker-popup>
						</div>
						<div class="col-lg-3 col-xs-6">
							<label for="outbox-datepicker-enddate-id"><bean:message bundle="ui" key="admin.fax.sr.search.endDate"/></label>
							<juno-datepicker-popup id="outbox-datepicker-enddate-id"
							                       juno-model="faxSendReceiveController.outbox.endDate"
							                       show-icon="true"
							                       type="Input">
							</juno-datepicker-popup>
						</div>
						<div class="col-lg-2 col-sm-3 col-xs-6">
							<label for="outbox-select-combinedStatus"><bean:message bundle="ui" key="admin.fax.sr.search.combinedStatus"/></label>
							<select id="outbox-select-combinedStatus" class="form-control"
							        ng-model="faxSendReceiveController.outbox.displayStatus"
							        ng-options="statusOption.label for statusOption in faxSendReceiveController.displayStatus">
							</select>
						</div>
					</div>
					<div class="row search-buttons">
						<div class="col-lg-3 col-xs-6">
							<button type="button" class="btn btn-primary"
							        ng-click="faxSendReceiveController.loadOutboxItems();"><bean:message bundle="ui" key="global.search"/>
							</button>
						</div>
					</div>
				</div>

				<table ng-table="faxSendReceiveController.tableParamsOutbox" show-filter="false" class="table table-striped table-bordered">
					<tbody>
					<tr ng-repeat="item in faxSendReceiveController.outboxItemList">
						<td data-title="'<bean:message bundle="ui" key="admin.fax.sr.outbox.tbl-hdr.systemDateSent"/>'">
							{{item.systemDateSent}}
						</td>
						<td data-title="'<bean:message bundle="ui" key="admin.fax.sr.outbox.tbl-hdr.providerName"/>'">
							{{item.providerName}} ({{item.providerNo}})
						</td>
						<td data-title="'<bean:message bundle="ui" key="admin.fax.sr.outbox.tbl-hdr.fileType"/>'">
							{{item.fileType}}
						</td>
						<td data-title="'<bean:message bundle="ui" key="admin.fax.sr.outbox.tbl-hdr.toFaxNumber"/>'">
							{{item.toFaxNumber}}
						</td>
						<td data-title="'<bean:message bundle="ui" key="admin.fax.sr.outbox.tbl-hdr.sentStatus"/>'">
							<span title="{{item.systemStatusMessage}}">
								{{faxSendReceiveController.getStatusDisplayLabel(item.combinedStatus)}}
							</span>
						</td>
						<td data-title="'<bean:message bundle="ui" key="admin.fax.sr.outbox.tbl-hdr.integrationDateSent"/>'">
							{{item.integrationDateSent}}
						</td>
						<td data-title="'<bean:message bundle="ui" key="admin.fax.sr.outbox.tbl-hdr.actionButtons"/>'">
							<div class="float-right">
								<button class="btn btn-xs btn-success"
								        title="<bean:message bundle="ui" key="admin.fax.sr.outbox.tbl-btn.resend-tooltip"/>"
								        ng-hide="item.archived
								            || (item.combinedStatus != faxSendReceiveController.displayStatus.queued.value
						                    && item.combinedStatus != faxSendReceiveController.displayStatus.error.value
						                    && item.combinedStatus != faxSendReceiveController.displayStatus.integrationFailed.value)"
								        ng-click="faxSendReceiveController.resendFax(item);">
									<bean:message bundle="ui" key="admin.fax.sr.outbox.tbl-btn.resend"/>
								</button>
								<button class="btn btn-primary btn-xs"
								        ng-click="faxSendReceiveController.viewDownloadFile(item.id);">
									<bean:message bundle="ui" key="admin.fax.sr.outbox.tbl-btn.download"/>
								</button>
							</div>
						</td>
						<td ng-if="faxSendReceiveController.displayNotificationColumn == true">
							<button class="btn btn-xs"
							        ng-show="item.notificationStatus == faxSendReceiveController.notificationStatusEnum.notify"
									ng-click="faxSendReceiveController.dismissNotification(item);">
								<bean:message bundle="ui" key="admin.fax.sr.outbox.tbl-btn.notification.dismiss"/>
							</button>
							<span ng-show="item.notificationStatus == faxSendReceiveController.notificationStatusEnum.dismissed">
								<bean:message bundle="ui" key="admin.fax.sr.outbox.notification.dismissed"/>
							</span>
						</td>
					</tr>
					</tbody>
				</table>
			</div>
			<div class="fax-outbox-footer">
			</div>
		</div>
	</div>
</div>

</body>
</html>
