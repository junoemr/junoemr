/*

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

 */

angular.module('Record.Summary').component('summaryModule', {
	templateUrl: "src/record/summary/summaryModuleTemplate.jsp",
	bindings: {
		module: '<',
		itemDisplayCount: '<', // how many list items to display before requiring expansion
		maxItemNameLength: '<',
		hideDate: "<?",
		onclickItem: '&', //callback function
		onclickAdd: '&', //callback function
		onclickTitle: '&?', //callback function, called when user clicks the title
		clickableTitle: '<', //true means title is clickable

		enableFilter: '<',
		enableAddButton: '<',
	},
	controller: [function ()
	{
		var ctrl = this;

		ctrl.displayDateFormat = Juno.Common.Util.DisplaySettings.dateFormat;

		ctrl.$onInit = function()
		{
			// initialize internal variables
			ctrl.itemFilter = null;
			ctrl.showAllItems = false;

			// set default binding values
			ctrl.itemDisplayCount = ctrl.itemDisplayCount || 5;
			ctrl.enableFilter = ctrl.enableFilter || false;
			ctrl.enableAddButton = ctrl.enableAddButton || false;
			ctrl.clickableTitle = ctrl.clickableTitle || false;
			ctrl.maxItemNameLength = ctrl.maxItemNameLength || 34;
			ctrl.onclickItem = ctrl.onclickItem || null;
			ctrl.onclickAdd = ctrl.onclickAdd || null;
			ctrl.onclickTitle = ctrl.onclickTitle || null;
			ctrl.hideDate = ctrl.hideDate || false;
		};

		ctrl.$onChanges = function(bindingHash)
		{
			// bindingsHash only has data for changed bindings, so check for object existance
			if(Juno.Common.Util.exists(bindingHash.itemDisplayCount))
			{
				ctrl.itemDisplayCount = bindingHash.itemDisplayCount.currentValue;
			}
			if(Juno.Common.Util.exists(bindingHash.module))
			{
				ctrl.module = bindingHash.module.currentValue;
			}
			if(Juno.Common.Util.exists(bindingHash.enableAddButton))
			{
				ctrl.enableAddButton = bindingHash.enableAddButton.currentValue;
			}
			if(Juno.Common.Util.exists(bindingHash.enableFilter))
			{
				ctrl.enableFilter = bindingHash.enableFilter.currentValue;
			}
		};

		ctrl.itemCallback = function itemCallback(item)
		{
			ctrl.onclickItem({
				'module': ctrl.module,
				'item': item,
				successCallback: function (newItem)
				{
					//TODO-legacy could update to the item list here instead of refresh in parent controller
				},
				dismissCallback: function (reason)
				{
					//do nothing
				}
			});
		};
		ctrl.addBtnCallback = function addBtnCallback()
		{
			ctrl.onclickAdd({
				'module': ctrl.module,
				successCallback: function (newItem)
				{
					//TODO-legacy could add to the item list here instead of refresh in parent controller
				},
				dismissCallback: function (reason)
				{
					//do nothing
				}
			});
		};

		ctrl.clickTitleCallback = function ()
		{
			if (ctrl.onclickTitle !== null)
			{
				ctrl.onclickTitle({'module': ctrl.module});
			}
		};

		ctrl.toggleShowAllItems = function toggleShowAllItems()
		{
			ctrl.showAllItems = !ctrl.showAllItems;
		}
	}]
});