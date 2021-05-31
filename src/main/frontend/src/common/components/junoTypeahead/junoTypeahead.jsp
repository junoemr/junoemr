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
<div class="juno-typeahead form-group" ng-class="$ctrl.componentClasses()">
	<label ng-if="$ctrl.title"
					ng-class="$ctrl.labelClasses()"
					for="name-{{$ctrl.name}}"
	>
		{{$ctrl.title}}
	</label>
	<div class="input-container flex-row flex-no-wrap flex-item-grow">
		<i ng-if="$ctrl.icon" class="icon" ng-class="$ctrl.icon"></i>
		<input
						id="name-{{$ctrl.name}}"
						type="text"
						autocomplete="off"
						ng-class="$ctrl.inputClasses()"
						ng-model="$ctrl.selectedValue"
						uib-typeahead="option as option.label for option in $ctrl.getOptions($viewValue)"
						typeahead-select-on-exact="true"
						typeahead-on-select="$ctrl.onSelect()"
						typeahead-min-length="$ctrl.typeaheadMinLength"
						ng-keypress="$ctrl.onKeyPress($event)"
						ng-change="$ctrl.doOnChange()"
						placeholder="{{$ctrl.placeholder}}"
						ng-disabled="$ctrl.disabled">
	</div>
</div>