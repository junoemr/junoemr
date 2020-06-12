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
<div class="juno-alert height-100">
	<div class="flex-column height-100">
		<div>
			<h6 class="body-bold text-center">{{$ctrl.resolve.title}}</h6>
			<hr class="md-margin-bottom no-margin-top header-divider">
		</div>
		<div class="col-xs-12 no-padding alert-content text-center">
			<p>{{$ctrl.resolve.message}}</p>
		</div>
		<div class="col-xs-12 no-padding md-margin-top">
			<div class="width-100" ng-if="$ctrl.resolve.mode === $ctrl.alertModes.ERROR || $ctrl.resolve.mode === $ctrl.alertModes.SUCCESS">
				<button
								class="btn width-100"
								ng-click="$ctrl.close()"
								ng-class="{
									'btn-danger': $ctrl.resolve.mode === $ctrl.alertModes.ERROR,
									'btn-primary': $ctrl.resolve.mode === $ctrl.alertModes.SUCCESS
								}">
					Ok
				</button>
			</div>
			<div class="width-100 flex-row justify-content-center" ng-if="$ctrl.resolve.mode === $ctrl.alertModes.CONFIRM">
				<button class="btn btn-danger confirm-btn flex-grow sm-margin-right" ng-click="$ctrl.onSelection(false)">Cancel</button>
				<button class="btn btn-primary confirm-btn flex-grow sm-margin-left" ng-click="$ctrl.onSelection(true)">Ok</button>
			</div>
		</div>
	</div>
</div>