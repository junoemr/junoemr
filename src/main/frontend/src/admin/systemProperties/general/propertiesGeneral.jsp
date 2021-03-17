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


<div class="admin-system-properties-body system-properties-general">
    <h3 class="title">Change Phone Prefix</h3>
    <div class="content">
        <div class="property flex-row" ng-repeat="property in $ctrl.propertiesList">
            <div class="property-toggle flex-column" >
                <juno-input-save
                        ng-model="$ctrl.phonePrefixValue"
                        click="$ctrl.updateProperty(property, value)">
                </juno-input-save>
            </div>
            <div class="property-text flex-column">
                <div class="name">{{ property.name }}</div>
                <div class="description">{{ property.description }}</div>
            </div>
        </div>
    </div>
</div>