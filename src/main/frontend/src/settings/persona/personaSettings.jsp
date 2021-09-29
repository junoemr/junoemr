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
	<div class="col-sm-6">
		<div class="col-sm-12">
			<h3>Dashboard</h3>
			<hr>
		</div>
		<div class="form-group col-sm-12">
			Enable Juno UI on login:<br>
			<label class="radio-inline" for="radios-per-0">
				<input ng-model="$ctrl.pref.useCobaltOnLogin" name="radios-per-0" id="radios-per-0" ng-value="true" type="radio">
				Enable
			</label>
			<label class="radio-inline" for="radios-per-1">
				<input ng-model="$ctrl.pref.useCobaltOnLogin" name="radios-per-0" id="radios-per-1" ng-value="false" type="radio">
				Disable
			</label>
		</div>
		<div class="col-sm-12">
			<h3>Recent Patient List</h3>
			<hr>
		</div>
		<div class="form-group col-sm-4">
			Number of recent patients to display:<br>
			<select ng-model="$ctrl.pref.recentPatients" class="form-control">
				<option value="1">1</option>
				<option value="2">2</option>
				<option value="3">3</option>
				<option value="4">4</option>
				<option value="5">5</option>
				<option value="6">6</option>
				<option value="7">7</option>
				<option value="8">8</option>
				<option value="9">9</option>
				<option value="10">10</option>
				<option value="11">11</option>
				<option value="12">12</option>
				<option value="13">13</option>
				<option value="14">14</option>
				<option value="15">15</option>
				<option value="16">16</option>
			</select>
		</div>
		<!-- Dashboard -->
	</div>
</div>
