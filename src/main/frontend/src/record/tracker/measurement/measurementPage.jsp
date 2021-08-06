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

<div class="tracker-measurements">
	<h5>Recorded Measurements</h5>

	<div ng-repeat="(type, measurements) in $ctrl.measurementGroups"
	     class="flex-column flex-wrap flex-grow flex-gap-4">
		<h6>{{type}}</h6>
		<div class="flex-row flex-wrap flex-grow flex-gap-4">
			<care-tracker-item-data ng-repeat="measurement in measurements" model="measurement"></care-tracker-item-data>
		</div>
	</div>
</div>