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
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="fax-config" ng-if="$ctrl.initialized">
	<div class="fax-config-header">
		<h1><bean:message bundle="ui" key="admin.fax.acct.header"/></h1>

		<span ng-show="!$ctrl.loggedInProvider.superAdmin && $ctrl.masterFaxDisabled">
			<bean:message bundle="ui" key="admin.fax.acct.accessDisabledMessage"/>
		</span>
		<!-- let super admin enable/disable faxing -->
		<panel ng-if="$ctrl.loggedInProvider.superAdmin">
			<panel-header>
				<h6><bean:message bundle="ui" key="admin.fax.acct.superAdminHeader"/></h6>
			</panel-header>
			<panel-body>
				<juno-toggle
						label="<bean:message bundle="ui" key="admin.fax.acct.masterFaxEnabledInbound"/>"
						label-position="$ctrl.LABEL_POSITION.RIGHT"
						ng-model="$ctrl.masterFaxEnabledInbound"
						change="$ctrl.saveMasterFaxEnabledStateInbound(value)"
						round="true"
				>
				</juno-toggle>
				<juno-toggle
						label="<bean:message bundle="ui" key="admin.fax.acct.masterFaxEnabledOutbound"/>"
						label-position="$ctrl.LABEL_POSITION.RIGHT"
						ng-model="$ctrl.masterFaxEnabledOutbound"
						change="$ctrl.saveMasterFaxEnabledStateOutbound(value)"
						round="true"
				>
				</juno-toggle>
			</panel-body>
		</panel>
	</div>
	<div class="fax-config-body">
		<panel no-header="true">
			<panel-body>
				<div ng-if="$ctrl.faxAccountList.length <= 0">
					<span><bean:message bundle="ui" key="admin.fax.acct.noAccountsMessage"/></span>
				</div>

				<div ng-if="$ctrl.faxAccountList.length > 0">
					<table ng-table="$ctrl.tableParamsOutbox" show-filter="false" class="table table-striped table-bordered">
						<tbody>
						<tr ng-repeat="faxAccount in $ctrl.faxAccountList">
							<td class="w-32">
								<juno-check-box ng-model="$ctrl.faxAccountSelectStates[faxAccount.id]"
								                readonly="faxAccount.equals($ctrl.activeAccount)"
								                change="$ctrl.setActiveAccount(value, faxAccount)">
								</juno-check-box>
							</td>
							<td data-title="'Account Name'">
								<div class="flex-column">
									<span class="table-text-primary m-b-4">{{faxAccount.displayName}}</span>
									<span class="table-text-secondary">{{faxAccount.faxNumber}}</span>
								</div>
							</td>
							<td data-title="'Fax Account'">
								<div class="flex-column">
									<span class="table-text-primary m-b-4">{{faxAccount.accountType}}</span>
									<span class="table-text-secondary">{{faxAccount.accountLogin}}</span>
								</div>
							</td>

							<td data-title="'<bean:message bundle="ui" key="admin.fax.acct.inbound"/>'">
								<div ng-if="faxAccount.enableInbound" class="state-enabled">
									<icon-badge icon="icon-check"></icon-badge>
									<span class="table-text-primary"><bean:message bundle="ui" key="admin.fax.acct.inboundEnabled"/></span>
								</div>
								<div ng-if="!faxAccount.enableInbound" class="state-disabled">
									<icon-badge icon="icon-private"></icon-badge>
									<span class="table-text-primary"><bean:message bundle="ui" key="admin.fax.acct.inboundDisabled"/></span>
								</div>
							</td>

							<td data-title="'<bean:message bundle="ui" key="admin.fax.acct.outbound"/>'">
								<div ng-if="faxAccount.enableOutbound" class="state-enabled">
									<icon-badge icon="icon-check"></icon-badge>
									<span class="table-text-primary"><bean:message bundle="ui" key="admin.fax.acct.outboundEnabled"/></span>
								</div>
								<div ng-if="!faxAccount.enableOutbound" class="state-disabled">
									<icon-badge icon="icon-private"></icon-badge>
									<span class="table-text-primary"><bean:message bundle="ui" key="admin.fax.acct.outboundDisabled"/></span>
								</div>
							</td>
							<td data-title="'Action'" class="w-128">
								<div>
									<juno-button
											disabled="$ctrl.masterFaxDisabled || !$ctrl.userCanEdit()"
											click="$ctrl.editFaxAccount(faxAccount)"
											button-color="$ctrl.JUNO_BUTTON_COLOR.DEFAULT"
											button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.COLORED">
										<i class="icon icon-write"></i>
										<bean:message bundle="ui" key="admin.fax.acct.btn-EditAccount"/>
									</juno-button>
								</div>
							</td>
						</tr>
						</tbody>
					</table>
				</div>
				<span ng-if="$ctrl.faxAccountList.length > 0">
					<bean:message bundle="ui" key="admin.fax.acct.accountSelectionMessage"/>
				</span>
			</panel-body>
		</panel>
	</div>
	<div class="fax-config-footer">
		<panel no-header="true" ng-if="!$ctrl.masterFaxDisabled">
			<panel-body>
				<button type="button" class="btn btn-primary"
				        ng-click="$ctrl.connectNewSRFaxAccount()">
					<bean:message bundle="ui" key="admin.fax.acct.btn-connectSRFax"/>
				</button>
			</panel-body>
		</panel>
	</div>
</div>
