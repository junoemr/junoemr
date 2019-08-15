<%--

    Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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

    This software was written for the
    Department of Family Medicine
    McMaster University
    Hamilton
    Ontario, Canada

--%>
<h1>No Show Appointment Sheet</h1>

<div class="row" ng-controller="Report.ReportNoShowAppointmentSheetController as reportNoShowAppointmentSheetCtrl">
	<div class="col-md-4">
		<form role="form">
			<div class="form-group">
				<label>Provider:</label>

				<input type="text"
					   ng-model="reportNoShowAppointmentSheetCtrl.params.name"
					   placeholder="Provider"
					   uib-typeahead="pt.providerNo as pt.name for pt in reportNoShowAppointmentSheetCtrl.searchProviders($viewValue)"
					   typeahead-on-select="reportNoShowAppointmentSheetCtrl.updateProviderNo($item, $model, $label)"
					   class="form-control"/>

			</div>
			<div class="form-group">
				<label for="startDate">Start Date</label>
				<input ng-model="reportNoShowAppointmentSheetCtrl.params.startDate"
					   type="date"
					   id="startDate"
					   name="startDate"
					   class="form-control">
			</div>

			<button type="submit" class="btn btn-default" ng-click="reportNoShowAppointmentSheetCtrl.generateReport()">Generate Report</button>
		</form>
	</div>
</div>