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

// displays a vertical, collapsible list such as the one found in the admin.
angular.module('Common.Components').component('accordionList', {
	templateUrl: 'src/common/components/accordionList/accordionList.jsp',
	bindings: {
		// items to display in the accordion list. hash of format
		// [{name: "groupName", expanded: true / false [optional] ,items: [{name: "item one", callback: func}, {name: "item two", callback: func}]},  ....
		// {name: "another group", items: [{name: "item 1000", callback: func}, {name: "over 90000", callback: func}]}]
		itemList: '<',
		itemClicked: '&?',
	},
	controller: ['$state', '$scope', function ($state, $scope)
	{
		let ctrl = this;

		ctrl.$onInit = function ()
		{

		};

		ctrl.bindCollapseListener = function (idx)
		{
			// listen for bootstrap collapse events
			let targetItem = ctrl.itemList[idx];
			if (!targetItem.eventsBound)
			{
				let collapseEl = angular.element(document.querySelector("#accordion-collapse-target-" + ctrl.getGroupCollapseId(targetItem)));

				collapseEl.on('hidden.bs.collapse', function ()
				{
					targetItem.expanded = false;
					// clear jquery set styling
					collapseEl.attr('style', '');
					$scope.$apply();
				});

				collapseEl.on('shown.bs.collapse', function ()
				{
					targetItem.expanded = true;
					// clear jquery set styling
					collapseEl.attr('style', '');
					$scope.$apply();
				});
			}
			targetItem.eventsBound = true;
		};

		ctrl.getGroupCollapseId = function (group)
		{
			return group.name.replace(' ', '').replace('/', '');
		};

		ctrl.onGroupClick = function (group)
		{
			for (let i = 0; i < ctrl.itemList.length; i ++)
			{
				if (group.name !== ctrl.itemList[i].name)
				{
					// collapse all. because we mixed boostrap and angular we need to do this the nasty way.
					angular.element(document.querySelector("#accordion-collapse-target-" + ctrl.getGroupCollapseId(ctrl.itemList[i]))).collapse('hide');
				}
			}
		};

		ctrl.onItemClicked = function (item)
		{
			if (item.callback !== undefined && item.callback !== null)
			{
				item.callback()
			}

			if(ctrl.itemClicked)
			{
				ctrl.itemClicked({item: item});
			}
		}

	}]
});