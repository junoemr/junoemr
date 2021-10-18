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
			<label>Consultation Cutoff Time Warning (in months):</label>
			<input ng-model="$ctrl.pref.consultationTimePeriodWarning" placeholder="Cutoff Time Warning" class="form-control" type="text">
		</div>
		<div class="form-group col-sm-12">
			<label>Consultation Team Warning:</label>
			<select class="form-control" ng-model="$ctrl.pref.consultationTeamWarning" ng-options="item.value as item.label for item in $ctrl.teamOptions">
			</select>
		</div>
		<div class="form-group col-sm-12">
			<label>Paste Format:</label>
			<select ng-model="$ctrl.pref.consultationPasteFormat" class="form-control" ng-options="p.value as p.label for p in $ctrl.pasteFormats">
			</select>
		</div>

		<div class="form-group col-sm-12">
			<label>Consult Letterhead Name Default:</label>
			<select ng-model="$ctrl.pref.consultationLetterHeadNameDefault" class="form-control" ng-options="l.value as l.label for l in $ctrl.letterHeadNameDefaults">
			</select>
		</div>
	</div>
</div>

