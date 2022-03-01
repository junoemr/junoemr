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

<div class="fax-config">
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
				<div class="account-list"
				     ng-if="$ctrl.faxAccountList.length > 0"
				     ng-repeat="faxAccount in $ctrl.faxAccountList">
					<div class="account-item">
						<div>
							<span class="glyphicon enabled glyphicon-ok glyphicon-lrg" ng-show="faxAccount.enabled"
							      title="<bean:message bundle="ui" key="admin.fax.acct.acctEnabled"/>"></span>
							<span class="glyphicon disabled glyphicon-remove glyphicon-lrg" ng-hide="faxAccount.enabled"
							      title="<bean:message bundle="ui" key="admin.fax.acct.acctDisabled"/>"></span>
						</div>
						<div>
							<h5><bean:message bundle="ui" key="admin.fax.acct.inbound"/>:
								<span class="glyphicon enabled glyphicon-ok glyphicon-sml" ng-show="faxAccount.enableInbound"
								      title="<bean:message bundle="ui" key="admin.fax.acct.inboundEnabled"/>"></span>
								<span class="glyphicon disabled glyphicon-remove glyphicon-sml" ng-hide="faxAccount.enableInbound"
								      title="<bean:message bundle="ui" key="admin.fax.acct.inboundDisabled"/>"></span>
							</h5>
							<h5><bean:message bundle="ui" key="admin.fax.acct.outbound"/>:
								<span class="glyphicon enabled glyphicon-ok glyphicon-sml" ng-show="faxAccount.enableOutbound"
								      title="<bean:message bundle="ui" key="admin.fax.acct.outboundEnabled"/>"></span>
								<span class="glyphicon disabled glyphicon-remove glyphicon-sml" ng-hide="faxAccount.enableOutbound"
								      title="<bean:message bundle="ui" key="admin.fax.acct.outboundDisabled"/>"></span>
							</h5>
						</div>
						<div>
							<h4>{{faxAccount.displayName}}</h4>
						</div>
						<div>
							<h4>{{faxAccount.coverLetterOption}}</h4>
						</div>
						<button type="button" class="btn btn-default"
						        <%-- TODO disable button for non-admin users? --%>
						        ng-click="$ctrl.editFaxAccount(faxAccount)"
								ng-disabled="$ctrl.masterFaxDisabled">
							<bean:message bundle="ui" key="admin.fax.acct.btn-EditAccount"/>
						</button>
					</div>
				</div>
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
