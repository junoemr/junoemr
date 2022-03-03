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
<juno-modal class="fax-configuration-edit-modal" component-style="$ctrl.resolve.style">
	<modal-title>
		<h3><bean:message bundle="ui" key="admin.fax.acct.edit.header"/></h3>
	</modal-title>
	<modal-ctl-buttons>
		<juno-simple-close-button click="$ctrl.cancel()"></juno-simple-close-button>
	</modal-ctl-buttons>
	<modal-body>
		<form name="form" class="fax-configuration-edit-modal-content">
			<div class="flex-column input-fieldset">
				<div class="m-l-48">
					<juno-toggle round="true"
					             ng-model="$ctrl.faxAccount.enabled"
					             label="<bean:message bundle='ui' key='admin.fax.acct.edit.acctEnabled'/>"
					             label-position="$ctrl.LABEL_POSITION.RIGHT">
					</juno-toggle>
				</div>
				<div class="input-group grid-flow-row grid-row-2 grid-row-gap-4">
					<juno-input ng-model="$ctrl.faxAccount.accountLogin"
					            disabled="$ctrl.isModalEditMode()"
					            ng-change="$ctrl.setDefaultConnectionStatus()"
					            label="<bean:message bundle="ui" key="admin.fax.acct.edit.accountLogin"/>"
					            label-position="$ctrl.LABEL_POSITION.LEFT">
					</juno-input>
					<juno-input ng-model="$ctrl.faxAccount.password"
					            ng-change="$ctrl.setDefaultConnectionStatus()"
					            label="<bean:message bundle="ui" key="admin.fax.acct.edit.password"/>"
					            label-position="$ctrl.LABEL_POSITION.LEFT">
					</juno-input>
					<juno-input ng-model="$ctrl.faxAccount.displayName"
					            title="<bean:message bundle="ui" key="admin.fax.acct.edit.displayName-tooltip"/>"
					            label="<bean:message bundle="ui" key="admin.fax.acct.edit.displayName"/>"
					            label-position="$ctrl.LABEL_POSITION.LEFT">
					</juno-input>
				</div>
			</div>
			<hr>
			<fieldset ng-disabled="!$ctrl.masterFaxEnabledOutbound" class="input-fieldset">
				<span ng-show="!$ctrl.masterFaxEnabledOutbound"><bean:message bundle="ui" key="admin.fax.acct.edit.outboundDisabledMessage"/></span>
				<div class="flex-row content-end info-section">
					<span class="glyphicon info-icon glyphicon-info-sign"
					      title="<bean:message bundle="ui" key="admin.fax.acct.edit.outboundInfoMessage"/>">
					</span>
				</div>
				<div class="m-l-48">
					<juno-toggle round="true"
					             ng-model="$ctrl.faxAccount.enableOutbound"
					             label="<bean:message bundle='ui' key='admin.fax.acct.edit.enableOutbound'/>"
					             label-position="$ctrl.LABEL_POSITION.RIGHT">
					</juno-toggle>
				</div>
				<div class="input-group grid-flow-row grid-row-2 grid-row-gap-4">
					<juno-input ng-model="$ctrl.faxAccount.accountEmail"
					            disabled="!$ctrl.faxAccount.enableOutbound"
					            title="<bean:message bundle="ui" key="admin.fax.acct.edit.accountEmail-tooltip"/>"
					            label="<bean:message bundle="ui" key="admin.fax.acct.edit.accountEmail"/>"
					            label-position="$ctrl.LABEL_POSITION.LEFT">
					</juno-input>
					<juno-input ng-model="$ctrl.faxAccount.faxNumber"
					            disabled="!$ctrl.faxAccount.enableOutbound"
					            title="<bean:message bundle="ui" key="admin.fax.acct.edit.faxNumber-tooltip"/>"
					            label="<bean:message bundle="ui" key="admin.fax.acct.edit.faxNumber"/>"
					            label-position="$ctrl.LABEL_POSITION.LEFT">
					</juno-input>
					<juno-select ng-model="$ctrl.faxAccount.coverLetterOption"
					             options="$ctrl.coverLetterOptions"
					             disabled="!$ctrl.faxAccount.enableOutbound"
					             title="<bean:message bundle="ui" key="admin.fax.acct.edit.coverLetterOption-tooltip"/>"
					             label="<bean:message bundle="ui" key="admin.fax.acct.edit.coverLetterOption"/>"
					             label-position="$ctrl.LABEL_POSITION.LEFT">
					</juno-select>
				</div>
