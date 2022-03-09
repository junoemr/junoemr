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

<div class="demographic-card flex-column align-items-center justify-content-center">
	<div class="demographic-card-form form-horizontal" ng-if="!$ctrl.disabled">
		<!-- patient dob -->
		<div class="form-group">
			<label class="control-label col-sm-2">
				Born:
			</label>
			<div class="col-sm-10">
				<span class="form-control-static input-sm">
					{{ $ctrl.model.displayData.birthDate }}
				</span>
			</div>
		</div>
		<!-- gender -->
		<div class="form-group">
			<label class="control-label col-sm-2">
				Sex:
			</label>
			<div class="col-sm-10">
				<span class="form-control-static input-sm">
					{{ $ctrl.model.data.displaySex }}
				</span>
			</div>
		</div>
		<!-- patient hin -->
		<div class="form-group">
			<label class="control-label col-sm-2">
				HIN:
			</label>
			<div class="col-sm-10">
				<span class="form-control-static">
					<span class="patient-health-number" ng-if="$ctrl.model.data.healthNumber">
						{{$ctrl.model.data.healthNumber}}
						{{$ctrl.model.data.healthNumberVersion}}
					</span>
					<%--<button type="button"--%>
					        <%--aria-label="Check Eligibility"--%>
					        <%--title="{{$ctrl.model.eligibilityText}}"--%>
					        <%--class="btn"--%>
					        <%--ng-class="{--%>
											<%--'btn-addon': ($ctrl.model.checkingEligibility || $ctrl.model.eligibility == null) && !$ctrl.model.pollingEligibility,--%>
											<%--'btn-warning': $ctrl.model.pollingEligibility,--%>
											<%--'btn-success': $ctrl.model.eligibility == 'eligible',--%>
											<%--'btn-danger': $ctrl.model.eligibility == 'ineligible' }"--%>
					        <%--ng-click="$ctrl.model.getEligibility(true, true)">--%>
						<%--<i class="fa fa-user" aria-hidden="true"></i>--%>
					<%--</button>--%>
				</span>
			</div>
		</div>
		<!-- patient address -->
		<div class="form-group">
			<label class="control-label col-sm-2">
				Address:
			</label>
			<div class="col-sm-10">
				<span class="form-control-static">{{$ctrl.model.displayData.addressLine}}</span>
			</div>
		</div>
		<!-- patient phone -->
		<div class="form-group">
			<label class="control-label col-sm-2">
				Phone:
			</label>
			<div class="col-sm-10">
				<span class="form-control-static">{{$ctrl.model.data.primaryPhone.formattedForDisplay}}</span>
			</div>
		</div>
		<!-- patient email -->
		<div class="form-group">
			<label class="control-label col-sm-2">
				Email:
			</label>
			<div class="col-sm-10">
				<span class="form-control-static">{{$ctrl.model.data.email}}</span>
			</div>
		</div>
	</div>
</div>