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
<div class="form-view-component" class="container-fluid">
    <table ng-table-dynamic="$ctrl.tableParams with $ctrl.cols" show-filter="false" class="table table-striped table-bordered">
        <tbody>
            <tr ng-repeat=" form in $ctrl.formList | filter:$ctrl.doFilterForms  | orderBy:$ctrl.sortMode">
                <td ng-repeat="col in $columns" ng-class="col.displayClass">
                    <div ng-if="col.field === 'name'">
                        <u><a href="javascript:" ng-if="form.type === FORM_CONTROLLER_FORM_TYPES.EFORM" ng-click="$ctrl.openEForm(form)">{{form.name}}</a></u>
                        <u><a href="javascript:" ng-if="form.type === FORM_CONTROLLER_FORM_TYPES.FORM" ng-click="$ctrl.openForm(form)">{{form.name}}</a></u>
                    </div>
                    <div ng-if="col.field === 'subject'">
                        <span ng-if="form.type === FORM_CONTROLLER_FORM_TYPES.EFORM">{{form.subject}}</span>
                        <span ng-if="form.type === FORM_CONTROLLER_FORM_TYPES.FORM"></span>
                    </div>
                    <div ng-if="col.field === 'date'">
                        {{form.date | date:'yyyy-MM-dd'}}
                    </div>
                    <div ng-if="col.field === 'createDate'">
                        {{form.createDate | date:'yyyy-MM-dd'}}
                    </div>
                    <div ng-if="col.field === 'delete' && $ctrl.canDeleteForm(form.type)">
                        <a class="delete-link" href="javascript:"
                           ng-if="form.type === FORM_CONTROLLER_FORM_TYPES.EFORM"
                           ng-click="$ctrl.deleteForm(form.id, form.type)">
                            <u>delete</u>
                        </a>
                    </div>
                    <div ng-if="col.field === 'restore' && $ctrl.canRestoreForm(form.type)">
                        <a class="restore-link" href="javascript:" ng-click="$ctrl.restoreForm(form.id, form.type)">
                            <u>Restore</u>
                        </a>
                    </div>
                </td>
            </tr>
        </tbody>
    </table>
</div>