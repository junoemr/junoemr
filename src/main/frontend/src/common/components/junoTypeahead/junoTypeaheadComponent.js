/**
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
 */

import {JUNO_STYLE, LABEL_POSITION} from "../junoComponentConstants";

angular.module('Common.Components').component('junoTypeahead',
{
	templateUrl: 'src/common/components/junoTypeahead/junoTypeahead.jsp',
	bindings: {
		title: '@?',
		labelPosition: "<?",
		name: '@',
		model: '=',
		options: "<",
		placeholder: "@?",
		onEnterKey: '&?',
		onChange: "&?",
		onSelected: "&?",
		disabled: '<?',
		typeaheadMinLength: "@?",
		componentStyle: "<?",
		// output the output of the ubi-typeahead directly, without translation
		rawOutput: "<?",
		// if true options are filtered on $viewValue. Set to false if you plan to self filter
		filterOptions: "<?",
		// Use with filterOptions: false. This callback is called when the option list needs to be fetched.
		// Most of the time you will use this to fetch a paged list of options. This callback is provided
		// the current search parameter as an argument.
		getOptionsCallback: "&?"
	},
	controller: ['$scope', "filterFilter", function ($scope, filterFilter)
	{
		let ctrl = this;
		$scope.LABEL_POSITION = LABEL_POSITION;

		ctrl.selectedValue = null;

		let lastModel = null;

		ctrl.$onInit = function()
		{
			ctrl.typeaheadMinLength = ctrl.typeaheadMinLength ? ctrl.typeaheadMinLength : 1;
			ctrl.rawOutput = ctrl.rawOutput || false;
			if (ctrl.filterOptions === undefined)
			{
				ctrl.filterOptions = true;
			}
			ctrl.labelPosition = ctrl.labelPosition || LABEL_POSITION.TOP;
			ctrl.componentStyle = ctrl.componentStyle || JUNO_STYLE.DEFAULT;
		};

		ctrl.$doCheck = () =>
		{
			if (ctrl.model && ctrl.options && ctrl.options.length >= 1 && !ctrl.rawOutput)
			{
				if (lastModel !== ctrl.model)
				{
					lastModel = ctrl.model;
					ctrl.selectedValue = Juno.Common.Util.typeaheadValueLookup(ctrl.model, ctrl.options);
				}
			}
			else if (ctrl.model && ctrl.options && ctrl.options.length >= 1)
			{
				if (lastModel !== ctrl.model)
				{
					lastModel = ctrl.model;
					ctrl.selectedValue = ctrl.model;
				}
			}
		};

		ctrl.doOnChange = () =>
		{
			if (ctrl.onChange)
			{
				if (ctrl.selectedValue.value)
				{
					ctrl.onChange({value: ctrl.selectedValue.value});
				}
				else
				{
					ctrl.onChange({value: ctrl.selectedValue});
				}
			}

			ctrl.model = null;
			lastModel = null;
		}

		ctrl.getOptions = async (viewValue) =>
		{
			if (ctrl.filterOptions)
			{
				return filterFilter(ctrl.options, viewValue);
			}
			else if (ctrl.getOptionsCallback)
			{
				return await ctrl.getOptionsCallback({value: viewValue});
			}
			return ctrl.options;
		}

		ctrl.onSelect = () =>
		{
			if (ctrl.rawOutput)
			{
				ctrl.model = ctrl.selectedValue;
			}
			else
			{
				ctrl.model = ctrl.selectedValue.value;
			}

			if (ctrl.onSelected)
			{
				ctrl.onSelected({value: ctrl.selectedValue});
			}

			ctrl.doOnChange();
		}

		ctrl.onKeyPress = function (event)
		{
			if (event.keyCode === 13)
			{// Enter key
				if (ctrl.onEnterKey)
				{
					ctrl.onEnterKey({});
				}
			}
		}

		ctrl.labelClasses = () =>
		{
			return [ctrl.labelPosition];
		};

		ctrl.componentClasses = () =>
		{
			return [ctrl.componentStyle];
		}

	}]
});