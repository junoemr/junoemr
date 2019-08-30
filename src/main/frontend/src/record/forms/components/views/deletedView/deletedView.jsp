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
<div id="deleted-view-component" class="container-fluid">
	<table ng-table="$ctrl.tableParams" show-filter="false" class="table table-striped table-bordered">
		<tbody>
			<tr ng-repeat="form in $ctrl.formList | filter:$ctrl.doFilterForms | orderBy:$ctrl.sortMode">
				<td class="col-md-3" title="'Form Name'" sortable="'name'">
					<u><a href="javascript:" ng-click="$ctrl.openForm(form.id)">{{form.name}}</a></u>
				</td>
				<td class="col-md-4" title="'Additional Information'" sortable="'subject'">
					{{form.subject}}
				</td>
				<td class="col-md-3" title="'Modified Date'" sortable="'date'">
					{{form.date | date:'yyyy-MM-dd'}}
				</td>
				<td class="col-md-2" title="'Action'">
					<a class="restore-link" href="javascript:" ng-click="$ctrl.restoreForm(form.id, form.type)"><u>Restore</u></a>
				</td>
			</tr>
		</tbody>
	</table>
</div>