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

angular.module('Common.Components').component('junoModal', {
	bindings: {
		hideFooter: '<',
		showLoading: '<',
	},
	templateUrl: "src/common/components/modalTemplate.jsp",
	transclude: {
		'title': '?modalTitle',
		'ctlButtons': '?modalCtlButtons',
		'body' : '?modalBody',
		'footer': '?modalFooter',
	},
	controller: function ()
	{
		let ctrl = this;
		ctrl.$onInit = function ()
		{
			ctrl.hideFooter = ctrl.hideFooter || false;
			ctrl.showLoading = ctrl.showLoading || false;
		};
		ctrl.$onChanges = function onChanges(bindingHash)
		{
			// bindingsHash only has data for changed bindings, so check for object existance
			if(Juno.Common.Util.exists(bindingHash.showLoading))
			{
				ctrl.showLoading = bindingHash.showLoading.currentValue;
			}
		}
	}
});