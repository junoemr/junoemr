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
<div class="directive-juno-typeahead"
     ng-class="{
		'input-group': hasButtons(),
		'left-addon': hasLeftIcon(),
		'right-addon': hasRightIcon()}"
     >
	<i ng-if="hasIcon()"
	   class="icon icon-search form-control-feedback"></i>

	<input ng-if="hasTemplateUrl()"
	       type="text"
	       class="form-control"
	       autocomplete="{{autocompleteOff}}"
	       spellcheck="false"
	       ng-model="$parent.searchField"
	       ng-model-options="typeaheadModelOptions"
	       ng-change="onChange()"
	       ng-blur="onBlur()"
           ng-disabled="isDisabled"
	       uib-typeahead="match for match in findMatches($viewValue)"
	       typeahead-template-url="{{ optionsTemplateUrl }}"
	       typeahead-input-formatter="formatMatch($model)"
	       typeahead-on-select="onSelect($item, $model, $label, $event)"
	       typeahead-min-length="1"
	       typeahead-editable="true"
	       typeahead-select-on-blur="false"
	       typeahead-focus-first="false"
	       typeahead-append-to-body="false"
	       placeholder="{{placeholder}}"
	       aria-describedby="patient-typeahead"
	/>

	<input ng-if="!hasTemplateUrl()"
	       type="text"
	       class="form-control"
	       autocomplete="{{autocompleteOff}}"
	       spellcheck="false"
	       ng-model="$parent.searchField"
	       ng-model-options="typeaheadModelOptions"
	       ng-change="onChange()"
	       ng-blur="onBlur()"
           ng-disabled="isDisabled"
	       uib-typeahead="match as typeaheadLabel(match) for match in findMatches($viewValue)"
	       typeahead-input-formatter="formatMatch($model)"
	       typeahead-on-select="onSelect($item, $model, $label, $event)"
	       typeahead-min-length="1"
	       typeahead-editable="true"
	       typeahead-select-on-blur="false"
	       typeahead-focus-first="false"
	       typeahead-append-to-body="false"
	       placeholder="{{placeholder}}"
	       aria-describedby="patient-typeahead"
	/>

	<span ng-if="hasButtons()" class="input-group-btn">
		<button ng-if="hasSearchButton()"
			type="button"
			class="btn btn-default btn-search"
			ng-click="onSearch()"
			title="{{searchButtonTitle}}"
            ng-disabled="isDisabled">
			<span class="glyphicon glyphicon-search" ></span>
		</button>

		<button ng-if="hasAddButton()" 
			type="button"
			class="btn btn-default" 
			ng-click="onAdd()"
			title="{{addButtonTitle}}"
            ng-disabled="isDisabled">
			<span class="glyphicon glyphicon-plus"></span>
		</button>
	</span>


</div>
