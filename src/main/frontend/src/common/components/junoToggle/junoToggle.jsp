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
<div ng-class="$ctrl.componentClasses()" class="juno-toggle">
    <label ng-if="$ctrl.label" ng-class="$ctrl.labelClasses()">
        {{$ctrl.label}}
    </label>
    <div class="flex-row justify-content-center align-items-center">
        <label ng-if="$ctrl.showValueLabels" class="toggle-value-label">{{$ctrl.toggleFalseValue}}</label>
        <label class="switch">
            <input id="{{$ctrl.id}}" type="checkbox"
                   ng-disabled="$ctrl.disabled"
                   ng-model="$ctrl.ngModel"
                   ng-change="$ctrl.onChange()"/>
            <span class="slider" ng-class="{'round': $ctrl.round}"></span>
        </label>
        <label ng-if="$ctrl.showValueLabels" class="toggle-value-label">{{$ctrl.toggleTrueValue}}</label>
    </div>
</div>