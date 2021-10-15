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
			<label>Default HC Type:</label>
			<select class="form-control" ng-model="$ctrl.pref.defaultHcType">
				<option value="" >--</option>
				<option value="AB" >AB-Alberta</option>
				<option value="BC" >BC-British Columbia</option>
				<option value="MB" >MB-Manitoba</option>
				<option value="NB" >NB-New Brunswick</option>
				<option value="NL" >NL-Newfoundland Labrador</option>
				<option value="NT" >NT-Northwest Territory</option>
				<option value="NS" >NS-Nova Scotia</option>
				<option value="NU" >NU-Nunavut</option>
				<option value="ON" >ON-Ontario</option>
				<option value="PE" >PE-Prince Edward Island</option>
				<option value="QC" >QC-Quebec</option>
				<option value="SK" >SK-Saskatchewan</option>
				<option value="YT" >YT-Yukon</option>
				<option value="US" >US resident</option>
				<option value="US-AK" >US-AK-Alaska</option>
				<option value="US-AL" >US-AL-Alabama</option>
				<option value="US-AR" >US-AR-Arkansas</option>
				<option value="US-AZ" >US-AZ-Arizona</option>
				<option value="US-CA" >US-CA-California</option>
				<option value="US-CO" >US-CO-Colorado</option>
				<option value="US-CT" >US-CT-Connecticut</option>
				<option value="US-CZ" >US-CZ-Canal Zone</option>
				<option value="US-DC" >US-DC-District Of Columbia</option>
				<option value="US-DE" >US-DE-Delaware</option>
				<option value="US-FL" >US-FL-Florida</option>
				<option value="US-GA" >US-GA-Georgia</option>
				<option value="US-GU" >US-GU-Guam</option>
				<option value="US-HI" >US-HI-Hawaii</option>
				<option value="US-IA" >US-IA-Iowa</option>
				<option value="US-ID" >US-ID-Idaho</option>
				<option value="US-IL" >US-IL-Illinois</option>
				<option value="US-IN" >US-IN-Indiana</option>
				<option value="US-KS" >US-KS-Kansas</option>
				<option value="US-KY" >US-KY-Kentucky</option>
				<option value="US-LA" >US-LA-Louisiana</option>
				<option value="US-MA" >US-MA-Massachusetts</option>
				<option value="US-MD" >US-MD-Maryland</option>
				<option value="US-ME" >US-ME-Maine</option>
				<option value="US-MI" >US-MI-Michigan</option>
				<option value="US-MN" >US-MN-Minnesota</option>
				<option value="US-MO" >US-MO-Missouri</option>
				<option value="US-MS" >US-MS-Mississippi</option>
				<option value="US-MT" >US-MT-Montana</option>
				<option value="US-NC" >US-NC-North Carolina</option>
				<option value="US-ND" >US-ND-North Dakota</option>
				<option value="US-NE" >US-NE-Nebraska</option>
				<option value="US-NH" >US-NH-New Hampshire</option>
				<option value="US-NJ" >US-NJ-New Jersey</option>
				<option value="US-NM" >US-NM-New Mexico</option>
				<option value="US-NU" >US-NU-Nunavut</option>
				<option value="US-NV" >US-NV-Nevada</option>
				<option value="US-NY" >US-NY-New York</option>
				<option value="US-OH" >US-OH-Ohio</option>
				<option value="US-OK" >US-OK-Oklahoma</option>
				<option value="US-OR" >US-OR-Oregon</option>
				<option value="US-PA" >US-PA-Pennsylvania</option>
				<option value="US-PR" >US-PR-Puerto Rico</option>
				<option value="US-RI" >US-RI-Rhode Island</option>
				<option value="US-SC" >US-SC-South Carolina</option>
				<option value="US-SD" >US-SD-South Dakota</option>
				<option value="US-TN" >US-TN-Tennessee</option>
				<option value="US-TX" >US-TX-Texas</option>
				<option value="US-UT" >US-UT-Utah</option>
				<option value="US-VA" >US-VA-Virginia</option>
				<option value="US-VI" >US-VI-Virgin Islands</option>
				<option value="US-VT" >US-VT-Vermont</option>
				<option value="US-WA" >US-WA-Washington</option>
				<option value="US-WI" >US-WI-Wisconsin</option>
				<option value="US-WV" >US-WV-West Virginia</option>
				<option value="US-WY" >US-WY-Wyoming</option>
				<option value="OT">Other</option>
			</select>
		</div>
		<div class="form-group col-sm-12">
			<label>Default Sex:</label>
			<select ng-model="$ctrl.pref.defaultSex" class="form-control">
				<option value="M">Male</option>
				<option value="F">Female</option>
				<option value="T">Transgender</option>
				<option value="O">Other</option>
				<option value="U">Undefined</option>
			</select>
		</div>
	</div>
</div>
