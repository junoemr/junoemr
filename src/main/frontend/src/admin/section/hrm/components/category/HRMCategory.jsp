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

<div id="hrm-category" class="flex-col align-items-center justify-content-top h-100">
    <div class="flex-row justify-content-between align-items-center w-100">
    <h4>Manage Report Classification</h4>
        <juno-button class="flex-grow-0 w-128"
                     ng-if="!$ctrl.newRole"
                     component-style="$ctrl.componentStyle"
                     button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
                     button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL"
                     click="$ctrl.onCreateCategory()"
                     disabled="!$ctrl.canCreate()">
            New Category
        </juno-button>
    </div>
    <table ng-table="$ctrl.tableParams" class="table table-striped table-bordered">
        <tbody>
        <tr ng-repeat="category in $ctrl.categories">
            <td data-title="'Name'"
                class="width-60">
                {{ category.name }}
            </td>
            <td data-title="'Subclasses'">
                {{ category.subClasses.length }}
            </td>
            <td>
                <juno-button component-style="$ctrl.componentStyle"
                             button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
                             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL"
                             click="$ctrl.onUpdateCategory(category)"
                             disabled="!$ctrl.canUpdate()">
                    Manage
                </juno-button>
            </td>
        </tr>
        </tbody>
    </table>
</div>