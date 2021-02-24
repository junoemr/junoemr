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
<div class="juno-progress-bar" ng-class="$ctrl.getComponentClasses()">
	<div class="flex-column width-100">
		<div ng-if="$ctrl.data.message" class="col-xs-12 no-padding alert-content text-center">
			<p>{{$ctrl.data.message}}</p>
		</div>
		<div class="progress">
			<div class="progress-bar" role="progressbar" aria-valuenow="75" aria-valuemin="0" aria-valuemax="100"
			     ng-style="$ctrl.getProgressStyle()">
				{{$ctrl.getProgressLabel()}}
			</div>
		</div>
	</div>
</div>