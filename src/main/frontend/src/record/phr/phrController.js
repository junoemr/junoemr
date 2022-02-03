/*

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

*/
angular.module('Record.PHR').controller('Record.PHR.PHRController', [

	'$scope',
	'$http',
	'$location',
	'$state',
	'$stateParams',
	'demo',
	'formService',

	function(
		$scope,
		$http,
		$location,
		$state,
		$stateParams,
		demo,
		formService)
	{

		var controller = this;

		console.log("phr ctrl ", $stateParams, $state);

		controller.page = {};
		controller.page.currentFormList = [];
		controller.page.currentForm = {};
		controller.page.currentlistId = 0;

		console.log("What is the state " + $state.params.type + " : " + angular.isUndefined($state.params.type) + " id " + $state.params.id, $state); // Use this to load the current form if the page is refreshed


		controller.page.formlists = [
		{
			id: 0,
			label: 'Data'
		}]; //,{id:1,label:'Msgs'}];  //Need to get this from the server.

		controller.page.currentFormList[0] = [
		{
			id: 0,
			name: 'Glucose',
			url: '../oscarEncounter/myoscar/measurements_glucose.do?type=GLUCOSE&demoNo=',
			type: 'frame'
		},
		{
			id: 1,
			name: 'BP',
			url: '../oscarEncounter/myoscar/measurements_blood_pressure.do?type=BLOOD_PRESSURE&demoNo=',
			type: 'frame'
		},
		{
			id: 2,
			name: 'Height & Weight',
			url: '../oscarEncounter/myoscar/measurements_height_and_weight.do?type=HEIGHT_AND_WEIGHT&demoNo=',
			type: 'frame'
		},
		{
			id: 3,
			name: 'Open Record',
			url: '../demographic/viewPhrRecord.do?demographic_no=',
			type: 'window'
		},
		{
			id: 4,
			name: 'Send a Message',
			url: '../phr/PhrMessage.do?method=createMessage&demographicNo=',
			type: 'window'
		},
		{
			id: 5,
			name: 'Data Sync',
			url: '../admin/oscar_myoscar_sync_config_redirect.jsp?uselessParam=',
			type: 'window'
		}];



		/*controller.page.formlists.forEach(function (item, index) {
			console.log('What is the item ',item);
			formService.getAllFormsByHeading($stateParams.demographicNo,item.label).then(function(data) {
		        console.debug('whats the index'+index,data);
		        controller.page.currentFormList[index] = data.list;
		    });
		});
		*/

		controller.changeTo = function changeTo(listId)
		{
			controller.page.currentlistId = listId;
			console.log('set currentlist to ' + listId);
			if (listId == 0)
			{
				formService.getAllFormsByHeading($stateParams.demographicNo, 'Completed').then(
					function success(results)
					{
						console.debug('whats the index' + 0, results);
						controller.page.currentFormList[0] = results.list;
					},
					function error(errors)
					{
						console.log(errors);
					});
			}
		};

		controller.viewFormState = function viewFormState(item)
		{

			while (document.getElementById('formInViewFrame').hasChildNodes())
			{
				document.getElementById('formInViewFrame').removeChild(document.getElementById('formInViewFrame').firstChild);
			}

			var url = item.url + $stateParams.demographicNo;
			console.log("item", item);

			if (item.type == 'frame')
			{
				controller.page.currentForm = item;
				var pymParent = new pym.Parent('formInViewFrame', url,
				{});
				controller.pymParent = pymParent;
			}
			else
			{
				var rnd = Math.round(Math.random() * 1000);
				win = "win" + rnd;
				window.open(url, win, "scrollbars=yes, location=no, width=900, height=600", "");
				return;
			}


		};



		/*
		 * Used to make the left side list tab be active
		 */
		controller.getListClass = function getListClass(listId)
		{
			if (listId === controller.page.currentlistId)
			{
				return "active";
			}
		};

		/*
		 * Used to mark which form is active.
		 */
		controller.getActiveFormClass = function getActiveFormClass(item)
		{
			if (item.type == controller.page.currentForm.type && item.id == controller.page.currentForm.id && angular.isDefined(item.id))
			{
				return "active";
			}
			else if (item.type == controller.page.currentForm.type && angular.isUndefined(item.id) && item.formId == controller.page.currentForm.formId)
			{
				return "active";
			}
		};

		// Remove this?
		function handleError(errorMessage)
		{
			console.log(errorMessage);
		}

		/*
		 * This still needs to be tested
		 */
		controller.keypress = function keypress(event)
		{
			if (event.altKey == true && event.key == "Up")
			{
				console.log("up", event);
				console.log(controller.page.currentFormList[controller.page.currentlistId].indexOf(controller.page.currentForm));
				var currIdx = controller.page.currentFormList[controller.page.currentlistId].indexOf(controller.page.currentForm);
				if (currIdx > 0)
				{
					controller.page.currentForm = controller.page.currentFormList[controller.page.currentlistId][currIdx - 1];
					controller.viewFormState(controller.page.currentForm);
				}
			}
			else if (event.altKey == true && event.key == "Down")
			{
				console.log("down", event);
				var currIdx = controller.page.currentFormList[controller.page.currentlistId].indexOf(controller.page.currentForm);
				console.log(currIdx, controller.page.currentFormList[controller.page.currentlistId].length);
				if (currIdx <= controller.page.currentFormList[controller.page.currentlistId].length)
				{
					controller.page.currentForm = controller.page.currentFormList[controller.page.currentlistId][currIdx + 1];
					controller.viewFormState(controller.page.currentForm);
				}
			}
			else
			{
				console.log("keypress", event.altKey, event.key, event);
			}
		};
	}
]);