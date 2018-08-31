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
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-label="<bean:message key="global.close"/>"
		        ng-click="faxConfigEditController.cancel()">
			<span aria-hidden="true" >&times;</span>
		</button>
		<h3 class="modal-title">Edit Fax Account</h3>
	</div>
	<div class="modal-body">
		<div class="switch-container">
			<label class="input-label" for="input-fax-enabled">Account Enabled</label>
			<label class="switch">
				<input id="input-fax-enabled" type="checkbox"
				       ng-model="faxConfigEditController.faxAccount.enabled"/>
				<span class="slider"></span>
			</label>
		</div>
		<div>
			<label class="input-label" for="input-fax-account-id">Account Number</label>
			<input id="input-fax-account-id" type="text"
			       ng-change="faxConfigEditController.setDefaultConnectionStatus()"
			       ng-model="faxConfigEditController.faxAccount.accountLogin">
		</div>
		<div>
			<label class="input-label" for="input-fax-account-pw">Password</label>
			<input id="input-fax-account-pw" type="password"
			       ng-change="faxConfigEditController.setDefaultConnectionStatus()"
			       ng-model="faxConfigEditController.faxAccount.password">
		</div>
		<hr>
		<div>
			<label class="input-label" for="input-fax-account-name">Display Name</label>
			<input id="input-fax-account-name" type="text"
			       ng-model="faxConfigEditController.faxAccount.displayName">
		</div>
		<div>
			<label class="input-label" for="input-fax-account-cover-letter-type">Cover Letter</label>
			<select id="input-fax-account-cover-letter-type"
			        ng-model="faxConfigEditController.faxAccount.coverLetterOption"
			        ng-options="coverLetter for coverLetter in faxConfigEditController.coverLetterOptions">
			</select>
		</div>
		<div class="switch-container">
			<label class="input-label" for="input-fax-enabled-inbound">Inbound Faxing</label>
			<label class="switch">
				<input id="input-fax-enabled-inbound" type="checkbox"
				       ng-model="faxConfigEditController.faxAccount.enableInbound"/>
				<span class="slider"></span>
			</label>
		</div>
		<div class="switch-container">
			<label class="input-label" for="input-fax-enabled-outbound">Outbound Faxing</label>
			<label class="switch">
				<input id="input-fax-enabled-outbound" type="checkbox"
				       ng-model="faxConfigEditController.faxAccount.enableOutbound"/>
				<span class="slider"></span>
			</label>
		</div>
		<hr>
		<div>
			<%--<label class="input-label">Connection Status</label>--%>
			<div class="input-label">
				<button type="button" class="btn"
				        ng-click="faxConfigEditController.testConnection(faxConfigEditController.faxAccount)">
					Test Connection
				</button>
			</div>
			<div>
				<span ng-show="faxConfigEditController.faxAccount.connectionStatus == faxConfigEditController.connectionStatusEnum.unknown"
				      class="glyphicon unknown glyphicon-question-sign"></span>
				<span ng-show="faxConfigEditController.faxAccount.connectionStatus == faxConfigEditController.connectionStatusEnum.success"
				      class="glyphicon success glyphicon-ok-sign"></span>
				<span ng-show="faxConfigEditController.faxAccount.connectionStatus == faxConfigEditController.connectionStatusEnum.failure"
				      class="glyphicon failure glyphicon-remove-sign"></span>
			</div>
		</div>
	</div>
	<div class="modal-footer">
		<button ng-click="faxConfigEditController.cancel()" type="button" class="btn" data-dismiss="modal">
			<bean:message key="modal.newPatient.cancel" bundle="ui"/>
		</button>
		<button ng-click="faxConfigEditController.saveSettings(faxConfigEditController.faxAccount)" data-dismiss="modal" type="button" class="btn btn-success">
			<bean:message key="modal.newPatient.submit" bundle="ui"/>
		</button>
	</div>
</div>



