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
<div class="row">
	<div class="col-lg-4 col-sm-6 md-margin-top">
		<div class="form-group col-sm-12">
			<label>Document Browser In Document Report:</label>
			<div class="controls">
				<label class="radio-inline" for="radios-doc-0">
					<input name="radios-doc-0" id="radios-doc-0" ng-model="$ctrl.pref.documentBrowserInDocumentReport" ng-value="true" type="radio">
					Enable
				</label>
				<label class="radio-inline" for="radios-doc-1">
					<input name="radios-doc-1" id="radios-doc-1" ng-model="$ctrl.pref.documentBrowserInDocumentReport" ng-value="false" type="radio">
					Disable
				</label>
			</div>
		</div>
		<div class="form-group col-sm-12">
			<label>Document Browser In Master File:</label>
			<div class="controls">
				<label class="radio-inline" for="radios-doc-2">
					<input name="radios-doc-2" id="radios-doc-2" ng-model="$ctrl.pref.documentBrowserInMasterFile" ng-value="true" type="radio">
					Enable
				</label>
				<label class="radio-inline" for="radios-doc-3">
					<input name="radios-doc-3" id="radios-doc-3" ng-model="$ctrl.pref.documentBrowserInMasterFile" ng-value="false" type="radio">
					Disable
				</label>
			</div>
		</div>
		<div class="form-group col-sm-12">
			<label>Clinic Document Template</label>
			<div class="controls">
				<button class="btn btn-default" ng-click="$ctrl.editDocumentTemplates()">Manage Document Templates</button>
			</div>
		</div>
	</div>
</div>

