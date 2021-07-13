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
		ctrl.thisDemo = null;

		ctrl.demoContacts = [];
		ctrl.demoContactsInternal = [];
		ctrl.demoContactPros = [];
		ctrl.demoContactsExternal = [];


		ctrl.$onInit = () =>
		{
			// retrieve contact lists for demographic
			ctrl.thisDemo = $stateParams.demographicNo;
					demographicService.getDemographicContacts(ctrl.thisDemo, "personal").then(
						function success(data)
						{
							console.log(data);
							ctrl.demoContacts = (data);
							ctrl.sortContacts();
						}
					);
					demographicService.getDemographicContacts(ctrl.thisDemo, "professional").then(
						function success(data)
						{
							ctrl.demoContactPros = demoContactShow(data);
						}
					);
					 //ctrl.sortContacts();
		}

		ctrl.sortContacts = function sortContacts()
		{
			for (let i = 0; i < ctrl.demoContacts.length; i++)
			{
				console.log(ctrl.demoContacts[i].type);
				if (ctrl.demoContacts[i].type === 1)
				{
					ctrl.demoContactsInternal.push(ctrl.demoContacts[i]);
				}
				else if (ctrl.demoContacts[i].type === 2)
				{
					ctrl.demoContactsExternal.push(ctrl.demoContacts[i]);
				}
				else {

				}
			}
		}

		function demoContactShow(demoContact)
{
	var contactShow = demoContact;
	if (demoContact.role != null)
	{ //only 1 entry
		var tmp = {};
		tmp.role = demoContact.role;
		tmp.sdm = demoContact.sdm;
		tmp.ec = demoContact.ec;
		tmp.type = demoContact.type;
		tmp.category = demoContact.category;
		tmp.lastName = demoContact.lastName;
		tmp.firstName = demoContact.firstName;
		tmp.phone = demoContact.phone;
		tmp.contactId = demoContact.contactId;
		contactShow = [tmp];
	}
	for (var i = 0; i < contactShow.length; i++)
	{
		if (contactShow[i].sdm == true) contactShow[i].role += " /sdm";
		if (contactShow[i].ec == true) contactShow[i].role += " /ec";
		if (contactShow[i].role == null || contactShow[i].role == "") contactShow[i].role = "-";

		if (contactShow[i].phone == null || contactShow[i].phone == "")
		{
			contactShow[i].phone = "-";
		}
		else if (contactShow[i].phone.charAt(contactShow[i].phone.length - 1) == "*")
		{
			contactShow[i].phone = contactShow[i].phone.substring(0, contactShow[i].phone.length - 1);
		}
	}
	return contactShow;
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
			console.log(demoContactId.contactId);

			if(ctrl.demo != null)
			{
				ctrl.getTabs(demoContactId.contactId);
				//ctrl.changeTab(ctrl.tab);
			}
			else
			{
				try
				{
					await $uibModal.open(
						{
							component: 'externalContactsModal',
							backdrop: 'static',
							windowClass: "juno-modal lg",
							resolve: {
								demographic: ctrl.thisDemo,
							}
						})
				}
				catch (_reason)
				{
					// do nothing on cancel
				}
			}
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
							let win;
								let rnd = Math.round(Math.random() * 1000);
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