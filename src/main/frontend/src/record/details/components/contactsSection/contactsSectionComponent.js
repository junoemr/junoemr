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

import {JUNO_STYLE, LABEL_POSITION} from "../../../../common/components/junoComponentConstants";
import {SystemPreferenceApi} from "../../../../../generated";

angular.module('Record.Details').component('contactsSection', {
	templateUrl: 'src/record/details/components/contactsSection/contactsSection.jsp',
	bindings: {
		ngModel: "=",
		validations: "=",
		componentStyle: "<?"
	},
	controller: [ "staticDataService", "$scope", "$http", "$httpParamSerializer", "$state", "$stateParams", "demographicService", "uxService", function (staticDataService, $scope, $http, $httpParamSerializer, $state, $stateParams, demographicService, uxService)
	{
		let ctrl = this;
		$scope.LABEL_POSITION = LABEL_POSITION;
		ctrl.tab = [];
		ctrl.demo = null;
		ctrl.thisDemo = $stateParams.demographicNo;

		ctrl.$onInit = () =>
		{
			console.log(ctrl.ngModel);
			console.log(ctrl.componentStyle);
		}

		ctrl.getTabs = function getTabs(demogNo)
		{
			uxService.menu(demogNo).then(
				function success(results)
				{
					ctrl.tab = results[0];
					ctrl.tab.demoId = demogNo;
					ctrl.changeTab(ctrl.tab);
				},
				function error(errors)
				{
					console.log(errors);
				});
		};

		ctrl.openContacts = async (demoContactId) =>
		{

			demographicService.getDemographic(demoContactId).then(
						function success(results)
						{
							ctrl.demo = results;
							console.log("it exists", results);
						},
						function error(errors)
						{
							alert('Error loading demographic: ', errors) // TODO-legacy: Display actual error message
						}
					);
			if(ctrl.demo != null)
			{
				ctrl.getTabs(demoContactId);
				//ctrl.changeTab(ctrl.tab);
			}
			//ctrl.getTabs(demoContactId);
			//ctrl.changeTab(ctrl.tab);
			/* try
             {
                 await $uibModal.open(
                     {
                         component: 'rosteredHistoryModal',
                         backdrop: 'static',
                         windowClass: "juno-modal lg",
                         resolve: {
                             demographic: ctrl.ngModel,
                         }
                     })
             }
             catch(_reason)
             {
                 // do nothing on cancel
             }*/
		}
		ctrl.changeTab = function changeTab(temp)
		{
			//controller.currenttab2 = controller.recordtabs2[temp.id];

			if (Juno.Common.Util.isDefinedAndNotNull(temp.state))
			{
				if(Juno.Common.Util.isDefinedAndNotNull(temp.demoId)){
					$state.go(temp.state[0],
						{
							demographicNo: temp.demoId
						});
				}
				else
				{
					$state.go(temp.state[0]);
				}
			}
			else
			{
						if (angular.isDefined(temp.url))
						{
							var win;
								var rnd = Math.round(Math.random() * 1000);
								win = "win" + rnd;
							window.open(temp.url, win, "scrollbars=yes, location=no, width=1000, height=600", "");
						}
			}
		};

		//manage contacts
		ctrl.manageContacts = function manageContacts()
		{
			var discard = true;
			/*if (ctrl.page.dataChanged > 0)
			{
				discard = confirm("You may have unsaved data. Are you sure to leave?");
			}*/
			if (discard)
			{
				console.log(ctrl.thisDemo);
				var url = "../demographic/Contact.do?method=manage&demographic_no=" + ctrl.thisDemo;
				window.open(url, "ManageContacts", "width=960, height=700");
			}
		};

	}]
});