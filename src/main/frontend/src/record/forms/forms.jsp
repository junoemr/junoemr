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
<div id="forms-page" class="container-fluid flex-row">
	<div class="options-panel-place-holder">
		<div class=" options-panel">
			<div class="col-md-12 form-library-title" >
				<h2 class="form-heading"><bean:message key="forms.title.form.library" bundle="ui"/></h2>
			</div>
			<div class="col-md-12" >
				<div class="container-fluid">
					<input type="text" class="form-control search-query" ng-model="formCtrl.formSearchStr" placeholder="Filter Forms">
					<mode-selector-component mode-change="formCtrl.onModeChange(mode)" view-state="formCtrl.viewState"></mode-selector-component>
					<hr>
					<group-selector-component group-change="formCtrl.onGroupChange(groupId, selectedForms)" group-selection="formCtrl.groupSelection"></group-selector-component>
				</div>
			</div>
		</div>
	</div>
	<div class="col-md-10">
		<div class="col-md-12">
			<div class="col-md-12 flex-row-reverse left-button-group" >
				<button class="btn btn-primary" ng-click="formCtrl.openAddFormsModal()">Add Form</button>
				<button class="btn btn-outline" ng-click="formCtrl.openManageForms()">Manage Forms</button>
				<button class="btn btn-outline" ng-click="formCtrl.showEditPopup()">Edit Groups</button>
			</div>
		</div>
		<div class="col-md-12">
			<form-view-component ng-if="formCtrl.viewState === FORM_CONTROLLER_STATES.ADD" form-list="displayFormList"
								 provider-no="formCtrl.providerNo" filter-forms="onFilterForms(form, index, array)"
								 view-state="FORM_CONTROLLER_STATES.ADD" instanced-forms="false"></form-view-component>

			<form-view-component ng-if="formCtrl.viewState === FORM_CONTROLLER_STATES.COMPLETED" form-list="displayFormList"
								 provider-no="formCtrl.providerNo" filter-forms="onFilterForms(form, index, array)"
								 view-state="FORM_CONTROLLER_STATES.COMPLETED" instanced-forms="true"></form-view-component>

			<form-view-component ng-if="formCtrl.viewState === FORM_CONTROLLER_STATES.REVISION" form-list="displayFormList"
								 provider-no="formCtrl.providerNo" filter-forms="onFilterForms(form, index, array)"
								 view-state="FORM_CONTROLLER_STATES.REVISION" instanced-forms="true"></form-view-component>

			<form-view-component ng-if="formCtrl.viewState === FORM_CONTROLLER_STATES.DELETED" form-list="displayFormList"
								 provider-no="formCtrl.providerNo" filter-forms="onFilterForms(form, index, array)"
								 view-state="FORM_CONTROLLER_STATES.DELETED" instanced-forms="true"></form-view-component>
		</div>
	</div>
</div>



<!--
REF DELETE LATER
<ul class="nav nav-tabs nav-justified">
<li ng-repeat="list in formCtrl.page.formlists"
ng-class="formCtrl.getListClass(list.id)"
class="hand-hover">
<a ng-click="formCtrl.changeTo(list.id)">{{list.label}}</a>
</li>

<li class="dropdown">
<a class="dropdown-toggle" data-toggle="dropdown" >
<span class="glyphicon glyphicon-tasks"> </span>
</a>
<ul class="dropdown-menu">
<li ng-show="formCtrl.hasAdminAccess">
<a class="hand-hover" onclick="popup(600, 1200, '../administration/?show=Forms', 'manageeforms')" >Manage eForms</a>
</li>
<li ng-show="formCtrl.hasAdminAccess">
<a class="hand-hover" onclick="popup(600, 1200, '../administration/?show=Forms&load=Groups', 'editGroups')" >Edit Groups</a>
</li>
<%--<li ng-show="formCtrl.hasAdminAccess" class="divider"></li>--%>
<li ng-repeat="opt in formCtrl.page.formOptions">
<a ng-click="formCtrl.formOption(opt)">{{opt.label}}</a>
</li>
</ul>
</li>
</ul>
<%--
<fieldset >
			   <legend style="margin-bottom:0px;">All Forms</legend>
			   <input type="search" class="form-control" placeholder="Filter" ng-model="filterFormsQ">
			<ul style="padding-left:12px;">
			<li ng-repeat="item in page.currentFormList[page.currentlistId] | filter:filterFormsQ"   ><a ng-click="viewForm(item.id)">{{item.label}}<small ng-show="item.type">({{item.type}})</small></a> <span class="pull-right">{{item.date}}</span></li>
			</ul>
