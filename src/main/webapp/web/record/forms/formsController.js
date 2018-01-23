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
angular.module('Record.Forms').controller('Record.Forms.FormController', [

	'$scope',
	'$http',
	'$location',
	'$stateParams',
	'$state',
	'demographicService',
	'demo',
	'formService',
	'user',
	'securityService',

	function(
		$scope,
		$http,
		$location,
		$stateParams,
		$state,
		demographicService,
		demo,
		formService,
		user,
		securityService)
	{

		var controller = this;

		console.log("form ctrl ", $stateParams, $state);

		controller.demographicNo = $stateParams.demographicNo;
		controller.providerNo = user.providerNo;

		controller.page = {};
		controller.page.currentFormList = [];
		controller.page.currentForm = {};
		controller.page.currentlistId = $stateParams.formListId;

		console.log("What is the state " + $state.params.type + " : " + angular.isUndefined($state.params.type) + " id " + $state.params.id, $state); // Use this to load the current form if the page is refreshed

		securityService.hasRights(
		{
			items: [
			{
				objectName: '_admin',
				privilege: 'w'
			},
			{
				objectName: '_admin.eform',
				privilege: 'w'
			}]
		}).then(
			function success(results)
			{
				controller.adminAccess = results.content[0];
				controller.adminEformAccess = results.content[1];
				if (results.content != null && results.content.length == 2)
				{
					if (controller.adminAccess || controller.adminEformAccess)
					{
						controller.hasAdminAccess = true;
					}
				}
				else
				{
					alert('failed to load rights');
				}
			},
			function error(errors)
			{
				console.log(errors);
			});


		controller.page.formlists = [
		{
			id: 0,
			label: 'Completed'
		},
		{
			id: 1,
			label: 'Library'
		}]; //Need to get this from the server.

		controller.page.formlists.forEach(function(item, index)
		{
			console.log('What is the item ', item);
			formService.getAllFormsByHeading($stateParams.demographicNo, item.label).then(
				function success(results)
				{
					controller.page.currentFormList[index] = Juno.Common.Util.toArray(results.list);
				},
				function error(errors)
				{
					console.log(errors);
				});
		});


		controller.page.encounterFormlist = [];
		controller.page.formGroups = [];
		controller.getFormGroups = function getFormGroups()
		{
			formService.getFormGroups().then(
				function success(results)
				{
					if (results instanceof Array)
					{
						controller.page.formGroups = results;
					}
					else
					{
						controller.page.formGroups.push(results);
					}

					for (var i = 0; i < controller.page.formGroups.length; i++)
					{
						if (!(controller.page.formGroups[i].summaryItem instanceof Array))
						{
							controller.page.formGroups[i].summaryItem = [controller.page.formGroups[i].summaryItem];
						}
					}
				},
				function error(errors)
				{
					console.log(errors);
				});
		};

		controller.getFormGroups();
		controller.page.formOptions = [];
		controller.favouriteGroup = null;

		getFavouriteFormGroup = function getFavouriteFormGroup()
		{
			formService.getFavouriteFormGroup().then(
				function success(results)
				{
					controller.favouriteGroup = results;

					if (!(controller.favouriteGroup.summaryItem instanceof Array))
					{
						controller.favouriteGroup.summaryItem = [controller.favouriteGroup.summaryItem];
					}

				},
				function error(errors)
				{
					console.log(errors);
				});
		};

		getFavouriteFormGroup();

		formService.getFormOptions(controller.demographicNo).then(
			function success(results)
			{
				console.log("data", results);

				if (results.items instanceof Array)
				{
					controller.page.formOptions = results.items;
				}
				else
				{
					controller.page.formOptions.push(results.items);
				}


				console.log("form options", controller.page.formOptions);
			},
			function error(errors)
			{
				console.log(errors);
			});

		formService.getCompletedEncounterForms($stateParams.demographicNo).then(
			function success(results)
			{
				controller.page.encounterFormlist[0] = results;
			},
			function error(errors)
			{
				console.log(errors);
			});

		formService.getSelectedEncounterForms().then(
			function success(results)
			{
				controller.page.encounterFormlist[1] = results;
			},
			function error(errors)
			{
				console.log(errors);
			});


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
						controller.page.currentFormList[0] = Juno.Common.Util.toArray(results.list);
					},
					function error(errors)
					{
						console.log(errors);
					});
			}
		};

		controller.viewFormState = function viewFormState(item, view)
		{

			while (document.getElementById('formInViewFrame').hasChildNodes())
			{
				document.getElementById('formInViewFrame').removeChild(document.getElementById('formInViewFrame').firstChild);
			}

			var queryName = $location.search().name;
			var params = {
				demographicNo: $stateParams.demographicNo,
				type: item.type === 'eform' ? 'eform' : 'form',
				id: item.id || item.formId
			};

			// The items sent from the server are such that
			// 1. Existing eforms have a formId and an id
			// 2. Existing forms have a formId only
			// 3. Library/available eforms have a formId only
			// 4. Library/available forms have neither
			if ((item.type === 'eform' && item.id) || (item.type === 'form' && item.formId))
			{
				params.name = item.type === 'form' ? item.name || queryName : '';
				$state.go('record.forms.view', params);
			}
			else if (item.type === 'eform')
			{
				$state.go('record.forms.add', params);
			}
			else
			{
				$state.go('record.forms');
			}

			/*
			* 1=frame
			* 2=newwindow
			*/
			if (view === undefined)
			{
				view = 1;
			}

			var url = '';
			if (item.type === 'eform')
			{
				if (!Juno.Common.Util.isUndefinedOrNull(item.id))
				{
					url = '../eform/efmshowform_data.jsp?fdid=' + item.id;
				}
				else
				{
					url = '../eform/efmformadd_data.jsp?fid=' + item.formId + '&demographic_no=' + $stateParams.demographicNo;
				}
			}
			else
			{ //form
				if (angular.isDefined(item.formId))
				{
					var name = item.name || queryName;
					url = '../form/forwardshortcutname.jsp?formname=' + name + '&demographic_no=' + $stateParams.demographicNo + '&formId=' + item.formId;
				}
				else
				{
					url = item.formValue + $stateParams.demographicNo + "&formId=0&provNo=" + user.providerNo + "&parentAjaxId=forms";
				}
			}

			if (view === 1)
			{
				$("html, body").animate(
					{
						scrollTop: 0
					}, "slow");
			}
			else if (view === 2)
			{
				var rnd = Math.round(Math.random() * 1000);
				win = "win" + rnd;

				window.open(url, win, "scrollbars=yes, location=no, width=900, height=600", "");
				return;
			}

			controller.page.currentForm = item;
			var pymParent = new pym.Parent('formInViewFrame', url,
			{});
			controller.pymParent = pymParent;

			/*if(item.type != 'eform' && view==1){
				document.getElementById('formInViewFrame').firstChild.style.height = "1600px"; //temp hack for the forms
			}*/

			//resize iframe for both form and eforms
			$('iframe').load(function()
			{
				var maxheight = Math.max(document.getElementById('formInViewFrame').firstChild.contentWindow.document.body.scrollHeight, document.getElementById('formInViewFrame').firstChild.contentWindow.document.body.offsetHeight) + 30 + 'px';
				document.getElementById('formInViewFrame').firstChild.style.height = maxheight;
			});

		};

		controller.isEmpty = function isEmpty(obj)
		{
			for (var i in obj)
				if (obj.hasOwnProperty(i)) return false;
			return true;
		};

		controller.currentEformGroup = {};

		controller.setCurrentEFormGroup = function setCurrentEFormGroup(mod)
		{
			controller.currentEformGroup = mod;
		};

		controller.openFormFromGroups = function openFormFromGroups(item)
		{
			console.log("group item", item);
			item.formId = item.id;
			delete item.id;
			controller.viewFormState(item, 2);
		};

		controller.formOption = function formOption(opt)
		{
			var atleastOneItemSelected = false;
			if (opt.extra == "send2PHR")
			{
				var docIds = "";
				for (var i = 0; i < controller.page.currentFormList[controller.page.currentlistId].length; i++)
				{
					if (controller.page.currentFormList[controller.page.currentlistId][i].isChecked)
					{
						docIds = docIds + '&sendToPhr=' + controller.page.currentFormList[controller.page.currentlistId][i].id;
						atleastOneItemSelected = true;
					}
				}
				if (atleastOneItemSelected)
				{
					var rnd = Math.round(Math.random() * 1000);
					win = "win" + rnd;
					var url = '../eform/efmpatientformlistSendPhrAction.jsp?clientId=' + controller.demographicNo + docIds;
					window.open(url, win, "scrollbars=yes, location=no, width=900, height=600", "");
				}
			}

			if (!atleastOneItemSelected)
			{
				alert("No Documents Selected");
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
		 * Used to make group setting active
		 */
		controller.getGroupListClass = function getGroupListClass(grp)
		{
			if (grp === controller.currentEformGroup)
			{
				return "active";
			}
		};

		/*
		 * Used to mark which form is active.
		 */
		controller.getActiveFormClass = function getActiveFormClass(item)
		{
			if (item.type === controller.page.currentForm.type && item.id === controller.page.currentForm.id && !Juno.Common.Util.isUndefinedOrNull(item.id))
			{
				return "active";
			}
			else if (item.type === controller.page.currentForm.type && item.formId === controller.page.currentForm.formId && Juno.Common.Util.isUndefinedOrNull(item.id))
			{
				return "active";
			}
		}

		var currentState = $state.current.name;
		if (currentState === 'record.forms.view' || currentState === 'record.forms.add')
		{
			var item = {};
			item.type = $state.params.type;

			// set item properties in a way that matches the content that the server returns
			if (currentState === 'record.forms.view' && item.type === 'eform')
			{
				item.id = $state.params.id;
			}
			else
			{
				item.formId = $state.params.id;
			}

			controller.viewFormState(item);
			controller.changeTo(currentState === 'record.forms.view' ? 0 : 1);
		}

		/*
		 * This still needs to be tested
		 */
		controller.keypress = function keypress(event)
		{
			if (event.altKey == true && event.keyCode == 38)
			{ //up
				console.log("up", event);
				console.log(controller.page.currentFormList[controller.page.currentlistId].indexOf(controller.page.currentForm));
				var currIdx = controller.page.currentFormList[controller.page.currentlistId].indexOf(controller.page.currentForm);
				if (currIdx > 0)
				{
					controller.page.currentForm = controller.page.currentFormList[controller.page.currentlistId][currIdx - 1];
					controller.viewFormState(controller.page.currentForm);
				}
			}
			else if (event.altKey == true && event.keyCode == 40)
			{ //Down
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