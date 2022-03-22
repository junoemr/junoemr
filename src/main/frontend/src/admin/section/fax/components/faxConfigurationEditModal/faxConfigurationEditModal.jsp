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
				<div>
					<juno-toggle round="true"
					             ng-model="$ctrl.faxAccount.enabled"
					             label="<bean:message bundle='ui' key='admin.fax.acct.edit.acctEnabled'/>"
					             label-class-list="['text-bold']"
					             label-position="$ctrl.LABEL_POSITION.RIGHT">
					</juno-toggle>
				</div>
				<div class="input-group grid-flow-row grid-row-2 grid-row-gap-4">
					<juno-input ng-model="$ctrl.faxAccount.accountLogin"
					            disabled="$ctrl.isModalEditMode() || $ctrl.faxAccountProvider.isOauth()"
					            ng-change="$ctrl.setDefaultConnectionStatus()"
					            invalid="$ctrl.initialSave && !$ctrl.validations.accountLoginFilled()"
					            label="<bean:message bundle="ui" key="admin.fax.acct.edit.accountLogin"/>"
					            label-position="$ctrl.LABEL_POSITION.LEFT">
					</juno-input>
					<juno-input ng-if="$ctrl.faxAccountProvider.showPasswordField()"
					            ng-model="$ctrl.faxAccount.password"
					            ng-change="$ctrl.setDefaultConnectionStatus()"
					            invalid="$ctrl.initialSave && !$ctrl.validations.passwordFilled()"
					            label="<bean:message bundle="ui" key="admin.fax.acct.edit.password"/>"
					            label-position="$ctrl.LABEL_POSITION.LEFT">
					</juno-input>
					<juno-input ng-model="$ctrl.faxAccount.displayName"
					            invalid="$ctrl.initialSave && !$ctrl.validations.displayNameFilled()"
					            title="<bean:message bundle="ui" key="admin.fax.acct.edit.displayName-tooltip"/>"
					            label="<bean:message bundle="ui" key="admin.fax.acct.edit.displayName"/>"
					            label-position="$ctrl.LABEL_POSITION.LEFT">
					</juno-input>
				</div>
			</div>
			<hr>
			<fieldset ng-disabled="!$ctrl.masterFaxEnabledOutbound" class="input-fieldset">
				<span ng-show="!$ctrl.masterFaxEnabledOutbound"><bean:message bundle="ui" key="admin.fax.acct.edit.outboundDisabledMessage"/></span>

				<div class="flex-row align-items-center justify-content-between">
					<juno-toggle round="true"
					             ng-model="$ctrl.faxAccount.enableOutbound"
					             label="<bean:message bundle='ui' key='admin.fax.acct.edit.enableOutbound'/>"
					             label-class-list="['text-bold']"
					             label-position="$ctrl.LABEL_POSITION.RIGHT">
					</juno-toggle>
					<i class="icon icon-info-circle info-badge" title="<bean:message bundle='ui' key='admin.fax.acct.edit.outboundInfoMessage'/>"></i>
				</div>
				<div class="input-group grid-flow-row grid-row-2 grid-row-gap-4">
					<juno-input ng-model="$ctrl.faxAccount.accountEmail"
					            ng-if="$ctrl.faxAccount.enableOutbound && $ctrl.faxAccountProvider.showOutboundEmailField()"
					            invalid="$ctrl.initialSave && !$ctrl.validations.emailFilled()"
					            title="<bean:message bundle="ui" key="admin.fax.acct.edit.accountEmail-tooltip"/>"
					            label="<bean:message bundle="ui" key="admin.fax.acct.edit.accountEmail"/>"
					            label-position="$ctrl.LABEL_POSITION.LEFT">
					</juno-input>
					<juno-input ng-model="$ctrl.faxAccount.faxNumber"
					            ng-if="$ctrl.faxAccount.enableOutbound && $ctrl.faxAccountProvider.showOutboundReturnFaxNoField()"
					            invalid="$ctrl.initialSave && !$ctrl.validations.faxNumberFilled()"
					            title="<bean:message bundle="ui" key="admin.fax.acct.edit.faxNumber-tooltip"/>"
					            label="<bean:message bundle="ui" key="admin.fax.acct.edit.faxNumber"/>"
					            label-position="$ctrl.LABEL_POSITION.LEFT">
					</juno-input>
					<juno-select ng-model="$ctrl.faxAccount.coverLetterOption"
					             options="$ctrl.coverLetterOptions"
					             ng-if="$ctrl.faxAccount.enableOutbound"
					             disabled="!$ctrl.initialized"
					             title="<bean:message bundle="ui" key="admin.fax.acct.edit.coverLetterOption-tooltip"/>"
					             label="<bean:message bundle="ui" key="admin.fax.acct.edit.coverLetterOption"/>"
					             label-position="$ctrl.LABEL_POSITION.LEFT">
					</juno-select>
				</div>
			</fieldset>
			<hr>
			<fieldset ng-disabled="!$ctrl.masterFaxEnabledInbound" class="input-fieldset">
				<span ng-show="!$ctrl.masterFaxEnabledInbound"><bean:message bundle="ui" key="admin.fax.acct.edit.inboundDisabledMessage"/></span>

				<div class="flex-row align-items-center justify-content-between">
					<juno-toggle round="true"
					             ng-model="$ctrl.faxAccount.enableInbound"
					             label="<bean:message bundle='ui' key='admin.fax.acct.edit.enableInbound'/>"
					             label-class-list="['text-bold']"
					             label-position="$ctrl.LABEL_POSITION.RIGHT">
					</juno-toggle>
					<i class="icon icon-info-circle info-badge" title="<bean:message bundle='ui' key='admin.fax.acct.edit.inboundInfoMessage'/>"></i>
				</div>
			</fieldset>
			<hr>
			<div class="flex-row input-fieldset" ng-class="$ctrl.getConnectionStatusClass()">
				<div class="input-group flex-row flex-grow">
					<juno-button click="$ctrl.testConnection()"
					             label="<bean:message bundle='ui' key='admin.fax.acct.edit.connectionStatus'/>"
					             label-position="$ctrl.LABEL_POSITION.LEFT"
					             button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
					             button-color="$ctrl.JUNO_BUTTON_COLOR_PATTERN.COLORED">
						<div class="flex-row justify-content-center align-items-center">
						<icon-badge class="m-r-4" icon="{{$ctrl.getConnectionIconClass()}}"></icon-badge>
						<span class="m-l-4">{{$ctrl.getConnectionTestText()}}</span>
						</div>
					</juno-button>
				</div>
			</div>
		</form>
	</modal-body>
	<modal-footer>
		<div class="flex-row justify-content-space-between align-items-center h-100">
			<div class="footer-left">
				<div class="w-128 m-r-2">
					<juno-button ng-show="$ctrl.isModalEditMode()"
					             click="$ctrl.deleteConfig()"
					             disabled="$ctrl.LoadingQueue.isLoading"
					             button-color="$ctrl.JUNO_BUTTON_COLOR.DANGER"
					             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.COLORED">
						<bean:message key="admin.fax.acct.edit.btn-delete" bundle="ui"/>
					</juno-button>
				</div>
			</div>
			<div class="footer-right flex-row">
				<div class="w-128 m-r-2">
					<juno-button click="$ctrl.cancel()"
					             disabled="$ctrl.LoadingQueue.isLoading"
					             button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
					             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.DEFAULT">
						<bean:message key="admin.fax.acct.edit.btn-cancel" bundle="ui"/>
					</juno-button>
				</div>
				<div class="w-128 m-l-2">
					<juno-button click="$ctrl.saveSettings()"
					             disabled="$ctrl.LoadingQueue.isLoading"
					             button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
					             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL">
						<bean:message key="admin.fax.acct.edit.btn-submit" bundle="ui"/>
					</juno-button>
				</div>
			</div>
		</div>
	</modal-footer>
</juno-modal>



