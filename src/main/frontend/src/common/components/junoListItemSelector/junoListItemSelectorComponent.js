/*
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

import {JUNO_STYLE} from "../junoComponentConstants";

angular.module('Common.Components').component('junoListItemSelector', {
	templateUrl: 'src/common/components/junoListItemSelector/junoListItemSelector.jsp',
	bindings: {
		ngModel: "=",
		componentStyle: "<?",
		labelSelected: "@?",
		labelOptions: "@?",
		onChange: "&?",
		disabled: "<?",
	},
	controller: ['$scope',
		function ($scope)
	{
		const ctrl = this;

		ctrl.selected = [];
		ctrl.options = [];

		ctrl.activeOption = null;
		ctrl.activeSelection = null;

		ctrl.$onInit = () =>
		{
			ctrl.componentStyle = ctrl.componentStyle || JUNO_STYLE.DEFAULT;
			ctrl.reComputeLists();
		}

		$scope.$watch("$ctrl.ngModel", (oldVal, newVal) =>
		{
			if(oldVal !== newVal)
			{
				ctrl.setActiveOption(null);
				ctrl.setActiveSelection(null);
				ctrl.reComputeLists();
			}
		})

		ctrl.emitChangeEvent = (item) =>
		{
			if (ctrl.onChange)
			{
				ctrl.onChange(
					{
						item: item,
						model: ctrl.ngModel,
					}
				);
			}
		}

		ctrl.componentClasses = () =>
		{
			return [ctrl.componentStyle];
		}

		ctrl.reComputeLists = () =>
		{
			ctrl.selected = ctrl.ngModel.filter((item) => item.selected);
			ctrl.options = ctrl.ngModel.filter((item) => !item.selected);
		}

		ctrl.setActiveOption = (item) =>
		{
			ctrl.activeOption = item;
		}

		ctrl.setActiveSelection = (item) =>
		{
			ctrl.activeSelection = item;
		}

		ctrl.addToSelected = (item) =>
		{
			if(item)
			{
				item.selected = true;
				ctrl.setActiveOption(ctrl.findNextItem(item, ctrl.options));
				ctrl.reComputeLists();
				ctrl.setActiveSelection(item);
				ctrl.emitChangeEvent(item);
			}
		}
		ctrl.removeFromSelected = (item) =>
		{
			if(item)
			{
				item.selected = false;
				ctrl.setActiveSelection(ctrl.findNextItem(item, ctrl.selected));
				ctrl.reComputeLists();
				ctrl.setActiveOption(item);
				ctrl.emitChangeEvent(item);
			}
		}

		ctrl.findNextItem = (item, list) =>
		{
			const index = list.indexOf(item);

			// get the next item in the list if possible
			if (index >= 0 && index < list.length - 1)
			{
				return list[index + 1];
			}
			// otherwise try for the previous item if possible
			else if(index > 0 && index < list.length)
			{
				return list[index - 1];
			}
			return null;
		}
	}],
});