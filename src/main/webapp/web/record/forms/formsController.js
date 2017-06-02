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
		console.log("form ctrl ", $stateParams, $state);

		$scope.demographicNo = $stateParams.demographicNo;
		$scope.providerNo = user.providerNo;

		$scope.page = {};
		$scope.page.currentFormList = [];
		$scope.page.currentForm = {};
		$scope.page.currentlistId = 0;

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
				$scope.adminAccess = results.content[0];
				$scope.adminEformAccess = results.content[1];
				if (results.content != null && results.content.length == 2)
				{
					if ($scope.adminAccess || $scope.adminEformAccess)
					{
						$scope.hasAdminAccess = true;
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


		$scope.page.formlists = [
		{
			id: 0,
			label: 'Completed'
		},
		{
			id: 1,
			label: 'Library'
		}]; //Need to get this from the server.

		$scope.page.formlists.forEach(function(item, index)
		{
			console.log('What is the item ', item);
			formService.getAllFormsByHeading($stateParams.demographicNo, item.label).then(
				function success(results)
				{
					console.debug('whats the index' + index, results);
					$scope.page.currentFormList[index] = Juno.Common.Util.toArray(results.list);
				},
				function error(errors)
				{
					console.log(errors);
				});
		});


		$scope.page.encounterFormlist = [];
		$scope.page.formGroups = [];
		$scope.getFormGroups = function getFormGroups()
		{
			formService.getFormGroups().then(
				function success(results)
				{
					if (results instanceof Array)
					{
						$scope.page.formGroups = results;
					}
					else
					{
						$scope.page.formGroups.push(results);
					}

					for (var i = 0; i < $scope.page.formGroups.length; i++)
					{
						if (!($scope.page.formGroups[i].summaryItem instanceof Array))
						{
							$scope.page.formGroups[i].summaryItem = [$scope.page.formGroups[i].summaryItem];
						}
					}
				},
				function error(errors)
				{
					console.log(errors);
				});
		};

		$scope.getFormGroups();
		$scope.page.formOptions = [];
		$scope.favouriteGroup = null;

		getFavouriteFormGroup = function getFavouriteFormGroup()
		{
			formService.getFavouriteFormGroup().then(
				function success(results)
				{
					$scope.favouriteGroup = results;

					if (!($scope.favouriteGroup.summaryItem instanceof Array))
					{
						$scope.favouriteGroup.summaryItem = [$scope.favouriteGroup.summaryItem];
					}

				},
				function error(errors)
				{
					console.log(errors);
				});
		};

		getFavouriteFormGroup();

		formService.getFormOptions($scope.demographicNo).then(
			function success(results)
			{
				console.log("data", results);

				if (results.items instanceof Array)
				{
					$scope.page.formOptions = results.items;
				}
				else
				{
					$scope.page.formOptions.push(results.items);
				}


				console.log("form options", $scope.page.formOptions);
			},
			function error(errors)
			{
				console.log(errors);
			});

		formService.getCompletedEncounterForms($stateParams.demographicNo).then(
			function success(results)
			{
				if (results.list instanceof Array)
				{
					$scope.page.encounterFormlist[0] = results.list;
				}
				else
				{
					var arr = new Array();
					arr[0] = results.list;
					$scope.page.encounterFormlist[0] = arr;
				}

				//$scope.page.encounterFormlist[0] = results.list;
				//console.log("completed list as is:" + JSON.stringify($scope.page.encounterFormlist[0]) );
			},
			function error(errors)
			{
				console.log(errors);
			});

		formService.getSelectedEncounterForms().then(
			function success(results)
			{
				$scope.page.encounterFormlist[1] = results;
			},
			function error(errors)
			{
				console.log(errors);
			});


		$scope.changeTo = function changeTo(listId)
		{
			$scope.page.currentlistId = listId;
			console.log('set currentlist to ' + listId);
			if (listId == 0)
			{
				formService.getAllFormsByHeading($stateParams.demographicNo, 'Completed').then(
					function success(results)
					{
						console.debug('whats the index' + 0, results);
						$scope.page.currentFormList[0] = Juno.Common.Util.toArray(results.list);
					},
					function error(errors)
					{
						console.log(errors);
					});
			}
		};

		$scope.viewFormState = function changeTo(item, view)
		{

			while (document.getElementById('formInViewFrame').hasChildNodes())
			{
				document.getElementById('formInViewFrame').removeChild(document.getElementById('formInViewFrame').firstChild);
			}

			var url = '';
			var addOrShow = '';
			var formId = 0;

			if (view === undefined)
			{
				view = 1;
			}


			if (item.type == 'eform')
			{
				if (angular.isDefined(item.id))
				{
					addOrShow = '../eform/efmshowform_data.jsp?fdid=' + item.id;
				}
				else
				{
					addOrShow = '../eform/efmformadd_data.jsp?fid=' + item.formId + '&demographic_no=' + $stateParams.demographicNo;
				}
				/*
				 * 1=frame
				 * 2=newwindow
				 */
				if (view == 1)
				{
					url = addOrShow;
					$state.go('record.forms.existing',
					{
						demographicNo: $stateParams.demographicNo,
						type: 'eform',
						id: item.id
					});
					$("html, body").animate(
					{
						scrollTop: 0
					}, "slow");
				}
				else if (view == 2)
				{
					url = addOrShow;

					var rnd = Math.round(Math.random() * 1000);
					win = "win" + rnd;

					window.open(url, win, "scrollbars=yes, location=no, width=900, height=600", "");
					return;
				}

			}
			else
			{ //form
				if (angular.isDefined(item.formId))
				{
					addOrShow = '../form/forwardshortcutname.jsp?formname=' + item.name + '&demographic_no=' + $stateParams.demographicNo + '&formId=' + item.formId;
				}
				else
				{
					addOrShow = item.formValue + $stateParams.demographicNo + "&formId=0&provNo=" + user.providerNo + "&parentAjaxId=forms";
				}

				if (view == 1)
				{
					url = addOrShow;
					$state.go('record.forms.existing',
					{
						demographicNo: $stateParams.demographicNo,
						type: 'form',
						id: item.id
					});
					$("html, body").animate(
					{
						scrollTop: 0
					}, "slow");

				}
				else if (view == 2)
				{
					url = addOrShow;

					var rnd = Math.round(Math.random() * 1000);
					win = "win" + rnd;

					window.open(url, win, "scrollbars=yes, location=no, width=900, height=600", "");
					return;
				}


			}

			$scope.page.currentForm = item;
			var pymParent = new pym.Parent('formInViewFrame', url,
			{});
			$scope.pymParent = pymParent;

			/*if(item.type != 'eform' && view==1){
				document.getElementById('formInViewFrame').firstChild.style.height = "1600px"; //temp hack for the forms
			}*/

			//resize iframe for both form and eforms
			$('iframe').load(function()
			{
				var maxheight = Math.max(document.getElementById('formInViewFrame').firstChild.contentWindow.document.body.scrollHeight, document.getElementById('formInViewFrame').firstChild.contentWindow.document.body.offsetHeight) + 30 + 'px';
				document.getElementById('formInViewFrame').firstChild.style.height = maxheight;
			});

		}

		$scope.isEmpty = function isEmpty(obj)
		{
			for (var i in obj)
				if (obj.hasOwnProperty(i)) return false;
			return true;
		};

		$scope.currentEformGroup = {};

		$scope.setCurrentEFormGroup = function setCurrentEFormGroup(mod)
		{
			$scope.currentEformGroup = mod;
		};

		$scope.openFormFromGroups = function openFormFromGroups(item)
		{
			console.log("group item", item);
			item.formId = item.id;
			delete item.id;
			$scope.viewFormState(item, 2);
		};

		$scope.formOption = function formOption(opt)
		{
			var atleastOneItemSelected = false;
			if (opt.extra == "send2PHR")
			{
				var docIds = "";
				for (var i = 0; i < $scope.page.currentFormList[$scope.page.currentlistId].length; i++)
				{
					if ($scope.page.currentFormList[$scope.page.currentlistId][i].isChecked)
					{
						docIds = docIds + '&sendToPhr=' + $scope.page.currentFormList[$scope.page.currentlistId][i].id;
						atleastOneItemSelected = true;
					}
				}
				if (atleastOneItemSelected)
				{
					var rnd = Math.round(Math.random() * 1000);
					win = "win" + rnd;
					var url = '../eform/efmpatientformlistSendPhrAction.jsp?clientId=' + $scope.demographicNo + docIds;
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
		$scope.getListClass = function getListClass(listId)
		{
			if (listId === $scope.page.currentlistId)
			{
				return "active";
			}
		};

		/*
		 * Used to make group setting active 
		 */
		$scope.getGroupListClass = function getGroupListClass(grp)
		{
			if (grp === $scope.currentEformGroup)
			{
				return "active";
			}
		};

		/*
		 * Used to mark which form is active.
		 */
		$scope.getActiveFormClass = function getActiveFormClass(item)
		{
			if (item.type == $scope.page.currentForm.type && item.id == $scope.page.currentForm.id && angular.isDefined(item.id))
			{
				return "active";
			}
			else if (item.type == $scope.page.currentForm.type && angular.isUndefined(item.id) && item.formId == $scope.page.currentForm.formId)
			{
				return "active";
			}
		};

		// Remove?
		function handleError(errorMessage)
		{
			console.log(errorMessage);
		}

		if ($state.current.name == 'record.forms.new')
		{
			var item = {};
			item.type = $state.params.type;
			item.formId = $state.params.id;
			$scope.viewFormState(item);
			$scope.changeTo(1);

		}
		else if ($state.current.name == 'record.forms.existing')
		{
			var item = {};
			item.type = $state.params.type;
			item.id = $state.params.id;
			$scope.viewFormState(item);
			$scope.changeTo(0);
		}


		/*
		 * This still needs to be tested
		 */
		$scope.keypress = function keypress(event)
		{
			if (event.altKey == true && event.keyCode == 38)
			{ //up
				console.log("up", event);
				console.log($scope.page.currentFormList[$scope.page.currentlistId].indexOf($scope.page.currentForm));
				var currIdx = $scope.page.currentFormList[$scope.page.currentlistId].indexOf($scope.page.currentForm);
				if (currIdx > 0)
				{
					$scope.page.currentForm = $scope.page.currentFormList[$scope.page.currentlistId][currIdx - 1];
					$scope.viewFormState($scope.page.currentForm);
				}
			}
			else if (event.altKey == true && event.keyCode == 40)
			{ //Down
				console.log("down", event);
				var currIdx = $scope.page.currentFormList[$scope.page.currentlistId].indexOf($scope.page.currentForm);
				console.log(currIdx, $scope.page.currentFormList[$scope.page.currentlistId].length);
				if (currIdx <= $scope.page.currentFormList[$scope.page.currentlistId].length)
				{
					$scope.page.currentForm = $scope.page.currentFormList[$scope.page.currentlistId][currIdx + 1];
					$scope.viewFormState($scope.page.currentForm);
				}
			}
			else
			{
				console.log("keypress", event.altKey, event.key, event);
			}
		};


	}
]);