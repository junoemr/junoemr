<%--
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
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<juno-security-check show-placeholder="true" permissions="[formCtrl.SecurityPermissions.EformRead, formCtrl.SecurityPermissions.FormRead]">
	<div id="forms-page" class="container-fluid flex-row">
		<div class="options-panel-place-holder">
			<div class=" options-panel">
				<div class="col-md-12 form-library-title" >
					<h2 class="form-heading">Patient Forms</h2>
				</div>
				<div class="col-md-12" >
					<div class="container-fluid">
						<mode-selector-component mode-change="formCtrl.onModeChange(mode)" view-state="formCtrl.viewState"></mode-selector-component>
						<hr>
						<group-selector-component group-change="formCtrl.onGroupChange(groupId, selectedForms)" group-selection="formCtrl.groupSelection"></group-selector-component>
					</div>
				</div>
			</div>
		</div>
		<div class="col-md-10">
			<div class="col-md-12">
				<div class="col-md-6 left-header-group">
					<input type="text" class="form-control search-query" ng-model="formCtrl.formSearchStr" placeholder="Search">
				</div>
				<div class="col-md-6 flex-row-reverse right-header-group" >
				</div>
			</div>
			<div class="col-md-12">
				<form-view-component ng-if="formCtrl.viewState === FORM_CONTROLLER_STATES.ADD" form-list="displayFormList"
									 provider-no="formCtrl.providerNo" filter-forms="onFilterForms(form, index, array)"
									 view-state="FORM_CONTROLLER_STATES.ADD" instanced-forms="false"
				                     reload-forms="formCtrl.onModeChange(FORM_CONTROLLER_STATES.ADD)"></form-view-component>

				<form-view-component ng-if="formCtrl.viewState === FORM_CONTROLLER_STATES.COMPLETED" form-list="displayFormList"
									 provider-no="formCtrl.providerNo" filter-forms="onFilterForms(form, index, array)"
									 view-state="FORM_CONTROLLER_STATES.COMPLETED" instanced-forms="true"
									 reload-forms="formCtrl.onModeChange(FORM_CONTROLLER_STATES.COMPLETED)"></form-view-component>

				<form-view-component ng-if="formCtrl.viewState === FORM_CONTROLLER_STATES.REVISION" form-list="displayFormList"
									 provider-no="formCtrl.providerNo" filter-forms="onFilterForms(form, index, array)"
									 view-state="FORM_CONTROLLER_STATES.REVISION" instanced-forms="true"
									 reload-forms="formCtrl.onModeChange(FORM_CONTROLLER_STATES.REVISION)"></form-view-component>

				<form-view-component ng-if="formCtrl.viewState === FORM_CONTROLLER_STATES.DELETED" form-list="displayFormList"
									 provider-no="formCtrl.providerNo" filter-forms="onFilterForms(form, index, array)"
									 view-state="FORM_CONTROLLER_STATES.DELETED" instanced-forms="true"
									 reload-forms="formCtrl.onModeChange(FORM_CONTROLLER_STATES.DELETED)"></form-view-component>
			</div>
		</div>
	</div>
</juno-security-check>