</fieldset>
 --%>
<div id="forms-panel" class="panel panel-success">
<input type="search" class="form-control" placeholder="Filter" ng-model="formCtrl.filterFormsQ">
<ul class="list-group" tabindex="0" ng-keypress="formCtrl.keypress($event)">

	<li class="list-group-item"
		ng-repeat="item in formCtrl.page.currentFormList[formCtrl.page.currentlistId] | filter:formCtrl.filterFormsQ"
		ng-class="formCtrl.getActiveFormClass(item)">
		<input type="checkbox" ng-model="item.isChecked"/>
		<a class="list-group-item-text hand-hover" title="{{item.subject}}" ng-click="formCtrl.viewFormState(item,1)">
			<span  ng-show="item.date" class="pull-right">{{item.date | date : 'd-MMM-y'}}</span>
			{{item.name}}
		</a>
	</li>

	<li class="list-group-item"
		ng-repeat="formItem2 in formCtrl.page.encounterFormlist[0] | filter:formCtrl.filterFormsQ"
		ng-hide="formCtrl.page.currentlistId==1">
		<a class="list-group-item-text hand-hover" ng-click="formCtrl.viewFormState(formItem2,1)">{{formItem2.name}}</a>
	</li>

	<li class="list-group-item" ng-repeat="formItem in formCtrl.page.encounterFormlist[1].content | filter:formCtrl.filterFormsQ"
		ng-hide="formCtrl.page.currentlistId==0">
		<a class="list-group-item-text hand-hover" ng-click="formCtrl.viewFormState(formItem,1)">
			{{formItem.formName}}
			<span ng-show="formItem.date" class="pull-right">{{formItem.date | date : 'd-MMM-y'}}</span>
		</a>
	</li>

</ul>
</div>
</div>
<div class="col-lg-9 col-md-8">
	<a class="hand-hover pull-right" ng-show="!formCtrl.isEmpty(formCtrl.page.currentForm)"
	   title="Open in new window" ng-click="formCtrl.viewFormState(formCtrl.page.currentForm,2)">
		<span class="glyphicon glyphicon-new-window"></span>
	</a>
	<div ng-if="formCtrl.isEmpty(formCtrl.page.currentForm)">
		<h2><bean:message key="forms.title.form.library" bundle="ui"/></h2>
		<div>

			<div  class="col-sm-4">
				<legend>
					<bean:message key="forms.title.form.groups" bundle="ui"/>
				</legend>
				<ul class="list-group" >
					<li ng-repeat="mod in formCtrl.page.formGroups" class="list-group-item" ng-class="formCtrl.getGroupListClass(mod)">
						<span class="badge">{{mod.summaryItem.length}}</span>
						<a class="list-group-item-text" ng-click="formCtrl.setCurrentEFormGroup(mod)" href="javascript:void(0)"> {{mod.displayName}} </a>
					</li>
				</ul>
			</div>

			<div  class="col-sm-4">
				<legend>
					&nbsp;{{formCtrl.currentEformGroup.displayName}}
				</legend>
				<ul>
					<li ng-repeat="item in formCtrl.currentEformGroup.summaryItem">
						<span class="pull-right">{{item.date | date : 'dd-MMM-yyyy'}}</span>
						<a ng-click="formCtrl.openFormFromGroups(item)" href="javascript:void(0)" ng-class="item.indicatorClass" >
							{{item.displayName | limitTo: 34 }} {{item.displayName.length > 34 ? '...' : '' }}
							<small ng-show="item.classification">({{item.classification}})</small>
						</a>
					</li>
				</ul>
			</div>

			<div ng-if="formCtrl.favouriteGroup"  class="col-sm-4">
				<legend>
					<bean:message key="forms.title.form.favourite" bundle="ui"/>: {{formCtrl.favouriteGroup.displayName}}
				</legend>

				<ul>
					<li ng-repeat="item in formCtrl.favouriteGroup.summaryItem">
						<span class="pull-right">{{item.date | date : 'dd-MMM-yyyy'}}</span>
						<a ng-click="formCtrl.openFormFromGroups(item)" href="javascript:void(0)" ng-class="item.indicatorClass" >
							{{item.displayName | limitTo: 34 }} {{item.displayName.length > 34 ? '...' : '' }}
							<small ng-show="item.classification">({{item.classification}})</small>
						</a>
					</li>
				</ul>
			</div>
		</div>
	</div>
	<div id="formInViewFrame"></div>

-->