<%--					<div class="flex-row">--%>
<%--						<label class="flex-row-label" for="input-fax-account-email"--%>
<%--						       title="<bean:message bundle="ui" key="admin.fax.acct.edit.accountEmail-tooltip"/>">--%>
<%--							<bean:message bundle="ui" key="admin.fax.acct.edit.accountEmail"/>--%>
<%--						</label>--%>
<%--						<input class="flex-row-content" id="input-fax-account-email" name="input-fax-account-email" type="email"--%>
<%--						       ng-model="$ctrl.faxAccount.accountEmail"--%>
<%--						       ng-required="$ctrl.faxAccount.enableOutbound">--%>
<%--					</div>--%>
<%--					<div class="flex-row input-validation" ng-show="!form['input-fax-account-email'].$valid">--%>
<%--						<label class="flex-row-label">*</label>--%>
<%--						<span><bean:message bundle="ui" key="admin.fax.acct.edit.accountEmail-invalid"/></span>--%>
<%--					</div>--%>
<%--				<div>--%>
<%--					<div class="flex-row">--%>
<%--						<label class="flex-row-label" for="input-fax-account-fax-no"--%>
<%--						       title="<bean:message bundle="ui" key="admin.fax.acct.edit.faxNumber-tooltip"/>">--%>
<%--							<bean:message bundle="ui" key="admin.fax.acct.edit.faxNumber"/>--%>
<%--						</label>--%>
<%--						<input class="flex-row-content" id="input-fax-account-fax-no" name="input-fax-account-fax-no" type="text"--%>
<%--						       ng-model="$ctrl.faxAccount.faxNumber"--%>
<%--						       ng-minlength="10" ng-maxlength="10" ng-pattern="/\d{10}/" ng-required="$ctrl.faxAccount.enableOutbound">--%>
<%--					</div>--%>
<%--					<div class="flex-row input-validation" ng-show="!form['input-fax-account-fax-no'].$valid">--%>
<%--						<label class="flex-row-label">*</label>--%>
<%--						<span><bean:message bundle="ui" key="admin.fax.acct.edit.faxNumber-invalid"/></span>--%>
<%--					</div>--%>
<%--				</div>--%>
<%--				<div class="flex-row">--%>
<%--					<label class="flex-row-label" for="input-fax-account-cover-letter-type"--%>
<%--					       title="<bean:message bundle="ui" key="admin.fax.acct.edit.coverLetterOption-tooltip"/>">--%>
<%--						<bean:message bundle="ui" key="admin.fax.acct.edit.coverLetterOption"/>--%>
<%--					</label>--%>
<%--					<select class="flex-row-content" id="input-fax-account-cover-letter-type"--%>
<%--					        ng-model="$ctrl.faxAccount.coverLetterOption"--%>
<%--					        ng-options="coverLetter for coverLetter in $ctrl.coverLetterOptions">--%>
<%--					</select>--%>
<%--				</div>--%>
			</fieldset>
			<hr>
			<fieldset ng-disabled="!$ctrl.masterFaxEnabledInbound" class="input-fieldset">
				<span ng-show="!$ctrl.masterFaxEnabledInbound"><bean:message bundle="ui" key="admin.fax.acct.edit.inboundDisabledMessage"/></span>
				<div class="flex-row content-end info-section">
					<span class="glyphicon info-icon glyphicon-info-sign"
					      title="<bean:message bundle="ui" key="admin.fax.acct.edit.inboundInfoMessage"/>">
					</span>
				</div>
				<div class="m-l-48 flex-row">
					<juno-toggle round="true"
					             ng-model="$ctrl.faxAccount.enableInbound"
					             label="<bean:message bundle='ui' key='admin.fax.acct.edit.enableInbound'/>"
					             label-position="$ctrl.LABEL_POSITION.RIGHT">
					</juno-toggle>
				</div>
			</fieldset>
			<hr>
			<div class="flex-row input-fieldset" ng-class="$ctrl.getConnectionStatusClass()">
				<juno-button click="$ctrl.testConnection()"
				             label="<bean:message bundle='ui' key='admin.fax.acct.edit.connectionStatus'/>"
				             label-position="$ctrl.LABEL_POSITION.LEFT"
				             button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
				             button-color="$ctrl.JUNO_BUTTON_COLOR_PATTERN.COLORED">
					<icon-badge icon="{{$ctrl.getConnectionIconClass()}}"></icon-badge>
					<bean:message bundle="ui" key="admin.fax.acct.edit.btn-testConnection"/>
				</juno-button>
			</div>
		</form>
	</modal-body>
	<modal-footer>
		<div class="flex-row justify-content-space-between">
			<div class="footer-left">
				<div class="w-128 m-r-2">
					<juno-button ng-show="$ctrl.isModalEditMode()"
					             click="$ctrl.deleteConfig()"
					             button-color="$ctrl.JUNO_BUTTON_COLOR.DANGER"
					             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.COLORED">
						<bean:message key="admin.fax.acct.edit.btn-delete" bundle="ui"/>
					</juno-button>
				</div>
			</div>
			<div class="footer-right flex-row">
				<div class="w-128 m-r-2">
					<juno-button click="$ctrl.cancel()"
					             button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
					             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.DEFAULT">
						<bean:message key="admin.fax.acct.edit.btn-cancel" bundle="ui"/>
					</juno-button>
				</div>
				<div class="w-128 m-l-2">
					<juno-button click="$ctrl.saveSettings(form)"
					             disabled="$ctrl.submitDisabled"
					             button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
					             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL">
						<bean:message key="admin.fax.acct.edit.btn-submit" bundle="ui"/>
					</juno-button>
				</div>
			</div>
		</div>
	</modal-footer>
</juno-modal>



