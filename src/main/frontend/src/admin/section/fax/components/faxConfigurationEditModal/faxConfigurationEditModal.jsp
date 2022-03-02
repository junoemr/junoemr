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
<div class="modal-content">
	<form name="form">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-label="<bean:message key="global.close"/>"
			        ng-click="$ctrl.cancel()">
				<span aria-hidden="true" >&times;</span>
			</button>
			<h3 class="modal-title"><bean:message bundle="ui" key="admin.fax.acct.edit.header"/></h3>
		</div>
		<div class="modal-body">
			<div class="flex-row">
				<label class="flex-row-label" for="input-fax-enabled"><bean:message bundle="ui" key="admin.fax.acct.edit.acctEnabled"/></label>
				<label class="flex-row-content switch">
					<input id="input-fax-enabled" type="checkbox"
					       ng-model="$ctrl.faxAccount.enabled"/>
					<span class="slider"></span>
				</label>
			</div>
			<div class="flex-row">
				<label class="flex-row-label" for="input-fax-account-id"><bean:message bundle="ui" key="admin.fax.acct.edit.accountLogin"/></label>
				<input class="flex-row-content" id="input-fax-account-id" type="text"
				       ng-change="$ctrl.setDefaultConnectionStatus()"
				       ng-model="$ctrl.faxAccount.accountLogin">
			</div>
			<div class="flex-row">
				<label class="flex-row-label" for="input-fax-account-pw"><bean:message bundle="ui" key="admin.fax.acct.edit.password"/></label>
				<input class="flex-row-content" id="input-fax-account-pw" type="password"
				       ng-change="$ctrl.setDefaultConnectionStatus()"
				       ng-model="$ctrl.faxAccount.password">
			</div>
			<div class="flex-row">
				<label class="flex-row-label" for="input-fax-account-name"
				       title="<bean:message bundle="ui" key="admin.fax.acct.edit.displayName-tooltip"/>">
					<bean:message bundle="ui" key="admin.fax.acct.edit.displayName"/>
				</label>
				<input class="flex-row-content" id="input-fax-account-name" type="text"
				       ng-model="$ctrl.faxAccount.displayName">
			</div>
			<hr>
			<fieldset ng-disabled="!$ctrl.masterFaxEnabledOutbound">
				<span ng-show="!$ctrl.masterFaxEnabledOutbound"><bean:message bundle="ui" key="admin.fax.acct.edit.outboundDisabledMessage"/></span>
				<div class="flex-row content-end info-section">
					<span class="glyphicon info-icon glyphicon-info-sign"
					      title="<bean:message bundle="ui" key="admin.fax.acct.edit.outboundInfoMessage"/>">
					</span>
				</div>
				<div class="flex-row">
					<label class="flex-row-label" for="input-fax-enabled-outbound"><bean:message bundle="ui" key="admin.fax.acct.edit.enableOutbound"/></label>
					<label class="flex-row-content switch">
						<input id="input-fax-enabled-outbound" type="checkbox"
						       ng-model="$ctrl.faxAccount.enableOutbound"/>
						<span class="slider"></span>
					</label>
				</div>
				<div>
					<div class="flex-row">
						<label class="flex-row-label" for="input-fax-account-email"
						       title="<bean:message bundle="ui" key="admin.fax.acct.edit.accountEmail-tooltip"/>">
							<bean:message bundle="ui" key="admin.fax.acct.edit.accountEmail"/>
						</label>
						<input class="flex-row-content" id="input-fax-account-email" name="input-fax-account-email" type="email"
						       ng-model="$ctrl.faxAccount.accountEmail"
						       ng-required="$ctrl.faxAccount.enableOutbound">
					</div>
					<div class="flex-row input-validation" ng-show="!form['input-fax-account-email'].$valid">
						<label class="flex-row-label">*</label>
						<span><bean:message bundle="ui" key="admin.fax.acct.edit.accountEmail-invalid"/></span>
					</div>
				</div>
				<div>
					<div class="flex-row">
						<label class="flex-row-label" for="input-fax-account-fax-no"
						       title="<bean:message bundle="ui" key="admin.fax.acct.edit.faxNumber-tooltip"/>">
							<bean:message bundle="ui" key="admin.fax.acct.edit.faxNumber"/>
						</label>
						<input class="flex-row-content" id="input-fax-account-fax-no" name="input-fax-account-fax-no" type="text"
						       ng-model="$ctrl.faxAccount.faxNumber"
						       ng-minlength="10" ng-maxlength="10" ng-pattern="/\d{10}/" ng-required="$ctrl.faxAccount.enableOutbound">
					</div>
					<div class="flex-row input-validation" ng-show="!form['input-fax-account-fax-no'].$valid">
						<label class="flex-row-label">*</label>
						<span><bean:message bundle="ui" key="admin.fax.acct.edit.faxNumber-invalid"/></span>
					</div>
				</div>
				<div class="flex-row">
					<label class="flex-row-label" for="input-fax-account-cover-letter-type"
					       title="<bean:message bundle="ui" key="admin.fax.acct.edit.coverLetterOption-tooltip"/>">
						<bean:message bundle="ui" key="admin.fax.acct.edit.coverLetterOption"/>
					</label>
					<select class="flex-row-content" id="input-fax-account-cover-letter-type"
					        ng-model="$ctrl.faxAccount.coverLetterOption"
					        ng-options="coverLetter for coverLetter in $ctrl.coverLetterOptions">
					</select>
				</div>
			</fieldset>
			<hr>
			<fieldset ng-disabled="!$ctrl.masterFaxEnabledInbound">
				<span ng-show="!$ctrl.masterFaxEnabledInbound"><bean:message bundle="ui" key="admin.fax.acct.edit.inboundDisabledMessage"/></span>
				<div class="flex-row content-end info-section">
					<span class="glyphicon info-icon glyphicon-info-sign"
					      title="<bean:message bundle="ui" key="admin.fax.acct.edit.inboundInfoMessage"/>">
					</span>
				</div>
				<div class="flex-row">
					<label class="flex-row-label" for="input-fax-enabled-inbound"><bean:message bundle="ui" key="admin.fax.acct.edit.enableInbound"/></label>
					<label class="flex-row-content switch">
						<input id="input-fax-enabled-inbound" type="checkbox"
						       ng-model="$ctrl.faxAccount.enableInbound"/>
						<span class="slider"></span>
					</label>
				</div>
			</fieldset>
			<hr>
			<div class="flex-row">
				<label class="flex-row-label"><bean:message bundle="ui" key="admin.fax.acct.edit.connectionStatus"/></label>
				<div class="flex-row-content">
					<span ng-show="$ctrl.faxAccount.connectionStatusUnknown"
					      class="glyphicon unknown glyphicon-question-sign"></span>
					<span ng-show="$ctrl.faxAccount.connectionStatusSuccess"
					      class="glyphicon success glyphicon-ok-sign"></span>
					<span ng-show="$ctrl.faxAccount.connectionStatusFailure"
					      class="glyphicon failure glyphicon-remove-sign"></span>
				</div>
			</div>
			<div class="flex-row">
				<div class="flex-row-label">
					<button type="button" class="btn input-content"
					        ng-click="$ctrl.testConnection()">
						<bean:message bundle="ui" key="admin.fax.acct.edit.btn-testConnection"/>
					</button>
				</div>
			</div>
		</div>
		<div class="modal-footer">
			<button ng-click="$ctrl.cancel()" type="button" class="btn" data-dismiss="modal">
				<bean:message key="modal.newPatient.cancel" bundle="ui"/>
			</button>
			<button ng-click="$ctrl.saveSettings(form)"
					ng-disabled="$ctrl.submitDisabled"
					data-dismiss="modal"
					type="button"
					class="btn btn-success">
				<bean:message key="modal.newPatient.submit" bundle="ui"/>
			</button>
		</div>
	</form>
</div>



